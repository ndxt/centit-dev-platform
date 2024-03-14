package com.centit.locode.platform.service;

import com.alibaba.fastjson2.JSONArray;
import com.centit.locode.platform.po.ApplicationVersion;
import com.centit.support.database.utils.PageDesc;

import java.util.List;

public interface ApplicationVersionService {
    String createApplicationVersion(ApplicationVersion applicationVersion);

    void updateApplicationVersion(ApplicationVersion applicationVersion);

    void deleteApplicationVersion(String versionId);

    List<ApplicationVersion> listApplicationVersion(String applicationId, PageDesc pageDesc);

    ApplicationVersion getApplicationVersion(String versionId);

    JSONArray compareTwoVersion(String versionId, String versionId2);

    JSONArray compareToOldVersion(String applicationId, String versionId);

    void restoreApplicationVersion(String versionId);
}
