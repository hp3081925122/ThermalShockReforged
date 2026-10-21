# 专服测试缺陷索引

本索引覆盖第0至99批固定快照的服务端测试。每个确认缺陷、待验证疑点或测试辅助误报都应有独立文档；本页只做状态索引与最终汇总。日志和测试辅助均留在 `D:\AI临时文件`。

记录规则：遇到确认或疑似问题立即分配独立 SB 编号，不将多个不同原因合并为一份问题记录。每份记录写明固定快照、触发步骤、预期依据、实际结果、源码与日志证据、影响范围、尚未证明的边界、当前分类及后续验证。修正测试预期或证明为误报时，保留原记录并补充关闭依据，不删除失败历史；同一问题的定向回归追加到原文档，不重复编号或增加独立用例计数。最终汇总分别列出本模组缺陷、依赖行为、注册覆盖缺口、环境或测试夹具问题及已关闭项。

| 编号 | 项目 | 状态 | 独立记录 |
|---|---|---|---|
| SB-001 | Electric Sphere 反弹测试的固定绝对速度预期 | 已关闭：测试预期偏差，未确认生产缺陷 | [SB-001-electric-sphere-test-expectation.md](SB-001-electric-sphere-test-expectation.md) |
| SB-002 | Ribbonblade 直线突进后的延迟斩击遗漏静止触发目标 | 已确认旧快照缺陷；修正夹具后13项通过，含真实原入口与精确伤害 | [SB-002-ribbonblade-delayed-strike-target.md](SB-002-ribbonblade-delayed-strike-target.md) |
| SB-003 | Ribbonblade 测试目标初次放在 GameTest 模板边界 | 已关闭：测试夹具错误，非生产缺陷 | [SB-003-ribbonblade-gametest-inwall-fixture.md](SB-003-ribbonblade-gametest-inwall-fixture.md) |
| SB-004 | Ribbonblade 测试在突进瞬间检查延迟斩击范围 | 已关闭：检查时点错误，非生产缺陷 | [SB-004-ribbonblade-strike-check-timing.md](SB-004-ribbonblade-strike-check-timing.md) |
| SB-005 | Benzor 测试导入 AreaEffectCloud 的包路径 | 已关闭：修正导入后 GameTest 编译及运行成功，非生产缺陷 | [SB-005-benzor-test-area-effect-cloud-package.md](SB-005-benzor-test-area-effect-cloud-package.md) |
| SB-006 | Huntress 三连斩统计到其他 GameTest 的任务实体 | 已关闭：按 caster UUID 隔离后整套回归通过该用例，非生产缺陷 | [SB-006-huntress-task-count-cross-test-contamination.md](SB-006-huntress-task-count-cross-test-contamination.md) |
| SB-007 | Huntress 回撤断言统计到其他 GameTest 的任务实体 | 已关闭：按 caster UUID 隔离后整套回归通过该用例，非生产缺陷 | [SB-007-huntress-rollback-task-count-cross-test-contamination.md](SB-007-huntress-rollback-task-count-cross-test-contamination.md) |
| SB-008 | Benzor 菜单 GameTest 在 mock 玩家登录时遇到 Jade payload 异常 | 已关闭：GameTest登录夹具触发Jade payload校验；绕开登录回调后Benzor交易服务端逻辑通过，不是生产缺陷；界面网络路径仍待测 | [SB-008-benzor-menu-gametest-login-payload.md](SB-008-benzor-menu-gametest-login-payload.md) |
| SB-009 | Supernova测试Cow在爆发前掉血 | 已关闭：场地越界导致inWall窒息，修独立模板后原用例通过，非生产缺陷 | [SB-009-supernova-test-target-preblast-damage.md](SB-009-supernova-test-target-preblast-damage.md) |
| SB-010 | Supernova新增测试误设Cow初始生命值为20 | 已关闭：读取真实初始生命值后两项通过，非生产缺陷 | [SB-010-supernova-test-cow-health-expectation.md](SB-010-supernova-test-cow-health-expectation.md) |
| SB-011 | NeoForge取消酿造且库存未改时是否误发Post | 已复现：取消且未改库存仍发Post；NeoForge依赖行为 | [SB-011-neoforge-brew-cancel-post-event.md](SB-011-neoforge-brew-cancel-post-event.md) |
| SB-012 | Loque奶饮用是否遗漏净化 | 已关闭：原件无净化效果，非遗漏 | [SB-012-loque-milk-effect-cure-audit.md](SB-012-loque-milk-effect-cure-audit.md) |
| SB-013 | 凿子交互是否遗漏方块功能 | 已关闭本体疑点；三组已迁转换通过，另三缺口见SB-015至017 | [SB-013-chisel-interaction-function-audit.md](SB-013-chisel-interaction-function-audit.md) |
| SB-014 | Adamantine Spice直接使用是否遗漏效果 | 已关闭：原件直接使用无效果，与喂食分开 | [SB-014-adamantine-spice-use-audit.md](SB-014-adamantine-spice-use-audit.md) |
| SB-015 | 星座块凿子转换链 | 已确认旧99快照注册缺口；真实服务端断言失败 | [SB-015-constellation-block-chisel-chain.md](SB-015-constellation-block-chisel-chain.md) |
| SB-016 | 强化窗凿子状态链 | 已确认旧99快照注册缺口；真实服务端断言失败 | [SB-016-reinforced-window-chisel-chain.md](SB-016-reinforced-window-chisel-chain.md) |
| SB-017 | 三叉灯凿子转换链 | 已确认旧99快照注册缺口；真实服务端断言失败 | [SB-017-trident-lamp-chisel-chain.md](SB-017-trident-lamp-chisel-chain.md) |
| SB-018 | 酿造台原生槽位筛选组合断言失败 | 已关闭：误把石头当非法原料；针对性回归通过 | [SB-018-brewing-stand-slot-filter-expectation.md](SB-018-brewing-stand-slot-filter-expectation.md) |
| SB-019 | 感染去效后续发语义 | 已关闭：原件明确20tick续发与Purified例外 | [SB-019-infection-removal-recurrence-audit.md](SB-019-infection-removal-recurrence-audit.md) |
| SB-020 | 甲虫玩家目标延迟任务重载失效 | 已关闭：迁移文档明确玩家会话隔离；未运行 | [SB-020-beetle-player-task-reload-audit.md](SB-020-beetle-player-task-reload-audit.md) |
| SB-021 | Blasphemer 核心样本加入被取消后的奖励边界 | 已关闭：原件同样单次提交且无重试，运行取消边界已通过 | [SB-021-blasphemer-sample-cancel-boundary.md](SB-021-blasphemer-sample-cancel-boundary.md) |
| SB-022 | Blasphemer 测试命名空间缺少 supernova_server 结构 | 已关闭：复制同哈希结构后8项正常执行，测试夹具问题 | [SB-022-blasphemer-namespace-template-missing.md](SB-022-blasphemer-namespace-template-missing.md) |
| SB-023 | Blasphemer 一次性锁与令牌未写入方块实体 NBT | 已关闭误报：独立 NBT round-trip 已通过，NeoForge 基类保存链保留字段 | [SB-023-blasphemer-persistent-claim-not-saved.md](SB-023-blasphemer-persistent-claim-not-saved.md) |
| SB-024 | Blasphemer 读档测试的绝对 tick 断言偏移 | 已关闭：改为130/140 tick边界后读档用例通过，测试时序误报 | [SB-024-blasphemer-reload-test-timing.md](SB-024-blasphemer-reload-test-timing.md) |
| SB-025 | Blasphemer 测试对可选 ProcessToken 直接读取 UUID | 已关闭：增加hasUUID防护后8项回归通过，测试夹具问题 | [SB-025-blasphemer-test-optional-uuid-read.md](SB-025-blasphemer-test-optional-uuid-read.md) |
| SB-026 | Blasphemer 重复激活测试错误把保存标签当作即时令牌断言 | 已关闭：改为内存令牌幂等断言后8项回归通过，测试夹具问题 | [SB-026-blasphemer-test-repeat-save-assertion.md](SB-026-blasphemer-test-repeat-save-assertion.md) |
| SB-027 | Fabricator局部材料表夹具查找错误 | 已关闭：真实注册表取材后10/10通过，非生产缺陷 | [SB-027-fabricator-fixture-local-material-map.md](SB-027-fabricator-fixture-local-material-map.md) |
| SB-028 | Spirit真实玩家受击未触发预期冷却与Aura | 已关闭：夹具出生保护；真实事件及Aura通过 | [SB-028-spirit-player-attack-fixture-gate.md](SB-028-spirit-player-attack-fixture-gate.md) |
| SB-029 | Lens最近目标与FEET朝向组合断言失败 | 已关闭：归一化比较修正后通过，非生产缺陷 | [SB-029-lens-nearest-gaze-orientation.md](SB-029-lens-nearest-gaze-orientation.md) |
| SB-030 | Spirit原生方向非严格单位向量的测试精度预期 | 已关闭：保原生方向倍率及精度后通过，非生产缺陷 | [SB-030-spirit-view-vector-normalization-fixture.md](SB-030-spirit-view-vector-normalization-fixture.md) |
| SB-031 | Kofoni测试Block/Item类型误用 | 已关闭：测试辅助类型错误，修后14/14通过 | [SB-031-kofoni-fixture-block-item-type.md](SB-031-kofoni-fixture-block-item-type.md) |
| SB-032 | Kofoni石板快捷移动数量断言失败 | 已关闭：合法组件多叠回归通过，非生产缺陷 | [SB-032-kofoni-quickmove-stack-limit.md](SB-032-kofoni-quickmove-stack-limit.md) |
| SB-033 | Kofoni关闭返还数量断言失败 | 已关闭：合法组件多叠回归通过，非生产缺陷 | [SB-033-kofoni-close-refund-stack-limit.md](SB-033-kofoni-close-refund-stack-limit.md) |
| SB-034 | Kofoni满包关闭掉落数量断言失败 | 已关闭：合法组件多叠回归通过，非生产缺陷 | [SB-034-kofoni-full-inventory-drop-stack-limit.md](SB-034-kofoni-full-inventory-drop-stack-limit.md) |
| SB-035 | 三巨人跨维度夹具UUID不可见 | 前置断言失败，待核夹具与原生加载语义 | [SB-035-titan-cross-dimension-fixture-visibility.md](SB-035-titan-cross-dimension-fixture-visibility.md) | 已关闭：ts_server261同用例1/1通过，目标邻区块FULL预载修正夹具，旧失败保留。
| SB-036 | 成熟作物移除失败后仍收获 | 已复现回调防御缺口；正常玩家可达性未证明 | [SB-036-crop-failed-removal-still-harvests.md](SB-036-crop-failed-removal-still-harvests.md) |
| SB-037 | Claw镰刀查找失败 | 已核旧快照Claw物品注册缺口，九分支未进入 | [SB-037-claw-sickle-lookup-failure.md](SB-037-claw-sickle-lookup-failure.md) |

| SB-060 | SB-060 离线副本缺少 Minecraft 元数据缓存 | 详见独立记录 | [SB-060-offline-project-cache-fixture.md](parallel-luna-forecast/SB-060-offline-project-cache-fixture.md) |
| SB-061 | SB-061 XP 统计夹具坐标类型错误 | 详见独立记录 | [SB-061-xp-fixture-pos-type.md](parallel-luna-forecast/SB-061-xp-fixture-pos-type.md) |
| SB-062 | SB-062 外部缓慢状态测试时间窗未重叠 | 详见独立记录 | [SB-062-internal-slow-test-window.md](parallel-luna-forecast/SB-062-internal-slow-test-window.md) |
| SB-063 | SB-063：Forecast回归断言覆盖缺口 | 详见独立记录 | [SB-063-forecast-item-filter-outside-area.md](parallel-luna-forecast/SB-063-forecast-item-filter-outside-area.md) |
| SB-064 | SB-064：Forecast回归断言覆盖缺口 | 详见独立记录 | [SB-064-forecast-pulse-reference-comparison.md](parallel-luna-forecast/SB-064-forecast-pulse-reference-comparison.md) |

| SB-041 | SB-041：Ribbonblade测试夹具缺口 | 详见独立记录 | [SB-041-bypassed-real-attack-entry.md](parallel-luna-bastion/SB-041-bypassed-real-attack-entry.md) |
| SB-042 | SB-042：Ribbonblade测试夹具缺口 | 详见独立记录 | [SB-042-direction-fixture-cross-hits.md](parallel-luna-bastion/SB-042-direction-fixture-cross-hits.md) |
| SB-043 | SB-043：Ribbonblade测试夹具缺口 | 详见独立记录 | [SB-043-task-query-at-world-origin.md](parallel-luna-bastion/SB-043-task-query-at-world-origin.md) |
| SB-044 | SB-044：Ribbonblade测试夹具缺口 | 详见独立记录 | [SB-044-audit-template-too-small.md](parallel-luna-bastion/SB-044-audit-template-too-small.md) |
| SB-045 | SB-045：Ribbonblade测试夹具缺口 | 详见独立记录 | [SB-045-nearby-test-tasks-contaminate-count.md](parallel-luna-bastion/SB-045-nearby-test-tasks-contaminate-count.md) |

| SB-080 | SB-080：GameTest 的实体坐标实参类型错误（已关闭） | 详见独立记录 | [SB-080-test-fixture-coordinate-overload.md](parallel-luna-early-content/SB-080-test-fixture-coordinate-overload.md) |
| SB-081 | SB-081：普通冰锥伤害来源断言误报（已关闭） | 详见独立记录 | [SB-081-icicle-damage-fixture-false-positive.md](parallel-luna-early-content/SB-081-icicle-damage-fixture-false-positive.md) |

| SB-038 | 当前生产星座块循环边界与原件不同 | 静态确认差异，运行待验 | [SB-038-current-constellation-cycle-boundary.md](SB-038-current-constellation-cycle-boundary.md) |

| SB-039 | 冰队测试引用底本不存在的寿命字段 | 已关闭：更正夹具4/4回归通过 | [SB-039-projectile-test-baseline-mismatch.md](SB-039-projectile-test-baseline-mismatch.md) |

| SB-046 | SB-046：Cryonic END tick 寿命未决 | 已关闭：更正夹具定向回归通过 | [SB-046-projectile-end-tick-lifetime-unverified.md](parallel-luna-bastion/SB-046-projectile-end-tick-lifetime-unverified.md) |
| SB-047 | SB-047：Cryonic 剩余寿命 NBT 往返未决 | 已关闭：更正夹具定向回归通过 | [SB-047-projectile-lifetime-nbt-roundtrip-unverified.md](parallel-luna-bastion/SB-047-projectile-lifetime-nbt-roundtrip-unverified.md) |
| SB-048 | SB-048：变体寿命初始化未决 | 已关闭：更正夹具定向回归通过 | [SB-048-variant-lifetime-initialization-unverified.md](parallel-luna-bastion/SB-048-variant-lifetime-initialization-unverified.md) |
| SB-049 | SB-049：Strider寿命NBT裁剪未决 | 已关闭：更正夹具定向回归通过 | [SB-049-projectile-lifetime-clamp-unverified.md](parallel-luna-bastion/SB-049-projectile-lifetime-clamp-unverified.md) |
| SB-082 | SB-082 Sootling自目标拒绝预期属未证实夹具假设 | 夹具问题已关闭，22项最终回归通过 | [SB-082-Sootling自目标拒绝预期属未证实夹具假设.md](parallel-luna-early-content/SB-082-Sootling自目标拒绝预期属未证实夹具假设.md) |
| SB-083 | SB-083 Razorwing夜间生成测试读取了尚未刷新的日光状态 | 夹具问题已关闭，22项最终回归通过 | [SB-083-Razorwing夜间生成测试读取了尚未刷新的日光状态.md](parallel-luna-early-content/SB-083-Razorwing夜间生成测试读取了尚未刷新的日光状态.md) |
| SB-084 | SB-084 实体局部坐标误用于绝对世界AABB查询 | 夹具问题已关闭，22项最终回归通过 | [SB-084-实体局部坐标误用于绝对世界AABB查询.md](parallel-luna-early-content/SB-084-实体局部坐标误用于绝对世界AABB查询.md) |
| SB-085 | SB-085 armored Razorwing乘骑场景使用了不适配的FakePlayer夹具 | 夹具问题已关闭，22项最终回归通过 | [SB-085-armored-Razorwing乘骑场景使用了不适配的FakePlayer夹具.md](parallel-luna-early-content/SB-085-armored-Razorwing乘骑场景使用了不适配的FakePlayer夹具.md) |
| SB-086 | SB-086 测试源码存在FakePlayer包名和玩家清理参数类型编译错误 | 夹具问题已关闭，22项最终回归通过 | [SB-086-测试源码存在FakePlayer包名和玩家清理参数类型编译错误.md](parallel-luna-early-content/SB-086-测试源码存在FakePlayer包名和玩家清理参数类型编译错误.md) |

| SB-120 | Huntress已提交尾段取消预期 | 已关闭：测试预期与原件/冻结迁移契约相反；正确尾段完整断言待测 | [SB-120-huntress-slam-after-caster-removal.md](parallel-luna-bastion/SB-120-huntress-slam-after-caster-removal.md) |

| SB-121 | Maiden已提交尾弹取消预期 | 已关闭：测试预期与原件/冻结迁移契约相反；正确尾段完整断言待测 | [SB-121-maiden-volley-after-caster-removal.md](parallel-luna-bastion/SB-121-maiden-volley-after-caster-removal.md) |

| SB-087 | 创意能力/随机种子夹具 | 已关闭：测试夹具误报，产品未改；本组19项证据已核 | [SB-087-creative-instabuild-fixture.md](parallel-luna-early-content/SB-087-creative-instabuild-fixture.md) |

| SB-088 | 声音ID与并行监听夹具 | 已关闭：测试夹具误报，产品未改；本组19项证据已核 | [SB-088-server-sound-event-listener-fixture.md](parallel-luna-early-content/SB-088-server-sound-event-listener-fixture.md) |

| SB-089 | 累计elapsed与失效引用夹具 | 已关闭：测试夹具误报，产品未改；本组19项证据已核 | [SB-089-dissolver-aggregate-elapsed-sampling-fixture.md](parallel-luna-early-content/SB-089-dissolver-aggregate-elapsed-sampling-fixture.md) |

## 汇总（持续更新，全部服务端测试尚未完成）

累计525项独立GameTest：518通过、7项历史或本轮失败；最新运行2项选定场景，2通过、0失败。重复回归单列不增加累计，第0至99批全部服务端功能仍未测完。 源码疑点与已关闭误报见逐项索引。

问题文档包含主目录历史记录与各路独立记录；确认缺陷、疑似问题和已关闭误报逐项见索引。仅判定固定快照，不推断最新生产版本。

## 历史运行汇总（保留，不代表当前结果）


固定第99批快照累计211项独立GameTest：205通过、6失败（SB-002、SB-011、SB-015、SB-016、SB-017、SB-035待核夹具）；最新只运行16项选定场景，15通过、1失败。0至99批所有服务端功能仍未测完。 各确认缺陷、未决疑点及已关闭误报的状态以上方逐项链接为准。

固定第99批快照累计195项独立GameTest：190通过、5失败（SB-002、SB-011、SB-015、SB-016、SB-017）；最新只运行14项选定场景，14通过、0失败。0至99批所有服务端功能仍未测完。 各确认缺陷、未决疑点及已关闭误报的状态以上方逐项链接为准。

固定第99批快照累计195项独立GameTest：187通过、8失败（SB-002、SB-011、SB-015、SB-016、SB-017、SB-032夹具待回归、SB-033夹具待回归、SB-034夹具待回归）；最新只运行14项选定场景，11通过、3失败。0至99批所有服务端功能仍未测完。 各确认缺陷、未决疑点及已关闭误报的状态以上方逐项链接为准。

固定第99批快照累计181项独立GameTest：176通过、5失败（SB-002、SB-011、SB-015、SB-016、SB-017）；最新只运行2项选定场景，2通过、0失败。0至99批所有服务端功能仍未测完。 各确认缺陷、未决疑点及已关闭误报的状态以上方逐项链接为准。

固定第99批快照累计181项独立GameTest：174通过、7失败（SB-002、SB-011、SB-015、SB-016、SB-017、SB-030待核夹具、SB-029待归一化回归）；最新只运行2项选定场景，0通过、2失败。0至99批所有服务端功能仍未测完。 各确认缺陷、未决疑点及已关闭误报的状态以上方逐项链接为准。

固定第99批快照累计181项独立GameTest：174通过、7失败（SB-002、SB-011、SB-015、SB-016、SB-017、SB-028待核夹具、SB-029待诊断）；最新只运行17项选定场景，15通过、2失败。0至99批所有服务端功能仍未测完。 各确认缺陷、未决疑点及已关闭误报的状态以上方逐项链接为准。

固定第99批快照累计164项独立GameTest：159通过、5失败（SB-002、SB-011、SB-015、SB-016、SB-017）；最新只运行10项选定场景，10通过、0失败。0至99批所有服务端功能仍未测完。 各确认缺陷、未决疑点及已关闭误报的状态以上方逐项链接为准。

SB-027：Fabricator测试局部材料表查找错误已关闭，真实注册表修正后10/10通过。详见 [SB-027-fabricator-fixture-local-material-map.md](SB-027-fabricator-fixture-local-material-map.md)。

当前共34份独立问题记录：SB-028至030均已由定向回归关闭；5项旧快照未关闭运行失败为SB-002、011、015、016、017；SB-021已证实与原件取消语义一致，SB-022至026已按夹具修正或独立回归关闭。旧失败结果与误判过程保留，不推断最新生产版本仍存在相同缺陷。

固定第99批快照累计154项独立GameTest：149通过、5失败（SB-002、SB-011、SB-015、SB-016、SB-017）；最新只运行8项选定场景，8通过、0失败。0至99批所有服务端功能仍未测完。 各确认缺陷、未决疑点及已关闭误报的状态以上方逐项链接为准。

固定第99批快照累计146项独立GameTest：141通过、5失败（SB-002、SB-011、SB-015、SB-016、SB-017）；最新只运行18项选定场景，18通过、0失败。0至99批所有服务端功能仍未测完。 各确认缺陷、未决疑点及已关闭误报的状态以上方逐项链接为准。

固定第99批快照累计128项独立GameTest：123通过、5失败（SB-002、SB-011、SB-015、SB-016、SB-017）；最新只运行12项选定场景，12通过、0失败。0至99批所有服务端功能仍未测完。 各确认缺陷、未决疑点及已关闭误报的状态以上方逐项链接为准。

当前未关闭项分为：SB-002本模组行为缺陷；SB-011 NeoForge依赖事件缺陷；SB-015至017旧99快照迁移覆盖缺口。SB-001、003至010、018为已关闭的测试问题，SB-012/014为原件不支持相应预期，SB-013工具本体疑点排除。历史截至128项时总计20份独立记录；SB-020已核为会话隔离设计并关闭；SB-019已核为原玩法并关闭；已关闭文档不删除。

固定第99批快照累计116项独立GameTest：111通过、5失败（SB-002、SB-011、SB-015、SB-016、SB-017）；最新只运行13项选定场景，10通过、3失败。0至99批所有服务端功能仍未测完。 各确认缺陷、未决疑点及已关闭误报的状态以上方逐项链接为准。

固定第99批快照累计104项独立GameTest：101通过、3失败（SB-002、SB-011、SB-018）；最新只运行12项新增场景，10通过、2失败。0至99批所有服务端功能仍未测完。 各确认缺陷、未决疑点及已关闭误报的状态以上方逐项链接为准。

固定第99批快照累计92项独立GameTest：91通过、1失败（SB-002）；最新只运行12项新增场景，全部通过。0至99批所有服务端功能仍未测完。 截至该次运行无新增异常；后续酿造源码疑点SB-011另行待测；SB-001和SB-003至SB-010为已关闭测试问题，SB-002待生产修复后再测。

固定第99批快照累计80项独立GameTest：79通过、1失败（SB-002）；最新只运行12项新增场景，全部通过。0至99批所有服务端功能仍未测完。 截至该次运行无新增异常；后续酿造源码疑点SB-011另行待测；SB-001和SB-003至SB-010为已关闭测试问题，SB-002待生产修复后再测。

固定第99批快照累计68项独立GameTest：67通过、1失败（SB-002）；最新只运行8项新增场景，全部通过。0至99批所有服务端功能仍未测完。 截至该次运行无新增异常；后续酿造源码疑点SB-011另行待测；SB-001和SB-003至SB-010为已关闭测试问题，SB-002待生产修复后再测。

历史：第99批60项GameTest中59项通过、1项Ribbonblade延迟斩击失败（SB-002）；新增Supernova四项与早期内容八项全部通过。全部服务端功能仍未测完。 SB-001与SB-003至SB-010均已关闭为测试夹具或预期问题；只有SB-002为已确认待生产修复的行为缺陷。完整新增断言和未测范围见server60-increment.md。下段48项记录保留历史。

历史：固定第99批快照运行共48项 required GameTest：47项通过，1项失败，为已确认生产行为问题SB-002。SB-008已由不触发玩家登录事件的真实ServerPlayer菜单夹具关闭，Benzor交易按钮逻辑、消耗、经验和进度通过；该测试未验证菜单网络打开与同步。SootleIdle单次受击转换、Bunker门红石/屏障边界、Sootling目标失效/冷却门控、第91批Glastrekker装甲升级和第94批Dissolver Gemlock三轮定界清除新用例通过。SB-001、SB-003至SB-008均为测试夹具或测试辅助问题，已有修正或关闭证据，不作为生产缺陷计数。第0至99批所有服务端功能尚未覆盖；汇总范围与未测清单以 `server-coverage-matrix.json`、`server-test-plan.md` 和 `batch99-runtime.md` 为准。客户端、渲染、Render Preview、Xaero均未测试。

