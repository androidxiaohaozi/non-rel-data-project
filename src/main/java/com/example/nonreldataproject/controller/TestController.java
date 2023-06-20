package com.example.nonreldataproject.controller;

import com.example.nonreldataproject.model.dto.OpcDataDTO;
import com.example.nonreldataproject.utils.Opcutil;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    @RequestMapping("/testSearch")
    public void testSearch(String type) {

        try {
            SearchRequest searchRequest = new SearchRequest("tspreceivedataindex");
            SearchSourceBuilder searchSourceBuilder;

            if ("1".equals(type)) {
                searchSourceBuilder = new SearchSourceBuilder().sort("dataTime", SortOrder.ASC);
            } else {
                searchSourceBuilder = new SearchSourceBuilder().sort("dataTime", SortOrder.DESC);
            }

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            boolQueryBuilder.must(QueryBuilders.matchQuery("MN", "JZTSP000000033"));
            boolQueryBuilder.must(QueryBuilders.rangeQuery("dataTime").gte("2023-06-05 11:56:28").lte("2023-06-05 16:01:28"));

            searchSourceBuilder.query(boolQueryBuilder);

            searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

            searchRequest.source(searchSourceBuilder);

            //不分页查询
            searchSourceBuilder.size(2);
            Scroll scroll = new Scroll(TimeValue.timeValueMillis(1L));
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
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    System.out.println(sourceAsMap);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
