(() => {
  const IDS = {
    '/item/add/32099-806981/': '红宝石套装',
    '/item/add/32099-806982/': '碳化硅套装',
    '/item/add/32099-806983/': '血石套装',
    '/item/add/32099-806985/': '哨兵套装',
    '/item/add/32099-806986/': '锈丝套装',
    '/item/add/32099-806987/': '奇异丝线套装',
    '/item/add/32099-806988/': '静谧套装',
    '/item/add/32099-806989/': '红宝石天顶套装',
    '/item/add/32099-806990/': '血石天顶套装',
    '/item/add/32099-807012/': '骨套具',
    '/item/add/32099-807013/': '碳化硅套具',
    '/item/add/32099-807014/': '红宝石套具',
    '/item/add/32099-807015/': '血石套具'
  };
  const expected = IDS[location.pathname];
  if (!expected) {
    console.warn('[tsr-icon] 当前页面不是 13 个套装/套具的再编辑页：', location.pathname);
    return;
  }
  const current = document.querySelector('input[placeholder="主要名称.."]').value;
  if (current !== expected) {
    console.warn('[tsr-icon] 主要名称不一致，已停止：', current, '≠', expected);
    return;
  }
  for (const size of ['32x', '128x']) {
    $('#icon-' + size).val('');
    $('#item-icon-' + size + '-preview-img').attr('src', '');
    const del = document.querySelector('#item-data-icon-' + size + '-delete');
    if (del && !del.checked) $(del).prop('checked', true).trigger('change');
  }
  console.log('[tsr-icon] 已清除「' + expected + '」的小图标和大图标，请确认后点击「完成添加」。');
})();
