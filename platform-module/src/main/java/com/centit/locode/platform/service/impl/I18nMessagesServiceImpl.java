package com.centit.locode.platform.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.centit.locode.platform.dao.I18nMessagesDao;
import com.centit.locode.platform.po.I18nMessages;
import com.centit.locode.platform.service.I18nMessagesService;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.database.utils.PageDesc;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class I18nMessagesServiceImpl implements I18nMessagesService {

    @Value("${app.default.lang:zh_CN}")
    private String defaultLang;

    @Autowired
    private I18nMessagesDao i18nMessagesDao;

    @Override
    public List<I18nMessages> listI18nMessages(String osId) {
        return i18nMessagesDao.listObjectsByProperties(
            CollectionsOpt.createHashMap("osId", osId));
    }

    @Override
    public List<I18nMessages> listI18nMessages(Map<String, Object> filterMap, PageDesc pageDesc) {
        if(!filterMap.containsKey("osId"))
            return null;
        return i18nMessagesDao.listObjectsByProperties(filterMap, pageDesc);
    }

    @Override
    public I18nMessages getI18nMessages(String osId, String msgKey) {
        JSONObject id = new JSONObject();
        id.put("osId", osId);
        id.put("msgKey", msgKey);
        return i18nMessagesDao.getObjectById(id);
    }

    @Override
    public void saveI18nMessages(I18nMessages i18nMessages) {
        i18nMessagesDao.saveNewObject(i18nMessages);
    }

    @Override
    public void updateI18nMessages(I18nMessages i18nMessages) {
        i18nMessagesDao.updateObject(i18nMessages);
    }

    /*@Override
    public int updateI18nMessages(String osId, String msgKey, String lang, String msgValue, String userCode) {
        JSONObject id = new JSONObject();
        id.put("osId", osId);
        id.put("msgKey", msgKey);
        I18nMessages messages = i18nMessagesDao.getObjectById(id);
        if(messages == null)
            return 0;
        JSONObject msgJson = messages.getMsgValue();
        if(msgJson==null){
            msgJson = new JSONObject();
        }
        msgJson.put(lang, msgValue);
        messages.setMsgValue(msgJson);
        messages.setUpdateUser(userCode);
        i18nMessagesDao.updateObject(messages);
        return 1;
    }
    */

    @Override
    public void deleteI18nMessages(String osId, String msgKey) {
        JSONObject id = new JSONObject();
        id.put("osId", osId);
        id.put("msgKey", msgKey);
        i18nMessagesDao.deleteObjectById(id);
    }

    @Override
    public Map<String, String> listI18nMessages(String osId, String lang) {
        List<I18nMessages> messages = i18nMessagesDao.listObjectsByProperties(
            CollectionsOpt.createHashMap("osId", osId));
        if(messages==null)
            return null;
        Map<String, String> msgMap = new HashMap<>(messages.size()+2);
        for(I18nMessages msg: messages){
            String value = msg.getMsgValue().getString(lang);
            if(StringUtils.isBlank(value)){
                // zh-HK 华 - 香港的 SAR zh-MO 华 - 澳门的 SAR  zh-CN 华 -中国 zh-CHS 华 (单一化)
                // zh-SG 华 -新加坡 zh-TW 华 -台湾  zh-CHT 华 (传统的)
                if(lang.startsWith("zh_")){
                    value = msg.getMsgValue().getString("zh_CN");
                }
                if(StringUtils.isBlank(value)) {
                    value = msg.getMsgValue().getString(defaultLang);
                }
            }
            msgMap.put(msg.getMsgKey(), value);
        }
        return msgMap;
    }
}
