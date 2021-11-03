package com.centit.platform.Vo;

import com.alibaba.fastjson.JSONObject;
import com.centit.dde.core.SimpleDataSet;
import com.centit.dde.po.DataPacket;
import com.centit.dde.po.DataPacketParam;
import com.centit.framework.model.basedata.IOsInfo;
import com.centit.metaform.po.MetaFormModel;
import com.centit.product.dbdesign.po.PendingMetaColumn;
import com.centit.product.dbdesign.po.PendingMetaTable;
import com.centit.product.metadata.po.MetaColumn;
import com.centit.product.metadata.po.MetaRelation;
import com.centit.product.metadata.po.MetaTable;
import com.centit.product.metadata.po.SourceInfo;
import com.centit.support.algorithm.GeneralAlgorithm;
import com.centit.support.algorithm.UuidOpt;
import com.centit.support.common.JavaBeanMetaData;
import com.centit.workflow.po.*;
import com.centit.workflow.service.FlowDefine;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhf
 */

public class JsonAppVo {
    private JSONObject jsonObject;
    private Boolean isCover;
    private Map<String, List<Map<String, Object>>> mapJsonObject = new HashMap<>();
    @Getter
    private String userCode;
    private String applicationId;
    @Getter
    private List<Object> object = new ArrayList<>();
    @Getter
    private List<Object> metaObject = new ArrayList<>();
    @Getter
    private List<String> listDatabaseName = new ArrayList<>();

    private Map<String, Object> databaseMap = new HashMap<>();
    private Map<String, Object> mdTableMap = new HashMap<>();
    private Map<String, Object> relationMap = new HashMap<>();
    private Map<String, Object> dataPacketMap = new HashMap<>();
    private Map<String, Object> groupMap = new HashMap<>();
    private Map<String, Object> metaFormMap = new HashMap<>();
    private Map<String, Object> flowDefineMap = new HashMap<>();
    private Map<String, Object> wfOptInfoMap = new HashMap<>();
    private Map<String, Object> wfOptPageMap = new HashMap<>();
    private Map<String, Object> wfNodeMap = new HashMap<>();

    public JsonAppVo(JSONObject jsonObject, String cover, String userCode) {
        this.jsonObject = jsonObject;
        this.isCover = "T".equals(cover);
        this.userCode = userCode;
    }

    public void prepareApp() {
        jsonObjectToMap();
        if (!isCover) {
            updatePrimary();
        }
        createAppObject();
        setDatabaseName();
    }

    private void jsonObjectToMap() {
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            mapJsonObject.put(entry.getKey(),
                new ObjectMapper().convertValue(entry.getValue(),SimpleDataSet.class).getDataAsList());
        }
    }

    private void createAppObject() {
        this.createApplicationObject().createDataBaseObject()
            .createMdTableWithColumnObject().createMdRelationWithDetailObject()
            .createMetaFormObject().createMetaFormObject()
            .createDataPacketAndParamsObject()//.createGroupObject()
            .createWfOptInfo().createWfOptTeamRole().createWfOptVariable().createWfOptPage()
            .createWfDefine().createWfNode().createWfTransition();
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

    private JsonAppVo createApplicationObject() {
        if (mapJsonObject.get(TableName.M_APPLICATION_INFO.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.M_APPLICATION_INFO.name());
        if (!isCover) {
            object.addAll(convertMap(IOsInfo.class, list));
//            ApplicationTeamUser teamUser = new ApplicationTeamUser();
//            teamUser.setApplicationId((String) list.get(0).get("applicationId"));
//            teamUser.setTeamUser(userCode);
//            teamUser.setCreateUser(userCode);
//            object.add(teamUser);
        }
        return this;
    }

    private JsonAppVo createDataBaseObject() {
        if (mapJsonObject.get(TableName.F_DATABASE_INFO.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.F_DATABASE_INFO.name());
        if (!isCover) {
            object.addAll(convertMap(SourceInfo.class, list));
        }
        return this;
    }

    private JsonAppVo createMdTableWithColumnObject() {
        if (mapJsonObject.get(TableName.F_MD_TABLE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.F_MD_TABLE.name());
        metaObject.addAll(convertMap(MetaTable.class, list));
        list.forEach(map -> map.put("tableState", "W"));
        object.addAll(convertMap(PendingMetaTable.class, list));
        if (mapJsonObject.get(TableName.F_MD_COLUMN.name()) == null) {
            return this;
        }
        list = mapJsonObject.get(TableName.F_MD_COLUMN.name());
        metaObject.addAll(convertMap(MetaColumn.class, list));
        list.forEach(map -> map.put("maxLength", map.get("columnLength")));
        object.addAll(convertMap(PendingMetaColumn.class, list));
        return this;
    }

    private JsonAppVo createMdRelationWithDetailObject() {
        if (mapJsonObject.get(TableName.F_MD_RELATION.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.F_MD_RELATION.name());
        metaObject.addAll(convertMap(MetaColumn.class, list));
        list.forEach(map -> map.put("maxLength", map.get("columnLength")));
        object.addAll(convertMap(PendingMetaColumn.class, list));
        if (mapJsonObject.get(TableName.F_MD_REL_DETAIL.name()) == null) {
            return this;
        }
        list = mapJsonObject.get(TableName.F_MD_REL_DETAIL.name());
        object.addAll(convertMap(MetaRelation.class, list));
        return this;
    }

    private JsonAppVo createMetaFormObject() {
        if (mapJsonObject.get(TableName.M_META_FORM_MODEL.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.M_META_FORM_MODEL.name());
        object.addAll(convertMap(MetaFormModel.class, list));
        return this;
    }

    private JsonAppVo createDataPacketAndParamsObject() {
        if (mapJsonObject.get(TableName.Q_DATA_PACKET.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.Q_DATA_PACKET.name());
        object.addAll(convertMap(DataPacket.class, list));
        if (mapJsonObject.get(TableName.Q_DATA_PACKET_PARAM.name()) == null) {
            return this;
        }
        list = mapJsonObject.get(TableName.Q_DATA_PACKET_PARAM.name());
        object.addAll(convertMap(DataPacketParam.class, list));
        return this;
    }

   /* private JsonAppVo createGroupObject() {
        if (mapJsonObject.get(TableName.F_GROUP_TABLE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.F_GROUP_TABLE.name());
        object.addAll(convertMap(GroupInfo.class, list));
        return this;
    }*/
    private JsonAppVo createWfOptInfo() {
        if (mapJsonObject.get(TableName.WF_OPTINFO.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.WF_OPTINFO.name());
        object.addAll(convertMap(FlowOptInfo.class, list));
        return this;
    }
    private JsonAppVo createWfOptTeamRole() {
        if (mapJsonObject.get(TableName.WF_OPT_TEAM_ROLE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.WF_OPT_TEAM_ROLE.name());
        object.addAll(convertMap(OptTeamRole.class, list));
        return this;
    }
    private JsonAppVo createWfOptVariable() {
        if (mapJsonObject.get(TableName.WF_OPT_VARIABLE_DEFINE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.WF_OPT_VARIABLE_DEFINE.name());
        object.addAll(convertMap(OptVariableDefine.class, list));
        return this;
    }
    private JsonAppVo createWfOptPage() {
        if (mapJsonObject.get(TableName.WF_OPTPAGE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.WF_OPTPAGE.name());
        object.addAll(convertMap(FlowOptPage.class, list));
        return this;
    }
    private JsonAppVo createWfDefine() {
        if (mapJsonObject.get(TableName.WF_FLOW_DEFINE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.WF_FLOW_DEFINE.name());
        object.addAll(convertMap(FlowDefine.class, list));
        return this;
    }
    private JsonAppVo createWfNode() {
        if (mapJsonObject.get(TableName.WF_NODE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.WF_NODE.name());
        object.addAll(convertMap(NodeInfo.class, list));
        return this;
    }
    private JsonAppVo createWfTransition() {
        if (mapJsonObject.get(TableName.WF_TRANSITION.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.WF_TRANSITION.name());
        object.addAll(convertMap(FlowTransition.class, list));
        return this;
    }

    private void setDatabaseName() {
        if (mapJsonObject.get(TableName.F_DATABASE_INFO.name()) == null) {
            return;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.F_DATABASE_INFO.name());
        list.stream().map(s -> (String) s.get("databaseCode")).forEach(listDatabaseName::add);
    }

    private void updatePrimary() {
        this.updateApplication().updateDatabase().updateMdTableWithColumn()
            .updateMdRelationWithDetail().updateGroup()
            .updatePacketWithParams().updateMetaForm().updatePacketUseMetaForm()
            .updateWfOptInfo().updateWfOptTeamRole().updateWfOptVariable().updateWfOptPage()
            .updateWfDefine().updateWfNode().updateWfTransition();
    }


    private JsonAppVo updateApplication() {
        if (mapJsonObject.get(TableName.M_APPLICATION_INFO.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.M_APPLICATION_INFO.name());
        applicationId = UuidOpt.getUuidAsString22();
        list.forEach(map -> map.put("applicationId", applicationId));
        return this;
    }

    private JsonAppVo updateDatabase() {
        if (mapJsonObject.get(TableName.F_DATABASE_INFO.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.F_DATABASE_INFO.name());
        list.forEach(map -> {
            String uuid = UuidOpt.getUuidAsString22();
            databaseMap.put((String) map.get("databaseCode"), uuid);
            map.put("databaseCode", uuid);
            map.put("osId", applicationId);
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
            mdTableMap.put((String) map.get("tableId"), uuid);
            map.put("tableId", uuid);
            databaseMap.keySet().stream().filter(key -> key.equals(map.get("databaseCode")))
                .findFirst().ifPresent(key -> map.put("databaseCode", databaseMap.get(key)));
        });
        if (mapJsonObject.get(TableName.F_MD_COLUMN.name()) == null) {
            return this;
        }
        list = mapJsonObject.get(TableName.F_MD_COLUMN.name());
        list.forEach(map -> {
            mdTableMap.keySet().stream().filter(key -> key.equals(map.get("tableId")))
                .findFirst().ifPresent(key -> map.put("tableId", mdTableMap.get(key)));
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
            relationMap.put((String) map.get("relationId"), uuid);
            map.put("relationId", uuid);
            mdTableMap.keySet().stream().filter(key -> key.equals(map.get("parentTableId")))
                .findFirst().ifPresent(key -> map.put("parentTableId", mdTableMap.get(key)));
            mdTableMap.keySet().stream().filter(key -> key.equals(map.get("childTableId")))
                .findFirst().ifPresent(key -> map.put("childTableId", mdTableMap.get(key)));
        });
        if (mapJsonObject.get(TableName.F_MD_REL_DETAIL.name()) == null) {
            return this;
        }
        list =mapJsonObject.get(TableName.F_MD_REL_DETAIL.name());
        list.forEach(map -> relationMap.keySet().stream().filter(key -> key.equals(map.get("relationId")))
            .findFirst().ifPresent(key -> map.put("relationId", relationMap.get(key))));
        return this;
    }

    private JsonAppVo updateGroup() {
        if (mapJsonObject.get(TableName.F_GROUP_TABLE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.F_GROUP_TABLE.name());
        list.forEach(map -> {
            String uuid = UuidOpt.getUuidAsString22();
            groupMap.put((String) map.get("groupId"), uuid);
            map.put("groupId", uuid);
            map.put("applicationId", applicationId);
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
            dataPacketMap.put((String) map.get("packetId"), uuid);
            map.put("packetId", uuid);
            map.put("applicationId", applicationId);
            groupMap.keySet().stream().filter(key -> key.equals(map.get("ownGroup")))
                .findFirst().ifPresent(key -> map.put("ownGroup", groupMap.get(key)));
        });
        list.forEach(map -> {
            String form = (String) map.get("dataOptDescJson");
            for (String key : mdTableMap.keySet()) {
                form = StringUtils.replace(form, key, (String) mdTableMap.get(key));
            }
            for (String key : databaseMap.keySet()) {
                form = StringUtils.replace(form, key, (String) databaseMap.get(key));
            }
            for (String key : dataPacketMap.keySet()) {
                form = StringUtils.replace(form, key, (String) dataPacketMap.get(key));
            }
            map.put("dataOptDescJson", form);
        });
        if (mapJsonObject.get(TableName.Q_DATA_PACKET_PARAM.name()) == null) {
            return this;
        }
        list = mapJsonObject.get(TableName.Q_DATA_PACKET_PARAM.name());
        list.forEach(map -> dataPacketMap.keySet().stream().filter(key -> key.equals(map.get("packetId")))
            .findFirst().ifPresent(key -> map.put("packetId", dataPacketMap.get(key))));
        return this;
    }

    private JsonAppVo updateMetaForm() {
        if (mapJsonObject.get(TableName.M_META_FORM_MODEL.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.M_META_FORM_MODEL.name());
        list.forEach(map -> {
            String uuid = UuidOpt.getUuidAsString36();
            metaFormMap.put((String) map.get("modelId"), uuid);
            map.put("modelId", uuid);
            map.put("applicationId", applicationId);
            mdTableMap.keySet().stream().filter(key -> key.equals(map.get("tableId")))
                .findFirst().ifPresent(key -> map.put("tableId", mdTableMap.get(key)));
            databaseMap.keySet().stream().filter(key -> key.equals(map.get("databaseCode")))
                .findFirst().ifPresent(key -> map.put("databaseCode", databaseMap.get(key)));
            groupMap.keySet().stream().filter(key -> key.equals(map.get("ownGroup")))
                .findFirst().ifPresent(key -> map.put("ownGroup", groupMap.get(key)));
        });
        list.forEach(map -> {
            String form = (String) map.get("formTemplate");
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
            map.put("formTemplate", form);
        });
        return this;
    }

    private JsonAppVo updatePacketUseMetaForm() {
        if (mapJsonObject.get(TableName.Q_DATA_PACKET.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list =
            mapJsonObject.get(TableName.Q_DATA_PACKET.name());
        list.forEach(map -> {
            String form = (String) map.get("dataOptDescJson");
            for (String key : metaFormMap.keySet()) {
                form = StringUtils.replace(form, key, (String) metaFormMap.get(key));
            }
            map.put("dataOptDescJson", form);
        });
        return this;
    }
    private JsonAppVo updateWfOptInfo() {
        if (mapJsonObject.get(TableName.WF_OPTINFO.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.WF_OPTINFO.name());
        list.forEach(map -> {
            String uuid = UuidOpt.getUuidAsString22();
            wfOptInfoMap.put((String) map.get("optId"), uuid);
            map.put("optId", uuid);
            map.put("applicationId", applicationId);
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
            map.put("optTeamRoleId", uuid);
            wfOptInfoMap.keySet().stream().filter(key -> key.equals(map.get("optId")))
                .findFirst().ifPresent(key -> map.put("optId", wfOptInfoMap.get(key)));
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
            map.put("optVariableId", uuid);
            wfOptInfoMap.keySet().stream().filter(key -> key.equals(map.get("optId")))
                .findFirst().ifPresent(key -> map.put("optId", wfOptInfoMap.get(key)));
        });
        return this;
    }
    private JsonAppVo updateWfOptPage() {
        if (mapJsonObject.get(TableName.WF_OPTPAGE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.WF_OPTPAGE.name());
        list.forEach(map -> {
            String uuid = UuidOpt.getUuidAsString22();
            wfOptPageMap.put((String) map.get("optCode"), uuid);
            map.put("optCode", uuid);
            wfOptInfoMap.keySet().stream().filter(key -> key.equals(map.get("optId")))
                .findFirst().ifPresent(key -> map.put("optId", wfOptInfoMap.get(key)));
        });
        return this;
    }
    private JsonAppVo updateWfDefine() {
        if (mapJsonObject.get(TableName.WF_FLOW_DEFINE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = mapJsonObject.get(TableName.WF_FLOW_DEFINE.name());
        list.sort((o1, o2) -> GeneralAlgorithm.compareTwoObject(o1.get("version"),o2.get("version")));
        list.forEach(map -> {
            if(map.get("version").equals(0)) {
                String uuid = UuidOpt.getUuidAsString22();
                flowDefineMap.put((String) map.get("flowCode"), uuid);
                map.put("flowCode", uuid);
            }else{
                flowDefineMap.keySet().stream().filter(key -> key.equals(map.get("flowCode")))
                    .findFirst().ifPresent(key -> map.put("flowCode", flowDefineMap.get(key)));
            }
            wfOptInfoMap.keySet().stream().filter(key -> key.equals(map.get("optId")))
                .findFirst().ifPresent(key -> map.put("optId", wfOptInfoMap.get(key)));
            map.put("osId", applicationId);
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
            wfNodeMap.put((String) map.get("nodeId"), uuid);
            map.put("nodeId", uuid);
            map.put("osId", applicationId);
            flowDefineMap.keySet().stream().filter(key -> key.equals(map.get("flowCode")))
                .findFirst().ifPresent(key -> map.put("flowCode", flowDefineMap.get(key)));
            wfOptPageMap.keySet().stream().filter(key -> key.equals(map.get("optCode")))
                .findFirst().ifPresent(key -> map.put("optCode", wfOptPageMap.get(key)));
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
            wfNodeMap.put((String) map.get("nodeId"), uuid);
            map.put("transId", uuid);
            flowDefineMap.keySet().stream().filter(key -> key.equals(map.get("flowCode")))
                .findFirst().ifPresent(key -> map.put("flowCode", flowDefineMap.get(key)));
            wfNodeMap.keySet().stream().filter(key -> key.equals(map.get("startNodeId")))
                .findFirst().ifPresent(key -> map.put("startNodeId", wfNodeMap.get(key)));
            wfNodeMap.keySet().stream().filter(key -> key.equals(map.get("endNodeId")))
                .findFirst().ifPresent(key -> map.put("endNodeId", wfNodeMap.get(key)));
        });
        return this;
    }

}

