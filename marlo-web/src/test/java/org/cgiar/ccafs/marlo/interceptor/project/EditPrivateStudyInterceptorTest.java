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

package org.cgiar.ccafs.marlo.interceptor.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import org.apache.struts2.dispatcher.HttpParameters;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * The study report is public, so the interceptor reads whatever request it is sent. A request it cannot resolve to a
 * study must be answered as not found, without reaching the action and without an unhandled exception.
 */
public class EditPrivateStudyInterceptorTest {

  private static final long STUDY_ID = 5L;

  private final AtomicInteger invocations = new AtomicInteger();

  private static ProjectExpectedStudyManager studies(ProjectExpectedStudy study) {
    return (ProjectExpectedStudyManager) Proxy.newProxyInstance(ProjectExpectedStudyManager.class.getClassLoader(),
      new Class<?>[] {ProjectExpectedStudyManager.class}, (proxy, method, args) -> {
        if ("getProjectExpectedStudyById".equals(method.getName()) && Long.valueOf(STUDY_ID).equals(args[0])) {
          return study;
        }
        throw new UnsupportedOperationException(method.getName());
      });
  }

  private static Map<String, Object> studyRequest() {
    Map<String, Object> values = new HashMap<>();
    values.put(APConstants.STUDY_REQUEST_ID, String.valueOf(STUDY_ID));
    values.put(APConstants.CYCLE, APConstants.REPORTING);
    values.put(APConstants.YEAR_REQUEST, "2024");
    return values;
  }

  private ActionInvocation invocation(Map<String, Object> values) {
    ActionContext context =
      ActionContext.of().withParameters(HttpParameters.create(values).build()).withSession(new HashMap<>());
    BaseAction action = new BaseAction(null) {

      private static final long serialVersionUID = 1L;
    };
    return (ActionInvocation) Proxy.newProxyInstance(ActionInvocation.class.getClassLoader(),
      new Class<?>[] {ActionInvocation.class}, (proxy, method, args) -> {
        switch (method.getName()) {
          case "getInvocationContext":
            return context;
          case "getAction":
            return action;
          case "invoke":
            invocations.incrementAndGet();
            return BaseAction.SUCCESS;
          default:
            throw new UnsupportedOperationException(method.getName());
        }
      });
  }

  private String intercept(ProjectExpectedStudy study, Map<String, Object> values) throws Exception {
    return new EditPrivateStudyInterceptor(studies(study), null, null, null).intercept(this.invocation(values));
  }

  @Test
  public void aStudyWhoseProjectHasNoActiveOriginUnitIsNotFound() throws Exception {
    GlobalUnitProject inactiveOrigin = new GlobalUnitProject();
    inactiveOrigin.setOrigin(true);
    inactiveOrigin.setActive(false);
    Set<GlobalUnitProject> units = new HashSet<>();
    units.add(inactiveOrigin);
    Project project = new Project();
    project.setGlobalUnitProjects(units);
    ProjectExpectedStudy study = new ProjectExpectedStudy();
    study.setProject(project);

    assertEquals(BaseAction.NOT_FOUND, this.intercept(study, studyRequest()));
    assertEquals(0, invocations.get());
  }

  @Test
  public void aRequestWithoutACycleIsNotFound() throws Exception {
    Map<String, Object> values = studyRequest();
    values.remove(APConstants.CYCLE);

    assertEquals(BaseAction.NOT_FOUND, this.intercept(new ProjectExpectedStudy(), values));
    assertEquals(0, invocations.get());
  }
}
