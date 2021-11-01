package com.centit.platform.controller;

import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.platform.po.GroupInfo;
import com.centit.platform.service.GroupInfoManager;
import com.centit.support.database.utils.PageDesc;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author zhf
 */
@RestController
@RequestMapping(value = "group")
@Api(value = "分组管理", tags = "分组管理")
public class GroupInfoController extends BaseController{
    @Autowired
    private GroupInfoManager groupInfoManager;
    @ApiOperation(value = "新增应用")
    @PostMapping
    @WrapUpResponseBody
    public void createGroupInfo(GroupInfo groupInfo, HttpServletResponse response){
        groupInfoManager.createGroupInfo(groupInfo);
        JsonResultUtils.writeSingleDataJson(groupInfo.getGroupId(),response);
    }
    @ApiOperation(value = "修改应用")
    @PutMapping
    @WrapUpResponseBody
    public void updateGroupInfo(@RequestBody GroupInfo groupInfo){
        groupInfoManager.updateGroupInfo(groupInfo);
    }
    @ApiOperation(value = "删除应用模块")
    @ApiImplicitParam(name = "groupId", value = "分组ID")
    @DeleteMapping(value = "/{groupId}")
    @WrapUpResponseBody
    public void deleteGroupInfo(@PathVariable String groupId){
        groupInfoManager.deleteGroupInfo(groupId);
    }
    @ApiOperation(value = "查询应用模块")
    @GetMapping
    @WrapUpResponseBody
    public PageQueryResult<GroupInfo> listGroupInfo(HttpServletRequest request,PageDesc pageDesc){
        Map<String, Object> searchColumn = collectRequestParameters(request);
        List<GroupInfo> list = groupInfoManager.listGroupInfo(searchColumn, pageDesc);
        return PageQueryResult.createResult(list, pageDesc);
    }

    @ApiOperation(value = "查询单个分组")
    @GetMapping(value = "/{groupId}")
    @WrapUpResponseBody
    public GroupInfo getGroupInfo(@PathVariable String groupId){
        return groupInfoManager.getGroupInfo(groupId);
    }
}
