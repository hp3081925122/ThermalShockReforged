# Buzzsaw 第84批实现前核查

第83批d63afb6已经本地提交；用户授权直接修正确认不合理的实现，并集中记录到REFORGED_CHANGES.md。仍不使用子代理，全模组代码完成前不启动游戏；本批完整武器、局部时序载体、渲染和粒子接线及配方一次完成后统一编译。

原Buzzsaw为普通Item，1350耐久/UNCOMMON、附魔值5、非抗火，没有每次挥手额外扣耐久。真实onEntitySwing调用BuzzsawFiring，玩家原11tickCD；非玩家也能调用。选中时真实CoreEjectFlareGun手持过程禁近战，复用已核HeldWeaponState。原双行tooltip、完整Geo/动画、builtin/entity父、entityTranslucent及既有同数值手势/两手变换均需保留。

启动播放原buzzsaw声音NEUTRAL/0.7/1并触发完整动画1，将自身速度改为look.x/0.1/look.z；属于主动冲刺而非受击击退。伤害首轮同步，嵌套1END再4轮，总五轮；每轮取使用者当时位置，inflate1的全部Entity按距离排序，跳过Player，分别2点无归属therm_iframe_bypass伤害。在每个目标眼位添加一BloodSplatter，初始脚位一Campfire Cosy Smoke，保数量/位置/方向。原blood_splatter为完整AAA bloodsplatter资源，0寿命、不受物理、原OPAQUE/非强制全亮、输入速度原倍率1，可直接复用已验证AaaStationaryParticle.Provider相同参数，不替成普通红尘粒子。

已证实缺陷：目标过滤仅排Player，非玩家施用会把自己也纳入伤害；改显式排使用者UUID及新任务本身，其他原目标不删。原两端都播放声音，SERVER队列仅服务端入队，客户端首轮特效不广播；声音与伤害统一服务端，并广播完整烟雾和血液，避免重复声并使旁观者可见。冲刺保原客户端即时预测与服务器权威同步，不能把两端都有移动一概当成漏洞。有限4END任务只保存UUID/原实体ID及计数，不捕获玩家或物品强引用；施用者死亡、卸载、换维度或换为同UUID的新玩家时取消后续，防移动武器脱离使用者继续伤害，正常存活五轮和轨迹不变。此项与原死亡后闭包继续运行有意不同，记录到集中改动文档。

本批再核原物品标签、完整声音/AAA及父模型引用、制造全部成本与实际注册，不因为资产已导入而假称武器已迁。最终游戏验证覆盖11tickCD、五轮位置跟随、非玩家自伤、死亡/换维停止、多人声画与Render Preview；此刻仅做编译和静态核对。


## 第84批完成记录

真实1350耐久/附魔5普通Item、完整0循环/1半秒动画/原Geo透明层/两手变换/手势、双行双语tooltip和按栈缓存接线完成，不额外增加耐久消耗。首轮同步加1/2/3/4 END共五轮，每轮使用者当前坐标inflate1 AABB/全部Entity距排序/原排Player/2 ownerless伤，原向前速度及客户端即时预测保留；有限持久载体只持UUID/实体ID，失效取消，不全局扫描或强加载。

BloodSplatter原完整AAA、0寿命/OPAQUE/非强制全亮/无物理/速度倍率1由已有Provider正确消费；补查其二进制UTF-16相对引用Texture/Blood Splot.png，和模型父链/三声音共十二原资产及七数据/语言/声音构建路径已对照。原服务端addParticle为空，改服务端完整广播血液/烟雾并单次播放声音，保玩家本地冲刺响应。非玩家自伤、离体残留锯击和声画修复集中记录于REFORGED_CHANGES.md。

整组Java/资源首轮55秒编译通过（24任务2执行22最新，仅既有客户端注解弃用警告），静态对照及制造注册复核通过。完整9 Ferrum/9 Beetle Claw/1 Bioelectric Cell与1 Cryonic Energy成本启用，Fabricator当前26活动/5待真实产物。未运行游戏和Render Preview，无子代理。用户随后要求新建独立对话分工，三件推进靴/静默装甲已交接独占目录，详armor-thread-handoff.md；主对话继续其余武器及统一集成，不在另对话写入Java期间并发编译同一源码快照。
