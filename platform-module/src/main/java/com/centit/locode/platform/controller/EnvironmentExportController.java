package com.centit.locode.platform.controller;

import com.centit.fileserver.utils.UploadDownloadUtils;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.locode.platform.service.EnvironmentExportManager;
import com.centit.support.common.ObjectException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author codefan@sina.com
 */
@Controller
@RequestMapping(value = "/exchange")
@Api(value = "应用资源导入导出", tags = "runtime环境导入导出")
public class EnvironmentExportController extends BaseController {

    @Autowired
    private EnvironmentExportManager environmentExportManager;

    @Autowired
    private PlatformEnvironment platformEnvironment;

    @ApiOperation(value = "导出应用的运行时环境")
    @ApiImplicitParam(name = "osId", type = "path", value = "应用ID")
    @GetMapping(value = "/export/{osId}")
    public void exportEnvironment(@PathVariable String osId,
                                  HttpServletRequest request, HttpServletResponse response) throws IOException {
        CentitUserDetails ud = WebOptUtils.getCurrentUserDetails(request);

        if(ud == null){
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN, "用户没有登录，没有对应的权限！");
        }
        if(!platformEnvironment.loginUserIsExistWorkGroup(osId, ud.getUserCode())){
            throw new ObjectException(ResponseData.ERROR_FORBIDDEN, "用户没有权限导出这个应用："+osId+"！");
        }

        InputStream zipFileStream = environmentExportManager.exportApplication(osId, ud);

        UploadDownloadUtils.downloadFile(zipFileStream,osId+".zip", request, response);
        /*UploadDownloadUtils.downFileRange(request, response,
            zipFileStream, zipFileStream.available(),osId+".zip", "attachment", "utf-8");*/
    }

}
