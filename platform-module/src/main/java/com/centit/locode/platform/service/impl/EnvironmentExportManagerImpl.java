package com.centit.locode.platform.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.fileserver.common.FileInfoOpt;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.framework.model.security.CentitUserDetails;
import com.centit.locode.platform.dao.ApplicationTemplateDao;
import com.centit.locode.platform.service.ApplicationInfoManager;
import com.centit.locode.platform.service.EnvironmentExportManager;
import com.centit.support.algorithm.ZipCompressor;
import com.centit.support.common.ObjectException;
import com.centit.support.file.FileIOOpt;
import com.centit.support.file.FileSystemOpt;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author codefan@sina.com
 */
@Service
public class EnvironmentExportManagerImpl implements EnvironmentExportManager {

    public static final Logger logger = LoggerFactory.getLogger(EnvironmentExportManagerImpl.class);
    @Value("${app.home:./}")
    private String appHome;

    @Autowired
    private FileInfoOpt fileInfoOpt;

    @Autowired
    private ApplicationTemplateDao applicationTemplateDao;

    @Autowired
    private ApplicationInfoManager applicationInfoManager;

    private void exportJsonArrayToFile(String sqlSen, Object[] params, String jsonFilePath) throws IOException {
        JSONArray jsonArray = DatabaseOptUtils.listObjectsBySqlAsJson(applicationTemplateDao, sqlSen, params);
        if(jsonArray != null) {
            FileIOOpt.writeStringToFile(jsonArray.toString(), jsonFilePath);
        }
    }

    private void exportResources(String osId, String appFileRoot) throws IOException {
        String resourceFile = appFileRoot + File.separator +  "resources.json";
        String sqlDatabase ="select a.* from f_database_info a where a.database_code " +
            "in (select DATABASE_ID from m_application_resources where os_id= ?)";
        exportJsonArrayToFile(sqlDatabase, new Object[]{osId}, resourceFile);
    }

    private void exportMetadata(String osId, String appFileRoot) throws IOException {
        String dictDir = appFileRoot + File.separator +  "metadata";
        FileSystemOpt.createDirect(new File(dictDir));

        String sqlTables = "select * from f_md_table where table_id in (select table_id from f_table_opt_relation where OS_ID = ?)" +
            " and database_code in (select DATABASE_ID from m_application_resources where OS_ID = ?)";
        String sqlColumns = "select * from f_md_column where table_id = ?";
        String sqlRelations = "select * from f_md_relation where parent_table_id = ?";
        String sqlRelationDetails = "select * from f_md_rel_detail where relation_id = ?";
        String sqlViewText = "select VIEW_SQL from F_PENDING_META_TABLE where TABLE_ID = ?";

        JSONArray tables = DatabaseOptUtils.listObjectsBySqlAsJson(applicationTemplateDao, sqlTables, new Object[]{osId, osId});
        if(tables != null) {
            for (Object obj : tables) {
                if (obj instanceof JSONObject) {
                    JSONObject table = (JSONObject) obj;
                    String tableId = table.getString("tableId");
                    JSONArray columns = DatabaseOptUtils.listObjectsBySqlAsJson(applicationTemplateDao,
                        sqlColumns, new Object[]{tableId});
                    table.put("columns", columns);

                    JSONArray relations = DatabaseOptUtils.listObjectsBySqlAsJson(applicationTemplateDao,
                        sqlRelations, new Object[]{tableId});
                    if (relations != null) {
                        for (Object rel : relations) {
                            if (rel instanceof JSONObject) {
                                JSONObject relation = (JSONObject) rel;
                                String relationId = relation.getString("relationId");
                                JSONArray relationDetails = DatabaseOptUtils.listObjectsBySqlAsJson(applicationTemplateDao,
                                    sqlRelationDetails, new Object[]{relationId});
                                relation.put("details", relationDetails);
                            }
                        }
                        table.put("relations", relations);
                    }

                    if("V".equalsIgnoreCase(table.getString("tableType"))){
                        Object viewSql = DatabaseOptUtils.getScalarObjectQuery(applicationTemplateDao, sqlViewText, new Object[]{tableId});
                        table.put("viewSql", viewSql);
                    }

                    FileIOOpt.writeStringToFile(table.toString(),
                        dictDir + File.separator + tableId + ".json");
                }
            }
        }
    }

    private void exportDictionaries(String osId, String appFileRoot) throws IOException {
        String dictDir = appFileRoot + File.separator +  "dictionary";
        FileSystemOpt.createDirect(new File(dictDir));
        String sqlCatalog = "select * from f_datacatalog where CATALOG_CODE in " +
            "(select dictionary_id from m_application_dictionary where os_id= ?)";
        String sqlDictionary = "select * from f_datadictionary where CATALOG_CODE = ?";

        JSONArray catalogs = DatabaseOptUtils.listObjectsBySqlAsJson(applicationTemplateDao, sqlCatalog, new Object[]{osId});
        if(catalogs != null) {
            for (Object obj : catalogs) {
                if (obj instanceof JSONObject) {
                    JSONObject catalog = (JSONObject) obj;
                    JSONArray dictionaries = DatabaseOptUtils.listObjectsBySqlAsJson(applicationTemplateDao,
                        sqlDictionary, new Object[]{catalog.getString("catalogCode")});
                    catalog.put("details", dictionaries);
                    FileIOOpt.writeStringToFile(catalog.toString(),
                        dictDir + File.separator + catalog.getString("catalogCode") + ".json");
                }
            }
        }
    }

    private void exportCheckRules(String topUnit, String appFileRoot) throws IOException {
        String ruleFile = appFileRoot + File.separator +  "checkRules.json";
        String sqlRules ="select a.* from F_DATA_CHECK_RULE a where a.TOP_UNIT = ?";
        exportJsonArrayToFile(sqlRules, new Object[]{topUnit}, ruleFile);
    }

    private void exportRoleFormula(String topUnit, String appFileRoot) throws IOException {
        String formulaFile = appFileRoot + File.separator +  "roleFormula.json";
        String sqlFormula ="select a.* from wf_role_formula a where a.top_unit = ?";
        exportJsonArrayToFile(sqlFormula, new Object[]{topUnit}, formulaFile);
    }

    private void deserializeFields(JSONObject jsonObject, String ... fileds){
        for(String field : fileds) {
            String strTemp = jsonObject.getString(field);
            if(StringUtils.isNotBlank(strTemp)){
                jsonObject.put(field, JSON.parse(strTemp));
            }
        }
    }

    //导出页面数据
    private void exportPages(String osId, String appFileRoot) throws IOException {
        String dictDir = appFileRoot + File.separator + "pages";
        FileSystemOpt.createDirect(new File(dictDir));
        String sqlPages ="select a.* from m_meta_form_model a where a.os_id = ?";
        JSONArray pages = DatabaseOptUtils.listObjectsBySqlAsJson(applicationTemplateDao, sqlPages, new Object[]{osId});
        if(pages != null) {
            for (Object obj : pages) {
                if (obj instanceof JSONObject) {
                    JSONObject page = (JSONObject) obj;
                    deserializeFields(page, "formTemplate", "mobileFormTemplate", "structureFunction");
                    FileIOOpt.writeStringToFile(page.toString(),
                        dictDir + File.separator + page.getString("modelId") + ".json");
                }
            }
        }
    }

    //导出API数据
    private void exportApis(String osId, String appFileRoot) throws IOException {
        String dictDir = appFileRoot + File.separator + "apis";
        FileSystemOpt.createDirect(new File(dictDir));
        String sqlApis ="select a.* from q_data_packet a where a.os_id = ?";
        String sqlApiParams ="select a.* from q_data_packet_param a where a.PACKET_ID = ?";

        JSONArray apis = DatabaseOptUtils.listObjectsBySqlAsJson(applicationTemplateDao, sqlApis, new Object[]{osId});
        if(apis != null) {
            for (Object obj : apis) {
                if (obj instanceof JSONObject) {
                    JSONObject api = (JSONObject) obj;
                    deserializeFields(api, "dataOptDescJson", "returnResult", "extProps", "schemaProps");
                    String packetId = api.getString("packetId");

                    JSONArray params = DatabaseOptUtils.listObjectsBySqlAsJson(applicationTemplateDao, sqlApiParams, new Object[]{packetId});
                    if(params != null) {
                        api.put("params", params);
                    }

                    FileIOOpt.writeStringToFile(api.toString(),
                        dictDir + File.separator + packetId + ".json");
                }
            }
        }
    }

    public static String matchFileStoreUrl(String fileMd5, String rootDir) {
        String pathname = String.valueOf(fileMd5.charAt(0))
            + File.separatorChar + fileMd5.charAt(1)
            + File.separatorChar + fileMd5.charAt(2);
        FileSystemOpt.createDirect(rootDir + File.separatorChar + pathname);
        return pathname + File.separatorChar + fileMd5 + ".dat";
    }

    private void exportFiles(String osId, String appFileRoot) throws IOException {
        String sqlLibrary = "select a.* from file_library_info a where a.library_id = ?";
        String fileInfoSql = "select a.* from file_info a where a.library_id = ? and a.file_catalog in ('A','B')";
        String fileStoreSql = "select distinct b.* from file_info a " +
            " join FILE_STORE_INFO b on (a.FILE_MD5 = b.FILE_MD5 or a.ATTACHED_FILE_MD5 = b.FILE_MD5 ) " +
            " where a.library_id = ? and a.file_catalog in ('A','B')";
        String fileDir = appFileRoot + File.separator + "files";
        FileSystemOpt.createDirect(new File(fileDir));

        JSONObject library = DatabaseOptUtils.getObjectBySqlAsJson(applicationTemplateDao, sqlLibrary, new Object[]{osId});
        if(library != null) {
            FileIOOpt.writeStringToFile(library.toString(),
                fileDir + File.separator + "library.json");
        }

        exportJsonArrayToFile(fileInfoSql, new Object[]{osId}, fileDir + File.separator + "fileInfo.json");
        JSONArray fileInfos = DatabaseOptUtils.listObjectsBySqlAsJson(applicationTemplateDao, fileInfoSql, new Object[]{osId});
        if(fileInfos != null) {
            for(Object obj : fileInfos) {
                if (obj instanceof JSONObject) {
                    JSONObject fileJson = (JSONObject) obj;
                    try {
                        InputStream is = fileInfoOpt.loadFileStream(fileJson.getString("fileId"));
                        if (is != null) {
                            String fileMd5 = fileJson.getString("fileMd5");
                            String filePath = matchFileStoreUrl(fileMd5, fileDir);
                            FileIOOpt.writeInputStreamToFile(is, fileDir + File.separatorChar + filePath);
                        }
                    } catch (ObjectException e){
                        logger.error(e.getMessage());
                    }
                }
            }

            FileIOOpt.writeStringToFile(fileInfos.toString(),
                fileDir + File.separator + "fileInfo.json");
        }
        exportJsonArrayToFile(fileStoreSql, new Object[]{osId}, fileDir + File.separator + "storeInfo.json");
    }

    private void exportWorkflows(String osId, String appFileRoot) throws IOException {
        String sqlFlowDefine = "select * from wf_flow_define where OS_ID= ? and flow_state in('E','B')";
        String sqlNodes = "select * from wf_node where (flow_code,version) in(" +
            "select flow_code,version from wf_flow_define where OS_ID= ? and flow_state='B')";
        String sqlTransitions = "select * from wf_transition where (flow_code,version) in(" +
            "select flow_code,version from wf_flow_define where OS_ID= ? and flow_state='B')";
        String sqlStages = "select * from wf_flow_stage where (flow_code,version) in(" +
            "select flow_code,version from wf_flow_define where OS_ID=? and flow_state='B')";
        String sqlTeams = "select * from wf_opt_team_role where opt_id in " +
            "(select opt_id from f_optinfo where top_opt_id= ?)";
        String sqlVariables =  "select * from wf_opt_variable_define where opt_id in " +
            "(select opt_id from f_optinfo where top_opt_id=?)";

        String flowDir = appFileRoot + File.separator + "flows";
        FileSystemOpt.createDirect(new File(flowDir));

        exportJsonArrayToFile(sqlFlowDefine, new Object[]{osId}, flowDir + File.separator + "defines.json");
        exportJsonArrayToFile(sqlNodes, new Object[]{osId}, flowDir + File.separator + "nodes.json");
        exportJsonArrayToFile(sqlTransitions, new Object[]{osId}, flowDir + File.separator + "transitions.json");
        exportJsonArrayToFile(sqlStages, new Object[]{osId}, flowDir + File.separator + "stages.json");
        exportJsonArrayToFile(sqlTeams, new Object[]{osId}, flowDir + File.separator + "teams.json");
        exportJsonArrayToFile(sqlVariables, new Object[]{osId}, flowDir + File.separator + "variables.json");
    }

    @Override
    public InputStream exportApplication(String osId, CentitUserDetails userDetails) throws IOException {
        String appFileRoot = appHome + File.separator +  osId + "-" + userDetails.getUserCode();// UuidOpt.randomString(4);
        FileSystemOpt.createDirect(new File(appFileRoot));

        String applicationFile = appFileRoot + File.separator +  "application.json";
        //String sqlApplication = "select * from f_os_info where os_id = ? ";
        JSONObject application = applicationInfoManager.getApplicationInfo(osId,
            userDetails.getTopUnitCode(), userDetails.getUserCode(),false);
        // DatabaseOptUtils.getObjectBySqlAsJson(applicationTemplateDao, sqlApplication, new Object[]{osId});
        if(application == null) {
            throw new ObjectException(ObjectException.DATA_VALIDATE_ERROR, "应用："+osId +" 不存在！");
        }
        FileIOOpt.writeStringToFile(application.toString(), applicationFile);
        String topUnit = application.getString("topUnit");

        exportDictionaries(osId, appFileRoot);
        exportResources(osId, appFileRoot);
        exportMetadata(osId, appFileRoot);
        exportCheckRules(topUnit, appFileRoot);
        exportRoleFormula(topUnit, appFileRoot);
        exportPages(osId, appFileRoot);
        exportApis(osId, appFileRoot);
        exportFiles(osId, appFileRoot);
        exportWorkflows(osId, appFileRoot);

        ZipCompressor.compressFileInDirectory( appFileRoot+".zip", appFileRoot );
        FileSystemOpt.deleteDirect(new File(appFileRoot));
        return Files.newInputStream(Paths.get(appFileRoot + ".zip"));
    }

}
