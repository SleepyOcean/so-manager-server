package com.sleepy.manager.main.service.impl;

import cn.hutool.core.util.ArrayUtil;
import com.sleepy.manager.blog.common.AssembledData;
import com.sleepy.manager.system.domain.Movie;
import com.sleepy.manager.utils.DataSnapshotUtils;
import org.junit.Test;

import java.io.IOException;

import static com.sleepy.manager.common.utils.DateUtils.dateTimeNow;
import static com.sleepy.manager.common.utils.StringUtils.format;

/**
 * @author captain1920
 * @classname ZiMuKuSubtitleCrawlServiceImplTest
 * @description TODO
 * @date 2022/5/1 20:26
 */
public class ZiMuKuSubtitleCrawlServiceImplTest {
    ZiMuKuSubtitleCrawlServiceImpl ziMuKuSubtitleCrawlService = new ZiMuKuSubtitleCrawlServiceImpl();

    @Test
    public void rematchNasMovieSubTest() throws IOException {
        AssembledData subMatchMap = ziMuKuSubtitleCrawlService.rematchNasMovieSub();
        DataSnapshotUtils.writeDataSnapshotToFile(format("local/DataSnapshot/subMatchMap-{}.json", dateTimeNow()), subMatchMap);
    }

    @Test
    public void listSubtitlesTest() {
        Movie movie = new Movie();
        movie.setImdbid("tt0470752");
        movie.setDetail("{\"studio\":\"Film4 Productions\",\"country\":\"英国\",\"year\":\"2014\",\"thumb\":\"https://assets.fanart.tv/fanart/movies/264660/hdmovielogo/ex-machina-546fb15067574.png\",\"premiered\":\"2014-12-16\",\"edition\":\"NONE\",\"source\":\"BLURAY\",\"title\":\"机械姬\",\"outline\":\"片中，伊萨克饰演一名神秘的亿万富翁，格里森饰演他公司的一名程序员，后者由于赢得公司一项幸运大奖而被邀请到老板的别墅共度周末。这栋别墅隐匿于林间，它其实是一座高科技的研究所。在那里，格里森被介绍给名为“伊娃”的人工智能机器人(艾丽西卡·维坎德 饰演)，原来他被邀请到这里的真正目的是进行针对伊娃的“图灵测试”(注：一项由图灵提出的测试机器是否具备人类智能的的著名实验）……\",\"originaltitle\":\"Ex Machina\",\"original_filename\":\"Ex.Machina.2015.2160p.HDR.BluRay.DTS-HDMA.7.1.HEVC-DDR.mkv\",\"plot\":\"片中，伊萨克饰演一名神秘的亿万富翁，格里森饰演他公司的一名程序员，后者由于赢得公司一项幸运大奖而被邀请到老板的别墅共度周末。这栋别墅隐匿于林间，它其实是一座高科技的研究所。在那里，格里森被介绍给名为“伊娃”的人工智能机器人(艾丽西卡·维坎德 饰演)，原来他被邀请到这里的真正目的是进行针对伊娃的“图灵测试”(注：一项由图灵提出的测试机器是否具备人类智能的的著名实验）……\",\"credits\":\"Alex Garland\",\"ratings\":{\"rating\":[{\"@name\":\"themoviedb\",\"@max\":\"10\",\"votes\":\"10823\",\"value\":\"7.6\",\"@default\":\"false\"},{\"@name\":\"imdb\",\"@max\":\"10\",\"votes\":\"511378\",\"value\":\"7.7\",\"@default\":\"true\"}]},\"genre\":\"Science Fiction\",\"fileinfo\":{\"streamdetails\":{\"subtitle\":[{\"language\":\"eng\"},{\"language\":\"eng\"},{\"language\":\"eng\"}],\"video\":{\"codec\":\"HEVC\",\"durationinseconds\":\"6496\",\"aspect\":\"2.4\",\"width\":\"3840\",\"height\":\"1608\"},\"audio\":{\"codec\":\"DTS_X\",\"channels\":\"8\",\"language\":\"eng\"}}},\"top250\":\"0\",\"id\":\"tt0470752\",\"tag\":\"human android relationship\",\"uniqueid\":\"tt0470752\",\"languages\":\"English\",\"director\":\"Alex Garland\",\"fanart\":{\"thumb\":\"http://image.tmdb.org/t/p/original/tucaGAyElYk85zYbpmQjuWxDH3H.jpg\"},\"userrating\":\"0.0\",\"runtime\":\"108\",\"dateadded\":\"2021-12-28 17:05:47\",\"actor\":[{\"role\":\"Caleb Smith\",\"thumb\":\"http://image.tmdb.org/t/p/h632/uDbwncuKlqL0fAuucXSvgakJDrc.jpg\",\"profile\":\"https://www.themoviedb.org/person/93210\",\"name\":\"Domhnall Gleeson\"},{\"role\":\"Ava\",\"thumb\":\"http://image.tmdb.org/t/p/h632/9pmHTbXeRxUF51jJKthmHI49u9z.jpg\",\"profile\":\"https://www.themoviedb.org/person/227454\",\"name\":\"Alicia Vikander\"},{\"role\":\"Nathan Bateman\",\"thumb\":\"http://image.tmdb.org/t/p/h632/dW5U5yrIIPmMjRThR9KT2xH6nTz.jpg\",\"profile\":\"https://www.themoviedb.org/person/25072\",\"name\":\"Oscar Isaac\"},{\"role\":\"Kyoko\",\"thumb\":\"http://image.tmdb.org/t/p/h632/scbRPaGPw3VnR6mrmZQZi6Cuj1m.jpg\",\"profile\":\"https://www.themoviedb.org/person/1457238\",\"name\":\"Sonoya Mizuno\"},{\"role\":\"Jay\",\"thumb\":\"http://image.tmdb.org/t/p/h632/gFPilsTUQlClMiXooO4Q3DSJ5Nr.jpg\",\"profile\":\"https://www.themoviedb.org/person/17199\",\"name\":\"Corey Johnson\"},{\"role\":\"Lily\",\"profile\":\"https://www.themoviedb.org/person/1457239\",\"name\":\"Claire Selby\"},{\"role\":\"Jasmine\",\"profile\":\"https://www.themoviedb.org/person/1394342\",\"name\":\"Symara A. Templeman\"},{\"role\":\"Jade\",\"thumb\":\"http://image.tmdb.org/t/p/h632/sYUEOvA9jVVwHcnytD5O6w49va4.jpg\",\"profile\":\"https://www.themoviedb.org/person/1457240\",\"name\":\"Gana Bayarsaikhan\"},{\"role\":\"Katya\",\"profile\":\"https://www.themoviedb.org/person/1457241\",\"name\":\"Tiffany Pisani\"},{\"role\":\"Amber\",\"thumb\":\"http://image.tmdb.org/t/p/h632/gblJEM8QEESmsHJ20m3UcFAWi6m.jpg\",\"profile\":\"https://www.themoviedb.org/person/1394348\",\"name\":\"Elina Alminas\"},{\"role\":\"Office Girl (uncredited)\",\"thumb\":\"http://image.tmdb.org/t/p/h632/rPSwiXORMUfvac3KXWWHG7R3LC2.jpg\",\"profile\":\"https://www.themoviedb.org/person/1423809\",\"name\":\"Chelsea Li\"},{\"role\":\"Office Worker (uncredited)\",\"profile\":\"https://www.themoviedb.org/person/2696436\",\"name\":\"Dan Pappaspanos\"}],\"watched\":\"false\",\"tmdbid\":\"264660\",\"producer\":[{\"role\":\"Executive Producer\",\"thumb\":\"http://image.tmdb.org/t/p/h632/jThGwDImycWNIkHZK87qAv4uhbX.jpg\",\"name\":\"Tessa Ross\"},{\"role\":\"Casting\",\"thumb\":\"http://image.tmdb.org/t/p/h632/5pOiu0SLr3XCB3a8SDwQ70KHINq.jpg\",\"name\":\"Francine Maisler\"},{\"role\":\"Producer\",\"name\":\"Andrew Macdonald\"},{\"role\":\"Executive Producer\",\"thumb\":\"http://image.tmdb.org/t/p/h632/zIeKeFgBERBHmabgmqZFmgcxqvO.jpg\",\"name\":\"Scott Rudin\"},{\"role\":\"Casting Associate\",\"name\":\"Kathy Driscoll\"},{\"role\":\"Producer\",\"name\":\"Allon Reich\"},{\"role\":\"Line Producer\",\"name\":\"Caroline Levy\"},{\"role\":\"Executive Producer\",\"name\":\"Eli Bush\"},{\"role\":\"Associate Producer\",\"name\":\"Joanne Smith\"},{\"role\":\"Location Manager\",\"name\":\"Alex Gladstone\"},{\"role\":\"Associate Producer\",\"name\":\"Jason Sack\"},{\"role\":\"Casting Associate\",\"name\":\"Melissa Kostenbauder\"},{\"role\":\"Location Manager\",\"name\":\"Per Henry Borch\"},{\"role\":\"Production Coordinator\",\"name\":\"Samantha Black\"},{\"role\":\"Unit Production Manager\",\"name\":\"Sara Desmond\"},{\"role\":\"Production Manager\",\"name\":\"Tor Arne Øvrebø\"},{\"role\":\"Line Producer\",\"name\":\"Jarle Tangen\"},{\"role\":\"Assistant Accountant\",\"name\":\"Tim Orlik\"},{\"role\":\"First Assistant Accountant\",\"name\":\"Daniel Budd\"},{\"role\":\"Location Assistant\",\"name\":\"Saba Kia\"},{\"role\":\"Producer's Assistant\",\"name\":\"Rebecca Cronshey\"},{\"role\":\"Producer's Assistant\",\"name\":\"Agnes Meath Baker\"},{\"role\":\"Production Accountant\",\"name\":\"Nuala Alen-Buckley\"},{\"role\":\"Production Assistant\",\"name\":\"Rory Johnston\"},{\"role\":\"Production Assistant\",\"name\":\"Vivien Kenny\"}]}");
        AssembledData data = ziMuKuSubtitleCrawlService.listSubtitles(movie);
        System.out.println(data.toJSONString());
    }

    @Test
    public void downloadSubtitleTest() {
        String[] s = {"asde", "werh"};
        ArrayUtil.insert(s, 0, "1");
        System.out.println(s);
    }
}
