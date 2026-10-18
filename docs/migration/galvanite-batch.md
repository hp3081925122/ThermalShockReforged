# 第十七批 Galvanite

先读取原GalvaniteEntity及生成/死亡procedure、模型/渲染/图层、TeleportFlash粒子和当前目标API。新增真实生物，不能只补创意蛋。原MONSTER .6×2、眼高.8、step1.6、XP10；HP12/甲6/攻击7/移速.15/追踪32/攻击击退.8，雷免疫。近战速度倍率2，原攻击距离平方为自身宽度平方+受害者宽度，不能套目标1.21新增LOS。原6个goal完整保留：近战、受伤呼援、随机散步、随机观察、玩家目标、32格避让Squall（慢1/快1.2）。Squall本体未迁入时按准确实体类型ID延迟匹配，不注册假Boss或删避让行为；最终须在真实Squall迁入后验收。

生成时眼部送1个TELEPORT_FLASH wrapper，offset .001³/speed1，并在出生BlockPos播end_portal_frame.fill HOSTILE1/1。原粒子lifetime0却在super.tick后发AAA，正常生命周期仍有一次teleportflash发射，不能因为0删掉。默认纹理galvanitenoglow、透明scale1/shadow.5、galvaniteglow眼睛层与head随动保。idle/walk/run及原程序动画均保留，移动时不激怒用walk、激怒且移动用run，其余idle。

死亡正常原entity loot完整迁入；额外bioelectric_cell要保doEntityDrops且全世界WorldCrux≥1的门槛，两次独立20%判定，各生成1个、pickupDelay10。原die无幂等锁，可被重复调用刷额外掉落；目标仅真正死亡且一次性提交，保存锁，不能为了避免刷取删掉第二次合法抽样。此额外逻辑使用doEntityDrops，不是doMobLoot，不强行合并两条规则。经验按原死亡20tick发放并防重入。

世界进度必须保原存储作用域，不按维度各存一份，也不常驻全局静态double；只在数据改变时setDirty。后续原Boss阶段写入需接真实行为，不提供无条件默认解锁或伪造掉落来源；当前未迁的触发器/界面同步明确留待办。天气判定复用已核的LevelData.isThundering+脚下snowy_shale/snowy_cobalt。原7个群系、权重20、组1的modifier待真实群系链闭合，不能用原版任意群系替代。

原资产/标签/模型/蛋与资源依赖由Luna机械核对；主线程编写全部代码。仅Java21编译和静态检查，完整模组迁完后再由Luna测试专用服务端、客户端及Render Preview。最终覆盖声音/眼部闪光/三种运动动画/近战距离/避让/雷免疫/原loot各pool/两次独立掉落、gamerule组合、世界进度跨维度保存及重复死亡。

避让先查Squall真实类型注册是否存在；未迁入该Boss时不反复进行必为空的32格怪物查询，后续注册真实Squall后自动启用原生避让。没有额外缩小范围、降低执行频率或削除该AI，原Squall父类已核为Monster。世界进度只在服务端消费；当前无客户端消费者，不照搬MCR把全部MapVariables广播给所有玩家，未来菜单/文本用真实读点补同步。

Luna有限静态核对通过，报告seventeenth-galvanite-static-validation.json。Java21离线compileJava/compileGametestJava/processResources成功48秒；避让缺类型短路补充后，仅增量compileJava再成功17秒。21个本批相关原资产目标均存在，三个tag各追加Galvanite且不删除原有目标成员，loot逐字段只变命名空间；原NumberProviders的min/max隐式uniform在目标Codec仍合法，未改变分布。原动画仅idle/walk/run，三者均接入。所有现有Java新增类无注释，临时脚本及日志在D盘。

当前WorldCrux仅有持久化和Galvanite消费端，没有advanceCrux真实caller，原唯一写入者AngryCavebloomer仍待迁；不能把这条额外掉落常规来源报告为闭合。普通原entity loot与生成蛋注册已迁，实际游戏取得、概率、状态保存、声音和渲染均待整模组完成后的两端验收。未启动客户端、服务端或Render Preview，未声称全模组完成。
