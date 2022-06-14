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
import com.centit.platform.service.ModelExportManager;
import com.centit.support.common.ObjectException;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

/**
 * @author zhf
 */
@RestController
@RequestMapping(value = "modelExport")
@Api(value = "应用导入导出", tags = "模板导入导出")
public class ModelExportController extends BaseController {
    @Autowired
    private ModelExportManager modelExportManager;
    @Autowired
    private PlatformEnvironment platformEnvironment;

    @ApiOperation(value = "导出应用")
    @GetMapping(value = "/downloadModel/{osId}")
    public void downLoadModel(@PathVariable String osId, HttpServletResponse response) throws IOException {
        String filePath = modelExportManager.downModel(osId);
        String fileName = FileSystemOpt.extractFileName(filePath);
        try {
            fileName = platformEnvironment.getOsInfo(osId).getOsName();
        } catch (Exception ignored) {

        }
        fileName = URLEncoder.encode(fileName, "UTF-8") +
            ".zip";
        response.setContentType(FileType.mapExtNameToMimeType("zip"));
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        InputStream in = null;
        try {
            in = new FileInputStream(filePath);
            IOUtils.copy(in, response.getOutputStream());
        } finally {
            if (in != null) {
                in.close();
                FileSystemOpt.deleteFile(filePath);
            }
        }
    }

    @ApiOperation(value = "导入zip获取json")
    @RequestMapping(value = "/updateApp", method = {RequestMethod.POST})
    @WrapUpResponseBody
    public JSONObject upLoadModel(HttpServletRequest request) {
        CentitUserDetails userDetails = WebOptUtils.getCurrentUserDetails(request);
        if (userDetails == null) {
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN, "您未登录，请先登录！");
        }
        FileSystemOpt.createDirect(SystemTempFileUtils.getTempDirectory());
        String tempFilePath = SystemTempFileUtils.getRandomTempFilePath();
        try {
            InputStream inputStream = UploadDownloadUtils.fetchInputStreamFromMultipartResolver(request).getRight();
            File file = new File(tempFilePath);
            FileUtils.copyInputStreamToFile(inputStream, file);
            return modelExportManager.uploadModel(file);
        } catch (Exception e) {
            throw new ObjectException(e.getMessage());
        } finally {
            FileSystemOpt.deleteFile(tempFilePath);
        }
    }
}
