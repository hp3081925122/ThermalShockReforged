# 第118批A共享接线交接

基线90665ea。本文件只描述主线待接内容，A没有修改共享入口、ClientEvents或ProjectileRenderers。A已停笔，不继续扩展范围。

## 必须接入的三项

1. 根构造器既有ModEntities.register(bus)附近，使用同一个模组事件总线注册新增实体：

```java
com.hp.thermalshockreforged.entity.ZapDetonationEntity.register(bus);
```

2. ClientEvents实体渲染注册事件中加入无显示载体绑定：

```java
event.registerEntityRenderer(com.hp.thermalshockreforged.entity.ZapDetonationEntity.TYPE.get(), net.minecraft.client.renderer.entity.NoopRenderer::new);
```

该载体注册名为zap_detonation，无属性、模型、语言、刷怪蛋或掉落表接线需求。不能用新载体替代原ZapWarning本体；本体仍保留原警告、属性和免疫行为。

3. client/projectile/ProjectileRenderers的register中，仅替换VOLCANO_BULLET现有ThrownItemRenderer绑定：

```java
event.registerEntityRenderer(ProjectileContent.VOLCANO_BULLET.get(), SmallBeamRenderer::new);
```

SmallBeamRenderer与该注册类同包，已泛化至LegacyArrowEntity。复用既有FirearmBulletRenderer模型层，无新增模型层注册；几何、UV、旋转、zerkglow纹理不变。不要改变其他弹体绑定。

## 主线记录要点

- IceSquad五kind改为首次实际tick启动SERVER END寿命，保留5、20、80的原差异并兼容旧Age存档。
- Mangler以60位待办保留每次受伤独立解除动画的时点，单独持久化实际stunned状态，并清理换维取消后的可见冷却状态。
- ZapWarning使用独立可持久化载体承接20 SERVER END，普通死亡后保留尾阶段，使用固定锚、伤害快照和调用方ignoreCaster。强制移除、换维取消及卸载分支需要运行验收。
- Volcano恢复原小激光模型的绑定需由主线完成。
- Guardian保留第51批明确记录的粒子位置修复，撤销第117批“未找到记录”的待决判断，没有新增生产行为改动。

## 验证限制与未验收项

静态审计511项：502通过、9失败。9项均为IceSquad非计时方法全文一致性比较，可能有换行因素，但没有核实；不能标记全通过。详见remaining-entity-dependencies-a118-validation.json及A主文档。Python参考模型和AST结果不代表Java运行行为。按主线要求不再搜索或重跑，未编译、启动、提交。

已知未验收边界：外部传送造成警告本体与固定锚载体跨区块分离，本体卸载而载体先完成时，本体可能保留旧任务UUID。当前未加入世界级完成台账。请主线决定后续处理，不能把这一边界视为已完成。

主线集成后应先编译，再按主线安排进行IceSquad寿命与存档、Mangler重叠受伤及换维、Zap死亡尾阶段/存档卸载/强制移除/伤害归属测试，并使用Render Preview比较Volcano原模型。A本路不执行这些运行测试。
