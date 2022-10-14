package com.sleepy.manager.main.adpater;

import cn.hutool.core.util.StrUtil;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static cn.hutool.log.StaticLog.error;

/**
 * @author Captain1920
 * @classname WebClientAdapter
 * @description TODO
 * @date 2022/7/3 10:58
 */
@Component
public class WebClientAdapter {
    private static final long DEFAULT_WAIT_JS_MIL_SEC = 1000;
    private static final String PROXY_HOST = "localhost";
    private static final int PROXY_PORT = 41091;
    WebClient webClient;

    public static WebClient create() {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
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
        return webClient;
    }

    @PostConstruct
    private void init() {
        webClient = create();
    }

    /**
     * 解决webClient.getPage(url)返回非HtmlPage对象（例如UnexpectedPage）创建
     *
     * @param url
     * @return
     */
    public WebResponse getWebResponse(String url) {
        try {
            return webClient.getPage(url).getWebResponse();
        } catch (IOException e) {
            String msg = StrUtil.format("webclient get web response failed! url[{}]", url);
            error(e, msg);
            throw new RuntimeException(msg);
        }
    }

    public HtmlPage getPage(String url) {
        try {
            return webClient.getPage(url);
        } catch (IOException e) {
            String msg = StrUtil.format("webclient get html page failed! url[{}]", url);
            error(e, msg);
            throw new RuntimeException(msg);
        }
    }

    public HtmlPage getPage(String url, long waitJsMilSec) {
        webClient.waitForBackgroundJavaScript(waitJsMilSec);
        HtmlPage page = getPage(url);
        webClient.waitForBackgroundJavaScript(DEFAULT_WAIT_JS_MIL_SEC);
        return page;
    }

    public HtmlPage getPageWithProxy(String url) {
        setProxy();
        HtmlPage page = getPage(url);
        resetProxy();
        return page;
    }

    public HtmlPage getPageWithProxy(String url, long waitJsMilSec) {
        setProxy();
        HtmlPage page = getPage(url, waitJsMilSec);
        resetProxy();
        return page;
    }

    private void setProxy() {
        webClient.getOptions().setProxyConfig(new ProxyConfig(PROXY_HOST, PROXY_PORT, null));
    }

    private void resetProxy() {
        webClient.getOptions().setProxyConfig(new ProxyConfig());
    }

    @PreDestroy
    private void close() {
        webClient.close();
    }
}
