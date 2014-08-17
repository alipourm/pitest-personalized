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
package org.pitest.util;

import java.util.Collection;
import java.util.regex.Pattern;

import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import org.pitest.functional.predicate.Predicate;

public class Glob implements Predicate<String> {

  private final String regex;

  public Glob(final String glob) {
    this.regex = convertGlobToRegex(glob);
  }

  public boolean matches(final CharSequence seq) {
    return Pattern.matches(this.regex, seq);
  }

  public static F<String, Predicate<String>> toGlobPredicate() {
    return new F<String, Predicate<String>>() {
      public Glob apply(final String glob) {
        return new Glob(glob);
      }
    };
  }

  public static Collection<Predicate<String>> toGlobPredicates(
      final Collection<String> globs) {
    return FCollection.map(globs, Glob.toGlobPredicate());
  }

  private static String convertGlobToRegex(final String glob) {
    final StringBuilder out = new StringBuilder("^");
    for (int i = 0; i < glob.length(); ++i) {
      final char c = glob.charAt(i);
      switch (c) {
      case '$':
        out.append("\\$");
        break;
      case '*':
        out.append(".*");
        break;
      case '?':
        out.append('.');
        break;
      case '.':
        out.append("\\.");
        break;
      case '\\':
        out.append("\\\\");
        break;
      default:
        out.append(c);
      }
    }
    out.append('$');
    return out.toString();
  }

  public Boolean apply(final String value) {
    return matches(value);
  }

  @Override
  public String toString() {
    return this.regex;
  }

}
