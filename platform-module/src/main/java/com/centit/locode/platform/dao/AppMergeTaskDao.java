package com.centit.locode.platform.dao;

import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.locode.platform.po.AppMergeTask;
import com.centit.support.algorithm.CollectionsOpt;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AppMergeTaskDao extends BaseDaoImpl<AppMergeTask, JSONObject> {

    public void clearMergeTask(String appVersionId) {
        this.deleteObjectsByProperties(CollectionsOpt.createHashMap(
            "appVersionId", appVersionId));
    }

    public void markTaskComplete(String appVersionId, String relationId) {
        DatabaseOptUtils.doExecuteSql(this,
            "update app_merge_task set merge_status = 'A' " +
                "where app_version_id = ? and relation_id = ?",
            new Object[] {appVersionId, relationId});
    }

    //VERSION_MERGE_STATUS_ROLLBACK
    public void markTaskRollback(String appVersionId, String relationId) {
        DatabaseOptUtils.doExecuteSql(this,
            "update app_merge_task set merge_status = 'C' " +
                "where app_version_id = ? and relation_id = ?",
            new Object[] {appVersionId, relationId});
    }

    public List<AppMergeTask> listMergeTask(String appVersionId, String mergeStatus) {
        return this.listObjectsByProperties(CollectionsOpt.createHashMap(
            "appVersionId",appVersionId, "mergeStatus", mergeStatus));
    }
}
