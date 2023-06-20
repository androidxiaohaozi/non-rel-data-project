package com.example.nonreldataproject.model.po;

import lombok.Data;

import java.util.Date;

/**
 * @Author wh
 * @Date 2023/6/20 11:01
 * @Describe
 */
@Data
public class TMicrostationPo {
    /**
     * 主键ID
     */
    private Integer fid;

    /**
     * 站点编号
     */
    private String fdeviceNo;

    /**
     * CO浓度值，单位 mg/m3
     */

    private Double fco;

    /**
     * NO2 浓度值，单位μg/m3
     */

    private Double no2;

    /**
     * SO2 浓度值，单位μg/m3
     */

    private Double so2;

    /**
     * O3 浓度值，单位μg/m3
     */

    private Double o3;

    /**
     * VOC 浓 度 值 ， 单 位 PPB
     */

    private Double voc;

    /**
     * PM2.5浓度值，单位μg/m3
     */

    private Double fpm25;

    /**
     * PM10浓度值，单位μg/m3
     */

    private Double fpm10;

    /**
     * 温度
     */

    private Double ftempurature;

    /**
     * 湿度
     */

    private Double fhumidity;

    /**
     * 风速
     */

    private Double fwindSpeed;

    /**
     * 风向
     */

    private Double fwindDirection;

    /**
     * 气压
     */

    private Double apress;

    /**
     *  PointHour 表示微型站数据
     */

    private String type;

    /**
     * NO 浓度值，单位μg/m3
     */

    private Double no;

    /**
     * TSP
     */

    private Double tsp;

    /**
     * 数据状态
     */

    private String equstatename;

    /**
     * 数据状态
     */

    private int equstate;

    /**
     * 时间
     */

    private Date createDate;

    /**
     * 返回数据最大ID
     */
    private Long maxId;

    /**
     * 接收时间
     */
    private String receiveTime;

}
