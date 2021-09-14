package com.centit.platform.service;

import com.centit.platform.po.HistoryVersion;
import com.centit.support.database.utils.PageDesc;

import java.util.List;
import java.util.Map;

public interface HistoryVersionService {
    void createHistoryVersion(HistoryVersion historyVersion);

    void updateHistoryVersion(HistoryVersion historyVersion);

    void deleteHistoryVersion(String historyId);

    List<HistoryVersion> listHistoryVersion(Map<String, Object> params, PageDesc pageDesc);

    HistoryVersion getHistoryVersion(String historyId);
}
