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

import java.util.List;

import com.opensymphony.xwork2.util.GlobalLocalizedTextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class to allow reseting of resourceBundles between user requests in order to cater for users who want to navigate
 * between CRPs during the same user session. There could be a nicer way to do this, but I haven't found it yet.
 * 
 * @author GrantL
 */
public class MarloLocalizedTextProvider extends GlobalLocalizedTextProvider {

  private static final Logger LOG = LoggerFactory.getLogger(MarloLocalizedTextProvider.class);

  private static final long serialVersionUID = -2138897485880686121L;

  /**
   * If we don't reset the properties bundles then the user will potentially get the properties loaded from another CRP
   * if that property has not been defined by that CRP or Center. It might be a good idea to see if there is a better
   * way of doing this, but from my limited research it seems MARLO is using the resource bundles (i.e. loading the
   * resource bundle based on a value in the user's session) in a way that struts2 designers did not consider, which
   * leads to these kind of issues.
   */
  public void resetResourceBundles() {
    // Need to determine if this a performance hit when multiple request from different users block on this.
    synchronized (this) {
      List<String> bundles = this.getCurrentBundleNames();
      if (bundles == null) {
        // Do nothing, not our responsibility to set up the bundles.
        return;
      }
      /**
       * Note that calling the resetResourceBundles does not seem to clear the bundles. It instead clears the bundlesMap
       * but we in fact need to clear the map that is on the current thread context.
       */
      bundles.clear();
      bundles.add(0, XWORK_MESSAGES_BUNDLE);
      bundles.add(0, STRUTS_MESSAGES_BUNDLE);

    }
  }


}
