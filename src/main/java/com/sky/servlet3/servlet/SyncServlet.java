package com.sky.servlet3.servlet;

import com.sky.servlet3.utils.TestUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
