package com.centit.platform.service;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.model.basedata.IOsInfo;
import java.util.List;

/**
 * @author zhf
 */
public interface ApplicationInfoManager {
    JSONObject createApplicationInfo(JSONObject osInfo);

    List<? extends IOsInfo> listApplicationInfo(String topUnit);

    JSONObject getApplicationInfo(String applicationId);

    IOsInfo deleteApplicationInfo(String applicationId);

    IOsInfo updateApplicationInfo(JSONObject osInfo);
}
