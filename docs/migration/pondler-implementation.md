# Pondler 与孵化蛋迁移（2026-10-18）

## 原件与保留行为

来源仍为07.9原JAR及readable源码。Pondler为WATER_AMBIENT，0.6×0.6、2HP、0护甲、0攻击、16范围、1移动及游泳速度、0.6台阶、2经验；保留原游泳／逃跑AI、河豚以外的鳕鱼音效、水下呼吸／穿刺标签、溺水免疫、无水流推力和生成点上下两格水检查。

50%橙色／默认绿色变体，仍保存Texture并限制为真实原纹理。透明渲染、0.2阴影、1.0缩放、原geo和三段idle／landidle／jump动画全部保留。没有将Pondler套用为Koi的swim动画。

原tick在脚下不可遮挡且冷却结束时，主体是严格的minecraft:air便沿视向水平0.2、向上1跳跃，冷却为round(80+randomDouble×220)，并播放jump；主体非air则每tick向上0.5。原hurt在主体非air时先沿视向水平1冲刺，再处理伤害。目标保留这些条件、速度、随机分布；服务器作为唯一逻辑来源。

原极高抗性替换为伤害吸收阶段的条件规则：主体非air时抵消普通伤害，但仍允许bypasses_effects／bypasses_resistance伤害；DROWN独立保持免疫。不使用笼统isInvulnerable吞掉所有伤害。原代码不是单纯“在水中”：非air也包括其他方块，这个字面条件未在本批擅自改成isInWater。原cooldown药水改为PondlerJumpCooldown整数字段，持久化、读取限0–300，不再每tick加效果。

掉落保留单池权重4:1：raw_pondler为1个，pondler_egg为0–2个，并保留random_sequence；不是每次必掉鱼再额外掉蛋。已有200tick熔炉、100tick烟熏、100tick营火三配方保留。

## 蛋链闭合与原缺陷修复

蛋保持WATERLOGGED、原模型、完整碰撞、空视觉形状、无光遮挡、干燥透天光、POINTED_DRIPSTONE音效、硬度0.2／抗爆0、非导体。新增绿色地图色使其拥有正常MapColor，未修改遮挡来强制地图绘制。

原方块每20tick调用孵化过程，过程先检查age>=600再增一；初始age=0时第601次调用孵化，即12020游戏tick。目标保留这一次序和单位，不缩成600tick。每次未孵化调用仍有10%裂蛋声；孵化声原pitch=-1超出合法播放范围，目标用原客户端实际下限0.5表达低音。

蛋原有9槽inventory、六方向自动化、比较器输出以及破坏／孵化时掉出库存，全部保留。目标用真实9槽绑定菜单替代原三行空菜单工厂（原未绑定BE，显示数量也不一致），未新增直接右键打开入口。仅库存和持久化确有改变时标脏，不每秒sendBlockUpdated广播整份NBT；孵化年龄不影响外观，不需同步年龄进度。孵化移除蛋、不掉蛋本体、生成一个Pondler；含水移除保留原流体恢复流程。

## 尚未闭合与验证边界

vermillion_woods维度／群系自然生成、蛋的世界生成来源、NetBullet捕捉和WideAngle图鉴由各自模块后续迁移。Chilloo蛋虽复用了同一原procedure，但本批不注册假Chilloo实体，不把Pondler蛋完成算成Chilloo链完成。

按用户最新AGENTS.md，整模组代码完整迁移前只做源码／数据静态核对及编译，不启动服务端或客户端。最终服务端和Render Preview对照均交给子代理执行。本批不能声称游戏内孵化、原画面或整个模组已通过验收。

动画控制器复核：procedure触发动作播放时暂停movement控制器，保留原专用动作独占语义，避免idle/walk与jump等动作同时驱动同一骨骼。GeckoLib4.9.2的isPlayingTriggeredAnimation已通过依赖字节码核对；真实画面仍待完整迁移后的Render Preview对照。

并行复核修正：NeoForge21.1.233的actuallyHurt忽略getDamageAfterMagicAbsorb返回值，读取DamageContainer.newDamage；因此Pondler原单return0不足以免伤。本批改在同阶段写MOB_EFFECTS全额减免，并保留原抵抗统计与两种绕过标签，未继续用抗性药水。此项是新确认修复，先前编译通过不能证明条件免伤实际有效。
