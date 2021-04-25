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
            jsonObject.put(fileName, csvDataSet.load(null).getData());
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
    public JSONObject createApp(JSONObject jsonObject, String isCover, String userCode){
        jsonObject.put("userCode", userCode);
        try {
            int success = createApp(jsonObject, isCover);
            jsonObject.put("success", success);
        } catch (Exception e) {
            throw new ObjectException(e.getMessage());
        }
        return jsonObject;
    }

    private int createApp(JSONObject jsonObject, String isCover) throws Exception {
        int result = 0;
        List<Object> object = new ArrayList<>();
        List<Object> metaObject = new ArrayList<>();
        List<String> listDatabaseName = new ArrayList<>();
        String COVER = "T";
        if (!isCover.equals(COVER)) {
            updatePrimary(jsonObject);
        }
        String userCode = jsonObject.getString("userCode");
        for (String fileName : jsonObject.keySet()) {
            if (jsonObject.get(fileName) instanceof List) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) jsonObject.get(fileName);
                switch (fileName) {
                    case "m_application_info":
                        if (!isCover.equals(COVER)) {
                            object.addAll(convertMap(ApplicationInfo.class, list));
                            ApplicationTeamUser teamUser = new ApplicationTeamUser();
                            teamUser.setApplicationId((String) list.get(0).get("applicationId"));
                            teamUser.setTeamUser(userCode);
                            teamUser.setCreateUser(userCode);
                            object.add(teamUser);
                        }
                        break;
                    case "f_database_info":
                        if (!isCover.equals(COVER)) {
                            object.addAll(convertMap(SourceInfo.class, list));
                        }
                        list.stream().map(s -> (String) s.get("databaseCode")).forEach(listDatabaseName::add);
                        break;
                    case "f_md_table":
                        metaObject.addAll(convertMap(MetaTable.class, list));
                        list.forEach(map -> map.put("tableState", "W"));
                        object.addAll(convertMap(PendingMetaTable.class, list));
                        break;
                    case "f_md_column":
                        metaObject.addAll(convertMap(MetaColumn.class, list));
                        list.forEach(map -> map.put("maxLength", map.get("columnLength")));
                        object.addAll(convertMap(PendingMetaColumn.class, list));
                        break;
                    case "f_md_relation":
                        object.addAll(convertMap(MetaRelation.class, list));
                        break;
                    case "f_md_rel_detail":
                        object.addAll(convertMap(MetaRelDetail.class, list));
                        break;
                    case "m_meta_form_model":
                        object.addAll(convertMap(MetaFormModel.class, list));
                        break;
                    case "q_data_packet":
                        object.addAll(convertMap(DataPacket.class, list));
                        break;
                    case "q_data_packet_param":
                        object.addAll(convertMap(DataPacketParam.class, list));
                        break;
                    case "f_group_table":
                        object.addAll(convertMap(GroupInfo.class, list));
                        break;
                    default:
                        break;
                }
            }
        }
        try {
            if (object.size() > 0) {
                result += DatabaseOptUtils.batchMergeObjects(databaseInfoDao, object);
                for (String s : listDatabaseName) {
                    Pair<Integer, String> pair = metaTableManager.publishDatabase(s, userCode);
                    if (GeneralAlgorithm.equals(pair.getLeft(), -1)) {
                        throw new Exception(pair.getRight());
                    }
                }
            }
            if (metaObject.size() > 0) {
                result += DatabaseOptUtils.batchMergeObjects(databaseInfoDao, metaObject);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return result;
    }

    private List<Object> convertMap(Class type, List<Map<String, Object>> list) throws InstantiationException, IllegalAccessException {
        JavaBeanMetaData javaBeanMetaData = JavaBeanMetaData.createBeanMetaDataFromType(type);
        List<Object> object = new ArrayList<>();
        for (Map map : list) {
            object.add(javaBeanMetaData.createBeanObjectFromMap(map));
        }
        return object;
    }

    private void updatePrimary(JSONObject jsonObject) {
        List<Map<String, Object>> list = (List<Map<String, Object>>) jsonObject.get("m_application_info");
        String applicationId = UuidOpt.getUuidAsString22();
        if (list != null) {
            list.forEach(map -> map.put("applicationId", applicationId));
        }
        Map<String, Object> databaseMap = new HashMap<>();
        list = (List<Map<String, Object>>) jsonObject.get("f_database_info");
        if (list != null) {
            list.forEach(map -> {
                String uuid = UuidOpt.getUuidAsString22();
                databaseMap.put((String) map.get("databaseCode"), uuid);
                map.put("databaseCode", uuid);
                map.put("osId", applicationId);
            });
        }

        Map<String, Object> mdtableMap = new HashMap<>();
        list = (List<Map<String, Object>>) jsonObject.get("f_md_table");
        if (list != null) {
            list.forEach(map -> {
                String uuid = UuidOpt.getUuidAsString32();
                mdtableMap.put((String) map.get("tableId"), uuid);
                map.put("tableId", uuid);
                databaseMap.keySet().stream().filter(key -> key.equals(map.get("databaseCode")))
                    .findFirst().ifPresent(key -> map.put("databaseCode", databaseMap.get(key)));
            });
        }

        list = (List<Map<String, Object>>) jsonObject.get("f_md_column");
        if (list != null) {
            list.forEach(map -> {
                mdtableMap.keySet().stream().filter(key -> key.equals(map.get("tableId")))
                    .findFirst().ifPresent(key -> map.put("tableId", mdtableMap.get(key)));
            });
        }

        Map<String, Object> relationMap = new HashMap<>();
        list = (List<Map<String, Object>>) jsonObject.get("f_md_relation");
        if (list != null) {
            list.forEach(map -> {
                String uuid = UuidOpt.getUuidAsString22();
                relationMap.put((String) map.get("relationId"), uuid);
                map.put("relationId", uuid);
                mdtableMap.keySet().stream().filter(key -> key.equals(map.get("parentTableId")))
                    .findFirst().ifPresent(key -> map.put("parentTableId", mdtableMap.get(key)));
                mdtableMap.keySet().stream().filter(key -> key.equals(map.get("childTableId")))
                    .findFirst().ifPresent(key -> map.put("childTableId", mdtableMap.get(key)));
            });
        }

        list = (List<Map<String, Object>>) jsonObject.get("f_md_rel_detail");
        if (list != null) {
            list.forEach(map -> relationMap.keySet().stream().filter(key -> key.equals(map.get("relationId")))
                .findFirst().ifPresent(key -> map.put("relationId", relationMap.get(key))));
        }

        Map<String, Object> datapacketMap = new HashMap<>();
        list = (List<Map<String, Object>>) jsonObject.get("q_data_packet");
        if (list != null) {
            list.forEach(map -> {
                String uuid = UuidOpt.getUuidAsString22();
                datapacketMap.put((String) map.get("packetId"), uuid);
                map.put("packetId", uuid);
                map.put("applicationId", applicationId);
            });
            list.forEach(map -> {
                String form = (String) map.get("dataOptDescJson");
                for (String key : mdtableMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) mdtableMap.get(key));
                }
                for (String key : databaseMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) databaseMap.get(key));
                }
                for (String key : datapacketMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) datapacketMap.get(key));
                }
                map.put("dataOptDescJson", form);
            });
        }

        list = (List<Map<String, Object>>) jsonObject.get("q_data_packet_param");
        if (list != null) {
            list.forEach(map -> datapacketMap.keySet().stream().filter(key -> key.equals(map.get("packetId")))
                .findFirst().ifPresent(key -> map.put("packetId", datapacketMap.get(key))));
        }
        Map<String, Object> groupMap = new HashMap<>();
        list = (List<Map<String, Object>>) jsonObject.get("f_group_table");
        if (list != null) {
            list.forEach(map -> {
                String uuid = UuidOpt.getUuidAsString22();
                groupMap.put((String) map.get("groupId"), uuid);
                map.put("groupId", uuid);
                map.put("applicationId", applicationId);
            });
        }
        list = (List<Map<String, Object>>) jsonObject.get("m_meta_form_model");
        Map<String, Object> metaformMap = new HashMap<>();
        if (list != null) {
            list.forEach(map -> {
                String uuid = UuidOpt.getUuidAsString36();
                metaformMap.put((String) map.get("modelId"), uuid);
                map.put("modelId", uuid);
                map.put("applicationId", applicationId);
                mdtableMap.keySet().stream().filter(key -> key.equals(map.get("tableId")))
                    .findFirst().ifPresent(key -> map.put("tableId", mdtableMap.get(key)));
                databaseMap.keySet().stream().filter(key -> key.equals(map.get("databaseCode")))
                    .findFirst().ifPresent(key -> map.put("databaseCode", databaseMap.get(key)));
                groupMap.keySet().stream().filter(key -> key.equals(map.get("ownGroup")))
                    .findFirst().ifPresent(key -> map.put("ownGroup", groupMap.get(key)));
            });
            list.forEach(map -> {
                String form = (String) map.get("formTemplate");
                for (String key : metaformMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) metaformMap.get(key));
                }
                for (String key : mdtableMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) mdtableMap.get(key));
                }
                for (String key : databaseMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) databaseMap.get(key));
                }
                for (String key : datapacketMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) datapacketMap.get(key));
                }
                map.put("formTemplate", form);
            });
        }
    }
}
