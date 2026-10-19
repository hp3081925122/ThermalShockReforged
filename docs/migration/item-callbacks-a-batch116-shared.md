# 第116批 A 共享源码精确增量

生产仍冻结；本文是可复核的问题交接，不表示已经修复或获准修改共享文件。

## 骨剑触距

原 `BoneswordTickProcedure` 在选中 inventoryTick 对服务端 LivingEntity 刷新 REACH 3tick/amp1；原 Reach 属性为 ENTITY_REACH +1×(amp+1)，因此骨剑应为 +2，并保留离手的剩余时长。

目标 `content/tools/ToolsContent.java:39` 当前把 bone_sword 注册为 DescribedSword，添加主手 `bone_sword_reach` 常驻 +2，没有 inventoryTick。`content/weapons/HeldWeaponState.java` 已有 refreshReach(entity, amount) 和持久3tick余时，Spear 使用同一 held_reach modifier。当前骨剑属性立即离手消失，并能与独立 held_reach modifier 相加；原同种 REACH MobEffect 不能这样直接相加。尚未发现批准删除骨剑余时的记录；remaining-effects-audit.md 明确保留3tick要求。

建议主线核定：骨剑取消独立装备触距 modifier，以选中 inventoryTick 接入共用触距状态；骨剑+2、长矛更强余效的 MobEffect 合并/降级规则也应核原 MobEffectInstance.update，不能简单把强余效直接覆盖为+2。需要主线持有 ToolsContent/HeldWeaponState，A 不抢写。原基础剑攻击、速度、耐久、修理、说明不变。此项未解决前不声明54根全部语义闭合。

## Leafcutter 破坏顺序已核正

原过程查询破坏位置世界状态，目标 LeafcutterItem 使用 mineBlock 参数 state。已读本机 Forge 1.20.1-47.4.23 mapped official 源码包 ServerPlayerGameMode.destroyBlock：生存路径先 itemstack.mineBlock，再 removeBlock；创造路径 removeBlock 后直接返回，不调 mineBlock。因此不能套用原版未打Forge补丁的调用顺序，把这里误报为原过程无效。正常生存路径九叶额外掉落原本即可执行，无须按“恢复缺失掉落”写REF。1/2各50%分布、独立实体和拾取延迟10保留；原 for 条件每轮重新随机，目标只抽一次，随机抽样顺序不同，不声称随机流完全一致。

## 触距合并规则已核

同一原源码包 MobEffectInstance.update 明确：较强新效果替换当前效果；较弱但更长效果进入 hiddenEffect，较弱且不更长则不覆盖当前效果。tick 对当前和隐藏层一同递减，当前到期才恢复隐藏层。故当前 HeldWeaponState.refreshReach 每次直接覆盖 amount 在不同强度长矛之间切换也不能完整表达原合并规则；本批主线修改该共用类时应一起处理，并保持3tick范围及持久化/死亡清理，不只给骨剑接一个覆盖调用。

A 的 BoneSword、BloodstoneHalberd、CarborundumHalberd、CarborundumSpear、HiVoltLance 共5根因此保留跨武器切换待闭合标记；它们各自独立技能的数值/声画/时间已经记录，不能将54根有笔记误报为54根全部关闭。

## 需主线集中补入REF的既存返回值差异

CobaltAxe 原 useOn 忽略 super 返回、执行本过程、无论目标一律 SUCCESS；当前尊重原生消耗结果，不匹配八种剥皮返回父结果。因此不匹配目标可以继续后续交互，属于可观察行为差异，八种剥皮数值本身未删。A 不改共享 REFORGED_CHANGES，请主线保留该实现并明确登记。

若干原非食物 use 调用过程后返回父 Item.use 的 PASS，而目标已完成生成/奖励/放置后返回 success/sidedSuccess、冷却或失败返回 fail；这是交互提交边界差异，会影响后续手/方块交互，不能只按过程参数称返回契约完全一致。矩阵逐根留原完整回调和目标方法，主线可集中记录成功提交和失败不重复消费原则，不建议为表面同原恢复重复交互。Buzzsaw服务端完整粒子广播已在总REF第84批明确登记，本批无需再当新增差异。
