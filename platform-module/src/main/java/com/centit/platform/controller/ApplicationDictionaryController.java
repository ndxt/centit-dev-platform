package com.centit.platform.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.IDataCatalog;
import com.centit.framework.system.po.DataCatalog;
import com.centit.platform.po.ApplicationDictionary;
import com.centit.platform.service.ApplicationDictionaryService;
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
@Api(value = "应用数据字典关联", tags = "应用数据字典关联")
@RestController
@RequestMapping(value = "/appDictionary")
public class ApplicationDictionaryController extends BaseController {

    @Autowired
    private ApplicationDictionaryService applicationDictionaryService;

    @Autowired
    private PlatformEnvironment platformEnvironment;

    @ApiOperation(value = "新增关联信息")
    @PostMapping()
    @WrapUpResponseBody
    public void createApplicationDictionary(@RequestBody JSONObject jsonObject, HttpServletResponse response){
        String osId = jsonObject.getString("osId");
        JSONArray dictionaryIds = jsonObject.getJSONArray("dictionaryIds");
        String pushUser = jsonObject.getString("pushUser");
        if (dictionaryIds != null && !dictionaryIds.isEmpty()) {
            for(int i = 0; i < dictionaryIds.size(); i ++){
                ApplicationDictionary app = new ApplicationDictionary();
                app.setOsId(osId);
                app.setDictionaryId(dictionaryIds.get(i) + "");
                app.setPushUser(pushUser);
                applicationDictionaryService.createApplicationDictionary(app);
            }
        }
        JsonResultUtils.writeSingleDataJson(dictionaryIds,response);
    }

    @ApiOperation(value = "修改关联信息")
    @PutMapping()
    @WrapUpResponseBody
    public void update(@RequestBody ApplicationDictionary applicationDictionary){
        applicationDictionaryService.updateApplicationDictionary(applicationDictionary);
    }

    @ApiOperation(value = "删除关联信息")
    @DeleteMapping(value = "/{id}")
    @WrapUpResponseBody
    public void deleteById(@PathVariable String id){
        applicationDictionaryService.deleteApplicationDictionary(id);
    }

    @ApiOperation(value = "查询关联信息列表")
    @GetMapping("/list")
    @WrapUpResponseBody
    public PageQueryResult list(String[] field, HttpServletRequest request, PageDesc pageDesc){
        List<ApplicationDictionary> list = applicationDictionaryService.listApplicationDictionary(BaseController.collectRequestParameters(request), pageDesc);
        List<Map<String, Object>> resultList = new ArrayList();
        Map<String, Object> dataMap = new HashMap<>();
        if (list != null && list.size() > 0) {
            for(ApplicationDictionary applicationDictionary : list) {
                Map<String, Object> resultMap = new HashMap<>();
                String dictionaryId = applicationDictionary.getDictionaryId();
                String topUnit = WebOptUtils.getCurrentTopUnit(request);
                List<? extends IDataCatalog> dataCatalogs =  platformEnvironment.listAllDataCatalogs(topUnit);
                for(IDataCatalog iDataCatalog : dataCatalogs){
                    if(iDataCatalog != null && dictionaryId.equals(iDataCatalog.getCatalogCode())){
                        DataCatalog dataCatalog = (DataCatalog)iDataCatalog;
                        dataMap.put("catalogName", dataCatalog.getCatalogName());
                        dataMap.put("catalogStyle", dataCatalog.getCatalogStyle());
                        dataMap.put("optId", dataCatalog.getOptId());
                        dataMap.put("catalogType",dataCatalog.getCatalogType());
                    }
                }
                if(dataMap != null && !dataMap.isEmpty()){
                    resultMap.put("id",applicationDictionary.getId());
                    resultMap.put("osId",applicationDictionary.getOsId());
                    resultMap.put("catalogCode", dictionaryId);
                    resultMap.put("pushTime",applicationDictionary.getPushTime());
                    resultMap.put("pushUser",applicationDictionary.getPushUser());
                    resultMap.put("catalogName",dataMap.get("catalogName"));
                    resultMap.put("catalogStyle",dataMap.get("catalogStyle"));
                    resultMap.put("catalogType",dataMap.get("catalogType"));
                    resultMap.put("optId",dataMap.get("optId"));
                    dataMap.clear();
                    resultList.add(resultMap);
                }else{
                    //未查询到数据字典，说明数据字典被删除，删除关联信息
                    applicationDictionaryService.deleteApplicationDictionary(applicationDictionary.getId());
                }
            }
        }
        return PageQueryResult.createJSONArrayResult(JSONArray.parseArray(JSON.toJSONString(resultList)), pageDesc, new Class[]{DataCatalog.class});
    }

    @ApiOperation(value = "查询单个关联信息")
    @GetMapping(value = "/{id}")
    @WrapUpResponseBody
    public ApplicationDictionary getHistory(@PathVariable String id) {
        return applicationDictionaryService.getApplicationDictionary(id);
    }


}
