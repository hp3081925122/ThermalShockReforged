# 第113批 A 线集成清单

独占生产5份Java已停笔，覆盖clash_page_1、clash_no_signal、char_trade_template、civil_8_tlk_2、limbo_vault_gui五个独立MenuType/Screen。source/PNG/原无正常入口证据见inventory113，完整生产SHA与固定检查快照见production113、validation113、final113。未独立编译、启动游戏、stage或commit。

## 共享接入

主线在ThermalShockReforged构造器已有DialogueContent.register(bus)后追加一次：

```java
com.hp.thermalshockreforged.content.remaininggui.RemainingGuiContent.register(bus);
```

其内部接五MenuType与VaultCodePayload的RegisterPayloadHandlersEvent，不需要再在全局重复注册网络事件，也没有新PlayerTick/ClientTick事件。

主线在ClientEvents.screens(RegisterMenuScreensEvent event)已有DialogueScreen.register(event)后追加一次：

```java
com.hp.thermalshockreforged.client.remaininggui.RemainingGuiScreen.register(event);
```

五个Screen由此逐项绑定；不在旧DialoguePage新增Civil8第二页，不新增方块/NPC打开路由，不改主线legacyfabricator包。准确anchor/addition与locale增量在shared113。en_us新增4个按钮朗读名；原页面24个英文值已存在且不变。zh_cn补28条（24原页面文字/输入提示+4朗读名），source字段为写入值，既有中文优先。不增加可见功能说明、按钮文字或tooltip。

REFORGED_CHANGES共享文档追加两项源缺陷修正，准确文本也在shared113：原Char按钮读零customSlots导致异常，现缺槽拒绝但保原按钮/布局/36玩家槽和2/4/8真实结算链；原Limbo包不传文本且服务端引用EditBox，现只发送原完整文本及当前容器ID并在服务端校验后执行。原孤立身份、密码/数量/音效/重复提交规则均不因此修改。

## 行为与原始边界

原五页没有可用的正常打开来源：四页无原菜单构造调用，Limbo只在一个独立procedure中打开，该procedure原class常量池引用仅自身与匿名类。保注册和构造能力，不凭页面名/背景图去新增交互方块或NPC；后台测试可以合法创建菜单，但不把测试入口纳入正式玩法。

原clash画面中的命令、Y/N和报告文件名全部是文字，未增加按钮或命令执行。Char三个图片按钮中两个原回调为空，保持可视/可点击声但不发送请求；主交易钮通过原生容器按钮包在服务端校验当前菜单。Char既有额外数据仍识别主/副手同栈绑定、实体库存和方块库存，零自定义槽不会取用、返还或掉落绑定库存。

Limbo输入框保持位置81,31/118×18、最大32767字符、空值提示、编辑/resize保留、Escape关闭、原18×18两帧箭头。密码精确DumbBug、成功每次给1个真实chorusian_antennae_helmet并原NEUTRAL经验球声1/1；不增加解锁、冷却或一次性限制。ItemHandlerHelper同时保原辅助入包声、满包掉落及40tick拾取延迟。文本只来自当前玩家请求，服务端不引用客户端静态状态，不在客户端奖励。

## 验证与具体测试项

812项静态检查包含五页完整几何/配色/图片顺序/按钮二帧、36槽逐项坐标、原18类与4577常量池/来源、10图片字节、5Java语法与服务端类隔离、已核原生/NeoForge API，以及576种保险库首拒绝条件AST组合。没有执行Java菜单、Netty往返或游戏渲染，主线统一编译和完整后的运行验收仍必要。

1. 五个MenuType均能由合法服务端测试创建，客户端Screen绑定正常；按原尺寸/位置/颜色对照10份原纹理，界面切换与Escape正常，无多绘背景或光标。
2. 两个Clash页只显示原文字；Civil8第二页只两行与两背景，不把第一页台词/肖像混入；缩放窗口后布局仍与原锚点一致。
3. Char显示27+9玩家槽，Shift移动方向和数量正常，主交易钮因原缺少customSlots安全拒绝、不扣玩家物品、不奖励或异常；两个原空按钮无网络业务。
4. Char合法物品/实体/方块绑定时原失效规则正常；关闭界面不清空或掉落绑定库存。
5. Limbo编辑、粘贴、提示、resize保值和32767字符上限正常；错误大小写/前后空白不给奖无经验球声，正确密码每次给1个头盔，满包按原辅助逻辑掉落，不重复客户端发奖。
6. 关闭菜单、换容器或无关玩家/旁观者发送旧请求不发奖；在专用服务端无EditBox类加载。本线不重开或联系用户独立服务端测试聊天。

上述运行项目由主线在完整迁移、无简化复核和最终统一构建后安排客户端/Render Preview；本线交付不宣称已实测。
