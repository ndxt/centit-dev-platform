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
import com.centit.fileserver.po.FileFolderInfo;
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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * JsonAppVo 优化版本
 * <p>
 * 主要优化点：
 * 1. 拆分职责 - 使用策略模式分离不同类型的更新逻辑
 * 2. 提取工具方法 - 消除重复代码
 * 3. 优化性能 - 缓存正则表达式，减少遍历次数
 * 4. 增强可读性 - 改进命名和注释
 * 5. 统一异常处理 - 使用 Optional 和防御性编程
 *
 * @author zhf
 * @since 2026-01-05
 */
public class JsonAppVo {

    protected static final Logger logger = LoggerFactory.getLogger(JsonAppVo.class);

    // ==================== 常量定义 ====================

    private static final String OS_ID = "osId";
    private static final String PAGE_FLOW = "pageFlow";
    private static final String DATABASE_CODE = "databaseCode";
    private static final String MAP_DATA_CODE = "mapDataCode";
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
    private static final String TOP_OPT_ID = "topOptId";
    private static final String PRE_OPT_ID = "preOptId";
    private static final String UPDATE_DATE = "updateDate";
    private static final String LAST_MODIFY_DATE = "lastModifyDate";
    private static final String RECORD_DATE = "recordDate";
    private static final String PUBLISH_DATE = "publishDate";
    private static final String MODIFY_TIME = "modifyTime";
    private static final String FLOW_PUBLISH_DATE = "flowPublishDate";
    private static final String RECORDER = "recorder";
    private static final String UPDATOR = "updator";
    private static final String CREATOR = "creator";
    private static final String CREATE_DATE = "createDate";
    private static final String TABLE_TYPE = "T";
    private static final String TABLE_NAME = "tableName";
    private static final String SOURCE_ID = "sourceId";
    private static final String DOC_ID = "docId";
    private static final String ROLE_CODE = "roleCode";
    private static final String VARIABLE_NAME = "variableName";
    private static final String FORM_CODE = "formCode";
    private static final String MOBILE_FORM_TEMPLATE = "mobileFormTemplate";
    private static final String STRUCTURE_FUNCTION = "structureFunction";
    private static final String DICTIONARY_ID = "dictionaryId";
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
    private static final String LOG_LEVEL = "logLevel";
    private static final String TASK_CRON = "taskCron";
    private static final String FOLDER_ID = "folderId";
    private static final String PARENT_FOLDER = "parentFolder";
    private static final String LIBRARY_ID = "libraryId";
    private static final String FILE_SHOW_PATH = "fileShowPath";
    private static final String FILE_NAME = "fileName";
    private static final String FILE_CATALOG = "fileCatalog";

    // ==================== 实例字段 ====================

    @Getter
    @Setter
    private JSONObject oldAppObject;

    @Getter
    private final Map<String, List<Map<String, Object>>> mapJsonObject = new HashMap<>();

    private final String zipFilePath;
    private final String appHome;
    private final FileInfoOpt fileInfoOpt;

    @Getter
    private String userCode;
    private final String topUnit;
    private final String osId;
    private final Date now;

    // 应用数据列表
    @Getter
    private List<Object> appList = new ArrayList<>();
    @Getter
    private List<Object> metaformList = new ArrayList<>();
    @Getter
    private List<Object> metaformDraftList = new ArrayList<>();
    @Getter
    private List<Object> packetList = new ArrayList<>();
    @Getter
    private List<Object> packetDraftList = new ArrayList<>();
    @Getter
    private List<Object> workFlowList = new ArrayList<>();
    @Getter
    private List<Object> wfNodeList = new ArrayList<>();
    @Getter
    private List<Object> wfTransitionList = new ArrayList<>();
    @Getter
    private List<Object> metaObject = new ArrayList<>();
    @Getter
    private List<Object> pendingMetaObject = new ArrayList<>();

    @Getter
    @Setter
    private boolean uploadFiles;
    @Getter
    @Setter
    private boolean runMetaData;
    @Getter
    @Setter
    private boolean runDictionary;

    // 差异映射 - 使用 ConcurrentHashMap 提高并发性能
    private final Map<String, String> databaseDiffMap = new ConcurrentHashMap<>();
    private final Map<String, String> mdTableDiffMap = new ConcurrentHashMap<>();
    private final Map<String, String> relationDiffMap = new ConcurrentHashMap<>();
    private final Map<String, String> dataPacketDiffMap = new ConcurrentHashMap<>();
    private final Map<String, String> metaFormDiffMap = new ConcurrentHashMap<>();
    private final Map<String, String> flowDefineDiffMap = new ConcurrentHashMap<>();
    private final Map<String, String> optInfoDiffMap = new ConcurrentHashMap<>();
    private final Map<String, String> wfNodeDiffMap = new ConcurrentHashMap<>();
    private final Map<String, String> dictionaryDiffMap = new ConcurrentHashMap<>();
    private final Map<String, String> fileDiffMap = new ConcurrentHashMap<>();
    private final Map<String, String> folderDiffMap = new ConcurrentHashMap<>();

    // 正则表达式缓存 - 性能优化
    private static final Map<String, Pattern> PATTERN_CACHE = new ConcurrentHashMap<>();

    // ==================== 构造函数 ====================

    public JsonAppVo(JSONObject jsonObject, JSONObject oldObject, String osId,
                     CentitUserDetails userDetails, String appHome,
                     FileInfoOpt fileInfoOpt, String zipFilePath) {
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

    // ==================== 公共API ====================

    /**
     * 获取不同类型的主键变化信息
     */
    public JSONArray getDiffIds() {
        JSONArray jsonArray = new JSONArray();
        addDiffEntry(jsonArray, "接口主键变化", dataPacketDiffMap);
        addDiffEntry(jsonArray, "页面主键变化", metaFormDiffMap);
        addDiffEntry(jsonArray, "流程主键变化", flowDefineDiffMap);
        addDiffEntry(jsonArray, "数据字典主键变化", dictionaryDiffMap);
        addDiffEntry(jsonArray, "菜单主键变化", optInfoDiffMap);
        addDiffEntry(jsonArray, "表主键变化", mdTableDiffMap);
        addDiffEntry(jsonArray, "文件主键变化", fileDiffMap);
        return jsonArray;
    }

    /**
     * 准备应用程序的启动环境
     */
    public void prepareApp() throws ObjectException {
        try {
            logger.info("开始更新主配置...");
            updatePrimary();
            logger.info("主配置更新完成");

            logger.info("开始创建应用程序对象...");
            createAppObject();
            logger.info("应用程序对象创建完成");
        } catch (Exception e) {
            logger.error("应用程序初始化失败", e);
            throw new ObjectException("Prepare app failed", e);
        }
    }

    /**
     * 刷新缓存中的应用列表
     */
    public void refreshCache(DdeDubboTaskRun ddeDubboTaskRun) {
        if (CollectionUtils.isEmpty(packetList)) {
            return;
        }
        packetList.stream()
            .filter(obj -> obj instanceof DataPacket)
            .map(obj -> ((DataPacket) obj).getPacketId())
            .filter(Objects::nonNull)
            .forEach(ddeDubboTaskRun::refreshCache);
    }

    /**
     * 获取发布数据库编码列表
     */
    public List<String> getPublishDatabaseCode() {
        List<SourceInfo> oldList = convertJavaList(SourceInfo.class, AppTableNames.F_DATABASE_INFO.name());
        if (CollectionUtils.isEmpty(oldList)) {
            return new ArrayList<>();
        }
        return oldList.stream()
            .filter(info -> "D".equals(info.getSourceType()))
            .map(SourceInfo::getDatabaseCode)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    // ==================== 核心更新逻辑 ====================

    /**
     * 更新主配置（按顺序执行所有更新方法）
     */
    public void updatePrimary() {
        executeUpdateStep("应用信息", this::updateOsInfo);
        executeUpdateStep("数据库配置", this::setDatabaseDiffMap);
        executeUpdateStep("数据字典", this::updateDataCatalog);
        executeUpdateStep("字典明细", this::updateDataDictionaryUseCatalog);
        executeUpdateStep("应用字典", this::updateApplicationDictionary);
        executeUpdateStep("表配置", this::updateMdTable);
        executeUpdateStep("字段配置", this::updateMdColumn);
        executeUpdateStep("表关系", this::updateMdRelation);
        executeUpdateStep("表关系明细", this::updateRelationDetail);
        executeUpdateStep("菜单", this::updateOptInfo);
        executeUpdateStep("操作定义", this::updateOptDef);
        executeUpdateStep("应用与表关联", this::updateTableRelation);
        executeUpdateStep("文件夹主键", this::updateFoldInfo);
        executeUpdateStep("文件里文件夹", this::updateFileInfoUseDiffMap);
        executeUpdateStep("文件", this::uploadFiles);
        executeUpdateStep("接口", this::updatePacket);
        executeUpdateStep("用接口更新操作定义", this::updateOptDefUsePacket);
        executeUpdateStep("接口参数", this::updatePacketParams);
        executeUpdateStep("页面", this::updateMetaForm);
        executeUpdateStep("用页面更新菜单", this::updateOptInfoUseMetaForm);
        executeUpdateStep("工作流团队角色", this::updateWfOptTeamRole);
        executeUpdateStep("工作流变量", this::updateWfOptVariable);
        executeUpdateStep("流程", this::updateWfDefine);
        executeUpdateStep("流程节点", this::updateWfNode);
        executeUpdateStep("流程定义更新", this::updateWfDefineUseDiffMap);
        executeUpdateStep("流程转换", this::updateWfTransition);
        executeUpdateStep("接口主键更新", this::updatePacketUseDiffMap);
        executeUpdateStep("页面主键更新", this::updateMetaFormUseDiffMap);
    }

    /**
     * 创建应用程序对象
     */
    public void createAppObject() {
        // 清空列表
        clearAllLists();

        // 链式创建
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
            .createTableRelation()
            .createFileFolder();
    }

    // ==================== 内部实现 - 更新逻辑 ====================

    private void updateOsInfo() {
        List<Map<String, Object>> list = getList(AppTableNames.F_OS_INFO.name());
        if (list == null) return;

        list.forEach(map -> {
            Object pageFlow = map.get(PAGE_FLOW);
            map.clear();
            map.put(OS_ID, osId);
            map.put(PAGE_FLOW, pageFlow);
        });
    }

    private void setDatabaseDiffMap() {
        List<Map<String, Object>> list = getList(AppTableNames.F_DATABASE_INFO.name());
        if (list == null) return;

        for (Map<String, Object> map : list) {
            String mapDataCode = StringBaseOpt.objectToString(map.get(MAP_DATA_CODE));
            String databaseCode = StringBaseOpt.objectToString(map.get(DATABASE_CODE));

            if (StringBaseOpt.isNvl(mapDataCode)) {
                throw new ObjectException(ObjectException.DATA_NOT_INTEGRATED,
                    map.get("databaseName") + ":没有指定资源");
            }

            if (!mapDataCode.equals(databaseCode)) {
                databaseDiffMap.put(databaseCode, mapDataCode);
                map.put(DATABASE_CODE, mapDataCode);
            }
        }
    }

    private void updateDataCatalog() {
        List<Map<String, Object>> list = getList(AppTableNames.F_DATACATALOG.name());
        if (list == null) return;

        List<DataCatalog> oldList = convertJavaList(DataCatalog.class, AppTableNames.F_DATACATALOG.name());
        Map<String, DataCatalog> catalogCodeMap = new HashMap<>();
        Map<String, DataCatalog> catalogSourceMap = new HashMap<>();

        if (!CollectionUtils.isEmpty(oldList)) {
            for (DataCatalog catalog : oldList) {
                String sourceId = catalog.getSourceId();
                String catalogCode = catalog.getCatalogCode();
                String catalogTopUnit = catalog.getTopUnit();

                String sourceKey = StringBaseOpt.isNvl(sourceId)
                    ? catalogCode + ":" + catalogTopUnit
                    : sourceId + ":" + catalogTopUnit;

                catalogCodeMap.put(catalogCode, catalog);
                catalogSourceMap.put(sourceKey, catalog);
            }
        }

        for (Map<String, Object> map : list) {
            final String catalogCode = StringBaseOpt.objectToString(map.get(CATALOG_CODE));
            final String sourceId = StringBaseOpt.objectToString(map.get(SOURCE_ID));

            String uuid = determineUuid(catalogCode, sourceId, catalogCodeMap, catalogSourceMap, dictionaryDiffMap);

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

    private void updateDataDictionaryUseCatalog() {
        if (dictionaryDiffMap.isEmpty()) return;

        List<Map<String, Object>> list = getList(AppTableNames.F_DATADICTIONARY.name());
        if (list == null) return;

        list.forEach(map -> updateIfMapped(map, CATALOG_CODE, dictionaryDiffMap));
    }

    private void updateApplicationDictionary() {
        List<Map<String, Object>> list = getList(AppTableNames.M_APPLICATION_DICTIONARY.name());
        if (list == null) return;

        List<ApplicationDictionary> oldList = convertJavaList(ApplicationDictionary.class,
            AppTableNames.M_APPLICATION_DICTIONARY.name());
        Map<String, ApplicationDictionary> oldDictMap = new HashMap<>();

        if (!CollectionUtils.isEmpty(oldList)) {
            for (ApplicationDictionary dict : oldList) {
                String key = dict.getDictionaryId() + ":" + dict.getOsId();
                oldDictMap.put(key, dict);
            }
        }

        for (Map<String, Object> map : list) {
            String dictionaryId = StringBaseOpt.objectToString(map.get(DICTIONARY_ID));
            String id = StringBaseOpt.objectToString(map.get(ID));

            // 更新字典ID
            String newDictId = dictionaryDiffMap.get(dictionaryId);
            if (!StringBaseOpt.isNvl(newDictId)) {
                map.put(DICTIONARY_ID, newDictId);
                dictionaryId = newDictId;
            }

            // 获取UUID
            String uuid = "";
            ApplicationDictionary appDict = oldDictMap.get(dictionaryId + ":" + osId);
            if (appDict != null) {
                uuid = appDict.getId();
            } else if (!CollectionUtils.isEmpty(oldList)) {
                uuid = UuidOpt.getUuidAsString();
            } else {
                uuid = id;
            }

            map.put(ID, uuid);
            map.put(OS_ID, osId);
            map.put(PUSH_USER, userCode);
            map.put(PUSH_TIME, now);
        }
    }

    private void updateMdTable() {
        List<Map<String, Object>> list = getList(AppTableNames.F_MD_TABLE.name());
        if (list == null) return;

        List<MetaTable> oldList = convertJavaList(MetaTable.class, AppTableNames.F_MD_TABLE.name());
        Map<String, MetaTable> oldMetaTableMap = new HashMap<>();

        if (!CollectionUtils.isEmpty(oldList)) {
            for (MetaTable metaTable : oldList) {
                String key = StringUtils.lowerCase(metaTable.getTableName()) + ":" + metaTable.getDatabaseCode();
                oldMetaTableMap.put(key, metaTable);
            }
        }

        for (Map<String, Object> map : list) {
            String tableName = StringUtils.lowerCase(StringBaseOpt.objectToString(map.get(TABLE_NAME)));
            String databaseCode = StringBaseOpt.objectToString(map.get(DATABASE_CODE));

            // 替换数据库编码
            String newDbCode = databaseDiffMap.get(databaseCode);
            if (newDbCode != null) {
                map.put(DATABASE_CODE, newDbCode);
                databaseCode = newDbCode;
            }

            String key = tableName + ":" + databaseCode;
            MetaTable metaTable = oldMetaTableMap.get(key);
            String uuid = metaTable != null ? metaTable.getTableId() : UuidOpt.getUuidAsString();

            String oldTableId = StringBaseOpt.objectToString(map.get(TABLE_ID));
            if (!uuid.equals(oldTableId)) {
                mdTableDiffMap.put(oldTableId, uuid);
                map.put(TABLE_ID, uuid);
                map.put(RECORDER, userCode);
                map.put(RECORD_DATE, now);
            }
        }
    }

    private void updateMdColumn() {
        List<Map<String, Object>> list = getList(AppTableNames.F_MD_COLUMN.name());
        if (list == null) return;

        list.forEach(map -> {
            map.put(RECORDER, userCode);
            map.put(LAST_MODIFY_DATE, now);
            updateIfMapped(map, TABLE_ID, mdTableDiffMap);
            updateIfMapped(map, REFERENCE_DATA, dictionaryDiffMap);
        });
    }

    private void updateMdRelation() {
        List<Map<String, Object>> list = getList(AppTableNames.F_MD_RELATION.name());
        if (list == null) return;

        List<MetaRelation> oldList = convertJavaList(MetaRelation.class, AppTableNames.F_MD_RELATION.name());

        for (Map<String, Object> map : list) {
            String parentTableId = StringBaseOpt.objectToString(map.get(PARENT_TABLE_ID));
            String childTableId = StringBaseOpt.objectToString(map.get(CHILD_TABLE_ID));
            String relationId = StringBaseOpt.objectToString(map.get(RELATION_ID));
            // 更新表ID
            updateIfMapped(map, PARENT_TABLE_ID, mdTableDiffMap);
            updateIfMapped(map, CHILD_TABLE_ID, mdTableDiffMap);
            // 获取UUID
            String uuid = "";
            if (!CollectionUtils.isEmpty(oldList)) {
                uuid = oldList.stream()
                    .filter(old -> parentTableId.equals(old.getParentTableId())
                        && childTableId.equals(old.getChildTableId()))
                    .findFirst()
                    .map(MetaRelation::getRelationId)
                    .orElseGet(UuidOpt::getUuidAsString);
            } else {
                uuid = relationId;
            }

            if (!uuid.equals(relationId)) {
                relationDiffMap.put(relationId, uuid);
            }

            map.put(RELATION_ID, uuid);
            map.put(RECORDER, userCode);
            map.put(LAST_MODIFY_DATE, now);
        }
    }

    private void updateRelationDetail() {
        List<Map<String, Object>> list = getList(AppTableNames.F_MD_REL_DETAIL.name());
        if (list == null) return;

        list.forEach(map -> updateIfMapped(map, RELATION_ID, relationDiffMap));
    }

    private void updateOptInfo() {
        List<Map<String, Object>> list = getList(AppTableNames.F_OPTINFO.name());
        if (list == null) return;

        List<OptInfo> oldList = convertJavaList(OptInfo.class, AppTableNames.F_OPTINFO.name());
        Map<String, String> commonOptMap = new HashMap<>();
        Map<String, OptInfo> optIdMap = new HashMap<>();
        Map<String, String> sourceIdMap = new HashMap<>();

        if (!CollectionUtils.isEmpty(oldList)) {
            for (OptInfo oldMap : oldList) {
                String formCode = oldMap.getFormCode();
                String oldTopOptId = oldMap.getTopOptId();
                String optId = oldMap.getOptId();
                String preOptId = oldMap.getPreOptId();
                String sourceId = oldMap.getSourceId();

                boolean isCommonOrPageEnter = OptInfo.OPT_INFO_FORM_CODE_COMMON.equals(formCode)
                    || OptInfo.OPT_INFO_FORM_CODE_PAGE_ENTER.equals(formCode);

                if (isCommonOrPageEnter && osId.equals(oldTopOptId)) {
                    commonOptMap.put(formCode, optId);
                }

                if ((optId.equals(oldTopOptId) && "0".equals(preOptId)) || isCommonOrPageEnter) {
                    continue;
                }

                optIdMap.put(optId, oldMap);
                String key = StringBaseOpt.isNvl(sourceId)
                    ? optId + ":" + oldTopOptId
                    : sourceId + ":" + oldTopOptId;
                sourceIdMap.put(key, optId);
            }
        }

        // 第一遍：处理顶级菜单和公共操作
        for (Map<String, Object> map : list) {
            String optId = StringBaseOpt.objectToString(map.get(OPT_ID));
            String sourceId = StringBaseOpt.objectToString(map.get(SOURCE_ID));
            String topOptId = StringBaseOpt.objectToString(map.get(TOP_OPT_ID));
            String formCode = StringBaseOpt.objectToString(map.get(FORM_CODE));
            String preOptId = StringBaseOpt.objectToString(map.get(PRE_OPT_ID));

            // 顶级菜单
            if (optId.equals(topOptId) && "0".equals(preOptId)) {
                map.clear();
                if (!topOptId.equals(osId)) {
                    optInfoDiffMap.put(optId, osId);
                }
                continue;
            }

            // 公共操作和页面入口
            boolean isCommonOrPageEnter = OptInfo.OPT_INFO_FORM_CODE_COMMON.equals(formCode)
                || OptInfo.OPT_INFO_FORM_CODE_PAGE_ENTER.equals(formCode);
            if (isCommonOrPageEnter) {
                map.remove(PRE_OPT_ID);
                map.remove(FORM_CODE);
                map.remove(SOURCE_ID);
                map.put(TOP_OPT_ID, osId);
                map.put(OS_ID, osId);
                String oldOptId = commonOptMap.get(formCode);
                if (!optId.equals(oldOptId)) {
                    optInfoDiffMap.put(optId, oldOptId);
                }
                map.put(OPT_ID, oldOptId);
                continue;
            }

            // 生成UUID
            String uuid = generateUuid(optId, sourceId, osId, oldList, optIdMap, sourceIdMap);

            if (!uuid.equals(optId)) {
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

        // 第二遍：更新前置操作ID
        list.forEach(map -> updateIfMapped(map, PRE_OPT_ID, optInfoDiffMap));
    }

    private void updateOptDef() {
        List<Map<String, Object>> list = getList(AppTableNames.F_OPTDEF.name());
        if (list == null) return;

        List<OptMethod> oldList = convertJavaList(OptMethod.class, AppTableNames.F_OPTDEF.name());
        Map<String, OptMethod> optCodeMap = new HashMap<>();
        Map<String, String> sourceIdMap = new HashMap<>();

        if (!CollectionUtils.isEmpty(oldList)) {
            for (OptMethod oldMap : oldList) {
                String optCode = oldMap.getOptCode();
                String sourceId = oldMap.getSourceId();
                String topOptId = oldMap.getTopOptId();

                optCodeMap.put(optCode, oldMap);
                String key = StringBaseOpt.isNvl(sourceId)
                    ? optCode + ":" + topOptId
                    : sourceId + ":" + topOptId;
                sourceIdMap.put(key, optCode);
            }
        }

        for (Map<String, Object> map : list) {
            String optCode = StringBaseOpt.objectToString(map.get(OPT_CODE));
            String sourceId = StringBaseOpt.objectToString(map.get(SOURCE_ID));
            String uuid = "";

            if (!CollectionUtils.isEmpty(oldList)) {
                if (!StringBaseOpt.isNvl(sourceId)) {
                    String sourceKey = sourceId + ":" + osId;
                    String oldOptMethod = sourceIdMap.get(sourceKey);
                    if (!StringBaseOpt.isNvl(oldOptMethod)) {
                        uuid = oldOptMethod;
                        map.put(API_ID, uuid);
                        map.put(OPT_URL, DDE_RUN + uuid);
                    }
                }

                if (StringBaseOpt.isNvl(uuid)) {
                    OptMethod matchOptMethod = optCodeMap.get(optCode);
                    if (matchOptMethod != null) {
                        String matchTopOptId = matchOptMethod.getTopOptId();
                        if (matchTopOptId != null && matchTopOptId.equals(osId)) {
                            uuid = optCode;
                            map.put(API_ID, uuid);
                            map.put(OPT_URL, DDE_RUN + uuid);
                        } else {
                            uuid = UuidOpt.getUuidAsString();
                            map.remove(OPT_URL);
                        }
                    } else {
                        uuid = optCode;
                    }
                }
            } else {
                uuid = optCode;
            }

            map.put(OPT_CODE, uuid);
            map.put(UPDATOR, userCode);
            map.put(UPDATE_DATE, now);
            map.put(CREATOR, userCode);
            map.put(CREATE_DATE, now);
            updateIfMapped(map, OPT_ID, optInfoDiffMap);

            if (StringBaseOpt.isNvl(sourceId)) {
                map.put(SOURCE_ID, optCode);
            }
        }
    }

    private void updateTableRelation() {
        List<Map<String, Object>> list = getList(AppTableNames.F_TABLE_OPT_RELATION.name());
        if (list == null) return;

        List<MetaOptRelation> oldList = convertJavaList(MetaOptRelation.class,
            AppTableNames.F_TABLE_OPT_RELATION.name());
        Map<String, String> idMap = new HashMap<>();
        Map<String, String> sourceIdMap = new HashMap<>();

        if (!CollectionUtils.isEmpty(oldList)) {
            for (MetaOptRelation oldMap : oldList) {
                idMap.put(oldMap.getId(), oldMap.getOsId());
                if (!StringBaseOpt.isNvl(oldMap.getTableId()) && !StringBaseOpt.isNvl(oldMap.getOptId())) {
                    String sourceKey = oldMap.getTableId() + ":" + oldMap.getOptId();
                    sourceIdMap.put(sourceKey, oldMap.getId());
                }
            }
        }

        for (Map<String, Object> map : list) {
            String tableId = StringBaseOpt.objectToString(map.get(TABLE_ID));
            String optId = StringBaseOpt.objectToString(map.get(OPT_ID));
            String id = StringBaseOpt.objectToString(map.get(ID));

            updateIfMapped(map, TABLE_ID, mdTableDiffMap);
            updateIfMapped(map, OPT_ID, optInfoDiffMap);

            tableId = StringBaseOpt.objectToString(map.get(TABLE_ID));
            optId = StringBaseOpt.objectToString(map.get(OPT_ID));

            String uuid = "";
            if (!CollectionUtils.isEmpty(oldList)) {
                String sourceKey = tableId + ":" + optId;
                String oldId = sourceIdMap.get(sourceKey);
                if (!StringBaseOpt.isNvl(oldId)) {
                    uuid = oldId;
                } else {
                    String matchOsId = idMap.get(id);
                    if (matchOsId != null) {
                        uuid = matchOsId.equals(osId) ? id : UuidOpt.getUuidAsString();
                    } else {
                        uuid = id;
                    }
                }
            } else {
                uuid = id;
            }

            map.put(ID, uuid);
            map.put(OS_ID, osId);
        }
    }

    private void updateFoldInfo() {
        if (!uploadFiles || StringUtils.isBlank(zipFilePath)) return;

        List<Map<String, Object>> list = getList(AppTableNames.FILE_FOLDER_INFO.name());
        if (list == null) return;

        List<FileFolderInfo> oldList = convertJavaList(FileFolderInfo.class,
            AppTableNames.FILE_FOLDER_INFO.name());
        Map<String, FileFolderInfo> sourceIdMap = new HashMap<>();

        if (!CollectionUtils.isEmpty(oldList)) {
            for (FileFolderInfo oldFolder : oldList) {
                String folderId = oldFolder.getFolderId();
                String sourceId = oldFolder.getSourceId();
                String libraryId = oldFolder.getLibraryId();
                String key = StringBaseOpt.isNvl(sourceId)
                    ? folderId + ":" + libraryId
                    : sourceId + ":" + libraryId;
                sourceIdMap.put(key, oldFolder);
            }
        }

        for (Map<String, Object> map : list) {
            String folderId = StringBaseOpt.objectToString(map.get(FOLDER_ID));
            String sourceId = StringBaseOpt.objectToString(map.get(SOURCE_ID));
            String parentId = StringBaseOpt.objectToString(map.get(PARENT_FOLDER));

            // 更新父文件夹
            String newParentId = folderDiffMap.get(parentId);
            if (newParentId != null) {
                map.put(PARENT_FOLDER, newParentId);
                parentId = newParentId;
            }

            // 获取UUID
            String uuid = "";
            if (!CollectionUtils.isEmpty(oldList)) {
                String sourceKey = StringBaseOpt.isNvl(sourceId)
                    ? folderId + ":" + osId
                    : sourceId + ":" + osId;
                FileFolderInfo oldFolder = sourceIdMap.get(sourceKey);
                uuid = oldFolder != null ? oldFolder.getFolderId() : UuidOpt.getUuidAsString();
            } else {
                uuid = folderId;
            }

            if (!uuid.equals(folderId)) {
                folderDiffMap.put(folderId, uuid);
            }

            map.put(FOLDER_ID, uuid);
            map.put(LIBRARY_ID, osId);

            if (StringBaseOpt.isNvl(sourceId)) {
                map.put(SOURCE_ID, folderId);
            }
        }

        // 更新模板字段
        for (Map<String, Object> map : list) {
            replaceTemplateFields(map, PARENT_FOLDER, folderDiffMap);
            replaceTemplateFields(map, "folderPath", folderDiffMap);
        }
    }

    private void updateFileInfoUseDiffMap() {
        if (!uploadFiles || StringUtils.isBlank(zipFilePath)) return;

        List<Map<String, Object>> list = getList(AppTableNames.FILE_INFO.name());
        if (list == null) return;

        list.forEach(map -> {
            updateIfMapped(map, PARENT_FOLDER, folderDiffMap);
            replaceTemplateFields(map, FILE_SHOW_PATH, folderDiffMap);
            map.put(LIBRARY_ID, osId);
        });
    }

    private void uploadFiles() {
        if (!uploadFiles || StringUtils.isBlank(zipFilePath)) return;

        List<File> zipArchives = FileSystemOpt.findFiles(zipFilePath, "file.zip");
        if (CollectionUtils.isEmpty(zipArchives)) {
            logger.warn("在路径 {} 未找到 file.zip", zipFilePath);
            return;
        }

        String timestamp = DatetimeOpt.convertDateToString(DatetimeOpt.currentUtilDate(), "YYYYMMddHHmmss");
        String tempDirPath = Paths.get(appHome, "u" + timestamp).toString();

        try {
            Files.createDirectories(Paths.get(tempDirPath));
            ZipCompressor.release(zipArchives.get(0), tempDirPath);

            List<File> extractedFiles = FileSystemOpt.findFiles(tempDirPath, "*");
            if (!CollectionUtils.isEmpty(extractedFiles)) {
                extractedFiles.forEach(this::processAndUploadFile);
            }
        } catch (IOException e) {
            logger.error("文件处理失败", e);
        } finally {
            try {
                FileSystemOpt.deleteDirect(tempDirPath);
            } catch (Exception e) {
                logger.warn("删除临时目录失败", e);
            }
        }
    }

    private void processAndUploadFile(File file) {
        String fileName = FileSystemOpt.extractFullFileName(file.getPath());
        String oldFileId = extractOldFileIdFromName(fileName);
        if (oldFileId == null) {
            logger.warn("无效的文件名格式，未找到 fileId: {}", fileName);
            return;
        }

        String fileShowPath = "/-1";
        String parentFolder = "-1";

        List<Map<String, Object>> fileInfoList = getList(AppTableNames.FILE_INFO.name());
        if (!CollectionUtils.isEmpty(fileInfoList)) {
            for (Map<String, Object> map : fileInfoList) {
                String fileId = StringBaseOpt.objectToString(map.get("fileId"));
                if (fileId.equals(oldFileId)) {
                    fileShowPath = StringBaseOpt.objectToString(map.get(FILE_SHOW_PATH));
                    parentFolder = StringBaseOpt.objectToString(map.get(PARENT_FOLDER));
                    break;
                }
            }
        }

        FileInfo fileInfo = new FileInfo();
        String cleanFileName = fileName.replaceFirst("\\(" + Pattern.quote(oldFileId) + "\\)", "").trim();
        fileInfo.setFileName(cleanFileName);
        fileInfo.setFileShowPath(fileShowPath);
        fileInfo.setParentFolder(parentFolder);
        fileInfo.setLibraryId(osId);
        fileInfo.setFileCatalog("A");

        try (InputStream inputStream = Files.newInputStream(file.toPath());
             BufferedInputStream bufferedStream = new BufferedInputStream(inputStream, 8192)) {
            String fileId = fileInfoOpt.saveFile(fileInfo, -1, bufferedStream);
            if (fileId != null && !fileId.equals(oldFileId)) {
                fileDiffMap.put(oldFileId, fileId);
            }
        } catch (IOException e) {
            logger.error("读取或保存文件失败: {}", fileName, e);
        }
    }

    private void updatePacket() {
        List<Map<String, Object>> list = getList(AppTableNames.Q_DATA_PACKET.name());
        if (list == null) return;

        List<DataPacket> oldList = convertJavaList(DataPacket.class, AppTableNames.Q_DATA_PACKET.name());
        Map<String, DataPacket> idMap = new HashMap<>();
        Map<String, DataPacket> sourceIdMap = new HashMap<>();

        if (!CollectionUtils.isEmpty(oldList)) {
            for (DataPacket oldMap : oldList) {
                String packetId = oldMap.getPacketId();
                String sourceId = oldMap.getSourceId();
                String oldOsId = oldMap.getOsId();

                idMap.put(packetId, oldMap);
                String key = StringBaseOpt.isNvl(sourceId)
                    ? packetId + ":" + oldOsId
                    : sourceId + ":" + oldOsId;
                sourceIdMap.put(key, oldMap);
            }
        }

        for (Map<String, Object> map : list) {
            String packetId = StringBaseOpt.objectToString(map.get(PACKET_ID));
            String taskType = StringBaseOpt.objectToString(map.get(TASK_TYPE));
            String sourceId = StringBaseOpt.objectToString(map.get(SOURCE_ID));
            String uuid = "";

            if (!CollectionUtils.isEmpty(oldList)) {
                String sourceKey = StringBaseOpt.isNvl(sourceId)
                    ? packetId + ":" + osId
                    : sourceId + ":" + osId;
                DataPacket matchSourcePacket = sourceIdMap.get(sourceKey);

                if (matchSourcePacket != null) {
                    uuid = matchSourcePacket.getPacketId();
                    map.put(LOG_LEVEL, matchSourcePacket.getLogLevel());
                    if (ConstantValue.TASK_TYPE_AGENT.equals(taskType)) {
                        map.put(TASK_CRON, matchSourcePacket.getTaskCron());
                        map.put(IS_VALID, matchSourcePacket.getIsValid());
                    }
                }

                if (StringBaseOpt.isNvl(uuid)) {
                    DataPacket matchPacket = idMap.get(packetId);
                    if (matchPacket != null) {
                        String matchOsId = matchPacket.getOsId();
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
                        uuid = packetId;
                        if (ConstantValue.TASK_TYPE_AGENT.equals(taskType)) {
                            map.put(IS_VALID, false);
                        }
                    }
                }
            }

            if (!uuid.equals(packetId)) {
                dataPacketDiffMap.put(packetId, uuid);
            }

            map.put(PACKET_ID, uuid);
            map.put(OS_ID, osId);
            map.put(UPDATE_DATE, now);
            map.put(PUBLISH_DATE, now);
            map.put(RECORDER, userCode);
            map.put(OPT_CODE, uuid);
            updateIfMapped(map, OPT_ID, optInfoDiffMap);

            if (StringBaseOpt.isNvl(sourceId)) {
                map.put(SOURCE_ID, packetId);
            }
        }
    }

    private void updateOptDefUsePacket() {
        List<Map<String, Object>> list = getList(AppTableNames.F_OPTDEF.name());
        if (list == null) return;

        for (Map<String, Object> map : list) {
            String apiId = StringBaseOpt.objectToString(map.get(API_ID));
            if (!StringBaseOpt.isNvl(apiId)) {
                String changeId = dataPacketDiffMap.get(apiId);
                if (changeId != null) {
                    map.put(API_ID, changeId);
                    map.put(OPT_URL, DDE_RUN + changeId);
                    map.put(OPT_CODE, changeId);
                }
            }
        }

        list.stream()
            .filter(map -> StringBaseOpt.isNvl(StringBaseOpt.objectToString(map.get(OPT_URL))))
            .forEach(Map::clear);
    }

    private void updatePacketParams() {
        List<Map<String, Object>> list = getList(AppTableNames.Q_DATA_PACKET_PARAM.name());
        if (list == null) return;

        list.forEach(map -> updateIfMapped(map, PACKET_ID, dataPacketDiffMap));
    }

    private void updateMetaForm() {
        List<Map<String, Object>> list = getList(AppTableNames.M_META_FORM_MODEL.name());
        if (list == null) return;

        List<MetaFormModel> oldList = convertJavaList(MetaFormModel.class,
            AppTableNames.M_META_FORM_MODEL.name());
        Map<String, MetaFormModel> idMap = new HashMap<>();
        Map<String, MetaFormModel> sourceIdMap = new HashMap<>();

        if (!CollectionUtils.isEmpty(oldList)) {
            for (MetaFormModel oldMap : oldList) {
                String modelId = oldMap.getModelId();
                String sourceId = oldMap.getSourceId();
                String oldOsId = oldMap.getOsId();

                idMap.put(modelId, oldMap);
                String key = StringBaseOpt.isNvl(sourceId)
                    ? modelId + ":" + oldOsId
                    : sourceId + ":" + oldOsId;
                sourceIdMap.put(key, oldMap);
            }
        }

        for (Map<String, Object> map : list) {
            String modelId = StringBaseOpt.objectToString(map.get(MODEL_ID));
            String sourceId = StringBaseOpt.objectToString(map.get(SOURCE_ID));
            String uuid = "";

            if (!CollectionUtils.isEmpty(oldList)) {
                String sourceKey = StringBaseOpt.isNvl(sourceId)
                    ? modelId + ":" + osId
                    : sourceId + ":" + osId;
                MetaFormModel matchSource = sourceIdMap.get(sourceKey);

                if (matchSource != null) {
                    uuid = matchSource.getModelId();
                }

                if (StringBaseOpt.isNvl(uuid)) {
                    MetaFormModel matchId = idMap.get(modelId);
                    if (matchId != null) {
                        String matchOsId = matchId.getOsId();
                        uuid = (matchOsId != null && matchOsId.equals(osId))
                            ? modelId
                            : UuidOpt.getUuidAsString();
                    } else {
                        uuid = modelId;
                    }
                }
            } else {
                uuid = modelId;
            }

            if (!uuid.equals(modelId)) {
                metaFormDiffMap.put(modelId, uuid);
            }

            map.put(MODEL_ID, uuid);
            map.put(OS_ID, osId);
            map.put(PUBLISH_DATE, now);
            map.put(RECORDER, userCode);

            if (StringBaseOpt.isNvl(sourceId)) {
                map.put(SOURCE_ID, modelId);
            }

            updateIfMapped(map, OPT_ID, optInfoDiffMap);
        }
    }

    private void updateOptInfoUseMetaForm() {
        List<Map<String, Object>> list = getList(AppTableNames.F_OPTINFO.name());
        if (list == null) return;

        for (Map<String, Object> map : list) {
            updateIfMapped(map, OPT_ROUTE, metaFormDiffMap);

            String originalOptUrl = StringBaseOpt.objectToString(map.get(OPT_URL));
            if (originalOptUrl == null) continue;

            String prefix = "./page/#/models/";
            if (originalOptUrl.startsWith(prefix)) {
                String modelId = originalOptUrl.substring(prefix.length());
                String changeModelId = metaFormDiffMap.get(modelId);
                if (!StringBaseOpt.isNvl(changeModelId)) {
                    map.put(OPT_URL, prefix + changeModelId);
                }
            }
        }
    }

    private void updateWfOptTeamRole() {
        List<Map<String, Object>> list = getList(AppTableNames.WF_OPT_TEAM_ROLE.name());
        if (list == null) return;

        List<OptTeamRole> oldList = convertJavaList(OptTeamRole.class,
            AppTableNames.WF_OPT_TEAM_ROLE.name());
        Map<String, OptTeamRole> sourceIdMap = new HashMap<>();

        if (!CollectionUtils.isEmpty(oldList)) {
            for (OptTeamRole oldMap : oldList) {
                String key = oldMap.getRoleCode() + ":" + oldMap.getOptId();
                sourceIdMap.put(key, oldMap);
            }
        }

        for (Map<String, Object> map : list) {
            String roleCode = StringBaseOpt.objectToString(map.get(ROLE_CODE));
            String teamRoleId = StringBaseOpt.objectToString(map.get(OPT_TEAM_ROLE_ID));

            updateIfMapped(map, OPT_ID, optInfoDiffMap);
            String optId = StringBaseOpt.objectToString(map.get(OPT_ID));

            String uuid = "";
            if (!CollectionUtils.isEmpty(oldList)) {
                OptTeamRole changeTeamRole = sourceIdMap.get(roleCode + ":" + optId);
                uuid = changeTeamRole != null
                    ? changeTeamRole.getOptTeamRoleId()
                    : UuidOpt.getUuidAsString();
            } else {
                uuid = teamRoleId;
            }

            map.put(OPT_TEAM_ROLE_ID, uuid);
            map.put(MODIFY_TIME, now);
        }
    }

    private void updateWfOptVariable() {
        List<Map<String, Object>> list = getList(AppTableNames.WF_OPT_VARIABLE_DEFINE.name());
        if (list == null) return;

        List<OptVariableDefine> oldList = convertJavaList(OptVariableDefine.class,
            AppTableNames.WF_OPT_VARIABLE_DEFINE.name());
        Map<String, OptVariableDefine> sourceIdMap = new HashMap<>();

        if (!CollectionUtils.isEmpty(oldList)) {
            for (OptVariableDefine oldMap : oldList) {
                String key = oldMap.getVariableName() + ":" + oldMap.getOptId();
                sourceIdMap.put(key, oldMap);
            }
        }

        for (Map<String, Object> map : list) {
            String variableName = StringBaseOpt.objectToString(map.get(VARIABLE_NAME));
            String variableId = StringBaseOpt.objectToString(map.get(OPT_VARIABLE_ID));

            updateIfMapped(map, OPT_ID, optInfoDiffMap);
            String optId = StringBaseOpt.objectToString(map.get(OPT_ID));

            String uuid = "";
            if (!CollectionUtils.isEmpty(oldList)) {
                OptVariableDefine changeVariable = sourceIdMap.get(variableName + ":" + optId);
                uuid = changeVariable != null
                    ? changeVariable.getOptVariableId()
                    : UuidOpt.getUuidAsString();
            } else {
                uuid = variableId;
            }

            map.put(OPT_VARIABLE_ID, uuid);
            map.put(MODIFY_TIME, now);
        }
    }

    private void updateWfDefine() {
        List<Map<String, Object>> list = getList(AppTableNames.WF_FLOW_DEFINE.name());
        if (list == null) return;

        list.sort((o1, o2) -> GeneralAlgorithm.compareTwoObject(o1.get(VERSION), o2.get(VERSION)));

        List<FlowInfo> oldList = convertJavaList(FlowInfo.class, AppTableNames.WF_FLOW_DEFINE.name());
        Map<String, FlowInfo> idMap = new HashMap<>();
        Map<String, FlowInfo> sourceIdMap = new HashMap<>();

        if (!CollectionUtils.isEmpty(oldList)) {
            for (FlowInfo oldMap : oldList) {
                String flowCode = oldMap.getFlowCode();
                String sourceId = oldMap.getSourceId();
                String oldOsId = oldMap.getOsId();

                idMap.put(flowCode, oldMap);
                String key = StringBaseOpt.isNvl(sourceId)
                    ? flowCode + ":" + oldOsId
                    : sourceId + ":" + oldOsId;
                sourceIdMap.put(key + ":" + oldOsId, oldMap);
            }
        }

        for (Map<String, Object> map : list) {
            String flowCode = StringBaseOpt.objectToString(map.get(FLOW_CODE));
            int version = NumberBaseOpt.castObjectToInteger(map.get(VERSION), -1);
            String sourceId = StringBaseOpt.objectToString(map.get(SOURCE_ID));

            if (version == 0) {
                String uuid = "";
                if (!CollectionUtils.isEmpty(oldList)) {
                    String sourceKey = StringBaseOpt.isNvl(sourceId)
                        ? flowCode + ":" + osId
                        : sourceId + ":" + osId;
                    FlowInfo matchSource = sourceIdMap.get(sourceKey);

                    if (matchSource != null) {
                        uuid = matchSource.getFlowCode();
                    }

                    if (StringBaseOpt.isNvl(uuid)) {
                        FlowInfo matchId = idMap.get(flowCode);
                        if (matchId != null) {
                            String matchOsId = matchId.getOsId();
                            uuid = (matchOsId != null && matchOsId.equals(osId))
                                ? flowCode
                                : UuidOpt.getUuidAsString();
                        } else {
                            uuid = flowCode;
                        }
                    }
                } else {
                    uuid = flowCode;
                }

                if (!uuid.equals(flowCode)) {
                    flowDefineDiffMap.put(flowCode, uuid);
                }
            }

            map.put(FLOW_PUBLISH_DATE, now);
            map.put(OS_ID, osId);
        }
    }

    private void updateWfNode() {
        List<Map<String, Object>> list = getList(AppTableNames.WF_NODE.name());
        if (list == null) return;

        List<NodeInfo> oldList = convertJavaList(NodeInfo.class, AppTableNames.WF_NODE.name());
        Map<String, NodeInfo> idMap = new HashMap<>();
        Map<String, NodeInfo> sourceIdMap = new HashMap<>();

        if (!CollectionUtils.isEmpty(oldList)) {
            for (NodeInfo oldMap : oldList) {
                String nodeId = oldMap.getNodeId();
                String sourceId = oldMap.getSourceId();
                String oldOsId = oldMap.getOsId();

                idMap.put(nodeId, oldMap);
                String key = StringBaseOpt.isNvl(sourceId)
                    ? nodeId + ":" + oldOsId
                    : sourceId + ":" + oldOsId;
                sourceIdMap.put(key, oldMap);
            }
        }

        for (Map<String, Object> map : list) {
            String nodeId = StringBaseOpt.objectToString(map.get(NODE_ID));
            String sourceId = StringBaseOpt.objectToString(map.get(SOURCE_ID));
            String uuid = "";

            if (!CollectionUtils.isEmpty(oldList)) {
                String sourceKey = StringBaseOpt.isNvl(sourceId)
                    ? nodeId + ":" + osId
                    : sourceId + ":" + osId;
                NodeInfo matchSource = sourceIdMap.get(sourceKey);

                if (matchSource != null) {
                    uuid = matchSource.getNodeId();
                }

                if (StringBaseOpt.isNvl(uuid)) {
                    NodeInfo matchId = idMap.get(nodeId);
                    if (matchId != null) {
                        String matchOsId = matchId.getOsId();
                        uuid = (matchOsId != null && matchOsId.equals(osId))
                            ? nodeId
                            : UuidOpt.getUuidAsString();
                    } else {
                        uuid = nodeId;
                    }
                }
            } else {
                uuid = nodeId;
            }

            if (!uuid.equals(nodeId)) {
                wfNodeDiffMap.put(nodeId, uuid);
            }

            map.put(NODE_ID, uuid);
            map.put(OS_ID, osId);
            updateIfMapped(map, FLOW_CODE, flowDefineDiffMap);
            updateIfMapped(map, SUB_FLOW_CODE, flowDefineDiffMap);
            updateIfMapped(map, OPT_ID, optInfoDiffMap);

            String optCode = StringBaseOpt.objectToString(map.get(OPT_CODE));
            String nodeType = StringBaseOpt.objectToString(map.get(NODE_TYPE));
            boolean isAutoNodeType = NodeInfo.NODE_TYPE_AUTO.equals(nodeType);
            String changeOptCode = isAutoNodeType
                ? dataPacketDiffMap.get(optCode)
                : metaFormDiffMap.get(optCode);

            if (!StringBaseOpt.isNvl(changeOptCode)) {
                map.put(OPT_CODE, changeOptCode);
            }
        }
    }

    private void updateWfDefineUseDiffMap() {
        List<Map<String, Object>> list = getList(AppTableNames.WF_FLOW_DEFINE.name());
        if (list == null) return;

        Map<String, String> allReplacements = new HashMap<>();
        allReplacements.putAll(metaFormDiffMap);
        allReplacements.putAll(dataPacketDiffMap);
        allReplacements.putAll(optInfoDiffMap);

        for (Map<String, Object> map : list) {
            updateIfMapped(map, FIRST_NODE_ID, wfNodeDiffMap);
            updateIfMapped(map, OPT_ID, optInfoDiffMap);
            updateIfMapped(map, FLOW_CODE, flowDefineDiffMap);
            replaceTemplateFields(map, FLOW_XML_DESC, allReplacements);
        }
    }

    private void updateWfTransition() {
        List<Map<String, Object>> list = getList(AppTableNames.WF_TRANSITION.name());
        if (list == null) return;

        List<FlowTransition> oldList = convertJavaList(FlowTransition.class,
            AppTableNames.WF_TRANSITION.name());
        Set<String> idSet = new HashSet<>();
        Map<String, String> sourceIdMap = new HashMap<>();

        if (!CollectionUtils.isEmpty(oldList)) {
            for (FlowTransition oldMap : oldList) {
                idSet.add(oldMap.getTransId());
                if (!StringBaseOpt.isNvl(oldMap.getFlowCode())
                    && !StringBaseOpt.isNvl(oldMap.getStartNodeId())
                    && !StringBaseOpt.isNvl(oldMap.getEndNodeId())) {
                    String key = oldMap.getFlowCode() + ":" + oldMap.getStartNodeId() + ":" + oldMap.getEndNodeId();
                    sourceIdMap.put(key, oldMap.getTransId());
                }
            }
        }

        for (Map<String, Object> map : list) {
            updateIfMapped(map, FLOW_CODE, flowDefineDiffMap);
            updateIfMapped(map, START_NODE_ID, wfNodeDiffMap);
            updateIfMapped(map, END_NODE_ID, wfNodeDiffMap);

            String transId = StringBaseOpt.objectToString(map.get(TRANS_ID));
            String flowCode = StringBaseOpt.objectToString(map.get(FLOW_CODE));
            String startNodeId = StringBaseOpt.objectToString(map.get(START_NODE_ID));
            String endNodeId = StringBaseOpt.objectToString(map.get(END_NODE_ID));
            String uuid = "";

            if (!CollectionUtils.isEmpty(oldList)) {
                String sourceKey = flowCode + ":" + startNodeId + ":" + endNodeId;
                String matchSource = sourceIdMap.get(sourceKey);
                if (!StringBaseOpt.isNvl(matchSource)) {
                    uuid = matchSource;
                }

                if (StringBaseOpt.isNvl(uuid)) {
                    uuid = idSet.contains(transId) ? UuidOpt.getUuidAsString() : transId;
                }
            } else {
                uuid = transId;
            }

            map.put(TRANS_ID, uuid);
        }
    }

    private void updatePacketUseDiffMap() {
        List<Map<String, Object>> list = getList(AppTableNames.Q_DATA_PACKET.name());
        if (list == null) return;

        Map<String, String> replacementMap = new HashMap<>();
        replacementMap.putAll(mdTableDiffMap);
        replacementMap.putAll(databaseDiffMap);
        replacementMap.putAll(dataPacketDiffMap);
        replacementMap.putAll(dictionaryDiffMap);
        replacementMap.putAll(fileDiffMap);
        replacementMap.putAll(flowDefineDiffMap);

        list.forEach(map -> replaceTemplateFields(map, DATA_OPT_DESC_JSON, replacementMap));
    }

    private void updateMetaFormUseDiffMap() {
        List<Map<String, Object>> list = getList(AppTableNames.M_META_FORM_MODEL.name());
        if (list == null) return;

        Map<String, String> allReplacements = new HashMap<>();
        allReplacements.putAll(metaFormDiffMap);
        allReplacements.putAll(dataPacketDiffMap);
        allReplacements.putAll(dictionaryDiffMap);
        allReplacements.putAll(fileDiffMap);
        allReplacements.putAll(flowDefineDiffMap);

        for (Map<String, Object> map : list) {
            replaceTemplateFields(map, FORM_TEMPLATE, allReplacements);
            replaceTemplateFields(map, MOBILE_FORM_TEMPLATE, allReplacements);
            replaceTemplateFields(map, STRUCTURE_FUNCTION, allReplacements);
        }
    }

    // ==================== 创建对象方法 ====================

    private JsonAppVo createOsInfo() {
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_OS_INFO.name());
        if (list == null) return this;

        try {
            List<Object> convertedList = convertMap(OsInfo.class, list);
            appList.addAll(convertedList);
        } catch (Exception e) {
            logger.error("转换 OsInfo 出错", e);
        }
        return this;
    }

    private JsonAppVo createDataCatalog() {
        if (!this.runDictionary) return this;

        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_DATACATALOG.name());
        if (list == null) return this;

        try {
            List<Object> convertedList = convertMap(DataCatalog.class, list);
            appList.addAll(convertedList);
        } catch (Exception e) {
            logger.error("转换 DataCatalog 出错", e);
        }
        return this;
    }

    private JsonAppVo createDataDictionary() {
        if (!this.runDictionary) return this;

        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_DATADICTIONARY.name());
        if (list == null) return this;

        try {
            List<Object> convertedList = convertMap(DataDictionary.class, list);
            appList.addAll(convertedList);
        } catch (Exception e) {
            logger.error("转换 DataDictionary 出错", e);
        }
        return this;
    }

    private JsonAppVo createApplicationDictionary() {
        if (!this.runDictionary) return this;

        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.M_APPLICATION_DICTIONARY.name());
        if (list == null) return this;

        try {
            List<Object> convertedList = convertMap(ApplicationDictionary.class, list);
            appList.addAll(convertedList);
        } catch (Exception e) {
            logger.error("转换 ApplicationDictionary 出错", e);
        }
        return this;
    }

    private JsonAppVo createMdTableWithColumnObject() {
        if (!this.runMetaData) return this;

        List<Map<String, Object>> tableList = mapJsonObject.get(AppTableNames.F_MD_TABLE.name());
        if (tableList == null) return this;

        try {
            metaObject.addAll(convertMap(MetaTable.class, tableList));

            List<Object> pendingTableList = convertMap(PendingMetaTable.class, tableList);
            pendingTableList.forEach(obj -> {
                PendingMetaTable pending = (PendingMetaTable) obj;
                if (TABLE_TYPE.equals(pending.getTableType())) {
                    pending.setTableState(NO_PUBLISH);
                    appList.add(pending);
                }
            });

            List<Map<String, Object>> columnList = mapJsonObject.get(AppTableNames.F_MD_COLUMN.name());
            if (columnList != null) {
                metaObject.addAll(convertMap(MetaColumn.class, columnList));
                columnList.forEach(map -> map.put(MAX_LENGTH, map.get(COLUMN_LENGTH)));
                pendingMetaObject.addAll(convertMap(PendingMetaColumn.class, columnList));
            }
        } catch (Exception e) {
            logger.error("转换元数据表和列出错", e);
        }
        return this;
    }

    private JsonAppVo createMdRelationWithDetailObject() {
        if (!this.runMetaData) return this;

        try {
            List<Map<String, Object>> relationList = mapJsonObject.get(AppTableNames.F_MD_RELATION.name());
            if (relationList != null) {
                List<Object> convertedList = convertMap(MetaRelation.class, relationList);
                metaObject.addAll(convertedList);
            }

            List<Map<String, Object>> detailList = mapJsonObject.get(AppTableNames.F_MD_REL_DETAIL.name());
            if (detailList != null) {
                List<Object> convertedList = convertMap(MetaRelDetail.class, detailList);
                metaObject.addAll(convertedList);
            }
        } catch (Exception e) {
            logger.error("转换 MetaRelation 出错", e);
        }
        return this;
    }

    private JsonAppVo createMetaFormObject() {
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.M_META_FORM_MODEL.name());
        if (list == null) return this;

        try {
            metaformList = convertMap(MetaFormModel.class, list);
            metaformDraftList = convertMap(MetaFormModelDraft.class, list);
        } catch (Exception e) {
            logger.error("转换 MetaFormModel 出错", e);
        }
        return this;
    }

    private JsonAppVo createDataPacketAndParamsObject() {
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.Q_DATA_PACKET.name());
        if (list == null) return this;

        try {
            packetList = convertMap(DataPacket.class, list);
            packetDraftList = convertMap(DataPacketDraft.class, list);

            List<Map<String, Object>> paramList = mapJsonObject.get(AppTableNames.Q_DATA_PACKET_PARAM.name());
            if (paramList != null) {
                List<Object> convertedList = convertMap(DataPacketParam.class, paramList);
                appList.addAll(convertedList);

                convertedList = convertMap(DataPacketParamDraft.class, paramList);
                appList.addAll(convertedList);
            }
        } catch (Exception e) {
            logger.error("转换 DataPacket 出错", e);
        }
        return this;
    }

    private JsonAppVo createWfOptTeamRole() {

        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.WF_OPT_TEAM_ROLE.name());
        if (list == null) return this;

        try {
            List<Object> convertedList = convertMap(OptTeamRole.class, list);
            workFlowList.addAll(convertedList);
        } catch (Exception e) {
            logger.error("转换 OptTeamRole 出错", e);
        }
        return this;
    }

    private JsonAppVo createWfOptVariable() {

        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.WF_OPT_VARIABLE_DEFINE.name());
        if (list == null) return this;

        try {
            List<Object> convertedList = convertMap(OptVariableDefine.class, list);
            workFlowList.addAll(convertedList);
        } catch (Exception e) {
            logger.error("转换 OptVariableDefine 出错", e);
        }
        return this;
    }

    private JsonAppVo createOptInfo() {

        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_OPTINFO.name());
        if (list == null) return this;

        try {
            List<Object> convertedList = convertMap(OptInfo.class, list);
            appList.addAll(convertedList);
        } catch (Exception e) {
            logger.error("转换 OptInfo 出错", e);
        }
        return this;
    }

    private JsonAppVo createOptDef() {

        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_OPTDEF.name());
        if (list == null) return this;

        try {
            List<Object> convertedList = convertMap(OptMethod.class, list);
            appList.addAll(convertedList);
        } catch (Exception e) {
            logger.error("转换 OptMethod 出错", e);
        }
        return this;
    }

    private JsonAppVo createWfDefine() {

        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.WF_FLOW_DEFINE.name());
        if (list == null) return this;

        try {
            List<Object> convertedList = convertMap(FlowInfo.class, list);
            workFlowList.addAll(convertedList);
        } catch (Exception e) {
            logger.error("转换 FlowInfo 出错", e);
        }
        return this;
    }

    private JsonAppVo createWfNode() {

        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.WF_NODE.name());
        if (list == null) return this;

        try {
            wfNodeList = convertMap(NodeInfo.class, list);
        } catch (Exception e) {
            logger.error("转换 NodeInfo 出错", e);
        }
        return this;
    }

    private JsonAppVo createWfTransition() {

        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.WF_TRANSITION.name());
        if (list == null) return this;

        try {
            wfTransitionList = convertMap(FlowTransition.class, list);
        } catch (Exception e) {
            logger.error("转换 FlowTransition 出错", e);
        }
        return this;
    }

    private JsonAppVo createTableRelation() {

        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_TABLE_OPT_RELATION.name());
        if (list == null) return this;

        try {
            List<Object> convertedList = convertMap(MetaOptRelation.class, list);
            appList.addAll(convertedList);
        } catch (Exception e) {
            logger.error("转换 MetaOptRelation 出错", e);
        }
        return this;
    }

    private void createFileFolder() {

        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.FILE_FOLDER_INFO.name());
        if (list == null) return;

        try {
            List<Object> convertedList = convertMap(FileFolderInfo.class, list);
            appList.addAll(convertedList);
        } catch (Exception e) {
            logger.error("转换 FileFolderInfo 出错", e);
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 执行更新步骤并记录日志
     */
    private void executeUpdateStep(String stepName, Runnable updateMethod) {
        try {
            logger.info("开始更新{}", stepName);
            updateMethod.run();
            logger.info("{}更新完成", stepName);
        } catch (Exception e) {
            logger.error("{}更新失败", stepName, e);
            throw new ObjectException(stepName + "更新失败", e);
        }
    }

    /**
     * 从JSONObject中提取List并转换为Map
     */
    private void createMapJsonObject(JSONObject jsonObject) {
        if (jsonObject == null) return;

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
     * 获取列表，避免null检查
     */
    private List<Map<String, Object>> getList(String key) {
        return mapJsonObject.get(key);
    }

    /**
     * 清空所有列表
     */
    private void clearAllLists() {
        appList.clear();
        metaformList.clear();
        metaformDraftList.clear();
        packetList.clear();
        packetDraftList.clear();
        workFlowList.clear();
        wfNodeList.clear();
        wfTransitionList.clear();
        metaObject.clear();
        pendingMetaObject.clear();
    }

    /**
     * 添加差异条目
     */
    private void addDiffEntry(JSONArray array, String keyName, Object value) {
        if (value == null) return;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(keyName, value);
        array.add(jsonObject);
    }

    /**
     * 如果映射中存在则更新
     */
    private void updateIfMapped(Map<String, Object> map, String key, Map<String, String> diffMap) {
        if (map == null || key == null || diffMap == null || diffMap.isEmpty()) {
            return;
        }
        String value = StringBaseOpt.objectToString(map.get(key));
        String newValue = diffMap.get(value);
        if (!StringBaseOpt.isNvl(newValue)) {
            map.put(key, newValue);
        }
    }

    /**
     * 替换模板字段中的占位符
     */
    private void replaceTemplateFields(Map<String, Object> map, String fieldKey, Map<String, String> allReplacements) {
        Object templateObj = map.get(fieldKey);
        if (templateObj == null || allReplacements == null || allReplacements.isEmpty()) {
            return;
        }

        String form = templateObj.toString();
        String result = replaceWithRegex(form, allReplacements);
        map.put(fieldKey, result);
    }

    /**
     * 使用正则表达式批量替换 - 带缓存优化
     */
    private String replaceWithRegex(String text, Map<String, String> replacements) {
        if (StringUtils.isBlank(text) || mapIsEmpty(replacements)) {
            return text;
        }

        // 构建缓存键
        String cacheKey = generatePatternCacheKey(replacements);
        Pattern pattern = PATTERN_CACHE.get(cacheKey);

        if (pattern == null) {
            StringBuilder patternBuilder = new StringBuilder();
            boolean first = true;
            for (String key : replacements.keySet()) {
                if (!first) {
                    patternBuilder.append("|");
                }
                patternBuilder.append(Pattern.quote(key));
                first = false;
            }
            pattern = Pattern.compile(patternBuilder.toString());
            PATTERN_CACHE.put(cacheKey, pattern);
        }

        Matcher matcher = pattern.matcher(text);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String matchedKey = matcher.group();
            String replacement = replacements.get(matchedKey);
            if (replacement != null) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
            }
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * 生成正则表达式缓存键
     */
    private String generatePatternCacheKey(Map<String, String> replacements) {
        return replacements.keySet().stream()
            .sorted()
            .collect(Collectors.joining("|"));
    }

    /**
     * 从文件名中提取oldFileId
     */
    private String extractOldFileIdFromName(String fileName) {
        if (StringUtils.isBlank(fileName)) return null;

        int startIdx = fileName.indexOf('(');
        int endIdx = fileName.indexOf(')');

        if (startIdx != -1 && endIdx != -1 && startIdx < endIdx) {
            return fileName.substring(startIdx + 1, endIdx);
        }
        return null;
    }

    /**
     * 确定UUID - 提取的通用逻辑
     */
    private String determineUuid(String catalogCode, String sourceId,
                                 Map<String, DataCatalog> catalogCodeMap,
                                 Map<String, DataCatalog> catalogSourceMap,
                                 Map<String, String> diffMap) {
        String uuid = "";
        boolean hasOldData = !catalogCodeMap.isEmpty();

        if (hasOldData) {
            String sourceKey = StringBaseOpt.isNvl(sourceId)
                ? catalogCode + ":" + topUnit
                : sourceId + ":" + topUnit;

            DataCatalog matchedBySourceId = catalogSourceMap.get(sourceKey);
            if (matchedBySourceId != null) {
                uuid = matchedBySourceId.getCatalogCode();
            }

            if (StringUtils.isBlank(uuid)) {
                DataCatalog matchedByCatalogCode = catalogCodeMap.get(catalogCode);
                if (matchedByCatalogCode != null) {
                    boolean sameTopUnit = topUnit.equals(matchedByCatalogCode.getTopUnit());
                    if (sameTopUnit) {
                        uuid = catalogCode;
                    } else {
                        uuid = UuidOpt.getUuidAsString();
                        diffMap.put(catalogCode, uuid);
                    }
                } else {
                    uuid = catalogCode;
                }
            }
        } else {
            uuid = catalogCode;
        }

        return uuid;
    }

    /**
     * 生成UUID - 提取的通用逻辑
     */
    private String generateUuid(String optId, String sourceId, String osId,
                                List<OptInfo> oldList,
                                Map<String, OptInfo> optIdMap,
                                Map<String, String> sourceIdMap) {
        String uuid = "";

        if (!CollectionUtils.isEmpty(oldList)) {
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

        return uuid;
    }

    /**
     * 转换Java列表 - 带异常处理
     */
    private <T> List<T> convertJavaList(Class<T> type, String className) {
        if (notHaveOldData(className)) {
            return null;
        }
        try {
            return oldAppObject.getJSONArray(className).toJavaList(type);
        } catch (Exception e) {
            logger.error("转换 {} 失败", className, e);
            return null;
        }
    }

    /**
     * 检查是否有旧数据
     */
    private boolean notHaveOldData(String dataName) {
        return oldAppObject == null
            || oldAppObject.get(dataName) == null
            || oldAppObject.getJSONArray(dataName).isEmpty();
    }

    /**
     * 将Map列表转换为Java对象列表
     */
    private List<Object> convertMap(Class<?> type, List<Map<String, Object>> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }

        List<Object> objects = new ArrayList<>();
        try {
            JavaBeanMetaData javaBeanMetaData = JavaBeanMetaData.createBeanMetaDataFromType(type);
            for (Map<String, Object> map : list) {
                if (mapIsEmpty(map)) {
                    continue;
                }
                objects.add(javaBeanMetaData.createBeanObjectFromMap(map));
            }
        } catch (Exception e) {
            logger.error("转换 {} 失败", type.getSimpleName(), e);
        }
        return objects;
    }

    private boolean mapIsEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
}
