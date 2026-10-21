# 第68项时点的模块覆盖盘点

固定第99批快照 `00bbd5801c3d373d0d239637de9dd69f2f5974d5`。由quick_explorer只读盘点，未将注册/存读烟测当行为覆盖。此页保留第68项时点，后续新增测试以各增量报告和累计JSON为准，不反推本统计为最新。

`src/main/java/com/hp/thermalshockreforged` 下content二级目录29个、entity二级目录48个、world二级目录4个，共81个目录。它们不是81种功能，也不与100个迁移批次等价。测试当时19个类、68个GameTest方法，其中7项全注册烟测；另外61项也只覆盖具体断言。

宽口径“至少被一个行为用例触达”的目录约22个，其中content8、entity13、world/timing间接触达1。其余59目录没有专用行为用例。即使触达也不意味着模块完成；根包实体和其他顶层combat/config/mixin不在这81目录统计内。

| 分组 | 当时没有专用行为测试的目录 |
|---|---|
| content：21/29 | armor、atlas、brewing、camera、crates、creative、dependencies、dialogue、fabrication、farming、fluids、food、glass、growth、lights、machines、music、plants、reactor、tools、vilebathblocks |
| entity：35/48 | aerocel、attack、base、beetles、boss、cavebloomer、drops、ecosystem、fauna、gardenling、gaze、gravers、growth、hunters、ice、icesquad、infestation、jellercel、kofoni、labmachinist、legacy、liftbug、magnetite、mangler、monolith、orb、protoi、qo、sanctum、spore、springslime、squall、summon、titans、whisper |
| world：3/4 | ore、sanctum、thermite；timing仅被延迟任务间接触达，仍缺完整专用调度套件 |

现有部分触达范围：content/decor、doors、labworldgen、npc（仅Benzor）、status、terrain（主要Sponge）、utility（主要Pesticide）、weapons（主要Supernova）；entity/ambient（仅Sootling）、benzor、electricsphere、glastrekker、huntress、maiden、pots、projectile（少数弹体）、ribbonblade、snobol（仅Antitoxin物品）、sootle、teslareed、weaponcore。根包Probosker/Forecast/Zap/Koi另有用例，不算二级目录数量。

证据入口：D盘固定快照 `src/gametest/java/com/hp/thermalshockreforged`；`ServerCoverageSmokeTests` 的7项仅核默认状态、创建/存读/短tick和资源解码，不能替代每个技能、自然生成、掉落、任务、菜单网络和持久边界。

本盘点用于防遗漏和选择下一组场景，不用于计算“全模组测试完成率”。第0批正式编号边界尚不明确，但其初期内容仍纳入累计清单，不会因为编号缺失而排除。
