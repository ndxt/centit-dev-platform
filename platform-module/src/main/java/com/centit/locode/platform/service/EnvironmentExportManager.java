package com.centit.locode.platform.service;

import com.centit.framework.model.security.CentitUserDetails;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author codefan@sina.com
 */
public interface EnvironmentExportManager {

    InputStream exportApplication(String osId, CentitUserDetails userDetails) throws IOException;

}
