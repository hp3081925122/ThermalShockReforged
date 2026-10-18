# 圣殿结构与正常来源：第52批迁移前契约

来源仍为07.9原JAR，只读。第51批守卫塔与四胶囊真实实体已闭合；本批不能用空模板、删palette、删除实体/容器来补自然来源。待Luna列出原模板内实际缺失注册与物品后，由主线补全部依赖，再迁完整数据。没有闭合前不激活半成品世界生成。

## 实际数据链

原数据不是orb_sanctum同名worldgen结构。blizzard_sanctum结构→同名template_pool→orb_sanctum.nbt；jigsaw size1/max_distance64/surface_structures/terrain_adaptation none；uniform绝对145..240；cobalt_heights群系；random_spread spacing30/separation20/salt1202105222，weight1/use_expansion_hack false。

sunken_blizzard_base结构→同名template_pool→sunken_blizzard_lab.nbt；jigsaw size1/max_distance64/underground_structures/terrain_adaptation none；uniform绝对0..40；luminous_sea群系；random_spread spacing28/separation24/salt1407213395，weight1/use_expansion_hack false。

两pool皆fallback minecraft:empty、唯一weight1、single_pool_element/rigid，内联block_ignore仅minecraft:structure_block。保原模板中空气/水/方块实体/嵌套库存/实体与所有状态属性；目标structure单数目录及解析命名空间转换参照此前37/39/44批，保DataVersion并让原生DFU升级，需逆替换后解压payload逐字节比较。不可对gzip直接字符串替换。

archive_sanctum.nbt没有直接同名JSON引用，正常可达入口仍需沿实际别名池/Java/原JAR二进制确认；不要凭文件存在新增捏造的自然生成或删除孤立资源。

## 接续与验收

只读盘点依赖由validation_luna负责；主线不重复盘点其结果。实现前依据缺失依赖选择闭合批次，并逐一记录实际来源。全模组尚未完成，不运行服务器/客户端或Render Preview；此批按固定代码资源快照进行编译、完整模板语义和注册/状态比对。


## Luna实证依赖清单

orb_sanctum：106×159×106/1513694记录/249状态/335BE/29实体，DataVersion3465。缺块brick_climbing_wall、desk_director、mantleslag、phaser、super_cryonic_cache、surge_pylon；后3个BE类型为desk_director(1)、super_cryonic_cache(8)、surge_pylon(3)。非空库存0。ProtoiSunbeam×2未注册；两个原生刷怪笼SpawnData.entity.id均为Icidrogrunt未注册。Soldier/Gunner/Cryomancer/Icidrozerk/Frostilyte/Guardian已有注册，仍需本次起逐链行为复审，注册不等于完整。

archive_sanctum：106³/1191016记录/203状态/333BE，缺brick_climbing_wall/mantleslag/phaser/surge_pylon与3个surge_pylon BE。另含原外部untagged_mobs:forcefield方块和automobility:automobile实体，目标没有对应依赖；需保源证据并核实际来源，不能擅自替为普通方块或删车后声称完整。正常生成入口尚未查证。

sunken_blizzard_lab：81×28×68/32174记录/185状态/32BE，缺bottle_of_lunarlight/hologram_monitor/trident_lamp，前两者各1原BE；Soldier4/Frostilyte6/Icidrozerk3已注册，库存为空。详细原始报告D:/AI临时文件/ThermalShockMigration/pylon-next-structures-dependencies.json及pylon-next-structures-target-coverage.json，后者早期自动表未计spawner，以上已补最终事实。

第52批先补Soldier END连招与死亡经验，完整契约见soldier-end-combo-batch.md，再补模板依赖。没有删模板内容、没有加入半成品自然生成。
