package com.centit.locode.platform.service.impl;

import com.centit.locode.platform.dao.HistoryVersionDao;
import com.centit.locode.platform.po.HistoryVersion;
import com.centit.locode.platform.service.HistoryVersionService;
import com.centit.support.database.utils.PageDesc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HistoryVersionServiceImpl implements HistoryVersionService {

    @Autowired
    HistoryVersionDao historyVersionDao;

    @Override
    public void createHistoryVersion(HistoryVersion historyVersion) {
        historyVersionDao.saveNewObject(historyVersion);
    }

    @Override
    public void updateHistoryVersion(HistoryVersion historyVersion) {
        historyVersionDao.updateObject(historyVersion);
    }

    @Override
    public void deleteHistoryVersion(String historyId) {
        historyVersionDao.deleteObjectById(historyId);
    }

    @Override
    public List<HistoryVersion> listHistoryVersion(Map<String, Object> params, PageDesc pageDesc) {
        return historyVersionDao.listObjectsByProperties(params,pageDesc);
    }

    @Override
    public HistoryVersion getHistoryVersion(String historyId) {
        return historyVersionDao.getObjectById(historyId);
    }
}
