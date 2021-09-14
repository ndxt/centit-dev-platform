package com.centit.platform.controller;

import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.platform.po.HistoryVersion;
import com.centit.platform.service.HistoryVersionService;
import com.centit.support.database.utils.PageDesc;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(value = "历史版本管理", tags = "历史版本管理")
@RestController
@RequestMapping(value = "/history")
public class HistoryVersionController extends BaseController {

    @Autowired
    private HistoryVersionService historyVersionService;

    @ApiOperation(value = "新增版本信息")
    @PostMapping("/addHistory")
    @WrapUpResponseBody
    public void createHistoryVersion(@RequestBody HistoryVersion historyVersion) {
         historyVersionService.createHistoryVersion(historyVersion);
    }

    @ApiOperation(value = "编辑版本信息")
    @PutMapping(value = "/updateHistory")
    @WrapUpResponseBody
    public void updateHistory(@RequestBody HistoryVersion historyVersion) {
        historyVersionService.updateHistoryVersion(historyVersion);
    }

    @ApiOperation(value = "删除版本信息")
    @DeleteMapping(value = "/{historyId}")
    @WrapUpResponseBody
    public void deleteHistory(@PathVariable String historyId) {
        historyVersionService.deleteHistoryVersion(historyId);
    }

    @ApiOperation(value = "查询版本信息列表")
    @GetMapping("/listHistory")
    @WrapUpResponseBody
    public PageQueryResult<HistoryVersion> listHistory(HttpServletRequest request, PageDesc pageDesc) {
        List<HistoryVersion> list = historyVersionService.listHistoryVersion(BaseController.collectRequestParameters(request), pageDesc);
        return PageQueryResult.createResult(list, pageDesc);
    }

    @ApiOperation(value = "查询单个版本信息")
    @GetMapping(value = "/{historyId}")
    @WrapUpResponseBody
    public HistoryVersion getHistory(@PathVariable String historyId) {
        return historyVersionService.getHistoryVersion(historyId);
    }
}
