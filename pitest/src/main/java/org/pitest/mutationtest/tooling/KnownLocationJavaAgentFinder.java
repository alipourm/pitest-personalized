package org.pitest.mutationtest.tooling;

import org.pitest.functional.Option;
import org.pitest.process.JavaAgent;

public class KnownLocationJavaAgentFinder implements JavaAgent {

  private final String location;

  public KnownLocationJavaAgentFinder(final String location) {
    this.location = location;
  }

  public Option<String> getJarLocation() {
    return Option.some(this.location);
  }

  public void close() {
  }

}
