# 第十四批 Thingamabob 与空投部署完整链路

实现前核原JAR注册/Item.use/Pod.finalizeSpawn字节码、原两procedure、FogCloud wrapper和AAA资源。旧ice-squad-next-audit无Pod caller结论错误，本批以原JAR纠正。Thingamabob真实注册且进入创意栏；无原recipe/loot/trade证据，不伪造生存配方。主线程写全部实现，Luna机械盘点与编译。未完整迁入全模组前不启动两端。

## 时序与权限

Thingamabob stack1/UNCOMMON与tooltip保，服务端实际使用手物品消费1（原创造也消费），不再从背包任意槽清同名物品或客户端双结算，返回成功防双手重复use。原起手坐标REVERSE_PORTAL8/偏移1/速度.2、bell.use NEUTRAL1/7，延迟22tick在BlockPos(x+.5,y,z+.5)生成Pod并置零速度，然后全服原消息§dMessage recieved!保。使用物品后请求是已消费的独立部署，用户离线/死亡不撤销，按原固定坐标而非追踪移动玩家。

Pod仅finalizeSpawn幂等启动，不从constructor或reload重复排任务，保存初始化原点、维度、age、phase。t15原位置x+.5/y/z+.5送25个FOG_CLOUD wrapper，offset2/speed0，anvil.place HOSTILE2/-4；t25固定原点dragon_fireball.explode HOSTILE1/2及magnetcrossbowfire HOSTILE1/-3，舱体真实discard；t26分别尝试生成1Gunner与1Burner，原点BlockPos(x+.5,y,z+.5)，各原额外随机yaw。

原全局queue捕获已移除舱体，目标用独立不可召唤/不可碰撞/不可见、只保值的DeploymentTaskEntity承接22tick和最后1tick，保存mode/remaining/anchor/UUID/dimension/consumed。每个任务在产生副作用前消费锁，一次尝试不重试；没有死对象闭包或全世界循环。舱体死亡/跨维取消尚未提交的阶段，t25已提交独立最终阶段后不再依赖已discard舱体；卸载重载保剩余时间，不强行加载别的区块，不捏附近Boss cap削合法一组两只。未配置的内部载体立即移除，外部召唤禁用。

## Pod与视觉

MONSTER2×3.5/fireImmune，HP300/原raw甲90/有效KBres1/speed0/ATK0/follow16/step0/XP0，无goal/常驻。保原明确伤害拒绝矩阵而非全局无敌：IN_FIRE、直接Arrow/Player/ThrownPotion/AreaEffectCloud、FALL/CACTUS/DROWN/LIGHTNING/EXPLOSION/TRIDENT/FALLING_ANVIL/DRAGON_BREATH/WITHER/WITHER_SKULL，其余原生可受伤。死亡20tick，部署自然消失不触发死亡奖励。配置增加Pod生命和甲，loaded不回血。

原drop_pod模型/动画、blizzarddroppod动态Texture NBT、透明scale1/shadow.5，无头部跟随。默认spawn循环与原4tickcontroller保，所有原动画作为可触发程序动画保留。FOG_CLOUD注册alwaysShow=true，原sprite/EFK/内部Fog3001纹理全保；wrapper .2²/lifetime7/gravity0/noPhysics/input velocity原倍率1/OPAQUE。先super.tick再无条件发AAA，包含移除当次共8次/每wrapper，25wrapper共200次实际发射；新建独立ParticleEmitterInfo防可变共享状态，不用普通烟替代、不缩数量范围。渲染实际结果仍待最终Render Preview及客户端验证。

## 门槛

Luna核原注册/模型/动画/免疫/22+15+25+26时序/25×8AAA/两个Boss各一次/真实来源和tag，Java21 compileJava compileGametestJava processResources --offline。最终测试须含重复finalize/死亡/跨维/卸载各阶段/载体保存/生成失败、创造与副手消费、双手防重、两Boss随机朝向、动画雾效音效、多人网络。

获取入口待办：Luna核目标当前无CreativeModeTab/BuildCreativeModeTabContentsEvent注册，故Thingamabob虽已可用注册/命令获取，尚不能声称目标创意栏入口已保留。原唯一确认常规入口是原模组创意标签页；下一批优先迁原完整创意分组与已有内容入口，不以捏生存配方绕过。原库存链复核：ContainerHelper把匹配且清空的槽替换为ItemStack.EMPTY，旧GameMode的创造恢复只修改已脱槽旧对象且不重挂原槽，因此全包消耗总数仍1，但可能消耗错槽；本次实际手消费包括创造保该总量并修错槽。

AAA数量是正常完整生命周期下25×8的静态控制流计数；客户端粒子上限/提前清理与实际渲染帧仍须真实环境验证，不能把静态计数当已观察到200次渲染。

后续第十五批已接入原两创意标签页，Thingamabob真实注册并按原顺序进入主页面。此前“获取入口待办”记录为第十四批当时状态；入口接线现已完成静态核对与编译，实际界面和功能仍待整模组完成后运行验收。

存档边界补强：创建tick门槛createdAt/startedAt也写NBT，保持同tick复载时不提前推进；常规服务器下一tick复载本就先推进gameTime，本项不冒称已观察到少一tick。若外部事件拒绝内部任务加入，尊重取消并记录debug，一次尝试不无限重试；不绕过外部拒绝直接补刷两Boss。最终测试需分别验证正常创建与拒绝分支，静态构建不证明时序运行。

Luna最终Java21离线compileJava/compileGametestJava/processResources通过，17秒（初次47秒）；两个创建tick字段保存复核通过，其余有限机械核见fourteenth-deployment-static-validation.json。原资产哈希相等，调用来源已由JAR字节码纠正。未启动两端或Render Preview，创意标签页入口及整体迁移仍待完成。
