package com.centit.locode.platform.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.OsInfo;
import com.centit.locode.platform.dao.ApplicationVersionDao;
import com.centit.locode.platform.po.ApplicationVersion;
import com.centit.locode.platform.po.HistoryVersion;
import com.centit.locode.platform.service.ApplicationVersionService;
import com.centit.locode.platform.service.HistoryVersionService;
import com.centit.locode.platform.service.ModelExportManager;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.UuidOpt;
import com.centit.support.common.ObjectException;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.security.Sha1Encoder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class ApplicationVersionServiceImpl implements ApplicationVersionService {

    @Autowired
    ApplicationVersionDao applicationVersionDao;

    @Autowired
    PlatformEnvironment platformEnvironment;

    @Autowired
    HistoryVersionService historyVersionService;

    @Autowired
    ModelExportManager modelExportManager;

    public static void mapJsonProperties(JSONObject json, String ... props){
        for(String p : props){
            String sp = json.getString(p);
            if(StringUtils.isNotBlank(sp)){
                json.put(p, JSON.parse(sp));
            }
        }
    }

    private List<HistoryVersion> createHistoryVersions(String osId){
        List<HistoryVersion> hvs = new ArrayList<>(100);
        //查找应用相关的所有页面
        JSONArray pages = DatabaseOptUtils.listObjectsBySqlAsJson(applicationVersionDao,
            "select MODEL_ID, Model_Name, OPT_ID, os_id, Model_Type," +
                "Model_Comment, MOBILE_FORM_TEMPLATE, form_template," +
                "STRUCTURE_FUNCTION, MODEL_TAG  " +
                " from m_meta_form_model where IS_VALID = 'F' and os_id = ?",
            new Object[]{osId});

        if(pages!=null){
            for(Object obj : pages){
                if(obj instanceof JSONObject) {
                    JSONObject jsonObject = (JSONObject) obj;
                    HistoryVersion hv = new HistoryVersion();
                    hv.setType("2");
                    hv.setRelationId(jsonObject.getString("modelId"));
                    hv.setMemo(jsonObject.getString("modelName"));
                    mapJsonProperties(jsonObject, "formTemplate", "mobileFormTemplate", "structureFunction");
                    hv.setContent(jsonObject);
                    hvs.add(hv);
                }
            }
        }
        //查找应用相关的所有api
        JSONArray apis = DatabaseOptUtils.listObjectsBySqlAsJson(applicationVersionDao,
            "select task_type, task_Cron, SOURCE_ID, schema_props, " +
                "return_type, return_result, request_body_type, " +
                "PACKET_TYPE, PACKET_NAME, PACKET_ID, PACKET_DESC, " +
                "os_id, OPT_ID, opt_code, need_rollback, " +
                "is_disable, interface_name, has_data_opt, " +
                "EXT_PROPS, data_opt_desc_json " +
                " from q_data_packet where is_disable = 'F' and os_id = ?",
            new Object[]{osId});

        if(apis!=null){
            for(Object obj : apis){
                if(obj instanceof JSONObject) {
                    JSONObject jsonObject = (JSONObject) obj;
                    HistoryVersion hv = new HistoryVersion();
                    hv.setType("3");
                    hv.setRelationId(jsonObject.getString("packetId"));
                    hv.setMemo(jsonObject.getString("packetName"));
                    //dataOptDescJson returnResult extProps schemaProps
                    mapJsonProperties(jsonObject, "dataOptDescJson","returnResult","extProps","schemaProps");
                    hv.setContent(jsonObject);
                    hvs.add(hv);
                }
            }
        }
        //查找应用相关的所有工作流，工作流的比较复杂，需要相关的版本和变量等信息
        JSONArray flows = DatabaseOptUtils.listObjectsBySqlAsJson(applicationVersionDao,
            "select b.FLOW_CODE, b.version, b.FLOW_NAME, b.FLOW_CLASS, " +
                "b.FLOW_STATE, b.FLOW_DESC, b.FLOW_XML_DESC, b.Time_Limit, b.Expire_Opt," +
                "b.Opt_ID, b.OS_ID  " +
                " from " +
                " (select FLOW_CODE, max(version) as version from wf_flow_define where os_id = ? group by flow_code) a " +
                " join wf_flow_define b on (a.flow_code=b.flow_code and a.version = b.version) " +
                " where b.FLOW_STATE = 'B' ",
            new Object[]{osId});
        if(flows!=null){
            for(Object obj : flows){
                if(obj instanceof JSONObject) {
                    JSONObject jsonObject = (JSONObject) obj;
                    HistoryVersion hv = new HistoryVersion();
                    hv.setType("1");
                    hv.setRelationId(jsonObject.getString("flowCode"));
                    hv.setMemo(jsonObject.getString("flowName"));
                    //dataOptDescJson returnResult extProps schemaProps
                    JSONObject jsonContent = JSON.parseObject(jsonObject.getString("flowXmlDesc"));
                    if(jsonContent!=null) {
                        jsonContent.put("optId", jsonObject.getString("optId"));
                        jsonContent.put("flowCode", jsonObject.getString("flowCode"));
                        jsonContent.put("version", jsonObject.getString("version"));
                        jsonContent.put("flowName", jsonObject.getString("flowName"));
                    }else{
                        jsonContent = jsonObject;
                    }
                    hv.setContent(jsonContent);
                    hvs.add(hv);
                }
            }
        }
        return hvs;
    }
    /**
     * 直接从数据库中获取 页面、api 和工作流引擎数据（工作流引擎引用最新版本的）
     * @param applicationVersion 版本信息
     */
    @Override
    public String createApplicationVersion(ApplicationVersion applicationVersion) {
        if(StringUtils.isBlank(applicationVersion.getApplicationId())){
            throw new ObjectException(ObjectException.DATA_VALIDATE_ERROR, "没有指定关联应用信息！");
        }
        OsInfo osInfo = platformEnvironment.getOsInfo(applicationVersion.getApplicationId());
        if(osInfo==null){
            throw new ObjectException(ObjectException.DATA_VALIDATE_ERROR, "关联应用信息不能存在！");
        }
        //保存版本信息

        String versionId = UuidOpt.getUuidAsString22();
        applicationVersion.setVersionId(versionId);
        Date currentTime = DatetimeOpt.currentUtilDate();
        applicationVersion.setDateCreated(currentTime);
        List<HistoryVersion> hvs = createHistoryVersions(applicationVersion.getApplicationId());
        for(HistoryVersion hv : hvs){
            hv.setAppVersionId(versionId);
            hv.setOsId(applicationVersion.getApplicationId());
            hv.setPushTime(currentTime);
            hv.setPushUser("system");
            hv.setLabel("全局版本："+versionId);
            historyVersionService.createHistoryVersion(hv);
        }
        // 生成 并上传 应用导出包 (zip文件）上传到文件服务器并返回 fileId
        try {
            String fileId = modelExportManager.exportModelAndSaveToFileServer(applicationVersion.getApplicationId());
            applicationVersion.setBackupFileId(fileId);
            applicationVersionDao.saveNewObject(applicationVersion);
            return versionId;
        } catch (IOException e){
            throw new RuntimeException("创建应用版本完成，但是上传应用快照失败："+e.getMessage(), e);
        }
    }

    @Override
    public void updateApplicationVersion(ApplicationVersion applicationVersion) {
        //只能更改基本的显示信息，关键信息不能修改
        applicationVersion.setBackupFileId(null);
        applicationVersion.setApplicationId(null);
        applicationVersionDao.updateObject(applicationVersion);
    }

    @Override
    public void deleteApplicationVersion(String versionId) {
        applicationVersionDao.deleteObjectById(versionId);
    }

    @Override
    public List<ApplicationVersion> listApplicationVersion(String applicationId, PageDesc pageDesc) {
        return applicationVersionDao.listObjectsByProperties(
            CollectionsOpt.createHashMap("applicationId", applicationId),
            pageDesc);
    }

    @Override
    public ApplicationVersion getApplicationVersion(String versionId) {
        return applicationVersionDao.getObjectById(versionId);
    }

    private JSONArray compareTwoHVS(List<HistoryVersion> hvs1, List<HistoryVersion> hvs2){
        int len1= hvs1==null?0:hvs1.size();
        int len2= hvs2==null?0:hvs2.size();
        int i=0, j=0;
        JSONArray diffJsons = new JSONArray();
        while(i<len1 && j<len2){
            HistoryVersion hv1 = hvs1.get(i);
            HistoryVersion hv2 = hvs2.get(j);
            if(StringUtils.equals(hv1.getType(), hv2.getType()) && StringUtils.equals(hv1.getRelationId(), hv2.getRelationId())){
                if(!StringUtils.equals(hv1.getHistorySha(), hv2.getHistorySha())){
                    JSONObject diff = new JSONObject();
                    diff.put("type", hv1.getType());
                    diff.put("typeDesc", hv1.getTypeDesc());
                    diff.put("relationId",hv1.getRelationId());
                    diff.put("historyId",hv1.getHistoryId());
                    diff.put("historyId2",hv2.getHistoryId());
                    diff.put("diff", "更改");
                    diff.put("memo", hv1.getMemo());
                    diffJsons.add(diff);
                }
                i++; j++;
            } else if(StringUtils.compare(hv1.getType(),hv2.getType())<0 ||
                (StringUtils.compare(hv1.getType(),hv2.getType())==0  &&
                        StringUtils.compare(hv1.getRelationId(), hv2.getRelationId())<0)) {
                JSONObject diff = new JSONObject();
                diff.put("type", hv1.getType());
                diff.put("typeDesc", hv1.getTypeDesc());
                diff.put("relationId",hv1.getRelationId());
                diff.put("historyId",hv1.getHistoryId());
                diff.put("diff", "删除");
                diff.put("memo", hv1.getMemo());
                diffJsons.add(diff);
                i++;
            } else {
                JSONObject diff = new JSONObject();
                diff.put("type", hv2.getType());
                diff.put("typeDesc", hv2.getTypeDesc());
                diff.put("relationId",hv2.getRelationId());
                diff.put("historyId2",hv2.getHistoryId());
                diff.put("diff", "新增");
                diff.put("memo", hv2.getMemo());
                diffJsons.add(diff);
                j++;
            }
        }

        while(i<len1){
            HistoryVersion hv1 = hvs1.get(i);
            JSONObject diff = new JSONObject();
            diff.put("type", hv1.getType());
            diff.put("typeDesc", hv1.getTypeDesc());
            diff.put("relationId",hv1.getRelationId());
            diff.put("historyId",hv1.getHistoryId());
            diff.put("diff", "删除");
            diff.put("memo", hv1.getMemo());
            diffJsons.add(diff);
            i++;
        }

        while(j<len2){
            HistoryVersion hv2 = hvs2.get(j);
            JSONObject diff = new JSONObject();
            diff.put("type", hv2.getType());
            diff.put("typeDesc", hv2.getTypeDesc());
            diff.put("relationId",hv2.getRelationId());
            diff.put("historyId2",hv2.getHistoryId());
            diff.put("diff", "新增");
            diff.put("memo", hv2.getMemo());
            diffJsons.add(diff);
            j++;
        }

        return diffJsons;
    }


    @Override
    public JSONArray compareTwoVersion(String versionId, String versionId2) {
        List<HistoryVersion> hvs1 = historyVersionService.listHistoryByAppVersion(versionId);
        List<HistoryVersion> hvs2 = historyVersionService.listHistoryByAppVersion(versionId2);
        return compareTwoHVS(hvs1, hvs2);
    }

    @Override
    public  JSONArray compareToOldVersion(String applicationId, String versionId){
        List<HistoryVersion> hvs1 = historyVersionService.listHistoryByAppVersion(versionId);
        List<HistoryVersion> hvs2 = createHistoryVersions(applicationId);
        for(HistoryVersion hv : hvs2){
           hv.setHistorySha(
                Sha1Encoder.encodeBase64(hv.getContent().toJSONString(), true) );
        }
        return compareTwoHVS(hvs1, hvs2);
    }

    @Override
    public void restoreApplicationVersion(String versionId) {

    }

}
