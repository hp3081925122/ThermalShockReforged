# 同系食品迁移

迁移前证据：原SnowdaeItem、LoquesicleItem、BloodPopItem、ChalkCandyItem、LuckTicketItem、MuckPieItem均调用同一个SnowdaePlayerFinishesUsingItemProcedure，原通过权限4命令向自己施加farmersdelight:nourishment 200秒等级0、显示粒子。与第一批MuckPie合并为一个真实重复逻辑，不按食物名称删掉任何调用。

六种都为COMMON、堆叠1、营养10、饱和乘数2、60tick；仅BloodPop是DRINK，其余EAT。均使用原生食物消费及饱食条件，真实消费完成才服务端查可选效果注册并施加4000tick等级0；未安装Farmer's Delight时不报命令权限／未知效果错误，也不添加硬依赖。原该procedure只加效果，没有其他隐藏buff或特效。

新增loque_milk_bottle原COMMON／单堆／营养1／饱和.5／DRINK20tick；原无清药效功能，不因为名字牛奶加清除。目标FoodProperties.usingConvertsTo原生给玩家返玻璃瓶，非玩家消费完也保留原返瓶；不无故新增合成余物属性。目标Item.finishUsingItem、LivingEntity.eat和Player.eat已核：usingConvertsTo仅Player负责，故非玩家额外处理必需。

此外frozen_chilloo与marrow_meld原都是COMMON／堆叠64／营养1／48tick，无procedure；饱和乘数分别0.1和-0.1。保留Marrow Meld真实负数参数，不擅改成正值或默认32tick。

此次新增8ID（5食品＋瓶装奶＋2慢食物），原MuckPie重用同类。原assets保持不改；相关配方按真实注册引用闭合后导入，未闭合生产源留清单。统一编译／静态待第三批冻结，不启动游戏。

完整MiscFoodDrops原6分支已迁为统一FoodDrops监听：doEntityDrops开启时，Effigy→Snowdae .007、Magnetite→Loquesicle .01、BeetleThrasher→BloodPop .01、Lens→ChalkCandy .01、Bonerahna→MuckPie .004、Spectre→LuckTicket .004，各一件且拾取延迟10。原没有这些食品直接配方，不虚构合成方式；原LyBuy GUI交易（食品10级、奶瓶5级）仍待菜单批次。对应未迁生物在本批不注册空实体；监听按真实原实体ID就绪，Bonerahna旧专用分支删除防重复。

版本边界复核：1.21.1 FoodProperties的Codec.FLOAT和ByteBufCodecs.FLOAT允许负数，Marrow Meld的-.1乘数实际生成-.2饱和值，网络／持久化不会因此拒绝。目标FoodData.add把结果限制在0..foodLevel，旧1.20.1只做Math.min，原在当前饱和度不足.2时可能变成负数。此迁移沿用目标原生下界0，不改全局FoodData或人为制造负饱和；当前饱和至少.2时仍扣原.2。此差异已明确记录，不能称所有边界逐字等价。
