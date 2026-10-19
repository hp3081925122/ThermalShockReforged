# 第77批：Lil' Jelly与部署/收回罐子

依据LilJellyEntity（包括其内置RangedAttackGoal）、原注册、模型/渲染、出生/右键过程、AmaljellyCanisterItem与实际发射helper。保持Monster/MONSTER、注册火免、0.9×1.6、64追踪/3更新、XP0、0.6台阶、永久存活、不远距消失、绿色常显姓名。原移速和飞行速度均0，不擅加跟随或移动；FlyingMoveControl(10,true)/FlyingPathNavigation、无重力、不互推、不受跌落。原护甲基础99保留，由原版属性范围求有效值，不当成99点最终伤害减免。

仅主动最近Monster（原false/true目标选择参数），随机朝向优先级2；20tick原生远程目标速度1.25/范围10，持续条件直接canUse。原内部远程目标只比目标版本原生多写未消费SHOOT字段，其余时序/视线/距离公式一致，复用原生实现。实际磁弓弹为1.4速度、12偏差、5基础箭伤、1原始击退、非暴击静音；5改为本体基础攻击3×5/3，保既有箭父类速度伤害公式、绿粒子/20%中毒/命中音和一次抗性折减，不捏造射击动画。

免疫逐项保留：IN_FIRE、直接箭/玩家/投掷药水/药水云、FALL/CACTUS/DROWN/LIGHTNING_BOLT/EXPLOSION/TRIDENT/FALLING_ANVIL/DRAGON_BREATH/WITHER/WITHER_SKULL；不把普通EXPLOSION扩成PLAYER_EXPLOSION或全伤免疫。保generic受伤/死亡声、完整idle/4tick过渡、head跟随、透明体、0.2阴影；原无发光层，不额外发明。出生5 POOF，偏差/速度均0.1，死亡20tick移除。

填充罐use在玩家x/y+1/z所在格生成MOB_SUMMONED（居中为原EntityType.spawn语义），随机yaw；保原召唤者位置15 WITCH/1范围/0.1速度、EVOKER_CAST_SPELL中立声1音量/3音高、60tick填充罐冷却和1个空罐返还。只服务器结算，已取消/未成功加入世界不消费罐，不双端重复播放。空罐交互收回一个实体，先discard再兑换，防重复请求；使用实际交互手而非总覆盖主手，每次只消费1个输入并返还1个输出（原堆叠上限1，创造也保持原单罐转换），不删除异常超量栈/满包内容。无主人的固定炮台设计照原保留，不擅加驯服或主人限制。

自然生成/蛋：原init为空且无蛋注册，不增加自然生成或创意蛋。正常制造来源确实经FabicGuidePageTwoButtonMessage的按钮调用AmaljellyCanisterOpenProcedure，不能只搜procedures就误判孤立；其制造页/输出、8 Corrial Ingot +12 Cell +1 Bucket of Siibol +1槽内Cryonic Energy和两原音效，随完整Fabricator配方页批次继续迁移。本批不以普通工作台配方替代，也不把尚未恢复的制造界面标完成。

本批实体/罐子/弹参数/渲染/语言一组完成后统一编译与原件静态核对，全模组完成前不启动游戏。最终主对话测试：双方手/创造/满包/双人同tick收回/生成取消与失败、原20tick射击及伤害归属、射弹不伤自身、存档恢复与完整渲染。

实体/罐子/渲染已实现，compileJava/processResources统一通过（48秒，2执行/22缓存）；7视觉资产及构建复制、五组中英键、创意双罐/无蛋入口与实际制造调用已静态核对。补回infectionimmune和sprintweed_blacklist原成员，避免草类错误给炮台加速；两个JSON已核原件与格式，构建复制留到下一组较大修改统一processResources，不为两份数据再跑Java编译。原制造页与最终游戏验收继续待完整迁移。


第78批后续：真实Fabricator第二引导页按钮8、凝胶罐详情/原贴图/材料标签、服务器原子扣料和两个原音效现已接通，配方data/thermal_shock_reforged/recipe/fabrication/amaljelly_canister.json已启用并核对8/12/1＋1能量。两个后补自定义标签构建副本也通过合并资源构建与逐字节核对。此两项不再待办；完整部署/制造/收回游戏流程仍待整个模组迁完后的最终运行验收。
