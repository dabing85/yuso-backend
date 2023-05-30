package com.yupi.springbootinit.model.dto.post;

import com.yupi.springbootinit.common.PageRequest;
import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "PostQueryRequest",description = "查找文章请求类")
public class PostQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * id
     */
    private Long notId;

    /**
     * 搜索词
     */
    @ApiModelProperty("搜索词")
    private String searchText;

    /**
     * 标题
     */
    @ApiModelProperty("标题")
    private String title;

    /**
     * 内容
     */
    @ApiModelProperty("内容")
    private String content;

    /**
     * 标签列表
     */
    @ApiModelProperty("标签列表")
    private List<String> tags;

    /**
     * 至少有一个标签
     */
    @ApiModelProperty("至少有一个标签")
    private List<String> orTags;

    /**
     * 创建用户 id
     */
    @ApiModelProperty("创建用户 id")
    private Long userId;

    /**
     * 收藏用户 id
     */
    @ApiModelProperty("收藏用户 id")
    private Long favourUserId;

    private static final long serialVersionUID = 1L;
}