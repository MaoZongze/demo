package com.mzz.demo.schedule;

import com.mzz.demo.es.MyPageProcessor;
import com.mzz.demo.es.MyPipeline;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;

import java.io.IOException;

import static com.mzz.demo.constants.CommonConstants.INDEX;

@Component
public class TestScheduler {

    private static final Logger logger = LoggerFactory.getLogger(TestScheduler.class);

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private MyPageProcessor myPageProcessor;

    @Autowired
    private MyPipeline myPipeline;

/*    @Scheduled(cron = "0/5 * * * * *")
    public void testScheduled() {
        logger.info("start==================");
    }*/

    /**
     * 首先判断是否存在我们需要的索引
     * 不存在 就创建一个索引 存在则直接数据
     */
    @Scheduled(fixedRate = 10000000L)
    public void flashData() throws IOException {
        logger.info("start flashData==================");
        GetIndexRequest getIndexRequest = new GetIndexRequest(INDEX);
        boolean exists = restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        if (!exists) {
            CreateIndexRequest request = new CreateIndexRequest(INDEX);
            request.settings("{\n" +
                    "      \n" +
                    "    \"analysis\": {\n" +
                    "      \"analyzer\": { \n" +
                    "        \"charSplit\": {\n" +
                    "\t      \"type\": \"custom\",\n" +
                    "              \"tokenizer\": \"ngram_tokenizer\"\n" +
                    "\t    }\n" +
                    "\t  },\n" +
                    "\t \"tokenizer\": {\n" +
                    "           \"ngram_tokenizer\": {\n" +
                    "             \"type\": \"ngram\",\n" +
                    "             \"min_gram\": \"1\",\n" +
                    "             \"max_gram\": \"1\",\n" +
                    "             \"token_chars\": [\n" +
                    "               \"letter\",\n" +
                    "               \"digit\",\n" +
                    "               \"punctuation\"\n" +
                    "             ]\n" +
                    "           }\n" +
                    "        }\n" +
                    "    }\n" +
                    "    }", XContentType.JSON);
            request.mapping("{\n" +
                    "      \"properties\" : {\n" +
                    "        \"created\" : {\n" +
                    "          \"properties\" : {\n" +
                    "            \"date\" : {\n" +
                    "              \"properties\" : {\n" +
                    "                \"day\" : {\n" +
                    "                  \"type\" : \"long\"\n" +
                    "                },\n" +
                    "                \"month\" : {\n" +
                    "                  \"type\" : \"long\"\n" +
                    "                },\n" +
                    "                \"year\" : {\n" +
                    "                  \"type\" : \"long\"\n" +
                    "                }\n" +
                    "              }\n" +
                    "            },\n" +
                    "            \"time\" : {\n" +
                    "              \"properties\" : {\n" +
                    "                \"hour\" : {\n" +
                    "                  \"type\" : \"long\"\n" +
                    "                },\n" +
                    "                \"minute\" : {\n" +
                    "                  \"type\" : \"long\"\n" +
                    "                },\n" +
                    "                \"nano\" : {\n" +
                    "                  \"type\" : \"long\"\n" +
                    "                },\n" +
                    "                \"second\" : {\n" +
                    "                  \"type\" : \"long\"\n" +
                    "                }\n" +
                    "              }\n" +
                    "            }\n" +
                    "          }\n" +
                    "        },\n" +
                    "        \"doc\" : {\n" +
                    "          \"type\" : \"text\",\n" +
                    "          \"analyzer\": \"charSplit\"\n" +
                    "        },\n" +
                    "        \"title\" : {\n" +
                    "          \"type\" : \"text\"\n" +
                    "        },\n" +
                    "        \"updated\" : {\n" +
                    "          \"properties\" : {\n" +
                    "            \"date\" : {\n" +
                    "              \"properties\" : {\n" +
                    "                \"day\" : {\n" +
                    "                  \"type\" : \"long\"\n" +
                    "                },\n" +
                    "                \"month\" : {\n" +
                    "                  \"type\" : \"long\"\n" +
                    "                },\n" +
                    "                \"year\" : {\n" +
                    "                  \"type\" : \"long\"\n" +
                    "                }\n" +
                    "              }\n" +
                    "            },\n" +
                    "            \"time\" : {\n" +
                    "              \"properties\" : {\n" +
                    "                \"hour\" : {\n" +
                    "                  \"type\" : \"long\"\n" +
                    "                },\n" +
                    "                \"minute\" : {\n" +
                    "                  \"type\" : \"long\"\n" +
                    "                },\n" +
                    "                \"nano\" : {\n" +
                    "                  \"type\" : \"long\"\n" +
                    "                },\n" +
                    "                \"second\" : {\n" +
                    "                  \"type\" : \"long\"\n" +
                    "                }\n" +
                    "              }\n" +
                    "            }\n" +
                    "          }\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }", XContentType.JSON);
            CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
            logger.info("create index: {}", response.isShardsAcknowledged());
            new Spider(myPageProcessor).addUrl("http://my.oschina.net/flashsword/blog").addPipeline(myPipeline).thread(6).run();
        }
    }
}
