package com.centit.platform.po;

import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;

/**
 * create by codefan@sina.com
 * @author codefan
 * 应用信息
 */
@Data
@Entity
@Table(name = "F_GROUP_TABLE")
@ApiModel("分组模块")
public class GroupInfo implements java.io.Serializable {

    private static final long serialVersionUID =  1L;

    @ApiModelProperty(value = "分组id", hidden = true)
    @Id
    @Column(name = "GROUP_ID")
    @ValueGenerator(strategy = GeneratorType.UUID22)
    private String groupId;

    @ApiModelProperty(value = "分组名称", required = true)
    @Column(name = "GROUP_NAME")
    @Length(max = 128, message = "字段长度不能大于{max}")
    private String  groupName;

    @ApiModelProperty(value = "父类分组")
    @Column(name = "PARENT_GROUP_ID")
    @Length(max = 32, message = "字段长度不能大于{max}")
    private String  parentGroupId;

    @ApiModelProperty(value = "业务模块代码")
    @Column(name = "APPLICATION_ID")
    private String  applicationId;

}
