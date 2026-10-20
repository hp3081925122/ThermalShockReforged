# 第120批主线：只读复审与确证修复

基线f1dffc6。原件与版本沿用第119批。Luna承担只读清单和机械比对，主线及迁移A负责核实修复；迁移A持续使用三个Luna分组。子代理不改生产或用户测试目录、不构建、不启动游戏。

## 主线确证修复

原NoMeleeMobEffect的基础修饰为-20，RubySpearItemInHandTickProcedure及SpeleothemHandTickProcedure的amp0为-20/3tick，远程amp1为-40/1tick。目标共用状态曾统一-40，现恢复两种数值，用两个最多3tick的局部保存通道表达同种效果的强优先和并行到期，实际只挂一个modifier，死亡清理、重载恢复，刷新不缩短同强度余时。常规触距都被原生下限封至0，数值差异在高触距属性下仍可能影响行为，不能以通常结果相同忽略。

HiVolt真实物品调用Ruby过程，保冷却分支；同名无条件过程没有调用，不补造行为。原Sentrarius喂骨头及Sylvia隐藏充能图标两候选经调用者/原生构造器证据撤回。既有镰刀原生击退属性和放置保护调整保已批准差异。

## 只读结果与范围

- 原6103份资产全部在，目标6105份；3152字节相同、2946仅命名空间、5真实JSON差异、0二进制差异、0解析错误。5份分别为已记录的语言/Plenky非法纹理/钛锤纹理/两NeoForge动态桶转换，见assets-main120-review.json。
- 原154实体注册及154renderer注册均映射到当前实现，原125属性目标均有对应。123处event.put属于共享/派生折叠，不按文本计数误判，见entities-main120-bindings.json。
- 效果本轮精核7项，不据历史清单的not_migrated误判内部替代，也不声称23项全消费者已闭合，见status-main120-reviewed.json。

资产字节、注册映射、静态报告和编译均不能代替完整运行验证。客户端、Render Preview在全部迁移/无简化复审后安排；服务端由用户独立负责。统一构建及最终输入证据另记main120-static-validation.json。

## 统一构建完成

两路停止修改生产输入后运行Java21的compileJava/processResources离线构建成功，日志SHA256为52975ede841ff76a811f3a873a735ba45c2706cb426ad1895d3e9d0e816a9ce2。构建前后全部src/main输入和构建资源字节对照见main120-static-validation.json；静态与编译结果不代表已执行客户端、服务端或Render Preview。
