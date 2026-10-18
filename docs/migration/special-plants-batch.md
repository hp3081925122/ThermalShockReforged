# 特殊植物五块完整迁移

先依据special-plants-next-audit与原五类/procedure写矩阵，再实施。范围为bounceweed、floatweed、vermillion_roots、loque_dandelion和windblown_loque_dandelion；农作物/镰刀/种子BE另由farming模块完整迁移，不以本批代替。

两增益花保原Flower原生土壤、XZ偏移、光9、发光渲染、火100/60，炖菜分别Jump/Slow Falling100tick。碰撞不在sprintweed_blacklist的LivingEntity服务端各施JumpII/SlowFallingII120tick、ambient和particles均true，不增加CD，不偷换为双高；与已迁Sprintweed一起闭合原共享三分支。

Roots实际Block水浸，不是Flower；光7、FUNGUS音、BASEDRUM、硬.2抗爆0、speed.7/jump.9、无碰撞、无可视形状、遮光0、无水时透天光、轮廓(5,0,5)-(11,16,11)，不附加原无土壤规则。原缺地图颜色，依据根块红色贴图补COLOR_RED，实际Xaero验证待整模组完成。

两蒲公英保REGENERATION炖菜100tick、火100/60及原生Flower属性。普通形态非潜行碰撞变windblown：5POOF位于块中心、偏移.3/速度.05、wool.break音1/.6；非潜行右键变形只有1POOF位于(.5,.7,.5)，原无右键声音。不潜行限制只在普通变体，windblown主手BONE_MEAL右键不限潜行恢复普通、消耗1份实际主手骨粉并原bone_meal.use音NEUTRAL1/1。原扫描整个背包/合成格扣材料改为实际合格堆栈；创造也保原消费规则。两形态没有原共享state属性，不引入虚构阶段。

旧碰撞/交互双端改块及本地发声改为服务端权威，每次真实转换仅一次；保粒子数量/位置/音量音高，避免双端随机和重复扣除。无物品、无效物品和潜行交互仍消费原方块交互结果，不顺带触发原无骨粉复制行为。

独立五loot保原行为，worldgen逐追bounce/float/heights_weeds全部分支与Roots的vermilli_fungus/vermillion_hyphae_patch。只导全依赖闭合配置，不删selector分支。两Dandelion原无直接自然配置，不虚构生成。编译和静态固定后由子代理执行；整个模组迁完前不运行两端。


已导入五个原自掉loot和bounceweed/floatweed/heights_weeds/vermilli_fungus/vermillion_hyphae_patch各完整configured+placed；heights_weeds真实只有bounce/float两分支（此前泛称需核Sprintweed，现已核原JSON纠正），没有删除分支。Roots纹理实际由模型cross/particle指向vermillionhyphae.png，主要非透明色(197,38,66)、(111,9,63)、(84,7,48)，COLOR_RED补色有实际取色证据。

新旧生成配置共25处UniformInt旧value包装已按目标MapCodec搬平，不减数值/次数，清单int-provider-format-fixes.json。此举修复此前“原JSON相同即兼容”的不足；其他provider Codec仍由子代理追加审计，运行解码待整模组完整后测试。
