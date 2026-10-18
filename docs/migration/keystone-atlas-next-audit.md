# Keystone 与 Atlas 后续迁移边界

第十五批创意入口补齐时确认 Keystone 尚无真实注册，不能为图标注册普通物品。已读取原 KeystoneItem、KeystoneRightclickedOnBlockProcedure、AtlasBlock、AtlasBlockAddedProcedure、AtlasOnBlockRightClickedProcedure 与 DisruptedSpaceEntityCollidesInTheBlockProcedure；以下只是原件源码证据和待办，未实现、未运行。

Keystone 为单件 EPIC 物品。右击仅允许主世界或 Loqendia，点击平面中心及 x/z 八邻居全部为黑曜石时，给玩家该物品100tick冷却，在中心上方放真实 Atlas，播放 end_portal_frame.fill NEUTRAL1/3；延迟20tick在中心(-1,0,-1)放 atlasbase 模板，保实体与依点击面旋转，播放 splash1/0、minecart.riding1/-2；再延迟60tick播 end_portal.spawn1/5。原上下点击面默认无旋转，不能只迁水平面。原无物品消费，不能擅自消耗。模板放置、区块边界、权限、重复请求及阶段持久化须结合原JAR再设计，避免继续全局闭包。

Atlas 是真实 Geo 方块/方块实体/菜单入口。STONE3/100、light15、正确工具、无碰撞、PushReaction.BLOCK，ANIMATION0..1与WATERLOGGED、水更新和透光语义保留；原无loot时回退掉 Keystone。放置改 animation=1，并初始化全局矿物等级变量。右击服务端打开 AtlasMenuMenu，不能只放一个外观空方块或省掉界面。

下一步须追全 AtlasMenuMenu、客户端界面、按钮及网络 procedure、BE/Geo动画、MapVariables所有读写、真实 Loqendia 传送/维度和 atlasbase 模板引用。原 DisruptedSpace 程序两次独立 Math.random 判定的 else-if 可让等级仍为0，不能把它当一个均分随机布尔或只读函数名就判定采样次数；是否修正需先追踪变量全部消费端及原JAR控制流。当前不据此变更世界选择概率。

完整迁移门槛包含真实世界数据保存/同步、菜单服务端位置与权限校验、传送后恢复、结构及旋转、完整音效时序、Geo模型、创意原图标及进度根链。本批创意列表保留 Keystone 待办，并不意味着上述链路已迁入。


## 第29批后续已核的入口边界（仍待实现）

世界矿种SavedData与六条件OreFeature已迁入，初始化保原两次独立random，但当前没有调用者。必须在真正Atlas放置链接入，不能在世界Load或feature.place偷偷选矿。default0仍不生成对应矿脉。

原AtlasMenuMenu零物品槽，bound默认false使stillValid实际一直true；原AtlasMenuButtonMessage只验证请求坐标hasChunkAt，不验证当前打开的Atlas菜单/距离/真实Atlas/维度。重做应用目标原生容器按钮通道或等价校验，绑定真实服务端菜单位置与玩家，不能保远程任意已载入坐标的传送入口。原界面196×196、atlas_rubicon贴图192×192偏移2、三个20×20按钮位于(43,83)/(114,130)/(124,53)，hover使用原20×40贴图下半；原客户端回调同时发包并本地执行，应消除逻辑重复而保完整界面与声光。

原三个目的入口为Loqendia/Overworld/Palace（aqeron），关闭菜单、保存来源Atlas坐标、按玩家目的Atlas坐标传送、更新能力和效果包及事件1032。切维失败后不能仍将玩家在原维度送往另一个维度坐标。原传送后180tick Resistance amp9属于伪装无敌，需要按真实伤害管线改为有期限、持久/同步语义明确的到达保护，逐项保原合法伤害例外和可见药效粒子；不能直接删保护或照抄超高抗性。Palace真实维度及内容、玩家坐标写入/重生/重启恢复、首次到达与LoqendiaPortal来源等继续追全，不能用按钮可点或空维度冒称完整。
