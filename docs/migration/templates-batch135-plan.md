# 第135批：既有97份结构模板格式消费者复审

日期：2026-10-20。工作分支 `codex/batch135-template-format`，基线 `fa12248c7a70c983adeda6575de71860edf9545c`。

## 范围与原件

源为 Forge 1.20.1 Thermal Shock 07.9，原件位于 `D:/AI临时文件/ThermalShockMigration/thermal_shock-07.9-original.jar`，约定 SHA-256 为 `3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436`。目标为 Minecraft 1.21.1、NeoForge 21.1.233、官方映射、Java 21。

仅修改9496401已有97份结构模板及本批文档。按 `templates-main132-production.json` 排除第132批新增67份模板。Java、语言及其他批次资源不属于本批。所有脚本、日志与快照位于D盘临时目录；原E盘项目仅供原生源码只读参考。

## 调查与预期行为

逐一读取当前生产模板的完整typed NBT，盘点旗帜、实际非空物品栈、组件和嵌套库存，依据1.21.1原生读取器及Codec确认格式。`rud` 的早期映射和原生tag落入custom_data仅为待查候选，不能直接视为当前缺陷。复用attributes-a122及attributes-main123对303组属性、113配置和出生生命周期的闭合证据，不重做全体属性调查。

保留方块列表顺序、位置、state、palette属性与顺序、实体库存、UUID和原剧情。无原注册ID的实体及第三方载荷保留并记录延期，不猜测别名，不删除内容。仅修复当前载荷中确证的目标原生消费者失效字段。

## 验证与交付

修复前后进行全typed允许差异核验，要求全部几何字节、实体非许可字段及第132批资源保持不变。固定src/main输入后执行Java 21离线 `compileJava processResources`，核资源复制结果与源直接字节一致，保留失败日志并本地提交。

本批不启动服务端、客户端或Render Preview；编译与静态格式验证不能替代最终完整代码闭合后的游戏验收。第135批提交后继续第138批最终客户端及Preview环境的只读准备，另建迁移前文档。

状态：本批调查、54处旗帜修复及验证已完成。全97份typed允许差异核验通过，第132批67份资源不变。Java21离线compileJava/processResources通过（7分8秒），8,947份固定src/main输入构建前后不变，8,127份直接资源字节一致。未启动游戏；完整迁移及最终运行验收仍未完成。
