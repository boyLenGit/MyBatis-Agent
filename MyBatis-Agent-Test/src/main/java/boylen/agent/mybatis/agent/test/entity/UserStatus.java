/**
 * @(#)UserStatus.java, Mar 24, 2017. 
 * 
 * Copyright 2017 Yodao, Inc. All rights reserved.
 * YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package boylen.agent.mybatis.agent.test.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatus {

    private int id;
    
    //登录用户userid
    private String userId;
    
    //最后一次支付订单id
    private String orderId;
    
    //VIP有效期结束时间
    private long endTime;

    /**
     * 超级会员有效期结束时间
     */
    private long svipEndTime;

    public UserStatus(String userId, String orderId, long endTime){
        this.userId = userId;
        this.orderId = orderId;
        this.endTime = endTime;
    }

}
