package com.example.nonreldataproject.controller;

import com.example.nonreldataproject.model.dto.OpcDataDTO;
import com.example.nonreldataproject.utils.Opcutil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    public Opcutil opcutil;

    @Autowired
    private RestHighLevelClient client;

    /**
     * read
     * @return o
     */
    @GetMapping("/read")
    public OpcDataDTO read(Integer namaspaceIndex, String tagName) {
        try {
            return opcutil.read(namaspaceIndex,tagName);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 测试批量修改
     * @param entitys e
     * @throws IOException i
     */
    @PostMapping("/bulkUpdate")
    public void bulkUpdate(@RequestBody List<Map<String,Object>> entitys) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();

        for (Map<String, Object> entity : entitys) {

            Object id = entity.get("id");
            Object index = entity.get("index");

            UpdateRequest updateRequest = new UpdateRequest();
            updateRequest.index(index.toString());
            updateRequest.id(id.toString());
            entity.remove("index");
            updateRequest.doc(entity);
            bulkRequest.add(updateRequest);
        }

        BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println("bulk write result is " + !response.hasFailures());
    }

    /**
     * 测试查询
     * @param index i
     * @param type t
     * @param startTime s
     * @param endTime e
     * @param tagName t
     */
    @RequestMapping("/testSearch")
    public void testSearch(String index,String type,String startTime,String endTime,String tagName) {


        long start=System.currentTimeMillis();
        try {
            SearchRequest searchRequest = new SearchRequest(index);
            SearchSourceBuilder searchSourceBuilder;

            if ("1".equals(type)) {
                searchSourceBuilder = new SearchSourceBuilder().sort("dataTime", SortOrder.ASC);
            } else {
                searchSourceBuilder = new SearchSourceBuilder().sort("dataTime", SortOrder.DESC);
            }

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            boolQueryBuilder.must(QueryBuilders.matchQuery("MN", tagName));
            boolQueryBuilder.must(QueryBuilders.rangeQuery("dataTime").gte(startTime).lte(endTime));

            searchSourceBuilder.query(boolQueryBuilder);

            searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

            searchRequest.source(searchSourceBuilder);

            //不分页查询
            searchSourceBuilder.size(1000);
            Scroll scroll = new Scroll(TimeValue.timeValueMillis(5L));
            searchRequest.scroll(scroll);

            SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);

            String scrollId = search.getScrollId();
            SearchHit[] hits = search.getHits().getHits();
            List<SearchHit> resultSearchHit = new ArrayList<>();
            while (hits != null && hits.length > 0) {
                resultSearchHit.addAll(Arrays.asList(hits));
                SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
                searchScrollRequest.scroll(scroll);
                SearchResponse searchScrollResponse = client.scroll(searchScrollRequest, RequestOptions.DEFAULT);
                scrollId = searchScrollResponse.getScrollId();
                hits = searchScrollResponse.getHits().getHits();
            }
            ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
            clearScrollRequest.addScrollId(scrollId);
            client.clearScroll(clearScrollRequest,RequestOptions.DEFAULT);

            if (resultSearchHit.size() > 0) {
                for (SearchHit hit : resultSearchHit) {
                    String id = hit.getId();
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    if (sourceAsMap == null) {
                        sourceAsMap = new HashMap<>();
                    }
                    sourceAsMap.put("id",id);
                    System.out.println(sourceAsMap);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        long endExport=System.currentTimeMillis();
        System.out.println("******** 处理时间:" + "  " + (endExport-start));
    }
}
