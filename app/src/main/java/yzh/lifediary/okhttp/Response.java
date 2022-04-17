package yzh.lifediary.okhttp;


import java.util.List;
import java.util.Map;

public class Response {

    public final int statusCode;
    public final String message;
    public final String body;

    public final Call  call;
    public final Map<String, List<String>> headFiles;

    public Response(int statusCode, String message, String body, Call call, Map<String, List<String>> headFiles) {
        this.statusCode = statusCode;
        this.message = message;
        this.body = body;
        this.call = call;
        this.headFiles = headFiles;
    }
}
