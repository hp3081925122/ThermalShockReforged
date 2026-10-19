# 第69批：Magnetic Crossbow 实际武器入口

按第68批接续审计迁移onEntitySwing入口。发射MagneticBomb而非MagnetCrossbowBullet；保800耐久、耐火、UNCOMMON、附魔值5、三tooltip、模型/两动画/透明渲染及完整手部变换。

成功发射保Specialized Round消耗1或创造免费、40tick CD、动画1、0.7音量magnet_bow_25、原BlockPos(y+1)出生和look×2运动。5/10/15 SERVER END固定位置依次crossbow.shoot，音量0.3/0.2/0.1，音高-1/1/2，用三阶段独立任务保存，换手或死亡不吞已提交声音。加载器取消生成时不扣弹或CD。

在手NoMelee改既有局部交互距离锁；潜行缓慢255改临时MOVEMENT_SPEED总乘数-1及1tick续期，切物品/松潜行后释放。当前原版AbstractClientPlayer.getFieldOfViewModifier取移动属性与walkingSpeed比值，0速度对应0.5基础视野因子，保瞄准反馈。开火15tick缓慢III和2tick速度I是实际短期移动/FOV反馈，保原效果及可见性；外部药效混合仍需真实客户端验收。

剩余状态持久化，重进恢复本模组属性，不改基础速度或使用静态玩家表。物品动画复用有界、按Stack ID隔离的缓存。耐久附魔接enchantable/durability，不擅归原版弩。

GUI制作来源继续迁移，不编造工作台配方。资源/API/参数静态核对与统一编译待完成，最终两端和Render Preview等整模组迁完后执行。

第69/70批统一验证：compileJava与processResources离线通过，耗时3分24秒；24任务中2执行、22最新。9项视觉资源和3组声音对照原JAR一致，详见seventieth-magnetic-epsilon-static-validation.json；静态与编译不代替最终两端验收。
