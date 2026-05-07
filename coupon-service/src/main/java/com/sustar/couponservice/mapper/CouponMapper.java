package com.sustar.couponservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sustar.couponservice.po.CouponPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券数据访问接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD操作
 */
@Mapper
public interface CouponMapper extends BaseMapper<CouponPO> {

}
