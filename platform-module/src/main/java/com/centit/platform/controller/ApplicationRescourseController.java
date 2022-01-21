package com.centit.platform.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.platform.po.ApplicationRescourse;
import com.centit.platform.service.ApplicationRescourseService;
import com.centit.product.adapter.po.SourceInfo;
import com.centit.product.metadata.service.SourceInfoManager;
import com.centit.support.database.utils.PageDesc;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author tian_y
 */
@Api(value = "应用资源关联", tags = "应用资源关联")
@RestController
@RequestMapping(value = "/appRescourse")
public class ApplicationRescourseController extends BaseController  {

    @Autowired
    private ApplicationRescourseService applicationRescourseService;

    @Autowired
    private SourceInfoManager databaseInfoMag;

    @ApiOperation(value = "新增关联信息")
    @PostMapping()
    @WrapUpResponseBody
    public void createApplicationDictionary(@RequestBody JSONObject jsonObject, HttpServletResponse response){
        String osId = jsonObject.getString("osId");
        JSONArray dataBaseIds = jsonObject.getJSONArray("dataBaseIds");
        String pushUser = jsonObject.getString("pushUser");
        String dataBaseId = jsonObject.getString("dataBaseId");
        applicationRescourseService.deleteAppRescourse(osId, "");
        if (dataBaseIds != null && !dataBaseIds.isEmpty()) {
            for(int i = 0; i < dataBaseIds.size(); i++){
                ApplicationRescourse app = new ApplicationRescourse();
                app.setOsId(osId);
                app.setDataBaseId(dataBaseIds.get(i) + "");
                app.setPushUser(pushUser);
                if(dataBaseId != null && !"".equals(dataBaseId) && dataBaseId.equals(dataBaseIds.get(i) + "")){
                    //默认关系数据库--是
                    app.setIsUsed("1");
                }else{
                    //默认关系数据库--否
                    app.setIsUsed("0");
                }
                applicationRescourseService.createApplicationRescourse(app);
            }
            JsonResultUtils.writeSingleDataJson(dataBaseIds,response);
        }
    }

    @ApiOperation(value = "修改关联信息")
    @PostMapping("/update")
    @WrapUpResponseBody
    public void update(@RequestBody ApplicationRescourse applicationRescourse){
        applicationRescourseService.updateApplicationRescourse(applicationRescourse);
    }

    @ApiOperation(value = "删除关联信息")
    @DeleteMapping(value = "/{id}")
    @WrapUpResponseBody
    public void deleteById(@PathVariable String id){
        applicationRescourseService.deleteApplicationRescourse(id);
    }

    @ApiOperation(value = "查询关联信息列表")
    @GetMapping("/list")
    @WrapUpResponseBody
    public PageQueryResult list(HttpServletRequest request, PageDesc pageDesc){
        List<ApplicationRescourse> list = applicationRescourseService.listApplicationRescourse(BaseController.collectRequestParameters(request), pageDesc);
        Set<String> appSet = new HashSet<>();
        list.forEach(t -> {
            appSet.add(t.getDataBaseId());
        });
        Map<String, Object> searchColumn = new HashMap<>();
        if (WebOptUtils.isTenantTopUnit(request)) {
            searchColumn.put("topUnit", WebOptUtils.getCurrentTopUnit(request));
        }
        List<SourceInfo> sourceInfoList = databaseInfoMag.listObjects(searchColumn);
        Map<String, SourceInfo> maps = new HashMap<>();
        for (SourceInfo sourceInfo : sourceInfoList) {
            maps.put(sourceInfo.getDatabaseCode(), sourceInfo);
        }
        Iterator var4 = appSet.iterator();
        List<SourceInfo> newSourceInfos = new ArrayList<>();
        while(var4.hasNext()) {
            String uc = (String)var4.next();
            SourceInfo sourceInfo = maps.get(uc);
            if (sourceInfo != null) {
                newSourceInfos.add(sourceInfo);
            }
        }
        if(newSourceInfos != null && newSourceInfos.size() > 0){
            pageDesc.setTotalRows(newSourceInfos.size());
        }
        return PageQueryResult.createJSONArrayResult(JSONArray.parseArray(JSON.toJSONString(newSourceInfos)), pageDesc, new Class[]{SourceInfo.class});
    }

    @ApiOperation(value = "查询单个关联信息")
    @GetMapping(value = "/{id}")
    @WrapUpResponseBody
    public ApplicationRescourse getHistory(@PathVariable String id) {
        return applicationRescourseService.getApplicationRescourse(id);
    }
}
