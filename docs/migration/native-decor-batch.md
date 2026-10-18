# 第八批原生装饰与坠落香料迁移矩阵

原件07.9只读；基于649原方块对已迁448的当前差集。先记录行为再实现。本模块43块，另3种香料物品保留原10tick使用/说明/稀有度，不作为普通材料删掉其use。共用类只归并相同行为，不归并状态或形状。原件属性/回调/消费者/loot完整事实在D盘 eighth-native-decor-facts.json。

普通方块保持所有硬度、抗爆、音效、音符、速度/跳跃/摩擦、发光/遮光、透天光、空visual形状、可燃性、植物支撑、附魔台加成及tooltip。MapColor缺失时依据原纹理补色；原有显式颜色保留，light_blocking_barrier作为不可见逻辑屏障保持NONE。Wrought两块为原32高单面/角面碰撞，不能换成连线IronBars。MantlestoneDoormat六向，其余有向块四向；放置反朝向、旋转镜像保留。三spice保FallingBlock原生坠落，不改普通块。

本批不把带真实DeskPhotoScreen的desk_photo静态化；三crate保完整未闭合loot待种子链。尚未迁的其他机器/NPC消费者继续待办，不给块新增伪回调。FloweringShale原没有loot文件，本批不凭空补自掉；原randomTicks标记保留，不新增原不存在的扩散。普通工具使用原版标签/等级机制。

|ID|方向|遮光/透天光|可燃|支撑植物|附魔加成|地图色|说明条数|
|---|---|---|---|---|---|---|---|
|glowcap|none|0/True|0|False|0|METAL|0|
|vermilli_planks|none|15/False|6|False|0|COLOR_RED|0|
|snowy_permafrost|none|15/False|0|True|0|SNOW|0|
|muck|none|15/False|0|False|0|COLOR_GRAY|2|
|spire_base|none|15/False|0|True|0|COLOR_GRAY|0|
|ochre_planks|none|15/False|6|False|0|TERRACOTTA_RED|0|
|mossy_cobbled_shale|none|15/False|0|True|0|TERRACOTTA_CYAN|0|
|bordered_vermillion_planks|none|15/False|6|False|0|WARPED_HYPHAE|0|
|bordered_ochre_planks|none|15/False|6|False|0|TERRACOTTA_RED|0|
|shiverglitter_planks|none|15/False|6|False|0|COLOR_PURPLE|0|
|bordered_shiverglitter_planks|none|15/False|6|False|0|COLOR_PURPLE|0|
|sparkly_muck|none|15/False|0|False|0|TERRACOTTA_BLUE|2|
|jellercel_cell_block|none|0/True|0|False|0|COLOR_MAGENTA|0|
|mithral_spice_block|none|15/False|0|False|0|WARPED_NYLIUM|0|
|tourmaline_spice_block|none|15/False|0|False|0|WARPED_HYPHAE|0|
|kyaneos_planks|none|15/False|5|False|0|TERRACOTTA_CYAN|0|
|bordered_kyaneos_planks|none|15/False|5|False|0|TERRACOTTA_CYAN|0|
|atlas_base_edge|horizontal|15/False|0|False|0|COLOR_BLACK|0|
|black_grid_corruption|none|15/False|0|False|0|COLOR_BLACK|0|
|red_grid_corruption|none|15/False|0|False|0|CRIMSON_NYLIUM|0|
|purified_grassy_shale|none|15/False|0|True|0|GRASS|0|
|blackpowder_spice_block|none|15/False|0|False|0|TERRACOTTA_BLACK|0|
|wrought_bars|horizontal|0/True|0|False|0|COLOR_GRAY|0|
|wrought_bar_corner|horizontal|0/True|0|False|0|COLOR_GRAY|0|
|rusty_machine|horizontal|0/True|0|False|0|TERRACOTTA_GRAY|1|
|cherry_mesh_block|none|15/False|6|False|0|COLOR_PINK|0|
|glowcap_block|none|15/False|6|False|0|QUARTZ|0|
|glauko_flower|none|0/True|0|False|0|DIAMOND|0|
|snowy_sanctum_bricks|none|15/False|0|True|0|SNOW|0|
|light_blocking_barrier|none|15/False|0|False|0|NONE|0|
|mavros_planks|none|15/False|5|False|0|TERRACOTTA_GRAY|0|
|metallic_debris|none|15/False|0|False|0|COLOR_GRAY|1|
|box_voltflower|none|0/True|0|False|0|TERRACOTTA_LIGHT_BLUE|0|
|modest_archive_cabinet|none|15/False|0|False|1.0|WARPED_NYLIUM|1|
|crammed_archive_cabinet|none|15/False|0|False|2.0|WARPED_NYLIUM|1|
|protoi_server|none|15/False|0|False|0.5|COLOR_BLUE|0|
|sea_carcass_membrane|none|15/False|0|False|0|TERRACOTTA_CYAN|0|
|sea_carcass_head|horizontal|15/False|0|False|0|COLOR_GRAY|0|
|kilolyte_spawner|none|15/False|0|False|2.0|WARPED_WART_BLOCK|0|
|archive_cube|none|15/False|0|False|2.0|WARPED_WART_BLOCK|0|
|flowering_shale|none|15/False|0|True|0|STONE|0|
|laptop|horizontal|0/True|0|False|0|DEEPSLATE|0|
|mantlestone_doormat|six|15/False|0|False|0|WARPED_HYPHAE|0|

验证计划：独立核43注册及BlockItem、原loot语义/配方闭合、各方向模型属性、所有原API覆写、工具标签与MapColor来源；统一编译后仍不启动两端，等待整模组完整。

## DeskPhoto补齐

追加desk_photo为第44块，保四向/1高轮廓/无碰撞/空visual/透天光、不可破坏与原屏幕。原菜单0槽且无任何有效bound分支，位置/全局guistate从未参与功能，使用原生0槽菜单保持stillValid=true；不创建虚假库存。原lotograph.png以164×164绘于176×166窗口的(6,1)，不绘标题/背包标签，保ESC关闭、原tooltip及背景。按目标AbstractContainerScreen原生renderBackground流程避免重复绘制背景，不删除照片UI。
