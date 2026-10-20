# 第123批：113组结构实体的属性与后加载闭合

基线为33d3bc6。只读子代理按第122批的113组身份逐条核对源NBT、目标NBT、属性配置及加载生命周期，主线核实差异并负责生产修复。矩阵保留在attributes-main123-before-fix.json，它记录的是Herald初始化时机修复前的快照，不代表最终源码哈希。

## 逐组结果

113组分布为BeetleThrasher32、DormantMonolith4、FrostilyteCryomancer16、FrostilyteSoldier33、Herald6、Icidrozerk20、ProtoiSunbeam2。107条保存属性与配置重应用相交记录均为movement_speed，源保存值、转换值与默认配置相等；6组Herald没有该类属性负载。113组均保留Health，读档先消费原生属性、药效与生命，再重应用配置，并将已保存生命限制在新最大生命以内，不回满血。

113组旧follow_range随机加成均按各自UUID、数值及运算迁移。结构使用finalize=true，加载旧修饰符后再进行一次生成初始化；目标额外生成的固定ID修饰符与旧版本再次新增随机UUID修饰符对应，不能误删为重复。普通世界读档不再调用此初始化。32组BeetleThrasher的短时、隐藏、ambient缓慢负一级标记保持相同字节位型，读入后精确转为sleepLock；其他组无原效果负载。

## 三处默认值差异的定性

|对象|静态差异|依据及结论|
|---|---|---|
|ProtoiSunbeam|攻击属性0→20|原光束固定基础箭伤20，普通直接近战被取消；第72批将基础攻击20×倍率1接入光束，符合用户技能关联基础攻击的要求。实际默认光束伤害未因此改变，见protoi-sunbeam-batch.md和原ProtoiSunbeamAttackProcedure的真实发射参数。|
|DormantMonolith|攻击击退3→0.6|按用户适中击退约束显式调整，见monolith-batch95.md“明确差异与保护”；眠体无攻击goal，不新增技能推力。|
|Herald|攻击击退4→0.6|按同一约束显式调整，见mangler-herald-next-audit.md；没有真实攻击goal。原20/99击退抗性均被原属性上限限制为1，目标1有效值等价。|

这三项不是未获授权的迁移简化，不恢复高击退，也不将Sunbeam光束改成零伤害。自定义配置仍会有意覆盖保存的对应基础属性；修饰符和受伤生命另行保留。

## Herald生成定位修复

原HeraldEntity.finalizeSpawn先调用父方法，再由HeraldOnInitialEntitySpawnProcedure保存当时的X/Y/Z。目标此前只在首次baseTick缺少锚点时初始化，导致生成初始化与首tick之间的位移可能被误认为固定起点；携带旧锚点的结构实体也没有原版的重新初始化。

恢复finalizeSpawn中父方法完成后立即记录当前position及维度，加入一次英文debug。世界存档加载不经过finalizeSpawn，仍保留已存锚点；直接create/add但未进行生成初始化的入口继续使用现有首tick兜底。保持既有跨维任务取消、锚点持久化、攻击与死亡逻辑。

当前目标ServerLevelAccessor.getLevel、Mob.finalizeSpawn及项目IceCreatureEntity的覆写确认了四参数入口；维度取其ServerLevel。此修复不增加每tick扫描或网络广播。

## 验证边界

固定源码后统一编译并记录验证报告。最终运行仍需核结构六组Herald锚点、生成后首tick前移动、受伤实体重载不回血、配置覆盖、随机加成和32组休眠标记消费。静态闭合不等于实机通过，客户端及Render Preview继续等待完整迁移收口，服务端沿用用户独立测试线。
