# 第118批A：独占实体依赖与时序

生产修改前建立。基线`90665ea`，目标Minecraft 1.21.1 / NeoForge 21.1.233，原07.9 JAR SHA256为`3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436`。

最新直接分工优先于主线首轮候选清单：A占用`entity/icesquad/IceSquadProjectileEntity.java`、`entity/mangler/ManglerEntity.java`、`entity/sanctum/GuardianPylonEntity.java`，以及后续确证且主线未占用的独占实体/渲染文件。主线首轮文档中这三项视为由A处理并交接，避免双方写同一文件。

不修改Probosker、Cavebloomer攻击及载体链、共享根入口、ClientEvents、配置、语言、标签、主线占用文件或`docs/testing/*`。新增共享接线只写A交接说明。不编译、启动、stage、commit或push。临时脚本与取证放`D:/AI临时文件/ThermalShockMigration`，Java不写注释，调试日志英文，全部UTF-8。

## 已证实缺口与方案

1. IceSquad五kind：AltCryonic/Cryonic/GunnerBullet为80 SERVER END，BurnerFlame20，StriderAttack5；原每tick重复排队的首次有效终止由有限剩余计时承接。从首次实际tick的原过程阶段启动，保父箭、命中、粒子、无重力差异、燃烧、归属、拾取保护与伤害/抗性击退。兼容第117批Age存档，不把生命周期提前到构造或加入世界。
2. Mangler/SummonedMangler：原每次hurt均独立排60 END清动画，当前单一重设计时不等价。改60位long有界待办，保同END幂等解除和不同受伤时点的全部解除时点；单独保存实际stunned动画状态，避免旧待办尚在时重载错误恢复动画。真实Slow与CD续期、适中抗性反冲及换维规则保持。
3. GuardianPylon：原`SparklerbulbUpdateTickProcedure`发END_ROD于固定(.5,.5,.5)。进一步补读`guardian-pylon-batch.md`的第51批实施结果，已有明确记录修正为柱子位置、保数量5/offset1.5/speed.5。因此撤销第117批“未找到记录”的待决判断，保现有修复，不修改该生产文件；本批刚进行的坐标还原已撤回，最终对90665ea不应有Git差异。
4. VolcanoBullet原渲染与Liteprism/Thistle同小激光模型和zerkglow，当前却绑定ThrownItemRenderer。A独占修改SmallBeamRenderer为可接收LegacyArrow子类的泛型，不改模型/旋转/纹理；Volcano绑定只交主线替换，不改共享ProjectileRenderers。
5. ZapWarning原初始化20 SERVER END，当前19未来实体tick阶段不等价。A占用ZapWarningEntity及新增独立ZapDetonationEntity，保普通死亡后的合法尾阶段、不延长20帧尸体，保固定锚/伤害快照/调用方ignoreCaster/完整范围伤害和警告粒子；真正强制删除取消，卸载暂停并持久关联。新载体注册/Noop由主线接入。

本批范围已冻结并停笔，不继续扩展第117批其他依赖或渲染缺口。登记已实现、共享待接和未验收，不以文件存在或AST通过代替语义完成。

## 验证与交付

静态固定原源码/class SHA、目标全文/AST、逐kind和重复受伤参考队列时序、存读档及取消边界、原模型/UV/纹理/控制器证据。主线统一集成编译后再安排运行/Render Preview，A本路不启动。完成后交独占矩阵、共享说明、文件SHA和静态验证，再停笔。

## 最终交接状态

本批生产文件共5个：IceSquadProjectileEntity、ManglerEntity、SmallBeamRenderer、ZapWarningEntity，以及新增ZapDetonationEntity。GuardianPylon保留第51批已记录的位置修复，本批无行为变更。未修改主线Probosker、共享入口、ClientEvents、ProjectileRenderers或用户测试文件。

静态审计实际结果为511项检查、502项通过、9项失败，不能作为全部通过交付。失败项均为IceSquad非计时方法全文一致性比较：configure、getDefaultPickupItem、doKnockback、claim、onHitEntity、onHitBlock、playerTouch、extraHit、sound。这些方法未手动修改；可能涉及换行差异，但原因尚未核实。按主线最终指令，仅记录失败，不继续搜索、修复审计或重跑。外层命令的退出状态不能代替审计JSON中的失败结果。此前git diff --check通过；停笔时不再重复运行。

已保存source、matrix、validation三个JSON；其中参考模型和AST检查不等于Java运行测试。矩阵不宣称第117批全部待办闭合。共享接线见remaining-entity-dependencies-a118-shared.md，固定文件SHA见remaining-entity-dependencies-a118-manifest.json。

Zap新增载体尚未注册，必须由主线完成根入口注册和Noop渲染绑定后统一编译。尚未验收极端边界：外部传送使警告本体与固定锚载体分离到不同区块，本体卸载而载体先完成时，本体可能保留旧任务UUID；未建立世界级完成台账，不能宣称该边界已解决。普通死亡、卸载、强制移除和换维分支仍需真实运行验证。

本路没有编译、启动服务端或客户端、Render Preview测试、stage、commit或push。按最新指令，保存交接后直接汇报并停笔。
