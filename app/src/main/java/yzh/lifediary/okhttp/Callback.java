package yzh.lifediary.okhttp;

import java.io.IOException;

public interface Callback {
    public void onFailure(Call call, IOException e) ;


    public void onResponse(Call call, Response response);

}
