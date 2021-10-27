package com.centit.platform.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.fileserver.utils.SystemTempFileUtils;
import com.centit.fileserver.utils.UploadDownloadUtils;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.platform.service.ModelExportManager;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.file.FileSystemOpt;
import com.centit.support.file.FileType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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

    @ApiOperation(value = "导出应用")
    @GetMapping(value = "/downloadModel/{applicationId}")
    public void downLoadModel(@PathVariable String applicationId, HttpServletResponse response) throws IOException {
        InputStream in = modelExportManager.downModel(applicationId);
        String fileName = URLEncoder.encode(applicationId, "UTF-8") +
            ".zip";
        response.setContentType(FileType.mapExtNameToMimeType("zip"));
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        IOUtils.copy(in, response.getOutputStream());
    }

    @ApiOperation(value = "导入zip覆盖应用")
    @RequestMapping(value = "/updateApp", method = {RequestMethod.POST})
    @WrapUpResponseBody
    public Integer upLoadModel(HttpServletRequest request) throws Exception {
        FileSystemOpt.createDirect(SystemTempFileUtils.getTempDirectory());
        String tempFilePath = SystemTempFileUtils.getRandomTempFilePath();
        InputStream inputStream = UploadDownloadUtils.fetchInputStreamFromMultipartResolver(request).getRight();
        File file = new File(tempFilePath);
        FileUtils.copyInputStreamToFile(inputStream, file);
        JSONObject jsonObject = modelExportManager.uploadModel(file);
        Integer app = modelExportManager.createApp(jsonObject, "T",
            StringBaseOpt.emptyValue(WebOptUtils.getCurrentUserCode(request), "admin"));
        FileSystemOpt.deleteFile(tempFilePath);
        return app;
    }
}
