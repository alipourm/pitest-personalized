package org.pitest.mutationtest.execute;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.pitest.mutationtest.LocationMother.aLocation;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.TimeoutLengthStrategy;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationEngine;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.testapi.Configuration;
import org.pitest.util.ExitCode;
import org.pitest.util.PitError;
import org.pitest.util.SafeDataInputStream;

public class MutationTestSlaveTest {

  private MutationTestSlave           testee;

  @Mock
  private Reporter                    reporter;

  @Mock
  private SafeDataInputStream         is;

  @Mock
  private MutationEngine              engine;

  @Mock
  private TimeoutLengthStrategy       timeoutStrategy;

  @Mock
  private Configuration               testConfig;

  @Mock
  private Mutater                     mutater;

  private SlaveArguments              args;

  private Collection<MutationDetails> mutations;

  private Collection<ClassName>       tests;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    this.mutations = new ArrayList<MutationDetails>();
    this.tests = new ArrayList<ClassName>();

    this.args = new SlaveArguments(this.mutations, this.tests, this.engine,
        this.timeoutStrategy, false, this.testConfig);

    when(this.is.read(SlaveArguments.class)).thenReturn(this.args);
    when(this.engine.createMutator(any(ClassByteArraySource.class)))
        .thenReturn(this.mutater);

    this.testee = new MutationTestSlave(this.is, this.reporter);
  }

  @Test
  public void shouldReportNoErrorWhenNoMutationsSupplied() {
    this.testee.run();
    verify(this.reporter).done(ExitCode.OK);
  }

  @Test
  public void shouldReportErrorWhenOneOccursDuringAnalysis() {
    this.mutations.add(new MutationDetails(new MutationIdentifier(aLocation(), 1,
        "foo"), null, null, 0, 0));
    when(this.mutater.getMutation(any(MutationIdentifier.class))).thenThrow(
        new PitError("foo"));
    this.testee.run();
    verify(this.reporter).done(ExitCode.UNKNOWN_ERROR);
  }

}
