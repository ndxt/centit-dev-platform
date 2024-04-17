package com.centit.locode.platform.service;

import com.centit.locode.platform.po.I18nMessages;
import com.centit.support.database.utils.PageDesc;

import java.util.List;
import java.util.Map;

public interface I18nMessagesService {

    List<I18nMessages> listI18nMessages(String osId);

    List<I18nMessages> listI18nMessages(Map<String, Object> filterMap, PageDesc pageDesc);

    I18nMessages getI18nMessages(String osId, String msgKey);

    void saveI18nMessages(I18nMessages i18nMessages);

    void updateI18nMessages(I18nMessages i18nMessages);

    //int updateI18nMessages(String osId, String msgKey, String lang, String msgValue, String userCode);

    void deleteI18nMessages(String osId, String msgKey);

    Map<String, String> listI18nMessages(String osId, String lang);
}
