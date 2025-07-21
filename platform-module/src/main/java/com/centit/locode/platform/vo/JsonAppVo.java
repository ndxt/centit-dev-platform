package com.centit.locode.platform.vo;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.dde.adapter.DdeDubboTaskRun;
import com.centit.dde.adapter.po.DataPacket;
import com.centit.dde.adapter.po.DataPacketDraft;
import com.centit.dde.adapter.po.DataPacketParam;
import com.centit.dde.adapter.po.DataPacketParamDraft;
import com.centit.dde.utils.ConstantValue;
import com.centit.fileserver.common.FileInfoOpt;
import com.centit.fileserver.po.FileInfo;
import com.centit.framework.model.basedata.*;
import com.centit.framework.model.security.CentitUserDetails;
import com.centit.locode.platform.po.ApplicationDictionary;
import com.centit.metaform.po.MetaFormModel;
import com.centit.metaform.po.MetaFormModelDraft;
import com.centit.product.metadata.po.*;
import com.centit.support.algorithm.*;
import com.centit.support.common.JavaBeanMetaData;
import com.centit.support.common.ObjectException;
import com.centit.support.file.FileSystemOpt;
import com.centit.workflow.po.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author zhf
 */

public class JsonAppVo {
    protected static final Logger logger = LoggerFactory.getLogger(JsonAppVo.class);
    private static final String OS_ID = "osId";
    private static final String PAGE_FLOW = "pageFlow";
    private static final String DATABASE_CODE = "databaseCode";
    private static final String TABLE_ID = "tableId";
    private static final String REFERENCE_DATA = "referenceData";
    private static final String RELATION_ID = "relationId";
    private static final String PARENT_TABLE_ID = "parentTableId";
    private static final String OPT_ID = "optId";
    private static final String NODE_TYPE = "nodeType";
    private static final String ID = "id";
    private static final String PACKET_ID = "packetId";
    private static final String DATA_OPT_DESC_JSON = "dataOptDescJson";
    private static final String FLOW_XML_DESC = "flowXmlDesc";
    private static final String MODEL_ID = "modelId";
    private static final String FORM_TEMPLATE = "formTemplate";
    private static final String OPT_TEAM_ROLE_ID = "optTeamRoleId";
    private static final String OPT_VARIABLE_ID = "optVariableId";
    private static final String OPT_CODE = "optCode";
    private static final String CHILD_TABLE_ID = "childTableId";
    private static final String VERSION = "version";
    private static final String FLOW_CODE = "flowCode";
    private static final String SUB_FLOW_CODE = "subFlowCode";
    private static final String NODE_ID = "nodeId";
    private static final String TRANS_ID = "transId";
    private static final String START_NODE_ID = "startNodeId";
    private static final String END_NODE_ID = "endNodeId";
    private static final String TOP_UNIT = "topUnit";
    private static final String NO_PUBLISH = "W";
    private static final String MAX_LENGTH = "maxLength";
    private static final String COLUMN_LENGTH = "columnLength";
    private static final String LIBRARY_ID = "libraryId";
    private static final String TOP_OPT_ID = "topOptId";
    private static final String PRE_OPT_ID = "preOptId";
    private static final String UPDATE_DATE = "updateDate";
    private static final String CREATE_TIME = "createTime";
    private static final String LAST_MODIFY_DATE = "lastModifyDate";
    private static final String UPDATE_TIME = "updateTime";
    private static final String RECORD_DATE = "recordDate";
    private static final String PUBLISH_DATE = "publishDate";
    private static final String MODIFY_TIME = "modifyTime";
    private static final String FLOW_PUBLISH_DATE = "flowPublishDate";
    private static final String REL_OPT_ID = "relOptId";
    private static final String CREATED = "created";
    private static final String CREATE_USER = "createUser";
    private static final String OWN_USER = "ownUser";
    private static final String RECORDER = "recorder";
    private static final String UPDATOR = "updator";
    private static final String CREATOR = "creator";
    private static final String CREATE_DATE = "createDate";
    private static final String TABLE_TYPE = "T";
    private static final String DEFAULT_DATABASE = "defaultDatabase";
    private static final String TABLE_NAME = "tableName";
    private static final String SOURCE_ID = "sourceId";
    private static final String DOC_ID = "docId";
    private static final String ROLE_CODE = "roleCode";
    private static final String VARIABLE_NAME = "variableName";
    private static final String OS_NAME = "osName";
    private static final String LIBRARY_NAME = "libraryName";
    private static final String FORM_CODE = "formCode";
    private static final String MOBILE_FORM_TEMPLATE = "mobileFormTemplate";
    private static final String STRUCTURE_FUNCTION = "structureFunction";
    private static final String DATABASE_ID = "databaseId";
    private static final String DICTIONARY_ID = "dictionaryId";
    private static final String DATABASE_ID_CLASS = "dataBaseId";
    private static final String PUSH_USER = "pushUser";
    private static final String PUSH_TIME = "pushTime";
    private static final String OPT_ROUTE = "optRoute";
    private static final String OPT_URL = "optUrl";
    private static final String API_ID = "apiId";
    private static final String DDE_RUN = "/dde/run/";
    private static final String CATALOG_CODE = "catalogCode";
    private static final String FIRST_NODE_ID = "firstNodeId";
    private static final String TASK_TYPE = "taskType";
    private static final String IS_VALID = "isValid";
    private static final String TASK_CRON = "taskCron";
    private static final String MAP_DATA_CODE = "mapDataCode";
    private static final String LOGO_FILE_ID = "logoFileId";
    private static final String PIC_ID = "picId";
    private final Date now;

    @Getter
    @Setter
    private JSONObject oldAppObject;
    @Getter
    private Map<String, List<Map<String, Object>>> mapJsonObject = new HashMap<>();
    private final String zipFilePath;
    private final String appHome;
    private final FileInfoOpt fileInfoOpt;
    @Getter
    private String userCode;
    private final String topUnit;
    private String osId;

    @Getter
    private List<Object> appList = new ArrayList<>();
    @Getter
    private List<Object> metaObject = new ArrayList<>();
    @Getter
    @Setter
    private boolean uploadFiles;
    @Getter
    @Setter
    private boolean runMetaData;
    @Getter
    @Setter
    private boolean runDictionary;

    private final Map<String, String> databaseDiffMap = new HashMap<>();
    private final Map<String, String> mdTableDiffMap = new HashMap<>();
    private final Map<String, String> relationDiffMap = new HashMap<>();
    private final Map<String, String> dataPacketDiffMap = new HashMap<>();
    private final Map<String, String> metaFormDiffMap = new HashMap<>();
    private final Map<String, String> flowDefineDiffMap = new HashMap<>();
    private final Map<String, String> optInfoDiffMap = new HashMap<>();
    private final Map<String, String> wfNodeDiffMap = new HashMap<>();
    private final Map<String, String> dictionaryDiffMap = new HashMap<>();
    private final Map<String, String> fileDiffMap = new HashMap<>();


    public JsonAppVo(JSONObject jsonObject, JSONObject oldObject, String osId, CentitUserDetails userDetails, String appHome, FileInfoOpt fileInfoOpt, String zipFilePath) {
        createMapJsonObject(jsonObject);
        this.zipFilePath = zipFilePath;
        this.oldAppObject = oldObject;
        this.userCode = userDetails == null ? "" : userDetails.getUserCode();
        this.topUnit = userDetails == null ? "" : userDetails.getTopUnitCode();
        this.appHome = appHome;
        this.fileInfoOpt = fileInfoOpt;
        this.runMetaData = true;
        this.runDictionary = true;
        this.uploadFiles = true;
        this.osId = osId;
        this.now = new Date();
    }

    /**
     * 获取不同类型的主键变化信息
     * <p>
     * 该方法汇总了不同数据类型（如接口、页面、流程、数据字典）的主键变化信息，
     * 并将其封装到一个JSONArray对象中，便于统一管理和传输这些差异信息
     *
     * @return JSONArray 包含各种数据类型主键变化信息的数组
     */
    public JSONArray getDiffIds() {
        JSONArray jsonArray = new JSONArray();

        addDiffEntry(jsonArray, "接口主键变化", dataPacketDiffMap);
        addDiffEntry(jsonArray, "页面主键变化", metaFormDiffMap);
        addDiffEntry(jsonArray, "流程主键变化", flowDefineDiffMap);
        addDiffEntry(jsonArray, "数据字典主键变化", dictionaryDiffMap);
        addDiffEntry(jsonArray, "菜单主键变化", optInfoDiffMap);
        addDiffEntry(jsonArray, "表主键变化", mdTableDiffMap);

        return jsonArray;
    }

    /**
     * 将指定类型的主键变化信息添加到JSONArray中
     *
     * @param array   JSONArray容器
     * @param keyName 键名（如"接口主键变化"）
     * @param value   对应的差值映射
     */
    private void addDiffEntry(JSONArray array, String keyName, Object value) {
        if (value == null) {
            // 可选：记录警告日志，或者抛出自定义异常
            // log.warn("主键变化数据为空: {}", keyName);
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(keyName, value);
        array.add(jsonObject);
    }


    /**
     * 准备应用程序的启动环境
     * 此方法在应用程序启动前执行必要的初始化操作：
     * 1. 更新主配置
     * 2. 创建应用程序对象
     * 3. 设置数据库名称（依赖于主配置更新完成）
     *
     * @throws ObjectException 如果初始化过程中发生错误
     */
    public void prepareApp() throws ObjectException {
        try {
            // 1. 更新主配置，确保应用程序的主配置是最新的
            logger.info("开始更新主配置...");
            updatePrimary();
            logger.info("主配置更新完成");

            // 2. 创建应用程序对象，这是应用程序运行所必需的步骤
            logger.info("开始创建应用程序对象...");
            createAppObject();
            logger.info("应用程序对象创建完成");

        } catch (Exception e) {
            logger.error("应用程序初始化失败: " + e.getMessage());
            throw new ObjectException("Prepare app failed", e);
        }
    }


    /**
     * 刷新缓存中的应用列表
     * 此方法旨在遍历应用列表，对于其中的每个DataPacket对象，调用DdeDubboTaskRun的refreshCache方法刷新缓存
     *
     * @param ddeDubboTaskRun DdeDubboTaskRun对象，用于执行缓存刷新操作
     */
    public void refreshCache(DdeDubboTaskRun ddeDubboTaskRun) {
        if (appList == null) {
            return;
        }
        for (Object object : appList) {
            if (object instanceof DataPacket) {
                String packetId = ((DataPacket) object).getPacketId();
                if (packetId != null) {
                    ddeDubboTaskRun.refreshCache(packetId);
                }
            }
        }
    }

    /**
     * 将JSONObject中的值为List类型的元素转换并存储为Map<String, Object>类型的List
     * 此方法的目的是处理和转换JSON对象中特定格式的列表数据，以便于后续操作或访问
     *
     * @param jsonObject 包含待转换数据的JSON对象
     */
    private void createMapJsonObject(JSONObject jsonObject) {
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> list = (List<Map<String, Object>>) value;
                mapJsonObject.put(entry.getKey(), list);
            }
        }
    }

    /**
     * 更新主方法，负责按顺序调用一系列更新方法来更新系统的各个部分
     * 该方法涵盖了从操作系统信息到工作流定义的全方位更新，确保系统状态的最新和一致性
     */
    public void updatePrimary() {
        updateOsInfo();
        setDatabaseDiffMap();
        updateDataCatalog();
        updateDataDictionaryUseCatalog();
        updateApplicationDictionary();
        updateMdTable();
        updateMdColumn();
        updateMdRelation();
        updateRelationDetail();
        updateOptInfo();
        updateOptDef();
        updateTableRelation();
        uploadFiles();
        updatePacket();
        updateOptDefUsePacket();
        updatePacketParams();
        updateMetaForm();
        updateOptInfoUseMetaForm();
        updateWfOptTeamRole();
        updateWfOptVariable();
        updateWfDefine();
        updateWfNode();
        updateWfDefineUseDiffMap();
        updateWfTransition();
        updatePacketUseDiffMap();
        updateMetaFormUseDiffMap();
    }


    /**
     * 创建应用程序对象的方法
     * 该方法用于初始化或更新应用程序所需的各种信息和资源
     * 它通过链式调用多个创建方法来构建应用程序的各个方面
     */
    public void createAppObject() {
        // 清空应用列表，为创建新的应用对象做准备
        appList.clear();

        // 以下代码块通过链式调用创建应用程序所需的各类信息和资源
        this.createOsInfo()
            .createDataCatalog()
            .createDataDictionary()
            .createApplicationDictionary()
            .createMdTableWithColumnObject()
            .createMdRelationWithDetailObject()
            .createMetaFormObject()
            .createDataPacketAndParamsObject()
            .createWfOptTeamRole()
            .createWfOptVariable()
            .createOptInfo()
            .createOptDef()
            .createWfDefine()
            .createWfNode()
            .createWfTransition()
            .createTableRelation();
    }


    /**
     * 从一个映射中获取数据库名称信息并添加到列表中
     * 此方法旨在处理特定的数据库信息，将其从一个JSON对象映射中提取出来
     * 并以列表形式存储，以便进一步使用
     */
    public List<String> getPublishDatabaseCode() {
        List<String> publishDatabaseCode = new ArrayList<>();
        List<SourceInfo> oldList = convertJavaList(SourceInfo.class, AppTableNames.F_DATABASE_INFO.name());
        if (oldList == null) {
            return publishDatabaseCode;
        }
        for (SourceInfo map : oldList) {
            String sourceType = map.getSourceType();
            if (!"D".equals(sourceType)) {
                continue;
            }
            publishDatabaseCode.add(map.getDatabaseCode());
        }
        return publishDatabaseCode;
    }


    /**
     * 更新操作系统信息
     * 此方法主要用于更新应用程序中的操作系统信息它会检查当前的应用信息是否为空，
     * 如果不为空，则会遍历操作系统信息列表，并更新每个操作系统的特定属性如果列表为空，
     * 则生成新的操作系统ID此方法还会设置创建者和创建时间等信息，并移除一些不必要的属性
     */
    private void updateOsInfo() {
        Object mapJsonObject = this.mapJsonObject.get(AppTableNames.F_OS_INFO.name());
        if (mapJsonObject == null) {
            return;
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) mapJsonObject;
        for (Map<String, Object> map : list) {
            Object pageFlow = map.get(PAGE_FLOW);
            map.clear();
            map.put(OS_ID, osId);
            map.put(PAGE_FLOW, pageFlow);
        }
    }

    private void setDatabaseDiffMap() {
        Object mapJsonObject = this.mapJsonObject.get(AppTableNames.F_DATABASE_INFO.name());
        if (mapJsonObject == null) {
            return;
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) mapJsonObject;
        for (Map<String, Object> map : list) {
            String uuid;
            String mapDataCode = StringBaseOpt.objectToString(map.get(MAP_DATA_CODE));
            String databaseCode = StringBaseOpt.objectToString(map.get(DATABASE_CODE));
            if (!StringBaseOpt.isNvl(mapDataCode)) {
                uuid = mapDataCode;
            } else {
                throw new ObjectException(ObjectException.DATA_NOT_INTEGRATED, map.get("databaseName") + ":没有指定资源");
            }
            if (!uuid.equals(databaseCode)) {
                databaseDiffMap.put(databaseCode, uuid);
                map.put(DATABASE_CODE, uuid);
            }
        }
    }

    /**
     * 更新数据目录信息
     * 此方法从已获取的JSON对象中更新数据目录信息，首先检查数据目录是否存在，
     * 如果存在，则遍历数据目录列表，为每个数据目录生成或更新唯一的UUID，
     * 并记录与旧数据目录的差异，最后更新数据目录的相关信息
     */
    private void updateDataCatalog() {
        // 从JSON对象中获取数据目录信息
        Object dataCatalogObj = mapJsonObject.get(AppTableNames.F_DATACATALOG.name());
        // 如果数据目录信息为空，则直接返回
        if (dataCatalogObj == null) {
            return;
        }

        // 强制转换数据目录信息为列表类型
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) dataCatalogObj;

        // 获取旧的数据目录列表
        List<DataCatalog> oldList = convertJavaList(DataCatalog.class, AppTableNames.F_DATACATALOG.name());
        // 创建一个映射，用于快速查找数据目录
        Map<String, DataCatalog> catalogCodeMap = new HashMap<>();
        Map<String, DataCatalog> catalogSourceMap = new HashMap<>();
        // 判断是否有旧的数据目录信息
        boolean hasOldData = oldList != null && !oldList.isEmpty();
        if (hasOldData) {
            // 将旧的数据目录信息存入映射中
            for (DataCatalog dataCatalog : oldList) {
                String sourceId = dataCatalog.getSourceId();
                String catalogCode = dataCatalog.getCatalogCode();
                String catalogTopUnit = dataCatalog.getTopUnit();
                String sourceKey;
                if (!StringBaseOpt.isNvl(sourceId)) {
                    sourceKey = sourceId + ":" + catalogTopUnit;
                } else {
                    sourceKey = catalogCode + ":" + catalogTopUnit;
                }
                catalogCodeMap.put(catalogCode, dataCatalog);
                catalogSourceMap.put(sourceKey, dataCatalog);
            }
        }

        // 遍历数据目录列表，更新每个数据目录的信息
        for (Map<String, Object> map : list) {
            // 获取数据目录代码
            final String catalogCode = StringBaseOpt.objectToString(map.get(CATALOG_CODE));
            final String sourceId = StringBaseOpt.objectToString(map.get(SOURCE_ID));
            String uuid = "";
            // 如果有旧的数据目录信息，则尝试复用UUID
            if (hasOldData) {
                if (!StringBaseOpt.isNvl(sourceId)) {
                    String sourceKey = sourceId + ":" + topUnit;
                    DataCatalog matchedBySourceId = catalogSourceMap.get(sourceKey);
                    // sourceId相同，获取目标应用catalogCode
                    if (matchedBySourceId != null) {
                        uuid = matchedBySourceId.getCatalogCode();
                    }
                }
                if (StringUtils.isBlank(uuid)) {
                    DataCatalog matchedByCatalogCode = catalogCodeMap.get(catalogCode);
                    if (matchedByCatalogCode != null) {
                        boolean sameTopUnit = topUnit.equals(matchedByCatalogCode.getTopUnit());
                        // 使用源应用 catalogCode 作为 UUID
                        if (sameTopUnit) {
                            uuid = catalogCode;
                        } else {
                            uuid = UuidOpt.getUuidAsString();
                            dictionaryDiffMap.put(catalogCode, uuid);
                        }
                    } else {
                        uuid = catalogCode;
                    }
                }
            } else {
                uuid = catalogCode;
            }
            // 更新 map 中的值
            map.put(CATALOG_CODE, uuid);
            map.put(OS_ID, osId);
            map.put(CREATOR, userCode);
            map.put(CREATE_DATE, now);
            map.put(UPDATOR, userCode);
            map.put(UPDATE_DATE, now);
            map.put(TOP_UNIT, topUnit);
            if (StringBaseOpt.isNvl(sourceId)) {
                map.put(SOURCE_ID, catalogCode);
            }
        }
    }


    /**
     * 根据目录差异映射更新数据字典使用目录
     * 此方法主要用于同步数据字典中的目录信息，确保数据一致性
     */
    private void updateDataDictionaryUseCatalog() {
        // 如果目录差异映射为空，则无需进行任何操作，直接返回
        if (dictionaryDiffMap.isEmpty()) {
            return;
        }

        // 获取数据字典的JSON对象，如果为null，则无需进行任何操作，直接返回
        Object mapJsonObject = this.mapJsonObject.get(AppTableNames.F_DATADICTIONARY.name());
        if (mapJsonObject == null) {
            return;
        }
        // 将获取到的JSON对象转换为列表，用于后续处理
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) mapJsonObject;

        // 遍历数据字典列表，寻找需要更新的目录代码
        for (Map<String, Object> map : list) {
            updateIfMapped(map, CATALOG_CODE, dictionaryDiffMap);
        }
    }


    /**
     * 更新应用字典数据
     * <p>
     * 本方法从JSON对象中获取应用字典列表，与本地旧数据进行对比和更新
     * 主要处理逻辑包括：
     * 1. 从JSON对象中提取应用字典列表
     * 2. 将提取的列表与本地旧数据进行对比，创建旧数据的映射以便快速查找
     * 3. 遍历提取的列表，更新字典ID并为每条记录分配UUID
     * 4. 添加或更新记录的其他必要信息，如操作系统ID、推送用户和推送时间
     */
    private void updateApplicationDictionary() {
        // 从JSON对象中获取应用字典数据
        Object mapJsonObject = this.mapJsonObject.get(AppTableNames.M_APPLICATION_DICTIONARY.name());
        if (mapJsonObject == null) {
            return;
        }

        // 将获取的JSON对象转换为列表
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) mapJsonObject;

        // 获取并转换旧的应用字典数据
        List<ApplicationDictionary> oldList = convertJavaList(ApplicationDictionary.class, AppTableNames.M_APPLICATION_DICTIONARY.name());
        Map<String, ApplicationDictionary> oldDictionaryToOsMap = new HashMap<>();

        // 检查是否存在旧数据，并将其转换为字典ID与操作系统ID的映射
        boolean hasOldData = oldList != null && !oldList.isEmpty();
        if (hasOldData) {
            for (ApplicationDictionary dict : oldList) {
                String key = dict.getDictionaryId() + ":" + dict.getOsId();
                oldDictionaryToOsMap.put(key, dict);
            }
        }
        // 遍历新数据列表，进行数据更新操作
        for (Map<String, Object> map : list) {
            String dictionaryId = StringBaseOpt.objectToString(map.get(DICTIONARY_ID));
            String id = StringBaseOpt.objectToString(map.get(ID));
            // 检查并更新字典ID
            String newDictionaryId = dictionaryDiffMap.get(dictionaryId);
            if (!StringBaseOpt.isNvl(newDictionaryId)) {
                map.put(DICTIONARY_ID, newDictionaryId);
                dictionaryId = newDictionaryId;
            }
            // 初始化UUID，如果存在旧数据，则使用旧数据的UUID
            String uuid = "";
            if (oldList != null) {
                ApplicationDictionary appDict = oldDictionaryToOsMap.get(dictionaryId + ":" + osId);
                if (appDict != null) {
                    uuid = appDict.getId();
                } else {
                    uuid = UuidOpt.getUuidAsString();
                }
            } else {
                uuid = id;
            }
            // 更新记录的UUID、操作系统ID、推送用户和推送时间
            map.put(ID, uuid);
            map.put(OS_ID, osId);
            map.put(PUSH_USER, userCode);
            map.put(PUSH_TIME, now);
        }
    }


    /**
     * 更新元数据表信息
     * 此方法主要用于同步和更新元数据表的信息，通过比较现有的数据和旧的数据，
     * 来确定哪些数据需要更新或者保持不变
     */
    private void updateMdTable() {
        // 获取元数据表的JSON对象
        Object mapJsonObject = this.mapJsonObject.get(AppTableNames.F_MD_TABLE.name());
        // 如果获取的对象为空，则直接返回，无需进一步处理
        if (mapJsonObject == null) {
            return;
        }

        // 将获取的JSON对象转换为列表，由于类型擦除，这里需要进行强制类型转换
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) mapJsonObject;
        // 获取旧的元数据表列表
        List<MetaTable> oldList = convertJavaList(MetaTable.class, AppTableNames.F_MD_TABLE.name());
        // 判断是否存在旧的数据
        boolean hasOldData = oldList != null && !oldList.isEmpty();
        // 创建一个映射，用于存储旧的元数据表信息，以便快速查找
        Map<String, MetaTable> oldMetaTableMap = new HashMap<>();
        // 如果存在旧的数据，则遍历旧的元数据表列表，将其加入到映射中
        if (hasOldData) {
            for (MetaTable metaTable : oldList) {
                String key = StringUtils.lowerCase(metaTable.getTableName()) + ":" + metaTable.getDatabaseCode();
                oldMetaTableMap.put(key, metaTable);
            }
        }
        // 遍历新的元数据表列表，进行数据的更新处理
        for (Map<String, Object> map : list) {
            // 获取表名和数据库编码
            String tableName = StringUtils.lowerCase(StringBaseOpt.objectToString(map.get(TABLE_NAME)));
            String databaseCode = StringBaseOpt.objectToString(map.get(DATABASE_CODE));
            // 替换数据库编码
            String newDbCode = databaseDiffMap.get(databaseCode);
            if (newDbCode != null) {
                map.put(DATABASE_CODE, newDbCode);
                databaseCode = newDbCode;
            }
            // 获取旧数据中的 uuid
            String key = tableName + ":" + databaseCode;
            MetaTable metaTable = oldMetaTableMap.get(key);
            String uuid = metaTable != null ? metaTable.getTableId() : UuidOpt.getUuidAsString();
            // 比较旧数据和新数据的 uuid，如果相同则跳过，否则更新 uuid
            String oldTableId = StringBaseOpt.objectToString(map.get(TABLE_ID));
            if (uuid.equals(oldTableId)) {
                continue; // 没有变化则跳过
            } else {
                mdTableDiffMap.put(oldTableId, uuid);
            }
            // 更新数据
            map.put(TABLE_ID, uuid);
            map.put(RECORDER, userCode);
            map.put(RECORD_DATE, now);
        }
    }


    /**
     * 更新元数据列信息
     * 此方法从mapJsonObject中获取F_MD_COLUMN对应的对象，解析并更新列表中的元数据列信息
     * 主要更新记录者和最后修改日期，同时根据mdTableDiffMap和dictionaryDiffMap更新表ID和引用数据
     */
    private void updateMdColumn() {
        // 获取F_MD_COLUMN对应的JSON对象
        Object mapJsonObject = this.mapJsonObject.get(AppTableNames.F_MD_COLUMN.name());
        // 如果对象为空，则直接返回
        if (mapJsonObject == null) {
            return;
        }
        // 强制转换为列表，并进行类型警告抑制，因为已知内容类型
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) mapJsonObject;
        // 遍历列表中的每个元数据列映射
        for (Map<String, Object> map : list) {
            // 更新记录者和最后修改日期
            map.put(RECORDER, userCode);
            map.put(LAST_MODIFY_DATE, now);
            // 获取并转换表ID和引用数据为字符串
            updateIfMapped(map, TABLE_ID, mdTableDiffMap);
            updateIfMapped(map, REFERENCE_DATA, dictionaryDiffMap);
        }
    }


    /**
     * 更新元数据关系
     * <p>
     * 本方法主要用于更新元数据关系，通过检查和更新映射中的父表ID和子表ID来维护关系的准确性
     * 同时，它还负责生成或更新关系ID，以确保每个关系的唯一性，并记录最后修改日期和操作者
     */
    private void updateMdRelation() {
        // 获取元数据关系的JSON对象
        Object mapJsonObject = this.mapJsonObject.get(AppTableNames.F_MD_RELATION.name());
        // 如果对象为空，则直接返回，无需进一步处理
        if (mapJsonObject == null) {
            return;
        }

        // 将JSON对象转换为列表，以便进一步处理
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) mapJsonObject;

        // 获取旧的元数据关系列表，用于后续的比较和更新
        List<MetaRelation> finalOldList = convertJavaList(MetaRelation.class, AppTableNames.F_MD_RELATION.name());

        // 遍历新列表中的每个关系映射
        for (Map<String, Object> map : list) {
            // 提取并转换父表ID、子表ID和关系ID
            String parentTableId = StringBaseOpt.objectToString(map.get(PARENT_TABLE_ID));
            String childTableId = StringBaseOpt.objectToString(map.get(CHILD_TABLE_ID));
            String relationId = StringBaseOpt.objectToString(map.get(RELATION_ID));
            // 更新父表ID，如果存在差异
            String newParentId = mdTableDiffMap.get(parentTableId);
            if (newParentId != null) {
                map.put(PARENT_TABLE_ID, newParentId);
                parentTableId = newParentId;
            }
            // 更新子表ID，如果存在差异
            String newChildId = mdTableDiffMap.get(childTableId);
            if (newChildId != null) {
                map.put(CHILD_TABLE_ID, newChildId);
                childTableId = newChildId;
            }
            // 初始化UUID，用于后续的关系ID更新
            String uuid = "";
            // 如果旧列表存在，尝试查找匹配的关系，并获取其UUID
            if (finalOldList != null) {
                for (MetaRelation oldMap : finalOldList) {
                    if (parentTableId.equals(oldMap.getParentTableId()) &&
                        childTableId.equals(oldMap.getChildTableId())) {
                        uuid = oldMap.getRelationId();
                        break;
                    }
                }
                // 如果未找到匹配项或UUID为空，则生成新的UUID
                if (StringBaseOpt.isNvl(uuid)) {
                    uuid = UuidOpt.getUuidAsString();
                }
            } else {
                // 如果旧列表不存在，使用原始关系ID作为UUID
                uuid = relationId;
            }
            // 如果新旧关系ID不匹配，更新关系ID映射
            if (!uuid.equals(relationId)) {
                relationDiffMap.put(relationId, uuid);
            }
            // 更新映射中的关系ID、记录者和最后修改日期
            map.put(RELATION_ID, uuid);
            map.put(RECORDER, userCode);
            map.put(LAST_MODIFY_DATE, now);
        }
    }


    /**
     * 更新关系详细信息
     * 此方法用于更新存储在mapJsonObject中的关系详细信息， specifically针对AppTableNames.F_MD_REL_DETAIL指定的数据
     * 它通过替换旧的关系ID来更新信息，以适应任何关系ID的变化
     */
    private void updateRelationDetail() {
        // 获取关系详细信息的JSON对象
        Object mapJsonObject = this.mapJsonObject.get(AppTableNames.F_MD_REL_DETAIL.name());
        // 如果对象为空，则不执行任何操作并退出方法
        if (mapJsonObject == null) {
            return;
        }

        // 将获取到的对象转换为List<Map<String, Object>>类型，用于进一步处理
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) mapJsonObject;

        // 遍历列表中的每个映射，以更新每个关系详细信息中的关系ID
        for (Map<String, Object> map : list) {
            updateIfMapped(map, RELATION_ID, relationDiffMap);
        }
    }


    /**
     * 更新操作信息的方法
     * 该方法主要用于同步和更新操作信息数据，包括识别和处理不同的表单代码，
     * 更新操作ID、源ID等字段，并处理特殊的操作类型，如公共操作和页面入口操作
     */
    private void updateOptInfo() {
        // 获取操作信息的JSON对象
        Object mapJsonObject = this.mapJsonObject.get(AppTableNames.F_OPTINFO.name());
        if (mapJsonObject == null) {
            return;
        }

        // 将JSON对象转换为列表
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) mapJsonObject;

        // 转换旧的操作信息列表
        List<OptInfo> finalOldList = convertJavaList(OptInfo.class, AppTableNames.F_OPTINFO.name());

        // 初始化映射，用于存储公共操作、操作ID和源ID的映射关系
        Map<String, String> commonOptMap = new HashMap<>();
        Map<String, OptInfo> optIdMap = new HashMap<>();
        Map<String, String> sourceIdMap = new HashMap<>();

        // 遍历旧的操作信息列表，填充映射
        if (finalOldList != null) {
            for (OptInfo oldMap : finalOldList) {
                String formCode = oldMap.getFormCode();
                String oldTopOptId = oldMap.getTopOptId();
                String optId = oldMap.getOptId();
                String preOptId = oldMap.getPreOptId();

                // 判断是否为公共操作或页面入口操作
                boolean isCommonOrPageEnter = OptInfo.OPT_INFO_FORM_CODE_COMMON.equals(formCode)
                    || OptInfo.OPT_INFO_FORM_CODE_PAGE_ENTER.equals(formCode);

                // 处理公共操作和页面入口操作
                if (isCommonOrPageEnter && osId.equals(oldTopOptId)) {
                    commonOptMap.put(formCode, oldMap.getOptId());
                }

                // 根据条件决定是否继续遍历或处理其他类型的操作
                if ((optId.equals(oldTopOptId) && "0".equals(preOptId)) ||
                    isCommonOrPageEnter) {
                    continue;
                } else {
                    optIdMap.put(optId, oldMap);
                    String sourceId = oldMap.getSourceId();
                    String key;
                    if (!StringBaseOpt.isNvl(sourceId)) {
                        key = sourceId + ":" + oldTopOptId;
                    } else {
                        key = optId + ":" + oldTopOptId;
                    }
                    sourceIdMap.put(key, optId);
                }
            }
        }

        // 遍历操作信息列表，更新数据
        for (Map<String, Object> map : list) {
            String optId = StringBaseOpt.objectToString(map.get(OPT_ID));
            String sourceId = StringBaseOpt.objectToString(map.get(SOURCE_ID));
            String topOptId = StringBaseOpt.objectToString(map.get(TOP_OPT_ID));
            String formCode = StringBaseOpt.objectToString(map.get(FORM_CODE));
            String preOptId = StringBaseOpt.objectToString(map.get(PRE_OPT_ID));

            // 处理顶级菜单的操作
            if (optId.equals(topOptId) && "0".equals(preOptId)) {
                map.clear();
                if (!topOptId.equals(osId)) {
                    optInfoDiffMap.put(optId, osId);
                }
                continue;
            }

            // 处理公共操作和页面入口操作
            boolean isCommonOrPageEnter = OptInfo.OPT_INFO_FORM_CODE_COMMON.equals(formCode)
                || OptInfo.OPT_INFO_FORM_CODE_PAGE_ENTER.equals(formCode);
            if (isCommonOrPageEnter) {
                String oldOptId = commonOptMap.get(formCode);
                if (!optId.equals(oldOptId)) {
                    optInfoDiffMap.put(optId, oldOptId);
                }
                continue;
            }

            // 生成UUID并更新映射
            String uuid = "";
            if (finalOldList != null) {
                if (!StringBaseOpt.isNvl(sourceId)) {
                    String sourceKey = sourceId + ":" + osId;
                    String oldOptId = sourceIdMap.get(sourceKey);
                    if (!StringBaseOpt.isNvl(oldOptId)) {
                        uuid = oldOptId;
                    }
                }
                if (StringBaseOpt.isNvl(uuid)) {
                    OptInfo matchOptInfo = optIdMap.get(optId);
                    if (matchOptInfo != null) {
                        if (matchOptInfo.getTopOptId().equals(osId)) {
                            uuid = matchOptInfo.getOptId();
                        } else {
                            uuid = UuidOpt.getUuidAsString();
                        }
                    } else {
                        uuid = optId;
                    }
                }
            } else {
                uuid = optId;
            }
            // 更新操作信息
            if(!uuid.equals(optId)){
                optInfoDiffMap.put(optId, uuid);
            }
            map.put(OPT_ID, uuid);
            map.put(TOP_OPT_ID, osId);
            map.put(OS_ID, osId);
            map.put(UPDATOR, userCode);
            map.put(UPDATE_DATE, now);
            map.put(CREATOR, userCode);
            map.put(CREATE_DATE, now);
            map.put(DOC_ID, uuid);
            if (StringBaseOpt.isNvl(sourceId)) {
                map.put(SOURCE_ID, optId);
            }
        }

        // 更新前置操作ID
        for (Map<String, Object> map : list) {
            updateIfMapped(map, PRE_OPT_ID, optInfoDiffMap);
        }
    }


    /**
     * 更新操作定义信息
     * <p>
     * 本方法主要用于更新操作定义（OptDef）的数据处理它通过检查和更新mapJsonObject中的数据，
     * 以确保操作定义信息是最新的并且正确的
     */
    private void updateOptDef() {
        // 获取操作定义的JSON对象
        Object mapJsonObject = this.mapJsonObject.get(AppTableNames.F_OPTDEF.name());
        // 如果获取的对象为空，则直接返回，不进行后续处理
        if (mapJsonObject == null) {
            return;
        }

        // 将获取的对象转换为List<Map<String, Object>>类型
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) mapJsonObject;

        // 获取操作定义的旧列表，并进行类型转换
        List<OptMethod> finalOldList = convertJavaList(OptMethod.class, AppTableNames.F_OPTDEF.name());

        // 创建操作代码映射表，用于快速查找操作定义
        Map<String, OptMethod> optCodeMap = new HashMap<>();

        // 创建源ID与顶级操作ID的映射表，用于处理特定的源ID和顶级操作ID组合
        Map<String, String> sourceIdMap = new HashMap<>();

        // 如果旧列表不为空，则遍历旧列表，填充映射表
        if (finalOldList != null) {
            for (OptMethod oldMap : finalOldList) {
                String optCode = oldMap.getOptCode();
                String sourceId = oldMap.getSourceId();
                String topOptId = oldMap.getTopOptId();
                optCodeMap.put(optCode, oldMap);
                String key;
                if (!StringBaseOpt.isNvl(sourceId)) {
                    key = sourceId + ":" + topOptId;
                } else {
                    key = optCode + ":" + topOptId;
                }
                sourceIdMap.put(key, optCode);
            }
        }

        // 遍历JSON对象列表，更新每个操作定义的信息
        for (Map<String, Object> map : list) {
            String uuid = "";
            // 从map中获取操作代码和源ID
            String optCode = StringBaseOpt.objectToString(map.get(OPT_CODE));
            String sourceId = StringBaseOpt.objectToString(map.get(SOURCE_ID));
            // 如果旧列表不为空，则尝试在映射表中找到匹配的操作定义
            if (finalOldList != null) {
                if (!StringBaseOpt.isNvl(sourceId)) {
                    String sourceKey = sourceId + ":" + osId;
                    String oldOptMethod = sourceIdMap.get(sourceKey);
                    if (!StringBaseOpt.isNvl(oldOptMethod)) {
                        uuid = oldOptMethod;
                        map.put(API_ID,uuid);
                        map.put(OPT_URL,"/dde/run/"+uuid);
                    }
                }
                if (StringBaseOpt.isNvl(uuid)) {
                    OptMethod matchOptMethod = optCodeMap.get(optCode);
                    if (matchOptMethod != null) {
                        String matchTopOptId = matchOptMethod.getTopOptId();
                        // 检查顶级操作ID是否匹配
                        if (matchTopOptId != null && matchTopOptId.equals(osId)) {
                            uuid = optCode;
                            map.put(API_ID,uuid);
                            map.put(OPT_URL,"/dde/run/"+uuid);
                        } else {
                            uuid = UuidOpt.getUuidAsString();
                            map.remove(OPT_URL);
                        }
                    } else {
                        // 如果没有找到匹配的操作定义，则使用操作代码作为UUID
                        uuid = optCode;
                    }
                }
            } else {
                uuid = optCode;
            }
            // 更新map中的操作代码、更新者、更新日期、创建者和创建日期
            map.put(OPT_CODE, uuid);
            map.put(UPDATOR, userCode);
            map.put(UPDATE_DATE, now);
            map.put(CREATOR, userCode);
            map.put(CREATE_DATE, now);
            // 获取并更新操作ID，如果需要更改
            updateIfMapped(map, OPT_ID, optInfoDiffMap);
            if (StringBaseOpt.isNvl(sourceId)) {
                map.put(SOURCE_ID, optCode);
            }
        }
    }


    /**
     * 更新表关系信息
     * 此方法主要用于更新应用程序中的表关系数据它通过映射JSON对象来更新关系列表，
     * 并确保现有的关系与新的数据保持一致
     */
    private void updateTableRelation() {
        // 获取表关系的JSON对象
        Object mapJsonObject = this.mapJsonObject.get(AppTableNames.F_TABLE_OPT_RELATION.name());
        // 如果对象为空，则直接返回
        if (mapJsonObject == null) {
            return;
        }

        // 将JSON对象转换为列表
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) mapJsonObject;

        // 获取旧的表关系列表
        List<MetaOptRelation> finalOldList = convertJavaList(MetaOptRelation.class, AppTableNames.F_TABLE_OPT_RELATION.name());

        // 初始化映射，用于存储ID与OS_ID的映射关系，以及源ID与新ID的映射关系
        Map<String, String> idMap = new HashMap<>();
        Map<String, String> sourceIdMap = new HashMap<>();

        // 如果旧列表不为空，则遍历旧列表，填充映射
        if (finalOldList != null) {
            for (MetaOptRelation oldMap : finalOldList) {
                String id = oldMap.getId();
                String tableId = oldMap.getTableId();
                String optId = oldMap.getOptId();
                String osId = oldMap.getOsId();
                idMap.put(id, osId);
                if (!StringBaseOpt.isNvl(tableId) && !StringBaseOpt.isNvl(optId)) {
                    String sourceKey = tableId + ":" + optId;
                    sourceIdMap.put(sourceKey, id);
                }
            }
        }

        // 遍历新列表，更新表关系信息
        for (Map<String, Object> map : list) {
            String tableId = StringBaseOpt.objectToString(map.get(TABLE_ID));
            String optId = StringBaseOpt.objectToString(map.get(OPT_ID));
            String id = StringBaseOpt.objectToString(map.get(ID));

            // 更新表ID和操作ID
            String changeTableId = mdTableDiffMap.get(tableId);
            if (changeTableId != null) {
                map.put(TABLE_ID, changeTableId);
                tableId = changeTableId;
            }
            String changeOptId = optInfoDiffMap.get(optId);
            if (changeOptId != null) {
                map.put(OPT_ID, changeOptId);
                optId = changeOptId;
            }
            // 生成或获取UUID
            String uuid = "";
            if (finalOldList != null) {
                String sourceKey = tableId + ":" + optId;
                String oldId = sourceIdMap.get(sourceKey);
                if (!StringBaseOpt.isNvl(oldId)) {
                    uuid = oldId;
                } else {
                    String matchOsId = idMap.get(id);
                    if (matchOsId != null) {
                        if (matchOsId.equals(osId)) {
                            uuid = id;
                        } else {
                            uuid = UuidOpt.getUuidAsString();
                        }
                    } else {
                        uuid = id;
                    }
                }
            } else {
                uuid = id;
            }
            // 更新映射中的ID和OS_ID
            map.put(ID, uuid);
            map.put(OS_ID, osId);
        }
    }


    //上传附件
    private void uploadFiles() {
        if (!uploadFiles || StringUtils.isBlank(zipFilePath)) {
            return;
        }
        List<File> zipArchives = FileSystemOpt.findFiles(zipFilePath, "file.zip");
        if (zipArchives == null || zipArchives.isEmpty()) {
            logger.warn("No file.zip found in path: {}", zipFilePath);
            return;
        }

        String timestamp = DatetimeOpt.convertDateToString(DatetimeOpt.currentUtilDate(), "YYYYMMddHHmmss");
        String tempDirPath = Paths.get(appHome, "u" + timestamp).toString();
        try {
            Files.createDirectories(Paths.get(tempDirPath));
        } catch (IOException e) {
            logger.error("Failed to create temporary directory: {}", tempDirPath, e);
            return;
        }
        File zipFile = zipArchives.get(0);
        try {
            ZipCompressor.release(zipFile, tempDirPath);
        } catch (Exception e) {
            logger.error("Failed to extract ZIP file: {}", zipFile.getAbsolutePath(), e);
            return;
        }
        List<File> extractedFiles = FileSystemOpt.findFiles(tempDirPath, "*");
        if (extractedFiles == null) {
            return;
        }
        for (File file : extractedFiles) {
            processAndUploadFile(file);
        }
        try {
            FileSystemOpt.deleteDirect(tempDirPath);
        } catch (Exception e) {
            logger.warn("Failed to delete temporary directory: {}", tempDirPath, e);
        }
    }

    /**
     * 处理单个文件并上传至文件服务
     */
    private void processAndUploadFile(File file) {
        String fileName = FileSystemOpt.extractFullFileName(file.getPath());
        String oldFileId = extractOldFileIdFromName(fileName);
        if (oldFileId == null) {
            logger.warn("Invalid file name format, no fileId found in '{}'", fileName);
            return;
        }
        FileInfo fileInfo = new FileInfo();
        String cleanFileName = fileName.replaceFirst("\\(" + Pattern.quote(oldFileId) + "\\)", "").trim();
        fileInfo.setFileName(cleanFileName);
        fileInfo.setFileShowPath("/-1");
        fileInfo.setLibraryId(osId);
        fileInfo.setFileCatalog("A");
        String fileId;
        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            fileId = fileInfoOpt.saveFile(fileInfo, -1, inputStream);
        } catch (IOException e) {
            logger.error("Failed to read or save file: {}", fileName, e);
            return;
        }
        if (fileId != null && !fileId.equals(oldFileId)) {
            fileDiffMap.put(oldFileId, fileId);
        }
    }

    /**
     * 从文件名中提取括号中的 oldFileId
     */
    private String extractOldFileIdFromName(String fileName) {
        int startIdx = fileName.indexOf('(');
        int endIdx = fileName.indexOf(')');
        if (startIdx != -1 && endIdx != -1 && startIdx < endIdx) {
            return fileName.substring(startIdx + 1, endIdx);
        }
        return null;
    }

    /**
     * 更新数据包信息
     * 该方法主要用于处理和更新数据包的相关信息，包括验证数据包的有效性、更新数据包的UUID以及其它元数据
     */
    private void updatePacket() {
        // 从JSON对象中获取数据包列表
        Object mapJsonObject = this.mapJsonObject.get(AppTableNames.Q_DATA_PACKET.name());
        if (mapJsonObject == null) {
            return;
        }
        // 强制转换获取到的JSON对象为列表类型
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) mapJsonObject;
        // 获取已存在的数据包列表，并创建映射表以加速查找
        List<DataPacket> finalOldList = convertJavaList(DataPacket.class, AppTableNames.Q_DATA_PACKET.name());
        Map<String, DataPacket> idMap = new HashMap<>();
        Map<String, DataPacket> sourceIdMap = new HashMap<>();
        // 遍历已存在的数据包，创建映射表
        if (finalOldList != null) {
            for (DataPacket oldMap : finalOldList) {
                String packetId = oldMap.getPacketId();
                String sourceId = oldMap.getSourceId();
                String oldOsId = oldMap.getOsId();
                idMap.put(packetId, oldMap);
                String key;
                if (!StringBaseOpt.isNvl(sourceId)) {
                    key = sourceId + ":" + oldOsId;
                } else {
                    key = packetId + ":" + oldOsId;
                }
                sourceIdMap.put(key, oldMap);
            }
        }
        // 遍历新获取的数据包列表，进行更新处理
        for (Map<String, Object> map : list) {
            String uuid = "";
            String packetId = StringBaseOpt.objectToString(map.get(PACKET_ID));
            String taskType = StringBaseOpt.objectToString(map.get(TASK_TYPE));
            String sourceId = StringBaseOpt.objectToString(map.get(SOURCE_ID));
            // 检查数据包是否已经存在
            if (finalOldList != null) {
                if (!StringBaseOpt.isNvl(packetId)) {
                    String sourceKey = sourceId + ":" + osId;
                    DataPacket matchSourcePacket = sourceIdMap.get(sourceKey);
                    if (matchSourcePacket != null) {
                        uuid = matchSourcePacket.getPacketId();
                        if (ConstantValue.TASK_TYPE_AGENT.equals(taskType)) {
                            map.put(TASK_CRON, matchSourcePacket.getTaskCron());
                            map.put(IS_VALID, matchSourcePacket.getIsValid());
                        }
                    }
                }
                if (StringBaseOpt.isNvl(uuid)) {
                    DataPacket matchPacket = idMap.get(packetId);
                    if (matchPacket != null) {
                        String matchOsId = matchPacket.getOsId();
                        // 检查顶级操作ID是否匹配
                        if (matchOsId != null && matchOsId.equals(osId)) {
                            uuid = packetId;
                            if (ConstantValue.TASK_TYPE_AGENT.equals(taskType)) {
                                map.put(TASK_CRON, matchPacket.getTaskCron());
                                map.put(IS_VALID, matchPacket.getIsValid());
                            }
                        } else {
                            uuid = UuidOpt.getUuidAsString();
                            if (ConstantValue.TASK_TYPE_AGENT.equals(taskType)) {
                                map.put(IS_VALID, false);
                            }
                        }
                    } else {
                        // 如果没有找到匹配的操作定义，则使用操作代码作为UUID
                        uuid = packetId;
                        if (ConstantValue.TASK_TYPE_AGENT.equals(taskType)) {
                            map.put(IS_VALID, false);
                        }
                    }
                }
            }
            // 更新数据包的UUID和其它元数据
            if(!uuid.equals(packetId)){
                dataPacketDiffMap.put(packetId, uuid);
            }
            map.put(PACKET_ID, uuid);
            map.put(OS_ID, osId);
            map.put(UPDATE_DATE, now);
            map.put(PUBLISH_DATE, now);
            map.put(RECORDER, userCode);
            map.put(OPT_CODE, uuid);
            // 更新操作ID，如果需要的话
            updateIfMapped(map, OPT_ID, optInfoDiffMap);
            if (StringBaseOpt.isNvl(sourceId)) {
                map.put(SOURCE_ID, packetId);
            }
        }
    }


    /**
     * 更新操作定义数据包中的信息
     * 此方法旨在处理和更新操作定义数据包内的各项信息，以确保数据包与当前数据状态保持同步
     */
    private void updateOptDefUsePacket() {
        // 获取操作定义数据包的JSON对象
        Object mapJsonObject = this.mapJsonObject.get(AppTableNames.F_OPTDEF.name());
        // 如果数据包不存在，则直接返回，无需处理
        if (mapJsonObject == null) {
            return;
        }
        // 强制转换JSON对象为列表，以便后续处理
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) mapJsonObject;
        // 遍历列表，更新每个操作定义项中的API ID、操作URL和操作代码
        for (Map<String, Object> map : list) {
            // 将API ID转换为字符串
            String apiId = StringBaseOpt.objectToString(map.get(API_ID));
            // 如果API ID不为空，则进一步处理
            if (!StringBaseOpt.isNvl(apiId)) {
                // 查找API ID在差异映射中的对应变更ID
                String changeId = dataPacketDiffMap.get(apiId);
                // 如果变更ID存在，则更新操作定义项中的相关字段
                if (changeId != null) {
                    map.put(API_ID, changeId);
                    map.put(OPT_URL, DDE_RUN + changeId);
                    map.put(OPT_CODE, changeId);
                }
            }
        }
        // 再次遍历列表，清除操作URL为空的操作定义项
        for (Map<String, Object> map : list) {
            String optUrl = StringBaseOpt.objectToString(map.get(OPT_URL));
            // 如果操作URL为空，则清除该项
            if (StringBaseOpt.isNvl(optUrl)) {
                map.clear();
            }
        }
    }


    /**
     * 更新数据包参数
     */
    private void updatePacketParams() {
        Object mapJsonObject = this.mapJsonObject.get(AppTableNames.Q_DATA_PACKET_PARAM.name());
        if (mapJsonObject == null) {
            return;
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) mapJsonObject;
        for (Map<String, Object> map : list) {
            updateIfMapped(map, PACKET_ID, dataPacketDiffMap);
        }
    }


    /**
     * 更新元数据表单信息
     * 此方法主要用于同步和更新元数据表单模型（MetaFormModel）的相关信息
     * 它通过比较现有的数据包和新的数据包，来确定如何更新或插入新的数据
     */
    private void updateMetaForm() {
        // 获取JSON对象中的元数据表单信息
        Object mapJsonObject = this.mapJsonObject.get(AppTableNames.M_META_FORM_MODEL.name());
        if (mapJsonObject == null) {
            // 如果获取的信息为空，则直接返回，不进行任何操作
            return;
        }
        // 将获取到的JSON对象转换为列表
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) mapJsonObject;
        // 获取已存在的元数据表单模型列表，并将其转换为Java对象列表
        List<MetaFormModel> finalOldList = convertJavaList(MetaFormModel.class, AppTableNames.M_META_FORM_MODEL.name());
        // 初始化两个映射表，用于快速查找元数据表单模型
        Map<String, MetaFormModel> idMap = new HashMap<>();
        Map<String, MetaFormModel> sourceIdMap = new HashMap<>();
        // 遍历已存在的数据包，创建映射表
        if (finalOldList != null) {
            for (MetaFormModel oldMap : finalOldList) {
                String modelId = oldMap.getModelId();
                String sourceId = oldMap.getSourceId();
                String oldOsId = oldMap.getOsId();
                idMap.put(modelId, oldMap);
                String key;
                if (!StringBaseOpt.isNvl(sourceId)) {
                    key = sourceId + ":" + oldOsId;
                } else {
                    key = modelId + ":" + oldOsId;
                }
                sourceIdMap.put(key, oldMap);
            }
        }
        // 遍历新获取的列表，进行数据更新或插入的处理
        for (Map<String, Object> map : list) {
            String uuid = "";
            String modelId = StringBaseOpt.objectToString(map.get(MODEL_ID));
            String sourceId = StringBaseOpt.objectToString(map.get(SOURCE_ID));
            // 如果已存在的数据包不为空
            if (finalOldList != null) {
                if (!StringBaseOpt.isNvl(modelId)) {
                    String sourceKey = sourceId + ":" + osId;
                    MetaFormModel matchSource = sourceIdMap.get(sourceKey);
                    if (matchSource != null) {
                        // 如果在sourceIdMap中找到匹配项，则使用匹配项的modelId和isValid
                        uuid = matchSource.getModelId();
                    }
                }
                if (StringBaseOpt.isNvl(uuid)) {
                    MetaFormModel matchId = idMap.get(modelId);
                    if (matchId != null) {
                        String matchOsId = matchId.getOsId();
                        // 检查顶级操作ID是否匹配
                        if (matchOsId != null && matchOsId.equals(osId)) {
                            uuid = modelId;
                        } else {
                            // 如果不匹配，则生成新的UUID，并记录到diffMap中
                            uuid = UuidOpt.getUuidAsString();
                        }
                    } else {
                        // 如果没有找到匹配的操作定义，则使用操作代码作为UUID
                        uuid = modelId;
                    }
                }
            } else {
                // 如果已存在的数据包为空，则直接使用modelId作为UUID
                uuid = modelId;
            }
            // 更新map中的UUID和其他必要字段
            if(!uuid.equals(modelId)){
                metaFormDiffMap.put(modelId, uuid);
            }
            map.put(MODEL_ID, uuid);
            map.put(OS_ID, osId);
            map.put(PUBLISH_DATE, now);
            map.put(RECORDER, userCode);
            if (StringBaseOpt.isNvl(sourceId)) {
                map.put(SOURCE_ID, modelId);
            }
            // 处理操作ID的变更
            updateIfMapped(map, OPT_ID, optInfoDiffMap);
        }
    }

    /**
     * 使用metaForm模型信息更新操作信息
     * 此方法从mapJsonObject中获取metaForm模型数据，并根据metaFormDiffMap中的差异信息更新操作路由和URL
     */
    private void updateOptInfoUseMetaForm() {
        // 获取metaForm模型的JSON对象
        Object mapJsonObject = this.mapJsonObject.get(AppTableNames.F_OPTINFO.name());
        // 如果对象为空，则直接返回
        if (mapJsonObject == null) {
            return;
        }

        // 将获取到的对象转换为List<Map<String, Object>>类型
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) mapJsonObject;

        // 遍历列表中的每个Map对象
        for (Map<String, Object> map : list) {
            updateIfMapped(map, OPT_ROUTE, metaFormDiffMap);
            // 获取并转换原始操作URL为字符串
            String originalOptUrl = StringBaseOpt.objectToString(map.get(OPT_URL));
            // 如果原始操作URL为空，则跳过当前循环
            if (originalOptUrl == null) {
                continue;
            }
            // 更新操作URL
            String prefix = "./page/#/models/";
            if (originalOptUrl.startsWith(prefix)) {
                String modelId = originalOptUrl.substring(prefix.length());
                String changeModelId = metaFormDiffMap.get(modelId);
                if (!StringBaseOpt.isNvl(changeModelId)) {
                    String updatedOptUrl = prefix + changeModelId;
                    map.put(OPT_URL, updatedOptUrl);
                }
            }
        }
    }


    /**
     * 更新工作流团队角色信息
     * 该方法用于处理和更新工作流中的团队角色信息，主要执行以下操作：
     * 1. 从mapJsonObject中获取指定表名的数据
     * 2. 如果数据为空，则直接返回
     * 3. 将获取到的数据转换为List<Map<String, Object>>类型
     * 4. 从数据库中获取旧的团队角色信息列表，并创建一个映射表sourceIdMap用于存储旧信息中的角色代码、操作ID和团队角色ID
     * 5. 遍历新数据列表，根据映射表更新角色代码和操作ID，生成或更新团队角色ID
     * 6. 更新数据列表中的团队角色ID和修改时间
     */
    private void updateWfOptTeamRole() {
        // 从mapJsonObject中获取指定表名的数据
        Object mapJsonObject = this.mapJsonObject.get(AppTableNames.WF_OPT_TEAM_ROLE.name());
        // 如果数据为空，则直接返回
        if (mapJsonObject == null) {
            return;
        }
        // 将获取到的数据转换为List<Map<String, Object>>类型
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) mapJsonObject;
        // 从数据库中获取旧的团队角色信息列表
        List<OptTeamRole> finalOldList = convertJavaList(OptTeamRole.class, AppTableNames.WF_OPT_TEAM_ROLE.name());
        // 创建一个映射表用于存储旧信息中的角色代码、操作ID和团队角色ID
        Map<String, OptTeamRole> sourceIdMap = new HashMap<>();
        // 遍历旧的团队角色信息列表，填充映射表
        if (finalOldList != null) {
            for (OptTeamRole oldMap : finalOldList) {
                String roleCode = oldMap.getRoleCode();
                String optId = oldMap.getOptId();
                sourceIdMap.put(roleCode + ":" + optId, oldMap);
            }
        }
        // 遍历新数据列表，更新角色代码和操作ID，生成或更新团队角色ID
        for (Map<String, Object> map : list) {
            String roleCode = StringBaseOpt.objectToString(map.get(ROLE_CODE));
            String teamRoleId = StringBaseOpt.objectToString(map.get(OPT_TEAM_ROLE_ID));
            // 获取变更后的操作ID，如果存在，则更新map中的操作ID
            updateIfMapped(map, OPT_ID, optInfoDiffMap);
            String optId = StringBaseOpt.objectToString(map.get(OPT_ID));
            // 生成或获取团队角色ID
            String uuid = "";
            if (finalOldList != null) {
                OptTeamRole changeTeamRole = sourceIdMap.get(roleCode + ":" + optId);
                if (changeTeamRole!=null) {
                    uuid= changeTeamRole.getOptTeamRoleId();
                } else {
                    uuid = UuidOpt.getUuidAsString();
                }
            } else {
                uuid = teamRoleId;
            }
            // 更新map中的团队角色ID和修改时间
            map.put(OPT_TEAM_ROLE_ID, uuid);
            map.put(MODIFY_TIME, now);
        }
    }


    /**
     * 更新工作流优化变量定义
     * 该方法用于处理和更新工作流优化变量定义的数据
     * 它通过比较现有数据和新数据，进行必要的转换和更新
     */
    private void updateWfOptVariable() {
        // 获取WF_OPT_VARIABLE_DEFINE表的JSON对象
        Object mapJsonObject = this.mapJsonObject.get(AppTableNames.WF_OPT_VARIABLE_DEFINE.name());
        // 如果JSON对象为空，则直接返回，无需进一步处理
        if (mapJsonObject == null) {
            return;
        }

        // 将JSON对象转换为List<Map<String, Object>>类型
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) mapJsonObject;

        // 获取WF_OPT_VARIABLE_DEFINE表的旧数据列表
        List<OptVariableDefine> finalOldList = convertJavaList(OptVariableDefine.class, AppTableNames.WF_OPT_VARIABLE_DEFINE.name());

        // 创建一个映射，用于存储变量名和操作ID的组合以及对应的变量ID
        Map<String, OptVariableDefine> sourceIdMap = new HashMap<>();

        // 如果旧数据列表不为空，则遍历列表，填充sourceIdMap
        if (finalOldList != null) {
            for (OptVariableDefine oldMap : finalOldList) {
                String variableName = oldMap.getVariableName();
                String optId = oldMap.getOptId();
                sourceIdMap.put(variableName + ":" + optId, oldMap);
            }
        }
        // 遍历新数据列表，进行数据更新
        for (Map<String, Object> map : list) {
            String variableName = StringBaseOpt.objectToString(map.get(VARIABLE_NAME));
            String variableId = StringBaseOpt.objectToString(map.get(OPT_VARIABLE_ID));
            // 获取操作ID的变更信息
            updateIfMapped(map, OPT_ID, optInfoDiffMap);
            String optId = StringBaseOpt.objectToString(map.get(OPT_ID));
            // 初始化UUID为空字符串
            String uuid = "";
            // 如果旧数据列表不为空，则尝试获取变量ID的变更信息
            if (finalOldList != null) {
                OptVariableDefine changeVariable = sourceIdMap.get(variableName + ":" + optId);
                // 如果变量ID有变更，则更新map中的变量ID
                if (changeVariable!=null) {
                    uuid= changeVariable.getOptVariableId();
                } else {
                    // 如果没有变更信息，则生成新的UUID
                    uuid = UuidOpt.getUuidAsString();
                }
            } else {
                // 如果旧数据列表为空，则直接使用原始变量ID
                uuid = variableId;
            }
            // 更新map中的变量ID和修改时间
            map.put(OPT_VARIABLE_ID, uuid);
            map.put(MODIFY_TIME, now);
        }
    }


    /**
     * 更新工作流定义信息
     * 此方法主要用于同步和更新工作流定义数据，确保本地数据与源数据一致
     */
    private void updateWfDefine() {
        // 获取工作流定义的JSON对象
        Object mapJsonObject = this.mapJsonObject.get(AppTableNames.WF_FLOW_DEFINE.name());
        // 如果对象为空，则直接返回，无需更新
        if (mapJsonObject == null) {
            return;
        }

        // 将JSON对象转换为List集合，用于后续处理
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) mapJsonObject;

        // 对工作流定义列表按版本号进行排序，以确保数据的有序性
        list.sort((o1, o2) -> GeneralAlgorithm.compareTwoObject(o1.get(VERSION), o2.get(VERSION)));

        // 获取已存在的工作流定义信息，转换为FlowInfo对象列表
        List<FlowInfo> finalOldList = convertJavaList(FlowInfo.class, AppTableNames.WF_FLOW_DEFINE.name());

        // 初始化两个HashMap，用于存储旧的工作流信息，以便快速查找
        Map<String, FlowInfo> idMap = new HashMap<>();
        Map<String, FlowInfo> sourceIdMap = new HashMap<>();

        // 遍历旧的工作流定义列表，填充HashMap以便后续比较
        if (finalOldList != null) {
            for (FlowInfo oldMap : finalOldList) {
                String flowCode = oldMap.getFlowCode();
                String sourceId = oldMap.getSourceId();
                String oldOsId = oldMap.getOsId();
                idMap.put(flowCode, oldMap);
                String key;
                if (!StringBaseOpt.isNvl(sourceId)) {
                    key = sourceId + ":" + oldOsId;
                } else {
                    key = flowCode + ":" + oldOsId;
                }
                sourceIdMap.put(key + ":" + oldOsId, oldMap);
            }
        }

        // 遍历排序后的工作流定义列表，进行数据更新
        for (Map<String, Object> map : list) {
            String flowCode = StringBaseOpt.objectToString(map.get(FLOW_CODE));
            int version = NumberBaseOpt.castObjectToInteger(map.get(VERSION), -1);
            String sourceId = StringBaseOpt.objectToString(map.get(SOURCE_ID));

            // 对于版本号为0的数据，进行特殊处理
            if (version == 0) {
                String uuid = "";
                if (finalOldList != null) {
                    String sourceKey = sourceId + ":" + osId;
                    FlowInfo matchSource = sourceIdMap.get(sourceKey);
                    // 根据sourceId和osId查找匹配的uuid
                    if (matchSource != null) {
                        uuid = matchSource.getFlowCode();
                    }
                    // 如果未找到匹配的uuid，尝试根据flowCode查找
                    if (StringBaseOpt.isNvl(uuid)) {
                        FlowInfo matchId = idMap.get(flowCode);
                        if (matchId != null) {
                            String matchOsId = matchId.getOsId();
                            if (matchOsId != null && matchOsId.equals(osId)) {
                                uuid = flowCode;
                            } else {
                                uuid = UuidOpt.getUuidAsString();
                            }
                        } else {
                            uuid = flowCode;
                        }
                    }
                } else {
                    uuid = flowCode;
                }
                // 如果生成的uuid与flowCode不一致，说明有变化，记录下来
                if (!uuid.equals(flowCode)) {
                    flowDefineDiffMap.put(flowCode, uuid);
                }
            }
            // 更新工作流定义的发布日期和操作系统ID
            map.put(FLOW_PUBLISH_DATE, now);
            map.put(OS_ID, osId);
        }
    }


    /**
     * 更新工作流节点信息
     * 此方法主要用于同步和更新工作流节点数据，通过比较新旧数据列表，识别并处理差异
     */
    private void updateWfNode() {
        // 从JSON对象中获取工作流节点列表
        Object mapJsonObject = this.mapJsonObject.get(AppTableNames.WF_NODE.name());
        if (mapJsonObject == null) {
            return;
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) mapJsonObject;
        // 获取旧的工作流节点列表，并将其转换为NodeInfo对象列表
        List<NodeInfo> finalOldList = convertJavaList(NodeInfo.class, AppTableNames.WF_NODE.name());
        // 初始化两个HashMap，用于存储旧的工作流节点信息，以便后续快速查找和比较
        Map<String, NodeInfo> idMap = new HashMap<>();
        Map<String, NodeInfo> sourceIdMap = new HashMap<>();

        // 遍历旧的工作流定义列表，填充HashMap以便后续比较
        if (finalOldList != null) {
            for (NodeInfo oldMap : finalOldList) {
                String nodeId = oldMap.getNodeId();
                String sourceId = oldMap.getSourceId();
                String oldOsId = oldMap.getOsId();
                idMap.put(nodeId, oldMap);
                String key;
                if (!StringBaseOpt.isNvl(sourceId)) {
                    key = sourceId + ":" + oldOsId;
                } else {
                    key = nodeId + ":" + oldOsId;
                }
                sourceIdMap.put(key, oldMap);
            }
        }
        // 遍历新的工作流节点列表，处理每个节点的信息
        for (Map<String, Object> map : list) {
            String nodeId = StringBaseOpt.objectToString(map.get(NODE_ID));
            String sourceId = StringBaseOpt.objectToString(map.get(SOURCE_ID));
            String uuid = "";
            // 根据旧的数据列表，查找并确定节点的UUID
            if (finalOldList != null) {
                String sourceKey = sourceId + ":" + osId;
                NodeInfo matchSource = sourceIdMap.get(sourceKey);
                // 根据sourceId和osId查找匹配的uuid
                if (matchSource != null) {
                    uuid = matchSource.getNodeId();
                }
                // 如果未找到匹配的uuid，尝试根据flowCode查找
                if (StringBaseOpt.isNvl(uuid)) {
                    NodeInfo matchId = idMap.get(nodeId);
                    if (matchId != null) {
                        String matchOsId = matchId.getOsId();
                        if (matchOsId != null && matchOsId.equals(osId)) {
                            uuid = nodeId;
                        } else {
                            uuid = UuidOpt.getUuidAsString();
                        }
                    } else {
                        uuid = nodeId;
                    }
                }
            } else {
                uuid = nodeId;
            }
            // 如果节点ID和UUID不一致，记录到差异映射表中
            if (!uuid.equals(nodeId)) {
                wfNodeDiffMap.put(nodeId, uuid);
            }
            // 更新节点的UUID和操作系统ID
            map.put(NODE_ID, uuid);
            map.put(OS_ID, osId);
            // 根据映射表更新FLOW_CODE、SUB_FLOW_CODE和OPT_ID
            updateIfMapped(map, FLOW_CODE, flowDefineDiffMap);
            updateIfMapped(map, SUB_FLOW_CODE, flowDefineDiffMap);
            updateIfMapped(map, OPT_ID, optInfoDiffMap);
            // 处理OPT_CODE的更新
            String optCode = StringBaseOpt.objectToString(map.get(OPT_CODE));
            String nodeType = StringBaseOpt.objectToString(map.get(NODE_TYPE));
            boolean isAutoNodeType = NodeInfo.NODE_TYPE_AUTO.equals(nodeType);
            String changeOptCode = isAutoNodeType ? dataPacketDiffMap.get(optCode) : metaFormDiffMap.get(optCode);
            if (!StringBaseOpt.isNvl(changeOptCode)) {
                map.put(OPT_CODE, changeOptCode);
            }
        }
    }


    /**
     * 更新工作流定义使用差异映射
     * 该方法从mapJsonObject中获取工作流定义的JSON对象，然后将其转换为列表
     * 遍历该列表，根据差异映射更新工作流定义中的字段
     */
    private void updateWfDefineUseDiffMap() {
        // 从mapJsonObject中获取工作流定义的JSON对象
        Object mapJsonObject = this.mapJsonObject.get(AppTableNames.WF_FLOW_DEFINE.name());
        // 如果对象为空，则直接返回
        if (mapJsonObject == null) {
            return;
        }
        // 将JSON对象转换为列表
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) mapJsonObject;
        // 创建一个映射来存储所有替换项
        Map<String, String> allReplacements = new HashMap<>();
        // 将metaFormDiffMap中的所有条目添加到allReplacements中
        allReplacements.putAll(metaFormDiffMap);
        // 将dataPacketDiffMap中的所有条目添加到allReplacements中
        allReplacements.putAll(dataPacketDiffMap);
        // 将optInfoDiffMap中的所有条目添加到allReplacements中
        allReplacements.putAll(optInfoDiffMap);
        // 遍历列表中的每个映射
        for (Map<String, Object> map : list) {
            // 如果映射中包含FIRST_NODE_ID字段，则根据wfNodeDiffMap更新该字段
            updateIfMapped(map, FIRST_NODE_ID, wfNodeDiffMap);
            // 如果映射中包含OPT_ID字段，则根据optInfoDiffMap更新该字段
            updateIfMapped(map, OPT_ID, optInfoDiffMap);
            // 如果映射中包含FLOW_CODE字段，则根据flowDefineDiffMap更新该字段
            updateIfMapped(map, FLOW_CODE, flowDefineDiffMap);
            // 替换映射中FLOW_XML_DESC字段中的模板变量
            replaceTemplateFields(map, FLOW_XML_DESC, allReplacements);
        }
    }


    /**
     * 更新工作流转换信息
     * 此方法主要用于同步工作流转换数据，通过比较已有的工作流定义和新的数据，
     * 来确定如何更新或插入新的工作流转换信息
     */
    private void updateWfTransition() {
        // 获取工作流转换的JSON对象
        Object mapJsonObject = this.mapJsonObject.get(AppTableNames.WF_TRANSITION.name());
        // 如果JSON对象为空，则直接返回，无需进一步处理
        if (mapJsonObject == null) {
            return;
        }
        // 将JSON对象转换为List<Map<String, Object>>类型
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) mapJsonObject;
        // 获取旧的工作流转换列表，并将其转换为FlowTransition对象列表
        List<FlowTransition> finalOldList = convertJavaList(FlowTransition.class, AppTableNames.WF_TRANSITION.name());
        // 初始化一个HashSet用于存储旧的工作流转换ID
        Set<String> idSet = new HashSet<>();
        // 初始化一个HashMap用于存储旧的工作流定义信息，以便后续比较
        Map<String, String> sourceIdMap = new HashMap<>();

        // 遍历旧的工作流定义列表，填充HashMap以便后续比较
        if (finalOldList != null) {
            for (FlowTransition oldMap : finalOldList) {
                String transId = oldMap.getTransId();
                String flowCode = oldMap.getFlowCode();
                String startNodeId = oldMap.getStartNodeId();
                String endNodeId = oldMap.getEndNodeId();
                idSet.add(transId);
                // 如果sourceId和oldOsId都不为空，则组合成键值对存入sourceIdMap
                if (!StringBaseOpt.isNvl(flowCode) && !StringBaseOpt.isNvl(startNodeId) && !StringBaseOpt.isNvl(endNodeId)) {
                    sourceIdMap.put(flowCode + ":" + startNodeId + ":" + endNodeId, transId);
                }
            }
        }
        // 遍历新的工作流转换列表，进行更新处理
        for (Map<String, Object> map : list) {
            // 更新工作流代码、开始节点ID和结束节点ID的映射
            updateIfMapped(map, FLOW_CODE, flowDefineDiffMap);
            updateIfMapped(map, START_NODE_ID, wfNodeDiffMap);
            updateIfMapped(map, END_NODE_ID, wfNodeDiffMap);
            // 从当前Map中获取转换ID、工作流代码、开始节点ID和结束节点ID
            String transId = StringBaseOpt.objectToString(map.get(TRANS_ID));
            String flowCode = StringBaseOpt.objectToString(map.get(FLOW_CODE));
            String startNodeId = StringBaseOpt.objectToString(map.get(START_NODE_ID));
            String endNodeId = StringBaseOpt.objectToString(map.get(END_NODE_ID));
            String uuid = "";
            // 如果旧的工作流转换列表不为空，则尝试找到匹配的旧记录
            if (finalOldList != null) {
                String sourceKey = flowCode + ":" + startNodeId + ":" + endNodeId;
                String matchSource = sourceIdMap.get(sourceKey);
                if (!StringBaseOpt.isNvl(matchSource)) {
                    uuid = matchSource;
                }
                // 如果未找到匹配的UUID且当前转换ID存在于旧记录中，则生成新的UUID
                if (StringBaseOpt.isNvl(uuid)) {
                    if (idSet.contains(transId)) {
                        uuid = UuidOpt.getUuidAsString();
                    } else {
                        uuid = transId;
                    }
                }
            } else {
                // 如果旧的工作流转换列表为空，则直接使用当前转换ID作为UUID
                uuid = transId;
            }
            // 将确定的UUID存回当前Map中
            map.put(TRANS_ID, uuid);
        }
    }


    /**
     * 使用差异映射更新数据包信息
     * 该方法旨在通过合并多个差异映射（mdTableDiffMap、databaseDiffMap、dataPacketDiffMap、dictionaryDiffMap、fileDiffMap、flowDefineDiffMap）
     * 来更新数据包中的每个模板的相关字段
     */
    private void updatePacketUseDiffMap() {
        // 获取数据包的JSON对象
        Object mapJsonObject = this.mapJsonObject.get(AppTableNames.Q_DATA_PACKET.name());
        // 如果数据包JSON对象为空，则直接返回
        if (mapJsonObject == null) {
            return;
        }
        // 强制转换JSON对象为列表，列表中的每个元素都是一个字符串到对象的映射
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) mapJsonObject;
        // 合并所有替换源
        Map<String, String> replacementMap = new HashMap<>();
        replacementMap.putAll(mdTableDiffMap);
        replacementMap.putAll(databaseDiffMap);
        replacementMap.putAll(dataPacketDiffMap);
        replacementMap.putAll(dictionaryDiffMap);
        replacementMap.putAll(fileDiffMap);
        replacementMap.putAll(flowDefineDiffMap);
        // 遍历列表，更新每个模板的相关字段
        for (Map<String, Object> map : list) {
            replaceTemplateFields(map, DATA_OPT_DESC_JSON, replacementMap);
        }
    }


    /**
     * 使用差异映射更新元表单数据
     * 该方法从mapJsonObject中获取元表单模型数据，并应用一系列差异映射来更新这些数据
     * 差异映射包含了不同来源的变更，如metaFormDiffMap、dataPacketDiffMap等
     */
    private void updateMetaFormUseDiffMap() {
        // 从mapJsonObject中获取元表单模型数据
        Object mapJsonObject = this.mapJsonObject.get(AppTableNames.M_META_FORM_MODEL.name());
        // 如果获取的数据为空，则直接返回，不进行后续操作
        if (mapJsonObject == null) {
            return;
        }
        // 将获取的数据转换为列表，包含映射字符串到对象的映射
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) mapJsonObject;
        // 创建一个哈希映射来存储所有的替换项
        Map<String, String> allReplacements = new HashMap<>();
        // 将所有的差异映射合并到一个映射中
        allReplacements.putAll(metaFormDiffMap);
        allReplacements.putAll(dataPacketDiffMap);
        allReplacements.putAll(dictionaryDiffMap);
        allReplacements.putAll(fileDiffMap);
        allReplacements.putAll(flowDefineDiffMap);
        // 遍历列表，替换模板字段中的占位符
        for (Map<String, Object> map : list) {
            replaceTemplateFields(map, FORM_TEMPLATE, allReplacements);
            replaceTemplateFields(map, MOBILE_FORM_TEMPLATE, allReplacements);
            replaceTemplateFields(map, STRUCTURE_FUNCTION, allReplacements);
        }
    }


    /**
     * 根据提供的替换项替换map中指定字段的模板内容
     * 此方法用于处理模板替换逻辑，将所有需要替换的内容根据字段键从map中提取出来，
     * 并根据allReplacements中的替换规则进行替换，最后将替换后的结果放回map中
     *
     * @param map             map对象，包含需要被替换模板内容的字段
     * @param fieldKey        需要进行模板替换的字段键
     * @param allReplacements 包含所有替换规则的映射，键是要被替换的字符串，值是替换后的字符串
     */
    private void replaceTemplateFields(Map<String, Object> map, String fieldKey, Map<String, String> allReplacements) {
        // 提取map中指定字段键的模板内容
        Object templateObj = map.get(fieldKey);
        // 如果指定字段键的模板内容为空，则直接返回，无需进行替换操作
        if (templateObj == null) {
            return;
        }
        // 将模板内容转换为字符串形式
        String form = templateObj.toString();
        // 遍历所有替换规则，对模板内容进行替换
        for (Map.Entry<String, String> entry : allReplacements.entrySet()) {
            // 使用StringUtils的replace方法进行字符串替换
            form = StringUtils.replace(form, entry.getKey(), entry.getValue());
        }
        // 将替换后的模板内容放回map中，覆盖原始内容
        map.put(fieldKey, form);
    }

    /**
     * 通用字段替换方法
     */
    private void updateIfMapped(Map<String, Object> map, String key, Map<String, String> diffMap) {
        String value = StringBaseOpt.objectToString(map.get(key));
        String newValue = diffMap.get(value);
        if (!StringBaseOpt.isNvl(newValue)) {
            map.put(key, newValue);
        }
    }

    private <T> List<T> convertJavaList(Class<T> type, String className) {
        if (notHaveOldData(className)) {
            return null;
        }
        return oldAppObject.getJSONArray(className).toJavaList(type);
    }

    private boolean notHaveOldData(String dataName) {
        return oldAppObject.get(dataName) == null || oldAppObject.getJSONArray(dataName).isEmpty();
    }

    private JsonAppVo createOsInfo() {
        // 防御 mapJsonObject 为 null
        if (mapJsonObject == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_OS_INFO.name());
        // 防御 list 为 null
        if (list == null) {
            return this;
        }
        // 防御 appList 未初始化
        if (appList == null) {
            appList = new ArrayList<>();
        }
        try {
            List<Object> convertedList = convertMap(OsInfo.class, list);
            if (convertedList != null) {
                appList.addAll(convertedList);
            }
        } catch (Exception e) {
            logger.error("转换 OsInfo 出错", e);
        }
        return this;
    }

    private JsonAppVo createDataCatalog() {
        //不更新字典
        if (!this.runDictionary) {
            return this;
        }
        // 防御 mapJsonObject 为 null
        if (mapJsonObject == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_DATACATALOG.name());
        // 防御 list 为 null
        if (list == null) {
            return this;
        }
        // 防御 appList 未初始化
        if (appList == null) {
            appList = new ArrayList<>();
        }
        try {
            List<Object> convertedList = convertMap(DataCatalog.class, list);
            if (convertedList != null) {
                appList.addAll(convertedList);
            }
        } catch (Exception e) {
            logger.error("转换 DataCatalog 出错", e);
        }
        return this;
    }

    private JsonAppVo createDataDictionary() {
        //不更新字典
        if (!this.runDictionary) {
            return this;
        }
        // 防御 mapJsonObject 为 null
        if (mapJsonObject == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_DATADICTIONARY.name());
        // 防御 list 为 null
        if (list == null) {
            return this;
        }
        // 防御 appList 未初始化
        if (appList == null) {
            appList = new ArrayList<>();
        }
        try {
            List<Object> convertedList = convertMap(DataDictionary.class, list);
            if (convertedList != null) {
                appList.addAll(convertedList);
            }
        } catch (Exception e) {
            logger.error("转换 DataDictionary 出错", e);
        }
        return this;
    }


    private JsonAppVo createApplicationDictionary() {
        //不更新字典
        if (!this.runDictionary) {
            return this;
        }
        // 防御 mapJsonObject 为 null
        if (mapJsonObject == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.M_APPLICATION_DICTIONARY.name());
        // 防御 list 为 null
        if (list == null) {
            return this;
        }
        // 防御 appList 未初始化
        if (appList == null) {
            appList = new ArrayList<>();
        }
        try {
            List<Object> convertedList = convertMap(ApplicationDictionary.class, list);
            if (convertedList != null) {
                appList.addAll(convertedList);
            }
        } catch (Exception e) {
            logger.error("转换 ApplicationDictionary 出错", e);
        }
        return this;
    }

    private JsonAppVo createMdTableWithColumnObject() {
        if (!this.runMetaData) {
            return this;
        }
        // 获取 F_MD_TABLE 数据
        List<Map<String, Object>> tableList = mapJsonObject.get(AppTableNames.F_MD_TABLE.name());
        if (tableList == null) {
            return this;
        }

        // 转换并添加 MetaTable 对象
        metaObject.addAll(convertMap(MetaTable.class, tableList));

        // 处理 PendingMetaTable 列表
        List<Object> pendingTableList = convertMap(PendingMetaTable.class, tableList);
        pendingTableList.forEach(map -> handlePendingMetaTable((PendingMetaTable) map));

        // 获取 F_MD_COLUMN 数据
        List<Map<String, Object>> columnList = mapJsonObject.get(AppTableNames.F_MD_COLUMN.name());
        if (columnList == null) {
            return this;
        }

        // 转换并添加 MetaColumn 对象
        metaObject.addAll(convertMap(MetaColumn.class, columnList));

        // 设置 MAX_LENGTH 字段（注意：这是对原始数据的修改）
        columnList.forEach(map -> map.put(MAX_LENGTH, map.get(COLUMN_LENGTH)));

        // 添加 PendingMetaColumn 对象
        appList.addAll(convertMap(PendingMetaColumn.class, columnList));

        return this;
    }

    // 封装对 PendingMetaTable 的处理逻辑
    private void handlePendingMetaTable(PendingMetaTable pendingMetaTable) {
        if (TABLE_TYPE.equals(pendingMetaTable.getTableType())) {
            pendingMetaTable.setTableState(NO_PUBLISH);
            appList.add(pendingMetaTable);
        }
    }

    private JsonAppVo createMdRelationWithDetailObject() {
        if (!this.runMetaData) {
            return this;
        }
        List<Map<String, Object>> relationList = mapJsonObject.get(AppTableNames.F_MD_RELATION.name());
        if (relationList != null) {
            try {
                List<Object> convertedList = convertMap(MetaRelation.class, relationList);
                if (convertedList != null) {
                    metaObject.addAll(convertedList);
                }
            } catch (Exception e) {
                logger.error("转换 MetaRelation 出错", e);
            }
        }
        List<Map<String, Object>> detailList = mapJsonObject.get(AppTableNames.F_MD_REL_DETAIL.name());
        if (detailList != null) {
            try {
                List<Object> convertedList = convertMap(MetaRelDetail.class, detailList);
                if (convertedList != null) {
                    metaObject.addAll(convertedList);
                }
            } catch (Exception e) {
                logger.error("转换 MetaRelDetail 出错", e);
            }
        }
        return this;
    }


    private JsonAppVo createMetaFormObject() {
        // 防御 mapJsonObject 为 null
        if (mapJsonObject == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.M_META_FORM_MODEL.name());
        // 防御 list 为 null
        if (list == null) {
            return this;
        }
        // 防御 appList 未初始化
        if (appList == null) {
            appList = new ArrayList<>();
        }
        try {
            List<Object> convertedList = convertMap(MetaFormModel.class, list);
            if (convertedList != null) {
                appList.addAll(convertedList);
            }
        } catch (Exception e) {
            logger.error("转换 MetaFormModel 出错", e);
        }
        try {
            List<Object> convertedList = convertMap(MetaFormModelDraft.class, list);
            if (convertedList != null) {
                appList.addAll(convertedList);
            }
        } catch (Exception e) {
            logger.error("转换 MetaFormModelDraft 出错", e);
        }
        return this;
    }

    private JsonAppVo createDataPacketAndParamsObject() {
        // 防御 mapJsonObject 为 null
        if (mapJsonObject == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.Q_DATA_PACKET.name());
        // 防御 list 为 null
        if (list == null) {
            return this;
        }
        // 防御 appList 未初始化
        if (appList == null) {
            appList = new ArrayList<>();
        }
        try {
            List<Object> convertedList = convertMap(DataPacket.class, list);
            if (convertedList != null) {
                appList.addAll(convertedList);
            }
        } catch (Exception e) {
            logger.error("转换 DataPacket 出错", e);
        }
        try {
            List<Object> convertedList = convertMap(DataPacketDraft.class, list);
            if (convertedList != null) {
                appList.addAll(convertedList);
            }
        } catch (Exception e) {
            logger.error("转换 DataPacketDraft 出错", e);
        }
        list = mapJsonObject.get(AppTableNames.Q_DATA_PACKET_PARAM.name());
        if (list == null) {
            return this;
        }
        try {
            List<Object> convertedList = convertMap(DataPacketParam.class, list);
            if (convertedList != null) {
                appList.addAll(convertedList);
            }
        } catch (Exception e) {
            logger.error("转换 DataPacketParam 出错", e);
        }
        try {
            List<Object> convertedList = convertMap(DataPacketParamDraft.class, list);
            if (convertedList != null) {
                appList.addAll(convertedList);
            }
        } catch (Exception e) {
            logger.error("转换 DataPacketParamDraft 出错", e);
        }
        return this;
    }

    private JsonAppVo createWfOptTeamRole() {
        // 防御 mapJsonObject 为 null
        if (mapJsonObject == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.WF_OPT_TEAM_ROLE.name());
        // 防御 list 为 null
        if (list == null) {
            return this;
        }
        // 防御 appList 未初始化
        if (appList == null) {
            appList = new ArrayList<>();
        }
        try {
            List<Object> convertedList = convertMap(OptTeamRole.class, list);
            if (convertedList != null) {
                appList.addAll(convertedList);
            }
        } catch (Exception e) {
            logger.error("转换 OptTeamRole 出错", e);
        }
        return this;
    }

    private JsonAppVo createWfOptVariable() {
        // 防御 mapJsonObject 为 null
        if (mapJsonObject == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.WF_OPT_VARIABLE_DEFINE.name());
        // 防御 list 为 null
        if (list == null) {
            return this;
        }
        // 防御 appList 未初始化
        if (appList == null) {
            appList = new ArrayList<>();
        }
        try {
            List<Object> convertedList = convertMap(OptVariableDefine.class, list);
            if (convertedList != null) {
                appList.addAll(convertedList);
            }
        } catch (Exception e) {
            logger.error("转换 OptVariableDefine 出错", e);
        }
        return this;
    }

    private JsonAppVo createOptInfo() {
        // 防御 mapJsonObject 为 null
        if (mapJsonObject == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_OPTINFO.name());
        // 防御 list 为 null
        if (list == null) {
            return this;
        }
        // 防御 appList 未初始化
        if (appList == null) {
            appList = new ArrayList<>();
        }
        try {
            List<Object> convertedList = convertMap(OptInfo.class, list);
            if (convertedList != null) {
                appList.addAll(convertedList);
            }
        } catch (Exception e) {
            logger.error("转换 OptInfo 出错", e);
        }
        return this;
    }

    private JsonAppVo createOptDef() {
        // 防御 mapJsonObject 为 null
        if (mapJsonObject == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_OPTDEF.name());
        // 防御 list 为 null
        if (list == null) {
            return this;
        }
        // 防御 appList 未初始化
        if (appList == null) {
            appList = new ArrayList<>();
        }
        try {
            List<Object> convertedList = convertMap(OptMethod.class, list);
            if (convertedList != null) {
                appList.addAll(convertedList);
            }
        } catch (Exception e) {
            logger.error("转换 OptMethod 出错", e);
        }
        return this;
    }

    private JsonAppVo createWfDefine() {
        // 防御 mapJsonObject 为 null
        if (mapJsonObject == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.WF_FLOW_DEFINE.name());
        // 防御 list 为 null
        if (list == null) {
            return this;
        }
        // 防御 appList 未初始化
        if (appList == null) {
            appList = new ArrayList<>();
        }
        try {
            List<Object> convertedList = convertMap(FlowInfo.class, list);
            if (convertedList != null) {
                appList.addAll(convertedList);
            }
        } catch (Exception e) {
            logger.error("转换 FlowInfo 出错", e);
        }
        return this;
    }

    private JsonAppVo createWfNode() {
        // 防御 mapJsonObject 为 null
        if (mapJsonObject == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.WF_NODE.name());
        // 防御 list 为 null
        if (list == null) {
            return this;
        }
        // 防御 appList 未初始化
        if (appList == null) {
            appList = new ArrayList<>();
        }
        try {
            List<Object> convertedList = convertMap(NodeInfo.class, list);
            if (convertedList != null) {
                appList.addAll(convertedList);
            }
        } catch (Exception e) {
            logger.error("转换 NodeInfo 出错", e);
        }
        return this;
    }

    private JsonAppVo createWfTransition() {
        // 防御 mapJsonObject 为 null
        if (mapJsonObject == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.WF_TRANSITION.name());
        // 防御 list 为 null
        if (list == null) {
            return this;
        }
        // 防御 appList 未初始化
        if (appList == null) {
            appList = new ArrayList<>();
        }
        try {
            List<Object> convertedList = convertMap(FlowTransition.class, list);
            if (convertedList != null) {
                appList.addAll(convertedList);
            }
        } catch (Exception e) {
            logger.error("转换 FlowTransition 出错", e);
        }
        return this;
    }

    private JsonAppVo createTableRelation() {
        // 防御 mapJsonObject 为 null
        if (mapJsonObject == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_TABLE_OPT_RELATION.name());
        // 防御 list 为 null
        if (list == null) {
            return this;
        }
        // 防御 appList 未初始化
        if (appList == null) {
            appList = new ArrayList<>();
        }
        try {
            List<Object> convertedList = convertMap(MetaOptRelation.class, list);
            if (convertedList != null) {
                appList.addAll(convertedList);
            }
        } catch (Exception e) {
            logger.error("转换 MetaOptRelation 出错", e);
        }
        return this;
    }

    private List<Object> convertMap(Class<?> type, List<Map<String, Object>> list) {
        List<Object> object = new ArrayList<>();
        try {
            JavaBeanMetaData javaBeanMetaData = JavaBeanMetaData.createBeanMetaDataFromType(type);
            for (Map<String, Object> map : list) {
                if (map == null || map.isEmpty()) {
                    continue;
                }
                object.add(javaBeanMetaData.createBeanObjectFromMap(map));
            }
            return object;
        } catch (Exception e) {
            return object;
        }
    }

}

