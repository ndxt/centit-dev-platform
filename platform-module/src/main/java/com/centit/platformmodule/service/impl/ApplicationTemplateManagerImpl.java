package com.centit.platformmodule.service.impl;

import com.centit.platformmodule.dao.ApplicationTemplateDao;
import com.centit.platformmodule.po.ApplicationTemplate;
import com.centit.platformmodule.service.ApplicationTemplateManager;
import com.centit.support.database.utils.PageDesc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author zhf
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ApplicationTemplateManagerImpl implements ApplicationTemplateManager {
    @Autowired
    private ApplicationTemplateDao applicationTemplateDao;

    @Override
    public void mergeApplicationTemplate(ApplicationTemplate applicationTemplate) {
        applicationTemplateDao.mergeObject(applicationTemplate);
    }

    @Override
    public List<ApplicationTemplate> listApplicationTemplate(Map<String, Object> param, PageDesc pageDesc) {
        return applicationTemplateDao.listObjectsByProperties(param, pageDesc);
    }

    @Override
    public ApplicationTemplate getApplicationTemplate(String templateId) {
        return applicationTemplateDao.getObjectWithReferences(templateId);
    }

    @Override
    public void deleteApplicationTemplate(String templateId) {
        applicationTemplateDao.deleteObjectById(templateId);
    }

}
