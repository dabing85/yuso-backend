package com.yupi.springbootinit.job.once;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yupi.springbootinit.esdao.PostEsDao;
import com.yupi.springbootinit.model.dto.post.PostEsDTO;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全量同步帖子到 es
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
// todo 取消注释开启任务
//@Component
@Slf4j
public class FetchInitPostList implements CommandLineRunner {

    @Resource
    private PostService postService;


    @Override
    public void run(String... args) {
        String json = "{\"current\":1,\"pageSize\":8,\"sortField\":\"_score\",\"sortOrder\":\"descend\",\"category\":\"文章\",\"reviewStatus\":1}";
        String url= "https://www.code-nav.cn/api/post/search/page/vo";
        String result = HttpRequest.post(url)
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
        if (b) {
            log.info("获取初始化帖子列表成功, 条数 = {}", postList.size());
        } else {
            log.error("获取初始化帖子列表失败");
        }
    }
}
