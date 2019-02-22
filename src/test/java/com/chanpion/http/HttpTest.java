package com.chanpion.http;

import com.boot.http.HttpTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author April Chen
 * @date 2019/2/22 13:48
 */
@Component
public class HttpTest {

    @Resource
    private HttpTemplate httpTemplate;

    public void invoke() throws Exception {
        String response = httpTemplate.get("https://www.baidu.com/");
        System.out.println(response);
        String response1 = httpTemplate.query("baidu");
        System.out.println(response1);
    }

}
