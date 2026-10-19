# 第103批预分工：剩余四功能方块

第102批两路生产、统一编译和固定输入验证已完成，并提交de91e13d13ff288fec1af26942665f899fb4bf63。现在解除第103批生产冻结，双方按下列归属继续；本批基线即该提交。

按用户最新要求，将较多生产工作分给迁移A，预计约六至七成；主线保留余下代码、共享集成和统一编译。迁移B停用，测试聊天由用户独立负责。Luna仅确定性只读清单与机械比对，不写生产代码。

迁移A负责brimstone_cake、lunar_panel、hologram_monitor三项完整功能、模型/动画/声音/全部原文本和真实获取链，独占content/decor/geo的共享扩展及必要client/decor/geo。主线负责icebreaker完整功能，独占新增content/icebreaker及对应独有数据，不同时修改Geo包。根入口、Noop渲染接线、语言、标签、总文档和REFORGED_CHANGES由主线唯一编辑。

四项均有原Geo和九格库存，不能套普通静态方块丢弃自动化/原动画。为避免复制既有体系，Icebreaker拟继承现有公开GeoDecorBlock.Directional/GeoDecorBlockEntity；A负责Geo枚举与工厂集中接线，主线交Icebreaker的精确元数据/工厂增量，在两路收尾后由Geo包所有者一次应用。若源码证据显示不能复用，先调整合同，不能并发改同文件。Root的Icebreaker任务Entity及注册独立放新增包，不扩大现有净化器或Geo核心为大杂烩。

已核原源码：Icebreaker首次放置发呼吸声，20 SERVER END进行半径9整球ICE→WATER、70%原levelEvent2001和10颗烟雾/铁砧声，30 END移除锚点并10颗POOF。需保存完整合法尾阶段，避免旧任务删新块；不强加载邻块，失败插入回滚归属。BrimstoneCake原即时及多轮食物/饱和/抗火/日期进度和切片动画须完整逐段核；LunarPanel原每tick发能、日夜/侧面能力分支须核字节码而非仅按名称推断；HologramMonitor原九软盘、十状态、全部录音文字与五种真实全息粒子须全保。

原件固定07.9 SHA256 3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436，目标1.21.1 NeoForge21.1.233/Java21/Gecko4.9.2。临时脚本仅D:/AI临时文件，UTF-8，Java无注释；原版采集标签与合理地图颜色，不绑定工具具体类。联动延期仅在独立文档，原生功能不延期。

双方完工交逐文件哈希、原行为/差异证据和共享增量后停笔。主线固定src/main输入后统一compileJava/processResources及构建资源核对，阶段提交后继续下一批；本线不联系或等待测试聊天。静态/编译不是游戏测试。
