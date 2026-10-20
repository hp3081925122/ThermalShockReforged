# 第120批A：Worldgen只读复审

## 边界与清单

quick_explorer/Luna Bohr分两轮只读核清单、引用和具体候选；主代理核报告矛盾并整理。本组没有生产修改、构建或游戏测试，未扩大A对loot以外资源的所有权。

原298个worldgen JSON与目标对应数量一致：biome 10、configured_feature 113、placed_feature 110、processor_list 3、structure 20、structure_set 20、template_pool 22；另有dimension和dimension_type各2。目录按1.21.1的registry种类处理，不能把所有目录一律机械改单数。

原162个biome modifier对应目标161个。除原悬空`hanging_bulb_patch_biome_modifier`外，其余161项经过namespace、Forge至NeoForge和已核路径转换后字段对应。未以目标文件数量相同替代Codec/引用与来源消费者核查。

原164个NBT模板、目标97个同名模板；本轮机械追到worldgen pool location和feature structure引用的79个模板ID均能在目标找到。剩余67个模板在本轮JSON引用闭环及精确Java调用搜索中未发现来源，不据此创建新自然生成入口，也不等于证明其全部不可达。目标97模板gzip解压后的原始SHA不同不构成内容缺失证据，本轮没有重做完整NBT标签级归一对照。

## 候选逐项去误报

| 候选 | 具体证据与判定 | 本批动作 |
| --- | --- | --- |
| `hanging_bulb_patch_biome_modifier`数量少1 | 原指向`#thermal_shock:purebiomes`，原JAR/Java均未定义该范围。`vilebathblocks-batch96.md:36`已保原文调查存证；真实灯泡feature和城堡来源保留。属于既有源悬空引用待办，不是已证实迁移遗漏。 | 不猜群系、不造空tag、不恢复导致悬空引用的modifier。 |
| `blizzard_sanctum`上界240变168 | `sanctum-features-a-batch106.md:33-37`按原最高实块相对Y152、根Y=start-1与世界顶界319核定：145..168全部合法，169..240会截实块。改168保全部合法高度与完整模板，未裁结构或删有效起点。 | 保留有授权的历史边界修复。 |
| `glaciation_icicles`的Glitterdrop Properties消失 | 首轮报告误把不同加权分支的普通icicles state0与目标Glitterdrop相对照。主代理重新直接读原JAR确认：原Glitterdrop分支本来就是`thermal_shock:glitterdrop_icicle`，仅附带无效`blockstate=0`；目标保同一方块并去无效属性。`twentieth-glaciation-static-validation.json`已有此记录，普通icicles三态完整保留。 | 不按误配的分支报告改回普通冰柱；没有确证生成方块替换缺陷。 |
| Aqeron两旧字段消失 | `aqeron-palace-next-audit.md:7`及Luna复核1.20.1 NoiseSettings/NoiseGeneratorSettings Codec确认，双方均不消费`settings.name`和`noise.island_noise_override`。原完整end_islands/sloped_cheese路由仍保留。 | 不为无效旧字段补造新算法。 |
| 三processor从Integrated API ID换为自有ID | `lab-worldgen-batch93.md:37`记录原作者common-1.20.1 commit `803ae70cc0879910fec29834381769f12f885490`的实现。目标`world/WaterloggingFixProcessor`保有流体状态块的中心chunk/高度区间更新排程并原样返回state/NBT，真实被blizzard_base、cavebloomer_field、machinist_lab调用。 | 保留等效原生实现，不删处理器、不重加外部依赖。 |

UniformInt/BiasedToBottomInt的旧`value`壳按目标MapCodec展平，范围保持；HeightProvider不作同样的错误展平。dimension_type的monster_spawn_light_level仍保0..7。自定义`structure_feature`已追到`VermilliContent`注册和`StructureFeature`实际placeInWorld调用，不把类名变化认作丢功能。

## 结论与未覆盖

当前有界候选均已分类，未确认需协调生产修改的worldgen缺陷；A本批未改worldgen文件。这不等于完整世界生成验收。

未覆盖：97模板的本轮全量NBT标签/嵌套实体/物品组件归一、67候选无用模板的完整跨模块调用可达证明、实际区块生成和邻居流体更新、全量群系叠加后的运行结果。原purebiomes范围仍无真实依据，单列来源待办，不以无证据修补宣称闭合。真实游戏生成、性能和渲染由后续独立验收负责，本批不启动游戏。
