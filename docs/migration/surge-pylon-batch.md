# 第54批：Surge Pylon真实Geo方块与随机脉冲

迁移前契约/原件/JAR字节码证据见sanctum-missing-blocks-next-audit.md。SurgePylon不是GuardianPylon实体，须保独立Block/BlockItem/Geo BE、animation0..1、完整模型/纹理/两个控制器/物品自定义渲染、9槽能力与原自掉落。

原父BlockBehaviour.randomTick在1.20.1委派tick，1.21.1为空，故新块显式randomTick和scheduled tick同调pulse；保原随机抽样频率，不换固定20tick。原pulse在整数blockpos播surge_pylon_pulses AMBIENT1/1、FROST_RING1个在(x,y+1,z)，偏差各.0001/speed1；原(x,y,z)中心inflate16立方体内仅SURVIVAL玩家施FROSTBITE1200/0隐藏。目标只查ServerPlayer并筛原gamemode，不降频/缩范围/排除低血玩家；原Frostbite状态与伤害/UI已有真实实现。

本体BASEDRUM、不可破坏/3600000爆抗、亮15/要求工具/无遮挡/红石非导体/push BLOCK/透天光/遮光0/box(4,.1,4,12,15.9,12)，破坏自定义声其余lodestone四声全保。MapColor明确蓝色用于Xaero，透明及原模型不改。GeoDecor真正同源库存/动画机制复用，原唯一clip0长度4秒/循环、两个控制器0过渡；原state仍0..1，但原JSON没有clip1且未找到正常writer，不编造第二个动画；保各面9槽插拔和NBT/loot，移除库存按此前共同修复不丢失。不新增原Block没有的右键GUI。

FrostRing复用已核同机制AaaStationaryParticle，保lifetime0/size.2/gravity0/noPhysics/速度0/LIT/全亮，并完整使用原frostring emitter和其纹理，不以普通圈粒替代。新增debug英语记录脉冲位置/实际生存玩家数供最终回归，不宣称静态性能收益。

正常圣殿模板入口待其它缺失BE/生物完成。FireBomb/Core拆解链还涉及未迁两祭坛/AltarUnlocked真实writer、40END转化与爆炸及loot；完整依赖已发现，不能用普通无行为Core填引用。本批先完整迁Surge本体，后续继续全部链路，保持原源证据与未完成状态。

Luna负责原所有randomTicks+tick覆写同模式清点，确认其它已迁类的randomTick覆盖，缺项后续一起补齐；全模组未完整，不启两端/Render Preview。


Luna同模式盘点已完成：原仅ShaleLamp/ChromaticShaleLamp/SurgePylon三种同时randomTicks与tick覆写；两种已迁Shale灯共用ShaleLampBlock且确实漏randomTick，主线已同批补randomTick→原tick，保原火焰环境音NEUTRAL1/1及熄灭状态仍有原无条件音，不按亮度另加门槛。清单D:/AI临时文件/ThermalShockMigration/randomtick-legacy-pattern-audit.json。不是只修Surge命名显眼的一块。


原仅复数blocks目录里给Surge标准镐与钻石等级，已迁入目标单数目录并补四低级incorrect tags；无原专用loot/recipe，保原fallback自掉落。原sanctum_get进度icon虽指Surge，父fabricator_get与实际writer仍待NPC链，记为未完成获取，不导入悬空进度。


编译时另查证本项目gradlew.bat原调用Java后直接endlocal且缺:fail标签，造成真实BUILD FAILED却返回0；主线保Java原生ERRORLEVEL跨endlocal返回、补真实失败出口。此为已复现工具链缺陷，不把过去包装退出码当构建成功证据；既有成功以Gradle实际BUILD SUCCESSFUL输出及源码核验为据。本批编译由Luna直接Java GradleWrapperMain取原生exit；bat另用版本成功/不存在任务失败验证两路径，避免只证正常0。


Luna资源复核纠正：ANIMATION属性0..1不代表原资产有两个clip，源JSON仅0一个4秒循环。此前迁移前“完整两个动画”的措辞已更正；目标JSON逐字节保原件，保原属性域，不自造缺失动画。


最终固定快照：Luna用Java21直接GradleWrapperMain离线compileJava/compileGametestJava/processResources，50秒BUILD SUCCESSFUL，2执行/23最新，原生Java退出码0；实际日志D:/AI临时文件/ThermalShockMigration/fiftyfourth-surge-randomtick-final-compile.log。修改bat经--version成功0/不存在任务失败1双路径验证，日志gradle-exit-code-version-probe.log与gradle-exit-code-failure-probe.log。原初次helper静态方法与父类properties同签名，已改createPylonProperties再成功编译；不把前两次BUILD FAILED/包装0当成功。

机械核全参数、真实模式筛选/效果、两灯同类回接、素材JSON命名空间归一/二进制SHA、Geo物品与BE自动接线通过。FrostRing EFK仅引用Texture/FrostbiteRing.png，target EFK/此PNG/粒子sprite均同原hash，完整AAA依赖闭合；无自造clip1/GUI。正常生存来源随完整圣殿模板与后续FireBomb/Core继续，尚未启游戏。skill54事实与已确认坑已更新并quick_validate通过。
