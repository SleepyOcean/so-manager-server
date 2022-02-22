package com.sleepy.manager.blog.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 组合数据
 *
 * @author gehoubao
 * @create 2021-10-22 15:52
 **/
@JsonIgnoreProperties({"params"})
public class AssembledData extends JSONObject {

    public static class Builder {
        private final AssembledData data;

        public Builder() {
            data = new AssembledData();
        }

        public AssembledData source() {
            return data;
        }

        public Builder put(String key, Object value) {
            data.put(key, value);
            return this;
        }

        public Builder putAll(JSONObject object) {
            data.putAll(object);
            return this;
        }

        public Builder putAll(Object object) {
            return putAll(JSON.parseObject(JSON.toJSONString(object)));
        }

        public AssembledData build() {
            return this.data;
        }
    }
}