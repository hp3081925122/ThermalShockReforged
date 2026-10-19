# 第96批：长戟与Vilebath接线

迁移前建立本文。目标Minecraft1.21.1、NeoForge21.1.233、Gecko4.9.2，原07.9只读JAR及readable目录，基线8568f29。另一个聊天负责两缺失原生功能方块，主线核BloodstoneHalberd真实普通命中／右键突刺／挥击／手持tick、两共用戟武器、模型动画／手持变换和来源；再核五Vilebath模板与四生成数据。

原BloodstoneHalberd普通Item＋Geo、1200耐久、COMMON、主手攻击+5.5／攻速-2.4，右键lunge，复用RubySpear命中／手持过程，挥击过程也支持CarborundumHalberd。须先核既有武器模块和实际调用链，不能复制过程导致双次命中。原模板问题承接monolith-dependencies95.json：两个功能块、图框内bloodstone_halberd、桥池名vilebath_bridges缺失及旧属性键。所有修复要保完整NBT语义与原结构数量，不按资源存在直接认定注册闭合。

当前为开工文档，生产和统一编译尚待，不启动游戏。

主线实现选择复用已核现有SpearItem／SpearClient，不新增空壳长戟框架。两戟原命中／手持tick与RubySpear相同，Geo手持变换和无额外骨骼渲染也一致；Kind追加BLOODSTONE_HALBERD与CARBORUNDUM_HALBERD，保1200/900耐久、+5.5/+6.5攻击、-2.4攻速及原0/1/2完整clip。普通挥击等概率1/2动画、21CD、sweep音高1，20 SERVER END单次经验声0.7/3；右键26CD、17tick真Aura amp3、原水平look加速度/+0.25Y和26/55 END两提示。不能套普通长矛amp2、音高3和20/23双提示；新增有限单次声音模式，旧长矛两提示模式保持。真实击杀进度复用已确认死亡提交桥，伤耐久归真实攻击者；持久内部Reach/NoMelee复用现有原生属性状态，真实Aura不删。两新栈接同一有界动画缓存及清理生命周期，原硬编码突刺提示补中英语言键。

原7模型／动画／贴图／displaysettings／物品模型完整导入，原两有序配方按目标result.id转换，材料bloodstone_sword/carborundum_sword/cobalt_rod均有真实注册。两戟原weapon标签和原创意顺序接回；不硬套剑类或凭空加附魔能力。

Vilebath五模板从原JAR typed NBT重新转换，核目标LivingEntity/AttributeInstance/AttributeModifier/Entity/Mob真实存储：attributes/id/base/modifiers和资源ID操作符、active_effects、NeoForgeData、neoforge:spawn_type。原桥jigsaw引用不存在的vilebath_bridges，改指向实际原有vilebath_castle_vilebath_bridges池；保原池3:1桥/分叉权重，不新增原没入池的center概率。保完整size／每块位置与状态／palette／实体位置、库存、图框、旗帜、状态和装备，不删air或实体；原四自然JSON仅命名空间转换，结构类型／大小／距离／地形适配／高度／群系／间距／salt和处理器不简化。

统一验证：8223固定输入下compileJava/processResources离线首次成功1m4s；7567构建资源字节匹配、两模板展开通过。主线四武器文件与halberds-static96.json哈希一致，分工15生产文件未改；两处旧英文/中文翻译值完整保留。最终总证据main96-static-validation.json。整模组尚未完成，游戏未启动。
