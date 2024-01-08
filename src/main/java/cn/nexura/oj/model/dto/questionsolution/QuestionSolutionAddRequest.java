package cn.nexura.oj.model.dto.questionsolution;

import cn.nexura.oj.model.dto.question.JudgeCase;
import cn.nexura.oj.model.dto.question.JudgeConfig;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建请求
 *
 * @author peiYP
 */
@Data
public class QuestionSolutionAddRequest implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String solution;

    /**
     * 题目id
     */
    private Long questionId;



    private static final long serialVersionUID = 1L;
}