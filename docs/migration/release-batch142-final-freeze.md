# 第142批最终整合产物再冻结

第141批交付清单基于第139批工作树中的第135批产物，不能直接作为当前主线的最终成品证明。本批在合入第138、139、140和141内容后的主线重新执行了 `compileJava processResources verifyReleaseJar`。

最终构建成功，产物为 [thermal_shock_reforged-1.21.1-neoforge-0.0.1.jar](../../build/libs/thermal_shock_reforged-1.21.1-neoforge-0.0.1.jar)，SHA-256 为 `0b97c63000b1a88b16b19a96fe5b1f9461fe293538a4ac1756c585f45faaa717`。JAR 含 9476 个文件条目、1346 个 Java 21 类和 5428 个 JSON，9475 项主输出逐字节匹配；NeoForge-only 元数据、Forge 类引用排除、开发文件泄漏、JSON/UTF-8、Mixin 类和主输出完整性门禁均通过。

本批没有修改玩法代码或资源，也没有启动游戏。服务端、客户端、Render Preview、世界生成、网络声音和动画首帧仍属于最终运行验收范围，因此本批状态是“静态就绪，运行验收待执行”，不等于完整迁移完成。
