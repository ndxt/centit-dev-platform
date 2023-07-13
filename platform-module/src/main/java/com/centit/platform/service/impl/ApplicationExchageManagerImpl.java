package com.centit.platform.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.platform.dao.ApplicationTemplateDao;
import com.centit.platform.service.ApplicationExchangeManager;
import com.centit.support.file.FileIOOpt;
import com.centit.support.file.FileSystemOpt;
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
public class ApplicationExchageManagerImpl implements ApplicationExchangeManager {
    @Value("${app.home:./}")
    private String appHome;

    @Autowired
    private ApplicationTemplateDao applicationTemplateDao;

    private void exportDictionaries(String osId, String appFileRoot) throws IOException {
        String dictDir = appFileRoot + File.separator +  "dictionary";
        FileSystemOpt.createDirect(new File(dictDir));

        String sqlCatalog = "select * from f_datacatalog where CATALOG_CODE in " +
            "(select dictionary_id from m_application_dictionary where os_id= ?)";

        String sqlDictionary = "select * from f_datadictionary where CATALOG_CODE = ?";

        JSONArray catalogs = DatabaseOptUtils.listObjectsBySqlAsJson(applicationTemplateDao, sqlCatalog, new Object[]{osId});
        for(Object obj : catalogs){
            if(obj instanceof JSONObject){
                JSONObject catalog = (JSONObject) obj;
                JSONArray dictionaries = DatabaseOptUtils.listObjectsBySqlAsJson(applicationTemplateDao,
                    sqlDictionary, new Object[]{catalog.getString("catalogCode")});
                catalog.put("details", dictionaries);
                FileIOOpt.writeStringToFile(catalog.toString(),
                    dictDir  + File.separator +  catalog.getString("catalogCode") +".json");
            }
        }
    }

    @Override
    public InputStream exportApplication(String osId, CentitUserDetails userDetails) throws IOException {
        String appFileRoot = appHome + File.separator +  osId + "-" + userDetails.getUserCode();
        FileSystemOpt.createDirect(new File(appFileRoot));
        exportDictionaries(osId, appFileRoot);

        return null;
    }

    @Override
    public int importApplication(InputStream offlineFile, String osId, CentitUserDetails userDetails) throws IOException {
        return 0;
    }
}
