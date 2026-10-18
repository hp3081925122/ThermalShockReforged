# Guardian Pylon接续审计（迁移前）

主线已读原GuardianPylonEntity、Model、Renderer、Layer及其三个直接Procedure。实体为Monster/Geo：330HP/0甲攻击速度飞行速度/16跟随/持久/0XP/0台阶，FlyingMoveControl10/true和FlyingPathNavigation、无目标goal、强制无重力、无坠落伤害、IN_FIRE/DROWN拒伤。Texture默认surge_pylon并NBT保存，primary_pylon模型/动画、0循环+4tick过渡、透明本体和surge_pylon eyes层、0阴影/scale1/死亡不翻转。原每tick refreshDimensions常量scale1可消除，不删实际动画/光层。

原每tick在自身makeStuckInBlock(AIR, .25,.05,.25)，遍历(x,y+22,z)为心inflate40立方体的全部Entity并排序，实际仅给Player服务器NO_BREAK100tick/amp19/不可见。应仅查询相同范围Player且显式true保旁观者语义、不排序，内部100tick建造锁代替隐藏药水并保持尾随时长/保存/死亡边界；不能以20tick轮询缩短原触发覆盖。状态写入与AqeronEvents的外维度建造禁用须同批闭合，不造占位effect。

hurt最先施加REGENERATION20tick/199（甚至之后IN_FIRE/DROWN失败路径）；这不是直接无敌，无前次无敌帧干扰时virustone的generic330可杀死满330HP柱子；此前受伤并回血后可能只补差伤害而未死。禁止直接改为永久无敌；须核原效果实际恢复节律/可见粒子、治疗/伤害事件、默认属性后确定合法内部重构。die调用完整BigBossDeath：即时+每4 END共15次EXPLOSION，原坐标+eyeheight/1粒/offset2,3,2/speed0，mob_death_explode HOSTILE2/2。tickDeath60移除再发SparklerbulbUpdateTick；原后者粒子固定世界(.5,.5,.5)很可能坐标误用，但须单列确认修复，不能偷偷减少5 END_ROD/40%conduit sound。

右键读取source主手virustone（原不消耗），播dragon.death AMBIENT1/3，直接generic330伤害。80 END后给当时维度玩家快照显示原Sopel文字，所谓windchime只在SERVER队列的isClientSide分支，实际不响；120 END后end_portal.spawn AMBIENT1/-3，扫描横半径79/竖29椭球，把sanctum_light换red_sanctum_light并保可用属性；capsule按blockstate1..4生成SanctumErrant/FrostilyteGunner/Kilolyte/另第四原实体并随机yaw，然后销毁capsule。原无交互领取锁与发起成功条件，可能重复触发；须核原完整分支/结构来源，闭合实际四实体与capsule状态再实施，不能只做屏障锁和动画就宣称完整。

原159×59×159笛卡尔检查约149万候选位置，存在明显首次卡顿成本；后续优化考虑chunk/section palette快速排除和保原处理顺序/范围/属性，不缩范围/少开胶囊、不跳原需加载的区块、不异步写世界。有限持久值任务替代捕获死柱/玩家/Level的全局闭包，并按死亡后合法后续任务语义幂等，不因柱子60tick移除删去80/120tick阶段。

待只读子代理：原注册尺寸/颜色、四实体真实ID及目标覆盖、相关物品/方块状态实际实现、正常获取/生成来源、引用Procedure及所有同类重复触发链、模型资源哈希。之后主线编写完整代码，冻结给子代理编译/机械核；全模组完整后两端/Render Preview测试。


## 第50批期间补充的实现约束（待执行）

四胶囊实际实体现已接入Errant/Ripple/Gunner及本批待验证Kilolyte，须待第50批冻结验证后再做Pylon。原120END capsule生成点不是简单原BlockPos：它用BlockPos.containing(anchorX+xi+.5, anchorY+i, anchorZ+zi+.5)，而读取/销毁位置用不加.5的anchor；当柱子位于半格，生成可能偏一个整格。因此任务必须存原Vec3 anchor，不把它提前整数化并“纠正”掉原布局。

保持原y→x→z处理顺序与每次spawn的随机yaw顺序。可在一次性静态表中用原double椭球公式计算每行实际z区间（完整逐点比较，不用近似sqrt舍入）；运行时按原顺序依区块边界切短span，首次访问仍加载原需访问FULL chunk，读取对应LevelChunkSection，并用hasOnlyAir/maybeHas(light或capsule)跳过无目标span。不要预收集目标后排序来掩盖生成区块/更新顺序变化，不跨匹配变更缓存palette布尔值；每span重新判定，自己world写入后后续span可见。仍保全部实际世界写入/RNG、无异步与范围削减；优化收益需实测，不作静态量化宣称。该方案需用固定原椭球候选枚举机械核区间/顺序一致后采用。

另应核死亡60tick阶段baseTick是否仍运行GuardianPylonOnEntityTickUpdate；原代码没isAlive门槛时，NO_BREAK100尾随锁会在死亡动画期间持续刷新至柱子移除，不能在die即清锁或只给活柱子更新。80/120剧情必须独立于60tick实体移除；只保存有限值/玩家UUID快照、幂等消费。高等级Regen199不是无敌：旧Java右移只取低5位，应保真实治疗节律；不能把它混同本批Kilolyte的Resist200出生保护。


## 第51批实施结果

完整实现和静态证据见guardian-pylon-batch.md；上述待执行方案已落地。第四胶囊类型确认为SanctumRipple。activated只锁剧情、后续活体交互仍可伤柱，修复原生无敌帧差伤后残血无法重试。原orb_sanctum模板及自然生成入口继续后续批次，不以刷怪蛋代替正常获取。
