# 第40批：Palace肖像居民完整实体与对话（代码与静态阶段完成）

原件07.9 PortraitCharacterEntity、Model/Renderer、RightClicked/VisualScale、LavenCivil1Tlk/2Tlk Menu/Screen及注册表为依据；Luna已逐项机械核原源与原JAR/资产。闭合Palace四个Portrait Character实体，不以空站桩解引用结构。Ly真实交易与宫殿/维度仍另批待迁。

保MONSTER且继承Monster、1×4.5/fireImmune/64追踪/3更新/75HP/有效30甲/1抗击退/0速度攻击/16跟随/0台阶/0XP、持久化/20tick死亡和原具体伤害拒绝，正常重力不添加Benzor处理。原lookAtPlayer16及head骨pitch/netHeadYaw弧度旋转保留。原纹理默认portrait_default，五张256²贴图、portrait_entity Geo/idle动画、半透明/零阴影/renderer整体scale1保留；只有碰撞尺寸默认1，其余.5，眼高始终1.62，不凭外观推断把渲染也缩半。纹理变化同步时刷新尺寸，替代每tick无条件refresh，不减少实际变体表现。

主手Keystone且instabuild：default→pente→mc→ibex→gradient，原末档停在gradient（源码与Luna确认），明确修为五档回环，保五原名称/颜色、不消费Keystone；未知外部纹理仍按原fallback进入gradient。常规交互只default打开LavenCivil1Tlk，ibex打开2，其余无菜单。服务端处理业务，不重复客户端改状态；原有自定义NBT Texture与ForgeData保留。

两菜单ID laven_civil_1_tlk/2_tlk，各276×182、零槽/无按钮/quickMoveEMPTY/ESC关闭。完整10条文字及颜色位置按原件：1页6条、2页4条(其中shocked为0xFF00CCCC)，两底板分别mainspeechbox在(0,103)276²和mainlabelbox在(0,-30)134²；没有立绘贴图或下一步按钮，不额外捏造。原菜单tick只推动全局WhisperSpriteIndex但这两页不读取此值，删除其跨玩家全局副作用，不生成无用会话计时。原stillValid无约束改真实NPC ID+UUID/维度/存活/距离8/对应纹理校验，防远程/旧绑定，不改变静态文本。

9相关资产（5PNG+Geo+animation+2GUI PNG）已由Luna哈希核原件相等。先实现再冻结交Luna最小三任务编译/机械对照；完整模组后再游戏与Render Preview，不把静态资产相等当实机渲染一致。


最终收口：实体/egg/两Menu/两Screen均唯一接线，Luna固定快照三任务离线Java21 exit0/24.5秒(Gradle24s)，只有既有弃用API警告；两页10原英文/颜色/位置/零槽、head与尺寸/alpha/追踪及九资产SHA通过。原旧Armor64是否限幅经Luna补核原JAR Attributes/AttributeInstance/RangedAttribute，ARMOR最终sanitize至0..30，普通无modifier读取就是30；撤回其初报按未限幅64估算高伤害差异的假设。外部直接读base64、或对base先加大负modifier再限幅与目标base30可有差别，不宣称完整外部数值等价；KB原999同样按有效上限规范。原shader/实机显示与多人切图仍待最终两端/Render Preview。本批整模组仍未完成。
