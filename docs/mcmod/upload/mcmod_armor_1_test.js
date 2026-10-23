(async () => {
  const DRY_RUN = false;
  const ENTRIES = [{"key": "ruby_armor_helmet", "name": "红宝石头盔", "ename": "Ruby Helmet", "regname": "thermal_shock_reforged:ruby_armor_helmet", "damage": "385", "maxstack": "1", "type": "装备:盔甲", "content": "<p>[ban:title_menu]</p><p>[h1=介绍]</p><p><strong>红宝石头盔（Ruby Helmet）</strong>是一件穿戴在头部的盔甲。</p><p>[h2=属性]</p><table class=\"table table-bordered text-nowrap\"><tbody><tr><th style=\"word-break: break-all;\" align=\"center\">穿戴部位</th><th style=\"word-break: break-all;\" align=\"center\">盔甲值</th><th style=\"word-break: break-all;\" align=\"center\">盔甲韧性</th><th style=\"word-break: break-all;\" align=\"center\">耐久度</th><th style=\"word-break: break-all;\" align=\"center\">最大堆叠</th></tr><tr><td style=\"word-break: break-all;\" align=\"center\">头部</td><td style=\"word-break: break-all;\" align=\"center\">4</td><td style=\"word-break: break-all;\" align=\"center\">1</td><td style=\"word-break: break-all;\" align=\"center\">385</td><td style=\"word-break: break-all;\" align=\"center\">1</td></tr></tbody></table><p>[h2=特性]</p><p>穿戴全套红宝石盔甲时获得急迫 I。</p><p>[h2=获取]</p><p>在工作台中使用红宝石×5合成。</p><p>打开愤怒的洞穴绽花首领宝箱时有概率获得。</p><p>[h2=用途]</p><p>可用于制作红宝石天顶头盔。</p>", "icon32": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAABBElEQVR4nO2WsQ7BUBSGP2Kw0KQLTQwVCaPJYPIUFq/hObyGxVOYDCYjScMgwdIEi41Bj6bV2yYqacX9tnvPf+/p+XvuzQWN5t8pJAk6tO9pEqzZxOYoptn8G5RUAal80m8BsD8cAGhYFgB2sxnQ77bbSN14wR3UTuTXAUEqOt3Kzwlv/KkuTOYOFJK6fFQ3UyWYHt3YeH4cCHe78PqnHrXyDfC7XHjrgZBe8E+HA+TJARVGrxU5X61WAuPL5RqpOy+d2A/I3IGS6oYSZ2y7AcBqNg/Ezwkbd4eD5zrPgd+9CV03qdZ06/LvgHS3nAbpftM0AjqpWPSqUxEmcwf0i0ij0TwAE9NbL9p610AAAAAASUVORK5CYII=", "icon128": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAIAAAACACAYAAADDPmHLAAACEklEQVR4nO3csU5UQRiG4YOhoBGSbYCEYokJllYWVl4FjbfhdXgbNl6FlYUVJSYECxOg2QRo6LS1MPNrxslAvudpJ4czLG8mOcPsWRYAAAAAAAAAAAAAAOCp2po9gZfLyc/Zc5jpfPk29W/wbObNmU8A4QQQTgDhBBBOAOEEEG579A2q5/wPb140r/9xddUcPzo8bI6vj4+b45Xvl5fN8d75vf+yND+f0fsEVoBwAggngHACCCeAcAIIJ4Bww/cBKtVz9M3DTvsHFNf3euzz62UFCCeAcAIIJ4BwAggngHACCLc1+1z+u4PVzNtP9/F6M/X+VoBwAggngHACCCeAcAIIJ4Bw5T5A77n9Svn/9ML+zkNzvDqXX+k+D1Co5l+pv3dw0Ry3AoQTQDgBhBNAOAGEE0A4AYSbfh5g73V7n6HX7u7zruvv7u7/00z+7PZr+zl9NCtAOAGEE0A4AYQTQDgBhBNAuO3R76Gr9hnW66Pm9WefPnfd/7br6n6vTt82x8+KfQDvCWQoAYQTQDgBhBNAOAGEE0C46e8J3GxmP6mP9dh/PytAOAGEE0A4AYQTQDgBhBNAuOn7ANW5++p7A9W5/9Vq75/n9LvqOb6a/+jvFfSyAoQTQDgBhBNAOAGEE0A4AYQbeub8b8x+P8Fso8/9V6wA4QQQTgDhBBBOAOEEEE4AAAAAAAAAAAAAAAAAT9QvyvBbp+D1A50AAAAASUVORK5CYII="}];
  const REMARK = '批量添加 Thermal Shock Reforged 盔甲资料，数据来自模组源码与语言文件';
  const sleep = ms => new Promise(r => setTimeout(r, ms));
  const results = [];
  const mold = document.querySelector('.item-category a[data-multi-value="1"]');
  if (mold && mold.getAttribute('data-category-selected') !== 'true') {
    $(mold).trigger('click');
    await sleep(1500);
  }
  for (const e of ENTRIES) {
    document.querySelector('input[placeholder="主要名称.."]').value = e.name;
    document.querySelector('input[placeholder="次要名称.."]').value = e.ename;
    if ($('#item-type-select').length) $('#item-type-select').selectpicker('val', '');
    $('#item-type-custom').val(e.type).show();
    editor.setContent(e.content);
    $('#item-regname').val(e.regname);
    $('#item-damage').val(e.damage);
    $('#item-maxstack').val(e.maxstack);
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
