package com.centit.locode.platform.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.locode.platform.dao.ApplicationTemplateDao;
import com.centit.locode.platform.service.EnvironmentExportManager;
import com.centit.support.common.ObjectException;
import com.centit.support.file.FileIOOpt;
import com.centit.support.file.FileSystemOpt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author codefan@sina.com
 */
@Service
public class EnvironmentExportManagerImpl implements EnvironmentExportManager {
    @Value("${app.home:./}")
    private String appHome;

    @Autowired
    private ApplicationTemplateDao applicationTemplateDao;

    private void exportResources(String osId, String appFileRoot) throws IOException {
        String resourceFile = appFileRoot + File.separator +  "resources.json";
        String sqlDatabase ="select a.* from f_database_info a where a.database_code " +
            "in (select DATABASE_ID from m_application_resources where os_id= ?)";
        JSONArray databases = DatabaseOptUtils.listObjectsBySqlAsJson(applicationTemplateDao, sqlDatabase, new Object[]{osId});
        if(databases != null) {
            FileIOOpt.writeStringToFile(databases.toString(), resourceFile);
        }
    }

    private void exportMetadata(String osId, String appFileRoot) throws IOException {
        String dictDir = appFileRoot + File.separator +  "metadata";
        FileSystemOpt.createDirect(new File(dictDir));

        String sqlTables = "select * from f_md_table where table_id in (select table_id from f_table_opt_relation where OS_ID = ?)" +
            " and database_code in (select DATABASE_ID from m_application_resources where OS_ID = ?)";
        String sqlColumns = "select * from f_md_column where table_id = ?";
        String sqlRelations = "select * from f_md_relation where parent_table_id = ?";
        String sqlRelationDetails = "select * from f_md_rel_detail where relation_id = ?";

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
                        for (Object obj2 : relations) {
                            if (obj2 instanceof JSONObject) {
                                JSONObject relation = (JSONObject) obj2;
                                String relationId = relation.getString("relationId");
                                JSONArray relationDetails = DatabaseOptUtils.listObjectsBySqlAsJson(applicationTemplateDao,
                                    sqlRelationDetails, new Object[]{relationId});
                                relation.put("details", relationDetails);
                            }
                        }
                        table.put("relations", relations);
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
        JSONArray rules = DatabaseOptUtils.listObjectsBySqlAsJson(applicationTemplateDao, sqlRules, new Object[]{topUnit});
        if(rules != null) {
            FileIOOpt.writeStringToFile(rules.toString(), ruleFile);
        }
    }

    private void exportRoleFormula(String topUnit, String appFileRoot) throws IOException {
        String formulaFile = appFileRoot + File.separator +  "roleFormula.json";
        String sqlFormula ="select a.* from wf_role_formula a where a.top_unit = ?";
        JSONArray formulas = DatabaseOptUtils.listObjectsBySqlAsJson(applicationTemplateDao, sqlFormula, new Object[]{topUnit});
        if(formulas != null) {
            FileIOOpt.writeStringToFile(formulas.toString(), formulaFile);
        }
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

    @Override
    public InputStream exportApplication(String osId, CentitUserDetails userDetails) throws IOException {
        String appFileRoot = appHome + File.separator +  osId + "-" + userDetails.getUserCode();
        FileSystemOpt.createDirect(new File(appFileRoot));

        String applicationFile = appFileRoot + File.separator +  "application.json";
        String sqlApplication = "select * from f_os_info where os_id = ? ";
        JSONObject application = DatabaseOptUtils.getObjectBySqlAsJson(applicationTemplateDao, sqlApplication, new Object[]{osId});
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
        return null;
    }

    @Override
    public int importApplication(InputStream offlineFile, String osId, CentitUserDetails userDetails) throws IOException {
        return 0;
    }
}
