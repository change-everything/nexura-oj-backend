package cn.nexura.oj.service;


import cn.nexura.oj.model.dto.question.QuestionQueryRequest;
import cn.nexura.oj.model.dto.questionsolution.QuestionSolutionQueryRequest;
import cn.nexura.oj.model.entity.Question;
import cn.nexura.oj.model.entity.QuestionSolution;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author 86188
* @description 针对表【question_solution(题解表)】的数据库操作Service
* @createDate 2024-01-08 15:59:12
*/
public interface QuestionSolutionService extends IService<QuestionSolution> {

    QueryWrapper<QuestionSolution> getQueryWrapper(QuestionSolutionQueryRequest questionQueryRequest);

    Page<QuestionSolution> getSolutionPage(Page<QuestionSolution> questionSolutionPage, HttpServletRequest request);
}
