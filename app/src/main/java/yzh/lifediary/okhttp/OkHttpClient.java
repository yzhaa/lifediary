package yzh.lifediary.okhttp;


import java.util.LinkedList;
import java.util.List;

public class OkHttpClient {

   public final Dispatcher dispatcher;
    private static OkHttpClient okHttpClient;

    private final List<Interceptor> interceptors = new LinkedList<>();


   private OkHttpClient() {
       dispatcher = new Dispatcher();
       interceptors.add(new Interceptor());
    }

    public Call newCall(Request request) {
        return RealCall.newCall(request, this);
    }

    public static OkHttpClient getOkHttpCline(){
        if (okHttpClient == null) {
            synchronized (OkHttpClient.class) {
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient();
                }
            }
        }
        return okHttpClient;
    }

    public List<Interceptor> getInterceptors(){
       return interceptors;
    }

    public void addInterceptor(Interceptor interceptor){
        interceptors.add(interceptor);
    }
}