# Heart Crystal 静态覆盖核对

核对对象：原件 `thermal_shock-07.9-original.jar` 中的 `HeartCrystalItem`、`HeartCrystalRightclickedProcedure`、`HeartBoostMobEffect`，及当前 1.21.1 NeoForge `HeartCrystalItem` / `ModEffects`。同步检查原 JAR 的配方、掉落 JSON 和外部产出引用。只读核对；未运行游戏或测试，未修改主工程。

## 使用效果与属性

| 项目 | 原 JAR | 当前实现 | 静态结论 |
|---|---|---|---|
| 初次 Heart Boost | 无旧效果时 amplifier = 1 | `previous == null ? 1` | 一致 |
| 再次使用 | 当前 amplifier + 1 | `previous.getAmplifier() + 1` | 一致；每次刷新时长并继续叠层 |
| Heart Boost 时长 | 5000 tick | 5000 tick | 一致 |
| 回复效果 | Regeneration，20 tick，amplifier 9 | 同数值 | 一致 |
| 最大生命 modifier | MAX_HEALTH，ADD/+4，按效果等级叠乘 amplifier+1 | `Attributes.MAX_HEALTH`，`ADD_VALUE` / +4 | 一致；1.21.1 `MobEffect.AttributeTemplate.create` 明确计算 `amount * (amplifier + 1)`，所以总增量为 `4 × (amp + 1)`生命值点 |
| 效果颜色 | Java 有符号整数 -39271 | `0xFF6699` | RGB 均为 `#FF6699` |
| 音效 | `block.enchantment_table.use`，NEUTRAL，volume 1，pitch 4 | `ENCHANTMENT_TABLE_USE`，NEUTRAL，1，4 | 资源与参数一致 |
| Tooltip | `item.thermal_shock.heart_crystal.description_0` | 同一翻译键 | 一致；英文描述仍为“暂时提高最大生命值，可叠加” |
| 物品属性 | 最大堆叠 1，RARE | 同样是 stack 1、RARE | 一致 |

原 `HeartBoostMobEffect.isDurationEffectTick()` 每 tick 返回 true，但类没有覆盖实际 tick 效果；因此每 tick 钩子没有额外治疗/属性行为。当前效果只定义最大生命修饰符，不添加无关 tick 逻辑，与实际作用相符。

## 消耗语义

原物品右键后执行 procedure：玩家背包按 Heart Crystal 类型清除 1 个，随后服务端应用 Heart Boost 与 Regeneration；音效由服务端广播，并在客户端本地播放。当前 `use` 在服务端对使用手中的 `ItemStack` 扣 1，并应用相同两个效果及音效，最后返回 `sidedSuccess`。普通使用时消费数量一致。当前固定扣使用的那一枚，避免了原 procedure 从背包/合成格任意匹配并清掉另一枚的 MCR 位置歧义；这是消费目标更明确的修复。

## 物品来源链：没有配方或 loot table

- 在原 JAR 的全部 `data/**/*.json` 中搜索 `heart_crystal`：没有任何 recipe 或 loot table 引用；所以原模组没有合成配方，也没有静态掉落表直接产出它。
- 原 JAR Java 引用中有三条外部产出链，均受 `loqendiaDropHeartCrystals` 游戏规则控制，规则类别 DROPS，默认 true：
  - `CavebloomerBossBoxOpenProcedure`：打开 Cavebloomer Boss Box 时若规则开启，服务端在盒子位置生成 1 个 Heart Crystal，拾取延迟 10 tick。
  - `IcidroburnerEntityDiesProcedure`：Icidroburner 死亡时相同条件生成 1 个。
  - `IcidrogunnerEntityDiesProcedure`：Icidrogunner 死亡时相同条件生成 1 个。
- Heart Crystal 自身还注册在创造栏中。
- 当前主源码只查到物品注册、Tooltip、使用逻辑和效果注册；`loqendiaDropHeartCrystals` 目前只在保留的英文翻译中出现，未找到对应 gamerule 注册、Boss Box 或这两个 Icidro death producer。按整模组迁移进度，这些属于外部内容来源链尚待迁移，不能误报成配方/loot 遗漏，也不应把来源链逻辑塞入 Heart Crystal 物品本身。

## 证据位置

- 原可读源码：`readable/.../item/HeartCrystalItem.java`、`readable/.../procedures/HeartCrystalRightclickedProcedure.java`、`readable/.../potion/HeartBoostMobEffect.java`。
- 原外部产出：`readable/.../procedures/CavebloomerBossBoxOpenProcedure.java`、`IcidroburnerEntityDiesProcedure.java`、`IcidrogunnerEntityDiesProcedure.java`、`init/ThermalShockModGameRules.java`。
- 当前实现：`src/main/java/com/hp/thermalshock/content/HeartCrystalItem.java`、`ModEffects.java`；物品属性位于 `MaterialContent.java`。来源链缺失核对范围是当前 `src/main/java` 与 `src/main/resources/data/thermal_shock/{recipe,loot_table}`。
- 1.21.1 乘数依据：本机目标源码 `net/minecraft/world/effect/MobEffect.java` 的 `AttributeTemplate.create`，其中默认公式为 modifier amount 乘以 `amplifier + 1`。
