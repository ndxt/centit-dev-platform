package com.centit.platform.service;

import com.centit.framework.security.model.CentitUserDetails;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author codefan@sina.com
 */
public interface ApplicationExchangeManager {

    InputStream exportApplication(String osId, CentitUserDetails userDetails) throws IOException;

    int importApplication(InputStream offlineFile, String osId, CentitUserDetails userDetails) throws IOException;
}
