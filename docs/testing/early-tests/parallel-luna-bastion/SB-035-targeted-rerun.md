# SB-035 定向回归记录

固定快照 `00bbd5801c3d373d0d239637de9dd69f2f5974d5`。本轮只定向重跑旧用例 `GT-196-titan-dimension-change`，新namespace为 `ts_server261`，1项通过、0项失败，Gradle退出码0。

本轮发现旧失败前置的主要差异是：单看中心chunk可读不能证明下界实体区块就绪；已有证据还表明GameTest逻辑tick没有50ms墙钟等待。新wrapper先同步加载目标附近5×5 FULL区块，再调用既有测试入口。日志显示预载耗时2457ms、中心`fullStatus=FULL`；在原有force与region ticket生效后，GameTest tick推进到entity-ticking，之后同UUID实体经原生`changeDimension`转入Nether且可从目标Level按UUID查到。旧世界任务运行20个END回调阶段后被移除，目标未受伤。

原方法中的末段由测试直接调用`ServerEndTickTask.endTick`，不是自然AI或世界后台自动调度的证据。它复用了生产Titan task与原case路径，并补上原生传送、目标维度UUID可见性、旧坐标命中取消的断言。

首次有效启动曾因新namespace没有其自己的`supernova_server`模板而崩溃；已将同一冻结模板复制到ts_server261资源路径后重跑通过。这属于夹具注册错误，不计产品失败。启动目录错误的Gradle尝试在进程启动前结束，也不计GameTest执行。

源、模板、主测试冻结副本、模板来源与旧批次日志指纹及本次完整日志SHA见同目录`sb035-targeted-rerun-manifest.json`和D盘`runs/sb035-next-freeze`。日志结尾确认Overworld、Nether、End和全部维度正常保存后关闭。单独future句柄完成状态没有打印；本轮以中心FULL状态、原生chunk debug、entity-ticking就绪和实际迁移成功作为前置证据，因此该夹具问题已按本case实测关闭，不推断其它维度加载边界。

本轮不包含Huntress、Maiden或Gravers包A剩余场景；不改生产src及共享覆盖矩阵。
