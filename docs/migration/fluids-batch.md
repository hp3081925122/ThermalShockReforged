# 第十批两种流体完整迁移

原07.9的Blood/Springelatin Fluid、FluidType、LiquidBlock及全部procedure为行为证据；原JAR只读。目标1.21.1 NeoForge21.1.233的BaseFlowingFluid/FluidType/LiquidBlock源码已核，不机械复制Forge包名。相关桶/资源/数据外部引用由Luna作只读盘点，代码由主线程实现。

## 注册与原参数

两fluid_type：blood与springelatin；四fluid：source及各flowing_前缀；两LiquidBlock以及两原桶，不注册空BlockItem。两流体每格衰减2、寻坡距离4、抗爆100；Blood tickRate15、可再生源，Springelatin原默认tickRate5且不可再生源。源amount8，flowing保LEVEL与FALLING。沿目标BaseFlowingFluid保原生流动、摧毁方块掉落、下向替换/液体桶交互，不复制全套底层流动算法。

两FluidType均fallDistanceModifier0、可灭火/支持船/可湿润，默认canSwim/canDrown等不凭名称修改。Blood motionScale0.0035，Springelatin0.007/light1/temperature350。fill均bucket.fill、vaporize均fire.extinguish；empty分别bucket.empty_lava与slime_block.place，保声音动作原接线。液体块保原显式MapColor.WATER、硬100/noCollision/noLoot/liquid/DESTROY/EMPTY音效/可替换，Spring发光1。

## 碰撞与表现

Springelatin每次原碰撞将所有实体速度设(0,.55,0)，原双端均执行；这是流体浮升机制，不能按攻击击退擅自削数或删横向归零。Blood仅服务端对Living施真实Infected100tick等级1，复用当前感染实现，不将所有接触状态当内部标记删除。

Blood随机客户端显示原super.animateTick后，1%且上方为空且原graveyard群系才产生CAMPFIRE_COSY_SMOKE1，坐标中心+.5、速度(0,.1,0)。原烟不扩到其它群系，也不减频。Spring无独有显示tick。

客户端四fluid均translucent。Blood still/flowing均block/bloodrubicon；Spring still=block/springel、flowing=block/springel32；保PNG与mcmeta动画原字节和默认tint/fog，不造近似纯色替代。客户端扩展只在client注册，服务端不加载渲染类。

## 验证范围

先确认桶原堆叠/回桶/tooltip与资源、流体单双tag目录并合并真实成员；逐参数/资源/调用链机械核，固定输入后Luna统一编译和静态引用检查。整个模组代码完整后再测试两端：流动/源再生差异、桶取放与回桶、船/水合/灭火/下落、Spring双端浮升、Blood感染/免疫/烟条件与渲染动画，未实测不声称通过。未迁上游群系/世界来源继续列真实待办，不造假配方或普通水替代。

实施补充：两桶原模型均存在，已按目标本地注册将loader和parent的forge前缀各两处改为neoforge，fluid字段保新mod ID；不是用静态桶图替代动态液面。扫描3569个JSON确认除此无其它forge loader/parent/texture。目标FluidType默认语言键为fluid_type.*而原lang实际是fluid.*，显式descriptionId对接已有翻译键。桶仍COMMON/stack1/empty-bucket remainder，客户端DynamicFluidContainerModel.Colors接线保标准流体着色扩展。两原replaceable目录成员已合并进目标单数目录。

两种原Springelatin桶合成8球的配方已完整迁入，图案与用量不变，目标结果item字段转为id；Slime Ball和Jellercel Cell输入均真实已注册，桶保原生合成剩余空桶。Blood没有原配方，不自行添加。
