package com.centit.locode.platform.controller;

import com.alibaba.fastjson2.JSONArray;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.framework.model.basedata.UserInfo;
import com.centit.locode.platform.po.AppMergeTask;
import com.centit.locode.platform.po.ApplicationVersion;
import com.centit.locode.platform.service.ApplicationVersionService;
import com.centit.support.common.ObjectException;
import com.centit.support.database.utils.PageDesc;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Controller
@RequestMapping("/appHistory")
@Api(tags = {"应用的全局历史版本管理接口"}, value = "应用的全局历史版本管理接口")
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
    public String createHistoryVersion(@RequestBody ApplicationVersion appVersion, HttpServletRequest request) {
        UserInfo userInfo = WebOptUtils.assertUserLogin(request);
        if(applicationVersionService.checkMergeState(appVersion.getApplicationId())){
            throw new ObjectException(
                ObjectException.DATA_VALIDATE_ERROR, "当前应用有正在合并中的版本，请等待合并完成后再创建新版本！"
            );
        }
        appVersion.setCreator(userInfo.getUserCode());
        return applicationVersionService.createApplicationVersion(appVersion);
    }

    @ApiOperation(value = "更改历史版本信息", notes = "更改历史版本信息")
    @PutMapping()
    @WrapUpResponseBody()
    public void updateHistoryVersion(@RequestBody ApplicationVersion appVersion) {
        applicationVersionService.updateApplicationVersion(appVersion);
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

    @ApiOperation(value = "查看历史版本中的文件", notes = "查看历史版本中的文件")
    @GetMapping("view/{objType}/{versionId}")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "objType", value = "类型：1：工作流 2：页面设计 3：api网关",
            required = true, paramType = "path", dataType = "String"),
        @ApiImplicitParam(
            name = "appVersionId", value = "历史版本号", required = true,
            paramType = "path", dataType = "String")
    })
    @WrapUpResponseBody()
    public PageQueryResult<Object> viewObjectByType(@PathVariable String objType,
                                                    @PathVariable String appVersionId, PageDesc pageDesc) {
        JSONArray objs = applicationVersionService.listAppComponents(appVersionId, objType, pageDesc);
        return PageQueryResult.createJSONArrayResult(objs, pageDesc);
    }

    @ApiOperation(value = "恢复（回退）历史版本", notes = "回退后调用mergeTask查看更新内容")
    @PostMapping("/restore/{appVersionId}")
    @WrapUpResponseBody()
    public int restoreAppVersion(@PathVariable String appVersionId, HttpServletRequest request){
        UserInfo userInfo = WebOptUtils.assertUserLogin(request);
        return applicationVersionService.restoreAppVersion(appVersionId, userInfo.getUserCode());
    }

    @ApiOperation(value = "合并历史版本中的部分页面、接口和流程", notes = "合并后调用mergeTask查看更新内容")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "appVersionId", value = "历史版本号", required = true,
            paramType = "path", dataType = "String"),
        @ApiImplicitParam(
            name = "components", value = "选中的对象，属性包括 mergeTask中返回的 historyId 即可", required = true,
            paramType = "body", dataTypeClass = JSONArray.class)

    })
    @PostMapping("/merge/{appVersionId}")
    @WrapUpResponseBody()
    public int mergeAppComponents(@PathVariable String appVersionId,
                                  @RequestBody JSONArray components, HttpServletRequest request){
        UserInfo userInfo = WebOptUtils.assertUserLogin(request);
        return applicationVersionService.mergeAppComponents(appVersionId, components, userInfo.getUserCode());
    }

    @ApiOperation(value = "查看合并历史版本中的更新内容", notes = "查看合并历史版本中的更新内容")
    @GetMapping("mergeTask/{objType}/{versionId}")
    @WrapUpResponseBody()
    public PageQueryResult<AppMergeTask> listMergeTask(@PathVariable String appVersionId, PageDesc pageDesc,
                                                 HttpServletRequest request) {

        List<AppMergeTask> objs = applicationVersionService.listAppMergeTasks(
            appVersionId, collectRequestParameters(request), pageDesc);
        return PageQueryResult.createResult(objs, pageDesc);
    }

    @ApiOperation(value = "标记页面、接口、api合并完成", notes = "标记页面、接口、api合并完成")
    @PutMapping("/mergeCompleted")
    @WrapUpResponseBody()
    public void markMergeCompleted(@RequestBody AppMergeTask task,  HttpServletRequest request) {
        UserInfo userInfo = WebOptUtils.assertUserLogin(request);
        task.setUpdateUser(userInfo.getUserCode());
        applicationVersionService.mergeCompleted(task);
    }

    @ApiOperation(value = "标记恢复合并完成", notes = "标记恢复合并完成")
    @PutMapping("/restoreCompleted/{appVersionId}")
    @WrapUpResponseBody()
    public void markeRestoreCompleted(@PathVariable String appVersionId,  HttpServletRequest request) {
        WebOptUtils.assertUserLogin(request);
        applicationVersionService.restoreCompleted(appVersionId);
    }

}
