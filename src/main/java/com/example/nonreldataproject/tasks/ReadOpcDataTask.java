package com.example.nonreldataproject.tasks;

import com.example.nonreldataproject.model.dto.OpcDataDTO;
import com.example.nonreldataproject.utils.Opcutil;
import org.apache.commons.io.IOUtils;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @Author wh
 * @Date 2023/6/20 14:57
 * @Describe
 */
@Component
public class ReadOpcDataTask {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public Opcutil opcutil;

    @Autowired
    private RestHighLevelClient client;

    /**
     * 读取五分钟的数据
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void fiveMinuteDataRead() {

        ClassPathResource classPathResource = new ClassPathResource("five-minute-data.txt");
        List<NodeId> nodeIds = new ArrayList<>();
        try (InputStream inputStream = classPathResource.getInputStream()){

            List<String> lines = IOUtils.readLines(inputStream, "utf-8");
            for (String pointStr : lines){
                String[] split = pointStr.split("@@");
                if (split.length < 2) {
                    continue;
                }

                String s = split[0];
                String[] split1 = s.split(",");
                String tagNameSplit = split1[0];
                String namaspaceIndexSplit = split1[1];

                String[] tagNameArr = tagNameSplit.split("=");
                String[] namaspaceIndexArr = namaspaceIndexSplit.split("=");

                NodeId nodeId = new NodeId(Integer.parseInt(namaspaceIndexArr[0]),tagNameArr[0]);
                nodeIds.add(nodeId);
            }
            try {
                List<OpcDataDTO> reads = opcutil.reads(nodeIds);
                Map<String, Object> data = new HashMap<>();
                BulkRequest bulkRequest = new BulkRequest();
                if (reads.size() > 0) {
                    for (OpcDataDTO read : reads) {
                        Date readTime = read.getReadTime();
                        Date serverTime = read.getServerTime();
                        String tagName = read.getTagName();
                        Object value = read.getValue();
                        Date sourceTime = read.getSourceTime();

                        data.put("tagName",tagName);
                        data.put("value",value);
                        data.put("readTime",readTime);
                        data.put("serverTime",serverTime);
                        data.put("sourceTime",sourceTime);
                        IndexRequest indexRequest = new IndexRequest("fiveminutedataindex").source(data);
                        bulkRequest.add(indexRequest);
                    }

                    BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
                }
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }catch (IOException e) {
            logger.error("找不到sql配置文件[classpath:batchWhere.txt]",e);
        }
    }
}
