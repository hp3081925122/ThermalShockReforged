# 第12批Icidrostrider服务端测试计划

固定99快照00bbd5801c3d373d0d239637de9dd69f2f5974d5。namespace ts_server225，共15个独立用例。先读strider-chain.md、四个生产类与目标原生骑乘/投射物/死亡规则；测试只写D盘隔离工程，Luna执行，生产不改。

| 场景 | 实际断言 |
|---|---|
| 构造器 | 真实onPlace消费原块、世界实际接纳一只Strider |
| 同块回调 | 重复同块onPlace不再次生成 |
| 构造器加入被取消 | 按原文档保一次消费、不无限重试 |
| 实际右键骑乘 | 原生startRiding、实际controller、下骑解除、死坐骑拒绝 |
| 骑乘输入 | 双轴完整、Y=0、yaw/body/head及half pitch、实际属性速度 |
| 越权开火 | 非乘客与真实第二非controller乘客不发射不烧CD |
| 真实发射 | 骑手owner、8基础攻击×3.5、原生Mixin pierce5、crit/silent/no pickup、脚部原位/水平速度1、KB0、caster/owner不可命中 |
| 冷却 | 同tick和真实第9tick拒绝、第10tick允许 |
| 冷却保存 | 实际保存剩10、重新载入/重骑不清CD、余期后允许 |
| 投射物寿命 | 直接推进实际tick，前4存活、第5移除；age4重载仅余1tick，强制不可拾取 |
| 原生穿透链 | 直接调用生产onHitEntity及原生父类，前6真实hurt各一次、清箭计数、第7不伤且移除 |
| 原免疫 | 直接箭/药水/云以及fall/cactus/drown/dragonBreath拒，generic有效 |
| 近战与常驻 | 普通近战false且不伤；distance despawn关闭，NBT伪PersistenceRequired=false仍恢复true，Texture保留 |
| 死亡回收 | 真致死伤害，在doMobLoot=true/false分别一/零构造器，死亡第1tick移除、零XP |
| 发射加入被取消 | 保本次尝试CD10，不允许同tick重试风暴 |

玩家为真实ServerPlayer并借FakePlayer无操作连接，只验证实际服务端骑乘/业务。未发登录事件，未覆盖实网协议。不能用FakePlayer自身startRiding，因为其覆写固定false；测试不替换核心骑乘/发射方法。

本组穿透项是直接命中路径验收，明确不冒充真实飞行射线穿过六目标。尚未测：完整飞行碰撞、多种骑手非玩家输入、实网按键payload与同步、客户端透明/发光/动画/声音/粒子、Render Preview/Xaero、进程级存档重启。结果随后落档，不按计划即视为通过。

本组已由quick_explorer执行15项并全部通过，Gradle退出0，全部维度正常保存关闭。证据与精确哈希见server239-strider.md。仅选定本组场景通过，不扩大未测边界；已通过项后续仅在相关输入改变或新增关切时回归。
