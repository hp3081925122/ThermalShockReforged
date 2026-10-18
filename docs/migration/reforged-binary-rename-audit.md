# Reforged 二进制资源命名空间审计

用户新名称 Thermal Shock Reforged / modid `thermal_shock_reforged`。审计后获root授权，仅结构化修改新路径下5份NBT；不修改原JAR、历史文档、Java、普通JSON或目录，不下载、不构建、不运行游戏。此清单记录重命名前路径；root后续移动目录后应按新路径定位，不能按旧路径重新创建副本。

## 实际扫描范围

非UTF8资源2459个：PNG2127、OGG272、EFKE效果52个.efkefc和1个.efkproj、EFKM材料2个.efkmat、gzip NBT5个。逐文件SHA256/大小、编码命中、NBT完整字符串字段、效果chunk元数据与路径样本写入 `D:/AI临时文件/ThermalShockMigration/reforged-binary-audit.json`。当前没有其他非UTF8扩展类型。普通UTF8资源归root文本替换，不在本代理变更范围。

## 必须结构化替换：5份NBT，共30字段

|重命名前实际相对resources路径|真实字段|原值|数量|
|---|---|---|---|
|data/thermal_shock/structure/planted_barbubble.nbt|$.palette[0].Name、$.palette[1].Name|thermal_shock:barbubble|2|
|data/thermal_shock/structure/vermillion_pine_1.nbt|$.palette[0].Name|thermal_shock:vermilli_log|1|
|同上|$.palette[1..6].Name|thermal_shock:vermilli_leaves|6|
|data/thermal_shock/structure/vermillion_pine_1b.nbt|$.palette[0].Name|thermal_shock:vermilli_log|1|
|同上|$.palette[1..6].Name|thermal_shock:vermilli_leaves|6|
|data/thermal_shock/structure/vermillion_pine_2.nbt|$.palette[0].Name|thermal_shock:vermilli_log|1|
|同上|$.palette[1..6].Name|thermal_shock:vermilli_leaves|6|
|data/thermal_shock/structure/vermillion_pine_2b.nbt|$.palette[0].Name|thermal_shock:vermilli_log|1|
|同上|$.palette[1..6].Name|thermal_shock:vermilli_leaves|6|

root目录迁移映射（文件名不变）：`data/thermal_shock/structure/{planted_barbubble,vermillion_pine_1,vermillion_pine_1b,vermillion_pine_2,vermillion_pine_2b}.nbt` → `data/thermal_shock_reforged/structure/`下同名文件；完整项目文件夹稍后由root改名为`1211-neoforge-ThermalShockReforged`，本代理不提前切cwd或重建旧目录。assets/thermal_shock → assets/thermal_shock_reforged仅由root移动，媒体原字节保持。

5份实际均gzip magic1f8b，解压为Java大端NBT，TAG_String含unsigned-short字节长度与字符串payload。审计解析全部TAG及所有命名字段/字符串值，并确认读到payload末尾；旧命名空间仅出现在上表30个palette Name值，没有包含旧namespace的entities/id、方块实体LootTable、物品id、指令或嵌套JSON字符串。此结论只针对当前已经导入的5结构，原JAR其他未迁结构不属于已迁资源，未来导入必须同规则扫描。

可靠实施机制：root授权后重新定位这5份当前路径并对比审计SHA256；若内容/路径已变化先重审。gzip解压→按NBT tag递归读取并保留类型与原顺序→只改TAG_String中精确ResourceLocation前缀thermal_shock:为thermal_shock_reforged:→重新编码该字符串长度→保留其他tag payload→gzip重新压缩。可以用已核目标NbtIo/NbtUtils的NBT库，也可以使用保留所有原始tag片段的完整递归序列器；不能对gzip或解压payload直接bytes.replace，因为新namespace长9字节，字符串长度必须同步修改。

验收必须重新解压解析，30旧值归零、30新值存在，除这30个字符串值外NBT结构深比较相同（尺寸、方块索引、Properties、实体、DataVersion均不改）；gzip容器的时间/压缩字节变化不作为语义变化。原件或备份只放D临时，项目中不留同ID旧namespace结构。目录迁移由root负责，本代理不得同时rename目录。

## 效果与材料：当前无需二进制改写

52个efkefc与boss_death.efkproj都使用EFKE头，前8字节后逐chunk读4字节标识+little-endian uint32长度，完整遍历INFO、EDIT、BIN_。EDIT是zlib压缩的Effekseer工程内部编码，已实际解压后再次检测UTF8/UTF16LE/UTF16BE旧namespace；INFO/BIN_原始也检测三种编码。全部零命中。不能仅对压缩原文件rg后就宣布没有引用。

2个efkmat为EFKM，16字节header后DESC/PRM_/PRM2/E_CD/GENE/DATA等长度块。全部chunk按长度遍历到EOF，UTF8/UTF16三种编码旧namespace零命中。DATA包含材料JSON但属于该二进制长度容器，不能当普通独立JSON直接替换。效果资源路径实际以Texture/Material等相对文件路径为主；整体assets目录迁移后相对关系应保持，当前不需把它们伪改为namespace路径。

若未来新导入效果出现旧namespace，必须用Effekseer对应版本解析/导出或精确格式序列器重建相关字符串、chunk长度及压缩流；EDIT、INFO和运行BIN_可能重复存同一资源信息，不能只改一个区段。当前无需承担这种未经证实的格式改写风险，保这55个文件原字节。

## PNG与OGG：当前无需改媒体内容

2127张PNG原字节三编码均零命中；另解析PNG文本元数据tEXt/zTXt/iTXt，实际16文件有文本chunk，压缩文本解压后仍零旧namespace。未把像素IDAT流当资源定位字符串。272个OGG三编码原字节扫描零命中，Vorbis可见注释/名称没有旧namespace。声音/贴图位置引用由普通sounds.json、模型、AAA preset JSON与Java由root处理，不在媒体二进制里添加字符串或重编码媒体。

## 审计产物与状态

- D:/AI临时文件/ThermalShockMigration/reforged-binary-audit.py：全部非UTF8扫描、NBT递归字段与EFKE/EFKM chunk审计。
- D:/AI临时文件/ThermalShockMigration/reforged-binary-metadata.py：补PNG压缩文本与效果资源路径样本。
- D:/AI临时文件/ThermalShockMigration/reforged-binary-audit.json：完整逐文件结构化证据。

已获root授权并完成新namespace路径下5份NBT、30个palette Name字符串改写。逐文件gzip解压、完整NBT tag递归重写字符串长度、重新gzip；更名后payload将新namespace结构化反向恢复旧namespace，逐字节严格等于原payload，证明除30个命名空间字符串及对应长度外全部NBT原始字节不变。重新解析完整到EOF，旧namespace零命中。其余2454个非UTF8资源在assets/data目录迁移后逐一SHA256与审计基线一致，改写前后均检查通过。

执行脚本D:/AI临时文件/ThermalShockMigration/reforged-nbt-rewrite.py，结果日志reforged-nbt-rewrite-result.json，5份原NBT备份在reforged-nbt-before目录；均位于D临时根。未改Java/普通JSON/目录或其他资源。项目根仍为1211-neoforge-ThermalShock，等待root统一移动。尚未游戏加载验证，也不把静态命名空间零命中当渲染/结构生成成功。
