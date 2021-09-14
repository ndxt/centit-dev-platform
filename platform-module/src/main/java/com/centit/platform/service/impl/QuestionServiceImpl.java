package com.centit.platform.service.impl;

import com.centit.platform.dao.QuestionDao;
import com.centit.platform.po.Question;
import com.centit.platform.service.QuestionService;
import com.centit.support.database.utils.PageDesc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    QuestionDao questionDao;

    @Override
    public void createQuestion(Question question) {
        questionDao.saveNewObject(question);
    }

    @Override
    public void updateQuestion(Question question) {
        questionDao.updateObject(question);
    }

    @Override
    public void deleteQuestion(String questionId) {
        questionDao.deleteObjectById(questionId);
    }

    @Override
    public List<Question> listQuestion(Map<String, Object> params, PageDesc pageDesc) {
        return questionDao.listObjects(params,pageDesc);
    }

    @Override
    public Question getQuestion(String questionId) {
        return questionDao.getObjectById(questionId);
    }
}
