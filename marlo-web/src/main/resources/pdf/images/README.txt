Report template images shipped inside MARLO
===========================================

What this is
  The images the three production report templates (oicr, innovation, project) and the summary actions load from the
  S3 bucket marlo-pdf-resources-dev (https://marlo-pdf-resources-dev.s3.us-east-1.amazonaws.com/). They are stored here
  at the bucket's own key paths, and PdfImageBundle replaces each bucket URL in a page with a data: URI of these bytes
  before PhantomJS renders it, so a render never reads the bucket (CHG-LPRE-T17, Decision Log "Everything from MARLO").
  manifest.tsv lists every object: key path, size, SHA-256 and the URL it was downloaded from. The application
  refuses bytes that do not match their manifest hash; PdfImageBundleTest checks the manifest against the templates.

Retrieved
  2026-11-24, once each, over https with curl. The bucket cannot be listed (AccessDenied), so the set was derived from
  the references: the static names in the templates, SDG icons E-WEB-Goal-1..17.png (ReportingSummaryAction), impact
  area icons ICONS CGIAR-1..5.png (st_impact_areas ids 1..5), and country flags flags/<iso alpha-2>.svg for every ISO
  3166-1 alpha-2 code plus xk and every iso_alpha_2 value of loc_elements in a local copy of the MARLO database.
  Result: 298 objects (48 PNG, 250 SVG), 3,588,059 bytes.

Objects that do not exist (HTTP 403 from the bucket, so production shows a broken image for them)
  The iso_alpha_2 column of loc_elements also holds non-country codes (regions and sites). The bucket has no flag for:
    flags/ba04.svg
    flags/bf01.svg
    flags/bfa0101.svg
    flags/ca01.svg
    flags/co01.svg
    flags/et01.svg
    flags/gh01.svg
    flags/gha0101.svg
    flags/gu01.svg
    flags/ho01.svg
    flags/in16.svg
    flags/in17.svg
    flags/ke01.svg
    flags/ke02.svg
    flags/kmh0101.svg
    flags/la01.svg
    flags/lao0101.svg
    flags/lao0102.svg
    flags/ma01.svg
    flags/ne03.svg
    flags/ne04.svg
    flags/ne05.svg
    flags/ner0101.svg
    flags/ni01.svg
    flags/nic01.svg
    flags/se01.svg
    flags/sen0102.svg
    flags/tz01.svg
    flags/ug01.svg
    flags/ug02.svg
    flags/vn01.svg
    flags/vnm0101.svg
    flags/vnm0102.svg
    flags/vnm0103.svg
  and no impact-area icon for ids 6..8 (probed; the database holds ids 1..5).
  A reference to one of these is replaced by an undecodable PNG data URI and logged at WARN the first time per process (then DEBUG) (name only): the image
  stays broken, as in production, and nothing is requested.

Ownership and licences
  - flags/*.svg: the flag-icons set (https://github.com/lipis/flag-icons); every file carries
    id="flag-icons-<code>". The release version is not recorded in the files. Its licence, reproduced as it
    requires, is the MIT licence:

      The MIT License (MIT)

      Copyright (c) 2013 Panayiotis Lipiridis

      Permission is hereby granted, free of charge, to any person obtaining a copy
      of this software and associated documentation files (the "Software"), to deal
      in the Software without restriction, including without limitation the rights
      to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
      copies of the Software, and to permit persons to whom the Software is
      furnished to do so, subject to the following conditions:

      The above copyright notice and this permission notice shall be included in all
      copies or substantial portions of the Software.

      THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
      IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
      FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
      AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
      LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
      OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
      SOFTWARE.

  - sdg/E-WEB-Goal-*.png: the United Nations Sustainable Development Goals icons. They are UN material used under the
    UN's guidelines for the SDG icons (https://www.un.org/sustainabledevelopment/news/communications-material/);
    keep them unmodified, with no UN endorsement implied.
  - everything else (AICCRA-logo.png, cover and header artwork, year/code/status/... icons, DAC marker icons,
    innovation and impact-area icons, score icons): CGIAR / AICCRA / MARLO assets, the same files MARLO has been
    serving to its own reports. The impact-area icons are the CGIAR impact area icons.
  No licence file ships inside the bucket; these notes record what could be established from the files and the
  repository. The owner of the CGIAR/AICCRA brand assets should confirm them before this directory is pushed.

Adding or changing an image
  Put the file under its bucket key path, add its row to manifest.tsv (size and SHA-256 of the file as stored), and
  run PdfImageBundleTest. An image the templates reference but this directory lacks renders as a broken image.
