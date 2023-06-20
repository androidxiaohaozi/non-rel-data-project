package com.example.nonreldataproject.netty;

import com.example.nonreldataproject.model.po.TMicrostationPo;
import com.example.nonreldataproject.model.po.TspReceivePo;
import com.example.nonreldataproject.enums.AllMicroStationEnums;
import com.example.nonreldataproject.enums.AllTspPointPositionEnums;
import com.example.nonreldataproject.utils.RedisUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author wh
 * @Date 2023/6/20 10:58
 * @Describe
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);

    //如果需要存储到数据库的话则需要把下面的dao打卡，并且把 init方法里面的打开。
/*    @Autowired
    private UnorgSourceDaoImpl unorgSourceDao;*/

    private static final Pattern p = Pattern.compile("\\s*|\t|\r|\n");

    private static ServerHandler serverHandler;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private RestHighLevelClient client;

    @PostConstruct
    public void init() {
        serverHandler = this;
/*        serverHandler.unorgSourceDao = this.unorgSourceDao;*/
    }

    /**
     * channelAction
     * channel 通道 action 活跃的
     * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     *
     * @param ctx 通道
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    /**
     * channelInactive
     * channel 通道 Inactive 不活跃的
     * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
     *
     * @param ctx 通道
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        LOGGER.warn("--------Netty Disconnect Client IP is :" + ctx.channel().id().asShortText() + " "
                + ctx.channel().remoteAddress() + "--------");
        ctx.close();
    }

    /**
     * 功能：读取服务器发送过来的信息
     *
     * @param ctx 通道
     * @param msg 消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, StandardCharsets.UTF_8);
        buf.release();
        LOGGER.info("[IP]" + ctx.channel().remoteAddress() + ",[请求:][" + body + "]");
        handleMessage(ctx, body);
    }

    /**
     * 解析数据入库操作
     * @param ctx ctx
     * @param body bocy
     */
    private void handleMessage(ChannelHandlerContext ctx, String body) {
        insertTspReceiveDate(body);

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    /**
     * 连接异常错误
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("--------Netty Exception ExceptionCaught :" + ctx.channel().id().asShortText() + " "
                + cause.getMessage() + "=======================", cause);
        ctx.close();
    }


    private String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public void insertTspReceiveDate(String msg) {

        if (msg != null && !"".equals( msg)) {

            msg = replaceBlank(msg);

            String cn = "";
            String[] split2 = msg.split("&&");

            TspReceivePo tspReceivePo = new TspReceivePo();

            TMicrostationPo tMicrostationPo = new TMicrostationPo();

            SimpleDateFormat simpleDates = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (String s : split2) {

                String[] split = s.split(";");

                for (String s1 : split) {

                    if (s1.contains(",")) {
                        String[] split1 = s1.split(",");
                        for (String s2 : split1) {
                            String[] split3 = s2.split("=");

                            if (split3.length > 1) {
                                String s3 = split3[0];
                                String s4 = split3[1];
                                if ("DataTime".equals(s3)) {
                                    SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmmss");
                                    try {
                                        Date parse = simpleDate.parse(s4);

                                        String format = simpleDates.format(parse);
                                        tspReceivePo.setDataTime(format);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if ("a34001-Rtd".equals(s3)) {
                                    double v = Double.parseDouble(s4);
                                    tspReceivePo.setA34001(v + "");
                                }

                                if ("a34002-Rtd".equals(s3)) {
                                    double v = Double.parseDouble(s4);
                                    tspReceivePo.setA34002(v +"");
                                    tMicrostationPo.setFpm10(Double.parseDouble(s4));
                                }

                                if ("a34004-Rtd".equals(s3)) {
                                    double v = Double.parseDouble(s4);
                                    tspReceivePo.setA34004(v +"");
                                    tMicrostationPo.setFpm25(Double.parseDouble(s4));
                                }

                                if ("a01002-Rtd".equals(s3)) {
                                    tMicrostationPo.setFhumidity(Double.parseDouble(s4));
                                }

                                if ("a01001-Rtd".equals(s3)) {
                                    tMicrostationPo.setFtempurature(Double.parseDouble(s4));
                                }

                                if ("a01006-Rtd".equals(s3)) {
                                    // 20220812 调整气压  * 10 调整
                                    double v = Double.parseDouble(s4);
                                    v = v * 10;
                                    tMicrostationPo.setApress(v);
                                }

                                if ("MN".equals(s3)) {
                                    AllTspPointPositionEnums byCode = AllTspPointPositionEnums.getByCode(s4);
                                    if (byCode != null) {
                                        tspReceivePo.setMN(s4);
                                        tspReceivePo.setMonitorType(byCode.getMonitryType());
                                        tspReceivePo.setMonitorPoints(byCode.getMonitorPoints());
                                    }
                                    AllMicroStationEnums byCode1 = AllMicroStationEnums.getByCode(s4);

                                    if (byCode1 != null) {
                                        tMicrostationPo.setFdeviceNo(s4);
                                    }
                                }
                                if ("CN".equals(s3)) {
                                    cn = s4;
                                }
                            }
                        }
                    } else {
                        String[] split1 = s1.split("=");

                        if (split1.length > 1) {
                            String s2 = split1[0];
                            String s3 = split1[1];
                            if ("DataTime".equals(s2)) {
                                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmmss");
                                try {
                                    Date parse = simpleDate.parse(s3);
                                    String format = simpleDates.format(parse);
                                    tspReceivePo.setDataTime(format);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            if ("a34001-Rtd".equals(s2)) {
                                double v = Double.parseDouble(s3);
                                tspReceivePo.setA34001(v + "");
                            }
                            if ("a34002-Rtd".equals(s2)) {
                                double v = Double.parseDouble(s3);
                                tspReceivePo.setA34002(v +"");
                                tMicrostationPo.setFpm10(Double.parseDouble(s3));
                            }
                            if ("a34004-Rtd".equals(s2)) {
                                double v = Double.parseDouble(s3);
                                tspReceivePo.setA34004(v +"");
                                tMicrostationPo.setFpm25(Double.parseDouble(s3));
                            }
                            if ("a01002-Rtd".equals(s2)) {
                                tMicrostationPo.setFhumidity(Double.parseDouble(s3));
                            }

                            if ("a01001-Rtd".equals(s2)) {
                                tMicrostationPo.setFtempurature(Double.parseDouble(s3));
                            }

                            if ("a01006-Rtd".equals(s2)) {
                                // 20220812 调整气压  * 10 调整
                                double v = Double.parseDouble(s3);
                                v = v * 10;
                                tMicrostationPo.setApress(v);
                            }

                            if ("CN".equals(s2)) {
                                cn = s3;
                            }

                            if ("MN".equals(s2)) {
                                AllTspPointPositionEnums byCode = AllTspPointPositionEnums.getByCode(s3);
                                if (byCode != null) {
                                    tspReceivePo.setMN(s3);
                                    tspReceivePo.setMonitorType(byCode.getMonitryType());
                                    tspReceivePo.setMonitorPoints(byCode.getMonitorPoints());
                                }

                                AllMicroStationEnums byCode1 = AllMicroStationEnums.getByCode(s3);
                                if (byCode1 != null) {
                                    tMicrostationPo.setFdeviceNo(s3);
                                }
                            }
                        }
                    }
                }
            }

            if (tspReceivePo.getA34001() != null && tspReceivePo.getA34002() != null && tspReceivePo.getA34004() != null
                    && tspReceivePo.getMN() != null && "2011".equals(cn)) {
                tspIndex(tspReceivePo);
                // 实时数据用 redis 存储
                redisUtil.set(tspReceivePo.getMN(), tspReceivePo.getA34001());
                redisUtil.set(tspReceivePo.getMN() + "-pm25", tspReceivePo.getA34004());
                redisUtil.set(tspReceivePo.getMN() + "-pm10", tspReceivePo.getA34002());
            }

            if (tMicrostationPo.getFpm10() != null && tMicrostationPo.getFpm25() != null && tMicrostationPo.getFhumidity() != null
                    && tMicrostationPo.getApress() != null && tMicrostationPo.getFdeviceNo() != null && "2011".equals(cn)) {

                tMicrostationPo.setType("PointMinute");
                tMicrostationPo.setEqustate(1);
                tMicrostationPo.setEqustatename("正常");
                tMicrostationPo.setCreateDate(new Date());
                tMicrostationPo.setReceiveTime(simpleDates.format(new Date()));

                microIndex(tMicrostationPo);
                // 实时数据用redis存储
                redisUtil.set(tMicrostationPo.getFdeviceNo(), tMicrostationPo.getFpm10());
            }
        }
    }

    /**
     * 微站数据新增
     * @param microstationPo  m
     * @return r
     * @throws IOException i
     */
    public String microIndex(TMicrostationPo microstationPo) {

        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject();
            builder.field("fdeviceNo", microstationPo.getFdeviceNo());
            builder.field("fco", microstationPo.getFco());
            builder.field("no2", microstationPo.getNo2());
            builder.field("so2", microstationPo.getSo2());
            builder.field("o3", microstationPo.getO3());
            builder.field("voc", microstationPo.getVoc());
            builder.field("fpm25", microstationPo.getFpm25());
            builder.field("fpm10", microstationPo.getFpm10());
            builder.field("ftempurature", microstationPo.getFtempurature());
            builder.field("fhumidity", microstationPo.getFhumidity());
            builder.field("fwindSpeed", microstationPo.getFwindSpeed());
            builder.field("fwindDirection", microstationPo.getFwindDirection());
            builder.field("apress", microstationPo.getApress());
            builder.field("type", microstationPo.getType());
            builder.field("no", microstationPo.getNo());
            builder.field("tsp", microstationPo.getTsp());
            builder.field("equstatename", microstationPo.getEqustatename());
            builder.field("equstate", microstationPo.getEqustate());
            builder.field("maxId", microstationPo.getMaxId());
            builder.field("receiveTime", microstationPo.getReceiveTime());
            builder.field("createDate", microstationPo.getCreateDate());
            builder.endObject();

            IndexRequest request = new IndexRequest("microreceivedataindex").source(builder);
            IndexResponse response;

            // 不使用默认的RequestOptions.DEFAULT，而通过使用自定义RequestOptions的方式（ES官方api已经给我们开放出来了）：
            RequestOptions.Builder buildersize = RequestOptions.DEFAULT.toBuilder();
            buildersize.setHttpAsyncResponseConsumerFactory(
                    new HttpAsyncResponseConsumerFactory
                            //修改为500MB
                            .HeapBufferedResponseConsumerFactory(500 * 1024 * 1024));
            //             response = restHighLevelClient.index(request, RequestOptions.DEAFULT);

            response = client.index(request, buildersize.build());

            if (response != null) {
                response.getId();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * tsp 数据新增
     * @param tspReceivePo t
     * @return r
     */
    public String tspIndex(TspReceivePo tspReceivePo) {

        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject();
            builder.field("dataTime", tspReceivePo.getDataTime());
            builder.field("a34001", tspReceivePo.getA34001());
            builder.field("a34001Flag", tspReceivePo.getA34001Flag());
            builder.field("a34002", tspReceivePo.getA34002());
            builder.field("a34002Flag", tspReceivePo.getA34002Flag());
            builder.field("a34004", tspReceivePo.getA34004());
            builder.field("a34004Flag", tspReceivePo.getA34004Flag());
            builder.field("cn", tspReceivePo.getCn());
            builder.field("MN", tspReceivePo.getMN());
            builder.field("monitorPoints", tspReceivePo.getMonitorPoints());
            builder.field("monitorType", tspReceivePo.getMonitorType());
            builder.endObject();

            IndexRequest request = new IndexRequest("tspreceivedataindex").source(builder);
            IndexResponse response;

            // 不使用默认的RequestOptions.DEFAULT，而通过使用自定义RequestOptions的方式（ES官方api已经给我们开放出来了）：
            RequestOptions.Builder buildersize = RequestOptions.DEFAULT.toBuilder();
            buildersize.setHttpAsyncResponseConsumerFactory(
                    new HttpAsyncResponseConsumerFactory
                            //修改为500MB
                            .HeapBufferedResponseConsumerFactory(500 * 1024 * 1024));
            //             response = restHighLevelClient.index(request, RequestOptions.DEAFULT);

            response = client.index(request, buildersize.build());

            if (response != null) {
                response.getId();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
