package com.yupi.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.picture.PictureQueryRequest;
import com.yupi.springbootinit.model.dto.post.PostQueryRequest;
import com.yupi.springbootinit.model.dto.search.SearchQueryRequest;
import com.yupi.springbootinit.model.dto.user.UserQueryRequest;
import com.yupi.springbootinit.model.entity.Picture;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.vo.PostVO;
import com.yupi.springbootinit.model.vo.SearchVO;
import com.yupi.springbootinit.model.vo.UserVO;
import com.yupi.springbootinit.service.PictureService;
import com.yupi.springbootinit.service.PostService;
import com.yupi.springbootinit.service.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 聚合搜索
 */
@Api(tags = "聚合搜索")
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {
    @Resource
    private PictureService pictureService;

    @Resource
    private UserService userService;

    @Resource
    private PostService postService;

    /**
     * 分页获取列表（封装类）
     *
     * @param searchQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchQueryRequest searchQueryRequest,
                                                  HttpServletRequest request) {
        long current= searchQueryRequest.getCurrent();
        long size= searchQueryRequest.getPageSize();
        String searchText= searchQueryRequest.getSearchText();

        //获取图片
        Page<Picture> picturePage = pictureService.searchPicture(searchText,current,size);
        //获取文章
        PostQueryRequest postQueryRequest=new PostQueryRequest();
        postQueryRequest.setSearchText(searchText);
        Page<Post> postPage = postService.page(new Page<>(current, size),
                postService.getQueryWrapper(postQueryRequest));
        Page<PostVO> postVoPage = postService.getPostVOPage(postPage, request);
        //获取用户
        UserQueryRequest userQueryRequest=new UserQueryRequest();
        userQueryRequest.setUserName(searchText);
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
        userVOPage.setRecords(userVO);
        //塞值
        SearchVO searchVO=new SearchVO();
        searchVO.setPictureList(picturePage.getRecords());
        searchVO.setPostList(postVoPage.getRecords());
        searchVO.setUserList(userVOPage.getRecords());
        return ResultUtils.success(searchVO);
    }

    /**
     * 并发查询
     */
    //@PostMapping("/all")
    public BaseResponse<SearchVO> searchAllConcurrence(@RequestBody SearchQueryRequest searchQueryRequest,
                                            HttpServletRequest request) {
        long current= searchQueryRequest.getCurrent();
        long size= searchQueryRequest.getPageSize();
        String searchText= searchQueryRequest.getSearchText();

        //获取图片
        CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> {
            Page<Picture> picturePage = pictureService.searchPicture(searchText, current, size);
            return picturePage;
        });

        //获取文章
        CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
            PostQueryRequest postQueryRequest = new PostQueryRequest();
            postQueryRequest.setSearchText(searchText);
            Page<Post> postPage = postService.page(new Page<>(current, size),
                    postService.getQueryWrapper(postQueryRequest));
            Page<PostVO> postVOPage = postService.getPostVOPage(postPage, request);
            return postVOPage;
        });
        //获取用户
        CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
            UserQueryRequest userQueryRequest = new UserQueryRequest();
            userQueryRequest.setUserName(searchText);
            Page<User> userPage = userService.page(new Page<>(current, size),
                    userService.getQueryWrapper(userQueryRequest));
            Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
            List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
            userVOPage.setRecords(userVO);
            return userVOPage;
        });
        CompletableFuture.allOf(pictureTask,postTask,userTask).join();

        try {
            Page<Picture> picturePage = pictureTask.get();
            Page<UserVO> userVOPage = userTask.get();
            Page<PostVO> postVOPage = postTask.get();
            //塞值
            SearchVO searchVO=new SearchVO();
            searchVO.setPictureList(picturePage.getRecords());
            searchVO.setPostList(postVOPage.getRecords());
            searchVO.setUserList(userVOPage.getRecords());
            return ResultUtils.success(searchVO);
        } catch (Exception e) {
            log.error("查询异常",e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"查询异常");
        }
    }
}
