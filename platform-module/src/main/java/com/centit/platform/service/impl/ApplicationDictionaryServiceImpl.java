package com.centit.platform.service.impl;


import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.platform.dao.ApplicationDictionaryDao;
import com.centit.platform.po.ApplicationDictionary;
import com.centit.platform.service.ApplicationDictionaryService;
import com.centit.support.database.utils.PageDesc;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tian_y
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ApplicationDictionaryServiceImpl implements ApplicationDictionaryService {

    @Autowired
    ApplicationDictionaryDao applicationDictionaryDao;

    @Autowired
    PlatformEnvironment platformEnvironment;

    @Override
    public void createApplicationDictionary(ApplicationDictionary applicationDictionary) {
        applicationDictionaryDao.saveNewObject(applicationDictionary);
    }

    @Override
    public void updateApplicationDictionary(ApplicationDictionary applicationDictionary) {
        applicationDictionaryDao.updateObject(applicationDictionary);
    }

    @Override
    public void deleteApplicationDictionary(String id) {
        applicationDictionaryDao.deleteObjectById(id);
    }

    @Override
    public List<ApplicationDictionary> listApplicationDictionary(Map<String, Object> params, PageDesc pageDesc) {
        return applicationDictionaryDao.listObjectsByProperties(params,pageDesc);
    }

    @Override
    public ApplicationDictionary getApplicationDictionary(String id) {
        return applicationDictionaryDao.getObjectById(id);
    }

    @Override
    public void deleteDataDictionary(String dictionaryId) {
        Map<String, Object> filterMap = new HashMap<>(1);
        if (StringUtils.isNotBlank(dictionaryId)) {
            filterMap.put("dictionaryId", dictionaryId);
        }
        applicationDictionaryDao.deleteObjectsByProperties(filterMap);
        platformEnvironment.deleteDataDictionary(dictionaryId);
    }

}
