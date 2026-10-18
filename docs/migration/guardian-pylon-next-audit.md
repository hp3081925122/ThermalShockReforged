# Guardian Pylon接续审计（迁移前）

主线已读原GuardianPylonEntity、Model、Renderer、Layer及其三个直接Procedure。实体为Monster/Geo：330HP/0甲攻击速度飞行速度/16跟随/持久/0XP/0台阶，FlyingMoveControl10/true和FlyingPathNavigation、无目标goal、强制无重力、无坠落伤害、IN_FIRE/DROWN拒伤。Texture默认surge_pylon并NBT保存，primary_pylon模型/动画、0循环+4tick过渡、透明本体和surge_pylon eyes层、0阴影/scale1/死亡不翻转。原每tick refreshDimensions常量scale1可消除，不删实际动画/光层。

原每tick在自身makeStuckInBlock(AIR, .25,.05,.25)，遍历(x,y+22,z)为心inflate40立方体的全部Entity并排序，实际仅给Player服务器NO_BREAK100tick/amp19/不可见。应仅查询相同范围Player且显式true保旁观者语义、不排序，内部100tick建造锁代替隐藏药水并保持尾随时长/保存/死亡边界；不能以20tick轮询缩短原触发覆盖。状态写入与AqeronEvents的外维度建造禁用须同批闭合，不造占位effect。

hurt最先施加REGENERATION20tick/199（甚至之后IN_FIRE/DROWN失败路径）；这不是直接无敌，virustone可一次generic330伤害杀死原330HP柱子。禁止直接改为永久无敌；须核原效果实际恢复节律/可见粒子、治疗/伤害事件、默认属性后确定合法内部重构。die调用完整BigBossDeath：即时+每4 END共15次EXPLOSION，原坐标+eyeheight/1粒/offset2,3,2/speed0，mob_death_explode HOSTILE2/2。tickDeath60移除再发SparklerbulbUpdateTick；原后者粒子固定世界(.5,.5,.5)很可能坐标误用，但须单列确认修复，不能偷偷减少5 END_ROD/40%conduit sound。

右键读取source主手virustone（原不消耗），播dragon.death AMBIENT1/3，直接generic330伤害。80 END后给当时维度玩家快照显示原Sopel文字，所谓windchime只在SERVER队列的isClientSide分支，实际不响；120 END后end_portal.spawn AMBIENT1/-3，扫描横半径79/竖29椭球，把sanctum_light换red_sanctum_light并保可用属性；capsule按blockstate1..4生成SanctumErrant/FrostilyteGunner/Kilolyte/另第四原实体并随机yaw，然后销毁capsule。原无交互领取锁与发起成功条件，可能重复触发；须核原完整分支/结构来源，闭合实际四实体与capsule状态再实施，不能只做屏障锁和动画就宣称完整。

原159×59×159笛卡尔检查约149万候选位置，存在明显首次卡顿成本；后续优化考虑chunk/section palette快速排除和保原处理顺序/范围/属性，不缩范围/少开胶囊、不跳原需加载的区块、不异步写世界。有限持久值任务替代捕获死柱/玩家/Level的全局闭包，并按死亡后合法后续任务语义幂等，不因柱子60tick移除删去80/120tick阶段。

待只读子代理：原注册尺寸/颜色、四实体真实ID及目标覆盖、相关物品/方块状态实际实现、正常获取/生成来源、引用Procedure及所有同类重复触发链、模型资源哈希。之后主线编写完整代码，冻结给子代理编译/机械核；全模组完整后两端/Render Preview测试。
