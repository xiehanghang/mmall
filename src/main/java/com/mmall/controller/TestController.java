package com.mmall.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Enzo Cotter on 2019/1/17.
 */
@Controller
@RequestMapping("/test/")
@Slf4j
public class TestController {

    @RequestMapping("test.do")
    @ResponseBody
    public String test(String value) {
        log.info("testinfo");
        log.error("testerror");
        log.warn("testwarn");
        return "testStr" + value;
    }
}
