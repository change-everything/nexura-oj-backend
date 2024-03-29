package cn.nexura.oj.service.impl;
import java.util.List;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.nexura.oj.common.ErrorCode;
import cn.nexura.oj.constant.CommonConstant;
import cn.nexura.oj.exception.BusinessException;
import cn.nexura.oj.exception.ThrowUtils;
import cn.nexura.oj.model.dto.question.CodeTemplateQuery;
import cn.nexura.oj.model.dto.question.QuestionQueryRequest;
import cn.nexura.oj.model.entity.*;
import cn.nexura.oj.model.enums.JudgeInfoMessageEnum;
import cn.nexura.oj.model.enums.QuestionSubmitLanguageEnum;
import cn.nexura.oj.model.enums.QuestionSubmitStatusEnum;
import cn.nexura.oj.model.vo.QuestionSubmitVO;
import cn.nexura.oj.model.vo.QuestionVO;
import cn.nexura.oj.model.vo.UserVO;
import cn.nexura.oj.service.QuestionSubmitService;
import cn.nexura.oj.service.UserService;
import cn.nexura.oj.utils.SqlUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.nexura.oj.service.QuestionService;
import cn.nexura.oj.mapper.QuestionMapper;
import com.google.gson.Gson;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author peiYP
* @description 针对表【question(题目)】的数据库操作Service实现
* @createDate 2023-12-28 10:35:52
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService{

    private final static Gson GSON = new Gson();

    @Resource
    private UserService userService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Override
    public void validQuestion(Question question, boolean add) {
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String answer = question.getAnswer();
        String judgeCase = question.getJudgeCase();
        String judgeConfig = question.getJudgeConfig();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(answer) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(judgeCase) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(judgeConfig) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {


        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }

        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        String answer = questionQueryRequest.getAnswer();
        Long userId = questionQueryRequest.getUserId();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();

        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.like(StringUtils.isNotBlank(answer), "answer", answer);
        if (CollectionUtils.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.eq("is_delete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
    

    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        QuestionVO questionVO = QuestionVO.objToVo(question);
        long questionId = question.getId();
        // 1. 关联查询用户信息
        Long userId = question.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionVO.setUser(userVO);
        return questionVO;
    }

    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollectionUtils.isEmpty(questionList)) {
            return questionVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        // 过滤所有的questionId
        List<Long> questionIds = questionList.stream()
                .map(Question::getId)
                .collect(Collectors.toList());

        // 查询每个题目的提交信息
        List<QuestionSubmit> questionSubmitList = questionSubmitService.list(Wrappers.lambdaQuery(QuestionSubmit.class)
                .in(QuestionSubmit::getQuestionId, questionIds));

        Map<Long, List<QuestionSubmit>> submitListMap = questionSubmitList.stream()
                .collect(Collectors.groupingBy(QuestionSubmit::getQuestionId));


        // 填充信息
        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            QuestionVO questionVO = QuestionVO.objToVo(question);
            Long userId = question.getUserId();
            Long questionId = question.getId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionVO.setUser(userService.getUserVO(user));
            List<QuestionSubmit> questionSubmits = submitListMap.getOrDefault(questionId, new ArrayList<>());
            long acSubmitCount = questionSubmits.stream().filter(questionSubmit -> {
                QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
                return JudgeInfoMessageEnum.ACCEPTED.getValue().equals(questionSubmitVO.getJudgeInfo().getMessage());
            }).count();
            questionVO.setSubmitNum(questionSubmits.size());
            questionVO.setAcceptedNum((int) acSubmitCount);
            return questionVO;
        }).collect(Collectors.toList());
        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }

    @Override
    public String getCodeTemplate(CodeTemplateQuery codeTemplateQuery) {

        String language = codeTemplateQuery.getLanguage();

        String title = codeTemplateQuery.getTitle();

        if (QuestionSubmitLanguageEnum.JAVA.getValue().equals(language)) {
            return ResourceUtil.readUtf8Str("classpath:/codeTemplate/CodeTemplate.java");
        } else if (QuestionSubmitLanguageEnum.CPP.getValue().equals(language)) {
            return ResourceUtil.readUtf8Str("classpath:/codeTemplate/CodeTemplate.cpp");
        } else if (QuestionSubmitLanguageEnum.GOLANG.getValue().equals(language)) {
            return ResourceUtil.readUtf8Str("classpath:/codeTemplate/CodeTemplate.go");
        }

        return ResourceUtil.readUtf8Str("classpath:/codeTemplate/CodeTemplate.java");
    }

}




