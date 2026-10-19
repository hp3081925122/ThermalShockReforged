# 完整迁移后的客户端与渲染验收计划

2026-10-20按用户最新分工记录：全部代码完整迁移并完成无简化复核后，由生产主线安排子代理进行客户端及Render Preview原件对照测试，保留具体结果与截图/日志。当前第111批仍在生产，未启动客户端。服务端由用户独立测试聊天负责，生产线不联系、不催促、不重复执行，也不把进行中视为通过。

## 执行边界和记录

以最终提交和固定构建JAR/SHA为输入，原件固定07.9 JAR（SHA256为3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436）。先记录游戏/加载器/Java/库版本和实际启动命令；测试代理负责执行和证据，生产聊天修复真实问题。重型原版与目标客户端错峰运行，避免与用户正在进行的服务端测试争用资源。

客户端覆盖真实注册与资源解码、创意页/物品/装备/方块、菜单/按钮/文字/提示、声音、粒子/透明/发光层、动画控制器/手持姿态、维度天空及需实际世界才能触发的表现。Render Preview分别在两版本采集对应实体/物品/装备/粒子目录和所有有效动画clip，记录具体条目、状态、tick、镜头、背景、尺寸与PNG/JSON，不能只打开界面或按目录数量宣称全部通过。工具不能覆盖的战斗特效、多个实体组合、世界天空与交互再由实际目标客户端补测；未覆盖项明确列出。

结果另建主线独占的客户端验收报告，区分通过、修复后复测、待验与原本无入口/原资源缺陷；不改用户独立服务端计划和early-tests目录。对照截图必须真实来自对应原件/目标客户端，不能用源码或资产相同替代实际渲染一致结论。用户授权合理修改在REFORGED_CHANGES中作为预期差异核对。

## Render Preview只读准备证据

quick_explorer `/root/coverage101_inventory` 只读确认两个工程：

- `E:/模组/发cf的模组/Render Preview/1.20.1-forge-render-preview`：Minecraft1.20.1、Forge47.4.23、Java17，用原JAR。
- `E:/模组/发cf的模组/Render Preview/1.21.1-neoforge-render-preview`：Minecraft1.21.1、NeoForge21.1.251、Java21，用最终迁移JAR；满足目标NeoForge最低范围。

两README提供`runClient -PpreviewOpen -PpreviewValidation -PpreviewHpJar=<绝对JAR路径>`。此命令仅是工具入口，不足以完成Thermal Shock全目录验收：默认选择hp_end_expansion（如加载）或minecraft，并不自动选本模组。应从真实`latest.json`取得HTTP地址，读取`/catalogue`获取key，再按`/open`、`/control`、`/capture`真实协议选择正确命名空间、时间和摄像机，保存截图及同名JSON。工具默认监听127.0.0.1:18761，端口占用时顺延最多19个，不能写死假定端口；输出在D:/AI临时文件/RenderPreview下。

依据为两个README/build.gradle/gradle.properties、NeoForge分支的`src/main/java/org/hp/renderpreview/AiControl.java`、`PreviewScreen.java`、`PreviewCatalogue.java`和`VALIDATION.md`。现有工具验证和HP专用适配器不是Thermal Shock验收结果；通用目录不执行服务端战斗AI或完整技能，未发现本模组专用适配器。

## 最终启动前需解决的依赖事实

只读快照显示Preview两个分支固定GeckoLib4.8.4，目标真实TOML要求[4.9.2,)，NeoForge分支未提供可覆盖版本的Gradle属性。`hpLocalMaven`只改变仓库，不能当版本覆盖参数。最终测试前需配置真正兼容的GeckoLib运行依赖，不编造命令，也不为省测试删低版本约束。

目标还要求aaa_particles[2.3.3,)；Preview的previewHpJar仅接一份模组JAR，不自动提供AAA。所查目标/Preview/迁移临时目录与Gradle缓存未发现兼容AAA成品JAR，两Preview run/mods当前为空，目标build/libs当前尚无最终成品。此为有限搜索快照，不声称整机没有依赖；最终启动前定位真实兼容运行库及原件必需组合，再留版本/SHA。原件把AAA/Gecko声明为可选不证明省略AAA后完整效果仍可比较，原件测试同样应满足真实效果依赖。

当前仅准备文档，没有更改Preview工程、下载依赖、运行构建或启动游戏；以上准备不算客户端测试已通过，也不影响当前迁移继续推进。
