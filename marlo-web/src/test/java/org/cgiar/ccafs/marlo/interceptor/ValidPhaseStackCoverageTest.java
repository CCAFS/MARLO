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

package org.cgiar.ccafs.marlo.interceptor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Guards the wiring of ValidPhaseInterceptor in the Struts configuration (A2-2606). A stack added later without
 * validPhase would silently let a phase of another Global Unit reach its actions again, so the rule is checked on the
 * real configuration files rather than trusted to review.
 */
public class ValidPhaseStackCoverageTest {

  private static final String[] STRUTS_FILES = {"/struts.xml", "/struts-json.xml", "/struts-home.xml"};

  private static final String VALID_PHASE = "validPhase";

  private static List<String> childRefs(Element element) {
    List<String> refs = new ArrayList<>();
    NodeList children = element.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      if (children.item(i) instanceof Element && "interceptor-ref".equals(((Element) children.item(i)).getTagName())) {
        refs.add(((Element) children.item(i)).getAttribute("name"));
      }
    }
    return refs;
  }

  private static Document load(String resource) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    // The files declare the Struts DTD by URL: never fetch it, a unit test must not depend on the network.
    factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    factory.setValidating(false);
    DocumentBuilder builder = factory.newDocumentBuilder();
    try (InputStream input = ValidPhaseStackCoverageTest.class.getResourceAsStream(resource)) {
      assertNotNull(resource + " must be on the test classpath", input);
      return builder.parse(input);
    }
  }

  private static Element packageNamed(Document document, String name) {
    NodeList packages = document.getElementsByTagName("package");
    for (int i = 0; i < packages.getLength(); i++) {
      Element element = (Element) packages.item(i);
      if (name.equals(element.getAttribute("name"))) {
        return element;
      }
    }
    return null;
  }

  private static List<String> stackRefs(Element pack, String stackName) {
    NodeList stacks = pack.getElementsByTagName("interceptor-stack");
    for (int i = 0; i < stacks.getLength(); i++) {
      Element stack = (Element) stacks.item(i);
      if (stackName.equals(stack.getAttribute("name"))) {
        return childRefs(stack);
      }
    }
    return null;
  }

  private void assertDefaultStackChecksThePhase(String resource, String packageName) throws Exception {
    Element pack = packageNamed(load(resource), packageName);
    assertNotNull("package " + packageName + " in " + resource, pack);
    NodeList defaults = pack.getElementsByTagName("default-interceptor-ref");
    assertEquals("package " + packageName + " must declare its default stack", 1, defaults.getLength());
    String stackName = ((Element) defaults.item(0)).getAttribute("name");
    List<String> refs = stackRefs(pack, stackName);
    assertNotNull("default stack " + stackName + " of " + packageName + " must be declared in the package", refs);
    assertTrue("default stack " + stackName + " of " + packageName + " must run validPhase",
      refs.contains(VALID_PHASE));
  }

  @Test
  public void everyActionDeclaringDefaultStackItselfRunsValidPhaseBeforeIt() throws Exception {
    for (String resource : STRUTS_FILES) {
      NodeList actions = load(resource).getElementsByTagName("action");
      for (int i = 0; i < actions.getLength(); i++) {
        Element action = (Element) actions.item(i);
        List<String> refs = childRefs(action);
        int defaultStack = refs.indexOf("defaultStack");
        if (defaultStack >= 0) {
          int validPhase = refs.indexOf(VALID_PHASE);
          assertTrue(resource + ": action " + action.getAttribute("name")
            + " declares defaultStack itself, so it must run validPhase before it",
            validPhase >= 0 && validPhase < defaultStack);
        }
      }
    }
  }

  @Test
  public void everyStackThatChecksTheUserOrTheGlobalUnitRunsValidPhaseRightAfter() throws Exception {
    int checkedStacks = 0;
    for (String resource : STRUTS_FILES) {
      NodeList stacks = load(resource).getElementsByTagName("interceptor-stack");
      for (int i = 0; i < stacks.getLength(); i++) {
        Element stack = (Element) stacks.item(i);
        List<String> refs = childRefs(stack);
        // validSessionCrp switches the session Global Unit, so the phase must be compared after it; a stack without
        // it compares against the Global Unit the user is already in, right after the user check.
        String anchor = refs.contains("validSessionCrp") ? "validSessionCrp"
          : refs.contains("requireUser") ? "requireUser" : null;
        if (anchor == null) {
          continue;
        }
        checkedStacks++;
        int position = refs.indexOf(anchor);
        assertTrue(resource + ": stack " + stack.getAttribute("name") + " must run validPhase right after " + anchor,
          position + 1 < refs.size() && VALID_PHASE.equals(refs.get(position + 1)));
      }
    }
    assertTrue("the configuration must still contain the stacks this test protects", checkedStacks >= 30);
  }

  @Test
  public void theDefaultStacksForActionsWithoutInterceptorsRunValidPhase() throws Exception {
    this.assertDefaultStackChecksThePhase("/struts.xml", "marlo-default");
    this.assertDefaultStackChecksThePhase("/struts-json.xml", "json");
    this.assertDefaultStackChecksThePhase("/struts-json.xml", "json-planning");
  }

}
