# Shock Stick 与 Knight's Edge（第80批）

## 实现前核查

已核原07.9两SwordItem、完整hurtEnemy/use实际调用、三procedure、HitFerro/HitCloro/BluePlusBlast原粒子和两原生模型路由。公共ParryingStatus、两actuallyHurt钩子、Charge40tick及Electrified传导已在目标存在，本批复用真实逻辑。不得按“ParryingHitCancel”名字改免伤：原过程没有cancel/setAmount，既有审计有原JAR字节码证据，保原受击反馈/再生/眩晕和既有适度抗性反冲。

## 原参数和完整时序

- Shock Stick：铁级标准工具语义，1150耐久/10速度/3tier攻击＋3剑攻击（玩家基础1合计7）、-2攻速、附魔6、无修复材料，三原tooltip/原平面模型；PNG确为两帧/每帧8tick/非插值，须保mcmeta。命中ferro_weak额外固定7 iframe-bypass伤害，无归属源；ANVIL_PLACE NEUTRAL .4/3、ZOMBIE_ATTACK_IRON_DOOR .5/1、目标半身1 HIT_FERRO、使用者look×-.05反冲。每次真实hurtEnemy另在5 SERVER END对原目标施Electrified240tick/amp1/非ambient/隐藏粒子，初始命中点sparkexplode NEUTRAL .6/3和1 BLUE_PLUS_BLAST(.1偏移，速度0)。
- Knight's Edge：钻石级标准工具语义，7500耐久/4速度/5tier攻击＋3剑攻击（基础合计9）、-2.6攻速、附魔40、Stratus Alloy修复、抗火、两原tooltip/原custom模型完整几何和显示变换。命中cloro_weak额外固定9 iframe-bypass伤害，无归属源；GRASS_BREAK NEUTRAL .4/1、铁门声.5/1、目标半身1 HIT_CLORO和同款反冲。
- Knight右键原super.use返回PASS；保真实交互手、100tick原生物品冷却、速度清零、仅本地SWEEP声PLAYERS1/3、服务端原位10 FIREWORK(1偏移，速度0)。PARRYING窗口NORMAL18/HARD12/其他25tick/amp0；复用原公共受击产生Charge40tick。Charge不是单次消耗，窗口内每次真实命中均在5 END触发，不能擅改一次爆炸。
- 充能命中后固定初始目标坐标：EXPLODE PLAYERS1/1、5 RED_EXPLODE(1偏移、速度0)、inflate3立方AABB按距离遍历全部Entity，排除使用者、ItemEntity和全部已驯服TamableAnimal，其他伤害固定10 iframe-bypass且归属原使用者；不换原生爆炸、不破坏方块、不改球形或只LivingEntity。
- 原HIT_FERRO/HIT_CLORO强制粒子类型为true，完整AAA hitferro/hitcloro、0寿命/全亮/有物理/零速度/LIT。BLUE_PLUS_BLAST为false、原九帧/8tick/quad×16/无物理/零速/全亮/LIT，保原逐帧索引(age%9+1,9)。RED_EXPLODE复用已有真实AAA，不能用单张贴图代替。

## 有证据的安全调整

只服务端执行伤害、状态、世界声音和延时提交。有限5 END任务仅保存原目标/归属UUID及固定中心，不持有玩家或实体引用，先消耗自身执行资格再施加伤害；对自身任务实体不判命中，不因目标移动改变爆炸中心。卸载暂停、重载恢复剩余tick，不强加载目标区块；Shock目标不可解析时仍保独立原位声画。近战后续hurt不再次调用物品hurtEnemy，不能将此行为搬进无来源判断的全局受伤事件而形成递归。

两种专用命中额外伤害不能再叠全局Ferro/Cloro弱点过程：原全局过程明确排除主手相应武器；未来完整元素系统须保此排除，不能复制两份额外倍率。当前该全局系统其余分支仍另待迁移，不将本批两把武器称为全元素系统完成。Shock Stick的Qo商人来源待Qo真实交易迁移，Fabricator为本批正常制造来源。

## 验收边界

本批实现两实际物品、三真实粒子类型/客户端提供器、有限任务及两个Fabricator配方后，统一Java/资源编译并静态比较原资源、数值、时序、父调用和排除规则。不启动游戏，不使用子代理；全模组代码完整后由主对话最终核两端完整命中/传导/帧/音效、冷却/难度/格挡/充能、卸载与重启、范围排除/自伤/递归及Render Preview。


## 本批完成记录

两实际物品、三粒子及真实提供器、有限5 END任务、四原标签成员和两个Fabricator配方均已接线。保Shock两帧贴图mcmeta与Knight完整定制几何；34原资产（含三AAA完整定义及全部引用纹理）、六数据构建副本静态比对通过。核目标VanillaItemTagsProvider：swords自动继承sword/fire_aspect/sharp_weapon/weapon等原版附魔标签，不另复制一套易漏继承的清单。全局Parrying与Electrified复用既有完整实现，本批未擅改免伤或单次充能规则。

统一离线compileJava/processResources通过，未启动游戏。顺带依目标EntityRenderers.validateRegistrations为本批BladeImpact和第79批Blasphemer任务显式接NoopRenderer；跟踪范围0不等于可省客户端注册。Fabricator当前21活动、10个真实产物待迁；全局元素剩余分支/Qo获取/其余模块及最终两端/Render Preview仍未完成。当前用户禁止子代理，全程主对话完成。
