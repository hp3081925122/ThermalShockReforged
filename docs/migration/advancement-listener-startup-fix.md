# 进度事件监听器启动修复

日期：2026-10-19。环境：Minecraft 1.21.1、NeoForge 21.1.233、Java 21。

## 原因与依据

客户端崩溃报告 `runs/client/crash-reports/crash-2026-10-19_16.42.04-fml.txt` 显示，模组构造期间 `ShalespringGeneration.register` 将监听器注册到抽象类 `AdvancementEvent`，被事件总线拒绝。`PalaceGeneration` 存在相同用法。

当前依赖源码 `neoforge-21.1.233-sources.jar` 中的 `AdvancementEvent.java` 明确禁止直接使用父事件，并定义 `AdvancementEarnEvent` 为逻辑服务端上完成进度全部要求后触发的具体事件。另一子事件 `AdvancementProgressEvent` 也会在授予或撤销单个条件时触发，不适合这两项建筑到达进度。

## 修改

两处监听器参数均改为 `AdvancementEvent.AdvancementEarnEvent`，注册成功后输出英文 DEBUG 日志。建筑模板、原点、游戏规则、实体放置、持久化防重复与失败恢复逻辑均保留。

## 验证范围

`compileJava processResources` 已通过（BUILD SUCCESSFUL，22 秒）。运行时应确认两处注册成功日志、模组构造完成以及不再出现抽象进度事件注册异常。建筑放置属于另外的游戏内功能验收，不能由编译结果代替。
