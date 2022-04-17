package yzh.lifediary.okhttp;

public class HandlerMessage {

    public  final Response response;
    public final Callback callback;

    public HandlerMessage(Response response, Callback callback) {
        this.response = response;
        this.callback = callback;
    }
}
