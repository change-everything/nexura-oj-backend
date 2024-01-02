package cn.nexura.oj.judege.codesandbox;

import cn.nexura.oj.judege.codesandbox.model.ExecuteCodeRequest;
import cn.nexura.oj.judege.codesandbox.model.ExecuteCodeResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author PeiYP
 * @since 2024年01月02日 10:04
 */
@Slf4j
@AllArgsConstructor
public class CodeSandboxProxy implements CodeSandbox {

    private CodeSandbox codeSandbox;

    @Override
    public ExecuteCodeResponse doExecute(ExecuteCodeRequest executeCodeRequest) {
        log.info("代码沙箱请求信息：" +  executeCodeRequest.toString());
        ExecuteCodeResponse executeCodeResponse = codeSandbox.doExecute(executeCodeRequest);
        log.info("代码沙箱响应信息：" +  executeCodeResponse.toString());
        return executeCodeResponse;
    }
}
