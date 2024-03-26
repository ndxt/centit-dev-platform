package com.centit.locode.platform.po;

import com.alibaba.fastjson2.JSONObject;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@ApiModel
@Entity
@Data
@Table(name = "question")
public class Question implements Serializable {
    private static final long serialVersionUID = 1;

    @Id
    @Column(name = "question_id")
    @ApiModelProperty(value = "id",hidden = true)
    @NotBlank
    @ValueGenerator(strategy = GeneratorType.UUID)
    private  String questionId;

    @ApiModelProperty(value = "关联表id")
    @Column(name = "relation_id")
    @Length(max = 32)
    private  String  relationId;

    @ApiModelProperty(value = "应用id")
    @Column(name = "os_id")
    @Length(max = 32)
    private  String osId;

    @ApiModelProperty(value = "关联表类型 1：工作流 2：页面设计 3：api网关")
    @Column(name = "type")
    @Length(max = 1)
    private  String   type;

    @ApiModelProperty(value = "问题备注")
    @Column(name = "memo")
    @Length(max = 500)
    private  String memo;

    @ApiModelProperty(value = "问题类型")
    @Column(name = "label")
    @Length(max = 100)
    private  String  label;

    @ApiModelProperty(value = "问题状态")
    @Column(name = "state")
    @Length(max = 1)
    private  String state;

    @ApiModelProperty(value = "检查时间",hidden = true)
    @Column(name = "check_time")
    @ValueGenerator(strategy = GeneratorType.FUNCTION, value = "today()")
    private Date checkTime;

    @ApiModelProperty(value = "检查人")
    @Column(name = "check_user")
    @Length(max = 32)
    private  String checkUser;

    @ApiModelProperty(value = "问题内容")
    @Column(name = "content")
    private  JSONObject content;


}
