# 第135批：既有97份结构模板剩余格式消费者复审

本批在 `codex/batch135-template-format` 上独立调查、修改、验证和本地提交，基线为 `fa12248`。仅修改9496401已有97份模板中的 `vilebath_castle.nbt` 与 `vilebathrubicon.nbt`。第132批67份新增模板按生产清单排除。

## 确证修复

两份模板各27面旗帜的第二层图案是原件 `rud`。原Forge 1.20.1的 `BannerPattern` 将此缩写注册给 `DIAGONAL_RIGHT_MIRROR`；1.21.1 `BannerPatterns` 将同一图案注册为 `minecraft:diagonal_right`。早期转换实际写成了有效但视觉不同的 `minecraft:diagonal_up_right`，因此不会依靠Codec报错自动发现。

54处只改图案字符串，保持图层顺序、颜色、旗帜方块状态与位置。两份模板已是DataVersion 3955，本批保持此版本。没有删除或替代任何剧情、库存、实体或几何。

## 当前载荷核对

全97份共4,380,315条方块记录。全部当前字段已递归盘点，包括实体、方块实体、嵌套库存及刷怪笼，不以旧脚本潜在缺陷替代实际载荷调查。

- 40个真实非空栈的 `count` 均已为目标格式，数量与原件相同；装备、掉落物、展示框、箱子、书架和讲台中的实际栈均纳入。另8个大写 `Count` 属于经验球，目标 `ExperienceOrb` 仍读取该键，不能改成物品字段。
- 3本可写书共8页，逐字与原件一致；`WritableBookContent` 的 `Filterable.codec` 支持直接字符串页，当前格式有效。4本附魔书的 `stored_enchantments.levels`、等级及原附魔ID对应原件。16个损伤组件与原件一致。
- 仅4份 `custom_data`，内容全为 `geckoAnim:""`；没有原生书页、附魔或名称被藏入其中。97份内没有纸张或成书栈，不把132批纸张结论重复算作本批修复。
- 64处 `patterns` 字段共231层，54处为上述确证错误；其他3面有图案旗帜的15层及7个空图案列表保持原语义。另2面无图案字段的旗帜不需补写。
- 303组属性及113组配置交集与出生生命周期复用 `attributes-a122-*`、`attributes-main123-*` 的闭合证据。旧无效 `forge:step_height` 等既有判定不重新改名为有效属性，避免改变原行为。

17块告示牌的正反面各4条JSON文本保持完整，使用目标 `SignText` 接受的 `front_text/back_text`、messages、颜色与发光字段。16幅画的variant使用新版字段，包含10个模组画作ID、5个原版画作ID（flowergirl出现两次）；只读子代理已核10个模组画作定义、asset_id与对应纹理均存在，5个原版画作均由目标PaintingVariants注册。7个饰纹陶罐的四面均为原心形陶片，`sherds` 仍有原生读取器。12个刷怪笼的 `SpawnData.entity` 包装与空 `SpawnPotentials` 符合目标读取器。唯一唱片机位于machinist_lab/blocks/10812，原本没有RecordItem且三个旧播放/计时值均为0；目标仍为空机，保留无效零元数据不损失播放状态，不凭旧键名制造修复。

第一轮其他消费者检查曾错误假设画作variant只能为minecraft命名空间，脚本因此断言失败。实际模板含合法模组画作ID，已撤销该检查假设并重新提取完整取值；未据此改动任何生产载荷。失败日志 `consumers135.log` 与成功重跑 `consumers135-retry.log` 均保留。

## 不透明载荷与边界

第三方Structure Gel、Curios、Balm、Embers等原字段原样保留，不猜测新版别名或假定原生已消费。97份中33份仍为DataVersion 3465；其原生结构入口会按版本走DataFixer，本批不因旧版本号本身批量重写。已手工迁移的64份仍保持3955，避免混合格式被重复升级。原未注册对象沿既有延期契约保留，本批不新增注册、不删除实体或库存。

原件、流式解析器、生产/验证脚本、全部97份修改前快照及日志位于 `D:/AI临时文件/ThermalShockMigration/templates135`。原件哈希、实际物品组件及54条修正路径见 `templates-batch135-source-evidence.json`。其他原生消费者值与最终验证结果另见本批JSON报告。

本批不启动服务端、客户端和Render Preview。格式静态闭合及构建通过不代表游戏、实际放置或视觉对照验收通过；最终由主线在完整代码和无简化复审闭合后安排。

## 验证

全97份修复前后进行原始解压字节往返及全typed允许差异核验：52,083个typed节点、4,380,315条方块记录、1,148份方块载荷、341个顶层实体，仅允许证据所列54处字符串变化。95份既有模板逐字节不变，第132批67份模板与其生产清单哈希一致。palette属性与顺序、size、DataVersion、实体库存及UUID均保持原样。

Java21离线 `compileJava processResources` 成功，耗时7分8秒，24任务中6项执行、18项up-to-date；有基线已有的7条弃用警告。只将A133已缓存的官方1.21.1 metadata复制到本worktree的.gradle目录，未修改共享构建配置或联网取版本。8,947份src/main固定输入在构建前后相同，8,127份直接资源与build/resources/main逐字节相同。结果见 `templates-batch135-build-validation.json`；源格式盘点、工具与日志哈希见 `templates-batch135-audit-summary.json`。
