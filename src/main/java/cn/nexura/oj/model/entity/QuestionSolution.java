package cn.nexura.oj.model.entity;

import cn.nexura.oj.model.vo.QuestionVO;
import cn.nexura.oj.model.vo.UserVO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 题解表
 * @author 86188
 * @TableName question_solution
 */
@TableName(value ="question_solution")
@Data
public class QuestionSolution implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID, value = "solution_id")
    private Long solutionId;

    /**
     * 题解内容
     */
    private String solution;

    /**
     * 标题
     */
    private String title;

    /**
     * 题号
     */
    @TableField(value = "question_id")
    private Long questionId;

    /**
     * 创建人
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 用户信息
     */
    @TableField(exist = false)
    private UserVO userVO;

    /**
     * 题目信息
     */
    private String tags;

    /**
     * 逻辑删除
     */
    @TableLogic
    @TableField(value = "is_delete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}