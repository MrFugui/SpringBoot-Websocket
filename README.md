## **写在前面：**

最近有一个想法，做一个程序员师徒管理系统。因为在大学期间的我在学习java的时候非常地迷茫，找不到自己的方向，也没有一个社会上有经验的前辈去指导，所以走了很多的弯路。后来工作了，想把自己的避坑经验分享给别人，但是发现身边都是有经验的开发者，也没有机会去分享自己的想法，所以富贵同学就想做一个程序员专属的师徒系统，秉承着徒弟能够有人指教少走弯路，师傅能桃李满天下的目的，所以开始做这个师徒系统，也会同步更新该系统所用到的技术，并且作为教程分享给大家，希望大家能够关注一波。
![请添加图片描述](https://img-blog.csdnimg.cn/93ce07a15e6c4ed2a125e4bd2d194e52.jpg)
其实聊天功能最开始的时候我们可以创建一个表，当人们发送的时候将消息往表里面插入，接收的时候将消息从表里面取出，然后定时去取出消息，这样勉强能实现一个消息聊天的功能，但是会大大的消耗服务器的性能，所以我们用到了一项新技术：WebSocket。那么老规矩，WebSocket是什么呢？
![在这里插入图片描述](https://img-blog.csdnimg.cn/4f9193bc7771463ab8b99a64f4a62075.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAY3NkbmVyTQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
在这里富贵同学用自己的话总结一次：websocket使得服务器能够主动得向客户端推送消息，而且服务器和客户端建立连接只要一次确认就可以了。
好了，那么怎么将这个功能集成到师徒管理系统中来呢？在这里富贵同学要感谢[wensocket开源项目](https://gitee.com/shenzhanwang/Spring-websocket)，因为富贵同学在改项目上进行改进使得websocket能够集成springsecurity和jwt技术到师徒管理系统中来。大家可以先去瞧一瞧这个项目，对接下来的教程能够理解得更加深透。

## 废话不多说，开始我们的实战
![在这里插入图片描述](https://img-blog.csdnimg.cn/8f20686e40fa4ae6a534d38360423778.jpg?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAY3NkbmVyTQ==,size_16,color_FFFFFF,t_70,g_se,x_16#pic_center)

## 第一步，我们导入相关的jar包

```java
	    <!--websocket-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-websocket</artifactId>
            <version>2.1.6.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
            <version>2.1.6.RELEASE</version>
        </dependency>
        <!--websocket-->
```
第二步，创建websocket的service类

```java
package com.wangfugui.apprentice.service;


import com.alibaba.fastjson.JSON;
import com.wangfugui.apprentice.dao.domain.Message;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint("/webSocket/{username}")
@Component
public class WebSocketServer {
	 //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static AtomicInteger onlineNum = new AtomicInteger();

    //concurrent包的线程安全Set，用来存放每个客户端对应的WebSocketServer对象。
    private static ConcurrentHashMap<String, Session> sessionPools = new ConcurrentHashMap<>();

    //发送消息
    public void sendMessage(Session session, String message) throws IOException {
        if(session != null){
            synchronized (session) {
                System.out.println("发送数据：" + message);
                session.getBasicRemote().sendText(message);
            }
        }
    }
    //给指定用户发送信息
    public void sendInfo(String userName, String message){
        Session session = sessionPools.get(userName);
        try {
            sendMessage(session, message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // 群发消息
    public void broadcast(String message){
    	for (Session session: sessionPools.values()) {
            try {
                sendMessage(session, message);
            } catch(Exception e){
                e.printStackTrace();
                continue;
            }
        }
    }

    //建立连接成功调用
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "username") String userName){
        sessionPools.put(userName, session);
        addOnlineCount();
        System.out.println(userName + "加入webSocket！当前人数为" + onlineNum);
        // 广播上线消息
        Message msg = new Message();
        msg.setDate(new Date());
        msg.setTo("0");
        msg.setText(userName);
        broadcast(JSON.toJSONString(msg,true));
    }

    //关闭连接时调用
    @OnClose
    public void onClose(@PathParam(value = "username") String userName){
        sessionPools.remove(userName);
        subOnlineCount();
        System.out.println(userName + "断开webSocket连接！当前人数为" + onlineNum);
        // 广播下线消息
        Message msg = new Message();
        msg.setDate(new Date());
        msg.setTo("-2");
        msg.setText(userName);
        broadcast(JSON.toJSONString(msg,true));
    }

    //收到客户端信息后，根据接收人的username把消息推下去或者群发
    // to=-1群发消息
    @OnMessage
    public void onMessage(String message) throws IOException{
        System.out.println("server get" + message);
        Message msg=JSON.parseObject(message, Message.class);
		msg.setDate(new Date());
		if (msg.getTo().equals("-1")) {
			broadcast(JSON.toJSONString(msg,true));
		} else {
			sendInfo(msg.getTo(), JSON.toJSONString(msg,true));
		}
    }

    //错误时调用
    @OnError
    public void onError(Session session, Throwable throwable){
        System.out.println("发生错误");
        throwable.printStackTrace();
    }

    public static void addOnlineCount(){
        onlineNum.incrementAndGet();
    }

    public static void subOnlineCount() {
        onlineNum.decrementAndGet();
    }
    
    public static AtomicInteger getOnlineNumber() {
        return onlineNum;
    }
    
    public static ConcurrentHashMap<String, Session> getSessionPools() {
        return sessionPools;
    }
}

```
大家仔细看这个类里面的四个注解：
`@OnOpen    @OnClose     @OnMessage     @OnError`
分别是websocket连接，关闭，收到消息，错误时调用 `@ServerEndpoint`也要加上，接下来我们配置一个config类使得上面的类生效：


```java
package com.wangfugui.apprentice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebScoket配置处理器
 */
@Configuration
public class WebSocketConfig {
	 /**
     * ServerEndpointExporter 作用
     *
     * 这个Bean会自动注册使用@ServerEndpoint注解声明的websocket endpoint
     *
     * @return
     */
	@Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}

```
这里是最关键的两个配置类了。
本来到这里就可以跑起来进行聊天了，前端的代码在仓库里面，这里就不贴出来来了。但是我们发现由于师徒系统是集成的jwt的，所以我们没有办法进行基本的聊天，因为都被拦截了！所以我们要做的第一步就是把相关接口放开：

```java
 @Override
    protected void configure(HttpSecurity http) throws Exception {
        // post请求要关闭csrf验证,不然访问报错；实际开发中开启，需要前端配合传递其他参数
        http.csrf().disable()
                .authorizeRequests()
                //swagger
                .antMatchers("/swagger-ui.html").anonymous()
                .antMatchers("/swagger-resources/**").anonymous()
                .antMatchers("/webjars/**").anonymous()
                .antMatchers("/*/api-docs").anonymous()
                //设置哪些路径不需要认证,这里也能放行静态资源
                .antMatchers("/webSocket/**").anonymous()
                .antMatchers("/static/**").anonymous()
                .antMatchers("/css/**", "/js/**").anonymous()
                .antMatchers("/favicon.ico").anonymous()
                //放开注册,登录用户接口
                .antMatchers("/user/register").anonymous()
                .antMatchers("/login").anonymous()
                .antMatchers("/logoutSystem").anonymous()
                .antMatchers("/chatroom").anonymous()
                .antMatchers("/onlineusers").anonymous()
                .antMatchers("/currentuser").anonymous()
                .anyRequest().authenticated() // 所有请求都需要验证
                .and()     //这里采用链式编程
                .logout()
                //注销成功后，调转的页面
                .logoutSuccessUrl("/login")
                // 配置自己的注销URL，默认为 /logout
                .logoutUrl("/logoutSystem")
                // 是否销毁session，默认ture
                .invalidateHttpSession(true)
                //  删除指定的cookies
                .deleteCookies(JwtTokenUtils.TOKEN_HEADER)
                .and()
                //添加用户账号的认证
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                //添加用户权限的认证
                .addFilter(new JWTAuthorizationFilter(authenticationManager()))
                //我们可以准确地控制什么时机创建session，有以下选项进行控制：
                //always – 如果session不存在总是需要创建；
                //ifRequired – 仅当需要时，创建session(默认配置)；
                //never – 框架从不创建session，但如果已经存在，会使用该session ；
                //stateless – Spring Security不会创建session，或使用session；
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                //添加没有携带token或者token无效操作
                .authenticationEntryPoint(new JWTAuthenticationEntryPoint())
                //添加无权限时的处理
                .accessDeniedHandler(new JWTAccessDeniedHandler());
    }
```
接着我们更改我们的默认登录接口：

```java
 public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        //这里特别注意是登录的接口，自定义登录接口，不写的话默认"/login"
        super.setFilterProcessesUrl("/loginvalidate");
    }
```
这样我们的默认登录就可以登录了，但是由于要集成到我们的系统中来，我们需要jwt生成的字符串，所以在登录成功我们要将jwt秘钥存储在cookie中来：
在`JWTAuthenticationFilter`类中

```java
 // 成功验证后调用的方法
    // 如果验证成功，就生成token并返回
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) {

        JwtUser jwtUser = (JwtUser) authResult.getPrincipal();
        System.out.println("jwtUser:" + jwtUser.toString());
        boolean isRemember = rememberMe.get() == 1;

        String role = "";
        Collection<? extends GrantedAuthority> authorities = jwtUser.getAuthorities();
        for (GrantedAuthority authority : authorities){
            role = authority.getAuthority();
        }

        String token = JwtTokenUtils.createToken(jwtUser.getUsername(), role, isRemember);
        // 返回创建成功的token
        // 但是这里创建的token只是单纯的token
        // 按照jwt的规定，最后请求的时候应该是 `Bearer token`
        response.setHeader("Authorization", JwtTokenUtils.TOKEN_PREFIX + token);
        response.addCookie(new Cookie("Authorization",token));
    }
```
这样之后我们在调用聊天室接口中就可以获取cookie，从而将在线列表显示出来

```java
   @RequestMapping("/chatroom")
    public String chatroom(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        //如果没有cookie则返回登录页面
        Cookie authCookie = Arrays.stream(cookies).filter(cookie -> cookie.getName()
                .contains(JwtTokenUtils.TOKEN_HEADER)).collect(Collectors.toList()).get(0);

        if (authCookie == null) {
            return "login";
        }
        String tokenHeader = authCookie.getValue();
        String username = JwtTokenUtils.getUsername(tokenHeader);
        HttpSession session = request.getSession();
        User idByUserName = userService.getIdByUserName(username);
        session.setAttribute("uid", idByUserName.getId());
        return "chatroom";
    }
```
还有一个登录接口:


```java
   @RequestMapping("/login")
    public String login(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return "login";
        }
        //如果没有cookie则返回登录页面
        List<Cookie> collect = Arrays.stream(cookies).filter(cookie -> cookie.getName()
                .contains(JwtTokenUtils.TOKEN_HEADER)).collect(Collectors.toList());

        if (collect.isEmpty()) {
            return "login";
        }
        return "home";
    }
```
我们创建一个首页页面

```html
<!DOCTYPE>
<html>
<head>
    <title>login</title>
</head>
<body>
<div class="container vertical-center">
    <a href="/chatroom">聊天室</a>
    <form action="/logoutSystem" method="post">
        <button type="submit" >注销</button>
    </form>
</div>

</body>

</html>

```
这样我们就可以进行注销和进入聊天室了。多余代码就不在这里贴出，请大家移步到仓库观看：[SpringBoot+WebSocket](https://gitee.com/WangFuGui-Ma/spring-boot-websocket)
## 说在之后
师徒系统我会一直更新，因为是开源的项目，所以我也希望又更多的小伙伴加入进来！！
这是程序员师徒管理系统的地址：
[程序员师徒管理系统](https://gitee.com/WangFuGui-Ma/Programmer-Apprentice)
![在这里插入图片描述](https://img-blog.csdnimg.cn/23eefccf438f4aaeb5a143f1db1f0fa7.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAY3NkbmVyTQ==,size_16,color_FFFFFF,t_70,g_se,x_16)