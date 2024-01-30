package com.centit.locode.platform.service;

import com.centit.locode.platform.po.ApplicationVersion;
import com.centit.support.database.utils.PageDesc;

import java.io.InputStream;
import java.util.List;

public interface ApplicationVersionService {
    String createApplicationVersion(ApplicationVersion applicationVersion);

    void updateApplicationVersion(ApplicationVersion applicationVersion);

    void deleteApplicationVersion(String versionId);

    List<ApplicationVersion> listApplicationVersion(String applicationId, PageDesc pageDesc);

    ApplicationVersion getApplicationVersion(String versionId);

    InputStream exportApplicationVersion(String versionId);

    void importApplicationVersion(InputStream inputStream);

    void restoreApplicationVersion(String versionId);
}
