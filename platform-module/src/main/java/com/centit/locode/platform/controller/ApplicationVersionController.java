package com.centit.locode.platform.controller;

import com.alibaba.fastjson2.JSONArray;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.locode.platform.po.ApplicationVersion;
import com.centit.locode.platform.service.ApplicationVersionService;
import com.centit.support.database.utils.PageDesc;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/appHistory")
@Api(tags = {"应用的历史版本管理接口"}, value = "应用的历史版本管理接口")
public class ApplicationVersionController extends BaseController
{

    @Autowired
    ApplicationVersionService applicationVersionService;

    @ApiOperation(value = "列举所有历史版本", notes = "列举所有历史版本")
    @ApiImplicitParam(
        name = "pageDesc", value = "json格式，分页对象信息",
        paramType = "body", dataTypeClass = PageDesc.class)
    @GetMapping("list/{osId}")
    @WrapUpResponseBody()
    public PageQueryResult<ApplicationVersion> list(@PathVariable String osId, PageDesc pageDesc) {
        List<ApplicationVersion> appVers = applicationVersionService.listApplicationVersion(osId, pageDesc);
        return PageQueryResult.createResult(appVers, pageDesc);
    }

    @ApiOperation(value = "创建历史版本", notes = "创建历史版本")
    @PostMapping()
    @WrapUpResponseBody()
    public String createHistoryVersion(@RequestBody ApplicationVersion appVersion) {
        return applicationVersionService.createApplicationVersion(appVersion);
    }

    /*@ApiOperation(value = "下载历史版本", notes = "download")
    @GetMapping("download/{versionId}")
    @WrapUpResponseBody()
    public void downloadHistoryVersion(@RequestBody String osId) {

    }*/

    @ApiOperation(value = "比较历史版本", notes = "比较历史版本")
    @GetMapping("compare/{versionId}/{versionId2}")
    @WrapUpResponseBody()
    public JSONArray compareHistoryVersion(@PathVariable String versionId, @PathVariable String versionId2) {
        return applicationVersionService.compareTwoVersion(versionId, versionId2);
    }

    @ApiOperation(value = "和最新的内容对比", notes = "和最新的内容对比")
    @GetMapping("diff/{osId}/{versionId}")
    @WrapUpResponseBody()
    public JSONArray compareToOldVersion(@PathVariable String osId, @PathVariable String versionId) {
        return applicationVersionService.compareToOldVersion(osId, versionId);
    }

    @ApiOperation(value = "删除历史版本", notes = "删除历史版本")
    @DeleteMapping("{versionId}")
    @WrapUpResponseBody()
    public void deleteHistoryVersion(@PathVariable String versionId) {
        applicationVersionService.deleteApplicationVersion(versionId);
    }
}
