package com.boot.http.utils;

import org.apache.http.Consts;
import org.apache.http.client.utils.URIBuilder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

/**
 * @author April Chen
 * @date 2019/2/15 10:15
 */
public class HttpUtils {

    public static URI createURI(String queryUrl, Map<String, Object> params) throws MalformedURLException, URISyntaxException {
        URL url = new URL(queryUrl);
        URIBuilder uriBuilder = new URIBuilder()
                .setCharset(Consts.UTF_8)
                .setScheme(url.getProtocol())
                .setUserInfo(url.getUserInfo())
                .setHost(url.getHost())
                .setPort(url.getPort())
                .setPath(url.getPath())
                .setCustomQuery(url.getQuery());
        if (params != null && !params.isEmpty()) {
            params.forEach((k, v) -> {
                if (v != null) {
                    uriBuilder.addParameter(k, v.toString());
                }
            });
        }

        return uriBuilder.build();
    }
}
