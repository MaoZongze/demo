package com.mzz.demo.es;

import com.google.gson.Gson;
import com.mzz.demo.SimpleDocument;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mzz.demo.constants.CommonConstants.INDEX;

@Component
public class MyPipeline implements Pipeline {

    private static final Logger logger = LoggerFactory.getLogger(MyPipeline.class);

    private static final AtomicInteger autoIncr = new AtomicInteger(1);

    private static Gson gson = new Gson();

    @Autowired
    private RestHighLevelClient client;

    @Override
    public void process(ResultItems resultItems, Task task) {
        String title = resultItems.get("title");
        String doc = resultItems.get("doc");
        if (StringUtils.isNotBlank(title) && StringUtils.isNotBlank(doc)) {
            logger.info("======================================");
            logger.info(title);
            logger.info(doc);
            LocalDateTime now = LocalDateTime.now();
            IndexRequest indexRequest = new IndexRequest(INDEX);
            indexRequest.id(String.valueOf(autoIncr.incrementAndGet()));
            indexRequest.source(gson.toJson(new SimpleDocument(title, doc, now, now)), XContentType.JSON);
            IndexResponse response = null;
            try {
                response = client.index(indexRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                logger.error("MyPipeline process error, title: {}", title, e);
            }
            logger.info("response: {}", response.status());
        }
    }
}
