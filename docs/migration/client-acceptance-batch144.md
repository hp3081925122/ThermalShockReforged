# 第144批客户端与 Render Preview 验收

日期：2026-10-20

本批使用最终构建 JAR：`build/libs/thermal_shock_reforged-1.21.1-neoforge-0.0.1.jar`，SHA-256：`00C2119903CE6DCFC4D02CB6705E3F2F20B923BDC9633FF0C174184AA57A7334`。

## 普通客户端

- 实例：`E:\模组\整合包环境\1.21.1-neoforge`
- Minecraft 1.21.1、NeoForge 21.1.248、Java 21.0.11
- 客户端成功到达主菜单并创建离线测试世界，MCP HTTP 接口在后台持续返回截图。
- Thermal Shock Reforged、GeckoLib 4、AAA Particles 2.3.3 和 ModDev MCP 均成功加载。
- 重新加载资源后，原先 9 项物品模型的缺失贴图告警消失；修正内容是为 displaysettings 父模型补齐 `thermal_shock_reforged:` 命名空间，并修正 4 个非法粒子纹理路径。

## Render Preview

- 工程：`E:\模组\发cf的模组\Render Preview\1.21.1-neoforge-render-preview`
- Minecraft 1.21.1、NeoForge 21.1.251、Java 21、GeckoLib 4.9.2、AAA Particles 2.3.3
- API：`http://127.0.0.1:18761`
- 输出：`D:\AI临时文件\RenderPreview\20261020-212416-40964`
- `/status`：`minecraftServer=false`、`worldAttached=false`、`capturing=false`
- `/catalogue`：3344 个总条目，其中 Thermal Shock 命名空间 1728 个条目
- 已实际导出 11 张 1280×720 PNG 与同名 JSON：默认 Allay、Benzor、Forecast、Frost Titan、Icidroviathan、High Director Sopel、Core Eject Flare Gun、Magnetic Crossbow、Ruby Spear、Bloodstone Halberd、The Judge。
- 上述截图全部有非零前景像素，`magentaRatio=0`，捕获过程没有 `captureError`。

## 剩余告警

客户端和 Preview 日志仍报告以下 3 个未被方块状态或物品父模型引用的旧资源：

- `models/custom/glauko_bloon.json`
- `models/custom/glaukobloom.json`
- `models/custom/voltflower_rubicon.json`

这三个文件使用 Blockbench 多轴 `x/y/z` 旋转对象，1.21.1 原生 BlockModel 解析器要求单轴 `axis` 字段，因此在资源扫描阶段报 `Missing axis`。它们没有进入本批实际预览条目；未直接删除或改写几何，避免未经原件对照改变潜在渲染。

结论：客户端启动、资源加载、普通实体/物品渲染和 Render Preview 离线渲染均通过；全资源目录仍保留上述 3 个原始模型格式告警，不能把本批记为零告警全绿。
