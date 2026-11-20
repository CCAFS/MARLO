#!/bin/zsh
# usage: dlg.sh <w> <h> <name> <kind: reject|offices>
S=<scratchpad>
P=<repo>/docs/specs/changes/revive-local-partner-request-approval/probes
playwright-cli -s=sa resize $1 $2 >/dev/null; playwright-cli -s=sa goto http://localhost:8080/marlo-web/superadmin/marloInstitutions.do >/dev/null 2>&1
if [ "$4" = reject ]; then OPEN="await page.locator('[data-prq-action=\"reject\"]').first().click();"; else OPEN="await page.locator('.officesRequestItem input[type=checkbox]').first().check({force:true}); await page.locator('[data-prq-action=\"reject-offices\"]').first().click();"; fi
playwright-cli -s=sa run-code "async page => { await page.waitForFunction(() => window.jQuery && jQuery.isReady); await page.waitForTimeout(1200);
 await page.addStyleTag({content: '.cc-window,#debugPanel{display:none!important}'});
 $OPEN await page.waitForTimeout(800);
 const st = await page.evaluate(() => { const vis=[...document.querySelectorAll('button[data-prq-confirm]')].filter(e=>e.offsetParent); const e=vis[0]; if(!e) return 'none'; const c=getComputedStyle(e); return {cls:e.className,bg:c.backgroundColor,color:c.color}; });
 await page.screenshot({path: '$P/$3'}); return st; }" 2>&1 | sed -n '/^### Result/,/^### Ran/p' | sed '$d'
