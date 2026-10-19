# 第117批 A：实体回调与传递过程迁移

本文件在生产修改前建立。基线本地提交27f8be1，项目1211-neoforge-ThermalShockReforged，Minecraft 1.21.1、NeoForge 21.1.233。原件Thermal Shock 07.9 JAR只读，SHA256固定3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436。

遵循parallel-batch117.md：从callback-roots-inventory115.json的entity目录129个实际过程调用根按sourcePath区分大小写排序，A承担前86根、主线余43根。实际方法须以AST和必要字节码核正，不能凭旧候选名推断入口。逐根保原实体全文/源码及class哈希、传递过程闭包、注册ID、目标业务/资源和现存批准差异。

核原真实父链、属性、目标/生成/掉落、每阶段时序、伤害与攻击属性关联、击退抗性、冷却/存活上限、外部状态写入、死亡/卸载/重启语义、完整Gecko模型动画、声音粒子与渲染层。不以注册、同名方法、文件存在或构建通过代替语义完成。

只改证实缺口及独占目标文件，不回退主线或其它用户改动。共享根入口、ClientEvents、配置、语言、标签和主线占用文件不直接修改，精确增量写entity-callbacks-a-batch117-shared.md；发现目标共享先记录归属。Java不写注释，新增调试日志英文，文件UTF-8，临时脚本和解压仅D:/AI临时文件/ThermalShockMigration。

本路不独立编译、启动、暂存或提交。代码完成后停笔交固定哈希及静态验证，由主线统一共享接线/编译。客户端和Render Preview在完整迁移及无简化复审后安排；服务端用户独立测试，本路不重复启动、不催促、不修改docs/testing。

## A线交接状态

A独占生产改动共11个Java文件，代码收尾后冻结，清单及逐文件SHA256见`entity-callbacks-a-batch117-manifest.json`。未编译、未启动、未暂存、未提交；未修改`docs/testing/*`。工作区中测试聊天及主线的其他变更不属于A交付。

86根、160实际回调调用、125种传递过程已建立原源码全文/JAR class哈希及逐根初审笔记；主要目标74文件，其中66独占候选、8共享只读。本批发现并修复的独占缺口如下；这不等于所有目标助手、外部状态写入、菜单内容和运行/完整视觉验收均已闭合。矩阵中`pending`仍是实际未验收项，不能在统一集成时整体改成完成。

## 生产改动

| 文件（Java根下） | 原因和结果 |
| --- | --- |
| `content/npc/concierge/ConciergeEntity.java` | notice每次交互原排20 SERVER END。改固定20位待办，保每次解除时点、旧计时兼容和实际蹲伏值持久化，不把重复点击变成重置单计时。 |
| `entity/kofoni/KofoniEntity.java` | wake同上，重复交互逐END解除，首次解除后旧待办重载不会把NPC重新设为蹲伏。 |
| `entity/snobol/ExplosiveSnobolEntity.java` | 单引信推进改20 SERVER END，保已批准有效正伤门槛、非FALL、死亡/换维取消、原点及攻击属性快照；不恢复原无界多引信。 |
| `entity/fauna/GoreFlayerOrbEntity.java` | 原40 SERVER END寿命替代41实体tick；剩余/启动状态持久，原粒子、无重力、工厂ATK×5与父箭命中链不变。 |
| `entity/ForecastEntity.java` | 漂移和后续缓慢状态分开在连续END执行。保双脉冲及40tick续期；原生局部Slow替代和较强真实外部Slow补偿沿第16批批准。 |
| `entity/ice/IceCreatureEntity.java` | Drone同原连续END漂移，仍施加真实Slow40/2。Drone20 END三弹与Zerk10 END追加技能改END推进，保原技能内容、攻击属性倍率、抗性击退和已批准生命周期取消。 |
| `entity/cavebloomer/BloomlingEntity.java` | 将Dandy20 END/Budding40 END提交给独立载体，原尸体20/40帧及XP不延长；保旧存档转交、有限失败重试与关联UUID。 |
| `entity/cavebloomer/BloomlingBurstEntity.java`（新增） | 有限持久END载体，父尸体正常KILLED不吞爆炸；保原声画/范围/排序/伤害类型/倍率/Slow/实际格挡物品及抗性位移。卸载暂停、不强加载；真正销毁释放本体关联，避免单独删除载体后永久忙碌。 |
| `entity/cavebloomer/AngryCavebloomerEntity.java` | 半血Aura回到baseTick且取最大生命比例；死亡声画交END推进，其他攻击、Boss条、掉落/进度/WorldCrux/60帧XP链不减少。 |
| `entity/boss/BossDeathSequence.java` | 保15轮：同步首轮，随后每4 END一轮，最后在相对END55；删除实体tick阶段偏差。 |
| `client/projectile/NetBulletRenderer.java`（新增） | 原捕网弹误用通用物品渲染，补回原0厚度32×32网面、64×64 UV、两骨骼姿态、cutout纹理和两轴插值旋转。共享绑定/模型层入口交主线接入。 |

## 静态证据及边界

- `entity-callbacks-a-batch117-source.json`：86原根与125传递过程完整源码、211文件源码/JAR class SHA256、实际回调AST入口和原注册ID。
- `entity-callbacks-a-batch117-matrix.json`：86逐根原→主要目标落点、目标方法全文/SHA256、初审笔记和未闭合项。
- `entity-callbacks-a-batch117-ownership.json`：66主要独占候选、8共享只读及始终只读目录；新增独占helper/renderer另在manifest列明。
- `entity-callbacks-a-batch117-assets.json`：158份原renderer/model/layer源码取证，208份已定位资源与原JAR逐字节一致，无缺失。追踪包含`entity/model`和`client/model`，不把资源相同当作绑定正确。
- `entity-callbacks-a-batch117-render.json`：86真实渲染绑定表达式、55份目标客户端源码、86属性方法及9份继承/配置支持源码；3组原生模型（小激光、棱镜盘、捕网）的几何与UV操作规范化逐项相等。捕网的新绑定仍未接入。
- `entity-callbacks-a-batch117-helpers.json`：20份已追助手/渲染器全文及SHA256，另有原SERVER END调度器、Dandy/Drone/Zerk全局技能4份额外原证据。
- `entity-callbacks-a-batch117-validation.json`：1256/1256静态检查通过，含原件/当前输入哈希、Java AST、资源字节、重复交互位图与队列等价、漂移双脉冲、尸体先移除仍爆炸/存读档、15轮死亡序列和模型几何对照；`git diff --check`通过。部分检查是结构约束，数量不能视为游戏功能测试数。

没有通过本路运行Java编译或游戏来确认API类型、注册事件、真实实体伤害交互、客户端动画/层参数及Render Preview。本次静态输入来自共享工作树，主线继续改共享文件后应重新固定输入并统一验证，不能拿旧SHA代表集成后的内容。

## 必须由主线处理

精确接线与未决差异见`entity-callbacks-a-batch117-shared.md`：Bloom载体注册/Noop两处、Net绑定替换/模型层两处、IceSquadProjectile寿命END、ShockImpact20 END、Mangler60 END解除及Guardian死亡粒子坐标差异。主线新修Jellercel的0攻击属性/固定1仙人掌触碰保持，A没有覆盖。

保留已有批准差异：无效再生命令不补真药效；scrambler跨模组皮肤联动按用户延期；GreenOrb撞块误伤owner不恢复；Infestophage无效的被杀者再生不改成自身回血；ElectricSphere有效正伤流水线与适中抗性弹力不退回免疫前满血；Qo第五次覆盖页的等价边界已核。其余逐根待核内容以矩阵为准。

统一集成、无简化复审及完整编译通过后，服务端测试仍交用户测试聊天，客户端/Render Preview按项目规则安排。A交接后停笔，不擅自开展以上测试或扩大共享文件修改范围。
