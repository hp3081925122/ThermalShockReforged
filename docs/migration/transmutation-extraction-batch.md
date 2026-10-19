# Transmutator、Extractor 与 Grand Blasphemer（第79批）

## 实现前核查

原07.9 JAR只读，已核三种实际方块、Extractor Constructor、各自真实交互过程、两动画方块的库存/渲染/物品模型、两进度及父链。Extractor反编译有嵌套break，关键判定和随机循环已另核原JAR字节码（extractor79-bytecode.txt）；不能按缩进猜分支或改成一次随机数量。

## 逐项保留

- Transmutator：金属声、硬度2/抗爆20/亮度10、光阻15、原完整立方体模型和tooltip；10%客户端显示tick生成1 END_ROD，方块中心上方1，速度(0,.2,0)。六个单物品转换：粗红宝石↔粗血石、红宝石↔血石、孔雀石↔铋；消耗1，结果掉在中心上方1/拾取延迟10，copper.place/方块声类/1音量3音高。不能擅加熔炼等待、机器能源或普通配方替代。
- Extractor：真实Geo/9槽/六面物品能力/比较器/破坏掉落；原朝向、水浸、形状(.1,.1,.1)-(15.9,14,15.9)、BASEDRUM、SHROOMLIGHT、硬度10/抗爆999/亮度13、光阻0、透明层。完整0循环4.5秒、1为7秒，原交互未实际触发1，不捏造处理动画。原物品经voltflower_bulb.item父模型走Geo，保物品0动画。
- Constructor：ANVIL/1/10/朝向、tooltip、放入后立即变实际Extractor，复制朝向；保留所在水而不在替换时清空水。已有非本块方块实体内容不覆盖。
- 核心样本→1 Primordial Blood，两个声音、Vermilli Leaves的2001事件及blasphemy_3；Herald Key→每轮重采样3..8个5XP球、另直接+10级、30%分支每轮重采样1..3份Helion Slag；Infected Seeds/Blood Vessel/Ashen Bone→2XP；Beetle Claw/Infected Flesh/Tainted Energy→8XP；Pesticide→3XP且10%1 Ferrous Nugget；Neural Chip→20XP且30%分支每轮重采样1..6份Phobotite。均消耗1，掉落原方块整数角坐标，保原各种音效/音高与块碎片，不合并掉落实体或删XP球。
- Grand Blasphemer：原不破坏/防活塞、完整立方碰撞、朝向、9槽、比较器、原模型/透明贴图、动画0(4秒循环)、1(9秒)、2(4秒循环)。原物品实际是平面图标，不能仅因存在GeoItemRenderer就激活3D物品显示。
- 一台装置仅首次可用，持久UsedHole。立即授blasphemy_2、动画1、经验拾取声和弩装填声；40 SERVER END两个声音、104 END拾取声、134 END拾取声+1核心样本、180 END进入已用动画2。保blasphemy_1→2→3完整原父链及后续任务引用，不删除父进度。

## 已核缺陷与替换

- Transmutator/Extractor原固定读主手，又在整个库存按物品类型删除，可能扣错同类有组件物品；改为服务器只消费本次真实交互手的一个输入，创造保持原本也消费的规则。仅服务端掉物品/经验/世界音效，避免双端扣料和重复音效。
- 无效输入保原Transmutator PASS；Extractor无效输入依原SUCCESS消费交互；仅主手无合法输入且副手有合法材料时返回SKIP_DEFAULT_BLOCK_INTERACTION，允许按目标原版的物品/副手顺序尝试。合法主手优先，两端返回值一致，成功后不会再次处理副手。
- Grand Blasphemer过程180 END要求动画2、资源中也有2，但方块属性只允许0..1，故原2永远无法设置。明确补合法2和循环消费，修复已用动画路由，完整保留0/1/2资源；不伪称原代码已能展示2。
- 全局延时闭包改为一个独立、最多180 END的可保存任务实体，只持有锚点和装置令牌，不捕获玩家/方块实体；卸载时暂停，不强加载区块，重载后恢复剩余阶段。先提交UsedHole/唯一任务，再奖励/效果；仅同令牌原装置能被设置最终动画，防新放方块被旧任务改写。原移除装置后合法音效/样本阶段仍继续，不因旧方块离开就删掉后续。
- 迁移版原生地图颜色显式按设备材质设置，挖掘使用原版标签，不写具体工具类检查。

## 实现和静态验收

上述真实类、常规放置newBlockEntity与注册加载两个BE工厂、原资源、两个进度和两个Fabricator配方已接线。四块/六转换/十输入、29视觉和声音资产及7个数据构建副本静态核对通过；Fabricator现在19项活动、12项仍待真实产物。统一compileJava/processResources通过；第一次编译暴露CROSSBOW_LOADING_START在1.21.1为Holder，核目标声明后改取value，修复后编译通过。未进行游戏或渲染运行验证。

原Grand Blasphemer的真实来源为vilebath_castle.nbt的(45,14,60)和vilebathrubicon.nbt的(33,14,26)，含原BE库存及朝向/动画0。当前两模板尚未进入活动结构数据，故自然获取与完整Concierge的blasphemy_1/4结算仍待后续，不能以创意可取得或本批进度JSON存在代替完整主线。详见seventyninth-machines-static-validation.json。

全模组代码完整之前不运行游戏。最终由主对话验证两手/创造/同类组件、所有加工分支、掉落与XP、容器/自动化、水浸与工具、一次性及卸载/重启/移除/替换装置边界；Render Preview对照完整模型、三动画和透明层。禁止子代理。
