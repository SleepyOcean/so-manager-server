package com.sleepy.manager.main.controller;

import cn.hutool.core.date.DateUtil;
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

/**
 * @author captain1920
 * @classname MockController
 * @description TODO
 * @date 2022/6/25 15:43
 */
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/mock")
public class MockController {
    Faker faker = new Faker();

    @GetMapping("/movie/local")
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
