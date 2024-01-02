package cn.nexura.oj.judege;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.json.JSONUtil;
import cn.nexura.oj.common.ErrorCode;
import cn.nexura.oj.exception.BusinessException;
import cn.nexura.oj.judege.codesandbox.CodeSandbox;
import cn.nexura.oj.judege.codesandbox.CodeSandboxFactory;
import cn.nexura.oj.judege.codesandbox.CodeSandboxProxy;
import cn.nexura.oj.judege.codesandbox.model.ExecuteCodeRequest;
import cn.nexura.oj.judege.codesandbox.model.ExecuteCodeResponse;
import cn.nexura.oj.judege.strategy.JudgeContext;
import cn.nexura.oj.judege.strategy.JudgeManager;
import cn.nexura.oj.model.dto.question.JudgeCase;
import cn.nexura.oj.judege.codesandbox.model.JudgeInfo;
import cn.nexura.oj.model.entity.Question;
import cn.nexura.oj.model.entity.QuestionSubmit;
import cn.nexura.oj.model.enums.QuestionSubmitStatusEnum;
import cn.nexura.oj.service.QuestionService;
import cn.nexura.oj.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author PeiYP
 * @since 2024年01月02日 10:39
 */
@Service
public class JudgeServiceImpl implements JudgeService {
    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String type;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {

        // 查询题目是否提交成功
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (null == questionSubmit) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }

        //  查询是否有此题目
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if (null == question) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }

        // 查看题目状态是否正常
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "正在判题中");
        }

        // 更新题目状态
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }


        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        String judgeCaseStr = question.getJudgeCase();
        // 获取测试用例
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);

        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);

        codeSandbox = new CodeSandboxProxy(codeSandbox);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .input(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.doExecute(executeCodeRequest);

        // 根据执行结果，判断题目是否正确
        List<String> outputList = executeCodeResponse.getOutput();

        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setQuestion(question);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestionSubmit(questionSubmit);

        // 调用判题策略
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);

        // 修改数据库的判题结果
        // 更新题目状态
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }

        QuestionSubmit questionSubmitResult = questionSubmitService.getById(questionSubmitId);
        return questionSubmitResult;
    }
}
