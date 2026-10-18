# 第七批 root 实现静态复核

本次按 gardenling-audit、gardenling-implementation、special-plants-batch 与 int-provider-format-fixes 既有矩阵定向复核，不重新做普查。检查原 readable/JAR 与目标 MC1.21.1、NeoForge21.1.233源码；未编译、未启动游戏。root 的 farming/glass 尚在修改，结论仅覆盖本文具名文件快照。

## 已发现并修复：Herald 配置加载时序

这是确定启动风险，不是推测。目标 CommonModLoader.begin 先执行 GameData.postRegisterEvents；后者在注册完成后调用 CommonHooks.modifyAttributes，发布 EntityAttributeCreationEvent；之后 CommonModLoader 才执行 ConfigTracker.loadConfigs(COMMON)。ModConfigSpec.ConfigValue.getRaw 在 loadedConfig=null 时明确 checkState 抛 IllegalStateException；getDefault() 直接返回 defaultSupplier，不要求配置已加载。不能以编译通过替代此启动检查。此前即时消息把触发阶段简写为 freezeData，准确入口是 postRegisterEvents。

Herald.createAttributes 原四个 get() 已经 root 授权改为 getDefault()。默认属性供应器始终可在注册阶段建立。实际服务器实体构造时仅 SPEC.isLoaded=true 才读取实际配置，设置 maxHealth/armor/attackKnockback/knockbackResistance，并给全新实体配置后的初始满血。客户端不读取 COMMON 覆盖属性，依靠原生服务器同步。

构造过早时不吞异常、不强读：服务器 join 与首次 baseTick 再调用幂等 applyConfiguredAttributes。NBT 加载先标记 loadedFromSave，调用 super 读取真实属性/Health，再重新应用配置基础属性，仅将保存 Health 上限钳到新 maxHealth，不补满血。这样旧伤实体重新加入不会被 join 回满；新实体只初始化一次。加入英文 debug，记录 UUID、是否存档加载、当前/最大生命、甲、击退与抗性，供最终运行验收；不是每tick日志。相关方法 getDefault/isLoaded 已核目标 ModConfigSpec，setBaseValue/getAttribute 用本项目既有与目标 LivingEntity/AttributeInstance API。

本次只修 HeraldEntity 与 ManglerContent 的配置应用入口，未改 Forecast（root 负责）。召唤技能等运行期 get() 保持原规则；正常世界开始前 COMMON 已加载，不在属性事件读取。数据生成若只走 begin 不加载 COMMON，此修复仍允许默认属性注册。

## Gardenling：未发现真实遗漏

GardenlingEntity 保留只环顾、HP1/ATK0/speed0/step0/实际抗性1、常驻、Texture保存、原hurt/death声音。直接药水/区域云与 FALL/CACTUS/DROWN/LIGHTNING 的拒伤逐项吻合原源码；没有添加原无的火免或全免。dropCustomDeathLoot 仍先 super，再回收固定一个构造器；由原生死亡掉落入口控制，不在 hurt 回收，不引入一击多次回收。

构造器 onPlace 先 previous 非同块、服务端、当前位置仍是自身，再 removeBlock 成功后才 spawn；重入时当前位置已移除，不再第二次生成。原无BE，此处不制造空BE。目标 Level.removeBlock 以原当前位置 fluidstate.createLegacyBlock/setBlock flag3 实际消费，不掉方块物品。保留 POOF20、中心+.5、三轴偏移.5、speed0，以及两个 BLOCKS 声音1/2。生成被其他模组取消仍消费，和原先先spawn最后置AIR同为消费结果。普通合法多块各自构造不加无依据全局cap或TTL。

GardenlingRenderers 保透明基础层、eyes发光层、NO_OVERLAY/白色packedColor、模型/贴图/idle动画、scale默认1与shadow.5；无死亡侧倒覆写，与原默认死亡表现相符。移除原无意义refreshDimensions(scale1)未减少真实变化。仅idle controller足以覆盖原唯一可达动画；原无caller的procedure字段不当作缺技能。

农业五种种子与图鉴/Skullscor等接口仍按原矩阵归各owner，本次不宣称它们已运行验证。

## 五特殊植物：未发现需返工的功能缺口

BuffWeed 两种效果120tick/amp1/ambient与particles true、黑名单、LivingEntity条件和服务器权威吻合。Flower父机制、100tick炖菜、光9、火100/60、XZ偏移由 GardenFlowerBlock/SpecialPlantContent 保留。没有擅加CD或额外粒子。

Dandelion 普通碰撞仅非潜行转换，服务端一次setBlock成功后POOF5、中心/偏移.3/speed.05和wool.break1/.6；普通右键POOF1在y+.7且无声音。windblown只检查真实玩家主手骨粉，成功改块后实际该堆栈shrink1；保原创造也消费规则。useItemOn 返回SUCCESS阻止继续原生骨粉物品逻辑，不会再从全背包/合成格扣除，useWithoutItem同样尊重原成功交互语义。这里主手限制来自原procedure，不能擅改为“任意交互手骨粉”。服务端广播碰撞声音是已记录的权威修复，原仅碰撞客户端本地发声；数量/音高/音量保留，未宣称听感已实测。

VermillionRoots 与原逐项对照：WATERLOGGED默认false，放置实际水检测，getFluidState返回source water，邻接变更scheduleTick；目标 SimpleWaterloggedBlock继续提供注水/取水。无新增土壤限制；光7/遮光0/无水透天光、轮廓5..11×16、空visual shape、无碰撞、speed.7/jump.9、硬.2/抗爆0与发光条件一致。MapColor.COLOR_RED为已记录贴图语义补色，不改透明性。

## 25处旧 provider 修正

独立脚本逐清单定位原JAR与当前8个JSON的25个provider：全部原type一致（原有uniform与minecraft:uniform短名均可由ResourceLocation默认minecraft解析），原value中的min_inclusive/max_inclusive与当前搬平后字段逐值相等，min<=max。目标 IntProvider.CODEC 是注册类型 dispatch，UniformInt.CODEC 是顶层两个字段的 MapCodec，因此去旧value包装正确；没有变更次数/范围，也没有把每次采样错误改成固定常量。

静态机械对照结果25/25通过。脚本 D:/AI临时文件/ThermalShockMigration/seventh-review-providers.py；第一次脚本错误地假设原type全部带minecraft前缀，已修脚本允许原合法短名再完整通过，未据脚本误报修改数据。此项不是整个worldgen Codec运行解码证明；其他provider、实际群系生成和Xaero仍待最终验证。

## 本次边界与后续验收

root源码与资源未修改。本次授权修复仅 HeraldEntity、ManglerContent 两文件，随后重新冻结。Gardenling/五植物控制流与具名25provider未发现额外真实遗漏；这不代表整个模组迁移完毕。最终需验证启动属性注册、非默认Herald配置、新生成满血与受伤存档重载不回血、客户端属性同步、构造器单次消费/死亡回收、植物双手与创造骨粉交互、水浸和粒子/发光实际渲染。
