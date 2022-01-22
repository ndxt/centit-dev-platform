package com.centit.platform.service;

import com.centit.platform.po.ApplicationDictionary;
import com.centit.support.database.utils.PageDesc;

import java.util.List;
import java.util.Map;

public interface ApplicationDictionaryService {

    void createApplicationDictionary(ApplicationDictionary applicationDictionary);

    void updateApplicationDictionary(ApplicationDictionary applicationDictionary);

    void deleteApplicationDictionary(String id);

    List<ApplicationDictionary> listApplicationDictionary(Map<String, Object> params, PageDesc pageDesc);

    ApplicationDictionary getApplicationDictionary(String id);

    void deleteAppDictionary(String osId, String dictionaryId);
}
