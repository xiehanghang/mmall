package com.mmall.test;

import org.junit.Test;

import java.math.BigDecimal;

/**
 * Created by Enzo Cotter on 2019/1/14.
 */
public class BigDecimalTest {

    @Test
    public void test1() {
        System.out.println(0.05+0.01);
        System.out.println(1.0 - 0.42);
        System.out.println(4.015*100);
        System.out.println(123.3/100);
    }

    @Test
    public void test2(){
        BigDecimal a = new BigDecimal(0.05);
        BigDecimal b = new BigDecimal(0.01);
        System.out.println(a.add(b));
    }

    @Test
    public void test3(){
        BigDecimal a = new BigDecimal("0.05");
        BigDecimal b = new BigDecimal("0.01");
        System.out.println(a.add(b));
    }
}
