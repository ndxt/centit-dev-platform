package com.centit.locode.platform.po;

import com.alibaba.fastjson2.JSONObject;
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
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Table(name = "I18N_MESSAGES")
@ApiModel(description = "国际化信息表")
@Data
public class I18nMessages implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "OS_ID")
    @NotBlank
    @ApiModelProperty(value = "应用ID")
    private String osId;

    @Id
    @NotBlank
    @Column(name = "MSG_KEY")
    @ApiModelProperty(value = "标签ID")
    private String msgKey;

    @NotBlank
    @Column(name = "MSG_VALUE")
    @ApiModelProperty(value = "标签文本")
    private JSONObject msgValue;

    @Column(name = "last_update_time")
    @ApiModelProperty(value = "最后更新时间")
    @ValueGenerator(strategy = GeneratorType.FUNCTION, value = "today()", condition = GeneratorCondition.ALWAYS)
    private Date lastUpdateTime;

    @Column(name = "update_user")
    @ApiModelProperty(value = "更新人员")
    private String updateUser;
}
