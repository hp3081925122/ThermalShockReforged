# 第127批A：末23项动画实际消费者复核

基线f0320d5e7d2acadef5593132d44bab5a347b3cc8，Minecraft 1.21.1 / NeoForge 21.1.233 / Java21。原件Thermal Shock 07.9只读JAR的SHA256仍为3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436。原动画assignment保留其旧f83be73定位基线，本批另记当前源码快照，不冒称旧矩阵就是当前实现。

## 分工与边界

原三个Luna/quick_explorer各自只读8/8/7项，身份及分片哈希保存于D:/AI临时文件/ThermalShockMigration/animation127/a127-split-manifest.json。

- 第一片：sentrarius、shutterbug、siibol、skeletitan、skullscor、snobol、sola、sootle。
- 第二片：sootle_idle、sootling、spectre、spirit、springling、springslime、starrite、summoned_mangler。
- 第三片：tesla_reed、thermoid、torpedo、trilobite、veridian_jellercel、whisper、willograde。
- A主代理负责分片、证据质量、候选与历史差异复核、全集及输入哈希验证，不重复Luna进行中的实体调查。125A的19个剩余项由主线独占处理，本批不重新调查。

只写本批docs/migration/*a127*报告。任何生产Java/资源、用户Ribbonblade、tests和.vscode均不修改；不build、不运行游戏、不stage、不commit，不联系独立服务端线。临时文件全部UTF-8放D:/AI临时文件/ThermalShockMigration/animation127。完成固定证据交付后停笔等待。

## 取证标准

核原movement/attack/procedure真实条件、优先级、有效clip及其loop、controller过渡参数，以及全部实际外部setAnimation/animationprocedure写入的接收者约束和时机。扫描不排除其他实体目录；阴性结果也需保存搜索范围及输入证据。反编译关键控制流争议核原JAR字节码。

目标空路径先核实际注册/共享工厂/枚举配置，不当未实现；实体自身没有registerControllers时追真实继承配置。公共Animated父类、全局Factory及Gecko API语义只引用主线已有报告，不重复公共库调查；具体尚缺的公共参数明确提问，不自行猜测。

所有未配对静态项、确证差异、历史授权差异及运行未测分开。thenPlay沿clip自身DEFAULT循环，不能一概当单次；触发器白名单存在也不证明调用条件和时机已保留。每行保实际原目标代码、行号、输入SHA256；当前工作区漂移必须明确验证并记录，不静默换快照。最终23身份无漏无重，交接清单固定文件与输入集合哈希。
