# Springboot servlet 3.0 组件学习
# Servlet 3.0 中关于Shared libraries / runtimes pluggability
```
    An instance of the ServletContainerInitializer is looked up via the jar services API by the container at container / application startup time. The framework 
providing an implementation of the ServletContainerInitializer MUST bundle in the META-INF/services directory of the jar file a file called 
javax.servlet.ServletContainerInitializer, as per the jar services API, that points to the implementation class of the ServletContainerInitializer.

    We also have an annotation - HandlesTypes. The HandlesTypes annotation on the implementation of the ServletContainerInitializer is used to express interest 
in classes that may have anotations (type, method or field level annotations) specified in the value of the HandlesTypes or if it extends / implements one those 
classes anywhere in the class’ super types. The container uses the HandlesTypes annotation to determine when to invoke the initializer's onStartup method. When 
examining the classes of an application to see if they match any of the criteria specified by the HandlesTypes annotation of a ServletContainerInitializer, the container 
may run into class loading problems if one or more of the application's optional JAR files are missing. Since the container is not in a position to decide whether these 
types of class loading failures will prevent the application from working correctly, it must ignore them, while at the same time providing a configuration option that 
would log them.
```
测试代码如下
```
// 支持Servlet 3.0 的容器启动的时候,会将@HandlesTypes指定的这个类型所有的子类(实现类子接口等)都放入set传递过来
@HandlesTypes(value = {DemoService.class})
public class DemoServletContainerInitializer implements ServletContainerInitializer {

    /**
     * @param set            指定的类所有的子类型
     * @param servletContext 当前web应用的ServletContext,一个web应用一个ServletContext
     * @throws ServletException
     */
    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
        System.out.println("--->All aware classes as follow :");
        set.stream().forEach(System.out::println);
        System.out.println("--->All aware classes end !!!");

        // 可以使用servletContext注册web组件(servlet,filter,listener)
        // 如果引入第三方包,需要往里面增加监听器,过滤器,可以用此方法
        
        // 注册servlet
        ServletRegistration.Dynamic helloServlet = servletContext.addServlet("helloServlet", new HelloServlet());
        helloServlet.addMapping("/hello");
        
        // 注册监听器
        servletContext.addListener(new HelloListener());
        
        // 注册过滤器
        FilterRegistration.Dynamic helloFilter = servletContext.addFilter("helloFilter", HelloFilter.class);
        helloFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
    }
}
```

# Http同步请求处理及问题
```
    在Servlet 3.0之前,Servlet采用Thread-Per-Request的方式处理请求,即每次http请求都由某一个线程从头到尾负责处理.
   
    如果一个请求需要进行IO操作,比如访问数据库,调用第三方服务接口等,那么其所对应的线程将同步地等待IO操作完成,而IO操
作是非常慢的,所以此时的线程并不能及时的释放回线程池以供后续使用,在并发量越来越大的情况下,这将带来严重的性能问题.即便
是Spring,Struts这样的高层框架也脱离不了这样的桎梏,因为他们都是建立在Servlet之上的.为了解决这样的问题,Servlet 3.0
引入了异步处理,然后再Servlet 3.1中,引入了非阻塞IO来进一步增强异步处理的性能.
```
同步方式是一个请求处理完所有本次请求的任务,测试代码如下:
```
@WebServlet("/sync")
public class SyncServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long start = System.currentTimeMillis();

        System.out.println(Thread.currentThread().getName() + " do sync task before>>>");

        // 耗时任务
        TestUtils.doTask(resp);

        resp.getOutputStream().write(" [sync] good good study , day day up !".getBytes());

        System.out.println(Thread.currentThread().getName() + " do sync task end ! 耗时: " + ((System.currentTimeMillis() - start) / 1000.0) + "秒");
    }
}
```
执行结果如下:
```
http-nio-8080-exec-5 do sync task before>>>
http-nio-8080-exec-5 do tasking ...
http-nio-8080-exec-5 do sync task end ! 耗时: 5.002秒
```

# Servlet 3.0 异步请求处理
```
@WebServlet(value = "/async", asyncSupported = true)
public class AsyncServlet extends HttpServlet {


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        long start = System.currentTimeMillis();

        // 1.支持异步处理 asyncSupported = true
        System.out.println("主线程[" + Thread.currentThread().getName() + "]- do async task before>>>");

        // 2.开启异步模式
        AsyncContext startAsync = req.startAsync();

        // 3.业务逻辑进行异步处理
        startAsync.start(() -> {
            try {
                System.out.println("副线程[" + Thread.currentThread().getName() + "]- 开始...");
                // 获取异步上下文
                AsyncContext asyncContext = req.getAsyncContext();

                HttpServletResponse response =(HttpServletResponse) asyncContext.getResponse();

                // 任务
                TestUtils.doTask(response);
                // 执行完成
                startAsync.complete();


                // 4.响应前端
                response.getOutputStream().write(" [async] good good study , day day up !".getBytes());
                System.out.println("副线程[" + Thread.currentThread().getName() + "]- 结束 ! 耗时: " + ((System.currentTimeMillis() - start) / 1000.0) + "秒");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        System.out.println("主线程[" + Thread.currentThread().getName() + "]- do async task end ! 耗时: " + ((System.currentTimeMillis() - start) / 1000.0) + "秒");
    }
}
```
异步请求打印日志如下:
```
// 异步请求主线程立即响应,但后台任务执行完再响应任务结果
// 注意:必须在异步执行时,再获取response取到异步的response,否则会报错不支持异步

主线程[http-nio-8080-exec-8]- do async task before>>>
主线程[http-nio-8080-exec-8]- do async task end ! 耗时: 0.0秒
副线程[http-nio-8080-exec-9]- 开始...
http-nio-8080-exec-9 do tasking ...
副线程[http-nio-8080-exec-9]- 结束 ! 耗时: 5.001秒
```