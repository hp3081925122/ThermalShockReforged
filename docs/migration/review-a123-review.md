# 第123批A：菜单复审交付

基线`33d3bc65c081427be13fc546c066ea725a5d5248`，原JAR与第122批一致。仅复审既有241身份中剩余190项，既有51项继承证据，不重新宣称运行通过。三个Luna分别提供57、65、68项只读证据，主代理核实生产候选；完整静态闭合数及逐身份具体剩余以`menu-a123-matrix241.json`和`review-a123-remaining.json`为准。登记齐全不等于全部语义闭合，待核数也不等于尚未实现的页面数。

## 唯一生产修复

`entity/labmachinist/LabMenu.java`将主背包至热栏的`reverseDirection`恢复为`true`，覆盖七个Qo交易页和Backdoor装配页。原八个Menu源码及原JAR八份class的`javap`证据均确认该方向；目标原生`AbstractContainerMenu.moveItemStackTo`的反向遍历从目标区间末槽开始。保持交易offset=0、装配offset=1，装配输出至玩家背包仍为true，热栏至主背包仍为false。未更改费用、产物、配方、槽位布局与按钮路由。

成功移动且数量确实改变后增加英文debug。限定差异核验同时要求实际文件精确等于基线加上述方向参数、成功日志和语句换行，不能夹带其他行为。生产停笔SHA256为`289d1a0801d4b25b51139f29fc788f883d8f79a7f1e249020541187860fb556c`。

## 复核中排除的误报

- Civil1原`Civil1TlkScreen.java:49`的mainlabelbox实际位于`leftPos+0`，不是62。不能用其他页面的常见坐标套用本页面，未据此修改生产。
- `WhisperMainWhileThisGUIIsOpenTickProcedure`虽存在10%概率及5帧边界，但全工程没有调用。真实`WhisperMainMenu.onPlayerTick`在END调用`ConciergeGUIOpenTickProcedure`，每次+1、超过12归零，与目标WhisperScreen当前推进一致。未按死过程改动画。
- 原`Inventory.clearOrCountMatchingItems`实际搜索自身Inventory、传入合成容器和当前菜单carried栈，不能写成排除合成槽或carried。原购买资格检查与扣款范围需分别核，helper的潜在范围差异不自动证明正常路径存在扣款行为差异。
- Concierge任务后Panacea交易入口和Ly Springelatin五级原子扣费是已有明确修复，不回退为原死入口或不一致费用。依据分别为`world-npc-thread-batch.md`及`ly-complete-shop-batch.md`。

## 语言增量与边界

组3确认`une_main`及`uma_main`实际绘制的动态问候语仍为英文硬编码，与原文相同但没有语言键。每页三条候选及原过程行号见D盘`a123-group3-matrix.json.language_increment`；交主线后续语言窗口处理，A未修改Screen或语言文件。Florist图片按钮的`Component.literal`仅用于朗读，不能冒充可见文本绘制。

其他未闭合项均保留逐身份精确原因，不以资源字节相同替代入口、状态条件、付款与奖励、布局及声音动画消费者对照。尚未取得充分依据的候选不改生产。

## 验证与停笔

已冻结全部`src/main`写入。A没有资源改动，没有build、游戏、stage或commit；主线负责固定输入后的统一`compileJava/processResources`。只运行证据JSON解析、身份全集及不交叉检查、输入SHA比对、限定差异和`git diff --check`等轻量静态核验。输入语言文件由主线独占并行修改，原逐值比较保留取证快照及漂移记录。

多人网络、真实物品/经验变动、断线和重入、真实声音及GUI裁切、透明层与头像帧均未由本批运行验证。服务端独立测试线不接管；完整迁移及无简化复审收口后，客户端和Render Preview交子代理验收。
