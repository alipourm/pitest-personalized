package org.pitest.mutationtest.verify;

/*
 * Copyright 2012 Henry Coles
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

import java.util.Collection;

import org.pitest.classinfo.ClassInfo;
import org.pitest.classpath.CodeSource;
import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import org.pitest.functional.SideEffect1;
import org.pitest.help.Help;
import org.pitest.help.PitHelpError;

public class DefaultBuildVerifier implements BuildVerifier {

  public void verify(final CodeSource code) {
    final Collection<ClassInfo> codeClasses = code.getCode();
    // perform only a weak check for line numbers as
    // some jvm languages are not guaranteed to produce them for all classes
    checkAtLeastOneClassHasLineNumbers(codeClasses);
    FCollection.forEach(codeClasses, throwErrorIfHasNoSourceFile());
  }

  private void checkAtLeastOneClassHasLineNumbers(
      final Collection<ClassInfo> codeClasses) {
    if (!FCollection.contains(codeClasses, aClassWithLineNumbers())
        && !codeClasses.isEmpty()) {
      throw new PitHelpError(Help.NO_LINE_NUMBERS);
    }
  }

  private static F<ClassInfo, Boolean> aClassWithLineNumbers() {
    return new F<ClassInfo, Boolean>() {
      public Boolean apply(final ClassInfo a) {
        return a.getNumberOfCodeLines() != 0;
      }

    };
  }

  private SideEffect1<ClassInfo> throwErrorIfHasNoSourceFile() {
    return new SideEffect1<ClassInfo>() {
      public void apply(final ClassInfo a) {
        if (a.getSourceFileName() == null) {
          throw new PitHelpError(Help.NO_SOURCE_FILE, a.getName().asJavaName());
        }
      }
    };
  }

}
