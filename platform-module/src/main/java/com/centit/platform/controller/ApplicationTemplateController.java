package com.centit.platform.controller;

import com.alibaba.fastjson2.JSONObject;
import com.centit.fileserver.utils.SystemTempFileUtils;
import com.centit.fileserver.utils.UploadDownloadUtils;
import com.centit.framework.common.GlobalConstValue;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.platform.po.ApplicationTemplate;
import com.centit.platform.service.ApplicationTemplateManager;
import com.centit.platform.service.ModelExportManager;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.ObjectException;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.file.FileSystemOpt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author zhf
 */
@RestController
@RequestMapping(value = "applicationTemplate")
@Api(value = "应用模板管理", tags = "应用模板管理")
public class ApplicationTemplateController extends BaseController {
    private static final String TEMPLATE_ID = "templateId";
    private static final String IS_USED = "isUsed";
    @Autowired
    private ApplicationTemplateManager applicationTemplateManager;

    @Autowired
    private ModelExportManager modelExportManager;

    @ApiOperation(value = "保存应用模板")
    @PostMapping(value = "/saveApp")
    public void createApplicationInfo(HttpServletRequest request, HttpServletResponse response){
        if(GlobalConstValue.SYSTEM_TENANT_TOP_UNIT.equals(WebOptUtils.getCurrentTopUnit(request))) {
            ApplicationTemplate applicationTemplate = new ApplicationTemplate();
            if(!StringBaseOpt.isNvl(request.getParameter(TEMPLATE_ID))){
                applicationTemplate.setTemplateId(request.getParameter(TEMPLATE_ID));
            }
            applicationTemplate.setTemplateName(request.getParameter("templateName"));
            applicationTemplate.setTemplateType(request.getParameter("templateType"));
            applicationTemplate.setPicId(request.getParameter("picId"));
            applicationTemplate.setTemplateMemo(request.getParameter("templateMemo"));
            if(request.getParameter(IS_USED)==null || "".equals(request.getParameter(IS_USED))){
                applicationTemplate.setIsUsed("T");
            }else{
                applicationTemplate.setIsUsed(request.getParameter(IS_USED));
            }
            FileSystemOpt.createDirect(SystemTempFileUtils.getTempDirectory());
            String tempFilePath = SystemTempFileUtils.getRandomTempFilePath();
            try {
                InputStream inputStream = UploadDownloadUtils.fetchInputStreamFromMultipartResolver(request).getRight();
                File file = new File(tempFilePath);
                FileUtils.copyInputStreamToFile(inputStream, file);
                if(file.length()!=0) {
                    applicationTemplate.setTemplateContent(modelExportManager.uploadModel(file));
                }
                applicationTemplateManager.mergeApplicationTemplate(applicationTemplate);
                JsonResultUtils.writeSingleDataJson(applicationTemplate, response);
            } catch (Exception e) {
                JsonResultUtils.writeErrorMessageJson(e.getLocalizedMessage(), response);
            } finally {
                FileSystemOpt.deleteFile(tempFilePath);
            }
        }else{
            JsonResultUtils.writeErrorMessageJson("只有平台管理员有权限", response);
        }
    }

    @ApiOperation(value = "删除应用模板")
    @ApiImplicitParam(name = TEMPLATE_ID, value = "模板ID")
    @DeleteMapping(value = "/{templateId}")
    @WrapUpResponseBody
    public void deleteApplicationTemplate(@PathVariable String templateId,HttpServletRequest request,HttpServletResponse response) {
        if(GlobalConstValue.SYSTEM_TENANT_TOP_UNIT.equals(WebOptUtils.getCurrentTopUnit(request))) {
            applicationTemplateManager.deleteApplicationTemplate(templateId);
        }else{
            JsonResultUtils.writeErrorMessageJson("只有平台管理员有权限", response);
        }
    }

    @ApiOperation(value = "查询应用模板")
    @GetMapping
    @WrapUpResponseBody
    public PageQueryResult<ApplicationTemplate> listApplicationTemplate(HttpServletRequest request, PageDesc pageDesc) {
        Map<String, Object> searchColumn = collectRequestParameters(request);
        if(!GlobalConstValue.SYSTEM_TENANT_TOP_UNIT.equals(WebOptUtils.getCurrentTopUnit(request))) {
          searchColumn.put(IS_USED,"T");
        }
        List<ApplicationTemplate> list = applicationTemplateManager.listApplicationTemplate(searchColumn, pageDesc);
        return PageQueryResult.createResult(list, pageDesc);
    }

    @ApiOperation(value = "查询单个应用模板")
    @GetMapping(value = "/{templateId}")
    @WrapUpResponseBody
    public ApplicationTemplate getApplicationTemplate(@PathVariable String templateId) {
        return applicationTemplateManager.getApplicationTemplate(templateId);
    }

    @ApiOperation(value = "根据模板创建应用")
    @RequestMapping(value = "/createApp", method = {RequestMethod.POST})
    @WrapUpResponseBody
    public Integer createApp(@RequestBody JSONObject jsonObject, HttpServletRequest request)  {
        CentitUserDetails userDetails = WebOptUtils.getCurrentUserDetails(request);
        if (userDetails==null){
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN, "您未登录，请先登录！");
        }
        if(jsonObject==null){
            throw new ObjectException(ResponseData.ERROR_BAD_REQUEST,"导入内容没有填写");
        }
        return modelExportManager.createApp(jsonObject, "",
            WebOptUtils.getCurrentUserDetails(request));
    }

    @ApiOperation(value = "导入覆盖应用")
    @RequestMapping(value = "/updateApp/{osId}", method = {RequestMethod.POST})
    @WrapUpResponseBody
    public Integer createApp(@RequestBody JSONObject jsonObject, HttpServletRequest request,
                             @PathVariable String osId)  {
        CentitUserDetails userDetails = WebOptUtils.getCurrentUserDetails(request);
        if (userDetails==null){
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN, "您未登录，请先登录！");
        }
        if(jsonObject==null){
            throw new ObjectException(ResponseData.ERROR_BAD_REQUEST,"导入内容没有填写");
        }
        return modelExportManager.createApp(jsonObject, osId,
            WebOptUtils.getCurrentUserDetails(request));
    }
    @ApiOperation(value = "根据模板导入应用返回预处理结果")
    @RequestMapping(value = "/prepareTemplateApp", method = {RequestMethod.POST})
    @WrapUpResponseBody
    public JSONObject prepareTemplateApp(@RequestBody JSONObject jsonObject, HttpServletRequest request)  {
        CentitUserDetails userDetails = WebOptUtils.getCurrentUserDetails(request);
        if (userDetails==null){
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN, "您未登录，请先登录！");
        }
        if(jsonObject==null){
            throw new ObjectException(ResponseData.ERROR_BAD_REQUEST,"导入内容没有填写");
        }
        return modelExportManager.prepareApp(jsonObject, "",
            WebOptUtils.getCurrentUserDetails(request));
    }
    @ApiOperation(value = "导入应用返回预处理结果")
    @RequestMapping(value = "/prepareUpdateApp/{osId}", method = {RequestMethod.POST})
    @WrapUpResponseBody
    public JSONObject prepareApp(@RequestBody JSONObject jsonObject, HttpServletRequest request,
                             @PathVariable String osId)  {
        CentitUserDetails userDetails = WebOptUtils.getCurrentUserDetails(request);
        if (userDetails==null){
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN, "您未登录，请先登录！");
        }
        if(jsonObject==null){
            throw new ObjectException(ResponseData.ERROR_BAD_REQUEST,"导入内容没有填写");
        }
        return modelExportManager.prepareApp(jsonObject, osId,
            WebOptUtils.getCurrentUserDetails(request));
    }
    @ApiOperation(value = "导入应用返回预处理结果")
    @RequestMapping(value = "/importApp", method = {RequestMethod.POST})
    @WrapUpResponseBody
    public Integer importApp(@RequestBody JSONObject jsonObject, HttpServletRequest request) throws Exception {
        CentitUserDetails userDetails = WebOptUtils.getCurrentUserDetails(request);
        if (userDetails==null){
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN, "您未登录，请先登录！");
        }
        if(jsonObject==null){
            throw new ObjectException(ResponseData.ERROR_BAD_REQUEST,"导入内容没有填写");
        }
        return modelExportManager.importApp(jsonObject);
    }
}
