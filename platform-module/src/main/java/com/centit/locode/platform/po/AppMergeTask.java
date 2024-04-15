package com.centit.locode.platform.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Table(name = "APP_MERGE_TASK ")
@ApiModel(description = "版本合并信息")
@Data
public class AppMergeTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "APP_VERSION_ID")
    @ApiModelProperty(value = "版本id")
    private String appVersionId;

    @Id
    @Column(name = "relateion_ID")
    @ApiModelProperty(value = "关联表ID")
    private String relateionId;

    @Column(name = "object_type")
    @ApiModelProperty(value = "对象类型")
    private String objectType;

    @Column(name = "history_ID")
    @ApiModelProperty(value = "历史版本")
    private String historyId;

    @Column(name = "merge_type")
    @ApiModelProperty(name = "合并类型", value = "新增 N 删除 D 更新 U")
    private String mergeType;

    @Column(name = "merge_time")
    @ApiModelProperty(value = "创建时间")
    private Date mergeTime;
}
