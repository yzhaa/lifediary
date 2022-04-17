package yzh.lifediary.okhttp;


import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RequestBody {


    private final HashMap<String, Object> params;

    public static final String ImageHeader = "Content-Disposition: form-data;name=images; filename";
    public static final String ImageType = "multipart/form-data;boundary=";

    private String type = "application/x-www-form-urlencoded";

    private byte[] postDataBytes;
    private List<Bitmap> bitmaps;



    private RequestBody() {
        params = new HashMap<>();
    }


    public void onWriteBody(OutputStream outputStream,String boundary) throws IOException {
        getContentLength(boundary);
        outputStream.write(postDataBytes);

    }

   private  void  getContentLength(String BOUNDARY) {
        StringBuilder postData = new StringBuilder();
       String TWO_HYPHENS = "--";
       String LINE_END = "\r\n";
       if(type.equals(ImageType)){
           for (Map.Entry<String, Object> param : params.entrySet()) {
               postData.append(TWO_HYPHENS);
               postData.append(BOUNDARY);
               postData.append(LINE_END);
               //健是以字符串对形式，因此有" 引号
               postData.append("Content-Disposition: form-data; name=\"").append(param.getKey()).append("\"");
               postData.append(LINE_END);
               postData.append("Content-Type: " + "text/plain" );

               //健值对，之间有两个换行
               postData.append(LINE_END);
//               postData.append("Content-Lenght: "+param.getKey().length());
//               postData.append(LINE_END);
               postData.append(LINE_END);
               postData.append(param.getValue());
               postData.append(LINE_END);
           }
           //结束也要记得加上这两个
           postData.append(LINE_END);
       }
      else  for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) {
                postData.append('&');
            }
            try {
                postData.append(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8.toString()));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), StandardCharsets.UTF_8.toString()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
        postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);

    }

    public String getType() {
        return type;
    }

    public static class Builder {
        private final RequestBody requestBody;

        public Builder() {
            requestBody = new RequestBody();
        }

        public Builder addParam(String key, String value) {
            requestBody.params.put(key, value);
            return this;
        }

        public Builder type(String type) {
            requestBody.type = type;
            return this;
        }

        public Builder writeBitmap(List<Bitmap> bitmaps) {
            requestBody.bitmaps = bitmaps;
            return this;
        }

        public RequestBody build() {

            return requestBody;
        }


    }

    public List<Bitmap> getBitmaps() {
        return bitmaps;
    }
}
