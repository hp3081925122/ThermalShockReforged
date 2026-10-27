(() => {
  const TD = '<td style="word-break: break-all;" align="center">';
  const TH = '<th style="word-break: break-all;" align="center">';
  const row = (cells, tag) => '<tr>' + cells.map(c => tag + c + tag.replace('<', '</').replace(/ .*/, '>')).join('') + '</tr>';
  const table = (head, rows) => '<table><tbody>' + row(head, TH) + rows.map(r => row(r, TD)).join('') + '</tbody></table>';
  const TOOL_HEAD = ['单件', '攻击伤害', '攻击速度', '挖掘等级', '挖掘速度', '耐久度', '最大堆叠'];
  const FIX = {
    '/item/add/32099-807013/': {
      name: '碳化硅套具',
      html: '<p>[ban:title_menu]</p><p>[h1=介绍]</p><p><strong>碳化硅套具（Carborundum Tools）</strong>由以下单件组成：碳化硅斧、碳化硅镐、碳化硅铲。</p><p>[h2=属性]</p>'
        + table(TOOL_HEAD, [
          ['碳化硅斧', '[icon:health=11,点]', '1.2', '钻石级', '15', '900', '1'],
          ['碳化硅镐', '[icon:health=3,点]', '1.2', '钻石级', '15', '900', '1'],
          ['碳化硅铲', '[icon:health=3,点]', '1.2', '钻石级', '15', '900', '1']])
        + '<p>[h2=获取]</p><p><strong>碳化硅斧</strong>：在工作台中使用碳化硅×3、钴棒×2合成。</p><p><strong>碳化硅镐</strong>：在工作台中使用碳化硅×3、钴棒×2合成。</p><p><strong>碳化硅铲</strong>：在工作台中使用碳化硅、钴棒×2合成。</p>'
    },
    '/item/add/32099-807012/': {
      name: '骨套具',
      html: '<p>[ban:title_menu]</p><p>[h1=介绍]</p><p><strong>骨套具（Bone Tools）</strong>由以下单件组成：骨斧、骨镐、骨铲。</p><p>[h2=属性]</p>'
        + table(TOOL_HEAD, [
          ['骨斧', '[icon:health=6.5,点]', '1', '石级', '5', '100', '1'],
          ['骨镐', '[icon:health=1.5,点]', '1.2', '石级', '5', '100', '1'],
          ['骨铲', '[icon:health=1.5,点]', '1.2', '石级', '5', '100', '1']])
        + '<p>[h2=特性]</p><p><strong>骨斧</strong>：具有铁属性。</p><p><strong>骨镐</strong>：具有铁属性。</p><p><strong>骨铲</strong>：具有铁属性。</p>'
        + '<p>[h2=获取]</p><p><strong>骨斧</strong>：在工作台中使用骨头×3、钴棒×2合成。在冰霜钻井、勘探者补给站的战利品箱中有概率找到。</p><p><strong>骨镐</strong>：在工作台中使用骨头×3、钴棒×2合成。在冰霜钻井、勘探者补给站、腐化农舍的战利品箱中有概率找到。</p><p><strong>骨铲</strong>：在工作台中使用骨头、钴棒×2合成。在冰霜钻井、勘探者补给站的战利品箱中有概率找到。</p>'
    },
    '/item/add/32099-806988/': {
      name: '静谧套装',
      html: '<p>[ban:title_menu]</p><p>[h1=介绍]</p><p><strong>静谧套装（Silenced Suit）</strong>由以下单件组成：静谧头盔、静谧披风。</p><p>[h2=属性]</p>'
        + table(['单件', '穿戴部位', '盔甲值', '盔甲韧性', '击退抗性', '耐久度', '最大堆叠'], [
          ['静谧头盔', '头盔', '7', '2', '1', '572', '1'],
          ['静谧披风', '胸甲', '5', '2', '1', '832', '1']])
        + '<p>[h2=套装效果]</p><p>同时穿戴静谧头盔与静谧披风并潜行时，每秒使所有已加载维度中的全部生物（包括玩家）发光。</p>'
        + '<p>[h2=单件特性]</p><p><strong>静谧头盔</strong>：穿戴时获得水下呼吸，不会被火焰和熔岩烧毁。</p><p><strong>静谧披风</strong>：受到伤害时有 50% 的概率点燃周围 2 格内的生物 2 秒，不会被火焰和熔岩烧毁。</p>'
        + '<p>[h2=获取]</p><p><strong>静谧头盔</strong>：在制造机中消耗深寒能量，使用层云合金、腐化能量×20、哨兵兜帽、科里尔潜水头盔制作。</p><p><strong>静谧披风</strong>：在制造机中消耗深寒能量，使用层云合金、腐化能量×25、哨兵肩甲、虚空焰围巾制作。</p>'
    },
    '/item/add/32099-806986/': {
      name: '锈丝套装',
      html: '<p>[ban:title_menu]</p><p>[h1=介绍]</p><p><strong>锈丝套装（Rustsilk Suit）</strong>由以下单件组成：锈丝帽、锈丝披风。</p><p>[h2=属性]</p>'
        + table(['单件', '穿戴部位', '盔甲值', '盔甲韧性', '耐久度', '最大堆叠'], [
          ['锈丝帽', '头盔', '1', '0', '55', '1'],
          ['锈丝披风', '胸甲', '1', '0', '80', '1']])
        + '<p>[h2=获取]</p><p><strong>锈丝帽</strong>：击杀亡魂勇士时有概率掉落。</p><p><strong>锈丝披风</strong>：击杀亡魂勇士时有概率掉落。</p>'
        + '<p>[h2=用途]</p><p><strong>锈丝帽</strong>：可用于制作奇异丝线帽。</p><p><strong>锈丝披风</strong>：可用于制作奇异丝线披风。</p>'
    }
  };
  const fix = FIX[location.pathname];
  if (!fix) {
    console.warn('[tsr-fix] 当前页面不是需要修正的 4 个编辑页之一：', location.pathname);
    return;
  }
  const current = document.querySelector('input[placeholder="主要名称.."]').value;
  if (current !== fix.name) {
    console.warn('[tsr-fix] 主要名称不一致，已停止：', current, '≠', fix.name);
    return;
  }
  editor.setContent(fix.html);
  $('input[placeholder="改动附言.."]').val('按套装规范补全单件获取方式，修正套装效果与单件特性');
  console.log('[tsr-fix] 已填入「' + fix.name + '」正文和附言，请检查正文后点击「完成添加」。');
})();
