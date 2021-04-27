package com.centit.platformmodule.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.fileserver.common.FileStore;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.platformmodule.service.ApplicationTemplateManager;
import com.centit.platformmodule.service.ModelExportManager;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.file.FileType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @ApiOperation(value = "导入zip覆盖应用")
    @RequestMapping(value = "/updateApp", method = {RequestMethod.POST})
    @WrapUpResponseBody
    public Integer upLoadModel(String fileId, HttpServletRequest request) throws Exception {
        JSONObject jsonObject = modelExportManager.uploadModel(fileStore.getFile(fileId));
        return modelExportManager.createApp(jsonObject, "T",
            StringBaseOpt.emptyValue(WebOptUtils.getCurrentUserCode(request), "admin"));
    }
}
