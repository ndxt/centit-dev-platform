package com.centit.locode.runtime.service.impl;

import com.centit.framework.security.model.CentitUserDetails;
import com.centit.locode.runtime.service.EnvironmentImportManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.centit.fileserver.common.FileStore;

import java.io.File;

@Service
public class EnvironmentImportManagerImpl implements EnvironmentImportManager {

    @Value("${app.home:./}")
    private String appHome;

    @Autowired
    protected FileStore fileStore;

    private void importDictionary(String dictionaryDir) {

    }

    private void importFile(String fileDir) {

    }

    private void importWorkflow(String flowDir) {

    }
    /**
     * @param importType dictionary，file，flow
     * @param ud 操作人员信息
     */
    @Override
    public void importEnvironment(String importType, CentitUserDetails ud) {
        String rootDir = appHome + File.separator +  "config";
        importDictionary(rootDir + File.separator + "dictionary");
        importFile(rootDir + File.separator + "files");
        importWorkflow(rootDir + File.separator + "flows");
    }
}
