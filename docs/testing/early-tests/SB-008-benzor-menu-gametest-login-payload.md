# SB-008：Benzor 菜单 GameTest 玩家登录触发不兼容 payload

- 状态：已关闭，属于测试夹具限制，不是模组生产缺陷。
- 发现：初次新增交易用例使用 `makeMockServerPlayerInLevel()`，在 `PlayerList.placeNewPlayer` 登录事件期间遇到 `Payload jade:server_ping_v1 may not be sent to the client!`，尚未进入 Benzor 菜单逻辑。
- 原因证据：原版 GameTest helper 会调用玩家登录流程，NeoForge 在此触发 Jade 登录监听；GameTest EmbeddedChannel 没有实际客户端能力协商。堆栈停在登录事件，早于 `BenzorMenus.open`。
- 处置与验证：改用原版 `ServerPlayer` 构造器，不注册登录事件；创建`BenzorMenu`并设为当前服务端菜单后，完整43项专服套件运行42项通过、仅SB-002失败。新增交易用例通过空/错配输入拒绝、有效输入消耗与18经验、进度一次性发放及重复点击不重复奖励。
- 最新证据：日志 `D:\AI临时文件\ThermalShockMigration\early-tests\00bbd58\benzor-menu-server-gametest-fixed.log`，SHA256 `081B12ABEDBE6967FFDACBB40B1F60743E530DFC49292F3C80530960628D1C4F`；测试辅助源码 SHA256 `6BCF1D7D8A7B6F3AA45B901F86D92BEFDF88317780FE812B20BFAF483665439E`。
- 覆盖边界：该服务端夹具直接测试`clickMenuButton`及交易业务，不覆盖`BenzorMenus.open`实际网络打开、客户端界面同步与包路由；这些项目仍在未测矩阵中。
