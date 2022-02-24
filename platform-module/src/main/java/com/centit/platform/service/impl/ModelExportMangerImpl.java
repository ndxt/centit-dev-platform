package com.centit.platform.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.dde.core.SimpleDataSet;
import com.centit.dde.dataset.CsvDataSet;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.platform.dao.ApplicationTemplateDao;
import com.centit.platform.service.ModelExportManager;
import com.centit.platform.vo.JsonAppVo;
import com.centit.platform.vo.TableName;
import com.centit.product.dbdesign.service.MetaTableManager;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.GeneralAlgorithm;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.algorithm.ZipCompressor;
import com.centit.support.common.ObjectException;
import com.centit.support.file.FileSystemOpt;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
    @Autowired
    private ApplicationTemplateDao applicationTemplateDao;
    @Autowired
    private MetaTableManager metaTableManager;
    private final Map<String, String> applicationSql = new HashMap<>(16);
    private final Map<String, String> dataBaseSql = new HashMap<>(4);

    @PostConstruct
    void init() {
        applicationSql.put(TableName.F_OS_INFO.name(), "select * from f_os_info where os_id=:osId");
        applicationSql.put(TableName.FILE_LIBRARY_INFO.name(), "select * from file_library_info where library_id=:osId");
        applicationSql.put(TableName.F_OPTINFO.name(), "select * from f_optinfo where top_opt_id=:osId");
        applicationSql.put(TableName.F_OPTDEF.name(), "select * from f_optdef where opt_id in " +
            "(select opt_id from f_optinfo where top_opt_id=:osId)");
        applicationSql.put(TableName.F_DATABASE_INFO.name(), "select database_code,top_unit,database_name,database_desc,source_type " +
            "from f_database_info where database_code in (select DATABASE_ID from m_application_resources where os_id=:osId)");
        applicationSql.put(TableName.M_APPLICATION_RESCOURSE.name(), "select * from m_application_resources where os_id=:osId");
        applicationSql.put(TableName.F_TABLE_OPT_RELATION.name(), "select * from f_table_opt_relation where OS_ID=:osId");
        applicationSql.put(TableName.M_META_FORM_MODEL.name(), "select * from m_meta_form_model where OS_ID=:osId");
        applicationSql.put(TableName.Q_DATA_PACKET.name(), "select * from q_data_packet where OS_ID=:osId");
        applicationSql.put(TableName.Q_DATA_PACKET_PARAM.name(), "select * from q_data_packet_param where packet_id in (" +
            "select packet_id from q_data_packet where OS_ID=:osId)");
        applicationSql.put(TableName.WF_FLOW_DEFINE.name(), "select * from wf_flow_define where OS_ID=:osId");
        applicationSql.put(TableName.WF_NODE.name(), "select * from wf_node where flow_code in(" +
            "select flow_code from wf_flow_define where OS_ID=:osId)");
        applicationSql.put(TableName.WF_TRANSITION.name(), "select * from wf_transition where flow_code in(" +
            "select flow_code from wf_flow_define where OS_ID=:osId)");
        applicationSql.put(TableName.WF_FLOW_STAGE.name(), "select * from wf_flow_stage where flow_code in(" +
            "select flow_code from wf_flow_define where OS_ID=:osId)");
        applicationSql.put(TableName.WF_OPT_TEAM_ROLE.name(), "select * from wf_opt_team_role where opt_id in " +
            "(select opt_id from f_optinfo where top_opt_id=:osId)");
        applicationSql.put(TableName.WF_OPT_VARIABLE_DEFINE.name(), "select * from wf_opt_variable_define where opt_id in " +
            "(select opt_id from f_optinfo where top_opt_id=:osId)");
        applicationSql.put(TableName.F_DATACATALOG.name(), "select * from f_datacatalog where CATALOG_CODE in " +
            "(select dictionary_id from m_application_dictionary where os_id=:osId)");
        applicationSql.put(TableName.F_DATADICTIONARY.name(), "select * from f_datadictionary where CATALOG_CODE in " +
            "(select dictionary_id from m_application_dictionary where os_id=:osId)");

        dataBaseSql.put(TableName.F_MD_TABLE.name(), "select * from f_md_table where table_id in (select table_id from f_table_opt_relation where OS_ID=:osId)");
        dataBaseSql.put(TableName.F_MD_COLUMN.name(), "select * from f_md_column where table_id in (select table_id from f_table_opt_relation where OS_ID=:osId)");
        dataBaseSql.put(TableName.F_MD_RELATION.name(), "select * from f_md_relation where parent_table_id in (select table_id from f_table_opt_relation where OS_ID=:osId)");
        dataBaseSql.put(TableName.F_MD_REL_DETAIL.name(), "select * from f_md_rel_detail where relation_id in (select relation_id from f_md_relation where parent_table_id in " +
            "(select table_id from f_table_opt_relation where OS_ID=:osId))");
    }

    @Override
    public InputStream downModel(String osId) throws FileNotFoundException {
        String filePath = appHome + File.separator + DatetimeOpt.convertDateToString(DatetimeOpt.currentUtilDate(), "YYYYMMddHHmmss");
        Map<String, Object> mapApplication = new HashMap<>(1);
        mapApplication.put("osId", osId);
        String[] databaseCodes = new String[0];
        for (Map.Entry<String, String> entry : applicationSql.entrySet()) {
            if (TableName.F_DATABASE_INFO.name().equals(entry.getKey())) {
                JSONArray jsonArrayDatabase = createFile(mapApplication, entry.getValue(), entry.getKey(), filePath);
                databaseCodes = new String[jsonArrayDatabase.size()];
                for (int i = 0; i < jsonArrayDatabase.size(); i++) {
                    JSONObject jsonObject = jsonArrayDatabase.getJSONObject(i);
                    databaseCodes[i] = (String) jsonObject.get("databaseCode");
                }
                continue;
            }
            createFile(mapApplication, entry.getValue(), entry.getKey(), filePath);
        }
        if (databaseCodes.length > 0) {
            Map<String, Object> mapDatabase = new HashMap<>(10);
            mapDatabase.put("databaseCode", databaseCodes);
            for (Map.Entry<String, String> entry : dataBaseSql.entrySet()) {
                createFile(mapDatabase, entry.getValue(), entry.getKey(), filePath);
            }
        }
        ZipCompressor.compress(filePath + ".zip", filePath);
        FileSystemOpt.deleteDirect(filePath);
        InputStream in = new FileInputStream(filePath + ".zip");
        FileSystemOpt.deleteFile(filePath + ".zip");
        return in;
    }

    private JSONArray createFile(Map<String, Object> map, String sql, String fileName, String filePath) throws FileNotFoundException {
        JSONArray jsonArray = DatabaseOptUtils.listObjectsByNamedSqlAsJson(applicationTemplateDao, sql, map);
        SimpleDataSet simpleDataSet = new SimpleDataSet();
        simpleDataSet.setData(jsonArray);
        CsvDataSet csvDataSet = new CsvDataSet();
        File file = new File(filePath);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new SecurityException();
            }
        }
        csvDataSet.setFilePath(filePath + File.separator + fileName + ".csv");
        csvDataSet.save(simpleDataSet);
        return jsonArray;
    }

    @Override
    public JSONObject uploadModel(File zipFile) throws Exception {
        return getFileContent(zipFile);
    }

    private JSONObject getFileContent(File zipFile) throws Exception {
        JSONObject jsonObject = new JSONObject();
        String filePath = appHome + File.separator + "u" + DatetimeOpt.convertDateToString(DatetimeOpt.currentUtilDate(), "YYYYMMddHHmmss");
        ZipCompressor.release(zipFile, filePath);
        List<File> files = FileSystemOpt.findFiles(filePath, "*.csv");
        CsvDataSet csvDataSet = new CsvDataSet();
        for (File file : files) {
            String fileName = FileSystemOpt.extractFileName(file.getPath());
            csvDataSet.setFilePath(file.getPath());
            jsonObject.put(fileName, csvDataSet.load(null));
        }
        FileSystemOpt.deleteDirect(filePath);
        return jsonObject;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer createApp(JSONObject jsonObject, String osId, CentitUserDetails userDetails) {
        try {
            JsonAppVo jsonAppVo = new JsonAppVo(jsonObject, getOldApplication(osId), userDetails);
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
        for (Map.Entry<String, String> entry : applicationSql.entrySet()) {
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
                        throw new Exception(pair.getRight());
                    }
                }
            }
            if (jsonAppVo.getMetaObject().size() > 0) {
                result += DatabaseOptUtils.batchMergeObjects(applicationTemplateDao, jsonAppVo.getMetaObject());
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return result;
    }
}
