# 第92批：原生十项酿造与星月瓶

主聊天在全局元素链之外负责原生酿造补齐，与独立聊天的实验室文件无交叉。原JEI插件只是十项配方展示；真实行为在十个 recipes/brewing/*BrewingRecipe，不能因JEI展示延期而漏掉酿造台玩法。

## 原始配方矩阵

| 输入 | 材料 | 输出 |
| --- | --- | --- |
| Loque Milk Bottle | Cocoa Beans | Cocoa |
| Glass Bottle | Loque Milk | Loque Milk Bottle |
| Glass Bottle | Starfruit | Bottle of Starlight |
| Glass Bottle | Lunalemon | Bottle of Lunarlight |
| Icy Snowcone | Starfruit | Starry Snowcone |
| Icy Snowcone | Lunalemon | Lunar Snowcone |
| Icy Snowcone | Barbubble Melon Slice | Fizzy Snowcone |
| Awkward Potion | Breather Sponge | Water Breathing |
| Awkward Potion | Bioelectric Cell | Electrifying（360tick、amp1、可见） |
| Dew Bottle | Hearth Honey | 原版Regeneration |

前两项药水配方应保普通/喷溅/滞留容器；Dew转固定普通药水。原电药水已真实注册，不能重复注册。配方返回新栈，不复制无关输入组件，材料剩余物由原生酿造台管理。

## 待补依赖与验收

实现前未发现 Bottle of Starlight/Lunarlight 的真实方块注册；本批已先核原方块/BE/完整Geo/光照/交互链，再完整补齐并启用所有十项配方。其余材料和食品的真实注册已找到。

先查目标NeoForge酿造事件/Builder、原生PotionContents和酿造槽规则；核玻璃瓶和雪球食品输入确实可放入。保原三瓶并行、单次材料消耗和容器余物，必要时仅修已核的输入槽阻塞问题。整批代码冻结后统一编译，完整模组前不启游戏；JEI专用展示仍在唯一延期文档。

## 完整实现（整组编译待冻结）

已通过目标21.1.233 sources核RegisterBrewingRecipesEvent在主事件总线上双端触发，Builder.addRecipe支持任意输入、addMix支持普通/喷溅/滞留容器，PotionSlot和BE自动化均查询PotionBrewing.isInput。直接使用原生Builder的8个普通配方+2个药水Mix，无额外槽位Mixin；一份Loque Milk的空桶由原生BE材料余物链返还，三底槽并行和燃料/400tick遵循原生。

两满瓶复用已有Geo装饰框架，保0/1动画属性、两个0过渡控制器、同原完整几何/动画、各自星/月贴图、entityTranslucent手持与世界渲染/全部原显示变换、0.1..15.9形状、10光照、0遮光/透天光、九槽存储/六面自动化/比较器及正常采集一份自身。原无loot JSON，由原Java fallback给自身；原无工具等级/挖掘标签，不额外绑定原版工具类。MapColor给星黄/月紫以满足既有Xaero通用要求。

原吸管交互双端替块/加饥饿，直接setFoodLevel(+5)/setSaturation(+6)可超过原版上限；重做只在服务端成功替为空瓶后提交一次，原生FoodData.eat(5,.6)保+5/+6并正常封顶，使用实际交互手，吸管仍不额外损耗耐久、不添加CD。替换时九槽库存由已有onRemove正常掉落，不遗失。饮用声一次服务端广播。原可满饥饿饮用规则保留，未添加canEat条件。

三个雪锥原件仅含FoodProperties而无完成使用procedure，现有营养/饱和度实现无需新增效果；电药水已注册360tick/amp1/可见，原注册复用不重复造药水。最终专服检查十配方、三底槽/漏斗、空桶、三类药水容器、两满瓶吸管/双人同tick/方块替换失败/九槽自动化和渲染仍待整模组完成。


最终状态由 batch92-integration-validation.json 与总迁移文档“第92批统一收尾”覆盖：整组类型编译、资源复制与集成静态核对通过，最终游戏／渲染验收仍待完整迁移。本报告先前的待编译／增量未应用描述为交接时状态。
