package com.centit.locode.platform.dao;

import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.locode.platform.po.ApplicationVersion;
import org.springframework.stereotype.Repository;

@Repository
public class ApplicationVersionDao extends BaseDaoImpl<ApplicationVersion, String> {

    public void setRestoreStatus(String appVersionId, String mergeStatus) {
        DatabaseOptUtils.doExecuteSql(this,
            "update application_version set MERGE_STATUS = ? " +
                "where VERSION_ID = ? ",
            new Object[] {mergeStatus, appVersionId});
    }
}
