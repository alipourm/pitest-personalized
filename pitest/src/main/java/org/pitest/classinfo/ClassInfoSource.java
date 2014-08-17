package org.pitest.classinfo;

import org.pitest.functional.Option;

public interface ClassInfoSource {
  Option<ClassInfo> fetchClass(final ClassName name);
}
