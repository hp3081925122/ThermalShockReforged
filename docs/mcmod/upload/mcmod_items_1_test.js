(async () => {
  const DRY_RUN = false;
  const ENTRIES = [{"key": "ruby", "name": "红宝石", "ename": "Ruby", "regname": "thermal_shock_reforged:ruby", "damage": "", "maxstack": "64", "type": "材料", "content": "<p>[ban:title_menu]</p><p>[h2=获取]</p><p>在工作台中使用红宝石块合成，一次产出 9 个。</p><p>在宝石加工台中加工粗制红宝石获得。</p><p>在转化仪中转化血石获得。</p><p>打开附魔宝箱时有概率获得。</p><p>使用深寒密钥打开深寒宝箱时有概率获得。</p><p>使用深寒密钥打开超级深寒宝箱时有概率获得。</p>", "icon32": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAABOklEQVR4nO2Vv0rDQBzHP1qpFQXJIKXQwSwqFHFwK/oAri6+j8/hK6gvIE5SXBRFBHWJFKEUh+IgaiTWIX4NObGXUiEp3me7P7m77+d+R8Dh+O9M2CZsl1b7o2xwEF0N3GNylMX/gh+nMxPv7mwCcB8EACz6fqot1L9/fA5A7ykEoB2+A3AdvQFwy11qz+IYUHIz8UOnA8BGswnASas1cMF6rQbAWdAD7CaKZ2BtYRaAdd8Dkrs1sdWEzN1044zFNyBME9XKK5DUwN7hKQDefDn1ne56pfoB2JOL4hkQyyz1AbZm5oAksWrj6KKbmq/xrO9f5G5g6reB7xO/xCYa4TSQ1EQ7jFLzPcpf/dmSi9wNWP+GQjXRKMUm9ErE5eMzkD25GB8DwjQhhk0uxs+AkAkxbHKRuwGHw/EJ2ayJe8RupC8AAAAASUVORK5CYII=", "icon128": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAIAAAACACAYAAADDPmHLAAACQ0lEQVR4nO3dv2pTYRjH8WjFPyhIBikFB7OoUMTBregFuLp4P16Ht6DegDhJcVEUEdQlUoRSHIqDqJGod/A8hUM4Cb/PZ304PYF8+8L75qSdTAAAAAAAAAAAAAAAgE11auwX8GDr1r+xX8OYni7fj/oenB7z5oxPAOEEEE4A4QQQTgDhBBBu8B506D7+0cN75fzLfF7Or81mg67vdD//yYs35fz4+6KcHyz+lPMPy9/l/NPk86D30AoQTgDhBBBOAOEEEE4A4QQQrt1Ddvv8ofv4r4eH5fzu3l45f7m/X85X7erOTjl/PT8u52OfE1gBwgkgnADCCSCcAMIJIJwAwg0+B7h95WJ5/Z3ZtJx3n7cPNfbzBN05x8ej+nfQOQArJYBwAggngHACCCeAcAIIt/LvBXTnBNvnf5Xz7nmAx89elfPp5bPlvNN9Xn9z+285H3uf37EChBNAOAGEE0A4AYQTQDgBhFv536i7MblenhPcv3CpvL7bx3fPGzx/e1TOO939x36ufygrQDgBhBNAOAGEE0A4AYQTQLgzq75Bu4/9WZ8T7C7OlZd3zxMcLJblvDOd1OcA677P71gBwgkgnADCCSCcAMIJIJwAwo3+fwM73fMEu1v1OUH3vYTOu28/yvm67/M7VoBwAggngHACCCeAcAIIJ4Bwa71HPYmh5wSdTd/nd6wA4QQQTgDhBBBOAOEEEE4A4TZ6D3sS3TlBZ9P3+R0rQDgBhBNAOAGEE0A4AYQTAAAAAAAAAAAAAAAAwIb6D+nOifMGSJHoAAAAAElFTkSuQmCC"}];
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
