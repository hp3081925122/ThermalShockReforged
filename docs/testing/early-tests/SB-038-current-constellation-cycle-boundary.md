# SB-038：当前生产星座块凿子循环包含原入口不可达的第6状态

状态：当前源码静态确认差异，尚无本轮运行验证；不替换SB015旧快照缺注册失败。

原件 ConstellationBlockOnBlockRightClickedProcedure 明确在 blockstate<5 时加1，>=5 时回0，故普通凿子入口循环0→1→2→3→4→5→0；若管理员设为6，下次也应回0。当前 ConstellationBlock 定义 DESIGN=0..6，服务端主手凿子直接 state.cycle(DESIGN)，因此5会变6，6才回0。属性上限保留6不等于交互要循环到6。

来源原反编译源码SHA256：BA993AA8C4A754E38E50387E53F470C06A962B1D13F5853DAA5DFA58C65C86DD。
当前源码与注册输入SHA见current-source-registration-followup.json。未提交生产源仍有其他聊天写入，本测试聊天没有改生产src或构建生产树；此差异属于固定99快照后补充的实现线索，运行结果不混入本轮373项。

建议修复：保留属性0..6与全部资源，仅把凿子服务端写入改为 DESIGN>=5 ? 0 : DESIGN+1，保留其余状态与交互条件。需在负责该生产模块的固定新快照中测全部7个初始状态、各朝向、非凿子及副手门槛；不能靠删状态6或删资源处理。

已生成最小修复提案（未应用、未编译或运行）：D:\AI临时文件\ThermalShockMigration\early-tests\resume-20261020\SB038\constellation-cycle-boundary.patch。保留原属性全域0..6，只改写入下一状态；提案与当前源码SHA及7状态真值表见同目录proposal.json。
