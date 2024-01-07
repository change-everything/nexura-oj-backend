package cn.nexura.oj.service;

import cn.nexura.oj.model.dto.question.CodeTemplateQuery;
import cn.nexura.oj.model.dto.question.QuestionQueryRequest;
import cn.nexura.oj.model.entity.Question;
import cn.nexura.oj.model.entity.Question;
import cn.nexura.oj.model.vo.QuestionVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author peiYP
* @description 针对表【question(题目)】的数据库操作Service
* @createDate 2023-12-28 10:35:52
*/
public interface QuestionService extends IService<Question> {

    /**
     * 校验
     *
     * @param question
     * @param add
     */
    void validQuestion(Question question, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    /**
     * 获取题目封装
     *
     * @param question
     * @param request
     * @return
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     * 分页获取题目封装
     *
     * @param questionPage
     * @param request
     * @return
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);


    /**
     * 获取代码模板
     * @param codeTemplateQuery
     * @return
     */
    String getCodeTemplate(CodeTemplateQuery codeTemplateQuery);
}
