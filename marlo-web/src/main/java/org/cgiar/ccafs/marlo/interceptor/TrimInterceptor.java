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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This interceptor is in charge of remove all the leading and trailing whitespace
 * present in the values received as paramter.
 * Example:
 * Value received: ' This value was received as parameter '
 * Value returned: 'This value was received as parameter'
 * 
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author Héctor Fabio Tobón R.
 */
public class TrimInterceptor extends MethodFilterInterceptor {

  private static final long serialVersionUID = 6288181036645927977L;

  private static final Logger LOG = LoggerFactory.getLogger(TrimInterceptor.class);

  private List<String> excluded = new ArrayList<>();

  @Override
  protected String doIntercept(ActionInvocation invocation) throws Exception {
    LOG.debug("=> TrimInterceptor");
    // Map<String, Object> parameters = invocation.getInvocationContext().getParameters();
    Map<String, Parameter> parameters = invocation.getInvocationContext().getParameters();

    for (String param : parameters.keySet()) {
      if (this.isIncluded(param)) {
        // String[] vals = (String[]) parameters.get(param);
        String[] vals = parameters.get(param).getMultipleValues();
        for (int i = 0; i < vals.length; i++) {
          vals[i] = vals[i].trim();
        }
      }
    }
    return invocation.invoke();
  }

  public boolean isIncluded(String param) {
    for (String exclude : excluded) {
      if (param.startsWith(exclude)) {
        return false;
      }
    }
    return true;
  }

  public void setExcludedParams(String excludedParams) {
    for (String s : StringUtils.split(excludedParams, ",")) {
      excluded.add(s.trim());
    }
  }

}
