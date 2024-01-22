package com.centit.locode.platform.service;

import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.jdbc.service.BaseEntityManager;
import com.centit.framework.tenant.po.AppInfo;

/**
 * @author tian_y
 */
public interface AppInfoService extends BaseEntityManager<AppInfo, String> {

    public JSONObject getLastAppInfo(String appType);
}
