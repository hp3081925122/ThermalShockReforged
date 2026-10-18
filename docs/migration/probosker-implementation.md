## Probosker 实施批次（2026-10-18）

保留AMBIENT分类、0.9×1.0尺寸、32生命／6基础伤害／0.25速度／16范围、河豚音效、狼脚步声、1经验、1/3驯服概率、六种鱼食物、仅Crystal Koi诱惑、营养值回血、40%紫色生成、双贴图、原geo、六段动画资源及头部转向。透明渲染、0.7阴影和1.0缩放不变。原掉落表按目标单数loot_table目录迁入，保留蓝羊毛0–2、吸管0–1池规则及random_sequence。

原蹲伏flag+每tick缓慢255替换为TamableAnimal持久化坐姿和SitWhenOrderedToGoal；不引入抗性或免伤。源hurt仅排除溺水伤害。原右键无条件翻转坐姿会让陌生人、喂食和刷怪蛋也切姿；现在主人非食物交互切姿，喂食与刷怪蛋不切姿，保留对应吹气声音。

OwnerHurtByTargetGoal从错误的goalSelector移至targetSelector。原两类猎物及Monolith威胁按实体注册ID选择，保留原目标优先级／检测范围／速度；它们实体尚未迁入，跨实体战斗验收仍待闭合，不注册占位实体。没有增加原本不存在的跟随主人AI。近战仍保留距离平方1的原阈值，并使用基础攻击属性。

原主人满血喂食走Animal.mobInteract，可进入love；原类与父类均没有自动添加BreedGoal。因此本批保留求爱和后代创建，没有把后代工厂存在写成实际繁殖已完成，也没有无说明新增繁殖AI。此缺陷待后续完整行为评审。

自然生成谓词保留脚下grassy_shale／snowy_shale、可见天空及白天限制；两个地表方块及三个源群系未闭合，原biome modifier暂不导入。Wide Angle图鉴与Louse反向回避同样待对应模块迁移。

Probosker回归测试与渲染仍分别记录；编译成功不表示真实视觉一致。原资源文件不修改。

动画控制器复核：procedure触发动作播放时暂停movement控制器，保留原专用动作独占语义，避免idle/walk与jump等动作同时驱动同一骨骼。GeckoLib4.9.2的isPlayingTriggeredAnimation已通过依赖字节码核对；真实画面仍待完整迁移后的Render Preview对照。
