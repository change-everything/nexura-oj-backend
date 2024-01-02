package cn.nexura.oj.judege.codesandbox.impl;
import java.util.List;

import cn.nexura.oj.judege.codesandbox.CodeSandbox;
import cn.nexura.oj.judege.codesandbox.model.ExecuteCodeRequest;
import cn.nexura.oj.judege.codesandbox.model.ExecuteCodeResponse;
import cn.nexura.oj.judege.codesandbox.model.JudgeInfo;
import cn.nexura.oj.model.enums.JudgeInfoMessageEnum;
import cn.nexura.oj.model.enums.QuestionSubmitStatusEnum;

/**、
 * 实例代码沙箱
 * @author peiYP
 * @create 2023-12-31 18:08
 **/
public class ExampleCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse doExecute(ExecuteCodeRequest executeCodeRequest) {
        List<String> input = executeCodeRequest.getInput();

        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setMessage("执行成功");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());
        executeCodeResponse.setOutput(input);
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getValue());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);

        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }
}
