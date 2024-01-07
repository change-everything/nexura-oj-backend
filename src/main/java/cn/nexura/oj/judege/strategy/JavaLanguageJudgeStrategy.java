package cn.nexura.oj.judege.strategy;

import cn.hutool.json.JSONUtil;
import cn.nexura.oj.model.dto.question.JudgeCase;
import cn.nexura.oj.model.dto.question.JudgeConfig;
import cn.nexura.oj.judege.codesandbox.model.JudgeInfo;
import cn.nexura.oj.model.entity.Question;
import cn.nexura.oj.model.enums.JudgeInfoMessageEnum;
import cn.nexura.oj.model.enums.QuestionSubmitStatusEnum;

import java.util.List;

/**
 * JAVA判题策略
 * @author PeiYP
 * @since 2024年01月02日 11:37
 */
public class JavaLanguageJudgeStrategy implements JudgeStrategy {
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        Long memory = judgeInfo.getMemory();
        Long time = judgeInfo.getTime();
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        Question question = judgeContext.getQuestion();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();

        JudgeInfo judgeInfoResponse = new JudgeInfo();
        judgeInfoResponse.setTime(time);
        judgeInfoResponse.setMemory(memory);


        JudgeInfoMessageEnum messageEnum = JudgeInfoMessageEnum.ACCEPTED;
        // 判断输入输出用例数量是否相同
        if (outputList.size() != inputList.size()) {
            messageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResponse.setMessage(messageEnum.getValue());
            judgeInfoResponse.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
            return judgeInfoResponse;
        }

        // 依次判断每一项输出和输入是否相同
        for (int i = 0; i < judgeCaseList.size(); i++) {
            JudgeCase judgeCase = judgeCaseList.get(i);
            if (!judgeCase.getOutput().equals(outputList.get(i))) {
                messageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoResponse.setMessage(messageEnum.getValue());
                judgeInfoResponse.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
                return judgeInfoResponse;
            }
        }

        // 判断题目限制
        // String message = judgeInfo.getMessage();
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Long needMemoryLimit = judgeConfig.getMemoryLimit();
        Long needTimeLimit = judgeConfig.getTimeLimit();
        // 判断是否超出限制
        if (memory > needMemoryLimit * 1024 * 1024) {
            messageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(messageEnum.getValue());
            judgeInfoResponse.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
            return judgeInfoResponse;
        }

        if (time > needTimeLimit) {
            messageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(messageEnum.getValue());
            judgeInfoResponse.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
            return judgeInfoResponse;
        }

        judgeInfoResponse.setMessage(messageEnum.getValue());
        judgeInfoResponse.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());
        return judgeInfoResponse;
    }
}
