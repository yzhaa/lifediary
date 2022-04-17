package yzh.lifediary.okhttp;

import java.io.IOException;

public class MyException {
    private  Callback callback;
    private Call  call;
    private IOException ioException;

    public MyException(Callback callback, Call call, IOException ioException) {
        this.callback = callback;
        this.call = call;
        this.ioException = ioException;
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public Call getCall() {
        return call;
    }

    public void setCall(Call call) {
        this.call = call;
    }

    public IOException getIoException() {
        return ioException;
    }

    public void setIoException(IOException ioException) {
        this.ioException = ioException;
    }
}
