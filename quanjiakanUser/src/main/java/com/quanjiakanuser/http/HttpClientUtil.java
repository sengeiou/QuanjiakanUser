package com.quanjiakanuser.http;

import com.androidquanjiakan.interfaces.INetRequestCodeInterface;
import com.androidquanjiakan.util.LogUtil;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

/**
 * For JMessage REST API
 *
 * Created by huangzhou on 2016/5/27 0027.
 */
public class HttpClientUtil {

    public static final String LOVAL_ERROR = "local_error";
    public static final String SERVER_OR_NET_ERROR = "server_or_net_error";

    public static final String ENCODE = "UTF-8";


    public static final String SUCCESS = "success";
    public static final String SUCCESS_NO_CONTENT = "success_no_content";
    public static final int CREATED = 201;
    public static final int NO_CONTENT = 204;
    public static final int SERVER_NO_RESPONSE = -1;

    private static DefaultHttpClient httpClient;
    private static HttpContext httpContext;

    static {

        HttpParams params = new BasicHttpParams();

        // 设置允许链接的做多链接数目
        ConnManagerParams.setMaxTotalConnections(params, 10);
        // 设置超时时间.
        ConnManagerParams.setTimeout(params, 1000);
        // 连接超时
        HttpConnectionParams.setConnectionTimeout(params, 2000);
        // 请求超时
        HttpConnectionParams.setSoTimeout(params, 15000);

        // 设置每个路由的最多链接数量是20
        ConnPerRouteBean connPerRoute = new ConnPerRouteBean(20);
        ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);

        // 设置链接使用的版本
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        // 设置链接使用的内容的编码
        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);

        httpClient = new DefaultHttpClient(cm, params);
        httpContext = new BasicHttpContext();
    }

    public static String postRequest(Map<String, String> params, String url, String entity) {
        LogUtil.e(  "---------URL:"+url);
        HttpPost httpPost = new HttpPost(url);
        try {
            if (params != null && params.keySet().size() > 0) {
                Iterator iterator = params.keySet().iterator();
                while (iterator.hasNext()) {
                    String entry = (String) iterator.next();
                    httpPost.addHeader(entry, params.get(entry));
                }
            }
            LogUtil.e( "Entity:"+entity);
            httpPost.setEntity(new StringEntity(entity, ENCODE));
            HttpResponse httpResponse = httpClient.execute(httpPost, httpContext);
            return setResponse(httpResponse);
        } catch (IOException e) {
            e.printStackTrace();
            return LOVAL_ERROR;
        }
    }

    /**
     * Get 请求
     * --------------------------------------------*  --------------------------------------------*  --------------------------------------------*  --------------------------------------------*  --------------------------------------------
     *
     * @param params
     * @param url
     * @param entity
     * @return
     */
    public static String getRequest(Map<String, String> params, String url, Map<String, String> entity) {
        String url_param = "?";
        if (entity != null && entity.keySet().size() > 0) {
            Iterator iterator = entity.keySet().iterator();
            while (iterator.hasNext()) {
                String uParam = (String) iterator.next();
                if (iterator.hasNext()) {
                    url_param += uParam + "=" + entity.get(uParam) + "&";
                } else {
                    url_param += uParam + "=" + entity.get(uParam);
                }
            }
        }
        LogUtil.e(  "---------URL:"+url + url_param);
        HttpGet httpPost = new HttpGet(url + url_param);

        try {
            if (params != null && params.keySet().size() > 0) {
                Iterator iterator = params.keySet().iterator();
                while (iterator.hasNext()) {
                    String entry = (String) iterator.next();
                    httpPost.addHeader(entry, params.get(entry));
                }
            }
            HttpResponse httpResponse = httpClient.execute(httpPost, httpContext);
            return setResponse(httpResponse);
        } catch (IOException e) {
            e.printStackTrace();
            return LOVAL_ERROR;
        }
    }

    public static String getRequest(Map<String, String> params, String url, String url_entity) {
        LogUtil.e(  "---------URL:"+url + url_entity);
        HttpGet httpPost = new HttpGet(url + url_entity);

        try {
            if (params != null && params.keySet().size() > 0) {
                Iterator iterator = params.keySet().iterator();
                while (iterator.hasNext()) {
                    String entry = (String) iterator.next();
                    httpPost.addHeader(entry, params.get(entry));
                }
            }
            HttpResponse httpResponse = httpClient.execute(httpPost, httpContext);
            return setResponse(httpResponse);
        } catch (IOException e) {
            e.printStackTrace();
            return LOVAL_ERROR;
        }
    }

    /**
     * putResponse
     * --------------------------------------------*  --------------------------------------------*  --------------------------------------------*  --------------------------------------------*  --------------------------------------------
     *
     * @param params
     * @param url
     * @param url_entity
     * @return
     */
    public static String putRequest(Map<String, String> params, String url, String url_entity, String content) {
        LogUtil.e(  "---------URL:"+url + url_entity);
        HttpPut httpPost = new HttpPut(url + url_entity);
        try {
            if (params != null && params.keySet().size() > 0) {
                Iterator iterator = params.keySet().iterator();
                while (iterator.hasNext()) {
                    String entry = (String) iterator.next();
                    httpPost.addHeader(entry, params.get(entry));
                }
            }
            LogUtil.e( "Entity:"+content);
            httpPost.setEntity(new StringEntity(content, ENCODE));
            HttpResponse httpResponse = httpClient.execute(httpPost, httpContext);
            if (httpResponse != null) {
                LogUtil.e(  "" + httpResponse.getStatusLine().getStatusCode());
                if (httpResponse.getStatusLine().getStatusCode() == 200 || httpResponse.getStatusLine().getStatusCode() == CREATED || httpResponse.getStatusLine().getStatusCode() == NO_CONTENT) {
                    return SUCCESS;
                } else {
                    return SERVER_OR_NET_ERROR;
                }
            } else {
                return SERVER_OR_NET_ERROR;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return LOVAL_ERROR;
        }
    }

    public static String putRequest(Map<String, String> params, String url, String url_entity, String content, INetRequestCodeInterface requestCodeInterface) {
        LogUtil.e(  "---------URL:"+url + url_entity);
        HttpPut httpPost = new HttpPut(url + url_entity);
        try {
            if (params != null && params.keySet().size() > 0) {
                Iterator iterator = params.keySet().iterator();
                while (iterator.hasNext()) {
                    String entry = (String) iterator.next();
                    httpPost.addHeader(entry, params.get(entry));
                }
            }
            LogUtil.e( "Entity:"+content);
            httpPost.setEntity(new StringEntity(content, ENCODE));
            HttpResponse httpResponse = httpClient.execute(httpPost, httpContext);
            if (httpResponse != null) {
                if(requestCodeInterface!=null){
                    requestCodeInterface.getResponseCode(httpResponse.getStatusLine().getStatusCode());
                }
                LogUtil.e(  "" + httpResponse.getStatusLine().getStatusCode());
                if (httpResponse.getStatusLine().getStatusCode() == 200 || httpResponse.getStatusLine().getStatusCode() == CREATED || httpResponse.getStatusLine().getStatusCode() == NO_CONTENT) {
                    return SUCCESS;
                } else {
                    return SERVER_OR_NET_ERROR;
                }
            } else {
                if(requestCodeInterface!=null){
                    requestCodeInterface.getResponseCode(SERVER_NO_RESPONSE);
                }
                return SERVER_OR_NET_ERROR;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return LOVAL_ERROR;
        }
    }


    public static String putRequestFileUpload(Map<String, String> params, String url, File filepath) {
        LogUtil.e(  "---------URL:"+url);
        HttpPut httpPost = new HttpPut(url);
        try {
            if (params != null && params.keySet().size() > 0) {
                Iterator iterator = params.keySet().iterator();
                while (iterator.hasNext()) {
                    String entry = (String) iterator.next();
                    httpPost.addHeader(entry, params.get(entry));
                }
            }
            httpPost.addHeader("filename",filepath.getName());

            FileBody fileBody = new FileBody(filepath);
            StringBody fileName = new StringBody(filepath.getName());
            MultipartEntity multipartEntity = new MultipartEntity();
            multipartEntity.addPart("filebodyname",fileBody);//key : image\voice\file
//            httpPost.setEntity(new StringEntity(content, ENCODE));
            httpPost.setEntity(multipartEntity);
            HttpResponse httpResponse = httpClient.execute(httpPost, httpContext);
            if (httpResponse != null) {
                LogUtil.e(  "------文件上传:" + httpResponse.getStatusLine().getStatusCode());
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK || httpResponse.getStatusLine().getStatusCode() == CREATED || httpResponse.getStatusLine().getStatusCode() == NO_CONTENT) {
                    return setResponse(httpResponse);
                } else{
                    return SERVER_OR_NET_ERROR;
                }
            } else {
                return SERVER_OR_NET_ERROR;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return LOVAL_ERROR;
        }
    }

    /**
     * deleteResponse
     * --------------------------------------------*  --------------------------------------------*  --------------------------------------------*  --------------------------------------------*  --------------------------------------------
     *
     * @param params
     * @param url
     * @param url_entity
     * @return
     */
    public static String deleteRequest(Map<String, String> params, String url, String url_entity, String content) {
        LogUtil.e(  "---------URL:"+url + url_entity);
        HttpDelete httpPost = new HttpDelete(url + url_entity);

        try {
            if (params != null && params.keySet().size() > 0) {
                Iterator iterator = params.keySet().iterator();
                while (iterator.hasNext()) {
                    String entry = (String) iterator.next();
                    httpPost.addHeader(entry, params.get(entry));
                }
            }
            HttpResponse httpResponse = httpClient.execute(httpPost, httpContext);
            if (httpResponse != null) {
                LogUtil.e(  "" + httpResponse.getStatusLine().getStatusCode());
                if (httpResponse.getStatusLine().getStatusCode() == 200 || httpResponse.getStatusLine().getStatusCode() == CREATED || httpResponse.getStatusLine().getStatusCode() == NO_CONTENT) {
                    return SUCCESS;
                } else {
                    return SERVER_OR_NET_ERROR;
                }
            } else {
                return SERVER_OR_NET_ERROR;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return LOVAL_ERROR;
        }
    }

    public static String deleteRequest(Map<String, String> params, String url, String url_entity) {
        LogUtil.e(  "---------URL:"+url + url_entity);
        HttpDelete httpPost = new HttpDelete(url + url_entity);

        try {
            if (params != null && params.keySet().size() > 0) {
                Iterator iterator = params.keySet().iterator();
                while (iterator.hasNext()) {
                    String entry = (String) iterator.next();
                    httpPost.addHeader(entry, params.get(entry));
                }
            }
            HttpResponse httpResponse = httpClient.execute(httpPost, httpContext);
            if (httpResponse != null) {
                LogUtil.e(  "" + httpResponse.getStatusLine().getStatusCode());
                if (httpResponse.getStatusLine().getStatusCode() == 200 || httpResponse.getStatusLine().getStatusCode() == CREATED || httpResponse.getStatusLine().getStatusCode() == NO_CONTENT) {
                    return SUCCESS;
                } else {
                    return SERVER_OR_NET_ERROR;
                }
            } else {
                return SERVER_OR_NET_ERROR;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return LOVAL_ERROR;
        }
    }

    public static String result;

    /**
     * setResponse
     * --------------------------------------------*  --------------------------------------------*  --------------------------------------------*  --------------------------------------------*  --------------------------------------------
     *
     * @param httpResponse
     * @return
     */
    public static String setResponse(HttpResponse httpResponse) {
        if (httpResponse == null) {
            return SERVER_OR_NET_ERROR;
        } else {
            result = null;
            LogUtil.e(  "" + httpResponse.getStatusLine().getStatusCode());
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                result = getResponseContent(httpResponse);
                return result;
            } else if (httpResponse.getStatusLine().getStatusCode() == CREATED) {
                result = getResponseContent(httpResponse);
                return result;
            } else if (httpResponse.getStatusLine().getStatusCode() == NO_CONTENT) {
                result = getResponseContent(httpResponse);
                return result;
            }
        }
        return SERVER_OR_NET_ERROR;
    }

    public static final String getResponseContent(HttpResponse httpResponse) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            if(httpResponse.getEntity()!=null){
                InputStream is = httpResponse.getEntity().getContent();
                if (is != null) {
                    byte[] bytes = new byte[2048];
                    int len = 0;
                    while ((len = is.read(bytes)) != -1) {
                        stringBuilder.append(new String(bytes, 0, len, ENCODE));
                    }
                    is.close();
                    String result = stringBuilder.toString();
                    if (result != null) {
                        return result;
                    } else {
                        return SUCCESS;
                    }
                } else {
                    return SUCCESS_NO_CONTENT;
                }
            }else{
                return SUCCESS_NO_CONTENT;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return LOVAL_ERROR;
        } finally {

        }
    }
}
