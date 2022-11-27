package boylen.agent.mybatis.agent.test.dao;

import boylen.agent.mybatis.agent.test.entity.UserStatus;
import org.springframework.stereotype.Repository;


@Repository
//@SourceAgent(database = Database.VIP)
public interface UserMapper {

    UserStatus getUserStatus(String userId);

    UserStatus getUserStatusByUserIdAndOrderId(String userId, String orderId);

    UserStatus getUserStatusByFullAttribute(Long id, String userId, String orderId, Long endTime, Long svipEndTime);
}
