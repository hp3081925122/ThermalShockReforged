# 三路 Luna 服务端功能测试

2026-10-20 用户明确要求多开 Luna 同步测试，主聊天只修复 Bug。此前主聊天编写测试、Luna仅运行的分工由本次要求替代。三个代理各自编写、运行、留档；主聊天负责问题修复、分派与汇总结果，不在代理构建或运行期间修改其输入。

固定原始测试基线为提交 `00bbd5801c3d373d0d239637de9dd69f2f5974d5`。原始副本 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\server` 只读；生产工作区仍有其他聊天改动，禁止将当前 HEAD 直接混入旧快照。修复回归单独记录补丁源码与哈希，保留原失败历史。

| 代理 | 本组工作量 | 独占 D 盘工程 | 独占测试文档目录 | 新问题编号范围 |
|---|---|---|---|---|
| luna_bastion_tests | 双 Boss 约22项、SB-002定向回归，之后Grunt链 | parallel-20261020/bastion | parallel-luna-bastion | SB-040至059 |
| luna_forecast_tests | Forecast/ZapWarning约15至20项完整状态链 | parallel-20261020/forecast | parallel-luna-forecast | SB-060至079 |
| luna_early_content_tests | Icicles/Glitterdrop与Shale Pot约20至30项 | parallel-20261020/early-content | parallel-luna-early-content | SB-080至099 |

D盘相对路径均以 `D:\AI临时文件\ThermalShockMigration\early-tests` 为根。文档相对目录均以本文件所在目录为根。代理不得共写总矩阵、总状态或问题索引，避免覆盖彼此结果。每个确认或疑似问题一项一文档，夹具误报保留关闭依据。

只启动服务端。独立源码、Gradle工作区和测试世界防止互相串场；每路Gradle heap1GiB、游戏heap2GiB、max-workers=1。旧客户端退出后已核可用内存15.52GiB，允许三路并行；后续若余量不足则错峰重型进程，不终止他人的进程。编译或资源复制期间冻结本路输入。

交付要求：每个用例记录唯一case_id、批次、真实入口与断言、未覆盖边界、实际结果和对应问题；日志、源文件、模板均记录SHA256；核真实GameTest执行数和全部维度关闭，不能仅凭Gradle退出码判通过。首轮失败源与日志不可覆盖，定向回归不增加独立用例计数。首组结果及时交回，不等后续链全部完成才汇报。

开工前原始累计为256项独立用例、248通过、8项旧快照失败。三路尚在进行，不能预计入新通过数量，也不能把每批有过一项测试当成整批完成。
