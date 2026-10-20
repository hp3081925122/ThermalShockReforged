# 第122批A：结构消费者与菜单剩余语义

## 基线与边界

基线为`126114f9a99aaa14c8f001a39415835c703689a0`，源Thermal Shock 07.9 / Minecraft 1.20.1，目标Minecraft 1.21.1 / NeoForge 21.1.233 / Java21。原JAR只读，SHA256为`3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436`。继承第121批证据，但不将静态一致或构建成功等同完整运行语义。

临时脚本、解压及机械结果均在`D:/AI临时文件/ThermalShockMigration/`，UTF-8。不单独编译、启动游戏、stage或commit，不读写用户测试目录。

## 三路只读复审

1. 303组结构属性转换：继承第121批typed矩阵，逐组核原属性注册、值、修饰符UUID与运算、目标真实属性消费者及结构初始化调用链；区分原无效属性、合法改名、历史授权修复及未核项。优先闭合真实缺口，不重复主线实体层和效果消费者全量审计。
2. 67份未确认加载模板：解析内部palette、实体、BE、库存、嵌套实体与模板或池引用，并追原注册字段、工厂、procedure调用者、结构池和运行入口。文件名无命中只能作为检索证据，不能证明不可达；不补造入口。
3. 241菜单身份剩余行为证据：建立逐身份剩余矩阵，继承已核范围，选择真实有交互且尚缺语义证据的有界子集，对照入口、服务端事务、槽位、文案、音效及画面资源。不得将共享实现或枚举覆盖视为每个菜单已经完整验证。

三名Luna/quick_explorer仅做确定性只读取证与比对，分别负责一组，不写生产与仓库文档，不构建或游戏。主代理核候选和消费者后修确证问题；未知项保留真实剩余矩阵。

## 所有权

A独占`docs/migration/*a122-*`。生产候选限结构NBT和菜单专属实现，确定缺陷后先在本计划登记精确文件及原因，再修改。实体层、效果消费者、工具标签、cobalt、共享注册、根入口、语言与汉化由主线所有，只提交证据和精确增量，不共改。尤其不改`zh_cn.json`。

主线另独占硬编码英文提取及`en_us.json`：`content/weapons/{SpearItem,FirearmItem,ZapGauntletItem,BlindingLanternItem}`、`world/KeystoneChamberGeneration`、`content/atlas/AtlasTravel`、`content/decor/nativeblocks/TrainTerminalBlock`、`content/decor/geo/{AltarBlock,CryonicCacheBlock,EnchantedCacheBlock,KeystonePuzzleBlock,PhobotiteOutcropBlock,WorldTourRedemption,SuspiciousCavebloomerBlockEntity}`、`content/doors/SpecialDoorBlock`、`entity/{cavebloomer/AngryCavebloomerEntity,icesquad/DeploymentTaskEntity,sanctum/PylonActivationEntity}`。A不共改；只读证据若遇主线新增翻译键，记录输入快照变化，不将等价语言键替换误报为玩法缺失。43套眼睛/发光映射已由主线复审，本批不重复。

主线随后追加独占`content/camera/CameraCatalog.java`与`content/armor/GeoSetArmorItem.java`的原文本提取，英文键增至3486并暂冻结新增。菜单发现的硬编码文本须分别标实际绘制、仅朗读和内部标题；仅给已有对应键或原文提取增量，不共改语言文件，不把词条补齐等同所有玩家界面完整汉化。

## 收尾门槛

保留原效果和可达业务；不以删除资源、空实现、捏造入口或静默减少内容闭合。对实际改动做最相关轻量验证，完成后冻结`src/main`，交付逐文件SHA256、输入快照、有界结论及真实剩余矩阵，由主线统一构建。DFU、组件Codec、多人事务与真实声画未执行时明确记为未验证。

## 已确认生产所有权与修改

A独占本批`src/main/java/com/hp/thermalshockreforged/entity/whisper/WhisperMenu.java`的快捷移动修复，不涉及`WhisperEntity`及实体层复审。八个原交易Menu的背包至热栏范围均为27..36且reverse=true，原class字节码一致；当前目标同范围误为false。目标原生`moveItemStackTo`也以该布尔值决定由末槽向前遍历，空热栏首次落槽因而由原第9格变第1格，既有文档没有授权改变。仅恢复这一方向并增加成功快捷移动英文debug；费用、奖励、按钮、槽位坐标与热栏回背包方向不变。
