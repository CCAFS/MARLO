/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.utils.pdf;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Proves the subset guard of the local PDF engine (local-pdf-report-engine FN-008, design.md section 7.1): every
 * construct outside the supported template subset is reported, the supported ones are not, and the three production
 * templates (copied from <code>report_configurations</code>) are inside the subset. The table-driven test compares the
 * guard with <code>subset-vectors.json</code>, whose expected categories were produced by the Handlebars.js 4.7.8 AST
 * (generator: <code>spike/helper-vectors-generator/subset-generate.js</code> in the spec folder).
 */
public class ReportTemplateSubsetTest {

  private static String readTemplate(String name) throws Exception {
    return readResource("/pdf/templates/" + name + ".html");
  }

  private static String readResource(String resource) throws Exception {
    InputStream stream = ReportTemplateSubsetTest.class.getResourceAsStream(resource);
    assertNotNull(resource + " is missing from the test classpath", stream);
    try {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      byte[] buffer = new byte[8192];
      for (int read = stream.read(buffer); read >= 0; read = stream.read(buffer)) {
        bytes.write(buffer, 0, read);
      }
      return new String(bytes.toByteArray(), "UTF-8");
    } finally {
      stream.close();
    }
  }

  /** Asserts that exactly one construct is reported, that it is named <code>construct</code> and quotes the tag. */
  private static void assertFlagged(String construct, String excerpt, String template) {
    List<String> found = ReportTemplateSubset.findUnsupported(template);
    assertEquals("findings for " + template + ": " + found, 1, found.size());
    assertTrue(found.get(0) + " should name <" + construct + ">", found.get(0).startsWith(construct + ":"));
    assertTrue(found.get(0) + " should quote <" + excerpt + ">", found.get(0).contains(excerpt));
  }

  private static void assertSupported(String template) {
    List<String> found = ReportTemplateSubset.findUnsupported(template);
    assertTrue("expected no findings for " + template + " but got " + found, found.isEmpty());
  }

  @Test
  public void flagsParentPaths() {
    assertFlagged("parent path", "{{../n}}", "{{#each xs}}{{../n}}{{/each}}");
    assertFlagged("parent path", "{{#ifEquals a ../b}}", "{{#each xs}}{{#ifEquals a ../b}}x{{/ifEquals}}{{/each}}");
    assertFlagged("parent path", "{{../../n}}", "{{#each a}}{{#each b}}{{../../n}}{{/each}}{{/each}}");
  }

  @Test
  public void flagsBlockParameters() {
    assertFlagged("block parameters", "{{#each xs as |x|}}", "{{#each xs as |x|}}{{x}}{{/each}}");
    assertFlagged("block parameters", "{{#each m as |v k|}}", "{{#each m as |v k|}}{{k}}{{/each}}");
  }

  @Test
  public void flagsPartialsAndInlinePartials() {
    assertFlagged("partial", "{{> footer}}", "a{{> footer}}b");
    assertFlagged("partial", "{{>footer}}", "{{>footer}}");
    assertFlagged("partial", "{{#> layout}}", "{{#> layout}}x{{/layout}}");
    assertFlagged("partial", "{{#*inline \"p\"}}", "{{#*inline \"p\"}}x{{/inline}}");
  }

  @Test
  public void flagsOddAndEvenDataVariables() {
    assertFlagged("data variable @odd", "{{@odd}}", "{{#each xs}}{{@odd}}{{/each}}");
    assertFlagged("data variable @even", "{{#if @even}}", "{{#each xs}}{{#if @even}}x{{/if}}{{/each}}");
  }

  @Test
  public void flagsPathSegmentsNamedNull() {
    assertFlagged("path segment named null", "{{x.null}}", "{{x.null}}");
    assertFlagged("path segment named null", "{{this.null}}", "{{#each xs}}{{this.null}}{{/each}}");
    assertFlagged("path segment named null", "{{a.null.b}}", "{{a.null.b}}");
    assertFlagged("path segment named null", "{{x.[null]}}", "{{x.[null]}}");
  }

  @Test
  public void flagsIfAndUnlessWithoutAnArgument() {
    assertFlagged("if without argument", "{{#if}}", "{{#if}}x{{/if}}");
    assertFlagged("unless without argument", "{{#unless}}", "{{#unless}}x{{/unless}}");
    assertFlagged("if without argument", "{{#if includeZero=true}}", "{{#if includeZero=true}}x{{/if}}");
    assertFlagged("if without argument", "{{else if}}", "{{#if a}}x{{else if}}y{{/if}}");
    assertFlagged("if without argument", "{{# if}}", "{{# if}}x{{/if}}");
    assertFlagged("if without argument", "{{#if includeZero = true}}", "{{#if includeZero = true}}x{{/if}}");
    assertFlagged("if without argument", "{{^if}}", "{{^if}}x{{/if}}");
    assertFlagged("unless without argument", "{{^unless}}", "{{^unless}}x{{/unless}}");
  }

  @Test
  public void reportsEveryFindingOnceInTemplateOrder() {
    List<String> found = ReportTemplateSubset.findUnsupported("{{#each xs}}{{../n}}{{> p}}{{@odd}}{{/each}}");
    assertEquals(found.toString(), 3, found.size());
    assertTrue(found.get(0).startsWith("parent path:"));
    assertTrue(found.get(1).startsWith("partial:"));
    assertTrue(found.get(2).startsWith("data variable @odd:"));
  }

  @Test
  public void acceptsTheLiteralNullArgumentAndOtherSupportedConstructs() {
    assertSupported("{{#ifEquals a null}}x{{/ifEquals}}");
    assertSupported("{{#ifCond a \"===\" null}}x{{else}}y{{/ifCond}}");
    assertSupported("{{#if a}}x{{else if b}}y{{else}}z{{/if}}");
    assertSupported("{{#unless a}}x{{/unless}}{{#if (or a b)}}y{{/if}}{{#if a includeZero=true}}z{{/if}}");
    assertSupported("{{# if x}}a{{/if}}{{#if x includeZero = true}}b{{/if}}{{^if x}}c{{/if}}");
    assertSupported("{{#if a}}d{{else if x}}e{{/if}}{{#unless a}}f{{else unless b}}g{{/unless}}");
    assertSupported("{{#items}}a{{/items}}{{^items}}none{{/items}}{{^iffy}}x{{/iffy}}");
    assertSupported("{{#each xs}}{{@index}}{{@key}}{{@first}}{{@last}}{{@root.n}}{{this.a.b}}{{./c}}{{/each}}");
    assertSupported("{{#with o}}{{name}}{{/with}}{{{raw}}}{{~trim~}}");
    assertSupported("{{#ifEquals a \"../x\"}}x{{/ifEquals}}{{inList \"x.null\" \"@odd, {{> p}}, as |x|\"}}");
    assertSupported("<a href=\"../up\">null.x @odd as |x| {{x}}</a>");
  }

  @Test
  public void ignoresHandlebarsComments() {
    assertSupported("{{!-- {{../n}} {{> p}} {{@odd}} {{#if}} as |x| x.null --}}");
    assertSupported("{{! note on ../n and @even and {{#if}} }}{{! {{../n}} }}");
    assertSupported("{{~!-- {{../n}} --~}}");
    assertFlagged("parent path", "{{../n}}", "{{!-- {{../a}} --}}{{#each xs}}{{../n}}{{/each}}");
  }

  @Test
  public void ignoresEscapedMustaches() {
    assertSupported("literal \\{{../n}} text");
  }

  @Test
  public void excerptsAreShort() {
    StringBuilder tag = new StringBuilder("{{../");
    for (int i = 0; i < 200; i++) {
      tag.append('a');
    }
    tag.append("}}");
    List<String> found = ReportTemplateSubset.findUnsupported(tag.toString());
    assertEquals(1, found.size());
    assertTrue(found.get(0), found.get(0).length() < 120);
  }

  @Test
  public void flagsRawBlocksBecauseTheEngineCannotStripStandaloneLinesAroundThem() throws Exception {
    assertFlagged("raw block", "{{{{raw}}}}", "a\n{{{{raw}}}}\n{{x}}\n{{{{/raw}}}}\nb");
    assertTrue(readTemplate("oicr").indexOf("{{{{") < 0);
    assertTrue(readTemplate("innovation").indexOf("{{{{") < 0);
    assertTrue(readTemplate("project").indexOf("{{{{") < 0);
  }

  @Test
  public void flagsAnInverseBlockWithAnElseWhichHandlebarsJavaCannotCompile() {
    assertFlagged("inverse block with else", "{{^x}}", "{{^x}}a{{else}}b{{/x}}");
    assertFlagged("inverse block with else", "{{^x}}", "{{^x}}a{{^}}b{{/x}}");
    assertFlagged("inverse block with else", "{{^x}}", "{{^x}}a{{else y}}b{{/x}}");
    assertSupported("{{^x}}a{{/x}}");
    assertSupported("{{#x}}a{{else}}b{{/x}}{{#y}}a{{^}}b{{/y}}");
    assertSupported("{{^x}}{{#y}}a{{else}}b{{/y}}{{/x}}");
  }

  @Test
  public void productionTemplatesAreInsideTheSubset() throws Exception {
    for (String name : new String[] {"oicr", "innovation", "project"}) {
      String template = readTemplate(name);
      assertTrue(name + ".html is unexpectedly small", template.length() > 10000);
      assertTrue(name + ".html carries no mustache", template.contains("{{"));
      List<String> found = ReportTemplateSubset.findUnsupported(template);
      assertTrue(name + ".html uses constructs outside the supported subset: " + found, found.isEmpty());
    }
  }

  @Test
  public void aProductionTemplateEditedWithAParentPathIsFlagged() throws Exception {
    String edited = readTemplate("oicr") + "{{#each xs}}{{../x}}{{/each}}";
    List<String> found = ReportTemplateSubset.findUnsupported(edited);
    assertEquals(found.toString(), 1, found.size());
    assertTrue(found.get(0), found.get(0).startsWith("parent path:"));
  }

  /** The categories the guard reports for a template: the construct names, without the quoted tags. */
  private static List<String> categoriesOf(String template) {
    TreeSet<String> categories = new TreeSet<String>();
    for (String finding : ReportTemplateSubset.findUnsupported(template)) {
      categories.add(finding.substring(0, finding.indexOf(':')));
    }
    return new ArrayList<String>(categories);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void agreesWithTheHandlebarsJsAstOnEveryVector() throws Exception {
    Map<String, Object> file = new ObjectMapper().readValue(readResource("/pdf/subset-vectors.json"), Map.class);
    List<Map<String, Object>> rows = (List<Map<String, Object>>) file.get("rows");
    assertTrue("subset-vectors.json carries too few rows: " + rows.size(), rows.size() >= 500);
    List<String> mismatches = new ArrayList<String>();
    for (Map<String, Object> row : rows) {
      String template = row.containsKey("templateResource") ? readResource((String) row.get("templateResource"))
        : (String) row.get("template");
      List<String> expected = new ArrayList<String>((List<String>) row.get("expected"));
      Collections.sort(expected);
      List<String> actual = categoriesOf(template);
      if (!expected.equals(actual)) {
        mismatches.add("template=" + (template.length() > 200 ? template.substring(0, 200) + "..." : template)
          .replace("\n", "\\n") + " expected=" + expected + " actual=" + actual);
      }
    }
    assertEquals(mismatches.size() + " of " + rows.size() + " rows disagree with Handlebars.js; first ones: "
      + mismatches.subList(0, Math.min(10, mismatches.size())), 0, mismatches.size());
  }
}
