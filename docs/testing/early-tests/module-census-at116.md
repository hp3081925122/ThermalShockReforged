# 累计116项时点的服务端模块触达盘点

固定第99批快照 `00bbd5801c3d373d0d239637de9dd69f2f5974d5`。只读盘点者quick_explorer `/root/next_test_candidate_2`，输入为D盘GameTest与累计覆盖矩阵。该时点116项独立用例，111通过、5失败。后续结果以最新矩阵及增量报告为准。

这里的“触达”仅指至少存在一个定向行为断言。注册、创建、保存、短tick和资源解码烟测不算完整功能测试；三个缺失方块的失败注册断言不算行为通过。目录也不是功能数量，不据此计算完成百分比。

| 分组 | 至少有部分定向行为场景 | 尚未发现定向行为场景 |
|---|---|---|
| content | brewing、decor、doors、farming、fluids、food、growth、lights、machines、npc、status、terrain、tools、utility、weapons、atlas的画作、labworldgen的Gemlock | armor、camera、crates、creative、dependencies、dialogue、fabrication、glass、music、plants、reactor、vilebathblocks |
| entity | ambient、benzor、electricsphere、fauna、glastrekker、huntress、maiden、pots、projectile、ribbonblade、snobol、sootle、teslareed、weaponcore | aerocel、attack、base、beetles、boss、cavebloomer、drops、ecosystem、gardenling、gaze、gravers、growth、hunters、ice、icesquad、infestation、jellercel、kofoni、labmachinist、legacy、liftbug、magnetite、mangler、monolith、orb、protoi、qo、sanctum、spore、springslime、squall、summon、titans、whisper |
| world | 已有任务通过其他业务间接触达，尚非调度模块完整定向测试 | ore、sanctum、thermite、timing |

机器目前只覆盖转化器/提取器，NPC主要是Benzor；Gemlock归属labworldgen，不能因测试类在另一包就遗漏。这个分类也不包含所有根包实体及combat/config/mixin职责，因此不能当整个服务端的最终清单。

优先候选：甲虫攻击任务的冷却/失效/伤害递归、Blasphemer样本事务与延迟结算、选定具体召唤者的账本/上限/事件重入。已有Sootling和Supernova的局部任务通过不代表其他召唤链通过。

部分场景仍需补真实玩家网络、完整机器库存与自动化、自然生成和结构实际放置、维度与剧情、存档/区块卸载/重启边界，以及性能相关计数。客户端与Render Preview不在本轮范围。
