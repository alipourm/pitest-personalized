/*
 * Copyright 2012 Henry Coles
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
package org.pitest.mutationtest.engine.gregor.inlinedcode;

import static org.pitest.functional.FCollection.bucket;
import static org.pitest.functional.FCollection.map;
import static org.pitest.functional.FCollection.mapTo;
import static org.pitest.functional.prelude.Prelude.isEqualTo;
import static org.pitest.functional.prelude.Prelude.not;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import org.pitest.functional.FunctionalList;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.util.Log;

/**
 * Detects mutations on same line, but within different code blocks. This
 * pattern indicates code inlined for a finally block . . . or normal code that
 * creates two blocks on the same line.
 * 
 * Cannot be used with code that uses single line if statements
 */
public class InlinedFinallyBlockDetector implements InlinedCodeFilter {

  private final static Logger LOG = Log.getLogger();

  public Collection<MutationDetails> process(
      final Collection<MutationDetails> mutations) {
    final List<MutationDetails> combined = new ArrayList<MutationDetails>(
        mutations.size());
    final Map<LineMutatorPair, Collection<MutationDetails>> mutatorLinebuckets = bucket(
        mutations, toLineMutatorPair());

    for (final Entry<LineMutatorPair, Collection<MutationDetails>> each : mutatorLinebuckets
        .entrySet()) {
      if (each.getValue().size() > 1) {
        checkForInlinedCode(combined, each);
      } else {
        combined.addAll(each.getValue());
      }
    }

    /** FIXME tests rely on order of returned mutants **/
    Collections.sort(combined, compareLineNumbers());
    return combined;
  }

  private static Comparator<MutationDetails> compareLineNumbers() {
    return new Comparator<MutationDetails>() {

      public int compare(final MutationDetails arg0, final MutationDetails arg1) {
        return arg0.getLineNumber() - arg1.getLineNumber();
      }

    };
  }

  private void checkForInlinedCode(final Collection<MutationDetails> combined,
      final Entry<LineMutatorPair, Collection<MutationDetails>> each) {

    final FunctionalList<MutationDetails> mutationsInHandlerBlock = FCollection
        .filter(each.getValue(), isInFinallyHandler());
    if (!isPossibleToCorrectInlining(mutationsInHandlerBlock)) {
      combined.addAll(each.getValue());
      return;
    }

    final MutationDetails baseMutation = mutationsInHandlerBlock.get(0);
    final int firstBlock = baseMutation.getBlock();

    // check that we have at least on mutation in a different block
    // to the base one (is this not implied by there being only 1 mutation in
    // the handler ????)
    final FunctionalList<Integer> ids = map(each.getValue(), mutationToBlock());
    if (ids.contains(not(isEqualTo(firstBlock)))) {
      combined.add(makeCombinedMutant(each.getValue()));
    } else {
      combined.addAll(each.getValue());
    }
  }

  private boolean isPossibleToCorrectInlining(
      final List<MutationDetails> mutationsInHandlerBlock) {
    if (mutationsInHandlerBlock.size() > 1) {
      LOG.warning("Found more than one mutation similar on same line in a finally block. Can't correct for inlining.");
      return false;
    }

    return !mutationsInHandlerBlock.isEmpty();
  }

  private static F<MutationDetails, Boolean> isInFinallyHandler() {
    return new F<MutationDetails, Boolean>() {
      public Boolean apply(final MutationDetails a) {
        return a.isInFinallyBlock();
      }

    };
  }

  private static MutationDetails makeCombinedMutant(
      final Collection<MutationDetails> value) {
    final MutationDetails first = value.iterator().next();
    final Set<Integer> indexes = new HashSet<Integer>();
    mapTo(value, mutationToIndex(), indexes);

    final MutationIdentifier id = new MutationIdentifier(first.getId()
        .getLocation(), indexes, first.getId().getMutator());

    return new MutationDetails(id, first.getFilename(), first.getDescription(),
        first.getLineNumber(), first.getBlock());
  }

  private static F<MutationDetails, Integer> mutationToIndex() {
    return new F<MutationDetails, Integer>() {
      public Integer apply(final MutationDetails a) {
        return a.getFirstIndex();
      }
    };
  }

  private static F<MutationDetails, Integer> mutationToBlock() {
    return new F<MutationDetails, Integer>() {
      public Integer apply(final MutationDetails a) {
        return a.getBlock();
      }
    };
  }

  private static F<MutationDetails, LineMutatorPair> toLineMutatorPair() {
    return new F<MutationDetails, LineMutatorPair>() {
      public LineMutatorPair apply(final MutationDetails a) {
        return new LineMutatorPair(a.getLineNumber(), a.getMutator());
      }

    };
  }

}
