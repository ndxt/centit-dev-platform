package com.centit.locode.platform.po;

import com.centit.support.database.orm.GeneratorCondition;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
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
    @Column(name = "relation_id")
    @ApiModelProperty(value = "关联表ID")
    private String relationId;

    @Column(name = "object_type")
    @ApiModelProperty(value = "对象类型")
    private String objectType;

    @Column(name = "history_ID")
    @ApiModelProperty(value = "历史版本")
    private String historyId;

    @Column(name = "merge_type")
    @ApiModelProperty(name = "合并类型", value = "新增 C 删除 D 更新 U")
    private String mergeType;

    @Column(name = "merge_desc")
    @ApiModelProperty(value =  "合并说明")
    private String mergeDesc;

    @ValueGenerator(strategy = GeneratorType.FUNCTION, value = "today()")
    @Column(name = "merge_time")
    @ApiModelProperty(value = "创建时间")
    private Date mergeTime;

    @Column(name = "merge_status")
    @ApiModelProperty(value ="合并状态")
    private Boolean mergeStatus;

    @ValueGenerator(strategy = GeneratorType.FUNCTION, value = "today()", condition = GeneratorCondition.ALWAYS)
    @Column(name = "last_update_time")
    @ApiModelProperty(value ="最后更新时间")
    private Date lastUpdateTime;

    @Column(name = "update_user")
    @ApiModelProperty(value ="最后更新人")
    private String updateUser;
}
