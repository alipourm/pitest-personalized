/*
 * Copyright 2010 Henry Coles
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package org.pitest.execute;

import java.util.List;
import java.util.logging.Logger;

import org.pitest.testapi.AbstractTestUnit;
import org.pitest.testapi.Description;
import org.pitest.testapi.ResultCollector;
import org.pitest.testapi.TestUnit;
import org.pitest.util.Log;

public class MultipleTestGroup extends AbstractTestUnit {
	  private static final Logger                               LOG = Log
      .getLogger();
  private final List<TestUnit> children;

  public MultipleTestGroup(final List<TestUnit> children) {
    super(new Description("MultipleTestGroup"));
    this.children = children;
  }

  @Override
  public void execute(final ClassLoader loader, final ResultCollector rc) {
    for (final TestUnit each : this.children) {
      each.execute(loader, rc);
      if (rc.shouldExit()) {
    	  LOG.fine("Didn't allow to stop for a failure!");
    	  // REMOVED BY AMIN
     //   break; 
      }
    }

  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result)
        + ((this.children == null) ? 0 : this.children.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final MultipleTestGroup other = (MultipleTestGroup) obj;
    if (this.children == null) {
      if (other.children != null) {
        return false;
      }
    } else if (!this.children.equals(other.children)) {
      return false;
    }
    return true;
  }

}
