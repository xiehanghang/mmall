# 基于 SSM 的仿天猫前后端分离项目

**开发环境：** IntelliJ IDEA + maven + git + Centos + Vsftpd



**软件架构：** Spring + SpringMVC + Mybatis + Nginx + Tomcat + Redis + Jedis + Lombok + Jackson + Guava Cache



**系统描述：** 本项目是基于 SSM 框架开发的前后端分离电商网站，数据库采用的是 MySQL。包含用户管理，订单，品类，产品，购物车，地址，在线支付七个模块。项目还融合了 Tomcat 集群，Nginx 负载均衡，Redis 缓存分布式，Redis 分布式锁，单点登录等技术。



## 项目架构图

<img src="https://raw.githubusercontent.com/xiehanghang/mmall/master/README-img/jiagou.png">



这是我的阿里云线上部署地址：[点击这里](<http://www.xiehang.art/>)



## 项目功能接口图

<img src="https://raw.githubusercontent.com/xiehanghang/mmall/master/README-img/mmall%20%E9%A1%B9%E7%9B%AE%E5%8A%9F%E8%83%BD%E6%8E%A5%E5%8F%A3%E6%B8%85%E5%8D%95.png">



## 项目预览图

<img src="https://raw.githubusercontent.com/xiehanghang/mmall/master/README-img/mmall-gif.gif">





# Nginx 配置

## Nginx 目录(root)转发 image.imooc.com.conf

- 在 `conf` 文件夹下创建 `vhost` 文件 并在 `conf` 下的 `nginx.conf` 文件中加入 `include vhost/*.conf;`

- `nginx.exe -t `验证 `nginx` 的配置文件是否生效

- 启动 Nginx `nginx.exe`

- ```
  server {
      listen 80; //监听 80 端口
      autoindex on;
      server_name image.imooc.com; //当访问这个域名时
      access_log d:/access.log combined;
      index index.html index.htm index.jsp index.php;
      #error_page 404 /404. html;
      if ($query_string ~* ".*[\;'\<\>].*") {
          return 404;
      }
      location ~ /(mmall_fe|mmall_admin_fe)/dist/view/* {
                  deny all;
          }
          location / {
                  root D:\coder\ftpfile\img;
                  add_header Access-Control-Allow-Origin *;
          }
  }
  ```

- 访问 `image.imooc.com` 时转发到路径 `root D:\coder\ftpfile\img;` 从而实现路径转发

- 修改配置文件后要重启 `nginx` ,`nginx.exe -s reload`

- 修改 Window 域名，`C:\Windows\System32\drivers\etc\hosts`



## Nginx ip 端口转发 tomcat.imooc.com.conf

- ```
  server {
      listen 80;
      autoindex on;
      server_name tomcat.imooc.com;
      access_log d:/access.log combined;
      index index.html index.htm index.jsp index.php;
      #error_page 404 /404. html;
      if ($query_string ~* ".*[\;'\<\>].*") {
          return 404;
      }
      
      location / {
  	proxy_pass http://127.0.0.1:8080;
  	add_header Access-Control-Allow-Origin *;
      }
  }
  ```

- 当访问域名 `tomcat.imooc.com` 时转发到 `proxy_pass http://127.0.0.1:8080;`，从而实现 ip 端口转发



## Nginx 负载均衡 http 转发

```
upstream www.happymmall.com{
	 server www.happymmall.com:8080 weight=1;
	 server www.happymmall.com:9080 weight=1;
	 #server 127.0.0.1:8080;
	 #server 127.0.0.1:9080;
}
server {
    listen 80;
    autoindex on;
    server_name www.happymmall.com happymmall.com	;
    access_log d:/access.log combined;
    index index.html index.htm index.jsp index.php;
    #error_page 404 /404. html;
    if ($query_string ~* ".*[\;'\<\>].*") {
        return 404;
    }
    
    location / {
	proxy_pass http://www.happymmall.com;
	add_header Access-Control-Allow-Origin *;
    }
}
```

讲解：当访问 ` server_name www.happymmall.com happymmall.com` 时，会转到  `proxy_pass http://www.happymmall.com;` ，然后根据负载均衡到 `upstream www.happymmall.com`(这里我配置了本机的 hosts 文件 当访问 www.happymmall.com 会转发到 127.0.0.1)

## 错误总结

- 路径名要使用英文，否则会报错 500



# 单点登录与 Tomcat 集群

<img src="https://raw.githubusercontent.com/xiehanghang/mmall/master/README-img/%E5%8D%95%E7%82%B9%E7%99%BB%E5%BD%951.png">



```Java
/**
     * 从 Tomcat 获取到的 sessionId 作为 token ，以此为 key ，序列化查询结果为 value 存储在 Redis 中。
     * 并新建一个 Cookie ，new Cookie(COOKIE_NAME, token)。token 为 sessionid。
     * @param response
     * @param token
     */
    public static void writeLoginToken(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(COOKIE_NAME, token); // COOKIE_NAME = mmall_login_token
        cookie.setDomain(COOKIE_DOMAIN); // www.happymmall.com
        cookie.setPath("/"); //代表设置在根目录，即 www.happymmall.com/
        cookie.setHttpOnly(true);
        //单位是秒。
        //如果这个maxage不设置的话，cookie就不会写入硬盘，而是写在内存。只在当前页面有效。
        cookie.setMaxAge(60 * 60 * 24 * 365); //-1代表永久
        log.info("write cookieName:{}, coo  kieValue:{}", cookie.getName(), cookie.getValue());
        response.addCookie(cookie);
    }
```



## 解决 SessionId 在多个 Tomcat 不一致问题：使用 Cookie 保存 SessionId

`private final static String COOKIE_DOMAIN = "www.happymmall.com";` 

将 Cookie 写在二级域名 happymmall.com 下，即 三级域名 xxx.happymmall.com 都能访问到这二级域名 Cookie。(以后做微服务可以把用户模块单独设置一个域，user.happymmall.com)

然后通过 www.happymmall.com 来访问



<img src="https://raw.githubusercontent.com/xiehanghang/mmall/master/README-img/%E5%8D%95%E7%82%B9%E7%99%BB%E5%BD%953.png">



<img src="https://raw.githubusercontent.com/xiehanghang/mmall/master/README-img/%E5%8D%95%E7%82%B9%E7%99%BB%E5%BD%954.png">

<img src="https://raw.githubusercontent.com/xiehanghang/mmall/master/README-img/%E5%8D%95%E7%82%B9%E7%99%BB%E5%BD%955.png">



## 解决用户 session 过期问题（SessionExpirefilter）



**问题描述：** 我们设置 session 是有有效期的，当用户访问一个新页面时，Session 的有效期应该重置，而不是用户即使一直在使用客户端，却出现 Session 过期的问题。

**解决方案：**拦截所有 .do 请求，然后重置 session 失效时间



```Java
package com.mmall.controller.common;

import com.mmall.common.Const;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SessionExpireFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;

        String loginToken = CookieUtil.readLoginToken(httpServletRequest);

        if(StringUtils.isNotEmpty(loginToken)){
            //判断logintoken是否为空或者""；
            //如果不为空的话，符合条件，继续拿user信息
            
            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
            User user = JsonUtil.string2Obj(userJsonStr,User.class);
            if(user != null){
                //如果user不为空，则重置session的时间，即调用expire命令
                RedisShardedPoolUtil.expire(loginToken, Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}

```

web.xml

```
<!-- 二期新增重置session时间的filter-->
    <filter>
        <filter-name>sessionExpireFilter</filter-name>
        <filter-class>com.mmall.controller.common.SessionExpireFilter</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>sessionExpireFilter</filter-name>
        <url-pattern>*.do</url-pattern>
    </filter-mapping>

```

### 测试

<img src="https://raw.githubusercontent.com/xiehanghang/mmall/master/README-img/Session%E8%BF%87%E6%9C%9F.png">



先 login.do ，然后 ttl 查看剩余时间，然后随便访问一个 .do 请求（因为拦截的是所有 .do 请求，然后重置时间），再 ttl 查看，发现重置时间了，成功~！





## GuavaCache 迁移 Redis 缓存

一期忘记密码采用 GuavaCache ，这个是存在 Tomcat 服务里的，即 JVM 内存之中。

**一期**

<img src="https://raw.githubusercontent.com/xiehanghang/mmall/master/README-img/GuavaCache%E8%BF%81%E7%A7%BBRedis.png">



**二期**

```Java
@Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0) {
            //说明问题及问题答案是该用户的，并且是正确的
            String forgetToken = UUID.randomUUID().toString();
            // 一期：token 放入本地缓存（存在集群之后的隐患） 二期：放入 Redis 中。
            RedisShardedPoolUtil.setEx(Const.TOKEN_PREFIX + username, forgetToken,60*60*12);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题答案错误");
    }
```

```Java
@Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)) {
            ServerResponse.createByErrorMessage("参数传递错误，Token需要传递");
        }
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        String token = RedisShardedPoolUtil.get(Const.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("token无效或过期");
        }
        if (StringUtils.equals(forgetToken, token)) {
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username, md5Password);

            if (rowCount > 0) {
                return ServerResponse.createBySuccessMsg("修改密码成功");
            }
        } else {
            return ServerResponse.createByErrorMessage("token获取错误，请重新 获取重置密码的token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }
```



# Redis 分布式

## 分布式算法

### 传统分布式算法：效率低下

### Redis 分布式一致性哈希算法：32位 圆环，从 0 开始

<img src="https://raw.githubusercontent.com/xiehanghang/mmall/master/README-img/Redis%E5%88%86%E5%B8%83%E5%BC%8F%E7%AE%97%E6%B3%95.png">

<img src="https://raw.githubusercontent.com/xiehanghang/mmall/master/README-img/Redis%E5%88%86%E5%B8%83%E5%BC%8F%E7%AE%97%E6%B3%952.png">

<img src="https://raw.githubusercontent.com/xiehanghang/mmall/master/README-img/Redis%E5%88%86%E5%B8%83%E5%BC%8F%E7%AE%97%E6%B3%953.png">



将 key 存到顺时针方向最近的 Cache 上，当 Cache 移除或者增加，只会影响到 Cache 到上一个 Cache 的方位的 key，并不会向传统的 hash，牵一发而动全身（导致大量缓存不命中造成缓存穿透从而给数据库增大压力）。



#### 一致性 hash 存在的问题：Hash 倾斜性

<img src="https://raw.githubusercontent.com/xiehanghang/mmall/master/README-img/Redis%E5%88%86%E5%B8%83%E5%BC%8F%E7%AE%97%E6%B3%954.png">

<img src="https://raw.githubusercontent.com/xiehanghang/mmall/master/README-img/Redis%E5%88%86%E5%B8%83%E5%BC%8F%E7%AE%97%E6%B3%955.png">



# Jedis 介绍

<https://www.jianshu.com/p/7913f9984765>



# 集群和分布式的区别

<https://www.zhihu.com/question/20004877>



# SpringSession 实现无入侵的单点登录

这是一个好的方法，以后有时间可以重构一下。



# SpringMVC 全局异常

## 无使用SpringMVC 全局异常

<img src="https://raw.githubusercontent.com/xiehanghang/mmall/master/README-img/SpringMVC%E5%85%A8%E5%B1%80%E5%BC%82%E5%B8%B81.png">



<img src="https://raw.githubusercontent.com/xiehanghang/mmall/master/README-img/SpringMVC%E5%85%A8%E5%B1%80%E5%BC%82%E5%B8%B82.png">



项目细节会被看到



<img src="https://raw.githubusercontent.com/xiehanghang/mmall/master/README-img/SpringMVC%E5%85%A8%E5%B1%80%E5%BC%82%E5%B8%B83.png">



## 扫描包隔离

**交给 springmvc 来扫描 controller**

只扫描 controller，关闭默认的扫描

```
<!-- springmvc 扫描包指定到 controller，防止重复扫描 -->
    <context:component-scan base-package="com.mmall.controller" annotation-config="true" use-default-filters="false">
        <context:include-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
    </context:component-scan>
```

### 具体实现

```java
@Slf4j
@Component
public class ExceptionResolver implements HandlerExceptionResolver{

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        log.error("{} Exception",httpServletRequest.getRequestURI(),e);
        ModelAndView modelAndView = new ModelAndView(new MappingJacksonJsonView());

        //当使用是jackson2.x的时候使用MappingJackson2JsonView，课程中使用的是1.9。
        modelAndView.addObject("status", ResponseCode.ERROR.getCode());
        modelAndView.addObject("msg","接口异常,详情请查看服务端日志的异常信息");
        modelAndView.addObject("data",e.toString());
        return modelAndView;
    }

}
```



# SpringMVC 实现权限统一校验

## 解决问题：大量的重复代码：校验用户是否登录

```xml
<mvc:interceptors>
        <!-- 定义在这里的，所有的都会拦截-->
        <mvc:interceptor>
            <!--manage/a.do  /manage/*-->
            <!--manage/b.do  /manage/*-->
            <!--manage/product/save.do /manage/**-->
            <!--manage/order/detail.do /manage/**-->
            <mvc:mapping path="/manage/**"/>
            <!--<mvc:exclude-mapping path="/manage/user/login.do"/>-->
            <bean class="com.mmall.controller.common.interceptor.AuthorityInterceptor" />
        </mvc:interceptor>
    </mvc:interceptors>
```



## 具体拦截器实现

```Java
/**
 * Created by geely
 */
@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("preHandle");
        //请求中Controller中的方法名
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        //解析HandlerMethod

        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getSimpleName();

        //解析参数,具体的参数key以及value是什么，我们打印日志
        StringBuffer requestParamBuffer = new StringBuffer();
        Map paramMap = request.getParameterMap();
        Iterator it = paramMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String mapKey = (String) entry.getKey();

            String mapValue = StringUtils.EMPTY;

            //request这个参数的map，里面的value返回的是一个String[]
            Object obj = entry.getValue();
            if (obj instanceof String[]) {
                String[] strs = (String[]) obj;
                mapValue = Arrays.toString(strs);
            }
            requestParamBuffer.append(mapKey).append("=").append(mapValue);
        }

        if (StringUtils.equals(className, "UserManageController") && StringUtils.equals(methodName, "login")) {
            log.info("权限拦截器拦截到请求,className:{},methodName:{},param:{}", className, methodName, requestParamBuffer);
            //如果是拦截到登录请求，不打印参数，因为参数里面有密码，全部会打印到日志中，防止日志泄露
            return true;
        }

        log.info("权限拦截器拦截到请求,className:{},methodName:{},param:{}", className, methodName, requestParamBuffer.toString());


        User user = null;

        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isNotEmpty(loginToken)) {
            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
            user = JsonUtil.string2Obj(userJsonStr, User.class);
        }

        if (user == null || (user.getRole().intValue() != Const.Role.ROLE_ADMIN)) {
            //返回false.即不会调用controller里的方法
            response.reset();//geelynote 这里要添加reset，否则报异常 getWriter() has already been called for this response.
            response.setCharacterEncoding("UTF-8");//geelynote 这里要设置编码，否则会乱码
            response.setContentType("application/json;charset=UTF-8");// 这里要设置返回值的类型，因为全部是json接口。

            PrintWriter out = response.getWriter();

            //上传由于富文本的控件要求，要特殊处理返回值，这里面区分是否登录以及是否有权限
            if (user == null) {
                if (StringUtils.equals(className, "ProductManageController") && StringUtils.equals(methodName, "richtextImgUpload")) {
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success", false);
                    resultMap.put("msg", "请登录管理员");
                    out.print(JsonUtil.obj2String(resultMap));
                } else {
                    out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截,用户未登录")));
                }
            } else {
                if (StringUtils.equals(className, "ProductManageController") && StringUtils.equals(methodName, "richtextImgUpload")) {
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success", false);
                    resultMap.put("msg", "无权限操作");
                    out.print(JsonUtil.obj2String(resultMap));
                } else {
                    out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截,用户无权限操作")));
                }
            }
            out.flush();
            out.close();//这里要关闭

            return false;

        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("afterCompletion");
    }
}

```



# Spring Schedule 实现定时关单



# Spring Schedule + Redis 分布式锁构建分布式任务调度

