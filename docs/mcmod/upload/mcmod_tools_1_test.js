(async () => {
  const DRY_RUN = false;
  const ENTRIES = [{"key": "cobalt_axe", "name": "钴斧", "ename": "Cobalt Axe", "regname": "thermal_shock_reforged:cobalt_axe", "damage": "400", "maxstack": "1", "type": "装备:工具", "content": "<p>[ban:title_menu]</p><p>[h1=介绍]</p><p><strong>钴斧（Cobalt Axe）</strong>是一种斧。</p><p>[h2=属性]</p><table class=\"table table-bordered text-nowrap\"><tbody><tr><th style=\"word-break: break-all;\" align=\"center\">攻击伤害</th><th style=\"word-break: break-all;\" align=\"center\">攻击速度</th><th style=\"word-break: break-all;\" align=\"center\">挖掘等级</th><th style=\"word-break: break-all;\" align=\"center\">挖掘速度</th><th style=\"word-break: break-all;\" align=\"center\">耐久度</th><th style=\"word-break: break-all;\" align=\"center\">最大堆叠</th></tr><tr><td style=\"word-break: break-all;\" align=\"center\">[icon:health=7,点]</td><td style=\"word-break: break-all;\" align=\"center\">1</td><td style=\"word-break: break-all;\" align=\"center\">钻石级</td><td style=\"word-break: break-all;\" align=\"center\">7</td><td style=\"word-break: break-all;\" align=\"center\">400</td><td style=\"word-break: break-all;\" align=\"center\">1</td></tr></tbody></table><p>[h2=特性]</p><p>除原版斧的功能外，还可以为赭色原木、朱红原木、寒光原木、深蓝原木以及对应的木头去皮。</p><p>[h2=获取]</p><p>在工作台中使用钴矿×3、钴棒×2合成。</p>", "icon32": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAABLElEQVR4nO2XPWsCQRCGnwSrkFMbUXPYKVjb2FyXzpAi/8LaH2Ltv0gRtLMI2CUIgRAE04VExSYfaquFDHLr7UXjwnqat9y53bv3mZ0ZDo5dJ6YPzOS9edD68LUT+K5T0x+wrWKmDhLnN9Wab/22UQ/dF30CqvOXx3sAUukLAKZfo9D91gn8uQrEuXd1DcBz98EXT7susCLRad4B69VgncDOd2A8+gDASTgAnJ3HAZhNvpfxX/ZHl4Dc7m675Vsvlr3Q51VZJ2BsFkhVJLMZAN57T774z7h/oLNAnOeKBQDeen1A71iVdQI7d0LJ+edgCOjnvk7RI6DL+bbORdEhYCrnqqwT0PYBcSw93FTOVe0vAXFeuqwAq6m3aYfbVNYJaN04qULgH87BEfjXAt/MY6JU3+VXAAAAAElFTkSuQmCC", "icon128": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAIAAAACACAYAAADDPmHLAAACXUlEQVR4nO3du0oDQRhA4ShWYtQmxAt2CqltbNLZKRa+hbUPYu1bWIh2FkI6JSCIBGInakIa762+wT/FOGSTc7522N1ADgOz11pNkiRJkiRRzIz7B0y6lc32b8n9Dx47Rf+j2ZI7V/UZAJwBwBkAnAHAGQCcAcDNjfsHVF1qnX94dJy1/7PTk6ztczkDwBkAnAHAGQCcAcAZAJwBwOHPA+Su8x9ur8PxRnMtHP9+H4bjpTkDwBkAnAHAGQCcAcAZAJwBwE39cwGpdX57/yDc/r57k3X85vp6OJ46T9C5OA/Hc58bcAaAMwA4A4AzADgDgDMAOAOAw98PMBq+hOP1pXo4Pr+wGI7/fH3Exw9Hy3MGgDMAOAOAMwA4A4AzADgDgJv68wCp++67V5dZ+2/ttLO2Tyn93IAzAJwBwBkAnAHAGQCcAcAZANzUPxeQK/VcwfLqSrj9c+8u6/ifo77fC1A5BgBnAHAGAGcAcAYAZwBwU38/QEpqnb/R2gq3f+r1w/HS6/hczgBwBgBnAHAGAGcAcAYAZwBwlV6j/ofc6/lvr4NwPPc9fePmDABnAHAGAGcAcAYAZwBwBgA30WvYWq389fxJX+enOAPAGQCcAcAZAJwBwBkAnAHAVX6N6/X8spwB4AwAzgDgDADOAOAMAM4A4Iq/HyC1jk+9D9/r+WU5A8AZAJwBwBkAnAHAGQCcAcAVPw+QWudv7+6F46nv+lX9PXxV5wwAZwBwBgBnAHAGAGcAcAYAV3wNXW9shfcD5PI8QB5nADgDgDMAOAOAMwA4A4AzAEmSJEmSJIg/BRVkSpZJqZkAAAAASUVORK5CYII="}];
  const REMARK = '批量添加 Thermal Shock Reforged 工具资料，数据来自模组源码与语言文件';
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
