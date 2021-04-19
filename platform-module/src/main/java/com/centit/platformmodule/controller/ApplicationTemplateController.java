package com.centit.platformmodule.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.fileserver.utils.UploadDownloadUtils;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.platformmodule.po.ApplicationTemplate;
import com.centit.platformmodule.service.ApplicationTemplateManager;
import com.centit.platformmodule.service.ModelExportManager;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.file.FileSystemOpt;
import com.centit.support.file.FileType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * @author zhf
 */
@RestController
@RequestMapping(value = "applicationTemplate")
@Api(value = "应用模板管理", tags = "应用模板管理")
public class ApplicationTemplateController extends BaseController {
    @Autowired
    private ApplicationTemplateManager applicationTemplateManager;
    @Autowired
    private ModelExportManager modelExportManager;

    @ApiOperation(value = "保存应用模板")
    @PostMapping
    @WrapUpResponseBody
    public void createApplicationInfo(ApplicationTemplate applicationTemplate, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Pair<String, InputStream> fileField = UploadDownloadUtils.fetchInputStreamFromMultipartResolver(request);
        applicationTemplate.setTemplateContent(StreamUtils.copyToByteArray(fileField.getRight()));
        applicationTemplateManager.mergeApplicationTemplate(applicationTemplate);
        JsonResultUtils.writeSingleDataJson(applicationTemplate.getTemplateId(), response);
    }


    @ApiOperation(value = "删除应用模板")
    @ApiImplicitParam(name = "templateId", value = "模板ID")
    @DeleteMapping(value = "/{templateId}")
    @WrapUpResponseBody
    public void deleteApplicationTemplate(@PathVariable String templateId) {
        applicationTemplateManager.deleteApplicationTemplate(templateId);
    }

    @ApiOperation(value = "查询应用模板")
    @GetMapping
    @WrapUpResponseBody
    public PageQueryResult<ApplicationTemplate> listApplicationTemplate(HttpServletRequest request, PageDesc pageDesc) {
        Map<String, Object> searchColumn = collectRequestParameters(request);
        List<ApplicationTemplate> list = applicationTemplateManager.listApplicationTemplate(searchColumn, pageDesc);
        return PageQueryResult.createResult(list, pageDesc);
    }

    @ApiOperation(value = "查询单个应用模板")
    @GetMapping(value = "/{templateId}")
    @WrapUpResponseBody
    public ApplicationTemplate getApplicationTemplate(@PathVariable String templateId) {
        return applicationTemplateManager.getApplicationTemplate(templateId);
    }

    @ApiOperation(value = "下载应用模板")
    @GetMapping(value = "/downloadTemplate/{templateId}")
    public void downLoadTemplate(@PathVariable String templateId, HttpServletResponse response) throws IOException {
        ApplicationTemplate applicationTemplate = applicationTemplateManager.getApplicationTemplate(templateId);
        InputStream in = new ByteArrayInputStream(applicationTemplate.getTemplateContent());
        String fileName = URLEncoder.encode(applicationTemplate.getTemplateName(), "UTF-8") +
            ".zip";
        response.setContentType(FileType.mapExtNameToMimeType("zip"));
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        IOUtils.copy(in, response.getOutputStream());
    }

    @ApiOperation(value = "根据模板创建应用")
    @RequestMapping(value = "/createApp", method = {RequestMethod.POST})
    @WrapUpResponseBody
    public void upLoadModel(String templateId, HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
        ApplicationTemplate applicationTemplate = applicationTemplateManager.getApplicationTemplate(templateId);
        JSONObject jsonObject = new JSONObject();
        File tempZip = FileSystemOpt.createTmpFile(new ByteArrayInputStream(applicationTemplate.getTemplateContent()), applicationTemplate.getTemplateId(), "zip");
        jsonObject.put("objList", modelExportManager.uploadModel(tempZip,
            "F", StringBaseOpt.emptyValue(WebOptUtils.getCurrentUserCode(request), "admin")));
        JsonResultUtils.writeSingleDataJson(jsonObject, response);
    }
}
