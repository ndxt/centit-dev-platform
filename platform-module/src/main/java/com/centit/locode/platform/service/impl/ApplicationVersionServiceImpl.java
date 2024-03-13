package com.centit.locode.platform.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.dde.adapter.dao.DataPacketDao;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.OsInfo;
import com.centit.locode.platform.dao.ApplicationVersionDao;
import com.centit.locode.platform.dao.HistoryVersionDao;
import com.centit.locode.platform.po.ApplicationVersion;
import com.centit.locode.platform.po.HistoryVersion;
import com.centit.locode.platform.service.ApplicationVersionService;
import com.centit.locode.platform.service.HistoryVersionService;
import com.centit.locode.platform.service.ModelExportManager;
import com.centit.metaform.dao.MetaFormModelDao;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.UuidOpt;
import com.centit.support.common.ObjectException;
import com.centit.support.database.utils.PageDesc;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
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
        //查找应用相关的所有页面
        JSONArray pages = DatabaseOptUtils.listObjectsBySqlAsJson(applicationVersionDao,
            "select MODEL_ID, Model_Name, OPT_ID, os_id, Model_Type," +
                "last_modify_Date, Recorder, Model_Comment, MOBILE_FORM_TEMPLATE, form_template," +
                "publish_date, SOURCE_ID, STRUCTURE_FUNCTION, MODEL_TAG, IS_VALID " +
                " from m_meta_form_model where os_id = ?",
            new Object[]{applicationVersion.getApplicationId()});

        if(pages!=null){
            for(Object obj : pages){
                if(obj instanceof JSONObject) {
                    JSONObject jsonObject = (JSONObject) obj;
                    HistoryVersion hv = new HistoryVersion();
                    hv.setAppVersionId(versionId);
                    hv.setType("2");
                    hv.setRelationId(jsonObject.getString("modelId"));
                    hv.setOsId(applicationVersion.getApplicationId());
                    hv.setPushTime(currentTime);
                    hv.setPushUser("system");
                    hv.setLabel(versionId);
                    hv.setMemo("全局版本："+versionId);
                    mapJsonProperties(jsonObject, "formTemplate", "mobileFormTemplate", "structureFunction");
                    hv.setContent(jsonObject);
                    historyVersionService.createHistoryVersion(hv);
                }
            }
        }
        //查找应用相关的所有api
        JSONArray apis = DatabaseOptUtils.listObjectsBySqlAsJson(applicationVersionDao,
            "select update_date, task_type, task_Cron, SOURCE_ID, schema_props, " +
                "return_type, return_result, request_body_type, Recorder, Record_Date, " +
                "publish_date, PACKET_TYPE, PACKET_NAME, PACKET_ID, PACKET_DESC, " +
                "Owner_Type, Owner_Code, os_id, OPT_ID, opt_code, " +
                "next_run_time, need_rollback, log_level, last_run_time, is_while, " +
                "is_valid, is_disable, interface_name, has_data_opt, FALL_BACK_LEVEL, " +
                "EXT_PROPS, data_opt_desc_json, buffer_fresh_period_type, BUFFER_FRESH_PERIOD" +
                " from q_data_packet where os_id = ?",
            new Object[]{applicationVersion.getApplicationId()});

        if(apis!=null){
            for(Object obj : apis){
                if(obj instanceof JSONObject) {
                    JSONObject jsonObject = (JSONObject) obj;
                    HistoryVersion hv = new HistoryVersion();
                    hv.setAppVersionId(versionId);
                    hv.setType("3");
                    hv.setRelationId(jsonObject.getString("packetId"));
                    hv.setOsId(applicationVersion.getApplicationId());
                    hv.setPushTime(currentTime);
                    hv.setPushUser("system");
                    hv.setLabel(versionId);
                    hv.setMemo("全局版本："+versionId);
                    //dataOptDescJson returnResult extProps schemaProps
                    mapJsonProperties(jsonObject, "dataOptDescJson","returnResult","extProps","schemaProps");
                    hv.setContent(jsonObject);
                    historyVersionService.createHistoryVersion(hv);
                }
            }
        }
        //查找应用相关的所有工作流，工作流的比较复杂，需要相关的版本和变量等信息
        JSONArray flows = DatabaseOptUtils.listObjectsBySqlAsJson(applicationVersionDao,
            "select b.FLOW_CODE, b.version, b.FLOW_NAME, b.FLOW_CLASS, b.FLOW_Publish_Date," +
                "b.FLOW_STATE, b.FLOW_DESC, b.FLOW_XML_DESC, b.Time_Limit, b.Expire_Opt," +
                "b.Opt_ID, b.AT_PUBLISH_DATE, b.OS_ID, b.FIRST_NODE_ID, b.SOURCE_ID " +
                " from " +
                " (select FLOW_CODE, max(version) as version from wf_flow_define where os_id = ? group by flow_code) a " +
                " join wf_flow_define b on (a.flow_code=b.flow_code and a.version = b.version)",
            new Object[]{applicationVersion.getApplicationId()});
        if(flows!=null){
            for(Object obj : flows){
                if(obj instanceof JSONObject) {
                    JSONObject jsonObject = (JSONObject) obj;
                    HistoryVersion hv = new HistoryVersion();
                    hv.setAppVersionId(versionId);
                    hv.setType("1");
                    hv.setRelationId(jsonObject.getString("flowCode"));
                    hv.setOsId(applicationVersion.getApplicationId());
                    hv.setPushTime(currentTime);
                    hv.setPushUser("system");
                    hv.setLabel(versionId);
                    hv.setMemo("全局版本："+versionId);
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
                    historyVersionService.createHistoryVersion(hv);
                }
            }
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

    }

    @Override
    public void deleteApplicationVersion(String versionId) {

    }

    @Override
    public List<ApplicationVersion> listApplicationVersion(String applicationId, PageDesc pageDesc) {
        return null;
    }

    @Override
    public ApplicationVersion getApplicationVersion(String versionId) {
        return null;
    }

    @Override
    public InputStream exportApplicationVersion(String versionId) {
        return null;
    }

    @Override
    public void importApplicationVersion(InputStream inputStream) {

    }

    @Override
    public void restoreApplicationVersion(String versionId) {

    }

}
