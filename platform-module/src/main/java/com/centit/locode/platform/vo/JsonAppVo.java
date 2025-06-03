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
import com.centit.fileserver.common.FileLibraryInfo;
import com.centit.fileserver.po.FileInfo;
import com.centit.framework.model.basedata.*;
import com.centit.framework.model.security.CentitUserDetails;
import com.centit.locode.platform.po.ApplicationDictionary;
import com.centit.locode.platform.po.ApplicationResources;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author zhf
 */

public class JsonAppVo {
    private static final String OS_ID = "osId";
    private static final String DATABASE_CODE = "databaseCode";
    private static final String TABLE_ID = "tableId";
    private static final String REFERENCE_DATA = "referenceData";
    private static final String RELATION_ID = "relationId";
    private static final String PARENT_TABLE_ID = "parentTableId";
    private static final String OPT_ID = "optId";
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
    private static final String MAP_DATA_CODE = "mapDataCode";

    @Getter
    @Setter
    private JSONObject oldAppObject;
    @Getter
    private Map<String, List<Map<String, Object>>> mapJsonObject = new HashMap<>();
    private String zipFilePath;
    private String appHome;
    private FileInfoOpt fileInfoOpt;
    @Getter
    private String userCode;
    private String topUnit;
    private String osId;
    private String defaultDatabase;
    @Getter
    private List<Object> appList = new ArrayList<>();
    @Getter
    private List<Object> metaObject = new ArrayList<>();
    @Getter
    private List<String> listDatabaseName = new ArrayList<>();
    @Getter
    @Setter
    private boolean uploadFiles;
    @Getter
    @Setter
    private boolean runMetaData;
    @Getter
    @Setter
    private boolean runDictionary;

    private Map<String, Object> databaseMap = new HashMap<>();
    private Map<String, Object> mdTableMap = new HashMap<>();
    private Map<String, Object> relationMap = new HashMap<>();
    private Map<String, Object> dataPacketMap = new HashMap<>();
    private Map<String, Object> metaFormMap = new HashMap<>();
    private Map<String, Object> flowDefineMap = new HashMap<>();
    private Map<String, Object> optInfoMap = new HashMap<>();
    private Map<String, Object> wfNodeMap = new HashMap<>();
    private Map<String, Object> dictionaryMap = new HashMap<>();
    private Map<String, Object> fileMap = new HashMap<>();


    public JsonAppVo(JSONObject jsonObject, JSONObject oldObject, CentitUserDetails userDetails, String appHome, FileInfoOpt fileInfoOpt, String zipFilePath) {
        createMapJsonObject(jsonObject);
        this.zipFilePath = zipFilePath;
        this.oldAppObject = oldObject;
        this.userCode = userDetails == null ? "" : userDetails.getUserCode();
        this.topUnit = userDetails == null ? "" : userDetails.getTopUnitCode();
        this.appHome = appHome;
        this.fileInfoOpt = fileInfoOpt;
        this.runMetaData=true;
        this.runDictionary=true;
        this.uploadFiles=true;
    }

    public JSONArray getDiffIds(){
        JSONArray jsonArray = new JSONArray();
        JSONObject dataPacketMapJson=new JSONObject();
        Map<String, Object> dataPacketDiffMap = new HashMap<>();
         for (Map.Entry<String, Object> entry : dataPacketMap.entrySet()) {
             if (!entry.getKey().equals(StringBaseOpt.objectToString(entry.getValue()))) {
                 dataPacketDiffMap.put(entry.getKey(), entry.getValue());
             }
        }
        dataPacketMapJson.put("接口主键变化",dataPacketDiffMap);
        jsonArray.add(dataPacketMapJson);
        JSONObject metaFormMapJson=new JSONObject();
        Map<String, Object> metaFormDiffMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : metaFormMap.entrySet()) {
            if (!entry.getKey().equals(StringBaseOpt.objectToString(entry.getValue()))) {
                metaFormDiffMap.put(entry.getKey(), entry.getValue());
            }
        }
        metaFormMapJson.put("页面主键变化",metaFormDiffMap);
        jsonArray.add(metaFormMapJson);
        JSONObject flowDefineMapJson=new JSONObject();
        Map<String, Object> flowDefineDiffMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : flowDefineMap.entrySet()) {
            if (!entry.getKey().equals(StringBaseOpt.objectToString(entry.getValue()))) {
                flowDefineDiffMap.put(entry.getKey(), entry.getValue());
            }
        }
        flowDefineMapJson.put("流程主键变化",flowDefineDiffMap);
        jsonArray.add(flowDefineMapJson);
        JSONObject dictionaryMapJson=new JSONObject();
        Map<String, Object> dictionaryDiffMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : dictionaryMap.entrySet()) {
            if (!entry.getKey().equals(StringBaseOpt.objectToString(entry.getValue()))) {
                dictionaryDiffMap.put(entry.getKey(), entry.getValue());
            }
        }
        dictionaryMapJson.put("数据字典主键变化",dictionaryDiffMap);
        jsonArray.add(dictionaryMapJson);
        return jsonArray;
    }


    public void prepareApp() {
        updatePrimary();
        createAppObject();
        setDatabaseName();
    }

    public void refreshCache(DdeDubboTaskRun ddeDubboTaskRun) {
        for (Object object : appList) {
            if (object instanceof DataPacket) {
                ddeDubboTaskRun.refreshCache(((DataPacket) object).getPacketId());
            }
        }
    }

    private void createMapJsonObject(JSONObject jsonObject) {
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            mapJsonObject.put(entry.getKey(),
                (List<Map<String, Object>>) entry.getValue());
        }
    }

    public void updatePrimary() {
        this.updateOsInfo().updateLibraryInfo().updateDatabase().updateApplicationResource()
            .updateDataCatalog().updateDataDictionaryUseCatalog().updateApplicationDictionary()
            .updateOsInfoUseDatabase()
            .updateMdTable().updateMdColumn().updateMdRelation().updateRelationDetail()
            .updateOptInfo().updateOptDef().updateTableRelation().uploadFiles()
            .updatePacket().updateOptDefUsePacket().updatePacketParams()
            .updateMetaForm().updateOptInfoUseMetaForm().updateWfDefineUseMetaFormWithPacket().updateWfNodeUseMetaFormWithPacket()
            .updateWfOptTeamRole().updateWfOptVariable()
            .updateWfDefine().updateWfNode().updateWfDefineUseWfNode().updateWfTransition().updatePacketUseWfDefine().updateMetaFormUseWfDefine();
    }

    public void createAppObject() {
        appList.clear();
        this.createOsInfo().createLibraryInfo().createApplicationResource().createDataCatalog().createDataDictionary().createApplicationDictionary()
            .createMdTableWithColumnObject().createMdRelationWithDetailObject()
            .createMetaFormObject().createDataPacketAndParamsObject()
            .createWfOptTeamRole().createWfOptVariable().createOptInfo().createOptDef()
            .createWfDefine().createWfNode().createWfTransition().createTableRelation();
    }

    public void setDatabaseName() {
        if (mapJsonObject.get(AppTableNames.F_DATABASE_INFO.name()) == null) {
            return;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_DATABASE_INFO.name());
        list.forEach(map -> {
            if ("D".equals(StringBaseOpt.objectToString(map.get("sourceType")))) {
                listDatabaseName.add(StringBaseOpt.objectToString(map.get(DATABASE_CODE)));
            }
        });
    }

    private JsonAppVo updateOsInfo() {
        if (mapJsonObject.get(AppTableNames.F_OS_INFO.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_OS_INFO.name());
        List<OsInfo> oldList = convertJavaList(OsInfo.class, AppTableNames.F_OS_INFO.name());
        list.forEach(map -> {
            if (oldList != null) {
                osId = oldList.get(0).getOsId();
                defaultDatabase = oldList.get(0).getDefaultDatabase();
                map.put(DEFAULT_DATABASE, defaultDatabase);
                map.put(OS_NAME, oldList.get(0).getOsName());
            } else {
                osId = UuidOpt.getUuidAsString();
            }
            map.put(OS_ID, osId);
            map.put(REL_OPT_ID, osId);
            map.put(CREATED, userCode);
            map.put(CREATE_TIME, new Date());
            map.put(LAST_MODIFY_DATE, new Date());
            map.remove(TOP_UNIT);
            map.remove("logoFileId");
            map.remove("picId");
        });
        return this;
    }

    private JsonAppVo updateLibraryInfo() {
        if (mapJsonObject.get(AppTableNames.FILE_LIBRARY_INFO.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.FILE_LIBRARY_INFO.name());
        List<FileLibraryInfo> oldList = convertJavaList(FileLibraryInfo.class, AppTableNames.FILE_LIBRARY_INFO.name());
        list.forEach(map -> {
            if (oldList != null) {
                map.put(LIBRARY_NAME, oldList.get(0).getLibraryName());
            }
            map.put(LIBRARY_ID, osId);
            map.put(CREATE_USER, userCode);
            map.put(OWN_USER, userCode);
            map.put(CREATE_TIME, new Date());
            map.put(UPDATE_TIME, new Date());
        });
        return this;
    }

    private JsonAppVo updateDatabase() {
        if (mapJsonObject.get(AppTableNames.F_DATABASE_INFO.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_DATABASE_INFO.name());
        List<SourceInfo> oldList = convertJavaList(SourceInfo.class, AppTableNames.F_DATABASE_INFO.name());
        list.forEach(map -> {
            String uuid;
            if (map.get(MAP_DATA_CODE) != null && StringUtils.isNotBlank((String) map.get(MAP_DATA_CODE))) {
                uuid = (String) map.get(MAP_DATA_CODE);
            } else if (StringUtils.isNotBlank(defaultDatabase)) {
                uuid = defaultDatabase;
            } else if (oldList != null) {
                uuid = oldList.get(0).getDatabaseCode();
            } else {
                throw new ObjectException(ObjectException.DATA_NOT_INTEGRATED, map.get("databaseName") + ":没有指定数据库");
            }
            for (SourceInfo sourceInfo : oldList) {
                if (uuid.equals(sourceInfo.getDatabaseCode())) {
                    map.put("databaseName", sourceInfo.getDatabaseName());
                }
            }
            databaseMap.put(map.get(DATABASE_CODE).toString(), uuid);
            map.put(DATABASE_CODE, uuid);
            map.put(OS_ID, osId);
            map.put(CREATED, userCode);
            map.put(CREATE_TIME, new Date());
            map.put(LAST_MODIFY_DATE, new Date());
        });
        return this;
    }

    private JsonAppVo updateApplicationResource() {
        if (mapJsonObject.get(AppTableNames.M_APPLICATION_RESOURCES.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.M_APPLICATION_RESOURCES.name());
        List<ApplicationResources> oldList = convertJavaList(ApplicationResources.class, AppTableNames.M_APPLICATION_RESOURCES.name());
        list.forEach(map -> {
            String uuid = "";
            databaseMap.keySet().stream().filter(key -> key.equals(map.get(DATABASE_ID)))
                .findFirst().ifPresent(key -> map.put(DATABASE_ID, databaseMap.get(key)));
            if (oldList != null) {
                for (ApplicationResources oldMap : oldList) {
                    if (oldMap.getDataBaseId() != null) {
                        boolean equalsResource = oldMap.getDataBaseId().equals(map.get(DATABASE_ID).toString()) &&
                            (oldMap.getOsId().equals(map.get(OS_ID).toString()) || oldMap.getOsId().equals(osId));
                        if (equalsResource) {
                            uuid = oldMap.getId();
                            break;
                        }
                    }
                }
            }
            if (StringUtils.isBlank(uuid)) {
                uuid = UuidOpt.getUuidAsString();
            }
            map.put(ID, uuid);
            map.put(OS_ID, osId);
            map.put(DATABASE_ID_CLASS, map.get(DATABASE_ID));
            map.put(PUSH_USER, userCode);
            map.put(PUSH_TIME, new Date());
        });
        return this;
    }

    private JsonAppVo updateDataCatalog() {
        if (mapJsonObject.get(AppTableNames.F_DATACATALOG.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_DATACATALOG.name());
        List<DataCatalog> oldList = convertJavaList(DataCatalog.class, AppTableNames.F_DATACATALOG.name());
        list.forEach(map -> {
            map.put(SOURCE_ID, map.get(CATALOG_CODE));
            String uuid = "";
            if (oldList != null) {
                for (DataCatalog oldMap : oldList) {
                    if (oldMap.getSourceId() != null) {
                        boolean equalsResource = oldMap.getSourceId().equals(map.get(SOURCE_ID).toString());
                        if (equalsResource) {
                            uuid = oldMap.getSourceId();
                            break;
                        }
                    }
                }
                if (StringUtils.isBlank(uuid)) {
                    for (DataCatalog oldMap : oldList) {
                        boolean findRepeatCatalog = map.get(CATALOG_CODE).toString().equals(oldMap.getCatalogCode())
                            && !osId.equals(oldMap.getOsId());
                        if (findRepeatCatalog) {
                            uuid = UuidOpt.getUuidAsString();
                            break;
                        }
                    }
                }
            }
            if (StringUtils.isBlank(uuid)) {
                uuid = map.get(CATALOG_CODE).toString();
            }
            dictionaryMap.put(map.get(CATALOG_CODE).toString(), uuid);
            map.put(CATALOG_CODE, uuid);
            map.put(OS_ID, osId);
            map.put(CREATOR, userCode);
            map.put(CREATE_DATE, new Date());
            map.put(UPDATOR, userCode);
            map.put(UPDATE_DATE, new Date());
            map.put(TOP_UNIT, topUnit);
        });
        return this;
    }

    private JsonAppVo updateDataDictionaryUseCatalog() {
        if (mapJsonObject.get(AppTableNames.F_DATADICTIONARY.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_DATADICTIONARY.name());
        list.forEach(map -> dictionaryMap.keySet().stream().filter(key -> key.equals(map.get(CATALOG_CODE)))
            .findFirst().ifPresent(key -> map.put(CATALOG_CODE, dictionaryMap.get(key))));
        return this;
    }

    private JsonAppVo updateApplicationDictionary() {
        if (mapJsonObject.get(AppTableNames.M_APPLICATION_DICTIONARY.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.M_APPLICATION_DICTIONARY.name());
        List<ApplicationDictionary> oldList = convertJavaList(ApplicationDictionary.class, AppTableNames.M_APPLICATION_DICTIONARY.name());
        list.forEach(map -> {
            String uuid = "";
            dictionaryMap.keySet().stream().filter(key -> key.equals(map.get(DICTIONARY_ID)))
                .findFirst().ifPresent(key -> map.put(DICTIONARY_ID, dictionaryMap.get(key)));
            if (oldList != null) {
                for (ApplicationDictionary oldMap : oldList) {
                    boolean equalsResource = oldMap.getDictionaryId().equals(map.get(DICTIONARY_ID).toString()) &&
                        (oldMap.getOsId().equals(map.get(OS_ID).toString()) || oldMap.getOsId().equals(osId));
                    if (equalsResource) {
                        uuid = oldMap.getId();
                        break;
                    }
                }
            }
            if (StringUtils.isBlank(uuid)) {
                uuid = UuidOpt.getUuidAsString();
            }
            map.put(ID, uuid);
            map.put(OS_ID, osId);
            map.put(DICTIONARY_ID, map.get(DICTIONARY_ID));
            map.put(PUSH_USER, userCode);
            map.put(PUSH_TIME, new Date());
        });
        return this;
    }

    private JsonAppVo updateOsInfoUseDatabase() {
        if (mapJsonObject.get(AppTableNames.F_OS_INFO.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_OS_INFO.name());
        list.forEach(map -> databaseMap.keySet().stream().filter(key -> key.equals(map.get(DEFAULT_DATABASE)))
            .findFirst().ifPresent(key -> map.put(DEFAULT_DATABASE, databaseMap.get(key))));
        return this;
    }

    private JsonAppVo updateMdTable() {
        if (mapJsonObject.get(AppTableNames.F_MD_TABLE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_MD_TABLE.name());
        List<MetaTable> finalOldList = convertJavaList(MetaTable.class, AppTableNames.F_MD_TABLE.name());
        list.forEach(map -> {
            String uuid = "";
            map.put(RECORDER, userCode);
            map.put(RECORD_DATE, new Date());
            databaseMap.keySet().stream().filter(key -> key.equals(map.get(DATABASE_CODE)))
                .findFirst().ifPresent(key -> map.put(DATABASE_CODE, databaseMap.get(key)));
            if (finalOldList != null) {
                for (MetaTable oldMap : finalOldList) {
                    if (oldMap.getTableName().equals(map.get(TABLE_NAME).toString()) &&
                        oldMap.getDatabaseCode().equals(map.get(DATABASE_CODE).toString())
                    ) {
                        uuid = oldMap.getTableId();
                        break;
                    }
                }
            }
            if (StringUtils.isBlank(uuid)) {
                uuid = UuidOpt.getUuidAsString();
            }
            mdTableMap.put(map.get(TABLE_ID).toString(), uuid);
            map.put(TABLE_ID, uuid);
        });
        return this;
    }

    private JsonAppVo updateMdColumn() {
        if (mapJsonObject.get(AppTableNames.F_MD_COLUMN.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_MD_COLUMN.name());
        list.forEach(map -> {
            map.put(RECORDER, userCode);
            map.put(LAST_MODIFY_DATE, new Date());
            mdTableMap.keySet().stream().filter(key -> key.equals(map.get(TABLE_ID)))
                .findFirst().ifPresent(key -> map.put(TABLE_ID, mdTableMap.get(key)));
            dictionaryMap.keySet().stream().filter(key -> key.equals(map.get(REFERENCE_DATA)))
                .findFirst().ifPresent(key -> map.put(REFERENCE_DATA, dictionaryMap.get(key)));
        });
        return this;
    }

    private JsonAppVo updateMdRelation() {
        if (mapJsonObject.get(AppTableNames.F_MD_RELATION.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_MD_RELATION.name());
        List<MetaRelation> finalOldList = convertJavaList(MetaRelation.class, AppTableNames.F_MD_RELATION.name());
        list.forEach(map -> {
            mdTableMap.keySet().stream().filter(key -> key.equals(map.get(PARENT_TABLE_ID)))
                .findFirst().ifPresent(key -> map.put(PARENT_TABLE_ID, mdTableMap.get(key)));
            mdTableMap.keySet().stream().filter(key -> key.equals(map.get(CHILD_TABLE_ID)))
                .findFirst().ifPresent(key -> map.put(CHILD_TABLE_ID, mdTableMap.get(key)));
            String uuid = "";
            if (finalOldList != null) {
                for (MetaRelation oldMap : finalOldList) {
                    if (oldMap.getParentTableId().equals(map.get(PARENT_TABLE_ID).toString()) &&
                        oldMap.getChildTableId().equals(map.get(CHILD_TABLE_ID).toString())
                    ) {
                        uuid = oldMap.getRelationId();
                        break;
                    }
                }
            }
            if (StringUtils.isBlank(uuid)) {
                uuid = UuidOpt.getUuidAsString();
            }
            relationMap.put(map.get(RELATION_ID).toString(), uuid);
            map.put(RELATION_ID, uuid);
            map.put(RECORDER, userCode);
            map.put(LAST_MODIFY_DATE, new Date());
        });
        return this;
    }

    private JsonAppVo updateRelationDetail() {
        if (mapJsonObject.get(AppTableNames.F_MD_REL_DETAIL.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_MD_REL_DETAIL.name());
        list.forEach(map -> relationMap.keySet().stream().filter(key -> key.equals(map.get(RELATION_ID)))
            .findFirst().ifPresent(key -> map.put(RELATION_ID, relationMap.get(key))));
        return this;
    }

    private JsonAppVo updateOptInfo() {
        if (mapJsonObject.get(AppTableNames.F_OPTINFO.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_OPTINFO.name());
        List<OptInfo> finalOldList = convertJavaList(OptInfo.class, AppTableNames.F_OPTINFO.name());
        list.forEach(map -> {
            map.put(DOC_ID, "");
            String uuid = "";
            if (map.get(OPT_ID).equals(map.get(TOP_OPT_ID))) {
                uuid = osId;
                boolean findRepeatOptId = false;
                for (OptInfo oldMap : finalOldList) {
                    findRepeatOptId = uuid.equals(oldMap.getOptId())
                        && uuid.equals(oldMap.getTopOptId());
                    if (findRepeatOptId) {
                        map.put("optName", oldMap.getOptName());
                        break;
                    }
                }
            } else if (OptInfo.OPT_INFO_FORM_CODE_COMMON.equals(map.get(FORM_CODE)) ||
                OptInfo.OPT_INFO_FORM_CODE_PAGE_ENTER.equals(map.get(FORM_CODE))) {
                if (finalOldList != null) {
                    for (OptInfo oldMap : finalOldList) {
                        boolean findSameCode = osId.equals(oldMap.getTopOptId()) &&
                            (map.get(FORM_CODE).equals(oldMap.getFormCode())
                                || map.get(FORM_CODE).equals(oldMap.getFormCode()));
                        if (findSameCode) {
                            uuid = oldMap.getOptId();
                            map.put(DOC_ID, oldMap.getDocId());
                            break;
                        }
                    }
                }
            } else {
                if (finalOldList != null) {
                    for (OptInfo oldMap : finalOldList) {
                        boolean canChangDocId = map.get(SOURCE_ID).toString().equals(oldMap.getSourceId()) && osId.equals(oldMap.getTopOptId());
                        if (canChangDocId) {
                            uuid = oldMap.getOptId();
                            map.put(DOC_ID, oldMap.getDocId());
                            break;
                        }
                    }
                    if (StringUtils.isBlank(uuid)) {
                        for (OptInfo oldMap : finalOldList) {
                            boolean findRepeatOptId = map.get(OPT_ID).toString().equals(oldMap.getOptId())
                                && !osId.equals(oldMap.getTopOptId());
                            if (findRepeatOptId) {
                                uuid = UuidOpt.getUuidAsString();
                                map.put(DOC_ID, uuid);
                                break;
                            }
                        }
                    }
                }
            }
            if (StringUtils.isBlank(uuid)) {
                uuid = map.get(OPT_ID).toString();
            }
            optInfoMap.put((String) map.get(OPT_ID), uuid);
            map.put(OPT_ID, uuid);
            map.put(TOP_OPT_ID, osId);
            map.put(OS_ID, osId);
            map.put(UPDATOR, userCode);
            map.put(UPDATE_DATE, new Date());
            map.put(CREATOR, userCode);
            map.put(CREATE_DATE, new Date());
        });
        list.forEach(map ->
            optInfoMap.keySet().stream().filter(key -> key.equals(map.get(PRE_OPT_ID)))
                .findFirst().ifPresent(key -> map.put(PRE_OPT_ID, optInfoMap.get(key)))
        );
        return this;
    }

    private JsonAppVo updateOptDef() {
        if (mapJsonObject.get(AppTableNames.F_OPTDEF.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_OPTDEF.name());
        List<OptMethod> finalOldList = convertJavaList(OptMethod.class, AppTableNames.F_OPTDEF.name());
        list.forEach(map -> {
            String uuid = "";
            if (finalOldList != null) {
                for (OptMethod oldMap : finalOldList) {
                    if (map.get(SOURCE_ID).toString().equals(oldMap.getSourceId()) && osId.equals(oldMap.getTopOptId())) {
                        uuid = oldMap.getOptCode();
                        break;
                    }
                }
                if (StringUtils.isBlank(uuid)) {
                    for (OptMethod oldMap : finalOldList) {
                        boolean findRepeatOptCode = map.get(OPT_CODE).toString().equals(oldMap.getOptCode())
                            && !osId.equals(oldMap.getTopOptId());
                        if (findRepeatOptCode) {
                            uuid = UuidOpt.getUuidAsString();
                            break;
                        }
                    }
                }
            }
            if (StringUtils.isBlank(uuid)) {
                uuid = map.get(OPT_CODE).toString();
            }
            map.put(OPT_CODE, uuid);
            map.put(UPDATOR, userCode);
            map.put(UPDATE_DATE, new Date());
            map.put(CREATOR, userCode);
            map.put(CREATE_DATE, new Date());
            optInfoMap.keySet().stream().filter(key -> key.equals(map.get(OPT_ID)))
                .findFirst().ifPresent(key -> map.put(OPT_ID, optInfoMap.get(key)));
        });
        return this;
    }

    private JsonAppVo updateTableRelation() {
        if (mapJsonObject.get(AppTableNames.F_TABLE_OPT_RELATION.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_TABLE_OPT_RELATION.name());
        List<MetaOptRelation> finalOldList = convertJavaList(MetaOptRelation.class, AppTableNames.F_TABLE_OPT_RELATION.name());
        list.forEach(map -> {
            mdTableMap.keySet().stream().filter(key -> key.equals(map.get(TABLE_ID)))
                .findFirst().ifPresent(key -> map.put(TABLE_ID, mdTableMap.get(key)));
            optInfoMap.keySet().stream().filter(key -> key.equals(map.get(OPT_ID)))
                .findFirst().ifPresent(key -> map.put(OPT_ID, optInfoMap.get(key)));
            String uuid = "";
            if (finalOldList != null) {
                for (MetaOptRelation oldMap : finalOldList) {
                    if (map.get(TABLE_ID).toString().equals(oldMap.getTableId())
                        && map.get(OPT_ID).toString().equals(oldMap.getOptId())) {
                        uuid = oldMap.getId();
                        break;
                    }
                }
            }
            if (StringUtils.isBlank(uuid)) {
                uuid = UuidOpt.getUuidAsString();
            }
            map.put(ID, uuid);
            map.put(OS_ID, osId);
        });
        return this;
    }

    private JsonAppVo uploadFiles() {
        if(!uploadFiles){
            return this;
        }
        if (StringUtils.isBlank(zipFilePath)) {
            return this;
        }
        List<File> files = FileSystemOpt.findFiles(zipFilePath, "file.zip");
        if (files == null || files.size() == 0) {
            return this;
        }
        String filePath = appHome + File.separator + "u" + DatetimeOpt.convertDateToString(DatetimeOpt.currentUtilDate(), "YYYYMMddHHmmss");
        ZipCompressor.release(files.get(0), filePath);
        List<File> zipFiles = FileSystemOpt.findFiles(filePath, "*");
        zipFiles.forEach(file -> {
            FileInfo fileInfo = new FileInfo();
            String fileName = FileSystemOpt.extractFullFileName(file.getPath());
            String oldFileId = fileName.substring(fileName.indexOf("(") + 1, fileName.indexOf(")"));
            fileInfo.setFileName(StringUtils.replace(fileName,"("+oldFileId+")",""));
            fileInfo.setFileShowPath("/-1");
            fileInfo.setLibraryId(osId);
            fileInfo.setFileCatalog("A");
            String fileId = null;
            try {
                fileId = fileInfoOpt.saveFile(fileInfo, -1, new FileInputStream(file.getPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileInfo.setFileId(fileId);
            fileMap.put(oldFileId, fileId);
        });
        FileSystemOpt.deleteDirect(filePath);
//        对于模板不能删除资源文件
//        FileSystemOpt.deleteDirect(zipFilePath);
        return this;
    }

    private JsonAppVo updatePacket() {
        if (mapJsonObject.get(AppTableNames.Q_DATA_PACKET.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.Q_DATA_PACKET.name());
        List<DataPacket> finalOldList = convertJavaList(DataPacket.class, AppTableNames.Q_DATA_PACKET.name());
        list.forEach(map -> {
            String uuid = "";
            if (finalOldList != null) {
                for (DataPacket oldMap : finalOldList) {
                    boolean samePacketId = map.get(SOURCE_ID).toString().equals(oldMap.getSourceId()) && osId.equals(oldMap.getOsId());
                    if (samePacketId) {
                        uuid = oldMap.getPacketId();
                        map.put(IS_VALID, oldMap.getIsValid());
                        break;
                    }
                }
                if (StringUtils.isBlank(uuid)) {
                    for (DataPacket oldMap : finalOldList) {
                        boolean findRepeatPacketId = map.get(PACKET_ID).toString().equals(oldMap.getPacketId())
                            && !osId.equals(oldMap.getOsId());
                        if (findRepeatPacketId) {
                            uuid = UuidOpt.getUuidAsString();
                            if (ConstantValue.TASK_TYPE_AGENT.equals(map.get(TASK_TYPE).toString())) {
                                map.put(IS_VALID, false);
                            }
                            break;
                        }
                    }
                }
            }
            if (StringUtils.isBlank(uuid)) {
                uuid = map.get(PACKET_ID).toString();
                if (ConstantValue.TASK_TYPE_AGENT.equals(map.get(TASK_TYPE).toString())) {
                    map.put(IS_VALID, false);
                }
            }
            dataPacketMap.put((String) map.get(PACKET_ID), uuid);
            map.put(PACKET_ID, uuid);
            map.put(OS_ID, osId);
            map.put(UPDATE_DATE, new Date());
            map.put(PUBLISH_DATE, new Date());
            map.put(RECORDER, userCode);
            map.put(OPT_CODE, uuid);
            optInfoMap.keySet().stream().filter(key -> key.equals(map.get(OPT_ID)))
                .findFirst().ifPresent(key -> map.put(OPT_ID, optInfoMap.get(key)));
        });
        list.forEach(map -> {
            String form = (String) map.get(DATA_OPT_DESC_JSON);
            for (String key : mdTableMap.keySet()) {
                form = StringUtils.replace(form, key, (String) mdTableMap.get(key));
            }
            for (String key : databaseMap.keySet()) {
                form = StringUtils.replace(form, key, (String) databaseMap.get(key));
            }
            for (String key : dataPacketMap.keySet()) {
                form = StringUtils.replace(form, key, (String) dataPacketMap.get(key));
            }
            for (String key : dictionaryMap.keySet()) {
                form = StringUtils.replace(form, key, (String) dictionaryMap.get(key));
            }
            for (String key : fileMap.keySet()) {
                form = StringUtils.replace(form, key, (String) fileMap.get(key));
            }
            map.put(DATA_OPT_DESC_JSON, form);
        });
        return this;
    }

    private JsonAppVo updateOptDefUsePacket() {
        if (mapJsonObject.get(AppTableNames.F_OPTDEF.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_OPTDEF.name());
        list.forEach(map -> {
            if (map.get(API_ID) != null) {
                dataPacketMap.keySet().stream().filter(key -> key.equals(map.get(API_ID)))
                    .findFirst().ifPresent(key -> map.put(API_ID, dataPacketMap.get(key)));
                map.put(OPT_URL, DDE_RUN + map.get(API_ID));
                map.put(OPT_CODE, map.get(API_ID));
            }
        });
        return this;
    }

    private JsonAppVo updatePacketParams() {
        if (mapJsonObject.get(AppTableNames.Q_DATA_PACKET_PARAM.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.Q_DATA_PACKET_PARAM.name());
        list.forEach(map -> dataPacketMap.keySet().stream().filter(key -> key.equals(map.get(PACKET_ID)))
            .findFirst().ifPresent(key -> map.put(PACKET_ID, dataPacketMap.get(key))));
        return this;
    }

    private JsonAppVo updateMetaForm() {
        if (mapJsonObject.get(AppTableNames.M_META_FORM_MODEL.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.M_META_FORM_MODEL.name());
        List<MetaFormModel> finalOldList = convertJavaList(MetaFormModel.class, AppTableNames.M_META_FORM_MODEL.name());
        list.forEach(map -> {
            String uuid = "";
            if (finalOldList != null) {
                for (MetaFormModel oldMap : finalOldList) {
                    if (map.get(SOURCE_ID).toString().equals(oldMap.getSourceId()) && osId.equals(oldMap.getOsId())) {
                        uuid = oldMap.getModelId();
                        break;
                    }
                }
                if (StringUtils.isBlank(uuid)) {
                    for (MetaFormModel oldMap : finalOldList) {
                        boolean findRepeatModelId = map.get(MODEL_ID).toString().equals(oldMap.getModelId())
                            && !osId.equals(oldMap.getOsId());
                        if (findRepeatModelId) {
                            uuid = UuidOpt.getUuidAsString();
                            break;
                        }
                    }
                }
            }
            if (StringUtils.isBlank(uuid)) {
                uuid = map.get(MODEL_ID).toString();
            }
            metaFormMap.put((String) map.get(MODEL_ID), uuid);
            map.put(MODEL_ID, uuid);
            map.put(OS_ID, osId);
            map.put(PUBLISH_DATE, new Date());
            map.put(RECORDER, userCode);
            optInfoMap.keySet().stream().filter(key -> key.equals(map.get(OPT_ID)))
                .findFirst().ifPresent(key -> map.put(OPT_ID, optInfoMap.get(key)));
        });
        list.forEach(map -> {
            String form;
            if (map.get(FORM_TEMPLATE) != null) {
                form = (String) map.get(FORM_TEMPLATE);
                for (String key : metaFormMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) metaFormMap.get(key));
                }
                for (String key : dataPacketMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) dataPacketMap.get(key));
                }
                for (String key : dictionaryMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) dictionaryMap.get(key));
                }
                for (String key : fileMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) fileMap.get(key));
                }
                map.put(FORM_TEMPLATE, form);
            }
            if (map.get(MOBILE_FORM_TEMPLATE) != null) {
                form = (String) map.get(MOBILE_FORM_TEMPLATE);
                for (String key : metaFormMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) metaFormMap.get(key));
                }
                for (String key : dataPacketMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) dataPacketMap.get(key));
                }
                for (String key : dictionaryMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) dictionaryMap.get(key));
                }
                for (String key : fileMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) fileMap.get(key));
                }
                map.put(MOBILE_FORM_TEMPLATE, form);
            }
            if (map.get(STRUCTURE_FUNCTION) != null) {
                form = (String) map.get(STRUCTURE_FUNCTION);
                for (String key : metaFormMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) metaFormMap.get(key));
                }
                for (String key : dataPacketMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) dataPacketMap.get(key));
                }
                for (String key : dictionaryMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) dictionaryMap.get(key));
                }
                for (String key : fileMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) fileMap.get(key));
                }
                map.put(STRUCTURE_FUNCTION, form);
            }
        });
        return this;
    }

    private JsonAppVo updateOptInfoUseMetaForm() {
        if (mapJsonObject.get(AppTableNames.F_OPTINFO.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_OPTINFO.name());
        list.forEach(map -> {
            metaFormMap.keySet().stream().filter(key -> key.equals(map.get(OPT_ROUTE)))
                .findFirst().ifPresent(key -> map.put(OPT_ROUTE, metaFormMap.get(key)));
            if (map.get(OPT_URL) != null) {
                String form = (String) map.get(OPT_URL);
                for (String key : metaFormMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) metaFormMap.get(key));
                }
                map.put(OPT_URL, form);
            }
        });
        return this;
    }

    private JsonAppVo updateWfDefineUseMetaFormWithPacket() {
        if (mapJsonObject.get(AppTableNames.WF_FLOW_DEFINE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.WF_FLOW_DEFINE.name());
        list.forEach(map -> {
            if (map.get(FLOW_XML_DESC) != null) {
                String form = (String) map.get(FLOW_XML_DESC);
                for (String key : metaFormMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) metaFormMap.get(key));
                }
                for (String key : dataPacketMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) dataPacketMap.get(key));
                }
                for (String key : optInfoMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) optInfoMap.get(key));
                }
                map.put(FLOW_XML_DESC, form);
            }
        });
        return this;
    }

    private JsonAppVo updateWfNodeUseMetaFormWithPacket() {
        if (mapJsonObject.get(AppTableNames.WF_NODE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.WF_NODE.name());
        list.forEach(map -> {
            dataPacketMap.keySet().stream().filter(key -> key.equals(map.get(OPT_CODE)))
                .findFirst().ifPresent(key -> map.put(OPT_CODE, dataPacketMap.get(key)));
            metaFormMap.keySet().stream().filter(key -> key.equals(map.get(OPT_CODE)))
                .findFirst().ifPresent(key -> map.put(OPT_CODE, metaFormMap.get(key)));
            optInfoMap.keySet().stream().filter(key -> key.equals(map.get(OPT_ID)))
                .findFirst().ifPresent(key -> map.put(OPT_ID, optInfoMap.get(key)));
        });
        return this;
    }

    private JsonAppVo updateWfOptTeamRole() {
        if (mapJsonObject.get(AppTableNames.WF_OPT_TEAM_ROLE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.WF_OPT_TEAM_ROLE.name());
        List<OptTeamRole> finalOldList = convertJavaList(OptTeamRole.class, AppTableNames.WF_OPT_TEAM_ROLE.name());
        list.forEach(map -> {
            optInfoMap.keySet().stream().filter(key -> key.equals(map.get(OPT_ID)))
                .findFirst().ifPresent(key -> map.put(OPT_ID, optInfoMap.get(key)));
            String uuid = "";
            if (finalOldList != null) {
                for (OptTeamRole oldMap : finalOldList) {
                    if (map.get(ROLE_CODE).toString().equals(oldMap.getRoleCode())
                        && map.get(OPT_ID).toString().equals(oldMap.getOptId())) {
                        uuid = oldMap.getOptTeamRoleId();
                        break;
                    }
                }
            }
            if (StringUtils.isBlank(uuid)) {
                uuid = UuidOpt.getUuidAsString();
            }
            map.put(OPT_TEAM_ROLE_ID, uuid);
            map.put(MODIFY_TIME, new Date());
        });
        return this;
    }

    private JsonAppVo updateWfOptVariable() {
        if (mapJsonObject.get(AppTableNames.WF_OPT_VARIABLE_DEFINE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.WF_OPT_VARIABLE_DEFINE.name());
        List<OptVariableDefine> finalOldList = convertJavaList(OptVariableDefine.class, AppTableNames.WF_OPT_VARIABLE_DEFINE.name());
        list.forEach(map -> {
            optInfoMap.keySet().stream().filter(key -> key.equals(map.get(OPT_ID)))
                .findFirst().ifPresent(key -> map.put(OPT_ID, optInfoMap.get(key)));
            String uuid = "";
            if (finalOldList != null) {
                for (OptVariableDefine oldMap : finalOldList) {
                    if (map.get(VARIABLE_NAME).toString().equals(oldMap.getVariableName())
                        && map.get(OPT_ID).toString().equals(oldMap.getOptId())) {
                        uuid = oldMap.getOptVariableId();
                        break;
                    }
                }
            }
            if (StringUtils.isBlank(uuid)) {
                uuid = UuidOpt.getUuidAsString();
            }
            map.put(OPT_VARIABLE_ID, uuid);
            map.put(MODIFY_TIME, new Date());
        });
        return this;
    }

    private JsonAppVo updateWfDefine() {
        if (mapJsonObject.get(AppTableNames.WF_FLOW_DEFINE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.WF_FLOW_DEFINE.name());
        list.sort((o1, o2) -> GeneralAlgorithm.compareTwoObject(o1.get(VERSION), o2.get(VERSION)));
        List<FlowInfo> finalOldList = convertJavaList(FlowInfo.class, AppTableNames.WF_FLOW_DEFINE.name());
        list.forEach(map -> {
            if (NumberBaseOpt.castObjectToInteger(map.get(VERSION), -1) == 0) {
                String uuid = "";
                if (finalOldList != null) {
                    for (FlowInfo oldMap : finalOldList) {
                        if (map.get(SOURCE_ID).toString().equals(oldMap.getSourceId()) && osId.equals(oldMap.getOsId())) {
                            uuid = oldMap.getFlowCode();
                            break;
                        }
                    }
                    if (StringUtils.isBlank(uuid)) {
                        for (FlowInfo oldMap : finalOldList) {
                            if (map.get(FLOW_CODE).toString().equals(oldMap.getFlowCode())
                                && !osId.equals(oldMap.getOsId())) {
                                uuid = UuidOpt.getUuidAsString();
                                break;
                            }
                        }
                    }
                }
                if (StringUtils.isBlank(uuid)) {
                    uuid = map.get(FLOW_CODE).toString();
                }
                flowDefineMap.put((String) map.get(FLOW_CODE), uuid);
            }
            map.put(FLOW_PUBLISH_DATE, new Date());
            optInfoMap.keySet().stream().filter(key -> key.equals(map.get(OPT_ID)))
                .findFirst().ifPresent(key -> map.put(OPT_ID, optInfoMap.get(key)));
            map.put(OS_ID, osId);
        });
        list.forEach(map ->
            flowDefineMap.keySet().stream().filter(key -> key.equals(map.get(FLOW_CODE)))
                .findFirst().ifPresent(key -> map.put(FLOW_CODE, flowDefineMap.get(key))));
        return this;
    }

    private JsonAppVo updateWfNode() {
        if (mapJsonObject.get(AppTableNames.WF_NODE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.WF_NODE.name());
        List<NodeInfo> finalOldList = convertJavaList(NodeInfo.class, AppTableNames.WF_NODE.name());
        list.forEach(map -> {
            String uuid = "";
            if (finalOldList != null) {
                for (NodeInfo oldMap : finalOldList) {
                    if (map.get(SOURCE_ID).toString().equals(oldMap.getSourceId()) && osId.equals(oldMap.getOsId())) {
                        uuid = oldMap.getNodeId();
                        break;
                    }
                }
                if (StringUtils.isBlank(uuid)) {
                    for (NodeInfo oldMap : finalOldList) {
                        if (map.get(NODE_ID).toString().equals(oldMap.getNodeId())
                            && !osId.equals(oldMap.getOsId())) {
                            uuid = UuidOpt.getUuidAsString();
                            break;
                        }
                    }
                }
            }
            if (StringUtils.isBlank(uuid)) {
                uuid = map.get(NODE_ID).toString();
            }
            wfNodeMap.put((String) map.get(NODE_ID), uuid);
            map.put(NODE_ID, uuid);
            map.put(OS_ID, osId);
            flowDefineMap.keySet().stream().filter(key -> key.equals(map.get(FLOW_CODE)))
                .findFirst().ifPresent(key -> map.put(FLOW_CODE, flowDefineMap.get(key)));
            metaFormMap.keySet().stream().filter(key -> key.equals(map.get(OPT_CODE)))
                .findFirst().ifPresent(key -> map.put(OPT_CODE, metaFormMap.get(key)));
        });
        return this;
    }

    private JsonAppVo updateWfDefineUseWfNode() {
        if (mapJsonObject.get(AppTableNames.WF_FLOW_DEFINE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.WF_FLOW_DEFINE.name());
        list.forEach(map -> wfNodeMap.keySet().stream().filter(key -> key.equals(map.get(FIRST_NODE_ID)))
            .findFirst().ifPresent(key -> map.put(FIRST_NODE_ID, wfNodeMap.get(key))));
        return this;
    }

    private JsonAppVo updateWfTransition() {
        if (mapJsonObject.get(AppTableNames.WF_TRANSITION.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.WF_TRANSITION.name());
        List<FlowTransition> finalOldList = convertJavaList(FlowTransition.class, AppTableNames.WF_TRANSITION.name());
        list.forEach(map -> {
            flowDefineMap.keySet().stream().filter(key -> key.equals(map.get(FLOW_CODE)))
                .findFirst().ifPresent(key -> map.put(FLOW_CODE, flowDefineMap.get(key)));
            wfNodeMap.keySet().stream().filter(key -> key.equals(map.get(START_NODE_ID)))
                .findFirst().ifPresent(key -> map.put(START_NODE_ID, wfNodeMap.get(key)));
            wfNodeMap.keySet().stream().filter(key -> key.equals(map.get(END_NODE_ID)))
                .findFirst().ifPresent(key -> map.put(END_NODE_ID, wfNodeMap.get(key)));
            String uuid = "";
            if (finalOldList != null) {
                for (FlowTransition oldMap : finalOldList) {
                    if (map.get(FLOW_CODE).toString().equals(oldMap.getFlowCode())
                        && map.get(START_NODE_ID).toString().equals(oldMap.getStartNodeId())
                        && map.get(END_NODE_ID).toString().equals(oldMap.getEndNodeId())) {
                        uuid = oldMap.getTransId();
                        break;
                    }
                }
            }
            if (StringUtils.isBlank(uuid)) {
                uuid = UuidOpt.getUuidAsString();
            }
            map.put(TRANS_ID, uuid);
        });
        return this;
    }

    private JsonAppVo updatePacketUseWfDefine() {
        if (mapJsonObject.get(AppTableNames.Q_DATA_PACKET.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.Q_DATA_PACKET.name());
        list.forEach(map -> {
            String form = (String) map.get(DATA_OPT_DESC_JSON);
            for (String key : flowDefineMap.keySet()) {
                form = StringUtils.replace(form, key, (String) flowDefineMap.get(key));
            }
            map.put(DATA_OPT_DESC_JSON, form);
        });
        return this;
    }

    private JsonAppVo updateMetaFormUseWfDefine() {
        if (mapJsonObject.get(AppTableNames.M_META_FORM_MODEL.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.M_META_FORM_MODEL.name());
        list.forEach(map -> {
            String form;
            if (map.get(FORM_TEMPLATE) != null) {
                form = (String) map.get(FORM_TEMPLATE);
                for (String key : flowDefineMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) flowDefineMap.get(key));
                }
                map.put(FORM_TEMPLATE, form);
            }
            if (map.get(MOBILE_FORM_TEMPLATE) != null) {
                form = (String) map.get(MOBILE_FORM_TEMPLATE);
                for (String key : flowDefineMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) flowDefineMap.get(key));
                }
                map.put(MOBILE_FORM_TEMPLATE, form);
            }
            if (map.get(STRUCTURE_FUNCTION) != null) {
                form = (String) map.get(STRUCTURE_FUNCTION);
                for (String key : flowDefineMap.keySet()) {
                    form = StringUtils.replace(form, key, (String) flowDefineMap.get(key));
                }
                map.put(STRUCTURE_FUNCTION, form);
            }
        });
        return this;
    }

    private <T> List<T> convertJavaList(Class<T> type, String className) {
        if (notHaveOldData(className)) {
            return null;
        }
        return oldAppObject.getJSONArray(className).toJavaList(type);
    }

    private boolean notHaveOldData(String dataName) {
        return oldAppObject.get(dataName) == null || oldAppObject.getJSONArray(dataName).size() == 0;
    }

    private JsonAppVo createOsInfo() {
        if (mapJsonObject.get(AppTableNames.F_OS_INFO.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_OS_INFO.name());
        List<OsInfo> oldList = convertJavaList(OsInfo.class, AppTableNames.F_OS_INFO.name());
        if (oldList == null) {
            list.forEach(map -> map.put(TOP_UNIT, topUnit));
            appList.addAll(convertMap(OsInfo.class, list));
            WorkGroup teamUser = assembleWorkGroup((String) list.get(0).get(OS_ID));
            appList.add(teamUser);
        } else {
            appList.addAll(convertMap(OsInfo.class, list));
        }
        return this;
    }

    private WorkGroup assembleWorkGroup(String osId) {
        WorkGroup teamUser = new WorkGroup();
        WorkGroupParameter workGroupParameter = new WorkGroupParameter();
        workGroupParameter.setUserCode(userCode);
        workGroupParameter.setGroupId(osId);
        workGroupParameter.setRoleCode("组长");//WorkGroup.WORKGROUP_ROLE_CODE_LEADER);
        teamUser.setWorkGroupParameter(workGroupParameter);
        teamUser.setCreator(userCode);
        return teamUser;
    }

    private JsonAppVo createLibraryInfo() {
        if (mapJsonObject.get(AppTableNames.FILE_LIBRARY_INFO.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.FILE_LIBRARY_INFO.name());
        List<FileLibraryInfo> oldList = convertJavaList(FileLibraryInfo.class, AppTableNames.FILE_LIBRARY_INFO.name());
        if (oldList == null) {
            appList.addAll(convertMap(FileLibraryInfo.class, list));
        }
        return this;
    }

    private JsonAppVo createApplicationResource() {
        if (mapJsonObject.get(AppTableNames.M_APPLICATION_RESOURCES.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.M_APPLICATION_RESOURCES.name());
        appList.addAll(convertMap(ApplicationResources.class, list));
        return this;
    }

    private JsonAppVo createDataCatalog() {
        if (mapJsonObject.get(AppTableNames.F_DATACATALOG.name()) == null || !this.runDictionary) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_DATACATALOG.name());
        appList.addAll(convertMap(DataCatalog.class, list));
        return this;
    }

    private JsonAppVo createDataDictionary() {
        if (mapJsonObject.get(AppTableNames.F_DATADICTIONARY.name()) == null || !this.runDictionary) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_DATADICTIONARY.name());
        appList.addAll(convertMap(DataDictionary.class, list));
        return this;
    }

    private JsonAppVo createApplicationDictionary() {
        if (mapJsonObject.get(AppTableNames.M_APPLICATION_DICTIONARY.name()) == null || !this.runDictionary) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.M_APPLICATION_DICTIONARY.name());
        appList.addAll(convertMap(ApplicationDictionary.class, list));
        return this;
    }

    private JsonAppVo createMdTableWithColumnObject() {
        if (mapJsonObject.get(AppTableNames.F_MD_TABLE.name()) == null || !this.runMetaData) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_MD_TABLE.name());
        metaObject.addAll(convertMap(MetaTable.class, list));
        List<Object> objectList = convertMap(PendingMetaTable.class, list);
        objectList.forEach(map -> {
            if (TABLE_TYPE.equals(((PendingMetaTable) map).getTableType())) {
                ((PendingMetaTable) map).setTableState(NO_PUBLISH);
                appList.add(map);
            }
        });
        if (mapJsonObject.get(AppTableNames.F_MD_COLUMN.name()) == null) {
            return this;
        }
        list = mapJsonObject.get(AppTableNames.F_MD_COLUMN.name());
        metaObject.addAll(convertMap(MetaColumn.class, list));
        list.forEach(map -> map.put(MAX_LENGTH, map.get(COLUMN_LENGTH)));
        appList.addAll(convertMap(PendingMetaColumn.class, list));
        return this;
    }

    private JsonAppVo createMdRelationWithDetailObject() {
        if (mapJsonObject.get(AppTableNames.F_MD_RELATION.name()) == null || !this.runMetaData) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_MD_RELATION.name());
        metaObject.addAll(convertMap(MetaRelation.class, list));
        if (mapJsonObject.get(AppTableNames.F_MD_REL_DETAIL.name()) == null) {
            return this;
        }
        list = mapJsonObject.get(AppTableNames.F_MD_REL_DETAIL.name());
        appList.addAll(convertMap(MetaRelDetail.class, list));
        return this;
    }

    private JsonAppVo createMetaFormObject() {
        if (mapJsonObject.get(AppTableNames.M_META_FORM_MODEL.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.M_META_FORM_MODEL.name());
        appList.addAll(convertMap(MetaFormModel.class, list));
        appList.addAll(convertMap(MetaFormModelDraft.class, list));
        return this;
    }

    private JsonAppVo createDataPacketAndParamsObject() {
        if (mapJsonObject.get(AppTableNames.Q_DATA_PACKET.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.Q_DATA_PACKET.name());
        appList.addAll(convertMap(DataPacket.class, list));
        appList.addAll(convertMap(DataPacketDraft.class, list));
        if (mapJsonObject.get(AppTableNames.Q_DATA_PACKET_PARAM.name()) == null) {
            return this;
        }
        list = mapJsonObject.get(AppTableNames.Q_DATA_PACKET_PARAM.name());
        appList.addAll(convertMap(DataPacketParam.class, list));
        appList.addAll(convertMap(DataPacketParamDraft.class, list));
        return this;
    }


    private JsonAppVo createWfOptTeamRole() {
        if (mapJsonObject.get(AppTableNames.WF_OPT_TEAM_ROLE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.WF_OPT_TEAM_ROLE.name());
        appList.addAll(convertMap(OptTeamRole.class, list));
        return this;
    }

    private JsonAppVo createWfOptVariable() {
        if (mapJsonObject.get(AppTableNames.WF_OPT_VARIABLE_DEFINE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.WF_OPT_VARIABLE_DEFINE.name());
        appList.addAll(convertMap(OptVariableDefine.class, list));
        return this;
    }

    private JsonAppVo createOptInfo() {
        if (mapJsonObject.get(AppTableNames.F_OPTINFO.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_OPTINFO.name());
        appList.addAll(convertMap(OptInfo.class, list));
        return this;
    }

    private JsonAppVo createOptDef() {
        if (mapJsonObject.get(AppTableNames.F_OPTDEF.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_OPTDEF.name());
        appList.addAll(convertMap(OptMethod.class, list));
        return this;
    }

    private JsonAppVo createWfDefine() {
        if (mapJsonObject.get(AppTableNames.WF_FLOW_DEFINE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.WF_FLOW_DEFINE.name());
        appList.addAll(convertMap(FlowInfo.class, list));
        return this;
    }

    private JsonAppVo createWfNode() {
        if (mapJsonObject.get(AppTableNames.WF_NODE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.WF_NODE.name());
        appList.addAll(convertMap(NodeInfo.class, list));
        return this;
    }

    private JsonAppVo createWfTransition() {
        if (mapJsonObject.get(AppTableNames.WF_TRANSITION.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.WF_TRANSITION.name());
        appList.addAll(convertMap(FlowTransition.class, list));
        return this;
    }

    private JsonAppVo createTableRelation() {
        if (mapJsonObject.get(AppTableNames.F_TABLE_OPT_RELATION.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(AppTableNames.F_TABLE_OPT_RELATION.name());
        appList.addAll(convertMap(MetaOptRelation.class, list));
        return this;
    }

    private List<Object> convertMap(Class type, List<Map<String, Object>> list) {
        List<Object> object = new ArrayList<>();
        try {
            JavaBeanMetaData javaBeanMetaData = JavaBeanMetaData.createBeanMetaDataFromType(type);
            for (Map<String, Object> map : list) {
                object.add(javaBeanMetaData.createBeanObjectFromMap(map));
            }
            return object;
        } catch (Exception e) {
            return object;
        }
    }

}

