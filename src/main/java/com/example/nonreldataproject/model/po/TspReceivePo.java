package com.example.nonreldataproject.model.po;

import lombok.Data;

/**
 * @Author wh
 * @Date 2023/6/20 11:01
 * @Describe
 */
@Data
public class TspReceivePo {

    private String dataTime;

    /**
     * 总悬浮颗粒物 TSP
     */
    private String a34001;

    /**
     * 总悬浮颗粒物 flag
     */
    private String a34001Flag;

    /**
     * 可吸入颗粒物 PM10
     */
    private String a34002;

    /**
     * 可吸入颗粒物 PM10 flag
     */
    private String a34002Flag;

    /**
     * 细微颗粒物 PM2.5
     */
    private String a34004;

    /**
     * 细微颗粒物 PM2.5 flag
     */
    private String a34004Flag;

    /**
     * cn
     */
    private String cn;

    /**
     * MN码
     */
    private String MN;

    /**
     * 监测点位
     */
    private String monitorPoints;

    /**
     * 监测类型
     */
    private String monitorType;
}
