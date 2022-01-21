package com.centit.platform.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.IDataCatalog;
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
        applicationDictionaryService.deleteAppDictionary(osId, "");
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
        Set<String> appSet = new HashSet<>();
        list.forEach(t -> {
            appSet.add(t.getDictionaryId());
        });
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        List<? extends IDataCatalog> dataCatalogs =  platformEnvironment.listAllDataCatalogs(topUnit);
        Map<String, IDataCatalog> maps = new HashMap<>();
        for (IDataCatalog dataCatalog : dataCatalogs) {
            maps.put(dataCatalog.getCatalogCode(), dataCatalog);
        }
        Iterator var4 = appSet.iterator();
        List<IDataCatalog> newDataCatalogs = new ArrayList<>();
        while(var4.hasNext()) {
            String uc = (String)var4.next();
            IDataCatalog dataCatalog = maps.get(uc);
            if (dataCatalog != null) {
                newDataCatalogs.add(dataCatalog);
            }
        }
        if(newDataCatalogs != null && newDataCatalogs.size() > 0){
            pageDesc.setTotalRows(newDataCatalogs.size());
        }
        return PageQueryResult.createResultMapDict(newDataCatalogs, pageDesc, field);
    }

    @ApiOperation(value = "查询单个关联信息")
    @GetMapping(value = "/{id}")
    @WrapUpResponseBody
    public ApplicationDictionary getHistory(@PathVariable String id) {
        return applicationDictionaryService.getApplicationDictionary(id);
    }

}
