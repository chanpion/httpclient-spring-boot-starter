package com.boot.http;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * @author April Chen
 * @date 2019/2/14 17:06
 */
@Configuration
@ConditionalOnClass(HttpClient.class)
@EnableConfigurationProperties(HttpClientProperties.class)
public class HttpClientAutoConfiguration {
    private final HttpClientProperties httpClientProperties;

    public HttpClientAutoConfiguration(HttpClientProperties httpClientProperties) {
        this.httpClientProperties = httpClientProperties;
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.httpclient.pool", name = {"maxTotal", "maxPerRoute"})
    @ConditionalOnMissingBean(PoolingHttpClientConnectionManager.class)
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        Registry<ConnectionSocketFactory> socketFactoryRegistry = null;
        try {
            SSLContext sslContext = createIgnoreVerifySSL();
            socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslContext))
                    .build();
        } catch (Exception ignored) {
        }

        PoolingHttpClientConnectionManager connectionManager = socketFactoryRegistry == null ?
                new PoolingHttpClientConnectionManager() : new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setMaxTotal(httpClientProperties.getPool().getMaxTotal());
        connectionManager.setDefaultMaxPerRoute(httpClientProperties.getPool().getMaxPerRoute());
        return connectionManager;
    }

    private SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext ctx = SSLContext.getInstance("SSL");
        X509TrustManager trustManager = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        ctx.init(null, new TrustManager[]{trustManager}, null);
        return ctx;
    }


    @Bean
    @ConditionalOnMissingBean(CloseableHttpClient.class)
    public CloseableHttpClient httpClient(PoolingHttpClientConnectionManager connectionManager) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(httpClientProperties.getConnectionRequestTimeout())
                .setConnectTimeout(httpClientProperties.getConnectTimeout())
                .setSocketTimeout(httpClientProperties.getSocketTimeout())
                .build();

        HttpClientBuilder builder = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
                .evictExpiredConnections();
        if (httpClientProperties.getMaxIdle() != null) {
            builder = builder.evictIdleConnections(httpClientProperties.getMaxIdle(), TimeUnit.MILLISECONDS);
        }

        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean(HttpTemplate.class)
    public HttpTemplate httpTemplate(CloseableHttpClient httpClient) {
        return new HttpTemplate(httpClient, httpClientProperties);
    }
}
