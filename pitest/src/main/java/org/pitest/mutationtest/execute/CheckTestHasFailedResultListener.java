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
package org.pitest.mutationtest.execute;

import java.util.ArrayList;

import org.pitest.functional.Option;
import org.pitest.mutationtest.DetectionStatus;
import org.pitest.testapi.Description;
import org.pitest.testapi.TestListener;
import org.pitest.testapi.TestResult;

public class CheckTestHasFailedResultListener implements TestListener {

  private Option<Description> lastFailingTest = Option.none();
  private int                 testsRun        = 0;
  private int                 testSuccess     = 0;
  private int                 testFailure     = 0;
  private int                 testIgnored     = 0;
  private ArrayList<Description> failingTests = new ArrayList();

  public void onTestError(final TestResult tr) {
    recordFailingTest(tr);
  }

  private void recordFailingTest(final TestResult tr) {
    this.lastFailingTest = Option.some(tr.getDescription());
    this.failingTests.add(tr.getDescription());
  }
  
  public ArrayList<Description> getAllFailinTestCases(){
	  return this.failingTests;
  }
  
  public void onTestFailure(final TestResult tr) {
    recordFailingTest(tr);
    this.testFailure++;
  }

  public void onTestSkipped(final TestResult tr) {
	  this.testIgnored ++;

  }
  public int getNumberOfTestIngnored(){
	  return this.testIgnored++;
  }
  
  public void onTestStart(final Description d) {
    this.testsRun++;

  }

  public void onTestSuccess(final TestResult tr) {
	  this.testSuccess++;
  }

  public DetectionStatus status() {
    if (this.lastFailingTest.hasSome()) {
      return DetectionStatus.KILLED;
    } else {
      return DetectionStatus.SURVIVED;
    }
  }

  public Option<Description> lastFailingTest() {
    return this.lastFailingTest;
  }

  public int getNumberOfTestsRun() {
    return this.testsRun;
  }

  public int getNumberOfKillers(){
	  return this.testFailure;
  }
  
  public int getNumberOfNonKillers(){
	  return this.testSuccess;
  }
  public void onRunEnd() {

  }

  public void onRunStart() {

  }

}
