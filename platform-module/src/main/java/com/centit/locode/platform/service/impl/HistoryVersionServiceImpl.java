package com.centit.locode.platform.service.impl;

import com.centit.locode.platform.dao.HistoryVersionDao;
import com.centit.locode.platform.po.HistoryVersion;
import com.centit.locode.platform.service.HistoryVersionService;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.security.Sha1Encoder;
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
        //保存时 生成sha指纹
        historyVersion.setHistorySha(
            Sha1Encoder.encodeBase64(historyVersion.getContent().toJSONString(), true) );
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

    @Override
    public List<HistoryVersion> listHistoryByAppVersion(String appVersion) {
        return historyVersionDao.listObjectsByFilter("where APP_VERSION_ID=? order by type, relation_id",
            new Object[]{appVersion});
    }

}
