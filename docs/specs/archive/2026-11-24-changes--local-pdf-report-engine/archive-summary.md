# Archive Summary — Local PDF Report Engine

## 1. Document Control

| Field | Value |
|---|---|
| Spec ID | CHG-LOCAL-PDF-REPORT-ENGINE-001 |
| Original path | `docs/specs/changes/local-pdf-report-engine/` |
| Archive path | `docs/specs/archive/2026-11-24-changes--local-pdf-report-engine/` |
| Archive date | 2026-11-24 |
| Branch | `feature/local-pdf-report-engine` (from `staging` at `2f67c5d2ab`), HEAD `fb29e29712` |
| Owner | Kenji Tanaka |

## 2. Final Status

**Delivered and validated (PASS: 0 FAIL, 0 open WARN).**

With the specificity `local_pdf_report_engine_active` on, MARLO renders the OICR, Innovation and Cluster summary PDFs **entirely by itself**. It does not use the reports microservice, RabbitMQ, S3 or CLARISA, and it makes no external network request. The output is identical to production: measured parity and a 0.000 % pixel difference on fixtures and real data, including a 481-page cluster report.

The microservice path is untouched while the flag is off. Retiring it is the follow-up spec (T08).

## 3. Requirements Delivered

| Area | Delivered |
|---|---|
| FN-001–FN-008 | Local rendering at all four call sites behind the flag. Same payload. The 17 helpers produce HTML byte-identical to Handlebars.js. The html-pdf page model comes from its own page script. A template-subset guard is in place. The flag off means the microservice is used, exactly as today |
| NF-001 | Measured parity: same pages, same words per page, ±3 pt, same text, 0.000 % pixel diff |
| NF-002–NF-007 | DejaVu fonts plus Debian fontconfig rules, isolated. Pool of 3 and queue of 6. Deadlines of 30 s and 50 s. No dependency drift; OpenHTMLtoPDF retired. PhantomJS 2.1.1 ships inside MARLO, SHA-256 verified, with a `pdf.phantomjs.path` override |
| DA-001 / DA-002 | Specificity migration (`'false'` for every unit) and constants in both `APConstants`; templates untouched |
| SEC-001–SEC-004 | Empty allowlist. Sanitizer covering scripts, handlers, embedded documents, foreign content, `noembed` and `template`. Per-render temp directory; process killed at the deadline. Generic bare 500 |
| OPS-001 | One INFO line per render; ERROR on failure |

## 4. Files Changed Summary

The spec has 17 commits (`c5fbf857d0` … `fb29e29712`); full per-task lists are in `tasks.md`.

| Area | Files |
|---|---|
| Engine | `marlo-web/.../utils/pdf/`: `LocalPdfReportService`, `PhantomJsRenderer`, `EmbeddedPhantomJs`, `PdfImageBundle`, `StandaloneLines`, `HandlebarsReportHelpers`, `ReportTemplateSubset`, `PdfRenderException` |
| Resources | `pdf/render-page.js` (html-pdf 3.0.1, MIT); `pdf/phantomjs/` (26 MB gzip binary plus BSD, LGPL, source and provenance files); `pdf/fontconfig/` (31 Debian rules); `pdf/fonts/` (6 DejaVu 2.37); `pdf/images/` (309 template images and manifest) |
| Call sites | `BaseStudySummaryData`, `ProjectInnovationSummaryAction`, `ReportingSummaryAction` (setter injection) |
| Configuration and data | `APConfig` (`pdf.phantomjs.path`), `marlo-test.properties` (empty key), both `APConstants`, migration `V2_6_0_20261120_2222__AddLocalPdfReportEngineSpecificity.sql`, poms (PDFBox test-scoped) |
| Tests | 10 classes in `utils/pdf` plus `LocalPdfEngineDispatchTest`; vectors (2,817 helper and 7,712 subset rows); expected HTML |
| Evidence | `spike/parity/` (production pipeline harness and comparator); `spike/helper-vectors-generator/`; `spike/t11-discarded-emulations.diff` |

## 5. Test Evidence Summary

| Evidence | Result |
|---|---|
| Full suite (`mvn -o -pl marlo-utils,marlo-data,marlo-web -am test`) | 1,458 tests, 0 failures, 8 skipped. The skipped tests are Linux-only or gated on `PHANTOMJS_PATH` |
| Gated `PhantomJsRenderingTest` on Linux (`tomcat:9.0-jdk17-temurin`, `--network none`) | OK, 7 tests, including the test that uses the shipped binary |
| Measured parity | 8 cases (3 fixtures, 3 real reports, 2 probes): PARITY and a 0.000 % pixel diff |
| Security probe (spy server) | 14 hostile payloads, 0 hits |
| Live wiring on the developer Mac | 3 downloads `outcome=ok`, no microservice call |

No `test-report.md` exists; `/akili-test` was not run. This was accepted in the validation report.

## 6. Validation Summary

- **First pass (2026-11-23, before Pivot 2):** 1 FAIL and 7 WARN, all remediated in T10.
- **Final pass (2026-11-24):** 2 FAIL and 12 WARN.
- **Re-check:** all 14 findings closed, confirmed by an independent auditor. Verdict PASS.

Details are in `validation-report.md` §13.

## 7. Accepted Warnings and Follow-Ups

| Item | Owner / home |
|---|---|
| **OQ-4 (before enabling the flag anywhere):** confirm production runs Linux x86_64 with `libfontconfig`/`libfreetype` (MARLO's Docker base has them), with an exec-allowed temp dir, and that PhantomJS network egress can be blocked | Infrastructure team |
| **Accepted risk:** PhantomJS 2.1.1 is unmaintained (outdated QtWebKit/OpenSSL). The sanitizer is the only barrier against script, because WebSockets bypass the page hook | design §18.3; OQ-4 egress control |
| Retire the microservice path, RabbitMQ and the `microservice.*` keys (T08) | Follow-up spec `changes/retire-reports-microservice` |
| TRD §7 and an ADR for the local engine; `.agents` persona drift | Pending items applied on `staging` (kaizen entry) |
| Carried advisories (T02 helper quirks, T06 `loadData()` with the flag on, double ERROR on submit, and others) | `validation-report.md` §7 |
| Throwaway database `lpre_live_tmp` (local only) | Owner: drop it when no longer needed |

## 8. Historical Notes

- **Pivot 1 (T02):** helper parity was scoped to a supported template subset, enforced by a guard (FN-008).
- **NF-001 raised (2026-11-23):** the owner asked that reports "se vean igual que en producción". Measured parity replaced reviewer acceptance.
- **Pivot 2 (T11 → option B):** emulating WebKit inside OpenHTMLtoPDF matched the fixtures but not real data. The cluster report came out at 478 pages against 481. The engine became PhantomJS itself, proven first with a throwaway prototype (PARITY 6/6).
- **"Everything from MARLO" (2026-11-24):** T16 ships the binary, T17 and T18 bundle every template image. No Dockerfile or server change is needed.
- **Defects found by measuring on the target platform rather than on macOS:**
  - the OpenSSL config parse on Ubuntu;
  - the minimal fontconfig breaking parity;
  - a sanitizer bypass through `noembed`;
  - PDFBox tab extraction.
- **Finding about production today:** the microservice renders unsanitized rich text with JavaScript enabled. An `<img onerror>` exfiltration works there (Control A in `execution.md`). Enabling the local engine closes it.

## 9. Diagrams

The spec has no `diagrams/` folder. Design §2 holds the original mermaid overview, marked superseded by §18. §18.1 has a text pipeline diagram.

Follow-up: a baseline diagram of the reports integration belongs to the TRD §7 pending item.
