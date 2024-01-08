package cn.nexura.oj.controller;

import cn.nexura.oj.annotation.AuthCheck;
import cn.nexura.oj.common.BaseResponse;
import cn.nexura.oj.common.DeleteRequest;
import cn.nexura.oj.common.ErrorCode;
import cn.nexura.oj.common.ResultUtils;
import cn.nexura.oj.constant.UserConstant;
import cn.nexura.oj.exception.BusinessException;
import cn.nexura.oj.exception.ThrowUtils;
import cn.nexura.oj.model.dto.question.*;
import cn.nexura.oj.model.dto.questionsolution.QuestionSolutionAddRequest;
import cn.nexura.oj.model.dto.questionsolution.QuestionSolutionQueryRequest;
import cn.nexura.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import cn.nexura.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import cn.nexura.oj.model.entity.Question;
import cn.nexura.oj.model.entity.QuestionSolution;
import cn.nexura.oj.model.entity.QuestionSubmit;
import cn.nexura.oj.model.entity.User;
import cn.nexura.oj.model.vo.QuestionSubmitVO;
import cn.nexura.oj.model.vo.QuestionVO;
import cn.nexura.oj.service.QuestionService;
import cn.nexura.oj.service.QuestionSolutionService;
import cn.nexura.oj.service.QuestionSubmitService;
import cn.nexura.oj.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 题目解析接口
 *
 * @author peiYP
 */
@RestController
@RequestMapping("/question/solution")
@Slf4j
public class QuestionSolutionController {

    @Resource
    private QuestionSolutionService questionSolutionService;

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;


    /**
     * 创建
     *
     * @param questionSolutionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionSolutionAddRequest questionSolutionAddRequest, HttpServletRequest request) {
        if (questionSolutionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionSolution questionSolution = new QuestionSolution();
        BeanUtils.copyProperties(questionSolutionAddRequest, questionSolution);
        User loginUser = userService.getLoginUser(request);
        questionSolution.setUserId(loginUser.getId());
//        questionSolution.setFavourNum(0);
//        questionSolution.setThumbNum(0);
        boolean result = questionSolutionService.save(questionSolution);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newSolutionId = questionSolution.getSolutionId();
        return ResultUtils.success(newSolutionId);
    }

    /**
     * 根据 id 获取脱敏类
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(questionService.getQuestionVO(question, request));
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<?> getQuestionById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        // 不是本人并且不是管理员
        if (!question.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            return ResultUtils.success(questionService.getQuestionVO(question, request));
        }
        return ResultUtils.success(question);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<QuestionSolution>> listQuestionVOByPage(@RequestBody QuestionSolutionQueryRequest questionQueryRequest,
            HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<QuestionSolution> questionSolutionPage = questionSolutionService.page(new Page<>(current, size),
                questionSolutionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionSolutionService.getSolutionPage(questionSolutionPage, request));
    }

}
