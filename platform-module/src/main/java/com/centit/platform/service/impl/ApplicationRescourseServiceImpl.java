package com.centit.platform.service.impl;

import com.centit.platform.dao.ApplicationRescourseDao;
import com.centit.platform.po.ApplicationRescourse;
import com.centit.platform.service.ApplicationRescourseService;
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
public class ApplicationRescourseServiceImpl implements ApplicationRescourseService {

    @Autowired
    ApplicationRescourseDao applicationRescourseDao;

    @Override
    public void createApplicationRescourse(ApplicationRescourse applicationRescourse) {
        applicationRescourseDao.saveNewObject(applicationRescourse);
    }

    @Override
    public void updateApplicationRescourse(ApplicationRescourse applicationRescourse) {
        applicationRescourseDao.updateObject(applicationRescourse);
    }

    @Override
    public void deleteApplicationRescourse(String id) {
        applicationRescourseDao.deleteObjectById(id);
    }

    @Override
    public List<ApplicationRescourse> listApplicationRescourse(Map<String, Object> params, PageDesc pageDesc) {
        return applicationRescourseDao.listObjects(params,pageDesc);
    }

    @Override
    public ApplicationRescourse getApplicationRescourse(String id) {
        return applicationRescourseDao.getObjectById(id);
    }

    @Override
    public void deleteAppRescourse(String osId, String dataBaseId) {
        if (osId == null) {
            return;
        }
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("osId", osId);
        if (StringUtils.isNotBlank(dataBaseId)) {
            filterMap.put("dataBaseId", dataBaseId);
        }
        applicationRescourseDao.deleteObjectsByProperties(filterMap);
    }

    @Override
    public List<ApplicationRescourse> listObjectsByProperty(Map<String, Object> propertiesMap) {
        return applicationRescourseDao.listObjectsByProperties(propertiesMap);
    }
}
