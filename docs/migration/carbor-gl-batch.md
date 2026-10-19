# 第91批：Carbor GL 榴弹发射器

## 实现前核对与分工

主对话负责 Carbor GL 本体、真实发射入口、完整 Geo 客户端和原获取方式；另一个聊天负责 Qo / Defector Machinist 与 Junkyard，不共改其独占文件。原件为已记录SHA256的07.9 JAR，已核 CarborGLItem、GrenadeLauncherFiringProcedure、HailfallItemInHandTickProcedure、GrenadeBulletEntity、GrenadeHitProcedure、GrenadeBulletProjectileHitsBlockProcedure、模型与renderer。目标 API 以现有 FirearmItem/CoreEject、LegacyArrowEntity、原生 BowItem/Item 用法为据。

## 完整保留要求

- 普通 Item，耐久1000、不可燃、UNCOMMON、附魔值5、不新增开火耐久消耗，原 BOW 使用姿态/60tick使用时长；挥手与右键均开火，冷却6tick，每发一重型弹药，创造免弹药。
- 每发从玩家脚位Y+1.5按实时方向发真实 GrenadeBullet，速度2、散布1、基础箭伤3、击退1、静音、不燃烧、不暴击、不额外穿透。使用直接构造入口，不能套用会点火且参数不同的原helper。弹体原父链和爆炸不削减：非玩家命中强度3 TNT交互、Cloro弱点额外6；撞块在块整数坐标强度2；命中玩家仍保原父箭伤但无附加爆炸。
- 原 grenade_launcher 几何、grenade_launcer 拼写的真实纹理、0循环/1完整后坐力动画、零过渡、半透明层、第三人称80弧度右臂原姿态、第一人称位移和全部显示变换。
- 原两种配方abb/acd/abb：3碳化硅、4孔雀石或铋、核心弹射信号枪、紫碳化硅块；原注册ID、语言和创意顺序。普通耐久物品的1.21附魔标签一并核。

## 已核不合理实现及授权修复

原 use 仅调用普通 Item.super.use 返回PASS，没有 startUsingItem；inventoryTick 却依赖 isUsingItem，持续开火会依赖其他手正使用物品。重做显式启动这把枪的原60tick使用状态，并仅在当前实际使用栈回调持续开火；共享6tick冷却防挥手/右键/续发同tick重复提交。双端音效仅服务端发一次；空弹不能进入持续使用，续发耗尽后停止使用，避免新增每tick空弹提示声。生成弹体成功后才扣弹/提交冷却。以上是用户授权行为修复，另记 REFORGED_CHANGES，不虚称逐帧完全等价。

## 验收

整批输入冻结后统一Java/资源编译；静态对照原模型/动画/贴图字节、两个配方语义、关键参数、所有客户端与缓存接线。整模组代码完整后交最终两端及Render Preview；当前不启动游戏。保原榴弹破坏环境行为，最终要测试命中实体/方块/玩家、Cloro弱点、抗击退、空弹/创造/左右键与持续使用、切换栈、多把同类动画隔离及原爆炸声画。

当前状态：源码、资源与共享接线已写完；原资产/数据静态核对见main91-static-validation.json。整组编译待双方输入冻结，运行未验。

本轮整组验证：Java21离线 `compileJava processResources` 首轮成功，1m8s，24任务2执行22最新，5个既有API弃用警告。编译期间8112份src输入哈希未变化。原生资源标签补齐后仅运行资源复制；客户端、服务端与Render Preview未启动。

主对话集成验收记录：公共入口/所有菜单与renderer/双语/进度及character_mobs已合并；整组Java21离线编译1m8s成功，标签补齐后单独processResources43s成功。全部本轮改动资源的源码与build复制逐字节相同。最终游戏/服务端/Render Preview未执行，独立交付报告中的“未接线/未编译”仅保留为交付时历史状态。
