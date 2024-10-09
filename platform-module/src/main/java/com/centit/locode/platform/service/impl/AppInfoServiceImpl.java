package com.centit.locode.platform.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.locode.platform.dao.AppInfoDao;
import com.centit.locode.platform.po.AppInfo;
import com.centit.locode.platform.service.AppInfoService;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.database.utils.PageDesc;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author tian_y
 */
@Service
public class AppInfoServiceImpl implements AppInfoService {

    @Autowired
    private AppInfoDao appInfoDao;

    @Override
    public List<AppInfo> listObjects() {
        return appInfoDao.listObjects();
    }


    @Override
    public List<AppInfo> listObjectsByProperties(Map<String, Object> map, PageDesc pageDesc) {
        return appInfoDao.listObjectsByProperties(map, pageDesc);
    }

    @Override
    public List<AppInfo> listObjectsByProperty(String s, Object o) {
        return appInfoDao.listObjectsByProperties(CollectionsOpt.createHashMap(s, o));
    }

    @Override
    public List<AppInfo> listObjectsByProperties(Map<String, Object> map) {
        return appInfoDao.listObjectsByProperties(map);
    }

    @Override
    public AppInfo getObjectById(String s) {
        return appInfoDao.getObjectById(s);
    }

    @Override
    public void saveNewObject(AppInfo appInfo) {
        appInfoDao.saveNewObject(appInfo);
    }

    @Override
    public void updateObject(AppInfo appInfo) {
        appInfoDao.updateObject(appInfo);
    }

    @Override
    public void mergeObject(AppInfo appInfo) {
        appInfoDao.mergeObject(appInfo);
    }

    @Override
    public void deleteObject(AppInfo appInfo) {
        appInfoDao.deleteObject(appInfo);
    }

    @Override
    public void deleteObjectById(String s) {
        appInfoDao.deleteObjectById(s);
    }

    @Override
    public AppInfo getObjectByProperty(String s, Object o) {
        if(StringUtils.isBlank(s) || o == null) return null;
        return appInfoDao.getObjectByProperties(CollectionsOpt.createHashMap(s, o));
    }

    @Override
    public AppInfo getObjectByProperties(Map<String, Object> map) {
        if(map == null || map.isEmpty()) return null;
        return appInfoDao.getObjectByProperties(map);
    }

    @Override
    public JSONArray listObjectsAsJson(Map<String, Object> map, PageDesc pageDesc) {
        return appInfoDao.listObjectsByPropertiesAsJson(map, pageDesc);
    }

    @Override
    public JSONArray listObjectsBySqlAsJson(String s, Map<String, Object> map, PageDesc pageDesc) {
        return null;
    }

    @Override
    public JSONObject getLastAppInfo(String appType) {
        return appInfoDao.getLastAppInfo(appType);
    }
}
