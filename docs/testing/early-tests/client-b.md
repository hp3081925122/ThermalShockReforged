# Thermal Shock 提前测试 B 路（客户端与渲染）

- 任务名：`/root/early_client_b`
- 基线：`8397254821278a42c577addcb6f1f34e53c97ebc`
- 原件 SHA256：`3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436`
- 独立快照：`D:\AI临时文件\ThermalShockMigration\early-tests\8397254\client-b`
- 状态：等待父代理明确通知快照解压完成；当前尚未读取快照，未执行构建或游戏。

## 已完成准备

- 已读取 `C:\Users\30819\.codex\skills\mcreator-rework\SKILL.md` 和 `minecraft-mod-dev-helper` 技能。迁移资源核对需按注册 ID、引用关系、动画 clip、粒子及技能表现追踪；资源存在本身不等于渲染通过。
- 只读检查 `E:\模组\发cf的模组\Render Preview\1.21.1-neoforge-render-preview\README.md`、`VALIDATION.md` 和 `build.gradle`。该工具目标为 MC 1.21.1 / NeoForge 21.1.251 / Java 21，可运行离线预览、采样 GeckoLib 4 动画、渲染实体/粒子/物品/装备/天空/屏幕效果，并通过本机 HTTP 接口输出 PNG 与 JSON。无 HP 适配器时仍能用通用目录覆盖注册实体、物品、装备、简单粒子和 GeckoLib 实体动画。
- 工具文档明确说明：复杂粒子参数、自定义天空、服务端生成的多实体技能和依赖特殊世界数据的效果需要专用适配器；客户端预览不执行服务端战斗 AI、伤害或完整技能生成流程。
- 预定预览启动命令（仅在父代理放行且重型客户端空闲后执行）：`gradlew.bat runClient -PpreviewOpen -PpreviewValidation -PpreviewHpJar=<构建出的 Thermal Shock JAR绝对路径>`。构建/启动前还需确认本机 Java 21 与工具依赖可用，并核对 `D:\AI临时文件\RenderPreview\latest.json` 的客户端目录和端口，避免连接其他实例。

## 待做与未覆盖

- 待父代理通知快照完成后，按固定基线只读盘点资源路径、文件数与哈希，并对照可达注册/渲染引用；逐个核对模型、贴图、Geo/动画文件和 clip 名、声音事件、粒子注册/调用。未迁移内容记录为待办，不作为失败。
- 待拿到迁移后的目标 JAR 或目标树并获准预览后，比较原件与目标的资源 ID/内容及可渲染条目；输出截图与 JSON 证据，记录实际执行参数和未覆盖项。
- 当前没有渲染结果、截图、FPS 数据或游戏内功能通过结论。
