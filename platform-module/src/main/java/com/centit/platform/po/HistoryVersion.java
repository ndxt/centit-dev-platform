package com.centit.platform.po;

import com.alibaba.fastjson.JSONObject;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@ApiModel
@Entity
@Data
@Table(name = "history_version")
public class HistoryVersion implements Serializable {
    private static final long serialVersionUID = 1;

    @Id
    @Column(name = "hitstory_id")
    @ApiModelProperty(value = "id",hidden = true)
    @NotBlank(message = "字段不能为空")
    @ValueGenerator(strategy = GeneratorType.UUID)
    private  String hitstoryId;

    @ApiModelProperty(value = "关联表id")
    @Column(name = "relation_id")
    @Length(max = 32, message = "字段长度不能大于{max}")
    private  String relationId;

    @ApiModelProperty(value = "应用id")
    @Column(name = "os_id")
    @Length(max = 32, message = "字段长度不能大于{max}")
    private  String osId;

    @ApiModelProperty(value = "内容")
    @Column(name = "content")
    @Basic(fetch = FetchType.LAZY)
    private JSONObject content;

    @ApiModelProperty(value = "标签")
    @Column(name = "lable")
    @Length(max = 100, message = "字段长度不能大于{max}")
    private  String lable;

    @ApiModelProperty(value = "备注")
    @Column(name = "memo")
    @Length(max = 500, message = "字段长度不能大于{max}")
    private  String  memo;

    @ApiModelProperty(value = "提交时间", name = "push_time",hidden = true)
    @ValueGenerator(strategy = GeneratorType.FUNCTION, value = "today()")
    @Column(name = "push_time")
    private Date pushTime;

    @ApiModelProperty(value = "提交人")
    @Column(name = "push_user")
    @Length(max = 32, message = "字段长度不能大于{max}")
    private  String pushUser;

    @ApiModelProperty(value = "类型，1：工作流 2：页面设计 3：api网关")
    @Column(name = "type")
    @NotBlank(message = "字段不能为空")
    @Length(max = 1, message = "字段长度不能大于{max}")
    private  String type;
}
