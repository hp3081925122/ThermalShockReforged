# 第112批原生标签只读盘点

生产前记录，由quick_explorer `/root/coverage101_inventory` 独立只读核对。快照时间2026-10-20T01:46:41.4040169+08:00，注册追踪补充时间01:49:08.0545950+08:00；第111批仍在生产，此快照不是最终覆盖或运行通过结论。

原07.9 JAR中49份命名空间标签，items/blocks/entity_types映到1.21.1单数目录后六组重复路径合并为43个逻辑标签。目标40个对应标签，另有目标独有worldgen/biome/loqendian。原全部replace:false，没有required:false成员。比较同时归一化直接ID和带#标签引用的命名空间；重复副本不重复计逻辑内容。

|标签|快照缺项|注册/来源依据|
|---|---|---|
|block/sustain_fire|minecraft:netherrack、soul_sand、soul_soil、magma_block|四原版方块；目标当前仅mantle标签|
|entity_type/therm_projectile|cryonic_projectile、flare_bullet、flintsteel_rifle_bullet、ghostglass_sword_bullet、hailfall_bullet、magnet_crossbow_bullet、thermoblaster_bullet、thermoblaster_shotgun_bullet、volcano_bullet、whistler_note|全部同名真实注册；IceSquadContent、ProjectileContent、WeaponContent.bullet(id)、OrbContent及根register调用已追踪|
|entity_type/infectionimmune|flare_gun_core、springslime、#character_mobs、florist、laut、ly、protoi_gamma、shutterbug|七实体同名真实注册，character_mobs目标标签存在；WeaponContent、SpringslimeContent、四NPC Content、ProtoiContent及根register调用已追踪|
|entity_type/sprintweed_blacklist|flare_gun_core、springslime|两者真实注册，BuffWeedBlock与TallPlantContent是当前直接消费者|
|item/weapon|18项，详zenith-crossbow-followup110.md|17项已注册，Zenith尚待112；ArmorSetEvents血甲逻辑真实消费。111集成先补合法46成员，112注册Zenith后47项闭合|
|item/nomelee|magnetic_crossbow、blinding_lantern、zenith_crossbow|前二已注册，Zenith待112。111先补合法9成员；当前未找到该标签直接消费者，不因此删除原数据|
|item/key|整份文件缺失，cryonic_key、frigid_altar_key|MaterialContent.item(id,factory)真实注册，原单复数副本合并|
|item/milk|整份文件缺失，loque_milk、minecraft:milk_bucket|UtilityContent真实注册及原版物品，原单复数副本合并|
|item/tombstones|原空标签整份缺失|保原空定义，不造墓碑成员|

undead_mobs没有原成员缺项，目标额外monolith、dormant_monolith是既有迁移内容；后续不整体覆盖而丢掉既有合法增量。当前未找到sustain_fire、therm_projectile、infectionimmune、nomelee、key、milk的直接Java消费者，仅能说明当前搜索结果，不证明原数据可删或行为已覆盖。

112生产必须在111统一构建/核验/提交后解除冻结；中央集成保原顺序、replace语义、标签引用及既有合法额外项，所有新增成员先有真实注册，不用optional掩盖漏注册。此盘点未修改生产输入、未编译、未启动游戏，标签恢复最终证据另建生产清单。
