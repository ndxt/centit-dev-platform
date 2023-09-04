package com.centit.locode.platform.service.impl;

import com.centit.locode.platform.dao.ApplicationResourcesDao;
import com.centit.locode.platform.po.ApplicationResources;
import com.centit.locode.platform.service.ApplicationResourcesService;
import com.centit.product.metadata.service.SourceInfoManager;
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
public class ApplicationResourcesServiceImpl implements ApplicationResourcesService {

    @Autowired
    ApplicationResourcesDao applicationResourcesDao;

    @Autowired
    private SourceInfoManager sourceInfoManager;

    @Override
    public void createApplicationResources(ApplicationResources applicationResources) {
        applicationResourcesDao.saveNewObject(applicationResources);
    }

    @Override
    public void updateApplicationResources(ApplicationResources applicationRescourse) {
        applicationResourcesDao.updateObject(applicationRescourse);
    }

    @Override
    public void deleteApplicationResources(String id) {
        applicationResourcesDao.deleteObjectById(id);
    }

    @Override
    public List<ApplicationResources> listApplicationResources(Map<String, Object> params, PageDesc pageDesc) {
        return applicationResourcesDao.listObjectsByProperties(params,pageDesc);
    }

    @Override
    public ApplicationResources getApplicationResources(String id) {
        return applicationResourcesDao.getObjectById(id);
    }

    @Override
    public void deleteAppResources(String osId, String dataBaseId) {
        if (osId == null) {
            return;
        }
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("osId", osId);
        if (StringUtils.isNotBlank(dataBaseId)) {
            filterMap.put("dataBaseId", dataBaseId);
        }
        applicationResourcesDao.deleteObjectsByProperties(filterMap);
    }

    @Override
    public List<ApplicationResources> listObjectsByProperty(Map<String, Object> propertiesMap) {
        return applicationResourcesDao.listObjectsByProperties(propertiesMap);
    }

    @Override
    public void deleteSourceInfo(String dataBaseId) {
        Map<String, Object> filterMap = new HashMap<>();
        if (StringUtils.isNotBlank(dataBaseId)) {
            filterMap.put("dataBaseId", dataBaseId);
        }
        applicationResourcesDao.deleteObjectsByProperties(filterMap);
        sourceInfoManager.deleteObjectById(dataBaseId);
    }
}
