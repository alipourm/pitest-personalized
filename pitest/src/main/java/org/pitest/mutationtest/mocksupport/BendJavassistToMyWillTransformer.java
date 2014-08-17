package org.pitest.mutationtest.mocksupport;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.pitest.bytecode.FrameOptions;
import org.pitest.functional.predicate.Predicate;

public class BendJavassistToMyWillTransformer implements ClassFileTransformer {

  private final Predicate<String> filter;

  public BendJavassistToMyWillTransformer(final Predicate<String> filter) {
    this.filter = filter;
  }

  public byte[] transform(final ClassLoader loader, final String className,
      final Class<?> classBeingRedefined,
      final ProtectionDomain protectionDomain, final byte[] classfileBuffer)
      throws IllegalClassFormatException {

    if (shouldInclude(className)) {

      final ClassReader reader = new ClassReader(classfileBuffer);
      final ClassWriter writer = new ClassWriter(FrameOptions.pickFlags(classfileBuffer));

      reader.accept(new JavassistInputStreamInterceptorAdapater(writer), ClassReader.EXPAND_FRAMES);
      return writer.toByteArray();
    } else {
      return null;
    }
  }

  private boolean shouldInclude(final String className) {
    return this.filter.apply(className);
  }

}
