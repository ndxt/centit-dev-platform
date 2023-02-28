package com.centit.platform.service;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.security.model.CentitUserDetails;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public interface ModelExportManager {
    String downModel(String osId) throws FileNotFoundException;
    JSONObject uploadModel(File zipFile) throws Exception;
    Integer createApp(JSONObject jsonObject, String osId, CentitUserDetails userDetails);

    JSONObject prepareApp(JSONObject jsonObject, String osId, CentitUserDetails currentUserDetails);

    Integer importApp(JSONObject jsonObject) throws Exception;
}
