package com.mzz.demo;

import com.google.gson.Gson;
import com.mzz.demo.pojo.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
class DemoApplicationTests {

    private Gson gson = new Gson();

    @Test
    void contextLoads() {
    }

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    //测试创建索引
    @Test
    public void testCreateIndex() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("zongze.mao");
        createIndexRequest.mapping("{\n" +
                "      \"properties\" : {\n" +
                "        \"age\" : {\n" +
                "          \"type\" : \"long\"\n" +
                "        },\n" +
                "        \"name\" : {\n" +
                "          \"type\" : \"text\",\n" +
                "          \"fields\" : {\n" +
                "            \"keyword\" : {\n" +
                "              \"type\" : \"keyword\",\n" +
                "              \"ignore_above\" : 256\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"desc\" : {\n" +
                "          \"type\" : \"text\"\n" +
                "        }\n" +
                "      }\n" +
                "    }", XContentType.JSON);
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);
    }

    @Test
    public void testExistIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("zongze.mao");
        boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    @Test
    public void testDeleteIndex() throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("zongze.mao");
        AcknowledgedResponse delete = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        System.out.println(new Gson().toJson(delete));
    }

    @Test
    public void testCreateDocument() throws IOException {
        User user1 = new User("张三 ", 25);
        IndexRequest indexRequest = new IndexRequest("zongze.mao");
        //indexRequest.id("1");
        indexRequest.timeout(TimeValue.timeValueSeconds(1));
        indexRequest.source(gson.toJson(user1), XContentType.JSON);
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(indexResponse.getResult());

    }

    @Test
    public void testGetDocument() throws IOException {
        GetRequest getRequest = new GetRequest("zongze.mao", "IkolGnMB-SobACxpVtD9");
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(getResponse.getSourceAsMap());
        System.out.println(getResponse);
    }

    @Test
    public void testBulk() throws IOException {
        BulkRequest bulkRequest = new BulkRequest("zongze.mao");
        Stream.iterate(1, n -> ++n).limit(100).map(n -> new User("zhangsan" + n, n, "刘青莲 是小 机灵鬼"+n)).forEach(
                user -> {
                    IndexRequest indexRequest = new IndexRequest("zongze.mao");
                    indexRequest.source(gson.toJson(user), XContentType.JSON);
                    bulkRequest.add(indexRequest);
                }
        );
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulkResponse.hasFailures());
    }

    @Test
    public void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("zongze.mao");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.boolQuery().must(QueryBuilders.matchPhraseQuery("desc", "我描述")));
//        builder.query(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("desc", "描述")));
        builder.from(0).size(100);
        searchRequest.source(builder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(searchResponse.getHits());
        System.out.println(searchResponse.getHits().getHits().length);
        Arrays.stream(searchResponse.getHits().getHits()).forEach(System.out::println);
    }

}
