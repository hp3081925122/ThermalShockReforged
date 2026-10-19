# 第74批：Spectre 实体与真实射击入口

原件依据为 SpectreEntity、SpectreAttack 全局 LivingAttack 入口、WhistlerNote 实际构造/发射、生成注入、属性标签、模型/透明渲染/发光层及 MiscFoodDrops。原攻击不是轨道球：50 tick CD，施法者眼位发速度 0.3、仰角 look.y+0.1、基础箭伤 5、击退 0 的音符；类内 helper 的速度 0.2、击退 5 不代表真实 Spectre 入口。

保 Monster、25 生命、4 护甲、3 基础攻击、0.3 移速、16 索敌、0.1 抗性/近战击退、1 台阶、0.8×1.7 尺寸、64 追踪和 5 经验/20 tick 死亡。保优先躲避玩家 6 格、平方攻击距离 64、受伤反击、0.8 散步和原玩家索敌设置；免疫溺水/凋零/凋零骷髅伤害，其余仍走原生。旧 UNDEAD 语义拆入目标原版标签，包含亡灵分类、亡灵杀手、反转治疗/伤害、毒/再生免疫、水下呼吸和凋零同伴；不擅加原自定义 undead_mobs 掉落标签。

内部 COOLDOWN 和缓慢 255 分别改持久 50 tick 冷却、20 tick 瞬时移动属性锁，不把内部状态继续写成药水。音效保受击者最初 BlockPos、shulker.shoot 音量 1/音高 2，白色音符轨迹与 40 SERVER END 生命周期复用已迁弹体。伤害按基础攻击×默认 5/3 倍率得到原基础箭伤 5；生命/防御/攻击/移速/索敌/击退/冷却可配置。

原命令会把全维度所有 Spectre 转向各自最近玩家。目标只处理施法者，保最近非旁观玩家脚位朝向，不再全场控制同类。原音符没有 owner，出生在施法者眼位，原生射线碰撞不会排除来源；目标补 owner 防自伤，并以 directEntity 为音符时放行其真实伤害，防造成者仍是 Spectre 时再次取消/施法。此为明确修复，同时恢复正确伤害归属，不声称旧入口已有该归属。

原 attack 动画有两个实现错误：全局过程把 setAnimation 写给受击实体，且 movement/procedure 不消费该同步字段；attackingPredicate 在同帧以 lastSwing+0 判定直接复位 swinging。目标在真实开火时同步触发施法者完整 attack，保 idle 与四 tick 过渡；不复制无效控制器。渲染仍使用 specter（资源拼写）、原 specter 眼光层、透明模型、1 缩放、0.5 阴影和不跟随头骨。

保四原群系刷怪、权重 2、1～2 只、黑暗/非和平/原版地面生成条件，以及 cloro_weak/ferro_resist 标签。既有 FoodDrops 保死亡 0.4% Luck Ticket、doEntityDrops 和原拾取延迟。完成后与本批和第73批后补数据统一编译/静态核对；最终两端及 Render Preview 验收待全模组完整后由主对话执行。

验证记录：离线 compileJava/processResources 通过，52 秒、24 任务中 2 执行/22 最新；仅既有弃用和 OrbRenderers 未检查提示。五视觉/物品资产、三声音组对照原JAR一致，两个动画片段完整，群系数据、两自定义标签及六原版亡灵标签闭合。13 份本批及第73批数据文件与 build/resources/main 输出一致；创意页 manifest 中原有刷怪蛋条目由既有动态注册查询显示。详见 seventyfourth-spectre-static-validation.json，最终游戏/渲染待验。
