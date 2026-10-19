# 第102批候选：原生方块行为预检

第101批生产期间仅调查和准备，不在其统一编译前启动下一批生产。原件、目标环境和分工原则沿用parallel-batch101.md；测试聊天由用户负责。

Luna的native-block-candidates102.json记录16个候选的原注册、BlockItem、procedure直接调用和原JAR资源引用。目标注册复核另存target-block-candidates102.json；两份临时报告均在D:/AI临时文件/ThermalShockMigration。未找到目标注册不直接等同于整个功能未迁，最终分配前结合真实实现确认。

## 主线已读原行为

- SparklerBulb与已迁HangingSparklerBulb的破坏过程相同：玩家采集、爆炸和投射物三个入口，真实power1/BLOCK爆炸加20颗END_ROD，偏移1、速度0.3。可复用已有SparklerDetonations的迭代排空，不另造递归爆炸过程；原客户端显示40%导管短环境声保留。显示procedure的5颗END_ROD仅写在ServerLevel分支，实际调用者只有客户端animateTick，该分支原本不可达，不能误称原有每帧粒子已经复现或凭名称加出新效果。原随机更新属性没有额外tick覆写。
- StratosilverBlock、StratusAlloyBlock共用相同放置粒子过程：10颗ELECTRIC_SPARK、方块中心、偏移0.5、速度0.04。两者均硬度2、抗爆10、金属声、正确工具掉落，水平facing与floor/wall/ceiling的face属性决定完整原模型朝向；不能缩成无方向材质块。原onPlace也可能由同块状态变化触发，是否保留需对照调用链，不直接假定只放置一次。
- GlitteryShale为原STONE地图色、下界岩声、硬度1/抗爆5、亮度3、正确工具掉落；客户端每次6%概率在中心顶面发一颗原GOLD_GLITTER，零速度。原说明文本、完整loot及真实自然生成引用一起闭合，不以新普通方块替代原粒子。
- ConstellationBlock保六方向facing、blockstate整数0..6、全立方碰撞、硬度2/抗爆30、下界合金声、正确工具掉落、全部有效emissive/postprocess、原说明。状态0不发光，1..6亮度15。原主手Chisel交互只循环0..5，第6套资源不能由正常交互进入；后续如修为0..6循环，属于需写入REFORGED_CHANGES的玩法修正，不能伪称原循环如此。
- LunarPanel是实际能量功能，原白天且下方无面能力可接收时向其UP面送5FE，否则送150FE。原回调频率及能力面不一致需继续核Block调用者和目标NeoForge能力源码后决定，不按名称猜成太阳能板，也不能列为外模组延期。
- Icebreaker保放置呼吸声、20个SERVER END的铁砧声与10颗舒适篝火烟、半径9球内普通ICE转WATER、各被改方块70%原冰碎裂效果；30 END消耗原块加10颗POOF。下一批应复用已确认的有限任务与激活归属机制，防旧任务误删新块，同时保原体拆除后的合法尾阶段。范围不能缩小，不能把所有冰种擅改为水。

## 下一批划分建议

主线承担一组较短的原生显示、方向与交互方块，迁移A承担Icebreaker/LunarPanel的完整有限任务和能量链，开批时按最终候选证据冻结独占目录和共享增量。净化器与雨鼓尚在第101批由A独占，不在此重复实现。

上述是源码预检，不是第102批生产交付、编译或游戏验收结果。尚余BrimstoneCake、HologramMonitor及花卉等真实候选需后续按完整调用链分批覆盖，不因本文件存在就标完成。
