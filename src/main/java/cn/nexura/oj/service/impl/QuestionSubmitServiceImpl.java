package cn.nexura.oj.service.impl;

import cn.nexura.oj.common.ErrorCode;
import cn.nexura.oj.constant.CommonConstant;
import cn.nexura.oj.exception.BusinessException;
import cn.nexura.oj.judege.JudgeService;
import cn.nexura.oj.model.dto.question.QuestionQueryRequest;
import cn.nexura.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import cn.nexura.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import cn.nexura.oj.model.entity.Question;
import cn.nexura.oj.model.entity.User;
import cn.nexura.oj.model.enums.QuestionSubmitLanguageEnum;
import cn.nexura.oj.model.enums.QuestionSubmitStatusEnum;
import cn.nexura.oj.model.vo.QuestionSubmitVO;
import cn.nexura.oj.model.vo.QuestionVO;
import cn.nexura.oj.model.vo.UserVO;
import cn.nexura.oj.service.QuestionService;
import cn.nexura.oj.service.UserService;
import cn.nexura.oj.utils.SqlUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.nexura.oj.model.entity.QuestionSubmit;
import cn.nexura.oj.service.QuestionSubmitService;
import cn.nexura.oj.mapper.QuestionSubmitMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* @author 86188
* @description 针对表【question_submit(题目提交)】的数据库操作Service实现
* @createDate 2023-12-28 10:36:33
*/
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
    implements QuestionSubmitService{

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private JudgeService judgeService;

    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        // 如果编程语言不合法直接报错
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "暂不支持此编程语言");
        }

        // 判断实体是否存在，根据类别获取实体
        long questionId = questionSubmitAddRequest.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        long userId = loginUser.getId();
        // 每个用户串行提交题目
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setLanguage(language);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setJudgeInfo("{}");
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setUserId(userId);

        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }

        Long submitId = questionSubmit.getId();
        // 2024/1/2 执行判题服务
        CompletableFuture.runAsync(() -> {
            judgeService.doJudge(submitId);
        });
        return submitId;
    }


    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        // 脱敏：仅本人和管理员能看见自己（提交 userId 和登录用户 id 不同）提交的代码
        long userId = loginUser.getId();
        // 处理脱敏
        if (userId != questionSubmit.getUserId() && !userService.isAdmin(loginUser)) {
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    /**
     * 获取查询包装类
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionQueryRequest) {

        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }

        String language = questionQueryRequest.getLanguage();
        Integer status = questionQueryRequest.getStatus();
        Long questionId = questionQueryRequest.getQuestionId();
        Long userId = questionQueryRequest.getUserId();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();


        queryWrapper.eq(ObjectUtils.isNotEmpty(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
    

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        Set<Long> questionIds = questionSubmitList.stream()
                .map(QuestionSubmit::getQuestionId)
                .collect(Collectors.toSet());
        Map<Long, QuestionVO> questionVOMap = questionService.listByIds(questionIds).stream()
                .map(QuestionVO::objToVo)
                .collect(Collectors.toMap(QuestionVO::getId, Function.identity()));


        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> {
                    QuestionSubmitVO questionSubmitVO = getQuestionSubmitVO(questionSubmit, loginUser);
                    questionSubmitVO.setUserName(userService.getById(questionSubmitVO.getUserId()).getUserName());
                    questionSubmitVO.setStatusStr(Objects.requireNonNull(QuestionSubmitStatusEnum.getEnumByValue(questionSubmitVO.getStatus())).getText());
                    questionSubmitVO.setQuestionVO(questionVOMap.getOrDefault(questionSubmitVO.getQuestionId(), null));
                    return questionSubmitVO;
                })
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }

}




