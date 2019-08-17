package com.sky.servlet3.utils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TestUtils {

    public static void doTask(HttpServletResponse resp) throws IOException {
        ServletOutputStream outputStream = resp.getOutputStream();
        try {
            System.out.println(Thread.currentThread().getName() + " do tasking ...");

            outputStream.println((Thread.currentThread().getName() + " do task ...!!!"));
            outputStream.flush();

            TimeUnit.SECONDS.sleep(5);

        } catch (Exception e) {
            outputStream.write((Thread.currentThread().getName() + " do task exp: " + e.getMessage()).getBytes());
        }
    }
}
