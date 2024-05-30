package com.centit.locode.platform.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.OsInfo;
import com.centit.locode.platform.dao.AppMergeTaskDao;
import com.centit.locode.platform.dao.ApplicationVersionDao;
import com.centit.locode.platform.po.AppMergeTask;
import com.centit.locode.platform.po.ApplicationVersion;
import com.centit.locode.platform.po.HistoryVersion;
import com.centit.locode.platform.service.ApplicationVersionService;
import com.centit.locode.platform.service.HistoryVersionService;
import com.centit.locode.platform.service.ModelExportManager;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.NumberBaseOpt;
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
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class ApplicationVersionServiceImpl implements ApplicationVersionService {

    @Autowired
    ApplicationVersionDao applicationVersionDao;

    @Autowired
    AppMergeTaskDao appMergeTaskDao;

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

    private HistoryVersion createFlowHV(JSONObject jsonObject){
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
            jsonContent.put("flowDesc", jsonObject.getString("flowDesc"));
        }else{
            jsonContent = jsonObject;
        }
        hv.setContent(jsonContent);
        return hv;
    }

    private HistoryVersion createPageHV(JSONObject jsonObject){
        HistoryVersion hv = new HistoryVersion();
        hv.setType("2");
        hv.setRelationId(jsonObject.getString("modelId"));
        hv.setMemo(jsonObject.getString("modelName"));
        mapJsonProperties(jsonObject, "formTemplate", "mobileFormTemplate", "structureFunction");
        hv.setContent(jsonObject);
        return hv;
    }

    private HistoryVersion createApiHV(JSONObject jsonObject){
        HistoryVersion hv = new HistoryVersion();
        hv.setType("3");
        hv.setRelationId(jsonObject.getString("packetId"));
        hv.setMemo(jsonObject.getString("packetName"));
        //dataOptDescJson returnResult extProps schemaProps
        mapJsonProperties(jsonObject, "dataOptDescJson","returnResult","extProps","schemaProps");
        hv.setContent(jsonObject);
        return hv;
    }

    private List<HistoryVersion> createHistoryVersions(String osId){
        List<HistoryVersion> hvs = new ArrayList<>(100);
        //// A 草稿 B 正常 C 过期 D 禁用  E 已发布
        //查找应用相关的所有工作流，工作流的比较复杂，需要相关的版本和变量等信息
        JSONArray flows = DatabaseOptUtils.listObjectsBySqlAsJson(applicationVersionDao,
            "select b.FLOW_CODE, b.version, b.FLOW_NAME, b.FLOW_CLASS, " +
                "b.FLOW_STATE, b.FLOW_DESC, b.FLOW_XML_DESC, b.Time_Limit, b.Expire_Opt," +
                "b.Opt_ID, b.OS_ID  " +
                " from " +
                " (select FLOW_CODE, max(version) as version from wf_flow_define where os_id = ? group by flow_code) a " +
                " join wf_flow_define b on (a.flow_code=b.flow_code and a.version = b.version) " +
                " where b.FLOW_STATE = 'B' " +
                " order by b.flow_code",
            new Object[]{osId});
        if(flows!=null){
            for(Object obj : flows){
                if(obj instanceof JSONObject) {
                    hvs.add(createFlowHV((JSONObject) obj));
                }
            }
        }
        //查找应用相关的所有页面
        JSONArray pages = DatabaseOptUtils.listObjectsBySqlAsJson(applicationVersionDao,
            "select MODEL_ID, Model_Name, OPT_ID, os_id, Model_Type," +
                "Model_Comment, MOBILE_FORM_TEMPLATE, form_template," +
                "STRUCTURE_FUNCTION, MODEL_TAG  " +
                " from m_meta_form_model where IS_VALID = 'F' and os_id = ?" +
                " order by MODEL_ID",
            new Object[]{osId});

        if(pages!=null){
            for(Object obj : pages){
                if(obj instanceof JSONObject) {
                    hvs.add(createPageHV((JSONObject) obj));
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
                "from q_data_packet where is_disable = 'F' and os_id = ? " +
                "order by PACKET_ID",
            new Object[]{osId});

        if(apis!=null){
            for(Object obj : apis){
                if(obj instanceof JSONObject) {
                    hvs.add(createApiHV((JSONObject) obj));
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
            //保存时 生成sha指纹
            hv.setHistorySha(
                Sha1Encoder.encodeBase64(hv.getContent().toJSONString(), true) );
            historyVersionService.createHistoryVersion(hv);
        }
        // 生成 并上传 应用导出包 (zip文件）上传到文件服务器并返回 fileId
        try {
            String fileId = modelExportManager.exportModelAndSaveToFileServer(osInfo);
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
        historyVersionService.removeAppHistoryTag(versionId);
    }

    @Override
    public boolean checkMergeState(String applicationId){
        return applicationVersionDao.countObjectByProperties(
            CollectionsOpt.createHashMap("applicationId" ,applicationId,
                "mergeStatus", "B")) > 0;
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

    private JSONArray compareTwoHVS(List<HistoryVersion> hvs1, List<HistoryVersion> hvs2, boolean withContent){
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
                    diff.put("diff", "U");
                    diff.put("diffDesc", "更改");
                    diff.put("memo", hv1.getMemo());
                    if(withContent) {
                        diff.put("content", hv2.getContent());
                    }
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
                diff.put("diff", "D");
                diff.put("diffDesc", "删除");
                diff.put("memo", hv1.getMemo());
                diffJsons.add(diff);
                i++;
            } else {
                JSONObject diff = new JSONObject();
                diff.put("type", hv2.getType());
                diff.put("typeDesc", hv2.getTypeDesc());
                diff.put("relationId",hv2.getRelationId());
                diff.put("historyId2",hv2.getHistoryId());
                diff.put("diff", "C");
                diff.put("diffDesc", "新增");
                diff.put("memo", hv2.getMemo());
                if(withContent) {
                    diff.put("content", hv2.getContent());
                }
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
            diff.put("diff", "D");
            diff.put("diffDesc", "删除");
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
            diff.put("diff", "C");
            diff.put("diffDesc", "新增");
            diff.put("memo", hv2.getMemo());
            if(withContent) {
                diff.put("content", hv2.getContent());
            }
            diffJsons.add(diff);
            j++;
        }
        return diffJsons;
    }

    @Override
    public JSONArray compareTwoVersion(String versionId, String versionId2) {
        List<HistoryVersion> hvs1 = historyVersionService.listHistoryByAppVersion(versionId);
        List<HistoryVersion> hvs2 = historyVersionService.listHistoryByAppVersion(versionId2);
        return compareTwoHVS(hvs1, hvs2, false);
    }

    private JSONArray innerCompareToOldVersion(String applicationId, String versionId, boolean withContent){
        List<HistoryVersion> hvs1 = historyVersionService.listHistoryByAppVersion(versionId);
        List<HistoryVersion> hvs2 = createHistoryVersions(applicationId);
        for(HistoryVersion hv : hvs2){
            hv.setHistorySha(
                Sha1Encoder.encodeBase64(hv.getContent().toJSONString(), true) );
        }
        return compareTwoHVS(hvs1, hvs2, withContent);
    }
    @Override
    public JSONArray compareToOldVersion(String applicationId, String versionId){
        return innerCompareToOldVersion(applicationId, versionId, false);
    }

    @Override
    public JSONArray listAppComponents(String appVersionId, String type, PageDesc pageDesc){
        if("1".equals(type)) { //"类型，1：工作流 2：页面设计 3：api网关"
            return DatabaseOptUtils.listObjectsBySqlAsJson(applicationVersionDao,
                "select h.history_id, h.relation_id, h.label, h.history_sha, h.os_id, h.type, " +
                    "f.FLOW_NAME, f.FLOW_DESC " +
                    "from history_version h join wf_flow_define f on (h.relation_id = f.FLOW_CODE and f.version =0) " +
                    "where h.type  = '1' and h.APP_VERSION_ID = ?",
                new Object[]{appVersionId}, pageDesc);
        }
        if("2".equals(type)) { //"类型，1：工作流 2：页面设计 3：api网关"
            return DatabaseOptUtils.listObjectsBySqlAsJson(applicationVersionDao,
                "select h.history_id, h.relation_id, h.label, h.history_sha, h.os_id, h.type, " +
                    "f.Model_Name, f.Model_Comment " +
                    "from history_version h join m_meta_form_model f on (h.relation_id = f.MODEL_ID) " +
                    "where h.type  = '2' and h.APP_VERSION_ID = ?",
                new Object[]{appVersionId}, pageDesc);
        }
        if("3".equals(type)) { //"类型，1：工作流 2：页面设计 3：api网关"
            return DatabaseOptUtils.listObjectsBySqlAsJson(applicationVersionDao,
                "select h.history_id, h.relation_id, h.label, h.history_sha, h.os_id, h.type, " +
                    "p.PACKET_NAME , p.PACKET_DESC " +
                    "from history_version h join q_data_packet p on (h.relation_id = p.PACKET_ID) " +
                    "where h.type  = '3' and h.APP_VERSION_ID = ?",
                new Object[]{appVersionId}, pageDesc);
        }
        return null;
    }

    private HistoryVersion createHistoryVersion(JSONObject jsonDiff, ApplicationVersion appVersion){
        HistoryVersion hv  = new HistoryVersion();
        hv.setOsId(appVersion.getApplicationId());
        hv.setRelationId(jsonDiff.getString("relationId"));
        hv.setType(jsonDiff.getString("type"));
        hv.setContent(jsonDiff.getJSONObject("content"));
        hv.setLabel("V_recovery_"+appVersion.getVersionId());
        hv.setMemo("因为恢复版本而创建的："+appVersion.getVersionId());//jsonDiff.getString("memo"));
        hv.setPushTime(DatetimeOpt.currentUtilDate());
        hv.setPushUser("system");
        //保存时 生成sha指纹
        hv.setHistorySha(
            Sha1Encoder.encodeBase64(hv.getContent().toJSONString(), true) );
        historyVersionService.createHistoryVersion(hv);
        return hv;
    }

    private HistoryVersion recoveryHistoryVersion(HistoryVersion hv , boolean isCreate){
        //HistoryVersion hv  =  historyVersionService.getHistoryVersion(jsonDiff.getString("historyId"));
        if("1".equals(hv.getType())){ //"类型，1：工作流 2：页面设计 3：api网关"
            if(isCreate) {
                Object obj = DatabaseOptUtils.getScalarObjectQuery(applicationVersionDao,
                    "select count(*) as hasFlow from wf_flow_define where FLOW_CODE= ? and version = 0",
                    new Object[]{hv.getRelationId()});
                if (NumberBaseOpt.castObjectToInteger(obj, 0) == 0) {
                    //insert into
                    return hv;
                }
            }
            JSONObject flowJson = hv.getContent();
            DatabaseOptUtils.doExecuteSql(applicationVersionDao,
                "update wf_flow_define set FLOW_XML_DESC = ?, FLOW_NAME =?, FLOW_DESC =?, FLOW_STATE='A' " +
                    " where FLOW_CODE= ? and version = 0",
                new Object[]{ JSON.toJSONString(flowJson),
                    flowJson.getString("flowName"),
                    flowJson.getString("flowDesc"),
                    hv.getRelationId()  }
            );
            return hv;
        }

        if("2".equals(hv.getType())){ //"类型，1：工作流 2：页面设计 3：api网关"
            // 恢复到 draft
            JSONObject modelJson = hv.getContent();
            DatabaseOptUtils.doExecuteSql( applicationVersionDao,
                "update m_meta_form_model_draft set IS_VALID = 'F', Model_Comment = ?, " +
                    "MOBILE_FORM_TEMPLATE = ? , form_template= ?, " +
                    "STRUCTURE_FUNCTION = ?, MODEL_TAG = ? " +
                    "where MODEL_ID = ?",
                new Object[]{ modelJson.getString("modelComment"),
                    modelJson.getString("mobileFormTemplate"),
                    modelJson.getString("formTemplate"),
                    modelJson.getString("structureFunction"),
                    modelJson.getString("modelTag"),
                    hv.getRelationId()  }
            );
            return hv;
        }
        if("3".equals(hv.getType())){ //"类型，1：工作流 2：页面设计 3：api网关"
            // 恢复到 draft
            JSONObject packetJson = hv.getContent();
            DatabaseOptUtils.doExecuteSql( applicationVersionDao,
                "update q_data_packet_draft set is_disable = 'F', task_type = ?," +
                    " schema_props = ?, return_type = ?, return_result = ?, request_body_type = ?," +
                    " PACKET_TYPE = ?, PACKET_NAME = ?, PACKET_DESC  = ?, " +
                    " interface_name = ?, has_data_opt = ?, " +
                    " EXT_PROPS  = ?, data_opt_desc_json = ?" +
                    " where PACKET_ID= ?",
                new Object[]{ packetJson.getString("taskType"),
                    packetJson.getString("schemaProps"),
                    packetJson.getString("returnType"),
                    packetJson.getString("returnResult"),
                    packetJson.getString("requestBodyType"),
                    packetJson.getString("packetType"),
                    packetJson.getString("packetName"),
                    packetJson.getString("packetDesc"),
                    packetJson.getString("interfaceName"),
                    packetJson.getString("hasDataOpt"),
                    packetJson.getString("extProps"),
                    packetJson.getString("dataOptDescJson"),
                    hv.getRelationId()  }
            );
        }

        return hv;
    }

    private HistoryVersion deleteHistoryVersion(JSONObject jsonDiff){
        HistoryVersion hv  =  historyVersionService.getHistoryVersion(jsonDiff.getString("historyId"));
        if("1".equals(hv.getType())){ //"类型，1：工作流 2：页面设计 3：api网关"
            DatabaseOptUtils.doExecuteSql( applicationVersionDao,
                // A 草稿 B 正常 C 过期 D 禁用  E 已发布
                "update wf_flow_define set FLOW_STATE='D' " +
                    " where FLOW_CODE= ? and version = 0",
                new Object[]{jsonDiff.getString("relationId") }
            );

        } else if("2".equals(hv.getType())){ //"类型，1：工作流 2：页面设计 3：api网关"
            // 恢复到 draft
            DatabaseOptUtils.doExecuteSql( applicationVersionDao,
                "update m_meta_form_model_draft set IS_VALID = 'T' " +
                    " where MODEL_ID = ?",
                new Object[]{
                    jsonDiff.getString("relationId") }
            );
            DatabaseOptUtils.doExecuteSql( applicationVersionDao,
                "update m_meta_form_model set IS_VALID = 'T' " +
                    " where MODEL_ID = ?",
                new Object[]{
                    jsonDiff.getString("relationId") }
            );
        } else if("3".equals(hv.getType())){ //"类型，1：工作流 2：页面设计 3：api网关"
            // 恢复到 draft
            DatabaseOptUtils.doExecuteSql( applicationVersionDao,
                "update q_data_packet_draft set is_disable = 'T' " +
                    " where PACKET_ID= ?",
                new Object[]{jsonDiff.getString("relationId") }
            );
            DatabaseOptUtils.doExecuteSql( applicationVersionDao,
                "update q_data_packet set is_disable = 'T' " +
                    " where PACKET_ID= ?",
                new Object[]{jsonDiff.getString("relationId") }
            );
        }

        return hv;
    }

    @Override
    public int restoreAppVersion(String appVersionId, String userCode) {
        ApplicationVersion appVersion = applicationVersionDao.getObjectById(appVersionId);
        JSONArray diff = innerCompareToOldVersion(appVersion.getApplicationId(), appVersionId, true);
        //恢复不同
        int mergeCount=0;
        for(Object obj : diff){
            if(obj instanceof JSONObject){
                JSONObject jsonDiff = (JSONObject) obj;

                AppMergeTask mergeTask = new AppMergeTask();
                mergeTask.setAppVersionId(appVersionId);
                mergeTask.setMergeStatus(false);
                mergeTask.setRelationId(jsonDiff.getString("relationId"));
                mergeTask.setObjectType(jsonDiff.getString("type"));
                mergeTask.setUpdateUser(userCode);

                if("D".equals(jsonDiff.getString("diff"))){
                    // 删除的，创建版本， 检查是否有删除状态的，如果有 先恢复
                    HistoryVersion hv  =  historyVersionService.getHistoryVersion(jsonDiff.getString("historyId"));
                    recoveryHistoryVersion(hv, true);
                    mergeTask.setMergeType("C");
                    mergeTask.setHistoryId(jsonDiff.getString("historyId"));
                    mergeTask.setMergeDesc("创建 - " + jsonDiff.getString("memo"));//
                } else if("C".equals(jsonDiff.getString("diff"))){
                    // createHistoryVersion(jsonDiff, appVersion);
                    // 新增的，修改为 删除
                    deleteHistoryVersion(jsonDiff);
                    mergeTask.setHistoryId(jsonDiff.getString("historyId2"));
                    mergeTask.setMergeDesc("删除 - " + jsonDiff.getString("memo"));
                    mergeTask.setMergeType("D");
                } else if("U".equals(jsonDiff.getString("diff"))){
                    createHistoryVersion(jsonDiff, appVersion);
                    // 更新的，创建版本，并恢复为旧版本
                    HistoryVersion hv  =  historyVersionService.getHistoryVersion(jsonDiff.getString("historyId"));
                    recoveryHistoryVersion(hv, false);
                    mergeTask.setHistoryId(jsonDiff.getString("historyId"));
                    mergeTask.setMergeDesc("更新 - " + jsonDiff.getString("memo"));
                    mergeTask.setMergeType("U");
                }
                appMergeTaskDao.saveNewObject(mergeTask);
                mergeCount++;
            }
        }
        if(mergeCount>0){
            applicationVersionDao.setRestoreStatus(appVersionId, "B");
        }
        return mergeCount;
    }

    private HistoryVersion createHistoryVersion(String objType, String objectId){
        //// A 草稿 B 正常 C 过期 D 禁用  E 已发布
        //查找应用相关的所有工作流，工作流的比较复杂，需要相关的版本和变量等信息
        if("1".equals(objType)) {
            JSONObject flow = DatabaseOptUtils.getObjectBySqlAsJson(applicationVersionDao,
                "select b.FLOW_CODE, b.version, b.FLOW_NAME, b.FLOW_CLASS, " +
                    "b.FLOW_STATE, b.FLOW_DESC, b.FLOW_XML_DESC, b.Time_Limit, b.Expire_Opt," +
                    "b.Opt_ID, b.OS_ID  " +
                    " from  wf_flow_define b  " +
                    " where b.FLOW_CODE = ? and b.version= 0 ",
                new Object[]{objectId});
            if (flow != null) {
                return createFlowHV(flow);
            }
        } else  if("2".equals(objType)) {
            //查找应用相关的所有页面
            JSONObject page = DatabaseOptUtils.getObjectBySqlAsJson(applicationVersionDao,
                "select MODEL_ID, Model_Name, OPT_ID, os_id, Model_Type," +
                    "Model_Comment, MOBILE_FORM_TEMPLATE, form_template," +
                    "STRUCTURE_FUNCTION, MODEL_TAG  " +
                    " from m_meta_form_model where MODEL_ID = ?" ,
                new Object[]{objectId});

            if (page != null) {
                return createPageHV(page);
            }
        } else  if("3".equals(objType)) {
            //查找应用相关的所有api
            JSONObject api = DatabaseOptUtils.getObjectBySqlAsJson(applicationVersionDao,
                "select task_type, task_Cron, SOURCE_ID, schema_props, " +
                    "return_type, return_result, request_body_type, " +
                    "PACKET_TYPE, PACKET_NAME, PACKET_ID, PACKET_DESC, " +
                    "os_id, OPT_ID, opt_code, need_rollback, " +
                    "is_disable, interface_name, has_data_opt, " +
                    "EXT_PROPS, data_opt_desc_json " +
                    "from q_data_packet where PACKET_ID = ? ",
                new Object[]{objectId});
            if (api != null) {
                return createApiHV(api);
            }
        }
        return null;
    }

    @Override
    public int mergeAppComponents(String appVersionId, JSONArray components, String userCode) {
        ApplicationVersion appVersion = applicationVersionDao.getObjectById(appVersionId);
        int mergeCount=0;
        for(Object obj : components){
            if(obj instanceof JSONObject) {
                JSONObject jsonDiff = (JSONObject) obj;
                HistoryVersion hv = historyVersionService.getHistoryVersion(jsonDiff.getString("historyId"));
                if(hv!=null){
                    AppMergeTask mergeTask = new AppMergeTask();
                    mergeTask.setAppVersionId(appVersionId);
                    mergeTask.setMergeStatus(false);
                    mergeTask.setRelationId(hv.getRelationId());
                    mergeTask.setObjectType(hv.getType());
                    mergeTask.setUpdateUser(userCode);

                    HistoryVersion hv2 = createHistoryVersion(hv.getType(), hv.getRelationId());
                    if(hv2==null){
                        recoveryHistoryVersion(hv, true);
                        mergeTask.setMergeType("C");
                        mergeTask.setHistoryId(hv.getHistoryId());
                        mergeTask.setMergeDesc("创建 - " + hv.getMemo());//
                    }else {
                        hv2.setOsId(appVersion.getApplicationId());
                        //hv2.setAppVersionId(appVersionId);
                        hv2.setHistorySha(
                            Sha1Encoder.encodeBase64(hv2.getContent().toJSONString(), true) );
                        if(! StringUtils.equals(hv.getHistorySha(), hv2.getHistorySha())){
                            //创建版本
                            hv2.setLabel("V_recovery_"+appVersion.getVersionId());
                            hv2.setMemo("因为恢复版本而创建的："+appVersion.getVersionId());//jsonDiff.getString("memo"));
                            hv2.setPushTime(DatetimeOpt.currentUtilDate());
                            hv2.setPushUser(userCode);
                            historyVersionService.createHistoryVersion(hv2);
                            // 更新的，创建版本，并恢复为旧版本
                            recoveryHistoryVersion(hv, false);
                            mergeTask.setHistoryId(jsonDiff.getString("historyId"));
                            mergeTask.setMergeDesc("更新 - " + hv.getMemo());
                            mergeTask.setMergeType("U");

                        }
                    }
                    if(StringUtils.isNotBlank(mergeTask.getMergeType())) {
                        appMergeTaskDao.saveNewObject(mergeTask);
                        mergeCount++;
                    }
                }

            }
        }
        if(mergeCount>0){
            applicationVersionDao.setRestoreStatus(appVersionId, "B");
        }
        return mergeCount;
    }

    @Override
    public List<AppMergeTask> listAppMergeTasks(String appVersionId, Map<String, Object> filterMap, PageDesc pageDesc) {
        filterMap.put("appVersionId", appVersionId);
        return appMergeTaskDao.listObjectsByProperties(filterMap ,pageDesc);
    }

    @Override
    public void mergeCompleted(AppMergeTask task) {
         appMergeTaskDao.markTaskComplete(task.getAppVersionId(), task.getRelationId());
    }

    @Override
    public void restoreCompleted(String appVersionId) {
        appMergeTaskDao.clearMergeTask(appVersionId);
        applicationVersionDao.setRestoreStatus(appVersionId, "A");
    }

}
