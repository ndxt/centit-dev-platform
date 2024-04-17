package com.centit.locode.platform.controller;

import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.framework.model.basedata.UserInfo;
import com.centit.locode.platform.po.I18nMessages;
import com.centit.locode.platform.service.I18nMessagesService;
import com.centit.support.database.utils.PageDesc;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/i18n")
@Api(tags = "国际化", value = "移动端版本管理接口")
public class I18nMessagesController {

    @Autowired
    private I18nMessagesService i18nMessagesService;

    @ApiOperation(value = "分页查询")
    @GetMapping("/list/{osId}")
    @WrapUpResponseBody
    public PageQueryResult<I18nMessages> listMessages(@PathVariable String osId,
                                                      HttpServletRequest request, PageDesc pageDesc) {
        Map<String, Object> filterMap = BaseController.collectRequestParameters(request);
        filterMap.put("osId", osId);
        List<I18nMessages> list = i18nMessagesService.listI18nMessages(filterMap, pageDesc);
        return PageQueryResult.createResult(list, pageDesc);
    }

    @ApiOperation(value = "查询所有")
    @GetMapping("/listAll/{osId}")
    @WrapUpResponseBody
    public List<I18nMessages> listMessagesByOsId(@PathVariable String osId) {
        return i18nMessagesService.listI18nMessages(osId);
    }

    @ApiOperation(value = "获取当前语言所有的国际化信息")
    @GetMapping("/listCurrentLang/{osId}")
    @WrapUpResponseBody
    public Map<String, String> listCurrentLangMessagesByOsId(@PathVariable String osId,
                                                             HttpServletRequest request) {
        String lang = WebOptUtils.getCurrentLang(request);
        return i18nMessagesService.listI18nMessages(osId, lang);
    }

    @ApiOperation(value = "获取一条国际化条目")
    @GetMapping("/get/{osId}/{msgKey}")
    @WrapUpResponseBody
    public I18nMessages listMessagesByOsId(@PathVariable String osId, @PathVariable String msgKey) {
        return i18nMessagesService.getI18nMessages(osId, msgKey);
    }

    @ApiOperation(value = "新增国际化信息")
    @PostMapping()
    @WrapUpResponseBody
    public void createI8nMessage(@RequestBody @Valid I18nMessages i18nMessages,
                                 HttpServletRequest request){
        UserInfo userInfo = WebOptUtils.assertUserLogin(request);
        i18nMessages.setUpdateUser(userInfo.getUserCode());
        i18nMessagesService.saveI18nMessages(i18nMessages);
    }

    @ApiOperation(value = "修改国际化信息")
    @PutMapping()
    @WrapUpResponseBody
    public void updateI8nMessage(@RequestBody @Valid I18nMessages i18nMessages,
                                 HttpServletRequest request) {
        UserInfo userInfo = WebOptUtils.assertUserLogin(request);
        i18nMessages.setUpdateUser(userInfo.getUserCode());
        i18nMessagesService.updateI18nMessages(i18nMessages);
    }

    @ApiOperation(value = "删除国际化信息")
    @DeleteMapping(value = "/{osId}/{msgKey}")
    @WrapUpResponseBody
    public void deleteI8nMessage(@PathVariable String osId, @PathVariable String msgKey) {
        i18nMessagesService.deleteI18nMessages(osId, msgKey);
    }

}
