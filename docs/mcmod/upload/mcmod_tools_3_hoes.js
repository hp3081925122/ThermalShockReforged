(async () => {
  const DRY_RUN = false;
  const ENTRIES = [{"key": "bloodstone_hoe", "name": "血石锄", "ename": "Bloodstone Hoe", "regname": "thermal_shock_reforged:bloodstone_hoe", "damage": "1500", "maxstack": "1", "type": "装备:工具", "content": "<p>[ban:title_menu]</p><p>[h1=介绍]</p><p><strong>血石锄（Bloodstone Hoe）</strong>是一种锄。</p><p>[h2=属性]</p><table class=\"table table-bordered text-nowrap\"><tbody><tr><th style=\"word-break: break-all;\" align=\"center\">攻击伤害</th><th style=\"word-break: break-all;\" align=\"center\">攻击速度</th><th style=\"word-break: break-all;\" align=\"center\">挖掘等级</th><th style=\"word-break: break-all;\" align=\"center\">挖掘速度</th><th style=\"word-break: break-all;\" align=\"center\">耐久度</th><th style=\"word-break: break-all;\" align=\"center\">最大堆叠</th></tr><tr><td style=\"word-break: break-all;\" align=\"center\">[icon:health=3,点]</td><td style=\"word-break: break-all;\" align=\"center\">1</td><td style=\"word-break: break-all;\" align=\"center\">钻石级</td><td style=\"word-break: break-all;\" align=\"center\">8</td><td style=\"word-break: break-all;\" align=\"center\">1500</td><td style=\"word-break: break-all;\" align=\"center\">1</td></tr></tbody></table><p>[h2=获取]</p><p>在工作台中使用血石×2、钴棒×2合成。</p>", "icon32": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAA8UlEQVR4nO2VwQ7BQBCGP40QJEQIF+Hi4IZ4Ew/qTYSjAwciEkKERA8irUMzh5aqVptto/9tN5nN/N/M7ECqf1cmaGC51jLf3V9PW19vakETCEtZvwHivDPqA1AqVwC4XS8ArKfYyHgRUU4g9B5o9rq2836xBNxJJJeAm4RMsVEFIF8oAHBe74BXEsoJpAn4/gfcJLWXKZDu1zl/jFNO4OcpcHa9frAcf7sTkktgWG+bACvTsN0nbhv6JiDOx7kHAJO7NUiz4yYQzeQQEOcDzcp5bli1D+pcFH8CYdfcqfgSiKrmTikn4LkNo3IuUk4g1RNjw1Hr2puYtAAAAABJRU5ErkJggg==", "icon128": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAIAAAACACAYAAADDPmHLAAACGElEQVR4nO3dwWrUUBiA0VhEqUJFFN2Ibly4U/FNfFDfRHTpoi4qIihKsWAXIq1vcKNMQ2bmO2cbMoTh48J/k8xMEwAAAAAAAACwb66tfQFLO7r36HLJzz/78Xmnv8ODtS+AdQkgTgBxAogTQJwA4gQQd33tC9jU3Jz/5NXz4fm3j+4Mj/86+zk8fvJ22mifYe19BCtAnADiBBAngDgBxAkgTgBxO30v+18s/TzAw2dPNzr/64fj4fGl9wmsAHECiBNAnADiBBAngDgBxO39PsDS5vYZbj24Ozz/5uHh8PjpyZfh8U33CawAcQKIE0CcAOIEECeAOAHECSBOAHECiBNAnADiBBAngDgBxO387wMsbe5+/9x7AXPP/Z9Pp/9/UVfIChAngDgBxAkgTgBxAogTQFz+vYBNn+s//zae49f+HcA5VoA4AcQJIE4AcQKIE0CcAOK2eka9Ci/vPx7O+R8vLzb6/G2f8+dYAeIEECeAOAHECSBOAHECiNvpGXaa5uf81zf+DM9/83v8asS77592/jsasQLECSBOAHECiBNAnADiBBC39TPu3Jz/4mDc8PuL8f3+fZ/z51gB4gQQJ4A4AcQJIE4AcQKIW30Gdj9/XVaAOAHECSBOAHECiBNAnADiFp+R3c/fblaAOAHECSBOAHECiBNAnADiVv/fQHP+uqwAcQKIE0CcAOIEECeAOAEAAAAARPwFUHRShxGKSaMAAAAASUVORK5CYII="}, {"key": "ruby_hoe", "name": "红宝石锄", "ename": "Ruby Hoe", "regname": "thermal_shock_reforged:ruby_hoe", "damage": "1200", "maxstack": "1", "type": "装备:工具", "content": "<p>[ban:title_menu]</p><p>[h1=介绍]</p><p><strong>红宝石锄（Ruby Hoe）</strong>是一种锄。</p><p>[h2=属性]</p><table class=\"table table-bordered text-nowrap\"><tbody><tr><th style=\"word-break: break-all;\" align=\"center\">攻击伤害</th><th style=\"word-break: break-all;\" align=\"center\">攻击速度</th><th style=\"word-break: break-all;\" align=\"center\">挖掘等级</th><th style=\"word-break: break-all;\" align=\"center\">挖掘速度</th><th style=\"word-break: break-all;\" align=\"center\">耐久度</th><th style=\"word-break: break-all;\" align=\"center\">最大堆叠</th></tr><tr><td style=\"word-break: break-all;\" align=\"center\">[icon:health=3,点]</td><td style=\"word-break: break-all;\" align=\"center\">1</td><td style=\"word-break: break-all;\" align=\"center\">钻石级</td><td style=\"word-break: break-all;\" align=\"center\">10</td><td style=\"word-break: break-all;\" align=\"center\">1200</td><td style=\"word-break: break-all;\" align=\"center\">1</td></tr></tbody></table><p>[h2=获取]</p><p>在工作台中使用红宝石×2、钴棒×2合成。</p>", "icon32": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAA9klEQVR4nO2VOQrCUBRFj2Jlo1jFQjA4bkFwJe5CXIdkF27BDQhuwQkMWBgsHEAUbLTQVyQxmEm+Q06XDy+8e94fIOHfSYUtbFC/PlufMgv0z3TYBuIiE7RAkvdbFQDKug6AuVwC0BtjM/PKiHIDse+Bbjlr+zbME+Bt4nsNeCFmOloBgN3hAsDwfATcJpQbSBoIfA94IbOXU2CYW191yg1EPgXOXT+w7sn9vgnfa0Crtq8AucXGtv77r6EkLzVrAKwe69ZiFMqmcgO+u5bk+aIGwH5tAeGTC59vwDXzyRyInlz4XAPvmrkT5QZe3gPvSi4oN5BwA7wqSjC8CI5LAAAAAElFTkSuQmCC", "icon128": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAIAAAACACAYAAADDPmHLAAACH0lEQVR4nO3cS4rUUBiA0VIcOVEctQOhG59bEFyJuxDXIb0Lt+AGBLdga0M3OLBw4ANEwYnu4EZIhVT1d870khCKjwt/UslmAwAAAAAAAABcNdfWvoClPd48+rvk+c82Hw76N7y+9gWwLgHECSBOAHECiBNAnADibqx9AXNNzfmvnt4fHn98cjJcv7y4GK6/fLeZdZ9h7fsIdoA4AcQJIE4AcQKIE0CcAOIO+ln2/1j6/wAvjm/OOv708tdwfen7BHaAOAHECSBOAHECiBNAnADirvx9gKVN3Wd4fnRnePy3H3+G629+/xyuz71PYAeIE0CcAOIEECeAOAHECSBOAHECiBNAnADiBBAngDgBxAkg7uC/D7C0qef9U+8FnF5+3en17JodIE4AcQKIE0CcAOIEECeAuPx7AXP/1/96O57z1/4O4BQ7QJwA4gQQJ4A4AcQJIE4AcXs9o+7C0YNnwzn/1vmXWeff9zl/ih0gTgBxAogTQJwA4gQQJ4C4g38vYGrOv/fk4fD4TxPn356/Peg5f4odIE4AcQKIE0CcAOIEECeAuL2fcafm/Nt3j4bHf/+8Ha5f9Tl/ih0gTgBxAogTQJwA4gQQJ4C41Wfg2c/z338crtfn/Cl2gDgBxAkgTgBxAogTQJwA4hafkT3P3292gDgBxAkgTgBxAogTQJwA4lb/PoA5f112gDgBxAkgTgBxAogTQJwAAAAAACL+AReQSsxbDbv6AAAAAElFTkSuQmCC"}];
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
