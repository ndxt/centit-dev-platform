package com.centit.locode.runtime.po;

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

@Data
@Entity
@Table(name = "m_dummy_table")
@ApiModel("一个不存在的表")
public class DummyPo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    @NotBlank
    @ApiModelProperty(value = "id", hidden = true)
    @ValueGenerator(strategy = GeneratorType.UUID22)
    private String id;
}
