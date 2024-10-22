package com.centit.locode.platform.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.dde.adapter.DdeDubboTaskRun;
import com.centit.fileserver.common.FileInfoOpt;
import com.centit.fileserver.po.FileInfo;
import com.centit.framework.common.ResponseData;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.framework.model.basedata.OsInfo;
import com.centit.framework.model.security.CentitUserDetails;
import com.centit.locode.platform.dao.ApplicationTemplateDao;
import com.centit.locode.platform.service.ModelExportManager;
import com.centit.locode.platform.vo.AppTableNames;
import com.centit.locode.platform.vo.JsonAppVo;
import com.centit.product.dbdesign.service.MetaTableManager;
import com.centit.product.metadata.po.PendingMetaColumn;
import com.centit.product.metadata.po.PendingMetaTable;
import com.centit.product.metadata.po.SourceInfo;
import com.centit.support.algorithm.*;
import com.centit.support.common.JavaBeanMetaData;
import com.centit.support.common.ObjectException;
import com.centit.support.file.CsvFileIO;
import com.centit.support.file.FileSystemOpt;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;

/**
 * @author zhf
 */
@Service
public class ModelExportMangerImpl implements ModelExportManager {
    @Value("${app.home:./}")
    private String appHome;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ModelExportMangerImpl.class);
    @Autowired
    private ApplicationTemplateDao applicationTemplateDao;
    @Autowired
    private MetaTableManager metaTableManager;
    @Autowired
    private FileInfoOpt fileInfoOpt;
    @Autowired
    private DdeDubboTaskRun ddeDubboTaskRun;
    private final Map<String, String> applicationSql = new HashMap<>(16);
    private final Map<String, String> oldApplicationSql = new HashMap<>(16);
    private final Map<String, String> newDatabaseSql = new HashMap<>(4);
    private final Map<String, String> oldDatabaseSql = new HashMap<>(4);

    @PostConstruct
    void init() {
        applicationSql.put(AppTableNames.F_OS_INFO.name(), "select * from f_os_info where [:osId | os_id=:osId]");
        applicationSql.put(AppTableNames.FILE_LIBRARY_INFO.name(), "select * from file_library_info where [:osId | library_id=:osId]");
        applicationSql.put(AppTableNames.F_OPTINFO.name(), "select * from f_optinfo where [:osId | top_opt_id=:osId]");
        applicationSql.put(AppTableNames.F_OPTDEF.name(), "select * from f_optdef where opt_id in " +
            "(select opt_id from f_optinfo where [:osId | top_opt_id=:osId] [:optId | and opt_id=:optId])");
        applicationSql.put(AppTableNames.F_DATABASE_INFO.name(), "select database_code,top_unit,database_name,database_desc,source_type " +
            "from f_database_info where database_code in (select DATABASE_ID from m_application_resources where [:osId | os_id=:osId])");
        applicationSql.put(AppTableNames.M_APPLICATION_RESOURCES.name(), "select * from m_application_resources where [:osId | os_id=:osId]");
        applicationSql.put(AppTableNames.F_TABLE_OPT_RELATION.name(), "select a.* from f_table_opt_relation a join f_md_table b on a.table_id=b.TABLE_ID where [:osId | a.os_id=:osId] [:optId | and opt_id=:optId]");
        applicationSql.put(AppTableNames.M_META_FORM_MODEL.name(), "select * from m_meta_form_model where [:osId | OS_ID=:osId] [:optId | and opt_id=:optId]");
        applicationSql.put(AppTableNames.Q_DATA_PACKET.name(), "select * from q_data_packet where [:osId | OS_ID=:osId] [:optId | and opt_id=:optId]");
        applicationSql.put(AppTableNames.WF_FLOW_DEFINE.name(), "select * from wf_flow_define where [:osId | OS_ID=:osId] and flow_state in('A','E','B','D') [:optId | and opt_id=:optId]");
        applicationSql.put(AppTableNames.WF_NODE.name(), "select * from wf_node where (flow_code,version) in(" +
            "select flow_code,version from wf_flow_define where [:osId | OS_ID=:osId] and flow_state='B' [:optId | and opt_id=:optId])");
        applicationSql.put(AppTableNames.WF_TRANSITION.name(), "select * from wf_transition where (flow_code,version) in(" +
            "select flow_code,version from wf_flow_define where [:osId | OS_ID=:osId] and flow_state='B' [:optId | and opt_id=:optId])");
        applicationSql.put(AppTableNames.WF_FLOW_STAGE.name(), "select * from wf_flow_stage where (flow_code,version) in(" +
            "select flow_code,version from wf_flow_define where [:osId | OS_ID=:osId] and flow_state='B' [:optId | and opt_id=:optId])");
        applicationSql.put(AppTableNames.WF_OPT_TEAM_ROLE.name(), "select * from wf_opt_team_role where opt_id in " +
            "(select opt_id from f_optinfo where [:osId | top_opt_id=:osId] [:optId | and opt_id=:optId])");
        applicationSql.put(AppTableNames.WF_OPT_VARIABLE_DEFINE.name(), "select * from wf_opt_variable_define where opt_id in " +
            "(select opt_id from f_optinfo where [:osId | top_opt_id=:osId] [:optId | and opt_id=:optId])");
        applicationSql.put(AppTableNames.F_DATACATALOG.name(), "select * from f_datacatalog where CATALOG_CODE in " +
            "(select dictionary_id from m_application_dictionary where [:osId | os_id=:osId])");
        applicationSql.put(AppTableNames.F_DATADICTIONARY.name(), "select * from f_datadictionary where CATALOG_CODE in " +
            "(select dictionary_id from m_application_dictionary where [:osId | os_id=:osId])");
        applicationSql.put(AppTableNames.M_APPLICATION_DICTIONARY.name(), "select * from m_application_dictionary where [:osId | os_id=:osId]");

        newDatabaseSql.put(AppTableNames.F_MD_TABLE.name(), "select * from f_md_table where table_id in (select table_id from f_table_opt_relation where [:osId | OS_ID=:osId] [:optId | and opt_id=:optId])" +
            " and database_code in (select DATABASE_ID from m_application_resources where [:osId | os_id=:osId])");
        newDatabaseSql.put(AppTableNames.F_MD_COLUMN.name(), "select * from f_md_column where table_id in (select table_id from f_table_opt_relation where [:osId | OS_ID=:osId] [:optId | and opt_id=:optId])");
        newDatabaseSql.put(AppTableNames.F_MD_RELATION.name(), "select * from f_md_relation where parent_table_id in (select table_id from f_table_opt_relation where [:osId | OS_ID=:osId] [:optId | and opt_id=:optId])");
        newDatabaseSql.put(AppTableNames.F_MD_REL_DETAIL.name(), "select * from f_md_rel_detail where relation_id in (select relation_id from f_md_relation where parent_table_id in " +
            "(select table_id from f_table_opt_relation where [:osId | OS_ID=:osId] [:optId | and opt_id=:optId]))");

        oldDatabaseSql.put(AppTableNames.F_MD_TABLE.name(), "select table_id,table_name,DATABASE_CODE from f_md_table where database_code in (select DATABASE_ID from m_application_resources where os_id=:osId)");
        oldDatabaseSql.put(AppTableNames.F_MD_RELATION.name(), "select RELATION_ID,PARENT_TABLE_ID,CHILD_TABLE_ID from f_md_relation where parent_table_id in (select table_id from f_md_table where database_code in " +
            "(select DATABASE_ID from m_application_resources where os_id=:osId))");

        oldApplicationSql.put(AppTableNames.F_OS_INFO.name(), "select os_id,os_name,default_database from f_os_info where os_id=:osId");
        oldApplicationSql.put(AppTableNames.FILE_LIBRARY_INFO.name(), "select library_name from file_library_info where library_id=:osId");
        oldApplicationSql.put(AppTableNames.F_OPTINFO.name(), "select SOURCE_ID,FORM_CODE,OPT_ID,DOC_ID,top_opt_id,opt_name from f_optinfo");
        oldApplicationSql.put(AppTableNames.F_OPTDEF.name(), "select a.SOURCE_ID,a.OPT_CODE,b.top_opt_id from f_optdef a join f_optinfo b on a.opt_id=b.opt_id");
        oldApplicationSql.put(AppTableNames.F_DATABASE_INFO.name(), "select database_code,database_name " +
            "from f_database_info where database_code in (select DATABASE_ID from m_application_resources where os_id=:osId)");
        oldApplicationSql.put(AppTableNames.M_APPLICATION_RESOURCES.name(), "select id,os_id,database_id from m_application_resources where os_id=:osId");
        oldApplicationSql.put(AppTableNames.F_TABLE_OPT_RELATION.name(), "select table_id,opt_id,id from f_table_opt_relation where OS_ID=:osId");
        oldApplicationSql.put(AppTableNames.M_META_FORM_MODEL.name(), "select source_id,MODEL_ID,os_id from m_meta_form_model");
        oldApplicationSql.put(AppTableNames.Q_DATA_PACKET.name(), "select source_id,packet_id,os_id from q_data_packet");
        oldApplicationSql.put(AppTableNames.WF_FLOW_DEFINE.name(), "select SOURCE_ID,FLOW_CODE,os_id from wf_flow_define");
        oldApplicationSql.put(AppTableNames.WF_NODE.name(), "select SOURCE_ID,NODE_ID,os_id from wf_node");
        oldApplicationSql.put(AppTableNames.WF_TRANSITION.name(), "select FLOW_CODE,START_NODE_ID,END_NODE_ID,TRANS_ID from wf_transition where flow_code in(" +
            "select flow_code from wf_flow_define where OS_ID=:osId and flow_state<>'D')");
        oldApplicationSql.put(AppTableNames.WF_OPT_TEAM_ROLE.name(), "select OPT_TEAM_ROLE_ID,OPT_ID,ROLE_CODE from wf_opt_team_role where opt_id in " +
            "(select opt_id from f_optinfo where top_opt_id=:osId)");
        oldApplicationSql.put(AppTableNames.WF_OPT_VARIABLE_DEFINE.name(), "select VARIABLE_NAME,OPT_ID,OPT_VARIABLE_ID from wf_opt_variable_define where opt_id in " +
            "(select opt_id from f_optinfo where top_opt_id=:osId)");
        oldApplicationSql.put(AppTableNames.F_DATACATALOG.name(), "select a.catalog_code,a.source_id,b.os_id from f_datacatalog a join m_application_dictionary b on a.CATALOG_CODE=b.dictionary_id");
        oldApplicationSql.put(AppTableNames.M_APPLICATION_DICTIONARY.name(), "select id,os_id,dictionary_id from m_application_dictionary where os_id=:osId");
    }

    @Override
    public String downModel(String osId, Map<String, Object> parameters) throws FileNotFoundException {
        String fileId = DatetimeOpt.convertDateToString(DatetimeOpt.currentUtilDate(), "YYYYMMddHHmmss");
        String filePath = appHome + File.separator + fileId;
        Map<String, Object> mapApplication = new HashMap<>(1);
        mapApplication.put("osId", osId);
        if (parameters != null && StringUtils.isNotBlank(StringBaseOpt.objectToString(parameters.get("optId")))) {
            mapApplication.put("optId", StringBaseOpt.objectToString(parameters.get("optId")));
        }
        updateSourceId(mapApplication);
        for (Map.Entry<String, String> entry : applicationSql.entrySet()) {
            createFile(mapApplication, entry.getValue(), entry.getKey(), filePath);
        }
        for (Map.Entry<String, String> entry : newDatabaseSql.entrySet()) {
            createFile(mapApplication, entry.getValue(), entry.getKey(), filePath);
        }
        try {
            compressFileInfo(osId, filePath);
        } catch (IOException e) {
            throw new ObjectException(e.getMessage());
        }
        ZipCompressor.compress(filePath + ".zip", filePath);
        FileSystemOpt.deleteDirect(filePath);
        return fileId;
    }

    @Override
    public String exportModelAndSaveToFileServer(OsInfo osInfo) throws IOException {
        String osId = osInfo.getOsId();
        String fileId = downModel(osId, null);
        String filePath = appHome + File.separator + fileId + ".zip";
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(osInfo.getOsName() + DatetimeOpt.currentDate() + ".zip");
        fileInfo.setFileType("zip");
        fileInfo.setOsId(osId);
        fileInfo.setLibraryId("backup");
        fileInfo.setFileOwner("system");
        return fileInfoOpt.saveFile(fileInfo, 0, new FileInputStream(filePath));
    }

    private void updateSourceId(Map<String, Object> map) {
        String updateOptInfo = "update f_optinfo set source_id=opt_id where top_opt_id=:osId and source_id is null";
        DatabaseOptUtils.doExecuteNamedSql(applicationTemplateDao, updateOptInfo, map);
        String updateOptDef = "update f_optdef set source_id=opt_code where opt_id in (select opt_id from f_optinfo where top_opt_id=:osId) and source_id is null";
        DatabaseOptUtils.doExecuteNamedSql(applicationTemplateDao, updateOptDef, map);
        String updateMetaForm = "update m_meta_form_model set source_id=model_id where os_id=:osId and source_id is null";
        DatabaseOptUtils.doExecuteNamedSql(applicationTemplateDao, updateMetaForm, map);
        String updateDataPacket = "update q_data_packet set source_id=packet_id where os_id=:osId and source_id is null";
        DatabaseOptUtils.doExecuteNamedSql(applicationTemplateDao, updateDataPacket, map);
        String updateFlowDefine = "update wf_flow_define set source_id=flow_code where os_id=:osId and source_id is null";
        DatabaseOptUtils.doExecuteNamedSql(applicationTemplateDao, updateFlowDefine, map);
        String updateFlowNode = "update wf_node set source_id=node_id where (flow_code,version) in (select flow_code,version from wf_flow_define where OS_ID=:osId and flow_state='B') and source_id is null";
        DatabaseOptUtils.doExecuteNamedSql(applicationTemplateDao, updateFlowNode, map);
    }

    private void createFile(Map<String, Object> map, String sql, String fileName, String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new SecurityException();
            }
        }
        JSONArray jsonArray = DatabaseOptUtils.listObjectsByParamsDriverSqlAsJson(applicationTemplateDao, sql, map);
        if (fileName.equals(AppTableNames.F_OPTINFO.name()) && map.containsKey("optId")) {
            jsonArray = parentOpt(jsonArray, StringBaseOpt.objectToString(map.get("optId")));
        }
        try (FileOutputStream fos = new FileOutputStream(filePath + File.separator + fileName + ".csv")) {
            CsvFileIO.saveJSON2OutputStream(jsonArray, fos, true, null, "gbk");
        } catch (IOException e) {
            throw new ObjectException(ResponseData.HTTP_IO_EXCEPTION, "导出文件出错" + e.getMessage());
        }
    }

    private JSONArray parentOpt(JSONArray jsonArray, String optId) {
        JSONArray parentOpt = new JSONArray();
        JSONObject optInfo = getOptInfo(jsonArray, optId);
        if (optInfo == null) {
            return jsonArray;
        }
        while (optInfo != null) {
            parentOpt.add(optInfo);
            String preOptId = optInfo.getString("preOptId");
            if (preOptId.equals("0")) {
                break;
            }
            optInfo = getOptInfo(jsonArray, preOptId);
        }
        return parentOpt;
    }

    private JSONObject getOptInfo(JSONArray jsonArray, String optId) {
        for (Object row : jsonArray) {
            if (row instanceof JSONObject) {
                if (((JSONObject) row).getString("optId").equals(optId)) {
                    return (JSONObject) row;
                }
            }
        }
        return null;
    }

    private void compressFileInfo(String osId, String filePath) throws IOException {
        String fileInfoSql = "select file_id, file_name from file_info where library_id=:osId and file_catalog in ('A','B')";
        List<Object[]> objects = DatabaseOptUtils.listObjectsByNamedSql(applicationTemplateDao, fileInfoSql, CollectionsOpt.createHashMap("osId", osId));
        if (objects == null) {
            return;
        }
        String fileInfoPath = filePath + File.separator + "file";
        File file = new File(fileInfoPath);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new SecurityException();
            }
        }
        for (Object[] object : objects) {
            String fileId = StringBaseOpt.castObjectToString(object[0]);
            InputStream inputStream = fileInfoOpt.loadFileStream(fileId);
            if(inputStream!=null) {
                String fileIdPath = fileInfoPath + File.separator + "(" + fileId + ")" + StringBaseOpt.castObjectToString(object[1]);
                FileSystemOpt.createFile(inputStream, fileIdPath);
            }
        }
        ZipCompressor.compress(fileInfoPath + ".zip", fileInfoPath);
        FileSystemOpt.deleteDirect(fileInfoPath);
    }

    @Override
    public JSONObject uploadModel(File zipFile) throws Exception {
        JSONObject jsonObject = new JSONObject();
        String filePath = appHome + File.separator + "c" + DatetimeOpt.convertDateToString(DatetimeOpt.currentUtilDate(), "YYYYMMddHHmmss");
        ZipCompressor.release(zipFile, filePath);
        parseCsvToJson(jsonObject, filePath);
        JSONObject returnJsonObject = new JSONObject();
        returnJsonObject.put("file", filePath);
        returnJsonObject.put("F_DATABASE_INFO", jsonObject.get("F_DATABASE_INFO"));
        return returnJsonObject;
    }

    private void parseCsvToJson(JSONObject jsonObject, String filePath) throws Exception {
        List<File> files = FileSystemOpt.findFiles(filePath, "*.csv");
        for (File file : files) {
            String fileName = FileSystemOpt.extractFileName(file.getPath());
            jsonObject.put(fileName, CsvFileIO.readDataFromInputStream(new FileInputStream(file.getPath()),
                true, null, "gbk"));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer createApp(JSONObject jsonObject, String osId, CentitUserDetails userDetails) {
        try {
            String zipFilePath = jsonObject.getString("file");
            JsonAppVo jsonAppVo = new JsonAppVo(jsonObject, getOldApplication(osId), userDetails, appHome, fileInfoOpt, zipFilePath);
            int result = createApp(jsonAppVo);
//            OperationLogCenter.log(OperationLog.create().application(osId).content("导入应用成功").user(userDetails.getUserCode())
//                .topUnit(userDetails.getTopUnitCode()).unit(userDetails.getCurrentUnitCode()).
//                    loginIp(userDetails.getLoginIp()).time(new Date()).tag(String.valueOf(result)));
            return result;
        } catch (Exception e) {
            OperationLogCenter.log(OperationLog.create().application(osId).content("导入应用失败").user(userDetails.getUserCode())
                .topUnit(userDetails.getTopUnitCode()).unit(userDetails.getCurrentUnitCode()).
                    loginIp(userDetails.getLoginIp()).time(new Date()).newObject(e.getMessage()));
            throw new ObjectException(e.getMessage());
        }
    }

    @Override
    public JSONObject prepareApp(JSONObject jsonObject, String osId, CentitUserDetails currentUserDetails) {
        try {
            String zipFilePath = jsonObject.getString("file");
            JSONObject sourceJson = new JSONObject();
            parseCsvToJson(sourceJson, zipFilePath);
            String copyString = JSON.toJSONString(jsonObject);
            JSONObject copyJson = JSONObject.parse(copyString);
            sourceJson.put("F_DATABASE_INFO", copyJson.get("F_DATABASE_INFO"));
            JsonAppVo jsonAppVo = new JsonAppVo(sourceJson, getOldApplication(osId), currentUserDetails, appHome, fileInfoOpt, zipFilePath);
            jsonAppVo.setUploadFiles(false);
            jsonAppVo.updatePrimary();
            List<Map<String, Object>> pendingTableList = jsonAppVo.getMapJsonObject().get(AppTableNames.F_MD_TABLE.name());
            List<Map<String, Object>> pendingColumnsList = jsonAppVo.getMapJsonObject().get(AppTableNames.F_MD_COLUMN.name());
            List<Map<String, Object>> databaseList = jsonAppVo.getMapJsonObject().get(AppTableNames.F_DATABASE_INFO.name());
            Map<String, List<String>> appSqls = new HashMap<>(2);
            JavaBeanMetaData javaBeanMetaData = JavaBeanMetaData.createBeanMetaDataFromType(PendingMetaTable.class);
            JavaBeanMetaData javaBeanMetaData2 = JavaBeanMetaData.createBeanMetaDataFromType(PendingMetaColumn.class);
            for (Map<String, Object> map : pendingTableList) {
                PendingMetaTable pendingMetaTable = (PendingMetaTable) javaBeanMetaData.createBeanObjectFromMap(map);
                List<PendingMetaColumn> pendingMetaColumns = new ArrayList<>();
                for (Map<String, Object> mapPendingColumn : pendingColumnsList) {
                    PendingMetaColumn pendingMetaColumn = (PendingMetaColumn) javaBeanMetaData2.createBeanObjectFromMap(mapPendingColumn);
                    pendingMetaColumn.setMaxLength(NumberBaseOpt.castObjectToInteger(mapPendingColumn.get("columnLength")));
                    if (pendingMetaColumn.getTableId().equals(pendingMetaTable.getTableId())) {
                        pendingMetaColumns.add(pendingMetaColumn);
                    }
                }
                pendingMetaTable.setMdColumns(pendingMetaColumns);
                List<String> sqls = metaTableManager.makeAlterTableSqlList(pendingMetaTable);
                boolean findDatabase = false;
                for (String databaseCode : appSqls.keySet()) {
                    if (databaseCode.equals(pendingMetaTable.getDatabaseCode())) {
                        findDatabase = true;
                        appSqls.get(databaseCode).addAll(sqls);
                    }
                }
                if (!findDatabase) {
                    appSqls.put(pendingMetaTable.getDatabaseCode(), sqls);
                }
            }
            javaBeanMetaData = JavaBeanMetaData.createBeanMetaDataFromType(SourceInfo.class);
            Map<String, List<String>> DDLs = new HashMap<>(2);
            for (Map<String, Object> map : databaseList) {
                SourceInfo sourceInfo = (SourceInfo) javaBeanMetaData.createBeanObjectFromMap(map);
                for (String databaseCode : appSqls.keySet()) {
                    if (sourceInfo.getDatabaseCode().equals(databaseCode)) {
                        DDLs.put(sourceInfo.getDatabaseName() + "(" + databaseCode + ")", appSqls.get(databaseCode));
                    }
                }
            }
            JSONObject returnJson = new JSONObject();
            JSONObject subJson = new JSONObject();
            subJson.put("F_DATABASE_INFO", jsonObject.get("F_DATABASE_INFO"));
            subJson.put("file", jsonObject.get("file"));
            subJson.put("targetOsId", osId);
            returnJson.put("jsonAppVo", subJson);
            returnJson.put("DDL", DDLs);
            returnJson.put("runDDL", true);
            return returnJson;
        } catch (Exception e) {
            throw new ObjectException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer importApp(JSONObject jsonObject, CentitUserDetails userDetails) throws Exception {
        JSONObject jsonAppVoJson = jsonObject.getJSONObject("jsonAppVo");
        JSONObject sourceJson = new JSONObject();
        String filePath = StringBaseOpt.objectToString(jsonAppVoJson.get("file"));
        parseCsvToJson(sourceJson, filePath);
        sourceJson.put("F_DATABASE_INFO", jsonAppVoJson.get("F_DATABASE_INFO"));
        JsonAppVo jsonAppVo = new JsonAppVo(sourceJson,
            getOldApplication(jsonAppVoJson.getString("targetOsId")),
            userDetails, appHome, fileInfoOpt, filePath);
        jsonAppVo.setRunDictionary(BooleanBaseOpt.castObjectToBoolean(jsonObject.get("runDictionary"), true));
        jsonAppVo.setRunMetaData(BooleanBaseOpt.castObjectToBoolean(jsonObject.get("runMetaData"), true));
        jsonAppVo.prepareApp();
        boolean runDDL = BooleanBaseOpt.castObjectToBoolean(jsonObject.get("runDDL"), true);
        int result = 0;
        try {
            if (jsonAppVo.getAppList().size() > 0) {
                if (!runDDL) {
                    for (Object object : jsonAppVo.getAppList()) {
                        if (object instanceof PendingMetaTable) {
                            ((PendingMetaTable) object).setTableState("S");
                        }
                    }
                }
                result += DatabaseOptUtils.batchMergeObjects(applicationTemplateDao, jsonAppVo.getAppList());
                if (runDDL) {
                    for (String sDatabaseName : jsonAppVo.getListDatabaseName()) {
                        ResponseData responseData = metaTableManager.publishDatabase(sDatabaseName, jsonAppVo.getUserCode());
                        if (responseData.getCode() < 0) {
                            logger.error(responseData.getMessage() + JSON.toJSONString(responseData.getData()));
                        }
                    }
                }
            }
            if (jsonAppVo.getMetaObject().size() > 0) {
                result += DatabaseOptUtils.batchMergeObjects(applicationTemplateDao, jsonAppVo.getMetaObject());
            }
            jsonAppVo.refreshCache(ddeDubboTaskRun);
            FileSystemOpt.deleteDirect(filePath);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return result;
    }

    private JSONObject getOldApplication(String osId) {
        if (StringUtils.isBlank(osId)) {
            return new JSONObject();
        }
        Map<String, Object> mapApplication = new HashMap<>(1);
        mapApplication.put("osId", osId);
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, String> entry : oldApplicationSql.entrySet()) {
            JSONArray jsonArray = DatabaseOptUtils.listObjectsByNamedSqlAsJson(applicationTemplateDao, entry.getValue(), mapApplication);
            if (jsonArray != null) {
                jsonObject.put(entry.getKey(), jsonArray);
            }
        }
        for (Map.Entry<String, String> entry : oldDatabaseSql.entrySet()) {
            JSONArray jsonArray = DatabaseOptUtils.listObjectsByNamedSqlAsJson(applicationTemplateDao, entry.getValue(), mapApplication);
            if (jsonArray != null) {
                jsonObject.put(entry.getKey(), jsonArray);
            }
        }
        return jsonObject;
    }

    private Integer createApp(JsonAppVo jsonAppVo) throws Exception {
        int result = 0;
        jsonAppVo.prepareApp();
        try {
            if (jsonAppVo.getAppList().size() > 0) {
                result += DatabaseOptUtils.batchMergeObjects(applicationTemplateDao, jsonAppVo.getAppList());
                for (String sDatabaseName : jsonAppVo.getListDatabaseName()) {
                    ResponseData responseData = metaTableManager.publishDatabase(sDatabaseName, jsonAppVo.getUserCode());
                    if (responseData.getCode() < 0) {
                        logger.error(responseData.getMessage() + JSON.toJSONString(responseData.getData()));
                    }
                }
            }
            if (jsonAppVo.getMetaObject().size() > 0) {
                result += DatabaseOptUtils.batchMergeObjects(applicationTemplateDao, jsonAppVo.getMetaObject());
            }
            jsonAppVo.refreshCache(ddeDubboTaskRun);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return result;
    }
}
