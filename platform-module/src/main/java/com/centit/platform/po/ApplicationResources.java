package com.centit.platform.po;

import com.centit.framework.core.dao.DictionaryMap;
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

/**
 * @author tian_y
 */
@Data
@Entity
@Table(name = "m_application_resources")
@ApiModel("应用资源关联表")
public class ApplicationResources implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    @NotBlank(message = "字段不能为空")
    @ApiModelProperty(value = "id", hidden = true)
    @ValueGenerator(strategy = GeneratorType.UUID22)
    private String id;

    @ApiModelProperty(value = "应用id")
    @Column(name = "os_id")
    @Length(max = 32, message = "字段长度不能大于{max}")
    private String osId;

    @ApiModelProperty(value = "资源id")
    @Column(name = "database_id")
    @Length(max = 32, message = "字段长度不能大于{max}")
    private String dataBaseId;

    @ApiModelProperty(value = "分配时间", name = "push_time",hidden = true)
    @ValueGenerator(strategy = GeneratorType.FUNCTION, value = "today()")
    @Column(name = "push_time")
    private Date pushTime;

    @ApiModelProperty(value = "分配人")
    @Column(name = "push_user")
    @Length(max = 32, message = "字段长度不能大于{max}")
    @DictionaryMap(
        fieldName = {"pushUserName"},
        value = {"userCode"}
    )
    private  String pushUser;

    @ApiModelProperty(value = "是否为默认关系数据库")
    @Column(name = "is_used")
    @Length(max = 1, message = "字段长度不能大于{max}")
    private String isUsed;

}
