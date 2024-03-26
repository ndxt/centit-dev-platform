package com.centit.locode.platform.po;

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

@Data
@Entity
@Table(name = "m_application_dictionary")
@ApiModel("应用数据字典关联表")
public class ApplicationDictionary implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    @NotBlank
    @ApiModelProperty(value = "id", hidden = true)
    @ValueGenerator(strategy = GeneratorType.UUID22)
    private String id;

    @ApiModelProperty(value = "应用id")
    @Column(name = "os_id")
    @Length(max = 32)
    private String osId;

    @ApiModelProperty(value = "数据字典Id")
    @Column(name = "dictionary_id")
    @Length(max = 32)
    private String dictionaryId;

    @ApiModelProperty(value = "分配时间", name = "push_time",hidden = true)
    @ValueGenerator(strategy = GeneratorType.FUNCTION, value = "today()")
    @Column(name = "push_time")
    private Date pushTime;

    @ApiModelProperty(value = "分配人")
    @Column(name = "push_user")
    @Length(max = 32)
    private  String pushUser;

}
