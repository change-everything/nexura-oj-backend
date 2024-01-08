package cn.nexura.oj.model.dto.questionsolution;

import cn.nexura.oj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询请求
 *
 * @author peiYP
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionSolutionQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long solutionId;

    /**
     * 标题
     */
    private String title;

    /**
     * 创建用户 id
     */
    private Long userId;


    private static final long serialVersionUID = 1L;
}