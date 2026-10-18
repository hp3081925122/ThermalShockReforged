# 第43批：ThermaDev Plush（迁移前契约）

原件ThermaDevPlushBlock/TileEntity/BlockModel及右键Procedure为依据。保水平朝向、animation0..1、九槽WorldlyContainer/六面自动化、WOOL音效、硬度1/抗爆999、实体动画渲染/透明、正常碰撞及0.1..15.9包围盒，不新增比较器。保thermplush Geo/animation、therm2贴图、原物品displaysettings及获取链，来源由只读子代理核实。

原右键先dropResources再destroyBlock(false)，且块无onRemove库存处理。目标服务端先确认成功移除，再按旧state结算原块战利品；共享Geo onRemove正常掉落九槽内容，修自动化库存丢失。空手及持物交互均保右键拾取，不新增音效、菜单或动画触发。MapColor按实际贴图补有意义颜色，Xaero最终渲染待整模组完成后验证。

复用既有Geo种类注册、九槽BE、透明方块/物品渲染器，避免复制类与逐tick额外逻辑。固定快照后Luna编译及原件机械对照；中途不启游戏。本批不将Aqeron/宫殿未实现内容标为完成。


实施及验证：真实Block/Item/BE及原创意页顺序已接入，原三份模型/纹理/动画哈希一致、三份JSON仅命名空间转换且实际显示设置父模型存在；无原配方/战利品/进度或其他Java来源，按原创意页/宫殿放置获取，不编造合成。Luna固定快照Java21离线三任务18.9秒成功（1执行/24缓存），原属性/动画域/九槽与六面能力/朝向机械对照通过。报告fortythird-plush-static-validation.json。未执行运行时、Xaero或渲染验收。
