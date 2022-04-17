package yzh.lifediary.okhttp;


import android.content.Context;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import kotlin.io.ByteStreamsKt;
import yzh.lifediary.MyApplication;
import yzh.lifediary.util.Constant;
import yzh.lifediary.view.MainActivity;
import yzh.lifediary.view.MainActivityKt;


public class RealCall implements Call {

    private final OkHttpClient client;
    private final Request originalRequest;
    private AsyncCall asyncCall;

    public RealCall(Request originalRequest, OkHttpClient client) {
        this.client = client;
        this.originalRequest = originalRequest;
    }

    public static Call newCall(Request request, OkHttpClient okHttpClient) {
        return new RealCall(request, okHttpClient);
    }

    //异步请求
    @Override
    public void enqueue(Callback callback) {
        //异步交给线程池
        asyncCall = new AsyncCall(callback);
        client.dispatcher.enqueue(asyncCall);
    }

    //同步请求
    @Override
    public Response execute() {
        try {
            return run();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private static final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {

            if (msg.obj instanceof HandlerMessage) {
                HandlerMessage r = (HandlerMessage) msg.obj;
                if (r.callback != null) {
                    r.callback.onResponse(r.response.call, r.response);
                }
            }
            if (msg.obj instanceof MyException) {
                MyException r = (MyException) msg.obj;
                if (r.getCallback() != null) {
                    r.getCallback().onFailure(r.getCall(), r.getIoException());
                }
            }
        }
    };

    private Response run() throws IOException {


        // 基于 HttpUrlConnection , OkHttp = Socket + okio(IO)
        final Request request = originalRequest;
        URL url = new URL(request.url);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setReadTimeout(8000);
        urlConnection.setConnectTimeout(5000);
        urlConnection.setRequestMethod(request.method.name());
        urlConnection.setDoOutput(request.method.getPost());
        //Post方式不能缓存,需手动设置为false
        urlConnection.setUseCaches(!request.method.getPost());

        RequestBody requestBody = request.requestBody;
        String BOUNDARY = "----------" + System.currentTimeMillis();


        urlConnection.setRequestProperty("Connection", "Keep-Alive");
        urlConnection.setRequestProperty("Charset", "UTF-8");
        if (originalRequest.isFetchCookie) {
            urlConnection.setRequestProperty("cookie",
                    MyApplication.context.getSharedPreferences(Constant.INSTANCE.getLoginInfo(), Context.MODE_PRIVATE).getString("cookie", ""));
        }
        if (requestBody != null) {
            // 头信息
            String type = requestBody.getType();
            if (type.equals(RequestBody.ImageType)) type = type + BOUNDARY;
            urlConnection.setRequestProperty("Content-Type", type);
//            urlConnection.setRequestProperty("Content-Length", Long.toString(requestBody.getContentLength()));
        }

        Map<String, String> headers = request.headers;
        if (headers != null) {
            Set<Map.Entry<String, String>> entries = headers.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                Log.d(MainActivityKt.TAG, "run:  执行了这个1111");
                urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        urlConnection.connect();

        if (requestBody != null) {
            requestBody.onWriteBody(urlConnection.getOutputStream(),BOUNDARY);
            if (requestBody.getBitmaps()!=null) {
                writeFile(requestBody.getBitmaps(),BOUNDARY,urlConnection);
            }
        }
        Response r = new Response(urlConnection.getResponseCode(), urlConnection.getResponseMessage(),
                convertStreamToString(urlConnection.getInputStream()), this, urlConnection.getHeaderFields());

        urlConnection.disconnect();
        return r;
    }


    //并没有集成Callable
    final class AsyncCall implements Runnable {

        Callback callback;

        public AsyncCall(Callback callback) {
            this.callback = callback;
        }

        @Override
        public void run() {
            try {
                Response response = RealCall.this.run();
                Message message = Message.obtain();
                message.obj = new HandlerMessage(response, callback);
                List<String> cookies = response.headFiles.get("Set-Cookie");
                if (originalRequest.saveCookie && cookies != null && cookies.size() > 0) {
                    MyApplication.context.getSharedPreferences(Constant.INSTANCE.getLoginInfo(), Context.MODE_PRIVATE).edit().
                            putString("cookie", cookies.get(0)).apply();
                }
                List<Interceptor> list = OkHttpClient.getOkHttpCline().getInterceptors();
                for (Interceptor i:list) {
                   if(!i.handle(response)) return;
                }
                mHandler.sendMessage(message);
            } catch (IOException e) {
                Message message = Message.obtain();
                message.what = -1;
                message.obj = new MyException(callback, RealCall.this, e);
                mHandler.sendMessage(message);

            }

        }
    }


    private <T> void writeFile(List<Bitmap> list, String BOUNDARY, HttpURLConnection con) throws IOException {
        for (int i = 0; i < list.size(); i++) {
            String sb = "--" + // 必须多两道线
                    BOUNDARY +
                    "\r\n" +
                    "Content-Disposition: form-data;name=\"images\";filename=\"" + i +".png"+ "\"\r\n" +
                    "Content-Type:application/octet-stream\r\n\r\n";
            byte[] head = sb.getBytes(StandardCharsets.UTF_8);
            // 获得输出流
            OutputStream out = new DataOutputStream(con.getOutputStream());
            // 输出表头
            out.write(head);
            // 文件正文部分
            // 把文件已流文件的方式 推入到url中
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            list.get(i).compress(Bitmap.CompressFormat.PNG, 100, bao);

            out.write(bao.toByteArray());

            // 结尾部分
            //---注意 --------
            //---注意 ，中间的文件没有最后--，只有最后一个文件有--，否则提前结束
            byte[] foot;
            if (i == list.size() - 1) {
                foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes(StandardCharsets.UTF_8); // 定义最后数据分隔线

            } else {
                foot = ("\r\n--" + BOUNDARY + "\r\n").getBytes(StandardCharsets.UTF_8); // 定义最后数据分隔线
            }

            out.write(foot);
            //---注意 --------
            //---注意 --------

        }



}

}
