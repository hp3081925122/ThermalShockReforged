# 第124批：补齐已定位的消费者缺项

基线d2608f4。第123批编译与输入/资源哈希通过，保留原goal active。当前批次不重做完整注册盘点，也不将未验证项目归零。

- 迁移A独占菜单组1的57项与组2的65项专属Java及A124文档，沿review-a123-remaining.json只补静态缺项；3个Luna只读，A核实真实修复，不写共享注册、语言或状态，不构建。
- 主线独占FloristScreen、ShutterbugScreen的动态问候语言接线，以及SickleItem、ClawSickleItem的真实效果提交顺序。原/目标addEffect及NeoForge取消语义先核后改，保20tick、等级、声音、伤害和有效入口。
- 语言Luna独占en_us.json、zh_cn.json和localization-zh-review.md；应用已准备的53条提示，继续剩余GUI正文，接收6个动态问候英文键。其他聊天不手改语言。候选先D盘核，构建停笔前统一提交当前可靠译文。
- 状态只读Luna只补45条查询/移除的控制流证据；渲染只读Luna只补26项未解RenderType的helper/enum与原生默认路径。主线不重复其盘点，负责接收证据和修复确证缺口。

所有权以文件为边界；共享生产需求先交主线，不互相回退改动。临时脚本和提取物均D:/AI临时文件/ThermalShockMigration。统一构建前全部src/main冻结、固定快照，再执行相关compileJava/processResources并核输出。最终客户端与Render Preview仍待代码及无简化复审完整后由子代理验收，服务端用户独立负责，不重复安排。不修改或提交用户docs/testing与Ribbonblade。

## 主线中期验证范围

本批主线另核Liteprism正常伤害/粒子/移除顺序并保留按实体短期防重入；追加AttackCarrierRenderer及ClientEvents、CavebloomerClient三份生产，修三种载体Noop/空render遗漏。语言最终118旧中文值、6新双语键。8880份固定输入编译/资源处理通过1m26s，8060直接资源字节一致，报告main124-static-validation.json。A的122项具体菜单复审仍在进行；中期提交不包含其未完成报告和后续生产。
