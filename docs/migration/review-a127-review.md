# 第127批A：末23项动画消费者复核

基线、原件及8/8/7分片见review-a127-plan.md。只读复核指定group2末23项，不重新处理主线独占的125A剩余19项。完整原目标代码、条件、有效clip、loop和过渡参数、外部写入接收者及目标消费者逐身份保存，不能把下列“候选”直接计作已复现的游戏缺陷。

## 第二片精确剩余

- Springslime：原碰撞写入procedure片段2，有限片段结束后可清空procedure，存活时movement返回循环1；目标POPPED保持到移除，movement持续选择片段2。源目标选择/退出条件确实不同，但4tick过渡、同步与20END尾任务可能影响实际可观察窗口，未断言必有可见画面错误。旧批次“同步字段未读”的描述不能替代已核全局Factory；一次激活尾任务的授权也不自动证明动画结束条件可改变。
- Summoned Mangler：原hurt在父类拒绝前仍写stunned，目标把写入放在isAlive门槛内；保留死亡阶段再次hurt的精确差异，不泛称正常受伤缺动画。boost到stunned的异名中断另有Raw重置依赖，主线127公共报告的具体配对未包含它，不泛用其他片段的hash证明。

## 其他片精确剩余

- Skeletitan：已找到共享SkeletalTitanEntity真实工厂、继承配置及bigswing1到bigswing2实际调用，不是未实现。第20 END切换时原第一片段可尚未结束；待主线补本配对Raw equals/hash与重置证明，不按别的异名片段证明外推。
- Willograde：原埋藏资格读取主手香料，目标读取本次交互手，扩大副手触发输入。此为确证门槛差异，是否授权改变交主线裁定，不夸大为已复现漏洞。另保单controller触发完成后的清理/恢复边界，以及保存BuryAge后重载从头播放完整bury的相位授权问题；游戏状态保存剩余进度，不自动证明动画从头重播也等价。

## 公共语义与证据质量

复用animation-main125-public-semantics.json及animation-main127-public-integration.json，不重复库源码研究。新公共报告明确默认movement/procedure为4tick、movement先添加、procedure正播时movement停止、move默认walk/idle及白名单idle/walk；每实体仍须核自身覆写。thenPlay按clip自身DEFAULT解释，异名Raw重置只按已证明配对使用，不因名字不同就无条件宣称等价。

旧assignment的空目标路径只作导航缺口，须核当前共享工厂、枚举和注册消费者，不当作未实现。外部写入取证覆盖其他Entity目录；关键反编译控制流核原JAR字节码，代码行及输入哈希另行机械校验。分片原始报告冻结后不修改，主代理如补历史裁定另存明确证据，不悄悄重写原结论。

本批不修改任何生产文件，不build、不运行服务端/客户端/Render Preview、不stage或commit，不触及用户Ribbonblade、tests和.vscode。最终23项全集、输入哈希、详细清单及真实剩余以review-a127-handoff.json为准；运行未测不拿来替代静态缺项。
