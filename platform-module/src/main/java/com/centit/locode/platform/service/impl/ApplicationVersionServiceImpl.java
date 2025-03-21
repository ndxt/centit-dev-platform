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
import com.centit.support.algorithm.*;
import com.centit.support.common.ObjectException;
import com.centit.support.database.utils.PageDesc;
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

    public static void mapJsonProperties(JSONObject json, String... props) {
        for (String p : props) {
            String sp = json.getString(p);
            if (StringUtils.isNotBlank(sp)) {
                json.put(p, JSON.parse(sp));
            }
        }
    }

    private HistoryVersion createFlowHV(JSONObject jsonObject) {
        HistoryVersion hv = new HistoryVersion();
        hv.setHistoryId(UuidOpt.getUuidAsString22());
        hv.setType("1");
        hv.setRelationId(jsonObject.getString("flowCode"));
        hv.setMemo(jsonObject.getString("flowName"));
        //dataOptDescJson returnResult extProps schemaProps
        JSONObject jsonContent = JSON.parseObject(jsonObject.getString("flowXmlDesc"));
        if (jsonContent != null) {
            jsonContent.put("optId", jsonObject.getString("optId"));
            jsonContent.put("flowCode", jsonObject.getString("flowCode"));
            jsonContent.put("version", jsonObject.getString("version"));
            jsonContent.put("flowName", jsonObject.getString("flowName"));
            jsonContent.put("flowDesc", jsonObject.getString("flowDesc"));
            jsonContent.put("flowClass", jsonObject.getString("flowClass"));
        } else {
            jsonContent = jsonObject;
        }
        hv.setContent(jsonContent);
        return hv;
    }

    private HistoryVersion createPageHV(JSONObject jsonObject) {
        HistoryVersion hv = new HistoryVersion();
        hv.setHistoryId(UuidOpt.getUuidAsString22());
        hv.setType("2");
        hv.setRelationId(jsonObject.getString("modelId"));
        hv.setMemo(jsonObject.getString("modelName"));
        mapJsonProperties(jsonObject, "formTemplate", "mobileFormTemplate", "structureFunction");
        hv.setPushUser(jsonObject.getString("recorder"));
        hv.setContent(jsonObject);
        return hv;
    }

    private HistoryVersion createApiHV(JSONObject jsonObject) {
        HistoryVersion hv = new HistoryVersion();
        hv.setHistoryId(UuidOpt.getUuidAsString22());
        hv.setType("3");
        hv.setRelationId(jsonObject.getString("packetId"));
        hv.setMemo(jsonObject.getString("packetName"));
        //dataOptDescJson returnResult extProps schemaProps
        mapJsonProperties(jsonObject, "dataOptDescJson", "returnResult", "extProps", "schemaProps");
        hv.setPushUser(jsonObject.getString("recorder"));
        hv.setContent(jsonObject);
        return hv;
    }


    private HistoryVersion createHistoryVersion(String objType, String objectId) {
        //// A 草稿 B 正常 C 过期 D 禁用  E 已发布
        //查找应用相关的所有工作流，工作流的比较复杂，需要相关的版本和变量等信息
        if ("1".equals(objType)) {
            JSONObject flow = DatabaseOptUtils.getObjectBySqlAsJson(applicationVersionDao,
                "select b.FLOW_CODE, b.version, b.FLOW_NAME, b.FLOW_CLASS, " +
                    "b.FLOW_STATE, b.FLOW_DESC, b.FLOW_XML_DESC, b.Time_Limit, b.Expire_Opt," +
                    "b.Opt_ID, b.OS_ID, b.SOURCE_ID, b.EXPIRE_CALL_API, b.Warning_Param  " +
                    " from  wf_flow_define b  " +
                    " where b.FLOW_CODE = ? and b.version= 0 ",
                new Object[]{objectId});
            if (flow != null) {
                return createFlowHV(flow);
            }
        } else if ("2".equals(objType)) {
            //查找应用相关的所有页面
            JSONObject page = DatabaseOptUtils.getObjectBySqlAsJson(applicationVersionDao,
                "select MODEL_ID, Model_Name, OPT_ID, os_id, Model_Type," +
                    " Recorder, Model_Comment, MOBILE_FORM_TEMPLATE, form_template," +
                    "STRUCTURE_FUNCTION, SOURCE_ID, MODEL_TAG  " +
                    " from m_meta_form_model where MODEL_ID = ?",
                new Object[]{objectId});

            if (page != null) {
                return createPageHV(page);
            }
        } else if ("3".equals(objType)) {
            //查找应用相关的所有api
            JSONObject api = DatabaseOptUtils.getObjectBySqlAsJson(applicationVersionDao,
                "select task_type, task_Cron, SOURCE_ID, schema_props, " +
                    "return_type, return_result, request_body_type, Recorder, " +
                    "PACKET_TYPE, PACKET_NAME, PACKET_ID, PACKET_DESC, FALL_BACK_LEVEL, " +
                    "os_id, OPT_ID, opt_code, need_rollback, Owner_Type, Owner_Code, " +
                    "is_disable, interface_name, has_data_opt, BUFFER_FRESH_PERIOD, buffer_fresh_period_type, " +
                    "EXT_PROPS, data_opt_desc_json, FALL_BACK_LEVEL, log_level " +
                    "from q_data_packet where PACKET_ID = ? ",
                new Object[]{objectId});
            if (api != null) {
                return createApiHV(api);
            }
        }
        return null;
    }

    private List<HistoryVersion> createHistoryVersions(String osId) {
        List<HistoryVersion> hvs = new ArrayList<>(100);
        //// A 草稿 B 正常 C 过期 D 禁用  E 已发布
        //查找应用相关的所有工作流，工作流的比较复杂，需要相关的版本和变量等信息
        JSONArray flows = DatabaseOptUtils.listObjectsBySqlAsJson(applicationVersionDao,
            "select b.FLOW_CODE, b.version, b.FLOW_NAME, b.FLOW_CLASS, " +
                " b.FLOW_STATE, b.FLOW_DESC, b.FLOW_XML_DESC, b.Time_Limit, b.Expire_Opt," +
                " b.Opt_ID, b.OS_ID, b.SOURCE_ID, b.EXPIRE_CALL_API, b.Warning_Param " +
                " from " +
                " (select FLOW_CODE, max(version) as version from wf_flow_define where os_id = ? group by flow_code) a " +
                " join wf_flow_define b on (a.flow_code=b.flow_code and a.version = b.version) " +
                " where b.FLOW_STATE = 'B' " +
                " order by b.flow_code",
            new Object[]{osId});
        if (flows != null) {
            for (Object obj : flows) {
                if (obj instanceof JSONObject) {
                    hvs.add(createFlowHV((JSONObject) obj));
                }
            }
        }
        //查找应用相关的所有页面
        JSONArray pages = DatabaseOptUtils.listObjectsBySqlAsJson(applicationVersionDao,
            "select MODEL_ID, Model_Name, OPT_ID, os_id, Model_Type," +
                " Recorder, Model_Comment, MOBILE_FORM_TEMPLATE, form_template," +
                " STRUCTURE_FUNCTION, SOURCE_ID, MODEL_TAG  " +
                " from m_meta_form_model where IS_VALID = 'F' and os_id = ?" +
                " order by MODEL_ID",
            new Object[]{osId});

        if (pages != null) {
            for (Object obj : pages) {
                if (obj instanceof JSONObject) {
                    hvs.add(createPageHV((JSONObject) obj));
                }
            }
        }
        //查找应用相关的所有api
        JSONArray apis = DatabaseOptUtils.listObjectsBySqlAsJson(applicationVersionDao,
            "select task_type, task_Cron, SOURCE_ID, schema_props, " +
                " return_type, return_result, request_body_type, Recorder, " +
                " PACKET_TYPE, PACKET_NAME, PACKET_ID, PACKET_DESC, FALL_BACK_LEVEL, " +
                " os_id, OPT_ID, opt_code, need_rollback, Owner_Type, Owner_Code, " +
                " is_disable, interface_name, has_data_opt, BUFFER_FRESH_PERIOD, buffer_fresh_period_type, " +
                " EXT_PROPS, data_opt_desc_json, FALL_BACK_LEVEL, log_level " +
                " from q_data_packet where is_disable = 'F' and os_id = ? " +
                " order by PACKET_ID",
            new Object[]{osId});

        if (apis != null) {
            for (Object obj : apis) {
                if (obj instanceof JSONObject) {
                    hvs.add(createApiHV((JSONObject) obj));
                }
            }
        }

        return hvs;
    }

    /**
     * 直接从数据库中获取 页面、api 和工作流引擎数据（工作流引擎引用最新版本的）
     *
     * @param applicationVersion 版本信息
     */
    @Override
    public String createApplicationVersion(ApplicationVersion applicationVersion) {
        if (StringUtils.isBlank(applicationVersion.getApplicationId())) {
            throw new ObjectException(ObjectException.DATA_VALIDATE_ERROR, "没有指定关联应用信息！");
        }
        OsInfo osInfo = platformEnvironment.getOsInfo(applicationVersion.getApplicationId());
        if (osInfo == null) {
            throw new ObjectException(ObjectException.DATA_VALIDATE_ERROR, "关联应用信息不能存在！");
        }
        //需要先导出，变动sourceId后再保存版本
        String fileId = "";
        Exception exception = null;
        try {
            fileId = modelExportManager.exportModelAndSaveToFileServer(osInfo);
        } catch (IOException e) {
            exception = e;
        }
        //保存版本信息
        String versionId = UuidOpt.getUuidAsString22();
        applicationVersion.setVersionId(versionId);
        Date currentTime = DatetimeOpt.currentUtilDate();
        applicationVersion.setDateCreated(currentTime);
        List<HistoryVersion> hvs = createHistoryVersions(applicationVersion.getApplicationId());
        for (HistoryVersion hv : hvs) {
            hv.setAppVersionId(versionId);
            hv.setOsId(applicationVersion.getApplicationId());
            hv.setPushTime(currentTime);
            hv.setPushUser("system");
            hv.setLabel("全局版本：" + versionId);
            //保存时 生成sha指纹
            hv.setHistorySha(hv.generateHistorySha());
            historyVersionService.createHistoryVersion(hv);
        }
        // 生成 并上传 应用导出包 (zip文件）上传到文件服务器并返回 fileId
        applicationVersion.setBackupFileId(fileId);
        applicationVersionDao.saveNewObject(applicationVersion);
        if (exception != null) {
            throw new RuntimeException("创建应用版本完成，但是上传应用快照失败：" + exception.getMessage(), exception);
        }
        return versionId;
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
    public boolean checkMergeState(String applicationId) {
        return applicationVersionDao.countObjectByProperties(
            CollectionsOpt.createHashMap("applicationId", applicationId,
                "mergeStatus", ApplicationVersion.VERSION_MERGE_STATUS_MERGING)) > 0; //"B"
    }

    @Override
    public List<ApplicationVersion> listApplicationVersion(String applicationId, PageDesc pageDesc, Map<String, Object> filterMap) {
        filterMap.put("applicationId", applicationId);
        List<ApplicationVersion> versionList = applicationVersionDao.listObjectsByProperties(
            filterMap,
            pageDesc);
        boolean isInMerging=false;
        if(versionList!=null) {
            for (ApplicationVersion version : versionList) {
                if(ApplicationVersion.VERSION_MERGE_STATUS_MERGING.equals(version.getMergeStatus())){
                    isInMerging = true;
                    break;
                }
            }
        }
        // 始终添加 合并中的 对象
        if(!isInMerging) {
            ApplicationVersion mergeObj = applicationVersionDao.getObjectByProperties(
                CollectionsOpt.createHashMap("mergeStatus", ApplicationVersion.VERSION_MERGE_STATUS_MERGING));
            if(mergeObj!=null) {
                List<ApplicationVersion> versionList2 = new ArrayList<>();
                versionList2.add(mergeObj);
                if(versionList!=null) {
                    versionList2.addAll(versionList);
                }
                return versionList2;
            }

        }
        return versionList;
    }

    @Override
    public ApplicationVersion getApplicationVersion(String versionId) {
        return applicationVersionDao.getObjectById(versionId);
    }

    private JSONArray compareTwoHVS(List<HistoryVersion> hvs1, List<HistoryVersion> hvs2, boolean withContent) {
        int len1 = hvs1 == null ? 0 : hvs1.size();
        int len2 = hvs2 == null ? 0 : hvs2.size();
        if (len1 > 0) hvs1.sort(HistoryVersion::compareTo);
        if (len2 > 0) hvs2.sort(HistoryVersion::compareTo);

        int i = 0, j = 0;
        JSONArray diffJsons = new JSONArray();
        while (i < len1 && j < len2) {
            HistoryVersion hv1 = hvs1.get(i);
            HistoryVersion hv2 = hvs2.get(j);
            if (hv1.compareTo(hv2) == 0) {
                if (!StringUtils.equals(hv1.getHistorySha(), hv2.getHistorySha())) {
                    JSONObject diff = new JSONObject();
                    diff.put("type", hv1.getType());
                    diff.put("typeDesc", hv1.getTypeDesc());
                    diff.put("relationId", hv1.getRelationId());
                    diff.put("historyId", hv1.getHistoryId());
                    diff.put("sha", hv1.getHistorySha());
                    diff.put("historyId2", hv2.getHistoryId());
                    diff.put("sha2", hv2.getHistorySha());
                    diff.put("diff", "U");
                    diff.put("diffDesc", "更改");
                    diff.put("memo", hv1.getMemo());
                    diff.put("lastUpdateUser", StringUtils.isBlank(hv2.getPushUser())? hv1.getPushUser(): hv2.getPushUser());
                    if (withContent) {
                        diff.put("content", hv2.getContent());
                    }
                    diffJsons.add(diff);
                }
                i++;
                j++;
            } else if (hv1.compareTo(hv2) < 0) {
                JSONObject diff = new JSONObject();
                diff.put("type", hv1.getType());
                diff.put("typeDesc", hv1.getTypeDesc());
                diff.put("relationId", hv1.getRelationId());
                diff.put("historyId", hv1.getHistoryId());
                diff.put("sha", hv1.getHistorySha());
                diff.put("diff", "D");
                diff.put("diffDesc", "删除");
                diff.put("memo", hv1.getMemo());
                diff.put("lastUpdateUser", hv1.getPushUser());
                diffJsons.add(diff);
                i++;
            } else {
                JSONObject diff = new JSONObject();
                diff.put("type", hv2.getType());
                diff.put("typeDesc", hv2.getTypeDesc());
                diff.put("relationId", hv2.getRelationId());
                diff.put("historyId2", hv2.getHistoryId());
                diff.put("sha2", hv2.getHistorySha());
                diff.put("diff", "C");
                diff.put("diffDesc", "新增");
                diff.put("memo", hv2.getMemo());
                if (withContent) {
                    diff.put("content", hv2.getContent());
                }
                diff.put("lastUpdateUser", hv2.getPushUser());
                diffJsons.add(diff);
                j++;
            }
        }

        while (i < len1) {
            HistoryVersion hv1 = hvs1.get(i);
            JSONObject diff = new JSONObject();
            diff.put("type", hv1.getType());
            diff.put("typeDesc", hv1.getTypeDesc());
            diff.put("relationId", hv1.getRelationId());
            diff.put("historyId", hv1.getHistoryId());
            diff.put("sha", hv1.getHistorySha());
            diff.put("diff", "D");
            diff.put("diffDesc", "删除");
            diff.put("memo", hv1.getMemo());
            diff.put("lastUpdateUser", hv1.getPushUser());
            diffJsons.add(diff);
            i++;
        }

        while (j < len2) {
            HistoryVersion hv2 = hvs2.get(j);
            JSONObject diff = new JSONObject();
            diff.put("type", hv2.getType());
            diff.put("typeDesc", hv2.getTypeDesc());
            diff.put("relationId", hv2.getRelationId());
            diff.put("historyId2", hv2.getHistoryId());
            diff.put("sha2", hv2.getHistorySha());
            diff.put("diff", "C");
            diff.put("diffDesc", "新增");
            diff.put("memo", hv2.getMemo());
            if (withContent) {
                diff.put("content", hv2.getContent());
            }
            diff.put("lastUpdateUser", hv2.getPushUser());
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

    private JSONArray innerCompareToOldVersion(String applicationId, String versionId, boolean withContent) {
        List<HistoryVersion> hvs1 = historyVersionService.listHistoryByAppVersion(versionId);
        List<HistoryVersion> hvs2 = createHistoryVersions(applicationId);
        for (HistoryVersion hv : hvs2) {
            hv.setHistorySha(hv.generateHistorySha());
        }
        return compareTwoHVS(hvs1, hvs2, withContent);
    }

    @Override
    public JSONArray compareToOldVersion(String applicationId, String versionId) {
        return innerCompareToOldVersion(applicationId, versionId, false);
    }

    @Override
    public JSONArray listAppComponents(String appVersionId, String type, PageDesc pageDesc) {
        if ("1".equals(type)) { //"类型，1：工作流 2：页面设计 3：api网关"
            return DatabaseOptUtils.listObjectsBySqlAsJson(applicationVersionDao,
                "select h.history_id, h.relation_id, h.label, h.history_sha, h.os_id, h.type, " +
                    "f.FLOW_NAME, f.FLOW_DESC, o.OPT_NAME " +
                    "from history_version h join wf_flow_define f on (h.relation_id = f.FLOW_CODE and f.version =0) " +
                    "left join F_OPTINFO o on (f.OPT_ID = o.OPT_ID) " + //, o.OPT_NAME
                    "where h.type  = '1' and h.APP_VERSION_ID = ?",
                new Object[]{appVersionId}, pageDesc);
        }
        if ("2".equals(type)) { //"类型，1：工作流 2：页面设计 3：api网关"
            return DatabaseOptUtils.listObjectsBySqlAsJson(applicationVersionDao,
                "select h.history_id, h.relation_id, h.label, h.history_sha, h.os_id, h.type, " +
                    "f.Model_Name, f.Model_Comment, o.OPT_NAME  " +
                    "from history_version h join m_meta_form_model f on (h.relation_id = f.MODEL_ID) " +
                    "left join F_OPTINFO o on (f.OPT_ID = o.OPT_ID) " + //, o.OPT_NAME
                    "where h.type = '2' and h.APP_VERSION_ID = ?",
                new Object[]{appVersionId}, pageDesc);
        }
        if ("3".equals(type)) { //"类型，1：工作流 2：页面设计 3：api网关"
            return DatabaseOptUtils.listObjectsBySqlAsJson(applicationVersionDao,
                "select h.history_id, h.relation_id, h.label, h.history_sha, h.os_id, h.type, " +
                    "p.PACKET_NAME, p.PACKET_DESC, o.OPT_NAME  " +
                    "from history_version h join q_data_packet p on (h.relation_id = p.PACKET_ID) " +
                    "left join F_OPTINFO o on (p.OPT_ID = o.OPT_ID) " + //, o.OPT_NAME
                    "where h.type  = '3' and h.APP_VERSION_ID = ?",
                new Object[]{appVersionId}, pageDesc);
        }
        return null;
    }

    private HistoryVersion createHistoryVersion(JSONObject jsonDiff, ApplicationVersion appVersion) {
        HistoryVersion hv = new HistoryVersion();
        hv.setOsId(appVersion.getApplicationId());
        hv.setRelationId(jsonDiff.getString("relationId"));
        hv.setType(jsonDiff.getString("type"));
        hv.setContent(jsonDiff.getJSONObject("content"));
        hv.setLabel("V_recovery_" + appVersion.getVersionId());
        hv.setMemo("因为恢复版本而创建的：" + appVersion.getVersionId());//jsonDiff.getString("memo"));
        hv.setPushTime(DatetimeOpt.currentUtilDate());
        hv.setPushUser("system");
        //保存时 生成sha指纹
        hv.setHistorySha(hv.generateHistorySha());
        historyVersionService.createHistoryVersion(hv);
        return hv;
    }

    private void recoveryHistoryVersion(HistoryVersion hv) {
        //HistoryVersion hv  =  historyVersionService.getHistoryVersion(jsonDiff.getString("historyId"));
        if ("1".equals(hv.getType())) { //"类型，1：工作流 2：页面设计 3：api网关"
            JSONObject flowJson = hv.getContent();
            Object obj = DatabaseOptUtils.getScalarObjectQuery(applicationVersionDao,
                "select count(*) as hasFlow from wf_flow_define where FLOW_CODE= ? and version = 0",
                new Object[]{hv.getRelationId()});
            if (NumberBaseOpt.castObjectToInteger(obj, 0) == 0) {
                flowJson.put("flowXmlDesc",flowJson.toString());
                flowJson.put("flowClass", StringBaseOpt.castObjectToString(flowJson.get("flowClass"),"R"));
                DatabaseOptUtils.doExecuteNamedSql(applicationVersionDao,
                    "insert into wf_flow_define (FLOW_CODE, version, FLOW_XML_DESC, FLOW_NAME, FLOW_DESC, FLOW_STATE, " +
                        " Time_Limit, Expire_Opt, Opt_ID, OS_ID, SOURCE_ID, EXPIRE_CALL_API, Warning_Param,flow_class )" +
                        " values ( :flowCode, 0, :flowXmlDesc, :flowName, :flowDesc, 'A'," +
                        ":timeLimit, :expireOpt, :optId, :osId, :sourceId, :expireCallApi, :warningParam,:flowClass)",
                    flowJson);
                return;
            }
            DatabaseOptUtils.doExecuteSql(applicationVersionDao,
                "update wf_flow_define set FLOW_XML_DESC = ?, FLOW_NAME =?, FLOW_DESC =?, FLOW_STATE='A' " +
                    " where FLOW_CODE= ? and version = 0",
                new Object[]{JSON.toJSONString(flowJson),
                    flowJson.getString("flowName"),
                    flowJson.getString("flowDesc"),
                    hv.getRelationId()}
            );
            return;
        }

        if ("2".equals(hv.getType())) { //"类型，1：工作流 2：页面设计 3：api网关"
            // 恢复到 draft
            JSONObject modelJson = hv.getContent();
            Object obj = DatabaseOptUtils.getScalarObjectQuery(applicationVersionDao,
                "select count(*) as hasPage from m_meta_form_model_draft where MODEL_ID= ?",
                new Object[]{hv.getRelationId()});
            if (NumberBaseOpt.castObjectToInteger(obj, 0) == 0) {
                modelJson.put("modelId", hv.getRelationId());
                modelJson.put("lastModifyDate", DatetimeOpt.currentUtilDate());
                modelJson.put("mobileFormTemplate", modelJson.getString("mobileFormTemplate"));
                modelJson.put("formTemplate", modelJson.getString("formTemplate"));
                modelJson.put("structureFunction", modelJson.getString("structureFunction"));
                DatabaseOptUtils.doExecuteNamedSql(applicationVersionDao,
                    "insert into m_meta_form_model_draft (MODEL_ID, Model_Name, OPT_ID, os_id, Model_Type, " +
                        "last_modify_Date, Recorder, Model_Comment, MOBILE_FORM_TEMPLATE, form_template, " +
                        " SOURCE_ID, STRUCTURE_FUNCTION, MODEL_TAG, IS_VALID )" +
                        " values ( :modelId, :modelName, :optId, :osId, :modelType, " +
                        " :lastModifyDate, :recorder, :modelComment, :mobileFormTemplate, :formTemplate," +
                        " :sourceId, :structureFunction, :modelTag, 'F' )",
                    modelJson);
                return;
            }

            DatabaseOptUtils.doExecuteSql(applicationVersionDao,
                "update m_meta_form_model_draft set IS_VALID = 'F', Model_Comment = ?, " +
                    "MOBILE_FORM_TEMPLATE = ? , form_template= ?, " +
                    "STRUCTURE_FUNCTION = ?, MODEL_TAG = ? " +
                    "where MODEL_ID = ?",
                new Object[]{modelJson.getString("modelComment"),
                    modelJson.getString("mobileFormTemplate"),
                    modelJson.getString("formTemplate"),
                    modelJson.getString("structureFunction"),
                    modelJson.getString("modelTag"),
                    hv.getRelationId()});
            return;
        }
        if ("3".equals(hv.getType())) { //"类型，1：工作流 2：页面设计 3：api网关"
            // 恢复到 draft
            JSONObject packetJson = hv.getContent();
            Object obj = DatabaseOptUtils.getScalarObjectQuery(applicationVersionDao,
                "select count(*) as hasApi from q_data_packet_draft where PACKET_ID= ?",
                new Object[]{hv.getRelationId()});
            if (NumberBaseOpt.castObjectToInteger(obj, 0) == 0) {
                packetJson.put("packetId", hv.getRelationId());
                packetJson.put("recordDate", DatetimeOpt.currentUtilDate());
                packetJson.put("updateDate", DatetimeOpt.currentUtilDate());
                packetJson.put("returnResult", packetJson.getString("returnResult"));
                packetJson.put("extProps", packetJson.getString("extProps"));
                packetJson.put("dataOptDescJson", packetJson.getString("dataOptDescJson"));
                packetJson.put("schemaProps", packetJson.getString("schemaProps"));
                DatabaseOptUtils.doExecuteNamedSql(applicationVersionDao,
                    "insert into q_data_packet_draft " +
                        "(PACKET_ID, os_id, Owner_Type, Owner_Code, PACKET_NAME, " +
                        " PACKET_TYPE, PACKET_DESC, Recorder, Record_Date, has_data_opt, " +
                        " data_opt_desc_json, task_type, task_Cron, " +
                        " is_valid, interface_name, return_type, return_result, update_date," +
                        " need_rollback, OPT_ID, EXT_PROPS, opt_code, " +
                        " BUFFER_FRESH_PERIOD, buffer_fresh_period_type, log_level," +
                        " is_disable, schema_props, request_body_type, FALL_BACK_LEVEL )" +
                        //--------------------------------------------------------------------//
                        " values (:packetId, :osId, :ownerType, :ownerCode, :packetName," +
                        " :packetType, :packetDesc, :recorder, :recordDate, :hasDataOpt," +
                        " :dataOptDescJson, :taskType, :taskCron, " +
                        " 'T', :interfaceName, :returnType, :returnResult, :updateDate, " +
                        " :needRollback, :optId, :extProps, :optCode," +
                        " :bufferFreshPeriod, :bufferFreshPeriodType, :logLevel, " +
                        " 'F', :schemaProps, :requestBodyType, :fallBackLevel )",
                    packetJson);
                return;
            }
            DatabaseOptUtils.doExecuteSql(applicationVersionDao,
                "update q_data_packet_draft set is_disable = 'F', task_type = ?," +
                    " schema_props = ?, return_type = ?, return_result = ?, request_body_type = ?," +
                    " PACKET_TYPE = ?, PACKET_NAME = ?, PACKET_DESC  = ?, " +
                    " interface_name = ?, has_data_opt = ?, " +
                    " EXT_PROPS  = ?, data_opt_desc_json = ? " +
                    " where PACKET_ID= ?",
                new Object[]{packetJson.getString("taskType"),
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
                    hv.getRelationId()}
            );
        }
    }

    private void changeRelationObjectState(String objType, String relationId, boolean toGarbage) {
        if ("1".equals(objType)) { //"类型，1：工作流 2：页面设计 3：api网关"
            DatabaseOptUtils.doExecuteSql(applicationVersionDao,
                // A 草稿 B 正常 C 过期 D 禁用  E 已发布
                "update wf_flow_define set FLOW_STATE= " + (toGarbage ? "'D'" : "'B'") +
                    " where FLOW_CODE= ? and version = 0",
                new Object[]{relationId}
            );

        } else if ("2".equals(objType)) { //"类型，1：工作流 2：页面设计 3：api网关"
            // 恢复到 draft
            DatabaseOptUtils.doExecuteSql(applicationVersionDao,
                "update m_meta_form_model_draft set IS_VALID =" + (toGarbage ? "'T'" : "'F'") +
                    " where MODEL_ID = ?",
                new Object[]{relationId}
            );
            DatabaseOptUtils.doExecuteSql(applicationVersionDao,
                "update m_meta_form_model set IS_VALID = " + (toGarbage ? "'T'" : "'F'") +
                    " where MODEL_ID = ?",
                new Object[]{relationId}
            );
        } else if ("3".equals(objType)) { //"类型，1：工作流 2：页面设计 3：api网关"
            // 恢复到 draft
            DatabaseOptUtils.doExecuteSql(applicationVersionDao,
                "update q_data_packet_draft set is_disable = " + (toGarbage ? "'T'" : "'F'") +
                    " where PACKET_ID= ?",
                new Object[]{relationId}
            );
            DatabaseOptUtils.doExecuteSql(applicationVersionDao,
                "update q_data_packet set is_disable = " + (toGarbage ? "'T'" : "'F'") +
                    " where PACKET_ID= ?",
                new Object[]{relationId}
            );
        }
    }

    @Override
    public int restoreAppVersion(String appVersionId, String userCode) {
        ApplicationVersion appVersion = applicationVersionDao.getObjectById(appVersionId);
        JSONArray diffs = innerCompareToOldVersion(appVersion.getApplicationId(), appVersionId, true);
        //恢复不同
        int mergeCount = 0;
        for (Object obj : diffs) {
            if (obj instanceof JSONObject) {
                JSONObject jsonDiff = (JSONObject) obj;
                AppMergeTask mergeTask = new AppMergeTask();
                mergeTask.setAppVersionId(appVersionId);
                mergeTask.setMergeStatus(ApplicationVersion.VERSION_MERGE_STATUS_MERGING);
                mergeTask.setRelationId(jsonDiff.getString("relationId"));
                mergeTask.setObjectType(jsonDiff.getString("type"));
                mergeTask.setUpdateUser(StringUtils.isBlank(jsonDiff.getString("lastUpdateUser"))?
                    userCode: jsonDiff.getString("lastUpdateUser"));

                if ("D".equals(jsonDiff.getString("diff"))) {
                    // 删除的，创建版本， 检查是否有删除状态的，如果有 先恢复
                    HistoryVersion hv = historyVersionService.getHistoryVersion(jsonDiff.getString("historyId"));
                    recoveryHistoryVersion(hv);
                    mergeTask.setMergeType(AppMergeTask.MERGE_TYPE_CREATE);//"C");
                    mergeTask.setHistoryId(jsonDiff.getString("historyId"));
                    mergeTask.setMergeDesc("创建 - " + jsonDiff.getString("memo"));//
                } else if ("C".equals(jsonDiff.getString("diff"))) {
                    // createHistoryVersion(jsonDiff, appVersion);
                    // 新增的，修改为 删除
                    changeRelationObjectState(jsonDiff.getString("type"),
                        jsonDiff.getString("relationId"), true);
                    mergeTask.setHistoryId(jsonDiff.getString("historyId2"));
                    mergeTask.setMergeDesc("删除 - " + jsonDiff.getString("memo"));
                    mergeTask.setMergeType(AppMergeTask.MERGE_TYPE_DELETE);//"D");
                } else if ("U".equals(jsonDiff.getString("diff"))) {
                    HistoryVersion hv2 = createHistoryVersion(jsonDiff, appVersion);
                    // 更新的，创建版本，并恢复为旧版本
                    HistoryVersion hv = historyVersionService.getHistoryVersion(jsonDiff.getString("historyId"));
                    recoveryHistoryVersion(hv);
                    mergeTask.setHistoryId(hv2.getHistoryId()); //jsonDiff.getString("historyId"));
                    mergeTask.setMergeDesc("更新 - " + jsonDiff.getString("memo"));
                    mergeTask.setMergeType(AppMergeTask.MERGE_TYPE_UPDATE);//"U");
                }
                appMergeTaskDao.saveNewObject(mergeTask);
                mergeCount++;
            }
        }
        if (mergeCount > 0) {
            applicationVersionDao.setRestoreStatus(appVersionId, ApplicationVersion.VERSION_MERGE_STATUS_MERGING);//"B");
        }
        return mergeCount;
    }

    @Override
    public int mergeAppComponents(String appVersionId, JSONArray components, String userCode) {
        ApplicationVersion appVersion = applicationVersionDao.getObjectById(appVersionId);
        int mergeCount = 0;
        for (Object obj : components) {
            if (obj instanceof JSONObject) {
                JSONObject jsonDiff = (JSONObject) obj;
                String historyId = jsonDiff.getString("historyId");
                if (StringUtils.isBlank(historyId)) continue;
                HistoryVersion hv = historyVersionService.getHistoryVersion(historyId);
                if (hv != null) {
                    AppMergeTask mergeTask = new AppMergeTask();
                    mergeTask.setAppVersionId(appVersionId);
                    mergeTask.setMergeStatus(ApplicationVersion.VERSION_MERGE_STATUS_MERGING);
                    mergeTask.setRelationId(hv.getRelationId());
                    mergeTask.setObjectType(hv.getType());
                    mergeTask.setUpdateUser(userCode);

                    HistoryVersion hv2 = createHistoryVersion(hv.getType(), hv.getRelationId());
                    if (hv2 == null) {
                        recoveryHistoryVersion(hv);
                        mergeTask.setMergeType(AppMergeTask.MERGE_TYPE_CREATE);//"C"
                        mergeTask.setHistoryId(hv.getHistoryId());
                        mergeTask.setMergeDesc("创建 - " + hv.getMemo());//
                    } else {
                        hv2.setOsId(appVersion.getApplicationId());
                        //hv2.setAppVersionId(appVersionId);
                        hv2.setHistorySha(hv2.generateHistorySha());
                        if (!StringUtils.equals(hv.getHistorySha(), hv2.getHistorySha())) {
                            //创建版本
                            hv2.setLabel("V_recovery_" + appVersion.getVersionId());
                            hv2.setMemo("因为恢复版本而创建的：" + appVersion.getVersionId());//jsonDiff.getString("memo"));
                            hv2.setPushTime(DatetimeOpt.currentUtilDate());
                            hv2.setPushUser(userCode);
                            historyVersionService.createHistoryVersion(hv2);
                            // 更新的，创建版本，并恢复为旧版本
                            recoveryHistoryVersion(hv);
                            mergeTask.setHistoryId(jsonDiff.getString("historyId"));
                            mergeTask.setMergeDesc("更新 - " + hv.getMemo());
                            mergeTask.setMergeType(AppMergeTask.MERGE_TYPE_UPDATE);//"U");

                        }
                    }
                    if (StringUtils.isNotBlank(mergeTask.getMergeType())) {
                        appMergeTaskDao.saveNewObject(mergeTask);
                        mergeCount++;
                    }
                }
            }
        }
        if (mergeCount > 0) {
            applicationVersionDao.setRestoreStatus(appVersionId, ApplicationVersion.VERSION_MERGE_STATUS_MERGING);//"B");
        }
        return mergeCount;
    }

    @Override
    public List<AppMergeTask> listAppMergeTasks(String appVersionId, Map<String, Object> filterMap, PageDesc pageDesc) {
        filterMap.put("appVersionId", appVersionId);
        return appMergeTaskDao.listObjectsByProperties(filterMap, pageDesc);
    }

    private void publishPage(String modelId) {
        String query = "select MODEL_ID, Model_Name, OPT_ID, os_id, Model_Type, " +
            " Recorder, Model_Comment, MOBILE_FORM_TEMPLATE, form_template, " +
            " SOURCE_ID, STRUCTURE_FUNCTION, MODEL_TAG, IS_VALID " +
            " from m_meta_form_model_draft where MODEL_ID = ?";
        JSONObject object = DatabaseOptUtils.getObjectBySqlAsJson(appMergeTaskDao, query, new Object[]{modelId});
        if (object == null) {
            throw new ObjectException(ObjectException.DATA_NOT_FOUND_EXCEPTION, "找不到对应的页面模块" + modelId);
        }
        Object count = DatabaseOptUtils.getScalarObjectQuery(appMergeTaskDao,
            "select count(*) from m_meta_form_model where MODEL_ID = ?", new Object[]{modelId});
        object.put("lastModifyDate", DatetimeOpt.currentUtilDate());
        object.put("publishDate", DatetimeOpt.currentUtilDate());
        if (NumberBaseOpt.castObjectToInteger(count, 0) > 0) {
            String updateSql = "update m_meta_form_model set " +
                " Model_Name = :modelName, OPT_ID = :optId, os_id = :osId, Model_Type = :modelType, " +
                " Recorder = :recorder, Model_Comment = :modelComment," +
                " MOBILE_FORM_TEMPLATE = :mobileFormTemplate, form_template = :formTemplate, " +
                " SOURCE_ID = :sourceId, STRUCTURE_FUNCTION = :structureFunction," +
                " MODEL_TAG = :modelTag, IS_VALID = 'F', last_modify_Date = :lastModifyDate," +
                " publish_date = :publishDate" +
                " where MODEL_ID = :modelId";
            DatabaseOptUtils.doExecuteNamedSql(appMergeTaskDao, updateSql, object);
        } else {
            String insertSql = "insert into m_meta_form_model" +
                " (MODEL_ID, Model_Name, OPT_ID, os_id, Model_Type," +
                "  Recorder, Model_Comment, MOBILE_FORM_TEMPLATE, form_template, last_modify_Date," +
                " SOURCE_ID, STRUCTURE_FUNCTION, MODEL_TAG, IS_VALID, publishDate )" +
                " values (:modelId, :modelName, :optId, :osId, :modelType," +
                " :recorder, :modelComment, :mobileFormTemplate, :formTemplate, :lastModifyDate," +
                " :sourceId, :structureFunction, :modelTag, 'F', :publishDate)";
            DatabaseOptUtils.doExecuteNamedSql(appMergeTaskDao, insertSql, object);
        }

        String updateSql = "update m_meta_form_model_draft set " +
            " last_modify_Date = :lastModifyDate, publish_date = :publishDate" +
            " where MODEL_ID = :modelId";
        DatabaseOptUtils.doExecuteNamedSql(appMergeTaskDao, updateSql, object);
    }

    private void publishApi(String apiId) {
        String query = "select PACKET_ID, os_id, Owner_Type, Owner_Code, PACKET_NAME, " +
            "PACKET_TYPE, PACKET_DESC, Recorder, Record_Date, has_data_opt, " +
            "data_opt_desc_json, task_type, task_Cron, is_valid, interface_name, " +
            "is_while, return_type, return_result, need_rollback, OPT_ID, " +
            "EXT_PROPS, opt_code, BUFFER_FRESH_PERIOD, buffer_fresh_period_type, " +
            "log_level, is_disable, schema_props, request_body_type, FALL_BACK_LEVEL " +
            "from q_data_packet_draft  where PACKET_ID = ?";
        JSONObject object = DatabaseOptUtils.getObjectBySqlAsJson(appMergeTaskDao, query, new Object[]{apiId});
        if (object == null) {
            throw new ObjectException(ObjectException.DATA_NOT_FOUND_EXCEPTION, "找不到对应的API接口模块" + apiId);
        }
        Object count = DatabaseOptUtils.getScalarObjectQuery(appMergeTaskDao,
            "select count(*) from q_data_packet where PACKET_ID = ?", new Object[]{apiId});
        object.put("updateDate", DatetimeOpt.currentUtilDate());
        object.put("publishDate", DatetimeOpt.currentUtilDate());
        if (NumberBaseOpt.castObjectToInteger(count, 0) > 0) {
            String updateSql = "update q_data_packet set " +
                "os_id = :osId, Owner_Type = :ownerType, Owner_Code = :ownerCode, PACKET_NAME = :packetName, " +
                "PACKET_TYPE = :packetType, PACKET_DESC = :packetDesc, Recorder = :recorder, Record_Date = :recordDate, has_data_opt = :hasDataOpt, " +
                "data_opt_desc_json = :dataOptDescJson, task_type = :taskType, task_Cron = :taskCron, is_valid = 'T', interface_name = :interfaceName, " +
                "is_while = :isWhile, return_type = :returnType, return_result = :returnResult, need_rollback = :needRollback, OPT_ID = :optId, " +
                "EXT_PROPS = :extProps, opt_code = :optCode, BUFFER_FRESH_PERIOD = :bufferFreshPeriod, buffer_fresh_period_type = :bufferFreshPeriodType, " +
                "log_level = :logLevel, is_disable = 'F', schema_props = :schemaProps, request_body_type = :requestBodyType, FALL_BACK_LEVEL = :fallBackLevel, " +
                "update_date = :updateDate, publish_date = :publishDate" +
                " where PACKET_ID = :packetId";
            DatabaseOptUtils.doExecuteNamedSql(appMergeTaskDao, updateSql, object);
        } else {
            String insertSql = "insert into q_data_packet" +
                "(PACKET_ID, os_id, Owner_Type, Owner_Code, PACKET_NAME, " +
                "PACKET_TYPE, PACKET_DESC, Recorder, Record_Date, has_data_opt, " +
                "data_opt_desc_json, task_type, task_Cron, is_valid, interface_name, " +
                "is_while, return_type, return_result, need_rollback, OPT_ID, " +
                "EXT_PROPS, opt_code, BUFFER_FRESH_PERIOD, buffer_fresh_period_type, " +
                "log_level, is_disable, schema_props, request_body_type, FALL_BACK_LEVEL " +
                "update_date,  publish_date)" +
                " values (:packetId, :osId, :ownerType, :ownerCode, :packetName, " +
                " :packetType, :packetDesc, :recorder, :recordDate, :hasDataOpt, " +
                " :dataOptDescJson, :taskType, :taskCron, 'T', :interfaceName, " +
                " :isWhile, :returnType, :returnResult, :needRollback, :optId, " +
                " :extProps, :optCode, :bufferFreshPeriod, :bufferFreshPeriodType, " +
                " :logLevel, 'F', :schemaProps, :requestBodyType, :fallBackLevel, " +
                " :updateDate, :publishDate)";
            DatabaseOptUtils.doExecuteNamedSql(appMergeTaskDao, insertSql, object);
        }
        String deleteParams = "delete from q_data_packet_param  where PACKET_ID = :packetId";
        DatabaseOptUtils.doExecuteNamedSql(appMergeTaskDao, deleteParams, object);

        String insertParams = "insert into q_data_packet_param (PACKET_ID, PARAM_Name, PARAM_Label, PARAM_Display_Style, param_Type, " +
            "param_Reference_Type, param_Reference_Data, param_Validate_Regex, param_Validate_Info, " +
            "param_Default_Value, PARAM_Order, IS_REQUIRED ) " +
            "select PACKET_ID, PARAM_Name, PARAM_Label, PARAM_Display_Style, param_Type, " +
            "param_Reference_Type, param_Reference_Data, param_Validate_Regex, param_Validate_Info, " +
            "param_Default_Value, PARAM_Order, IS_REQUIRED " +
            "from q_data_packet_param_draft where PACKET_ID = ?";
        DatabaseOptUtils.doExecuteSql(appMergeTaskDao, insertParams, new Object[]{apiId});

        String updateSql = "update q_data_packet_draft set " +
            " update_date = :updateDate,  publish_date = :publishDate" +
            " where PACKET_ID = :packetId";
        DatabaseOptUtils.doExecuteNamedSql(appMergeTaskDao, updateSql, object);
    }

    @Override
    public void mergeCompleted(AppMergeTask task) {
        // 发布对象
        if ("2".equals(task.getObjectType())) {
            publishPage(task.getRelationId());
        } else if ("3".equals(task.getObjectType())) {
            publishApi(task.getRelationId());
        }
        appMergeTaskDao.markTaskComplete(task.getAppVersionId(), task.getRelationId());
    }

    @Override
    public boolean checkRestoreCompleted(String appVersionId){
        int task = appMergeTaskDao.countObjectByProperties(CollectionsOpt.createHashMap(
            "appVersionId", appVersionId,
            "mergeStatus", ApplicationVersion.VERSION_MERGE_STATUS_MERGING
        ));
        boolean complete = task == 0;
        if(complete){
            restoreCompleted(appVersionId);
        }
        return complete;
    }

    @Override
    public void restoreCompleted(String appVersionId) {
        // 发布所有对象
        List<AppMergeTask> tasks = appMergeTaskDao.listMergeTask(appVersionId,
            ApplicationVersion.VERSION_MERGE_STATUS_MERGING);
        if (tasks != null && !tasks.isEmpty()) {
            for (AppMergeTask task : tasks) {
                if ("2".equals(task.getObjectType())) {
                    publishPage(task.getRelationId());
                } else if ("3".equals(task.getObjectType())) {
                    publishApi(task.getRelationId());
                }
            }
        }
        appMergeTaskDao.clearMergeTask(appVersionId);
        applicationVersionDao.setRestoreStatus(appVersionId, ApplicationVersion.VERSION_MERGE_STATUS_COMPLETED);//"A");
    }

    private void innerRollbackMergeTask(AppMergeTask task) {
        if (AppMergeTask.MERGE_TYPE_CREATE.equals(task.getMergeType())) {
            //update state to delete
            changeRelationObjectState(task.getObjectType(), task.getRelationId(), true);
        } else if (AppMergeTask.MERGE_TYPE_UPDATE.equals(task.getMergeType())) {
            HistoryVersion hv = historyVersionService.getHistoryVersion(task.getHistoryId());
            recoveryHistoryVersion(hv);
        } else if (AppMergeTask.MERGE_TYPE_DELETE.equals(task.getMergeType())) {
            changeRelationObjectState(task.getObjectType(), task.getRelationId(), false);
        }
    }

    @Override
    public void rollbackMergeTask(AppMergeTask task) {
        innerRollbackMergeTask(task);
        appMergeTaskDao.markTaskRollback(task.getAppVersionId(), task.getRelationId());
    }

    @Override
    public void rollbackRestore(String appVersionId) {
        List<AppMergeTask> tasks = appMergeTaskDao.listMergeTask(appVersionId,
            ApplicationVersion.VERSION_MERGE_STATUS_MERGING);// B
        if (tasks != null && !tasks.isEmpty()) {
            for (AppMergeTask task : tasks) {
                innerRollbackMergeTask(task);
            }
        }
        restoreCompleted(appVersionId);
    }

}
