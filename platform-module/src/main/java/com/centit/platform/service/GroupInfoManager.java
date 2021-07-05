package com.centit.platform.service;

import com.centit.platform.po.GroupInfo;
import com.centit.support.database.utils.PageDesc;

import java.util.List;
import java.util.Map;

public interface GroupInfoManager {
    void createGroupInfo(GroupInfo groupInfo);

    List<GroupInfo> listGroupInfo(Map<String, Object> param, PageDesc pageDesc);

    GroupInfo getGroupInfo(String groupId);

    void deleteGroupInfo(String groupId);

    void updateGroupInfo(GroupInfo groupInfo);
}