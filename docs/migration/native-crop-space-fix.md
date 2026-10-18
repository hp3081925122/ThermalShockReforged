# 多格作物成熟空间保护

第九批审计Sparkler三高模板时，回查既有FarmSeedBlock.water，确认Barbubble在waterings到3后调用1×2×1 planted_barbubble模板，原未检查pos.above目标方块。StructureTemplate.placeInWorld flag3会直接覆盖上格建筑/箱子，可能丢库存。此为源码和真实NBT尺寸证据，非性能猜测。

现对Barbubble第三次合格浇水（thirsty=true、主手watering_can且旧waterings=2）和旧NBT已为3的成熟尝试，在任何计数/粒子/音效/进度改变前检查上格可替换且未超构建顶界；不满足则原样保留seed/thirsty/waterings与上方容器，移走障碍可继续。合法空间仍完整放原1×2×1模板，浇水次数、概率、音效和成熟粒子不变，未改成单格替代。Sparkler同类3高空间由Growth owner在其模块实施。

待完整迁移后的服务端测试：普通空位成熟上下两格；上方带物箱阻挡时不耗末次水、不删箱/物；移除后一次正常成熟；最大构建高度处拒绝；旧NBT=3阻塞保持可恢复；其余8作物水次数与Gardenling条件不变。本批编译与静态核对仅证明代码/引用，不称实测通过。
