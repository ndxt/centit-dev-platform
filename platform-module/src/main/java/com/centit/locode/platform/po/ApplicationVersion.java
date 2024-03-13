package com.centit.locode.platform.po;

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

@Entity
@Table(name = "application_version")
@ApiModel(description = "应用版本信息")
@Data
public class ApplicationVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "VERSION_ID")
    @ApiModelProperty(value = "版本id")
    @ValueGenerator(strategy = GeneratorType.UUID22)
    private String versionId;

    @Column(name = "APPLICATION_ID")
    @ApiModelProperty(value = "关联的应用")
    private String applicationId;

    @Column(name = "VERSION_LABEL")
    @ApiModelProperty(value = "版本标签")
    private String versionLabel;

    @Column(name = "DATE_CREATED")
    @ApiModelProperty(value = "创建日期")
    private String dateCreated;

    @Column(name = "CREATOR")
    @ApiModelProperty(value = "创建人")
    private String creator;

    @Column(name = "NOTE_INFO")
    @ApiModelProperty(value = "备注信息")
    private String noteInfo;

    @Column(name = "BACKUP_FILE_ID")
    @ApiModelProperty(value = "备份文件")
    private String backupFileId;
}
