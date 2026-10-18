# 第51批：Guardian Pylon 完整流程

原件07.9 Forge1.20.1→NeoForge1.21.1。前置四真实胶囊怪已在47–50批迁入。此批接完整守卫塔、其原类型/蛋/创意目录入口、真实禁造刷新、主手Virustone激活、60死亡、80/120 END剧情、完整原椭球扫描/灯转换/胶囊释放，不用显示实体或空技能代替。原orb_sanctum结构来源仍待完整模板模块，不宣称此批自然结构入口已迁。

## 本体与表现

MONSTER1×1/防火/速度更新/tracking64/update3，330HP、0甲/攻击/速度/飞行速度/台阶、follow16、0XP、持久/无距离消失。FlyingMoveControl10/true、FlyingPathNavigation、强制无重力/免坠落、IN_FIRE和DROWN拒伤；所有hurt尝试先保原真实REGENERATION20/199默认可见粒子与图标，合法每tick回血不是永久无敌。去掉常量尺寸每tick refresh；保每tick makeStuckInBlock(AIR,.25,.05,.25)，含死亡最后baseTick。primary_pylon模型与8秒clip0/4过渡、动态默认surge_pylon贴图、同图eyes层、透明、scale1/shadow0/death rotation0全保。

## 禁造内部状态

只查原(x,y+22,z)中心inflate40 AABB的Player，显式true含旁观者，去掉全Entity扫描与不影响状态赋值的排序；没有降为每20tick。每个玩家本地NBT剩余100tick取代不可见NO_BREAK标记，接既有LivingEntityStatusMixin.tickEffects尾部递减，保同一原生效果时点、活玩家/死亡期间刷新顺序与尾随时间；非逐tick网络计数。保存/离线暂停/跨维保剩余，普通死亡重生按ServerPlayer仅复制Persisted子树的原生规则清此非persisted字段。通用药水清除不再控制这种内部建造状态。AqeronEvents统一按Palace维度或此锁取消真实Break/Place，保creative例外及原空entity分支；不复刻放置后掉资源毁块的错误收费链。

## 激活与剧情

原实际mobInteract只看玩家主手Virustone，不消费。仍返回原sidedSuccess，并调用super交互。服务端活体门控，activated只限制同柱剧情提交一次：先成功添加独立剧情实体、提交持久锁，再龙死亡声AMBIENT1/3与原无归属generic330伤害。若未死，后续主手交互仍保声音/330伤害，不重排剧情；初始化任务提交失败则日志告警且不伤柱、不锁死。不假设hurt一定致死，也不从伤害结果取消原可发生的剧情。已核原生无敌帧：先受10伤后回血1至321，330后续伤仅补320会剩1血，若把activated同时包住伤害就永久无法再次击毁；明确修此锁耦合，没有新物品费用。

任务保存原Vec3位置/维度、触发时该维度玩家UUID快照，80 SERVER END原Sopel整条文本发给当前在线同UUID玩家，之后40 END即总120发原portal声AMBIENT1/-3并完整释放。玩家后续切维仍能收到原捕获语义消息；重连/重启由UUID恢复，避免捕获旧连接/Level/Entity。未添加原服务器队列内永远达不到的client-only windchime。任务卸载暂停/重载续期，不强加载，不因柱子60tick移除而吞80/120；每阶段先消费，有限阶段幂等。

## 范围与顺序

原159×59×159、横79/竖29椭球用原double公式逐行计算实际连续z区间，保y/x/z遍历与所有原边界。运行时同原首次遇到的位置加载FULL chunk，区块引用只缓存本次调用；每个同chunk短z段重新看LevelChunkSection.hasOnlyAir/maybeHas(light或capsule)，跳过不含目标的段。未预收集目标、未重排RNG/放置顺序、未缩范围或异步写世界，也不跨自己世界变更缓存palette判断。相对偏移到实际坐标仍按原double相加后floor，避免负坐标/半格生成点偏移丢失。

sanctum_light→red_sanctum_light通过原生withPropertiesOf保相同Slab TYPE/WATERLOGGED。capsule读blockstate，1/2/3/4→SanctumErrant/FrostilyteGunner/Kilolyte/SanctumRipple，按原anchor+.5再BlockPos.containing生成和原随机yaw，随后destroyBlock(false)；0档仍按原移除，空/水含/库存沿已迁真实Capsule机制。Capsule第46批库存不丢失修复自然保留。Luna已逐一枚举757827个椭球坐标/7181行，原/目标y/x/z有序序列SHA256均为77663f8630fb2ecffe4d7e8e31f7a271e21a67adf3ca5b0b20c3944d2fd6297a；四种正负整数/半格锚点的读取与+.5生成位置一致。属性和区块加载顺序另按源码核对，不以静态声称FPS/MSPT改善。新增一次debug统计供最终实机分析。

## 死亡程序共享与缺陷修复

原BigBossDeath与Herald真实共用：抽成BossDeathEffectEntity，复用既有注册IDherald_death_effect，保持已有存档Remaining/Delay/VisualEyeHeight键，有限独立END任务保存原锚/眼高/维度；即时一发后每4 SERVER END直到共15个EXPLOSION（offset2/3/2、speed0），每发mob_death_explode HOSTILE2/2。同步修旧Herald目标实现用实体tick起算而非原END队列的帧偏移；不删Herald的任何粒子或音。Pylon真正死亡后只提交一次，取消死亡不播放假死亡特效。

Pylon60tick末帧remove和原生XP门控/事件保存，保0基础XP及可能的原生装备XP；最后5 END_ROD/偏差1.5/speed.5及40%conduit short NEUTRAL1/1全保。原程序粒子硬编码世界(.5,.5,.5)在远处柱子死亡时跑到原点，明确修正为实际柱子位置，未减少粒子。死亡期间含移除当次baseTick仍续100禁造，不在die提前清锁。

固定源码后交Luna离线compileJava/compileGametestJava/processResources和原件/数据/范围顺序/时序机械核。全模组尚未完整，不启动游戏；最终服务端、客户端和Render Preview仍为必须门槛。


最终固定快照验证：Luna使用Java21离线compileJava/compileGametestJava/processResources，BUILD SUCCESSFUL in17s，25任务中1执行/24最新；两条既有ClientEvents.bus过时警告。实际日志D:/AI临时文件/ThermalShockMigration/fiftyfirst-pylon-final-compile.log。五原资产同hash，椭球枚举有序相同。原正常结构入口仍待后续，全模组最终运行验收未开始。
