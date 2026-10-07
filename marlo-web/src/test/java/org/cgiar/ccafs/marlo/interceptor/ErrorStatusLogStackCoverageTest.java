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

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
 * Guards the wiring of ErrorStatusLogInterceptor in the Struts configuration. It only sees the results of the
 * interceptors after it, so a stack, or an action declaring its own interceptors, that does not start with it would
 * silently drop its 401, 403 and 404 from the log again.
 */
public class ErrorStatusLogStackCoverageTest {

  private static final String[] STRUTS_FILES = {"/struts.xml", "/struts-json.xml", "/struts-home.xml"};

  private static final String LOG_ERROR_STATUS = "logErrorStatus";

  // Actions that run no MARLO stack on purpose: the two login lookups of homeJson never return 401, 403 or 404, and the
  // api package is owned by Spring MVC, whose errors ExceptionTranslator logs with their status.
  private static final Set<String> WITHOUT_LOG_ERROR_STATUS =
    new HashSet<>(Arrays.asList("homeJson/crpByEmail", "homeJson/validateUser", "api/institutions/*"));

  // The default stack of the packages Struts ships, which never contain logErrorStatus.
  private static final String FRAMEWORK_DEFAULT_STACK = "defaultStack";

  private static int countIn(String ref, Map<String, List<String>> stacks, Set<String> expanding) {
    if (LOG_ERROR_STATUS.equals(ref)) {
      return 1;
    }
    List<String> refs = stacks.get(ref);
    if (refs == null || !expanding.add(ref)) {
      return 0;
    }
    int count = 0;
    for (String child : refs) {
      count += countIn(child, stacks, expanding);
    }
    expanding.remove(ref);
    return count;
  }

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
    try (InputStream input = ErrorStatusLogStackCoverageTest.class.getResourceAsStream(resource)) {
      assertNotNull(resource + " must be on the test classpath", input);
      return builder.parse(input);
    }
  }

  @Test
  public void everyActionDeclaringDefaultStackItselfStartsWithLogErrorStatus() throws Exception {
    int checkedActions = 0;
    for (String resource : STRUTS_FILES) {
      NodeList actions = load(resource).getElementsByTagName("action");
      for (int i = 0; i < actions.getLength(); i++) {
        Element action = (Element) actions.item(i);
        List<String> refs = childRefs(action);
        if (refs.contains("defaultStack")) {
          checkedActions++;
          assertTrue(resource + ": action " + action.getAttribute("name")
            + " declares defaultStack itself, so it must start with " + LOG_ERROR_STATUS,
            LOG_ERROR_STATUS.equals(refs.get(0)));
        }
      }
    }
    assertTrue("the configuration must still contain the actions this test protects", checkedActions >= 10);
  }

  private static List<Document> loadAllStrutsFiles() throws Exception {
    File directory = new File(ErrorStatusLogStackCoverageTest.class.getResource("/struts.xml").toURI()).getParentFile();
    File[] files = directory.listFiles((dir, name) -> name.startsWith("struts") && name.endsWith(".xml"));
    assertNotNull("the Struts files must be listed", files);
    List<Document> documents = new ArrayList<>();
    for (File file : files) {
      documents.add(load("/" + file.getName()));
    }
    assertTrue("every struts*.xml must be read, not only struts.xml", documents.size() > STRUTS_FILES.length);
    return documents;
  }

  @Test
  public void everyActionInEveryStrutsFileRunsLogErrorStatusExactlyOnce() throws Exception {
    List<Document> documents = loadAllStrutsFiles();
    Map<String, List<String>> stacks = new HashMap<>();
    Map<String, String> packageDefaults = new HashMap<>();
    Map<String, String> packageParents = new HashMap<>();
    List<Element> packages = new ArrayList<>();
    for (Document document : documents) {
      NodeList stackNodes = document.getElementsByTagName("interceptor-stack");
      for (int i = 0; i < stackNodes.getLength(); i++) {
        Element stack = (Element) stackNodes.item(i);
        stacks.put(stack.getAttribute("name"), childRefs(stack));
      }
      NodeList packageNodes = document.getElementsByTagName("package");
      for (int i = 0; i < packageNodes.getLength(); i++) {
        Element pack = (Element) packageNodes.item(i);
        packages.add(pack);
        packageParents.put(pack.getAttribute("name"), pack.getAttribute("extends"));
        NodeList defaults = pack.getElementsByTagName("default-interceptor-ref");
        if (defaults.getLength() > 0) {
          packageDefaults.put(pack.getAttribute("name"), ((Element) defaults.item(0)).getAttribute("name"));
        }
      }
    }
    int checkedActions = 0;
    for (Element pack : packages) {
      String packageName = pack.getAttribute("name");
      String inherited = packageName;
      while (inherited != null && !packageDefaults.containsKey(inherited)) {
        inherited = packageParents.get(inherited);
      }
      String packageDefault = inherited == null ? FRAMEWORK_DEFAULT_STACK : packageDefaults.get(inherited);
      NodeList actions = pack.getElementsByTagName("action");
      for (int i = 0; i < actions.getLength(); i++) {
        Element action = (Element) actions.item(i);
        String actionName = packageName + "/" + action.getAttribute("name");
        List<String> refs = childRefs(action);
        if (refs.isEmpty()) {
          refs = Arrays.asList(packageDefault);
        }
        int count = 0;
        for (String ref : refs) {
          count += countIn(ref, stacks, new HashSet<>());
        }
        checkedActions++;
        assertEquals(actionName + " must run " + LOG_ERROR_STATUS + " exactly once (twice would log twice)",
          WITHOUT_LOG_ERROR_STATUS.contains(actionName) ? 0 : 1, count);
      }
    }
    assertTrue("the configuration must still contain the actions this test protects", checkedActions >= 440);
  }

  @Test
  public void everyStackStartsWithLogErrorStatus() throws Exception {
    int checkedStacks = 0;
    for (String resource : STRUTS_FILES) {
      NodeList stacks = load(resource).getElementsByTagName("interceptor-stack");
      for (int i = 0; i < stacks.getLength(); i++) {
        Element stack = (Element) stacks.item(i);
        List<String> refs = childRefs(stack);
        checkedStacks++;
        assertTrue(resource + ": stack " + stack.getAttribute("name") + " must start with " + LOG_ERROR_STATUS,
          !refs.isEmpty() && LOG_ERROR_STATUS.equals(refs.get(0)));
      }
    }
    assertTrue("the configuration must still contain the stacks this test protects", checkedStacks >= 36);
  }

}
