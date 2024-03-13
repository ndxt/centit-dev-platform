package com.centit.locode.platform.controller;

import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.support.database.utils.PageDesc;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/appHistory")
@Api(tags = {"应用的历史版本管理接口"}, value = "应用的历史版本管理接口")
public class ApplicationVersionController extends BaseController
{
    @ApiOperation(value = "列举所有历史版本", notes = "列举所有历史版本")
    @ApiImplicitParam(
        name = "pageDesc", value = "json格式，分页对象信息",
        paramType = "body", dataTypeClass = PageDesc.class)
    @GetMapping("list/{osId}")
    @WrapUpResponseBody()
    public PageQueryResult<Object> list(@PathVariable String osId, PageDesc pageDesc) {

        return null;
    }

    @ApiOperation(value = "创建历史版本", notes = "创建历史版本")
    @PostMapping()
    @WrapUpResponseBody()
    public void createHistoryVersion(@RequestBody String osId) {


    }

    @ApiOperation(value = "下载历史版本", notes = "download")
    @GetMapping("download/{versionId}")
    @WrapUpResponseBody()
    public void downloadHistoryVersion(@RequestBody String osId) {


    }

    @ApiOperation(value = "比较历史版本", notes = "比较历史版本")
    @GetMapping("compare/{versionId}/{versionId2}")
    @WrapUpResponseBody()
    public void compareHistoryVersion(@RequestBody String osId) {


    }


    @ApiOperation(value = "删除历史版本", notes = "删除历史版本")
    @DeleteMapping("{versionId}")
    @WrapUpResponseBody()
    public void deleteHistoryVersion(@RequestBody String osId) {


    }
}
