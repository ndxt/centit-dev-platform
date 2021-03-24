package com.centit.platformmodule.service;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public interface ModelExportManager {
    InputStream downModel(String applicationId) throws FileNotFoundException;
    JSONObject uploadModel(File zipFile, String isCover,String userCode) throws Exception;
}
