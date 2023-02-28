package com.centit.platform.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.model.basedata.IOsInfo;
import com.centit.framework.system.po.OsInfo;

import java.util.Map;

/**
 * @author zhf
 */
public interface ApplicationInfoManager {
    JSONObject createApplicationInfo(OsInfo osInfo);

    JSONArray listApplicationInfo(String topUnit, Map<String, Object> parameters);

    JSONObject getApplicationInfo(String applicationId,String topUnit,boolean checkAuth);

    IOsInfo deleteApplicationInfo(String applicationId);

    IOsInfo updateApplicationInfo(OsInfo osInfo);

    /**
     * 获取资源详情及使用情况
     * @param parameters
     * @return
     */
    JSONObject getResourceInfo(Map<String, Object> parameters);
}
