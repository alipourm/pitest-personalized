package org.pitest.testng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.testng.ITestListener;
import org.testng.TestNG;
import org.testng.xml.XmlSuite;

public class ForeignClassLoaderTestNGExecutor implements Callable<List<String>>{
  
  private final XmlSuite suite;
  
  public ForeignClassLoaderTestNGExecutor(XmlSuite suite) {
    this.suite = suite;
  }
  
  public List<String> call() throws Exception {
    List<String> queue = new ArrayList<String>();
    final ITestListener listener = new ForeignClassLoaderAdaptingListener(queue);
    final TestNG testng = new TestNG(false);
    testng.setDefaultSuiteName(suite.getName());
    testng.setXmlSuites(Collections.singletonList(suite));

    testng.addListener(listener);
    testng.run();
    
    return queue;
  }


}


