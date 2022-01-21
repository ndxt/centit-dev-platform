package com.centit.platform.service;

import com.centit.platform.po.ApplicationRescourse;
import com.centit.support.database.utils.PageDesc;

import java.util.List;
import java.util.Map;

public interface ApplicationRescourseService {

    void createApplicationRescourse(ApplicationRescourse applicationRescourse);

    void updateApplicationRescourse(ApplicationRescourse applicationRescourse);

    void deleteApplicationRescourse(String id);

    List<ApplicationRescourse> listApplicationRescourse(Map<String, Object> params, PageDesc pageDesc);

    ApplicationRescourse getApplicationRescourse(String id);

    void deleteAppRescourse(String osId, String dataBaseId);

    List<ApplicationRescourse> listObjectsByProperty(Map<String, Object> propertiesMap);
}
