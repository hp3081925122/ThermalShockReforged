# 第117批A共享接线与未决项

基线`27f8be1`；A独占生产11文件收尾后冻结，不编译/启动/stage/commit，不修改`docs/testing/*`。完整清单、哈希、静态范围见A主文档、manifest及matrix。以下共享文件均未由A修改。

## 必须接入：Bloomling爆炸载体

原Budding死亡40 SERVER END、Dandy攻击20 SERVER END不能由尸体自身计时：正常尸体先KILLED移除会吞掉回调。A已新增`entity/cavebloomer/BloomlingBurstEntity.java`，由`BloomlingEntity`提交；保原20/40帧尸体，不靠延长寿命补回调。

1. `ThermalShockReforged`构造器已有`CavebloomerContent.register(bus)`旁增加：

   ```java
   com.hp.thermalshockreforged.entity.cavebloomer.BloomlingBurstEntity.register(bus);
   ```

2. `ClientEvents.renderers(EntityRenderersEvent.RegisterRenderers)`增加：

   ```java
   event.registerEntityRenderer(com.hp.thermalshockreforged.entity.cavebloomer.BloomlingBurstEntity.TYPE.get(), net.minecraft.client.renderer.entity.NoopRenderer::new);
   ```

普通Entity载体不需要生物属性、贴图/动画、生成蛋、战利品或语言键。任务保存剩余END、攻击快照、姿态/眼高、固定声音原点和目标眼高兜底；本体仅保存任务UUID。正常KILLED继续，真正discard/召唤TTL到期取消，任务真正销毁释放本体关联；卸载保关联且不强加载。提交失败20tick重试，保有界创建。弱引用只读取原本体当帧移除后的最终姿态，不保强实体闭包。

主线须在`REFORGED_CHANGES.md`记录持久载体/卸载及真正移除边界。先完成接线再统一编译，本路未宣称可运行。

## 必须接入：Net捕网弹完整模型

原`client/renderer/NetBulletRenderer`使用`ModelNet_Proj`，不是ThrownItemRenderer。现共享`ProjectileRenderers`却绑定通用物品，构成已确认简化。A新增`client/projectile/NetBulletRenderer.java`，原几何/UV/骨骼姿态逐项相同，纹理`textures/entities/net_proj.png`已与原JAR字节相同。

1. `client/projectile/ProjectileRenderers.register`只替换NET_BULLET这一行：

   ```java
   event.registerEntityRenderer(ProjectileContent.NET_BULLET.get(), NetBulletRenderer::new);
   ```

   新类位于同包，无需import。保留其他投射物绑定。

2. `ClientEvents.firearmLayers(EntityRenderersEvent.RegisterLayerDefinitions)`现有两处模型层调用后增加：

   ```java
   com.hp.thermalshockreforged.client.projectile.NetBulletRenderer.layers(event);
   ```

模型的0厚度网面、32×32尺寸、64×64 UV和两轴插值旋转均为原值，不用黄色物品替代。客户端实际朝向/双面可见性和Render Preview仍待完整集成后验收。

## 共享时序缺口：IceSquadProjectileEntity

A根2/16 AltCryonic/Cryonic、根52 GunnerBullet原tick先super后本地SOUL_FIRE_FLAME/无重力并排80 SERVER END删除；根51 BurnerFlame排20 END。主线StriderAttack为5 END。当前共享目标`age++`按实体tick终止，阶段不同。

请主线在整合Strider根时统一有限SERVER END计时，保每kind寿命、首个实际tick开始、客户端粒子、父箭链、拾取禁用、归属过滤、伤害/燃烧及既批准撞块误伤owner修复。原每tick重复排队不必复制，只保首个有效终止时点。

## 共享时序缺口：CavebloomerImpactEntity

A根84 PoisonAttackEntityEntity与主线ShockAttack共类，A已标共享只读。当前Shock的`remaining=19`、跳出生gameTime并按实体tick减；原ShockAttackSpawn为20 SERVER END，需主线修阶段。

Poison必须保持`finalizeSpawn`同步发POISON_CLOUD count0/speed1、ZOMBIE_VILLAGER_CURE NEUTRAL1/1、inflate2仅Player真实Poison100/3与6 UNATTRIBUTED_FIREBALL并删除。不得误改为延时。保原出生原点、伤害快照与两分支不同伤害类型。

## 共享时序待主线复核：ManglerEntity

A Mangler与主线SummonedMangler共用实现。原hurt免疫前设置stunned/60真实Slow/可见CD，并排60 SERVER END清动画；当前共享类在实体tick中`--stunRemaining`解除。请主线核同tick受伤发生在本体tick前/后的差异，统一END解除时保原药效、白色混色、非summoned抗性反冲、死亡/换维和重载边界。

## 未决原差异：GuardianPylon死亡粒子坐标

原`SparklerbulbOnTickProcedure`服务端死亡收尾END_ROD使用固定`(.5,.5,.5)`；当前`GuardianPylonEntity`使用本体`getX/Y/Z`。粒子种类/5数量/offset1.5/speed.5已对上，但坐标不能声称一致。

REF115要求保真实入口，未找到批准改该坐标的记录。A未擅自还原原明显异常坐标，也未把它记成等价；请主线选择保原坐标或在REF明确记录修正依据。该独占实体本轮未修改。

## 归属与保留项

8个主要共享只读：SkeletalTitanEntity、JellercelEntity、MoverOrbEntity、IceSquadProjectileEntity、ErrantEntity、ManglerEntity、CavebloomerImpactEntity、HighDirectorSopelEntity。根入口、ClientEvents、配置、语言、标签及主线占用文件始终只读。主线本轮新修Jellercel原0攻击属性/固定1仙人掌碰撞保持，A没有回写。

BlizzardDropPod保116批已有END链；Benzor/Concierge scrambler换肤按用户跨模组延期；原无效再生命令不造药效；ElectricSphere有效正伤桥、GreenOrb撞块owner与Infestophage无效被杀者再生沿既批批准修复。其余矩阵显式pending继续保留，不能因1256静态检查通过而声明所有语义/运行/渲染已完成。
