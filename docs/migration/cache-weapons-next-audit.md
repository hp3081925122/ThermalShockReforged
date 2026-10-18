# 第58批预审：高级Cache剩余三件真实武器

第57批是明确WIP检查点，完整38个输出里只有FlintsteelRifle、Hailfall、Speleothem尚无实际注册，不能删原奖励表项/普通物品占位。其它35件由Luna已核真实登记。主线负责代码，Luna确定性只读/编译；整体完整前不启动两端。

## 已读直接源码与下一步

主线已直接读取FlintsteelRifleItem、HailfallItem、SpeleothemItem的主体hook/动画/姿态片段，以及完整FlintsteelRifleBulletEntity/FlintsteelFiringProcedure。实现前继续完整Hail两父类/所有回调与Flint/Hail/Spear共用procedure、配置/弹体/动画/资源/API，不能以本预审替代完整理解。源路径均在D:/AI临时文件/ThermalShockMigration/readable/net/mcreator/thermal_shock。

Flintsteel唯一Item直接发射hook是onEntitySwing，没有use；空枪在无CD时原click_on NEUTRAL1/5应保，合法发射CD11、geckoAnim1、flint_gun_25 NEUTRAL1/随机离散1或2，5 SERVER END在原固定坐标click_off1.5/-2。实际getArrow路径new实体、setOwner、damage1/KB0/silent，(x,y+1.5,z)速度6/散布0，生存扣LightRound1；不要复制同一实体static shoot助手里仅另一调用路径的本体100秒燃烧。延迟5END声音须用有限值任务，不因玩家/子弹移除丢原合法后续，也不用捕获Level/实体的全局闭包。

Flint弹是AbstractArrow+ItemSupplier，视觉原YellowOrb。onHitEntity先super再FlintsteelHitProcedure，玩家过滤只限制附加程序，原生箭伤/友伤规则仍真实存在；不能整弹免PvP。tick先super后程序noGravity，再inGround discard，首tick重力顺序不可构造时提前。程序每tick排7延时是冗余全局任务，有限SERVER END状态应保第一份7END到期与有效首帧，不统一按出生tick先扣。玩家setOwner/父类伤害失败反弹可能允许拾取/掉外观Orb，需核目标箭父类并修复制而保原视觉/伤害。原Pyro弱点另6 therm_iframe_bypass、声音及玩家排除范围要读全程序后写。

Hailfall actual use/swing/selected inventoryTick→isUsingItem发射及CoreEject，UseAnim.BOW/useDuration60、CD5、geckoAnim1、misc_gun_2 1/.75、damage.2/速度6/散布2，生存每次70%概率扣1 LightRound。原同一80f+head.xRot弧度右臂姿态与手持平移不能凭角度看着夸张就改。其弹super箭伤与附加非玩家1、Cryo弱点另6、原Snowflake1/TTL40、撞块按owner判断的discard范围均需全父链核，不把附加玩家筛选扩大。Luna初表中的“玩家免”已因Flint父类证据纠正，Hail要同样复核。

Speleothem是Geo Item/耐久1800/UNCOMMON，主手伤害modifier9、速度−2.4，实际use与hurtEnemy/swing/selectedTick四入口：RubySpearRightclicked、RubySpearLivingEntityIsHitWithItem、SpeleothemSwing、SpeleothemHandTick。右键CD26/Firework/原DamagingAura17t amp2/look位移+y.25；挥击另CD35/动画与延迟音，近战真实Reach/NoMelee内部状态须重构，保攻击距离与有效阶段/全部表现，不删。击杀授spear_kill_get(icon RubySpear,parent ruby_get)仍缺，RubySpear本体同族尚须真实闭合，不改图标删奖励链。

Flint/Hail原手臂姿态仅usedHand且非左臂设置xRot=80f+head.xRot；两手基础(i*.56,-.52,-.72)，若同useItem再(.05,.05,.05)。目标已有EnumProxy/enumExtensions在ZapGauntlet批验证的正式机制，不用已移除ArmPose.create。新枪应复用真正相同的逐实例Geo播放/清理，不让单例stack动画互串。原两粒子无AAA引用，Flint无粒子，Hail原版Snowflake；禁止增加假AAA或删原模型层。

## 真实制造/父链后续

FlintsteelPage原唯一按钮0进行CryonicEnergy slot1+Ferrum24/SteelstemBlock6/BlazePowder4/Flint10制造，进slot0并扣材料/两声；另有完整原新配方。两Strangesilk同站点工艺尚未迁，不能普通配方取代其真实GUI。

FabricatorConstructor原不是打开GUI的站点：方块ANVIL/1,10/FACING/tooltip/光阻15，onPlace会同坐标替换成实际Fabricator并按同名Property复制朝向。实际FabricatorBlock.rightClick打开FabicGuidePageOneMenu，须迁完整GUI和全部制造分支。此项关系Sopel的fabricator_get→sanctum_get→beta_complete真实父链；不要只导JSON、注册空Fabricator或换icon造成表面闭合。原sanctum_get无已定位Java writer保持未确认，不编造触发。

继续保原版工具/附魔数据tag、全部正常获取入口、完整资源；每一固定源码/资源检查点委派Luna离线相关命令，完整模组完成后才委派专服/客户端/Render Preview最终验收。
