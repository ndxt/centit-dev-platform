package com.centit.locode.platform.po;

import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.core.dao.DictionaryMap;
import com.centit.support.database.orm.GeneratorCondition;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.Date;

/**
 * create by codefan@sina.com
 *
 * @author codefan
 * 应用信息
 */
@Data
@Entity
@Table(name = "M_APPLICATION_TEMPLATE")
@ApiModel("业务系统模块")
public class ApplicationTemplate implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "模版代码", hidden = true)
    @Id
    @Column(name = "TEMPLATE_ID")
    @ValueGenerator(strategy = GeneratorType.UUID22)
    private String templateId;

    @ApiModelProperty(value = "模板名称", required = true)
    @Column(name = "TEMPLATE_NAME")
    @Length(max = 500)
    private String templateName;

    @ApiModelProperty(value = "模板分类")
    @Column(name = "TEMPLATE_TYPE")
    @Length(max = 32)
    @DictionaryMap(fieldName = "templateTypeText", value = "TemplateType")
    private String templateType;

    @ApiModelProperty(value = "模板说明")
    @Column(name = "TEMPLATE_MEMO")
    @Length(max = 1000)
    private String templateMemo;

    @ApiModelProperty(value = "模板内容")
    @Column(name = "TEMPLATE_CONTENT")
    @Basic(fetch = FetchType.LAZY)
    private JSONObject templateContent;

    @Column(name = "LAST_UPDATE_TIME")
    @ValueGenerator(strategy = GeneratorType.FUNCTION,
        condition = GeneratorCondition.ALWAYS, value = "today()")
    private Date lastUpdateTime;

    @ApiModelProperty(value = "图片id")
    @Column(name = "PIC_ID")
    @Length(max = 64)
    private String picId;

    @ApiModelProperty(value = "是否启用")
    @Column(name = "IS_USED")
    @Length(max = 1)
    private String isUsed;
}
