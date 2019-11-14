# httpclient-spring-boot-starter

spring-booter-starter整合HttpClient，快速配置，方便使用
可单独配置每个调用服务的请求参数

## 快速开始
- 引入依赖
  ```
    <dependency>
      <groupId>com.chanpion</groupId>
      <artifactId>spring-boot-starter-httpclient</artifactId>
      <version>1.0.0</version>
    </dependency>
  ```
- 添加配置，spring配置文件application.xml

  ```
    spring:
      httpclient:
        connect-timeout: 100
        socket-timeout: 3000
        pool:
          max-total: 2000
          max-per-route: 500
        routes: 
          baudu:
            url: http://www.baidu.com
            method: get
            connect-timeout: 1000
            socket-timetou: 1000
  ```
- 使用
  ```
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
  ```
