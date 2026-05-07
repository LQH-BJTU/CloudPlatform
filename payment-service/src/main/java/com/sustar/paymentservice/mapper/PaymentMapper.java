package com.sustar.paymentservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sustar.paymentservice.po.PaymentPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付数据访问接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD操作
 */
@Mapper
public interface PaymentMapper extends BaseMapper<PaymentPO> {

}
