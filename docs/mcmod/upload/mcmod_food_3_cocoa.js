(async () => {
  const DRY_RUN = false;
  const ENTRIES = [{"key": "cocoa", "name": "热可可", "ename": "Cocoa", "regname": "thermal_shock_reforged:cocoa", "damage": "", "maxstack": "1", "type": "食物", "content": "<p>[ban:title_menu]</p><p>[h1=介绍]</p><p><strong>热可可（Cocoa）</strong>是一种食物。</p><p>[h2=属性]</p><table class=\"table table-bordered text-nowrap\"><tbody><tr><th style=\"word-break: break-all;\" align=\"center\">饥饿值</th><th style=\"word-break: break-all;\" align=\"center\">饱和度</th><th style=\"word-break: break-all;\" align=\"center\">食用时间</th><th style=\"word-break: break-all;\" align=\"center\">最大堆叠</th></tr><tr><td style=\"word-break: break-all;\" align=\"center\">6</td><td style=\"word-break: break-all;\" align=\"center\">24</td><td style=\"word-break: break-all;\" align=\"center\">1.6 秒</td><td style=\"word-break: break-all;\" align=\"center\">1</td></tr></tbody></table><p>[h2=特性]</p><p>饮用后获得 6 秒生命恢复 II 与 50 秒温暖惬意 II，并立即解除冻伤；温暖惬意持续期间会不断解除冻伤。</p><p>饮用后返还一个玻璃瓶。</p><p>[h2=获取]</p><p>可以向低语者消耗 5 级经验交易获得。</p><p>在冰霜钻井、层云山巅站、层云气象站的战利品箱中有概率找到。</p><p>在酿造台中以洛肯牛奶瓶为底料，加入原版可可豆酿造获得。</p>", "icon32": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAABWUlEQVR4nO2WP0jDUBCHP4uzIDiJQ3BysVC6O4ni4KKDU0edFC0OgpOTThIHhw46OSiCLp26iHTs8kBbERXtIopUsYL/oBCH5okNoX0vCC/V/Jbk3rs77j7IXSDSf1dH0MDi3Jjz0y5dXAEwlbvUyhkLWsBvSZtA8fbdAfgoHPre31mjAIwnepRyGyfQqRtw8/AKwFtmpeH86bkKQF/mTCufcQKBlRUVJysq319Cys47KTvvNIvxk3ECUQGB50Dv3prv/XxsGICd9FB7zAFlArLzx6VpAF4WNwAY6c4B8Fm6B0DsCgC2kjNAaxLhJ9Cqc690SRgnoLwLCuUyAAOuLTv1qnZebfAn2Txv+AnI7SfVtb4AQC3R7+u/bU24b0dKBYSfwP5xfb/HPeen4hqAQZeEtLH0CjBOQHkSyl0fP1j2d0hv1p/2LAAnk6vAX5iEXqn+9bTNNoz0BTTxcmrqqUPiAAAAAElFTkSuQmCC", "icon128": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAIAAAACACAYAAADDPmHLAAACk0lEQVR4nO3dv0tVYRyA8TdpDoImabg0tSSIu1MUDS45NDnmZJQ0CE5NNcltaHDQyaEIamlyiXB0uVAqUpEukURFBfYDBP0Pvi/2enjP6Xk+6/eecw+XhxfOey/npiRJkiRJkiRJkiRJkv43p2pfQNM2bl07LDl+8+37cH5j9V2nP8Oh2hegugwAzgDgDADOAOAMAM4A4Dp9D5tSShsff4f3+X/Wnzf6/p96V8P5xOi5Vn/GrgBwBgBnAHAGAGcAcAYAZwBwp2tfQKmdz/vh/NfivaLzf/v+I5yfX9wqOn9trgBwBgBnAHAGAGcAcAYAZwBwrf6u+iS8GHwJfy+Q+75+qr8WHr8yO97pz9AVAM4A4AwAzgDgDADOAOAMAM4A4AwAzgDgDADOAOAMAM4A4AwArtPfZaeUfz7A8JMHjb7/7aHL4bztvxdwBYAzADgDgDMAOAOAMwA4A4Br9T1qSvn7/K9zN8Pjf959GM6vnF0N538398L54PEgnC+NTYfz2vsErgBwBgBnAHAGAGcAcAYAZwBw1fcBat/nl+r6PoErAJwBwBkAnAHAGQCcAcAZAFzr/y9gfXc3nF/MHJ+7Ty91sB3/n0Du+tPYyV3Lv3AFgDMAOAOAMwA4A4AzADgDgKu+D5D737+cMwt3wvnB6IWi8+cs965nXvGy0fcv5QoAZwBwBgBnAHAGAGcAcAYAV30f4OmrrXA+Unj+N4MP4fxSZp8gd3zqHfOCWsYVAM4A4AwAzgDgDADOAOAMAK768wFypvpr4fMDRp7NN3sBs4/ieX8mHL+evB/OfU6gqjIAOAOAMwA4A4AzADgDgGv9PkBObp+gttr3+TmuAHAGAGcAcAYAZwBwBgBnAJIkSZIkSRBHWgJu0yLi1BIAAAAASUVORK5CYII="}];
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
