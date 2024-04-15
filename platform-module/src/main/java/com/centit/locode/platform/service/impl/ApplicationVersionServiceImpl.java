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
                "from q_data_packet where is_disable = 'F' and os_id = ? " +
                "order by PACKET_ID",
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
                "select h.history_id, h.relation_id, h.label, h.history_sha, h.os_id, " +
                    "f.FLOW_NAME, f.FLOW_DESC " +
                    "from history_version h join wf_flow_define f on (h.relation_id = f.FLOW_CODE and f.version =0) " +
                    "where h.type  = '1' and h.APP_VERSION_ID = ?",
                new Object[]{appVersionId}, pageDesc);
        }
        if("2".equals(type)) { //"类型，1：工作流 2：页面设计 3：api网关"
            return DatabaseOptUtils.listObjectsBySqlAsJson(applicationVersionDao,
                "select h.history_id, h.relation_id, h.label, h.history_sha, h.os_id, " +
                    "f.Model_Name, f.Model_Comment " +
                    "from history_version h join m_meta_form_model f on (h.relation_id = f.MODEL_ID) " +
                    "where h.type  = '2' and h.APP_VERSION_ID = ?",
                new Object[]{appVersionId}, pageDesc);
        }
        if("3".equals(type)) { //"类型，1：工作流 2：页面设计 3：api网关"
            return DatabaseOptUtils.listObjectsBySqlAsJson(applicationVersionDao,
                "select h.history_id, h.relation_id, h.label, h.history_sha, h.os_id, " +
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
        hv.setMemo(jsonDiff.getString("memo"));
        hv.setPushTime(DatetimeOpt.currentUtilDate());
        hv.setPushUser("system");
        hv.setLabel("因为恢复版本而创建的："+appVersion.getVersionId());
        //保存时 生成sha指纹
        hv.setHistorySha(
            Sha1Encoder.encodeBase64(hv.getContent().toJSONString(), true) );
        historyVersionService.createHistoryVersion(hv);
        return hv;
    }

    private HistoryVersion recoveryHistoryVersion(JSONObject jsonDiff, boolean isCreate){
        HistoryVersion hv  =  historyVersionService.getHistoryVersion(jsonDiff.getString("historyId"));
        if("1".equals(hv.getType())){ //"类型，1：工作流 2：页面设计 3：api网关"
            DatabaseOptUtils.doExecuteSql( applicationVersionDao,
                "update wf_flow_define set FLOW_XML_DESC = ?, FLOW_DESC =?, FLOW_STATE='A' " +
                    " where FLOW_CODE= ? and version = 0",
                new Object[]{ jsonDiff.getString("content"),
                    jsonDiff.getString("memo"),
                    jsonDiff.getString("relationId") }
            );
        }
        return hv;
    }

    @Override
    public int restoreAppVersion(String appVersionId) {
        ApplicationVersion appVersion = applicationVersionDao.getObjectById(appVersionId);
        JSONArray diff = innerCompareToOldVersion(appVersion.getApplicationId(), appVersionId, true);
        //恢复不同
        for(Object obj : diff){
            if(obj instanceof JSONObject){
                JSONObject jsonDiff = (JSONObject) obj;
                if("D".equals(jsonDiff.getString("diff"))){
                    // 删除的，创建版本， 检查是否有删除状态的，如果有 先恢复

                } else if("C".equals(jsonDiff.getString("diff"))){
                    // 新增的，修改为 删除

                } else if("U".equals(jsonDiff.getString("diff"))){
                    // 更新的，创建版本，并恢复为旧版本
                }
            }
        }
        return diff.size();
    }

    @Override
    public int mergeAppComponents(String appVersionId, JSONArray components) {

        return components.size();
    }

    @Override
    public List<AppMergeTask> listAppMergeTasks(String appVersionId, PageDesc pageDesc) {
        return appMergeTaskDao.listObjectsByProperties(
            CollectionsOpt.createHashMap("appVersionId", appVersionId) ,pageDesc);
    }

    @Override
    public void restoreCompleted(AppMergeTask task) {

    }

    @Override
    public int mergeCompleted(AppMergeTask task) {
        return 0;
    }

}
