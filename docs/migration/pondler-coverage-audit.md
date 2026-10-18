# Pondler 静态覆盖核对

核对范围：原件 `D:\AI临时文件\ThermalShockMigration\thermal_shock-07.9-original.jar`、其 CFR 可读源码 `D:\AI临时文件\ThermalShockMigration\readable
et\mcreator\thermal_shock` 与项目当前 Pondler 源码/资源。仅做静态核对；没有运行游戏、服务端、客户端或测试。

## 实体及三个实体过程

- 注册与基本属性：原 EntityType 为 `WATER_AMBIENT`、尺寸 0.6×0.6、tracking 64、update interval 3，当前对应值相同。原 attributes 为移动速度 1、生命 2、护甲 0、攻击 0、跟随范围 16、游泳速度 1；当前属性也相同，并以 STEP_HEIGHT 0.6 对应原 `setMaxUpStep(0.6)`。PanicGoal(1.2)、RandomSwimmingGoal(1.0, 40)、WaterBoundPathNavigation、水路径惩罚 0、受流体推动关闭、碰撞可通行检查均已迁入。移动控制与原代码的水中上浮、朝向、垂直/水平行进步骤逐项相符，见 `PassiveSwimMoveControl.java`。
- 外观变体：原初生过程以 50% 概率设置橙色，否则默认绿色；当前用实体随机源选择两色，并保存/读取 `Texture`。两张纹理与 geo 和 animation 文件逐字节相同。Renderer 的半径 0.2、半透明 RenderType、模型/纹理/动画资源路径与原 renderer/model 相符。
- `PondlerOnEntityTickUpdateProcedure`：原实体处于非空气方块时每 tick 加入 1 tick、amplifier 255 的抗性；离开地面且无 cooldown 时，实体位于空气方块则朝视线方向以 Y=1 弹起，并获得 80–300 tick 冷却；否则 Y=0.5。当前用 `jumpCooldown` 替代冷却效果，保存于 NBT；仍在非空气方块时通过伤害拦截模拟原抗性，同时让带 `BYPASSES_EFFECTS` 或 `BYPASSES_RESISTANCE` 标签的伤害通过。未发现把药水效果继续用作内部计时器。
- `PondlerEntityIsHurtProcedure`：原在实体当前位置非空气时，受击前按视线方向施加水平击退；当前保留该条件与向量，并设置 `hasImpulse` 供同步。溺水伤害仍被忽略。
- `PondlerOnInitialEntitySpawnProcedure`：原只处理 50% 橙色变体；当前 `finalizeSpawn` 的随机分支等价。
- 动画控制：原 movement controller 在水/气泡中循环 `idle`，岸上循环 `landidle`；jump 由单次过程动画触发。当前控制器保留同样的 movement 选择与 jump trigger。`idle`、`jump`、`landidle` 三段原始动画数据未变化。
- 水生身份迁移：目标 1.21.1 源码已不存在 `MobType` / `getMobType()`；当前以实体类型标签表达。`can_breathe_under_water` 中包含 Pondler，目标 `LivingEntity.canBreatheUnderwater()` 通过该标签判定；`sensitive_to_impaling` 也包含 Pondler，目标附魔 `impaling` 的伤害谓词读取此标签。因而不应尝试恢复已删除的旧 API。
- 群系自然生成仍列为迁移文档中的 pending 维度链：原 `pondler_biome_modifier.json` 对 vermillion_woods 配置 weight 70、每组 1–2 只；当前仅有“当前位置及上方均为原版水”的 spawn placement。该 modifier 尚未迁入，属于已记录的待闭合内容，不是本批 Pondler 主体实现遗漏。

## Pondler Egg 方块与方块实体

- 方块性质：原强度 0.2/爆炸抗性 0、POINTED_DRIPSTONE 音效、noOcclusion、非红石导体、可水浸；light block 0、流体为空时透天光、visual shape 为空。当前注册属性与这些值相同，保留 WATERLOGGED 和放置/邻块更新逻辑。
- 孵化周期：原方块放置后 20 tick 计划第一次 block tick，以后每 20 tick 调用一次过程。过程先检查 `age >= 600`，再在未孵化分支把 age 加 1。因此 age 从 0 开始时，第 600 次调用后 age 才变为 600，第 **601 次**调用才孵化，即放置后 **12020 tick（20×601）**。当前 `advanceIncubation()` 的“先检查 `>=600`，否则加一”与原判断时序一致；不是放置后 600 tick 孵化。`IncubationAge` 随区块实体 NBT 保存。
- 孵化流程：原先无掉落摧毁方块、播放 Turtle Egg Crack、按 `MOB_SUMMONED` 生成 Pondler；当前顺序相同，摧毁不丢方块本身，但 onRemove 会掉出方块实体库存。非孵化 tick 每次有 10% 概率播放同一裂纹声；当前也保留。
- 孵化音效参数：原调用传入 pitch -1.0，当前传入 0.5F。目标版 `SoundEngine.calculatePitch()` 使用 `Mth.clamp(pitch, 0.5F, 2.0F)`，因此原负值实际播放时也会被限制为 0.5；当前值保留了实际听到的音高，不构成功能或效果缩水。普通 10% 裂纹声 pitch 1.0 保留。
- BE 容量与自动化：原 9 个槽、容量 64、任意物品可放入；每个方向都暴露 0–8 槽，漏斗等自动化可插入与抽出所有槽。当前 `WorldlyContainer` 同样开放 9 槽及双向传输，并注册 NeoForge `ItemHandler.BLOCK`，按方向提供 `SidedInvWrapper`。NBT 中库存持久化；方块移除时掉出库存。方块比较器信号也保留。
- 菜单：原 `createMenu` 调用不带该 BE 的 `ChestMenu.threeRows(id, inventory)`，没有把 9 槽 BE 传给菜单；当前明确创建连到 BE 的 `GENERIC_9x1` 菜单。这是修复原 MCR 菜单脱离库存的问题，显示九槽并能实际操作。
- 旧过程每次更新 age 后调用 `sendBlockUpdated`；当前只调用 `setChanged()` 持久化 age。静态代码未找到客户端展示孵化年龄的用途，尚不能认定这造成可见差异；如果要逐位兼容旧同步行为，可考虑在定期 tick 标记方块更新。
- Pondler 的区块实体与方块注册已存在，独立的 `pondler_egg` 方块物品模型及其方块掉落表也存在。它不同于 `pondler_spawn_egg`，两者均已保留。Spawn egg 注册继续使用旧资源贴图。

## 掉落与资源比对

- 地图颜色覆盖清单共 354 项：353 个 `simple-definitions.json` 方块 ID 全部在清单内，另含已注册的方块实体蛋 `pondler_egg`。蛋的注册属性显式设置 `MapColor.COLOR_GREEN`，清单项为 `semantic_color / COLOR_GREEN`，与绿色蛋模型语义一致；核对无缺项。
- 原 Pondler 实体掉落表：1 次 roll，raw_pondler 权重 4、数量 1；pondler_egg 权重 1、数量 0–2。当前 `data/thermal_shock/loot_table/entities/pondler.json` 内容一致。
- 原 Pondler Egg 方块掉落：幸存爆炸条件下掉 1 个 pondler_egg。当前 `data/thermal_shock/loot_table/blocks/pondler_egg.json` 相符。
- Python zipfile 逐字节比较原 JAR 与项目资源：Pondler geo、animation、green/orange textures、egg blockstate、block/item model、spawn egg model 均为 MATCH。
- 原自然群系 modifier 具体配置：`data/thermal_shock/forge/biome_modifier/pondler_biome_modifier.json`，维米利恩森林（vermillion_woods），weight 70，群组 1–2。该条按迁移文档维度链状态保留为 pending；双水方块 spawn placement 不等同于群系生成频率配置。

## 证据定位

- 原实体：`readable/.../entity/PondlerEntity.java`；三个实体过程：`PondlerOnEntityTickUpdateProcedure.java`、`PondlerEntityIsHurtProcedure.java`、`PondlerOnInitialEntitySpawnProcedure.java`。
- 原孵化过程：`readable/.../procedures/PondlerEggOnTickUpdateProcedure.java`；原方块与 BE：`readable/.../block/PondlerEggBlock.java`、`readable/.../block/entity/PondlerEggBlockEntity.java`。
- 当前实体：`src/main/java/com/hp/thermalshock/entity/PondlerEntity.java`；当前移动控制：`PassiveSwimMoveControl.java`；当前方块/BE：`src/main/java/com/hp/thermalshock/content/PondlerEggBlock.java`、`PondlerEggBlockEntity.java`；注册：`ModEntities.java`、`ModBlockEntities.java`。
- 目标源码证据：`EntityTypeTags.java` 定义 `CAN_BREATHE_UNDER_WATER` 与 `SENSITIVE_TO_IMPALING`；`LivingEntity.java` 通过 `CAN_BREATHE_UNDER_WATER` 判断水下呼吸；`Enchantments.java` 的 Impaling 谓词读取 `SENSITIVE_TO_IMPALING`；`client/sounds/SoundEngine.java` 的 `calculatePitch` 将音调限制在 0.5–2.0。
- 当前标签文件 `data/minecraft/tags/entity_type/can_breathe_under_water.json` 与 `sensitive_to_impaling.json` 均包含 `thermal_shock:pondler`。当前 `docs/migration/map-colors.json` 有 354 个唯一条目，Pondler Egg 项为绿色语义色。
- 本报告只说明源码和资源静态比对结果，不替代用户要求的完整迁移后服务端/渲染验证；当前项目规则要求完成全模组迁移前不运行游戏测试。


