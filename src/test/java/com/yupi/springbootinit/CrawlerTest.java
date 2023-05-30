package com.yupi.springbootinit;

import cn.hutool.http.HttpRequest;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yupi.springbootinit.model.entity.Picture;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description 爬虫测试
 * @Author Dabing
 * @Date 2023/5/26 14:34
 */
@SpringBootTest
@Slf4j
public class CrawlerTest {
    @Resource
    private PostService postService;
    @Test
    public void getPostTest(){
        String json = "{\"current\": 1, \"pageSize\": 8, \"sortField\": \"createTime\", \"sortOrder\": \"descend\", \"category\": \"文章\"}";
        String result = HttpRequest.post("https://www.code-nav.cn/api/post/search/page/vo")
                .body(json)
                .execute().body();
        Map<String, Object> map = JSONUtil.toBean(result, Map.class);
        int code = (Integer)map.get("code");
        if(code !=0){
            log.error("文章数据抓取异常",map.get("message"));
            return;
        }
        JSONObject data= (JSONObject) map.get("data");
        JSONArray records = (JSONArray) data.get("records");
        List<Post> postList=new ArrayList<>();
        for (Object record : records) {
            JSONObject tempRecord=(JSONObject) record;
            //todo 取值的时候需要判空
            Post post=new Post();
            post.setTitle(tempRecord.getStr("title"));
            post.setContent(tempRecord.getStr("content"));
            JSONArray tags = (JSONArray) tempRecord.get("tags");
            String tag = JSONUtil.toJsonStr(tags.toList(String.class));
            post.setTags(tag);
            post.setFavourNum(tempRecord.getInt("favourNum"));
            post.setFavourNum(tempRecord.getInt("thumbNum"));
            post.setUserId(1L);
            postList.add(post);
        }
        boolean b = postService.saveBatch(postList);
        Assertions.assertTrue(b);
    }

    @Test
    public void fetchPictureTest() throws IOException {
        int current=1;
        String url="https://www.bing.com/images/search?q=王一博&first="+current;
        Document doc = Jsoup.connect(url).get();
        log.info(doc.title());
        Elements elements = doc.select(".iuscp.isv");
        List<Picture> pictureList=new ArrayList<>();
        for (Element element : elements) {
            // 取图片地址 (murl)
            String m = element.select(".iusc").get(0).attr("m");
            Map<String, Object> map = JSONUtil.toBean(m, Map.class);
            String murl = (String) map.get("murl");
            // 取标题
            String title = element.select(".inflnk").get(0).attr("aria-label");
            Picture picture=new Picture();
            picture.setTitle(title);
            picture.setUrl(murl);
            pictureList.add(picture);
        }
        System.out.println(pictureList);
    }
}
