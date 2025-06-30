package com.centit.locode.platform.controller;

import com.alibaba.fastjson2.JSONObject;
import com.centit.fileserver.utils.SystemTempFileUtils;
import com.centit.fileserver.utils.UploadDownloadUtils;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.OsInfo;
import com.centit.framework.model.basedata.WorkGroup;
import com.centit.framework.model.security.CentitUserDetails;
import com.centit.locode.platform.service.ModelExportManager;
import com.centit.locode.platform.service.impl.ModelExportMangerImpl;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.ObjectException;
import com.centit.support.file.FileSystemOpt;
import com.centit.support.file.FileType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhf
 */
@RestController
@RequestMapping(value = "modelExport")
@Api(value = "应用导入导出", tags = "模板导入导出")
public class ModelExportController extends BaseController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ModelExportController.class);
    @Value("${app.home:./}")
    private String appHome;
    @Autowired
    private ModelExportManager modelExportManager;
    @Autowired
    private PlatformEnvironment platformEnvironment;

    @ApiOperation(value = "1.导出应用")
    @GetMapping(value = "/downloadModel/{osId}")
    public Map<String, String> downLoadModel(@PathVariable String osId, HttpServletRequest request) throws IOException {
        // 校验 osId 合法性
        if (osId == null || osId.trim().isEmpty()) {
            throw new ObjectException("osId 不能为空！");
        }
        CentitUserDetails ud = WebOptUtils.getCurrentUserDetails(request);

        if (ud == null) {
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN, "用户没有登录，没有对应的权限！");
        }
        Map<String, Object> parameters = collectRequestParameters(request);
        List<WorkGroup> userGroups = platformEnvironment.listWorkGroup(osId, ud.getUserCode(), null);
        if (CollectionUtils.isEmpty(userGroups)) {
            throw new ObjectException(ResponseData.ERROR_FORBIDDEN, "用户没有权限导出这个应用：" + osId + "！");
        }
        String fileId = modelExportManager.downModel(osId, parameters);
        if (fileId == null || fileId.isEmpty()) {
            throw new ObjectException(ResponseData.ERROR_INTERNAL_SERVER_ERROR, "文件生成失败，请稍后重试");
        }
        String fileName = fileId;
        try {
            OsInfo osInfo = platformEnvironment.getOsInfo(osId);
            if (osInfo != null && osInfo.getOsName() != null) {
                fileName = osInfo.getOsName();
            }
        } catch (Exception e) {
            // 记录日志以便排查问题
            // logger.warn("获取操作系统名称失败，使用默认文件名替代", e);
        }

        Map<String, String> map = new HashMap<>();
        map.put("fileName", fileName);
        map.put("fileId", fileId);
        return map;
    }


    @ApiOperation(value = "2.根据返回id下载应用文件")
    @GetMapping(value = "/downloadModelFile")
    public void downLoadModel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> parameters = collectRequestParameters(request);

        String fileName = StringBaseOpt.castObjectToString(parameters.get("fileName"),"");
        String fileId = StringBaseOpt.objectToString(parameters.get("fileId"));

        // 参数校验
        if (fileId.isEmpty()) {
            throw new ObjectException("fileId 不能为空！");
        }

        String safeFileName = URLEncoder.encode(fileName.isEmpty() ? fileId : fileName, "UTF-8") + ".zip";
        String filePath = appHome + File.separator + fileId + ".zip";
        Path fileFullPath = Paths.get(filePath);

        // 文件是否存在
        if (!Files.exists(fileFullPath)) {
            throw new ObjectException("没有发现文件！");
        }

        // 设置响应头
        response.setContentType(FileType.mapExtNameToMimeType("zip"));
        response.setHeader("Content-disposition", "attachment; filename=" + safeFileName);
        response.setContentLengthLong(Files.size(fileFullPath));

        try (InputStream in = Files.newInputStream(fileFullPath)) {
            IOUtils.copy(new BufferedInputStream(in), response.getOutputStream());
        } catch (IOException e) {
            // 建议替换为日志框架记录
            logger.error("下载文件失败：{}", filePath, e);
        } finally {
            response.getOutputStream().close();
        }
    }


    @ApiOperation(value = "3.文件导入")
    @RequestMapping(value = "/updateApp", method = {RequestMethod.POST})
    @WrapUpResponseBody
    public JSONObject upLoadModel(HttpServletRequest request) throws IOException {
        CentitUserDetails userDetails = WebOptUtils.getCurrentUserDetails(request);
        if (userDetails == null) {
            throw new ObjectException(ResponseData.ERROR_USER_NOT_LOGIN, "您未登录，请先登录！");
        }

        String tempFilePath = SystemTempFileUtils.getRandomTempFilePath();
        if (StringUtils.isBlank(tempFilePath)) {
            throw new ObjectException("临时文件路径无效");
        }

        InputStream inputStream = UploadDownloadUtils.fetchInputStreamFromMultipartResolver(request).getRight();
        if (inputStream == null) {
            throw new ObjectException("上传文件内容为空");
        }

        try (InputStream is = inputStream) {
            File file = new File(tempFilePath);
            FileUtils.copyInputStreamToFile(is, file);
            return modelExportManager.uploadModel(file);
        } catch (Exception e) {
            logger.error("文件上传过程中发生IO异常", e);
            throw new ObjectException(e.getMessage());
        } finally {
            boolean deleted = FileSystemOpt.deleteFile(tempFilePath);
            if (!deleted) {
                logger.warn("临时文件删除失败: {}", tempFilePath);
            }
        }
    }

}
