package com.centit.platform.service;

import com.centit.platform.po.ApplicationResources;
import com.centit.support.database.utils.PageDesc;

import java.util.List;
import java.util.Map;

/**
 * @author tian_y
 */
public interface ApplicationResourcesService {

    void createApplicationResources(ApplicationResources applicationRescourse);

    void updateApplicationResources(ApplicationResources applicationRescourse);

    void deleteApplicationResources(String id);

    List<ApplicationResources> listApplicationResources(Map<String, Object> params, PageDesc pageDesc);

    ApplicationResources getApplicationResources(String id);

    void deleteAppResources(String osId, String dataBaseId);

    List<ApplicationResources> listObjectsByProperty(Map<String, Object> propertiesMap);
}
