package com.example.seckilldemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckilldemo.entity.TSeckillOrder;
import com.example.seckilldemo.entity.TUser;
import com.example.seckilldemo.mapper.TSeckillOrderMapper;
import com.example.seckilldemo.service.ITSeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 秒杀订单表 服务实现类
 *
 * @author LiChao
 * @since 2022-03-03
 */
@Service
@Primary
public class TSeckillOrderServiceImpl extends ServiceImpl<TSeckillOrderMapper, TSeckillOrder> implements ITSeckillOrderService {

    @Autowired
    private TSeckillOrderMapper tSeckillOrderMapper;
    @Resource
    private RedisTemplate redisTemplate;

    @Override//@return orderId 成功 ；-1 秒杀失败 ；0 排队中
    public Long getResult(TUser tUser, Long goodsId) {

        TSeckillOrder tSeckillOrder = tSeckillOrderMapper.selectOne(new QueryWrapper<TSeckillOrder>().eq("user_id", tUser.getId()).eq("goods_id", goodsId));
        if (null != tSeckillOrder) {
            return tSeckillOrder.getOrderId();//成功
        } else if (redisTemplate.hasKey("isStockEmpty:" + goodsId)) {//跟
            return -1L;//失败
        } else {
            return 0L;//排队中
        }
      //判断是否还有库存，如果没库存，那就set成功了isStockEmpty这个值，等到com.example.seckilldemo.service.impl.TOrderServiceImpl轮询结果的时候，
    	// else if (redisTemplate.hasKey("isStockEmpty:" + goodsId))检测到是true,那就显示没库存了，  return -1L;//秒杀失败
    }
}
