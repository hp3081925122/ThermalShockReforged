(async () => {
  const DRY_RUN = false;
  const ENTRIES = [{"key": "starfruit_item", "name": "星果", "ename": "Starfruit", "regname": "thermal_shock_reforged:starfruit_item", "damage": "", "maxstack": "64", "type": "食物", "content": "<p>[ban:title_menu]</p><p>[h1=介绍]</p><p><strong>星果（Starfruit）</strong>是一种食物。</p><p>[h2=属性]</p><table class=\"table table-bordered text-nowrap\"><tbody><tr><th style=\"word-break: break-all;\" align=\"center\">饥饿值</th><th style=\"word-break: break-all;\" align=\"center\">饱和度</th><th style=\"word-break: break-all;\" align=\"center\">食用时间</th><th style=\"word-break: break-all;\" align=\"center\">最大堆叠</th></tr><tr><td style=\"word-break: break-all;\" align=\"center\">3</td><td style=\"word-break: break-all;\" align=\"center\">1.8</td><td style=\"word-break: break-all;\" align=\"center\">1.6 秒</td><td style=\"word-break: break-all;\" align=\"center\">64</td></tr></tbody></table><p>[h2=获取]</p><p>在勘探者补给站、腐化农舍的战利品箱中有概率找到。</p><p>破坏星果树上结出的星果获得，有 20% 的概率同时掉落星果种子。</p><p>[h2=用途]</p><p>可用于制作铁素蛋糕、星光雪锥、星光瓶。</p>", "icon32": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAABdklEQVR4nO2WMUoDQRiF3waPEFQQcbdLROwtBU2dyj6Fd/AE4h0EtfYCwsbeXiQEJCRBhCg5g2sx8ybsW2ezpMhswNf8mfl/NvO9+Wd2gTWr3e1l7W4v47ix7gWoonX9Eam3D5sAgO/BHMAmOzA+O8rK8snzWwQAceciA4DmwQ4AYD79AgBM0scIqIEDW1ULlTi+2zc/XuxEwkIXTP3PAABwOjXzJKeCO7C0B0juiFUzFto4tLGVL5vcfgBY9AYV3AFvDzjyayFXYirxjJ/KF1A/Bxz5pSXf1QIbSah5iuS2F+KWeV46amQA0Bm/1v0e0D3V8+4j9z1He8YquAPuTBb2Xle+jJynQ/ae6l99AljsPRXcgWIPrErOepJv7D1QuNNPJD+TsXY36+9N6D/8vfdUfRx4H5m1xPo2U2JPl7s9r0hO1ccBJ+1i33teu9zmq5JTNXRgmI8kOr/ZK81TVcmp4A4UVpsmx7mvXxLpvOZXVXAH/vULEnV28vbSw8sAAAAASUVORK5CYII=", "icon128": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAIAAAACACAYAAADDPmHLAAACz0lEQVR4nO3dwUpUYRiHcRUvQSyIaGZnEe1bBuW6lfsW3YNXIN1DkK1btQtG9+0jLAgZRYQSr0G7gXz/yNvhjPM8v+2nM4fh4YN3zjczqyu60x6/fnNdrf/4vL9ara/938vRXWMAcAYAZwBwBgBnAHAGAFfOiBpfmvM3n2yU/39xdFmuuwPAGQCcAcAZAJwBwBkAnAHALf37APOXT8s5emjTw+/lazzZ3imvb+PRvfLxL0//lOsns0+eB9DNDADOAOAMAM4A4AwAzgDg1se+gKQ7x08+PKz/4Gt4gGlYn8fl+vqvjsrlF6f146c5P3EHgDMAOAOAMwA4A4AzADgDgBv9PECa8+Mc3/U7rIc5f+VnWN+6xbX8w8n7s3I9nTdI3AHgDADOAOAMAM4A4AwAzgDgBj8PEOf8veac353jk3QeoPv/X5qP3+QOAGcAcAYAZwBwBgBnAHAGANd+HyDO+W/DnH+/ewFhPc3h3edP0pwfzgtMturXb3a8Vr7+2/Nvfj+AbmYAcAYAZwBwBgBnAHAGADf89wN076d3P78/9Jzf1fz+gS53ADgDgDMAOAOAMwA4A4AzALj42fL2/f7unDv2nJ8+d9C8358c7J6X6+l+f+IOAGcAcAYAZwBwBgBnAHAGANc/D7Dsc366/jTn+/0AWmQGAGcAcAYAZwBwBgBnAHD99wG636f/vPn8aY5Puufu0/Xv18sHH4e935+4A8AZAJwBwBkAnAHAGQCcAcDF9wF+HdeNTLq/m9ed4wc+lx/v5y/4nJ+4A8AZAJwBwBkAnAHAGQCcAcD1zwN0z8V3zxMMfS4/PP+iz/mJOwCcAcAZAJwBwBkAnAHAGQBc/32ANKc35+hX7x6M+vzJos/5iTsAnAHAGQCcAcAZAJwBwBkAXHuGnU2flb8nkKQ5uvv43edfdu4AcAYAZwBwBgBnAHAGAGcAkiRJkiRJEH8B3X139bjDnJoAAAAASUVORK5CYII="}];
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
