package com.centit.platformmodule.service;

import com.centit.platformmodule.po.ApplicationInfo;
import com.centit.platformmodule.po.ApplicationTemplate;
import com.centit.support.database.utils.PageDesc;

import java.util.List;
import java.util.Map;

/**
 * @author zhf
 */
public interface ApplicationTemplateManager {
    void mergeApplicationTemplate(ApplicationTemplate applicationTemplate);

    List<ApplicationTemplate> listApplicationTemplate(Map<String, Object> param, PageDesc pageDesc);

    ApplicationTemplate getApplicationTemplate(String templateId);

    void deleteApplicationTemplate(String templateId);

}