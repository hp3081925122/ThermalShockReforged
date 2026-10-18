# 第57批预审：Desk Director与Super Cryonic Cache

原orb_sanctum依赖仍有真实DeskDirector块/BE与8个SuperCryonicCache，不能用空注册或删模板内容交差。主线已读原两Block、DeskDirectorOnBlockRightClicked、SuperCryonicCacheOpen、SopelIntroEpl1Menu/Screen、SopelGUIOpenTick及首屏onOpened，完整页链/依赖清单由Luna只读核对。

DeskDirector是方向Geo方块/显示物品/BE，animation0..1、STONE、强度−1/3600000、noOcclusion/非红石导体、透天光/0光阻；原三格横向大桌轮廓box(-16,.1,-16,32,15.9,32)，各朝向轮廓一致但模型方向仍保留。移除掉真实库存、fallback自己一块，无普通加工合成。右键实际打开SopelIntroEpl1，而非普通九槽容器。原界面276×182，speechbox(0,104)276²、labelbox(62,-29)134²，首屏Sopel肖像(-30,-23)128²/128×1536图集，帧索引来自误名WhisperMainOpenTick；首屏7条原红字及(237,162)38×20原Next atlas不能删。

原SopelGUIOpenTick只递增共享WhisperSpriteIndex，每次+.125、>12后归0，屏幕帧是int索引×128截0..1408；需要逐页核真实肖像/声音/文本/按钮/状态，改会话局部时钟，不让多玩家开GUI叠加控制全服的同一个静态变量。不能只改Sopel名字便复用已经迁入NPC的不同剧情。翻页采用服务器验证当前菜单及真实锚点/距离/维度的原生菜单按钮，拒绝任意坐标伪造；不注册每页两端重复执行的网络消息。

SuperCryonicCache原无trap分支：只在主手CryonicKey时destroy(false)、扣1（含原创造模式）、glass.break NEUTRAL .5/-4、CHEST上下文在块中心spawn原blocks/super_cryonic_cache表；非key及起挖走同原locked hint/BASS声音。源先destroy却不检查成功导致失败也收费给奖励，目标应先检查完整表与真实块/BE、原子claim、成功移除后再收费/给奖励，失败不吞key也不复制奖励。原属性BASEDRUM/AMETHYST_CLUSTER、−1/3600000、light12/noOcclusion/pushBLOCK/非导体/noLoot、box(0,0,0,16,28,16)。两0过渡Geo控制器、真实9槽及全部loot/物品来源须闭合。

目标已有CryonicCacheBlock/CacheBlockEntity的有限claim与真实原生loot机制可复用；若参数化共享，只允许普通CryonicCache保其原trap分支，不能把Super也变成可无key诱发的原没有陷阱。共用的既有普通cache行为和奖励不得改变。

完整代码/资源冻结后委派Luna离线三任务及全页/资产/loot机械对照；整模组完成前不运行两端或Render Preview。其它ProtoiSunbeam与嵌套Icidrogrunt、完整模板和其它未迁模块仍未完成。
