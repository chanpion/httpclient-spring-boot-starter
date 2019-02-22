package com.boot.http;

import com.boot.http.utils.HttpUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * @author April Chen
 * @date 2019/2/14 17:29
 */
public class HttpTemplate {
    private CloseableHttpClient httpClient;
    private HttpClientProperties httpClientProperties;

    public HttpTemplate(CloseableHttpClient httpClient, HttpClientProperties httpClientProperties) {
        this.httpClient = httpClient;
        this.httpClientProperties = httpClientProperties;
    }

    public String get(String url) throws Exception {
        return get(url, null);
    }

    public String get(String url, Map<String, Object> params) throws Exception {
        return get(url, params, null);
    }

    public String get(String url, Map<String, Object> params, Map<String, String> headers) throws Exception {
        return execute(url, params, headers, HttpGet.METHOD_NAME);
    }

    public String post(String url) throws Exception {
        return post(url, null);
    }

    public String post(String url, Map<String, Object> params) throws Exception {
        return post(url, params, null);
    }

    public String post(String url, Map<String, Object> params, Map<String, String> headers) throws Exception {
        return execute(url, params, headers, HttpPost.METHOD_NAME);
    }

    private String execute(String url, Map<String, Object> params, Map<String, String> headers, String httpMethod) throws Exception {
        URI uri = HttpUtils.createURI(url, params);
        HttpRequestBase httpRequest;
        if (HttpGet.METHOD_NAME.equals(httpMethod)) {
            httpRequest = new HttpGet(uri);
        } else {
            httpRequest = new HttpPost(uri);
        }
        addHeaders(httpRequest, headers);

        return doExecute(httpRequest);
    }

    public String query(String name) throws Exception {
        return query(name, null);
    }

    public String query(String name, Map<String, Object> params) throws Exception {
        return query(name, params, null);
    }

    public String query(String name, Map<String, Object> params, Map<String, String> headers) throws Exception {
        RouteConfig routeConfig = httpClientProperties.getRoutes().get(name);
        return execute(routeConfig, params, headers);
    }

    private String execute(RouteConfig routeConfig, Map<String, Object> params, Map<String, String> headers) throws Exception {
        HttpRequestBase httpRequest = null;
        URI uri = HttpUtils.createURI(routeConfig.getUrl(), params);
        if (HttpGet.METHOD_NAME.equals(routeConfig.getMethod())) {
            httpRequest = new HttpGet(uri);
        } else if (HttpPost.METHOD_NAME.equals(routeConfig.getMethod())) {
            httpRequest = new HttpPost(uri);
        }
        addHeaders(httpRequest, headers);
        RequestConfig requestConfig = getRequestConfig(routeConfig);
        if (httpRequest != null) {
            httpRequest.setConfig(requestConfig);
        }
        return doExecute(httpRequest);
    }


    private String doExecute(HttpRequestBase httpRequest) throws IOException {
        return httpClient.execute(httpRequest, StringResponseHandler.instance);
    }

    private void addHeaders(HttpRequestBase httpRequest, Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(httpRequest::addHeader);
        }
    }

    private RequestConfig getRequestConfig(RouteConfig routeConfig) {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(routeConfig.getConnectionRequestTimeout())
                .setConnectTimeout(routeConfig.getConnectTimeout())
                .setSocketTimeout(routeConfig.getSocketTimeout())
                .build();
    }
}
