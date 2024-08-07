package com.centit.locode.platform.po;

import com.alibaba.fastjson2.JSONObject;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import com.centit.support.security.Sha1Encoder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@ApiModel
@Entity
@Data
@Table(name = "history_version")
public class HistoryVersion implements Serializable, Comparable<HistoryVersion> {

    private static final long serialVersionUID = 1;

    @Id
    @Column(name = "history_id")
    @ApiModelProperty(value = "id",hidden = true)
    @NotBlank
    @ValueGenerator(strategy = GeneratorType.UUID22)
    private  String historyId;

    @ApiModelProperty(value = "类型，1：工作流 2：页面设计 3：api网关")
    @Column(name = "type")
    @NotBlank
    @Length(max = 1)
    private  String type;

    @Column(name = "APP_VERSION_ID")
    @ApiModelProperty(value = "关联应用版本ID")
    private String appVersionId;

    @ApiModelProperty(value = "关联表id")
    @Column(name = "relation_id")
    @Length(max = 32)
    private  String relationId;

    @ApiModelProperty(value = "应用id")
    @Column(name = "os_id")
    @Length(max = 32)
    private  String osId;

    @ApiModelProperty(value = "内容")
    @Column(name = "content")
    @Basic(fetch = FetchType.LAZY)
    private JSONObject content;

    @ApiModelProperty(value = "标签")
    @Column(name = "label")
    @Length(max = 100)
    private  String label;

    @ApiModelProperty(value = "备注")
    @Column(name = "memo")
    @Length(max = 500)
    private  String  memo;

    @ApiModelProperty(value = "提交时间", name = "push_time", hidden = true)
    @ValueGenerator(strategy = GeneratorType.FUNCTION, value = "today()")
    @Column(name = "push_time")
    private Date pushTime;

    @ApiModelProperty(value = "提交人")
    @Column(name = "push_user")
    @Length(max = 32)
    private  String pushUser;

    @Column(name = "history_sha")
    @ApiModelProperty(value = "版本指纹")
    @Length(max = 80)
    private String historySha;

    public String getTypeDesc(){
        //1：工作流 2：页面设计 3：api网关")
        if("1".equals(type)) return "工作流";
        if("2".equals(type)) return "页面设计";
        if("3".equals(type)) return "api接口";
        return "unknown";
    }

    @Override
    public int compareTo(HistoryVersion o) {
        if(StringUtils.equals(this.getType(), o.getType()) && StringUtils.equals(this.getRelationId(), o.getRelationId()))
            return 0;
        if(StringUtils.compare(this.getType(),o.getType())<0 ||
            (StringUtils.compare(this.getType(), o.getType())==0  &&
                StringUtils.compare(this.getRelationId(), o.getRelationId())<0))
            return -1;
        return 1;
    }

    public String generateHistorySha(){
        JSONObject jsonObject = new JSONObject();
        for(Map.Entry<String, Object> entry : this.getContent().entrySet()){
            if(StringUtils.equalsAny(entry.getKey(),
                "formTemplate","mobileFormTemplate","structureFunction",
                    "dataOptDescJson","extProps","schemaProps",
                "nodeList","transList")){
                jsonObject.put(entry.getKey(),entry.getValue());
            }
        }
        return Sha1Encoder.encodeBase64(jsonObject.toJSONString(), true);
    }
}
