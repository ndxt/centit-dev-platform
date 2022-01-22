package com.centit.platform.service.impl;


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

@Service
@Transactional(rollbackFor = Exception.class)
public class ApplicationDictionaryServiceImpl implements ApplicationDictionaryService {

    @Autowired
    ApplicationDictionaryDao applicationDictionaryDao;

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
        return applicationDictionaryDao.listObjects(params,pageDesc);
    }

    @Override
    public ApplicationDictionary getApplicationDictionary(String id) {
        return applicationDictionaryDao.getObjectById(id);
    }

    @Override
    public void deleteAppDictionary(String osId, String dictionaryId) {
        if (osId == null) {
            return;
        }
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("osId", osId);
        if (StringUtils.isNotBlank(dictionaryId)) {
            filterMap.put("dictionaryId", dictionaryId);
        }
        applicationDictionaryDao.deleteObjectsByProperties(filterMap);
    }
}
