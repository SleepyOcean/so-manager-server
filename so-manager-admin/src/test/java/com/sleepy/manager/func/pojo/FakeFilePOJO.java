package com.sleepy.manager.func.pojo;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.List;

/**
 * 假文件对象
 *
 * @author gehoubao
 * @create 2021-01-20 13:51
 **/
@Data
public class FakeFilePOJO {
    private String type;
    private String name;

    private List<FakeFilePOJO> sub;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}