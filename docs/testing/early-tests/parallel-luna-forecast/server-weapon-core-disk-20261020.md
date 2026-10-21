# 服务端武器核心、光棱盘与 Carbor GL 测试：2026-10-20

本组覆盖 25 个与共享矩阵未测项去重后的服务端用例：Core Eject 与 FlareGunCore（批次 87）11 项、Liteprism Disk 物品/叠层/核心同步（批次 88）7 项、Disk Resonance（批次 89）1 项、Carbor GL（批次 91）6 项。依据固定源码底本 `00bbd5801c3d373d0d239637de9dd69f2f5974d5` 编写断言，未修改生产 `src` 或共享矩阵。

25 项均已通过：完整服务端运行注册并执行 25 项，23 项首跑通过；两项测试夹具失败经定向复测通过。循环阶段测试首跑实际观测到主键 1–18、副键 1–20 的预期阶段与tick偏移，失败来自断言还要求监听列表恰好只有两项；同一 GameTest 世界中其他测试也会加入循环实体。复测按两位发射者位置过滤后通过。120 tick 生命周期用例首跑超过默认100 tick测试超时，增加该用例自己的140 tick超时后通过。这两项是夹具校准，不构成产品缺陷证据。

手动调用 `onHitEntity` / `onHitBlock` 的项目均在 manifest 标明合成命中，不代表真实弹体飞行碰撞或客户端网络流程；手动调用 `use` / `onUseTick` 也如实列明。逐tick监听使用 HIGHEST `ServerTickEvent.Post`，用于在默认 END 阶段调度前记时。首跑夹具问题及校准依据见 [夹具校准记录](fixture-calibration-core-disk-20261020.md)。详细逐 case 入口、断言、限制、矩阵批次、冻结源/模板/底本依赖 SHA、每次运行日志哈希、退出码与 GameTest 数见 [逐例 manifest](server-weapon-core-disk-20261020.manifest.json)。

最终定向运行注册并通过 2/2 项，Gradle 与 GameTestServer 退出码均为 0；服务端正常关闭并保存 Overworld、Nether、End。完整首跑注册25项，23通过、2夹具失败；服务端仍正常保存三个维度并关闭。原始运行日志保存在 `D:\AI临时文件\ThermalShockMigration\early-tests\parallel-20261020\forecast\runs`。

按前序累计 446/438/8 仅作算术汇总，本组后为 471/463/8；没有改写共享矩阵。该数值不表示整个模组或全部武器功能已完成服务端验证。


主协调范围审查：正确完整底本为00bbd5801c3d373d0d239637de9dd69f2f5974d5，worker SHA文本末尾漏5；实际17产品输入对只读底本哈希一致。Core03未断言核心owner；Core10为同步ThreadLocal去重队列；Disk03未核基础伤害，Disk06仅21tick观察消失，Disk07未捕获初始burst，Disk08为2/4内外点；GL04未核cloro_weak6点或真实power3，GL06未读取power2。逐项未测已收窄写入主矩阵。主矩阵最新累计491/484/7，原471/463/8为过时算术参考。
