package com.sleepy.manager.main.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.github.javafaker.Faker;
import com.sleepy.manager.main.common.AssembledData;
import com.sleepy.manager.main.common.UnionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author captain1920
 * @classname MockController
 * @description TODO
 * @date 2022/6/25 15:43
 */
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/faker")
public class MockController {
    Faker faker = new Faker(Locale.CHINA);

    @GetMapping("/common")
    public UnionResponse getCommonInfo() {
        List<AssembledData> data = new ArrayList<>();
        for (int i = 0; i < RandomUtil.randomInt(12, 20); i++) {
            data.add(new AssembledData.Builder()
                    .put("title", faker.zelda().game())
                    .put("intro", faker.pokemon().location())
                    .put("name", faker.name().fullName())
                    .put("address", faker.address().fullAddress())
                    .put("color1", faker.color().hex())
                    .put("color2", faker.color().hex())
                    .put("year", DateUtil.year(faker.date().birthday()))
                    .put("createTime", DateUtil.formatDateTime(faker.date().birthday()))
                    .put("updateTime", DateUtil.formatDateTime(faker.date().birthday()))
                    .put("bool1", faker.bool().bool())
                    .put("bool2", faker.bool().bool())
                    .build());
        }
        return new UnionResponse.Builder()
                .data(data)
                .build();
    }


    @GetMapping("/movie-local")
    public UnionResponse getFundsStrategyReport() {
        List<AssembledData> data = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            data.add(new AssembledData.Builder()
                    .put("fileName", faker.name().title())
                    .put("dirName", faker.file().fileName())
                    .put("name", faker.name().name())
                    .put("year", DateUtil.year(faker.date().birthday()))
                    .put("hasCover", faker.bool().bool())
                    .put("hasHeadCover", faker.bool().bool())
                    .put("hasSub", faker.bool().bool())
                    .put("hasTrailer", faker.bool().bool())
                    .build());
        }
        return new UnionResponse.Builder()
                .data(data)
                .build();
    }
}
