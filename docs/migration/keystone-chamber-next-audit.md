# Keystone自然获取链：第37批后只读预审（尚未实施）

原件07.9路径/哈希不变。后续闭合Keystone实际自然入口，不只保创造物品。已读SpawnKeystoneChamberSpawnn、SpawnForceKeystoneChamber、RedMantlestoneLeverOnBlockRightClicked、InactiveKeystonePedestalOnBlockRightClicked、KeystonePedestalOnBlockRightClicked、ReinforcedWindowKeystoneCycle，以及四原方块/Geo模型/BE。这里只记录证据，不代表已迁移。

原keystonerubicon结构为DataVersion3465、45×40×45、81000方块、63 palette状态、14带NBT块、0实体；含4 rinthium_button、1 rinthium_lever、1 inactive_keystone_pedestal、1 giant_gear及原版床/箱子/熔炉/桶。模板尚未导入，需要完整查非空库存与palette依赖，不能删未实现块或跳空气。

四功能块尚未注册：rinthium_button、rinthium_lever、inactive_keystone_pedestal、keystone_pedestal，均原Geo与9槽BE。按钮实际调用RedMantlestoneLever程序，首次active=false时先置true/animation1并播mantle_lever，原非普通红石按钮、无自动复位。lever调用InactiveKeystone程序：检测相对(14,0,7)/(0,0,7)/(14,0,-7)/(0,0,-7)四BE active，中心(7,2,0) caninteract=false，置中心真实keystone_pedestal，三个位置animation1，center caninteract=true，两声；失败bass。pedestal凭真实caninteract一次发Keystone后清false/animation2/ambient.cave和全服原文本。inactive pedestal自身也调用同程序，不把lever四角朝向/坐标改成附近扫描。所有状态应权威端先占一次锁，保持久化和原动画完整。

原button/lever animation0..1、horizontal facing、metal、硬度-1/抗爆3600000/光5；button noOcclusion/非红石导体、近满方块形；lever原满方块/有遮挡；inactive animation0..1、无碰撞、16×2×16；pedestal animation0..2、2,0,2至14,32,14；两台座光15/noOcclusion/非导体/无掉落，不能为复用当前GeoDecorBlock0..1强行删animation2或扩大所有已有块的状态域。四原Geo/texture/animation路径已核：rinthbutton/rinthlever/pedestalhole/keystonepedestal；纹理rubicon_button/rubicon_lever/pedestal。保持透明renderer，最终Render Preview另验。

重要新发现：原两个Spawn程序都带EventBusSubscriber和PlayerWakeUpEvent SubscribeEvent，已由原JAR javap核SpawnForce实际注解。普通分支检查loqendiaGenerateSpecialStructures/KeystoneExists/玩家未到Loqendia；Force分支只查KeystoneExists，因共享flag且订阅次序未知，可能旁路gamerule和玩家进度门槛并改变广播文本/OST提示。不可凭Force名字推断它是未调用工具方法，需查原实际注册顺序/目标设计后明确修复边界，不能无记录删真实入口。原两程序还在放置前设Exists，缺模板也永远消费；新实现应在真实成功后提交。延迟100再45/100 END消息和windchime位于醒来坐标，原循环按该维度每个玩家重复向相同坐标广播声音，须明确是否去重及听众语义；不把遍历循环变量误读为以每位玩家坐标播放。

尚未实施的延迟逻辑应使用归属明确、有限寿命、可持久化且幂等载体，不继续捕获Level/Entity的MCR全局闭包。保原100→45/100嵌套END时序（队列处理时机已在既有skill核实），原文本/原声/完整场景和一次Keystone获取均需闭合后才称完成。本批未启动任一游戏端。
