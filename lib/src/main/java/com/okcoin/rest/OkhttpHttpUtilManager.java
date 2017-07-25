package com.okcoin.rest;

import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * 封装HTTP get post请求，简化发送http请求
 *
 * @author zhangchi
 */
public class OkhttpHttpUtilManager {
    private static OkhttpHttpUtilManager okhttpHttpUtilManager;
    OkHttpClient okHttpClient;

    private OkhttpHttpUtilManager() {
        okHttpClient = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
    }


    public static OkhttpHttpUtilManager getInstance() {
        if (okhttpHttpUtilManager == null) {
            okhttpHttpUtilManager = new OkhttpHttpUtilManager();
        }
        return okhttpHttpUtilManager;
    }

    public String requestHttpGet(String url_prex, String url, String param) throws HttpException, IOException {

        url = url_prex + url;
        if (param != null && !param.equals("")) {
            if (url.endsWith("?")) {
                url = url + param;
            } else {
                url = url + "?" + param;
            }
        }
        Request request = new Request.Builder().url(url).build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    public String requestHttpPost(String url_prex, String url, Map<String, String> params) throws HttpException, IOException {

        url = url_prex + url;
        FormBody.Builder formBody = new FormBody.Builder();
        for (String key : params.keySet()) {
            formBody.add(key, params.get(key));
        }
        Request request = new Request.Builder()
                .url(url)
                .post(formBody.build())
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }

    }

}

