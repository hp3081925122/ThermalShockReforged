# Stratus Reactor Core 迁移

## 原件与范围

来源 thermal_shock-07.9-original.jar 及对应只读反编译源码。核心原类 StratusReactorCoreBlock，交互 StratusReactorCoreOnBlockRightClickedProcedure，采集条件 StratusReactorCoreAdditionalHarvestConditionProcedure。

保留 blockstate 0/1、亮度15/0、四条说明、完整多方块条件、一次激活进度及声音、25轮每60 SERVER END召唤、1900 END最终 Squall 生成和核心消耗，保原所有粒子与分支概率。持久化载体只保存值，有限且幂等，不捕获方块/实体闭包。

## 已确认问题与待核对

原同层黑霜岩角检查重复 (-1,+1)，漏掉 (-1,-1)；修为完整四角，记录差异。需核激活后采集实际掉落与破坏语义、原队列边界、核心中途被替换时最终阶段、真实制造/结构来源和粒子资源，不能推定或删减。

## 验收

静态核对多方块坐标、25轮采样次数和概率、0/60…1440及1900 END时序、资源原件哈希、任务持久化与重入、所有引用和真实来源。统一编译待本批两路停笔；最终游戏测试待整个模组代码完整。

## 已实现与来源结论

原下层9格（中心与十字钢茎块、四角镶边警示块）、同层4暗霜岩角、上层5暗霜岩十字完整校验，共18格，修重复角。保核心完整两状态/模型、15/0亮度、强度2/30、镐采集与四说明。激活首轮同步，随后24轮每60 END，63% Galvanite、27% Frostilyte Cryomancer、10% Lens，原坐标取样[-7,8)、高度-1、MOB_SUMMONED及零速度。每轮原单冲击波、末影之眼声音，初始10雾云/凋灵声，1900 END一次Squall尝试、原核心消耗和物品破裂声均保留。AAA载体寿命7、每tick发射、全亮/物理/原速度均保留，雾云复用已验证完整实现。

有界实体任务保存维度、锚点、已完成轮、END计时、提交/终结锁；不捕获旧方块实体或强制加载区块。邻块未加载时暂停下一轮及后续时间线，重载不重放已提交轮。每次激活最多25次普通召唤及1次Boss尝试，不新增正常生物的全局数量上限。任务实际加入失败回滚本次状态，避免吞激活资格；先消费阶段再调用spawn，取消生成不无限重试。原核心被拆后合法余轮仍执行，最终只删除UUID归属匹配的核心，不误删后来放置的新方块。

核心新增只保存归属UUID的无tick方块实体；按目标原生PistonBaseBlock规则，带BE的核心不能被活塞推动（包括未激活态），避免移动核心与固定锚点事件脱离。没有库存、额外菜单或逐tick方块扫描。

原 canHarvestBlock 只禁止激活核心的玩家采集掉落，未禁止真实破坏，也未覆盖爆炸loot；原爆炸能回收为默认未激活核心而复用召唤。保正常未激活采集，给原完整loot加blockstate=0条件，堵激活后回收；不把强度改为不可破坏。MapColor.METAL用于普通地图颜色，Xaero真实验证留最终。

原真实生存来源为3材料原配方（4 ferrum_plating、4 cryonic_alloy、1 stratus_alloy）；目标均已真实注册。原激活进度保10经验、goal框/聊天提示及父squall_get，父进度由Squall分工闭合。创意清单原有条目通过真实BlockItem注册激活。

原英文说明写放入即可触发，与真实右键入口不符，改为Right click；保四条说明及结构分层信息，未增加额外操作。

Luna解析原全部164结构，任何palette均无核心；stratusreactorspire为21×49×21、21609块/4palette/0实体，只含air及三种暗霜岩。原全部JSON及class的模板名引用为0，完整typed模板已保留，不新增虚构自然生成。模板几何、palette状态和全部air不删减。来源盘点原快照在D盘reactor97-source-inventory.json，生产转换/资源哈希见reactor-resources97.json。


## 本批验证结果

统一编译首次通过1m6s，8240固定输入、7574构建资源字节及2展开模板核对通过；分工9生产文件哈希保持。反应堆Luna机械审计和Squall11组静态通过。总证据main97-static-validation.json；全模组及游戏/渲染验收仍未完成。

提交前git whitespace检查发现原英文语言文件混合换行，已只规范本次触碰的一行CRLF为LF；JSON值及全部Java输入保持不变。仅追加processResources离线47秒刷新资源，最终重新核对固定输入与构建资源，未重复Java编译。
