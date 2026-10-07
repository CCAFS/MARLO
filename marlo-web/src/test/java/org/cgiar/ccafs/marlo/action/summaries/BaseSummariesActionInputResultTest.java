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

package org.cgiar.ccafs.marlo.action.summaries;

import org.cgiar.ccafs.marlo.action.BaseAction;

import org.apache.struts2.interceptor.ValidationWorkflowAware;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * A report has no input result, so a request parameter Struts cannot convert (projectID=abc) made the workflow
 * interceptor ask for one and the request failed with Error 500 and an exception e-mail. The workflow interceptor
 * asks a ValidationWorkflowAware action which result to use instead; a report answers with the not-found page.
 */
public class BaseSummariesActionInputResultTest {

  @Test
  public void reportsTellTheWorkflowInterceptorWhichResultToUse() {
    assertTrue(ValidationWorkflowAware.class.isAssignableFrom(BaseSummariesAction.class));
    assertTrue("every report built on it inherits the answer",
      BaseSummariesAction.class.isAssignableFrom(ProjectActivitiesSummaryAction.class));
  }

  @Test
  public void anUnconvertibleParameterIsAnsweredAsNotFound() {
    BaseSummariesAction action = new BaseSummariesAction(null, null, null, null);
    action.addFieldError("projectID", "Invalid field value for field \"projectID\".");

    assertEquals(BaseAction.NOT_FOUND, action.getInputResultName());
  }
}
