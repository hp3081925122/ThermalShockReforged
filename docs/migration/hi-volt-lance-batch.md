# Hi-Volt Lance 第82批迁移前核查

本批先按真实入口核查再实现；第81批已本地提交24eb8bc。禁止子代理，全模组代码完整前不启动两端或Render Preview。本批聚焦Hi-Volt Lance；Thermoblaster两种环境及Zenith左右键已初步核出，留后续独立完整实现，不提前启用其制造。

原HiVoltLanceItem为普通Item，1200耐久/UNCOMMON、主手攻击+6/攻速-2.4，无getEnchantmentValue覆盖，保持原生默认0；不能按长枪名字改成SwordItem/TridentItem或虚构附魔值。原命中调用RubySpearLivingEntityIsHitWithItem：扣1耐久、死亡授spear_kill_get。使用真实原版扣耐久流程；已有公共SpearItem按health判死亡还需核原生dead，避免死亡取消误奖。

实际手持入口是RubySpearItemInHandTickProcedure，不是同名HiVoltLanceHandTickProcedure。无CD时REACH3tick/amp2，原Reach每级属性+1，等于实体交互范围+3；有CD时NO_MELEE3tick/amp0，复用既有HeldWeaponState原生属性状态。不能盲用没有真实调用的过程而丢掉CD期间禁近战。

左键无CD挥手：动画1、玩家16tickCD、NEUTRAL PLAYER_ATTACK_SWEEP1/3及galvanitehurt .5/8，在初始位置16 SERVER END EXPERIENCE_ORB_PICKUP .7/3。右键保原super.use结果，动画2、120tickCD、自身Slow20tick/amp1/false/true、初始位置sparkhum1/1；这是短暂真实减速，不当255级内部锁复制。

右键初发同步范围伤，后在2/4/6/8/10/12/14/16/18 SERVER END重复，共十次。每次在最初x/y/z的inflate2.5立方AABB查询全部Entity、按距排序，只排除原使用者；每次固定1.5 ownerless therm_iframe_bypass。不能改球形、只Living、排除掉物/已驯服动物或把十次压成一次15伤。迁移载体需额外排除自己。20 END在同锚点对除使用者的Living施加Electrified360/amp0，并播放galvanitehurt1/7与IRON_GOLEM_HURT .7/5；120及122 END各XP提示声 .7/4。一个有限可保存122 END任务区分挥手提示和右键脉冲，不持强实体引用、不强加载、不随使用者移动。

复用真实SpearItem中属性、选中状态、扣耐久与Geo渲染公共逻辑，新增明确Hi-Volt类型及专用范围流程，不把原右键突进套到电击长枪。保完整geo/hi_volt_lance.geo.json、animations/hi_volt_lance.animation.json、textures/item/hi_volt_lance.png，沿item父模型及builtin/entity保显示变换、0/1/2全部动画、entityTranslucent及原手变换。原ItemAnimationFactory确有主副手消费动画，目标按栈同步并回收缓存；新任务实际NoopRenderer。

原JAR清点只含ferro_type成员，weapon原标签没有此枪，不擅自补入。完整制造成本、双语tooltip和原spear_kill_get父链同时核。第78批已证实制造检查2电池、扣料和显示3电池，目标已统一完整3，不回退错误门槛。原普通耐久Item附魔资格按已核1.20.1 BREAKABLE与1.21.1 durability标签迁；同时逐项核此前三长枪/两枪实际原生类并补缺项。

整组完成后统一compileJava/processResources及静态原资产/数据/工厂核对；最后两端与Render Preview仍待全模组代码完整后主对话执行。


## 第82批完成记录

新增SpearItem.Kind.HI_VOLT及真实ITEMS注册，复用原生属性/手持状态/命中扣耐久/完整Geo渲染，右键独立电击分支不走旧突进。保1200耐久、UNCOMMON、攻击+6/攻速-2.4、默认附魔值0；完整0循环、1为0.75秒、2为1.5833秒，两手原变换/entityTranslucent/builtin/entity完整。原始标签仅ferro_type（weapon无该成员），按证据迁入而非凭名字补齐类别。

有限保存HiVoltPulseEntity同时承载单次16END挥手提示或最长122END电击：同步一次+2至18每隔2END共十次1.5 ownerless伤，固定最初坐标inflate2.5全部Entity按距排序，排除使用者UUID及新载体自身；20END带电360/amp0和双声，120/122两提示声。没有改球形、合并总伤、删宠物/掉物范围或追随玩家。只持UUID和数据，卸载可存且不强加载；NoopRenderer和控制器缓存回收/断开清空实际接线。20tick/amp1可见Slow保实际施法减速，隐藏REACH/NO_MELEE改原生属性状态，真实入口是RubySpear公共过程。

原公共长枪命中按health判断击杀会在NeoForge死亡取消时误奖，现经已核LivingEntity.dead的Mixin Accessor确认后再奖spear_kill_get，完整原父ruby_get与impossible条件不变，添加英文debug记录被跳过场景；其他三长枪也共享修复。逐类核原Speleothem/Ruby/Carborundum/FlintsteelRifle/Hailfall均extends Item且有耐久，补它们与Hi-Volt共六项原durability附魔资格，不擅加Sword/Trident/Mace附魔。

整组compileJava/processResources首轮通过（52秒，24任务2执行22最新）；十原资产（五模型/动画/贴图父链和五音频）及八数据/语言/Mixin构建路径静态通过，原三电池完整费用和Fabricator实际引用复核，当前24活动/7真实产物待迁。未启动任何游戏，无子代理。Thermoblaster/Zenith两模式初查留后续，不因其已有弹体而假称整枪已迁；全局元素与其余未迁内容、最终两端和Render Preview仍待整个模组代码完整。
