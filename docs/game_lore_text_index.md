# Thermal Shock Reforged 游戏文本与剧情索引

> 这是基于当前 1.21.1 NeoForge 源码和资源的文本审计。重点收集剧情、NPC 对话、进度描述、广播、全息录音、结构内书籍和物品/方块描述；纯物品名称、按钮名称、交易数字等 UI 文本没有逐条抄录。

## 文本来源

- 中文语言文件：`src/main/resources/assets/thermal_shock_reforged/lang/zh_cn.json`
- 英文语言文件：`src/main/resources/assets/thermal_shock_reforged/lang/en_us.json`
- 结构内书籍：`src/main/resources/data/thermal_shock_reforged/structure/*.nbt`
- 进度定义：`src/main/resources/data/thermal_shock_reforged/advancement/*.json`
- 硬编码/回退文本：`src/main/java/com/hp/thermalshockreforged/content/decor/geo/HologramRecordings.java`

## 剧情梳理（按文本明确内容整理）

### 1. 世界与玩家入口

- 玩家从主世界使用界域基石，在平放的 3×3 黑曜石平台上进入无尽花园（Loqendia）。
- 无尽花园是一个以虫族文明、冰雪灾难、感染和遗迹为核心的异世界；页岩泉镇是玩家最早接触的重要聚落之一。
- 尤妮特称自己和姐姐从合唱城搬来，是在“那件事”之后；她还说这里的游客减少“是有原因的”，但没有明说具体事件。

### 2. 旧文明、光能与灾难

- 结构书《PyLON EXTRAKTING/recykling》写到：旧王的尸体升起后，士兵和人造金属巨人拆除维系城市的电塔，把电塔核心带进黑暗高塔并拆解；核心蕴含来自太阳的巨大能量，Phoenum 曾把它严密封存。
- 另一本书描述 Phoenum 的塔、Zenillia 的深红回廊、Ferraq 城堡，以及 Uuril 花园曾经繁盛却后来变得死寂。叙述者承认自己曾经“看不见”光所传达的内容，并施法让页岩泉镇永远安全、永远盲目。
- 全息录音显示，企业/高层主管正在把原本做卫星和气象气球的层云部门转向军事化，强迫研发冰蚀无人机、战车和“千灵”等项目；工虫联合会因此抗议并停工。
- “晋升”并不只是职位升迁：录音暗示寇在成为百周期最佳员工后要接受外科手术，并可能遭到脑部改造。

### 3. 对花园的探索与失败的远征

- 录音 7 记录了一支队伍挖进巨大蒲公英花园，发现从未见过的虫形植物；这很可能对应花园深处的洞穴绽花生态。
- 录音 8 说明他们试图进入一座蜂巢/大型结构，但在第一道墙前就损失人员，最终撤退；Perr 的坦克被黄蜂包围。
- 摄影师委托线要求玩家拍摄冰川群系中的洞穴绽花，再把照片带回页岩泉镇，说明这些生物和花田是当地重要谜团。

### 4. 感染、墓园与秽血浴场

- 尤妮特描述“感染”：人会死亡或吸入孢子，感染物会占据身体，有些只剩白骨，另一些下场更糟。
- 礼宾使认为，感染和失衡能量相关；他把“亵渎者”及其信徒视为文明自毁的表现。
- 亵渎任务线：礼宾使先发布源初之血委托；玩家深入秽血浴场，击败三名先驱，进入大亵渎者密室并取得腐化核心碎片；再用提取机从核心碎片提取源初之血；最后回到大教堂取得万灵药。
- 这条线对应进度“亵渎 其一”到“亵渎 其四”，是当前文本中最完整的任务剧情链。

### 5. 暴雪计划、层云部门与机械师

- 尤妮特说暴雪计划在地下、尤其水边建立基地，因为那里有凝胶储能块；他们用电力研发激光枪、电击拳套等技术。
- 玩家在钴石高地登上暴雪圣所并进入档案馆，之后会接触机械师和层云合金相关的“不安同盟”。
- 机械师索取病毒石组件；后续进度要求制作病毒石。录音和 NPC 文本共同暗示，暴雪计划/层云部门与更高层主管之间存在强制研发、军事化和人员改造问题。

### 6. 神秘资助者与界域基石

- 玩家获得界域基石的进度描述为“从神秘资助者处取回界域基石”。
- 相关广播包括“我有份礼物要送给你”“你知道该怎么做……”以及邀请玩家前往 X1333 Z1333。
- 这些消息没有明确揭示资助者身份；当前文本只能确认对方在引导玩家进入无尽花园并继续探索。

### 7. 尚未闭合的线索

- “Beta v07”进度直接写着“未完待续……”，说明主线文本仍未完结。
- 全息录音 9 是一段未翻译的密码化报告，包含“Req Derektor 4203”“Projekt ztatoz ekzel”等内容，现有中文语言文件没有对应解码文本。
- 车票、列车余额、部分广播和若干 NPC 交易相关文本仍是占位或未实现状态，不能当作完整剧情事实。

## 结构内书籍全文

这 4 本书的结构 NBT 已改为 `minecraft:written_book_content`，每页通过 `book.thermal_shock_reforged.*` 语言键加载；下面保留的是原始英文内容，便于核对。

### `cathedral.nbt`

**第 1 页**

```text
Come together sons and daughters.
Let the light seep into your heart, mind and soul.
Let it remold you in it's image.
What is a moment's agony in the face of eternal cold and emptiness?
```

### `lavender_palace.nbt`

**第 1 页**

```text
Hi. I'm ThermaDev. 
3 odd years ago I had a craaaazy idea to make my own dimension mod, after being exposed to minecraft modding basically my whole life. 'Course I had nooo idea what I was doing then, so the mod's gone through some growing pains. But I think it's pretty good now.
```

**第 2 页**

```text
Originally Thermal Shock was a lot more watery, mostly inspired by Subnautica, with the intent of having some crazy Terraria-style gameplay. I dropped the water aspect pretty quickly though, instead settling for the whole "snow apocalypse" thingy.
```

**第 3 页**

```text
Well, not long after Thermal Shock hit the internet, I played Hollow Knight for the first time, and I'm sure you can see how that's changed things, bugs and all.
```

**第 4 页**

```text
Not to say I don't enjoy making this mod, but nowadays I see it more as an outlet for the non-programmy things I like doing, art and music. I'm still going to see this thing to the end, of course. I'm not about to leave 3 years sit stuck in beta. Lol. 
```

### `shalespring_rubicon.nbt`

**第 1 页**

```text
I see the towers of Phoenum under marigold rain. Zenillia's crimson galleries buried beneath dunes of glistening ash, as the serpent of Ouroboros devours Ferraq in the heart of the castle.
The light of helion is blinding to most. Weaker eyes cannot understand it's true
```

**第 2 页**

```text
beauty and so they avert their gaze, they cower away in deeper caverns and behind stronger walls. They shut their windows and wash away the webs.
But the gathered mind knows no barriers, and the squall needs no invitation. I snap my fingers, click! And I see that great wall null and void.
```

**第 3 页**

```text
But not these walls, not this shining cobalt glass. Among the overgrown graves of the grandest of cities remains the humblest of towns. Before, I'd have thought this place weak too, but this lonely sight arrests me with guilt. The gardens of Uuril were never meant for this eerie quiet.
```

**第 4 页**

```text
I was blind. I was the blindest of all. My mind could not comprehend what the light told me and so it rejected the words, and spun a terrible lie.
In one last futile act of shame and regret, I bless this town of Shalespring, so you may remain safe and blind forevermore.
```

### `snowtower.nbt`

**第 1 页**

```text
PyLON EXTRAKTING/recykling:
I see them wanduring in the nite. Soldiurs marching thru mountains of twice-dead bugz, artificed metallik giants tearing down the pylons that once held our cities togethur. Then, as the old king's corpse rises, they take the   
```

**第 2 页**

```text
pylon harts into there dark blu towers, They break the corez apart like boiling snow.
That iz how they do it, that iz how they gro there mashines and build there grand towers. The secret is in the cores, great latent power from the Sun itself of which Phoenem dared to keep from us
```

**第 3 页**

```text
under loq and key.

Yes. Thats it; I must findBUILD the keys, like the onez the bugz of the towers use to hide there secrets away. 
There is work too be don.
```

## 广播、提示与剧情消息

### `message.thermal_shock_reforged.strider_controls`

```text
按 %s 发动攻击！
```

### `message.thermal_shock_reforged.atlas.destination_unavailable`

```text
寰宇图鉴目的地当前不可用。
```

### `message.thermal_shock_reforged.squall_seek_light`

```text
寻找光亮！
```

### `message.thermal_shock_reforged.hologram_monitor.recording_1`

```text
§7正在回放录音：
§7----------
§b"个人日志107，周期日期255K029。
§7----------
§b真不敢相信他们把我调到瞭望前哨了！我是说，我花了差不多一百个周期研究死灵学，结果这张证书就给我换来这个？？这里太冷，太亮，风老把底层虫子的文件吹到我脸上，到处都是油漆和§kXXXX§r§b的味道！他们在这里试验某种能量回收技术，我猜他们扔掉了普罗托伊·阳光，换了座祭坛。别问我为什么，我——唉，我们明明有实验室！一点死去的阳光能给我们的研究带来什么不同？而且不只我这么想！Arita也气坏了，她说明天要触电子游戏来，因为，反正我们照样领工资。这大概是我这一整个周期听到的最好的消息了。去他的。"
```

### `message.thermal_shock_reforged.hologram_monitor.recording_2`

```text
§7正在回放录音：
§7----------
§b"个人日志040，周期日期254K788。
§7----------
§b嘿，我直说了，我不太喜欢这位新主管。我不——……什么？？我这么想有理，你也知道！……好，那你告诉我——不不不，先回答这个：我们为什么已经200个周期没休过甜甜圈茶歇了？……不——这当然重要！这——这——这说明还有更大的问题！……看看Qo，他刚从她办公室出来——看他那一脸受够了的样子……什——你到底站哪边的？？？"
```

### `message.thermal_shock_reforged.hologram_monitor.recording_3`

```text
§7正在回放通讯：
§7----------
§b"好吧……
§7----------
§b我现在头疼得要命，所以长话短说。层云那边的实验原型，我现在就要。今天就要。Incognito生物机械部门也一样。你们那个“千灵”已经“开发中”50个周期了。今天。别再找借口。"
```

### `message.thermal_shock_reforged.hologram_monitor.recording_4`

```text
§7正在回放录音：
§7----------
§b"尊敬的最高主管……
§7----------
§b我和同事们对您的……“命令”越来越担忧。您让我们制作实验型模块化冰蚀无人机 XL，又在过去5个该死的太阳周期里，要求我们给冰蚀无人机 L做了8版修订。我们注意到工作方向正令人不安地转向军事化。我们不是军械设计所。我们造的是卫星和气象气球。层云部门的职责若有重大变化，请您在推进之前尽量沟通，谢谢。
§7----------
§b此致，层云部门的Qo，代表宇宙工虫联合会。"
```

### `message.thermal_shock_reforged.hologram_monitor.recording_5`

```text
§7正在回放录音：
§7----------
§b"真好笑，主管女士。
§7----------
§b我本来真想心平气和地处理这事，可您现在倒好！27页修订，3个周期交齐？？？抱歉，女士，办不到。我们不可能在那点时间里做出一辆12格战车的原型，我们又不是蜈蚣！坦白说，大家都已经精疲力尽了。宇宙工虫联合会决定停止层云部门的工作，直至另行通知。之后会有其他代表联系您，讨论……算了——您明白我的意思！"
```

### `message.thermal_shock_reforged.hologram_monitor.recording_6`

```text
§7正在回放录音：
§7----------
§b"开始录了吗？嗯，好，看这边……
§7----------
§b嗯，那就是寇。百周期最佳员工。其实已经两次了……你们都知道这意味着什么，对吧？“晋升”。他——……是啊，外科医生随时都会到，然后他们会，呃……他的身影从来没怎么出现在私人日志里，所以，想着在，呃，手术之前再录一次。嗯，祝手术手下留情，尽量少改动他的脑子。"
```

### `message.thermal_shock_reforged.hologram_monitor.recording_7`

```text
§7正在回放录音：
§7----------
§b"个人日志261，周期日期255K003。
§7----------
§b我们找到东西了。我们真的找到东西了！几个小时前我们挖进了这个，呃，就像——一个巨大的蒲公英花园，上面有很大的石头顶棚，还——……听到了吗？这里有些像虫子一样的植物。我们从没见过这种东西！嘿，看，有一只正过来——"
```

### `message.thermal_shock_reforged.hologram_monitor.recording_8`

```text
§7正在回放通讯：
§7----------
§b"伙计们，得撤了，整个行动取消。
§7----------
§b不——不，我们要回去，我们已经少了5只虫子。不！没事，我们会追上——先退到外围去。我——听着，我们连第一道墙都没过去！Perr的坦克顶上大概爬了10只黄蜂。该死的太阳，快——我不管，告诉最高主管，这个蜂巢我们进不去。我宁愿被降职，也不想被砍头！"
```

### `message.thermal_shock_reforged.hologram_monitor.recording_9`

```text
§7正在回放录音：
§7----------
§b"Req Derektor 4203. Ziqel 1K977.
§7----------
§bProjekt ztatoz ekzel. tranzver zekzez 47. Invekzia ztatoz minor, puri 59. Notel; Sujekt 7202 evoluzia orol atvanzia, sujekt 841 evoluzia praza metenenz divizia nekro, sujekt 1009 ditto azziztenz. Theor zu projekt konkluzia. Zu zekzez puri total, pluz perzonel.
§7----------
§b"Req Derektor 4203, fin."
```

### `message.thermal_shock_reforged.ignition_score.-2`

```text
群系点火评分：§e-2
§c此处存在涌能塔，冻伤会阻止治疗
 
§c花园植物无法在此生长
```

### `message.thermal_shock_reforged.ignition_score.-1`

```text
群系点火评分：§e-1
 
§c花园植物无法在此生长
```

### `message.thermal_shock_reforged.ignition_score.0`

```text
群系点火评分：§e0
§a此群系状态稳定！
```

### `message.thermal_shock_reforged.ignition_score.1`

```text
群系点火评分：§e1
 
```

### `message.thermal_shock_reforged.ignition_score.2`

```text
群系点火评分：§e2
§c此处存在感染，请谨慎行事
 
```

### `message.thermal_shock_reforged.ignition_score.irrelevant`

```text
群系点火评分：不适用
```

### `message.thermal_shock_reforged.fixed_structure_start`

```text
正在 X330 Z330 生成，请稍候……
```

### `message.thermal_shock_reforged.fixed_structure_failed`

```text
结构生成失败，物品未消耗。
```

### `message.thermal_shock_reforged.keystone.gift`

```text
§c<§kSopel§r§c> 我有份礼物要送给你 :^)
```

### `message.thermal_shock_reforged.keystone.invitation`

```text
§c<§k某处§r§c> 来 X1333 Z1333 找我。待会儿见……
```

### `message.thermal_shock_reforged.atlas.already_here`

```text
§c老兄，你已经在这里了……
```

### `message.thermal_shock_reforged.train.insufficient_credit`

```text
§a余额不足……
```

### `message.thermal_shock_reforged.altar.unlocked`

```text
§a你已解锁所有无尽花园祭坛！
```

### `message.thermal_shock_reforged.altar.phobotite_unsealed`

```text
§b守护世界中惧光石的能量已经消散！
```

### `message.thermal_shock_reforged.cache.locked`

```text
§a这是一处上锁的贮藏箱；手持钥匙右键打开。
```

### `message.thermal_shock_reforged.cache.open`

```text
§a这是一处贮藏箱；右键打开。
```

### `message.thermal_shock_reforged.keystone.you_know`

```text
§c<§k某处§r§c> 你知道该怎么做……
```

### `message.thermal_shock_reforged.keystone.puzzle_buttons`

```text
§e结构四周的四个按钮需要全部按下。
```

### `message.thermal_shock_reforged.keystone.puzzle_lever`

```text
§a四个按钮都已按下，拉下结构内的拉杆。
```

### `message.thermal_shock_reforged.phobotite.too_cold`

```text
§a这处矿石太冷了，你还无法采集……
```

### `message.thermal_shock_reforged.door.herald_keys`

```text
§a这扇门需要三把先驱密钥……
```

### `message.thermal_shock_reforged.world_tour.congratulations`

```text
§a恭喜 Fscht 赢得世界巡回竞赛！收下你的专属奖杯吧！:D
```

### `message.thermal_shock_reforged.cavebloomer.awoken`

```text
§d愤怒的洞穴绽花已苏醒！
```

### `message.thermal_shock_reforged.cavebloomer.defeated`

```text
§d愤怒的洞穴绽花已被击败！
```

### `message.thermal_shock_reforged.deployment.received`

```text
§d已收到消息！
```

### `message.thermal_shock_reforged.sopel.arrival`

```text
§c<§kSopel§r§c> 你总算来了。:^)
```

### `message.thermal_shock_reforged.altar.already_unlocked`

```text
§7你已经解锁过祭坛了。
```

### `message.thermal_shock_reforged.altar.locked`

```text
§c你尚未解锁祭坛，手持凛冬祭坛密钥右键祭坛即可解锁。
```

## 进度标题与描述

### `advancements.ruby_get.title`

```text
赤冰
```

### `advancements.blackpowder_get.descr`

```text
获得黑火药香料...不过你最好还是别碰这东西。
```

### `advancements.knight_edge_get.title`

```text
汝乃天命之人！
```

### `advancements.borean_tundra_advancement.title`

```text
北境冻土
```

### `advancements.cavebloomer_kill.descr`

```text
击败愤怒的洞穴绽花
```

### `advancements.yoylecake.descr`

```text
富含铁质。
```

### `advancements.hpsm_get.descr`

```text
使用 HPSM 添加或移除雪层
```

### `advancements.rain_drum_get.title`

```text
我去这不是阿门碎拍吗也太顶了 这可是我最爱的采样节奏 必须给每首歌都加一段 我爱阿门碎拍
```

### `advancements.leaf_it_to_me_3.title`

```text
交给我吧！3
```

### `advancements.garden_plant_get.title`

```text
绿手指
```

### `advancements.borean_forest_advancement.title`

```text
北境森林
```

### `advancements.errant_get.title`

```text
棋逢对手
```

### `advancements.blasphemy_2.descr`

```text
深入墓地下方的秽血浴场，击败三名先驱，进入大亵渎者的密室并获取腐化核心碎片。
```

### `advancements.glastrekker_armor_get.descr`

```text
为晶甲兽装备护甲，将其驯服。
```

### `advancements.icebreaker_2.title`

```text
破冰器 2
```

### `advancements.breather_sponge_get.descr`

```text
用吸管从供氧海绵中汲取氧气。
```

### `advancements.drinking_straw_get.descr`

```text
制作一根搞怪吸管，因为它真的超搞怪的！！！
```

### `advancements.blasphemy_1.descr`

```text
接收到礼宾使的源初之血委托
```

### `advancements.palace_get.descr`

```text
感谢游玩！
```

### `advancements.blasphemy_4.title`

```text
亵渎 其四
```

### `advancements.babies_or_pets.descr`

```text
把普兰奇们送回劳特那里
```

### `advancements.whisper_advancement.descr`

```text
与角色型生物进行交易
```

### `advancements.thermofebruary_get.descr`

```text
在热二月活动期间吃下硫磺蛋糕，兑换一份酷炫奖励
```

### `advancements.purifier_get.title`

```text
失落的无尽花园
```

### `advancements.stardust_garden_advancement.title`

```text
星尘花园
```

### `advancements.zenith_get.descr`

```text
集齐一整套天顶护甲
```

### `advancements.lapidary_get.title`

```text
哇哦好闪！
```

### `advancements.uneasy_alliance_1.descr`

```text
交给机械师一块层云合金。也许这会成为一段互惠关系的开始？
```

### `advancements.lapidary_get.descr`

```text
使用宝石加工台打磨一颗宝石。
```

### `advancements.errands_get.descr`

```text
部分角色型生物会提供额外进度线，相关内容将在此显示！
```

### `advancements.lod_pozar_get.descr`

```text
击败暴雪计划最强机械双子——洛德与波扎
```

### `advancements.malachite_get.descr`

```text
从矿石露头或晶洞中获取孔雀石。
```

### `advancements.zenith_get.title`

```text
天顶亦会为你骄傲
```

### `advancements.loqendia_get.title`

```text
热力震荡
```

### `advancements.bismuth_get.descr`

```text
从矿石露头或晶洞中获取铋（部分世界不会生成铋）。
```

### `advancements.spice_get.descr`

```text
采集香料，切勿食用。
```

### `advancements.bloodstone_get.title`

```text
黑浊血
```

### `advancements.glaciation_get.descr`

```text
小心别滑倒……
```

### `advancements.herald_get.descr`

```text
喂养先驱直至其绽放，夺取其密钥
```

### `advancements.spear_kill_get.descr`

```text
用长矛类武器击杀一只生物。
```

### `advancements.uneasy_alliance_1.title`

```text
脆弱同盟
```

### `advancements.zap_gauntlet_get.descr`

```text
找到并击败一只千灵，它们常被关在地下海洋基地中。
```

### `advancements.blasphemy_1.title`

```text
亵渎 其一
```

### `advancements.leaf_it_to_me_2.title`

```text
交给我吧！2
```

### `advancements.graveyard_advancement.title`

```text
墓园与秽血浴场
```

### `advancements.siibol_get.descr`

```text
用桶收集一只悉柏尔。
```

### `advancements.ferrous_get.title`

```text
好次。
```

### `advancements.blasphemy_3.title`

```text
亵渎 其三
```

### `advancements.icidrozerk_mace_kill_get.title`

```text
别再自讨苦吃了，别再自讨苦吃了。
```

### `advancements.vermillion_woods_advancement.title`

```text
朱红木林
```

### `advancements.squall_get.title`

```text
现在你感受到压力了吧——压——力——
```

### `advancements.squall_kill_get.descr`

```text
不借助牛奶击败狂风怪
```

### `advancements.leaf_it_to_me_1.descr`

```text
接受摄影师的委托，拍摄一张北极洞穴绽花的照片，它们分布在冰川群系。
```

### `advancements.keystone_get.descr`

```text
从神秘资助者处取回界域基石，将其放置在3x3黑曜石平台上...
```

### `advancements.cobalt_get.title`

```text
冰点之下。
```

### `advancements.cooked_torpedo_consume_get.title`

```text
噫——
```

### `advancements.carborundum_get.title`

```text
一大步
```

### `advancements.climbing_pick_get.title`

```text
掘地求升
```

### `advancements.luminous_sea_get.descr`

```text
……宛如凝望整片夜空
```

### `advancements.stratus_reactor_get.title`

```text
它们实在太多了！
```

### `advancements.frigid_altar_get.title`

```text
神秘坩埚
```

### `advancements.beta_complete.title`

```text
未完待续……
```

### `advancements.ferrous_altar_get.descr`

```text
制作一座凛冬祭坛的复刻品。
```

### `advancements.gardenling_get.title`

```text
池中薄雾
```

### `advancements.fabricator_get.title`

```text
合成进阶：电光霹雳
```

### `advancements.frost_titan_core_kill_get.title`

```text
精准弹射
```

### `advancements.corroded_get.descr`

```text
从幻光海域获取锈蚀岩石，或用铜与含铁岩石合成。
```

### `advancements.snowcone_consume_get.descr`

```text
食用一份冰爽雪锥。
```

### `advancements.blasphemy_2.title`

```text
亵渎 其二
```

### `advancements.leaf_it_to_me_3.descr`

```text
带着洞穴绽花的照片回到页岩泉镇。
```

### `advancements.errant_get.descr`

```text
击败亡魂勇士，一名被亡灵大军腐化的冒险者同胞
```

### `advancements.drinking_straw_get.title`

```text
畅饮搞怪风
```

### `advancements.purifier_get.descr`

```text
净化这片大地
```

### `advancements.climbing_pick_get.descr`

```text
获得攀岩镐，可在冰蚀基地中找到。
```

### `advancements.leaf_it_to_me_1.title`

```text
交给我吧！1
```

### `advancements.whisper_advancement.title`

```text
和平交涉
```

### `advancements.babies_or_pets.title`

```text
孩子还是宠物？
```

### `advancements.cauldron_get.title`

```text
秘银到手啦 :)
```

### `advancements.squall_kill_get.title`

```text
圣光降临！
```

### `advancements.ruby_get.descr`

```text
获得一颗抛光红宝石（部分世界不会生成红宝石）。
```

### `advancements.breather_sponge_get.title`

```text
我 需 要 它！
```

### `advancements.sanctum_get.title`

```text
崩塌之塔
```

### `advancements.cobalt_heights_get.descr`

```text
空气中噼啪作响，充满静电……
```

### `advancements.icidrozerk_mace_kill_get.descr`

```text
用钛合金战锤击杀一只冰蚀巨兽，哼，罪有应得...
```

### `advancements.knight_edge_get.descr`

```text
获得骑士圣剑！
```

### `advancements.blasphemy_4.descr`

```text
返回大教堂，获取万灵药
```

### `advancements.cavebloomer_kill.title`

```text
谦逊的除草机
```

### `advancements.icebreaker_1.descr`

```text
收到机械师索取病毒石组件的请求
```

### `advancements.vermillion_woods_advancement.descr`

```text
你感受到了来自地底的震动...
```

### `advancements.carborundum_get.descr`

```text
装备一整套碳化硅护甲。
```

### `advancements.rain_drum_get.descr`

```text
将雨鼓放置在热铁块顶部
```

### `advancements.torpedo_consume_get.descr`

```text
赌上自己和队友的性命，吃下一块生鱼雷肉（万一它其实超好吃呢？？？）
```

### `advancements.yoylecake.title`

```text
约伊尔蛋糕！
```

### `advancements.loqendia_get.descr`

```text
在主世界的3x3黑曜石平台上使用界域基石，搭建寰宇图鉴，进入冰封花园
注意：黑曜石需要平放，而不是像地狱门那样竖着放。
```

### `advancements.spear_kill_get.title`

```text
就算给我三十九尺半的杆子，我都不碰你！
```

### `advancements.errands_get.title`

```text
热力震荡委托
```

### `advancements.spice_get.title`

```text
嗨，我爱金币！
```

### `advancements.blackpowder_get.title`

```text
这就是香料上头的样子
```

### `advancements.thermofebruary_get.title`

```text
热二月！
```

### `advancements.borean_tundra_advancement.descr`

```text
飘落的雪花刺痛了你的肌肤...
```

### `advancements.palace_get.title`

```text
薰衣草宫殿
```

### `advancements.stardust_garden_advancement.descr`

```text
一片散发着微光的绿色星云笼罩了天空...
```

### `advancements.graveyard_advancement.descr`

```text
空气中弥漫着呕血的腥气...
```

### `advancements.malachite_get.title`

```text
末影钢
```

### `advancements.lod_pozar_get.title`

```text
并肩作战
```

### `advancements.frost_titan_core_kill_get.descr`

```text
用空中引爆的信号枪核心击杀寒霜泰坦，这才叫真正的「天罚」！
```

### `advancements.siibol_get.title`

```text
这云够味啊兄弟
```

### `advancements.stratus_reactor_get.descr`

```text
启动层云反应堆，祝你好运 :)
```

### `advancements.snowcone_consume_get.title`

```text
脑壳冻僵啦！
```

### `advancements.cooked_torpedo_consume_get.descr`

```text
你真没劲
```

### `advancements.torpedo_consume_get.title`

```text
要么搞大事，要么回家去！
```

### `advancements.leaf_it_to_me_2.descr`

```text
顺利拍下洞穴绽花的照片……？
```

### `advancements.bloodstone_get.descr`

```text
获得抛光血石（部分世界不会生成血石）。
```

### `advancements.borean_forest_advancement.descr`

```text
树木摸起来竟带着暖意...
```

### `advancements.blasphemy_3.descr`

```text
制作一台提取机，可从核心碎片中提取源初之血。
```

### `advancements.squall_get.descr`

```text
遭遇狂风怪，你就不能拿个火把吗？？
```

### `advancements.icebreaker_2.descr`

```text
在装配机中制作病毒石。
```

### `advancements.ferrous_get.descr`

```text
获取含铁岩石，一种富含碳元素的岩石聚合体。
```

### `advancements.corroded_get.title`

```text
说不定能吃，谁知道呢。
```

### `advancements.zap_gauntlet_get.title`

```text
放大效应
```

### `advancements.sanctum_get.descr`

```text
前往钴石高地，登上暴雪圣所并勇闯档案馆。
```

### `advancements.herald_get.title`

```text
无声无息，无忆无念
```

### `advancements.glaciation_get.title`

```text
冰川期
```

### `advancements.icebreaker_1.title`

```text
破冰器 1
```

### `advancements.cauldron_get.descr`

```text
用坩埚捕获一只三叶虫，可用于合成香料堆叠，或将单簇香料分解为低等级。
```

### `advancements.hpsm_get.title`

```text
雪活儿
```

### `advancements.gardenling_get.descr`

```text
制作一个花园灵
```

### `advancements.luminous_sea_get.title`

```text
雪泥海岸与幻光海域
```

### `advancements.frigid_altar_get.descr`

```text
用特殊密钥解锁凛冬祭坛，可从塔尖中提取深寒能量。
```

### `advancements.garden_plant_get.descr`

```text
在盆中种下一颗种子
```

### `advancements.glastrekker_armor_get.title`

```text
才不是你想的那样！
```

### `advancements.cobalt_heights_get.title`

```text
钴石高地
```

### `advancements.bismuth_get.title`

```text
深空33层
```

### `advancements.fabricator_get.descr`

```text
使用暴雪阵营特制合金建造制造机。
```

### `advancements.keystone_get.title`

```text
超凡脱俗
```

### `advancements.cobalt_get.descr`

```text
获取钴矿，遇冷变硬，遇热变软。
```

### `advancements.ferrous_altar_get.title`

```text
足不出户，坐拥祭坛
```

### `advancements.beta_complete.descr`

```text
抵达 Beta v07 的终点。感谢游玩！
```

## 远极相机图鉴文本

### `camera.thermal_shock_reforged.catalog.maiden`

```text
§a“少女。” §r受诅咒的刑具
类型：小型机械造物
§a弱点：氯属性
```

### `camera.thermal_shock_reforged.catalog.probosker`

```text
§a“长鼻兽。” §r爱吃鱼的可爱森林小动物
类型：小型昆虫
```

### `camera.thermal_shock_reforged.catalog.snobol`

```text
§a“斯诺波尔。” §r适应能力极强的双足昆虫
类型：小型昆虫
```

### `camera.thermal_shock_reforged.catalog.explosive_snobol`

```text
§a“爆炸型斯诺波尔。” §r你怎么能这样！！！！！！！！！
类型：小型昆虫
```

### `camera.thermal_shock_reforged.catalog.razorwing`

```text
§a“剃翼。” §r高大的不会飞的虫子，是迷途旅人的理想坐骑
类型：小型昆虫
```

### `camera.thermal_shock_reforged.catalog.trilobite`

```text
§a“三叶虫。” §r无翅滑翔虫，常睡在花盆和炼药锅里
类型：小型昆虫
```

### `camera.thermal_shock_reforged.catalog.sentrarius`

```text
§a“哨兵虫。” §r温顺的长角昆虫，以骨头为食
类型：小型昆虫
```

### `camera.thermal_shock_reforged.catalog.willograde`

```text
§a“滚球怪。” §r擅长防御的重型虫子，喜欢香料和挖掘
类型：小型昆虫
```

### `camera.thermal_shock_reforged.catalog.cerulean_jellercel`

```text
§a“凝胶储能块。” §r腿太多了
类型：小型昆虫
```

### `camera.thermal_shock_reforged.catalog.aerocel`

```text
§a“云浮水母。” §r总是面带微笑
类型：中型昆虫
```

### `camera.thermal_shock_reforged.catalog.chilloo`

```text
§a“奇噜。” §r冷得刺骨，却很讨人喜欢
类型：小型昆虫
```

### `camera.thermal_shock_reforged.catalog.louse`

```text
§a“虱虫。” §r嗯，昆虫……哈啊啊啊啊啊
类型：小型昆虫
```

### `camera.thermal_shock_reforged.catalog.glastrekker`

```text
§a“晶甲兽。” §r大步向前
类型：中型鱼类
```

### `camera.thermal_shock_reforged.catalog.pixie`

```text
§a“小精灵。” §r来自无尽花园失落小行星带的发光昆虫
类型：小型昆虫
```

### `camera.thermal_shock_reforged.catalog.pillbud`

```text
§a“药丸虫。” §r流行歌剧——啊——啊！
类型：小型植物
§c抗性：氯属性
```

### `camera.thermal_shock_reforged.catalog.gelshooter`

```text
§a“凝胶射手。” §r掘墓者……要来了啊啊啊啊啊
类型：小型植物
§c抗性：氯属性
```

### `camera.thermal_shock_reforged.catalog.tesla_reed`

```text
§a“特斯拉芦苇。” §r喜欢钢琴声
类型：小型植物
§c抗性：氯属性
```

### `camera.thermal_shock_reforged.catalog.gardenling`

```text
§a“花园灵。” §r帮助古老植物在寒冷中生长
类型：小型植物
§c抗性：氯属性
```

### `camera.thermal_shock_reforged.catalog.crystal_koi`

```text
§a“水晶锦鲤。” §r花园各处都能找到的闪亮鱼类
类型：小型鱼类
```

### `camera.thermal_shock_reforged.catalog.chromefish`

```text
§a“铬鳞鱼。” §r别拿你那只大眼睛盯着我！
类型：小型鱼类
```

### `camera.thermal_shock_reforged.catalog.siibol`

```text
§a“悉柏尔。” §r湿得不像虫，干得不像鱼
类型：小型悉柏尔
```

### `camera.thermal_shock_reforged.catalog.bonerahna`

```text
§a“骨颌鱼。” §r凶残的食肉鱼
类型：小型鱼类
§a弱点：铁属性
```

### `camera.thermal_shock_reforged.catalog.scarlet_ray`

```text
§a“猩红射线怪。” §r温顺的底栖鱼
类型：小型鱼类
```

### `camera.thermal_shock_reforged.catalog.pondler`

```text
§a“塘鱼。” §r弹跳
类型：小型鱼类
```

### `camera.thermal_shock_reforged.catalog.torpedo`

```text
§a“鱼雷怪。” §r身披银鳞的爆炸性海洋生物
类型：小型鱼类
```

### `camera.thermal_shock_reforged.catalog.springling`

```text
§a“弹性幼体。” §r由赫利昂赋予生命的弹性凝胶
类型：小型机械造物
```

### `camera.thermal_shock_reforged.catalog.sola`

```text
§a“索拉。” §r烫手土豆
类型：小型机械造物
§a弱点：冰霜属性
```

### `camera.thermal_shock_reforged.catalog.magnetite`

```text
§a“磁铁矿怪。” §r由星界矿物构成的有机簇
类型：小型异星生物
§a弱点：冰霜属性
```

### `camera.thermal_shock_reforged.catalog.lens`

```text
§a“透镜怪。” §r大家都在看我！
类型：小型植物
§c抗性：氯属性
§a弱点：冰霜属性
```

### `camera.thermal_shock_reforged.catalog.frostilyte`

```text
§a“霜晶怪。” §r暴雪计划中等级最低的飞蛾
类型：小型虫类
§c抗性：冰霜属性
§a弱点：火焰属性
```

### `camera.thermal_shock_reforged.catalog.frostilyte_soldier`

```text
§a“霜晶士兵。” §r擅长近身作战
类型：小型虫类
§c抗性：冰霜属性
§a弱点：火焰属性
```

### `camera.thermal_shock_reforged.catalog.frostilyte_gunner`

```text
§a“霜晶怪枪手。” §r遭到削弱
类型：小型虫类
§c抗性：冰霜属性
§a弱点：火焰属性
```

### `camera.thermal_shock_reforged.catalog.frostilyte_cryomancer`

```text
§a“霜晶咒术师。” §r暴雪计划亡灵部门的学徒
类型：小型虫类
§c抗性：冰霜属性
§a弱点：火焰属性
```

### `camera.thermal_shock_reforged.catalog.kilolyte`

```text
§a“千灵。” §r重击手，公司保龄球赛的不败选手
类型：中型虫类
§c抗性：冰霜属性
§c抗性：铁属性
§c抗性：氯属性
```

### `camera.thermal_shock_reforged.catalog.icidrone`

```text
§a“冰蚀无人机。” §r脆弱的单魂自动机械
类型：小型机械造物
§a弱点：火焰属性
```

### `camera.thermal_shock_reforged.catalog.icidrogrunt`

```text
§a“冰蚀杂兵。” §r廉价制造的无脑造物
类型：小型机械造物
§a弱点：火焰属性
```

### `camera.thermal_shock_reforged.catalog.icidrozerk`

```text
§a“冰蚀巨兽。” §r武器化的灵魂沙牢笼
类型：小型机械造物
§c抗性：冰霜属性
§a弱点：火焰属性
```

### `camera.thermal_shock_reforged.catalog.protoi_gamma`

```text
§a“普罗托伊·伽马。” §r古代地形改造技术
类型：小型机械造物
```

### `camera.thermal_shock_reforged.catalog.protoi_delta`

```text
§a“普罗托伊·德尔塔。” §r四处游荡的守卫者
类型：小型机械造物
```

### `camera.thermal_shock_reforged.catalog.protoi_sunbeam`

```text
§a“普罗托伊·阳光。” §r难以置信地无法移动，也难以置信地无法阻挡
类型：中型机械造物
```

### `camera.thermal_shock_reforged.catalog.sootle`

```text
§a“煤烟灵。” §r虫子们渴望矿井
类型：小型虫类
§c抗性：铁属性
§c抗性：火焰属性
```

### `camera.thermal_shock_reforged.catalog.sootling`

```text
§a“煤烟怪。” §r数量众多时十分致命
类型：小型虫类
§c抗性：铁属性
§c抗性：火焰属性
```

### `camera.thermal_shock_reforged.catalog.beetle_thrasher`

```text
§a“巨甲战虫。” §r崇拜鲜血的异见者
类型：小型虫类
§c抗性：铁属性
§c抗性：火焰属性
```

### `camera.thermal_shock_reforged.catalog.beetle_crasher`

```text
§a“甲虫冲撞者。” §r吃岩石
类型：小型虫类
§c抗性：铁属性
§c抗性：火焰属性
```

### `camera.thermal_shock_reforged.catalog.dormant_beetle_crasher`

```text
§a"未知生物." §r无可描述
类型：小型未知生物
§c抗性未知
§a弱点未知
```

### `camera.thermal_shock_reforged.catalog.galvanite`

```text
§a“电锌矿怪。” §r被盗的设计图
类型：小型机械造物
§c抗性：冰霜属性
§c抗性：铁属性
§a弱点：火焰属性
```

### `camera.thermal_shock_reforged.catalog.forecast`

```text
§a“预报者。” §r多云，可能伴有银子
类型：小型机械造物
§c抗性：冰霜属性
§a弱点：火焰属性
```

### `camera.thermal_shock_reforged.catalog.ripple`

```text
§a“涟漪。” §r“它是时空的一部分”
类型：小型机械造物
§c抗性：冰霜属性
§c抗性：铁属性
§a弱点：火焰属性
```

### `camera.thermal_shock_reforged.catalog.ribbonblade_galvanite`

```text
§a“缎刃电锌矿怪。” §r冷酷的精干机器
类型：小型机械造物
§c抗性：冰霜属性
§c抗性：铁属性
§a弱点：火焰属性
```

### `camera.thermal_shock_reforged.catalog.graver`

```text
§a“掘墓者。” §r平平无奇
类型：小型亡灵
§a弱点：铁属性
§a弱点：氯属性
```

### `camera.thermal_shock_reforged.catalog.neon_lime_graver`

```text
§a“霓虹青掘墓者！！！。” §r我绿得发亮，但可不好惹
类型：小型亡灵
```

### `camera.thermal_shock_reforged.catalog.lanternbearer`

```text
§a“持灯者。” §r离我远点
类型：小型亡灵
§c抗性：火焰属性
§a弱点：氯属性
```

### `camera.thermal_shock_reforged.catalog.raincatcher`

```text
§a“集雨者。” §r依旧没什么趣味
类型：小型亡灵
```

### `camera.thermal_shock_reforged.catalog.skeletitan`

```text
§a“骸骨泰坦。” §r由骨骼、象牙和霉菌构成的巨型傀儡
类型：小型亡灵
§a弱点：铁属性
§a弱点：氯属性
```

### `camera.thermal_shock_reforged.catalog.errant`

```text
§a“亡魂勇士。” §r颇有些本事的剑士尸骸
类型：小型亡灵
§a弱点：铁属性
```

### `camera.thermal_shock_reforged.catalog.skullscor`

```text
§a“焚颅怪。” §r戴着上一顿猎物的头骨
类型：小型虫类
§a弱点：铁属性
```

### `camera.thermal_shock_reforged.catalog.spectre`

```text
§a“哨鸣者。” §r富有旋律的幽灵
类型：小型亡灵
§c抗性：铁属性
§a弱点：氯属性
```

### `camera.thermal_shock_reforged.catalog.spirit`

```text
§a“幽魂。” §r敏捷但脆弱
类型：小型亡灵
§a弱点：冰霜属性
§a弱点：氯属性
```

### `camera.thermal_shock_reforged.catalog.effigy`

```text
§a“机械塑像。” §r你的冰箱还在运行吗？
类型：小型机械造物
§c抗性：铁属性
§a弱点：火焰属性
```

### `camera.thermal_shock_reforged.catalog.assimilator`

```text
§a“同化者。” §rLamar，快从那上面下来！
类型：小型亡灵
§a弱点：氯属性
```

### `camera.thermal_shock_reforged.catalog.assimilated_graver`

```text
§a“腐化掘墓者。” §rYabba，我的糖霜！
类型：小型亡灵
§a弱点：氯属性
```

### `camera.thermal_shock_reforged.catalog.razoraptor`

```text
§a“锋爪兽。” §r比大多数噬寄虫都聪明
类型：小型亡灵
§a弱点：氯属性
```

### `camera.thermal_shock_reforged.catalog.monolith`

```text
§a“巨石像。” §r感染的伟大先兆
类型：中型亡灵
§c抗性未知
§a弱点未知
```

### `camera.thermal_shock_reforged.catalog.dormant_monolith`

```text
§a"未知生物." §r无可描述
类型：中型未知生物
§c抗性未知
§a弱点未知
```

### `camera.thermal_shock_reforged.catalog.herald`

```text
§a“先驱。” §r被过度包装的钥匙扣
类型：中型亡灵
§c抗性未知
§a弱点未知
```

### `camera.thermal_shock_reforged.catalog.squall`

```text
§a"未知生物." §r无可描述
类型：中型未知生物
§c抗性未知
§a弱点未知
```

### `camera.thermal_shock_reforged.catalog.flare`

```text
§a“信号弹怪。” §r感染创造的注入烈焰的造物
类型：小型亡灵
§a弱点：氯属性
```

### `camera.thermal_shock_reforged.catalog.infestophage_seed`

```text
§a"未知生物." §r无可描述
类型：小型未知生物
§c抗性未知
§a弱点未知
```

### `camera.thermal_shock_reforged.catalog.infestophage`

```text
§a“感染噬体。” §r还饿着
类型：小型虫类
§a弱点：氯属性
```

### `camera.thermal_shock_reforged.catalog.mangler`

```text
§a“畸变体。” §r感染创造的飞行造物
类型：小型亡灵
§a弱点：氯属性
```

### `camera.thermal_shock_reforged.catalog.blighted_spore`

```text
§a“孢子。” §r最简单的感染形态
类型：小型亡灵
§a弱点：氯属性
```

### `camera.thermal_shock_reforged.catalog.angry_cavebloomer`

```text
§a“愤怒的洞穴绽花。” §r爱打盹，脾气还很差
类型：大型植物
§c抗性未知
§a弱点未知
```

### `camera.thermal_shock_reforged.catalog.budding_bloomer`

```text
§a“萌芽绽花。” §r爱爆炸的小家伙
类型：小型植物
```

### `camera.thermal_shock_reforged.catalog.dandybloomer`

```text
§a“蒲公绽花。” §r烦人
类型：小型植物
```

### `camera.thermal_shock_reforged.catalog.icidrogunner`

```text
§a“冰卓枪手。” §r嘴巴好凉，嘴巴好凉
类型：大型机械造物
§c抗性未知
§a弱点未知
```

### `camera.thermal_shock_reforged.catalog.icidroburner`

```text
§a“冰卓喷火兵。” §r嘴巴好烫，嘴巴好烫
类型：大型机械造物
§c抗性未知
§a弱点未知
```

### `camera.thermal_shock_reforged.catalog.whisper`

```text
§a“低语者。” §r滞留在月球上的居民
类型：小型异星生物
```

### `camera.thermal_shock_reforged.catalog.shutterbug`

```text
§a“尤妮特·亚当斯基。” §r业余摄影师
类型：小型虫类
```

### `camera.thermal_shock_reforged.catalog.florist`

```text
§a“乌玛贝尔。” §r搜索“休息中的虫脸”
类型：小型亡灵
```

### `camera.thermal_shock_reforged.catalog.kofoni`

```text
§a“科佛尼。” §r我才没有囤积癖，是你有囤积癖！
类型：小型虫类
```

### `camera.thermal_shock_reforged.catalog.benzor`

```text
§a“本佐。” §r他会拉小提琴吗？
类型：小型虫类
```

### `camera.thermal_shock_reforged.catalog.concierge`

```text
§a“礼宾使。” §r还没感染，还是只是发育得晚？
类型：小型虫类
```

### `camera.thermal_shock_reforged.catalog.defector_machinist`

```text
§a“寇。” §r逃犯 #001
类型：小型虫类
```

### `camera.thermal_shock_reforged.catalog.huntress`

```text
§a“女猎手。” §r百万分之一……或者说几百个里就有一个
类型：中型虫类
```

### `camera.thermal_shock_reforged.catalog.ly`

```text
§a“莉。” §r欢迎再来
类型：小型虫类
```

### `camera.thermal_shock_reforged.catalog.laut`

```text
§a“劳特。” §r我完全不知道自己在看什么……某种东西吧
类型：小型……呃，某种东西
```

## NPC 对话与剧情界面文本

### `gui.thermal_shock_reforged.conc_info_florist.label_i_havent_had_much_trouble_sin`

```text
❄ 此后倒没遇到什么麻烦，所以
```

### `gui.thermal_shock_reforged.qo_lab_safe_1.label_i_could_actually_do_with_s`

```text
❄ 懂了吗？无所谓。快去办。
```

### `gui.thermal_shock_reforged.une_tip_17.label_impressive_stuff`

```text
❄ 你会以为城堡能让你躲过
```

### `gui.thermal_shock_reforged.qo_lab_errand_1_e.label_kutty`

```text
❄ 如果你能给我拿来一把祭坛钥匙，还有，嗯，能让我拆用的导电物件……理论上，我想我能调校你的界域基石，让它匹配档案馆的护盾锁，你就能把他们的家底都挖空了！
❄ 听懂了吗？无所谓。快去办。
```

### `gui.thermal_shock_reforged.conc_errand_1_epl_3.label_calls_for_a_rather_special_ing`

```text
❄ 这赤色花蜜必须新鲜纯净，未被沾染
```

### `gui.thermal_shock_reforged.qo_stratus_alloy_3a.label_are_you_trying_to_blow_us_up`

```text
❄ 连我都不知道那配方！
```

### `gui.thermal_shock_reforged.qo_feddy_fivebears.label_are_you_trying_to_blow_us_up`

```text
❄ 我该觉得你很厉害吗？你为什么
```

### `gui.thermal_shock_reforged.qo_stellar_scrap_1.label_kutty`

```text
❄ 暴风雪又要起来了，是吧？
```

### `gui.thermal_shock_reforged.une_info_blizzard.label_kutty`

```text
❄ 你知道什么事特别奇怪吗？
❄ 我们那边可没有“暴雪计划”！
❄ 我们当然用过他们的发明，比如电视、汽车、感应灯，可是从来没有挂过暴雪的名号。
```

### `gui.thermal_shock_reforged.qo_neural_chip_1.label_kutty`

```text
❄ 呃，把那东西拿远点！
```

### `gui.thermal_shock_reforged.kofoni_malachite_info.label_kutty`

```text
❄ 嗯？这绿油油亮晶晶的玩意儿？
```

### `gui.thermal_shock_reforged.uma_info_concierge.label_id_hate_to_be`

```text
❄ 要是死在那种东西手里我可受不了。
```

### `gui.thermal_shock_reforged.laut_errand_1_reject.label_kutty`

```text
❄ 嗯，我不怪你啦。
```

### `gui.thermal_shock_reforged.civil_8_tlk_2.label_kutty`

```text
❄ 暖和 :)
```

### `gui.thermal_shock_reforged.une_tip_4.label_kutty`

```text
❄ 有些生物群系比其他地方危险得多，去那些陌生古怪的地方时，一定要做好充分准备。
❄ 要是觉得某个阴森的生物群系太吓人，
❄ 中途离开，之后再来探索也没什么丢人的。以后再回来继续冒险吧！
```

### `gui.thermal_shock_reforged.qo_lab_intro_1.label_kutty`

```text
❄ 搞什么？？闯进别人家之前，不知道先敲门吗？？？
❄ 算了，进都进来了。只要记住，什么都别碰。
```

### `gui.thermal_shock_reforged.une_tip_12.label_kutty`

```text
❄ 有些矿石会有“姐妹款”。
❄ 它们基本上差不多，只是颜色不同——我本来想说口味来着。
```

### `gui.thermal_shock_reforged.kofoni_benzor_info.label_kutty`

```text
❄ 他是个好人。
```

### `gui.thermal_shock_reforged.laven_civil_1_tlk.label_our_snow_is_really_dusty_for_s`

```text
❄ 嗯……你该不会是 §6Patreon
```

### `gui.thermal_shock_reforged.sopel_intro_epl_5b.label_kutty`

```text
❄ 哎呀！不过也没差，毕竟这还只是测试版。我本来应该有一场超酷的首领战，本该现在就登场，不过你得等到版本 1 :(
❄ 我也不知道那是什么时候，不过到时应该会有相关消息，之后会发在 瑟姆aDevYT 上。
```

### `gui.thermal_shock_reforged.conc_errand_1_epl_f.label_kutty`

```text
❄ 看来你已经拿到萃取液了！
```

### `gui.thermal_shock_reforged.uma_info_gardening_2.label_kutty`

```text
❄ 你知道吗，自己种东西能收获多少，你会惊讶的。
❄ 植物看起来确实漂亮，可要是你拿一把磨得锋利的镰刀收割它们，还能从中收获实用的东西呢。
```

### `gui.thermal_shock_reforged.une_info_ruby.label_kutty`

```text
❄ 我听说无尽花园的地下有很多宝石，尤其是红色的宝石。
❄ 有次我遇到一只高个子虫子，是个“宝石雕琢师”。她有一张工作台，上面摆满了打磨宝石和制作精美饰品的工具。
❄ 陌生人！你也可以试着做做看。
```

### `gui.thermal_shock_reforged.huntress_intro.label_kutty`

```text
❄ 看来是我误会了，你显然不是噬寄虫。
❄ 你也在冒险吗？也是一名猎人？
❄ 无论如何，你让我刮目相看。如果愿意，可以在我的领地暂留片刻。
```

### `gui.thermal_shock_reforged.clash_page_1.label_they_will_be_arriving_shortly`

```text
❄ 他们就快到了。
```

### `gui.thermal_shock_reforged.conc_info_infection.label_lest_i_risk_being_taken_by_that`

```text
❄ §7若早知你的爱意会将我们焚烧殆尽。
```

### `gui.thermal_shock_reforged.une_intro_epl_6.label_kutty`

```text
❄ 要是弄丢了，你得赔。
```

### `gui.thermal_shock_reforged.benzor_spice_epl.label_kutty`

```text
❄ 你给我§e香料§f，本佐给你珍贵的§e经验值§f。
```

### `gui.thermal_shock_reforged.civil_2_tlk.label_kutty`

```text
❄ 自从“生活成本”上涨后，我就变了。现在大家都去了薰衣草宫殿。
❄ ……算了，反正这也不算正史。
```

### `gui.thermal_shock_reforged.conc_info_blizzard.label_lest_i_risk_being_taken_by_that`

```text
❄ 若早知你的爱意会将我们焚烧殆尽。
```

### `gui.thermal_shock_reforged.conc_errand_1_epl_2.label_kutty`

```text
❄ 很久以前，有一只源初甲虫，它
```

### `gui.thermal_shock_reforged.qo_lab_assembler_touch.label_i_could_actually_do_with_s`

```text
❄ 算了，反正损失已经造成了。就
```

### `gui.thermal_shock_reforged.rusty_radio_tlk.label_should_prepare_for_also_snow`

```text
将会……再次迎来降雪。
```

### `gui.thermal_shock_reforged.qo_stratus_alloy_3b.label_kutty`

```text
❄ 终于！现在我能造些真正的机器了！
```

### `gui.thermal_shock_reforged.une_errand_null.label_were_you_down_there_for_a_while`

```text
❄ 就是说，有一种花我想放进我的
```

### `gui.thermal_shock_reforged.qo_stratus_alloy_3a.label_i_could_actually_do_with_s`

```text
❄ 我一直想亲手弄到一些
```

### `gui.thermal_shock_reforged.qo_lab_errand_1.label_youre_not_an_employee`

```text
❄ 你又不是员工什么的。
```

### `gui.thermal_shock_reforged.kofoni_ruby_info.label_kutty`

```text
❄ 听说这玩意儿是凝固的血液结晶。
```

### `gui.thermal_shock_reforged.spawn_tablet_gui.label_an_artifact_on_a_pedestal`

```text
基座上的神器？
```

### `gui.thermal_shock_reforged.civil_3_tlk.label_our_snow_is_really_dusty_for_s`

```text
❄ 那东西就像是会放动画的收音机，
```

### `gui.thermal_shock_reforged.clash_page_1.label_goodbye`

```text
❄ 再见 :^)
```

### `gui.thermal_shock_reforged.une_tip_19.label_impressive_stuff`

```text
❄ 它们用电力研发各种
```

### `gui.thermal_shock_reforged.laut_errand_1_epl_1.label_kutty`

```text
❄ 对不起对不起！没法说话没法说话！
❄ 都不见了，小家伙们都不见了，我的普连基们！
❄ 粉红色的小普连基虫，我才移开视线一秒，它们就全都不见了！
❄ 我可怎么办 ❄ 我可怎么办 ❄ 我还能怎么办？？？
```

### `gui.thermal_shock_reforged.une_tip_13.label_kutty`

```text
❄ 大多数人用普通工作台制作东西，但合唱城有些人会使用制造机造出一些令人惊叹的玩意儿。
❄ 要是家里也有一台就方便了。
```

### `gui.thermal_shock_reforged.civil_2_tlk.label_but_if_i_gave_it_water_itd_f`

```text
❄ 但要是给它浇水，它就会冻住，然后
```

### `gui.thermal_shock_reforged.une_tip_3.label_kutty`

```text
❄ 我以前从一只黄蜂那儿学到件事；
❄ “准备充分的猎人需要多样化的武器库。有些对手用冰刃就能轻松击败，但有些战斗需要换用另一种武器。”
```

### `gui.thermal_shock_reforged.une_tip_12.label_other_types_youll_have_to_do_s`

```text
❄ 别问我怎么知道的。
```

### `gui.thermal_shock_reforged.civil_8_tlk.label_whenever_im_out_in_the_forest`

```text
❄ 你整个人都绿了！
```

### `gui.thermal_shock_reforged.qo_thermoid_epl_4.label_kutty`

```text
❄ §7老天啊§f，你就这么想要这破玩意儿
```

### `gui.thermal_shock_reforged.une_info_self_3.label_kutty`

```text
❄ 我想掘墓者应该不介意我借用他的相机。
```

### `gui.thermal_shock_reforged.kofoni_blizzard_info.label_kutty`

```text
❄ §a寇§f以前为他们效力，还参与研发了
```

### `gui.thermal_shock_reforged.une_tip_16.label_kutty`

```text
❄ 我们以前也有甲虫族。
❄ 哦，他们有超棒的地下城堡，地表虫族可建不出那种东西。
❄ 你会以为城堡能让你躲过那场感染，可……呃，看来还是没能躲过？
```

### `gui.thermal_shock_reforged.rusty_radio_tlk.label_kutty`

```text
❄ ……如果你在西北边界一带，预计会有很多……呃，雪。
❄ 内区也会迎来一些……
```

### `gui.thermal_shock_reforged.clash_page_1.label_wait`

```text
❄ 等等
```

### `gui.thermal_shock_reforged.rusty_radio_tlk.label_concierge`

```text
生锈的收音机
```

### `gui.thermal_shock_reforged.clash_page_1.label_no_signal`

```text
❄ 就是今天。
```

### `gui.thermal_shock_reforged.kofoni_qo_info.label_kutty`

```text
❄ 哦你说他啊？每隔一天就来这儿一趟，
```

### `gui.thermal_shock_reforged.qo_lab_errand_1_b.label_kutty`

```text
❄ 这么说，你就喜欢去那些不该去的地方乱翻，是吧？我正好知道一处适合你的地方。
❄ 高地上有一座老档案馆，里面满是宝物和古老的设计图。我很想说随便拿，不过……
```

### `gui.thermal_shock_reforged.qo_lab_safe_1.label_kutty`

```text
❄ 别费劲了。钥匙早几年前就丢了，而且这东西不知是什么材料做的，怎么也砸不坏。
❄ 我都不记得往里面放了什么……
```

### `gui.thermal_shock_reforged.benzor_blackpowder_info.label_kutty`

```text
❄ 外来人找到黑火药了？？？
```

### `gui.thermal_shock_reforged.qo_lab_errand_null.label_kutty`

```text
❄ 钥匙你都拿到了，赶紧走吧，随便找个冰湖玩去。
```

### `gui.thermal_shock_reforged.rusty_radio_tlk.label_well_more_snow`

```text
嗯，更多的雪。
```

### `gui.thermal_shock_reforged.qo_stratus_alloy_1.label_kutty`

```text
❄ 那是……层云合金？？
```

### `gui.thermal_shock_reforged.qo_thermoid_epl_3.label_kutty`

```text
❄ 还杵在这儿干嘛？？
```

### `gui.thermal_shock_reforged.spawn_tablet_gui_3.label_an_artifact_on_a_pedestal`

```text
传送门框架？
```

### `gui.thermal_shock_reforged.qo_cryo_alloy_1.label_my_stockpiles_been_running_low`

```text
自从他们切断供应后，我的库存一直告急
```

### `gui.thermal_shock_reforged.qo_stratus_alloy_2.label_please_give_it_to_me_ill_mak`

```text
❄ ……把它给我吧！
```

### `gui.thermal_shock_reforged.qo_cryo_energy_1.label_kutty`

```text
❄ 哦，看啊，深寒能量。
```

### `gui.thermal_shock_reforged.laven_civil_2_tlk.label_kutty`

```text
❄ 你知道吗，我能站在这儿还有点意外，
```

### `gui.thermal_shock_reforged.clash_page_1.label_they_will_come_to_you`

```text
❄ 等他们准备好了，
```

### `gui.thermal_shock_reforged.une_errand_1_a.label_kutty`

```text
❄ 嗯……也许你能帮我找一样我一直在找的东西。
❄ 我想把一种花放进我的相册里，可找起来实在不容易……
```

### `gui.thermal_shock_reforged.une_errand_1_c.label_kutty`

```text
❄ 呃，不过你好像完全不怕冷。
❄ 如果不麻烦的话，你能帮我拍一张那种花的照片吗？我会特别开心的！洞穴绽花的照片！
❄ 记得小心点，好吗？下面有些脾气很差的小家伙……
```

### `gui.thermal_shock_reforged.une_tip_17.label_kutty`

```text
❄ 我以前见过一座大教堂-cathedral。
❄ 没错，你没听错，整整一座 §k超§r大的教堂。
§r❄ 在哪儿？我怎么知道……
```

### `gui.thermal_shock_reforged.civil_3_tlk.label_kutty`

```text
❄ 怎么了？你看起来像是见了鬼。
```

### `gui.thermal_shock_reforged.laven_civil_2_tlk.label_but_if_i_gave_it_water_itd_f`

```text
❄ 那可惜了，如果你是的话，这里也能给你留个位置，
```

### `gui.thermal_shock_reforged.concierge_main.label_kutty`

```text
❄ 占位文本
```

### `gui.thermal_shock_reforged.huntress_vilebath_info.label_kutty`

```text
❄ 啊，甲虫王国……
❄ 大雪前，我们视他们为可敬的盟友，直到疾病与谵妄吞噬了费拉克的心智。听说如今他们称他为“大亵渎者”。
❄ 那是我出生很久以前的事了，可惜我错过了他们的鼎盛时期。
```

### `gui.thermal_shock_reforged.conc_info_blizzard.label_kutty`

```text
❄ 玩弄失衡能量，简直是异端邪说
```

### `gui.thermal_shock_reforged.whisper_malachite_info.label_kutty`

```text
❄ 呃，这玩意儿是从太空掉下来的。
```

### `gui.thermal_shock_reforged.laut_errand_1_reject.label_were_you_down_there_for_a_while`

```text
❄ 我能留着它吗？？我付你香料 香料
```

### `gui.thermal_shock_reforged.clash_page_1.label_try_again_yn`

```text
   重试？[Y/N]
```

### `gui.thermal_shock_reforged.concierge_errand_none.label_kutty`

```text
❄ 无需担心，你的价值仍被认可。
```

### `gui.thermal_shock_reforged.benzor_self_info.label_kutty`

```text
❄ 本佐只是单纯喜欢香料而已，仅此而已。
```

### `gui.thermal_shock_reforged.benzor_spice_info.label_more_spice_more_exp_yes`

```text
❄ 或许本佐确实有点贪嘴，但也不算太糟啦！
```

### `gui.thermal_shock_reforged.une_tip_1.label_lets_just_say_its_not`

```text
❄ 就这么说吧，游客减少是有原因的。
```

### `gui.thermal_shock_reforged.clash_page_1.label_func33_report_103txt`

```text
-- [Func33_Report_107.txt]
```

### `gui.thermal_shock_reforged.une_intro_epl_5.label_kutty`

```text
❄ 欢迎你在页泉镇多待一会儿，不过你要是打算离开穹顶，我想让你带上这台特别的 远极相机。
❄ 专业摄影师会用这种设备记录并拍下他们遇见的各种小生物。非常方便！
```

### `gui.thermal_shock_reforged.civil_5_tlk.label_kutty`

```text
❄ 想知道我的绒毛为什么这么鲜亮吗？原来赭色树叶能染出时髦又亮眼的颜色！
```

### `gui.thermal_shock_reforged.une_intro_epl_2.label_kutty`

```text
❄ 呃，我叫尤妮特！我算是页泉镇的非正式代表。
❄ 那你呢……？
```

### `gui.thermal_shock_reforged.une_tip_17.label_just_mix_shale_and_thermite`

```text
那场感染，可……呃，看来还是没能躲过？
```

### `gui.thermal_shock_reforged.clash_page_1.label_they_are_ready_they_will_find`

```text
他们会来找你的。他们会
```

### `gui.thermal_shock_reforged.conc_info_infection.label_kutty`

```text
❄ 这世间存在着某些力量，从来都不是
```

### `gui.thermal_shock_reforged.whisper_self_info.label_kutty`

```text
❄ 呃，我不是这儿的人。你大概也看出来了
```

### `gui.thermal_shock_reforged.une_info_self_2.label_kutty`

```text
❄ 那地方离这里很远，要走 20 个周期，差不多得换乘 5 趟火车呢。
❄ 倒也没那么糟，我就是那时候开始学摄影的！
```

### `gui.thermal_shock_reforged.conc_errand_1_epl_1.label_kutty`

```text
❄ 你见过外面的世界，对吧？
```

### `gui.thermal_shock_reforged.sopel_intro_epl_2.label_kutty`

```text
❄ 看来你明智地用上了我的礼物。仅凭我给的一点提示，你终于把强化后的界域基石带回了我这里。
❄ 干得漂亮。确实漂亮。
```

### `gui.thermal_shock_reforged.sopel_intro_epl_2.label_were_you_down_there_for_a_while`

```text
❄ 必要的附带损失，为了维护
```

### `gui.thermal_shock_reforged.une_tip_5.label_kutty`

```text
❄ 掘墓者之类的亡灵会掉很多那种黑色黏糊糊的东西，还有别的玩意儿。
❄ 我听说那其实是凝固的经验值。我在想，有没有办法把它回收利用什么的。
```

### `gui.thermal_shock_reforged.une_tip_8.label_kutty`

```text
❄ 你见过结冰的钴湖吗？
❄ 我们会用钴制作很多工具柄之类的东西。
❄ 我建议你多囤一些钴。
```

### `gui.thermal_shock_reforged.une_info_self_3.label_big_sister`

```text
❄ 大概吧。
```

### `gui.thermal_shock_reforged.une_tip_11.label_kutty`

```text
❄ 你知道那些住在枯树上的虱子吗？
❄ 我会把它们留到万不得已时才吃，不过它们应该还算能吃，直接舀起来然后……
❄ 别问我怎么知道的。
```

### `gui.thermal_shock_reforged.sopel_intro_epl_5.label_kutty`

```text
❄ 哇哦！可惜我们还卡在测试版……我本来应该有一场超酷的首领战，本该现在就登场，不过你得等到版本 1 :(
❄ 我也不知道那是什么时候，不过到时应该会有相关消息，之后会发在 瑟姆aDevYT 上。
```

### `gui.thermal_shock_reforged.sopel_intro_epl_2.label_im_sure_you_understand`

```text
❄ 我相信你能理解。
```

### `gui.thermal_shock_reforged.benzor_blackpowder_info.label_this_is_why_benzor_stay_down_h`

```text
❄ 地表早已是一片死地...
```

### `gui.thermal_shock_reforged.conc_errand_1_epl_f.label_i_suspect_he_too_has_been_tarn`

```text
❄ 我猜他的核心早已被腐化玷污，
```

### `gui.thermal_shock_reforged.civil_7_tlk.label_kutty`

```text
❄ 我是绿色的，原因可厉害了！
❄ 你真是嫉妒得发绿了！
```

### `gui.thermal_shock_reforged.uma_info_infection.label_kutty`

```text
❄ 嗯，你见过那些橙色的大球吗？上面还有缠在灰色的扭曲根须里？
❄ 那些其实是尸体，你知道吗？
❄ 大部分是骨头，夹着一点肉和石块，全都混在一起，变成了……那种东西。
❄ 我可不想死在那种东西手里。
```

### `gui.thermal_shock_reforged.qo_cryo_alloy_1.label_kutty`

```text
❄ 什么——那是我想的那个东西吗？
```

### `gui.thermal_shock_reforged.kofoni_shalespring_info.label_kutty`

```text
❄ 对啊，我以前就住那儿！
```

### `gui.thermal_shock_reforged.une_errand_1_end_a.label_kutty`

```text
❄ 哇，我都不知道洞穴绽花居然这么有攻击性！
❄ 谢谢，我就把这张照片收下啦！
❄ 对了，这是给你的一小袋谢礼，谢谢你费心帮忙。:)
```

### `gui.thermal_shock_reforged.spawn_tablet_gui_2.label_an_artifact_on_a_pedestal`

```text
以水浇岩浆而成？
```

### `gui.thermal_shock_reforged.qo_lab_assembler_touch.label_kutty`

```text
❄ 不行，绝对不行。用你自己的制造机！
```

### `gui.thermal_shock_reforged.qo_lab_errand_1_c.label_kutty`

```text
❄ 还记得那些惧光石都被魔法锁住了吗？就是祭坛和钥匙那一套。
❄ 那里也装了类似的东西。
❄ 那地方完全无法破坏……但说到底，你需要的只是一把更强的钥匙。
```

### `gui.thermal_shock_reforged.qo_lab_errand_1.label_kutty`

```text
❄ 哦，你可真好心。
❄ 其实嘛……也许还真有件事能请你帮忙。
```

### `gui.thermal_shock_reforged.laut_errand_1_reject.label_especially_if_you_have_xp`

```text
❄ 你找到它们了！哇哇哇哇哇哇
```

### `gui.thermal_shock_reforged.qo_thermoid_epl_2.label_kutty`

```text
❄ 这是我的淤泥堆，自己找去。
```

### `gui.thermal_shock_reforged.qo_lab_safe_2.label_kutty`

```text
❄ 什么鬼——啊？？？？
❄ 可、可你是怎么弄开的？？里面有什么？？
❄ 电池？我还一直在找它呢！
❄ 嗯，这个其实正好用得上……
```

### `gui.thermal_shock_reforged.une_tip_18.label_kutty`

```text
❄ 暴雪计划在地下各处都有基地，尤其是水边。
❄ 因为那儿有凝胶储能块，你懂吧？
❄ 它们用电力研发各种技术：激光枪、触电手套等等。
```

### `gui.thermal_shock_reforged.une_tip_2.label_kutty`

```text
❄ 我以前遇到过一个人，他有种叫“自然罗盘”的东西。
❄ 无尽花园好像没有这种东西，不过据说它能帮你找到远方的生物群系。
```

### `gui.thermal_shock_reforged.uma_info_self.label_kutty`

```text
❄ 嗯，要说我自己的事嘛……
❄ 我大概是唯一一个不会试图杀你的亡灵。
❄ 什么？不，我死得可一点也不酷，是被埋在雪崩里。不过也没什么好抱怨的，它还保护我的尸体免受感染侵蚀。
```

### `gui.thermal_shock_reforged.une_tip_14.label_kutty`

```text
❄ 你知道无尽花园的植物以前绿得出奇吗？对，很难相信吧，我知道。
❄ 要是有人知道怎样让那些漂亮的颜色重新回来就好了……
```

### `gui.thermal_shock_reforged.conc_info_vilebath.label_kutty`

```text
❄ §a亵渎者§e与其信徒或许属于§a
```

### `gui.thermal_shock_reforged.qo_lab_assembler_touch_alt.label_kutty`

```text
❄ 我刚刚说什么来着！！！！！！！！！
```

### `gui.thermal_shock_reforged.laven_civil_1_tlk.label_but_if_i_gave_it_water_itd_f`

```text
❄ 那可惜了，如果你是的话，这里也能给你留个位置，
```

### `gui.thermal_shock_reforged.une_info_blizzard_2.label_kutty`

```text
❄ 原来暴雪更像是“军事研究”机构，他们所有的商业产品都只是……顺带做出来的副产品。
❄ 天啊，他们到底是做什么的？
```

### `gui.thermal_shock_reforged.qo_feddy_fivebears.label_kutty`

```text
❄ 听好了。
```

### `gui.thermal_shock_reforged.conc_info_self.label_kutty`

```text
❄ 大腐化降临之前，我曾照料着
```

### `gui.thermal_shock_reforged.uma_info_concierge.label_kutty`

```text
❄ 那个……
❄ 不会吧，那个老太太居然还活蹦乱跳的？？
❄ 我记得我们曾一起对付噬寄虫，埋葬宫殿陷落的时候。
❄ 她简直疯得####，不过是好意的那种。
```

### `gui.thermal_shock_reforged.qo_thermoid_epl_1.label_house_my_collection`

```text
但有时我能听到它们在上方行军的声响。
```

### `gui.thermal_shock_reforged.sopel_intro_epl_4.label_kutty`

```text
❄ 不管怎样，你的工作已经完成了。
❄ 界域基石对你来说已经完成了它的使命，所以现在我要把它收回。
```

### `gui.thermal_shock_reforged.uma_info_gardening.label_kutty`

```text
❄ 嗯，你觉得它为什么叫“无尽花园”？
❄ 从蛋里孵出来后我就一直种东西，不过……刚开始我淹死了不少，后来总算掌握了诀窍，所以我用浇水壶，而不是水源方块。
```

### `gui.thermal_shock_reforged.une_intro_epl_4.label_kutty`

```text
❄ 等等，你应该不是本地人吧……
❄ 这里是无尽花园！本地人叫它 Loqendia，那个 Q 要读成 K 音。
❄ 你会发现这地方很漂亮，只要你能接受这里的……呃，亡灵问题。
```

### `gui.thermal_shock_reforged.une_intro_epl_3.label_kutty`

```text
❄ ……你话不多啊，陌生人？
❄ 不过“陌生人”这个称呼确实挺酷的。
```

### `gui.thermal_shock_reforged.q_phobotite_1.label_kutty`

```text
❄ 嗯？惧光石？
```

### `gui.thermal_shock_reforged.conc_info_florist.label_kutty`

```text
❄ 哦，是啊，我很记得那只小臭虫！
❄ 你不觉得她该找时间来拜访我吗？毕竟，若不是我，她现在早就成了一具爬满蛆虫的尸体！
```

### `gui.thermal_shock_reforged.q_phobotite_1.label_empty`

```text
❄ 你最好把那东西拿去祭坛溶解掉，
```

### `gui.thermal_shock_reforged.qo_exhaust.label_kutty`

```text
❄ ...
```

### `gui.thermal_shock_reforged.kofoni_qo_info.label_eh_who_cares_i_have_a_new_co`

```text
❄ 不过对我来说也不算什么稀罕东西
```

### `gui.thermal_shock_reforged.qo_thermoid_epl_1.label_but_you_know_everything_went`

```text
❄ 我从没亲眼见过那种机器人
```

### `gui.thermal_shock_reforged.une_tip_6.label_kutty`

```text
❄ 有一次我钻进一个山洞，发现下面竟然有一个巨大的垃圾场，还找到些挺有意思的东西，一大堆烂泥……但我不记得它具体在哪儿了。
```

### `gui.thermal_shock_reforged.une_info_self.label_were_you_down_there_for_a_while`

```text
❄ 天哪，你是不是在下面困了好一阵子？
```

### `gui.thermal_shock_reforged.benzor_blizzard_info.label_kutty`

```text
❄ 本佐对这个暴雪阵营了解不多。
```

### `gui.thermal_shock_reforged.une_tip_10.label_kutty`

```text
❄ 这里大多数人会用经验值和香料交易。
❄ 一般大家交易的几乎都是红色那种，但也有人会要其他种类。你得四处探索并再搜刮一番才能找到。
```

### `gui.thermal_shock_reforged.une_intro_epl_3.label_were_you_down_there_for_a_while`

```text
❄ 那你呢……？
```

### `gui.thermal_shock_reforged.une_tip_19.label_kutty`

```text
❄ 建议你做几个收纳袋，等口袋装满时就能用上。只要一些绳子和一张哨兵虫的皮。
```

### `gui.thermal_shock_reforged.qo_neural_chip_1.label_i_could_actually_do_with_s`

```text
❄ 我建议你能拆多少就拆多少
```

### `gui.thermal_shock_reforged.sopel_intro_epl_3.label_kutty`

```text
❄ 哦，对，我敢肯定那只叛逃的蜈蚣已经把我们的事全告诉你了。
❄ 真遗憾，为了让他替我做事，现在想让他干点活，可真是越来越费劲。
```

### `gui.thermal_shock_reforged.uma_info_unette.label_kutty`

```text
❄ 哦，那个女孩……我想是从合唱城来的？为什么会有人愿意放弃那样的生活呢？
❄ 自从她来了以后，镇上确实热闹多了。她来了之后，镇上热闹多了。我挺喜欢她。
```

### `gui.thermal_shock_reforged.qo_feddy_fivebears.label_go_dissolve_some_pylon_cores_i`

```text
❄ 你要是真这么有本事，就去溶解几个塔尖核心
```

### `gui.thermal_shock_reforged.qo_thermoid_epl_1.label_kutty`

```text
❄ 滚开，我忙着呢。
```

### `gui.thermal_shock_reforged.laut_errand_1_accept.label_kutty`

```text
❄ 哎呀，哎呀。我开始怀疑，我是不是真的把这些小家伙弄丢了，弄丢了。如果我真把它们弄丢了，那才叫弄丢了。
❄ 不过不过，还是谢谢你你！
```

### `gui.thermal_shock_reforged.civil_1_tlk.label_kutty`

```text
❄ 我滚这个雪球已经滚了 2 个周期了。
❄ 不知为什么，我们这里的雪特别干，不太容易黏在一起。
❄ 可要是给它浇水，它就会冻起来，砸到人会特别疼。
```

### `gui.thermal_shock_reforged.rusty_radio_tlk.label_also_anyone_living_in_the`

```text
❄ 另外，住在萨克斯带的居民
```

### `gui.thermal_shock_reforged.qo_thermoid_epl_1.label_big_ones_too_felt_like_an_ea`

```text
❄ 动静还挺大，跟地震似的！
```

### `gui.thermal_shock_reforged.qo_altar_key_1.label_kutty`

```text
❄ 那是什么，祭坛钥匙？
```

### `gui.thermal_shock_reforged.une_tip_8.label_no_shame_in`

```text
❄ 如果你想挑战雷暴，记得带上灯
```

### `gui.thermal_shock_reforged.qo_lab_errand_1_d.label_kutty`

```text
❄ 听着，我知道你有界域基石。否则就没法解释你是怎么来到这里的。
❄ 那东西能打开各种门，无论是有形的还是别的什么。
❄ ……至少，它有这个潜力，可能还需要稍微改造一下。
```

### `gui.thermal_shock_reforged.qo_stratus_alloy_2.label_i_could_actually_do_with_s`

```text
❄ 我一直想亲手弄到一些
```

### `gui.thermal_shock_reforged.kofoni_self_info.label_kutty`

```text
❄ 我以前算是个历史学家，专门收集
```

### `gui.thermal_shock_reforged.une_errand_1_end_b.label_kutty`

```text
❄ 哇，我都不知道洞穴绽花居然这么有攻击性！
❄ 嘿，照片的事别放在心上，咳……是我不好，把你派到那么危险的地方。
❄ 我迟早会找到别的东西，填上相册里的那个空位吧，大概。
```

### `gui.thermal_shock_reforged.benzor_infection_info.label_kutty`

```text
❄ 本佐还记得灾难发生那天的场景。
```

### `gui.thermal_shock_reforged.kofoni_pylons_info.label_kutty`

```text
❄ §a寇§f说这玩意儿能在城市间传输能量
```

### `gui.thermal_shock_reforged.clash_page_1.label_shortly`

```text
就快了。
```

### `gui.thermal_shock_reforged.qo_stratus_alloy_3a.label_please_give_it_to_me_ill_mak`

```text
❄ ……把它给我吧！
```

### `gui.thermal_shock_reforged.benzor_spice_info.label_kutty`

```text
❄ 珍贵资源，美味至极。
```

### `gui.thermal_shock_reforged.une_intro_epl_1.label_were_you_down_there_for_a_while`

```text
❄ 天哪，你在下面困了好一阵子吗？
```

### `gui.thermal_shock_reforged.une_tip_9.label_kutty`

```text
❄ 你喜欢建造吗？
❄ 拿把钴凿子吧，可以帮你凿出一些方块上的精细纹理。
```

### `gui.thermal_shock_reforged.une_tip_7.label_kutty`

```text
❄ 这一带经常打雷，雷暴还会引来许多令人毛骨悚然的小虫。
❄ 钴石高地那边的情况尤其糟糕。
❄ 如果你想挑战雷暴，记得带上灯或几支火把。
```

### `gui.thermal_shock_reforged.laut_errand_1_epl_2.label_kutty`

```text
❄ 什么？什么？普连基们？？
❄ 你找到它们了！哇哇哇哇哇哇！
❄ 我还能继续照顾它们吗？？可以的话，我会付你香料香料是，是。
```

### `gui.thermal_shock_reforged.une_tip_15.label_kutty`

```text
❄ 嘿，你看起来像是会随身带枪的人。
❄ 弹药可能有点贵，除非你知道去哪儿找。
❄ 把页岩和热铁块混在一起，就能得到一大堆火药！
```

### `gui.thermal_shock_reforged.sopel_intro_epl_1.label_kutty`

```text
❄ 好久不见了，不是吗？
❄ 你在这么短的时间里就闯出了名声。毕竟你砍倒了我那么多员工。
❄ 为了维持我的体面，必要的牺牲总是少不了的。
❄ 我相信你能理解。:^)
```

### `gui.thermal_shock_reforged.une_tip_1.label_kutty`

```text
❄ 花园这边的居民都很团结，认识新人时，问问有没有其他人也认识他们！
```

### `gui.thermal_shock_reforged.une_info_blizzard.label_bare_bones_others_arent_so_l`

```text
只剩一副骨架……其他的就没这么幸运了。
```

### `gui.thermal_shock_reforged.qo_thermoid_epl_5.label_kutty`

```text
❄ 行了行了！拿上这破玩意儿，去烦别人
```

### `gui.thermal_shock_reforged.une_info_shalespring.label_kutty`

```text
❄ 挺舒适的小地方，对吧？
❄ 看来，“穹顶城镇”在花园的这一侧很常见。
❄ 我也不知道这个名字从哪儿来的，这儿也没人知道。
```

### `gui.thermal_shock_reforged.qo_stratus_alloy_3a.label_kutty`

```text
❄ 行吧，随你，谁在乎呢……
```

### `gui.thermal_shock_reforged.conc_errand_1_epl_3.label_kutty`

```text
❄ 我命你前往秽血浴场深处，寻找§a大亵渎者§f，
```

### `gui.thermal_shock_reforged.civil_4_tlk.label_kutty`

```text
❄ 我妻子喜欢把那些赭黄色的树带回家，每次我到森林里，脑子里就总是想着她。
```

### `gui.thermal_shock_reforged.une_info_train.label_kutty`

```text
❄ 火车嘛，呃，我们也管不了。
❄ 上一个开走火车的人半夜才出现，是个沉默寡言的家伙。
❄ 没人知道他们把火车开去哪儿了，它也一直没回来。我倒想把它叫回来，可我的车票已经没余额了。
```

### `gui.thermal_shock_reforged.civil_8_tlk.label_kutty`

```text
❄ 好冷 :(
```

### `gui.thermal_shock_reforged.une_intro_epl_1.label_kutty`

```text
❄ 哇，你刚从地堡出来吗？
❄ 那扇门已经封闭很久了。
```

### `gui.thermal_shock_reforged.clash_page_1.label_execute_kgcfunc_33`

```text
❄ /execute kgcfunc 33
```

### `gui.thermal_shock_reforged.laut_errand_1_epl_2.label_how_will_i_ever_how_will_i`

```text
❄ 我到底能不能 ❄ 我到底能不能 ❄ 我到底
```

### `gui.thermal_shock_reforged.huntress_self_info.label_kutty`

```text
❄ 我来自桑格雷黄蜂帝国，正在寻找适合扩张领土的土地。
❄ 我和数百名同族自出生起就接受训练，学会独自狩猎、抵御严寒，与感染抗争。
```

### `gui.thermal_shock_reforged.une_info_self.label_kutty`

```text
❄ 我吗？我来这儿也就几百个周期。我和姐姐从合唱城搬来，是在那件事之后，呃……
❄ 只能说，游客变少是有原因的。
```

### `gui.thermal_shock_reforged.une_tip_14.label_i_wonder_if_you_could_find_one`

```text
❄ 要是家里也能有一份就方便了。
```

### `gui.thermal_shock_reforged.clash_page_1.label_take_a_look`

```text
❄ 看看吧。
```

### `gui.thermal_shock_reforged.laven_civil_1_tlk.label_kutty`

```text
❄ 我是不是在哪儿见过你？
```

### `gui.thermal_shock_reforged.une_errand_null.label_kutty`

```text
❄ 啊，没关系，你已经帮了我很多啦！
❄ 真的很感谢，不过我自己没问题啦 :p
```

### `gui.thermal_shock_reforged.qo_stratus_alloy_2.label_are_you_trying_to_blow_us_up`

```text
❄ 连我都不知道那配方！
```

### `gui.thermal_shock_reforged.une_errand_1_b.label_kutty`

```text
❄ 它叫北极洞穴绽花，有四片花瓣，浅紫色，个头很大，但只长在那些湿漉漉的冰洞里。
❄ 我自己试着找过好几次，可实在受不了那里的寒冷！
```

### `gui.thermal_shock_reforged.une_info_infection.label_kutty`

```text
❄ 嗯，呃，就是那个。
❄ 如今这已经是生活的一部分了。其实我从来没见过没有“感染”的世界。
❄ 人们会死去，或吸入孢子，之后它便会占据他们的身体。有些只剩下白骨……另一些就没这么幸运了。
```

### `gui.thermal_shock_reforged.une_intro_epl_6.label_can_look_past_the_uhh`

```text
❄ 专业摄影师会用这种设备
```

### `gui.thermal_shock_reforged.civil_6_tlk.label_kutty`

```text
❄ 没错，这堆落叶是我自己做的，包的。
❄ 它也许没有雪球那么结实，不过嘛，我觉得待在里面还挺舒服的。
```

### `gui.thermal_shock_reforged.clash_page_1.label_find_their_way`

```text
找到路的。
```

### `gui.thermal_shock_reforged.clash_page_1.label_how_will_i_find_them`

```text
❄ 我要怎样找到他们？
```

### `gui.thermal_shock_reforged.clash_page_1.label_message_not_sent_try_again`

```text
-- 消息未发送，
```

### `gui.thermal_shock_reforged.kimro_errand_1_epl_1.label_i_know_a_way_to_pacify_them_b`

```text
❄ 我知道什么能安抚它们，但这个§e万灵药
```

### `gui.thermal_shock_reforged.concierge.intro_1`

```text
❄ 今天真是蒙福的一天，不是吗？
```

### `gui.thermal_shock_reforged.concierge.intro_2`

```text
❄ 我的庇护所永远欢迎你。
```

### `gui.thermal_shock_reforged.concierge.intro_fallback`

```text
❄ 小可爱
```

### `gui.thermal_shock_reforged.kofoni.intro_1`

```text
❄ 最近咋样？
```

### `gui.thermal_shock_reforged.kofoni.intro_2`

```text
❄ 是啊，我知道这里臭得要命。
```

### `gui.thermal_shock_reforged.kofoni.intro_fallback`

```text
❄ 小可爱
```

### `gui.thermal_shock_reforged.qo_main.greeting_1`

```text
❄ 干什么？这回又想要什么？？
```

### `gui.thermal_shock_reforged.qo_main.greeting_2`

```text
❄ 往后退，别挤进我的私人空间。
```

### `gui.thermal_shock_reforged.qo_main.greeting_0`

```text
❄ 小可爱
```

### `gui.thermal_shock_reforged.whisper_main.greeting_0`

```text
❄ 小可爱
```

### `gui.thermal_shock_reforged.whisper_main.greeting_1`

```text
❄ 你是食物吗？？
```

### `gui.thermal_shock_reforged.whisper_main.greeting_2`

```text
❄ ……你有蛋糕吗？？
```

### `gui.thermal_shock_reforged.huntress.intro_1`

```text
❄ 需要我帮忙吗？
```

### `gui.thermal_shock_reforged.huntress.intro_2`

```text
❄ 乐意效劳，探险同伴。
```

### `gui.thermal_shock_reforged.huntress.intro_fallback`

```text
❄ 小可爱
```

### `gui.thermal_shock_reforged.benzor.intro_1`

```text
❄ 唔……你带了§e香料§f给§a本佐§f？
```

### `gui.thermal_shock_reforged.benzor.intro_2`

```text
❄ §a陌生人§f，你又带来§e香料§f了？
```

### `gui.thermal_shock_reforged.benzor.intro_fallback`

```text
❄ 库蒂
```

### `gui.thermal_shock_reforged.uma_main.greeting_1`

```text
❄ 抱歉，万寿菊已经卖光了。已经有人预订了。
```

### `gui.thermal_shock_reforged.uma_main.greeting_2`

```text
❄ ……喂。
```

### `gui.thermal_shock_reforged.uma_main.greeting_0`

```text
❄ 小可爱
```

### `gui.thermal_shock_reforged.une_main.greeting_1`

```text
❄ 又见面了，陌生人！
```

### `gui.thermal_shock_reforged.une_main.greeting_2`

```text
❄ 嘿，哥们……哥们？姐们？？嗯。
```

### `gui.thermal_shock_reforged.une_main.greeting_0`

```text
❄ 小可爱
```

### `gui.thermal_shock_reforged.ly_main.intro_1`

```text
❄ 今天天气真不错，对吧？呃……对吧？
```

### `gui.thermal_shock_reforged.ly_main.intro_2`

```text
❄ 碗里有雪，瓶里也有雪。要买吗？
```

### `gui.thermal_shock_reforged.ly_main.intro_fallback`

```text
❄ kutty
```

## 物品与方块描述文本

### `item.thermal_shock_reforged.diskette_7.description_0`

```text
§7可用全息监视器读取
```

### `item.thermal_shock_reforged.diskette_7.description_1`

```text
§8§o“回收的探险录音”
```

### `block.thermal_shock_reforged.visi_bulb.description_0`

```text
§7必须种在种植盆或种植箱上，口渴时要用浇水壶浇水！
```

### `item.thermal_shock_reforged.bone_axe.description_0`

```text
§9- 铁属性
```

### `item.thermal_shock_reforged.diskette_9.description_1`

```text
§8§o"4203"
```

### `item.thermal_shock_reforged.diskette_9.description_0`

```text
§7可用全息监视器读取
```

### `item.thermal_shock_reforged.frigid_altar_key.description_1`

```text
§7§e废料场§7里或许有人知道这东西……
```

### `item.thermal_shock_reforged.frigid_altar_key.description_0`

```text
§7解锁§e祭坛§7。
```

### `block.thermal_shock_reforged.lapidarian_workbench.description_0`

```text
§7用于打磨§e宝石§7，亦可研磨部分方块（如§e淤泥§7）。
```

### `block.thermal_shock_reforged.muck_button.description_0`

```text
§7杂质减少 100%！
```

### `item.thermal_shock_reforged.carborundum_shovel.description_0`

```text
§9 - 铁属性
```

### `item.thermal_shock_reforged.flower_bomb.description_0`

```text
§7使附近生物获得氯属性弱点，可重复使用！
```

### `item.thermal_shock_reforged.bloodstone_hoe.description_0`

```text
§9 - 铁属性
```

### `item.thermal_shock_reforged.bloodstone_sickle.description_0`

```text
§7连续攻击后造成额外伤害。
```

### `item.thermal_shock_reforged.bloodstone_sickle.description_1`

```text
§7可收割园艺植物，也可当锄头使用。
```

### `block.thermal_shock_reforged.hologram_constructor.description_0`

```text
§7读取软盘。
```

### `item.thermal_shock_reforged.cocoa.description_0`

```text
§9"获得生命恢复效果，对暴雪阵营生物造成额外伤害。"
```

### `item.thermal_shock_reforged.bloodstone_pickaxe.description_0`

```text
§9 - 铁属性
```

### `block.thermal_shock_reforged.hearthfly_branch_seed.description_1`

```text
§7没有§e花园灵§7的帮助，这颗种子无法生长。
```

### `block.thermal_shock_reforged.hearthfly_branch_seed.description_0`

```text
§7必须种在种植盆或种植箱上，干渴时记得浇水！
```

### `item.thermal_shock_reforged.blood_vessel.description_0`

```text
§7可在§e提取机§7中回收利用。
```

### `block.thermal_shock_reforged.trident_lamp.description_0`

```text
§7该方块可通过§e凿子§7改变外观。
```

### `item.thermal_shock_reforged.frozen_blade.description_0`

```text
§b- 冰霜属性
```

### `block.thermal_shock_reforged.glittery_shale.description_0`

```text
§7可在§e宝石加工台§7提取。
```

### `item.thermal_shock_reforged.cryonic_energy.description_0`

```text
用于为制造机供能
```

### `item.thermal_shock_reforged.cryonic_energy.description_1`

```text
§7§e废料场§7里或许有人知道这东西……
```

### `item.thermal_shock_reforged.micro_dome.description_1`

```text
§c如果页泉聚落已经生成，就不需要再使用此物品。
```

### `item.thermal_shock_reforged.micro_dome.description_0`

```text
§c为 Beta v06 或更早版本创建的世界，在无尽花园生成页泉聚落。
```

### `item.thermal_shock_reforged.chisel.description_0`

```text
§7改变部分方块的外观样式
```

### `item.thermal_shock_reforged.beetle_claw.description_0`

```text
§7可在§e提取机§7中回收利用。
```

### `item.thermal_shock_reforged.ruby_pickaxe.description_0`

```text
§9 - 铁属性
```

### `item.thermal_shock_reforged.tainted_energy.description_0`

```text
§7可在§e提取机§7中回收利用。
```

### `item.thermal_shock_reforged.climbing_pick.description_0`

```text
§9 - 冰霜属性
```

### `item.thermal_shock_reforged.tombscythe.description_1`

```text
§7连续命中后造成额外伤害。
```

### `block.thermal_shock_reforged.rusty_machine.description_0`

```text
§7它的用途目前还不明确。
```

### `item.thermal_shock_reforged.diskette_3.description_1`

```text
§8§o“这么没耐心？？？”
```

### `item.thermal_shock_reforged.diskette_3.description_0`

```text
§7可用全息监视器读取
```

### `block.thermal_shock_reforged.pure_muck_stairs.description_0`

```text
§7杂质减少 100%！
```

### `item.thermal_shock_reforged.rusty_edge.description_1`

```text
§7右键冲刺
```

### `item.thermal_shock_reforged.rusty_edge.description_0`

```text
§9- 铁属性
```

### `block.thermal_shock_reforged.gel_bulb_seed.description_0`

```text
§7必须种在种植盆或种植箱上，干渴时记得浇水！
```

### `item.thermal_shock_reforged.ghostglass_pick.description_0`

```text
§9 - 氯属性
```

### `block.thermal_shock_reforged.modest_archive_cabinet.description_0`

```text
§7可在§e宝石加工台§7中提取。
```

### `block.thermal_shock_reforged.wallbore_constructor.description_0`

```text
§7在凿壁机前方第4格放置页岩，即可开始采矿。
```

### `block.thermal_shock_reforged.wallbore_constructor.description_1`

```text
§7产物会落在凿壁机下方。
```

### `block.thermal_shock_reforged.wallbore_constructor.description_2`

```text
§7用熔岩桶重新激活冻结的凿壁机。
```

### `block.thermal_shock_reforged.extractor.description_0`

```text
§7将某些物品回收为有用材料或经验。
```

### `block.thermal_shock_reforged.slush.description_1`

```text
§7可在§e宝石加工台§7中提取。
```

### `block.thermal_shock_reforged.slush.description_0`

```text
§7满是杂质。
```

### `block.thermal_shock_reforged.purifier.description_0`

```text
§7右键使用，净化附近冰封植物，清除腐化方块，一次性用品。
```

### `block.thermal_shock_reforged.purifier.description_1`

```text
§c[立即使用！]
```

### `item.thermal_shock_reforged.raw_torpedo.description_0`

```text
§7食用时有概率爆炸
```

### `block.thermal_shock_reforged.muck.description_1`

```text
§7可在§e宝石加工台§7中提取。
```

### `block.thermal_shock_reforged.muck.description_0`

```text
§7满是杂质。
```

### `item.thermal_shock_reforged.pesticide.description_0`

```text
§7可在§e提取机§7中回收。
```

### `item.thermal_shock_reforged.infected_flesh.description_0`

```text
§7可在§e提取机§7中回收利用。
```

### `item.thermal_shock_reforged.primordial_blood.description_0`

```text
§d[任务物品]
```

### `item.thermal_shock_reforged.model_train.description_1`

```text
§c如果你的世界还没有生成页泉聚落，请勿使用此物品。
```

### `item.thermal_shock_reforged.model_train.description_0`

```text
§c为在 v07.4 之前生成了 v07 版页泉聚落的世界，在无尽花园生成页泉火车站。
```

### `block.thermal_shock_reforged.low_gravity_field.description_0`

```text
§7将实体向上推送，潜行时向下。
```

### `item.thermal_shock_reforged.ashen_bone.description_0`

```text
§7可在§e提取机§7中回收利用。
```

### `item.thermal_shock_reforged.rough_bloodstone.description_0`

```text
§7可在§e宝石加工台§7中提纯。
```

### `item.thermal_shock_reforged.ruby_sickle.description_1`

```text
§7可收获园艺植物，也能用作锄头。
```

### `item.thermal_shock_reforged.ruby_sickle.description_0`

```text
§7连续攻击后造成额外伤害。
```

### `item.thermal_shock_reforged.ignition_detector.description_0`

```text
§7检测当前群系的点火评分。
```

### `item.thermal_shock_reforged.antitoxin.description_0`

```text
§7生效期间免疫中毒效果
```

### `block.thermal_shock_reforged.volatine_seed.description_0`

```text
§7必须种在种植盆或种植箱上，口渴时要用浇水壶浇水！
```

### `item.thermal_shock_reforged.phobotite.description_0`

```text
§7§e废料场§7里或许有人知道这东西……
```

### `item.thermal_shock_reforged.liteprism_sword.description_1`

```text
§7潜行挥剑时，在使用者前方召唤超新星。
```

### `item.thermal_shock_reforged.liteprism_sword.description_0`

```text
§7潜行状态下，在使用者前方生成超新星。
```

### `item.thermal_shock_reforged.adamantine_spice.description_0`

```text
§7最常见的香料，深受无尽花园地表及浅层洞穴住民喜爱。
```

### `block.thermal_shock_reforged.visi_bulb_seed.description_0`

```text
§7必须种在种植盆或种植箱上，口渴时要用浇水壶浇水！
```

### `block.thermal_shock_reforged.thermite.description_0`

```text
§7高度易燃，会引发周围§e热铁块§7连锁爆炸。
```

### `block.thermal_shock_reforged.thermite.description_1`

```text
§c[音量警告！]
```

### `item.thermal_shock_reforged.stratus_alloy.description_0`

```text
§7§e废料场§7里或许有人知道这东西……
```

### `block.thermal_shock_reforged.dewleaf_seed.description_0`

```text
§7必须种在种植盆或种植箱上，干渴时记得浇水！
```

### `block.thermal_shock_reforged.dewleaf_seed.description_1`

```text
§7没有§e花园灵§7的帮助，这颗种子无法生长。
```

### `block.thermal_shock_reforged.constellation_block.description_0`

```text
§7使用§e凿子§7可改变此方块的图案。
```

### `item.thermal_shock_reforged.fire_bomb.description_0`

```text
§7灼烧附近具有火焰弱点的生物。
```

### `block.thermal_shock_reforged.red_sanctum_light.description_0`

```text
§7可在§e宝石加工台§7中提取。
```

### `item.thermal_shock_reforged.stratosilver_drill.description_0`

```text
§9 - 冰霜属性
```

### `item.thermal_shock_reforged.stratosilver_drill.description_1`

```text
§9 - 冰霜属性
```

### `item.thermal_shock_reforged.molten_bat.description_0`

```text
§6- 火焰属性
```

### `item.thermal_shock_reforged.bone_shovel.description_0`

```text
§9- 铁属性
```

### `item.thermal_shock_reforged.neural_chip.description_0`

```text
§7可在§e提取机§7中回收利用。
```

### `item.thermal_shock_reforged.neural_chip.description_1`

```text
§7§e废料场§7里或许有人知道这东西……
```

### `block.thermal_shock_reforged.glastrekker_egg.description_0`

```text
§7需要使用骨粉才能孵化（还需要非常开阔的空间！）
```

### `block.thermal_shock_reforged.electric_sphere_constructor.description_0`

```text
§7触碰会放电，击打会回弹。
```

### `block.thermal_shock_reforged.electric_sphere_constructor.description_1`

```text
§7使用剪刀或剪叶器收回。
```

### `item.thermal_shock_reforged.shock_stick.description_0`

```text
§7使目标触电，对触电目标造成的伤害会传导至其他触电目标。
```

### `item.thermal_shock_reforged.shock_stick.description_1`

```text
§7施加触电效果。对触电生物造成的伤害会传导至其他触电生物。
```

### `item.thermal_shock_reforged.shock_stick.description_2`

```text
§7触电效果可打断某些机械类生物的攻击。
```

### `item.thermal_shock_reforged.rough_ruby.description_0`

```text
§7可在§e宝石加工台§7中提纯。
```

### `block.thermal_shock_reforged.ancient_skorio_seed.description_0`

```text
§7必须种在种植盆或种植箱上，干渴时记得浇水！
```

### `block.thermal_shock_reforged.ancient_skorio_seed.description_1`

```text
§7没有§e花园灵§7的帮助，这颗种子无法生长。
```

### `item.thermal_shock_reforged.ruby_shovel.description_0`

```text
§9 - 铁属性
```

### `item.thermal_shock_reforged.tourmaline_spice.description_0`

```text
§7无尽花园地幔深处的异域香料，呈丝线状生长。
```

### `item.thermal_shock_reforged.tourmaline_spice.description_1`

```text
§e（等效于64个秘银香料）
```

### `item.thermal_shock_reforged.tourmaline_spice.description_2`

```text
§e（等效于4096个精金香料）
```

### `item.thermal_shock_reforged.silly_straw.description_1`

```text
§a恢复全部氧气！
```

### `item.thermal_shock_reforged.silly_straw.description_0`

```text
§7可从§e供氧海绵§7中提取氧气。
```

### `item.thermal_shock_reforged.diskette_1.description_1`

```text
§8§o“这烂透了”
```

### `item.thermal_shock_reforged.diskette_1.description_0`

```text
§7可用全息监视器读取
```

### `item.thermal_shock_reforged.diskette_8.description_1`

```text
§8§o“蜂巢探险事故样本 1”
```

### `item.thermal_shock_reforged.diskette_8.description_0`

```text
§7可用全息监视器读取
```

### `block.thermal_shock_reforged.fabricator.description_0`

```text
§7以深寒能量为燃料制作高级装备
```

### `item.thermal_shock_reforged.ice_bomb.description_0`

```text
§7冻结附近弱冰素的生物。
```

### `item.thermal_shock_reforged.ender_tentacle.description_0`

```text
§7锚定至玩家朝向的方块，将玩家拉向目标点，消耗末影珍珠。
```

### `item.thermal_shock_reforged.infected_core_sample.description_1`

```text
§7可在§e提取机§7中回收利用。
```

### `item.thermal_shock_reforged.infected_core_sample.description_0`

```text
§7大亵渎者的核心碎片，渗出源初之血。
```

### `item.thermal_shock_reforged.amaljelly_canister.description_0`

```text
§7部署一台静止的小果冻炮台，自动攻击附近生物
```

### `item.thermal_shock_reforged.ruby_sword.description_0`

```text
§9 - 铁属性
```

### `item.thermal_shock_reforged.cryonic_alloy.description_0`

```text
§7§e废料场§7里或许有人知道这东西……
```

### `block.thermal_shock_reforged.pure_muck.description_0`

```text
§7不含任何杂质，纯度100%！
```

### `item.thermal_shock_reforged.carborundum_sickle.description_1`

```text
§7可收割园艺植物，也可当锄头使用。
```

### `item.thermal_shock_reforged.carborundum_sickle.description_0`

```text
§7连续攻击后造成额外伤害。
```

### `item.thermal_shock_reforged.herald_key.description_1`

```text
§7可在§e提取机§7中回收利用。
```

### `item.thermal_shock_reforged.herald_key.description_0`

```text
§7解锁秽血浴场城堡中心的§e巨口之门§7，需集齐三把方可开启。
获取方法，在§e先驱§7周围击杀10只怪物即可出现。
```

### `block.thermal_shock_reforged.blue_crystalleye_lamp.description_0`

```text
§7该方块可通过§e凿子§7改变外观样式。
```

### `item.thermal_shock_reforged.diskette_6.description_0`

```text
§7可用全息监视器读取
```

### `item.thermal_shock_reforged.diskette_6.description_1`

```text
§8§o“寇的晋升”
```

### `item.thermal_shock_reforged.loqendia.description_0`

```text
§d"这枚结晶造物中流淌着异界能量。"
```

### `item.thermal_shock_reforged.diskette_2.description_1`

```text
§8§o“对新任高等主管的看法”
```

### `item.thermal_shock_reforged.diskette_2.description_0`

```text
§7可用全息监视器读取
```

### `item.thermal_shock_reforged.diskette_4.description_1`

```text
§8§o“联合会对层云合金的请求”
```

### `item.thermal_shock_reforged.diskette_4.description_0`

```text
§7可用全息监视器读取
```

### `item.thermal_shock_reforged.cobalt_axe.description_0`

```text
§9 - 铁属性
```

### `item.thermal_shock_reforged.carborundum_sword.description_0`

```text
§9 - 铁属性
```

### `item.thermal_shock_reforged.mithral_spice.description_1`

```text
§e（等效于64个精金香料）
```

### `item.thermal_shock_reforged.mithral_spice.description_0`

```text
§7颗粒状香料，内含闪耀的海玻璃碎片。
```

### `block.thermal_shock_reforged.icidrostrider_constructor.description_0`

```text
§7按空格键【可绑定】进行攻击！
```

### `block.thermal_shock_reforged.skorio_seed.description_0`

```text
§7必须种在种植盆或种植箱上，干渴时记得浇水！
```

### `item.thermal_shock_reforged.carborundum_axe.description_0`

```text
§9 - 铁属性
```

### `block.thermal_shock_reforged.transmutator.description_0`

```text
§7将矿石转化为其§e对应变体§7。
```

### `block.thermal_shock_reforged.transmutator.description_1`

```text
§7可将§e香料§7堆叠压缩为更高等级，亦可将单簇香料分解为低等级堆叠。
```

### `item.thermal_shock_reforged.knights_edge.description_1`

```text
§7右键招架来袭的攻击。招架受击后短时间内攻击会产生爆炸。
```

### `item.thermal_shock_reforged.knights_edge.description_0`

```text
§9- 氯属性
```

### `item.thermal_shock_reforged.pyrogenic_energy.description_0`

```text
用于为制造机供能
```

### `item.thermal_shock_reforged.stellar_scrap.description_0`

```text
§7§e废料场§7里或许有人知道这东西……
```

### `item.thermal_shock_reforged.stratosilver_auger.description_0`

```text
§9 - 冰霜属性
```

### `item.thermal_shock_reforged.stratosilver_sickle.description_1`

```text
§9- 连续命中积累镰伤
```

### `item.thermal_shock_reforged.stratosilver_sickle.description_0`

```text
§7连续攻击后造成额外伤害。
```

### `item.thermal_shock_reforged.stratosilver_sickle.description_2`

```text
§7收割园艺作物，也可用作锄头。
```

### `item.thermal_shock_reforged.blackpowder_spice.description_0`

```text
§7违禁品。
```

### `item.thermal_shock_reforged.bloodstone_sword.description_0`

```text
§9 - 铁属性
```

### `item.thermal_shock_reforged.keystone.description_0`

```text
§7在主世界的3x3平放黑曜石平台中心右键使用，进入无尽花园维度。
```

### `item.thermal_shock_reforged.keystone.description_1`

```text
§7黑曜石必须平放，不可像地狱门一样竖立。
```

### `block.thermal_shock_reforged.red_crystalleye_lamp.description_0`

```text
§7该方块可通过§e凿子§7改变外观样式。
```

### `block.thermal_shock_reforged.chiseled_mantlestone.description_0`

```text
§7该方块可通过§e凿子§7改变外观样式。
```

### `block.thermal_shock_reforged.phaser.description_0`

```text
§7右键将实体向上传送，潜行右键向下传送。
```

### `item.thermal_shock_reforged.bone_pickaxe.description_0`

```text
§9- 铁属性
```

### `item.thermal_shock_reforged.carborundum_pickaxe.description_0`

```text
§9 - 铁属性
```

### `block.thermal_shock_reforged.crammed_archive_cabinet.description_0`

```text
§7可在§e宝石加工台§7中提取。
```

### `item.thermal_shock_reforged.e_xpolar_camera.description_0`

```text
§7对生物使用，以分析其元素弱点与抗性。
```

### `item.thermal_shock_reforged.bone_sword.description_0`

```text
§7延展臂距增加！
```

### `item.thermal_shock_reforged.bone_sword.description_1`

```text
§9- 铁属性
```

### `block.thermal_shock_reforged.frost_lotus_seed.description_1`

```text
§7没有§e花园灵§7的帮助，这颗种子无法生长。
```

### `block.thermal_shock_reforged.frost_lotus_seed.description_0`

```text
§7必须种在种植盆或种植箱上，干渴时记得浇水！
```

### `item.thermal_shock_reforged.ruby_hoe.description_0`

```text
§9 - 铁属性
```

### `block.thermal_shock_reforged.sparkler_seed.description_0`

```text
§7必须种在种植盆或种植箱上，干渴时记得浇水！
```

### `item.thermal_shock_reforged.ruby_axe.description_0`

```text
§9 - 铁属性
```

### `block.thermal_shock_reforged.reinforced_window.description_0`

```text
§7可使用§e凿子§7改变此方块的外观。
```

### `block.thermal_shock_reforged.pure_muck_wall.description_0`

```text
§7杂质减少 100%！
```

### `item.thermal_shock_reforged.ghostglass_sword.description_0`

```text
§9 - 氯属性
```

### `item.thermal_shock_reforged.ghostglass_sword.description_1`

```text
§7挥动时发射幽灵玻璃弹
```

### `item.thermal_shock_reforged.hpsm.description_1`

```text
§8§o“手摇式积雪机！”
```

### `item.thermal_shock_reforged.hpsm.description_0`

```text
§7可为部分方块移除或添加雪层。
```

### `block.thermal_shock_reforged.metallic_debris.description_0`

```text
§7可在§e宝石加工台§7中提取。
```

### `item.thermal_shock_reforged.train_pass.description_0`

```text
§7它的用途目前还不明确。
```

### `item.thermal_shock_reforged.train_pass.description_1`

```text
§7效果暂未实现。
```

### `item.thermal_shock_reforged.empty_amaljelly_canister.description_0`

```text
§7右键已部署的小果冻，可将其回收至罐中
```

### `block.thermal_shock_reforged.muck_pressure_plate.description_0`

```text
§7杂质减少 100%！
```

### `item.thermal_shock_reforged.heart_crystal.description_0`

```text
§7食用后临时提升最大生命值，效果可叠加。
```

### `item.thermal_shock_reforged.bloodstone_shovel.description_0`

```text
§9 - 铁属性
```

### `block.thermal_shock_reforged.sparkly_muck.description_1`

```text
§7可在§e宝石加工台§7中提取。
```

### `block.thermal_shock_reforged.sparkly_muck.description_0`

```text
§7凝胶风味！
```

### `block.thermal_shock_reforged.barbubble_seed.description_0`

```text
§7必须种在种植盆或种植箱上，干渴时记得浇水！
```

### `block.thermal_shock_reforged.ancient_barbubble_seed.description_0`

```text
§7必须种在种植盆或种植箱上，干渴时记得浇水！
```

### `block.thermal_shock_reforged.ancient_barbubble_seed.description_1`

```text
§7没有§e花园灵§7的帮助，这颗种子无法生长。
```

### `item.thermal_shock_reforged.claw_sickle.description_0`

```text
§7连续攻击后造成额外伤害。
```

### `item.thermal_shock_reforged.claw_sickle.description_2`

```text
§7收获园艺植物，也可用作锄头。
```

### `item.thermal_shock_reforged.claw_sickle.description_1`

```text
§7连续攻击后造成额外伤害。
```

### `item.thermal_shock_reforged.amber_spice.description_2`

```text
§e（等效于4096个秘银香料）
```

### `item.thermal_shock_reforged.amber_spice.description_3`

```text
§e（等效于262144个精金香料）
```

### `item.thermal_shock_reforged.amber_spice.description_0`

```text
§7无尽花园标准货币
```

### `block.thermal_shock_reforged.sanctum_light.description_0`

```text
§7可在§e宝石加工台§7中提取。
```

### `item.thermal_shock_reforged.amber_spice.description_1`

```text
§e（等效于64个碧玺香料）
```

### `item.thermal_shock_reforged.virustone.description_0`

```text
§7用于让守护者涌能塔超载，关闭圣所的保护力场。
```

### `block.thermal_shock_reforged.centrifuge.description_0`

```text
§7将能量转化为其§e对应形态§7。
```

### `item.thermal_shock_reforged.watering_can.description_0`

```text
§7用于给干渴的植物浇水。
```

### `item.thermal_shock_reforged.bloodstone_axe.description_0`

```text
§9 - 铁属性
```

### `item.thermal_shock_reforged.liteprism_disk.description_1`

```text
§8§o"帕尚卡！"
```

### `item.thermal_shock_reforged.liteprism_disk.description_2`

```text
§8§o“波尚卡！”
```

### `item.thermal_shock_reforged.liteprism_disk.description_0`

```text
§7连续命中敌人后会使其爆炸。
```

### `block.thermal_shock_reforged.stratus_reactor_core.description_3`

```text
§7第三层：深霜岩块十字形摆放
```

### `block.thermal_shock_reforged.stratus_reactor_core.description_0`

```text
§7放置于搭建完成的反应堆内时，会召唤一大波敌人
```

### `block.thermal_shock_reforged.trapped_archive_cube.description_0`

```text
§7可在§e宝石加工台§7中提取。
```

### `block.thermal_shock_reforged.stratus_reactor_core.description_2`

```text
§7第二层：深霜岩块摆角落，核心置于中央
```

### `block.thermal_shock_reforged.stratus_reactor_core.description_1`

```text
§7第一层：钢茎块打底，角落摆放镶边警示块
```

### `item.thermal_shock_reforged.pathiolite_scrap.description_0`

```text
§7可在§e宝石加工台§7中提取。
```

### `item.thermal_shock_reforged.stratosilver_chainsaw.description_1`

```text
§9 - 冰霜属性
```

### `item.thermal_shock_reforged.stratosilver_chainsaw.description_0`

```text
§9 - 冰霜属性
```

### `item.thermal_shock_reforged.infected_seeds.description_0`

```text
§7可在§e提取机§7中回收利用。
```

### `block.thermal_shock_reforged.neoflorm_seed.description_0`

```text
§7必须种在种植盆或种植箱上，干渴时记得浇水！
```

### `block.thermal_shock_reforged.kilolyte_spawner.description_0`

```text
§7可在§e宝石加工台§7中提取。
```

### `item.thermal_shock_reforged.drinking_straw.description_0`

```text
§7可从§e供氧海绵§7中提取氧气。
```

### `item.thermal_shock_reforged.diskette_5.description_0`

```text
§7可用全息监视器读取
```

### `item.thermal_shock_reforged.diskette_5.description_1`

```text
§8§o“层云部门罢工”
```

### `item.thermal_shock_reforged.zenith_smithing_template.description_4`

```text
 §9血石护甲
```

### `item.thermal_shock_reforged.zenith_smithing_template.description_3`

```text
 §9红宝石护甲
```

### `item.thermal_shock_reforged.zenith_smithing_template.description_2`

```text
§7适用对象：
```

### `item.thermal_shock_reforged.zenith_smithing_template.description_1`

```text

```

### `item.thermal_shock_reforged.zenith_smithing_template.description_0`

```text
§7天顶强化
```

### `item.thermal_shock_reforged.zenith_smithing_template.description_6`

```text
 §9对应宝石
```

### `item.thermal_shock_reforged.zenith_smithing_template.description_5`

```text
§7所需材料：
```

### `item.thermal_shock_reforged.cryonic_key.description_0`

```text
§7解锁§e深寒宝箱§7。
```

### `block.thermal_shock_reforged.pure_muck_slab.description_0`

```text
§7杂质减少 100%！
```

### `item.thermal_shock_reforged.thingamabob.description_0`

```text
§7向§c某处之地§f发送一条信息
```

### `item.thermal_shock_reforged.titanium_mace.description_0`

```text
§9 - 冰属性
```

### `item.thermal_shock_reforged.titanium_mace.description_1`

```text
§7技能冷却结束后命中会将目标向上击飞
```

### `item.thermal_shock_reforged.volcano.description_0`

```text
§9- 火属性
```

### `item.thermal_shock_reforged.volcano.description_1`

```text
§9- 中等射速
```

### `item.thermal_shock_reforged.volcano.description_2`

```text
§9- 发射§e重型弹药
```

### `item.thermal_shock_reforged.hi_volt_lance.description_0`

```text
§7右键电击范围内的敌人。
```

### `item.thermal_shock_reforged.hi_volt_lance.description_1`

```text
§7右键使范围内的敌人遭受电击
```

### `item.thermal_shock_reforged.cobalt_thermoblaster.description_0`

```text
§c- 随环境切换模式
```

### `item.thermal_shock_reforged.cobalt_thermoblaster.description_1`

```text
§9- 快速射速（模式1）
```

### `item.thermal_shock_reforged.cobalt_thermoblaster.description_2`

```text
§9- 中等射速（模式2）
```

### `item.thermal_shock_reforged.cobalt_thermoblaster.description_3`

```text
§9- 发射§e特制弹药
```

### `item.thermal_shock_reforged.zenith_thermoblaster.description_0`

```text
§c- 次要开火模式
```

### `item.thermal_shock_reforged.zenith_thermoblaster.description_1`

```text
§9- 快速射速（模式1）
```

### `item.thermal_shock_reforged.zenith_thermoblaster.description_2`

```text
§9- 中等射速（模式2）
```

### `item.thermal_shock_reforged.zenith_thermoblaster.description_3`

```text
§9- 发射§e特制弹药
```

### `item.thermal_shock_reforged.buzzsaw.description_0`

```text
§7推动使用者向前冲刺，锯开沿途目标。
```

### `item.thermal_shock_reforged.buzzsaw.description_1`

```text
§7向前推进使用者，切开路径上的一切。
```

### `item.thermal_shock_reforged.liteprism_laser.description_0`

```text
§c- 瞬发光束
```

### `item.thermal_shock_reforged.liteprism_laser.description_1`

```text
§9- 快速射速
```

### `item.thermal_shock_reforged.liteprism_laser.description_2`

```text
§9- 发射§e特制弹药
```

### `item.thermal_shock_reforged.liteprism_laser.description_3`

```text
§8§o“这次应该不会再把一切弄坏了吧，大概。——瑟姆”
```

### `item.thermal_shock_reforged.core_eject_flare_gun.description_0`

```text
§9 - 火焰
```

### `item.thermal_shock_reforged.core_eject_flare_gun.description_1`

```text
§c - 次要开火模式
```

### `item.thermal_shock_reforged.core_eject_flare_gun.description_2`

```text
§9 - 中等射速（模式一）
```

### `item.thermal_shock_reforged.core_eject_flare_gun.description_3`

```text
§9 - 较慢射速（模式二）
```

### `item.thermal_shock_reforged.core_eject_flare_gun.description_4`

```text
§9 - 发射§e轻型弹药（模式一）
```

### `item.thermal_shock_reforged.core_eject_flare_gun.description_5`

```text
§9 - 发射§e重型弹药（模式二）
```

### `item.thermal_shock_reforged.carbor_gl.description_0`

```text
§c- 破坏环境，可切换射击方式
```

### `item.thermal_shock_reforged.carbor_gl.description_1`

```text
§9- 较快射速
```

### `item.thermal_shock_reforged.carbor_gl.description_2`

```text
§9- 发射§e重型弹药
```

### `item.thermal_shock_reforged.thistle_bow.description_0`

```text
§9- 叶绿属性
```

### `item.thermal_shock_reforged.thistle_bow.description_1`

```text
§a- 生命低于一半时发射附毒光束
```

### `item.thermal_shock_reforged.thistle_bow.description_2`

```text
§c- 可切换射击方式
```

### `item.thermal_shock_reforged.thistle_bow.description_3`

```text
§9- 较快射速
```

### `item.thermal_shock_reforged.thistle_bow.description_4`

```text
§9- 发射§e特制弹药
```

### `item.thermal_shock_reforged.sylvia_rifle.description_0`

```text
§c- 泵压蓄能
```

### `item.thermal_shock_reforged.sylvia_rifle.description_1`

```text
§9- 极其缓慢的射速
```

### `item.thermal_shock_reforged.sylvia_rifle.description_2`

```text
§9- 发射§e特制弹药
```

### `item.thermal_shock_reforged.chorusian_antennae_helmet.description_0`

```text
§7很适合拿来扮演热力冲击角色！
```

### `item.thermal_shock_reforged.jelly_umbrella.description_0`

```text
§7激活时获得一小段缓降。
```

### `item.thermal_shock_reforged.legendary_jelly_umbrella.description_1`

```text
§6传奇：效果持续时间降低至3秒，冷却降低至4秒。
```

### `item.thermal_shock_reforged.piston_gauntlet.description_0`

```text
§7放在副手使用时，短暂抵消生物攻击。
```

### `item.thermal_shock_reforged.net_launcher.description_0`

```text
§9- 中等射速
```

### `item.thermal_shock_reforged.net_launcher.description_1`

```text
§9- 发射§e线
```

### `item.thermal_shock_reforged.the_judge.description_0`

```text
§c- 动能
```

### `item.thermal_shock_reforged.the_judge.description_1`

```text
§9- 中等射速
```

### `item.thermal_shock_reforged.the_judge.description_2`

```text
§9- 发射§e重型弹药
```

### `item.thermal_shock_reforged.magnetic_crossbow.description_0`

```text
§a- 潜行以瞄准
```

### `item.thermal_shock_reforged.magnetic_crossbow.description_1`

```text
§9- 缓慢射速
```

### `item.thermal_shock_reforged.magnetic_crossbow.description_2`

```text
§9- 发射§e专用弹药
```

### `item.thermal_shock_reforged.zenith_crossbow.description_0`

```text
§a- 潜行以瞄准
```

### `item.thermal_shock_reforged.zenith_crossbow.description_1`

```text
§9- 中等射速
```

### `item.thermal_shock_reforged.zenith_crossbow.description_2`

```text
§9- 发射§e专用弹药
```

### `item.thermal_shock_reforged.cauldron_of_trilobite.description_0`

```text
§7手持炼药锅右键三叶虫即可获得。
```

## 文本统计

- 中文语言键总数：2991
- 结构内书籍文件：4 个，书籍实例：4 个
- 广播/提示消息：40 条
- 进度标题/描述：138 条
- 相机图鉴：86 条
- NPC/剧情界面筛选文本：225 条
- 物品/方块描述：272 条

## 审计边界

- 纯名称、按钮、交易价格、JEI 分类名、音效字幕和技术 ID 没有在正文逐条展开；它们仍全部保留在中文语言文件中。
- 结构书籍页面现在通过语言键加载：英文内容在 `en_us.json`，中文翻译在 `zh_cn.json`；结构 NBT 只保存翻译键。
- 以上“剧情梳理”区分了文本明确陈述和未完成/只能推断的线索，没有把未实现功能当作剧情事实。
