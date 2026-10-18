# 第46批：Capsule与Virustone完整前置（迁移前契约）

先闭合Guardian Pylon的真实依赖，不把柱子/胶囊开场或四种生物标成已完成。原CapsuleBlock/TileEntity/Model/Renderer及CapsuleOnBlockRightClicked为依据：blockstate0..4、animation0..1、四向水平facing、waterlogged、形状(1,.1,1)..(15,47.9,15)、BASALT声/硬度-1/抗爆3600000/正确工具/noOcclusion/Push BLOCK/不导红石/无光。九槽六面自动化、两0过渡动画控制器、0循环/非0播完复位，切variant重置主控制器；无右键开库存/无比较器。保完整pillar_statue Geo/animation、五capsule纹理、item默认capsule纹理及原displaysettings。目标共享Geo onRemove掉库存修原丢失，不统一抹掉九槽能力。

原创意右键仅把当前blockstate加1且范围内才写，0→1→2→3→4之后卡住；目标服务器权威0..4循环，修创作切换不可回0，正常生存右键仍无动作。WATERLOGGED按原放置/更新规则，不丢朝向或水源，渲染透明且物品/方块均真实注册到原创意页。

VirustoneItem原stacksTo1/EPIC/原description_0文案，用在方块时实际调用同KeystoneRightclickedOnBlockProcedure，没有item==Keystone限制。复用已迁KeystoneItem的真实Atlas入口和原自身冷却类别，不造仅装饰物品；提取继承为两个实际调用者，Keystone原行为不变。tooltip以目标TooltipContext签名实现，原图/模型/语言保留。取得来源和配方由Luna沿原引用核，不凭物品名称造合成。

固定快照由Luna离线compileJava/compileGametestJava/processResources及原件机械对照。全模组完整后测试五外观/动画/水含/创意循环/九槽保存、两个物品分别激活Atlas和自身冷却/满包等。Guardian Pylon实体、内部禁造锁、80/120 END有限剧情和四胶囊生物依赖仍继续下一阶段，不用空注册替代。


来源补核：原Virustone并非无生存来源，它由BackdoorScarabAssembler在FRIGID_ALTAR_KEY/ZAP_GAUNTLET/STORM_BATTERY/KEYSTONE各至少1的条件分支输出，并授予icebreaker_2。原icebreaker_2→icebreaker_1→errands_get父链中，根errands_get目标已存在，前两项现补原impossible/task/hidden/toast/announce/title/descr/图标、icon.item转1.21 id。QoPageing与ConcErrandsPageOpen/QoAlloyErrandAccept是原NPC的额外解锁入口；未迁writer明确留其完整NPC/assembler模块，不能以补JSON假装已解锁/获得，也不改impossible为自动领取。图标virustone/keystone/frigid_altar_key均真注册。先前子代理初报三adv皆缺，现已纠正为只缺两个，未覆盖现有根。

Guardian依赖事实补齐：四胶囊生物为sanctum_errant/frostilyte_gunner/kilolyte/sanctum_ripple，现均缺实际实体；Guardian无命中原模板或正常自然生成，确认入口为创意刷怪蛋。NO_BREAK NEUTRAL/无可治愈物品/无tick动作/隐藏，不保留假状态effect；原Regen199在Java移位199&31=7后50>>7=0，因此每有效tick最多heal1，20tick共最多20，不是20tick一次。后续不能改成无敌或误判成每20tick治1。


冻结验收：Luna核实全部上述参数、五纹理/模型/动画、原物品图及10帧2tick mcmeta字节一致，注册/真实创意入口和两个实际Item各自100tick冷却通过。Java21离线三任务18.8秒成功，增加2adv后仅processResources离线40秒成功；两个进度source/build哈希一致且转换外逐字段同原件，现有errands_get不覆盖。详见fortysixth-capsule-virustone-static-validation.json。下步按sanctum-capsule-mobs-next-audit.md迁四真实实体，然后闭合Guardian；本批无游戏实测。
