package com.example.nonreldataproject.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author wh
 * @Date 2023/6/20 11:06
 * @Describe
 */
public enum AllTspPointPositionEnums{

    /**
     * 废钢库7#门卸料点
     */
    ScrapWar7DoorUnlPoint("JZTSP000000001","物料存储","废钢库7#门卸料点"),
    ScrapWar8DoorUnlPoint("JZTSP000000002","物料存储","废钢库8#门卸料点"),
    ScrapWar9DoorUnlPoint("JZTSP000000003","物料存储","废钢库9#门卸料点"),
    ScrapWar10DoorUnlPoint("JZTSP000000004","物料存储","废钢库10#门卸料点"),
    ScrapWar11DoorUnlPoint("JZTSP000000005","物料存储","废钢库11#门卸料点"),

    SteelSlag1SlagStuRoom("JZTSP000000006","物料存储","钢渣1#闷渣间"),
    SteelSlag2SlagStuRoom("JZTSP000000007","物料存储","钢渣2#闷渣间"),
    SteelSlag3SlagStuRoom("JZTSP000000008","物料存储","钢渣3#闷渣间"),

    ElectricFur1PlatForm("JZTSP000000009","生产工艺","1#电炉平台"),
    ElectricFur2PlatForm("JZTSP000000010","生产工艺","2#电炉平台"),
    ElectricFur1LfPlatForm("JZTSP000000011","生产工艺","1#LF炉平台"),
    ElectricFur2LfPlatForm("JZTSP000000012","生产工艺","2#LF炉平台"),
    CasterPlatForm("JZTSP000000013","生产工艺","连铸机平台"),
    LadleHotRepArea("JZTSP000000014","生产工艺","钢包热修区域"),
    LadleColdRepArea("JZTSP000000015","生产工艺","钢包冷修区域"),
    MiddleSecSteelWork("JZTSP000000016","生产工艺","型钢车间中部"),
    SectionSteelStrArea("JZTSP000000017","生产工艺","型钢矫直机区域"),
    SectionSteel12BetSaw("JZTSP000000018","生产工艺","型钢1#2#锯齿间"),
    MiddleWireRodWorkshop("JZTSP000000019","生产工艺","线材车间中部"),

    WeiBucPlatEleFur1Silo("JZTSP000000020","物料输送","1#电炉料仓称量斗平台"),
    EleFur1LarIncBeltTail("JZTSP000000021","物料输送","1#电炉大倾角皮带机尾"),
    EleFur1LarIncBeltHead("JZTSP000000022","物料输送","1#电炉大倾角皮带机头"),
    CarPowLoaAreaEleFur1("JZTSP000000023","物料输送","1#电炉碳粉上料区域"),
    EleFur1ScrFeeArea("JZTSP000000024","物料输送","1#电炉废钢给料机区域"),
    WeiBucPlatEleFur2Silo("JZTSP000000025","物料输送","2#电炉料仓称量斗平台"),
    EleFur2LarIncBeltTail("JZTSP000000026","物料输送","2#电炉大倾角皮带机尾"),
    EleFur2LarIncBeltHead("JZTSP000000027","物料输送","2#电炉大倾角皮带机头"),
    CarPowLoaAreaEleFur2("JZTSP000000028","物料输送","2#电炉碳粉上料区域"),
    EleFur2ScrFeeArea("JZTSP000000029","物料输送","2#电炉废钢给料机区域"),
    WeiBucPlaChaSiloFur1Lf("JZTSP000000030","物料输送","1#LF炉料仓称量斗平台"),
    FeedBeltFur1Lf("JZTSP000000031","物料输送","1#LF炉上料皮带"),
    WeiBucPlaChaSiloFur2Lf("JZTSP000000032","物料输送","2#LF炉料仓称量斗平台"),
    FeedBeltFur2Lf("JZTSP000000033","物料输送","2#LF炉上料皮带"),
    ColdBriArea("JZTSP000000034","物料输送","冷压造球区域");

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

    AllTspPointPositionEnums(String mn, String monitryType, String monitorPoints) {
        this.mn = mn;
        this.monitryType = monitryType;
        this.monitorPoints = monitorPoints;
    }

    private static final Map<String, AllTspPointPositionEnums> codeToEnum = new HashMap<>();

    static {
        for(AllTspPointPositionEnums myenum : values()) {
            codeToEnum.put(myenum.getMn(), myenum);
        }
    }

    public static AllTspPointPositionEnums getByCode(String mn) {
        return codeToEnum.get(mn);
    }
}