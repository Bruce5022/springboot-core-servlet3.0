package com.sky.servlet3.servlet;

import com.sky.servlet3.utils.TestUtils;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 异步处理任务:
 * 1.支持异步处理 asyncSupported = true
 * 2.开启异步模式
 * 3.异步处理业务逻辑;
 */
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
