# SB-024：Blasphemer 读档测试的绝对 tick 断言偏移

状态：已关闭为测试时序误报。修正绝对tick断言后读档用例通过。

固定第99批快照首次 Blasphemer 重跑中，`savedProcessKeepsRemainingElapsedAndDoesNotRestart` 在测试开始后第135 tick断言“样本尚未生成”，但该用例在第12 tick先保存并替换任务；从测试起点计时的第135 tick已经超过原定第134 END阶段，因此实际样本可能合法存在。失败日志 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\blasphemer-server-gametest-rerun.log`，SHA256 `4E515EDBA30FBAB14B68AAF8A4AD8BABA020EAAF66CBB277CF0E97B993368B15`。

修正：保留保存时的 `Elapsed` 断言，把未产出检查移到测试起点第130 tick，把产出检查移到第140 tick；不改变生产时序。下一轮同命名空间回归后，若修正断言通过，本记录关闭为测试时间边界误报；若仍在原定阶段前产生样本，再建立独立生产问题。

关闭证据：第四轮日志 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\blasphemer-server-gametest-rerun4.log` 中读档用例通过，证明原失败是测试起点绝对tick断言偏移。
