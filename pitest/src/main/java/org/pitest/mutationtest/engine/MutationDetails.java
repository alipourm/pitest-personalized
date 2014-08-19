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
package org.pitest.mutationtest.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.simple.JSONObject;
import org.pitest.classinfo.ClassName;
import org.pitest.coverage.ClassLine;
import org.pitest.coverage.TestInfo;
import org.pitest.util.StringUtil;

public class MutationDetails {

  private final MutationIdentifier  id;
  private final String              filename;
  private final int                 block;
  private final int                 lineNumber;
  private final String              description;
  private final ArrayList<TestInfo> testsInOrder = new ArrayList<TestInfo>();
  private final boolean             isInFinallyBlock;
  private final boolean             poison;

  public MutationDetails(final MutationIdentifier id, final String filename,
      final String description, final int lineNumber, final int block) {
    this(id, filename, description, lineNumber, block, false, false);
  }

  public MutationDetails(final MutationIdentifier id, final String filename,
      final String description, final int lineNumber, final int block,
      final boolean isInFinallyBlock, final boolean poison) {
    this.id = id;
    this.description = description;
    this.filename = filename;
    this.lineNumber = lineNumber;
    this.block = block;
    this.isInFinallyBlock = isInFinallyBlock;
    this.poison = poison;
  }


  
  @Override
  public String toString() {
	  return "MutationDetails [id=" + this.id + ", filename=" + this.filename
        + ", block=" + this.block + ", lineNumber=" + this.lineNumber
        + ", description=" + this.description + ", testsInOrder="
        + this.testsInOrder + "]";
  }

  
  public JSONObject toJSON(){
	  JSONObject js = new JSONObject();
	  js.put("id", this.id.toJSON());
	  js.put("filename", this.filename);
	  js.put("block", this.block);
	  js.put("line", this.lineNumber);
	  return js;
	  
  }

  public String getDescription() {
    return this.description;
  }

  public String getHtmlSafeDescription() {
    return StringUtil.escapeBasicHtmlChars(this.description);
  }

  public String getLocation() {
    return this.id.getLocation().describe();
  }

  public ClassName getClassName() {
    return this.id.getClassName();
  }

  public MethodName getMethod() {
    return this.id.getLocation().getMethodName();
  }

  public String getMethodDescription() {
    return this.id.getLocation().getMethodDesc();
  }

  public String getFilename() {
    return this.filename;
  }

  public int getLineNumber() {
    return this.lineNumber;
  }

  public ClassLine getClassLine() {
    return new ClassLine(this.id.getClassName(), this.lineNumber);
  }

  public MutationIdentifier getId() {
    return this.id;
  }

  public List<TestInfo> getTestsInOrder() {
    return this.testsInOrder;
  }

  public void addTestsInOrder(final Collection<TestInfo> testNames) {
    this.testsInOrder.addAll(testNames);
    this.testsInOrder.trimToSize();
  }

  public boolean mayPoisonJVM() {
    return poison || isInStaticInitializer();
  }
  
  public boolean isInStaticInitializer() {
    return this.getMethod().name().trim().startsWith("<clinit>");
  }

  public int getBlock() {
    return this.block;
  }

  public Boolean matchesId(final MutationIdentifier id) {
    return this.id.matches(id);
  }

  public String getMutator() {
    return this.id.getMutator();
  }

  public int getFirstIndex() {
    return this.id.getFirstIndex();
  }

  public boolean isInFinallyBlock() {
    return this.isInFinallyBlock;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.id == null) ? 0 : this.id.hashCode());
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
    final MutationDetails other = (MutationDetails) obj;
    if (this.id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!this.id.equals(other.id)) {
      return false;
    }
    return true;
  }

}
