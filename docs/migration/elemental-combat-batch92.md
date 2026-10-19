# 第92批：全局元素战斗链

## 分工与边界

主聊天负责 Cryo/Pyro/Ferro/Cloro 八个全局攻击过程、元素标签、完整命中特效及共享入口；独立聊天负责 LabMachinist、原21页实验室业务和自身资源，不修改本文件及共享入口。合并前双方停笔，再统一编译；整模组完整前不启动游戏。

## 原件证据与迁移要求

来源为原07.9 JAR及只读反编译源码。八个过程均监听 LivingAttackEvent，须保留发生于护甲及原生无敌帧过滤前的语义。当前 AttackAttemptEvent 已提供对应入口，不另建受伤后监听。

- 弱点：不取消原伤，额外施加事件原始伤害×1.5的无归属 therm_iframe_bypass 伤害，保两声及对应完整 HIT 元素粒子。Ferro 主手 Shock Stick、Cloro 主手 Knight's Edge 排除全局加成，保各武器已有固定伤害。
- 抗性：取消原事件，替换为无归属同类1点伤害，保玻璃声及完整 HIT_RESIST 粒子。
- 原手持元素与实体元素判断出现 XOR，需用原JAR字节码确认；Pyro 使用 directEntity，另三系使用 causingEntity，不能不留记录地混同。
- 现有武器和投射物自有弱点追加伤害须对照实际调用顺序，不将其机械删除或重复迁移。
- 额外伤必须防同一事件递归、拒绝非有限/非正伤害；事件取消及怪物技能替代攻击的先后须明确。
- 标签只接已有真实注册，缺失成员留待办；完整 AAA 资产按原件保留，不用普通粒子替代。

## 当前状态

原 Cryo/Ferro/Pyro/Cloro 八过程已找到；正在补齐字节码核查、目标事件顺序、原生粒子广播及已有本地伤害调用证据。生产实现、静态矩阵及整批编译待完成。

## 验收

静态核四系持物/实体归属、双手与双元素、弱点/抗性、两特殊剑排除、取消事件、无归属二次伤、完整声/粒子参数及资源字节；固定源码快照统一 compileJava/processResources。最终游戏中核单机/专服一致、玩家与非玩家、同系双来源、免疫/无敌帧及投射物命中表现。

## 实现与有意调整（代码已写，整批编译待冻结）

原八过程字节码均出现 `ixor`。Pyro Bonus/Resist取directEntity；Cryo/Ferro/Cloro均取causingEntity，已保这个差别。原元素entity_type标签只有弹体，没有元素生物；可实际复现的抵消例子是玩家持火系枪、发出同为火系的弹体。现在改为OR，同系只提交一次，避免持枪时反而失去元素反应。

统一最低优先级监听已存在AttackAttemptEvent，不接收已取消事件；原技能取消基础伤后不再漏出元素额外伤。四系双手按当前原生栈核对，不扫描世界。多系同时触发时先核抗性：任一活跃元素被抵抗，整次攻击仅替换一次1点伤与一份抗性声画，不因监听注册顺序在不同环境出现多次替伤/先增后减；无抗性时每个有效弱点保各自1.5倍附加伤。此多系优先级属于用户授权修正，非逐事件顺序完全等价。

无归属二次伤使新入口causingEntity为空而立即返回，保原防自递归性质；非有限/非正输入不触发反应，明确排除攻击者自身元素加成，原正常自伤保留基础伤。两特殊剑主手排除仅作用全局弱点奖励；武器原7/9固定弱点伤、枪弹原6点自有伤和其原生命中后顺序均保留。轻微-.05回动仍作用直接来源，Living来源改为只折一次击退抗性并标记速度同步；两特殊剑使用同一回动实现。

HIT_CRYO/PYRO/RESIST沿原包装寿命0、LIT、全亮、有物理、零速度；HIT_FERRO/CLORO复用第80批完整资产。1.20.1原Level.addParticle为空，ServerLevel没有覆写该重载，原服务端该路径不会广播；现在服务端sendParticles各一份，保每个完整AAA而非替换简化特效。两原弱点声音及抗性玻璃声1/-1保留。

补完整四份实体元素来源标签；补已真实注册的骨工具、Rusty Edge、Ghostglass工具、磁弩和三Stratosilver工具的物品来源标签。初审缺口为cryo物品stratosilver_sickle/frozen_blade、pyro物品molten_bat及cloro物品liteprism_sword/thistle_bow；同批随后已补前四件，只余Liteprism Sword完整超新星链待办；弱点/抗性标签此前已核的未注册生物仍待，不导入必需缺失引用假装完整。

## 同批补齐的已核元素来源物品（实现前冻结需求）

Frozen Blade与Molten Bat原本是普通Item：100耐久、1.6挖掘速度、主手+4.5伤/-2.4速度、默认0附魔值/无修理料、各一条tooltip和原平面generated图标，完整两工作台配方；不能凭名字改成SwordItem或凭火系名字添加原未有的点燃。原hurtAndBreak把受害者传为持有者，重做改真实攻击者以尊重创造/损坏回调。

Stratosilver Sickle原Hoe：5800耐久/50挖速/+4攻击/-2速度/钻石等级/8附魔/合金修复、完整3提示，复用原SickleHitsEntity程序的三击蓄积，但其爆伤为14，普通镰刀10；现有SickleItem需显式保这个分支，不能只复用默认伤害。原NoKnockback手持tick由已有攻击击退属性替代，实际农作物三倍收割和Growth脚本早已按其真实ID写入，注册后接通。全原Java字段和原JAR资源搜索未找到配方或正常奖励来源，仅原创意页入口，不为原件凭空添加配方。

同批来源物品已实现：两普通Item和层银镰刀真实注册/原创意条目/完整资产及中英提示已接通，原两配方保全部原成本，层银镰刀仅保原已有来源。Cryo与Pyro物品标签已全闭合；仅Cloro的liteprism_sword/thistle_bow继续待迁。层银三击14与两普通Item损坏归属修正已写代码，整组编译仍待冻结。

## 同批Thistle Bow实现前核对

已读原Item、Swing/Hailfall持物链、两Beam与原model/renderer。保1000耐久/UNCOMMON/附魔5/UseDuration20/普通Item默认NONE姿态，左右键开火、11tick CD、每次一Specialized Round且创造免弹药，不新增耐久消耗。无障碍时40段：HAPPY_VILLAGER在0..39距离，箭弹在1..40距离；玩家生命严格低于最大一半发毒ThistleBeam，否则LiteprismBeam。两者直接构造6基础箭伤/KB1/速度1/散布0/静音/不暴击/不额外穿透，不套参数不同helper。原两声和完整Geo0/1、80弧度右臂与第一人称偏移全部保留。

延续Carbor的已授权use修复：显式启动本栈20tick使用、仅实际使用栈续发并刷新原禁近战状态，共用11tick CD；正常未使用持物不凭空禁近战。原全40段可在墙后直接出生，沿已批准Liteprism Laser方案按一次原生碰撞射线截断，无遮挡保40段和完整原毒状态；声/粒子服务端单次广播，实际发出至少一段后才扣弹/提交CD。40段/11tick是原有数量级，最终专服须测连续多玩家负载，不能以优化为由缩成单弹或删粒子。

Liteprism Sword另依赖未迁PrismaticSupernova实体，完整链未实现前不注册空剑，本批先保它为唯一未闭合Cloro来源待办。

Thistle真实Item/客户端、原完整Geo两动画和原配方已实现，独立入口位于已有ThistleBeamEntity，不误套0速度/3伤静态helper。复用已有5tick真实光束/半血100tick毒与原生KB抗性，完整40段无额外穿透。Cloro标签现仅Liteprism Sword待完整PrismaticSupernova链；全局其他标签不创建空引用。


最终状态由 batch92-integration-validation.json 与总迁移文档“第92批统一收尾”覆盖：整组类型编译、资源复制与集成静态核对通过，最终游戏／渲染验收仍待完整迁移。本报告先前的待编译／增量未应用描述为交接时状态。
