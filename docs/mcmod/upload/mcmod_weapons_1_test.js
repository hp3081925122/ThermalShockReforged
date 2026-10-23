(async () => {
  const DRY_RUN = false;
  const ENTRIES = [{"key": "fire_bomb", "name": "火焰炸弹", "ename": "Fire Bomb", "regname": "thermal_shock_reforged:fire_bomb", "damage": "", "maxstack": "16", "type": "装备:武器", "content": "<p>[ban:title_menu]</p><p>[h1=介绍]</p><p><strong>火焰炸弹（Fire Bomb）</strong>是一种消耗型武器。使用后会在使用者所在位置引爆，灼烧附近具有火焰弱点的生物。</p><p>[h2=属性]</p><table class=\"table table-bordered text-nowrap\"><tbody><tr><th style=\"word-break: break-all;\" align=\"center\">最大堆叠</th></tr><tr><td style=\"word-break: break-all;\" align=\"center\">16</td></tr></tbody></table><p>[h2=特性]</p><p>每次使用消耗 1 个，使用后进入 3 秒冷却。</p><p>[h2=获取]</p><p>在工作台中使用岩浆块×4、钴矿×4、火药合成，一次产出 6 个。</p>", "icon32": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAACLklEQVR4nO2XzUtUURjGf4ZIRYaQOpZOo6g5CyWwkTYmRuFCEElqM+DKjdrOQfwTXA26M7CFIARBtIlaKRjOcgpCEGMCZxhHnaZASsRPxsWZ1+s5d0YZEOZKPbtzz8d7nud93nPugX8dRflO8D56nj6rf2XxXV5rXsl3AxeNYvND0OPJynD6bhsAj3v92vdUPKK1tzY20wCjB/GsAQOxmKaQ8xQQjESjWnu64wVgMU4l1wHY2f4DwPUbN7Xxw6/vAfDb5QLgbc9i1jjOVSCx1A/A05e7ADS3Kg8I8+/hL9p4t7cRgCbfAwDqhlT/6lQSgPhhS2ZkTJtXcAVsNVvV0J4GKLtdBeRmvhFRORWlfH1RwFJCPGEqtfkj5KwqONmNMH82OALAcvgzYLk8vqLcL8zXgp0AlN8vASy3m0p46r1awNDHD4ClRMEVsFWB1Pl5zGsCC4DlgVtJ5fbw+1oAfH1qnnihwnUn6wacp0Au7M51AVDU5dba6W/7AFwNLKh2RiFBMpE4c13nKSD1LrkTN9cNqZzKySaurwnMApY32l4dafME4ikTBVfAdhKWVjRq/wPeh+1av1TF6tQ1AH5lPGAyvzQnoc0DAyVNAIzP7AFQ6Q8B0PqkG7B7QmAy/zr/CYDlMVX/wYlSACaNeM5TQCC5/fmmFoBKv2IkSshtacJkLndFLjhPAXfxEgDBiRbb4NNwVVcDuetbcn4eCq5A3i8j85ww8TcVuVwvo/84BngwwRQEFu9VAAAAAElFTkSuQmCC", "icon128": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAIAAAACACAYAAADDPmHLAAADjElEQVR4nO3dT0tUYRTH8TFEKjKC0rHUVMqcRRLYSBuLonARRCS1EVq1ydo5RC+h1WA7A1sEQRBEm6hVgeEspyAEMQycYTKdLIiKsH/YOzgHPFxu8vt+toc748iXC8+d597JZAAAAAAAgIqGtP+AqNzxi+tpvv/8zKNN/T/ckvYfgHQRgDgCEEcA4ghAHAGIIwBxjdEXKHZ1JboOn9o/aM5PnR8Nvf5qbSF0/JflFfPz3/hdC72+p1Cthq5DcAYQRwDiCEAcAYgjAHEEII4AxIWvA3jGK5XQ8VMnLplzbx2/Wv9gzn98/2rOt+/Yac491+4eMuefs1lz/vDcTOj9PZwBxBGAOAIQRwDiCEAcAYgjAHGJXwdYmr1szs9cXzPnhwfs/QDeOv9t+ZU593Tmes15X/6oOe8Zs99/cbJuzmt/+s15JlN15jbOAOIIQBwBiCMAcQQgjgDEEYC48L3tbQeHzH3xu/a2mccnvc5fXrC/T/euU+RHKubcu07g7SeIXqdYeVfivgBsHAGIIwBxBCCOAMQRgDgCEOeuIb11/oWr4+bxc+WX5tzbl1+bt/f9e+v898WT5nzPkSZz7u3bj14n6DqQM+ee0tMn5ty7TsAZQBwBiCMAcQQgjgDEEYA4AhAXvi/Auz8/7XV+R2HanHv7AXbX7X375cfd5jw/Yn8+b79AS3afOY/iDCCOAMQRgDgCEEcA4ghAHAGIS/z5AFFrz4fNecNwZ+j49Te/zPnWwrR9vHMdwlNfWgodH8UZQBwBiCMAcQQgjgDEEYA4AhAX3w/g3L/vfd/t7ZvvGbO/T/ees+ft6+8o3Dfn3n6DwTt/zbn3+TzefooozgDiCEAcAYgjAHEEII4AxBGAuPBzAptbes3nB3hyx4ZC7+/dV7A4uc2cf3L2A0TX+TwnEP81AhBHAOIIQBwBiCMAcQQgLrwf4EpTnzm/de+nOW8dLZnzgdNnzXl0P4Enus5//eKZOZ+7ad//X5xoNue3zamPM4A4AhBHAOIIQBwBiCMAcQQgLvHnA3jft3980G3OW0ftdbR3ncD73cKo6Drf+72CpHEGEEcA4ghAHAGIIwBxBCCOAMSFrwN0Ns6a8+JEf/QtQrLt7eY86fvvve/z08YZQBwBiCMAcQQgjgDEEYA4AhAXfj5A2qLPJ4j6trqwqf+HnAHEEYA4AhBHAOIIQBwBiCMAAAAAAAAAEf8A+/vBvEw/X8UAAAAASUVORK5CYII="}];
  const REMARK = '批量添加 Thermal Shock Reforged 武器资料，数据来自模组源码与语言文件';
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
