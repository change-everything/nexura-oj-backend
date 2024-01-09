INSERT INTO nexura_oj.user (userAccount, userPassword, unionId, mpOpenId, userName, userAvatar, userProfile, userRole, createTime, updateTime, isDelete) VALUES ('nexura', 'b03d227f78c0c79334fca76e7b8eb46a', null, null, 'nexura官方', '//p1-arco.byteimg.com/tos-cn-i-uwbnlip3yd/9eeb1800d9b78349b24682c3518ac4a3.png~tplv-uwbnlip3yd-webp.webp', null, 'admin', '2023-12-27 18:46:44', '2024-01-09 16:21:48', 0);
INSERT INTO nexura_oj.question_solution (solution_id, solution, question_id, user_id, create_time, is_delete, title, tags) VALUES ('1744282346928005122', '# 题解：最大子序列和

## 题目描述

给定一个由非负整数组成的数组 `nums`，找到数组中的一个非空子序列，使得该子序列的元素总和最大。同时，子序列中的元素必须是连续的。

编写一个函数 `maxSubArray`，返回该子序列的最大和。

## 思路与解法

这是一个典型的动态规划问题。我们使用两个变量 `currentSum` 和 `maxSum` 分别记录当前子序列的和以及历史最大和。

1. 初始化：将 `maxSum` 和 `currentSum` 初始化为数组的第一个元素 `nums[0]`。
2. 遍历数组：从数组的第二个元素开始遍历，对于每个元素 `nums[i]`，更新 `currentSum` 为 `Math.max(nums[i], currentSum + nums[i])`，即选取当前元素或者加入当前元素的和更大的值。同时，更新 `maxSum` 为 `Math.max(maxSum, currentSum)`。
3. 遍历完成后，`maxSum` 即为最大子序列和。

## Java 代码实现

```java
public class MaxSubArray {

    public static int maxSubArray(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }

        int maxSum = nums[0];
        int currentSum = nums[0];

        for (int i = 1; i < nums.length; i++) {
            currentSum = Math.max(nums[i], currentSum + nums[i]);
            maxSum = Math.max(maxSum, currentSum);
        }

        return maxSum;
    }

    public static void main(String[] args) {
        int[] nums = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        System.out.println(maxSubArray(nums));
    }
}
```
## 复杂度分析
- 时间复杂度：O(n)，其中 n 为数组的长度。
- 空间复杂度：O(1)。', '1740669297868496898', '1739961003063406593', '2024-01-08 16:58:13', 0, '官方题解', '["c++","c","go","动态规划"]');
INSERT INTO nexura_oj.question_solution (solution_id, solution, question_id, user_id, create_time, is_delete, title, tags) VALUES ('1744535205644922881', '测试题解', '1740669297868496898', '1739961003063406593', '2024-01-09 09:42:59', 0, '最大子序列和', '["Java","c++","c","go","动态规划"]');
INSERT INTO nexura_oj.question_solution (solution_id, solution, question_id, user_id, create_time, is_delete, title, tags) VALUES ('1744543128358023169', '**测试机哦**', '1740669297868496898', '1739961003063406593', '2024-01-09 10:14:28', 0, '测试', '["Java","c++","c","go","动态规划"]');
INSERT INTO nexura_oj.question_solution (solution_id, solution, question_id, user_id, create_time, is_delete, title, tags) VALUES ('1744543920028774402', '> 达瓦达瓦', '1740669297868496898', '1739961003063406593', '2024-01-09 10:17:37', 0, '测试', '["Java","c++","c","go","动态规划"]');
INSERT INTO nexura_oj.question_solution (solution_id, solution, question_id, user_id, create_time, is_delete, title, tags) VALUES ('1744545724640555009', '`达娃大`', '1740669297868496898', '1739961003063406593', '2024-01-09 10:24:47', 0, 'tags', '["Java","c++","c","go","动态规划"]');
INSERT INTO nexura_oj.question_solution (solution_id, solution, question_id, user_id, create_time, is_delete, title, tags) VALUES ('1744575216570363906', '2323', '1740669297868496898', '1739961003063406593', '2024-01-09 12:21:58', 0, '12312', '["121"]');
