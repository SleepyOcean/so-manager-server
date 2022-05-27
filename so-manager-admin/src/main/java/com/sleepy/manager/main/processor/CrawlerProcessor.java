package com.sleepy.manager.main.processor;

import cn.hutool.core.util.URLUtil;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.sleepy.manager.main.common.AssembledData;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author captain1920
 * @classname CrawlerProcessor
 * @description TODO
 * @date 2022/4/11 15:44
 */
@Component
public class CrawlerProcessor {

    //设置解析网页favicon.ico的link的正则表达式
    private static final Pattern[] ICON_PATTERNS = new Pattern[]{
            Pattern.compile("rel=[\"']shortcut icon[\"'][^\r\n>]+?((?<=href=[\"']).+?(?=[\"']))"),
            Pattern.compile("((?<=href=[\"'])[^\r\n\"']+?(?=[\"']))[^\n\r<]+?rel=[\"']shortcut icon[\"']"),
            Pattern.compile("property=[\"']og:image[\"'][^\n\r>]+?((?<=content=[\"“]).+?(?=[\"']))"),
            Pattern.compile("sizes=[\"'](192x192|96x96|32x32|16x16)[\"'][^\n\r>]+?((?<=href=[\"']).+?(?=[\"']))"),
            Pattern.compile("((?<=href=[\"'])[^\n\r\"']+?(?=[\"']))\"sizes=[\"'](192x192|96x96|32x32|16x16)[\"']"),
            Pattern.compile("rel=[\"']icon[\"'][^\r\n>]+?((?<=href=[\"']).+?(?=[\"']))")
    };

    public AssembledData analysisWebPageBaseInfo(String urlStr) {
        AssembledData.Builder builder = new AssembledData.Builder()
                .put("address", urlStr);
        try {
            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setUseInsecureSSL(true);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);

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

    private String getDefaultFavIco(String urlStr) {
        URL url = URLUtil.url(urlStr);
        return url.getProtocol() + "://" + url.getHost() + "/favicon.ico";
    }
}
