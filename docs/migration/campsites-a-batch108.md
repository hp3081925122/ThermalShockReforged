# 第108批 A 线：营地自然入口与模板载荷

迁移前建立。基线 `758d15ac612aebff67f3cf84a6b068f0156fce79`，目标 Minecraft 1.21.1、NeoForge 21.1.233；原件只读 `D:/AI临时文件/ThermalShockMigration/thermal_shock-07.9-original.jar`，SHA256 `3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436`。

## 范围

独占 large_campsite、medium_campsite、small_campsite、shalespring_grove 四条 structure/structure_set/template_pool；新增完整 campmed、campsmall、shalespring_grove_derelict 模板，核验复用 whisper_home 的完整几何和载荷；补原 non_biome 群系并追踪其真实维度/自然消费者。独占现有 blizzard_outpost、blizzardoutpostrubicon 的全部载荷显式升级，保留后者原无精确自然消费者的边界。

四自然链保持原群系、起点、投影高度图、terrain_adaptation、处理器、候选、盐、间距、分离距离及全部随机参数；递归核真实池引用及Jigsaw载荷。non_biome 不凭名字换成其他群系或增加维度分布。

## 依据与方案

旧模板的原生 DFU 入口不保证未知模组实体或方块实体choice被转换；两 Blizzard 模板的122个模组实体、七处漂浮效果及全部实际库存须先盘点typed路径，再按当前源码转换后写3955。原件七处均为 `forge:id=minecraft:levitation`、旧Id25，不是预分工口述的缓慢，以实际载荷为准。经验球的根Count属于实体数据，不按ItemStack处理；药效CurativeItems属于治疗配置，不按可见库存计数；附魔、书籍等按实际目标组件处理，禁止仅落入custom_data冒充功能迁移。

复用已核的typed NBT读写和流式几何复制，只复用基础类型与序列化能力，不调用旧通用converter。每项属性、修饰符、效果、cures、持久数据、spawn、库存及旗帜按实际字段逐项核源码；保全部块记录、palette、实体、位置、朝向、载荷和外部caps。外部cap元数据保留与外部模组功能已实现分开记录。

## 所有权与验收

只写本线独占资源与文档；根入口、共享语言/标签、总文档、REF和skill由主线接精确增量。临时及脚本仅D:/AI临时文件，UTF-8。交原件SHA、typed差异、逐载荷独立预期核验、几何字节哈希与最终生产清单后停笔，由主线整批固定输入统一编译；本线不独立构建、游戏、stage或commit。

全部代码完整迁移并通过无简化复审后，由主线安排子代理执行最终客户端及Render Preview完整原件对照；当前客户端/渲染待验，不提前启动。服务端正在独立测试，本线不联系不干预，不将进行中视为通过。

## 本批生产与复查

已生产13份JSON和5份NBT：四条完整自然链、原non_biome、campmed/campsmall/shalespring_grove_derelict三新模板及两份Blizzard模板载荷升级。所属五模板共251815个方块记录、134实体、183方块实体，完整几何/状态/位置/顺序字节哈希与原件一致。全部实体与BE逐份按独立预期比较typed NBT；只允许列出的命名空间和真实格式转换。

两Blizzard的223条Random spawn bonus逐UUID映射独立minecraft:<uuid> ID，保原Amount、Operation及每条身份，不求和不折叠。两营地pixie另保两条；复用whisper修正文件另保一条。原Mob.finalizeSpawn每次建立随机UUID并抽样，目标canonical ID存在才跳过抽样；独立保存ID让Jigsaw首次放置继续抽样。当前源码结论不扩张为已运行验证，其他模板留109批。

七条漂浮全部保Amplifier=1、Duration=5、ambient/show_particles/show_icon及牛奶/图腾治愈；旧Id实际为INT25。两掉落phobotite的Item转count=INT1，不造custom_data。八个经验球根Count仍为INT1。六个刷怪笼保原SpawnData、空SpawnPotentials和所有时间/距离/数量参数；装备、空库存、血量、UUID、朝向、持久标记、外部ForgeCaps/BalmData保类型和值。外部structure_gel传送元数据保留不等于外部模组联动已实现。

forge:step_height_addition按实际旧实体setMaxUpStep的float值加原addition转原生STEP_HEIGHT；icidrostrider为1.0f，其余实际涉及实体0.6f。复用whisper保存的forge:step_height不同：1.20.1 Forge没有注册该ID，旧AttributeMap会忽略它。现有目标将其误映射为生效的原生步高0；交给主线的修正文件恢复未知ID（保Base0），目标AttributeMap同样忽略，维持实体默认0.6f步高，不静默为无效旧字段赋予新行为。

复用whisper_home生产文件本线未写；D盘修正文件及原目标SHA、目标SHA、四个typed差异项见integration108。三个逻辑变化为出生字段改名、随机修饰符身份和上述未知属性ID纠正；所有BE、几何、实体其他字段不变。需主线接收这个文件后再统一验证。

109个唯一palette ID中108个已核真实注册、994个属性值合法；剩余loque_bush在原件注册表也不存在，旧/新NbtUtils均回退空气。保持原palette遗留ID而不补造方块、不替换模板。366项既有模型、贴图、动画和音频资源已对原件静态比较，其中sounds.json仅核boreanforest节点；这不替代Render Preview。

non_biome完整保原天气、音乐、粒子、颜色、刷怪和11个空features阶段。原JSON仅grove/slagway/test_jigsaw引用，原维度JSON和原Java无实际分布消费者；未添加气候表或替换群系。blizzardoutpostrubicon未找到精确原自然消费者，不补生成概率。四条自然链保高度提供器及高度图、terrain_adaptation、忽略空气差别、盐/间距/分离和权重；本批无嵌套Jigsaw载荷。

独立静态核验5770项通过，涵盖本线五模板和D盘whisper修正文件全部329份载荷。源码依据、typed差异、原件及生产SHA分别在本批配套JSON中。无Java、共享语言/标签、根入口改动。本线不运行Gradle或游戏，由主线固定全src/main输入后统一构建。

## 待统一验收

主线接收whisper修正后统一编译和资源验证，检查结构与群系解码、六笼实体、七处漂浮、两掉落及八经验球。全部代码完整和无简化复审完成后，再由主线安排子代理做最终服务端/客户端功能及Render Preview原件对照；当前服务端独立测试不联系干预、不标通过，客户端及渲染仍待验。本批静态检查不能证明自然实际分布、运行性能或渲染一致。

当前：所属生产与静态检查完成，交付清单后停笔，等待主线集成及统一构建；完整迁移的最终功能和渲染验收未完成。
