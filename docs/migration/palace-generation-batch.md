# 第44批：完整Lavender Palace与首次生成

迁移前契约见aqeron-palace-next-audit.md。原600608块/135状态/46BE/13实体完整保留，NBT解析改134个资源字符串命名空间，反向改回后解压payload逐字节等于原件，保DataVersion3465交原生DFU。讲台writable_book四页、15牌前后文本、7装饰壶四heart sherd、8画作完整保留；本模组三个BE类型的Items实际均空。资源字符串以外书页、作者署名和正文不改。

Luna已核44个唯一自有方块/全部palette属性值/三个自有BE工厂/居民类型与8painting variant真实注册，无缺项。Ly与四肖像模板无NoGravity，保正常重力及各自旧NBT Texture，不把finalizeSpawn生成蛋状态套上模板居民。

PalaceGeneration保base AdvancementEvent（含其子事件）、palace_get匹配、原gamerule、当前玩家ServerLevel、原(-74,178,-132)origin/pivot、NONE/NONE/ignoreEntities false/flags3。WorldProgressData主世界共享PalaceExists持久状态、临时palacePlacing重入锁；真实模板get为空或失败不消费Exists，finally解锁。补原隐藏palace_get changed_dimension→aqeron进度，10XP，不新增display/parent。维度本体继续下一批，不能把模板导入当完整维度已完成。

保留全部空气及一次原生模板放置，已向用户说明首次60万记录同步放置的卡顿成本，不以跳块/省实体/异步改世界降低保真度。固定快照由Luna机械验证与最小三任务编译；整模组完整后再服务端/客户端/Render Preview验证首次进入、牌/书/容器/居民/画/失败重试与共享一次标记。


固定快照验收：Luna完成上述模板/库存/注册/事件机械核对，实际源码与build/resources模板同SHA256 fd6316fc6a245424078f71b028c2f53b23a32b00c1ecc4210ec896bd65f9973f。Java21离线三任务54秒成功，3执行/22缓存；原输出未写磁盘日志，不虚构日志路径。详见fortyfourth-palace-static-validation.json。实际放置/讲台DFU/真实居民渲染仍待最终两端验收。
