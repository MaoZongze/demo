package com.mzz.demo.controller;

import com.google.gson.Gson;
import com.mzz.demo.SimpleDocument;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.mzz.demo.constants.CommonConstants.INDEX;

@RestController
public class DocController {

    private static final Logger logger = LoggerFactory.getLogger(DocController.class);

    private static Gson gson = new Gson();

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @GetMapping("/query/{keyword}")
    public void search(@PathVariable("keyword") String keyword) throws IOException {
        logger.info("search keywords: {}", keyword);
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchPhraseQuery("title", keyword));
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        List<SearchHit> searchHits = Arrays.asList(search.getHits().getHits());
        logger.info("hit size: {}", searchHits.size());
        logger.info("hit {}", searchHits.stream().map(s -> gson.fromJson(s.getSourceAsString(), SimpleDocument.class)).collect(Collectors.toList()));

    }
}
