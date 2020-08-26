#### Spring Cloud 是一个相对比较新的微服务框架
###### 1. 项目目录
* [创建依赖管理项目](https://www.funtl.com/zh/spring-cloud-netflix/Spring-Cloud-%E5%88%9B%E5%BB%BA%E7%BB%9F%E4%B8%80%E7%9A%84%E4%BE%9D%E8%B5%96%E7%AE%A1%E7%90%86.html#%E5%88%9B%E5%BB%BA%E4%BE%9D%E8%B5%96%E7%AE%A1%E7%90%86%E9%A1%B9%E7%9B%AE)  
    * hello-spring-cloud-dependencies
    * parent：继承了 Spring Boot 的 Parent，表示我们是一个 Spring Boot 工程
    * package：pom，表示该项目仅当做依赖项目，没有具体的实现代码
    * spring-cloud-dependencies：在 properties 配置中预定义了版本号为 Finchley.RC1 ，表示我们的 Spring Cloud 使用的是 F 版
    * build：配置了项目所需的各种插件
    * repositories：配置项目下载依赖时的第三方库
    * 在实际开发中，我们所有的项目都会依赖这个 dependencies 项目，整个项目周期中的所有第三方依赖的版本也都由该项目进行管理。
* [服务注册与发现](https://www.funtl.com/zh/spring-cloud-netflix/Spring-Cloud-%E6%9C%8D%E5%8A%A1%E6%B3%A8%E5%86%8C%E4%B8%8E%E5%8F%91%E7%8E%B0.html#%E5%88%9B%E5%BB%BA%E6%9C%8D%E5%8A%A1%E6%B3%A8%E5%86%8C%E4%B8%AD%E5%BF%83)
    * hello.spring.cloud.eureka
    * 启动一个服务注册中心，只需要一个注解 @EnableEurekaServer
    * 通过 eureka.client.registerWithEureka:false 和 fetchRegistry:false 来表明自己是一个 Eureka Server.
    * Eureka 是一个高可用的组件，它没有后端缓存，每一个实例注册之后需要向注册中心发送心跳（因此可以在内存中完成），在默认情况下 Erureka Server 也是一个 Eureka Client ,必须要指定一个 Server。
    * Eureka Server 是有界面的，启动工程，打开浏览器访问：[http://localhost:8761](http://localhost:8761)
* [创建服务提供者](https://www.funtl.com/zh/spring-cloud-netflix/Spring-Cloud-%E5%88%9B%E5%BB%BA%E6%9C%8D%E5%8A%A1%E6%8F%90%E4%BE%9B%E8%80%85.html#%E6%A6%82%E8%BF%B0)
    * 当 Client 向 Server 注册时，它会提供一些元数据，例如主机和端口，URL，主页等。Eureka Server 从每个 Client 实例接收心跳消息。 如果心跳超时，则通常将该实例从注册 Server 中删除。
    * hello.spring.cloud.service.admin
    * 通过注解 @EnableEurekaClient 表明自己是一个 Eureka Client.
    * 注意： 需要指明 spring.application.name，这个很重要，这在以后的服务与服务之间相互调用一般都是根据这个 name
    * 启动工程，打开 http://localhost:8761 ，即 Eureka Server 的网址：
    * 你会发现一个服务已经注册在服务中了，服务名为 HELLO-SPRING-CLOUD-SERVICE-ADMIN ,端口为 8762
      这时打开 http://localhost:8762/hi?message=HelloSpring ，你会在浏览器上看到 :
      Hi，your message is :"HelloSpring" i am from port：8762
* [创建服务消费者（Ribbon）](https://www.funtl.com/zh/spring-cloud-netflix/Spring-Cloud-%E5%88%9B%E5%BB%BA%E6%9C%8D%E5%8A%A1%E6%B6%88%E8%B4%B9%E8%80%85%EF%BC%88Ribbon%EF%BC%89.html#ribbon-%E7%AE%80%E4%BB%8B)
    * Ribbon 是一个负载均衡客户端，可以很好的控制 http 和 tcp 的一些行为。
    *启动服务提供者（本教程案例工程为：hello-spring-cloud-service-admin），端口号为：8762
     修改配置文件的端口号为：8763，启动后在 Eureka 中会注册两个实例，这相当于一个小集群
    * hello-spring-cloud-web-admin-ribbon
    * 通过 @EnableDiscoveryClient 注解注册到服务中心
    * 配置注入 RestTemplate 的 Bean，并通过 @LoadBalanced 注解表明开启负载均衡功能
    * 在这里我们直接用的程序名替代了具体的 URL 地址，在 Ribbon 中它会根据服务名来选择具体的服务实例，根据服务实例在请求的时候会用具体的 URL 替换掉服务名，代码如下：
    ```java
      @Service
      public class AdminService {
      
          @Autowired
          private RestTemplate restTemplate;
      
          // Ribbon 中使用熔断器
          @HystrixCommand(fallbackMethod = "hiError")
          public String sayHi(String message) {
              return restTemplate.getForObject("http://HELLO-SPRING-CLOUD-SERVICE-ADMIN/hi?message=" + message, String.class);
          }
      
          public String hiError(String message) {
              return "Hi，your message is :\"" + message + "\" but request error.";
          }
      }
    ```
    * 在浏览器上多次访问 http://localhost:8764/hi?message=HelloRibbon, 浏览器交替显示:
    Hi，your message is :"HelloRibbon" i am from port：8762
    Hi，your message is :"HelloRibbon" i am from port：8763
    * 此时的架构:
        * 一个服务注册中心，Eureka Server，端口号为：8761
        * service-admin 工程运行了两个实例，端口号分别为：8762，8763
        * web-admin-ribbon 工程端口号为：8764
        * web-admin-ribbon 通过 RestTemplate 调用 service-admin 接口时因为启用了负载均衡功能故会轮流调用它的 8762 和 8763 端口
    ![](https://www.funtl.com/assets/Lusifer201805292246004.png)
    * 附：
        * 在 IDEA 中配置一个工程启动多个实例
            * 点击 Run -> Edit Configurations...
              ![](https://www.funtl.com/assets/Lusifer201805292246005.png)
            * 选择需要启动多实例的项目并去掉 Single instance only 前面的勾
              ![](https://www.funtl.com/assets/Lusifer201805292246006.png)
            * 通过修改 application.yml 配置文件的 server.port 的端口，启动多个实例，需要多个端口，分别进行启动即可。
* [创建服务消费者（Feign）](https://www.funtl.com/zh/spring-cloud-netflix/Spring-Cloud-%E5%88%9B%E5%BB%BA%E6%9C%8D%E5%8A%A1%E6%B6%88%E8%B4%B9%E8%80%85%EF%BC%88Feign%EF%BC%89.html#%E6%A6%82%E8%BF%B0)
    * Feign 是一个声明式的伪 Http 客户端，它使得写 Http 客户端变得更简单。使用 Feign，只需要创建一个接口并注解。它具有可插拔的注解特性，可使用 Feign 注解和 JAX-RS 注解。Feign 支持可插拔的编码器和解码器。Feign 默认集成了 Ribbon，并和 Eureka 结合，默认实现了负载均衡的效果
    * Feign 采用的是基于接口的注解, Feign 整合了 ribbon
    * hello-spring-cloud-web-admin-feign
    * 通过 @EnableFeignClients 注解开启 Feign 功能
    * 通过 @FeignClient("服务名") 注解来指定调用哪个服务。代码如下：
    * 在浏览器上多次访问 http://localhost:8765/hi?message=HelloFeign
    浏览器交替显示：
    Hi，your message is :"HelloFeign" i am from port：8762
    Hi，your message is :"HelloFeign" i am from port：8763
 * [Ribbon 中使用熔断器](https://www.funtl.com/zh/spring-cloud-netflix/Spring-Cloud-%E4%BD%BF%E7%94%A8%E7%86%94%E6%96%AD%E5%99%A8%E9%98%B2%E6%AD%A2%E6%9C%8D%E5%8A%A1%E9%9B%AA%E5%B4%A9.html#%E6%A6%82%E8%BF%B0)
    * hello.spring.cloud.web.admin.ribbon
    * 在 Ribbon 调用方法上增加 @HystrixCommand 注解并指定 fallbackMethod 熔断方法
    * 此时我们关闭服务提供者，再次请求 http://localhost:8764/hi?message=HelloRibbon 浏览器会显示
        Hi，your message is :"HelloRibbon" but request error.
* [Feign 中使用熔断器](https://www.funtl.com/zh/spring-cloud-netflix/Spring-Cloud-%E4%BD%BF%E7%94%A8%E7%86%94%E6%96%AD%E5%99%A8%E9%98%B2%E6%AD%A2%E6%9C%8D%E5%8A%A1%E9%9B%AA%E5%B4%A9.html#feign-%E4%B8%AD%E4%BD%BF%E7%94%A8%E7%86%94%E6%96%AD%E5%99%A8)
    * hello.spring.cloud.web.admin.feign.service
    * 创建熔断器类并实现对应的 Feign 接口
    * 此时我们关闭服务提供者，再次请求 http://localhost:8765/hi?message=HelloFeign 浏览器会显示：
    Hi，your message is :"HelloFeign" but request error.
* [使用熔断器仪表盘监控](https://www.funtl.com/zh/spring-cloud-netflix/Spring-Cloud-%E4%BD%BF%E7%94%A8%E7%86%94%E6%96%AD%E5%99%A8%E4%BB%AA%E8%A1%A8%E7%9B%98%E7%9B%91%E6%8E%A7.html#%E6%A6%82%E8%BF%B0)
    * hello.spring.cloud.web.admin.ribbon
    * 在 Application 中增加 @EnableHystrixDashboard 注解
    * Spring Boot 2.x 版本开启 Hystrix Dashboard 与 Spring Boot 1.x 的方式略有不同，需要增加一个 HystrixMetricsStreamServlet 的配置
    * 浏览器端访问 http://localhost:8764/hystrix 界面如下
    ![](https://www.funtl.com/assets/Lusifer201805292246009.png)
    * 点击 Monitor Stream，进入下一个界面，访问 http://localhost:8764/hi?message=HelloRibbon 此时会出现监控界面
    ![](https://www.funtl.com/assets/Lusifer201805292246010.png)
    * Hystrix Dashboard 界面监控参数
    ![](https://www.funtl.com/assets/20171123110838020.png)
    * [Hystrix 常用配置信息](https://www.funtl.com/zh/spring-cloud-netflix/Spring-Cloud-%E4%BD%BF%E7%94%A8%E7%86%94%E6%96%AD%E5%99%A8%E4%BB%AA%E8%A1%A8%E7%9B%98%E7%9B%91%E6%8E%A7.html#%E9%99%84%EF%BC%9Ahystrix-%E8%AF%B4%E6%98%8E)
    * 附：Hystrix 说明
        * 什么情况下会触发 fallback 方法
* [使用路由网关统一访问接口](https://www.funtl.com/zh/spring-cloud-netflix/Spring-Cloud-%E4%BD%BF%E7%94%A8%E8%B7%AF%E7%94%B1%E7%BD%91%E5%85%B3%E7%BB%9F%E4%B8%80%E8%AE%BF%E9%97%AE%E6%8E%A5%E5%8F%A3.html#%E6%A6%82%E8%BF%B0)
    * 在 Spring Cloud 微服务系统中，一种常见的负载均衡方式是，客户端的请求首先经过负载均衡（Zuul、Ngnix），再到达服务网关（Zuul 集群），然后再到具体的服。服务统一注册到高可用的服务注册中心集群，服务的所有的配置文件由配置服务管理，配置服务的配置文件放在 GIT 仓库，方便开发人员随时改配置。
    * ![](https://www.funtl.com/assets/Lusifer201805292246011.png)
    * [Zuul 简介](https://www.funtl.com/zh/spring-cloud-netflix/Spring-Cloud-%E4%BD%BF%E7%94%A8%E8%B7%AF%E7%94%B1%E7%BD%91%E5%85%B3%E7%BB%9F%E4%B8%80%E8%AE%BF%E9%97%AE%E6%8E%A5%E5%8F%A3.html#zuul-%E7%AE%80%E4%BB%8B)
    * 增加 @EnableZuulProxy 注解开启 Zuul 功能
    * 依次运行 EurekaApplication、ServiceAdminApplication、WebAdminRibbonApplication、WebAdminFeignApplication、ZuulApplication**
    * 打开浏览器访问：http://localhost:8769/api/a/hi?message=HelloZuul 浏览器显示
    ```Hi，your message is :"HelloZuul" i am from port：8763```
    * 打开浏览器访问：http://localhost:8769/api/b/hi?message=HelloZuul 浏览器显示
    ```Hi，your message is :"HelloZuul" i am from port：8763```
* [使用路由网关的服务过滤功能](https://www.funtl.com/zh/spring-cloud-netflix/Spring-Cloud-%E4%BD%BF%E7%94%A8%E8%B7%AF%E7%94%B1%E7%BD%91%E5%85%B3%E7%9A%84%E6%9C%8D%E5%8A%A1%E8%BF%87%E6%BB%A4%E5%8A%9F%E8%83%BD.html#%E5%88%9B%E5%BB%BA%E6%9C%8D%E5%8A%A1%E8%BF%87%E6%BB%A4%E5%99%A8)
    * hello.spring.cloud.zuul.filter
    * Zuul 不仅仅只是路由，还有很多强大的功能，本节演示一下它的服务过滤功能，比如用在安全验证方面。
    * 继承 ZuulFilter 类并在类上增加 @Component 注解就可以使用服务过滤功能了，非常简单方便
    * filterType
        * 返回一个字符串代表过滤器的类型，在 Zuul 中定义了四种不同生命周期的过滤器类型
        * pre：路由之前
          routing：路由之时
          post： 路由之后
          error：发送错误调用
    * filterOrder
        * 过滤的顺序
    * shouldFilter
        * 是否需要过滤，这里是 true，需要过滤
    * run
        * 过滤器的具体业务代码
    * 浏览器访问：http://localhost:8769/api/a/hi?message=HelloZuul 网页显示:
    ```Token is empty```
    * 浏览器访问：http://localhost:8769/api/b/hi?message=HelloZuul&token=123 网页显示
    ```Hi，your message is :"HelloZuul" i am from port：8763```