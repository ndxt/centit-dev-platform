package com.centit.platform.dao;

import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.platform.po.Question;
import org.springframework.stereotype.Repository;

@Repository
public class QuestionDao extends BaseDaoImpl<Question,String> {
}
