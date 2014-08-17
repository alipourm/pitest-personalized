package org.pitest.mutationtest.build;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.pitest.mutationtest.LocationMother.aLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.classinfo.ClassName;
import org.pitest.coverage.ClassLine;
import org.pitest.coverage.CoverageDatabase;
import org.pitest.coverage.TestInfo;
import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import org.pitest.functional.Option;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;

public class DefaultTestPrioritiserTest {

  private DefaultTestPrioritiser        testee;


  @Mock
  private CoverageDatabase      coverage;

  @Mock
  private ClassByteArraySource  source;

  private final ClassName       foo = ClassName.fromString("foo");

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    this.testee =  new DefaultTestPrioritiser(coverage);
  }

  @Test
  public void shouldAssignTestsForRelevantLineToGeneratedMutations() {
    final List<TestInfo> expected = makeTestInfos(0);
    when(this.coverage.getTestsForClassLine(any(ClassLine.class))).thenReturn(
        expected);
    final List<TestInfo> actual = this.testee.assignTests(makeMutation("foo"));
    assertEquals(expected, actual);
  }

  @Test
  public void shouldAssignAllTestsForClassWhenMutationInStaticInitialiser() {
    final List<TestInfo> expected = makeTestInfos(0);
    when(this.coverage.getTestsForClass(this.foo)).thenReturn(expected);
    final List<TestInfo> actual = this.testee.assignTests(makeMutation("<clinit>"));
    assertEquals(expected, actual);
  }

  @Test
  public void shouldPrioritiseTestsByExecutionTime() {
    final List<TestInfo> unorderedTests = makeTestInfos(10000, 100, 1000, 1);
    when(this.coverage.getTestsForClassLine(any(ClassLine.class))).thenReturn(
        unorderedTests);
    final List<TestInfo> actual = this.testee.assignTests(makeMutation("foo"));

    assertEquals(Arrays.asList(1,100,1000,10000), FCollection.map(actual, toTime()));
  }

  private F<TestInfo, Integer> toTime() {
    return new F<TestInfo, Integer>() {
      public Integer apply(TestInfo a) {
        return a.getTime();
      }
      
    };
  }

  private List<TestInfo> makeTestInfos(final Integer... times) {
    return new ArrayList<TestInfo>(FCollection.map(Arrays.asList(times),
        timeToTestInfo()));
  }

  private F<Integer, TestInfo> timeToTestInfo() {
    return new F<Integer, TestInfo>() {
      public TestInfo apply(final Integer a) {
        return new TestInfo("foo", "bar", a, Option.<ClassName> none(), 0);
      }

    };
  }

  private MutationDetails makeMutation(final String method) {
    final MutationIdentifier id = new MutationIdentifier(aLocation().with(foo).withMethod(method), 0, "mutator");
    return new MutationDetails(id, "file", "desc", 1, 2);
  }

}
