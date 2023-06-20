package com.example.nonreldataproject.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author wh
 * @Date 2023/6/20 11:07
 * @Describe
 */
public enum AllMicroStationEnums {

    /**
     * 废钢库7#门卸料点
     */
    NorthPlantBoundary("JZTSP000000035","厂界监测","东北厂界"),
    EastBoundary("JZTSP000000036","厂界监测","东厂界"),
    SouthPlantBoundary("JZTSP000000037","厂界监测","东南厂界"),
    SouthBoundary("JZTSP000000038","厂界监测","南厂界"),
    MiddleSecJinZhouRoad("JZTSP000000039","道路监测","金州路中段"),
    SteelPlantSouth("JZTSP000000040","重点区域","炼钢厂南");

    private String mn;

    private String monitryType;

    private String monitorPoints;

    public String getMn() {
        return mn;
    }

    public void setMn(String mn) {
        this.mn = mn;
    }

    public String getMonitryType() {
        return monitryType;
    }

    public void setMonitryType(String monitryType) {
        this.monitryType = monitryType;
    }

    public String getMonitorPoints() {
        return monitorPoints;
    }

    public void setMonitorPoints(String monitorPoints) {
        this.monitorPoints = monitorPoints;
    }

    AllMicroStationEnums(String mn, String monitryType, String monitorPoints) {
        this.mn = mn;
        this.monitryType = monitryType;
        this.monitorPoints = monitorPoints;
    }

    private static final Map<String, AllMicroStationEnums> codeToEnum = new HashMap<>();

    static {
        for(AllMicroStationEnums myenum : values()) {
            codeToEnum.put(myenum.getMn(), myenum);
        }
    }

    public static AllMicroStationEnums getByCode(String mn) {
        return codeToEnum.get(mn);
    }
}
