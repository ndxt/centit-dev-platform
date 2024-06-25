package com.centit.locode.platform.service;

import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.model.basedata.OsInfo;
import com.centit.framework.model.security.CentitUserDetails;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public interface ModelExportManager {
    String downModel(String osId, Map<String, Object> parameters) throws FileNotFoundException;

    String exportModelAndSaveToFileServer(OsInfo osInfo) throws IOException;

    JSONObject uploadModel(File zipFile) throws Exception;
    Integer createApp(JSONObject jsonObject, String osId, CentitUserDetails userDetails);

    JSONObject prepareApp(JSONObject jsonObject, String osId, CentitUserDetails currentUserDetails);

    Integer importApp(JSONObject jsonObject, CentitUserDetails userDetails) throws Exception;
}
