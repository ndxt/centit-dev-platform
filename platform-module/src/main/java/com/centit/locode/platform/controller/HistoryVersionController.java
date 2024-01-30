package com.centit.locode.platform.controller;

import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.locode.platform.po.HistoryVersion;
import com.centit.locode.platform.service.HistoryVersionService;
import com.centit.support.common.ObjectException;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.json.JSONOpt;
import com.centit.support.json.JsonDifferent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
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
    @PostMapping()
    @WrapUpResponseBody
    public String createHistoryVersion(@RequestBody HistoryVersion historyVersion){
        historyVersion.setAppVersionId(null);
        historyVersionService.createHistoryVersion(historyVersion);
        return historyVersion.getHistoryId();
    }

    @ApiOperation(value = "修改版本信息")
    @PutMapping()
    @WrapUpResponseBody
    public void updateHistory(@RequestBody HistoryVersion historyVersion) {
        HistoryVersion dbVersion = historyVersionService.getHistoryVersion(historyVersion.getHistoryId());
        if(dbVersion==null){
            throw new ObjectException(ObjectException.DATA_VALIDATE_ERROR, "需要修改的版本信息不存在!");
        }
        if(StringUtils.isNotBlank(dbVersion.getAppVersionId())){
            throw new ObjectException(ObjectException.DATA_VALIDATE_ERROR, "应用全局版本，不能修改!");
        }
        historyVersion.setAppVersionId(null);
        historyVersionService.updateHistoryVersion(historyVersion);
    }

    @ApiOperation(value = "删除版本信息")
    @DeleteMapping(value = "/{historyId}")
    @WrapUpResponseBody
    public void deleteHistory(@PathVariable String historyId) {
        HistoryVersion dbVersion = historyVersionService.getHistoryVersion(historyId);
        if(dbVersion==null){
            return;
        }
        if(StringUtils.isNotBlank(dbVersion.getAppVersionId())){
            throw new ObjectException(ObjectException.DATA_VALIDATE_ERROR, "应用全局版本，不能删除!");
        }
        historyVersionService.deleteHistoryVersion(historyId);
    }

    @ApiOperation(value = "查询版本信息列表")
    @GetMapping("/list")
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

    @ApiOperation(value = "和历史版本对比")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "historyId", type = "path", value = "历史版本信息"),
        @ApiImplicitParam(name = "jsonType", type = "param", paramType = "String", value = "返回结果机构 tree / object"),
        @ApiImplicitParam(name = "currentVersion", type="body", value="当前版本信息，一般是草稿信息", dataTypeClass = HistoryVersion.class)})
    @PostMapping("/compare/{historyId}")
    @WrapUpResponseBody
    public JSONObject compareHistoryVersion(@PathVariable String historyId,
                                            @RequestBody HistoryVersion currentVersion,
                                            String jsonType) {
        // historyVersion.content
        HistoryVersion historyVersion = historyVersionService.getHistoryVersion(historyId);
        if(historyVersion==null || historyVersion.getContent() == null || currentVersion.getContent()==null){
            throw new ObjectException(ObjectException.DATA_VALIDATE_ERROR, "版本信息有误，请检查输入参数");
        }
        JsonDifferent jsonDiff = JSONOpt.diff(currentVersion.getContent(), historyVersion.getContent(), "$item.id", "id");
        if("tree".equals(jsonType)){
            return JSONObject.from(jsonDiff);
        }
        return jsonDiff.toJSONObject();
    }

    @ApiOperation(value = "两个历史版本对比")
    @ApiImplicitParams({
        @ApiImplicitParam(),
        @ApiImplicitParam(name = "jsonType", type = "param", paramType = "String", value = "返回结果机构 tree / object"),
        @ApiImplicitParam(name = "versionInfo", type="body", value="两个历史版本信息，{'version1':'v1', 'version2':'v2'}")})
    @PostMapping("/compareHistory")
    @WrapUpResponseBody
    public JSONObject compareHistoryVersion(@RequestBody String versionInfo,
                                            String jsonType) {
        JSONObject versionJson = JSONObject.parseObject(versionInfo);
        String v1 = versionJson.getString("version1");
        String v2 = versionJson.getString("version2");
        if(StringUtils.isBlank(v1) || StringUtils.isBlank(v2)){
            throw new ObjectException(ObjectException.DATA_VALIDATE_ERROR, "版本信息有误，请检查输入参数");
        }
        HistoryVersion currentVersion = historyVersionService.getHistoryVersion(v1);
        HistoryVersion historyVersion = historyVersionService.getHistoryVersion(v2);

        if(historyVersion.getContent() == null || currentVersion.getContent()==null){
            throw new ObjectException(ObjectException.DATA_VALIDATE_ERROR, "版本信息有误，请检查输入参数");
        }
        JsonDifferent jsonDiff = JSONOpt.diff(currentVersion.getContent(), historyVersion.getContent(), "$item.id", "id");
        if("tree".equals(jsonType)){
            return JSONObject.from(jsonDiff);
        }
        return jsonDiff.toJSONObject();
    }
}
