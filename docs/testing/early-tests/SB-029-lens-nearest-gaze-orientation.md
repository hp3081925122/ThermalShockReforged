# SB-029：Lens最近目标与FEET朝向组合断言失败

状态：已关闭，方向归一化比较夹具错误，非生产缺陷。

固定快照00bbd5801c3d373d0d239637de9dd69f2f5974d5，MC1.21.1 / NeoForge21.1.233，namespace ts_server165首轮17项15通过2失败、退出2，服务端正常保存关闭。

最近Lens/FEET锚点/视角点积组合断言失败，未执行后续保速度断言。尚无具体子断言的实际值，先增加观测值并保持原断言，不据组合失败推定选择目标或视角实现错误。

原玩法依据：docs/migration/gaze-undead-batch94.md中Spirit玩家伤害取消/40tickCD/真Aura20 amp3，以及Lens朝最近透镜FEET至FEET的服务端逻辑。目标源为entity/gaze/SpiritEntity.java、LensEntity.java、GazeContent.java。当前不修改生产源码。

日志：D盘00bbd58/gaze-server-gametest.log，SHA256 E4014889C1FF8DAA15DAF521A5F63C3559B67B8EE4C9721AF21736FEB9F45C06。测试源初轮SHA256 02D35F51F691F2528265AB20C27212F4E7478F48296D5CFE242556FD1AF21C89，初轮不可变副本GazeServerBehaviorTests-first-run.java保留。后续诊断与回归另补，不删除初轮日志。

诊断：looks=1、target==nearest、FEET/FEET均正确；实际方向(-0.7071067691,0.7070389986,0)，理想单位方向(-0.7071067812,0.7071067812,0)，直接点积0.999952061918972。原生Mth三角查表使方向并非严格单位向量；比较夹具误把未归一化点积当cos。仅把实际方向归一化后比较同一角度阈值，目标选择/锚点/保速度/真实恶心断言不删，待回归。诊断日志SHA256 74136AE7ED7D40D95BD3BFCD150103E72D99E72F56A764481857881C99799A2A。

关闭证据：ts_server165_diag最终两项2/2通过、退出0、三维度保存关闭。日志gaze-server-diagnostic-rerun.log SHA256 C61021E244F011F7E1CC64075F9F11319E51C6A976ADAB79B9C019996403B910；Gaze最终源码SHA256 B2E995E0755F395B2CA69448A3E1F2EBD5B01B68323E640B83AC463BBAE4FBCD。只修测试夹具，生产实现未改。详见server181-gaze-final.md。
