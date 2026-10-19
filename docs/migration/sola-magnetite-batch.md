# 第73批：Sola 与 Magnetite 行为复审

原件依据为 Sola/Magnetite 实体、初始化与自然生成过程、FlareAttack 全局事件、模型/渲染/发光层和原注册参数。之前两个实体共用了 PathfinderMob 的 Protoi 基类，额外获得持久化、跌落/溺水/火伤免疫，同时丢失 Monster 的和平难度消失等语义，需要按真实继承恢复。

Sola 保 9 最大生命、50% 出生时变为 6 当前生命、生命低于 4 时缩放 1，否则当前生命除以 3；碰撞和模型都缩放。保 0.9×0.9 基础尺寸、火免、追踪 16、阴影 0.3、透明主体 zerkglow 与 sola 发光层、不跟随头骨、idle/walk 和四 tick 过渡。尺寸只在缩放实际变化后刷新，保持原 baseTick 后刷新阶段。保近战平方距离 4、原 AI 顺序/速度、史莱姆声音、1 Pathiolite Scrap、20 tick 死亡和 10 经验。自然生成要求脚下 Mantlestone 且世界祭坛进度已解锁；不增加黑暗/难度门槛。全局攻击尝试事件补回 Sola 造成伤害时点燃四秒的分支，和 Flare 使用相同已迁移入口。

Magnetite 保 35 生命、6 护甲、3 基础攻击、0.3 移速、16 索敌、0.1 抗性/近战击退、1 台阶、0.8×2 基础尺寸和追踪 64。免疫直接箭、跌落和溺水，不免疫火伤；保 Blaze 声音和自然消失。原自然生成过程的两个分支均为 true，不能凭分支名字擅加祭坛/矿物进度限制。

原近战与远程均优先级 1，占用 MOVE/LOOK；GoalSelector 的相同优先级不能互相替换，近战持有路径时会阻止远程启动。目标改成按原近战距离互斥选择：近处近战、远处原生远程寻路与射击。保射击速度 1.4、散布 12、80 tick 间隔和 10 格射程，以及原绿弹/声音/附加命中效果。此为有证据的 AI 修复；默认基础箭伤仍 2，通过基础攻击 3×技能倍率 2/3 得到，新增生命/攻击/防御/移速/索敌/击退/射击间隔配置。保磁铁 idle/walk/shield 资源与触发能力，不凭动画名新增原无入口的自动护盾。

获取链复审补回两份原群系 add_spawns：Sola 六群系、权重 7、1～3 只，Magnetite 五群系、权重 1、单只；转换为目标 neoforge codec，不修改原群系范围与数量。补回两实体 cryo_weak 和磁弹 therm_projectile 标签，不能把实体注册/placement 当成生成与属性关系已闭合。Magnetite 死亡 1% Loquesicle 由既有 FoodDrops 全局事件保留，按 doEntityDrops、原坐标和 10 tick 拾取延迟；不重复加到实体掉落。

本批完成后统一 compileJava/processResources 与资源/API 静态核对。全模组尚未完整，最终客户端、专服和 Render Preview 验收仍待；按用户最新要求全部由主对话执行，不调用子代理。

验证记录：本批 Java 离线编译通过，20 秒、24 任务中 1 执行/23 最新；随后补入的数据与第74批统一 compileJava/processResources 通过，52 秒、2 执行/22 最新。八项视觉资产、两份群系刷怪表、相关标签和 ghostbullet 声音对照原件通过，构建输出数据已核实更新。详见 seventythird-sola-magnetite-static-validation.json；这不替代最终游戏验收。
