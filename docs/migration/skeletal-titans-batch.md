# 第90批：骷髅巨人家族

## 范围与原件证据

迁移 Skeletitan、Frost Titan、Mantletitan 的真实实体、属性、近战、生成替换、自然生成、原生亡灵语义、掉落、刷怪蛋和完整 Geo 表现。原件为已登记 SHA-256 的 07.9 JAR；参考其 entity 三类、模型、renderer、MantletitanLayer、SkeletitanOnInitialEntitySpawn 与 FrostTitanOnEntityTickUpdate。另一个聊天独占 Kofoni 家族，独立记录于 kofoni-batch.md；不共改双方 Java。

源码确认三者均为普通 Monster，不是 Boss，没有血量阶段或受伤召唤。名为 SkeletitanAttack 的全局程序实际筛选 Kilolyte，不是本家族招式，不移植到巨人身上。TitanSpawnCondition 的雪页岩/草页岩判断没有实际调用入口，不能作为自然生成限制。

## 保留清单

- 三实体注册 ID、1×2.5 基础尺寸和原 1.6 倍有效碰撞/视觉尺寸、0.8 阴影、透明主层、头部转动；Mantletitan 保原 mantletitannoglow 主纹理和 mantletitanglow 发光层。
- 默认生命32、护甲6、攻击6、跟随32、击退抗性0.2、近战击退0.5、步高0.6；两陆生变体速度0.2、Frost速度0.25；经验3在20tick死亡末帧结算。
- 原五组 AI 优先级、近战速度1、固定攻击距离平方16、原近战不额外要求视线、原近战事件先取消伤害，再由真实 TestmobattackProcedure 提交蓄力挥击，不能误当普通近战结算。仅 Frost 免疫溺水，并在水下续10tick速度III和海豚恩惠III；这是实际增益，不用内部状态代替。
- 每次正常原生掉落流程额外一骨头；复用既有全局 skeletitan/undead_mobs 标签掉落，不重复实现、不擅加 graveyard_mobs。
- 仅 Skeletitan 接原自然生成注入和非和平/黑暗/原生地面生成规则。其初次生成先判断脚下 Mantlestone：必替 Mantletitan；否则12%替 Frost Titan。后两者不新增独立自然刷新。
- 保原 idle/walk/walk2 控制条件、零过渡帧和 procedure 单次动画接口；直接保原模型/纹理/动画资源。原控制器提到 death，但原动画文件没有 death clip，不能把这个缺失片段宣称已迁入完整死亡动画；最终渲染需对照真实原件行为。
- 原刷怪蛋、语言、创意栏排序及已迁核心击杀 Frost Titan 挑战共同接通。

## 已确认缺陷与替换

原初次生成先 discard 原生物，再尝试 spawn 变体，失败/事件取消会丢失原生物。新实现只在变体初始化未取消且实际成功加入世界后移除原生物；生成选择只判一次，失败不每tick重试、不制造无界任务。保原 block-center 落点、MOB_SUMMONED 原因、朝向和零速度。

固定尺寸在构造完成后单次 refreshDimensions，通过原生尺寸计算直接保1.6比例，不再每tick刷新。统一实体家族仅复用真正相同逻辑，变体差异显式保留，配置控制三者属性。内部状态持久化，已有基类承接经验末帧结算；不加全局扫描/延迟队列。

## 验收门槛

实现前文档已建立。完成后由 Luna 核对实际根入口/三工厂/渲染接线、原资源哈希、自然生成参数、标签和配置默认值；主聊天统一在足量代码固定后编译和处理资源。全模组完整前不启动客户端、服务端或 Render Preview。最终检查生成变体取消、区块卸载、原近战距离/击退、三种掉落、Frost水下增益与核心挑战、透明/发光/动画真实显示。

当前状态：代码与共享接线已完成，整组 Java/资源编译通过；本轮收尾后的静态与编译另记结果。游戏与 Render Preview 待全模组完整后验收。

## 隐藏蓄力攻击与持久身份

真实入口为 TestmobattackProcedure 的全局受击事件：skeletitan 标签的攻击均取消原伤，有冷却时也取消。无冷却播放 bigswing1，内部冷却50tick、移动锁20tick；第20个 SERVER END 播 bigswing2/凋灵破块声及施法者实时位置30篝火烟。目标相对原位置移动不超过1.7格时承受默认7点无归属 errant_damage，Mantle 另点火5秒并在原目标锚点发18火焰。默认伤害按基础6×7/6，保原 bypasses_cooldown 标签，不加入来源避免攻击事件重入。新增施法者4格命中距离限制属于已记录修复。

有限任务使用UUID找回正常卸载重载后的巨人和非玩家目标；玩家额外保运行实体ID与弱引用校验，死亡重生/重连的新实例不接续旧击；任务经过保存重载后保音粒子但不再命中玩家，避免重启后ID偶合。非玩家正常重载仍按UUID承接。施法者死亡移除后保原已提交后续与最后位置；能查到其在其他维度时取消任务，不用旧维度坐标命中。区块卸载暂停，不强加载区块。尺寸在首次生成碰撞检查前已刷新。Frost核心挑战按注册ID和真实死亡提交状态判断，共用Java实体类不改变识别。

本轮整组验证：Java21离线 `compileJava processResources` 首轮成功，1m8s，24任务2执行22最新，5个既有API弃用警告。编译期间8112份src输入哈希未变化。原生资源标签补齐后仅运行资源复制；客户端、服务端与Render Preview未启动。
