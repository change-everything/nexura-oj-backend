package cn.nexura.oj.judege.codesandbox;

import cn.nexura.oj.judege.codesandbox.impl.ExampleCodeSandbox;
import cn.nexura.oj.judege.codesandbox.model.ExecuteCodeRequest;
import cn.nexura.oj.judege.codesandbox.model.ExecuteCodeResponse;
import cn.nexura.oj.model.enums.QuestionSubmitLanguageEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@SpringBootTest
class CodeSandboxTest {


    @Value("${codesandbox.type:example}")
    private String type;

    @Test
    void executeCode() {
        ExampleCodeSandbox codeSandbox = new ExampleCodeSandbox();
        String code = "int main() { }";
        String language = QuestionSubmitLanguageEnum.JAVA.getValue();
        List<String> inputList = Arrays.asList("1 2", "3 4");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .input(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.doExecute(executeCodeRequest);
        Assertions.assertNotNull(executeCodeResponse);
    }


    @Test
    void executeCodeProxy() {
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String code = "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        int a = Integer.parseInt(args[0]);\n" +
                "        int b = Integer.parseInt(args[1]);\n" +
                "        System.out.println(\"结果:\" + (a + b));\n" +
                "    }\n" +
                "}";
        String language = QuestionSubmitLanguageEnum.JAVA.getValue();
        List<String> inputList = Arrays.asList("1 2", "3 4");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .input(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.doExecute(executeCodeRequest);
        Assertions.assertNotNull(executeCodeResponse);

    }

}