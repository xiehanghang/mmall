package com.mmall.common;

import com.google.common.collect.Sets;
import com.mmall.pojo.Product;

import java.util.Set;

/**
 * Created by Enzo Cotter on 2019/1/11.
 */
public class Const {
    public static final String CURRENT_USER = "current_user";
    public static final String EMAIL= "email";
    public static final String USERNAME = "username";

    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc", "price_asc");
    }

    public interface Cart {
        int CHECKED = 1; //购物车选中
        int UN_CHECKED = 0; //购物车未选中
        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }

    public interface Role {
        int ROLE_CUSTOMER = 0; //普通用户
        int ROLE_ADMIN = 1; //管理员
     }

     public enum ProductStatusEnum{
        ON_SALE(1, "在售");

        private String value;
        private int code;
        ProductStatusEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

         public String getValue() {
             return value;
         }

         public int getCode() {
             return code;
         }
     }
}
