package com.centit.platformmodule.service.impl;

import com.centit.framework.jdbc.service.BaseEntityManagerImpl;
import com.centit.platformmodule.dao.ApplicationTeamUserDao;
import com.centit.platformmodule.po.ApplicationTeamUser;
import com.centit.platformmodule.service.ApplicationTeamUserManager;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.database.utils.PageDesc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FileLibraryAccess  Service.
 * create by scaffold 2020-08-18 13:38:15
 *
 * @author codefan@sina.com
 * 项目库授权信息
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ApplicationTeamUserManagerImpl extends BaseEntityManagerImpl<ApplicationTeamUser, String, ApplicationTeamUserDao>
    implements ApplicationTeamUserManager {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationTeamUserManager.class);

    @Autowired
    private ApplicationTeamUserDao applicationTeamUserDao;

    @Override
    public void updateApplicationTeamUser(ApplicationTeamUser applicationTeamUser) {
        applicationTeamUserDao.deleteObjectsForceByProperties(CollectionsOpt.createHashMap("teamUser", applicationTeamUser.getTeamUser(), "applicationId", applicationTeamUser.getApplicationId()));
        applicationTeamUserDao.saveNewObject(applicationTeamUser);
        applicationTeamUserDao.saveObjectReferences(applicationTeamUser);
    }

    @Override
    public void createApplicationTeamUser(ApplicationTeamUser applicationTeamUser) {
        applicationTeamUserDao.saveNewObject(applicationTeamUser);
        applicationTeamUserDao.saveObjectReferences(applicationTeamUser);
    }

    @Override
    public List<ApplicationTeamUser> listApplicationTeamUser(Map<String, Object> param, PageDesc pageDesc) {
        return applicationTeamUserDao.listObjects(param, pageDesc);
    }


    @Override
    public ApplicationTeamUser getApplicationTeamUser(String uuId) {
        return applicationTeamUserDao.getObjectById(uuId);
    }

    @Override
    public void deleteApplicationTeamUser(String uuId) {
        applicationTeamUserDao.deleteObjectById(uuId);
    }

}

