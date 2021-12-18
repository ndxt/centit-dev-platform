package com.centit.platform.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.fileserver.utils.SystemTempFileUtils;
import com.centit.fileserver.utils.UploadDownloadUtils;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.platform.service.ApplicationInfoManager;
import com.centit.platform.service.ModelExportManager;
import com.centit.support.common.ObjectException;
import com.centit.support.file.FileSystemOpt;
import com.centit.support.file.FileType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

@RestController
@RequestMapping(value = "modelExport")
@Api(value = "应用导入导出", tags = "模板导入导出")
public class ModelExportController extends BaseController {
    @Autowired
    private ModelExportManager modelExportManager;
    @Autowired
    private PlatformEnvironment platformEnvironment;

    @ApiOperation(value = "导出应用")
    @GetMapping(value = "/downloadModel/{applicationId}")
    public void downLoadModel(@PathVariable String applicationId, HttpServletResponse response) throws IOException {
        InputStream in = modelExportManager.downModel(applicationId);
        String fileName = platformEnvironment.getOsInfo(applicationId).getOsName();
        fileName = URLEncoder.encode(fileName, "UTF-8") +
            ".zip";
        response.setContentType(FileType.mapExtNameToMimeType("zip"));
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        IOUtils.copy(in, response.getOutputStream());
    }

    @ApiOperation(value = "导入zip覆盖应用")
    @RequestMapping(value = "/updateApp", method = {RequestMethod.POST})
    @WrapUpResponseBody
    public Integer upLoadModel(HttpServletRequest request) throws Exception {
        CentitUserDetails userDetails = WebOptUtils.getCurrentUserDetails(request);
        if (userDetails==null){
            throw new ObjectException(ResponseData.HTTP_MOVE_TEMPORARILY, "您未登录，请先登录！");
        }
        FileSystemOpt.createDirect(SystemTempFileUtils.getTempDirectory());
        String tempFilePath = SystemTempFileUtils.getRandomTempFilePath();
        InputStream inputStream = UploadDownloadUtils.fetchInputStreamFromMultipartResolver(request).getRight();
        File file = new File(tempFilePath);
        FileUtils.copyInputStreamToFile(inputStream, file);
        JSONObject jsonObject = modelExportManager.uploadModel(file);
        Integer app = modelExportManager.createApp(jsonObject, "T",
            WebOptUtils.getCurrentUserDetails(request));
        FileSystemOpt.deleteFile(tempFilePath);
        return app;
    }
}
