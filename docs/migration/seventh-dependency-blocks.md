# 农作物和玻璃获取链补齐

本批共享资源审计确认pure_muck和cobalt_block未注册，分别阻塞两花盆制作及两玻璃饰面制作；属于必须补真实实现的获取依赖，不能删配方规避引用。

已核原PureMuckBlock：普通Block/无BE无程序，MUD声音、硬.8抗爆4、速度.6跳.7、遮光15、原翻译tooltip。目标按完整属性实现；原缺地图色，依slush.png实际灰紫色(74,72,96)/(87,85,109)及既有楼梯/台阶COLOR_GRAY保持同族补色。不是泥巴默认属性整块复制。

原CobaltBlockBlock有blockstate整数0..2，亮度0/5/15，ANCIENT_DEBRIS声音、BASEDRUM、硬1/抗爆8.001806、正确工具要求、遮光15、全方块shape。目标保存该真实三态/光照，不简化静态块；原未见直接procedure，外部Hailstorm/Zerker制作读取它作材料，随机器模块后续迁。地图色蓝与同族保持。

两真实BlockItem、loot、工具标签和闭合配方由共享资源owner整合；整个模块未完成前不宣称机器来源全部实现。修改后固定快照由子代理执行编译和数据验证。
