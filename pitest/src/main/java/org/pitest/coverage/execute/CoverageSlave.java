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
package org.pitest.coverage.execute;

import static org.pitest.util.Unchecked.translateCheckedException;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.pitest.boot.HotSwapAgent;
import org.pitest.classinfo.ClassName;
import org.pitest.classpath.ClassPathByteArraySource;
import org.pitest.coverage.CoverageTransformer;
import org.pitest.dependency.DependencyExtractor;
import org.pitest.execute.Pitest;
import org.pitest.execute.UnGroupedStrategy;
import org.pitest.functional.FCollection;
import org.pitest.functional.predicate.Predicate;
import org.pitest.testapi.TestUnit;
import org.pitest.util.ExitCode;
import org.pitest.util.Functions;
import org.pitest.util.Log;
import org.pitest.util.SafeDataInputStream;

import sun.pitest.CodeCoverageStore;

public class CoverageSlave {

  private final static Logger LOG = Log.getLogger();

  public static void main(final String[] args) {

    ExitCode exitCode = ExitCode.OK;
    Socket s = null;
    CoveragePipe invokeQueue = null;
    try {

      final int port = Integer.valueOf(args[0]);
      s = new Socket("localhost", port);

      final SafeDataInputStream dis = new SafeDataInputStream(
          s.getInputStream());

      final CoverageOptions paramsFromParent = dis.read(CoverageOptions.class);

      Log.setVerbose(paramsFromParent.isVerbose());

      if (paramsFromParent.getPitConfig().verifyEnvironment().hasSome()) {
        throw paramsFromParent.getPitConfig().verifyEnvironment().value();
      }

      invokeQueue = new CoveragePipe(new BufferedOutputStream(
          s.getOutputStream()));

      CodeCoverageStore.init(invokeQueue);

      HotSwapAgent.addTransformer(new CoverageTransformer(
          convertToJVMClassFilter(paramsFromParent.getFilter())));

      final List<TestUnit> tus = getTestsFromParent(dis, paramsFromParent);

      LOG.info(tus.size() + " tests received");

      final CoverageWorker worker = new CoverageWorker(invokeQueue, tus);

      worker.run();

    } catch (final Throwable ex) {
      LOG.log(Level.SEVERE, "Error calculating coverage. Process will exit.",
          ex);
      exitCode = ExitCode.UNKNOWN_ERROR;
    } finally {
      if (invokeQueue != null) {
        invokeQueue.end(exitCode);
      }

      try {
        if (s != null) {
          s.close();
        }
      } catch (final IOException e) {
        throw translateCheckedException(e);
      }
    }

    System.exit(exitCode.getCode());

  }

  private static Predicate<String> convertToJVMClassFilter(
      final Predicate<String> child) {
    return new Predicate<String>() {
      public Boolean apply(final String a) {
        return child.apply(a.replace("/", "."));
      }

    };
  }

  private static List<TestUnit> getTestsFromParent(
      final SafeDataInputStream dis, final CoverageOptions paramsFromParent)
      throws IOException {
    final List<ClassName> classes = receiveTestClassesFromParent(dis);
    Collections.sort(classes); // ensure classes loaded in a consistent order

    final List<TestUnit> tus = discoverTests(paramsFromParent, classes);

    final DependencyFilter filter = new DependencyFilter(
        new DependencyExtractor(new ClassPathByteArraySource(),
            paramsFromParent.getDependencyAnalysisMaxDistance()),
        paramsFromParent.getFilter());
    final List<TestUnit> filteredTus = filter
        .filterTestsByDependencyAnalysis(tus);

    LOG.info("Dependency analysis reduced number of potential tests by "
        + (tus.size() - filteredTus.size()));
    return filteredTus;

  }

  private static List<TestUnit> discoverTests(
      final CoverageOptions paramsFromParent, final List<ClassName> classes) {
    final List<TestUnit> tus = Pitest.findTestUnitsForAllSuppliedClasses(
        paramsFromParent.getPitConfig(), new UnGroupedStrategy(),
        FCollection.flatMap(classes, Functions.nameToClass()));
    LOG.info("Found  " + tus.size() + " tests");
    return tus;
  }

  private static List<ClassName> receiveTestClassesFromParent(
      final SafeDataInputStream dis) {
    final int count = dis.readInt();
    LOG.fine("Expecting " + count + " tests classes from parent");
    final List<ClassName> classes = new ArrayList<ClassName>(count);
    for (int i = 0; i != count; i++) {
      classes.add(new ClassName(dis.readString()));
    }
    LOG.fine("Tests classes received");

    return classes;
  }

}
