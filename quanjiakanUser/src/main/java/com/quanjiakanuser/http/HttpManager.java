package com.quanjiakanuser.http;

import android.net.Uri;

import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.util.SignatureUtil;
import com.google.gson.Gson;
import com.quanjiakanuser.util.InfoPrinter;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HttpManager {

    /**
     * post data
     *
     * @param params
     * @param path
     * @return result string
     */
    public static String postData(Map<String, Object> params, String path) {
        try {
            URL url = new URL(path);
            StringBuilder stringBuilder = new StringBuilder();
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            if (params != null && params.keySet().size() > 0) {
                for (String key : params.keySet()) {
                    httpURLConnection.addRequestProperty(key, (String) params.get(key));
                    stringBuilder.append("&" + key + "=" + (String) params.get(key));
                }
            }
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDefaultUseCaches(false);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            OutputStream os = httpURLConnection.getOutputStream();
//			os.write(new Gson().toJson(params).toString().getBytes("UTF-8"));//JSON
            os.write(stringBuilder.toString().getBytes("UTF-8"));//NORMAL
            os.flush();
            os.close();

            httpURLConnection.connect();
            int responseCode = httpURLConnection.getResponseCode();

            for(int redirectCount = 0; responseCode / 100 == 3 && redirectCount < 5; ++redirectCount) {
                httpURLConnection = createConnection(httpURLConnection.getHeaderField("Location"));
                responseCode = httpURLConnection.getResponseCode();
            }

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream reader = httpURLConnection.getInputStream();
                String result = convertStreamToString(reader);
                return result;
            } else {
                return "";
            }
        } catch (UnsupportedEncodingException e) {
            InfoPrinter.printLog(e.getMessage());
        } catch (ClientProtocolException e) {
            InfoPrinter.printLog(e.getMessage());
        } catch (IOException e) {
            InfoPrinter.printLog(e.getMessage());
        } catch (Exception e){
            InfoPrinter.printLog(e.getMessage());
        }
        return "";
    }

    protected static HttpURLConnection createConnection(String url) throws IOException {
        String encodedUrl = Uri.encode(url, "@#&=*+-_.,:!?()/~\'%");
        HttpURLConnection conn = (HttpURLConnection)(new URL(encodedUrl)).openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(20000);
        return conn;
    }

    /**
     * stream to string
     *
     * @param is
     * @return 转锟斤拷锟斤拷锟絪tring
     * @throws UnsupportedEncodingException
     */
    public static String convertStreamToString(InputStream is) throws UnsupportedEncodingException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
        } finally {
            try {
                reader.close();
                is.close();
            } catch (IOException e) {
                InfoPrinter.printLog(e.toString());
            }
        }
        return sb.toString();
    }

    /**
     * @param path
     * @param obj
     * @return 锟斤拷锟截斤拷锟絪tring
     */
    public static String postObject(String path, Object obj) {
        try {
            // get connection
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // set connection锟斤拷锟斤拷
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-type", "application/x-java-serialized-object");
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10 * 1000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            conn.setRequestProperty("Accept", "*/*");
            conn.connect();
            OutputStream os = conn.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(obj);
            oos.flush();
            oos.close();
            InputStream in = conn.getInputStream();
            int code = conn.getResponseCode();
            if (code == 200) {
                return convertStreamToString(in);
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            InfoPrinter.printLog(e.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            InfoPrinter.printLog(e.toString());
        }
        return "";
    }

    public static String getImageRequest(String path) {
        String dirString = BaseConstants.imageDir;
        int code = 0;
        HttpURLConnection conn = null;
        InputStream in = null;
        FileOutputStream outputStream = null;
        String filepath = dirString + SignatureUtil.getMD5String(path);
        try {
            URL url = new URL(path);
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            code = conn.getResponseCode();
            File file = new File(dirString);
            if (!file.exists()) {
                file.mkdirs();
            }
            outputStream = new FileOutputStream(new File(filepath));
            // 锟斤拷锟斤拷锟节达拷锟侥硷拷
            if (code == 200) {
                int num = 0;
                in = conn.getInputStream();
                int length = conn.getContentLength();
                byte[] buff = new byte[1024 * 4];
                while ((num = in.read(buff)) != -1) {
                    outputStream.write(buff, 0, num);
                }
                int filelength = (int) new File(filepath).length();
                if (filelength == length) {
                    return filepath;
                } else {
                    return "";
                }
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            InfoPrinter.printLog(e.toString());
            if (new File(filepath).exists()) {
                new File(filepath).delete();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            InfoPrinter.printLog(e.toString());
            if (new File(filepath).exists()) {
                new File(filepath).delete();
            }
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                    if (in != null) {
                        in.close();
                    }
                    conn.disconnect();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                InfoPrinter.printLog(e.toString());
            }
        }
        return "";
    }

    public static String getRequest(String path) {
        // 锟叫断伙拷锟斤拷锟斤拷锟角凤拷锟斤拷诟锟斤拷锟斤拷
        HttpURLConnection conn = null;
        int code = 0;
        try {
            URL url = new URL(path);
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            code = conn.getResponseCode();
            String string = conn.getURL().toString();
            for(int redirectCount = 0; code / 100 == 3 && redirectCount < 5; ++redirectCount) {
                conn = createConnection(conn.getHeaderField("Location"));
                code = conn.getResponseCode();
            }
            if (code == 200 /*&& string.equals(path)*/) {
                InputStream in = conn.getInputStream();
                String str = convertStreamToString(in);
                return str;
            } else {
                return "";
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            InfoPrinter.printLog("http_response_exception:" + e.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            InfoPrinter.printLog("http_response_exception:" + e.toString());
        } finally {
            conn.disconnect();
        }
        return "";
    }

    public static String uploadImageFaceTalk(String filename, String path, String actionUrl) {
        String end = "/r/n";
        String Hyphens = "--";
        String boundary = "*****";
        try {
            URL url = new URL(actionUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            /* 鍏佽Input銆丱utput锛屼笉浣跨敤Cache */
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            /* 璁惧畾浼狅拷?鐨刴ethod=POST */
            con.setRequestMethod("POST");
			/* setRequestProperty */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type", "application/octet-stream;boundary=" + boundary);
			/* 璁惧畾DataOutputStream */
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
            ds.writeBytes(Hyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data;name=\"image\" filename=/" + filename + "/" + end);
            ds.writeBytes(end);
			/* 鍙栧緱鏂囦欢鐨凢ileInputStream */
            FileInputStream fStream = new FileInputStream(new File(path));
			/* 璁惧畾姣忔鍐欏叆1024bytes */
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
			/* 浠庢枃浠惰鍙栨暟鎹埌缂撳啿锟?*/
            while ((length = fStream.read(buffer)) != -1) {
				/* 灏嗘暟鎹啓鍏ataOutputStream锟?*/
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            ds.writeBytes(Hyphens + boundary + Hyphens + end);
            fStream.close();
            ds.flush();
			/* 鍙栧緱Response鍐呭 */
            InputStream is = con.getInputStream();
            if (is != null) {
                return convertStreamToString(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String postFile(String path, Map<String, String> params, ProgressChangeListener listener, String filename) {
//		InfoPrinter.printLog("Post File url:"+path);
//		InfoPrinter.printLog("Post File params:"+new Gson().toJson(params));
        /**
         *
         */
        try {
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            if (pairs != null && pairs.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if (entry.getKey().equals("image")) {
                        entity.addPart("image", new FileBody(new File(params.get("image").toString())));
                    } else if (entry.getKey().equals("voice")) {
                        entity.addPart("voice", new FileBody(new File(params.get("voice").toString())));
                    } else if (entry.getKey().equals("audio")) {
                        entity.addPart("audio", new FileBody(new File(params.get("audio").toString())));
                    } else if (entry.getKey().equals("file")) {
                        entity.addPart("file", new FileBody(new File(params.get("file").toString())));
                    } else {
                        entity.addPart(entry.getKey(), new StringBody(entry.getValue().toString(), Charset.forName("utf-8")));
                    }
//					InfoPrinter.printLog(entry.getKey() + "---" + entry.getValue().toString());
                }
            }
            HttpPost httpPost = new HttpPost(path);
            httpPost.setEntity(entity);
            DefaultHttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(httpPost);//执行到这里时可能出现IOException
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                InputStream input = response.getEntity().getContent();
                String data = convertStreamToString(input);
                return data;
            } else {
                return "";
            }
        } catch (UnsupportedEncodingException e) {
            if (e != null) {
            } else {
            }
        } catch (ClientProtocolException e) {
            if (e != null) {
            } else {
            }
        } catch (IOException e) {
            if (e != null) {
            } else {
            }

        }
        return "";

    }

    public static String postFile(String path, Map<String, String> params, ProgressChangeListener listener, String filename,int number) {
//		InfoPrinter.printLog("Post File url:"+path);
//		InfoPrinter.printLog("Post File params:"+new Gson().toJson(params));
        /**
         *
         */
        try {
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            if (pairs != null && pairs.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if (entry.getKey().equals("image")) {
                        entity.addPart("image", new FileBody(new File(params.get("image").toString())));
                    } else if (entry.getKey().equals("voice")) {
                        entity.addPart("voice", new FileBody(new File(params.get("voice").toString())));
                    } else if (entry.getKey().equals("audio")) {
                        entity.addPart("audio", new FileBody(new File(params.get("audio").toString())));
                    } else if (entry.getKey().equals("file")) {
                        entity.addPart("file", new FileBody(new File(params.get("file").toString())));
                    } else {
                        entity.addPart(entry.getKey(), new StringBody(entry.getValue().toString(), Charset.forName("utf-8")));
                    }
//					InfoPrinter.printLog(entry.getKey() + "---" + entry.getValue().toString());
                }
            }
            HttpPost httpPost = new HttpPost(path);
            httpPost.setEntity(entity);
            DefaultHttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(httpPost);//执行到这里时可能出现IOException
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                InputStream input = response.getEntity().getContent();
                String data = convertStreamToString(input);
                return data;
            } else {
                return "";
            }
        } catch (UnsupportedEncodingException e) {
            if (e != null) {
            } else {
            }
        } catch (ClientProtocolException e) {
            if (e != null) {
            } else {
            }
        } catch (IOException e) {
            if (e != null) {
            } else {
            }

        }
        return "";

    }

    public static String uploadFile(String path, Map<String, String> params, ProgressChangeListener listener, String filename){
        String end = "/r/n";
        String Hyphens = "--";
        String boundary = "*****";
        try{
            URL url = new URL(path);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
      /* 允许Input、Output，不使用Cache */
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
      /* 设定传送的method=POST */
            con.setRequestMethod("POST");
      /* setRequestProperty */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
      /* 设定DataOutputStream */
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
            ds.writeBytes(Hyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; "
                    + "name=" + filename + ";filename=" + filename + "" + end);
            ds.writeBytes(end);
      /* 取得文件的FileInputStream */
            FileInputStream fStream = new FileInputStream(params.get("file"));
      /* 设定每次写入1024bytes */
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
      /* 从文件读取数据到缓冲区 */
            while ((length = fStream.read(buffer)) != -1){
        /* 将数据写入DataOutputStream中 */
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            ds.writeBytes(Hyphens + boundary + Hyphens + end);
            fStream.close();
            ds.flush();
      /* 取得Response内容 */
            InputStream is = con.getInputStream();
            int ch;
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1){
                b.append((char) ch);
            }
            ds.close();
            is.close();
            return b.toString();
        } catch (Exception e){
            MobclickAgent.reportError(BaseApplication.getInstances(),e);
        }
        return "";
    }


    public static String postFileParamters(HashMap<String, Object> params, String path) {
        MultipartEntity entity;
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        if (pairs != null && pairs.isEmpty()) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }
        }
        try {
            entity = new MultipartEntity();
            // entity.addPart("mid",new
            // StringBody(params.get("mid").toString(),Charset.forName("utf-8")));
            entity.addPart("file", new FileBody(new File(params.get("file").toString())));
            HttpPost httpPost = new HttpPost(path);
            httpPost.setEntity(entity);
            DefaultHttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(httpPost);
            InfoPrinter.printLog("锟斤拷锟截碉拷锟斤拷锟斤拷锟斤拷  = " + response.getStatusLine().getStatusCode());
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                InfoPrinter.printLog("response code:" + code);
                InputStream input = response.getEntity().getContent();
                String data = convertStreamToString(input);
                return data;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            InfoPrinter.printLog(e.toString());
        } catch (ClientProtocolException e) {
            InfoPrinter.printLog(e.toString());
        } catch (IOException e) {
            InfoPrinter.printLog(e.toString());
        }
        return "";
    }

    public static String postMultiPartParams(HashMap<String, Object> params, String path) {
        // 锟斤拷锟斤拷锟斤拷锟?
        InfoPrinter.printLog("Post MultiPart url:" + path);
        InfoPrinter.printLog("Post MultiPart params:" + new Gson().toJson(params));
        try {
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            if (pairs != null && pairs.isEmpty()) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    if (entry.getKey().equals("image")) {
                        entity.addPart("image", new FileBody(new File(params.get("image").toString())));
                    } else if (entry.getKey().equals("voice")) {
                        entity.addPart("voice", new FileBody(new File(params.get("voice").toString())));
                    } else {
                        entity.addPart(entry.getKey(),
                                new StringBody(entry.getValue().toString(), Charset.forName("utf-8")));
                    }
                    InfoPrinter.printLog(entry.getKey() + "---" + entry.getValue().toString());
                }


            }
            HttpPost httpPost = new HttpPost(path);
            httpPost.setEntity(entity);
            System.out.println(entity.toString());
            DefaultHttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(httpPost);
            InfoPrinter.printLog("Response StatusCode = " + response.getStatusLine().getStatusCode());
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                InputStream input = response.getEntity().getContent();
                String data = convertStreamToString(input);
                return data;
            } else {
                return "";
            }
        } catch (UnsupportedEncodingException e) {
            InfoPrinter.printLog(e.toString());
        } catch (ClientProtocolException e) {
            InfoPrinter.printLog(e.toString());
        } catch (IOException e) {
            InfoPrinter.printLog(e.toString());
        }
        return "";
    }

public interface ProgressChangeListener {
    public void hasFinished(int finished);
}

}
