package boylen.agent.mybatis.agent.test.service;

import boylen.agent.mybatis.agent.test.dao.GoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;

    public Integer getGoodsNum(){
        Integer integer = goodsMapper.countGoodsNum();
        return integer;
    }
}
