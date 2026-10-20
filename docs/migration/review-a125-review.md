# 第125批A：动画实际消费者复核

只读复核group1的59个原Geo实体，三片20/20/19，完整身份及原目标逐字段证据见animation-a125-group1-evidence.json。生产修改为零，无build、游戏、stage或commit；主线group2、公共库语义及生产修复不重复处理。

## 待主线修核

- Icidrone、Icidrozerk：原JAR与可读源码均为movement/procedure转换0 tick，目标IceCreatureEntity自有controller为4 tick。真实触发及有效片段已对照，不属于扫描未展开父类的误报。修复时按kind精确处理，勿影响共用类其他实体。
- Icidroburner、Icidrogunner：原queueServerWork(10)在当次SERVER END先减1，目标跳过起手tick，后续第10次实体tick才从aim切fire，正常原生攻击场景晚约1 tick。需保明确END阶段，不只比较两个常量10；异名trigger切换的重置语义仍交主线公共证据补齐。
- 其余精确未闭合项含共享controller默认参数/注册顺序、出生动画首次消费阶段、Angry Cavebloomer延迟换算、Defector随机动画逻辑端及冷却替代、Herald异名动画连续切换。逐身份问题和原目标片段保留，不当作已证实缺失，更不改成“运行未测”来清零。

## 历史差异解释

主代理另核三项历史授权：Dandy原受击者volley改施法者、Explosive Snobol改首次合法伤害唯一引信且后续不追加、Concierge的scrambler外部换皮入口延期。原报告不改，汇总中附原文及哈希后解除对应待裁定项，见animation-a125-history-resolution.json。保留不同调用条件，不宣称原目标完全相同。

Assimilator原death/walk2不在其真实动画资源中，不能为生成器残留补造动画。Jellercel等枚举配置和共享实体类均追实际参数，不按旧正则空值判缺controller。所有thenPlay结合clip自身loop解释；公共事实引用animation-main125-public-semantics.json，不重复公共源码调查。

## 证据质量与快照

第二片原收集器排除entity目录，A发现后要求同一Luna补扫：313个Java、472处相关语法，20项均无新增跨Entity写入，未分类接收者为0。独立证据、315份输入校验及原final未改哈希保留，未扩大为其他片业务复核。

开工基线f83be73用于范围追踪，报告取自当时工作区，并不冒称全部文件字节就是该提交。Defector原始取证记录到了主线126中间快照；最终脚本先从带行号原文还原并校验其哈希，再与4cb7d4b及当前源码比较：只有mobInteract及一个日志import变化，baseTick和registerControllers逐字未变，并记录旧/新行号及两份哈希。分片原行号指其保存快照，不悄悄重写为当前快照。

最终校验包括59身份无缺无重、三片归属、逐报告和完整取证清单哈希、公共报告输入哈希、明确受控漂移及UTF-8。方法片段与带行号证据已由各片机械校验；真实客户端/GPU效果、服务端和Render Preview均未由A运行，本地配对不是运行验收完成。
