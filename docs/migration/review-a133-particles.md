# 第133批：52粒子定义与 provider 闭环

## 结果

独占worktree：`D:/AI临时文件/ThermalShockMigration/worktrees/batch133`；分支 `codex/batch133-particles`；起点 `949640162380f3f15a50fc3f63858319c690a097`。

52/52原粒子定义已核真实注册、总线、客户端provider与参数：41个AAA外壳，11个普通精灵粒子。23个注册入口、12个间接provider入口及5组动画帧公式闭合。**这是provider层静态闭环，不是G02整体完成。**

## 本批修复

- 补回 `orb_red_med`、`orb_red_smol`、`projectile_impact`、`blood_petal`、`kilo_attack` 的真实注册及provider。前四项标志false，kilo_attack为true；未补造实体技能入口。
- `prismatic_boom` 的 `SimpleParticleType` 标志由false恢复原true，其他注册标志与原件逐项一致。
- BloodPetal保size0.1、寿命50..69、gravity=-0.4、物理碰撞、输入速度×0.6、随机精灵、OPAQUE、原生环境亮度与每tick旋转0.05；原角加速度恒0的状态简化为同值常量更新，不减少效果。
- 其余四个缺失AAA provider保原effect、寿命、速度、渲染层、亮度与物理：redshortorb(1/opaque/环境亮度/无物理/零速)，smallredshortorb(1/opaque/环境亮度/有物理/零速)，crosshair(0/LIT/全亮/有物理/输入速度×1)，kiloattack(0/LIT/全亮/有物理/零速)。

## 行为与资源证据

原52粒子源码、原JAR类哈希及完整javap输出已固定；全部目标provider参数逐项核对。原/目标Particle.tick主体、friction0.98、随机quadSize与SpriteSet初始化语义相同。共享AAA provider保super.tick后调用，含移除当次；寿命0/1/7正常发1/2/8次，不能改成只在未移除时发射。每次独立EmitterInfo保原默认scale/speed，不共享可变position。

141份粒子JSON与直接引用精灵资源按命名空间等价/二进制一致验证；另核完整粒子贴图树121份文件（含5份.mcmeta），原AAA整棵effeks资源树130份文件逐字节一致。去重后303份资源在processResources输出中再次核对。资源树一致证明原依赖未被删减，不证明新版引擎视觉已实测一致。

## 逐项矩阵

| 粒子ID | 原类型标志 | 原/目标表现 | 注册/provider |
| --- | --- | --- | --- |
| `green_particle` | true | 精灵; TTL=Math.max(1, 16 + (random.nextInt(2) - 1)); LIT; 全亮; physics=false | 静态闭合 |
| `purple_sparkle` | false | 精灵; TTL=Math.max(1, 30 + (random.nextInt(24) - 12)); LIT; 全亮; physics=false | 静态闭合 |
| `orange_ring_blast` | false | 精灵; TTL=8; LIT; 全亮; physics=false | 静态闭合 |
| `toxic_bubble` | false | 精灵; TTL=Math.max(1, 30 + (random.nextInt(24) - 12)); LIT; 全亮; physics=false | 静态闭合 |
| `blue_x_blast` | false | 精灵; TTL=8; LIT; 全亮; physics=false | 静态闭合 |
| `blue_plus_blast` | false | 精灵; TTL=8; LIT; 全亮; physics=false | 静态闭合 |
| `orb_red_med` | false | AAA:redshortorb; TTL=1; OPAQUE; 环境亮度; physics=false | 静态闭合 |
| `orb_red_smol` | false | AAA:smallredshortorb; TTL=1; OPAQUE; 环境亮度; physics=true | 静态闭合 |
| `squall_smoke` | false | AAA:squallsmoke; TTL=1; OPAQUE; 环境亮度; physics=false | 静态闭合 |
| `squall_visage` | true | 精灵; TTL=1; LIT; 全亮; physics=false | 静态闭合 |
| `lens_visage_1` | true | 精灵; TTL=1; LIT; 全亮; physics=false | 静态闭合 |
| `lens_visage_2` | true | 精灵; TTL=1; LIT; 全亮; physics=false | 静态闭合 |
| `fog_cloud` | true | AAA:fogcloud; TTL=7; OPAQUE; 环境亮度; physics=false | 静态闭合 |
| `reactor_shockwave` | false | AAA:shockwavereactor; TTL=7; LIT; 全亮; physics=true | 静态闭合 |
| `projectile_impact` | false | AAA:crosshair; TTL=0; LIT; 全亮; physics=true | 静态闭合 |
| `blood_petal` | false | 精灵; TTL=Math.max(1, 60 + (random.nextInt(20) - 10)); OPAQUE; 环境亮度; physics=true | 静态闭合 |
| `blood_spiral` | true | AAA:bloodspiral; TTL=1; OPAQUE; 环境亮度; physics=false | 静态闭合 |
| `blood_attack` | true | AAA:bloodattack; TTL=1; OPAQUE; 环境亮度; physics=false | 静态闭合 |
| `blood_splatter` | true | AAA:bloodsplatter; TTL=0; OPAQUE; 环境亮度; physics=false | 静态闭合 |
| `magnetic_boom` | true | AAA:greenexplode; TTL=0; OPAQUE; 环境亮度; physics=false | 静态闭合 |
| `prismatic_boom` | true | AAA:purpleexplode; TTL=1; OPAQUE; 环境亮度; physics=false | 静态闭合 |
| `prism_cluster` | true | AAA:prismcluster; TTL=1; OPAQUE; 环境亮度; physics=false | 静态闭合 |
| `hearthfly` | false | 精灵; TTL=Math.max(1, 26 + (random.nextInt(20) - 10)); LIT; 全亮; physics=false | 静态闭合 |
| `white_orb` | false | AAA:whiteorb; TTL=1; OPAQUE; 环境亮度; physics=false | 静态闭合 |
| `red_orb` | false | AAA:redorb; TTL=1; OPAQUE; 环境亮度; physics=true | 静态闭合 |
| `cam_photo` | true | AAA:camphoto; TTL=0; LIT; 全亮; physics=true | 静态闭合 |
| `hit_resist` | true | AAA:hitresist; TTL=0; LIT; 全亮; physics=true | 静态闭合 |
| `hit_cryo` | true | AAA:hitcryo; TTL=0; LIT; 全亮; physics=true | 静态闭合 |
| `hit_pyro` | true | AAA:hitpyro; TTL=0; LIT; 全亮; physics=true | 静态闭合 |
| `hit_ferro` | true | AAA:hitferro; TTL=0; LIT; 全亮; physics=true | 静态闭合 |
| `hit_cloro` | true | AAA:hitcloro; TTL=0; LIT; 全亮; physics=true | 静态闭合 |
| `zap_attack` | true | AAA:zapattack; TTL=1; LIT; 全亮; physics=true | 静态闭合 |
| `red_explode` | false | AAA:redexplode; TTL=0; OPAQUE; 环境亮度; physics=true | 静态闭合 |
| `ice_bomb_effect` | false | AAA:icebomb; TTL=0; OPAQUE; 环境亮度; physics=true | 静态闭合 |
| `turq_explode` | false | AAA:turqexplode; TTL=0; OPAQUE; 环境亮度; physics=true | 静态闭合 |
| `fire_bomb_effect` | false | AAA:firebomb; TTL=0; OPAQUE; 环境亮度; physics=true | 静态闭合 |
| `gold_glitter` | false | AAA:goldglitter; TTL=0; LIT; 全亮; physics=false | 静态闭合 |
| `volcano_spark` | false | AAA:volcanospark; TTL=0; LIT; 全亮; physics=false | 静态闭合 |
| `holo_1` | false | AAA:holo1; TTL=0; LIT; 全亮; physics=false | 静态闭合 |
| `holo_2` | false | AAA:holo2; TTL=0; LIT; 全亮; physics=false | 静态闭合 |
| `frost_ring` | false | AAA:frostring; TTL=0; LIT; 全亮; physics=false | 静态闭合 |
| `err_effect` | false | AAA:err; TTL=0; LIT; 全亮; physics=false | 静态闭合 |
| `teleport_flash` | true | AAA:teleportflash; TTL=0; LIT; 全亮; physics=true | 静态闭合 |
| `kilo_attack` | true | AAA:kiloattack; TTL=0; LIT; 全亮; physics=true | 静态闭合 |
| `shock_attack` | true | AAA:shockattack; TTL=0; LIT; 全亮; physics=true | 静态闭合 |
| `poison_cloud` | true | AAA:poisoncloud; TTL=0; LIT; 全亮; physics=true | 静态闭合 |
| `nuke_cloud` | true | AAA:boomshroom; TTL=0; LIT; 全亮; physics=true | 静态闭合 |
| `bloomer_aura` | true | AAA:bloomeraura; TTL=0; LIT; 全亮; physics=true | 静态闭合 |
| `holo_3` | false | AAA:holo3; TTL=0; LIT; 全亮; physics=false | 静态闭合 |
| `holo_4` | false | AAA:holo4; TTL=0; LIT; 全亮; physics=false | 静态闭合 |
| `holo_5` | false | AAA:holo5; TTL=0; LIT; 全亮; physics=false | 静态闭合 |
| `electric_glow` | true | AAA:electricglow; TTL=0; LIT; 全亮; physics=true | 静态闭合 |

## 源调用边界

48个粒子ID共有87处原Java字段消费候选，已保全部路径/行号/源码快照，但没有逐调用验收触发端、次数、条件与坐标；不把87处当成87个缺陷。原调用清单另收录粒子相关字面量候选、AAA声明与消费，不能宣称穷尽外部数据包和动态路径。

`orb_red_med`、`orb_red_smol`、`projectile_impact`、`kilo_attack` 未发现原Java字段消费，仍有原生粒子指令公共入口，故恢复注册/provider而非排除。BloodPetal唯一原字段消费在LargeSpiderLily客户端animateTick调用中要求ServerLevel；通常客户端不满足，不擅自改为新可见玩法。

## 验证与剩余

Java21.0.11离线 `compileJava processResources` 成功；本批4个修改/新增类输出major65。前两次尝试仅因新worktree缺离线Minecraft元数据失败，复用现有缓存并修正复制层级后成功，未联网或修改共享项目。冻结8881份main源码/资源输入，构建后全部哈希不变；未改语言、模板、loot、用户测试、Ribbonblade或.vscode。

未启动游戏、服务端或Render Preview，未增加系统测试套壳。剩余包括逐源调用完整行为配对、真实AAA/精灵视觉验收，以及G02声音和非实体装备/方块实体/屏幕消费者；这些不由52provider闭合代替。

完整逐项参数、源码行锚、输入哈希、原JAR字节码、构建日志和D盘快照路径见同名JSON。原生旧源码提供者为本机Forge1.20.1-47.4.23映射源，不声称原作者使用同补丁。
