package com.yupi.springbootinit.model.dto.post;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 创建请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
@ApiModel(value = "PostAddRequest",description = "添加文章请求类")
public class PostAddRequest implements Serializable {

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

    private static final long serialVersionUID = 1L;
}