package boylen.agent.mybatis.agent.test.web;

import boylen.agent.mybatis.agent.test.service.GoodsService;
import boylen.agent.mybatis.agent.test.service.UserService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@ResponseBody
@RequestMapping("/test")
public class TestController {
    @Autowired
    private UserService userService;

    @Autowired
    private GoodsService goodsService;

    @GetMapping("/getUserById")
    public JSONObject getUserById(@RequestParam String userId){
        return userService.getUserById(userId);
    }

    @GetMapping("/getUserByIdAndOrderId")
    public JSONObject getUserByIdAndOrderId(@RequestParam String userId, @RequestParam String orderId){
        return userService.getUserByIdAndOrderId(userId, orderId);
    }

    @GetMapping("/getUserStatusByFullAttribute")
    public JSONObject getUserStatusByFullAttribute(@RequestParam Long id, @RequestParam String userId, @RequestParam String orderId, @RequestParam Long endTime, @RequestParam Long svipEndTime){
        return userService.getUserStatusByFullAttribute(id, userId, orderId, endTime, svipEndTime);
    }

    @GetMapping("/getGoodsNum")
    public JSONObject getGoodsNum(){
        return new JSONObject().fluentPut("num", goodsService.getGoodsNum());
    }
}
