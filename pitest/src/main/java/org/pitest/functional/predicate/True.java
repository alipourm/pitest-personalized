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
package org.pitest.functional.predicate;

/**
 * @author henry
 * 
 */
public class True<A> implements Predicate<A> {

  private final static True<?> INSTANCE = new True<Object>();

  @SuppressWarnings("unchecked")
  public static <A> Predicate<A> all() {
    return (True<A>) INSTANCE;
  }

  public Boolean apply(final A a) {
    return true;
  }

}
