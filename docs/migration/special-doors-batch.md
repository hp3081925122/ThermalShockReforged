# 第九批：Maw与Bunker特殊门

原件07.9源码/JAR为证据，全部原类/BE/GeoItem/model/renderer/两个procedure原文及哈希见D:/AI临时文件/ThermalShockMigration/ninth-doors-original-facts.json。先审计再实现，不用DoorBlock替换。

## 两门完整共同性质

两门均不可破坏、抗爆3600000、禁止活塞推动、光0、遮光0、非红石导体、非遮挡；四向反向放置、旋转镜像，blockstate/animation各0..1。原全部Geo模型、透明渲染、贴图、双controller及完整0/1动画保留。两者真实9槽BE/持久化/六面handler，null面不暴露，不新增普通右键库存UI；原外部MenuProvider脱离真实9槽的27槽菜单修复为绑定9x1，破坏释放库存修复与第八批同原则。原无独有loot文件，代码fallback自掉保留。两物品原JSON实际item/generated二维图标，保原显示同时接原Geo扩展，不擅改3D图标。

## Maw

原闭门48宽×64高×16厚，开门为1高门槛与两侧2宽牙柱；东西向整体转90度，精确原坐标。任意手交互实际都检查主手HeraldKey且count>=3，闭合态才永久开门，blockstate与animation同时设1。消耗3枚钥匙（原创造也扣，不擅豁免），beacon.power_select与minecart.riding各NEUTRAL音量1/音调1。失败note_block.bass 1/1＋原英文actionbar提示；已有3钥匙且门已开则无重复消费/音效。原全库存扫描消费改实际符合条件的主手栈扣3，总成本不变，服务端单次执行。

## Bunker

原闭门64宽×48高×16厚，按方向有非对称负坐标；开门为1高地槛与1宽框架，精确四朝向。含水及流体排程完整、天光仅流体为空时透下；四向均可连接红石。原neighborChanged只有收到>0邻居信号且blockstate=0才执行，永久打开（原没有断电关闭，不新增关闭逻辑）；同时设置animation1、bunker_door声NEUTRAL1/1，按原y/x/z顺序扫描半径4的9×9×9共729位置，仅删除light_blocking_barrier，保原flag3通知。

重要纠正：原Bunker过程只删除场景预置barrier，从不负责放置；先前分组文字“放/删”不准确，以本次原procedure全文为准。保原整个扫描范围，不缩成门前面；用单MutableBlockPos和一次开启幂等门槛减临时对象，不增加全局tick扫描或无限队列。

## 动画语义

两门0/1资源均存在；1分别3.1667秒和3秒且loop=hold_on_last_frame。真实GeckoLib4.9.2 RawAnimation.thenPlay字节码使用LoopType.DEFAULT，遵从资源hold，并不是PLAY_ONCE。保原双controller、blockstate变化重置与非0结束条件，不凭名称误修成播完回闭门或删动画。

## 验证

待：编译/全部状态形状与原坐标对照、原资源递归哈希与GUI/capability/钥匙成本/单次barrier删除静态检查；整模组代码完整后再由子代理做服务端与Render Preview验收。
