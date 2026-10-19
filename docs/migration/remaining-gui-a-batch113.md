# 第113批 A 线独立菜单迁移

生产前建立。基线为112统一构建通过后的本地提交4b5a97269b0a235dd4f62aa5ba90903c5d33285f（主线回报8852固定输入、61生产文件、1m31s、8047复制资源及2模板）。原07.9 JAR SHA256固定3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436，目标1.21.1 NeoForge21.1.233。113已获主线明确解冻。

## 范围与所有权

本线负责clash_page_1、clash_no_signal、char_trade_template、civil_8_tlk_2、limbo_vault_gui五项。新增业务独占content/remaininggui和client/remaininggui及本线新资源/文档。共享入口、ClientEvents、旧DialoguePage、语言及变更总表只交精确增量；主线负责另外两个legacyfabricator页面。UTF-8，Java无注释，脚本和临时仅D:/AI临时文件；本线不编译、不游戏、不stage、不commit。全部迁移与无简化复审后主线安排客户端/Render Preview，用户独立服务端测试不接管、不联系。

## 原实际内容与目标映射

|原菜单|实际页面与逻辑|已有目标与迁移边界|
|---|---|---|
|clash_page_1|176×166，kgcpage背景、14行原位置/颜色文字，无按钮、无槽位|目标没有同ID注册或完整别名；新增独立菜单/屏幕，不把文字中的执行命令当真按钮|
|clash_no_signal|176×166，cbt_nosignal背景、1行文字，无按钮/槽位|同上；不捏造消息/翻页|
|char_trade_template|206×196，两张图、5行文字、3个图片按钮；玩家背包27+9槽，customSlots为空；按钮0调用BenzorSpiceTrade，其余回调为空|目标其它交易页不能代替本原模板。保36槽和图片按钮，保无自定义交易槽的事实，不伪造三个交易输入|
|civil_8_tlk_2|276×182，两图两行，无按钮/槽位；原菜单END调用ConciergeGUIOpenTick，WhisperSpriteIndex按0..13循环|目标civil_8_tlk只是第一页，不能自动视为第二页覆盖。新增独立同ID；保本页画面和时钟推进，不增加肖像或台词|
|limbo_vault_gui|276×76，无背景图；标题、118×18输入框（最大32767字符）、18×18图片提交钮；输入精确等于DumbBug给1触角头盔并播放经验球声|目标已有真实头盔但缺菜单。保无冷却/一次性限制的原规则和大小写比较；输入随请求到服务端，权威发放不在客户端改库存|

已扫描全部原可读Java：前四项未发现构造打开调用；Limbo仅LimboVaultOnBlockRightClickedProcedure中有openMenu，该procedure原无外部caller（111已有原class常量证据）。本批继续核全原class常量和数据引用后固定证据。五项仍需独立同ID MenuType及Screen注册，不以原孤立为由删除，也不添加正常方块/NPC/配方来源。

## 原缺陷与有依据的修正

Char模板的customSlots实际为空，BenzorSpiceTradeProcedure对_slots.get(0/1/2)直接解引用会异常。目标保原36玩家槽和零自定义槽，按钮0先检查原所需三个槽是否真实存在，缺失时英文debug并拒绝；不拿玩家背包槽冒充输入，不凭按钮图造交易。若未来明确恢复真实三槽，其原2/4/8经验倍率、清空输入和whisper_advancement链必须一并保留。

Limbo原网络只发送按钮ID和坐标，procedure读取静态guistate里的客户端EditBox：独立服务器无法拿到真实输入，并有客户端类引用。本次发送当前容器ID和原完整文本（上限32767），服务端核当前菜单/拥有者/原坐标已加载后执行精确密码、给1个已注册头盔、原声源NEUTRAL/音量1/音调1；错误密码无奖励无声。重复正确提交按原每次奖励，不擅加冷却或持久解锁。仅限当前已打开原菜单，不增加正常打开来源。

保原页面无绑定时stillValid=true的独立能力；Char原有物品/实体/方块能力绑定分支亦按目标Capabilities核用法迁移，绑定时保物品同一栈、实体存活或方块距离校验。零自定义槽不会搬运、掉落或清空绑定库存。

## 验证与交付要求

核全部标签/坐标/颜色、图尺寸/裁剪/悬停帧、空按钮、玩家背包位置与Shift移动方向、输入编辑/resize保留/最大长度、服务端安全加载、奖励/音效/原来源。目标已存在纹理只核哈希，不共改。原语言值保留，缺失中文及必要无障碍按钮名只交精确增量，不用英文硬编码玩家提示。所有Java调用先核既有项目或目标源码；静态验证不宣称已运行GUI或服务端。

## 完成状态与无简化复核

五份独占Java已完成并停笔：RemainingGuiPage保五页数据，RemainingGuiContent逐页真实MenuType注册，RemainingGuiMenu保原库存/绑定/奖励，VaultCodePayload传原完整输入，RemainingGuiScreen逐页绑定Screen并绘制原图/文字/按钮。无新增图片资源，原10份GUI PNG与目标逐字节相同；6次图片绘制、23行文字的坐标/颜色/顺序、4个按钮的尺寸/二帧与原界面尺寸均已由AST数据反向对照。原四页没有打开构造caller，Limbo打开procedure在原4577个class常量池中仅自身及匿名类引用，原data文本也没有隐藏菜单引用；均保独立同ID，不造正常来源。

Char保27+9玩家槽、原背包/热栏移动方向、零customSlots及其空槽防护；未来真实槽被原Map接口明确提供时仍保2/4/8经验结算、逐槽清空/广播和whisper进度原条件，不取玩家背包冒充。Limbo保大小写密码、原32767字符输入上限、提示、resize保值、Escape、原每次给1头盔、原经验球音，入包和满包掉落复用已核NeoForge ItemHandlerHelper。原方法并非只有显式经验球声，辅助入包声/满包40tick拾取延迟同样保留。

Civil8第二页原时钟虽然由菜单END推进，但本页两张图均无该变量读取，不补原没有的肖像/动画。按当前目标GUI已采用的页面归属方式保0..13客户端页面时钟，避免恢复原跨玩家静态计数；无服务端奖励/物品业务。新版EditBox按原生毫秒计时显示光标，无已删除的tick API；用setResponder保持提示更新，仍由addWidget注册并在原渲染顺序单次绘制。

静态检查812项通过，其中576种保险库条件组合直接求值目标方法首拒绝条件AST，只准当前合法菜单/服务端玩家/非旁观/有效/坐标已加载/精确密码；这些是静态控制流检查，不是Java运行或网络集成测试。五Java均语法通过、无注释，服务端四类无客户端类型引用。18份原Java及对应原class SHA、4577类反向引用、10图片SHA、目标/NeoForge API证据均保存inventory113和validation113。

共享仅两处各一行：root构造器注册RemainingGuiContent、ClientEvents.screens注册RemainingGuiScreen。语言增量英文4/中文28（含24原页面中文和4图片按钮朗读名），24原英文值保留。无旧DialoguePage或任何既有生产文件改动，主线统一集成后固定输入构建；完整迁移/无简化复审后再客户端与Render Preview，用户独立服务端测试边界不变。
