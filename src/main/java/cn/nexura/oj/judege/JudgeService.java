package cn.nexura.oj.judege;

import cn.nexura.oj.model.entity.QuestionSubmit;

/**
 * @author PeiYP
 * @since 2024年01月02日 10:35
 */
public interface JudgeService {

    /**
     * 判题服务
     *
     * @param questionSubmitId
     * @return QuestionSubmitVO
     * @author PeiYP
     * @since 2024/1/2 10:39
     */
    QuestionSubmit doJudge(long questionSubmitId);

}
