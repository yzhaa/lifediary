package yzh.lifediary.okhttp;

import java.util.HashMap;
import java.util.Map;


public class Request {

    final String url;//url
    final Method method;//请求方式
    final Map<String, String> headers;//头信息
    final RequestBody requestBody;//请求体,用于post请求
    final boolean saveCookie;
    final boolean isFetchCookie;

    private Request(Builder builder) {
        if(builder.BaseUrl!=null&&!builder.BaseUrl.equals(""))        this.url =builder.BaseUrl+"/"+ builder.url;
        else this.url=builder.url;
        this.method = builder.method;
        this.headers = builder.headers;
        this.requestBody = builder.requestBody;
        this.saveCookie = builder.saveCookie;
        this.isFetchCookie = builder.isFetchCookie;
    }
    public static enum Method{
        GET,
        POST;

       boolean getPost(){
            return this==POST;
        }
    }

    public static class Builder {
        //为什么要这样，不能像RequestBody，

        String url;//url
        Method method;//请求方式
        Map<String, String> headers;//头信息
        RequestBody requestBody;//请求体,用于post请求
        boolean saveCookie;
        String BaseUrl;
        boolean isFetchCookie=true;




        public Builder() {
            method = Method.GET;
            headers = new HashMap<>();
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder saveCookie() {
            saveCookie=true;
            return this;
        }
        public Builder get() {
            method = Method.GET;
            return this;
        }

        public void noFetchCookie() {
            isFetchCookie=false;
        }

        public Builder post(RequestBody body) {
            method = Method.POST;
            this.requestBody = body;
            return this;
        }

        public Builder headers(String key, String value) {
            headers.put(key, value);
            return this;
        }

        public Builder headers( Map<String, String> map) {
            headers.putAll(map);
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            BaseUrl = baseUrl;
            return this;
        }

        public Request build() {
            return new Request(this);
        }
    }




}
