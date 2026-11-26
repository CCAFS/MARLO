#!/bin/zsh
# T11b helpers (shot / opener / office / direct), concatenated for reference. <repo>, <scratchpad> stand for local paths.

### ---- shot.sh
# usage: shot.sh <session> <w> <h> <name|-> <full|fold> <url> <ctaSelector>
# Measures (before hiding any test-environment overlay): horizontal overflow, CTA fold/hit-test, banner state.
# Then hides the cookie banner + debug panel (test-environment artefacts) and screenshots.
P=<repo>/docs/specs/changes/revive-local-partner-request-approval/probes
SS=$1; W=$2; H=$3; NAME=$4; MODE=$5; URL=$6; CTA=$7
playwright-cli -s=$SS resize $W $H >/dev/null 2>&1; playwright-cli -s=$SS goto "$URL" >/dev/null 2>&1
playwright-cli -s=$SS run-code "async page => {
  await page.waitForFunction(() => window.jQuery && jQuery.isReady); await page.evaluate(() => document.fonts.ready); await page.waitForTimeout(2500);
  const m = await page.evaluate((sel) => {
    const r = {vw: innerWidth, vh: innerHeight, scrollWidth: document.documentElement.scrollWidth, clientWidth: document.documentElement.clientWidth};
    r.overflowX = r.scrollWidth > r.clientWidth;
    const ban = document.querySelector('.cc-window'); const bs = ban && getComputedStyle(ban); const bb = ban && ban.getBoundingClientRect();
    r.banner = ban ? (bs.display!=='none' && bs.visibility!=='hidden' && bb.height>0 ? 'visible y=' + Math.round(bb.top) + '..' + Math.round(bb.bottom) : 'not rendered (hidden)') : 'not rendered (absent)';
    const cta = sel ? document.querySelector(sel) : null;
    if (!cta) { r.cta = 'no CTA found'; return r; }
    const cs = getComputedStyle(cta); const b = cta.getBoundingClientRect();
    r.ctaBox = [Math.round(b.left), Math.round(b.top), Math.round(b.width), Math.round(b.height)];
    if (cs.display==='none' || cs.visibility==='hidden' || +cs.opacity===0 || b.width===0 || b.height===0) { r.cta = 'not visible'; return r; }
    const cx = b.left + b.width/2, cy = b.top + b.height/2;
    if (cy >= innerHeight) { r.cta = 'below fold (y=' + Math.round(b.top) + ')'; return r; }
    const el = document.elementFromPoint(cx, cy);
    r.cta = (el && (cta===el || cta.contains(el))) ? 'pass' : 'covered (' + (el ? el.tagName + '.' + (el.className||'').toString().slice(0,40) : 'none') + ')';
    return r; }, '$CTA');
  return m; }" 2>&1 | sed -n '/^### Result/,/^### Ran/p' | sed '$d' | tail -n +2 | tr -d '\n'; echo
if [ "$NAME" != "-" ]; then
  playwright-cli -s=$SS run-code "async page => { await page.addStyleTag({content: '.cc-window,#debugPanel{display:none!important}'}); await page.waitForTimeout(300); return 'ok'; }" >/dev/null 2>&1
  if [ "$MODE" = "full" ]; then playwright-cli -s=$SS screenshot --full-page --filename=$P/$NAME >/dev/null 2>&1; else playwright-cli -s=$SS screenshot --filename=$P/$NAME >/dev/null 2>&1; fi
  echo "shot $NAME ($MODE)"
fi

### ---- opener.sh
# usage: opener.sh <session> <page url> <orgname> <acronym> [shotname] : opens page, clicks first a.popup[partnerSave], fills + submits the popup
P=<repo>/docs/specs/changes/revive-local-partner-request-approval/probes
SHOT=${5:-}
playwright-cli -s=$1 resize 1280 800 >/dev/null; playwright-cli -s=$1 goto "$2" >/dev/null 2>&1
playwright-cli -s=$1 run-code "async page => {
  await page.waitForFunction(() => window.jQuery && jQuery.isReady); await page.waitForTimeout(2500);
  await page.addStyleTag({content: '.cc-window,#debugPanel{display:none!important}'});
  const links = page.locator('a.popup[href*=partnerSave]'); const count = await links.count();
  if (!count) { return {openerPageLinks: 0, title: await page.title(), url: page.url().replace(/^.*marlo-web/,'')}; }
  const href = await links.first().getAttribute('href');
  const [pop] = await Promise.all([page.context().waitForEvent('page'), links.first().evaluate(a => a.click())]);
  await pop.waitForLoadState('load');
  await pop.waitForFunction(() => window.jQuery && jQuery.isReady && document.querySelector('.select2-container'));
  await pop.waitForTimeout(800);
  await pop.addStyleTag({content: '.cc-window,#debugPanel{display:none!important}'});
  const info = await pop.evaluate(() => ({title: document.title, hidden: [...document.querySelectorAll('form input[type=hidden]')].map(i => i.name + '=' + i.value.slice(0,30)).filter(s => !/^(struts|__)/.test(s))}));
  await pop.locator('input[name=\"activityPartner.partner.name\"]').fill('$3'); await pop.locator('input[name=\"activityPartner.partner.acronym\"]').fill('$4');
  await pop.locator('#partnerTypes .select2-selection').click(); await pop.waitForSelector('.select2-results__option'); await pop.locator('.select2-results__option').nth(3).click();
  await pop.locator('#partnerCountry .select2-selection').click(); await pop.keyboard.type('Kenya'); await pop.waitForTimeout(300); await pop.keyboard.press('Enter');
  await pop.locator('form.prq-request__form button[type=submit]').click(); await pop.waitForTimeout(3500);
  await pop.addStyleTag({content: '.cc-window,#debugPanel{display:none!important}'});
  const after = await pop.evaluate(() => ({url: location.pathname + location.search, success: (document.querySelector('.prq-success__title')||{}).innerText, successText: (document.querySelector('.prq-success')||{}).innerText, danger: (document.querySelector('.prq-note--danger')||{}).innerText}));
  if ('$SHOT') { await pop.screenshot({path: '$P/$SHOT', fullPage: true}); }
  await pop.close();
  return {openerPageLinks: count, href: href.replace(/^.*marlo-web/, '').slice(0,160), popup: info, after};
}" 2>&1 | sed -n '/^### Result/,/^### Ran/p' | sed '$d' | tail -n +2

### ---- office.sh
# usage: office.sh <session> <institutionID> <ISO,ISO,...> : the real requestCountryOffice.do call, from the logged-in project page
playwright-cli -s=$1 goto "http://localhost:8080/marlo-web/clusters/AICCRA/partners.do?projectID=102076&edit=true&phaseID=403" >/dev/null 2>&1
playwright-cli -s=$1 run-code "async page => {
  await page.waitForFunction(() => window.jQuery && jQuery.isReady); await page.waitForTimeout(1500);
  const r = await page.evaluate(async () => { const d = {projectID: 102076, institutionID: $2, countriesID: '$3'.split(',')};
    return await new Promise(res => jQuery.ajax({url: baseURL + '/requestCountryOffice.do', data: d, traditional: true, success: x => res(x), error: (a,b,c) => res({err: a.status})})); });
  return r; }" 2>&1 | sed -n '/^### Result/,/^### Ran/p' | sed '$d' | tail -n +2

### ---- direct.sh
# usage: direct.sh <session> <popup url path+query> <orgname> <acronym> : the popup URL the opener would link to, opened directly, filled and submitted
playwright-cli -s=$1 resize 1280 800 >/dev/null; playwright-cli -s=$1 goto "http://localhost:8080/marlo-web$2" >/dev/null 2>&1
playwright-cli -s=$1 run-code "async page => {
  await page.waitForFunction(() => window.jQuery && jQuery.isReady && document.querySelector('.select2-container')); await page.waitForTimeout(800);
  await page.addStyleTag({content: '.cc-window,#debugPanel{display:none!important}'});
  const info = await page.evaluate(() => ({title: document.title, hidden: [...document.querySelectorAll('form input[type=hidden]')].map(i => i.name + '=' + i.value.slice(0,30)).filter(s => !/^(struts|__)/.test(s) && !/=0\$|=\$/.test(s))}));
  await page.locator('input[name=\"activityPartner.partner.name\"]').fill('$3'); await page.locator('input[name=\"activityPartner.partner.acronym\"]').fill('$4');
  await page.locator('#partnerTypes .select2-selection').click(); await page.waitForSelector('.select2-results__option'); await page.locator('.select2-results__option').nth(3).click();
  await page.locator('#partnerCountry .select2-selection').click(); await page.keyboard.type('Kenya'); await page.waitForTimeout(300); await page.keyboard.press('Enter');
  await page.locator('form.prq-request__form button[type=submit]').click(); await page.waitForTimeout(3500);
  const after = await page.evaluate(() => ({success: (document.querySelector('.prq-success__title')||{}).innerText, danger: (document.querySelector('.prq-note--danger')||{}).innerText}));
  return {info, after}; }" 2>&1 | sed -n '/^### Result/,/^### Ran/p' | sed '$d' | tail -n +2
