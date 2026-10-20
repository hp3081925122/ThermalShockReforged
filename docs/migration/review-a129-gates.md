# 第129批A：完整迁移与无简化复审门禁

文档盘点基线：`da865812c8df21f2e054e67ef4f1a9367b3c43a2`。未读写生产、未构建、未开游戏、未新建代理或联系服务端线。

本批未确认新的生产缺陷，但现有证据不足以宣称完整代码迁移/无简化复审已全部收口。三类静态证据门禁与两项来源条件分别列出，不等于三个缺失功能；运行未测不计源码缺失。已纳入主线129动画收口及130九项失败的行尾原因解释，旧原始报告不改。

## 范围

语言独立；主线129的118有界动画集合静态剩0、10项运行检查未执行，本批只承接不重查。130九失败行尾解释已登记，18方法与loot注册不重复调查。127A原快照保持冻结；用户测试/Ribbonblade/.vscode独立。

以下为文档所证实的审查状态，不是本批重新检查生产源码。完整依据路径、SHA256、行锚及JSON指针见同名JSON。

## 仍未闭合的静态证据

### G01 67模板外部动态可达性及纳入范围

状态：静态证据待查，未确认生产缺漏。

67份原模板未纳入目标97份。122A已核8个procedure加载点、共享feature、池/结构JSON及164份NBT引用：64份未确认默认加载链，另3份有明确排除解释；内部递归依赖已盘点，不再写成未解析。

- 外部指令、附加数据包、用户结构工具、运行时动态模板路径尚无穷尽或范围裁定。默认自然入口无命中不能推出资源对外不可加载。
- 尚无逐模板纳入/保留来源证据/明确延期的最终处置，不能把67份全部当孤儿删除，也不能直接认定67项玩法缺失。
- 若确认入口，须按已有递归父字段继续核该模板实际依赖及目标加载语义；现有未闭合原注册ID不准按近似名替换。

建议：先固定允许纳入的外部入口范围，再复用67矩阵逐行挂入口与处置依据；不重复NBT全集盘点，不造自然入口。第三方依赖按独立延期契约处理，不能借延期删本模组可达资源。

依据：`templates-a122-evidence.json` /counts/reachability_classification；`templates-a122-evidence.json` /limits；`templates-a122-matrix.md`；`review-a122-review.md`；`DEFERRED_MOD_INTEGRATIONS.md`。

### G02 完整声画消费者的逐行为对账

状态：覆盖证明待查，未确认新的特效简化。

资产120报告覆盖原6103资源：没有资产缺失，52粒子JSON仅命名空间变化。43显式实体eyes/glow绑定已核；26原未决实体透明路径中23一致、3已恢复。以上均不是所有声画执行消费者的证明。

- 粒子：未在本次所读交接中取得52粒子定义对应全部实际provider及调用源的完备差集；需复用各批已有实现证据，核注册/客户端提供器、触发端与次数、位置速度、寿命/帧、透明/亮度/物理及AAA effect所引用纹理。不能把52 JSON误作52个已确认缺陷或独立技能。
- 声音：尚缺覆盖全部真实调用源的参数与作用端对账，特别是client本地与server广播、listener、坐标/声道、次数、音量/音高及延迟阶段。utility-main-sounds111只核自己的两个声组，资产sounds.json/OGG相等不能替代调用消费者。已核菜单双开和Bastion/Cavebloomer时序不重复调查。
- 非实体声画：118种实体动画有界结论不能替代物品/盔甲/Geo方块/BE控制器、手持姿态及其发光/透明消费的全范围核销。当前43项是显式实体层范围，不能据此重开已闭合43项，也不能推广为所有非实体层全部已审。
- 117矩阵保留的“全技能/父链/资源”等旧pending需按实际行为对账到后续专题证据，不能用注册/资产/118实体数量整体清零；本批没有新的源码调查，未得证据的覆盖仍明确待查。

建议：先用既有115/116/117与各模块报告建立源调用到目标消费者的差集，仅调查没有可继承证据的行为；优先粒子provider/AAA及非实体层，不重做23动画实体。

依据：`assets-main120-review.json` /limits；`original-inventory.json` /resource_categories；`render-layers-main122-review.json` /scope；`render-layers-main122-review.json` /counts；`render-main124-reviewed.json` /scope；`entity-callbacks-a-batch117-matrix.json` /limitations；`entity-callbacks-a-batch117-render.json` /limitations；`utility-main-sounds111.json` /scope；`client-final-acceptance-plan.md`。

### G03 掉落引用完整注册链的静态补证

状态：引用覆盖待查，不是已确认掉落表缺失。

610原表均有目标、池/entry与次数/权重已审，Seaglass谓词及darksteel set_count补偿已有具体修复；最终内容remainingDifferences为空。

- loot-a120-validation明确referenceClosureComplete=false：659唯一掉落ID中626模组ID，614仅有Java字面量命中，另12已追到GemlockKind/ArmorContent枚举循环；614的命中不能当完整注册/helper/总线消费者证明。
- 还需把实际item entry、方块状态条件与已核真实注册关联成静态消费链。原生Codec实际解码属于后续运行验收，不能因尚未执行而另判资源漏迁。

建议：复用loot-a120-matrix与现有注册报告，对未具完整链的ID按真实字段/helper/枚举和总线登记去重补证，列真缺项或排除项；不重比610表内容，不用字面量数代替行为。 主线已安排130独立注册盘点，本路不重复；新报告交付前仍保留当前false快照。

依据：`loot-a120-validation.json` /referenceClosureComplete；`loot-a120-validation.json` /remainingDifferences；`loot-a120-review.md`。

## 已过时待办的消项

- **C01 241菜单仍余190/124/122/15/7身份未闭合**：该串历史待办已被124修复和126双开链覆盖：Concierge七交易资格恢复41格Inventory、Stratus单帧画像恢复；Qo先开基础页再开覆盖页，保前页声音类别、递增和一次性奖励。241/241静态闭合。此为页面行为证据，不是只凭计数；实际网络/画像/声音留运行验收。

- **C02 303结构属性全部未审、113组配置后有效值未闭合**：122A已核303组typed映射、supplier/加载/finalize父链；190组没有识别到配置后写边界，113组由123补齐107个同默认movement_speed及Health/sleepLock/随机bonus生命周期。Herald恢复finalize定位；Sunbeam攻击关联与两项适中击退有明确要求/差异依据。不再保留113项静态缺口，也不把未跑DFU/非默认配置实测改写成源码缺失。

- **C03 67模板内部递归实体/库存依赖尚未盘点**：122A已递归805载荷、18顶层实体、5非空栈和6刷怪笼，并按真实注册helper/枚举分类；不再重复盘内部清单。仍未闭合的是G01外部可达与处置，不是同一项。

- **C04 45非冷却查询/移除消费者、Sickle/Liteprism次序未闭合**：124逐45行给控制流和历史差异依据。Sickle五种先add再读实际等级、移除被拒仍保原声伤；Liteprism恢复伤害/粒子→remove→声音并保同步防重入。不再引用123中间候选作为当前缺漏。

- **C05 23效果字段差集、HiVolt无条件reach、原6000抗毒时长等候选**：内部状态替代不能按未注册补假效果；抗毒6000为秒、目标120000tick正确；HiVolt实际调用Ruby过程；-20/-40已分通道；Willograde/Mangler可见药效抽样、权重与ambient已有123证据。只清这些明确候选，不外推所有声画已审。

- **C06 43发光层与26透明路径仍全部缺证/三载体完全不画**：43显式实体eyes/glow逐项绑定已核；26项23路径一致，Poison/Shock/Zap恢复原生translucent/outline三分支。isBodyVisible=false不等于完全不画；该旧误诊撤回。实际UV/混合/轮廓列运行验收，完整消费者范围另见G02。

- **C07 Civilian创造循环差异未批准/Net与Bloom载体未接线/Guardian粒子坐标未解释**：Civilian7→0已正式作为合理差异登记，生存八页不变；Net模型层、弹体绑定及Bloom Noop已接；Guardian位置是51批记录的修复。旧115/117未接线和待登记不再作为当前缺口。

- **C08 117粗粒度pending均按当前代码缺漏保留**：孢子跨实体朝向副作用、裸@p选择资格/FEET与完整速度已有明确来源；磁力炸弹提交幂等与尾阶段、Core连锁及Disk同步/3/6END已有专项实现证据；Liteprism次序以后续124为准。资源/注册已由120覆盖的字段不重开，未得具体继承证据的消费者留G02，不把86行pending直接算86个缺陷。

- **C09 127A的Skeletitan/Mangler/Springslime/Qo/Cavebloomer及主手资格旧候选仍全未决**：仅承接主线128修复和receipt结论，不重读23实体、不改原冻结报告。Specific Raw差值、Mangler死亡hurt视觉、Springslime有限procedure退出、Qo客户端随机、Cave SERVER END及Willograde主手已另留修后证据；不借此宣布主线剩3动画完成。 此后129的剩3收口以C12为当前结论。

- **C10 早期工具标签29项及muck_slab名称差集即为漏迁**：原未注册陈旧ID不造占位，实际pure_muck_slab及cobalt铁级已有闭合记录；实际挖掘与地图结果留运行验收。

- **C11 118A九项失败原因未解释**：已由主线130新证据解释：原tree-sitter直接字节抽取以90665ea Git LF对118冻结CRLF，九个多行方法原比较失败；仅CRLF转LF后18完整方法字节一致，未忽略语句/标识符/数值/顺序。旧511/502/9报告保留，原因在新报告消项；本批仅承接，不重复18方法调查，也不把这项等同游戏通过。

- **C12 主线129前剩3动画仍作静态待办**：承接129：Willograde恢复原双4tick控制器并取消整段重载重播，业务持久化仍保留；两出生链的有限静态条件/追踪路由配对，网络到可见首帧另列运行检查。118有界集合静态剩0，10项运行未执行；不等于全模组无简化复审完结。

## 来源条件与结构边界

- **P01 hanging_bulb的purebiomes源悬空范围**：原modifier指向未定义的thermal_shock:purebiomes；真实feature与城堡灯泡来源保留。没有范围依据前不造空tag、不猜群系、不恢复悬空注入。是否要修复此原缺陷需另获真实依据或明确裁定，不把它算成当前已确认漏迁。

- **P02 已迁模板中7份属性审查未建立入口**：这些模板已在目标97份，122属性矩阵对应entrypoint_modes为空，不能据此断言从未有外部加载；也不应补造自然生成。与G01做同一入口范围决策，但不混入67未迁模板计数，不重开303属性转换。

对应模板：`blizzardoutpostrubicon`、`cathedral_base`、`junkyard`、`monolith`、`vilebath_castle`、`whisperden`、`whisperdenalt`。

## 仅属最终运行验收

- **R01 结构/数据真实运行**：实际DFU及ItemStack组件Codec、结构放置/出生finalize、非默认配置、随机bonus/受伤重载不回血、流体邻居与群系叠加。静态源码已有的转换/生命周期不因未运行退回未迁。

- **R02 菜单真实客户端往返**：241身份对应已审行为的真实按钮/槽位/多人事务、双开前页声、画像帧与支付回退；不再以旧7项静态缺口派工。

- **R03 声画及战斗表现**：半透明/outline、43显式层UV混合、粒子数量/范围/运动/时序和音效实际监听；保与技能节点相同的镜头/tick对照，不能只开Preview或比资产。

- **R04 测试环境准备**：Preview仍固定Gecko4.8.4而目标要求4.9.2，需正确覆盖并提供AAA2.3.3；目标兼容JAR已定位。原Forge1.20.1 AAA在限定缓存未找到，不声称整机缺库。此为最终验收环境门禁，不是本模组源码简化。

- **R05 loot真实解码及抽样**：实际Codec/数据重载、条件与精准采集、随机附魔。on_random_loot遵循新版及数据包集合的限制已公开，不无据锁死旧集合。

这些未运行项不能增加“源码缺失”数量。本批不启动任何测试，服务端继续独立；客户端/Render Preview须等待完整静态门禁收口及主线安排。

## 建议下一项

1. A后续独占review-a130-templates.*，对G01做确定性原模板外部加载入口调查，超过机械可达链范围先报告，不造新入口。
2. G02复用现有矩阵做消费者差集，未覆盖行为才新查。
3. G03由主线130独立盘点承接，新证据交付前不代算闭合；不重复18方法、loot注册或语言工作。

## 依据SHA256

下表为本批读取的迁移文档原始字节，不是历史生产文件重新验证。全部是docs/migration内文件；绝对路径及每项消费锚点保存在JSON。

|依据文件|SHA256|
|---|---|
|parallel-batch129.md|`9e6090189e5fdabbf76fd33f0ad18560acc84d6a53055cf52a281b00e8bf8a1a`|
|main129-static-validation.json|`25d4ceadcaa94ae950f00d4147be8d9ceffb56aafcb011ef1fbed8183122cc68`|
|animation-main129-review.json|`90fdf2ebe7fac0704f48d19cf1c2131bbc4017f0e584476eee198962d70eb369`|
|animation-main129-runtime-cases.json|`21401a4b790565412ee4f97335e116543184056e927c9be525fce2f528d6ec7a`|
|audit-main130-ice-methods.json|`85a5b0ad96bedb154eeead0bbc2f971a8bc7e3d6dc0b5064f4a45a67430e454d`|
|parallel-batch128.md|`dfca45cbff1f89a7c95c19b7c3d30ddc008d44de4b9b981be0e4026f77370874`|
|PRIMARY_HANDOFF_2026-10-20.md|`975942d5bea549095a1b502239fd923f8d38269b1f9677d4c0461257811d5915`|
|main128-static-validation.json|`2fe7ac674d974198a7991228dae3cbcef14ebe061de30f7cd3cbfe950198e3ea`|
|animation-main128-remaining.json|`689ba4bf981b35b7b8aa8acf821f26eb8b439bac1b49881cadef8f0400853d45`|
|review-main128-a127-receipt.json|`ed254bfabd318ee2b8859e6ad12f0dc3083919b8735eeb3a073a9dec3cefb229`|
|main126-static-validation.json|`dd3a0d5f056cbb5bd39f99221a59bfe803ed53109dbf6d55613f777b7cf6ead4`|
|menu-main126-qo-validation.json|`bd1b49870d9a131b26398d7b641f87709e1c62a37acccd0a7c11d25d275ddf47`|
|menu-main124-followup-validation.json|`d31920b7bd9fa9db9ed9396d3e24493c8b54df9db8712032b758830f07b2cdb3`|
|review-a124-review.md|`86c7b485ba759f844d0fbe4c1805c1038dc2b022b8b21a6ca9118be7c68f5d7e`|
|review-a122-review.md|`440dd5ebcdd5a5583d2a3b4fe88dfc554e23128a9715617ecca23af8e3a816b4`|
|review-a122-remaining.json|`03829ec99384c6b999fd2d6b93b55177bd13d45d199b611fe28a43635df56178`|
|structure-a121-review.md|`eb1a84e73ed264241b4343b6e1bcc2e849e7b7510a1498f3ee32e4ac77392330`|
|structure-a121-matrix.json|`de5df274b77c150579c47ed943a5dae436c709b00c79fe88589b076aff6de4a8`|
|attributes-a122-matrix.json|`c4917dc429f86be11017a1102b3468e63a47c41ee8263b19ecdf7d7704362ae1`|
|attributes-a122-summary.json|`657cddbd356dcb3c1dd9616ea03c681c457e9d863d3a4af08c13bddbc17ef708`|
|attributes-main123-review.md|`e65defbb12c34b25837a9081d4dd62c4d1e6f60642a5dc0b740d3687d84d8332`|
|attributes-main123-before-fix.json|`8340d0bf13c3c490c4830f4a4fe1345e626fc77ec783e1a2d67eceb149dd4514`|
|main123-static-validation.json|`39fdd950cba7fa8169763b0746ca53e80cce45a68a3a83612967dc76f1cf9122`|
|templates-a122-matrix.md|`be5d796c0f215215e900d2a6656257bd7e5949172a1dcbbe902c1f13ba99b799`|
|templates-a122-evidence.json|`f1ad7a7a96ad637e477c0a5e7818c98e614e8674657b89695975e88224407914`|
|loot-a120-worldgen-review.md|`c1e5c790f1039d32ad7b7accd6bbb0e0e8758ba974d0f11a95e6233719acc4de`|
|loot-a120-review.md|`af4ec9596e5ecb801965c12abc37a712c34a432f9811bb0eacb019eb0da23d3f`|
|loot-a120-validation.json|`723ece83667507fb93447f154be06e07ef2d5b313535da4c27e1f75314fc8586`|
|assets-main120-review.json|`8e70f400a5a84b8aa848353d2a281ad416467e5f6a89b430aae6328708eb70ab`|
|original-inventory.json|`581a4d6e8269e29dfc6bd89a5fe52501516a528419943a9eee2e7c2629f45f1c`|
|entities-main120-bindings.json|`8ddd39020c89ad6ce19e17fb4266d4d662c062251c61522a0ad067279ce3b359`|
|render-layers-main122-review.json|`318b4db87e89a35efecc9783e560873aa649526935db8118086220f1cc40a8b2`|
|render-main124-review.md|`9bb355a44a8dcf6723507914bab33e685ecdc2966067e2e32042970791c1f3c3`|
|render-main124-reviewed.json|`cb154d84f13db4ba8bd27a7b806f0987d9d640ad8fd404708c8915ce4d8911e9`|
|effects-main122-review.md|`d446be9ed5f2813f80ca1c15385aaa6862b54bcd8c1ef4010f8fa5ff052160bb`|
|effects-main123-review.md|`9800611a0c7d048e7f74e27b458f40cfbe53c3b456aa82b7e68084efffd08cf1`|
|effects-main124-reviewed.json|`bdb77207ade300d2346648bb5b6a83d02c1124b1a3cb8ec214d602572bbe2a79`|
|sickle-main124-review.md|`746203dd6d73f69eeb674b539f7194788c6ffd5b7451abfa161eef4e3506e510`|
|combat-main124-static-validation.json|`50b40cf390dc5a21444d7c538203d925960ef8dedd4cc2f9c0d1b73be7898fb5`|
|status-main120-reviewed.json|`82dc164f64b3b02fcfe6462e652998c2a49ab6d6be08676f20e3ed32159b1f9e`|
|entity-callbacks-a-batch117.md|`ed55b1282fb7e1453d0b0012d859a87d546fe6d1fc1d511c049d9fc8f5927228`|
|entity-callbacks-a-batch117-matrix.json|`15ec54a662926c512a71ff8ede20eaf5291f2267ff4c4f72dab21fa99d01886b`|
|entity-callbacks-a-batch117-render.json|`1a219eb0817d73e467d07ed4ad24cfba97becc3a33d5a7485a73bc6576f4b60e`|
|remaining-entity-dependencies-a118.md|`6b9d4f5c7de9a93773a37c7016eadf745a62212751abc8e188e997e7f8a96298`|
|remaining-entity-dependencies-a118-validation.json|`710d4a6674796340c551dc66c9069a3752ab5b46b8ba936c7e0f75e2c6c650c8`|
|remaining-entity-dependencies-main118.md|`bdd8d1f64ad2cf28feb7772f340c6edb7a08756ba16a77412274ba4ceab2da59`|
|main118-static-validation.json|`f77879ad7225b2ffbf6728b19d1df3839613e90806567a3aae50b5768dc2fd7a`|
|block-callbacks-a-batch115.md|`cdd226acd93675a4c02d795c6c742fd883828629a51981997d61085d76be2739`|
|main115-static-validation.json|`624d977761a4809b5e226360f2785f962fa024d1bd214701e33f0f65a665a774`|
|item-callbacks-main-batch116.md|`34b84614e252539d668047216564f56e409767847d34cd8e42639c646cf2caaa`|
|REFORGED_CHANGES.md|`1bdda03edb0a189d8c50f208c59d51743758ebb8507331bdb0a7341d863d310d`|
|spore-next-audit.md|`fb6e67ba8c0a028c601817634fcf6729241e7e3344dcec773ff603d1e2b1b521`|
|core-eject-disk-chain-batch.md|`9059111a0323362151cdcd93582400081a70a673bd12f2fa99cfe05faba0b910`|
|magnetic-bombs-ghostglass-batch.md|`c19312b45d58ccb36b6bbfbbcc4af9a4744202151854a43aaf2c5c94826fef4f`|
|utility-main-sounds111.json|`adc63ab000eb34892ed0f6e279b86ad972710122151aadcc3af22b4cf5905b1c`|
|client-final-acceptance-plan.md|`f5d5ccf4a7a7915c32a6c6963aa43a7e1b074b3f14e05714a3a7f6db39a51bdd`|
|client-dependency-preparation112.json|`06ec7ab683e21ab74b55dbd3d62897889969926f193ad69cd96ff0067c5951c3`|
|DEFERRED_MOD_INTEGRATIONS.md|`affc9e184cf17150396158521ad4b6df24c3efe5658b844336f7d5ea94a443da`|
|tool-tags-main122-closure.json|`4da6927dce7cc01d9a99e08bde25471c58c079149186522df3fe9a73a8f5e9a1`|
