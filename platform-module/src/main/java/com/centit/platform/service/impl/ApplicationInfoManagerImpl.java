package com.centit.platform.service.impl;

import com.centit.platform.dao.ApplicationInfoDao;
import com.centit.platform.po.ApplicationInfo;
import com.centit.platform.service.ApplicationInfoManager;
import com.centit.support.database.utils.PageDesc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
@Service
@Transactional(rollbackFor = Exception.class)
public class ApplicationInfoManagerImpl implements ApplicationInfoManager {
    @Autowired
    private ApplicationInfoDao applicationInfoDao;
    @Override
    public void createApplicationInfo(ApplicationInfo applicationInfo){
        applicationInfoDao.saveNewObject(applicationInfo);
        applicationInfoDao.saveObjectReferences(applicationInfo);

    }
    @Override
    public List<ApplicationInfo> listApplicationInfo(Map<String, Object> param, PageDesc pageDesc){
        return applicationInfoDao.listObjectsByProperties(param,pageDesc);
    }
    @Override
    public ApplicationInfo getApplicationInfo(String applicationId){
       return applicationInfoDao.getObjectWithReferences(applicationId);
    }
    @Override
    public void deleteApplicationInfo(String applicationId){
        ApplicationInfo applicationInfo=applicationInfoDao.getObjectById(applicationId);
        applicationInfo.setIsDelete(true);
        updateApplicationInfo(applicationInfo);
    }

    @Override
    public void updateApplicationInfo(ApplicationInfo applicationInfo){
        applicationInfoDao.updateObject(applicationInfo);
        applicationInfoDao.saveObjectReferences(applicationInfo);
    }
}
