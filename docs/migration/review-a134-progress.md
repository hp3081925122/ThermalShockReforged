# 第134批声音复审阶段记录

状态：进行中，不能视为134整批、G02或整模组完成。起点7134013066670dbb1c56897906596b85491e6aee，原07.9 JAR SHA256为3e7065fa74d4e7a62849521e25e0e68fb81c6c68eadff58dd53844f6770ca436。133报告冻结，不回写。

## 本阶段修正

1. FabricatedArmorEvents：推进靴启动原声只在使用者客户端本地播放，迁移后错误广播给附近玩家。改为ServerPlayer.playNotifySound，只发当前连接；保NEUTRAL、音量1、音高1与合法启动条件。不改原冷却到期的广播声音。目标仍在服务器校验后发送，不宣称网络延迟和原客户端即时预测一致。
2. StarlightBottleBlock：原用合法吸管交互时，客户端先在整数方块坐标播放GENERIC_DRINK。目标误移到服务端成功换块后广播。恢复客户端本地NEUTRAL 1/1一次，取消该广播；服务端换块、饥饿值与饱和度不变。
3. SootleEntity：原queueServerWork在起手当次SERVER END就扣减，旧迁移普通实体tick从下一tick计数，挥击和声音晚一拍。技能年龄改为既有ServerEndTickTasks消费，单击第7 END，双击第8/10 END；固定受击者原点、NEUTRAL 1、音高4/5、次数与公式不变。保原既有30/40冷却、18行动锁、死亡/跨维取消和局部NBT字段；重载重新登记任务，集合按UUID去重。
4. FrostilyteEntity：同样查证11延迟先跳过起手tick，实际比原11 END晚一拍。只把attackRemaining抽到帧末消费，冷却与行动锁仍走既有tick；保受击者固定原点、HOSTILE 1/2、距离、公式及原既有取消边界。

上述4项新增诊断均为英文debug日志，未加入Java注释，未降低声音次数、粒子或技能伤害。

## 已核静态证据

- 118组sounds.json按namespace替换完全对应，272份OGG逐字节一致；没有通过删资源隐藏差异。
- 原649个注册方块声音属性全部按真实注册、枚举、父类或复制属性配对，649相同、0差异。实际引用的38种原生SoundType构造音量/音高/五事件参数在1.20.1与1.21.1源码中相同。surge_pylon曾因1.0f与1的表示不同误报，数值归一化后撤回。
- 15张唱片的实际RecordItem构造长度按tick迁秒，时长、比较器、稀有度、声音事件、stream与物品jukeboxPlayable关联逐项配对；描述键两语言存在。不是仅注册普通物品或按OGG长度猜歌曲时长。
- 8份群系声音JSON及276份动画JSON按namespace对应；原/目标动画均无声音时间线字段，不能伪造缺失的Gecko声音消费者。
- 285个原实体ambient/hurt/death/step回调映射到真实目标variant分支；52项脚步音量.15/音高1对应。回调配对不等于全部技能声音消费闭合。
- 15个原仅本地声音来源逐调用链核对。GUI Screen除了发包还会直接执行客户端handler；购买timedLoop只有同步首轮在客户端发声，后续队列只收SERVER线程组。不能把循环次数当可听声次数而额外叠声。Une差事jingle在原ServerPlayer分支不可达，目标单播属于原迁移文档明确记录的逻辑端修复；合法已完成重开进NULL页，不补重复jingle，独立页面语音仍保留。
- 14个技能家族的36个原procedure、39个逻辑播放点核了实际方法拓扑。37项沿用参数/原点/次数和实际调度配对，1项Monolith失败重试边界为既有文档明确记录的有意差异，1项Frostilyte普通tick时序误判已由本阶段修正。phase3覆盖phase2中该项错误matched。
- 59项自动字面量签名差集逐项解释，包含条件表达式、数据表、随机公式、辅助重载与原未调用便利方法；25组解释均有当前目标文件hash。该差集不是59个漏洞，也不是全部374个原声音来源已验收。

## 撤回的误判

WorldTourRedemption没有改动。只看ThermofebRedeemProcedure的entity==null返回会误判控制台失败声；真实LoqendiaRedeemCommand先补FakePlayerFactory，原失败声确实可达，现实现保留它是正确的。

Monolith初始化音效只有任务加入成功才播放，失败重试时不同于原无条件播放。monolith-batch95.md已明确记录该取消边界修复，本阶段不擅自改回。

## 验证与剩余工作

四文件冻结后Java21离线compileJava/processResources成功，20秒，24任务中1执行、23最新；仅有既有弃用与Razorwing未检查操作提示。159项静态时序模型核Sootle原7及8+2嵌套队列、重复登记、保存年龄恢复与取消，以及Frostilyte剩余1..11的帧末计数；这是源码绑定和离散模型，不是Minecraft运行测试。

全量AST盘点原374个真实播放API来源、1448调用点；目标405调用点及7个声音包构造点。两端合并结构与互斥逻辑端不同，数量不能用来推断删减。45份原CFR源码带解析错误，保留快照，涉及争议分支继续查原JAR字节码。

剩余攻击、制造机和世界交互来源继续按完整条件/调用端/位置/音量/音高/次数/延迟逐源审查。没有据本阶段宣称这些剩余来源或全部消费者闭合。134完成前不越级标137粒子消费复审完成。

本阶段未运行客户端、专服或Render Preview。按用户约定，完整迁移及缺漏审查完成后再由Luna执行最终功能和渲染测试。27武器外部动画桥归主线136，不重复修改；不碰结构NBT、语言、loot、Ribbonblade、用户测试或.vscode。

临时脚本、日志、原件字节码与固定快照均位于D:/AI临时文件/ThermalShockMigration；精确路径、hash及证据边界见同批JSON。
