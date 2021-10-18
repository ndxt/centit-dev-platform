package com.centit.platform.service;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.model.basedata.IOsInfo;
import com.centit.framework.system.po.OsInfo;

import java.util.List;
import java.util.Map;

/**
 * @author zhf
 */
public interface ApplicationInfoManager {
    JSONObject createApplicationInfo(OsInfo osInfo);

    List<? extends IOsInfo> listApplicationInfo(String topUnit, Map<String, Object> parameters);

    JSONObject getApplicationInfo(String applicationId);

    IOsInfo deleteApplicationInfo(String applicationId);

    IOsInfo updateApplicationInfo(OsInfo osInfo);
}
