package com.yupi.springbootinit.model.dto.search;

import com.yupi.springbootinit.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Description TODO
 * @Author Dabing
 * @Date 2023/5/28 13:12
 */
@EqualsAndHashCode
@Data
@ApiModel("聚合搜索请求类")
public class SearchQueryRequest extends PageRequest implements Serializable {

    @ApiModelProperty("搜索词")
    private String searchText;

    private static final long serialVersionUID = 1L;
}
