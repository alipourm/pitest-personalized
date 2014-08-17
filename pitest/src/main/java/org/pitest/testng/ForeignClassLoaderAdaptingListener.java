package org.pitest.testng;

import java.util.List;

import org.pitest.functional.SideEffect2;
import org.pitest.testapi.Description;
import org.pitest.testapi.ResultCollector;
import org.pitest.testapi.foreignclassloader.Fail;
import org.pitest.testapi.foreignclassloader.Start;
import org.pitest.testapi.foreignclassloader.Success;
import org.pitest.util.IsolationUtils;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.SkipException;

public class ForeignClassLoaderAdaptingListener implements ITestListener {
  
  private final List<String> events;
  private Throwable             error;
  private boolean               hasHadFailure = false;
  
  public ForeignClassLoaderAdaptingListener(List<String> events) {
    this.events = events;
  }

  public void onFinish(ITestContext arg0) {
    if (this.error != null) {
      storeAsString(new Fail(this.error));
    } else {
      storeAsString(new Success());
    }
  }

  public void onStart(ITestContext arg0) {
    storeAsString(new Start()); 
  }

  public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
    storeAsString(new TestSuccess(arg0.getMethod().getMethodName()));
  }

  public void onTestFailure(ITestResult arg0) {
    this.hasHadFailure = true;
    this.error = arg0.getThrowable();
    storeAsString(new TestFail(arg0.getMethod().getMethodName(), error));
  }

  public void onTestSkipped(ITestResult arg0) {
    storeAsString(new TestSkipped(arg0.getMethod().getMethodName()));   
  }

  public void onTestStart(ITestResult result) {
    if (this.hasHadFailure) {
      throw new SkipException("skipping");
    }
    storeAsString(new TestStart(result.getMethod().getMethodName()));
  }

  public void onTestSuccess(ITestResult arg0) {
      storeAsString(new TestSuccess(arg0.getMethod().getMethodName()));
  }
  
  private void storeAsString(
      final SideEffect2<ResultCollector, org.pitest.testapi.Description> result) {
    this.events.add(IsolationUtils.toXml(result));
  }
  

}

class TestStart implements SideEffect2<ResultCollector, org.pitest.testapi.Description> {
  private String methodName;
  
  TestStart(String methodName) {
   this.methodName = methodName; 
  }

  public void apply(ResultCollector rc, Description d) {
    rc.notifyStart(new Description(methodName, d.getFirstTestClass()));
  }
}

class TestSuccess implements SideEffect2<ResultCollector, org.pitest.testapi.Description> {
  private String methodName;
  
  TestSuccess(String methodName) {
   this.methodName = methodName; 
  }

  public void apply(ResultCollector rc, Description d) {
    rc.notifyEnd(new Description(methodName, d.getFirstTestClass()));
  }
}

class TestFail implements SideEffect2<ResultCollector, org.pitest.testapi.Description> {
  private String methodName;
  private final Throwable throwable;
  
  TestFail(String methodName, Throwable throwable) {
   this.methodName = methodName; 
   this.throwable = throwable;
  }

  public void apply(ResultCollector rc, Description d) {
    rc.notifyEnd(new Description(methodName, d.getFirstTestClass()), throwable);
  }
}

class TestSkipped implements SideEffect2<ResultCollector, org.pitest.testapi.Description> {
  private String methodName;
  
  TestSkipped(String methodName) {
   this.methodName = methodName; 
  }

  public void apply(ResultCollector rc, Description d) {
    rc.notifySkipped(new Description(methodName, d.getFirstTestClass()));
  }
}