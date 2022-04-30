package com.sleepy.manager.main.service.impl;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.sleepy.manager.blog.common.AssembledData;
import com.sleepy.manager.main.helper.MovieHelper;
import com.sleepy.manager.main.service.SubtitleCrawlService;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.MalformedURLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author captain1920
 * @classname ZiMuKuSubtitleCrawlServiceImpl
 * @description TODO
 * @date 2022/4/20 18:46
 */
public class ZiMuKuSubtitleCrawlServiceImpl implements SubtitleCrawlService {

    public static final String ZIMUKU_HOST = "zmk.pw";
    public static final String SUBTITLE_DOWNLOAD_ROOT = "";

    // step 1. search the movie all subtitles and try to automatic match the best, return the best match and all subtitle list
    // step 2. confirm the subtitle download link, and save to the specific file as cache
    // step 3. start the crawl process to download all subtitle and record downloaded subtitle to the specific file as cache for next crawl
    // step 4. read the downloaded subtitle cache and reconstruct the subtitle path with the same construct as movie


    @Override
    public AssembledData listSubtitles(String ttID) {
        try (WebClient webClient = new WebClient(BrowserVersion.CHROME)) {

            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setCssEnabled(false);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);

            String searchUrl = ZIMUKU_HOST + "/search?q=" + ttID;
            webClient.getPage(searchUrl);
            webClient.waitForBackgroundJavaScript(1000);
            HtmlPage htmlpage = webClient.getPage(searchUrl);
            String html = htmlpage.asXml();
            webClient.waitForBackgroundJavaScript(1000);
            Document searchPageDoc = Jsoup.parse(html);

            String subDetailUrl = ZIMUKU_HOST + searchPageDoc.getElementsByClass("litpic").get(0).getElementsByTag("a").get(0).attr("href");
            Thread.sleep(1000);
            HtmlPage subDetailPage = webClient.getPage(subDetailUrl);
            String subDetailHtml = subDetailPage.asXml();

            List<AssembledData> detailList = Jsoup.parse(subDetailHtml).getElementsByTag("tbody").get(0).getElementsByClass("first")
                    .stream().map(p ->
                            new AssembledData.Builder()
                                    .put("title", p.getElementsByTag("a").get(0).attr("title"))
                                    .put("detail", p.getElementsByTag("a").get(0).attr("href"))
                                    .put("download", p.getElementsByTag("a").get(0).attr("href").replace("detail", "dld"))
                                    .build())
                    .collect(Collectors.toList());

            // todo: add automatic subtitle match
            AssembledData bestMatch = MovieHelper.searchBestMatch(detailList, "");

            return new AssembledData.Builder()
                    .put("subtitles", detailList)
                    .put("bestMatch", bestMatch)
                    .build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new AssembledData.Builder().build();
    }

    @Override
    public AssembledData downloadSubtitle(String movieName, String downloadPageRoute) {

        try (WebClient webClient = new WebClient(BrowserVersion.CHROME)) {

            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setCssEnabled(false);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);

            HtmlPage downloadPage = webClient.getPage(ZIMUKU_HOST + downloadPageRoute);
            String downloadHtml = downloadPage.asXml();
            List<AssembledData> downloadLinkList = Jsoup.parse(downloadHtml).getElementsByClass("down").get(0).getElementsByTag("a").stream().map(p -> new AssembledData.Builder().put("download", p.attr("href")).put("title", p.text()).build()).collect(Collectors.toList());
            Thread.sleep(10000);

            String downloadLink = ZIMUKU_HOST + downloadLinkList.get(0).getString("download");
            webClient.getPage(downloadLink);
            webClient.waitForBackgroundJavaScript(10000);
            webClient.addRequestHeader("Referer", downloadLink);
            Page page = webClient.getPage(downloadLink + "?security_verify_data=313531322c393832");
            webClient.waitForBackgroundJavaScript(10000);

            String fileNameContent = page.getWebResponse().getResponseHeaderValue("Content-Disposition");
            String fileName = fileNameContent.substring(fileNameContent.indexOf("=") + 2, fileNameContent.length() - 1);
            InputStream contentAsStream = page.getWebResponse().getContentAsStream();
            String downloadPath = SUBTITLE_DOWNLOAD_ROOT + movieName + File.separator + fileName;
            IOUtils.write(
                    IOUtils.readFully(contentAsStream, (int) page.getWebResponse().getContentLength()),
                    new FileOutputStream(downloadPath));
            return new AssembledData.Builder().put("downloadedPath", downloadPath).build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new AssembledData.Builder().build();
    }
}
