package org.pitest.coverage;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.pitest.bytecode.FrameOptions;
import org.pitest.classpath.ClassloaderByteArraySource;
import org.pitest.coverage.codeassist.CoverageClassVisitor;
import org.pitest.functional.predicate.Predicate;
import org.pitest.util.ComputeClassWriter;

import sun.pitest.CodeCoverageStore;

public class CoverageTransformer implements ClassFileTransformer {

  private final Predicate<String>   filter;
  private final Map<String, String> computeCache = new ConcurrentHashMap<String, String>();

  public CoverageTransformer(final Predicate<String> filter) {
    this.filter = filter;
  }

  public byte[] transform(final ClassLoader loader, final String className,
      final Class<?> classBeingRedefined,
      final ProtectionDomain protectionDomain, final byte[] classfileBuffer)
      throws IllegalClassFormatException {
    final boolean include = shouldInclude(className);
    if (include) {
      try {
        return transformBytes(loader, className, classfileBuffer);
      } catch (final RuntimeException t) {
        System.err.println("RuntimeException while transforming  " + className);
        t.printStackTrace();
        throw t;
      }
    } else {
      return null;
    }
  }

  private byte[] transformBytes(final ClassLoader loader,
      final String className, final byte[] classfileBuffer) {
    final ClassReader reader = new ClassReader(classfileBuffer);
    final ClassWriter writer = new ComputeClassWriter(
        new ClassloaderByteArraySource(loader), this.computeCache,
        FrameOptions.pickFlags(classfileBuffer));

    final int id = CodeCoverageStore.registerClass(className);
    reader.accept(new CoverageClassVisitor(id, writer),
        ClassReader.EXPAND_FRAMES);
    return writer.toByteArray();
  }

  private boolean shouldInclude(final String className) {
    return this.filter.apply(className);
  }

}
