package com.centit.framework.system.controller;

import com.centit.locode.platform.controller.AppInfoController;
import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/appInfo")
@Api(tags = {"移动端版本管理接口"}, value = "移动端版本管理接口")
public class BackAppInfoController extends AppInfoController {

}
