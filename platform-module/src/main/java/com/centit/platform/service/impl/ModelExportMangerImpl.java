package com.centit.platform.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.dde.adapter.DdeDubboTaskRun;
import com.centit.dde.core.DataSet;
import com.centit.dde.dataset.CsvDataSet;
import com.centit.fileserver.common.FileInfoOpt;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.platform.dao.ApplicationTemplateDao;
import com.centit.platform.service.ModelExportManager;
import com.centit.platform.vo.JsonAppVo;
import com.centit.platform.vo.TableName;
import com.centit.product.dbdesign.service.MetaTableManager;
import com.centit.support.algorithm.*;
import com.centit.support.common.ObjectException;
import com.centit.support.file.FileSystemOpt;
import com.sun.media.jfxmedia.logging.Logger;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        applicationSql.put(TableName.F_OS_INFO.name(), "select * from f_os_info where os_id=:osId");
        applicationSql.put(TableName.FILE_LIBRARY_INFO.name(), "select * from file_library_info where library_id=:osId");
        applicationSql.put(TableName.F_OPTINFO.name(), "select * from f_optinfo where top_opt_id=:osId");
        applicationSql.put(TableName.F_OPTDEF.name(), "select * from f_optdef where opt_id in " +
            "(select opt_id from f_optinfo where top_opt_id=:osId)");
        applicationSql.put(TableName.F_DATABASE_INFO.name(), "select database_code,top_unit,database_name,database_desc,source_type " +
            "from f_database_info where database_code in (select DATABASE_ID from m_application_resources where os_id=:osId)");
        applicationSql.put(TableName.M_APPLICATION_RESOURCES.name(), "select * from m_application_resources where os_id=:osId");
        applicationSql.put(TableName.F_TABLE_OPT_RELATION.name(), "select * from f_table_opt_relation where OS_ID=:osId");
        applicationSql.put(TableName.M_META_FORM_MODEL.name(), "select * from m_meta_form_model where OS_ID=:osId and is_valid='F'");
        applicationSql.put(TableName.Q_DATA_PACKET.name(), "select * from q_data_packet where OS_ID=:osId and is_disable='F'");
        applicationSql.put(TableName.Q_DATA_PACKET_PARAM.name(), "select * from q_data_packet_param where packet_id in (" +
            "select packet_id from q_data_packet where OS_ID=:osId and is_disable='F')");
        applicationSql.put(TableName.WF_FLOW_DEFINE.name(), "select * from wf_flow_define where OS_ID=:osId and flow_state in('E','B')");
        applicationSql.put(TableName.WF_NODE.name(), "select * from wf_node where (flow_code,version) in(" +
            "select flow_code,version from wf_flow_define where OS_ID=:osId and flow_state='B')");
        applicationSql.put(TableName.WF_TRANSITION.name(), "select * from wf_transition where (flow_code,version) in(" +
            "select flow_code,version from wf_flow_define where OS_ID=:osId and flow_state='B')");
        applicationSql.put(TableName.WF_FLOW_STAGE.name(), "select * from wf_flow_stage where (flow_code,version) in(" +
            "select flow_code,version from wf_flow_define where OS_ID=:osId and flow_state='B')");
        applicationSql.put(TableName.WF_OPT_TEAM_ROLE.name(), "select * from wf_opt_team_role where opt_id in " +
            "(select opt_id from f_optinfo where top_opt_id=:osId)");
        applicationSql.put(TableName.WF_OPT_VARIABLE_DEFINE.name(), "select * from wf_opt_variable_define where opt_id in " +
            "(select opt_id from f_optinfo where top_opt_id=:osId)");
        applicationSql.put(TableName.F_DATACATALOG.name(), "select * from f_datacatalog where CATALOG_CODE in " +
            "(select dictionary_id from m_application_dictionary where os_id=:osId)");
        applicationSql.put(TableName.F_DATADICTIONARY.name(), "select * from f_datadictionary where CATALOG_CODE in " +
            "(select dictionary_id from m_application_dictionary where os_id=:osId)");
        applicationSql.put(TableName.M_APPLICATION_DICTIONARY.name(), "select * from m_application_dictionary where os_id=:osId");

        newDatabaseSql.put(TableName.F_MD_TABLE.name(), "select * from f_md_table where table_id in (select table_id from f_table_opt_relation where OS_ID=:osId)");
        newDatabaseSql.put(TableName.F_MD_COLUMN.name(), "select * from f_md_column where table_id in (select table_id from f_table_opt_relation where OS_ID=:osId)");
        newDatabaseSql.put(TableName.F_MD_RELATION.name(), "select * from f_md_relation where parent_table_id in (select table_id from f_table_opt_relation where OS_ID=:osId)");
        newDatabaseSql.put(TableName.F_MD_REL_DETAIL.name(), "select * from f_md_rel_detail where relation_id in (select relation_id from f_md_relation where parent_table_id in " +
            "(select table_id from f_table_opt_relation where OS_ID=:osId))");

        oldDatabaseSql.put(TableName.F_MD_TABLE.name(), "select table_id,table_name,DATABASE_CODE from f_md_table where database_code in (select DATABASE_ID from m_application_resources where os_id=:osId)");
        oldDatabaseSql.put(TableName.F_MD_RELATION.name(), "select RELATION_ID,PARENT_TABLE_ID,CHILD_TABLE_ID from f_md_relation where parent_table_id in (select table_id from f_md_table where database_code in " +
            "(select DATABASE_ID from m_application_resources where os_id=:osId))");

        oldApplicationSql.put(TableName.F_OS_INFO.name(), "select os_id,os_name,default_database from f_os_info where os_id=:osId");
        oldApplicationSql.put(TableName.FILE_LIBRARY_INFO.name(), "select library_name from file_library_info where library_id=:osId");
        oldApplicationSql.put(TableName.F_OPTINFO.name(), "select SOURCE_ID,FORM_CODE,OPT_ID,DOC_ID from f_optinfo where top_opt_id=:osId");
        oldApplicationSql.put(TableName.F_OPTDEF.name(), "select SOURCE_ID,OPT_CODE from f_optdef where opt_id in " +
            "(select opt_id from f_optinfo where top_opt_id=:osId)");
        oldApplicationSql.put(TableName.F_DATABASE_INFO.name(), "select database_code " +
            "from f_database_info where database_code in (select DATABASE_ID from m_application_resources where os_id=:osId)");
        oldApplicationSql.put(TableName.M_APPLICATION_RESOURCES.name(), "select id,os_id,database_id from m_application_resources where os_id=:osId");
        oldApplicationSql.put(TableName.F_TABLE_OPT_RELATION.name(), "select table_id,opt_id,id from f_table_opt_relation where OS_ID=:osId");
        oldApplicationSql.put(TableName.M_META_FORM_MODEL.name(), "select source_id,MODEL_ID from m_meta_form_model where OS_ID=:osId and is_valid='F'");
        oldApplicationSql.put(TableName.Q_DATA_PACKET.name(), "select source_id,packet_id,os_id from q_data_packet where is_disable='F'");
        oldApplicationSql.put(TableName.WF_FLOW_DEFINE.name(), "select SOURCE_ID,FLOW_CODE from wf_flow_define where OS_ID=:osId and flow_state<>'D'");
        oldApplicationSql.put(TableName.WF_NODE.name(), "select SOURCE_ID,NODE_ID from wf_node where flow_code in(" +
            "select flow_code from wf_flow_define where OS_ID=:osId and flow_state<>'D')");
        oldApplicationSql.put(TableName.WF_TRANSITION.name(), "select FLOW_CODE,START_NODE_ID,END_NODE_ID,TRANS_ID from wf_transition where flow_code in(" +
            "select flow_code from wf_flow_define where OS_ID=:osId and flow_state<>'D')");
        oldApplicationSql.put(TableName.WF_OPT_TEAM_ROLE.name(), "select OPT_TEAM_ROLE_ID,OPT_ID,ROLE_CODE from wf_opt_team_role where opt_id in " +
            "(select opt_id from f_optinfo where top_opt_id=:osId)");
        oldApplicationSql.put(TableName.WF_OPT_VARIABLE_DEFINE.name(), "select VARIABLE_NAME,OPT_ID,OPT_VARIABLE_ID from wf_opt_variable_define where opt_id in " +
            "(select opt_id from f_optinfo where top_opt_id=:osId)");
        oldApplicationSql.put(TableName.F_DATACATALOG.name(), "select a.catalog_code,a.source_id,b.os_id from f_datacatalog a join m_application_dictionary b on a.CATALOG_CODE=b.dictionary_id");
        oldApplicationSql.put(TableName.M_APPLICATION_DICTIONARY.name(), "select id,os_id,dictionary_id from m_application_dictionary where os_id=:osId");
    }

    @Override
    public String downModel(String osId) throws FileNotFoundException {
        String fileId = DatetimeOpt.convertDateToString(DatetimeOpt.currentUtilDate(), "YYYYMMddHHmmss");
        String filePath = appHome + File.separator + fileId;
        Map<String, Object> mapApplication = new HashMap<>(1);
        mapApplication.put("osId", osId);
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

    private void createFile(Map<String, Object> map, String sql, String fileName, String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new SecurityException();
            }
        }
        JSONArray jsonArray = DatabaseOptUtils.listObjectsByNamedSqlAsJson(applicationTemplateDao, sql, map);
        DataSet simpleDataSet = new DataSet();
        simpleDataSet.setData(jsonArray);
        CsvDataSet csvDataSet = new CsvDataSet();
        csvDataSet.setFilePath(filePath + File.separator + fileName + ".csv");
        csvDataSet.save(simpleDataSet);
    }

    private void compressFileInfo(String osId, String filePath) throws IOException {
        String fileInfoSql = "select file_id,file_name from file_info where library_id=:osId and file_catalog in ('A','B')";
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
            String fileIdPath = fileInfoPath + File.separator + "(" + fileId + ")" + StringBaseOpt.castObjectToString(object[1]);
            FileSystemOpt.createFile(inputStream, fileIdPath);
        }
        ZipCompressor.compress(fileInfoPath + ".zip", fileInfoPath);
        FileSystemOpt.deleteDirect(fileInfoPath);
    }

    @Override
    public JSONObject uploadModel(File zipFile) throws Exception {
        JSONObject jsonObject = new JSONObject();
        String filePath = appHome + File.separator + "u" + DatetimeOpt.convertDateToString(DatetimeOpt.currentUtilDate(), "YYYYMMddHHmmss");
        ZipCompressor.release(zipFile, filePath);
        List<File> files = FileSystemOpt.findFiles(filePath, "*.csv");
        CsvDataSet csvDataSet = new CsvDataSet();
        for (File file : files) {
            String fileName = FileSystemOpt.extractFileName(file.getPath());
            csvDataSet.setInputStream(new FileInputStream(file.getPath()));
            jsonObject.put(fileName, csvDataSet.load(null));
        }
        jsonObject.put("file", filePath);
        return jsonObject;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer createApp(JSONObject jsonObject, String osId, CentitUserDetails userDetails) {
        try {
            JsonAppVo jsonAppVo = new JsonAppVo(jsonObject, getOldApplication(osId), userDetails, appHome, fileInfoOpt);
            return createApp(jsonAppVo);
        } catch (Exception e) {
            throw new ObjectException(e.getMessage());
        }
    }

    private JSONObject getOldApplication(String osId) {
        if (StringBaseOpt.isNvl(osId)) {
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
                    Pair<Integer, String> pair = metaTableManager.publishDatabase(sDatabaseName, jsonAppVo.getUserCode());
                    if (GeneralAlgorithm.equals(pair.getLeft(), -1)) {
                        logger.error(pair.getRight());
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
