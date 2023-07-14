package com.centit.locode.runtime.service;

import com.centit.framework.security.model.CentitUserDetails;

public interface EnvironmentImportManager {

    void importEnvironment(String importType, CentitUserDetails ud);
}
