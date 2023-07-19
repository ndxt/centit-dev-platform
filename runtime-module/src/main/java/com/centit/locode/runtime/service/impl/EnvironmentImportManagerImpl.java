package com.centit.locode.runtime.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.fileserver.common.FileInfoOpt;
import com.centit.fileserver.po.FileInfo;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.locode.runtime.dao.DummyDao;
import com.centit.locode.runtime.service.EnvironmentImportManager;
import com.centit.support.algorithm.NumberBaseOpt;
import com.centit.support.database.utils.FieldType;
import com.centit.support.file.FileSystemOpt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class EnvironmentImportManagerImpl implements EnvironmentImportManager {

    public static final Logger logger = LoggerFactory.getLogger(EnvironmentImportManagerImpl.class);

    @Value("${app.home:./}")
    private String appHome;

    @Autowired
    private FileInfoOpt fileInfoOpt;

    @Autowired
    protected DummyDao dummyDao;

    private void deleteObject(JSONObject obj, String tableName, String [] matchFields){
        StringBuilder sqlBuilder = new StringBuilder("delete from ");
        sqlBuilder.append(tableName).append(" where ");
        List<Object> params = new ArrayList<>();
        int n =0;
        for(String s : matchFields){
            if(n>0)
                sqlBuilder.append(" and ");
            sqlBuilder.append(s).append(" = ?");
            params.add(obj.get(FieldType.mapPropName(s)));
            n++;
        }
        DatabaseOptUtils.doExecuteSql(dummyDao, sqlBuilder.toString(), params.toArray());
    }

    private void insertObject(JSONObject obj, String tableName, String[] fields, String [] pkFileds){
        StringBuilder sqlBuilder = new StringBuilder("insert into ");
        sqlBuilder.append(tableName).append(" ( ");
        List<Object> params = new ArrayList<>();
        int n = 0;
        for(String s : pkFileds){
            Object fieldValue = obj.get(FieldType.mapPropName(s));
            if(fieldValue!=null) {
                if (n > 0)
                    sqlBuilder.append(", ");
                sqlBuilder.append(s);
                params.add(fieldValue);
                n++;
            }
        }
        for(String s : fields){
            Object fieldValue = obj.get(FieldType.mapPropName(s));
            if(fieldValue!=null) {
                if (n > 0)
                    sqlBuilder.append(", ");
                sqlBuilder.append(s);
                params.add(fieldValue);
                n++;
            }
        }
        sqlBuilder.append(" ) values ( ");
        for(int i=0;i<n;i++){
            if(i>0)
                sqlBuilder.append(", ");
            sqlBuilder.append("?");
        }
        sqlBuilder.append(" )");
        DatabaseOptUtils.doExecuteSql(dummyDao, sqlBuilder.toString(), params.toArray());
    }
    private void mergeObject(JSONObject obj, String tableName, String[] fields, String [] pkFileds){
        StringBuilder sqlBuilder = new StringBuilder("select count(*) hasExists from ");
        sqlBuilder.append(tableName).append(" where ");
        List<Object> params = new ArrayList<>();
        int n =0;
        for(String s : pkFileds){
            if(n>0)
                sqlBuilder.append(" and ");
            sqlBuilder.append(s).append(" = ?");
            params.add(obj.get(FieldType.mapPropName(s)));
            n++;
        }
        int hasCount = NumberBaseOpt.castObjectToInteger(
            DatabaseOptUtils.getScalarObjectQuery(dummyDao, sqlBuilder.toString(), params.toArray()), 0);
        if(hasCount>0){
            params = new ArrayList<>();
            sqlBuilder = new StringBuilder("update ");
            sqlBuilder.append(tableName).append(" set ");
            n = 0;
            for(String s : fields){
                Object fieldValue = obj.get(FieldType.mapPropName(s));
                if(fieldValue!=null) {
                    if (n > 0)
                        sqlBuilder.append(", ");
                    sqlBuilder.append(s).append(" = ?");
                    params.add(fieldValue);
                    n++;
                }
            }
            sqlBuilder.append(" where ");
            n =0;
            for(String s : pkFileds){
                if(n>0)
                    sqlBuilder.append(" and ");
                sqlBuilder.append(s).append(" = ?");
                params.add(obj.get(FieldType.mapPropName(s)));
            }
            DatabaseOptUtils.doExecuteSql(dummyDao, sqlBuilder.toString(), params.toArray());
        } else {
            insertObject(obj, tableName, fields, pkFileds);
        }
    }

    private void importDictionary(String dictionaryDir) throws IOException {
        //F_DATACATALOG PK:CATALOG_CODE
        String [] catalogFields = new String[] {"CATALOG_NAME", "CATALOG_STYLE", "CATALOG_TYPE", "CATALOG_DESC",
            "FIELD_DESC", "NEED_CACHE", "TOP_UNIT", "OS_ID", "OPT_ID",
            "CREATE_DATE", "CREATOR", "UPDATOR", "UPDATE_DATE", "SOURCE_ID"};

        //F_DATADICTIONARY PK:CATALOG_CODE DATA_CODE
        String [] dictionaryFields = new String[] {"EXTRA_CODE", "EXTRA_CODE2", "DATA_TAG",
            "DATA_VALUE", "DATA_STYLE", "DATA_ORDER", "DATA_DESC", "CREATE_DATE",
            "LAST_MODIFY_DATE"};
        List<File> files = FileSystemOpt.findFiles(dictionaryDir, "*.json");
        for(File f :files) {
            //String catalog = StringUtils.substringBefore(f.getName(), '.');
            JSONObject catalogJson = JSON.parseObject(new FileInputStream(f));
            mergeObject(catalogJson, "F_DATACATALOG", catalogFields, new String[] {"CATALOG_CODE"});
            deleteObject(catalogJson, "F_DATADICTIONARY", new String[] {"CATALOG_CODE"});
            Object josnArray = catalogJson.get("details");
            if(josnArray instanceof JSONArray){
                JSONArray details =(JSONArray) josnArray;
                for(Object obj : details){
                    if(obj instanceof JSONObject){
                        JSONObject dictionary = (JSONObject) obj;
                        insertObject(dictionary, "F_DATADICTIONARY", dictionaryFields,
                            new String[]{"CATALOG_CODE", "DATA_CODE"});
                    }
                }
            }
        }

    }

    private void saveJsonArrayFile(String filePath, String tableName, String[] fields, String [] pkFileds) throws IOException {
        JSONArray filesJson = JSON.parseArray(Files.newInputStream(Paths.get(filePath)));
        for(Object obj : filesJson){
            if(obj instanceof JSONObject){
                JSONObject fileObj = (JSONObject) obj;
                mergeObject(fileObj, tableName, fields, pkFileds);
            }
        }
    }

    public static String matchFileStoreUrl(String fileMd5) {
        String pathname = String.valueOf(fileMd5.charAt(0))
            + File.separatorChar + fileMd5.charAt(1)
            + File.separatorChar + fileMd5.charAt(2);
        return pathname + File.separatorChar + fileMd5 + ".dat";
    }

    private void importFile(String fileDir, boolean storeFile) throws IOException {
        //FILE_LIBRARY_INFO PK:library_id
        String [] libraryFields = new String[] {"library_name", "library_type", "create_user", "create_time",
            "own_unit", "own_user", "is_create_folder", "is_upload", "auth_code",
            "update_user", "update_time", "UPDATOR", "UPDATE_DATE", "SOURCE_ID"};

        //FILE_INFO PK:FILE_ID
        String [] fileFields = new String[] {"FILE_MD5", "FILE_NAME", "FILE_SHOW_PATH", "FILE_TYPE",
            "FILE_STATE", "FILE_DESC", "INDEX_STATE", "DOWNLOAD_TIMES", "OS_ID", /*library_id*/
            "OPT_ID", "OPT_METHOD", "OPT_TAG", "CREATED", "CREATE_TIME",
            "ENCRYPT_TYPE", "FILE_OWNER", "FILE_UNIT", "ATTACHED_FILE_MD5", "ATTACHED_TYPE",
            "auth_code", "library_id", "parent_folder", "file_catalog"};

        //FILE_STORE_INFO PK:FILE_MD5
        String [] storeFields = new String[] {"FILE_SIZE", "FILE_STORE_PATH", "FILE_REFERENCE_COUNT",
            "IS_TEMP", "CREATE_TIME"};

        JSONObject libraryJson = JSON.parseObject(Files.newInputStream(Paths.get(fileDir + File.separator + "library.json")));
        if(libraryJson != null) {
            mergeObject(libraryJson, "FILE_LIBRARY_INFO", libraryFields, new String[]{"library_id"});
        }

        // 文件传输
        JSONArray filesJson = JSON.parseArray(Files.newInputStream(Paths.get(fileDir + File.separator + "fileInfo.json")));
        for(Object obj : filesJson){
            if(obj instanceof JSONObject){
                JSONObject fileJson = (JSONObject) obj;
                if(storeFile){
                    try {
                        FileInfo fileInfo = fileJson.toJavaObject(FileInfo.class);
                        String localFilePath = matchFileStoreUrl(fileInfo.getFileMd5());
                        FileInputStream is =  new FileInputStream(fileDir + File.separator + localFilePath);
                        fileInfoOpt.saveFile(fileInfo, is.available(), is);
                    } catch (IOException e){
                        logger.error(e.getMessage());
                    }
                }
            }
        }

    }

    private void importWorkflow(String flowDir) throws IOException {

        //WF_FLOW_DEFINE PK:FLOW_CODE VERSION
        String [] flowFields = new String[] {"FLOW_NAME", "FLOW_CLASS", "FLOW_STATE",
            "FLOW_DESC", "FLOW_XML_DESC", "FLOW_PUBLISH_DATE", "FIRST_NODE_ID", "OS_ID",
            "OPT_ID", "TIME_LIMIT", "EXPIRE_OPT", "AT_PUBLISH_DATE", "SOURCE_ID"};

        saveJsonArrayFile(flowDir + File.separator + "defines.json",
            "WF_FLOW_DEFINE", flowFields, new String[]{"FLOW_CODE", "VERSION"});

        //WF_NODE PK:NODE_ID
        String [] nodeFields = new String[] {"FLOW_CODE", "VERSION", "NODE_TYPE", "NODE_NAME",
            "OPT_TYPE", "OPT_ID", "OPT_CODE", "OPT_PARAM", "OS_ID",
            "OPT_BEAN", "SUB_FLOW_CODE", "ROLE_TYPE", "ROLE_CODE", "UNIT_EXP",
            "POWER_EXP", "NODE_DESC", "IS_ACCOUNT_TIME", "LIMIT_TYPE", "TIME_LIMIT",
            "INHERIT_TYPE", "INHERIT_NODE_CODE", "EXPIRE_OPT", "NOTICE_TYPE", "NOTICE_USER_EXP",
            "NOTICE_MESSAGE", "NODE_CODE", "RISK_INFO", "STAGE_CODE", "MULTI_INST_TYPE",
            "MULTI_INST_PARAM", "CONVERGE_TYPE", "CONVERGE_PARAM", "WARNING_RULE", "WARNING_PARAM",
            "SOURCE_ID"};

        saveJsonArrayFile(flowDir + File.separator + "nodes.json",
            "WF_NODE", nodeFields, new String[]{"NODE_ID"});

        //WF_TRANSITION PK:TRANS_ID
        String [] transitionFields = new String[] {"TRANS_CLASS", "TRANS_NAME", "TRANS_DESC", "START_NODE_ID",
            "END_NODE_ID", "TRANS_CONDITION", "LIMIT_TYPE", "TIME_LIMIT", "IS_ACCOUNT_TIME",
            "CAN_IGNORE", "VERSION", "FLOW_CODE"};

        saveJsonArrayFile(flowDir + File.separator + "transitions.json",
            "WF_TRANSITION", transitionFields, new String[]{"TRANS_ID"});

        //WF_FLOW_STAGE PK:STAGE_ID
        String [] stageFields = new String[] {"STAGE_CODE", "STAGE_NAME", "IS_ACCOUNT_TIME", "LIMIT_TYPE",
            "TIME_LIMIT", "EXPIRE_OPT", "STAGE_ORDER", "VERSION", "FLOW_CODE"};

        saveJsonArrayFile(flowDir + File.separator + "stages.json",
            "WF_FLOW_STAGE", stageFields, new String[]{"STAGE_ID"});

        //WF_OPT_TEAM_ROLE PK:OPT_TEAM_ROLE_ID
        String [] teamFields = new String[] {"OPT_ID", "ROLE_CODE", "ROLE_NAME", "FORMULA_CODE",
            "TEAM_ROLE_ORDER", "MODIFY_TIME"};

        saveJsonArrayFile(flowDir + File.separator + "teams.json",
            "WF_OPT_TEAM_ROLE", teamFields, new String[]{"OPT_TEAM_ROLE_ID"});

        //WF_OPT_VARIABLE_DEFINE PK:OPT_VARIABLE_ID
        String [] variableFields = new String[] {"OPT_ID", "VARIABLE_NAME", "VARIABLE_DESC", "VARIABLE_TYPE",
            "DEFAULT_VALUE", "MODIFY_TIME"};

        saveJsonArrayFile(flowDir + File.separator + "variables.json",
            "WF_OPT_VARIABLE_DEFINE", variableFields, new String[]{"OPT_VARIABLE_ID"});
    }

    /**
     * @param importType dictionary，file，flow, fileAndStore
     * @param ud 操作人员信息
     */
    @Override
    public void importEnvironment(String importType, CentitUserDetails ud) throws IOException {
        String rootDir = appHome + File.separator +  "config";
        if("dictionary".equalsIgnoreCase(importType)) {
            importDictionary(rootDir + File.separator + "dictionary");
        }
        if("file".equalsIgnoreCase(importType)) {
            importFile(rootDir + File.separator + "files", false);
        }

        if("fileAndStore".equalsIgnoreCase(importType)) {
            importFile(rootDir + File.separator + "files", true);
        }

        if("flow".equalsIgnoreCase(importType)) {
            importWorkflow(rootDir + File.separator + "flows");
        }
    }
}