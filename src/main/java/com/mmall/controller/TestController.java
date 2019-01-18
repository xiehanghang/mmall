package com.mmall.controller;

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
public class TestController {

    private Logger logger = LoggerFactory.getLogger(TestController.class);

    @RequestMapping("test.do")
    @ResponseBody
    public String test(String value) {
        logger.info("testinfo");
        logger.error("testerror");
        logger.warn("testwarn");
        return "testStr" + value;
    }
}
