# 第117批主线：实体回调与传递过程

基线提交：`27f8be1`。原实体回调根共129个、241处实际调用、177种传递过程。本线承担排序后第87至129根，共
43
根；迁移A承担前86根。

主线范围见`entity-callbacks-main-assignments117.json`，A范围见`entity-callbacks-a-assignments117.json`。所有实体必须逐项保留父类语义、属性、目标、技能、伤害倍率、击退、冷却与上限、掉落、生成、动画、声音、粒子、持久化和卸载重载边界。

共享注册、ClientEvents、配置、语言、标签由主线在两路交付后统一接入；双方不得共改同一生产Java文件。完成前不编译、不启动游戏、不修改`docs/testing/*`。

### 已完成的别名语义复核

- `StopperOrbEntity` 继承的 `OrbEntityBase` 用保存的 `OrbGraceTicks` 原生计时器承接原隐藏 `COOLDOWN` 的100 tick窗口；伤害范围、潜行过滤和蓝色粒子仍由实体分支提供。
- `StriderAttackEntity` 对应 `IceSquadProjectileEntity` 的 `strider_attack` 分支，服务端寿命为5 tick并强制无重力。
- `SpringslimeProjectileEntity` 对应 `SpringslimeProjectile`，方块命中后在命中方块偏移(1,1,1)生成一只Spring、清零速度并一次性消费投射物。
- 三种Jellercel共用 `JellercelEntity`；原实体的攻击属性为0，碰撞过程单独造成1点仙人掌伤害，已修正目标共享属性与碰撞分支的混用。
