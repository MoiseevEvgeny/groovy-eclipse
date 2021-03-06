/*
 * Copyright 2009-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.jdt.groovy.core.tests.basic;

import static org.eclipse.jdt.groovy.core.tests.GroovyBundle.isAtLeastGroovy;
import static org.junit.Assume.assumeTrue;

import org.junit.Test;

public final class InnerClassTests extends GroovyCompilerTestSuite {

    @Test
    public void testInnerTypeReferencing1() {
        //@formatter:off
        String[] sources = {
            "Script.groovy",
            "class Script {\n" +
            "  public static void main(String[] argv) {\n" +
            "    print Outer.Inner.VALUE\n" +
            "  }\n" +
            "}\n",

            "Outer.java",
            "public class Outer {\n" +
            "  public static class Inner {\n" +
            "    public static final String VALUE = \"value\";\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "value");
    }

    @Test // interface
    public void testInnerTypeReferencing2() {
        //@formatter:off
        String[] sources = {
            "Script.groovy",
            "class Script {\n" +
            "  public static void main(String[] argv) {\n" +
            "    print Outer.Inner.VALUE\n" +
            "  }\n" +
            "}\n",

            "Outer.java",
            "public interface Outer {\n" +
            "  public interface Inner {\n" +
            "    String VALUE = \"value\";\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "value");
    }

    @Test // script
    public void testInnerTypeReferencing3() {
        //@formatter:off
        String[] sources = {
            "script.groovy",
            "print Outer.Inner.VALUE\n",

            "Outer.java",
            "public interface Outer {\n" +
            "  public interface Inner {\n" +
            "    String VALUE = \"value\";\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "value");
    }

    @Test
    public void testInnerClass1() {
        //@formatter:off
        String[] sources = {
            "p/X.groovy",
            "package p;\n" +
            "public class X {\n" +
            " class Inner {}\n" +
            "  static main(args) {\n" +
            "    print \"success\"\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "success");

        checkGCUDeclaration("X.groovy",
            "package p;\n" +
            "public class X {\n" +
            "  public class Inner {\n" +
            "    public Inner() {\n" +
            "    }\n" +
            "  }\n" +
            "  public X() {\n" +
            "  }\n" +
            "  public static void main(java.lang.String... args) {\n" +
            "  }\n" +
            "}\n"
        );
    }

    @Test // https://github.com/groovy/groovy-eclipse/issues/718
    public void testInnerClass2() {
        //@formatter:off
        String[] sources = {
            "Outer.groovy",
            "class Outer {\n" +
            "  class Inner {\n" +
            "    static {\n" +
            "      println '<clinit>'\n" +
            "    }\n" +
            "  }\n" +
            "  def method() {\n" +
            "    new Inner()\n" +
            "  }\n" +
            "  static void main(args) {\n" +
            "    new Outer().method()\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "<clinit>");
    }

    @Test
    public void testInnerClass2a() {
        //@formatter:off
        String[] sources = {
            "Outer.groovy",
            "class Outer {\n" +
            "  static class Inner {\n" +
            "    static {\n" +
            "      println '<clinit>'\n" +
            "    }\n" +
            "  }\n" +
            "  static void main(args) {\n" +
            "    new Inner()\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "<clinit>");
    }

    @Test
    public void testInnerClass3() {
        //@formatter:off
        String[] sources = {
            "WithInnerClass.groovy",
            "class WithInnerClass {\n" +
            "  interface InnerInterface {\n" +
            "    void foo()\n" +
            "  }\n" +
            "  private final InnerInterface foo = new InnerInterface() {\n" +
            "     void foo() {\n" +
            "     }\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");
    }

    @Test // https://github.com/groovy/groovy-eclipse/issues/708
    public void testInnerClass4() {
        //@formatter:off
        String[] sources = {
            "Script.groovy",
            "import p.Sideways.Inner\n",

            "p/Outer.groovy",
            "package p\n" +
            "class Outer {\n" +
            "  static class Inner {\n" +
            "  }\n" +
            "}\n" +
            "class Sideways extends Outer {\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources,
            "----------\n" +
            "1. ERROR in Script.groovy (at line 1)\n" +
            "\timport p.Sideways.Inner\n" +
            "\t       ^^^^^^^^^^^^^^^^\n" +
            "Groovy:unable to resolve class p.Sideways.Inner\n" +
            "----------\n");
    }

    @Test
    public void testAnonymousInnerClass1() {
        //@formatter:off
        String[] sources = {
            "A.groovy",
            "def foo = new Runnable() {\n" +
            "  void run() {\n" +
            "    println 'hi!'\n" +
            "  }\n" +
            "}\n" +
            "foo.run()\n",
        };
        //@formatter:on

        runConformTest(sources, "hi!");

        checkGCUDeclaration("A.groovy",
            "public class A extends groovy.lang.Script {\n" +
            "  public A() {\n" +
            "  }\n" +
            "  public A(groovy.lang.Binding context) {\n" +
            "  }\n" +
            "  public static void main(java.lang.String... args) {\n" +
            "  }\n" +
            "  public @java.lang.Override java.lang.Object run() {\n" +
            "    java.lang.Object foo;\n" +
            "    new Runnable() {\n" +
            "      x() {\n" +
            "        super();\n" +
            "      }\n" +
            "      public void run() {\n" +
            "      }\n" +
            "    };\n" +
            "  }\n" +
            "}");
    }

    @Test
    public void testAnonymousInnerClass2() {
        //@formatter:off
        String[] sources = {
            "A.groovy",
            "class A {" +
            "  def foo = new Runnable() {\n" +
            "    void run() {\n" +
            "      println 'hi!'\n" +
            "    }\n" +
            "  }\n" +
            "  static main(args) {\n" +
            "    new A().foo.run()\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "hi!");

        checkGCUDeclaration("A.groovy",
            "public class A {\n" +
            "  private java.lang.Object foo = new Runnable() {\n" +
            "    x() {\n" +
            "      super();\n" +
            "    }\n" +
            "    public void run() {\n" +
            "    }\n" +
            "  };\n" +
            "  public A() {\n" +
            "  }\n" +
            "  public static void main(java.lang.String... args) {\n" +
            "  }\n" +
            "}");
    }

    @Test
    public void testAnonymousInnerClass2a() {
        //@formatter:off
        String[] sources = {
            "A.groovy",
            "class A {" +
            "  @Lazy def foo = new Runnable() {\n" +
            "    void run() {\n" +
            "      println 'hi!'\n" +
            "    }\n" +
            "  }\n" +
            "  static main(args) {\n" +
            "    new A().foo.run()\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "hi!");

        checkGCUDeclaration("A.groovy",
            "public class A {\n" +
            "  private @Lazy java.lang.Object foo = new Runnable() {\n" +
            "    x() {\n" +
            "      super();\n" +
            "    }\n" +
            "    public void run() {\n" +
            "    }\n" +
            "  };\n" +
            "  public A() {\n" +
            "  }\n" +
            "  public static void main(java.lang.String... args) {\n" +
            "  }\n" +
            "}");
    }

    @Test
    public void testAnonymousInnerClass3() {
        //@formatter:off
        String[] sources = {
            "A.groovy",
            "class A {" +
            "  def foo(int bar) {\n" +
            "    new Runnable() {\n" +
            "      void run() {\n" +
            "        println 'hi!'\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "  static main(args) {\n" +
            "    new A().foo(0).run()\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "hi!");

        checkGCUDeclaration("A.groovy",
            "public class A {\n" +
            "  public A() {\n" +
            "  }\n" +
            "  public java.lang.Object foo(int bar) {\n" +
            "    new Runnable() {\n" +
            "      x() {\n" +
            "        super();\n" +
            "      }\n" +
            "      public void run() {\n" +
            "      }\n" +
            "    };\n" +
            "  }\n" +
            "  public static void main(java.lang.String... args) {\n" +
            "  }\n" +
            "}");
    }

    @Test
    public void testAnonymousInnerClass4() {
        //@formatter:off
        String[] sources = {
            "A.groovy",
            "class A {" +
            "  def foo(int bar, int baz = 0) {\n" +
            "    new Runnable() {\n" +
            "      void run() {\n" +
            "        println 'hi!'\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "  static main(args) {\n" +
            "    new A().foo(0, 1).run()\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "hi!");

        checkGCUDeclaration("A.groovy",
            "public class A {\n" +
            "  public A() {\n" +
            "  }\n" +
            "  public java.lang.Object foo(int bar, int baz) {\n" +
            "    new Runnable() {\n" +
            "      x() {\n" +
            "        super();\n" +
            "      }\n" +
            "      public void run() {\n" +
            "      }\n" +
            "    };\n" +
            "  }\n" +
            "  public java.lang.Object foo(int bar) {\n" +
            "  }\n" +
            "  public static void main(java.lang.String... args) {\n" +
            "  }\n" +
            "}");
    }

    @Test
    public void testAnonymousInnerClass5() {
        //@formatter:off
        String[] sources = {
            "A.groovy",
            "def foo = new Runnable() {\n" +
            "  void run() {\n" +
            "    println 'bye!'\n" +
            "  }\n" +
            "}\n" +
            "foo = new Runnable() {\n" +
            "  void run() {\n" +
            "    println 'hi!'\n" +
            "  }\n" +
            "}\n" +
            "foo.run()",
        };
        //@formatter:on

        runConformTest(sources, "hi!");
    }

    @Test
    public void testAnonymousInnerClass6() {
        //@formatter:off
        String[] sources = {
            "A.groovy",
            "def foo() {\n" +
            "  new Runnable() {\n" +
            "    void run() {\n" +
            "      println 'hi!'\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "foo().run()",
        };
        //@formatter:on

        runConformTest(sources, "hi!");
    }

    @Test
    public void testAnonymousInnerClass7() {
        //@formatter:off
        String[] sources = {
            "A.groovy",
            "class Foo {\n" +
            "  def foo = new Runnable() {\n" +
            "    void run() {\n" +
            "      println 'hi!'\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "new Foo().foo.run()\n",
        };
        //@formatter:on

        runConformTest(sources, "hi!");
    }

    @Test
    public void testAnonymousInnerClass8() {
        //@formatter:off
        String[] sources = {
            "A.groovy",
            "def foo = new Runnable() {\n" +
            "  void bad() {\n" +
            "    println 'hi!'\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources,
            "----------\n" +
            "1. ERROR in A.groovy (at line 1)\n" +
            "\tdef foo = new Runnable() {\n" +
            "\t              ^^^^^^^^^^\n" +
            "Groovy:Can't have an abstract method in a non-abstract class." +
            " The class 'A$1' must be declared abstract or the method 'void run()' must be implemented.\n" +
            "----------\n");
    }

    @Test
    public void testAnonymousInnerClass9() {
        //@formatter:off
        String[] sources = {
            "A.groovy",
            "class A {\n" +
            "  static {\n" +
            "    def foo = new Runnable() {\n" +
            "    }\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources,
            "----------\n" +
            "1. ERROR in A.groovy (at line 3)\n" +
            "\tdef foo = new Runnable() {\n" +
            "\t              ^^^^^^^^^^\n" +
            "Groovy:Can't have an abstract method in a non-abstract class." +
            " The class 'A$1' must be declared abstract or the method 'void run()' must be implemented.\n" +
            "----------\n");

        checkGCUDeclaration("A.groovy",
            "public class A {\n" +
            "  public A() {\n" +
            "  }\n" +
            "  <clinit>() {\n" +
            "    java.lang.Object foo;\n" +
            "    new Runnable() {\n" +
            "      x() {\n" +
            "        super();\n" +
            "      }\n" +
            "    };\n" +
            "  }\n" +
            "}");
    }

    @Test
    public void testAnonymousInnerClass9a() {
        //@formatter:off
        String[] sources = {
            "A.groovy",
            "class A {\n" +
            "  {\n" +
            "    def foo = new Runnable() {\n" +
            "    }\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources,
            "----------\n" +
            "1. ERROR in A.groovy (at line 3)\n" +
            "\tdef foo = new Runnable() {\n" +
            "\t              ^^^^^^^^^^\n" +
            "Groovy:Can't have an abstract method in a non-abstract class." +
            " The class 'A$1' must be declared abstract or the method 'void run()' must be implemented.\n" +
            "----------\n");

        checkGCUDeclaration("A.groovy",
            "public class A {\n" +
            "  public A() {\n" +
            "    new Runnable() {\n" +
            "      x() {\n" +
            "        super();\n" +
            "      }\n" +
            "    };\n" +
            "  }\n" +
            "}");
    }

    @Test // https://github.com/groovy/groovy-eclipse/issues/715
    public void testAnonymousInnerClass10() {
        //@formatter:off
        String[] sources = {
            "A.groovy",
            "class A {" +
            "  def foo = new I<String>() {\n" +
            "    private static final long serialVersionUID = 1L\n" +
            "    String bar() {\n" +
            "      println 'hi!'\n" +
            "    }\n" +
            "  }\n" +
            "  static main(args) {\n" +
            "    new A().foo.bar()\n" +
            "  }\n" +
            "}\n",

            "I.groovy",
            "interface I<T> extends Serializable {\n" +
            "  T bar()\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "hi!");

        checkGCUDeclaration("A.groovy",
            "public class A {\n" +
            "  private java.lang.Object foo = new I<String>() {\n" +
            "    private static final long serialVersionUID = 1L;\n" +
            "    x() {\n" +
            "      super();\n" +
            "    }\n" +
            "    public String bar() {\n" +
            "    }\n" +
            "  };\n" +
            "  public A() {\n" +
            "  }\n" +
            "  public static void main(java.lang.String... args) {\n" +
            "  }\n" +
            "}\n");
    }

    @Test // https://github.com/groovy/groovy-eclipse/issues/800
    public void testAnonymousInnerClass11() {
        //@formatter:off
        String[] sources = {
            "A.groovy",
            "class A {" +
            // field with anon. inner initializer argument:
            "  C cee = new C(1, '2', new Runnable() {\n" +
            "    void run() {\n" +
            "      println 'hi!'\n" +
            "    }\n" +
            "  })\n" +
            "  static main(args) {\n" +
            "    new A()\n" +
            "  }\n" +
            "}\n",

            "C.groovy",
            "class C {\n" +
            "  C(int one, String two, Runnable three) {\n" +
            "    three.run()\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "hi!");

        checkGCUDeclaration("A.groovy",
            "public class A {\n" +
            "  private C cee = (C) (java.lang.Object) new Runnable() {\n" +
            "  x() {\n" +
            "    super();\n" +
            "  }\n" +
            "  public void run() {\n" +
            "  }\n" +
            "};\n" +
            "  public A() {\n" +
            "  }\n" +
            "  public static void main(java.lang.String... args) {\n" +
            "  }\n" +
            "}\n");
    }

    @Test
    public void testAnonymousInnerClass11a() {
        //@formatter:off
        String[] sources = {
            "A.groovy",
            "class A {" +
            // field with anon. inner initializer argument:
            "  C cee = newC(1, '2', new Runnable() {\n" +
            "    void run() {\n" +
            "    }\n" +
            "  })\n" +
            "  static C newC(int one, String two, Runnable three) {\n" +
            "    new C()\n" +
            "  }\n" +
            "  static main(args) {\n" +
            "    new A()\n" +
            "  }\n" +
            "}\n",

            "C.groovy",
            "class C {\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");

        checkGCUDeclaration("A.groovy",
            "public class A {\n" +
            "  private C cee = (C) (java.lang.Object) new Runnable() {\n" +
            "  x() {\n" +
            "    super();\n" +
            "  }\n" +
            "  public void run() {\n" +
            "  }\n" +
            "};\n" +
            "  public A() {\n" +
            "  }\n" +
            "  public static C newC(int one, String two, Runnable three) {\n" +
            "  }\n" +
            "  public static void main(java.lang.String... args) {\n" +
            "  }\n" +
            "}\n");
    }

    @Test
    public void testAnonymousInnerClass11b() {
        //@formatter:off
        String[] sources = {
            "A.groovy",
            "class A {" +
            // field with anon. inner initializer argument:
            "  C cee = newC().one(1).two('2').three(new Runnable() {\n" +
            "    void run() {\n" +
            "    }\n" +
            "  })\n" +
            "  static C newC() {\n" +
            "    new C()\n" +
            "  }\n" +
            "  static main(args) {\n" +
            "    new A()\n" +
            "  }\n" +
            "}\n",

            "C.groovy",
            "class C {\n" +
            "  C one(int i) {\n" +
            "    this\n" +
            "  }\n" +
            "  C two(String s) {\n" +
            "    this\n" +
            "  }\n" +
            "  C three(Runnable r) {\n" +
            "    this\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");

        checkGCUDeclaration("A.groovy",
            "public class A {\n" +
            "  private C cee = (C) (java.lang.Object) new Runnable() {\n" +
            "  x() {\n" +
            "    super();\n" +
            "  }\n" +
            "  public void run() {\n" +
            "  }\n" +
            "};\n" +
            "  public A() {\n" +
            "  }\n" +
            "  public static C newC() {\n" +
            "  }\n" +
            "  public static void main(java.lang.String... args) {\n" +
            "  }\n" +
            "}\n");
    }

    @Test
    public void testAnonymousInnerClass12() {
        //@formatter:off
        String[] sources = {
            "A.groovy",
            "class A {" +
            "  static main(args) {\n" +
            // local with anon. inner initializer argument:
            "    C cee = new C(1, '2', new Runnable() {\n" +
            "      void run() {\n" +
            "        println 'hi!'\n" +
            "      }\n" +
            "    })\n" +
            "  }\n" +
            "}\n",

            "C.groovy",
            "class C {\n" +
            "  C(int one, String two, Runnable three) {\n" +
            "    three.run()\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "hi!");

        checkGCUDeclaration("A.groovy",
            "public class A {\n" +
            "  public A() {\n" +
            "  }\n" +
            "  public static void main(java.lang.String... args) {\n" +
            "    C cee;\n" +
            "    new Runnable() {\n" +
            "      x() {\n" +
            "        super();\n" +
            "      }\n" +
            "      public void run() {\n" +
            "      }\n" +
            "    };\n" +
            "  }\n" +
            "}\n");
    }

    @Test
    public void testAnonymousInnerClass13() {
        //@formatter:off
        String[] sources = {
            "C.groovy",
            "class C {\n" +
            "  int count\n" +
            "  @SuppressWarnings('rawtypes')\n" +
            "  static def m() {\n" +
            "    new LinkedList() {\n" +
            "      @Override\n" +
            "      def get(int i) {\n" +
            "        count += 1\n" +
            "        super.get(i)\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources,
            "----------\n" +
            "1. ERROR in C.groovy (at line 8)\n" +
            "\tcount += 1\n" +
            "\t^^^^^\n" +
            "Groovy:Apparent variable 'count' was found in a static scope but doesn't refer to a local variable, static field or class. Possible causes:\n" +
            "----------\n");
    }

    @Test
    public void testAnonymousInnerClass14() {
        //@formatter:off
        String[] sources = {
            "C.groovy",
            "class C {\n" +
            "  static int count\n" +
            "  @SuppressWarnings('rawtypes')\n" +
            "  static def m() {\n" +
            "    new LinkedList() {\n" +
            "      @Override\n" +
            "      def get(int i) {\n" +
            "        count += 1\n" +
            "        super.get(i)\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");
    }

    @Test
    public void testAnonymousInnerClass15() {
        //@formatter:off
        String[] sources = {
            "C.groovy",
            "class C {\n" +
            "  @SuppressWarnings('rawtypes')\n" +
            "  static def m() {\n" +
            "    int count = 0\n" +
            "    new LinkedList() {\n" +
            "      @Override\n" +
            "      def get(int i) {\n" +
            "        count += 1\n" +
            "        super.get(i)\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");
    }

    @Test
    public void testAnonymousInnerClass16() {
        //@formatter:off
        String[] sources = {
            "C.groovy",
            "class C {\n" +
            "  @SuppressWarnings('rawtypes')\n" +
            "  static def m(int count) {\n" +
            "    new LinkedList() {\n" +
            "      @Override\n" +
            "      def get(int i) {\n" +
            "        count += 1\n" +
            "        super.get(i)\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");
    }

    @Test // https://issues.apache.org/jira/browse/GROOVY-5961
    public void testAnonymousInnerClass17() {
        assumeTrue(isAtLeastGroovy(25));

        //@formatter:off
        String[] sources = {
            "Script.groovy",
            "@SuppressWarnings('rawtypes')\n" +
            "static def m() {\n" +
            "  new LinkedList() {\n" +
            "    int count\n" +
            "    @Override\n" +
            "    def get(int i) {\n" +
            "      count += 1\n" + // Apparent variable 'count' was found in a static scope but doesn't refer to a local variable, static field or class.
            "      super.get(i)\n" +
            "    }\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");
    }

    @Test // https://issues.apache.org/jira/browse/GROOVY-5961
    public void testAnonymousInnerClass18() {
        assumeTrue(isAtLeastGroovy(25));

        //@formatter:off
        String[] sources = {
            "Abstract.groovy",
            "abstract class Abstract {\n" +
            "  abstract def find(key)\n" +
            "  @SuppressWarnings('rawtypes')\n" +
            "  protected Map map = [:]\n" +
            "}\n",

            "Script.groovy",
            "static def m() {\n" +
            "  def anon = new Abstract() {\n" +
            "    @Override\n" +
            "    def find(key) {\n" +
            "      map.get(key)\n" + // Apparent variable 'map' was found in a static scope but doesn't refer to a local variable, static field or class.
            "    }\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");
    }

    @Test
    public void testAnonymousInnerClass19() {
        //@formatter:off
        String[] sources = {
            "Script.groovy",
            "print Main.bar()\n" +
            "print ' '\n" +
            "Main.foo = 2\n" +
            "print Main.bar()\n",

            "Main.groovy",
            "class Main {\n" +
            "  static foo = 1\n" +
            "  static bar() {\n" +
            "    def impl = new java.util.function.Supplier() {\n" +
            "      @Override def get() {\n" +
            "        return foo\n" +
            "      }\n" +
            "    }\n" +
            "    return impl.get()\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "1 2");
    }

    @Test
    public void testAnonymousInnerClass20() {
        //@formatter:off
        String[] sources = {
            "Script.groovy",
            "def bar = new Bar()\n" +
            "print bar.baz()\n",

            "Types.groovy",
            "class Foo {\n" +
            "  static baz() {\n" +
            "    def impl = new java.util.function.Supplier() {\n" +
            "      @Override def get() {\n" +
            "        return x()\n" +
            "      }\n" +
            "    }\n" +
            "    return impl.get()\n" +
            "  }\n" +
            "  private static def x() { 'foo' }\n" +
            "}\n" +
            "class Bar extends Foo {\n" +
            "  private static def x() { 'bar' }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "foo");
    }

    @Test
    public void testMixedModeInnerProperties_GRE597() {
        //@formatter:off
        String[] sources = {
            "gr8/JointGroovy.groovy",
            "package gr8\n" +
            "\n" +
            "class JointGroovy {\n" +
            "StaticInner property\n" +
            "\n" +
            " static class StaticInner {\n" +
            "  NonStaticInner property2\n" +
            "\n" +
            "  class NonStaticInner {\n" +
            "    Closure property3 = {}\n" +
            "  }\n" +
            " }\n" +
            "}",

            "gr8/JointJava.java",
            "package gr8;\n" +
            "\n" +
            "import groovy.lang.Closure;\n" +
            "\n" +
            "public class JointJava {\n" +
            "    public void method() {\n" +
            "        Closure closure = new JointGroovy().getProperty().getProperty2().getProperty3();\n" +
            "    }\n" +
            "}",
        };
        //@formatter:on

        runConformTest(sources);
    }

    @Test
    public void testMixedModeInnerProperties2_GRE597() {
        //@formatter:off
        String[] sources = {
            "gr8/JointGroovy.groovy",
            "package gr8\n" +
            "\n" +
            "class JointGroovy {\n" +
            "StaticInner property\n" +
            "\n" +
            " }\n" +
            // now the inner is not an inner (like the previous test) but the property3 still is
            " class StaticInner {\n" +
            "  NonStaticInner property2\n" +
            "\n" +
            "  class NonStaticInner {\n" +
            "    Closure property3 = {}\n" +
            "  }\n" +
            "}",

            "gr8/JointJava.java",
            "package gr8;\n" +
            "\n" +
            "import groovy.lang.Closure;\n" +
            "\n" +
            "public class JointJava {\n" +
            "    public void method() {\n" +
            "        Closure closure = new JointGroovy().getProperty().getProperty2().getProperty3();\n" +
            "    }\n" +
            "}",
        };
        //@formatter:on

        runConformTest(sources);
    }

    @Test // Ensures that the Point2D.Double reference is resolved in the context of X and not Y (if Y is used then the import isn't found)
    public void testMemberTypeResolution() {
        //@formatter:off
        String[] sources = {
            "p/X.groovy",
            "package p;\n" +
            "import java.awt.geom.Point2D;\n" +
            "public class X {\n" +
            "  public void foo() {\n" +
            "    Object o = new Point2D.Double(p.x(),p.y());\n" +
            "  }\n" +
            "  public static void main(String[] argv) {\n" +
            "    print \"success\"\n" +
            "  }\n" +
            "}\n",

            "p/Y.groovy",
            "package p;\n" +
            "public class Y {\n" +
            "  public void foo() {\n" +
            "  }\n" +
            "  public static void main(String[] argv) {\n" +
            "    print \"success\"\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "success");
    }
}
