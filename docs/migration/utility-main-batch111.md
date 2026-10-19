# 第111批主线：七项工具、结构物品和饮料

生产前建档。110统一构建及资源核验通过并提交d76f90e869d60cf075a902161ae6b9091f309d20，现解除111生产冻结。原件仍为07.9固定JAR、SHA256为3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436。原实现不是需求模板，只保经源码确认的完整行为。

主线独占dev_deletor、dev_rotator、hpsm、ignition_detector、micro_dome、model_train、sprintweed_soda及content/remainingutility；迁移A负责九项较复杂武器/盔甲/雨伞/炸弹和各自独占资源。根入口、语言、共享标签/事件/进度由主线集成，不共改同文件。全部脚本和临时只放D:/AI临时文件，UTF-8，Java不写注释。

## 原行为与实现策略

- 两开发工具都是单件EPIC普通Item，无消耗、无冷却/权限特殊门槛。以玩家脚部位置为中心inflate1立方查询character_mobs并按距离排序；deletor服务端discard每只并逐只在原锚点播放infestorkill，rotator让各实体EYES锚点看向玩家脚部。保原范围、过滤、声音次数，不误按球形或仅NPC单体实现。
- HPSM单件COMMON，useOn返回SUCCESS；5tick冷却，授hpsm_get进度，在点击方块整数位置播放player.breath(NEUTRAL,1,2)，发送5POOF于块中心、偏移(.25,0,.25)、速度0。shale→snowy_shale，snowy_shale→shale，grassy_shale→snowy_shale，permafrost→snowy_permafrost，snowy_permafrost→permafrost，薄雪→air；完整保可兼容属性，不误处理完整雪块。原即使无转换也有声画/进度/冷却，目标保留。
- Ignition Detector单件COMMON，100tick冷却，原五标签按-2、-1、0、1、2优先级报告，未匹配Irrelevant。完整保颜色、换行、原警告和提示，音频源原拼写igniton_detector.ogg通过已注册ignition_detector_click保留；原仅客户端本地播放，目标保一份持有者本地声音，不服务端广播给全场。
- Micro Dome与Model Train单件EPIC，右键分别在当前维度固定(316,53,299)放完整shalespring_august和shalespring_trainstation，不旋转不镜像、不忽略实体、flags3。原提示X330 Z330即结构语义中心，保原提示，不偷换玩家位置或截模板。Micro成功后设置主世界共享ShalespringExists；Train没有该进度。完整模板/数据依赖须先核，不因目前自然模板已迁就漏掉Train。原无gamerule/生存来源以外权限限制，不无据新增。
- Sprintweed Soda单件COMMON，20tickDRINK，完整真实速度I持续1500tick并返corrial_flask。原按ID从全库存扣除且对非玩家不消耗，目标改为服务端消费实际饮用栈且只成功返一瓶，保创造玩家原明确消耗语义，防喝副手扣主手和客户端/服务端重复业务。

两结构原为同步完整placeInWorld，有明显单次生成成本；保持完整几何/实体和原时序，不用删块或截实体来降低耗时。原先扣任意同ID库存、缺模板也标进度的路径要修为加载有效模板、成功提交后只扣实际持有栈/设置进度；失败不扣费。结构仍可重复用材料放置，不能凭主世界进度擅自禁止再次生成。

## 验证与测试边界

先确认原Item入口/所有procedure、目标API与现有资源，再生产；记录合理差异到REFORGED_CHANGES。完成后交独立机械核值、源/目标/资产哈希与真实来源；等A完工固定全src/main后统一编译/资源校验，不逐小改编译。当前不启动游戏，完整代码与无简化复审后子代理客户端及Render Preview对照；服务端独立线不联系干预。

## 本线生产及已核来源例外

七物品在RemainingUtilityContent真实注册，六个新Java类按职责分开；完整四配方及hpsm_get进度新数据已接，全部原14份模型/纹理核对相等，原英文提示逐字符保留，中文新增仅真缺键。dev工具与HPSM只由服务端执行世界业务与广播声音，避免原双端调用重复声画/修改；点火探测器仍是持有者客户端本地音。结构成功返回已处理结果，避免同次交互继续尝试另一手导致再次生成；消耗仍含创造玩家，不新增原无的权限/冷却或一次性限制。

点火探测器原代码查询ignition_-2/-1/0/1/2，但固定原JAR没有任一标签定义。当前完整保原优先级、六分支消息/颜色/换行、100tick冷却与原音频，允许数据包以后提供真实标签；原默认只能走Irrelevant是原生数据事实，不猜分配群系、不造空标签，也不冒充所有评分在原默认即可触发。

Train模板已完整迁入，原SHA7fedab18bc0d2332201c1913d3c45997c4f933760c7dfdf23ce15f60e24f797c，目标001093684a1010f7a99a9f3ae432979d923c59306f617ff19fe481c5287bc0f2。30×24×70、50400方块记录、54palette、33模组方块、零实体，三处BE均空Items和空ForgeCaps；只改命名空间与目标DataVersion，所有typed记录和原几何保留。旧空caps逐类型不动，不把外cap数据保留说成外模组兼容。已有343000记录/六实体的Shalespring完整模板保持110 HEAD字节哈希不变，不覆盖108/109已完成的书/附魔/属性转换。

本线13独占生产文件已停笔并交独立核值，含六Java、四配方、一进度、一Train模板与世界进度精确增量；根入口/两语言及A共享增量在最终集成时统一固定。此处生产和静态证据不是编译或游戏通过，最终总报告以main111-static-validation为准。
