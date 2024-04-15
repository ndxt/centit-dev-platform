package com.centit.locode.platform.service;

import com.centit.locode.platform.po.HistoryVersion;
import com.centit.support.database.utils.PageDesc;

import java.util.List;
import java.util.Map;

public interface HistoryVersionService {
    void createHistoryVersion(HistoryVersion historyVersion);

    int countHistoryVersion(String relationId, String historySha);

    void updateHistoryVersion(HistoryVersion historyVersion);

    void deleteHistoryVersion(String historyId);

    List<HistoryVersion> listHistoryVersion(Map<String, Object> params, PageDesc pageDesc);

    HistoryVersion getHistoryVersion(String historyId);

    List<HistoryVersion> listHistoryByAppVersion(String appVersion);
}
