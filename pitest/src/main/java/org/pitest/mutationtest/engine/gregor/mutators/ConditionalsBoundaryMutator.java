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
package org.pitest.mutationtest.engine.gregor.mutators;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.pitest.mutationtest.engine.gregor.AbstractJumpMutator;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.mutationtest.engine.gregor.MutationContext;

public enum ConditionalsBoundaryMutator implements MethodMutatorFactory {

  CONDITIONALS_BOUNDARY_MUTATOR;

  public MethodVisitor create(final MutationContext context,
      final MethodInfo methodInfo, final MethodVisitor methodVisitor) {
    return new ConditionalsBoundaryMethodVisitor(this, context, methodVisitor);
  }

  public String getGloballyUniqueId() {
    return this.getClass().getName();
  }

  public String getName() {
    return name();
  }

}

class ConditionalsBoundaryMethodVisitor extends AbstractJumpMutator {

  private static final String                     DESCRIPTION = "changed conditional boundary";
  private final static Map<Integer, Substitution> mutations   = new HashMap<Integer, Substitution>();

  static {
    mutations.put(Opcodes.IFLE, new Substitution(Opcodes.IFLT, DESCRIPTION));
    mutations.put(Opcodes.IFGE, new Substitution(Opcodes.IFGT, DESCRIPTION));
    mutations.put(Opcodes.IFGT, new Substitution(Opcodes.IFGE, DESCRIPTION));
    mutations.put(Opcodes.IFLT, new Substitution(Opcodes.IFLE, DESCRIPTION));
    mutations.put(Opcodes.IF_ICMPLE, new Substitution(Opcodes.IF_ICMPLT,
        DESCRIPTION));
    mutations.put(Opcodes.IF_ICMPGE, new Substitution(Opcodes.IF_ICMPGT,
        DESCRIPTION));
    mutations.put(Opcodes.IF_ICMPGT, new Substitution(Opcodes.IF_ICMPGE,
        DESCRIPTION));
    mutations.put(Opcodes.IF_ICMPLT, new Substitution(Opcodes.IF_ICMPLE,
        DESCRIPTION));
  }

  public ConditionalsBoundaryMethodVisitor(final MethodMutatorFactory factory,
      final MutationContext context, final MethodVisitor delegateMethodVisitor) {
    super(factory, context, delegateMethodVisitor);
  }

  @Override
  protected Map<Integer, Substitution> getMutations() {
    return mutations;
  }

}
