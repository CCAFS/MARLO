#!/usr/bin/env python3
"""Layout parity of two PDFs (production vs local) by word positions.

Criterion (requirements NF-001 as amended 2026-11-23):
  1. same page count;
  2. same words on every page (the page breaks fall in the same place);
  3. every matched word within +/-TOL pt of its production position (x and y);
  4. the same extracted text: a word that differs only by a ligature glyph (fi -> U+FB01) counts as a text
     difference (search and copy-paste see it), reported separately; words are aligned on their NFKC form.
Words matching --ignore (regex, e.g. the generation timestamp) are left out of both sides.
Usage: compare.py prod.pdf local.pdf [--tol 3] [--ignore REGEX] [--json out.json]
Exit 0 = parity, 1 = differences (printed per page).
"""
import argparse
import difflib
import html
import json
import re
import subprocess
import sys
import unicodedata

WORD = re.compile(r'<word xMin="([\d.]+)" yMin="([\d.]+)" xMax="([\d.]+)" yMax="([\d.]+)">(.*?)</word>')


def pages(pdf, ignore):
    out = subprocess.run(['pdftotext', '-bbox', pdf, '-'], capture_output=True, check=True).stdout.decode('utf-8')
    result = []
    for chunk in out.split('<page ')[1:]:
        words = []
        for x0, y0, x1, y1, text in WORD.findall(chunk):
            text = html.unescape(text)
            if ignore and ignore.search(text):
                continue
            words.append((text, float(x0), float(y0)))
        result.append(words)
    return result


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('prod')
    ap.add_argument('local')
    ap.add_argument('--tol', type=float, default=3.0)
    ap.add_argument('--ignore', default=None)
    ap.add_argument('--json', default=None)
    ap.add_argument('--show', type=int, default=4, help='offending words to print per page')
    a = ap.parse_args()
    ignore = re.compile(a.ignore) if a.ignore else None
    prod, local = pages(a.prod, ignore), pages(a.local, ignore)
    report = {'prodPages': len(prod), 'localPages': len(local), 'pages': []}
    ok = len(prod) == len(local)
    print(f'pages: production {len(prod)}, local {len(local)}' + ('' if ok else '  <-- DIFFERENT'))
    for i in range(max(len(prod), len(local))):
        pw = prod[i] if i < len(prod) else []
        lw = local[i] if i < len(local) else []
        nf = lambda t: unicodedata.normalize('NFKC', t)
        sm = difflib.SequenceMatcher(a=[nf(w[0]) for w in pw], b=[nf(w[0]) for w in lw], autojunk=False)
        same_words = sm.ratio() == 1.0
        moved = []
        ligatures = []
        for tag, i1, i2, j1, j2 in sm.get_opcodes():
            if tag == 'equal':
                for k in range(i2 - i1):
                    p, l = pw[i1 + k], lw[j1 + k]
                    if p[0] != l[0]:
                        ligatures.append(l[0])
                    dx, dy = l[1] - p[1], l[2] - p[2]
                    if abs(dx) > a.tol or abs(dy) > a.tol:
                        moved.append((p[0], round(dx, 1), round(dy, 1)))
        missing = [pw[k][0] for t, i1, i2, j1, j2 in sm.get_opcodes() if t in ('delete', 'replace') for k in range(i1, i2)]
        extra = [lw[k][0] for t, i1, i2, j1, j2 in sm.get_opcodes() if t in ('insert', 'replace') for k in range(j1, j2)]
        page_ok = same_words and not moved and not ligatures
        ok = ok and page_ok
        report['pages'].append({'page': i + 1, 'prodWords': len(pw), 'localWords': len(lw), 'sameWords': same_words,
                                'moved': len(moved), 'ligatures': len(ligatures), 'missing': missing[:50], 'extra': extra[:50],
                                'movedSample': moved[:50]})
        if not page_ok:
            print(f'page {i + 1}: words prod={len(pw)} local={len(lw)} same={same_words} moved>{a.tol}pt={len(moved)}'
                  f' ligatures={len(ligatures)}')
            if missing:
                print('   only in production:', ' '.join(missing[:12]) + (' ...' if len(missing) > 12 else ''))
            if extra:
                print('   only in local     :', ' '.join(extra[:12]) + (' ...' if len(extra) > 12 else ''))
            for w in moved[:a.show]:
                print(f'   moved {w[0]!r}: dx={w[1]} dy={w[2]}')
    report['parity'] = ok
    if a.json:
        with open(a.json, 'w') as f:
            json.dump(report, f, indent=1)
    print('PARITY' if ok else 'NO PARITY')
    sys.exit(0 if ok else 1)


if __name__ == '__main__':
    main()
