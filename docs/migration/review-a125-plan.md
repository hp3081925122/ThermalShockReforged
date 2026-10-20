# 第125批A：59项实体动画消费者只读复核

承接主线parallel-batch125.md；固定基线f83be73，Minecraft 1.21.1 / NeoForge 21.1.233 / Java21。原件仍为只读Thermal Shock 07.9 JAR，SHA256为3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436。

第124批A已交付并冻结，后续主线接手的ConciergeTrade.java、QoPage.java不属A生产。125A不修改任何生产代码；不build、不stage、不commit、不运行游戏。代理只使用既有三个Luna，临时脚本和全部取证放D:/AI临时文件/ThermalShockMigration/animation125。

## 分工

主线group1-assignment.json按原顺序切为20/20/19项，对应a125-g1-luna1/2/3-assignment.json，分片哈希及完整身份见a125-g1-split-manifest.json。三个Luna各自只读本片，禁止重复其他片、主线group2或公共库调查。A主代理负责证据格式、输入校验、集合完整性、具体候选复核和最终汇总，不重复Luna进行中的实体调查。

## 逐实体要求

追原实体movement、attack、procedure实际谓词、优先级、循环/一次性设置和返回分支；有效clip与原资源集合相交，原不存在的残留引用不补造。动态animationprocedure/setAnimation必须带真实写入者、接收实体约束、条件和具体clip，沿本地过程调用追到原消费者；不能按文件名、符号名或资源存在判定闭合。

目标需核真实controller、trigger、状态设置和实体特定配置。继承共享父类时记录实际super参数、配置覆写和依赖，不因当前类没有registerControllers误报缺实现；共享Animated父类、全局Factory及Gecko API公共语义由主线独占调查，代理仅提交精确问题，不能用猜测替公共结论。

每个身份分别记录已配对字段、确证差异和未闭合字段。静态未配对不能改名为运行待验；静态复核也不代表客户端或Render Preview已验收。输出保存原/目标文件、行号、真实条件与调用片段及输入SHA256。最终59项必须无漏、无重，具体差异交主线登记所有权后修复。
