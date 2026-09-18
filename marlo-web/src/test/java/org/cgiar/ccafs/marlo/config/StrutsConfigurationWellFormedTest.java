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

package org.cgiar.ccafs.marlo.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * Parses every Struts configuration file in this module and fails if one is not well-formed XML.
 * <p>
 * <b>Why this test exists.</b> A comment added to {@code struts-home.xml} used {@code --} as an em-dash.
 * That sequence is illegal inside an XML comment, so {@code ConfigurationManager} threw
 * {@code SAXParseException: The string "--" is not permitted within comments} while loading the file,
 * {@code StrutsPrepareAndExecuteFilter} failed to initialise, and <b>the entire web application refused to
 * start</b> — local login included, nothing to do with Cognito.
 * <p>
 * Ninety-seven unit tests and twelve independent review rounds did not see it, for one structural reason:
 * <b>nothing in the suite parsed these files.</b> Every test drove Java objects directly, and the Struts
 * configuration is only read by a running container. A one-line typo in a comment could therefore take the
 * whole application down while every gate reported green.
 * <p>
 * This is deliberately a <i>well-formedness</i> check, not a DTD validation: resolving the Struts DTD needs
 * network access or a local catalog, and a test that silently degrades when offline is worse than none. It
 * catches the class of defect that actually occurred — malformed XML — and makes no claim beyond that.
 */
public class StrutsConfigurationWellFormedTest {

  private static final String RESOURCES = "src/main/resources";

  /**
   * The parameter is {@code File} rather than a Struts type on purpose; this class deliberately touches no
   * Struts API, so it can run with no container and no configuration bootstrapping.
   */
  private static void assertWellFormed(File file) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setValidating(false);
      factory.setNamespaceAware(false);
      DocumentBuilder builder = factory.newDocumentBuilder();
      // The DTD is declared but must not be fetched: a test that needs the network is a test that gets
      // skipped in CI and rots. An empty entity satisfies the declaration without resolving it.
      builder.setEntityResolver(new org.xml.sax.EntityResolver() {

        @Override
        public InputSource resolveEntity(String publicId, String systemId) {
          return new InputSource(new java.io.StringReader(""));
        }
      });
      builder.parse(file);
    } catch (SAXException e) {
      fail(file.getName() + " is not well-formed XML: " + e.getMessage());
    } catch (Exception e) {
      fail("could not read " + file.getName() + ": " + e);
    }
  }

  @Test
  public void everyStrutsConfigurationFileIsWellFormed() {
    File resources = new File(RESOURCES);
    File[] candidates = resources.listFiles((dir, name) -> name.startsWith("struts") && name.endsWith(".xml"));

    List<String> checked = new ArrayList<String>();
    if (candidates != null) {
      for (File candidate : candidates) {
        assertWellFormed(candidate);
        checked.add(candidate.getName());
      }
    }

    // Guards the guard: if the files ever move, this test would otherwise pass by checking nothing.
    assertFalse("no struts*.xml found under " + resources.getAbsolutePath() + " — this test has stopped "
      + "checking anything and must be pointed at the new location", checked.isEmpty());
  }
}
