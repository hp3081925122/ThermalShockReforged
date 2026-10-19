# Thermoblaster 第83批实现前核查

第82批已本地提交1b0fc3d，统一编译/静态核对通过；本批先建文档再实现。仍禁子代理，全代码完整前不启动游戏。

CobaltThermoblasterItem与ZenithThermoblasterItem均普通Item，1400耐久/UNCOMMON、附魔5、非抗火、reequip false，无显式每发耐久消耗。两枪选中时CoreEjectFlareGunItemInHandTick禁近战，复用HeldWeaponState；完整四行tooltip、独立原纹理、共用完整cobalt_thermoblaster Geo/0/1/2动画、原builtin/entity父和entityTranslucent、手势/手变换不能省。

Cobalt实际左键唯一入口ThermoblasterEnviron；水下才三连发，陆地六发散弹，不添加右键人为切模式。已核原JAR字节码：玩家无CD且创造或有Specialized Round才发，非玩家无弹权；水下CD10、立刻一发及2/4 SERVER END后两发，固定初始x/y+1.5/z、逐发当前look、原baseDamage0.5/KB0/速度8/散布1，每发原thermoblasterburstfire NEUTRAL1/1与动画1。陆地CD20、同步六发baseDamage8/KB1/pierce2/速度0.8/散布20，原thermoblasterspreadfire1/1和动画2；每个完整触发只消耗一Specialized Round，创造免耗。

Zenith真实左键ThermoblasterMainFire、右键ThermoblasterAltFire，模式不依赖环境。三连发与Cobalt水下相同但速度10而非8；散弹相同。不要偷用已有弹体辅助shoot：其默认速度2.6/伤害1，且显式点火100秒，而原真实物品工厂没有点火。两个真实工厂都复用已有ThermoblasterBulletEntity/Shotgun及全部命中效果、40 END寿命、直射无重力/魂火拖尾和散弹减速60/amp3、原弱点附伤，不删现有效果。

连续延时只需有限4END保存任务，存原位置、使用者UUID及实体ID、最近视线、原物品Geo实例ID和枪种。第一发同步且当场播放完整动画，后两发也重新触发原栈对应动画ID，不捕获ItemStack/玩家对象或写到后来换上的另一枪。离线或移除后保最近方向继续原合法发射，重生新玩家不继承旧操作，固定锚点不追人，不强加载。UUID离线归属复用第81批已核的最小新弹Accessor，原有弹体KB由同包工厂写，穿透复用实际Invoker。新有限载体NoopRenderer和按栈缓存清理一起接。

原Cobalt无弹陆地会先走水下分支失败音再走陆地失败音，字节码287与后续953分支均会播放；改成服务端一次反馈，明确为无弹重复音修复，不发弹、不加CD、不减料。其余正常有弹分支原时序/数量/参数完整保留。未凭名称加入非原有元素/weapon标签，须以原JAR清单核。

真实Cobalt制造产物实现后才启用原完整费用。原Zenith确有zenith_thermoblaster_recipe锻造配方：template=Cobalt枪、base=Zenith模板、addition=Stratus合金。用户明确要求修正这个不合理槽位，现改为template=Zenith模板、base=Cobalt枪、addition不变。已核1.21.1 SmithingTransformRecipe.assemble从input.base()复制组件，因此改正后升级还能继承原枪的附魔、损耗和名称；旧槽位复制的却是模板组件。记录于[重做行为调整记录](REFORGED_CHANGES.md)。完整动画资源除0/1/2还含3（0.0625秒），全保留；真实发射入口只触发1/2。原标签nomelee包含两枪，weapon只有Cobalt；未发现Zenith结构NBT引用，不能据此漏掉已存在的锻造获取。两普通耐久Item补经核原BREAKABLE对应的新durability标签。较大整组完成统一compileJava/processResources，逐项原资产/声音/动画/参数/调用/构建副本静态核对；运行验收到整模组代码完整后主对话执行。


## 第83批完成记录

两款普通耐久Item的真实注册、完整手持Geo/原两纹理/透明层/手势/变换/全部0至3动画、四行双语说明与缓存回收实际接线。Cobalt左键水下三连发/陆地六散弹，Zenith左键三连发/右键六散弹；各次仅耗一Specialized Round、创造免耗，不增加每发耐久。三连发保同步与2/4 SERVER END、固定初始位置和逐发动态方向、0.5伤/8或10速度/1散布；六散弹保8伤/0.8速度/20散布/KB1/穿透2、原10/20CD。新有限保存载体只持UUID/实体ID/动画ID与值，接NoopRenderer；保已有完整弹体寿命、尾迹、命中与弱点附伤，真实物品工厂不点火。

三项明确修正：Cobalt陆地无弹重复失败声改一次；Zenith散弹误检查Cobalt类型导致动画不播，接真实Zenith动画2；用户要求的锻造槽位改模板template、枪base、Stratus合金addition。已核原生assemble从base复制组件，升级保原枪附魔、名称和损耗。集中记录见REFORGED_CHANGES.md，未削枪械模式或表现。

整组compileJava/processResources已通过（1分32秒，24任务2执行22最新）。槽位修改只涉及JSON，此后仅processResources通过；九原资产和八数据/语言/声音构建路径静态比对通过。Cobalt原完整22/5/5/1费用真实启用，Fabricator当前25活动、6待真实产物实现。整模组未完整，未启任一游戏端或Render Preview，无子代理；最终枪械水陆模式、左右键、锻造组件、存档和渲染仍待全部代码完成后验证。
