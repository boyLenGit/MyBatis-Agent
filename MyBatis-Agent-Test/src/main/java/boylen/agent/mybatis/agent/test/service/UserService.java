package boylen.agent.mybatis.agent.test.service;

import boylen.agent.mybatis.agent.test.dao.UserMapper;
import boylen.agent.mybatis.agent.test.entity.UserStatus;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public JSONObject getUserById(String userId){
        UserStatus userStatus = userMapper.getUserStatus(userId);
        JSONObject res = new JSONObject();
        res.put("list", userStatus);
        return res;
    }

    public JSONObject getUserByIdAndOrderId(String userId, String orderId){
        UserStatus userStatus = userMapper.getUserStatusByUserIdAndOrderId(userId, orderId);
        JSONObject res = new JSONObject();
        res.put("list", userStatus);
        return res;
    }

    public JSONObject getUserStatusByFullAttribute(Long id, String userId, String orderId, Long endTime, Long svipEndTime){
        UserStatus userStatus = userMapper.getUserStatusByFullAttribute(id, userId, orderId, endTime, svipEndTime);
        JSONObject res = new JSONObject();
        res.put("list", userStatus);
        return res;
    }
}
