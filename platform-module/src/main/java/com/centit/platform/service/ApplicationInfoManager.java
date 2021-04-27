package com.centit.platform.service;

import com.centit.platform.po.ApplicationInfo;
import com.centit.support.database.utils.PageDesc;

import java.util.List;
import java.util.Map;

public interface ApplicationInfoManager {
    void createApplicationInfo(ApplicationInfo applicationInfo);

    List<ApplicationInfo> listApplicationInfo(Map<String, Object> param, PageDesc pageDesc);

    ApplicationInfo getApplicationInfo(String applicationId);

    void deleteApplicationInfo(String applicationId);

    void updateApplicationInfo(ApplicationInfo applicationInfo);
}
