# 第95批：Graver家族

迁移前建立本文。只读原07.9 JAR及readable/net/mcreator/thermal_shock，目标Minecraft1.21.1、NeoForge21.1.233、Gecko4.9.2。基线bc6b2b5。

范围：Graver、NeonLimeGraver、Assimilator、AssimilatedGraver、Razoraptor及真实初始化／转换／受伤／死亡／攻击事件、全部表现资源和数据。原始行为调查后补功能矩阵及修复依据，先核调用者／EntityAnimationFactory／目标原生API，再实现。Monolith家族由另一聊天独占，Graver避让等精确依赖由主聊天共享集成，不共改其生产目录。

已确认Graver有随机初始化及骨头直接掉落、自定义近战距离、20tick死亡；初稿30tick已按原tickDeath更正。五实体都是20tick死亡。技能不得用空实现代替；内部药水状态与真实药效分别审计，跨实体转换须防取消加入丢原体，受伤召唤须查冷却／上限／幂等，伤害防递归，保全部原声画与来源。

|实体|原默认／特殊行为|
|---|---|
|Graver|0.6×1.7、11血／1甲／3攻／0.2速／32索敌／0.6台阶／3XP，1.7近战、报警、0.8闲逛、环视／视线玩家、6格避Monolith；骨头一枚普通死亡掉落；1e-5稀有转换后四贴图连续0.5抽样，概率1/2、1/4、1/8、1/8|
|NeonLimeGraver|0.6×1.7、100血／1甲／3攻／0.15速／32索敌／0.6台阶／5345XP，2速近战／1速闲逛／其余类普通但不避Monolith，固定可见红名；原unglow透明底与neon eyes层|
|Assimilator|0.6×0.5、12血／0甲／3攻／0.2速／32索敌／0.6台阶／3XP，2速近战、0.5跳扑、0.8闲逛、报警／玩家／Graver目标／24格避Monolith；击杀Graver时在自身BlockPos生成AssimilatedGraver并消费自身|
|AssimilatedGraver|0.6×1.7、36血／4甲／5攻／0.16速／32索敌／0.2抗性及击退／0.6台阶／5XP，2近战、0.9闲逛、报警、玩家、6格避Monolith，run动画|
|Razoraptor|AMBIENT分类但Monster基类、1.3×2.6、24血／4甲／7攻／0.3速／32索敌／0.2抗性／0.5攻击击退／1台阶／15XP；3格技能触发、报复／闲逛／无视线玩家；30%hold盾声＋真parry15/1隐藏粒子和30内部停步，70%attack按钮声＋20停步及10 END固定受害者位置sweep声，≤3格原无归属errant_damage伤5−甲×0.4和look×2击退；两支CD30，完整透明／eyes层及head旋转|

原InfectingMobAttack已由InfectionStatus桥接，补真实infecting_mobs标签后闭合，Graver排除感染保持。原全局GraveyardDrops／FoodDrops等按真实类型或标签生效，不重复复制掉落事件。Razoraptor假slowness／cooldown全隐藏粒子，可直接改局部计时／速度modifier；真Parrying保留。10 END尾阶段用有限持久任务，即使原施法者移除，合法已排动作仍保最后位置／朝向快照，跨维和玩家重连身份另防误伤。

已接入10个生产Java文件、五个真实实体／四刷怪蛋／有界Razoraptor尾攻击、原24资产、四自然生成及11组标签。Neon原注册没有刷怪蛋，不擅加获取来源；原固定红名和5345经验保留并可配置。相机图鉴既有五项记录直接复用。Graver避让Monolith与甲虫锁定Graver通过真实类闭合。

原GraverModel确实引用chorpse，原JAR中对应Geo与五clip都存在；Luna首次误报缺失已经其重新查JAR纠正。Razoraptor原构造器setMaxUpStep(1)、getAttackReachSqr返回9，现用STEP_HEIGHT属性1和距离平方9保语义，并非增高台阶或扩大触发。Luna第二份报告的这两项推测经原源码纠正，不纳入修复差异。

已确认并记录合理修复：Assimilator原控制器请求不存在的death/walk2，而资源只有idle/walk/bounce，追击沿原walk、死亡沿原idle加原生死亡姿态，不向资源捏造缺失动作；三原clip完整保留可触发。Graver稀有初始化移到真实加入后执行转换，初始化仍只抽一次，避免finalize期间移除又被调用者加入；取消替身保原体。Assimilator转换加重入及已消费锁，同样成功后消费；不把正常一换一误判无限召唤并凭空加全局上限。Razoraptor技能默认伤5仍按基础攻击7乘5/7再减甲，负／非有限伤不下传，击退从原look×2改可配置0.6并折抗性；真实15tick招架保留、30CD及20/30行动锁改持久局部状态，原全隐藏粒子没有删可见表现。尾阶段在10 SERVER END先消费资格，保固定受害者位置声音和施法者消失后合法尾击；跨维、自己、死亡目标、玩家重连身份有界防护。

主线24资产逐字节、四生成语义和11组标签机械核对通过。统一编译等待另一聊天最终停笔；本批不启动游戏，最终专服／客户端／Render Preview待全模组完整后由子代理执行。

统一结果：两路固定8202输入后compileJava/processResources离线首次成功1m30s，7551构建资源字节一致；主线10生产Java哈希与gravers-static95.json一致，Monolith12交付文件未改，所有既有翻译保留。最终总证据main95-static-validation.json，游戏未启动。
