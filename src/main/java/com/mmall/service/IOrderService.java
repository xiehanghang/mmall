package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderVo;

import java.util.Map;

/**
 * Created by Enzo Cotter on 2019/1/16.
 */
public interface IOrderService {
    //--------------------------------portal------------------------------
    ServerResponse pay(Long orderNo, Integer userId, String path);
    ServerResponse aliCallback(Map<String, String> params);
    ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);
    ServerResponse<String> cancel(Integer userId,Long orderNo);
    ServerResponse create(Integer userId, Integer shippingId);
    ServerResponse getOrderCartProduct(Integer userId);
    ServerResponse getOrderDetail(Integer userId, Long orderNo);
    ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);

    //--------------------------------backend------------------------------
    ServerResponse<PageInfo> manageList(int pageNum, int pageSize);
    ServerResponse<OrderVo> manageDetail(Long orderNo);
    ServerResponse<PageInfo> manageSearch(Long orderNo, int pageNum, int pageSize);
    ServerResponse<String> manageSendGoods(Long orderNo);
}
