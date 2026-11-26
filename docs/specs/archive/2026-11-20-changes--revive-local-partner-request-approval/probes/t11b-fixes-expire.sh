#!/bin/zsh
S=<scratchpad>
P=<repo>/docs/specs/changes/revive-local-partner-request-approval/probes
playwright-cli -s=sa resize 1280 800 >/dev/null; playwright-cli -s=sa goto http://localhost:8080/marlo-web/superadmin/marloInstitutions.do >/dev/null 2>&1
playwright-cli -s=sa run-code "async page => { await page.waitForFunction(() => window.jQuery && jQuery.isReady); await page.waitForTimeout(1200);
 await page.addStyleTag({content: '.cc-window,#debugPanel{display:none!important}'});
 const resp = []; page.on('response', r => { if (r.request().method()==='POST') resp.push(r.status() + ' ' + r.url().replace(/^.*marlo-web/,'').split('?')[0]); });
 await page.locator('[data-prq-action=\"reject\"]').first().click(); await page.waitForTimeout(700);
 await page.locator('dialog[open] textarea, .prq-dialog textarea').first().fill('Probe justification'); 
 await page.context().clearCookies();
 await page.locator('button[data-prq-confirm]:visible').first().click(); await page.waitForTimeout(2500);
 const note = await page.evaluate(() => [...document.querySelectorAll('.prq-note')].map(n => n.className + ' | ' + n.innerText.trim()));
 const hasCard = await page.locator('.partnerRequestItem').count();
 await page.screenshot({path: '$P/$1'}); return {resp, note, hasCard}; }" 2>&1
