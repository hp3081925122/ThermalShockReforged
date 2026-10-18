# 四胶囊生物接续审计（迁移前）

Guardian Pylon剧情依赖真实sanctum_errant/frostilyte_gunner/kilolyte/sanctum_ripple四实体，目前不能用相机目录当实现；先迁完整实体再接Guardian。模型/动画/材质/透明/eyes层/体积/头部转动/目标与寻路/伤害及掉落进度均依原类逐项核。

主线已读SanctumErrantEntity及其ErrantEntityIsHurt/ErrantEntityDies、Model、Renderer。Monster/持久/XP30/台阶1，属性速度.3/血40/甲6/伤6/跟随12/KB抗.3/攻击KB1；melee优先1/速度1/false/固定距离平方9，HurtBy目标2带alert，stroll3速度1/look4/player目标5(true,false)。纹理默认dweller，errant Geo/animation，bipedHead pitch/yaw弧度，透明/阴影.5/scale1。动画transition2，普通移动walk、潜行idle、aggressive+moving aggro、其余aggroidle；保布尔实际条件，不改成常规“静止idle”。20tick死亡后原掉XP，需核1.21经验时机而非默认提前。

受击在super.hurt前检查PARRYING，有则服务器加HEAL duration1 amp0 ambient=true visible=false，音enchantment_table.use NEUTRAL .8/3；不能把1tick瞬间效果改为伤害前直接heal4，原可能在致死前来不及恢复。原super伤害仍会经已迁ParryingStatus全局流程，不能删除实体独立行为或重复全局调用。死亡原给sourceEntity为ServerPlayer时award errant_get，需核原adv完整父链与UI参数和其它正常入口；取消死亡不应提前/重复奖进度。该类无自身tick Procedure，但需核外部实体字段/类型tag的真实调用。

主线初读其余：FrostilyteGunner实体本体scale1.1，属性.25速度/32血/8甲/5伤/16跟随/.2攻击KB，不可混同现有普通frostilyte；Kilolyte Monster持久/本体scale2，属性.17速度/100血/10甲/0基础伤/12跟随/原KB抗10有效1，DROWN拒伤、掉ZapGauntlet、finalizeSpawn调用KilolyteOnInitialEntitySpawn，其有限攻击链须完整读完，不能因为普通melee0就当无战斗实体。SanctumRipple属性.15/35血/6甲/7伤/16跟随/.8攻击KB，melee1.2、Squall回避32/1/1.2，免LIGHTNING/WITHER/WITHER_SKULL；死亡复用GalvaniteEntityDies（doEntityDrops+WorldCrux>=1，两次各20% BioelectricCell），原init自然spawn predicate雷暴且下方SnowyShale或SnowyCobalt，不能误加通用黑暗/普通Monster规则。source第三项重复SnowyCobalt保逻辑含义不发明新地面。

现有GalvaniteEntity有完整有效XP延迟/两次独立掉落和按真实Squall ID的Avoid目标，可按确切共有行为提取两个真实调用者；不能整类继承带进Ripple没有的finalizeSpawn初始化。各怪技能伤害按基础攻击与倍率，0基础伤类须核原实际技能定值用途再给有意义配置，不能擅自置0造成哑炮。攻击击退受原生抗性影响、数值不要额外放大；精英配置按用户规则。

后续只读子代理盘点原注册尺寸/蛋色/loot/tag/正常来源/外部调用与全部资源，主线实现，固定快照编译/机械核。全模组完成后才做服务端和Render Preview。最后全量复查自定义旧melee reach迁移：新canPerformAttack不能未经证据额外加LOS；现有Ice/Hunter某些目标显式LOS尚待沿原Procedure判断，不仅凭关键词判错。
