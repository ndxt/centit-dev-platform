package com.centit.platformmodule.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.dde.core.SimpleDataSet;
import com.centit.dde.dataset.CsvDataSet;
import com.centit.dde.po.DataPacket;
import com.centit.dde.po.DataPacketParam;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.framework.system.po.DataCatalog;
import com.centit.framework.system.po.DataDictionary;
import com.centit.metaform.po.MetaFormModel;
import com.centit.platformmodule.Vo.JsonAppVo;
import com.centit.platformmodule.Vo.TableName;
import com.centit.platformmodule.po.ApplicationInfo;
import com.centit.platformmodule.po.ApplicationTeamUser;
import com.centit.platformmodule.po.GroupInfo;
import com.centit.platformmodule.service.ModelExportManager;
import com.centit.product.dbdesign.po.PendingMetaColumn;
import com.centit.product.dbdesign.po.PendingMetaTable;
import com.centit.product.dbdesign.service.MetaTableManager;
import com.centit.product.metadata.dao.SourceInfoDao;
import com.centit.product.metadata.po.*;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.GeneralAlgorithm;
import com.centit.support.algorithm.UuidOpt;
import com.centit.support.algorithm.ZipCompressor;
import com.centit.support.common.JavaBeanMetaData;
import com.centit.support.common.ObjectException;
import com.centit.support.file.FileSystemOpt;
import org.apache.commons.lang3.StringUtils;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModelExportMangerImpl implements ModelExportManager {
    @Value("${app.home:./}")
    private String appHome;
    @Autowired
    private SourceInfoDao databaseInfoDao;
    @Autowired
    private MetaTableManager metaTableManager;
    private Map<String, String> applicationSql = new HashMap<>(10);
    private Map<String, String> dataBaseSql = new HashMap<>(4);

    @PostConstruct
    void init() {
        applicationSql.put(TableName.M_APPLICATION_INFO.name(), "select * from m_application_info where APPLICATION_ID=:applicationId");
        applicationSql.put(TableName.F_DATABASE_INFO.name(), "select database_code,database_name,os_id,database_url,database_desc,source_type,ext_props from f_database_info where os_id=:applicationId");
        applicationSql.put(TableName.M_META_FORM_MODEL.name(), "select * from m_meta_form_model where APPLICATION_ID=:applicationId");
        applicationSql.put(TableName.F_GROUP_TABLE.name(), "select * from f_group_table where APPLICATION_ID=:applicationId");
        applicationSql.put(TableName.Q_DATA_PACKET.name(), "select * from q_data_packet where APPLICATION_ID=:applicationId");
        applicationSql.put(TableName.Q_DATA_PACKET_PARAM.name(), "select * from q_data_packet_param where packet_id in (" +
            "select packet_id from q_data_packet where APPLICATION_ID=:applicationId)");
        dataBaseSql.put(TableName.F_MD_TABLE.name(), "select * from f_md_table where database_code in (:databaseCode)");
        dataBaseSql.put(TableName.F_MD_COLUMN.name(), "select * from f_md_column where table_id in (select table_id from f_md_table where database_code in (:databaseCode))");
        dataBaseSql.put(TableName.F_MD_RELATION.name(), "select * from f_md_relation where parent_table_id in (select table_id from f_md_table where database_code in (:databaseCode))");
        dataBaseSql.put(TableName.F_MD_REL_DETAIL.name(), "select * from f_md_rel_detail where relation_id in (select relation_id from f_md_relation where parent_table_id in (select table_id from f_md_table where database_code in (:databaseCode)))");
    }

    @Override
    public InputStream downModel(String applicationId) throws FileNotFoundException {
        String filePath = appHome + File.separator + DatetimeOpt.convertDateToString(DatetimeOpt.currentUtilDate(), "YYYYMMddHHmmss");
        Map<String, Object> mapApplication = new HashMap<>();
        mapApplication.put("applicationId", applicationId);
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
            Map<String, Object> mapDatabase = new HashMap<>();
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
        JSONArray jsonArray = DatabaseOptUtils.listObjectsByNamedSqlAsJson(databaseInfoDao, sql, map);
        SimpleDataSet simpleDataSet = new SimpleDataSet();
        simpleDataSet.setData(jsonArray);
        CsvDataSet csvDataSet = new CsvDataSet();
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        csvDataSet.setFilePath(filePath + File.separator + fileName + ".csv");
        csvDataSet.save(simpleDataSet);
        return jsonArray;
    }

    private JSONObject getFileContent(File zipFile) throws FileNotFoundException {
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
    public JSONObject uploadModel(File zipFile) throws FileNotFoundException {
        return getFileContent(zipFile);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer createApp(JSONObject jsonObject, String isCover, String userCode){
        try {
            JsonAppVo jsonAppVo=new JsonAppVo(jsonObject,isCover,userCode);
            return createApp(jsonAppVo);
        } catch (Exception e) {
            throw new ObjectException(e.getMessage());
        }
    }

    private Integer createApp(JsonAppVo jsonAppVo) throws Exception {
        int result = 0;
        jsonAppVo.prepareApp();
        try {
            if (jsonAppVo.getObject().size() > 0) {
                result += DatabaseOptUtils.batchMergeObjects(databaseInfoDao, jsonAppVo.getObject());
                for (String sDatabaseName : jsonAppVo.getListDatabaseName()) {
                    Pair<Integer, String> pair = metaTableManager.publishDatabase(sDatabaseName, jsonAppVo.getUserCode());
                    if (GeneralAlgorithm.equals(pair.getLeft(), -1)) {
                        throw new Exception(pair.getRight());
                    }
                }
            }
            if (jsonAppVo.getMetaObject().size() > 0) {
                result += DatabaseOptUtils.batchMergeObjects(databaseInfoDao, jsonAppVo.getMetaObject());
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return result;
    }
}
