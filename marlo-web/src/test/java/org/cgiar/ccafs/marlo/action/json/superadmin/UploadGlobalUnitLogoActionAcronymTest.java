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

package org.cgiar.ccafs.marlo.action.json.superadmin;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * The acronym of a logo upload becomes the file name. One that could step outside the logos folder must be refused
 * before a path is built from it, the same way the logo download refuses it.
 */
public class UploadGlobalUnitLogoActionAcronymTest {

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  private static UploadGlobalUnitLogoAction action() {
    // No configuration: a refused acronym must return before the uploads folder is read
    return new UploadGlobalUnitLogoAction(null) {

      private static final long serialVersionUID = 1L;

      @Override
      public String getText(String key) {
        return key;
      }
    };
  }

  private void assertRefused(String acronym) throws Exception {
    File image = folder.newFile();
    UploadGlobalUnitLogoAction action = action();
    action.setAcronym(acronym);
    action.setFile(image);

    assertEquals(UploadGlobalUnitLogoAction.SUCCESS, action.execute());
    assertFalse(action.isSaved());
    assertEquals("globalUnitManagement.logo.upload.acronymInvalid", action.getMessage());
  }

  @Test
  public void anAcronymWithAPathIsRefused() throws Exception {
    this.assertRefused("../AICCRA");
    this.assertRefused("A/B");
    this.assertRefused("A\\B");
  }

  @Test
  public void anAcronymWithADotIsRefused() throws Exception {
    this.assertRefused("A.B");
    this.assertRefused("..");
  }
}
