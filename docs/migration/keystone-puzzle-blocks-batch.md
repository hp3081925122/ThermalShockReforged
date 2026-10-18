# 第38批：Keystone四功能机关（代码与静态阶段完成）

依原07.9四Block/TileEntity/Model/Renderer/DisplayItem与三个交互procedure迁移：Rinthium Button、Rinthium Lever、Inactive Keystone Pedestal、Keystone Pedestal。完整状态/材质/碰撞/发光/掉落/九槽自动化与比较器、Geo模型/透明材质/数字动画保留；原实体/资产证据见keystone-chamber-next-audit.md。复用实际共用的GeoDecor九槽BE与客户端渲染，活动台座独立animation0..2属性，其他已有块维持0..1，禁止删去领取动画2或扩大所有方块状态域。

按钮一次active true/animation1/mantle_lever；拉杆与inactive台座检测四固定偏移active及中心caninteract，原三位置animation1、两声、失败bass。台座可交互时发真实Keystone1/animation2/cave声/原全服文本，状态全部服务端先消费/持久化/更新客户端，取消旧客户端重复业务。给物用既有原生give helper保满包掉落。

源码发现领取后的caninteract=false会使拉杆再次满足!caninteract，原没有已领取标记，可能反复重置后刷Keystone。交给Luna核原JAR字段分支，确认后增加中心台座持久KeystoneClaimed锁：只拦同一机关已经领取后的再武装，不删首次解锁、按钮动画和奖励；初始化caninteract的旧模板仍可正常领取。更换/重造机关的创造操作不扩大成全世界领取锁。

本批只迁四功能块和真实获取动作，不提前导入依赖未审完的81000块密室、睡醒双监听冲突与有限延迟广播载体；它们另批闭合。模板/功能来源仍明确待办，不把四注册当完成整条自然来源。完整模组后再运行两端与Render Preview，当前只交Luna静态核对与最小编译。


实施依据追加：Luna已核原JAR两procedure的字节码及所有caninteract读写，确认无其他claimed门槛，重复再武装路径成立。目标在中心BE先提交KeystoneClaimed/清caninteract并setChanged，再发奖励；lever同步排除claimed。按钮旧类虽有9槽BE却未覆写移除掉库存，现采用已有GeoDecor原生容器移除掉物逻辑修正库存丢失，不声称这是原先已有的完整行为。四块复用既有透明Geo/ItemRenderer与九槽能力；其他块动画范围仍0..1。


最终收口：四真实方块/物品/九槽BE/原生能力与原Geo/Item透明渲染通过既有注册循环接线；独立活动台座animation0..2，其余既有块保持0..1。Luna原JAR字节码、四组模型/动画/贴图SHA、命名空间转换后的itemmodels/blockstates和原displaysettings、四偏移/三处触发/声音参数及持久claimed排重全部核对通过，无待修具体差异。固定快照三任务离线Java21编译exit0/18.7秒/25tasks(1executed24up-to-date)，日志D:\AI临时文件\ThermalShockMigration\thirtyeighth-keystone-compile.log。本批没有启游戏，密室模板/自然生成仍待下一批，整模组未完成。
