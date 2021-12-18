package com.centit.platform.vo;

import com.alibaba.fastjson.JSONObject;
import com.centit.dde.core.SimpleDataSet;
import com.centit.dde.po.DataPacket;
import com.centit.dde.po.DataPacketDraft;
import com.centit.dde.po.DataPacketParam;
import com.centit.dde.po.DataPacketParamDraft;
import com.centit.fileserver.common.FileLibrary;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.system.po.OptInfo;
import com.centit.framework.system.po.OptMethod;
import com.centit.framework.system.po.OsInfo;
import com.centit.metaform.dubbo.adapter.po.MetaFormModel;
import com.centit.metaform.dubbo.adapter.po.MetaFormModelDraft;
import com.centit.product.adapter.po.WorkGroup;
import com.centit.product.adapter.po.WorkGroupParameter;
import com.centit.product.dbdesign.po.PendingMetaColumn;
import com.centit.product.dbdesign.po.PendingMetaTable;
import com.centit.product.metadata.po.*;
import com.centit.support.algorithm.GeneralAlgorithm;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.algorithm.UuidOpt;
import com.centit.support.common.JavaBeanMetaData;
import com.centit.workflow.po.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author zhf
 */

public class JsonAppVo {
    private static final String OS_ID = "osId";
    private static final String DATABASE_CODE = "databaseCode";
    private static final String TABLE_ID = "tableId";
    private static final String RELATION_ID = "relationId";
    private static final String PARENT_TABLE_ID = "parentTableId";
    private static final String OPT_ID = "optId";
    private static final String ID = "id";
    private static final String PACKET_ID = "packetId";
    private static final String DATA_OPT_DESC_JSON = "dataOptDescJson";
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
    private static final String WORKGROUP_ROLE_CODE_LEADER = "组长";
    private static final String TABLE_STATE = "tableState";
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
    private static final String MAP_DATA_CODE = "mapDataCode";


    private Boolean coverApp;
    private Map<String, List<Map<String, Object>>> mapJsonObject = new HashMap<>();
    @Getter
    private String userCode;
    private String topUnit;
    private String osId;
    @Getter
    private List<Object> appList = new ArrayList<>();
    @Getter
    private List<Object> metaObject = new ArrayList<>();
    @Getter
    private List<String> listDatabaseName = new ArrayList<>();

    private Map<String, Object> databaseMap = new HashMap<>();
    private Map<String, Object> mdTableMap = new HashMap<>();
    private Map<String, Object> relationMap = new HashMap<>();
    private Map<String, Object> dataPacketMap = new HashMap<>();
    private Map<String, Object> metaFormMap = new HashMap<>();
    private Map<String, Object> flowDefineMap = new HashMap<>();
    private Map<String, Object> optInfoMap = new HashMap<>();
    private Map<String, Object> optDefMap = new HashMap<>();
    private Map<String, Object> wfNodeMap = new HashMap<>();

    public JsonAppVo(JSONObject jsonObject, String cover, CentitUserDetails userDetails) {
        createMapJsonObject(jsonObject);
        this.coverApp = "T".equals(cover);
        this.userCode = userDetails.getUserCode();
        this.topUnit = userDetails.getTopUnitCode();
    }

    private void createMapJsonObject(JSONObject jsonObject) {
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            mapJsonObject.put(entry.getKey(),
                new ObjectMapper().convertValue(entry.getValue(), SimpleDataSet.class).getDataAsList());
        }
    }

    public void prepareApp() {
        if (!coverApp) {
            updatePrimary();
        }
        createAppObject();
        setDatabaseName();
    }

    private void updatePrimary() {
        this.updateOsInfo().updateLibraryInfo().updateDatabase().updateMdTableWithColumn()
            .updateMdRelationWithDetail()
            .updateOptInfo().updateOptDef().updateTableRelation().updatePacketWithParams()
            .updateMetaForm().updatePacketByMetaFormMap()
            .updateWfOptTeamRole().updateWfOptVariable()
            .updateWfDefine().updateWfNode().updateWfTransition();
    }

    private void createAppObject() {
        this.createOsInfo().createLibraryInfo()
            .createMdTableWithColumnObject().createMdRelationWithDetailObject()
            .createMetaFormObject().createDataPacketAndParamsObject()
            .createWfOptTeamRole().createWfOptVariable().createOptInfo().createOptDef()
            .createWfDefine().createWfNode().createWfTransition().createTableRelation();
    }

    private void setDatabaseName() {
        if (mapJsonObject.get(TableName.F_DATABASE_INFO.name()) == null) {
            return;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.F_DATABASE_INFO.name());
        list.stream().map(s -> (String) s.get(DATABASE_CODE)).forEach(listDatabaseName::add);
    }

    private JsonAppVo updateOsInfo() {
        if (mapJsonObject.get(TableName.F_OS_INFO.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.F_OS_INFO.name());
        osId = UuidOpt.getUuidAsString22();
        list.forEach(map -> {
            map.put(OS_ID, osId);
            map.put(REL_OPT_ID, osId);
            map.put(CREATED, userCode);
            map.put(CREATE_TIME, new Date());
            map.put(LAST_MODIFY_DATE, new Date());
        });
        return this;
    }

    private JsonAppVo updateLibraryInfo() {
        if (mapJsonObject.get(TableName.FILE_LIBRARY_INFO.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.FILE_LIBRARY_INFO.name());
        list.forEach(map -> {
            map.put(LIBRARY_ID, osId);
            map.put(CREATE_USER, userCode);
            map.put(OWN_USER, userCode);
            map.put(CREATE_TIME, new Date());
            map.put(UPDATE_TIME, new Date());
        });
        return this;
    }

    private JsonAppVo updateDatabase() {
        if (mapJsonObject.get(TableName.F_DATABASE_INFO.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.F_DATABASE_INFO.name());
        list.forEach(map -> {
            String uuid;
            if (map.get(MAP_DATA_CODE) != null && !StringBaseOpt.isNvl((String) map.get(MAP_DATA_CODE))) {
                uuid = (String) map.get(MAP_DATA_CODE);
            } else {
                uuid = UuidOpt.getUuidAsString22();
            }
            databaseMap.put((String) map.get(DATABASE_CODE), uuid);
            map.put(DATABASE_CODE, uuid);
            map.put(OS_ID, osId);
            map.put(CREATED, userCode);
            map.put(CREATE_TIME, new Date());
            map.put(LAST_MODIFY_DATE, new Date());
        });
        return this;
    }

    private JsonAppVo updateMdTableWithColumn() {
        if (mapJsonObject.get(TableName.F_MD_TABLE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.F_MD_TABLE.name());
        list.forEach(map -> {
            String uuid = UuidOpt.getUuidAsString32();
            mdTableMap.put((String) map.get(TABLE_ID), uuid);
            map.put(TABLE_ID, uuid);
            map.put(RECORDER, userCode);
            map.put(RECORD_DATE, new Date());
            databaseMap.keySet().stream().filter(key -> key.equals(map.get(DATABASE_CODE)))
                .findFirst().ifPresent(key -> map.put(DATABASE_CODE, databaseMap.get(key)));
        });
        if (mapJsonObject.get(TableName.F_MD_COLUMN.name()) == null) {
            return this;
        }
        list = mapJsonObject.get(TableName.F_MD_COLUMN.name());
        list.forEach(map -> {
            map.put(RECORDER, userCode);
            map.put(LAST_MODIFY_DATE, new Date());
            mdTableMap.keySet().stream().filter(key -> key.equals(map.get(TABLE_ID)))
                .findFirst().ifPresent(key -> map.put(TABLE_ID, mdTableMap.get(key)));
        });
        return this;
    }

    private JsonAppVo updateMdRelationWithDetail() {
        if (mapJsonObject.get(TableName.F_MD_RELATION.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.F_MD_RELATION.name());
        list.forEach(map -> {
            String uuid = UuidOpt.getUuidAsString22();
            relationMap.put((String) map.get(RELATION_ID), uuid);
            map.put(RELATION_ID, uuid);
            map.put(RECORDER, userCode);
            map.put(LAST_MODIFY_DATE, new Date());
            mdTableMap.keySet().stream().filter(key -> key.equals(map.get(PARENT_TABLE_ID)))
                .findFirst().ifPresent(key -> map.put(PARENT_TABLE_ID, mdTableMap.get(key)));
            mdTableMap.keySet().stream().filter(key -> key.equals(map.get(CHILD_TABLE_ID)))
                .findFirst().ifPresent(key -> map.put(CHILD_TABLE_ID, mdTableMap.get(key)));
        });
        if (mapJsonObject.get(TableName.F_MD_REL_DETAIL.name()) == null) {
            return this;
        }
        list = mapJsonObject.get(TableName.F_MD_REL_DETAIL.name());
        list.forEach(map -> relationMap.keySet().stream().filter(key -> key.equals(map.get(RELATION_ID)))
            .findFirst().ifPresent(key -> map.put(RELATION_ID, relationMap.get(key))));
        return this;
    }

    private JsonAppVo updateOptInfo() {
        if (mapJsonObject.get(TableName.F_OPTINFO.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.F_OPTINFO.name());
        list.forEach(map -> {
            String uuid;
            if (map.get(OPT_ID).equals(map.get(TOP_OPT_ID))) {
                uuid = osId;
            } else {
                uuid = UuidOpt.getUuidAsString22();
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
        if (mapJsonObject.get(TableName.F_OPTDEF.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.F_OPTDEF.name());
        list.forEach(map -> {
            String uuid = UuidOpt.getUuidAsString22();
            optDefMap.put((String) map.get(OPT_CODE), uuid);
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
        if (mapJsonObject.get(TableName.F_TABLE_OPT_RELATION.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.F_TABLE_OPT_RELATION.name());
        list.forEach(map -> {
            String uuid = UuidOpt.getUuidAsString22();
            map.put(ID, uuid);
            map.put(OS_ID, osId);
            mdTableMap.keySet().stream().filter(key -> key.equals(map.get(TABLE_ID)))
                .findFirst().ifPresent(key -> map.put(TABLE_ID, mdTableMap.get(key)));
            optInfoMap.keySet().stream().filter(key -> key.equals(map.get(OPT_ID)))
                .findFirst().ifPresent(key -> map.put(OPT_ID, optInfoMap.get(key)));
        });
        return this;
    }

    private JsonAppVo updatePacketWithParams() {
        if (mapJsonObject.get(TableName.Q_DATA_PACKET.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.Q_DATA_PACKET.name());
        list.forEach(map -> {
            String uuid = UuidOpt.getUuidAsString22();
            dataPacketMap.put((String) map.get(PACKET_ID), uuid);
            map.put(PACKET_ID, uuid);
            map.put(OS_ID, osId);
            map.put(RECORD_DATE, new Date());
            map.put(UPDATE_DATE, new Date());
            map.put(PUBLISH_DATE, new Date());
            map.put(RECORDER, userCode);
            optInfoMap.keySet().stream().filter(key -> key.equals(map.get(OPT_ID)))
                .findFirst().ifPresent(key -> map.put(OPT_ID, optInfoMap.get(key)));
            optDefMap.keySet().stream().filter(key -> key.equals(map.get(OPT_CODE)))
                .findFirst().ifPresent(key -> map.put(OPT_CODE, optDefMap.get(key)));
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
            map.put(DATA_OPT_DESC_JSON, form);
        });
        if (mapJsonObject.get(TableName.Q_DATA_PACKET_PARAM.name()) == null) {
            return this;
        }
        list = mapJsonObject.get(TableName.Q_DATA_PACKET_PARAM.name());
        list.forEach(map -> dataPacketMap.keySet().stream().filter(key -> key.equals(map.get(PACKET_ID)))
            .findFirst().ifPresent(key -> map.put(PACKET_ID, dataPacketMap.get(key))));
        return this;
    }

    private JsonAppVo updateMetaForm() {
        if (mapJsonObject.get(TableName.M_META_FORM_MODEL.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.M_META_FORM_MODEL.name());
        list.forEach(map -> {
            String uuid = UuidOpt.getUuidAsString36();
            metaFormMap.put((String) map.get(MODEL_ID), uuid);
            map.put(MODEL_ID, uuid);
            map.put(OS_ID, osId);
            map.put(LAST_MODIFY_DATE, new Date());
            map.put(PUBLISH_DATE, new Date());
            map.put(RECORDER, userCode);
            mdTableMap.keySet().stream().filter(key -> key.equals(map.get(TABLE_ID)))
                .findFirst().ifPresent(key -> map.put(TABLE_ID, mdTableMap.get(key)));
            databaseMap.keySet().stream().filter(key -> key.equals(map.get(DATABASE_CODE)))
                .findFirst().ifPresent(key -> map.put(DATABASE_CODE, databaseMap.get(key)));
            optInfoMap.keySet().stream().filter(key -> key.equals(map.get(OPT_ID)))
                .findFirst().ifPresent(key -> map.put(OPT_ID, optInfoMap.get(key)));
        });
        list.forEach(map -> {
            String form = (String) map.get(FORM_TEMPLATE);
            for (String key : metaFormMap.keySet()) {
                form = StringUtils.replace(form, key, (String) metaFormMap.get(key));
            }
            for (String key : mdTableMap.keySet()) {
                form = StringUtils.replace(form, key, (String) mdTableMap.get(key));
            }
            for (String key : databaseMap.keySet()) {
                form = StringUtils.replace(form, key, (String) databaseMap.get(key));
            }
            for (String key : dataPacketMap.keySet()) {
                form = StringUtils.replace(form, key, (String) dataPacketMap.get(key));
            }
            map.put(FORM_TEMPLATE, form);
        });
        return this;
    }

    private JsonAppVo updatePacketByMetaFormMap() {
        if (mapJsonObject.get(TableName.Q_DATA_PACKET.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list =
            mapJsonObject.get(TableName.Q_DATA_PACKET.name());
        list.forEach(map -> {
            String form = (String) map.get(DATA_OPT_DESC_JSON);
            for (String key : metaFormMap.keySet()) {
                form = StringUtils.replace(form, key, (String) metaFormMap.get(key));
            }
            map.put(DATA_OPT_DESC_JSON, form);
        });
        return this;
    }


    private JsonAppVo updateWfOptTeamRole() {
        if (mapJsonObject.get(TableName.WF_OPT_TEAM_ROLE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.WF_OPT_TEAM_ROLE.name());
        list.forEach(map -> {
            String uuid = UuidOpt.getUuidAsString22();
            map.put(OPT_TEAM_ROLE_ID, uuid);
            map.put(MODIFY_TIME, new Date());
            optInfoMap.keySet().stream().filter(key -> key.equals(map.get(OPT_ID)))
                .findFirst().ifPresent(key -> map.put(OPT_ID, optInfoMap.get(key)));
        });
        return this;
    }

    private JsonAppVo updateWfOptVariable() {
        if (mapJsonObject.get(TableName.WF_OPT_VARIABLE_DEFINE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.WF_OPT_VARIABLE_DEFINE.name());
        list.forEach(map -> {
            String uuid = UuidOpt.getUuidAsString22();
            map.put(OPT_VARIABLE_ID, uuid);
            map.put(MODIFY_TIME, new Date());
            optInfoMap.keySet().stream().filter(key -> key.equals(map.get(OPT_ID)))
                .findFirst().ifPresent(key -> map.put(OPT_ID, optInfoMap.get(key)));
        });
        return this;
    }

    private JsonAppVo updateWfDefine() {
        if (mapJsonObject.get(TableName.WF_FLOW_DEFINE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.WF_FLOW_DEFINE.name());
        list.sort((o1, o2) -> GeneralAlgorithm.compareTwoObject(o1.get(VERSION), o2.get(VERSION)));
        list.forEach(map -> {
            if (map.get(VERSION).equals(0)) {
                String uuid = UuidOpt.getUuidAsString22();
                flowDefineMap.put((String) map.get(FLOW_CODE), uuid);
                map.put(FLOW_CODE, uuid);
            } else {
                flowDefineMap.keySet().stream().filter(key -> key.equals(map.get(FLOW_CODE)))
                    .findFirst().ifPresent(key -> map.put(FLOW_CODE, flowDefineMap.get(key)));
            }
            map.put(FLOW_PUBLISH_DATE, new Date());
            optInfoMap.keySet().stream().filter(key -> key.equals(map.get(OPT_ID)))
                .findFirst().ifPresent(key -> map.put(OPT_ID, optInfoMap.get(key)));
            map.put(OS_ID, osId);
        });
        return this;
    }

    private JsonAppVo updateWfNode() {
        if (mapJsonObject.get(TableName.WF_NODE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.WF_NODE.name());
        list.forEach(map -> {
            String uuid = UuidOpt.getUuidAsString22();
            wfNodeMap.put((String) map.get(NODE_ID), uuid);
            map.put(NODE_ID, uuid);
            map.put(OS_ID, osId);
            flowDefineMap.keySet().stream().filter(key -> key.equals(map.get(FLOW_CODE)))
                .findFirst().ifPresent(key -> map.put(FLOW_CODE, flowDefineMap.get(key)));
            optDefMap.keySet().stream().filter(key -> key.equals(map.get(OPT_CODE)))
                .findFirst().ifPresent(key -> map.put(OPT_CODE, optDefMap.get(key)));
        });
        return this;
    }

    private JsonAppVo updateWfTransition() {
        if (mapJsonObject.get(TableName.WF_TRANSITION.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.WF_TRANSITION.name());
        list.forEach(map -> {
            String uuid = UuidOpt.getUuidAsString22();
            map.put(TRANS_ID, uuid);
            flowDefineMap.keySet().stream().filter(key -> key.equals(map.get(FLOW_CODE)))
                .findFirst().ifPresent(key -> map.put(FLOW_CODE, flowDefineMap.get(key)));
            wfNodeMap.keySet().stream().filter(key -> key.equals(map.get(START_NODE_ID)))
                .findFirst().ifPresent(key -> map.put(START_NODE_ID, wfNodeMap.get(key)));
            wfNodeMap.keySet().stream().filter(key -> key.equals(map.get(END_NODE_ID)))
                .findFirst().ifPresent(key -> map.put(END_NODE_ID, wfNodeMap.get(key)));
        });
        return this;
    }

    private JsonAppVo createOsInfo() {
        if (mapJsonObject.get(TableName.F_OS_INFO.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.F_OS_INFO.name());
        if (!coverApp) {
            list.forEach(map -> map.put(TOP_UNIT, topUnit));
            appList.addAll(convertMap(OsInfo.class, list));
            WorkGroup teamUser = getWorkGroup((String) list.get(0).get(OS_ID));
            appList.add(teamUser);
        }
        return this;
    }

    private WorkGroup getWorkGroup(String osId) {
        WorkGroup teamUser = new WorkGroup();
        WorkGroupParameter workGroupParameter = new WorkGroupParameter();
        workGroupParameter.setUserCode(userCode);
        workGroupParameter.setGroupId(osId);
        workGroupParameter.setRoleCode(WORKGROUP_ROLE_CODE_LEADER);
        teamUser.setWorkGroupParameter(workGroupParameter);
        teamUser.setCreator(userCode);
        return teamUser;
    }

    private JsonAppVo createLibraryInfo() {
        if (mapJsonObject.get(TableName.FILE_LIBRARY_INFO.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.FILE_LIBRARY_INFO.name());
        if (!coverApp) {
            appList.addAll(convertMap(FileLibrary.class, list));
        }
        return this;
    }

    private JsonAppVo createMdTableWithColumnObject() {
        if (mapJsonObject.get(TableName.F_MD_TABLE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.F_MD_TABLE.name());
        metaObject.addAll(convertMap(MetaTable.class, list));
        List<Object> objectList = convertMap(PendingMetaTable.class, list);
        objectList.forEach(map -> {
            if (TABLE_TYPE.equals(((PendingMetaTable) map).getTableType())) {
                ((PendingMetaTable) map).setTableState(NO_PUBLISH);
                appList.add(map);
            }
        });
        if (mapJsonObject.get(TableName.F_MD_COLUMN.name()) == null) {
            return this;
        }
        list = mapJsonObject.get(TableName.F_MD_COLUMN.name());
        metaObject.addAll(convertMap(MetaColumn.class, list));
        list.forEach(map -> map.put(MAX_LENGTH, map.get(COLUMN_LENGTH)));
        appList.addAll(convertMap(PendingMetaColumn.class, list));
        return this;
    }

    private JsonAppVo createMdRelationWithDetailObject() {
        if (mapJsonObject.get(TableName.F_MD_RELATION.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.F_MD_RELATION.name());
        metaObject.addAll(convertMap(MetaRelation.class, list));
        if (mapJsonObject.get(TableName.F_MD_REL_DETAIL.name()) == null) {
            return this;
        }
        list = mapJsonObject.get(TableName.F_MD_REL_DETAIL.name());
        appList.addAll(convertMap(MetaRelDetail.class, list));
        return this;
    }

    private JsonAppVo createMetaFormObject() {
        if (mapJsonObject.get(TableName.M_META_FORM_MODEL.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.M_META_FORM_MODEL.name());
        appList.addAll(convertMap(MetaFormModel.class, list));
        appList.addAll(convertMap(MetaFormModelDraft.class, list));
        return this;
    }

    private JsonAppVo createDataPacketAndParamsObject() {
        if (mapJsonObject.get(TableName.Q_DATA_PACKET.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.Q_DATA_PACKET.name());
        appList.addAll(convertMap(DataPacket.class, list));
        appList.addAll(convertMap(DataPacketDraft.class, list));
        if (mapJsonObject.get(TableName.Q_DATA_PACKET_PARAM.name()) == null) {
            return this;
        }
        list = mapJsonObject.get(TableName.Q_DATA_PACKET_PARAM.name());
        appList.addAll(convertMap(DataPacketParam.class, list));
        appList.addAll(convertMap(DataPacketParamDraft.class, list));
        return this;
    }


    private JsonAppVo createWfOptTeamRole() {
        if (mapJsonObject.get(TableName.WF_OPT_TEAM_ROLE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.WF_OPT_TEAM_ROLE.name());
        appList.addAll(convertMap(OptTeamRole.class, list));
        return this;
    }

    private JsonAppVo createWfOptVariable() {
        if (mapJsonObject.get(TableName.WF_OPT_VARIABLE_DEFINE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.WF_OPT_VARIABLE_DEFINE.name());
        appList.addAll(convertMap(OptVariableDefine.class, list));
        return this;
    }

    private JsonAppVo createOptInfo() {
        if (mapJsonObject.get(TableName.F_OPTINFO.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.F_OPTINFO.name());
        appList.addAll(convertMap(OptInfo.class, list));
        return this;
    }

    private JsonAppVo createOptDef() {
        if (mapJsonObject.get(TableName.F_OPTDEF.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.F_OPTDEF.name());
        appList.addAll(convertMap(OptMethod.class, list));
        return this;
    }

    private JsonAppVo createWfDefine() {
        if (mapJsonObject.get(TableName.WF_FLOW_DEFINE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.WF_FLOW_DEFINE.name());
        appList.addAll(convertMap(FlowInfo.class, list));
        return this;
    }

    private JsonAppVo createWfNode() {
        if (mapJsonObject.get(TableName.WF_NODE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.WF_NODE.name());
        appList.addAll(convertMap(NodeInfo.class, list));
        return this;
    }

    private JsonAppVo createWfTransition() {
        if (mapJsonObject.get(TableName.WF_TRANSITION.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.WF_TRANSITION.name());
        appList.addAll(convertMap(FlowTransition.class, list));
        return this;
    }

    private JsonAppVo createTableRelation() {
        if (mapJsonObject.get(TableName.F_TABLE_OPT_RELATION.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.F_TABLE_OPT_RELATION.name());
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

