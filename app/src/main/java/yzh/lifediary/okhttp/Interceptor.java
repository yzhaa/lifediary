package yzh.lifediary.okhttp;

import android.content.Intent;
import android.util.Log;

import com.yzh.myjson.Gson;
import com.yzh.myjson.TypeToken;

import yzh.lifediary.MyApplication;
import yzh.lifediary.NetWork.LoginOutBroadCastKt;
import yzh.lifediary.entity.LoginResponse;

import yzh.lifediary.view.MainActivityKt;

public class Interceptor {

    public boolean handle(Response response){

        LoginResponse message = Gson.getGson().fromJson(response.body, new TypeToken<LoginResponse>() {}.getType());
        Log.d(MainActivityKt.TAG, "handle: "+response.body);
        if (message.getMessage().equals("没有权限，请先登录")) {
            MyApplication.context.sendBroadcast(new Intent().setAction(LoginOutBroadCastKt.LOGIN_OUT));
            return  false;
        }
        return true;
    }

}
