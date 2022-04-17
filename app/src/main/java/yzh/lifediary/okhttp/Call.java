package yzh.lifediary.okhttp;



public interface Call {

    void  enqueue(Callback callback);


    Response execute();
}
