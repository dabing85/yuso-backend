package com.yupi.springbootinit.model.vo;

import com.yupi.springbootinit.model.entity.Picture;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 聚合搜索结果视图
 *
 */
@Data
@ApiModel("聚合搜索结果视图")
public class SearchVO implements Serializable {
    @ApiModelProperty("文章")
    private List<PostVO> postList;
    @ApiModelProperty("用户")
    private List<UserVO> userList;
    @ApiModelProperty("图片")
    private List<Picture> pictureList;

    private static final long serialVersionUID = 1L;
}