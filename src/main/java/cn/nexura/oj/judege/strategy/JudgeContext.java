package cn.nexura.oj.judege.strategy;

import cn.nexura.oj.model.dto.question.JudgeCase;
import cn.nexura.oj.judege.codesandbox.model.JudgeInfo;
import cn.nexura.oj.model.entity.Question;
import cn.nexura.oj.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 上下文，用于定义在策略传递的参数
 * @author PeiYP
 * @since 2024年01月02日 11:36
 */
@Data
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private Question question;

    private List<JudgeCase> judgeCaseList;

    private QuestionSubmit questionSubmit;

}
