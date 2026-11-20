#!/bin/zsh
# helpers around playwright-cli session "sa" (superadmin browser). usage: source ui.sh
S=<scratchpad>
cd $S/pw
FLASH_JS="JSON.stringify({url:location.href.replace(/^.*marlo-web/,''), actionMessages:[...document.querySelectorAll('.messages')].map(e=>e.innerText.replace(/\\s+/g,' ').trim()).filter(Boolean), errors:[...document.querySelectorAll('.alert-danger, .errorMessage, .actionError, .error')].map(e=>e.innerText.replace(/\\s+/g,' ').trim().slice(0,100)).filter(Boolean).slice(0,3)})"
pw() { playwright-cli -s=sa "$@"; }
flash() { pw --raw eval "$FLASH_JS"; }
ui_open() { pw goto "http://localhost:8080/marlo-web/superadmin/marloInstitutions.do" >/dev/null 2>&1; pw --raw eval "(document.querySelector(\".cc-dismiss\")||{click(){}}).click()" >/dev/null 2>&1; pw --raw eval "document.readyState + ' cards=' + document.querySelectorAll('.partnerRequestItem').length + ' offices=' + document.querySelectorAll('.officesRequestItem').length"; }
ui_accept() { # copy id
  ui_open >/dev/null
  pw run-code "async page => { await page.waitForFunction(() => window.jQuery && jQuery.isReady); await page.locator('#partnerRequestItem-$1 form.acceptRequestForm button[type=submit]').click({timeout:8000}); return 'clicked'; }" > run.out 2>&1
  grep -o '"confirm" dialog with message "[^"]*"' run.out | sed 's/^/  dialog shown: /'
  pw dialog-accept >/dev/null 2>&1; sleep 2; echo "  after accept: $(flash)"
}
ui_reject() { # copy id, justification, notify(true|false)
  ui_open >/dev/null
  pw run-code "async page => {
    await page.waitForFunction(() => window.jQuery && jQuery.isReady);
    const reqs = [];
    page.on('response', r => { if (r.url().includes('rejectPartnerRequest')) reqs.push(r.request().method() + ' ' + r.status()); });
    await page.locator('a.rejectRequest.partnerRequestId-$1').click();
    await page.waitForSelector('#myModal.in', {timeout: 8000});
    const box = page.locator('#myModal input.sendEmailInput');
    if ((await box.isChecked()) !== $3) { await page.locator('#myModal label[for=sendNotification], #myModal .sendEmailInput').first().click({force:true}); }
    const notify = await box.isChecked();
    await page.locator('#myModal textarea').fill('$2');
    await page.waitForTimeout(900); await page.locator('#myModal .rejectButton').click({force:true});
    await page.waitForTimeout(2500);
    return {notifyChecked: notify, requests: reqs, cardStillVisible: await page.locator('#partnerRequestItem-$1').isVisible()};
  }" > run.out 2>&1
  sed -n '/^### Result/,/^### Ran/p' run.out | sed '$d' | tr -d '\n'; echo
}
ui_office_reject() { # institution id, justification, notify(true|false), request ids...
  inst=$1; just=$2; notify=$3; shift 3
  ui_open >/dev/null
  checks=""; for id in "$@"; do checks="$checks await card.locator('#officeRequest-$id').check({force:true});"; done
  pw run-code "async page => {
    await page.waitForFunction(() => window.jQuery && jQuery.isReady);
    const card = page.locator('#officesRequestItem-$inst');
    $checks
    await card.locator('a.openRejectOfficeRequest').dispatchEvent('click');
    await page.waitForSelector('#rejectOfficeRequest.in', {timeout: 8000});
    await page.waitForTimeout(900);
    const box = page.locator('#rejectOfficeRequest input.sendEmailInput');
    if ((await box.isChecked()) !== $notify) { await page.locator('#rejectOfficeRequest label[for=sendNotification-offices], #rejectOfficeRequest .sendEmailInput').first().click({force:true}); }
    const notifyChecked = await box.isChecked();
    await page.locator('#rejectOfficeRequest textarea').fill('$just');
    await page.locator('#rejectOfficeRequest a.rejectOfficesRequest').dispatchEvent('click');
    await page.waitForTimeout(3500);
    return {notifyChecked, url: page.url().replace(/^.*marlo-web/,'')};
  }" > run.out 2>&1
  sed -n '/^### Result/,/^### Ran/p' run.out | sed '$d' | tr -d '\n'; echo
}
