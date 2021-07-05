package com.centit.platform.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.fileserver.common.FileStore;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.platform.po.ApplicationTemplate;
import com.centit.platform.service.ApplicationTemplateManager;
import com.centit.platform.service.ModelExportManager;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.database.utils.PageDesc;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
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

    @Autowired(required = false)
    private FileStore fileStore;

    @ApiOperation(value = "保存应用模板")
    @PostMapping
    @WrapUpResponseBody
    public void createApplicationInfo(ApplicationTemplate applicationTemplate, HttpServletRequest request, HttpServletResponse response) throws IOException {
        File file = fileStore.getFile(applicationTemplate.getFileId());
        try {
            applicationTemplate.setTemplateContent(modelExportManager.uploadModel(file));
            applicationTemplateManager.mergeApplicationTemplate(applicationTemplate);
            JsonResultUtils.writeSingleDataJson(applicationTemplate.getTemplateId(), response);
        } catch (Exception e) {
            JsonResultUtils.writeErrorMessageJson(e.getLocalizedMessage(),response);
        }
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

    @ApiOperation(value = "传入json创建应用")
    @RequestMapping(value = "/createApp", method = {RequestMethod.POST})
    @WrapUpResponseBody
    public Integer createApp(@RequestBody JSONObject jsonObject, HttpServletRequest request)  {
        return modelExportManager.createApp(jsonObject, "F",
            StringBaseOpt.emptyValue(WebOptUtils.getCurrentUserCode(request), "admin"));
    }
}