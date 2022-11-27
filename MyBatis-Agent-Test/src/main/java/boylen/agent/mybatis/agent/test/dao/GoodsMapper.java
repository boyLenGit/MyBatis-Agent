package boylen.agent.mybatis.agent.test.dao;

import org.springframework.stereotype.Repository;


@Repository
public interface GoodsMapper {
//    @SourceAgent(database = Database.LIVE_SERVER)
    Integer countGoodsNum();
}
