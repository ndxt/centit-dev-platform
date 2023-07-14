package com.centit.locode.platform.controller;

import com.alibaba.fastjson2.JSONObject;
import com.centit.fileserver.utils.SystemTempFileUtils;
import com.centit.fileserver.utils.UploadDownloadUtils;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.locode.platform.service.ModelExportManager;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.ObjectException;
import com.centit.support.file.FileSystemOpt;
import com.centit.support.file.FileType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhf
 */
@RestController
@RequestMapping(value = "modelExport")
@Api(value = "应用导入导出", tags = "模板导入导出")
public class ModelExportController extends BaseController {
    @Value("${app.home:./}")
    private String appHome;
    @Autowired
    private ModelExportManager modelExportManager;
    @Autowired
    private PlatformEnvironment platformEnvironment;

    @ApiOperation(value = "导出应用路径")
    @GetMapping(value = "/downloadModel/{osId}")
    public Map<String,String> downLoadModel(@PathVariable String osId) throws FileNotFoundException {
        String fileId = modelExportManager.downModel(osId);
        String fileName = fileId;
        try {
            fileName = platformEnvironment.getOsInfo(osId).getOsName();
        } catch (Exception ignored) {

        }
        Map<String,String> map=new HashMap<>(2);
        map.put("fileName",fileName);
        map.put("fileId",fileId);
        return map;
    }

    @ApiOperation(value = "导出应用文件")
    @GetMapping(value = "/downloadModelFile")
    public void downLoadModel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> parameters = collectRequestParameters(request);
        String fileName = URLEncoder.encode(StringBaseOpt.objectToString(parameters.get("fileName")), "UTF-8") +
            ".zip";
        String fileId=StringBaseOpt.objectToString(parameters.get("fileId"));
        String filePath=appHome + File.separator+fileId+".zip";
        response.setContentType(FileType.mapExtNameToMimeType("zip"));
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        InputStream in = null;
        try {
            in = new FileInputStream(filePath);
            IOUtils.copy(in, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
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
