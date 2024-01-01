package cn.nexura.oj.judege.codesandbox;

import cn.nexura.oj.judege.codesandbox.model.ExecuteCodeRequest;
import cn.nexura.oj.judege.codesandbox.model.ExecuteCodeResponse;

/**
 * @author peiYP
 * @create 2023-12-31 17:54
 **/
public interface CodeSandbox {

    ExecuteCodeResponse doExecute(ExecuteCodeRequest executeCodeRequest);


}

