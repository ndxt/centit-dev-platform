package com.centit.locode.platform.po;

import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

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

    @Column(name = "CREATOR")
    @ApiModelProperty(value = "创建人")
    private String creator;

    @Column(name = "NOTE_INFO")
    @ApiModelProperty(value = "备注信息")
    private String noteInfo;

    @Column(name = "BACKUP_FILE_ID")
    @ApiModelProperty(value = "备份文件")
    private String backupFileId;

    public static final String VERSION_MERGE_STATUS_NONE = "A";
    public static final String VERSION_MERGE_STATUS_COMPLETED = "A";
    public static final String VERSION_MERGE_STATUS_MERGING = "B";

    @OrderBy("DESC")
    @Column(name = "MERGE_STATUS")
    @ApiModelProperty(name = "合并状态", value = "A: 合并完成(或无需合并）  B：合并中")
    private String mergeStatus;

    @Column(name = "MERGE_TIME")
    @ApiModelProperty(name = "合并时间")
    private Date mergeTime;

    @OrderBy("DESC")
    @Column(name = "DATE_CREATED")
    @ApiModelProperty(value = "创建日期")
    @ValueGenerator(strategy = GeneratorType.FUNCTION, value = "today()")
    private Date dateCreated;

}
