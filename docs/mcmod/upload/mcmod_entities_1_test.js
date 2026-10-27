(async () => {
  const DRY_RUN = false;
  const ENTRIES = [{"key": "galvanite", "name": "电锌矿怪", "ename": "Galvanite", "regname": "", "damage": "", "maxstack": "", "type": "敌对生物", "category": "4", "content": "<p>[ban:title_menu]</p><p>[h1=概述]</p><p>电锌矿怪是雨天出现的矿物生物，发现目标后会冲锋。</p><p>[h1=基础属性]</p><div class=\"table-scroll\"><table width=\"420\" class=\"table table-bordered text-nowrap\"><thead><tr><th align=\"center\"><p>属性</p></th><th align=\"center\"><p>数值</p></th></tr></thead><tbody><tr><td><p>生命值</p></td><td><p>[icon:health=12,点]</p></td></tr><tr><td><p>护甲值</p></td><td><p>6</p></td></tr><tr><td><p>攻击力</p></td><td><p>7</p></td></tr><tr><td><p>移动速度</p></td><td><p>0.15</p></td></tr><tr><td><p>击退抗性</p></td><td><p>0</p></td></tr><tr><td><p>体型（宽×高）</p></td><td><p>0.6 × 2</p></td></tr><tr><td><p>经验掉落</p></td><td><p>10</p></td></tr><tr><td><p>战利品</p></td><td><p>见下文</p></td></tr></tbody></table></div><p>[h1=生成]</p><p>只在下雨或雷暴时生成。</p><p>会自然生成于星尘花园、墓园、钴石高地、北境冻土、幻光海域、北境森林、朱红木林（权重 20，每群 1 只）。</p><p>也可以使用电锌矿怪刷怪蛋生成。</p><p>[h1=行为]</p><p>主动攻击玩家（追踪范围 32 格），冲锋时速度提高到 2 倍，普通近战攻击，攻击击退 0.8。受到攻击时会呼叫附近的同类。</p><p>免疫雷击伤害。会躲避 32 格内的狂风怪。</p><p>[h1=掉落物]</p><div class=\"table-scroll\"><table width=\"560\" class=\"table table-bordered text-nowrap\"><thead><tr><th align=\"center\"><p>物品</p></th><th align=\"center\"><p>数量</p></th><th align=\"center\"><p>说明</p></th></tr></thead><tbody><tr><td><p>恒星光屑</p></td><td><p>0～1</p></td><td><p>权重 5/6（约83.33%）</p></td></tr><tr><td><p>惧光石</p></td><td><p>1～2</p></td><td><p>权重 1/6（约16.67%）</p></td></tr><tr><td><p>铁粒</p></td><td><p>3～5</p></td><td><p>权重 1/2（约50%）；抽取 0～1 次</p></td></tr><tr><td><p>金粒</p></td><td><p>5～8</p></td><td><p>权重 1/2（约50%）；抽取 0～1 次</p></td></tr><tr><td><p>生物电池</p></td><td><p>1</p></td><td><p>20%概率；抽取 2 次</p></td></tr></tbody></table></div><p>[h1=元素克制]</p><p>抵抗冰霜、铁属性攻击（该属性攻击只造成 1 点伤害）；弱于火焰属性攻击（受到该属性攻击时额外受到原伤害×1.5 的伤害）。</p>", "icon32": "", "icon128": ""}];
  const REMARK = "";
  const sleep = ms => new Promise(r => setTimeout(r, ms));
  const setIf = (sel, val) => { if ($(sel).length) $(sel).val(val); };
  const results = [];
  for (const e of ENTRIES) {
    const category = String(e.category || 1);
    const tab = document.querySelector('.item-category a[data-multi-value="' + category + '"]');
    if (!tab) {
      console.warn('[mcmod-batch] 找不到资料分类', category, e.name);
      break;
    }
    if (tab.getAttribute('data-category-selected') !== 'true') {
      $(tab).trigger('click');
      await sleep(1500);
    }
    document.querySelector('input[placeholder="主要名称.."]').value = e.name;
    document.querySelector('input[placeholder="次要名称.."]').value = e.ename || '';
    if ($('#item-type-select').length) $('#item-type-select').selectpicker('val', '');
    $('#item-type-custom').val(e.type || '').show();
    editor.setContent(e.content || '');
    setIf('#item-regname', e.regname || '');
    setIf('#item-damage', e.damage || '');
    setIf('#item-maxstack', e.maxstack || '');
    setIf('#item-oredict', e.oredict || '');
    setIf('#item-metadata', e.metadata || '');
    $('#icon-32x').val(e.icon32 || '');
    $('#icon-128x').val(e.icon128 || '');
    if (e.icon32) $('#item-icon-32x-preview-img').attr('src', e.icon32);
    if (e.icon128) $('#item-icon-128x-preview-img').attr('src', e.icon128);
    $('input[placeholder="改动附言.."]').val(REMARK);
    const data = getEditorData(DRY_RUN);
    if (DRY_RUN) {
      results.push({ name: e.name, data });
      continue;
    }
    const resp = await new Promise(resolve => {
      $.ajax({
        url: '//' + strEventUrl + '/action/edit/doEdit/',
        type: 'post',
        async: true,
        data: { data: JSON.stringify(data) },
        xhrFields: { withCredentials: true },
        crossDomain: true,
        complete: x => resolve(x.responseText)
      });
    });
    let state = -1;
    let url = '';
    try {
      const j = JSON.parse(resp);
      state = j.state;
      url = j.url || '';
    } catch (err) {}
    results.push({ name: e.name, state, url });
    console.log('[mcmod-batch]', e.name, state, url);
    if (state !== 0) break;
    await sleep(4000);
  }
  window.__mcmodBatchResults = results;
  return results;
})();
