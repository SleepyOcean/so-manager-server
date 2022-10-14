package com.sleepy.manager.main.processor;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.sleepy.manager.common.utils.StringUtils;
import com.sleepy.manager.common.utils.file.ImageUtils;
import com.sleepy.manager.main.adpater.WebClientAdapter;
import com.sleepy.manager.main.common.AssembledData;
import com.sleepy.manager.main.common.HtmlElementType;
import com.sleepy.manager.system.domain.ArticleReading;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.sleepy.manager.common.utils.LogUtils.logError;
import static com.sleepy.manager.common.utils.LogUtils.logServiceError;
import static com.sleepy.manager.common.utils.StringUtils.format;
import static com.sleepy.manager.common.utils.StringUtils.md5ForString;
import static com.sleepy.manager.common.utils.file.FileUtils.*;
import static com.sleepy.manager.main.common.BizConst.ARTICLE_STYLE_CONFIG_KEY;

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
    @Autowired
    WebClientAdapter webClientAdapter;
    @Value("${so-manager-server.galleryPrefix}")
    private String galleryServerUrlPrefix;
    private Cache<String, CrawlerRule> articleCrawlerRuleCache;

    //设置解析网页favicon.ico的link的正则表达式
    private static final Pattern[] ICON_PATTERNS = new Pattern[]{
            Pattern.compile("rel=[\"']shortcut icon[\"'][^\r\n>]+?((?<=href=[\"']).+?(?=[\"']))"),
            Pattern.compile("((?<=href=[\"'])[^\r\n\"']+?(?=[\"']))[^\n\r<]+?rel=[\"']shortcut icon[\"']"),
            Pattern.compile("property=[\"']og:image[\"'][^\n\r>]+?((?<=content=[\"“]).+?(?=[\"']))"),
            Pattern.compile("sizes=[\"'](192x192|96x96|32x32|16x16)[\"'][^\n\r>]+?((?<=href=[\"']).+?(?=[\"']))"),
            Pattern.compile("((?<=href=[\"'])[^\n\r\"']+?(?=[\"']))\"sizes=[\"'](192x192|96x96|32x32|16x16)[\"']"),
            Pattern.compile("rel=[\"']icon[\"'][^\r\n>]+?((?<=href=[\"']).+?(?=[\"']))")
    };

    @PostConstruct
    private void init() {
        // 文章爬取规则缓存 容量20，过期时间12h
        articleCrawlerRuleCache = CacheUtil.newLFUCache(20, 12 * 60 * 60 * 1000);
    }

    public AssembledData analysisWebPageBaseInfo(String urlStr) {
        AssembledData.Builder builder = new AssembledData.Builder()
                .put("address", urlStr);
        try {
            HtmlPage htmlpage = webClientAdapter.getPage(urlStr);
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

    public ArticleReading analysisWebArticle(String url, String type, String target) {
        Document doc = simpleGet(url);
        String host = URLUtil.url(url).getHost();
        CrawlerRule articleRule = articleCrawlerRule(host);
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
        String titleString = Jsoup.parse(title).text();
        type = targetRule.getJSONObject("content").getString("type");
        target = targetRule.getJSONObject("content").getString("target");
        String content = extractContent(doc, type, target);

        // step 2. replace article img
        Document contentDoc = Jsoup.parse(content);
        List<String> articleImgList = cacheArticleImg(contentDoc, targetRule, url);

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

        ArticleReading articleReading = new ArticleReading();
        articleReading.setTitle(titleString);
        articleReading.setContent(contentDoc.toString());
        articleReading.setSource(url);
        articleReading.setHost(host);
        articleReading.setMd5(md5ForString(url));
        if (ObjectUtil.isNotEmpty(contentDoc.selectFirst("p"))) {
            articleReading.setIntro(contentDoc.selectFirst("p").text());
        }
        if (articleImgList.size() > 0) {
            articleReading.setCover(articleImgList.get(0));
        }

        return articleReading;
    }

    private CrawlerRule articleCrawlerRule(String host) {
        CrawlerRule crawlerRule = articleCrawlerRuleCache.get(host);
        if (ObjectUtil.isEmpty(crawlerRule)) {
            crawlerRule = crawlerRuleMapper.selectArticleCrawlerRuleListByHost(host);
            articleCrawlerRuleCache.put(host, crawlerRule);
        }
        return crawlerRule;
    }

    public String beautifyArticle(ArticleReading articleReading) {
        CrawlerRule articleRule = articleCrawlerRule(articleReading.getHost());
        String beautifyStyle = articleRule.getBeautifyRule();
        if (StringUtils.isEmpty(beautifyStyle)) {
            beautifyStyle = configService.selectConfigByKey(ARTICLE_STYLE_CONFIG_KEY);
        }
        Document contentDoc = Jsoup.parse(articleReading.getContent());
        Document root = Jsoup.parse("<html><body></body></html>");
        root.selectFirst("body").appendElement("style").text(beautifyStyle);
        if (ObjectUtil.isNotEmpty(contentDoc.selectFirst("body"))) {
            root.selectFirst("body").appendChild(contentDoc.selectFirst("body").tagName("div").attr("id", "reading-later"));
        } else {
            root.selectFirst("body").appendChild(contentDoc.createElement("div").attr("id", "reading-later").appendChild(contentDoc));
        }
        root.selectFirst("#reading-later").prependElement("h1")
                .attr("style", "font-weight:bold;font-size:32px;text-align:center;padding-top:20px;padding-bottom:10px;")
                .text(articleReading.getTitle());
        return root.toString();
    }

    private List<String> cacheArticleImg(Document doc, AssembledData rule, String url) {
        List<String> articleImgList = new ArrayList<>();
        String urlMd5 = md5ForString(url);
        String attrKey = "src";
        if (ObjectUtil.isNotEmpty(rule.getJSONObject("img"))) {
            attrKey = rule.getJSONObject("img").getString("attr");
        }
        for (Element image : doc.getElementsByTag("img")) {
            String img = image.attr(attrKey);
            String targetDirPath = constructArticleReadingImgCachePath(urlMd5);
            String newImgUrl = format("{}article/{}-{}", galleryServerUrlPrefix, urlMd5, md5ForString(img));
            checkDirExistAndCreate(targetDirPath);
            String targetFilePath = constructPath(targetDirPath, md5ForString(img) + ".jpg");
            if (!new File(targetFilePath).exists()) {
                // TODO 图片获取规则待优化。考虑通过参数控制而非硬Code
                if (img.contains("http")) {
                    downloadImg(img, targetFilePath);
                } else if (img.contains("base64")) {
                    try {
                        ImageUtils.base64ToImgFile(img, targetFilePath);
                    } catch (IOException e) {
                        logError(e, "文章base64图片写入失败");
                    }
                } else if (img.startsWith("//")) {
                    downloadImg("https:" + img, targetFilePath);
                } else if (img.startsWith("/")) {
                    downloadImg(getHostWithProtocol(url) + img, targetFilePath);
                }
            }
            image.clearAttributes();
            image.attr("src", newImgUrl);
            articleImgList.add(newImgUrl);
        }
        return articleImgList;
    }

    public String constructArticleReadingImgCachePath(String urlMd5) {
        return constructDataPath("article", "img", urlMd5);
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
            htmlpage = webClientAdapter.getPage(url, 1000);
        } catch (Exception e) {
            logError(e, format("webClient请求url({})出错!", url));
        }
        String html = htmlpage.getWebResponse().getContentAsString();
        Document doc = Jsoup.parse(html);

        return doc;
    }

    private void downloadImg(String url, String downloadPath) {
        try {
            WebResponse response = webClientAdapter.getWebResponse(url);
            InputStream contentAsStream = response.getContentAsStream();
            FileOutputStream fos = new FileOutputStream(downloadPath);
            IOUtils.write(
                    IOUtils.readFully(contentAsStream, (int) response.getContentLength()),
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
        String targetFilePath = constructArticleReadingImgCachePath(constructPath(md5[0], md5[1] + ".jpg"));
        return ImageUtils.getImage(targetFilePath);
    }
}
