# 第99批 Benzor / Maiden 迁移

迁移前已建立本文档。开工只读HEAD为f519ebd；主线解除冻结后的生产基线为8397254821278a42c577addcb6f1f34e53c97ebc。目标Minecraft1.21.1、NeoForge21.1.233、Java21、GeckoLib4.9.2。原件只读D:/AI临时文件/ThermalShockMigration/thermal_shock-07.9-original.jar，SHA256为3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436。

本路独占entity/benzor、entity/maiden、content/npc/benzor及对应client目录、独有资源和本批文档。主线同时迁移电系三实体。根入口、ClientEvents、配置入口、语言、所有标签仅交精确增量，没有修改共享文件。主线已提供BloodBullet公开零击退入口。本路未运行Gradle、游戏、stage或commit；双方停笔固定输入后由主线统一编译。沿用mcreator-rework、minecraft-mod-dev-helper；Java无注释、UTF8、调试英文，临时文件仅D盘。

## 证据范围

benzor-maiden-investigation99.json保存原九页布局、按钮、网络分支及procedure源哈希；inventory99是Luna原注册/真蛋/动画/资源/两NBT/标签快照；source-closure99是Luna完整BenzorHome递归NBT、动态注册helper和进度父链报告。source-evidence99保存本路原JAR类引用和进度链；structures99保存NBT字节往返、几何和世界生成核对；validation99保存实际目标AST与原数据对照；integration99交共享增量；final99给最终生产哈希。调查报告中的target状态是其生成时快照，不代表最终交付状态。

关键分支已核原JAR javap：MaidenAttack主类及九个内类、Shielding、TickUpdate、EntityDies；Benzor交互、交易、初始化、黑火药检查和Kofoni检查。输出保存在D盘。原全局EntityAnimationFactory桥真实存在，不能误报同步字段断链。

## Benzor 完整行为

| 项目 | 保留内容 |
| --- | --- |
| 本体 | PathfinderMob，MONSTER类别，3×3.3，fireImmune，追踪64/更新3；HP75、护甲64、攻击0、速度0、跟随16、台阶0、XP0；永久保存、死亡20tick。原抗性999已满抗，目标合法值1保同样满抗。 |
| AI/碰撞 | 仅priority1看6格内玩家；canCollideWith及canBeCollidedWith恒true。没有新增近战、游荡或无敌。 |
| 免疫 | IN_FIRE、直接箭、直接药水/区域药水云、FALL/CACTUS/DROWN/LIGHTNING_BOLT/EXPLOSION/TRIDENT/FALLING_ANVIL/DRAGON_BREATH/WITHER/WITHER_SKULL。未扩大为所有爆炸，原没有PLAYER_EXPLOSION。 |
| 初始化 | 真实无重力。原regeneration命令缺give且参数位置错误，核1.20.1 EffectCommands后确认无效，未补不存在的永久回血。 |
| 交互 | 判断NPC自身潜行：解除并播warden.emerge NEUTRAL .6/3；否则开Main并记录玩家信息flag。不是检测玩家潜行。 |
| 动画/渲染 | 潜行idle，否则idle2，transition4；原无实际setAnimation调用。透明、动态Texture、固定benzorglow眼睛层、head跟随、阴影2.2、默认比例和死亡旋转。 |
| 来源 | 原真实蛋-1/-1，创造栏及Camera条目已有；BenzorHome自然来源完整接通。无原额外死亡loot表。 |

九页完整保留：Main、Info、SelfInfo、SpiceInfo、InfectionInfo、BlizzardInfo、BlackpowderInfo、SpiceEpl、Spice。实际目标AST与原Screen逐项核了尺寸、文字、图片、按钮及坐标。原静态benzoridlerubiconstatic肖像、Main交易按钮y17、Self背景x2、Info背景x-1均保留。

Main→Info→五话题→Info。首次交易→SpiceEpl并记介绍flag，后续直接Spice；介绍按钮→Spice。Main/Info重抽原两个问候，其余页继承；目标独立会话状态，不再全服共用static。信息与介绍flag持久保存并在死亡Clone保留。KofoniBenzorCheck没有execute调用，原Kofoni按钮也无门槛，没有强加新的解锁。

Spice三输入槽及36背包槽保原坐标、快速移动分段。各槽只接受adamantine_spice/mithral_spice/tourmaline_spice，分别每个2/4/8经验并消费整栈；任槽非空授予原whisper_advancement全部剩余criteria。无新增产物、扣币或固定交易次数。关闭/死亡/断连通过clearContainer返还或掉落剩余物品。

## Maiden 完整行为

| 项目 | 保留内容 |
| --- | --- |
| 本体 | Monster、MONSTER、fireImmune，1.2×3.1、追踪64/更新3；HP50、护甲6、速度.3、跟随16、台阶1、满抗性1、攻击击退属性1。普通自然消失；XP50、死亡60tick。 |
| AI | priority1近战1.2、距离平方256，无新增LOS；priority2玩家mustSee=false/mustReach=true；priority3游荡1、priority4受击目标、priority5漂浮。 |
| 免疫/声音 | IN_FIRE/FALL/CACTUS/DROWN/EXPLOSION，未扩大为PLAYER_EXPLOSION；iron_golem受伤和死亡声。 |
| 全局触发 | 源为Maiden且无冷却才取消并开技能；冷却中BloodBullet正常伤害。doHurtTarget只开技能并返回false，避免技能基础攻击10变成额外近战伤害。 |
| 单圈50% | attack1、CD90、移动锁44；第9个SERVER END真实STUNNED28；第29个END发8箭，原受害者提交时位置shulker.shoot HOSTILE1/2。 |
| 三圈50% | attack2、CD90、锁56；第9个END真实STUNNED45；29/34/39 END每轮8箭，共24；每轮同固定音位置。 |
| 弹序 | 八个原世界轴方向和顺序，Y=.17、偏差0。单圈速度.3、baseDamage系数10。三圈速度.4，每圈前七发7，末方向(-1,.17,1)为5；已核原JAR内类，不统一改成7。 |
| 子弹 | 当前施法者X/Z及Y+原受害者当前eyeHeight；静音、不暴击、零技能击退、禁拾取、40tick、无重力时序和RED_ORB逐tick效果复用真实BloodBullet。 |
| 护盾 | 没有真实STUNNED时取消攻击，shield.block NEUTRAL1/2；含外来STUNNED时开放受伤，hurt免疫仍有效。真实黏滞(.25,.05,.25)及效果可见性完整保留。 |
| 眩晕声画 | baseTick后每tick ENCHANTED_HIT1，Y+3.5、offset .001³；dispenser.fail NEUTRAL .2/5，保原服务端广播和客户端本地调用。 |
| 死亡 | 复用BossDeathEffectEntity的15次爆炸、offset2/3/2、每4 END、首次同步及死亡爆炸声；用super.die后的原生dead确认提交，取消死亡不误播；60tick末发XP。 |
| 渲染 | 原geo/animation/Texture、透明、阴影.7、默认死亡旋转；原无head跟随，不新增。idle/walk/attack1/attack2可达；fall原无入口，保资产但不造技能。 |
| 来源 | 原真蛋-1/-1及创造栏；borean_forest/borean_tundra/graveyard/vermillion_woods自然weight1、1只；ON_GROUND/MOTION_BLOCKING_NO_LEAVES、非和平、原生黑暗及刷怪规则。 |

配置由主线接thermal_shock_reforged-maiden.toml。技能基础攻击10乘1/.7/.5保原箭baseDamage系数；实际命中仍由AbstractArrow速度、护甲、无敌帧结算，不误称固定伤害10/7/5。生命、护甲、攻击、速度、抗性、倍率、概率及CD可配置，读档不回血。

## 实现替换与明确差异

1. 仅作内部标记的隐藏COOLDOWN/255级缓慢改实体计时与属性移动锁并保存；真实STUNNED、护盾窗口、声画没有删除。原隐藏效果不产生需要补画的可见粒子。
2. 有限Maiden载体最多39 END，加入失败不消费CD、提交防再入、先消费阶段再出弹；卸载暂停，读档不重放。玩家目标绑定原实例，读档或重连保旧eyeHeight，不误接新玩家。施法者移除保最后位置完成合法尾弹；确认转维则取消旧维任务，避免跨维坐标错误。
3. owner移除后的尾箭仍保存原Owner UUID：利用已核LegacyArrow公开的完整add/readAdditionalSaveData往返，只补Owner字段，然后正常设置技能参数，不对空NBT调用Entity.load。无活owner时原生getOwner返回null，之后可按UUID重新解析；没有保留移除实体强引用，也没有丢弃原归属元数据。正常施法直接setOwner，不增加序列化。
4. 交易先完整校验并消费，再授进度和经验；事务锁及active container/NPC UUID/维度/存活/8格距离校验防重复包、远程包和事件重入。缺必要advancement时保留输入并英文警告。
5. 原黑火药显示检查只接受ServerPlayer，LocalPlayer永远false，而原服务端包无同门槛。目标服务端查blackpowder_get并DataSlot同步按钮，点击复查。新增缺失blackpowder_get，保原库存1..64条件、显示、父spice_get；spice_get→loqendia_get已存在。
6. 固定文本本地化，55项中文及所需新增英文交主线。Benzor主手untagged_mobs:scrambler换rozneb分支按用户本批跨模组延期要求单独记录；原贴图保留，不加可选TOML。

## 来源闭合与原闲置数据

BenzorHome原17×15×15、3825块、39palette状态、22种方块、20真实BE、1只Benzor；BE库存全空，无物品堆或loot引用。MaterialContent.block和GeoDecorKind动态注册已展开，内部依赖均有真实实现。NBT转换保几何、state index、palette属性值、实体位置与未知字段；转换属性、命名空间、NeoForge持久字段和DataVersion3955。原/目标字节往返通过。

原三worldgen完整接入：jigsaw size1/max_distance64、underground_structures/beard_box、高度uniform -36..30、vermillion_woods/borean_tundra；random_spread spacing37/separation29/salt1835144946；单模板rigid权重1，structure_block忽略processor。未提高频率。ForgeCaps.structure_gel:gel_entity.portal=structure_gel:empty保为惰性证据，外部能力按用户授权延期，不阻止本模组自然来源。

MaidenChamber原17×20×17、5780块、三个boss_spawner内放普通/Micah/Umabel蛋。已核原注册没有boss_spawner和两种变体蛋，原源码/类及资源扫描未找到真实放置消费者；普通Maiden真蛋已注册。不能凭残留贴图或模板造新Boss，也不把未证实的入口写为“原生待迁”。保原件和报告，本批不激活该独立模板。以后若出现实际调用新证据，需重新评估。

## 验证与待运行验收

12Java通过AST语法、UTF8、无Java注释、自有路径diff检查。9页实际AST对原文字/图片/按钮和布局一致；25份实际使用资产字节一致，两蛋模型只改命名空间。原八向量/速度/16个系数、9/29/34/39时序、保存字段对称已核；274625种正常三槽数量组合符合原2/4/8经验。有限任务先消费阶段、冷却中箭伤害、STUNNED护盾及取消死亡已核控制流。这些是静态与独立数据模型核对，不是游戏实体或网络集成测试。

未编译、未运行服务端/客户端/Render Preview。主线统一编译；全模组完整后按用户要求交子代理验收：Benzor两姿态/透明/发光/head、9页/黑火药门槛/过滤/快速移动/关屏返还/死亡flag/重复与远程包；Maiden两技能全周期、护盾/外来眩晕/CD内箭命中、方向/速度/死亡取消/60tick经验、卸载读档/重连/转维/多实体；BenzorHome自然放置及四群系Maiden来源。不能把静态组合数当作游戏测试次数。

共享增量见integration99，最终逐文件及聚合SHA256见final99。停笔后不再修改生产输入，由主线统一集成验证。

