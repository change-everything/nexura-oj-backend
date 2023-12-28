package cn.nexura.oj.service;

import cn.nexura.oj.model.dto.question.QuestionQueryRequest;
import cn.nexura.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import cn.nexura.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import cn.nexura.oj.model.entity.Question;
import cn.nexura.oj.model.entity.QuestionSubmit;
import cn.nexura.oj.model.entity.User;
import cn.nexura.oj.model.vo.QuestionSubmitVO;
import cn.nexura.oj.model.vo.QuestionVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

/**
* @author 86188
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2023-12-28 10:36:33
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {

    /**
     * 题目提交
     *
     * @param questionId
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionId, User loginUser);


    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionQueryRequest);

    /**
     * 获取题目封装
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目封装
     *
     * @param questionPage
     * @param loginUser
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionPage, User loginUser);

}
