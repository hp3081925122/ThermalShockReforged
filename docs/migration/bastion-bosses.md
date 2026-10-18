# 第十三批军团双 Boss 与奖杯

以原Icidrogunner/Icidroburner实体、AttackProcedure、DiesProcedure、两奖杯全链、原掉落及进度为据；本地目标API先核。主线程写所有代码，Luna仅机械核与编译。整个模组未完整迁入前不启动两端。

## AI、配置与战斗

两者MONSTER2.5×3.5/scale1/shadow.5，HP125/甲10/攻击10/follow48/step1/XP400；speed分别.2/.3，原KBres10实际1，攻击KB3.5按用户约束合理降至.6并受原生抗性。BLUE/RED PROGRESS BossBar、持久化、不跨维、拒DROWN和LIGHTNING，golem原声音保。原四goal优先级与reach²400/144、longMemoryfalse保；1.20.1攻击判定只距与CD，目标不额外加LOS。独立双Boss配置支持生命/攻击/速度/甲/抗性/击退、连射CD140下限56、位移CD45/20、弹伤倍率.2与技能位移4；真实攻击均按基础ATK乘倍率。

仅Player受害且source为本Boss并非原排除弹，原攻击事件先cancel；非Player不扩取消。Gunner<=10后跳-look*4/CD45，其余STUNNED60/1、aimchaingun、CD140。Burner>=7突进look*4/CD20，近距离aimflamethrower/CD140，不擅加眩晕。原@e全世界转向越权改本体，@p语义保原生选择器查询，缓存解析且不执行命令，不擅滤创造/旁观/跨维。

连射t10切firechaingun2/fireflamethrower2，目标起手坐标golem.hurt音Gunner2/5或Burner1/5；t10..55步5共10次辅助golem.hurt .5/2；t10..42步2共17发与发射音（hailfire .9/-1或firecharge .9/2）。弹位casterY+1.5，ownerless/casterUUID分离/baseATK*.2/KB0/silent；Gunner speed2/inaccuracy3/noCrit；Burner speed1/inaccuracy8/crit/本体燃烧100秒。原时序/数量不砍。

CD改实体内部保存状态，保白色原粒子、非ambient频率，不能把伪状态药水当真机制；Gunner真实眩晕仍原效果。死/跨维取消pending；卸载保存attackAge/原目标坐标/CD等，不保闭包死引用；复载恢复当前aim/fire阶段动画，已发弹独立TTL。任意时刻最多一轮且CD下限覆盖最后55tick，不能清药水递归叠连射。

## 死亡与奖杯

经验保deathTick20时序。死亡幂等一次，原loqendiaDropHeartCrystals开时独立DOMOBLOOT生成1心晶/拾取延迟10。point.inflate21盒内所有另一Boss加Speed9999/1/ambienttrue/particlesfalse，不改成球或只给一个搭档。实体loot完整迁入多pool（奖杯、合金、碎片、能量、三选一、petrichor），不以简化dropCustom替代。

奖杯复用真实GeoDecor9槽BE/六面能力、库存NBT、Geo物品渲染；FACING/animation0..1、整方块shape、COPPER1/10/光7/正确工具/透明/非导体。原没有玩家打开入口，不捏UI；保自动化并沿用既有释放隐藏库存修复。原item/generated二维图标和已有display均保。每animateTick而非每游戏tick发END_ROD1，x/z范围块内、y范围0..2、速度0/.1/0；复用传入RandomSource不每轴new随机源。原两动画仅0，状态1缺键按原停止/复位，不自造新动画。

原lod_pozar_get/knight_edge_get进度及关联首次掉落/物品tag逐项核依赖；只在真实依赖齐全后导入，不用未知ID假闭合。完整迁移后两端测试17发/10声、事件递归/ownerless、死亡/卸载/配置/库存、Render Preview模型动画透明粒子与地图。

静态纠错：死亡加速的原MobEffectInstance真实参数是ambient=true、particles=false，已按源码保，不能按省略参数误推默认粒子。原COOLDOWN颜色-1且本次实例非ambient可见，原1.20非ambient普通抽样1/2、隐形1/15，RGB按可见效果(amplifier+1)加权平均；目标原生改为1/4且随机选效果色。因此两处CD代理显式保原1/2与1/15并以白色等级1权重2混合真实效果色，不用1.21原生默认削粒子或改成跳色。旧IceCreature代理同源频率/颜色问题同步修正。客户端过期效果按原生等服务器移除同步，不能据客户端列表误判服务端延长状态；每600tick更新仅tick返回true时调用。只为真实两奖杯补原生minecraft:mineable/pickaxe，无工具类硬绑定/无额外等级门槛。

进度门槛：lod_pozar_get父项fabricator_get尚未迁入，knight_edge_get父项又为lod_pozar_get；本批明确留两项及首次奖励进度待完整进度链模块，不能删parent或造空壳父项。奖杯tag完整迁入；Boss实体多pool loot已完整迁入，不将尚未导入的进度宣称可获得。

掉落闭合补齐Petrichor：原是RecordItem（比较器6、RARE、stack1、petrichordisc音效），不能用普通无功能Item占位。原Forge Supplier构造器的lengthInTicks参数5000直接赋值，和另一deprecated原版构造器的秒×20不同；目标jukebox_song.length_in_seconds真实250，组件jukeboxPlayable接该song，描述沿用原item.*.desc键。原模型/贴图/音频保，1.21.1无需捏minecraft:music_discs标签，也不擅加creeper掉落。本项用MusicContent独立归属，便后续其它真实唱片迁入，不混入Boss实体注册。

Luna最终Java21离线compileJava/compileGametestJava/processResources通过，52秒；有限机械核确认17发/10声时序、原攻击条件/排除、动画/属性/配置、死亡Buff、两奖杯与全部loot引用闭合，Petrichor歌曲/描述/stream音频存在，代理原频率/混色实现到位。详见thirteenth-bastions-static-validation.json。没有客户端/服务端/Render Preview证据，整体与进度链未完成。后续全量复查还须机械核旧已迁近战类有无额外LOS门槛，不能只修新军团便宣称全模组同类问题清零。
