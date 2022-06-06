package com.sleepy.manager.main.processor;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;
import com.sleepy.manager.common.utils.StringUtils;
import com.sleepy.manager.common.utils.file.ImageUtils;
import com.sleepy.manager.main.common.AssembledData;
import com.sleepy.manager.main.common.HtmlElementType;
import com.sleepy.manager.system.domain.CrawlerRule;
import com.sleepy.manager.system.mapper.CrawlerRuleMapper;
import com.sleepy.manager.system.service.ISysConfigService;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.sleepy.manager.common.utils.LogUtils.logError;
import static com.sleepy.manager.common.utils.LogUtils.logServiceError;
import static com.sleepy.manager.common.utils.StringUtils.format;
import static com.sleepy.manager.common.utils.StringUtils.md5ForString;
import static com.sleepy.manager.common.utils.file.FileUtils.*;

/**
 * @author captain1920
 * @classname CrawlerProcessor
 * @description TODO
 * @date 2022/4/11 15:44
 */
@Component
public class CrawlerProcessor {
    @Autowired
    CrawlerRuleMapper crawlerRuleMapper;
    @Autowired
    ISysConfigService configService;
    WebClient webClient;

    //设置解析网页favicon.ico的link的正则表达式
    private static final Pattern[] ICON_PATTERNS = new Pattern[]{
            Pattern.compile("rel=[\"']shortcut icon[\"'][^\r\n>]+?((?<=href=[\"']).+?(?=[\"']))"),
            Pattern.compile("((?<=href=[\"'])[^\r\n\"']+?(?=[\"']))[^\n\r<]+?rel=[\"']shortcut icon[\"']"),
            Pattern.compile("property=[\"']og:image[\"'][^\n\r>]+?((?<=content=[\"“]).+?(?=[\"']))"),
            Pattern.compile("sizes=[\"'](192x192|96x96|32x32|16x16)[\"'][^\n\r>]+?((?<=href=[\"']).+?(?=[\"']))"),
            Pattern.compile("((?<=href=[\"'])[^\n\r\"']+?(?=[\"']))\"sizes=[\"'](192x192|96x96|32x32|16x16)[\"']"),
            Pattern.compile("rel=[\"']icon[\"'][^\r\n>]+?((?<=href=[\"']).+?(?=[\"']))")
    };
    @Value("${so-manager-server.galleryPrefix}")
    private String galleryServerUrlPrefix;

    @PostConstruct
    private void init() {
        webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.setJavaScriptErrorListener(new JavaScriptErrorListener() {
            @Override
            public void scriptException(HtmlPage page, ScriptException scriptException) {

            }

            @Override
            public void timeoutError(HtmlPage page, long allowedTime, long executionTime) {

            }

            @Override
            public void malformedScriptURL(HtmlPage page, String url, MalformedURLException malformedURLException) {

            }

            @Override
            public void loadScriptError(HtmlPage page, URL scriptUrl, Exception exception) {

            }

            @Override
            public void warn(String message, String sourceName, int line, String lineSource, int lineOffset) {

            }
        });
    }

    public AssembledData analysisWebPageBaseInfo(String urlStr) {
        AssembledData.Builder builder = new AssembledData.Builder()
                .put("address", urlStr);
        try {
            HtmlPage htmlpage = webClient.getPage(urlStr);
            builder.put("title", htmlpage.getTitleText());
            String head = htmlpage.getHead().asXml();
            String icoUrl = "";
            for (Pattern iconPattern : ICON_PATTERNS) {
                Matcher matcher = iconPattern.matcher(head);
                if (matcher.find()) {
                    //这个时候已经拿到原始的icon地址了
                    String iconUrl = matcher.group(1);
                    //判断是否为http或https路径
                    if (iconUrl.contains("http")) {
                        icoUrl = iconUrl;
                    }//判断是否为相对路径或根路径
                    else if (iconUrl.charAt(0) == '/') {
                        URL url = new URL(urlStr);
                        if (iconUrl.charAt(1) == '/') {
                            iconUrl = url.getProtocol() + ":" + iconUrl;
                        } else {
                            iconUrl = url.getProtocol() + "://" + url.getHost() + iconUrl;
                        }
                    } else {
                        iconUrl = urlStr + "/" + iconUrl;
                    }
                    icoUrl = iconUrl;
                }
            }
            if (icoUrl.length() < 1) {
                icoUrl = getDefaultFavIco(urlStr);
            }

            return builder.put("icon", icoUrl)
                    .build();
        } catch (Exception e) {
            return builder.put("icon", getDefaultFavIco(urlStr))
                    .put("error", e.getMessage())
                    .build();
        }
    }

    public AssembledData analysisWebArticle(String url, String type, String target) {
        AssembledData.Builder resultBuilder = new AssembledData.Builder();
        Document doc = simpleGet(url);

        List<CrawlerRule> crawlerRules = crawlerRuleMapper.selectCrawlerRuleListByHost(URLUtil.url(url).getHost());
        if (crawlerRules.size() != 1) {
            logServiceError(new IllegalArgumentException("crawlerRules.size() != 1"));
        }
        CrawlerRule articleRule = crawlerRules.get(0);
        AssembledData targetRule = new AssembledData.Builder().putAll(articleRule.getTargetRule()).build();

        // step 1. extract article
        type = targetRule.getJSONObject("title").getString("type");
        target = targetRule.getJSONObject("title").getString("target");
        String title = extractContent(doc, type, target);
        int count = 0;
        while (StringUtils.isEmpty(title)) {
            if (count++ > 3) {
                logServiceError(new Exception(), "多次获取文章标题失败！");
            }
            // simpleGet failed will use webClient way
            doc = webClientGet(url);
            title = extractContent(doc, type, target);
        }
        resultBuilder.put("title", Jsoup.parse(title).text());
        type = targetRule.getJSONObject("content").getString("type");
        target = targetRule.getJSONObject("content").getString("target");
        String content = extractContent(doc, type, target);

        // step 2. replace article img
        Document contentDoc = Jsoup.parse(content);
        cacheArticleImg(contentDoc, targetRule, url);

        // step 3. exclude Elements
        String excludeRule = articleRule.getExcludeRule();
        if (StringUtils.isNotEmpty(excludeRule)) {
            List<AssembledData> excludeList = JSON.parseArray(excludeRule)
                    .stream()
                    .map(m -> new AssembledData.Builder().putAll(m).build())
                    .collect(Collectors.toList());
            for (AssembledData m : excludeList) {
                String excludeType = m.getString("type");
                switch (HtmlElementType.valueOf(excludeType)) {
                    case STRING:
                        contentDoc = Jsoup.parse(contentDoc.toString().replaceAll(m.getString("target"), m.getString("replacement")));
                        break;
                    case ATTR:
                        contentDoc.select(m.getString("selector")).stream().forEach(s -> s.removeAttr(m.getString("attr")));
                        break;
                    case ID:
                    case CLASS:
                    case TAG:
                        contentDoc.select(m.getString("selector")).stream().forEach(s -> s.remove());
                        break;
                    default:
                }
            }
        }

        // step 4. beautify style
        String beautifyStyle = articleRule.getBeautifyRule();
        if (StringUtils.isEmpty(beautifyStyle)) {
            beautifyStyle = configService.selectConfigByKey("so.article.commonStyle");
        }
        Document root = Jsoup.parse("<html><body></body></html>");
        root.selectFirst("body").appendElement("style").text(beautifyStyle);
        if (ObjectUtil.isNotEmpty(contentDoc.selectFirst("body"))) {
            root.selectFirst("body").appendChild(contentDoc.selectFirst("body").tagName("div").attr("id", "reading-later"));
        } else {
            root.selectFirst("body").appendChild(contentDoc.createElement("div").attr("id", "reading-later").appendChild(contentDoc));
        }
        root.selectFirst("#reading-later").prependElement("h1")
                .attr("style", "font-weight:bold;font-size:32px;text-align:center;padding-top:20px;padding-bottom:10px;")
                .text(resultBuilder.source().getString("title"));

        resultBuilder.put("content", root.toString());

        return resultBuilder.build();
    }

    private void cacheArticleImg(Document doc, AssembledData rule, String url) {
        String urlMd5 = md5ForString(url);
        String attrKey = "src";
        if (ObjectUtil.isNotEmpty(rule.getJSONObject("img"))) {
            attrKey = rule.getJSONObject("img").getString("attr");
        }
        for (Element image : doc.getElementsByTag("img")) {
            String img = image.attr(attrKey);
            String targetDirPath = constructCachePath("article", "img", urlMd5);
            String newImgUrl = format("{}article/{}-{}", galleryServerUrlPrefix, urlMd5, md5ForString(img));
            checkDirExistAndCreate(targetDirPath);
            String targetFilePath = constructPath(targetDirPath, md5ForString(img) + ".jpg");
            if (!new File(targetFilePath).exists()) {
                if (img.contains("http")) {
                    downloadImg(img, targetFilePath);
                } else if (img.contains("base64")) {
                    try {
                        ImageUtils.base64ToImgFile(img, targetFilePath);
                    } catch (IOException e) {
                        logError(e, "文章base64图片写入失败");
                    }
                } else if (img.startsWith("/")) {
                    downloadImg(getHostWithProtocol(url) + img, targetFilePath);
                }
            }
            image.clearAttributes();
            image.attr("src", newImgUrl);
        }
    }

    private String extractContent(Document doc, String type, String target) {
        Element element = null;
        switch (HtmlElementType.valueOf(type)) {
            case ID:
                element = doc.getElementById(target);
                break;
            case CLASS:
                element = doc.selectFirst(format(".{}", target));
                break;
            case TAG:
                element = doc.selectFirst(target);
                break;
            default:
        }
        if (ObjectUtil.isEmpty(element)) {
            return "";
        }
        return element.toString();
    }

    private Document webClientGet(String url) {
        HtmlPage htmlpage = null;
        try {
            webClient.waitForBackgroundJavaScript(1000);
            htmlpage = webClient.getPage(url);
        } catch (Exception e) {
            logError(e, format("webClient请求url({})出错!", url));
        }
        String html = htmlpage.getWebResponse().getContentAsString();
        Document doc = Jsoup.parse(html);

        return doc;
    }

    private void downloadImg(String url, String downloadPath) {
        try {
            Page page = webClient.getPage(url);
            InputStream contentAsStream = page.getWebResponse().getContentAsStream();
            FileOutputStream fos = new FileOutputStream(downloadPath);
            IOUtils.write(
                    IOUtils.readFully(contentAsStream, (int) page.getWebResponse().getContentLength()),
                    fos);
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Document simpleGet(String url) {
        String html = HttpUtil.get(url);
        return Jsoup.parse(html);
    }

    private String getDefaultFavIco(String urlStr) {
        return getHostWithProtocol(urlStr) + "/favicon.ico";
    }

    private String getHostWithProtocol(String urlStr) {
        URL url = URLUtil.url(urlStr);
        return url.getProtocol() + "://" + url.getHost();
    }

    public byte[] getThirdPartArticleImgCache(String id) {
        String[] md5 = id.split("-");
        if (md5.length != 2) {
            logServiceError(new IllegalArgumentException(id));
        }
        String targetFilePath = constructCachePath("article", "img", md5[0], md5[1] + ".jpg");
        return ImageUtils.getImage(targetFilePath);
    }
}
