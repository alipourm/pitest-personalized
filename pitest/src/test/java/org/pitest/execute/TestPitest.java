/**
 * 
 */
package org.pitest.execute;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.pitest.execute.containers.UnContainer;
import org.pitest.simpletest.ConfigurationForTesting;
import org.pitest.simpletest.TestAnnotationForTesting;
import org.pitest.testapi.Description;
import org.pitest.testapi.TestListener;
import org.pitest.testapi.TestResult;

/**
 * @author henry
 * 
 */

public class TestPitest {

  private Pitest              testee;
  private Container           container;

  @Mock
  private TestListener        listener;
  private DefaultStaticConfig staticConfig;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    this.container = new UnContainer();
    this.staticConfig = new DefaultStaticConfig();
    this.staticConfig.getTestListeners().add(this.listener);
    this.testee = new Pitest(this.staticConfig);
  }

  public static class PassingTest {
    @TestAnnotationForTesting
    public void shouldPass() {

    }
  };

  @Test
  public void shouldNotifyAllListenersOfRunStart() {
    final TestListener listener2 = Mockito.mock(TestListener.class);
    this.staticConfig.addTestListener(listener2);
    run(PassingTest.class);
    verify(this.listener).onRunStart();
    verify(listener2).onRunStart();
  }

  @Test
  public void shouldNotifyAllListenersOfTestStart() {
    final TestListener listener2 = Mockito.mock(TestListener.class);
    this.staticConfig.addTestListener(listener2);
    run(PassingTest.class);
    verify(this.listener).onTestStart(any(Description.class));
    verify(listener2).onTestStart(any(Description.class));
  }

  @Test
  public void shouldNotifyAllListenersOfTestSuccess() {
    final TestListener listener2 = Mockito.mock(TestListener.class);
    this.staticConfig.addTestListener(listener2);
    run(PassingTest.class);
    verify(this.listener).onTestSuccess(any(TestResult.class));
    verify(listener2).onTestSuccess(any(TestResult.class));
  }

  @Test
  public void shouldNotifyAllListenersOfRunEnd() {
    final TestListener listener2 = Mockito.mock(TestListener.class);
    this.staticConfig.addTestListener(listener2);
    run(PassingTest.class);
    verify(this.listener).onRunEnd();
    verify(listener2).onRunEnd();
  }

  @Test
  public void shouldReportsSuccessIfNoExceptionThrown() {
    run(PassingTest.class);
    verify(this.listener).onTestSuccess(any(TestResult.class));
  }

  public static class FailsAssertion {
    @TestAnnotationForTesting
    public void willFail() {
      throw new AssertionError();
    }
  };

  @Test
  public void shouldReportedAsFailureIfAssertionThrown() {
    run(FailsAssertion.class);
    verify(this.listener).onTestFailure(any(TestResult.class));
  }

  public static class ExpectedExceptionThrown {
    @TestAnnotationForTesting(expected = NullPointerException.class)
    public void expectToThrowNullPointer() {
      throw new NullPointerException();
    }

  };

  @Test
  public void shouldReportSuccessIfExpectedExceptionThrown() {
    run(ExpectedExceptionThrown.class);
    verify(this.listener).onTestSuccess(any(TestResult.class));

  }

  public static class ExpectedExceptionNotThrown {
    @TestAnnotationForTesting(expected = NullPointerException.class)
    public void expectToThrowNullPointer() {

    }
  };

  @Test
  public void shouldReportFailureIfExpectedExceptionNotThrown() {
    run(ExpectedExceptionNotThrown.class);
    verify(this.listener).onTestFailure(any(TestResult.class));
  }

  public static class UnexpectedExceptionThrown {
    @TestAnnotationForTesting
    public void willFailWithError() throws Exception {
      throw new FileNotFoundException();
    }
  };

  @Test
  public void shouldReportErrorWhenExceptionThrownAndNoExpectationSet() {
    run(UnexpectedExceptionThrown.class);
    verify(this.listener).onTestError(any(TestResult.class));
  }

  public static class WrongExceptionThrown {
    @TestAnnotationForTesting(expected = NullPointerException.class)
    public void willFailWithError() throws Exception {
      throw new FileNotFoundException();
    }
  };

  @Test
  public void shouldReportErrorWhenThrownExceptionDifferentFromExpectedException() {
    run(WrongExceptionThrown.class);
    verify(this.listener).onTestError(any(TestResult.class));
  }

  public static class SubclassOfExpectedExceptionThrown {
    @TestAnnotationForTesting(expected = Exception.class)
    public void shouldPass() throws Exception {
      throw new FileNotFoundException();
    }
  };

  @Test
  public void shouldReportSuccessWhenSubclassOfExpectedExceptionThrown() {
    run(SubclassOfExpectedExceptionThrown.class);
    verify(this.listener).onTestSuccess(any(TestResult.class));
  }

  public static class StaticTestCase {
    @TestAnnotationForTesting
    public static void staticTestMethod() {
    }
  };

  @Test
  public void shouldBeAbleToCallStaticTestMethods() {
    run(StaticTestCase.class);
    verify(this.listener).onTestSuccess(any(TestResult.class));
  }

  private void run(final Class<?> clazz) {
    this.testee.run(this.container, new ConfigurationForTesting(), clazz);
  }
}