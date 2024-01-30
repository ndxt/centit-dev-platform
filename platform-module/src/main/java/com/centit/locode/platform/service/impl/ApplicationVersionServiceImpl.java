package com.centit.locode.platform.service.impl;

import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.OsInfo;
import com.centit.locode.platform.dao.ApplicationVersionDao;
import com.centit.locode.platform.po.ApplicationVersion;
import com.centit.locode.platform.service.ApplicationVersionService;
import com.centit.support.common.ObjectException;
import com.centit.support.database.utils.PageDesc;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class ApplicationVersionServiceImpl implements ApplicationVersionService {

    @Autowired
    ApplicationVersionDao applicationVersionDao;

    @Autowired
    private PlatformEnvironment platformEnvironment;
    /**
     * 直接从数据库中获取 页面、api 和工作流引擎数据（工作流引擎引用最新版本的）
     * @param applicationVersion 版本信息
     */
    @Override
    public String createApplicationVersion(ApplicationVersion applicationVersion) {
        if(StringUtils.isBlank(applicationVersion.getApplicationId())){
            throw new ObjectException(ObjectException.DATA_VALIDATE_ERROR, "没有指定关联应用信息！");
        }
        OsInfo osInfo = platformEnvironment.getOsInfo(applicationVersion.getApplicationId());
        if(osInfo==null){
            throw new ObjectException(ObjectException.DATA_VALIDATE_ERROR, "关联应用信息不能存在！");
        }
        //保存版本信息
        applicationVersionDao.saveNewObject(applicationVersion);
        String versionId= applicationVersion.getVersionId();
        //查找应用相关的所有页面

        //查找应用相关的所有api

        //查找应用相关的所有工作流，工作流的比较复杂，需要相关的版本和变量等信息
        return versionId;
    }

    @Override
    public void updateApplicationVersion(ApplicationVersion applicationVersion) {

    }

    @Override
    public void deleteApplicationVersion(String versionId) {

    }

    @Override
    public List<ApplicationVersion> listApplicationVersion(String applicationId, PageDesc pageDesc) {
        return null;
    }

    @Override
    public ApplicationVersion getApplicationVersion(String versionId) {
        return null;
    }

    @Override
    public InputStream exportApplicationVersion(String versionId) {
        return null;
    }

    @Override
    public void importApplicationVersion(InputStream inputStream) {

    }

    @Override
    public void restoreApplicationVersion(String versionId) {

    }

}
