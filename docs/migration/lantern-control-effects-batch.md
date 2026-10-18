# 致盲灯与控制效果迁移

原07.9类、真实caller及目标1.21.1源码作为证据，先写矩阵再实施。stunned是实际施加给目标的控制效果，不是内部CD标记；tremors是真实随机速度扰动，两者保留效果ID、颜色、类别、每tick行为与原可见性，空curative仍保图腾原生清除。当前只代码迁移，整模组完成后才两端测试。

BlindingLantern唯一触发是onEntitySwing，不是use。耐久565、UNCOMMON、附魔值6、不因换装重播；冷却265tick，45个FLASH、偏移各5/速度0.3、原lanternflashattack声音。以本体位置6格AABB按距离查询，排自己和所有已驯TamableAnimal；先无owner GENERIC3伤，再对LivingEntity施stunned60tick/amp1/无粒子，保持不耗耐久不耗物品。范围伤害和表现由服务器权威处理，避免双端执行伤害；客户端保原手持变换与透明Geo渲染。

原selected inventoryTick每tick施NO_MELEE1tick/amp1，靠ENTITY_REACH -20*(amp+1)把范围降为0。改原生ENTITY_INTERACTION_RANGE临时-40，局部1tick尾效持久化与解除，不注册伪状态药水、不擅自硬取消全部伤害；原生属性同步仅在真实变化时发生。其他武器同类来源后续复用此明确语义。

灯动画0循环与1单次由目标GeoItem原生逻辑stackID和triggerAnim隔离，替换旧单例animationprocedure。缓存定时回收闲置manager并在离开世界清理；不得积累所有历史物品ID。原动画、Geo、贴图、displaysettings和配方保留。配方材料corrial_ingot/chain/glowstone/ferrum_plating均已实现。

stunned每tick makeStuckInBlock(AIR,Vec3(.25,.05,.25))；tremors每tick XYZ独立[-.1,.1]增量，复用实体随机源替代每轴新建RandomSource，不减少抽样/幅度/频率。tremors隐藏库存和HUD，stunned保持原默认显示。其他原来源随各实体/武器迁移，不能凭这批效果注册宣称来源全部闭合。

加载尾效：EntityJoinLevel事件读取保存的剩余1tick并恢复transient触距modifier，未持灯时首个Post解除，持灯时由inventoryTick续时。死亡Post解除，无永久药效或世界级队列。原存档的三处异常NaN/Inf伤害倍率另作finite置0防御，正常值不改。
