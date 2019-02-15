package com.boot.http;

import org.apache.http.*;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author April Chen
 * @date 2019/2/14 17:02
 */
public class StringResponseHandler implements ResponseHandler<String> {
    public static StringResponseHandler instance = new StringResponseHandler();

    public String handleResponse(HttpResponse response) throws IOException {
        final StatusLine statusLine = response.getStatusLine();
        final HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
            EntityUtils.consume(entity);
            throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
        }
        return entity == null ? null : EntityUtils.toString(entity, Consts.UTF_8);
    }
}
