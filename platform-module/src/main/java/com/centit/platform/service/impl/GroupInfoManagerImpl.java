package com.centit.platform.service.impl;

import com.centit.platform.dao.GroupInfoDao;
import com.centit.platform.po.GroupInfo;
import com.centit.platform.service.GroupInfoManager;
import com.centit.support.database.utils.PageDesc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author zhf
 */
@Service
public class GroupInfoManagerImpl implements GroupInfoManager {
    @Autowired
    private GroupInfoDao groupInfoDao;
    @Override
    public void createGroupInfo(GroupInfo groupInfo){
        groupInfoDao.saveNewObject(groupInfo);
    }
    @Override
    public List<GroupInfo> listGroupInfo(Map<String, Object> param, PageDesc pageDesc){
        return groupInfoDao.listObjectsByProperties(param,pageDesc);
    }
    @Override
    public GroupInfo getGroupInfo(String applicationId){
       return groupInfoDao.getObjectWithReferences(applicationId);
    }
    @Override
    public void deleteGroupInfo(String groupId){
        groupInfoDao.deleteObjectById(groupId);
    }

    @Override
    public void updateGroupInfo(GroupInfo groupInfo){
        groupInfoDao.updateObject(groupInfo);
    }
}
