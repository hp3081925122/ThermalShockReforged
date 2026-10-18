# 第30批：Keystone／Atlas（迁移前契约）

需求与原件行为证据见keystone-atlas-next-audit.md。真实Keystone单件EPIC、任意点击面但XZ3×3黑曜石检测、无消费/100tick冷却、Atlas放置/20tick模板/内层60tick声效全部保留。真实Atlas含Geo模型/动画/9槽自动化、方向无关的WATERLOGGED与ANIMATION、原掉落Keystone、零槽地图菜单/三按钮/全部贴图/尺寸和位置。不会以空菜单或普通物品占位。

移除旧客户端同一按钮本地执行+发包的重复逻辑，使用原生容器按钮协议并验证当前服务端菜单、真实方块、维度、距离与合法按钮；失败传送不在原维度套用目的地坐标。玩家三组Atlas点在重生/登录/存档后保留，保存真实来源维度；原aqeron→Loqendia覆盖OverworldAtlas改为保存PalaceAtlas，作为明确修复。保未配置默认坐标，不用新随机落点替代原场景。

矿种只在真实Atlas放置时按原两次独立随机初始化，不在异步矿脉生成时写SavedData。首次到达场景/进度/Portal依赖据原事件继续闭合；未实现的Aqeron世界内容仍是整模组待办，不能做假维度。

延迟激活使用有限、持久、自有维度/锚点/阶段的载体，SERVER-END推进时序保首次20在T+19、嵌套60在下一60次END执行；实体删除/重载/消费幂等与Thermite同规则。两种实际载体共享只含UUID的END索引，不持Runnable/Entity强引用或复制全局闭包。

180tick到达保护使用玩家自身有期限状态，服务端本地tick与NBT、只同步开始/结束。伤害在原MagicAbsorb抗性阶段处理，保BYPASSES_EFFECTS/BYPASSES_RESISTANCE例外、原护甲/盾/击退/受击与统计语义，禁止高抗性buff充状态。原RGB#9146F0、amp9混色权重10、普通1/2及隐身1/15抽样粒子保留，不能照用1.21的1/4频率；混色与其他可见效果共存一并核对。整模组完成前仅编译/静态，不运行游戏。


第30批当前源码实现：Keystone/Atlas真实注册、零槽地图菜单+原生按钮协议、真实九槽BE与透明Geo、全部原图像/参数、原atlasbase模板完整迁入。只在目标维度存在且切维未取消时保存实际来源坐标；从aqeron出发保存Palace组，修原覆盖Overworld组。保护仅原Loqendia/Palace目的地新增180tick；返回Overworld不新增，只同步已经存在的剩余状态。首次Shalespring/Palace与Keystone自然来源仍未闭合，正在补依赖；不声称整链正常获取完成。

目标1.21.1 ClientPacketListener收到WIN_GAME直接打开WinScreen，不再按0执行旧刷新路径。重做用原生ServerPlayer.teleportTo/changeDimension一次去最终坐标，其自带respawn/能力/活跃效果/维度事件同步，不复发WIN_GAME或重复能力效果包；保1032传送声。源与目标API行为已读，不能机械迁旧MCR“传送模板代码”。

到达保护用Player自身字段/NBT与开始结束INT快照同步，混色排除被旧amp9覆盖的低级真实抗性（高于9者取其权重），避免双算已有药效。保牛奶/图腾等DEFAULT_CURES、清除全部及清除抗性入口能终止保护；当前若真实抗性被对应删除事件保留，则不越过其取消强清。没有应用/维持高等级抗性实例，客户端只按同步状态生成原可见粒子。极端非有限/负输入防御归0，超大有限伤害仍正确减免、统计保持原有效范围，将真实减免与统计上限条件分开，避免目标NeoForge伤害容器只在统计有效范围内更新而漏减免；这里是目标源码所见极端输入边界，不冒称原1.20.1或普通玩法已经复现。

发现并修同模式粒子偏差：1.20.1 SpellParticle.MobProvider虽然把速度参数当RGB，还将green传构造器初始Y速度，red/blue零值参与水平缩放；目标ColorParticleOption把RGB移到选项后仍保留相同构造速度语义。不能统一给0或1冒充旧运动。LegacyEffectParticles同时修现有IceCreature/Bastion与到达保护，发原混色RGB/255速度，保采样与粒子数，非性能削减。渲染仍待最终实测。

原Atlas1动画长6秒/120tick，服务端排一次块tick在120后收ANIMATION0，避免晚加入反复回放旧开场；无永久ticker或范围扫描。原激活两段20/60由载体共享SERVER-END索引，剩余时间/阶段持久，写阶段后执行声光/模板，独立任务不随Atlas破坏误取消。首次创建任务事件取消时不先替换方块或加冷却；模板缺失时明确警告且不消耗动作。


HUD/背包效果栏不删原抗性图标和倒计时。仅客户端renderEffects的只读视图将内部保护转换为显示DTO，不插入实体activeEffects、不参加伤害/状态/药水判断；已有高等级真实抗性按原显示合并规则呈现。INT快照在启动/续期/载入/维度重发及结束发送，客户端本地倒计时作显示，服务端独立180tick权威；不会每tick发包。实体同步回调恢复本地显示时间，客户端计到0也等服务端结束快照收视觉，沿原权威移除规则。

首次到达链仍待补Shalespring：原70³模板343000条block/6实体，缺rock_climbing_wall、chiseled_mantlestone、train_terminal、little_spring、cryonic_cache、spice_geyser、civilian、rusty_radio及laut/shutterbug/florist；Atlas本批已真实实现。缺项不以删除NBT实体/调换ID/普通方块占位解引用，原ShalespringSpawn/loqendia_get/世界首次flag待完整依赖后迁入。Aqeron和Palace等完整内容仍待迁，本批只是主链实现，禁止当作整体传送场景完成。

验证：compileJava、compileGametestJava、processResources --offline 通过；最终语言格式恢复后重新 processResources 通过。Atlas 模板源与构建副本 SHA-256 相同，逆向命名空间后的 NBT payload 与原件全等。三个 Mixin 目标调用通过实际目标字节码静态核对；未启动游戏，实际注入、菜单交互、传送与渲染留待整模组迁完后的两端测试。详见 thirtieth-atlas-static-validation.json。
