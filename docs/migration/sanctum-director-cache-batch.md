# 第57批实施中：Sopel完整页链与高级Cache依赖

本批仍在实施，不能据本次WIP编译宣称奖励/进度/完整圣殿已迁完。前置真实契约见sanctum-director-cache-next-audit.md。

## 已写入的真实功能

DeskDirector方向Geo块/真实9槽BE/独立物品及透明渲染接已有完整Geo机制，保原桌子三格横向轮廓、−1/3600000、STONE/0光阻/透天光/非导体及FACING。右键真实打开Sopel 7页：Intro1→2→3→4→Choice1→Yes Epl5/No Epl5b。Enum的36条翻译键/坐标/颜色直接按原七Screen逐项提取并核期望数量7/5/5/4/1/7/7；不新增背包槽，原各Menu都是0槽。

完整普通页276×182、两背景/肖像/Next原图集与位置、六个普通页VOICE sopel_main_speak 1/1均保留，Choice只画原speechbox与一条白字、两原Yes/No图按钮，无语音/肖像。客户端会话Clock每tick+.125、>12归0，取int索引*128截0..1408；Choice暂停，翻页继承同桌子Clock，新会话自零。修共享WhisperSpriteIndex被所有玩家/两逻辑端叠加驱动的脆弱状态，未减少帧或加速合法单人原设计节拍；不宣称旧集成服共享静态轨迹一致。

翻页用原生菜单按钮与固定服务端锚点/当前实例/真实桌子/活体/距离/维度检查，无任意坐标自定义C2S；一次changedPage锁防同页重复执行，两尾页按钮关闭。Choice两分支都应授原beta_complete，Epl4仅开Choice；已在原Progress/Reject逐行确认。缺进度数据时日志告警并保Choice不吞流程，待下面完整依赖闭合，不通过删除奖励或换图标让它假通过。

CrystalSopelTrophy已真实方向Geo块/BlockItem/9槽BE：COPPER、1/10、light7、原全方碰撞/noOcclusion/非导体/0光阻/透天光、fallback自身、原sopel_relic模型/动画与rubicon_trophy贴图；原Amalgel名粒子procedure实为每animateTick一个END_ROD，位置xyz随机0..1/0..2/0..1、速度0,.1,0，接共用真正同效路径，不按命名删它。

PhobosLament/AzureGrey已真实1.21 jukebox song组件，不是普通占位Item。旧Forge Supplier RecordItem构造长度单位为tick：3900→195秒/comparator0/phobosdisc，5000→250秒/comparator9/azure_grey_disc；原RARE/stack1/desc/原声音资产均保。CarborundumSickle用真实HoeItem Sickle机制、900耐久/15速度/4.5 Tier伤害/附魔16/等级3原生工具门槛/Carborundum修复/原两tooltip，复用同SickleHits第三次10伤与20tick wound真实效果及无击退内部替代；原Ruby/Blood既有参数保持4/21未变。对应完整recipe/tags等尚在机械盘点，不能只看注册就称来源全完成。

SuperCryonicCache已真实GeoKind/9槽CacheBE/物品/透明渲染，完整原−1/3600000/light12/AMETHYST_CLUSTER/BASEDRUM/16×28×16轮廓/pushBLOCK/noLoot参数。参数化既有CryonicCacheBlock，仅普通CRYONIC保原trapped分支；Super只真实key解锁。原claim→确认真实移除→扣当前主手1key/原玻璃声/原CHEST loot，表缺先保块/钥匙；修MCR不看destroy成功也收费给奖励。完整Super原4池/38不同物品已原样导入，未删条目；本批后续又迁入四件真实护甲，当前只剩三件枪/矛仍缺实现，见下。

## 明确尚未闭合

beta_complete原hidden challenge/impossible/1000XP，奖表应给真实CrystalSopelTrophy和PhobosLament各1；父sanctum_get(impossible/100XP、图标SurgePylon)→fabricator_get(真实持有FabricatorConstructor1..64、parent frigid_altar_get)。后四层Frigid→Lapidary→Cobalt→Loqendia已在55完成。FabricatorConstructor功能尚未迁入，不能换图标/删父/普通空物品占位；因此这三进度/奖励文件仍须完整闭合后接。原sanctum_get在原JAR class常量及可读代码中都未找到Java writer，暂按无已证实writer记录，不发明自然触发来代替。

Super4池剩三件未注册：FlintsteelRifle、Hailfall、Speleothem；须保各真Geo/手持姿态/技能/弹体/粒子/GUI与来源。CorrialDivingHelmet、DevScarf、Strangesilk head/chest已补下述真实材质/穿戴/Geo路径；两Strangesilk原经Fabricator消费CryonicEnergy与Rustsilk/Wool/HelionSlag改造，站点与制造菜单仍待，不能普通配方假替代。

原orb_sanctum另仍缺ProtoiSunbeam和两嵌套原生spawner里的Icidrogrunt；完整模板/自然入口没有提前宣称完成。全模组依然未完整，不运行客户端、服务器或Render Preview。当前仅冻结WIP源码资源后委派Luna离线编译，编译不证明上述闭合或游戏效果。


## 后续已完成的装备与特效实现

四装备沿真实GeoSetArmorItem/原控制器delay5及逐实例cache/客户端扩展：Corrial头盔耐久330、防御6、附魔7、tough1/KB抗性.2、Corrial Ingot修复/EMPTY装备声；DevScarf胸甲368/4、附魔50、tough0/KB.1、无修复、SNOW_PLACE装备声、物品fireResistant；Strange头165/3与胸240/3，附魔9、无tough/KB、leather装备声，使用两Rustsilk任一修复。数值按原slot索引HEAD=3/CHEST=2的数组，未误用新type默认耐久。九个原Geo/animation/texture资源SHA相同，默认clip为diving/strange的0、scarf的idle。DevScarf真实只有胸甲，不加入原未注册头盔的Fire Resistance虚构效果。

Corrial Water Breathing60/0/ambient true/visible false逐玩家END刷新，保原Zenith头/脚标签准入；已补其原Zenith成员，不擅自授予非玩家。四件分别入原版head_armor/chest_armor，Luna已核原版enchantable/armor、durability/equippable/vanishing及部位标签继承图闭合，不新造不存在的armor类tag。

DevScarf反击使用LivingDamageEvent.Pre：已核NeoForge实际actuallyHurt入口，过无敌/伤害帧后、吸收前执行，避免Incoming阶段对被拒绝的重复攻击放烟花。原声NEUTRAL音量1/离散pitch1或2，原15紫粒在(x,y+1.2,z)/偏移(1,.1,1)/速度0；原inflate2立方体内全部Mob按距离顺序点燃2秒，不把tooltip误读成只烧攻击者，不缩成半径2球，不新增直接伤害或瞬时递归。只新增originalDamage非有限/非正不触发的防御，正常护甲/吸收前反应保持；兼容原另一个Silenced胸甲ID的同逻辑条件，但Silenced本体并未因这个条件就被声明完成。

PurpleSparkle是实际原生双帧粒子，无AAA引用；真type(false)/Provider已接，尺寸.2、quad×1.3、寿命18..41、gravity−.2、无碰撞、原输入速度、LIT/fullbright与sprites.get(age/6%2+1,2)全部保留。JSON仅namespace替换，三张相关PNG同原SHA；不会用只有JSON而无provider的占位实现。

CarborundumSickle补原误名carborundum_hoe_recipe（aa/carborundum与cobalt_rod完整原3×3排法）和原weapon/hoes tag；原版hoe附魔继承链由真实hoes成员闭合。原NO_KNOCKBACK只为ATTACK_KNOCKBACK加−20 ADDITION并以amp50续1tick，没有冲刺/受击事件取消；目标主手最终攻KB归0是内部状态替代，不宣称全局免击退，也不承诺异常外部超大modifier数值逐字同原。Phobos唱片原DiscShard围CryonicAlloy合成亦已迁入。

当前WIP检查点一：Java21离线三任务56秒/25任务3执行22最新/exit0。四装备/粒子及补充数据后的检查点二：47秒/25任务2执行23最新/exit0，日志fiftyseventh-sopel-armor-final-wip-compile.log。后加Sopel同坐标时钟继承必须同Level实例，防跨维度/存档误接，正在作最后增量验证。两个编译结果均不证明三武器、Fabricator父链或整个模组已完成。


最终同Level场景隔离补丁已委派Luna增量验证：16秒/25任务1执行24最新/exit0，fiftyseventh-sopel-world-scope-compile.log；静态sceneLevel绑定与同anchor继承检查通过。当前保存的是明确WIP检查点，不是本批奖励/进度链完成。下一步先完整迁Flintsteel/Hailfall/Speleothem，再补真实Fabricator/FabricatorConstructor及全部制造/父链；不得据35/38登记率给出整模组进度百分比。
