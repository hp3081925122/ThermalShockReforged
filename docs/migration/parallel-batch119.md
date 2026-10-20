# 第119批：Cavebloomer来源闭合与最终剩余依赖审计

基线提交：`cf9fa76`。第118批已统一编译并完成实体时序与渲染接线；本批先核当前真实未闭项，再补来源、进度、世界生成和资源闭合，不把旧清单中的陈旧 `not_migrated` 当作缺口。

迁移A承担约三分之二：独占核对并补齐 Cavebloomer 相关进度父链、召唤方块与 `cavebloomer_field` 世界生成链、世界进度/Keystone/Loqendia 真实调用关系，以及由这些调用直接暴露的资源或数据缺口。A只修改约定的 `docs/migration/cavebloomer-a119-*` 与 Cavebloomer 世界/进度资源；共享Java入口、总文档、技能和测试文件由主线负责。

主线承担余下：对原JAR的物品/实体/方块/配方/战利品/标签/语言/模型渲染注册做最终差集审计，修复已证实的闭合缺口，复核伤害递归、召唤上限、无限任务和性能热点；统一接线、编译和本批提交。不得改 `docs/testing/*`，不启动游戏。

两路均须先用原JAR源码/字节码和目标源码确认起因；保留全部功能、动画、粒子、声音、模型和正常获取入口。新发现的MCR重做坑写入 `REFORGED_CHANGES.md` 和 mcreator-rework verified-cases，客户端与Render Preview继续留到全量迁移收口后。
