package com.centit.locode.platform.service;

import com.centit.locode.platform.po.ApplicationDictionary;
import com.centit.support.database.utils.PageDesc;

import java.util.List;
import java.util.Map;

public interface ApplicationDictionaryService {

    void createApplicationDictionary(ApplicationDictionary applicationDictionary);

    void updateApplicationDictionary(ApplicationDictionary applicationDictionary);

    void deleteApplicationDictionary(String id);

    List<ApplicationDictionary> listApplicationDictionary(Map<String, Object> params, PageDesc pageDesc);

    ApplicationDictionary getApplicationDictionary(String id);

    void deleteDataDictionary(String dictionaryId);

}
