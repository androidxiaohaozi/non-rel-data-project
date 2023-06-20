package com.example.nonreldataproject.utils;

import com.example.nonreldataproject.model.dto.OpcDataDTO;
import com.example.nonreldataproject.properties.OpcProperties;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.IdentityProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
@Component
@Slf4j
public class Opcutil {

    @Autowired
    public OpcProperties opcProperties;

    public OpcUaClient opcUaClient;
    public SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public OpcUaClient getOpcUaClient() {
        if (opcUaClient == null) {
            System.out.println("初始化OPC UA Client......");
            try {
                IdentityProvider identityProvider;
                if (StringUtils.isEmpty(opcProperties.getUsername()) && !StringUtils.isEmpty(opcProperties.getPassword())) {
                    identityProvider = new UsernameProvider(opcProperties.getUsername(), opcProperties.getPassword());
                } else {
                    identityProvider = new AnonymousProvider();
                }
                opcUaClient = OpcUaClient.create(
                        opcProperties.getEndPointUrl(),
                        endpoints -> endpoints.stream().findFirst(),
                        configBuilder -> configBuilder
                                .setIdentityProvider(identityProvider)
                                .setRequestTimeout(uint(opcProperties.getRequestTimeout()))
                                .build()
                );
                System.out.println("初始化OPC UA Client......成功");
            } catch (Exception e) {
                System.out.println("初始化OPC UA Client失败");
                return null;
            }
        }
        if (!opcUaClient.getSession().isDone()) {
            try {
                // synchronous connect
                opcUaClient.connect().get();
                System.out.println("OPC UA Client连接connect成功");
            } catch (Exception e) {
                System.out.println("OPC UA Client连接connect失败, {}");
                opcUaClient.disconnect();
                opcUaClient = null;
                return null;
            }
        }
        return opcUaClient;
    }

    public OpcDataDTO read(Integer namaspaceIndex, String tagName) throws ExecutionException, InterruptedException {
        OpcUaClient opcUaClient = getOpcUaClient();
        if (opcUaClient != null){
            NodeId nodeId = new NodeId(namaspaceIndex, tagName);
            Date date = new Date();
            CompletableFuture<DataValue> future = opcUaClient.readValue(0.0, TimestampsToReturn.Both, nodeId);
            DataValue dataValue = future.get();
            if (dataValue.getValue() == null || dataValue.getStatusCode() != StatusCode.GOOD) {
                System.out.println("OPC UA字段读取失败");
                return null;
            }
            OpcDataDTO opcDataDTO = new OpcDataDTO();
            opcDataDTO.setReadTime(date);
            opcDataDTO.setSourceTime(dataValue.getSourceTime() == null ? null : dataValue.getSourceTime().getJavaDate());
            opcDataDTO.setServerTime(dataValue.getServerTime() == null ? null : dataValue.getServerTime().getJavaDate());
            opcDataDTO.setValue(dataValue.getValue().getValue());
            opcDataDTO.setTagName(tagName);
            return opcDataDTO;
        }
        return null;
    }

    public List<OpcDataDTO> reads(List<NodeId> nodeIds) throws ExecutionException, InterruptedException {
        OpcUaClient opcUaClient = getOpcUaClient();
        if (opcUaClient != null){
            Date date = new Date();
            CompletableFuture<List<DataValue>> future = opcUaClient.readValues(0.0, TimestampsToReturn.Both, nodeIds);
            List<DataValue> dataValues = future.get();
            List<OpcDataDTO> list = new ArrayList<>();
            for (int i = 0; i < dataValues.size(); i++) {
                DataValue dataValue = dataValues.get(i);
                boolean isError = dataValue.getValue() == null || dataValue.getStatusCode() != StatusCode.GOOD;
                if (isError) {
                    System.out.println("OPC UA字段批量读取失败");

                    OpcDataDTO opcDataDTO = new OpcDataDTO();
                    opcDataDTO.setReadTime(date);
                    opcDataDTO.setTagName(nodeIds.get(i).getIdentifier().toString());
                    list.add(opcDataDTO);
                }else {
                    OpcDataDTO opcDataDTO = new OpcDataDTO();
                    opcDataDTO.setReadTime(date);
                    opcDataDTO.setSourceTime(dataValue.getSourceTime() == null ? null : dataValue.getSourceTime().getJavaDate());
                    opcDataDTO.setServerTime(dataValue.getServerTime() == null ? null : dataValue.getServerTime().getJavaDate());
                    opcDataDTO.setValue(dataValue.getValue().getValue());
                    opcDataDTO.setTagName(nodeIds.get(i).getIdentifier().toString());
                    list.add(opcDataDTO);
                }
            }
            return list;
        }
        return null;
    }
}
