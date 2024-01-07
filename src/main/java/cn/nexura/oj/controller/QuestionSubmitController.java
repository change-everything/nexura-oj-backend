package cn.nexura.oj.controller;

import cn.nexura.oj.annotation.AuthCheck;
import cn.nexura.oj.common.BaseResponse;
import cn.nexura.oj.common.ErrorCode;
import cn.nexura.oj.common.ResultUtils;
import cn.nexura.oj.constant.UserConstant;
import cn.nexura.oj.exception.BusinessException;
import cn.nexura.oj.model.dto.question.QuestionQueryRequest;
import cn.nexura.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import cn.nexura.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import cn.nexura.oj.model.entity.Question;
import cn.nexura.oj.model.entity.QuestionSubmit;
import cn.nexura.oj.model.entity.User;
import cn.nexura.oj.model.vo.QuestionSubmitVO;
import cn.nexura.oj.service.QuestionSubmitService;
import cn.nexura.oj.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子点赞接口
 *
 * @author peiyp
 */
@RestController
@RequestMapping("/question_submit")
@Slf4j
@Deprecated
public class QuestionSubmitController {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserService userService;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param request
     */
    @PostMapping("/")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
                                         HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        final User loginUser = userService.getLoginUser(request);
        long result = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(result);
    }


    /**
     * 分页获取题目提交列表（除管理员外，普通用户只能看到非答案、提交代码等公开信息）
     *
     * @param questionSubmitQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                 HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        Page<QuestionSubmit> questionPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        final User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionPage, loginUser));
    }

}
