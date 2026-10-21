# 第94批 Gaze 三生物专服功能测试计划

固定99快照00bbd5801c3d373d0d239637de9dd69f2f5974d5；生产源码不改。17项已全部完成：首轮15通过、两项夹具修正后定向回归通过；最终17独立项全部通过，证据见server181-gaze-final.md。

Spirit：真实玩家攻击取消与冷却重复、40 baseTick边界及NBT钳制、7格攻击范围、四类免疫及普通伤害、真实Aura默认3伤/基础攻击倍率/排除自身及物品/无归属防反入、死施法者与跨维度受害者门控。

Lanternbearer：被取消死亡不提交、重复死亡和尸体重载不双提交、真实SERVER END24爆炸与25tick移除、任务保存后只走余轮且真实爆炸仅一次、攻击快照比例伤害及完全击退抗性、外部取消任务加入无已加载任务泄漏。

Lens：强制无重力与溺水/摔落免疫、无玩家关闭、FEET至FEET朝最近已加载透镜/保位置及速度/120tick amp1恶心、重叠同tick玩家去重且保原激活区旁观者受影响、严格8格激活与旁观者独自不激活。

夹具使用FakePlayer派生以提供原生假网络连接，覆盖isInvulnerableTo以允许真实伤害入口，记录lookAt后委派原生ServerPlayer逻辑。需要在线列表时仅ServerLevel.addNewPlayer、finally removePlayerImmediately，不调用PlayerList.placeNewPlayer或登录链，不断言真实客户端收到包。Cow真实承受伤害，仅记录DamageSource；1000血防止倍率用例过早死亡。模板20×8×16，爆炸锚点居中抬高；监听按实体/锚点过滤并finally卸载。

本组未测：实际登录网络/渲染/声音听感/死亡XP实际掉落、自然生成天气条件/客户端粒子数、长时间负载和真实进程重启磁盘IO。未覆盖项保留，不等于第94批全服务端通过。
