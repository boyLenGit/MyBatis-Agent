package boylen.agent.mybatis.agent.test.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;


@Data
public class GoodsEntity implements Serializable {
    /**
     * 自增id
     */
    private Integer id;

    /**
     * 直播场次id
     */
    private Long liveSessionId;

    /**
     * 商品id,来自橱窗服务
     */
    private String goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品图片
     */
    private String goodsPicture;

    /**
     * 商品链接
     */
    private String goodsUrl;

    /**
     * 是否删除。0：正常；1：删除
     */
    private Integer isDelete;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 2L;
}