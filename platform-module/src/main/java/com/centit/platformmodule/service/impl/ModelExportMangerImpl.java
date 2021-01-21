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
import com.centit.platformmodule.po.ApplicationInfo;
import com.centit.platformmodule.service.ModelExportManager;
import com.centit.product.dbdesign.po.PendingMetaColumn;
import com.centit.product.dbdesign.po.PendingMetaTable;
import com.centit.product.dbdesign.service.MetaTableManager;
import com.centit.product.metadata.dao.DatabaseInfoDao;
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
    private DatabaseInfoDao databaseInfoDao;
    @Autowired
    private MetaTableManager metaTableManager;

    @Override
    public InputStream downModel(String applicationId) throws FileNotFoundException {
        String filePath = appHome + File.separator + DatetimeOpt.convertDateToString(DatetimeOpt.currentUtilDate(), "YYYYMMddHHmmss");
        Map<String, Object> mapApplicaton = new HashMap<>();
        mapApplicaton.put("applicationId", applicationId);
        String sql = "select * from m_application_info where APPLICATION_ID=:applicationId";
        createFile(mapApplicaton, sql, "m_application_info", filePath);

//        sql = "select * from f_database_info where os_id=:applicationId";
        JSONArray jsonArrayDatabase = createFile(mapApplicaton, sql, "f_database_info", filePath);
        String[] databaseCodes = new String[jsonArrayDatabase.size()];
        for (int i = 0; i < jsonArrayDatabase.size(); i++) {
            JSONObject jsonObject = jsonArrayDatabase.getJSONObject(i);
            databaseCodes[i] = (String) jsonObject.get("databaseCode");
        }
        if (databaseCodes.length > 0) {
            Map<String, Object> mapDatabase = new HashMap<>();
            mapDatabase.put("databaseCode", databaseCodes);
            sql = "select * from f_md_table where database_code in (:databaseCode)";
//        sql = "select * from f_md_table where database_code in (select database_code from " +
//            "f_database_info where os_id=:applicationId)";
            createFile(mapDatabase, sql, "f_md_table", filePath);
            sql = "select * from f_md_column where table_id in (select table_id from f_md_table where database_code in (:databaseCode))";
//        sql = "select * from f_md_column where table_id in (select table_id from f_md_table where database_code in (select database_code from " +
//            "f_database_info where os_id=:applicationId))";
            createFile(mapDatabase, sql, "f_md_column", filePath);
            sql = "select * from f_md_relation where parent_table_id in (select table_id from f_md_table where database_code in (:databaseCode))";
//        sql = "select * from f_md_relation where parent_table_id in (select table_id from f_md_table where database_code in (select database_code from " +
//            "f_database_info where os_id=:applicationId))";
            createFile(mapDatabase, sql, "f_md_relation", filePath);
            sql = "select * from f_md_rel_detail where relation_id in (select relation_id from f_md_relation where parent_table_id in (select table_id from " +
                "f_md_table where database_code in (:databaseCode)))";
//        sql = "select * from f_md_rel_detail where relation_id in (select relation_id from f_md_relation where parent_table_id in (select table_id from " +
//            "f_md_table where database_code in (select database_code from " +
//            "f_database_info where os_id=:applicationId)))";
            createFile(mapDatabase, sql, "f_md_rel_detail", filePath);
        }
        sql = "select * from m_meta_form_model where APPLICATION_ID=:applicationId";
        createFile(mapApplicaton, sql, "m_meta_form_model", filePath);
        sql = "select * from f_datacatalog where opt_ID=:applicationId";
        createFile(mapApplicaton, sql, "f_datacatalog", filePath);

        sql = "select * from f_datadictionary where catalog_code in (" +
            "select catalog_code from f_datacatalog where opt_ID=:applicationId)";
        createFile(mapApplicaton, sql, "f_datadictionary", filePath);

        sql = "select * from q_chart_model where APPLICATION_ID=:applicationId";
        createFile(mapApplicaton, sql, "q_chart_model", filePath);

        sql = "select * from q_data_packet where APPLICATION_ID=:applicationId";
        createFile(mapApplicaton, sql, "q_data_packet", filePath);

        sql = "select * from q_data_packet_param where packet_id in (" +
            "select packet_id from q_data_packet where APPLICATION_ID=:applicationId)";
        createFile(mapApplicaton, sql, "q_data_packet_param", filePath);

        sql = "select * from q_dataset_define where packet_id in (" +
            "select packet_id from q_data_packet where APPLICATION_ID=:applicationId)";
        createFile(mapApplicaton, sql, "q_dataset_define", filePath);

        sql = "select * from q_dataset_columndesc where packet_id in (" +
            "select packet_id from q_data_packet where APPLICATION_ID=:applicationId)";
        createFile(mapApplicaton, sql, "q_dataset_columndesc", filePath);

        sql = "select * from m_page_model where APPLICATION_ID=:applicationId";
        createFile(mapApplicaton, sql, "m_page_model", filePath);

        ZipCompressor.compress(filePath + ".zip", filePath);
        FileSystemOpt.deleteDirect(filePath);
        InputStream in = new FileInputStream(filePath + ".zip");
        FileSystemOpt.deleteFile(filePath + ".zip");
        return in;
    }

    private JSONArray createFile(Map<String, Object> map, String sql, String fileName, String filePath) {
        JSONArray jsonArray = new JSONArray();
        if ("f_database_info".equals(fileName)) {
            for (DatabaseInfo db : databaseInfoDao.listDatabase()) {
                if (StringUtils.isNotBlank(db.getOsId()) && db.getOsId().equals(map.get("applicationId"))) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("databaseCode", db.getDatabaseCode());
                    jsonObject.put("osId", db.getOsId());
                    jsonObject.put("databaseName", db.getDatabaseName());
                    jsonObject.put("databaseUrl", db.getDatabaseUrl());
                    jsonObject.put("username", db.getUsername());
                    jsonObject.put("password", "");
                    jsonArray.add(jsonObject);
                }
            }
        } else {
            jsonArray = DatabaseOptUtils.listObjectsByNamedSqlAsJson(databaseInfoDao, sql, map);
        }
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

    @Override
    @Transactional
    public JSONObject uploadModel(File zipFile, String isCover) {
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
        for (String fileName : jsonObject.keySet()) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) jsonObject.get(fileName);
            switch (fileName) {
                case "m_application_info":
                    if (!isCover.equals(COVER)) {
                        object.addAll(convertMap(ApplicationInfo.class, list));
                    }
                    break;
                case "f_database_info":
                    if (!isCover.equals(COVER)) {
                        object.addAll(convertMap(DatabaseInfo.class, list));
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
                case "f_datacatalog":
                    object.addAll(convertMap(DataCatalog.class, list));
                    break;
                case "f_datadictionary":
                    object.addAll(convertMap(DataDictionary.class, list));
                    break;
                case "q_data_packet":
                    object.addAll(convertMap(DataPacket.class, list));
                    break;
                case "q_data_packet_param":
                    object.addAll(convertMap(DataPacketParam.class, list));
                    break;
                default:
                    break;
            }
        }
        try {
            if (object.size() > 0) {
                result += DatabaseOptUtils.batchMergeObjects(databaseInfoDao, object);
                for (String s : listDatabaseName) {
                    Pair<Integer, String> pair = metaTableManager.publishDatabase(s, "admin");
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

    private JSONObject updatePrimary(JSONObject jsonObject) {
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

        Map<String, Object> dictionaryMap = new HashMap<>();
        list = (List<Map<String, Object>>) jsonObject.get("f_datacatalog");
        if (list != null) {
            list.forEach(map -> {
                String uuid = UuidOpt.getUuidAsString22();
                dictionaryMap.put((String) map.get("catalogCode"), uuid);
                map.put("catalogCode", uuid);
                map.put("optId", applicationId);
            });
        }

        list = (List<Map<String, Object>>) jsonObject.get("f_datadictionary");
        if (list != null) {
            list.forEach(map -> dictionaryMap.keySet().stream().filter(key -> key.equals(map.get("catalogCode")))
                .findFirst().ifPresent(key -> map.put("catalogCode", dictionaryMap.get(key))));
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
                dictionaryMap.keySet().stream().filter(key -> key.equals(map.get("referenceData")))
                    .findFirst().ifPresent(key -> map.put("referenceData", dictionaryMap.get(key)));
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
        }

        list = (List<Map<String, Object>>) jsonObject.get("q_data_packet_param");
        if (list != null) {
            list.forEach(map -> datapacketMap.keySet().stream().filter(key -> key.equals(map.get("packetId")))
                .findFirst().ifPresent(key -> map.put("packetId", datapacketMap.get(key))));
        }

        Map<String, Object> datasetdefineMap = new HashMap<>();
        list = (List<Map<String, Object>>) jsonObject.get("q_dataset_define");
        if (list != null) {
            list.forEach(map -> {
                String uuid = UuidOpt.getUuidAsString22();
                datasetdefineMap.put((String) map.get("queryId"), uuid);
                map.put("queryId", uuid);
                databaseMap.keySet().stream().filter(key -> key.equals(map.get("databaseCode")))
                    .findFirst().ifPresent(key -> map.put("databaseCode", databaseMap.get(key)));
                datapacketMap.keySet().stream().filter(key -> key.equals(map.get("packetId")))
                    .findFirst().ifPresent(key -> map.put("packetId", datapacketMap.get(key)));
            });
        }

        list = (List<Map<String, Object>>) jsonObject.get("q_dataset_columndesc");
        if (list != null) {
            list.forEach(map -> {
                datapacketMap.keySet().stream().filter(key -> key.equals(map.get("packetId")))
                    .findFirst().ifPresent(key -> map.put("packetId", datapacketMap.get(key)));
                datasetdefineMap.keySet().stream().filter(key -> key.equals(map.get("queryId")))
                    .findFirst().ifPresent(key -> map.put("queryId", datasetdefineMap.get(key)));
            });
        }

        list = (List<Map<String, Object>>) jsonObject.get("q_data_packet");
        if (list != null) {
            list.forEach(map -> {
                String form = (String) map.get("dataOptDescJson");
                for (String key : datasetdefineMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) datasetdefineMap.get(key));
                }
                map.put("dataOptDescJson", form);
            });
        }

        Map<String, Object> chartMap = new HashMap<>();
        list = (List<Map<String, Object>>) jsonObject.get("q_chart_model");
        if (list != null) {
            list.forEach(map -> {
                String uuid = UuidOpt.getUuidAsString22();
                chartMap.put((String) map.get("chartId"), uuid);
                map.put("chartId", uuid);
                datapacketMap.keySet().stream().filter(key -> key.equals(map.get("packetId")))
                    .findFirst().ifPresent(key -> map.put("packetId", datapacketMap.get(key)));
                datasetdefineMap.keySet().stream().filter(key -> key.equals(map.get("queryId")))
                    .findFirst().ifPresent(key -> map.put("queryId", datasetdefineMap.get(key)));
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
                for (String key : datasetdefineMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) datasetdefineMap.get(key));
                }
                for (String key : dictionaryMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) dictionaryMap.get(key));
                }
                map.put("formTemplate", form);
            });
        }

        Map<String, Object> pageMap = new HashMap<>();
        list = (List<Map<String, Object>>) jsonObject.get("m_page_model");
        if (list != null) {
            list.forEach(map -> {
                String uuid = UuidOpt.getUuidAsString22();
                pageMap.put((String) map.get("pageCode"), uuid);
                map.put("pageCode", uuid);
                map.put("applicationId", applicationId);
            });
            list.forEach(map -> {
                String form = (String) map.get("pageDesignJson");
                for (String key : metaformMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) metaformMap.get(key));
                }
                for (String key : pageMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) pageMap.get(key));
                }
                for (String key : chartMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) chartMap.get(key));
                }
                map.put("pageDesignJson", form);
            });
        }
        return jsonObject;
    }
}
