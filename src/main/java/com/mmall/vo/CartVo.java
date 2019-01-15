package com.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Enzo Cotter on 2019/1/14.
 */
public class CartVo {

    private List<CartProductVo> cartProductVoList;
    private BigDecimal cartTotalPrice; //购物车总价
    private boolean allChecked; //是否全勾选
    private String imageHost;

    public List<CartProductVo> getCartProductVoList() {
        return cartProductVoList;
    }

    public void setCartProductVoList(List<CartProductVo> cartProductVoList) {
        this.cartProductVoList = cartProductVoList;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public boolean isAllChecked() {
        return allChecked;
    }

    public void setAllChecked(boolean allCheck) {
        this.allChecked = allCheck;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
