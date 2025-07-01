package com.centit.locode.platform.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.dde.adapter.DdeDubboTaskRun;
import com.centit.fileserver.common.FileInfoOpt;
import com.centit.fileserver.po.FileInfo;
import com.centit.framework.common.ResponseData;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.OsInfo;
import com.centit.framework.model.basedata.WorkGroup;
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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    @Autowired
    private PlatformEnvironment platformEnvironment;
    private static final String F_DATABASE_INFO = "F_DATABASE_INFO";
    private final Map<String, String> applicationSql = new HashMap<>(16);
    private final Map<String, String> oldApplicationSql = new HashMap<>(16);
    private final Map<String, String> newDatabaseSql = new HashMap<>(4);
    private final Map<String, String> oldDatabaseSql = new HashMap<>(4);

    @PostConstruct
    void init() {
        applicationSql.put(AppTableNames.F_OS_INFO.name(), "select os_id,page_flow from f_os_info where [:osId | os_id=:osId]");
        applicationSql.put(AppTableNames.F_OPTINFO.name(), "select * from f_optinfo where [:osId | top_opt_id=:osId]");
        applicationSql.put(AppTableNames.F_OPTDEF.name(), "select * from f_optdef where opt_id in " + "(select opt_id from f_optinfo where [:osId | top_opt_id=:osId] [:(splitforin)optId | and opt_id in (:optId)])");
        applicationSql.put(AppTableNames.F_DATABASE_INFO.name(), "select database_code,top_unit,database_name,database_desc,source_type " + "from f_database_info where database_code in (select DATABASE_ID from m_application_resources where [:osId | os_id=:osId])");
        applicationSql.put(AppTableNames.M_APPLICATION_RESOURCES.name(), "select * from m_application_resources where [:osId | os_id=:osId]");
        applicationSql.put(AppTableNames.F_TABLE_OPT_RELATION.name(), "select a.* from f_table_opt_relation a join f_md_table b on a.table_id=b.TABLE_ID where [:osId | a.os_id=:osId] [:(splitforin)optId | and opt_id in (:optId)]");
        applicationSql.put(AppTableNames.M_META_FORM_MODEL.name(), "select * from m_meta_form_model where [:osId | OS_ID=:osId] [:(splitforin)optId | and opt_id in (:optId)]");
        applicationSql.put(AppTableNames.Q_DATA_PACKET.name(), "select * from q_data_packet where [:osId | OS_ID=:osId] [:(splitforin)optId | and opt_id in (:optId)]");
        applicationSql.put(AppTableNames.WF_FLOW_DEFINE.name(), "select * from wf_flow_define where [:osId | OS_ID=:osId] and (flow_state<>'C' or version=0) [:(splitforin)optId | and opt_id in (:optId)]");
        applicationSql.put(AppTableNames.WF_NODE.name(), "select * from wf_node where (flow_code,version) in(" + "select flow_code,version from wf_flow_define where [:osId | OS_ID=:osId] and flow_state='B' [:(splitforin)optId | and opt_id in (:optId)])");
        applicationSql.put(AppTableNames.WF_TRANSITION.name(), "select * from wf_transition where (flow_code,version) in(" + "select flow_code,version from wf_flow_define where [:osId | OS_ID=:osId] and flow_state='B' [:(splitforin)optId | and opt_id in (:optId)])");
        applicationSql.put(AppTableNames.WF_FLOW_STAGE.name(), "select * from wf_flow_stage where (flow_code,version) in(" + "select flow_code,version from wf_flow_define where [:osId | OS_ID=:osId] and flow_state='B' [:(splitforin)optId | and opt_id in (:optId)])");
        applicationSql.put(AppTableNames.WF_OPT_TEAM_ROLE.name(), "select * from wf_opt_team_role where opt_id in " + "(select opt_id from f_optinfo where [:osId | top_opt_id=:osId] [:(splitforin)optId | and opt_id in (:optId)])");
        applicationSql.put(AppTableNames.WF_OPT_VARIABLE_DEFINE.name(), "select * from wf_opt_variable_define where opt_id in " + "(select opt_id from f_optinfo where [:osId | top_opt_id=:osId] [:(splitforin)optId | and opt_id in (:optId)])");
        applicationSql.put(AppTableNames.F_DATACATALOG.name(), "select * from f_datacatalog where CATALOG_CODE in " + "(select dictionary_id from m_application_dictionary where [:osId | os_id=:osId]) and catalog_style<>'S'");
        applicationSql.put(AppTableNames.F_DATADICTIONARY.name(), "select * from f_datadictionary where CATALOG_CODE in " + "(select a.dictionary_id from m_application_dictionary a join f_datacatalog b on a.dictionary_id=b.CATALOG_CODE where [:osId | a.os_id=:osId] and catalog_style<>'S')");
        applicationSql.put(AppTableNames.M_APPLICATION_DICTIONARY.name(), "select * from m_application_dictionary where [:osId | os_id=:osId]");

        newDatabaseSql.put(AppTableNames.F_MD_TABLE.name(), "select * from f_md_table where table_id in (select table_id from f_table_opt_relation where [:osId | OS_ID=:osId] [:(splitforin)optId | and opt_id in (:optId)])" + " and database_code in (select DATABASE_ID from m_application_resources where [:osId | os_id=:osId])");
        newDatabaseSql.put(AppTableNames.F_MD_COLUMN.name(), "select * from f_md_column where table_id in (select table_id from f_table_opt_relation where [:osId | OS_ID=:osId] [:(splitforin)optId | and opt_id in (:optId)])");
        newDatabaseSql.put(AppTableNames.F_MD_RELATION.name(), "select * from f_md_relation where parent_table_id in (select table_id from f_table_opt_relation where [:osId | OS_ID=:osId] [:(splitforin)optId | and opt_id in (:optId)])");
        newDatabaseSql.put(AppTableNames.F_MD_REL_DETAIL.name(), "select * from f_md_rel_detail where relation_id in (select relation_id from f_md_relation where parent_table_id in " + "(select table_id from f_table_opt_relation where [:osId | OS_ID=:osId] [:(splitforin)optId | and opt_id in (:optId)]))");

        oldDatabaseSql.put(AppTableNames.F_MD_TABLE.name(), "select table_id,table_name,DATABASE_CODE from f_md_table where database_code in (select DATABASE_ID from m_application_resources where os_id=:osId)");
        oldDatabaseSql.put(AppTableNames.F_MD_RELATION.name(), "select RELATION_ID,PARENT_TABLE_ID,CHILD_TABLE_ID from f_md_relation where parent_table_id in (select table_id from f_md_table where database_code in " + "(select DATABASE_ID from m_application_resources where os_id=:osId))");

        oldApplicationSql.put(AppTableNames.F_OS_INFO.name(), "select os_id,os_name from f_os_info where os_id=:osId");
        oldApplicationSql.put(AppTableNames.F_OPTINFO.name(), "select SOURCE_ID,FORM_CODE,OPT_ID,DOC_ID,top_opt_id,opt_name from f_optinfo");
        oldApplicationSql.put(AppTableNames.F_OPTDEF.name(), "select a.SOURCE_ID,a.OPT_CODE,b.top_opt_id from f_optdef a join f_optinfo b on a.opt_id=b.opt_id");
        oldApplicationSql.put(AppTableNames.F_DATABASE_INFO.name(), "select database_code,database_name " + "from f_database_info where database_code in (select DATABASE_ID from m_application_resources where os_id=:osId)");
        oldApplicationSql.put(AppTableNames.F_TABLE_OPT_RELATION.name(), "select table_id,opt_id,id from f_table_opt_relation");
        oldApplicationSql.put(AppTableNames.M_META_FORM_MODEL.name(), "select source_id,MODEL_ID,os_id,is_valid from m_meta_form_model");
        oldApplicationSql.put(AppTableNames.Q_DATA_PACKET.name(), "select source_id,packet_id,os_id,is_valid,task_cron from q_data_packet");
        oldApplicationSql.put(AppTableNames.WF_FLOW_DEFINE.name(), "select SOURCE_ID,FLOW_CODE,os_id from wf_flow_define");
        oldApplicationSql.put(AppTableNames.WF_NODE.name(), "select SOURCE_ID,NODE_ID,os_id from wf_node");
        oldApplicationSql.put(AppTableNames.WF_TRANSITION.name(), "select FLOW_CODE,START_NODE_ID,END_NODE_ID,TRANS_ID from wf_transition");
        oldApplicationSql.put(AppTableNames.WF_OPT_TEAM_ROLE.name(), "select OPT_TEAM_ROLE_ID,OPT_ID,ROLE_CODE from wf_opt_team_role");
        oldApplicationSql.put(AppTableNames.WF_OPT_VARIABLE_DEFINE.name(), "select VARIABLE_NAME,OPT_ID,OPT_VARIABLE_ID from wf_opt_variable_define");
        oldApplicationSql.put(AppTableNames.F_DATACATALOG.name(), "select catalog_code,top_unit,source_id from f_datacatalog");
        oldApplicationSql.put(AppTableNames.M_APPLICATION_DICTIONARY.name(), "select id,os_id,dictionary_id from m_application_dictionary");
    }

    /**
     * 下载模型文件
     * <p>
     * 本方法用于处理模型的下载请求根据操作系统ID和额外参数，生成一个唯一的文件ID，
     * 并将相关的SQL条目处理后存储到一个临时目录中如果不需要导出模型（根据参数判断），
     * 则对临时目录进行压缩并删除原目录最后返回生成的文件ID
     *
     * @param osId       操作系统ID，用于标识特定的操作系统
     * @param parameters 包含额外参数的映射，可能包括"optId"用于导出特定的模型
     * @return 生成的文件ID
     * @throws IOException 如果文件处理过程中发生I/O错误
     */
    @Override
    public String downModel(String osId, Map<String, Object> parameters) throws IOException {
        // 生成唯一的文件ID
        String fileId = DatetimeOpt.convertDateToString(DatetimeOpt.currentUtilDate(), "YYYYMMddHHmmss") + "_" + UUID.randomUUID().toString().substring(0, 8);
        // 创建目标目录
        File targetDir = new File(appHome, fileId);
        String filePath = targetDir.getAbsolutePath();

        // 初始化参数映射，至少包含osId
        Map<String, Object> paramsMap = new HashMap<>(1);
        paramsMap.put("osId", osId);

        // 判断是否需要导出模型
        boolean isModelExport = false;
        if (parameters != null && parameters.containsKey("optId")) {
            String optId = StringBaseOpt.objectToString(parameters.get("optId"));
            if (StringUtils.isNotBlank(optId)) {
                paramsMap.put("optId", optId);
                isModelExport = true;
            }
        }

        // 处理SQL条目并写入文件
        processSqlEntries(applicationSql, paramsMap, filePath);
        processSqlEntries(newDatabaseSql, paramsMap, filePath);

        // 如果不需要导出模型，则压缩文件信息
        if (!isModelExport) {
            try {
                compressFileInfo(osId, filePath);
            } catch (IOException e) {
                throw new ObjectException(e);
            }
        }

        // 创建并压缩目标目录为ZIP文件
        File zipFile = new File(targetDir.getParent(), targetDir.getName() + ".zip");
        ZipCompressor.compress(zipFile.getAbsolutePath(), filePath);

        // 尝试删除临时文件目录
        try {
            FileSystemOpt.deleteDirect(filePath);
        } catch (Exception e) {
            // 可选：记录删除失败的日志
        }

        // 返回生成的文件ID
        return fileId;
    }

    /**
     * 处理SQL条目，为每个条目生成相应的文件
     * 此方法遍历SQL条目映射，并使用提供的参数和基础路径创建文件
     *
     * @param sqlEntries 包含SQL语句及其对应名称的映射如果为null或空，则不执行任何操作
     * @param params     用于创建文件时替换模板中占位符的参数映射
     * @param basePath   用于保存生成文件的基础路径
     * @throws IOException 如果在创建文件过程中发生I/O错误
     */
    private void processSqlEntries(Map<String, String> sqlEntries, Map<String, Object> params, String basePath) throws IOException {
        // 检查sqlEntries是否为null或空，如果是，则直接返回，不执行任何操作
        if (sqlEntries == null || sqlEntries.isEmpty()) {
            return;
        }

        // 遍历sqlEntries中的每个条目，为每个条目生成一个文件
        for (Map.Entry<String, String> entry : sqlEntries.entrySet()) {
            // 调用createFile方法，传入参数映射、SQL语句、文件名和基础路径
            createFile(params, entry.getValue(), entry.getKey(), basePath);
        }
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
        return fileInfoOpt.saveFile(fileInfo, 0, Files.newInputStream(Paths.get(filePath)));
    }

    /**
     * 根据查询结果创建文件
     * 该方法用于根据给定的SQL查询和参数映射从数据库中获取数据，并将结果保存到指定路径下的文件中
     * 如果文件不存在，会自动创建相应的目录和文件
     *
     * @param map      包含查询参数的映射
     * @param sql      执行查询的SQL语句
     * @param fileName 保存数据的文件名，不包括文件扩展名
     * @param filePath 文件保存的目录路径
     * @throws IOException 如果文件写入过程中发生错误
     */
    private void createFile(Map<String, Object> map, String sql, String fileName, String filePath) throws IOException {
        // 创建File对象以检查目录是否存在
        File file = new File(filePath);
        // 如果目录不存在，则尝试创建
        if (!file.exists()) {
            // 如果创建目录失败，则抛出异常
            if (!file.mkdirs()) {
                throw new IOException("无法创建目录: " + filePath);
            }
        }

        // 执行SQL查询并获取结果作为JSONArray
        JSONArray jsonArray = DatabaseOptUtils.listObjectsByParamsDriverSqlAsJson(applicationTemplateDao, sql, map);

        // 如果是特定的文件名且map中包含特定键，则对结果进行额外处理
        if (fileName.equals(AppTableNames.F_OPTINFO.name()) && map.containsKey("optId")) {
            jsonArray = parentOpt(jsonArray, map.get("optId"));
        }

        // 使用FileOutputStream打开文件流，并确保在操作完成后关闭流
        try (FileOutputStream fos = new FileOutputStream(filePath + File.separator + fileName + ".csv")) {
            // 将JSONArray中的数据保存到CSV文件中，指定字符编码为GBK
            CsvFileIO.saveJSON2OutputStream(jsonArray, fos, true, null, "UTF-8");
        } catch (IOException e) {
            // 如果文件写入过程中发生IOException，抛出自定义异常
            throw new ObjectException(ResponseData.HTTP_IO_EXCEPTION, "导出文件出错" + e.getMessage());
        }
    }


    /**
     * 根据给定的选项ID收集所有父选项信息
     * 此方法旨在从一个JSONArray中提取与给定选项ID相关的所有父选项信息
     * 它通过递归查找每个选项的直接父选项，直到没有更多父选项为止
     *
     * @param jsonArray 包含所有选项信息的JSONArray
     * @param optId     需要收集父选项信息的选项ID，可以是单个ID或ID数组
     * @return 包含所有父选项信息的JSONArray
     */
    private JSONArray parentOpt(JSONArray jsonArray, Object optId) {
        // 初始化结果数组，用于存储所有父选项信息
        JSONArray resultArray = new JSONArray();
        // 将给定的选项ID转换为字符串数组，以便统一处理单个ID或ID数组
        String[] optIds = StringBaseOpt.objectToStringArray(optId);
        // 如果转换后的ID数组为空，则直接返回空的结果数组
        if (optIds == null) {
            return resultArray; // 提前返回空数组以避免无效操作
        }

        // 遍历每个选项ID，收集它们的父选项信息
        for (String currentOptId : optIds) {
            // 获取当前选项的直接父选项信息
            JSONObject optInfo = getOptInfo(jsonArray, currentOptId);
            // 如果父选项信息不存在，则跳过当前选项，继续处理下一个选项
            if (optInfo == null) {
                continue; // 避免 NullPointerException
            }
            // 将父选项信息添加到结果数组中
            resultArray.add(optInfo);
            // 获取当前父选项的前一个选项ID，以便进一步追溯更高级别的父选项
            String preOptId = optInfo.getString("preOptId");
            // 递归调用以收集当前父选项的所有上级父选项信息
            collectParentOptions(resultArray, jsonArray, preOptId);
        }
        // 返回包含所有父选项信息的结果数组
        return resultArray;
    }


    /**
     * 收集父选项信息
     * 此方法避免了重复遍历jsonArray，提高了代码效率和可读性
     *
     * @param pOptInfo  存储父选项信息的JSONArray
     * @param jsonArray 包含所有选项信息的JSONArray
     * @param preOptId  上级选项ID，用于在递归调用中传递
     */
    private void collectParentOptions(JSONArray pOptInfo, JSONArray jsonArray, String preOptId) {
        // 构建 optId 到 JSONObject 的映射，避免重复遍历
        Map<String, JSONObject> optIdMap = new HashMap<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            String optId = item.getString("optId");
            if (optId != null) {
                optIdMap.put(optId, item);
            }
        }

        // 使用 Set 快速判断是否重复
        Set<String> existingOptIds = new HashSet<>();
        for (int i = 0; i < pOptInfo.size(); i++) {
            JSONObject item = pOptInfo.getJSONObject(i);
            String optId = item.getString("optId");
            if (optId != null) {
                existingOptIds.add(optId);
            }
        }

        // 递归收集父选项信息
        collect(pOptInfo, optIdMap, existingOptIds, preOptId);
    }


    /**
     * 递归收集前置操作信息
     * 该方法用于构建一个有序的操作列表，根据每个操作的前置操作关系
     * 它从一个给定的前置操作ID开始，追溯到最初的操作，将途中的每个操作信息收集到一个JSONArray中
     * 同时，通过一个集合记录已经存在的操作ID，以避免重复收集
     *
     * @param pOptInfo       存储收集的操作信息的JSONArray
     * @param optIdMap       操作ID与操作信息的映射表
     * @param existingOptIds 已经收集的操作ID集合，用于去重
     * @param preOptId       当前需要处理的前置操作ID
     */
    private void collect(JSONArray pOptInfo, Map<String, JSONObject> optIdMap, Set<String> existingOptIds, String preOptId) {
        // 如果前置操作ID为空或为"0"，表示已达到最初的操作，结束递归
        if (preOptId == null || "0".equals(preOptId)) {
            return;
        }

        // 根据前置操作ID获取对应的操作信息
        JSONObject optInfo = optIdMap.get(preOptId);
        // 如果找不到对应的节点，结束递归
        if (optInfo == null) {
            return;
        }

        // 获取当前操作的ID
        String currentOptId = optInfo.getString("optId");
        // 如果当前操作ID不为空且未被收集过，则将其添加到收集列表中，并记录该操作ID
        if (currentOptId != null && !existingOptIds.contains(currentOptId)) {
            pOptInfo.add(optInfo);
            existingOptIds.add(currentOptId);
        }

        // 获取下一个前置操作ID，继续递归收集
        String nextPreOptId = optInfo.getString("preOptId");
        // 如果下一个前置操作ID不为空且不为"0"，则继续递归
        if (nextPreOptId != null && !nextPreOptId.equals("0")) {
            collect(pOptInfo, optIdMap, existingOptIds, nextPreOptId);
        }
    }


    /**
     * 从给定的JSONArray中获取特定optId对应的JSONObject
     *
     * @param jsonArray 包含多个JSONObject的JSONArray，每个JSONObject中都应包含"optId"字段
     * @param optId     需要查找的特定操作ID字符串
     * @return 如果找到匹配的JSONObject则返回它，否则返回null
     */
    private JSONObject getOptInfo(JSONArray jsonArray, String optId) {
        // 检查输入参数是否有null值，如果是则直接返回null
        if (jsonArray == null || optId == null) {
            return null;
        }
        // 遍历JSONArray中的每个JSONObject
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            // 获取当前项的optId字段值，并与目标optId进行比较
            String currentOptId = item.getString("optId");
            // 如果找到匹配的optId，立即返回对应的JSONObject
            if (optId.equals(currentOptId)) {
                return item;
            }
        }
        // 如果遍历结束后没有找到匹配的项，返回null
        return null;
    }


    /**
     * 根据操作系统ID和文件路径压缩文件信息
     * 此方法从数据库中获取指定库ID和分类的文件信息，将这些文件复制到一个新目录，并将该目录压缩为ZIP文件
     *
     * @param osId     操作系统ID，用于查询文件信息
     * @param filePath 文件路径，用于指定压缩文件的存储位置
     * @throws IOException 如果文件操作失败，则抛出IOException
     */
    private void compressFileInfo(String osId, String filePath) throws IOException {
        // 定义文件分隔符，用于构建文件路径
        final String FILE_SEPARATOR = File.separator;

        // SQL查询语句，用于获取指定库ID和分类的文件信息
        String fileInfoSql = "select a.file_id, a.file_name from file_info a join file_store_info b on a.file_md5=b.file_md5 where a.library_id=:osId and a.file_catalog in ('A','B')";
        // 执行SQL查询，获取文件信息列表
        List<Object[]> objects = DatabaseOptUtils.listObjectsByNamedSql(applicationTemplateDao, fileInfoSql, CollectionsOpt.createHashMap("osId", osId));
        // 如果没有查询到文件信息，则直接返回
        if (objects == null || objects.isEmpty()) {
            return;
        }

        // 构建文件信息路径和压缩文件路径
        String fileInfoPath = filePath + FILE_SEPARATOR + "file";
        String zipFilePath = fileInfoPath + ".zip";

        // 创建文件信息目录，如果目录不存在且创建失败，则抛出运行时异常
        File fileDir = new File(fileInfoPath);
        if (!fileDir.exists()) {
            if (!fileDir.mkdirs()) {
                throw new RuntimeException("Failed to create directory: " + fileDir.getAbsolutePath());
            }
        }

        // 遍历文件信息列表，将每个文件复制到文件信息目录中
        for (Object[] object : objects) {
            String fileId = StringBaseOpt.castObjectToString(object[0]);
            String fileName = StringBaseOpt.castObjectToString(object[1]);
            try (InputStream inputStream = fileInfoOpt.loadFileStream(fileId)) {
                if (inputStream != null) {
                    String fileIdPath = fileInfoPath + FILE_SEPARATOR + "(" + fileId + ")" + fileName;
                    FileSystemOpt.createFile(inputStream, fileIdPath);
                }
            } catch (Exception e) {
                // 如果复制文件时发生异常，则跳过当前文件，继续复制下一个文件
                continue;
            }
        }

        // 压缩文件信息目录为ZIP文件，无论压缩成功与否，最后都会删除文件信息目录
        try {
            ZipCompressor.compress(zipFilePath, fileInfoPath);
        } finally {
            FileSystemOpt.deleteDirect(fileInfoPath);
        }
    }

    /**
     * 上传并解压模型文件（ZIP格式），然后解析其中的CSV文件到JSON对象
     *
     * @param zipFile 待上传的ZIP文件
     * @return 包含上传结果信息的JSON对象
     * @throws Exception 如果文件上传、解压或解析过程中发生错误
     */
    @Override
    public JSONObject uploadModel(File zipFile) throws Exception {
        // 校验上传的ZIP文件是否有效
        if (zipFile == null || !zipFile.exists()) {
            throw new IllegalArgumentException("上传的 ZIP 文件无效");
        }

        JSONObject returnJsonObject = new JSONObject();
        // 生成时间戳用于创建唯一目录名
        String timestamp = DatetimeOpt.convertDateToString(DatetimeOpt.currentUtilDate(), "yyyyMMddHHmmss");
        String filePath = appHome + File.separator + "c" + timestamp;

        File targetDir = new File(filePath);
        // 尝试创建目标目录，如果失败且目录不存在，则抛出异常
        if (!targetDir.mkdirs() && !targetDir.isDirectory()) {
            throw new IOException("无法创建目标目录: " + filePath);
        }

        try {
            // 解压ZIP文件到目标目录
            ZipCompressor.release(zipFile, filePath);

            // 创建一个新的JSON对象用于存储解析后的数据
            JSONObject jsonObject = new JSONObject();
            // 解析CSV文件到JSON对象
            parseCsvToJson(jsonObject, filePath);

            // 将文件路径和解析后的数据添加到返回的JSON对象中
            returnJsonObject.put("file", filePath); // 注意：路径暴露仍存在安全风险
            returnJsonObject.put(F_DATABASE_INFO, jsonObject.get(F_DATABASE_INFO));
        } catch (Exception e) {
            // 如果发生异常，清理已生成的目录
            deleteDirectoryRecursively(targetDir);
            // 重新抛出异常
            throw e;
        }

        // 返回包含上传结果信息的JSON对象
        return returnJsonObject;
    }


    // 工具方法：递归删除目录
    private void deleteDirectoryRecursively(File dir) {
        if (dir != null && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    deleteDirectoryRecursively(file);
                }
                file.delete();
            }
        }
        dir.delete();
    }


    /**
     * 准备应用程序的相关信息和数据结构
     * 此方法主要用于解析和处理输入的JSON对象，以准备应用程序在特定操作系统环境中的部署或更新
     * 它涉及从JSON中提取文件路径，解析CSV数据到JSON格式，以及根据数据库信息生成相应的DDL语句
     *
     * @param jsonObject         包含应用程序信息和数据结构的JSON对象，包括文件路径等
     * @param osId               操作系统标识符，用于指定目标操作系统环境
     * @param currentUserDetails 当前用户详细信息，用于权限和审计目的
     * @return 返回一个包含应用程序信息、DDL语句和运行DDL标志的JSON对象
     * @throws IllegalArgumentException 如果输入JSON中缺少必要的'field'字段
     * @throws ObjectException          如果在处理过程中遇到任何异常
     */
    @Override
    public JSONObject prepareApp(JSONObject jsonObject, String osId, CentitUserDetails currentUserDetails) {
        try {
            // 检查输入JSON中是否包含必要的'field'字段
            if (!jsonObject.containsKey("file")) {
                throw new IllegalArgumentException("Missing 'file' field in input JSON");
            }
            String zipFilePath = jsonObject.getString("file");

            // 初始化一个空的JSON对象，用于存储解析后的数据
            JSONObject sourceJson = new JSONObject();
            // 调用方法解析CSV数据到JSON格式
            parseCsvToJson(sourceJson, zipFilePath);

            // 使用更高效的深拷贝方式复制输入的JSON对象
            JSONObject copyJson = JSON.parseObject(JSON.toJSONString(jsonObject));
            // 将数据库信息从复制的JSON对象中提取并添加到sourceJson中
            sourceJson.put(F_DATABASE_INFO, copyJson.get(F_DATABASE_INFO));

            // 创建JsonAppVo对象，用于进一步处理和管理应用程序的数据和逻辑
            JsonAppVo jsonAppVo = new JsonAppVo(sourceJson, getOldApplication(osId), osId, currentUserDetails, appHome, fileInfoOpt, zipFilePath);
            // 设置不上传文件的标志
            jsonAppVo.setUploadFiles(false);
            // 更新主要信息
            jsonAppVo.updatePrimary();

            // 从jsonAppVo中提取待处理的表列表、列列表和数据库信息列表
            List<Map<String, Object>> pendingTableList = jsonAppVo.getMapJsonObject().get(AppTableNames.F_MD_TABLE.name());
            List<Map<String, Object>> pendingColumnsList = jsonAppVo.getMapJsonObject().get(AppTableNames.F_MD_COLUMN.name());
            List<Map<String, Object>> databaseList = jsonAppVo.getMapJsonObject().get(AppTableNames.F_DATABASE_INFO.name());

            // 初始化一个Map对象，用于存储应用程序相关的SQL语句
            Map<String, List<String>> appSqls = new HashMap<>(4);
            // 创建JavaBean元数据对象，用于处理表和列的信息
            JavaBeanMetaData tableMeta = JavaBeanMetaData.createBeanMetaDataFromType(PendingMetaTable.class);
            JavaBeanMetaData columnMeta = JavaBeanMetaData.createBeanMetaDataFromType(PendingMetaColumn.class);

            // 预处理列，按 tableId 分组
            Map<String, List<PendingMetaColumn>> columnsByTableId = new HashMap<>();
            for (Map<String, Object> map : pendingColumnsList) {
                PendingMetaColumn column = (PendingMetaColumn) columnMeta.createBeanObjectFromMap(map);
                column.setMaxLength(NumberBaseOpt.castObjectToInteger(map.get("columnLength")));
                columnsByTableId.computeIfAbsent(column.getTableId(), k -> new ArrayList<>()).add(column);
            }

            // 构建 SQL
            for (Map<String, Object> map : pendingTableList) {
                PendingMetaTable table = (PendingMetaTable) tableMeta.createBeanObjectFromMap(map);
                table.setMdColumns(columnsByTableId.getOrDefault(table.getTableId(), Collections.emptyList()));
                List<String> sqls = metaTableManager.makeAlterTableSqlList(table);

                appSqls.computeIfAbsent(table.getDatabaseCode(), k -> new ArrayList<>()).addAll(sqls);
            }

            // 构建 DDL 映射
            JavaBeanMetaData sourceInfoMeta = JavaBeanMetaData.createBeanMetaDataFromType(SourceInfo.class);
            Map<String, List<String>> DDLs = new HashMap<>(4);
            for (Map<String, Object> map : databaseList) {
                SourceInfo sourceInfo = (SourceInfo) sourceInfoMeta.createBeanObjectFromMap(map);
                List<String> sqlList = appSqls.get(sourceInfo.getDatabaseCode());
                if (sqlList != null && !sqlList.isEmpty()) {
                    DDLs.put(sourceInfo.getDatabaseName() + "(" + sourceInfo.getDatabaseCode() + ")", new ArrayList<>(sqlList));
                }
            }

            // 构造返回值
            JSONObject returnJson = new JSONObject();
            JSONObject subJson = new JSONObject();
            subJson.put(F_DATABASE_INFO, jsonObject.get(F_DATABASE_INFO));
            subJson.put("file", jsonObject.get("file"));
            subJson.put("targetOsId", osId);
            returnJson.put("jsonAppVo", subJson);
            returnJson.put("DDL", DDLs);
            returnJson.put("runDDL", true);

            return returnJson;
        } catch (Exception e) {
            // 记录异常日志（建议替换为 logger.error）
            throw new ObjectException(e.getMessage(), e);
        }
    }


    /**
     * 导入应用程序信息
     * <p>
     * 该方法负责解析JSON对象中的应用信息，并根据提供的文件路径解析CSV数据，
     * 将其转换为JSON格式，然后根据JSON数据更新或创建应用程序及相关元数据
     *
     * @param jsonObject  包含应用程序信息的JSON对象，必须包含"jsonAppVo"字段
     * @param userDetails 当前用户信息，用于记录操作者
     * @return 返回包含差异ID的JSONArray，表示此次导入新增或更新的记录ID
     * @throws Exception 如果解析过程中遇到错误或缺少必要信息，则抛出异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONArray importApp(JSONObject jsonObject, CentitUserDetails userDetails) throws Exception {
        // 检查输入参数是否满足基本要求
        if (jsonObject == null || !jsonObject.containsKey("jsonAppVo")) {
            throw new IllegalArgumentException("jsonObject 或 jsonAppVo 字段不能为空");
        }

        // 提取应用程序信息并初始化源JSON对象
        JSONObject jsonAppVoJson = jsonObject.getJSONObject("jsonAppVo");
        JSONObject sourceJson = new JSONObject();
        String filePath = StringBaseOpt.objectToString(jsonAppVoJson.get("file"));

        // 确保文件路径不为空
        if (StringUtils.isBlank(filePath)) {
            throw new IllegalArgumentException("文件路径不能为空");
        }

        // 解析CSV文件到JSON对象
        parseCsvToJson(sourceJson, filePath);

        // 如果存在数据库信息，则添加到源JSON对象中
        Object dbInfo = jsonAppVoJson.get(F_DATABASE_INFO);
        if (dbInfo != null) {
            sourceJson.put(F_DATABASE_INFO, dbInfo);
        }

        // 创建JsonAppVo对象，用于处理应用程序导入过程
        String osId = jsonAppVoJson.getString("targetOsId");
        if (StringBaseOpt.isNvl(osId)) {
            throw new IllegalArgumentException("targetOsId字段不能为空");
        }
        List<WorkGroup> userGroups = platformEnvironment.listWorkGroup(osId, userDetails.getUserCode(), null);
        if (CollectionUtils.isEmpty(userGroups)) {
            throw new ObjectException(ResponseData.ERROR_FORBIDDEN, "用户没有权限导出这个应用：" + osId + "！");
        }
        JsonAppVo jsonAppVo = new JsonAppVo(sourceJson, getOldApplication(osId), osId, userDetails, appHome, fileInfoOpt, filePath);

        // 设置是否运行字典和元数据的标志
        jsonAppVo.setRunDictionary(BooleanBaseOpt.castObjectToBoolean(jsonObject.get("runDictionary"), true));
        jsonAppVo.setRunMetaData(BooleanBaseOpt.castObjectToBoolean(jsonObject.get("runMetaData"), true));

        // 准备应用程序数据，包括解析和设置必要的信息
        jsonAppVo.prepareApp();

        // 获取是否运行DDL的标志
        boolean runDDL = BooleanBaseOpt.castObjectToBoolean(jsonObject.get("runDDL"), true);

        try {
            // 根据runDDL标志决定是否执行DDL操作
            if (!jsonAppVo.getAppList().isEmpty()) {
                if (!runDDL) {
                    // 如果不运行DDL，设置所有表的状态为"S"
                    for (Object object : jsonAppVo.getAppList()) {
                        if (object instanceof PendingMetaTable) {
                            ((PendingMetaTable) object).setTableState("S");
                        }
                    }
                }
                // 批量合并对象到数据库
                DatabaseOptUtils.batchMergeObjects(applicationTemplateDao, jsonAppVo.getAppList());

                // 如果需要运行DDL，发布所有数据库
                if (runDDL) {
                    for (String databaseCode : jsonAppVo.getPublishDatabaseCode()) {
                        ResponseData responseData = metaTableManager.publishDatabase(databaseCode, jsonAppVo.getUserCode());
                        if (responseData.getCode() < 0) {
                            logger.error("发布数据库 {} 失败: {}, 数据: {}", databaseCode, responseData.getMessage(), JSON.toJSONString(responseData.getData()));
                        }
                    }
                }
            }

            // 处理元对象，如果存在，则批量合并到数据库
            if (!jsonAppVo.getMetaObject().isEmpty()) {
                DatabaseOptUtils.batchMergeObjects(applicationTemplateDao, jsonAppVo.getMetaObject());
            }

            // 刷新缓存，确保最新数据被正确处理
            jsonAppVo.refreshCache(ddeDubboTaskRun);

        } catch (Exception e) {
            // 保留原始异常堆栈信息
            throw new RuntimeException("导入应用过程中发生异常", e);
        } finally {
            // 尝试删除临时文件，如果失败，则记录警告日志
            try {
//                FileSystemOpt.deleteDirect(filePath);
            } catch (Exception e) {
                logger.warn("删除临时文件失败: {}", filePath, e);
            }
        }

        // 返回差异ID，表示此次导入影响的记录
        return jsonAppVo.getDiffIds();
    }

    private void parseCsvToJson(JSONObject jsonObject, String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return;
        }

        List<File> files = FileSystemOpt.findFiles(filePath, "*.csv");
        if (files == null || files.isEmpty()) {
            return;
        }

        for (File file : files) {
            String path = file.getPath();
            String fileName = FileSystemOpt.extractFileName(path);

            try (InputStream is = Files.newInputStream(Paths.get(path))) {
                jsonObject.put(fileName, CsvFileIO.readDataFromInputStream(is, true, null, "gbk"));
            } catch (Exception e) {
                // 可替换为日志框架输出
                logger.error("读取CSV文件失败: {}", path);
            }
        }
    }


    /**
     * 根据操作系统ID获取旧应用的配置信息
     * 该方法首先会检查操作系统ID是否为空，如果为空则直接返回一个空的JSONObject
     * 然后通过执行预定义的SQL查询来获取旧应用的配置信息，并将结果封装到JSONObject中
     *
     * @param osId 操作系统ID，用于查询旧应用的配置信息
     * @return 返回一个包含旧应用配置信息的JSONObject，如果查询过程中发生异常或osId为空，则返回一个空的JSONObject
     */
    private JSONObject getOldApplication(String osId) {
        // 检查操作系统ID是否为空，如果为空则直接返回一个空的JSONObject
        if (StringUtils.isBlank(osId)) {
            return new JSONObject();
        }

        try {
            // 创建一个包含查询参数的Map，用于执行SQL查询
            Map<String, Object> mapApplication = CollectionsOpt.createHashMap("osId", osId);
            // 创建一个空的JSONObject用于存储查询结果
            JSONObject jsonObject = new JSONObject();

            // 执行旧应用的SQL查询，并将结果填充到jsonObject中
            processSqlEntries(oldApplicationSql, mapApplication, jsonObject);
            // 执行旧数据库的SQL查询，并将结果填充到jsonObject中
            processSqlEntries(oldDatabaseSql, mapApplication, jsonObject);

            // 返回包含查询结果的JSONObject
            return jsonObject;
        } catch (Exception e) {
            // 记录异常信息，避免静默失败
            // 可根据实际项目替换为 logger.error(...)
            logger.error(e.getMessage());
            // 返回空对象作为降级处理
            return new JSONObject();
        }
    }


    /**
     * 处理SQL条目并更新JSON对象
     * 此方法用于执行一系列SQL查询，并将查询结果以JSON格式组织到给定的JSON对象中
     * 它通过命名SQL语句和相应参数来查询数据库，并将结果作为JSON数组放入JSON对象中
     *
     * @param sqlEntries 包含SQL查询语句的映射，键为查询标识，值为SQL语句
     * @param params     查询参数，用于执行带有参数的SQL查询
     * @param jsonObject 用于存储查询结果的JSON对象
     */
    private void processSqlEntries(Map<String, String> sqlEntries, Map<String, Object> params, JSONObject jsonObject) {
        // 检查sqlEntries是否为null或空，如果是，则直接返回，不执行任何操作
        if (sqlEntries == null || sqlEntries.isEmpty()) {
            return;
        }
        // 遍历SQL条目，执行查询并将结果添加到jsonObject中
        for (Map.Entry<String, String> entry : sqlEntries.entrySet()) {
            // 使用DatabaseOptUtils工具类执行SQL查询，并将结果转换为JSONArray
            JSONArray jsonArray = DatabaseOptUtils.listObjectsByNamedSqlAsJson(applicationTemplateDao, entry.getValue(), params);
            // 如果查询结果不为null，则将其放入JSON对象中，键为SQL条目的键
            if (jsonArray != null) {
                jsonObject.put(entry.getKey(), jsonArray);
            }
        }
    }

}
