# 第九批两特殊门与Parrying独立只读评审

范围为root冻结的content/doors、client/doors、ParryingStatus及StatusContent/StatusClient/两Mixin。本轮未修改src/资源，未构建或运行游戏，其他owner的Ice/Growth未重复探索。结构化证据D:/AI临时文件/ThermalShockMigration/ninth-special-doors-parrying-review.json；原件ninth-doors-original-facts.json与parrying-next-audit.json复用并逐真实方法核对。

## 两门行为与几何

16种形状（Maw/Bunker×开闭×四朝向）已脚本从原switch.box与当前DoorKind数组逐坐标比较，16/16相等；不是只比较包围盒，所有原union子box一致。Maw闭48宽64高、开阈1高及两牙柱；Bunker64宽48高、非对称负坐标与1宽开框均未缩小。原12/15个公开覆写方法各有目标等效实现；getShape/碰撞默认委托、四向默认北/反向放置/旋转镜像、ENTITYBLOCK_ANIMATED、无遮挡/光0/遮光0/不导红石/不可破坏/禁止活塞保留。原没有空visual覆写，目标也未错误新增。MapColor由原缺省NONE修为可见门材色，属于已声明地图显示修复。

Bunker WATERLOGGED默认false、放置水判定、sourcefalse、updateShape水排程、天光仅空fluid、全方向redstone连接保持。neighborChanged只在服务端、闭态且邻居信号>0时执行；取currentstate后二次幂等门槛，blockstate/animation同时1，保水/FACING。开后不复关，不在onPlace凭空触发红石，不新增tick扫描。y→x→z的-4..4完整729位置只把light_blocking_barrier变AIR并flag3，不放置barrier；一次打开先状态切换，递归neighbor不会重复扫描/声音。单MutableBlockPos减分配不减范围。

Maw任意手路径实际看主手HeraldKey>=3；闭门且currentstate仍闭后成功setBlock才扣3，同一次成功后只放原两声。持物useItemOn及空手useWithoutItem都给消耗结果，避免双手回退；服务端单执行。创造也shrink3，符合原成本，不使用会豁免创造的consumeItems。原全背包扣3改符合资格的主手扣3已冻结为修复。已开且满足3钥匙条件不再消费/声音；主手不合格即使已开仍保原bass声和actionbar英文提示。没有普通右键打开库存的新增行为。

## 库存、动画、物品与资源

两者9槽RandomizableContainer、完整load/save/loot-table延迟、更新包/更新tag、旧ForgeData迁入、六方向SidedInvWrapper及null不公开保持；NBT Items不只存引用，ContainerHelper按目标Provider持久化。原外部MenuProvider27虚拟槽不绑定真实9槽，目标改绑定GENERIC_9x1；onRemove掉9槽与普通super清BE，均是文档明确修复，没有省库存或制造普通GUI。

原双controller默认0循环和procedure动画1保留，blockstate变化重置/prevAnim/STOP条件对应原方法。原Maw动画1长3.1667秒、Bunker1长3秒均hold_on_last_frame，Gecko4.9.2 thenPlay用DEFAULT尊重资源；不擅强PLAY_ONCE、不改开门后回关。此为源码/owner已核字节码证据，未启动渲染。

10关键原asset已身份归一逐项相等：两geo、两动画、两纹理、两blockstate、两item模型；原item/generated二维图标保留，SpecialDoorItem仍GeoItem、loop0、缓存与extensions注册。GeoItemRenderer仅保原接口，不覆盖item/generated为3D模型；两世界/物品renderer仍entityTranslucent。原无独有loot文件，getDrops仍super空时fallback自掉1，不造配方。

## Parrying机械复核

原LivingHurt未取消伤害。当前ParryingStatus只服务端hasEffect条件，无amount>0、攻击者/类型/失血过滤；先remove整个原生effect链，anvil1/5与EXPLOSION1、inflate2其它Living按距离stun20/9、自身regen20/9与slow20/1 ambienttrue/particlesfalse、主手精确KnightsEdge才charge40/0全部保持。原getEntitiesOfClass(Entity)后过滤Living，目标直接查询Living不改变最终范围/目标；无hurt反击递归。

两Mixin已使用审计确认的精确owner LivingEntity与Player各自getDamageAfterArmorAbsorb descriptor，INVOKE ordinal0 require1，默认At NONE即调用前；都在isInvulnerableTo内部。Living排Player，Player独立hook无doublefire。未挂早期hurtHEAD/AttackAttempt，原感染/早攻入口保持原位。方法名actuallyHurt当前无重载，handler签名与目标source/float/CallbackInfo吻合。只是静态目标与签名核对，不能称Mixin实际运行已验。

StatusContent两效果有益白-1、Action.NONE无空tick，保持原生MobEffectInstance隐藏恢复/等级覆盖，不用maxduration简化。Parry仅PROTECTED_BY_TOTEM、不可Milk/Honey；ChargeMILK+TOTEM，匹配1.20原图腾全清与目标cures差异。Parry背包/HUD隐藏，Charge只背包隐藏、HUD继承true，粒子由原callerflags控制，未统一删白粒子。

### 明确差异待主线记录

当前ParryingStatus:34将原所有实体delta+=look×-1.5改为delta+=look×[-0.6*(1-clampedResistance)]，玩家也使用新值；并加hasImpulse/hurtMarked。这是依据用户击退适中/受抗性要求的候选修复，不是原数值等价。已经直接报root，主线需明确记录适用玩家的决定；本评审不擅改源码或恢复1.5。除此未发现当前已证实代码错误。尚待全输入冻结后统一编译和最终静态验证，之后整模组完整才能游戏测试。
