package com.centit.platform.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.model.basedata.IOsInfo;
import com.centit.framework.system.po.OsInfo;

import java.util.Map;

/**
 * @author zhf
 */
public interface ApplicationInfoManager {
    JSONObject createApplicationInfo(OsInfo osInfo);

    JSONArray listApplicationInfo(String topUnit, Map<String, Object> parameters);

    JSONObject getApplicationInfo(String applicationId,String topUnit);

    IOsInfo deleteApplicationInfo(String applicationId);

    IOsInfo updateApplicationInfo(OsInfo osInfo);
}
