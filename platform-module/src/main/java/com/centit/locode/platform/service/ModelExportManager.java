package com.centit.locode.platform.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.model.basedata.OsInfo;
import com.centit.framework.model.security.CentitUserDetails;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface ModelExportManager {
    String downModel(String osId, Map<String, Object> parameters) throws IOException;

    String exportModelAndSaveToFileServer(OsInfo osInfo) throws IOException;

    JSONObject uploadModel(File zipFile) throws Exception;

    JSONObject prepareApp(JSONObject jsonObject, String osId, CentitUserDetails currentUserDetails);

    JSONArray importApp(JSONObject jsonObject, CentitUserDetails userDetails) throws Exception;
}
