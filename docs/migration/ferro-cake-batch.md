# 第41批：真实Ferro Cake及完整分段食用（代码与静态阶段完成）

为Ly商店的真实商品依赖，迁原FerroCakeBlock/TileEntity/Display/Model/Renderer与FerroCakeOnBlockRightClickedProcedure，而非注册普通空BlockItem。原四次自动食用不等于原版Cake六次点击，不能套原版CakeBlock删原时序。原块水平朝向、animation0..2、WOOL/硬度1/抗爆0、noOcclusion/非导体/DESTROY活塞反应/noLootTable、(1,.1,1)至(15,7,15)碰撞、9槽自动化/比较器与容器移除掉落保留；补合理灰色MapColor供Xaero，真实地图仍待验。Geo yoylecake/texture yoylecake4/动画与透明Item/BE渲染保留。

原slicecount双精度值!=1时可开启，先存1锁，然后honey_block.break中立音量1/pitch1、nomnomnom音量.8/pitch1、animation1、食物+3/饱和+3并给真实Resistance260tick/amplifier1/默认可见。该Resistance为正常食品增益，不能误当内部假无敌删除。此后嵌套5 END/5 END/5 END，再各+3/+3与honey pitch2/3/4，末次先清空气flags3再pitch4/玩家burp1/1及食物。共四脉冲/12营养/12饱和，满饥饿仍可点击；不添加原来没有的饥饿前提。

修原直接setFoodLevel/setSaturation可超过原版上限：用原生FoodData.eat(3,.5)，保低于上限时每脉冲+3/+3，按正常20饥饿/饱和不高于当前饥饿限制。明确这只是修越界，不宣称原高于20时数值完全等价。

原全局闭包捕获玩家和Level且无目标身份，最后直接setBlock AIR会删除15 END内被替换的新方块。目标以原BE slicecount占锁、独立不可召唤MISC有限消费载体保存ownerUUID/原维度/方块锚点/阶段/5 END倒计时，复用已有ServerEndTickTasks只保存UUID的END调度。初始分支先占锁，载体加入失败则回退，不发免费增益；有效阶段先消费再执行。最后只移除仍带本次消费UUID的原蛋糕，防误删新方块。玩家离开原坐标或切维不改变原声位置，营养按UUID找当前合法活玩家；离线/死亡不捕获旧Player对象补营养，原声音和最后消费可继续。块被提前拆除时不吞剩余声音/阶段，载体卸载保存并在加载后续行，服务器重启亦续行；不强加载区块或留下永久任务。与旧全局队列在区块卸载期间仍计时的差异明确记录，最终边界测试再验。

配方/自然来源与Ly贸易后续整链核对，不让本批普通item冒充Geo食品。整模组代码未完成，只有固定快照静态核对/编译，最终两端/Render Preview继续由Luna负责。


生命周期补证：目标PlayerList.respawn会把旧实体ID复制给新ServerPlayer，不能单用owner entity ID判断同一次生命。载体保存实际Stats.DEATHS计数快照（ServerPlayer.die会累加），只给同UUID且死亡计数未变化的活玩家继续营养；支持正常跨维/存档重载，避免立即重生后把旧消费阶段补给新生命。此为源码定义的边界，最终实际存档/重生测试仍待完成模组。


原正常获取链已核并补：原shapeless misc配方9输入槽/8唯一ID（loque_milk×2、vermillion_roots、starfruit_item、ferrous_rock、barbubble_melon_slice、wheat、egg、cobalt），实际依赖均已注册；导入目标recipe单数目录，仅命名空间与result.item→id转换，不改配方与桶容器余物。原inventory_changed进度yoylecake（持有数量正好1、3XP）及parent ferrous_get（ferrous_rock1..64、无XP）一起恢复，父loqendia_get已存在，完整icon/文字/显示字段保留。原11资产=8二进制内容与3命名空间转换JSON；不把未来Ly交易当原配方替代。


最终验证收口：Luna固定快照Java21实际根执行compileJava compileGametestJava processResources --offline，BUILD SUCCESSFUL/exit0/54秒，只有既存弃用/unchecked警告，日志D:\AI临时文件\ThermalShockMigration\fortyfirst-ferro-cake-compile.log。四食用脉冲/三层各5 END嵌套、原声音/260tick真Resistance、状态域/9槽、8文件原字节SHA与3规范化JSON、原配方和两进度父链均核对。5 END计数包含触发当tick的END，不写成完整5个未来tick以免误判一tick偏移。原两FoodData setter不限幅，目标原生eat确认正常+3/+3且合法范围截断；PlayerList重生复用entityID/同UUID统计和ServerPlayer增加DEATHS已核，生命周期快照有效。退款、移除只作用于同BE/同消费UUID。以上是源码与编译证据，不代表卸载/重启/多人点击/渲染实测。Ly完整商店仍下一批，整模组未完成。
