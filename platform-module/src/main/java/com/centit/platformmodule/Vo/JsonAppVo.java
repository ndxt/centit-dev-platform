package com.centit.platformmodule.Vo;

import com.alibaba.fastjson.JSONObject;
import com.centit.dde.po.DataPacket;
import com.centit.dde.po.DataPacketParam;
import com.centit.metaform.po.MetaFormModel;
import com.centit.platformmodule.po.ApplicationInfo;
import com.centit.platformmodule.po.ApplicationTeamUser;
import com.centit.platformmodule.po.GroupInfo;
import com.centit.product.dbdesign.po.PendingMetaColumn;
import com.centit.product.dbdesign.po.PendingMetaTable;
import com.centit.product.metadata.po.MetaColumn;
import com.centit.product.metadata.po.MetaRelation;
import com.centit.product.metadata.po.MetaTable;
import com.centit.product.metadata.po.SourceInfo;
import com.centit.support.algorithm.UuidOpt;
import com.centit.support.common.JavaBeanMetaData;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhf
 */
@Data
public class JsonAppVo {
    private JSONObject jsonObject;
    private Boolean isCover;
    private String userCode;
    private String applicationId;
    private List<Object> object = new ArrayList<>();
    private List<Object> metaObject = new ArrayList<>();
    private List<String> listDatabaseName = new ArrayList<>();
    private Map<String, Object> databaseMap = new HashMap<>();
    private Map<String, Object> mdTableMap = new HashMap<>();
    private Map<String, Object> relationMap = new HashMap<>();
    private Map<String, Object> dataPacketMap = new HashMap<>();
    private Map<String, Object> groupMap = new HashMap<>();
    private Map<String, Object> metaFormMap = new HashMap<>();

    public JsonAppVo(JSONObject jsonObject, String cover, String userCode) {
        this.jsonObject = jsonObject;
        this.isCover = "T".equals(cover);
        this.userCode = userCode;
    }

    public void createApp() {
        if (!isCover) {
            updatePrimary();
        }
        createAppObject();
    }

    private void createAppObject() {
        this.createApplicationObject().createDataBaseObject()
            .createMdTableWithColumnObject().createMdRelationWithDetailObject()
            .createMetaFormObject().createMetaFormObject()
            .createDataPacketAndParamsObject().createGroupObject();
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
        if (jsonObject.get(TableName.M_APPLICATION_INFO.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = objectToMap(jsonObject.get(TableName.M_APPLICATION_INFO.name()));
        if (!isCover) {
            object.addAll(convertMap(ApplicationInfo.class, list));
            ApplicationTeamUser teamUser = new ApplicationTeamUser();
            teamUser.setApplicationId((String) list.get(0).get("applicationId"));
            teamUser.setTeamUser(userCode);
            teamUser.setCreateUser(userCode);
            object.add(teamUser);
        }
        return this;
    }

    private JsonAppVo createDataBaseObject() {
        if (jsonObject.get(TableName.F_DATABASE_INFO.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = objectToMap(jsonObject.get(TableName.F_DATABASE_INFO.name()));
        if (!isCover) {
            object.addAll(convertMap(SourceInfo.class, list));
        }
        return this;
    }

    private JsonAppVo createMdTableWithColumnObject() {
        if (jsonObject.get(TableName.F_MD_TABLE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = objectToMap(jsonObject.get(TableName.F_MD_TABLE.name()));
        metaObject.addAll(convertMap(MetaTable.class, list));
        list.forEach(map -> map.put("tableState", "W"));
        object.addAll(convertMap(PendingMetaTable.class, list));
        if (jsonObject.get(TableName.F_MD_COLUMN.name()) == null) {
            return this;
        }
        list = objectToMap(jsonObject.get(TableName.F_MD_COLUMN.name()));
        metaObject.addAll(convertMap(MetaColumn.class, list));
        list.forEach(map -> map.put("maxLength", map.get("columnLength")));
        object.addAll(convertMap(PendingMetaColumn.class, list));
        return this;
    }

    private JsonAppVo createMdRelationWithDetailObject() {
        if (jsonObject.get(TableName.F_MD_RELATION.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = objectToMap(jsonObject.get(TableName.F_MD_RELATION.name()));
        metaObject.addAll(convertMap(MetaColumn.class, list));
        list.forEach(map -> map.put("maxLength", map.get("columnLength")));
        object.addAll(convertMap(PendingMetaColumn.class, list));
        if (jsonObject.get(TableName.F_MD_REL_DETAIL.name()) == null) {
            return this;
        }
        list = objectToMap(jsonObject.get(TableName.F_MD_REL_DETAIL.name()));
        object.addAll(convertMap(MetaRelation.class, list));
        return this;
    }

    private JsonAppVo createMetaFormObject() {
        if (jsonObject.get(TableName.M_META_FORM_MODEL.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = objectToMap(jsonObject.get(TableName.M_META_FORM_MODEL.name()));
        object.addAll(convertMap(MetaFormModel.class, list));
        return this;
    }

    private JsonAppVo createDataPacketAndParamsObject() {
        if (jsonObject.get(TableName.Q_DATA_PACKET.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = objectToMap(jsonObject.get(TableName.Q_DATA_PACKET.name()));
        object.addAll(convertMap(DataPacket.class, list));
        if (jsonObject.get(TableName.Q_DATA_PACKET_PARAM.name()) == null) {
            return this;
        }
        list = objectToMap(jsonObject.get(TableName.Q_DATA_PACKET_PARAM.name()));
        object.addAll(convertMap(DataPacketParam.class, list));
        return this;
    }

    private JsonAppVo createGroupObject() {
        if (jsonObject.get(TableName.F_GROUP_TABLE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = objectToMap(jsonObject.get(TableName.F_GROUP_TABLE.name()));
        object.addAll(convertMap(GroupInfo.class, list));
        return this;
    }


    private void setDatabaseName() {
        if (jsonObject.get(TableName.F_DATABASE_INFO.name()) == null) {
            return;
        }
        List<Map<String, Object>> list = objectToMap(jsonObject.get(TableName.F_DATABASE_INFO.name()));
        list.stream().map(s -> (String) s.get("databaseCode")).forEach(listDatabaseName::add);
    }

    private void updatePrimary() {
        this.updateApplication().updateDatabase().updateMdTableWithColumn()
            .updateMdRelationWithDetail().updateGroup()
            .updatePacketWithParams().updateMetaForm()
            .updatePacketUseMetaForm();
    }

    private List<Map<String, Object>> objectToMap(Object object) {
        return (List<Map<String, Object>>) object;
    }

    private JsonAppVo updateApplication() {
        if (jsonObject.get(TableName.M_APPLICATION_INFO.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = objectToMap(jsonObject.get(TableName.M_APPLICATION_INFO.name()));
        applicationId = UuidOpt.getUuidAsString22();
        list.forEach(map -> map.put("applicationId", applicationId));
        return this;
    }

    private JsonAppVo updateDatabase() {
        if (jsonObject.get(TableName.F_DATABASE_INFO.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = objectToMap(jsonObject.get(TableName.F_DATABASE_INFO.name()));
        list.forEach(map -> {
            String uuid = UuidOpt.getUuidAsString22();
            databaseMap.put((String) map.get("databaseCode"), uuid);
            map.put("databaseCode", uuid);
            map.put("osId", applicationId);
        });
        return this;
    }

    private JsonAppVo updateMdTableWithColumn() {
        if (jsonObject.get(TableName.F_MD_TABLE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = objectToMap(jsonObject.get(TableName.F_MD_TABLE.name()));
        list.forEach(map -> {
            String uuid = UuidOpt.getUuidAsString32();
            mdTableMap.put((String) map.get("tableId"), uuid);
            map.put("tableId", uuid);
            databaseMap.keySet().stream().filter(key -> key.equals(map.get("databaseCode")))
                .findFirst().ifPresent(key -> map.put("databaseCode", databaseMap.get(key)));
        });
        if (jsonObject.get(TableName.F_MD_COLUMN.name()) == null) {
            return this;
        }
        list = objectToMap(jsonObject.get(TableName.F_MD_COLUMN.name()));
        list.forEach(map -> {
            mdTableMap.keySet().stream().filter(key -> key.equals(map.get("tableId")))
                .findFirst().ifPresent(key -> map.put("tableId", mdTableMap.get(key)));
        });
        return this;
    }

    private JsonAppVo updateMdRelationWithDetail() {
        if (jsonObject.get(TableName.F_MD_RELATION.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = objectToMap(jsonObject.get(TableName.F_MD_RELATION.name()));
        list.forEach(map -> {
            String uuid = UuidOpt.getUuidAsString22();
            relationMap.put((String) map.get("relationId"), uuid);
            map.put("relationId", uuid);
            mdTableMap.keySet().stream().filter(key -> key.equals(map.get("parentTableId")))
                .findFirst().ifPresent(key -> map.put("parentTableId", mdTableMap.get(key)));
            mdTableMap.keySet().stream().filter(key -> key.equals(map.get("childTableId")))
                .findFirst().ifPresent(key -> map.put("childTableId", mdTableMap.get(key)));
        });
        if (jsonObject.get(TableName.F_MD_REL_DETAIL.name()) == null) {
            return this;
        }
        list = objectToMap(jsonObject.get(TableName.F_MD_REL_DETAIL.name()));
        list.forEach(map -> relationMap.keySet().stream().filter(key -> key.equals(map.get("relationId")))
            .findFirst().ifPresent(key -> map.put("relationId", relationMap.get(key))));
        return this;
    }

    private JsonAppVo updateGroup() {
        if (jsonObject.get(TableName.F_GROUP_TABLE.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = objectToMap(jsonObject.get(TableName.F_GROUP_TABLE.name()));
        list.forEach(map -> {
            String uuid = UuidOpt.getUuidAsString22();
            groupMap.put((String) map.get("groupId"), uuid);
            map.put("groupId", uuid);
            map.put("applicationId", applicationId);
        });
        return this;
    }

    private JsonAppVo updatePacketWithParams() {
        if (jsonObject.get(TableName.Q_DATA_PACKET.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = objectToMap(jsonObject.get(TableName.Q_DATA_PACKET.name()));
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
        if (jsonObject.get(TableName.Q_DATA_PACKET_PARAM.name()) == null) {
            return this;
        }
        list = objectToMap(jsonObject.get(TableName.Q_DATA_PACKET_PARAM.name()));
        list.forEach(map -> dataPacketMap.keySet().stream().filter(key -> key.equals(map.get("packetId")))
            .findFirst().ifPresent(key -> map.put("packetId", dataPacketMap.get(key))));
        return this;
    }

    private JsonAppVo updateMetaForm() {
        if (jsonObject.get(TableName.M_META_FORM_MODEL.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list = objectToMap(jsonObject.get(TableName.M_META_FORM_MODEL.name()));
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
        if (jsonObject.get(TableName.Q_DATA_PACKET.name()) == null) {
            return this;
        }
        List<Map<String, Object>> list =
            objectToMap(jsonObject.get(TableName.Q_DATA_PACKET.name()));
        list.forEach(map -> {
            String form = (String) map.get("dataOptDescJson");
            for (String key : metaFormMap.keySet()) {
                form = StringUtils.replace(form, key, (String) metaFormMap.get(key));
            }
            map.put("dataOptDescJson", form);
        });
        return this;
    }


}

