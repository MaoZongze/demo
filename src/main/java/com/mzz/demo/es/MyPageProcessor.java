package com.mzz.demo.es;

import com.mzz.demo.SimpleDocument;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.stream.Collectors;

@Component
public class MyPageProcessor implements PageProcessor {
    @Override
    public void process(Page page) {
        page.getHtml().xpath("//div[@class='ui relaxed divided items list-container space-list-container']/div[@class='item blog-item']").nodes()
                .stream()
                .map(selectable -> selectable.xpath("//div[@class='content']"))
//                .peek(System.out::println)
                .forEach(selectable -> {
                    String link = selectable.xpath("a/@href").toString();
                    String desc = selectable.xpath("div[@class='description']/p/text()").toString();
                    String title = selectable.xpath("a/text()").toString();
                    SimpleDocument simpleDocument = new SimpleDocument(title, desc);
                    //System.out.println(simpleDocument);
                    page.addTargetRequest(link);
                });
        String title = page.getHtml().xpath("//h2[@class='header']/text()").toString();
        String doc = page.getHtml().xpath("//div[@id='articleContent']/p").nodes().stream().skip(1).map(String::valueOf).collect(Collectors.joining("\n"));
        page.putField("title", title);
        page.putField("doc", doc);
    }

    @Override
    public Site getSite() {
        return Site.me().setRetrySleepTime(3).setSleepTime(100);
    }
}
