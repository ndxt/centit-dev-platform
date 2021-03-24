package com.centit.platformmodule.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.fileserver.common.FileStore;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.platformmodule.po.ApplicationTemplate;
import com.centit.platformmodule.service.ApplicationTemplateManager;
import com.centit.platformmodule.service.ModelExportManager;
import com.centit.support.algorithm.GeneralAlgorithm;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.file.FileSystemOpt;
import com.centit.support.file.FileType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
@RestController
@RequestMapping(value = "modelExport")
@Api(value = "应用导入导出", tags = "模板导入导出")
public class ModelExportController extends BaseController {
    @Autowired
    private ModelExportManager modelExportManager;
    @Autowired
    private ApplicationTemplateManager applicationTemplateManager;
    @Autowired(required = false)
    private FileStore fileStore;
    @ApiOperation(value = "导出应用")
    @GetMapping(value = "/downloadModel/{applicationId}")
    public void downLoadModel(@PathVariable String applicationId, HttpServletResponse response) throws IOException {
        InputStream in = modelExportManager.downModel(applicationId);
        String fileName = URLEncoder.encode(applicationId, "UTF-8") +
            ".zip";
        response.setContentType(FileType.mapExtNameToMimeType("zip"));
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        IOUtils.copy(in, response.getOutputStream());
    }

    @ApiOperation(value = "导入zip创建应用")
    @RequestMapping(value = "/uploadModel", method = {RequestMethod.POST})
    @WrapUpResponseBody
    public JSONArray upLoadModel(String fileId, String isCover, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("objList", modelExportManager.uploadModel(fileStore.getFile(fileId), isCover,
            GeneralAlgorithm.nvl(WebOptUtils.getCurrentUserCode(request),"admin")));
        JsonResultUtils.writeSingleDataJson(jsonObject, response);
        return null;
    }
    @ApiOperation(value = "根据模板创建应用")
    @RequestMapping(value = "/createApp", method = {RequestMethod.POST})
    @WrapUpResponseBody
    public JSONArray upLoadModel(String templateId, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        ApplicationTemplate applicationTemplate=applicationTemplateManager.getApplicationTemplate(templateId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("objList", modelExportManager.uploadModel(FileSystemOpt.createTmpFile(new ByteArrayInputStream(applicationTemplate.getTemplateContent()),applicationTemplate.getTemplateId(),"zip"),
            "F", GeneralAlgorithm.nvl(WebOptUtils.getCurrentUserCode(request),"admin")));
        JsonResultUtils.writeSingleDataJson(jsonObject, response);
        return null;
    }
}
