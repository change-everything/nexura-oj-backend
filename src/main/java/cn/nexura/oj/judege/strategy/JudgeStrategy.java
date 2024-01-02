package cn.nexura.oj.judege.strategy;

import cn.nexura.oj.judege.codesandbox.model.JudgeInfo;

/**
 * 盘体策略
 * @author PeiYP
 * @since 2024年01月02日 11:34
 */
public interface JudgeStrategy {

    JudgeInfo doJudge(JudgeContext judgeContext);

}
