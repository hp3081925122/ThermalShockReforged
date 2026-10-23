(async () => {
  const DRY_RUN = false;
  const ENTRIES = [{"key": "bone_sword", "name": "骨剑", "ename": "Bone Sword", "regname": "thermal_shock_reforged:bone_sword", "damage": "100", "maxstack": "1", "type": "装备:武器", "content": "<p>[ban:title_menu]</p><p>[h1=介绍]</p><p><strong>骨剑（Bone Sword）</strong>是一种剑。具有铁属性，手持时攻击距离增加 2 格。</p><p>[h2=属性]</p><table class=\"table table-bordered text-nowrap\"><tbody><tr><th style=\"word-break: break-all;\" align=\"center\">攻击伤害</th><th style=\"word-break: break-all;\" align=\"center\">攻击速度</th><th style=\"word-break: break-all;\" align=\"center\">耐久度</th><th style=\"word-break: break-all;\" align=\"center\">最大堆叠</th></tr><tr><td style=\"word-break: break-all;\" align=\"center\">[icon:health=4.5,点]</td><td style=\"word-break: break-all;\" align=\"center\">1.8</td><td style=\"word-break: break-all;\" align=\"center\">100</td><td style=\"word-break: break-all;\" align=\"center\">1</td></tr></tbody></table><p>[h2=获取]</p><p>在工作台中使用骨头×2、钴棒合成。</p><p>在勘探者补给站、腐化农舍、隔离哨所的战利品箱中有概率找到。</p>", "icon32": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAABhElEQVR4nOWXu0vDUBSHv2gFBRcLQpFaiuBUqU/UpYtgNxE6OTp0ddC/p4OrIIXilsHBTSwohUxSkbaUgtJMPlAwDumxmAcNJTEp/rbkJrn3++7JIYH/HiXoCTYyGQNgJh53HB8LegGDEpgBIc/n8wDMpVK/xk9LJSACBmJ+P9BKflOtApDudAC4rdUAqGqaAhEw4FsNCPlqNgvAY6sFQDqZBOzkktE1sJvLGQB6tws47HmP/PLaJK8/aI5zjZ4BId8vFABoNxoAPPWqfDaRAOCsogLu5JLQDXjuA1LlQl4pl4H+Xgu5qnojl0TfgLWzWcmlyo+PNodaQOgGbPskVS4R0kGdTUzpb+Z9o1sD0tkOi0Wg/55L3Hq69dhrQjfgumpr9cv7PSypW0I3YKuBvfkVA2BRmQT6PX1nKyuXGOCfiegYOFhYMwDWp81TV/o7APXmD6n5fT/l7wJCN6CcLG0bAMsT4wCcP78AcNG8C/yvCSJgINZ+/QDg/vML+DtySegGvgFjIoyBCGT2kgAAAABJRU5ErkJggg==", "icon128": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAIAAAACACAYAAADDPmHLAAAC0UlEQVR4nO3cP0scURRAcdc/YCCNgiBBRQKplI2akKSxCWS7IFhZWtimSD6PRVohCGK3RYp04kKCYBUMoiJCglsZJUL0G9xbPIad9Zxf+5zZLQ4P7uxzBgYkSZIkSRJFo9dfgO7l3NxdtD42Pl7p5w9WenfVngHAGQCcAcAZAJwBwBkAnM8BKpbN+a1WK7z+ycxM0ed/3twM190B4AwAzgDgDADOAOAMAM4A4IZ7/QX6Xemcv9/phOuzFxfh+veDg3C9c3gYPutxB4AzADgDgDMAOAOAMwA4A4DzPEAim/MXm83w+uOzs3B9dmoqXC+d8zPuAHAGAGcAcAYAZwBwBgBnAHAP/jnAu+XlcI7vXl6G1xf/np/M+V/34jn/6FfZnJ9xB4AzADgDgDMAOAOAMwA4A4Dr++cA2Zy/sroaXn9+chKu/07O5U9MTobrWzvtcL3qOT/jDgBnAHAGAGcAcAYAZwBwBgBX+/cDZOfyszl/Z3s7XM9+r8/m/Ha73nN+xh0AzgDgDADOAOAMAM4A4AwArufPAUrfs1c652fn8j9+eBWu9zt3ADgDgDMAOAOAMwA4A4AzALji36qzc/mZbE7v9Xv2sucU3evw9p4HUL0ZAJwBwBkAnAHAGQCcAcAVnwfI3rO3vrERrmf/n5+p+n36pdfXnTsAnAHAGQCcAcAZAJwBwBkAXOUzbum5/+z/7x/6nF41dwA4A4AzADgDgDMAOAOAMwC44vMA76cXwjn/WWM0vD57n/7b183sK4Sf73OCmDsAnAHAGQCcAcAZAJwBwBkAXPocYO3pUjhnv3gc3+Jb9yZcPzpN5/Tw88ceJVcr5A4AZwBwBgBnAHAGAGcAcAYA1/g0/yacs5+PDIU3+PLnKlzfPf3h7/E15g4AZwBwBgBnAHAGAGcAcAYAN3z+91/4Bz9v/4frzvn9zR0AzgDgDADOAOAMAM4A4AwA7h4U5I01SF9iQQAAAABJRU5ErkJggg=="}, {"key": "ice_bomb", "name": "冰弹", "ename": "Ice Bomb", "regname": "thermal_shock_reforged:ice_bomb", "damage": "", "maxstack": "16", "type": "装备:武器", "content": "<p>[ban:title_menu]</p><p>[h1=介绍]</p><p><strong>冰弹（Ice Bomb）</strong>是一种消耗型武器。使用后会在使用者所在位置引爆，冻结附近具有冰霜弱点的生物。</p><p>[h2=属性]</p><table class=\"table table-bordered text-nowrap\"><tbody><tr><th style=\"word-break: break-all;\" align=\"center\">最大堆叠</th></tr><tr><td style=\"word-break: break-all;\" align=\"center\">16</td></tr></tbody></table><p>[h2=特性]</p><p>每次使用消耗 1 个，使用后进入 3 秒冷却。</p><p>[h2=获取]</p><p>在工作台中使用永冻石×4、钴矿×4、火药合成，一次产出 6 个。</p><p>击杀普罗托伊·德尔塔时有概率掉落。</p><p>在冰霜钻井、层云山巅站、层云气象站的战利品箱中有概率找到。</p>", "icon32": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAABsElEQVR4nO2Xv0sCcRiHn6KpMltEC6KhBIMmIVosiIagJYoacm4Im2rpb2iouT8gaokcoraI0CWwliAM28xUXPq92hAfjvuepwiBJ/Vs3ve+x32ee9/3PPjrdDS7ITK1XK23nk0dN3XNzmZv4LfpanRCaDRWBegfCAEwsxC3rVfyOdvvl2LJZqj0mK5rxLsGlLyYSwEwNr0CWIkr5WcAvj7eAOju7bPtX1zfAiC5TxXcTbTcgOOulPzw5AiAxMYmAOPRCcBK/pC5se0bioQBy4TWLRN7gNOEdw2o6t2SqzYu754AiC+tAu4mhHcNKLme2X3mCrCqPJ/9qX4lFzIgTBPDIxHbevrsFLBMtNyAYw6ozxsld0PdIxOqhUBwsOb53jPgRmI3WfO4+S4wKRcKdde9Z0D9rmenatYkU5e4oSrXPqGaMmm5Acck9AXCtvd5ZDJmW1dXyIRqwEzefpNQmBPxYGcbgOjsPOCcD8JMfntxDsBcfA1wTkDhPQOikQmzv31+HwDZ6zTQOLnwrgEhE5+vZcAyIMz+loEefxBog3/FTX8ZmXPC5L2Sa68vo3++AUEvw6fxwdYxAAAAAElFTkSuQmCC", "icon128": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAIAAAACACAYAAADDPmHLAAADGElEQVR4nO3dv2oUURSA8VWs1DU2IVEQCw2sYBUQmyiIhWAjihZubSGx0sZnsNDaBxBtxBSinYhkGyHaCBJZuxiTJY3/W32DcwiH4Rq+79dexlmXj4F7MjPb60mSJEmSJIpdrT9A1eDM1b8tz7+6/HRHf4e7W38AtWUAcAYAZwBwBgBnAHAGALen9QeYPb4Q7uMPHpoNjz93aVg6/9bauHT8t43N0hxi8/Oo6RzBKwCcAcAZAJwBwBkAnAHAGQBc53OAbJ+/MV4Ojz9x9lq4nu3jtyZfw/U/v36E63v3HwjXM5dv3gnXlx72wu+n6zmBVwA4A4AzADgDgDMAOAOAMwC48h4z2+c/fvYkPH7x1u1w/eT8qXA92+d/WnkXrmeODObC9WxOkJ0/nxM8CNercwKvAHAGAGcAcAYAZwBwBgBnAHCdzwGy+/q73udn9xu8/vAlXB9euR6udz0nyDgHUIkBwBkAnAHAGQCcAcAZAFy6h8z2+dnfsz+uvAnXs/vy11bj+/6zfX4mmwNkqnOCo8cGpfOPXjwP17M5gVcAOAOAMwA4A4AzADgDgDMAuPL7AbLn81vv87uWPfeQzQmy+wWmZw5v+zNth1cAOAOAMwA4A4AzADgDgDMAuOa/F5BZvL/U6b9f/b2Aqsn6etPzewWAMwA4A4AzADgDgDMAOAOAq98PkDy/n/29O7tvPntPXvZcQtey+/Kz/18mu5+iyisAnAHAGQCcAcAZAJwBwBkAXPk9gf3pufD9AZnB6YXS+bPnCrI5QXY/QHWf73sC9V8zADgDgDMAOAOAMwA4A4Dr/PcCsn34o3t3w/X58xfD9er7BzLVff77Vy/D9QvDG+F69T2AGa8AcAYAZwBwBgBnAHAGAGcAcOU5QKb1nKD6/H1/qh+ur74dheut9/kZrwBwBgBnAHAGAGcAcAYAZwBwnc8BMtmc4Pf3SXh8NgfIVJ+/z+YA+6ZmwvWu9/kZrwBwBgBnAHAGAGcAcAYAZwBwzecAVdX3E1T93Brv6O/QKwCcAcAZAJwBwBkAnAHAGYAkSZIkSRLEP1kbxE+e3BvQAAAAAElFTkSuQmCC"}];
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
