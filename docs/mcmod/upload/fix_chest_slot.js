(() => {
  const PAGES = {
    '/item/add/32099-806981/': ['红宝石套装', true],
    '/item/add/32099-806982/': ['碳化硅套装', true],
    '/item/add/32099-806983/': ['血石套装', true],
    '/item/add/32099-806985/': ['哨兵套装', true],
    '/item/add/32099-806986/': ['锈丝套装', true],
    '/item/add/32099-806987/': ['奇异丝线套装', true],
    '/item/add/32099-806988/': ['静谧套装', true],
    '/item/add/32099-806989/': ['红宝石天顶套装', true],
    '/item/add/32099-806990/': ['血石天顶套装', true],
    '/item/edit/1024040/': ['科里尔潜水头盔', false],
    '/item/edit/1024041/': ['虚空焰围巾', false],
    '/item/edit/1024046/': ['推进靴', false]
  };
  const SLOT = { '头部': '头盔', '胸部': '胸甲', '腿部': '护腿', '脚部': '靴子' };
  const page = PAGES[location.pathname];
  if (!page) {
    console.warn('[tsr-slot] 当前页面不在修正列表中：', location.pathname);
    return;
  }
  const [expected, abstract] = page;
  const current = document.querySelector('input[placeholder="主要名称.."]').value;
  if (current !== expected) {
    console.warn('[tsr-slot] 主要名称不一致，已停止：', current, '≠', expected);
    return;
  }
  const before = editor.getContent();
  const count = (before.match(/头部|胸部|腿部|脚部/g) || []).length;
  const after = before
    .replace(/穿戴在(头部|胸部|腿部|脚部)的盔甲/g, (m, s) => '穿戴在' + SLOT[s] + '栏的盔甲')
    .replace(/头部|胸部|腿部|脚部/g, s => SLOT[s]);
  if (count) editor.setContent(after);
  if (abstract) {
    for (const size of ['32x', '128x']) {
      $('#icon-' + size).val('');
      $('#item-icon-' + size + '-preview-img').attr('src', '');
      const del = document.querySelector('#item-data-icon-' + size + '-delete');
      if (del && !del.checked) $(del).prop('checked', true).trigger('change');
    }
  }
  console.log('[tsr-slot] 「' + expected + '」已替换 ' + count + ' 处穿戴部位' + (abstract ? '，并勾选删除图标' : '') + '，请检查后点击「添加资料」→「确定提交」。');
})();
