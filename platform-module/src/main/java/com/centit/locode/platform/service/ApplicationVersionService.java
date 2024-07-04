package com.centit.locode.platform.service;

import com.alibaba.fastjson2.JSONArray;
import com.centit.locode.platform.po.AppMergeTask;
import com.centit.locode.platform.po.ApplicationVersion;
import com.centit.support.database.utils.PageDesc;

import java.util.List;
import java.util.Map;

public interface ApplicationVersionService {
    String createApplicationVersion(ApplicationVersion applicationVersion);

    void updateApplicationVersion(ApplicationVersion applicationVersion);

    void deleteApplicationVersion(String versionId);

    boolean checkMergeState(String applicationId);

    List<ApplicationVersion> listApplicationVersion(String applicationId, PageDesc pageDesc, Map<String, Object> filterMap);

    ApplicationVersion getApplicationVersion(String versionId);

    JSONArray compareTwoVersion(String versionId, String versionId2);

    JSONArray compareToOldVersion(String applicationId, String versionId);

    JSONArray listAppComponents(String appVersionId, String type, PageDesc pageDesc);

    int restoreAppVersion(String appVersionId, String userCode);

    int mergeAppComponents(String appVersionId, JSONArray components, String userCode);

    List<AppMergeTask> listAppMergeTasks(String appVersionId, Map<String, Object> filterMap, PageDesc pageDesc);

    void mergeCompleted(AppMergeTask task);

    void restoreCompleted(String appVersionId);

    void rollbackMergeTask(AppMergeTask task);

    void rollbackRestore(String appVersionId);
}
