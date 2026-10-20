# 第145批客户端 Render Preview 全量渲染验收

日期：2026-10-20

## 测试输入

- 构建 JAR：`build/libs/thermal_shock_reforged-1.21.1-neoforge-0.0.1.jar`
- JAR SHA-256：`00C2119903CE6DCFC4D02CB6705E3F2F20B923BDC9633FF0C174184AA57A7334`
- Render Preview：`E:\模组\发cf的模组\Render Preview\1.21.1-neoforge-render-preview`
- Minecraft 1.21.1、NeoForge 21.1.251、Java 21、GeckoLib 4.9.2、AAA Particles 2.3.3
- API：`http://127.0.0.1:18761`
- 场景：离线预览，`minecraftServer=false`、`worldAttached=false`、深色背景、无地面、1280×720
- 每个条目先通过 `/control` 定位，再通过 `/capture` 导出 PNG 和同名 JSON；脚本会等待捕获结束并对接口时序错误重试。

## 全量结果

| 类别 | 总数 | 捕获成功 | 捕获失败 | 成功但前景像素为 0 | magentaRatio > 0 |
| --- | ---: | ---: | ---: | ---: | ---: |
| entities | 204 | 202 | 2 | 64 | 0 |
| effects | 52 | 50 | 2 | 23 | 0 |
| particles | 52 | 52 | 0 | 20 | 0 |
| equipment | 32 | 32 | 0 | 0 | 0 |
| items | 1000 | 1000 | 0 | 1 | 0 |
| animations | 388 | 378 | 10 | 4 | 0 |
| 合计 | 1728 | 1714 | 14 | 112 | 0 |

可靠批次结果目录均位于 `D:\AI临时文件\RenderPreview\20261020-223155-32320`，汇总文件为：

- `reliable-entities-20261020-233555/summary.json`
- `reliable-effects-20261020-230541/summary.json`
- `reliable-particles-20261020-230649/summary.json`
- `reliable-equipment-20261020-230738/summary.json`
- `reliable-items-20261020-230814/summary.json`
- `reliable-animations-20261020-232000/summary.json`

## 明确失败项

实体和动画失败项是同一组模型骨骼问题：

- `entities/thermal_shock_reforged:willograde`
- `entities/thermal_shock_reforged:raincatcher`
- `animations/thermal_shock_reforged:willograde/idle`
- `animations/thermal_shock_reforged:willograde/walk`
- `animations/thermal_shock_reforged:willograde/bury`
- `animations/thermal_shock_reforged:raincatcher/idle`
- `animations/thermal_shock_reforged:raincatcher/walk`
- `animations/thermal_shock_reforged:raincatcher/run`
- `animations/thermal_shock_reforged:raincatcher/walk2`
- `animations/thermal_shock_reforged:beetle_thrasher/walk`
- `animations/thermal_shock_reforged:graver/walk`
- `animations/thermal_shock_reforged:graver/walk2`

Render Preview 日志给出的根因是 `Animation bone missing`：Roly Poly 动画引用 `bone2`，Raincatcher 动画引用 `eyes`、`leg3` 等，Beetle Thrasher 动画引用 `antennae`，Graver 动画引用 `shoulder2`；对应 `roly_poly.geo.json`、`raincatcher.geo.json`、`beetle_bastion.geo.json`、`chorpse.geo.json` 中不存在这些骨骼。对迁移前的 `thermal_shock` 资源执行同样对账，动画与几何的缺失骨骼引用已经存在，因此这是原资源/Preview 严格骨骼校验暴露的问题，不是命名空间迁移新增的问题。没有擅自补骨骼或改动画，以免改变原件模型结构。

特效失败项：

- `effects/thermal_shock_reforged:bigredshortorb/aaa`
- `effects/thermal_shock_reforged:hit_hanmado_0409/aaa`

日志根因均为 `AAA native effect failed to load`，其余 50 个 AAA 特效均成功捕获。

## 零前景像素说明

零前景条目均已完成 `/control` 和 `/capture`，没有 `captureError`、没有 magenta 像素；主要是投射物、声音/逻辑任务实体，或在 tick 0 尚未产生可见帧的粒子/动画入口，不能按渲染异常计数。已保留对应 PNG 和 JSON 供逐项复核。

## 结论

客户端 Render Preview 已覆盖 Thermal Shock 命名空间的 1728 个目录条目。1714 个条目完成有效捕获，所有有效捕获均无紫色占位纹理；14 个失败项已经定位到 2 个 AAA 原生特效资源加载失败，以及 4 个实体模型/动画文件的缺失骨骼引用。普通客户端启动和此前第144批代表性实体、物品渲染结果仍保持通过；本批没有修改生产代码。
