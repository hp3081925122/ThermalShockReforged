# Errant、Ripple 与 FrostilyteGunner 服务端用例

固定基线：`00bbd5801c3d373d0d239637de9dd69f2f5974d5`。本批独立记录 20 个唯一服务端用例，主批运行20项（19项通过、1项测试断言时点错误）；之后只针对招架断言和Ripple第29个END边界分别运行1项，最终20个唯一用例均通过。包含focused回归共执行22次用例。没有记录产品Bug或创建SB编号。

D盘项目中与本功能有关的14个生产源码文件均逐项与固定提交比较，SHA一致。本工程另保留SB036的 `BarbubbleBlock.java`、`GeoCropBlock.java`、`SkorioBlock.java` 三项守卫补丁；本批不覆盖这些作物路径。生产 `src` 未修改，共享矩阵未修改。

首轮招架用例的断言错误：测试曾要求瞬时治疗在同一 `hurt` 调用中立刻改写血量。源码及日志表明这里先添加一 tick `HEAL`，随后真实伤害阶段消费 `PARRYING`；专用复核检查受击后治疗效果存在、血量按无护甲十点伤害降到40且招架状态已消费，1/1通过。原失败日志保留，未计为生产失败。

命令使用 Java 21、Gradle堆1G、单worker、offline/no-daemon。GameTest服务器由工程的 `runGameTestServer` 配置使用 `-Xmx2G`。没有启动客户端。每个namespace只运行本批被选用例；服务器日志均记录 GameTest完成、世界/chunk保存和专服关闭。日志路径、exit code、源码/模板/字节码SHA及逐case入口/断言/未覆盖都在同目录manifest中。

| 用例ID | 功能批次 | 模块 | 结果 | 实际断言 |
|---|---:|---|---|---|
| `GT-LUNA-earlycontent-sanctum-47-01` | 47 | Errant | 通过 | 确定性格挡seed：PARRYING持续20 tick、amplifier 1；冷却45、行动锁30、无待斩阶段。 |
| `GT-LUNA-earlycontent-sanctum-47-02` | 47 | Errant | 通过；首跑夹具断言误差，已定向通过 | 确定性格挡后直接受击；检查一 tick HEAL效果已排入、普通伤害得到处理，真实before-armor入口消费PARRYING。 |
| `GT-LUNA-earlycontent-sanctum-47-03` | 47 | Errant | 通过 | 确定性闪避seed：向后look向量×2、hasImpulse、冷却10、无行动锁及PARRYING。 |
| `GT-LUNA-earlycontent-sanctum-47-04` | 47 | Errant | 通过 | 确定性反击分支提交12 END tick；前11 tick目标无伤，第12 tick按攻击力×配置倍数伤害且任务清零。 |
| `GT-LUNA-earlycontent-sanctum-47-05` | 47 | Errant | 通过 | 斩击已提交后目标移至攻击距离外；到任务结束无追加伤害，待斩计数清除。 |
| `GT-LUNA-earlycontent-sanctum-47-06` | 47 | Errant | 通过 | 斩击选定目标被移除，附近另一目标保持原血量；无UUID重定向。 |
| `GT-LUNA-earlycontent-sanctum-47-07` | 47 | Errant | 通过 | 攻击者在待斩阶段移除；目标保持原血量。 |
| `GT-LUNA-earlycontent-sanctum-47-08` | 47 | Errant | 通过 | 玩家伤害进入真实死亡流程；19th death tick前无经验，20th death tick移除且生成合计30经验。 |
| `GT-LUNA-earlycontent-sanctum-47-09` | 47 | Errant | 通过 | strike阶段执行4次END并进行实体NBT往返；剩余8 tick复载，提前不命中，之后只命中一次。 |
| `GT-LUNA-earlycontent-sanctum-48-01` | 48 | Ripple | 通过 | Ripple开始施法建立一个归属cast；冷却180、锁40；冷却中重复调用不复制或重置cast。 |
| `GT-LUNA-earlycontent-sanctum-48-02` | 48 | Ripple | 通过；精确29 END边界定向通过 | 第28个END阶段前任务remaining=1且无ERR；第29个END阶段消费任务并创建一个ERR场。 |
| `GT-LUNA-earlycontent-sanctum-48-03` | 48 | Ripple | 通过 | 施法后给Ripple ELECTRIFIED；任务到期时不生成ERR，任务清除。 |
| `GT-LUNA-earlycontent-sanctum-48-04` | 48 | Ripple | 通过 | 施法后添加WEAKNESS；到期不生成ERR，任务清除。 |
| `GT-LUNA-earlycontent-sanctum-48-05` | 48 | Ripple | 通过 | 施法后添加INFECTED；到期不生成ERR，任务清除。 |
| `GT-LUNA-earlycontent-sanctum-48-06` | 48 | Ripple | 通过 | 施法者移除后，已提交cast结束且不生成ERR场。 |
| `GT-LUNA-earlycontent-sanctum-48-07` | 48 | Ripple | 通过 | 目标移除后cast仍使用已保存瞄准坐标完成并生成一个ERR场。 |
| `GT-LUNA-earlycontent-sanctum-49-01` | 49 | FrostilyteGunner | 通过 | 事件钩子捕获真实IceBullet：第10 tick为0、第11 tick为1、第16 tick为2、第21 tick为3；射弹含caster UUID及5点快照伤害，任务结束。 |
| `GT-LUNA-earlycontent-sanctum-49-02` | 49 | FrostilyteGunner | 通过 | 第二发后施法者受致死伤害进入死亡状态；已提交任务仍在第21 tick发出第三发。 |
| `GT-LUNA-earlycontent-sanctum-49-03` | 49 | FrostilyteGunner | 通过 | 第一发后移除目标，任务继续按最后快照方向发出第2/3发。 |
| `GT-LUNA-earlycontent-sanctum-49-04` | 49 | FrostilyteGunner | 通过 | 同一冷却中再次攻击不重置60冷却/29行动锁、不重复创建volley。 |

用例通过范围是本表明确列出的服务端入口与状态。Errant、Ripple、Gunner的技能入口由GameTest直接调用 `beginAttack`，不等于自然AI寻敌和真实玩家联机已覆盖。Ripple另用独立namespace核验第28 tick剩余1、第29 END tick创建ERR。视觉、实际命中碰撞、跨进程重启、自然生成、完整掉落/进度链和第三方兼容仍未测；详见manifest。

执行证据：[`manifest-sanctum-batch01.json`](./manifest-sanctum-batch01.json)。
