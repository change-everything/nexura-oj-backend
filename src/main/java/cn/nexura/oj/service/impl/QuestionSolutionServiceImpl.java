package cn.nexura.oj.service.impl;

import cn.nexura.oj.constant.CommonConstant;
import cn.nexura.oj.mapper.QuestionSolutionMapper;
import cn.nexura.oj.model.dto.question.QuestionQueryRequest;
import cn.nexura.oj.model.dto.questionsolution.QuestionSolutionQueryRequest;
import cn.nexura.oj.model.entity.Question;
import cn.nexura.oj.model.entity.QuestionSolution;
import cn.nexura.oj.model.entity.User;
import cn.nexura.oj.model.vo.QuestionVO;
import cn.nexura.oj.model.vo.UserVO;
import cn.nexura.oj.service.QuestionService;
import cn.nexura.oj.service.QuestionSolutionService;
import cn.nexura.oj.service.UserService;
import cn.nexura.oj.utils.SqlUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* @author 86188
* @description 针对表【question_solution(题解表)】的数据库操作Service实现
* @createDate 2024-01-08 15:59:12
*/
@Service
public class QuestionSolutionServiceImpl extends ServiceImpl<QuestionSolutionMapper, QuestionSolution>
    implements QuestionSolutionService {


    @Resource
    private UserService userService;

    @Resource
    private QuestionService questionService;


    /**
     * 获取查询包装类
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSolution> getQueryWrapper(QuestionSolutionQueryRequest questionQueryRequest) {

        QueryWrapper<QuestionSolution> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }

        Long solutionId = questionQueryRequest.getSolutionId();
        String title = questionQueryRequest.getTitle();
        Long userId = questionQueryRequest.getUserId();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();
        Long questionId = questionQueryRequest.getQuestionId();


        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.eq(ObjectUtils.isNotEmpty(solutionId), "solution_id", solutionId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "question_id", questionId);
        queryWrapper.eq("is_delete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<QuestionSolution> getSolutionPage(Page<QuestionSolution> questionSolutionPage, HttpServletRequest request) {

        // 拿到题解列表
        List<QuestionSolution> questionSolutionList = questionSolutionPage.getRecords();

        if (questionSolutionList.isEmpty()) {
            return questionSolutionPage;
        }

        // 拿到用户map
        Set<Long> userIdSet = questionSolutionList.stream()
                .map(QuestionSolution::getUserId)
                .collect(Collectors.toSet());
        List<User> users = userService.listByIds(userIdSet);
        Map<Long, UserVO> solutionUserMap;
        if (!users.isEmpty()) {
            solutionUserMap = users.stream()
                    .map(UserVO::objToVo)
                    .collect(Collectors.toMap(UserVO::getId, Function.identity()));
        } else {
            solutionUserMap = new HashMap<>();
        }

        // 设置题目信息,用户信息
        questionSolutionList = questionSolutionList.stream()
                .peek(questionSolution -> {
//                    questionSolution.setQuestionVO(solutionQuestionMap.getOrDefault(questionSolution.getQuestionId(), null));
                    questionSolution.setUserVO(solutionUserMap.getOrDefault(questionSolution.getUserId(), null));
                })
                .collect(Collectors.toList());

        questionSolutionPage.setRecords(questionSolutionList);

        return questionSolutionPage;
    }

}




