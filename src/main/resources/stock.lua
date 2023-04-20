if(redis.call('exists',KEYS[1])==1) then
    local stock =tonumber(redis.call('get',KEYS[1]));
    if(stock>0) then
        redis.call('incrby',KEYS[1],-1);
        return stock;
    end;
        return -1;
end;
//查询库存和扣减库存需要原子操作，此时可以借助lua脚本)下次下单再获取库存的时候，直接从redis里面查就可以了。