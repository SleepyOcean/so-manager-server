package com.sleepy.manager.blog.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Union Response
 *
 * @author gehoubao
 * @create 2021-10-05 8:50
 **/
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnionResponse {

    private int code;

    private String message;

    private Object data;

    private UnionResponse() {
    }

    public static class Builder {
        private final UnionResponse response;

        public Builder() {
            response = new UnionResponse();
        }

        public Builder status(HttpStatus status) {
            this.response.code = status.value();
            this.response.message = status.toString();
            return this;
        }

        public Builder code(int code) {
            this.response.code = code;
            return this;
        }

        public Builder message(String message) {
            this.response.message = message;
            return this;
        }

        public Builder data(Object data) {
            this.response.data = data;
            return this;
        }

        public UnionResponse build() {
            return this.response;
        }
    }
}