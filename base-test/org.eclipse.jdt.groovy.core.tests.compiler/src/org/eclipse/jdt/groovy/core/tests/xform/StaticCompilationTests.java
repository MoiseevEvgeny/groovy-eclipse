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
package org.eclipse.jdt.groovy.core.tests.xform;

import static org.eclipse.jdt.groovy.core.tests.GroovyBundle.isAtLeastGroovy;
import static org.junit.Assume.assumeTrue;

import org.eclipse.jdt.groovy.core.tests.basic.GroovyCompilerTestSuite;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test cases for {@link groovy.transform.CompileStatic} and {@link groovy.transform.CompileDynamic}.
 */
public final class StaticCompilationTests extends GroovyCompilerTestSuite {

    @Test
    public void testCompileDynamic() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "@groovy.transform.CompileStatic\n" +
            "class Main {\n" +
            "  int prop\n" +
            "  int computeStatic(int input) {\n" +
            "    prop + input\n" +
            "  }\n" +
            "  @groovy.transform.CompileDynamic\n" +
            "  int computeDynamic(int input) {\n" +
            "    missing(prop, input)\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");
    }

    @Test
    public void testCompileStatic1() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "@groovy.transform.CompileStatic\n" +
            "void test() {\n" +
            "  List<Integer> ls = new ArrayList<Integer>()\n" +
            "  ls.add(123)\n" +
            "  ls.add('abc')\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources,
            "----------\n" +
            "1. ERROR in Main.groovy (at line 5)\n" +
            "\tls.add('abc')\n" +
            "\t^^^^^^^^^^^^^\n" +
            "Groovy:[Static type checking] - Cannot call java.util.ArrayList <Integer>#add(java.lang.Integer) with arguments [java.lang.String] \n" +
            "----------\n");
    }

    /**
     * Testing the code in the StaticTypeCheckingSupport.checkCompatibleAssignmentTypes.
     *
     * That method does a lot of == testing against ClassNode constants, which may not work so well for us.
     */
    @Test
    public void testCompileStatic2() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "@groovy.transform.CompileStatic\n" +
            "class Main { \n" +
            "  String getPropertyValue(String propertyName, Properties props, String defaultValue) {\n" +
            "    // First check whether we have a system property with the given name.\n" +
            "    def value = getValueFromSystemOrBuild(propertyName, props)\n" +
            "    \n" +
            "    // Return the BuildSettings value if there is one, otherwise use the default.\n" +
            "    return value != null ? value : defaultValue \n" +
            "  }\n" +
            "  \n" +
            "  def getValueFromSystemOrBuild(String propertyName, Properties props) {\n" +
            "    def value = System.getProperty(propertyName)\n" +
            "    if (value != null) return value\n" +
            "    \n" +
            "    // Now try the BuildSettings config.\n" +
            "    value = props[propertyName]\n" +
            "    return value\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");
    }

    @Test
    public void testCompileStatic3() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "@groovy.transform.CompileStatic\n" +
            "void test(int primitive) {\n" +
            "  Integer wrapper = primitive\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");
    }

    @Test
    public void testCompileStatic4() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "@groovy.transform.CompileStatic\n" +
            "void test(Number n) {\n" +
            "  Integer i = n\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources,
            "----------\n" +
            "1. ERROR in Main.groovy (at line 3)\n" +
            "\tInteger i = n\n" +
            "\t            ^\n" +
            "Groovy:[Static type checking] - Cannot assign value of type java.lang.Number to variable of type java.lang.Integer\n" +
            "----------\n");
    }

    @Test
    public void testCompileStatic4a() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "@groovy.transform.CompileStatic\n" +
            "void test(Number n) {\n" +
            "  Object o\n" +
            "  Integer i = (o = n)\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources,
            "----------\n" +
            "1. ERROR in Main.groovy (at line 4)\n" +
            "\tInteger i = (o = n)\n" +
            "\t            ^^^^^^^\n" +
            "Groovy:[Static type checking] - Cannot assign value of type java.lang.Number to variable of type java.lang.Integer\n" +
            "----------\n");
    }

    @Test
    public void testCompileStatic5() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "@groovy.transform.CompileStatic\n" +
            "void test(Number n) {\n" +
            "  Object o = n\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");
    }

    @Test
    public void testCompileStatic6() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "@groovy.transform.CompileStatic\n" +
            "void test(Number n) {\n" +
            "  String s = n\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");
    }

    @Test
    public void testCompileStatic7() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "@groovy.transform.CompileStatic\n" +
            "void test(Number n) {\n" +
            "  boolean b = n\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");
    }

    @Test
    public void testCompileStatic7a() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "@groovy.transform.CompileStatic\n" +
            "void test(Number n) {\n" +
            "  Boolean b = n\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");
    }

    @Test // verify generics are correct for the 'Closure<?>' as CompileStatic will attempt an exact match
    public void testCompileStatic8() {
        //@formatter:off
        String[] sources = {
            "A.groovy",
            "class A {\n" +
            "  public void profile(String name, groovy.lang.Closure<?> callable) { }\n" +
            "}\n",

            "B.groovy",
            "@groovy.transform.CompileStatic\n" +
            "class B extends A {\n" +
            "  def foo() {\n" +
            "    profile('creating plugin manager with classes') {\n" +
            "      println 'abc'\n" +
            "    }\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");
    }

    @Test
    public void testCompileStatic9() {
        //@formatter:off
        String[] sources = {
            "FlowTyping.groovy",
            "@groovy.transform.CompileStatic\n" +
            "class FlowTyping {\n" +
            "  private Number number\n" +
            "  BigDecimal method() {\n" +
            "    return (number == null || number instanceof BigDecimal) \\\n" +
            "      ? (BigDecimal) number : new BigDecimal(number.toString())\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");
    }

    @Test // GROOVY-8337
    public void testCompileStatic10() {
        //@formatter:off
        String[] sources = {
            "FlowTyping.groovy",
            "@groovy.transform.CompileStatic\n" +
            "class FlowTyping {\n" +
            "  private Number number;\n" +
            "  private BigDecimal method() {\n" +
            "    return (number == null || number instanceof BigDecimal) ? number : new BigDecimal(number.toString());\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");
    }

    @Test
    public void testCompileStatic11() {
        //@formatter:off
        String[] sources = {
            "BridgeMethod.groovy",
            "@groovy.transform.CompileStatic\n" +
            "int compare(Integer integer) {\n" +
            "  if (integer.compareTo(0) == 0)\n" +
            "    return 0\n" +
            "  return 1\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");
    }

    @Test
    public void testCompileStatic13() {
        assumeTrue(isAtLeastGroovy(25));

        //@formatter:off
        String[] sources = {
            "q/Foo.groovy",
            "package q\n" +
            "class Foo {\n" +
            "  protected void m() {}\n" +
            "}\n",

            "r/Bar.groovy",
            "package r\n" +
            "@groovy.transform.CompileStatic\n" +
            "class Bar {\n" +
            "  void testM(q.Foo f) {\n" +
            "    f.m()\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources,
            "----------\n" +
            "1. ERROR in r\\Bar.groovy (at line 5)\n" +
            "\tf.m()\n" +
            "\t^^^^^\n" +
            "Groovy:[Static type checking] - Cannot find matching method q.Foo#m(). Please check if the declared type is correct and if the method exists.\n" +
            "----------\n");
    }

    @Test
    public void testCompileStatic1505() {
        //@formatter:off
        String[] sources = {
            "DynamicQuery.groovy",
            "import groovy.transform.TypeChecked\n" +
            "@TypeChecked\n" +
            "class DynamicQuery {\n" +
            "  public static void main(String[]argv) {\n" +
            "    new DynamicQuery().foo(null);\n" +
            "  }\n" +
            "  private foo(Map sumpin) {\n" +
            "    Map foo = [:]\n" +
            "    foo.collect{ Map.Entry it -> it.key }\n" +
            "    print 'abc';\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "abc");
    }

    @Test
    public void testCompileStatic1511() {
        //@formatter:off
        String[] sources = {
            "Foo.groovy",
            "@groovy.transform.CompileStatic\n" +
            "def meth() {\n" +
            "   List<String> one = []\n" +
            "   List<String> two = []\n" +
            "   one.addAll(two)\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");
    }

    @Test
    public void testCompileStatic1514() {
        //@formatter:off
        String[] sources = {
            "C.groovy",
            "@SuppressWarnings('rawtypes')\n" +
            "@groovy.transform.CompileStatic\n" +
            "class C {\n" +
            "  def xxx(List list) {\n" +
            "    list.unique().each { }\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");
    }

    @Test
    public void testCompileStatic1515() {
        //@formatter:off
        String[] sources = {
            "C.groovy",
            "import groovy.transform.CompileStatic;\n" +
            "import java.util.regex.Pattern\n" +
            "@CompileStatic\n" +
            "class C {\n" +
            "  void validate() {\n" +
            "    for (String validationKey : [:].keySet()) {\n" +
            "      String regex\n" +
            "      Pattern pattern = ~regex\n" + // NPE on this bitwise negation
            "    }\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");
    }

    @Test
    public void testCompileStatic1521() {
        //@formatter:off
        String[] sources = {
            "Foo.groovy",
            "@groovy.transform.CompileStatic\n" +
            "class Foo {\n" +
            "  enum Status { ON, OFF }\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");
    }

    @Test // https://issues.apache.org/jira/browse/GROOVY-7687
    public void testCompileStatic7687() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "@groovy.transform.CompileStatic\n" +
            "class Main {\n" +
            "  static class Foo {\n" +
            "    public List<Bar> bars = [new Bar()]\n" +
            "  }\n" +
            "  static class Bar {\n" +
            "    String message\n" +
            "  }\n" +
            "  void interactions(@DelegatesTo.Target Foo foo, @DelegatesTo(strategy=Closure.DELEGATE_FIRST) Closure block) {\n" +
            "    block.resolveStrategy = Closure.DELEGATE_FIRST\n" +
            "    block.delegate = foo\n" +
            "    block()\n" +
            "  }\n" +
            "  void execute(Foo foo) {\n" +
            "    interactions(foo) {\n" +
            "      bars.each { bar ->\n" + // ClassCastException
            "        bar.message = 'hello world'\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "  static main(args) {\n" +
            "    def foo = new Foo()\n" +
            "    new Main().execute(foo)\n" +
            "    print foo.bars*.message\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "[hello world]");
    }

    @Test // https://issues.apache.org/jira/browse/GROOVY-7687
    public void testCompileStatic7687a() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "@groovy.transform.CompileStatic\n" +
            "class Main {\n" +
            "  static class Foo {\n" +
            "    public List<Bar> bars = [new Bar()]\n" +
            "  }\n" +
            "  static class Bar {\n" +
            "    String message\n" +
            "  }\n" +
            "  void interactions(@DelegatesTo.Target Foo foo, @DelegatesTo(strategy=Closure.OWNER_FIRST) Closure block) {\n" +
            "    block.resolveStrategy = Closure.OWNER_FIRST\n" +
            "    block.delegate = foo\n" +
            "    block()\n" +
            "  }\n" +
            "  void execute(Foo foo) {\n" +
            "    interactions(foo) {\n" +
            "      bars.each { bar ->\n" + // ClassCastException
            "        bar.message = 'hello world'\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "  static main(args) {\n" +
            "    def foo = new Foo()\n" +
            "    new Main().execute(foo)\n" +
            "    print foo.bars*.message\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "[hello world]");
    }

    @Test // https://issues.apache.org/jira/browse/GROOVY-7687
    public void testCompileStatic7687b() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "@groovy.transform.CompileStatic\n" +
            "class Main {\n" +
            "  static class Foo {\n" +
            "    public List<String> messages = ['hello world']\n" +
            "  }\n" +
            "  void interactions(@DelegatesTo.Target Foo foo, @DelegatesTo Closure block) {\n" +
            "    block.delegate = foo\n" +
            "    block()\n" +
            "  }\n" +
            "  static main(args) {\n" +
            "    new Main().interactions(new Foo()) {\n" +
            "      messages.each { message ->\n" + // ClassCastException
            "        print message\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "hello world");
    }

    @Test // https://issues.apache.org/jira/browse/GROOVY-7996
    public void testCompileStatic7996() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "print new Bar().doStuff()\n",

            "Foo.groovy",
            "class Foo {\n" +
            "  def propertyMissing(String name) {\n" +
            "    return 'stuff'\n" +
            "  }\n" +
            "  def build(Closure block) {\n" +
            "    return this.with(block)\n" +
            "  }\n" +
            "}\n",

            "Bar.groovy",
            "@groovy.transform.CompileStatic\n" +
            "class Bar {\n" +
            "  protected List bars = []\n" +
            "  boolean doStuff() {\n" +
            "    new Foo().build {\n" +
            "      return bars.isEmpty()\n" + // ClassCastException: java.lang.String cannot be cast to java.util.List
            "    }\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "true");
    }

    @Test
    public void testCompileStatic7996a() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "print new Bar().doStuff()\n",

            "Foo.groovy",
            "class Foo {\n" +
            "  def propertyMissing(String name) {\n" +
            "    return 'stuff'\n" +
            "  }\n" +
            "  def build(@DelegatesTo(value=Foo, strategy=Closure.DELEGATE_FIRST) Closure block) {\n" +
            "    return this.with(block)\n" +
            "  }\n" +
            "}\n",

            "Bar.groovy",
            "@groovy.transform.CompileStatic\n" +
            "class Bar {\n" +
            "  protected List bars = []\n" +
            "  boolean doStuff() {\n" +
            "    new Foo().build {\n" +
            "      return bars.isEmpty()\n" + // ClassCastException: java.lang.String cannot be cast to java.util.List
            "    }\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "true");
    }

    @Test
    public void testCompileStatic7996b() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "print new Bar().doStuff()\n",

            "Foo.groovy",
            "class Foo {" +
            "  def propertyMissing(String name) {\n" +
            "    return 'stuff'\n" +
            "  }\n" +
            "  def build(@DelegatesTo(value=Foo, strategy=Closure.OWNER_FIRST) Closure block) {\n" +
            "    block.delegate = this\n" +
            "    return block.call()\n" +
            "  }\n" +
            "}\n",

            "Bar.groovy",
            "@groovy.transform.CompileStatic\n" +
            "class Bar {\n" +
            "  protected List bars = []\n" +
            "  boolean doStuff() {\n" +
            "    new Foo().build {\n" +
            "      return bars.isEmpty()\n" + // ClassCastException: java.lang.String cannot be cast to java.util.List
            "    }\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "true");
    }

    @Test
    public void testCompileStatic8342() {
        //@formatter:off
        String[] sources = {
            "Foo.groovy",
            "@groovy.transform.CompileStatic\n" +
            "class Foo {\n" +
            "  protected <T> List<T[]> bar(T thing) {\n" +
            "    return Collections.emptyList()\n" +
            "  }\n" +
            "  protected void baz() {\n" +
            "    List<Integer[]> list = bar(1)\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");
    }

    @Test
    public void testCompileStatic8638() {
        //@formatter:off
        String[] sources = {
            "Foo.groovy",
            "@groovy.transform.CompileStatic\n" +
            "class Foo {\n" +
            "  protected void bar(Multimap<String, Integer> mmap) {\n" +
            "    Map<String, Collection<Integer>> map = mmap.asMap()\n" +
            "    Set<Map.Entry<String, Collection<Integer>>> entrySet = map.entrySet()\n" +
            "    Iterator<Map.Entry<String, Collection<Integer>>> iter = entrySet.iterator()\n" +
            "    while (iter.hasNext()) {\n" +
            "      Map.Entry<String, Collection<Integer>> group = iter.next()\n" +
            "      Collection<Integer> values = group.value\n" +
            "    }\n" +
            "  }\n" +
            "}\n",

            "Multimap.java",
            "import java.util.*;\n" +
            "interface Multimap<K, V> {\n" +
            "  Map<K, Collection<V>> asMap();\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");
    }

    @Test
    public void testCompileStatic8509() {
        //@formatter:off
        String[] sources = {
            "p/Foo.groovy",
            "package p\n" +
            "class Foo {\n" +
            "  protected void m() {}\n" +
            "}\n",

            "p/Bar.groovy",
            "package p\n" +
            "@groovy.transform.CompileStatic\n" +
            "class Bar {\n" +
            "  void testM(Foo f) {\n" +
            "    f.m()\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");
    }

    @Test
    public void testCompileStatic8839() {
        //@formatter:off
        String[] sources = {
            "p/Main.groovy",
            "package p\n" +
            "import q.ResultHandle\n" +
            "@groovy.transform.CompileStatic\n" +
            "class Main {\n" +
            "  protected Map<String, ResultHandle[]> getResultsByType() {\n" +
            "    Map<String, ResultHandle[]> resultsByType = [:]\n" +
            "    // populate resultsByType\n" +
            "    return resultsByType\n" +
            "  }\n" +
            "}\n",

            "q/ResultHandle.java",
            "package q;\n" +
            "public class ResultHandle {\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");
    }

    @Test // https://issues.apache.org/jira/browse/GROOVY-8873
    public void testCompileStatic8873() {
        //@formatter:off
        String[] sources = {
            "Script.groovy",
            "class Foo {\n" +
            "  String foo = 'foo'\n" +
            "  String foom() { 'foom' }\n" +
            "}\n" +
            "class Bar {\n" +
            "  String bar = 'bar'\n" +
            "  String barm() { 'barm' }\n" +
            "}\n" +
            "class Baz {\n" +
            "  String baz = 'baz'\n" +
            "  String bazm() { 'bazm' }\n" +
            "}\n" +
            "String other() { 'other' }\n" +
            "\n" +
            "@groovy.transform.CompileStatic\n" +
            "void test() {\n" +
            "  new Foo().with {\n" +
            "    assert foo == 'foo'\n" +
            "    assert foom() == 'foom'\n" +
            "    assert other() == 'other'\n" +
            "    new Bar().with {\n" +
            "      assert foo == 'foo'\n" +
            "      assert bar == 'bar'\n" +
            "      assert foom() == 'foom'\n" +
            "      assert barm() == 'barm'\n" +
            "      assert other() == 'other'\n" +
            "      new Baz().with {\n" +
            "        assert foo == 'foo'\n" +
            "        assert bar == 'bar'\n" +
            "        assert baz == 'baz'\n" +
            "        assert foom() == 'foom'\n" +
            "        assert barm() == 'barm'\n" +
            "        assert bazm() == 'bazm'\n" +
            "        assert other() == 'other'\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "test()\n",
        };
        //@formatter:on

        runConformTest(sources, "");
    }

    @Test // https://issues.apache.org/jira/browse/GROOVY-9043
    public void testCompileStatic9043_nonStaticInnerToPackage() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "import groovy.transform.*\n" +
            "@CompileStatic class Main {\n" +
            "  @PackageScope static final String VALUE = 'value'\n" +
            "  class Inner {\n" +
            "    void meth() { print VALUE }\n" +
            "  }\n" +
            "  static main(args) {\n" +
            "    new Inner(new Main()).meth()\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "value");
    }

    @Test
    public void testCompileStatic9043_nonStaticInnerToProtected() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "import groovy.transform.*\n" +
            "@CompileStatic class Main {\n" +
            "  protected static final String VALUE = 'value'\n" +
            "  class Inner {\n" +
            "    void meth() { print VALUE }\n" +
            "  }\n" +
            "  static main(args) {\n" +
            "    new Inner(new Main()).meth()\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "value");
    }

    @Test
    public void testCompileStatic9043_nonStaticInnerToPublic() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "import groovy.transform.*\n" +
            "@CompileStatic class Main {\n" +
            "  public static final String VALUE = 'value'\n" +
            "  class Inner {\n" +
            "    void meth() { print VALUE }\n" +
            "  }\n" +
            "  static main(args) {\n" +
            "    new Inner(new Main()).meth()\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "value");
    }

    @Test
    public void testCompileStatic9043_nonStaticInnerToPrivate() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "import groovy.transform.*\n" +
            "@CompileStatic class Main {\n" +
            "  private static final String VALUE = 'value'\n" +
            "  class Inner {\n" +
            "    void meth() { print VALUE }\n" +
            "  }\n" +
            "  static main(args) {\n" +
            "    new Inner(new Main()).meth()\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "value");
    }

    @Test // https://issues.apache.org/jira/browse/GROOVY-9043
    public void testCompileStatic9043_staticInnerToPackage() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "import groovy.transform.*\n" +
            "@CompileStatic class Main {\n" +
            "  @PackageScope static final String VALUE = 'value'\n" +
            "  static class Inner {\n" +
            "    void meth() { print VALUE }\n" +
            "  }\n" +
            "  static main(args) {\n" +
            "    new Inner().meth()\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "value");
    }

    @Test
    public void testCompileStatic9043_staticInnerToProtected() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "import groovy.transform.*\n" +
            "@CompileStatic class Main {\n" +
            "  protected static final String VALUE = 'value'\n" +
            "  static class Inner {\n" +
            "    void meth() { print VALUE }\n" +
            "  }\n" +
            "  static main(args) {\n" +
            "    new Inner().meth()\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "value");
    }

    @Test
    public void testCompileStatic9043_staticInnerToPublic() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "import groovy.transform.*\n" +
            "@CompileStatic class Main {\n" +
            "  public static final String VALUE = 'value'\n" +
            "  static class Inner {\n" +
            "    void meth() { print VALUE }\n" +
            "  }\n" +
            "  static main(args) {\n" +
            "    new Inner().meth()\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "value");
    }

    @Test
    public void testCompileStatic9043_staticInnerToPrivate() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "import groovy.transform.*\n" +
            "@CompileStatic class Main {\n" +
            "  private static final String VALUE = 'value'\n" +
            "  static class Inner {\n" +
            "    void meth() { print VALUE }\n" +
            "  }\n" +
            "  static main(args) {\n" +
            "    new Inner().meth()\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "value");
    }

    @Test
    public void testCompileStatic9043_selfToPackage() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "import groovy.transform.*\n" +
            "@CompileStatic class Main {\n" +
            "  @PackageScope static final String VALUE = 'value'\n" +
            "  static main(args) {\n" +
            "    print VALUE\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "value");
    }

    @Test
    public void testCompileStatic9043_selfToProtected() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "import groovy.transform.*\n" +
            "@CompileStatic class Main {\n" +
            "  protected static final String VALUE = 'value'\n" +
            "  static main(args) {\n" +
            "    print VALUE\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "value");
    }

    @Test
    public void testCompileStatic9043_selfToPublic() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "import groovy.transform.*\n" +
            "@CompileStatic class Main {\n" +
            "  public static final String VALUE = 'value'\n" +
            "  static main(args) {\n" +
            "    print VALUE\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "value");
    }

    @Test
    public void testCompileStatic9043_selfToPrivate() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "import groovy.transform.*\n" +
            "@CompileStatic class Main {\n" +
            "  private static final String VALUE = 'value'\n" +
            "  static main(args) {\n" +
            "    print VALUE\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "value");
    }

    @Test
    public void testCompileStatic9043_peerToPackage() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "import groovy.transform.*\n" +
            "@CompileStatic class Main {\n" +
            "  @PackageScope static final String VALUE = 'value'\n" +
            "  static main(args) {\n" +
            "    new SamePack().meth()\n" +
            "  }\n" +
            "}\n" +
            "@CompileStatic class SamePack {\n" +
            "  void meth() {\n" +
            "    print Main.VALUE\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "value");
    }

    @Test
    public void testCompileStatic9043_peerToProtected() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "import groovy.transform.*\n" +
            "@CompileStatic class Main {\n" +
            "  protected static final String VALUE = 'value'\n" +
            "  static main(args) {\n" +
            "    new SamePack().meth()\n" +
            "  }\n" +
            "}\n" +
            "@CompileStatic class SamePack {\n" +
            "  void meth() {\n" +
            "    print Main.VALUE\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "value");
    }

    @Test
    public void testCompileStatic9043_peerToPublic() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "import groovy.transform.*\n" +
            "@CompileStatic class Main {\n" +
            "  public static final String VALUE = 'value'\n" +
            "  static main(args) {\n" +
            "    new SamePack().meth()\n" +
            "  }\n" +
            "}\n" +
            "@CompileStatic class SamePack {\n" +
            "  void meth() {\n" +
            "    print Main.VALUE\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "value");
    }

    @Test
    public void testCompileStatic9043_peerToPrivate() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "import groovy.transform.*\n" +
            "@CompileStatic class Main {\n" +
            "  private static final String VALUE = 'value'\n" +
            "  static main(args) {\n" +
            "    new SamePack().meth()\n" +
            "  }\n" +
            "}\n" +
            "@CompileStatic class SamePack {\n" +
            "  void meth() {\n" +
            "    print Main.VALUE\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources,
            "----------\n" +
            "1. ERROR in Main.groovy (at line 10)\n" +
            "\tprint Main.VALUE\n" +
            "\t      ^^^^\n" +
            "Groovy:Access to Main#VALUE is forbidden @ line 10, column 11.\n" +
            "----------\n");
    }

    @Test @Ignore("IllegalAccessError: tried to access field p.Main.VALUE from class q.Sub") // GROOVY-9093
    public void testCompileStatic9043_subToPackage() {
        //@formatter:off
        String[] sources = {
            "p/Main.groovy",
            "package p\n" +
            "import groovy.transform.*\n" +
            "@CompileStatic class Main {\n" +
            "  @PackageScope static final String VALUE = 'value'\n" +
            "  static main(args) {\n" +
            "    new q.Sub().meth()\n" +
            "  }\n" +
            "}\n",

            "q/Sub.groovy",
            "package q\n" +
            "import groovy.transform.*\n" +
            "@CompileStatic class Sub extends p.Main {\n" +
            "  void meth() {\n" +
            "    print VALUE\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources,
            "----------\n" +
            "1. ERROR in Sub.groovy\n" +
            "Groovy:Access to Main#VALUE is forbidden @ line -1, column -1.\n" +
            "----------\n");
    }

    @Test
    public void testCompileStatic9043_subToProtected() {
        //@formatter:off
        String[] sources = {
            "p/Main.groovy",
            "package p\n" +
            "import groovy.transform.*\n" +
            "@CompileStatic class Main {\n" +
            "  protected static final String VALUE = 'value'\n" +
            "  static main(args) {\n" +
            "    new q.Sub().meth()\n" +
            "  }\n" +
            "}\n",

            "q/Sub.groovy",
            "package q\n" +
            "import groovy.transform.*\n" +
            "@CompileStatic class Sub extends p.Main {\n" +
            "  void meth() {\n" +
            "    print VALUE\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "value");
    }

    @Test
    public void testCompileStatic9043_subToPublic() {
        //@formatter:off
        String[] sources = {
            "p/Main.groovy",
            "package p\n" +
            "import groovy.transform.*\n" +
            "@CompileStatic class Main {\n" +
            "  public static final String VALUE = 'value'\n" +
            "  static main(args) {\n" +
            "    new q.Sub().meth()\n" +
            "  }\n" +
            "}\n",

            "q/Sub.groovy",
            "package q\n" +
            "import groovy.transform.*\n" +
            "@CompileStatic class Sub extends p.Main {\n" +
            "  void meth() {\n" +
            "    print VALUE\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "value");
    }

    @Test @Ignore("MissingPropertyExceptionNoStack: No such property: VALUE for class: q.Sub") // GROOVY-9093
    public void testCompileStatic9043_subToPrivate() {
        //@formatter:off
        String[] sources = {
            "p/Main.groovy",
            "package p\n" +
            "import groovy.transform.*\n" +
            "@CompileStatic class Main {\n" +
            "  private static final String VALUE = 'value'\n" +
            "  static main(args) {\n" +
            "    new q.Sub().meth()\n" +
            "  }\n" +
            "}\n",

            "q/Sub.groovy",
            "package q\n" +
            "import groovy.transform.*\n" +
            "@CompileStatic class Sub extends p.Main {\n" +
            "  void meth() {\n" +
            "    print VALUE\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources,
            "----------\n" +
            "1. ERROR in Sub.groovy\n" +
            "Groovy:Access to Main#VALUE is forbidden @ line -1, column -1.\n" +
            "----------\n");
    }

    @Test
    public void testCompileStatic9058() {
        assumeTrue(isAtLeastGroovy(25));

        //@formatter:off
        String[] sources = {
            "p/Main.groovy",
            "package p\n" +
            "class Main {\n" +
            "  @groovy.transform.CompileStatic\n" +
            "  void meth() {\n" +
            "    List<Object[]> rows = new Foo().bar()\n" +
            "    rows.each { row ->\n" + // should be Object[]
            "      def col = row[0]\n" +
            "    }\n" +
            "  }\n" +
            "}\n",

            "p/Foo.java",
            "package p;\n" +
            "public class Foo {\n" +
            "  @SuppressWarnings(\"rawtypes\")\n" +
            "  public java.util.List bar() { return null; }\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "");
    }

    @Test @Ignore // https://issues.apache.org/jira/browse/GROOVY-9063
    public void testCompileStatic9063() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "@groovy.transform.CompileStatic\n" +
            "class Main {\n" +
            "  protected String message = 'hello'\n" +
            "  \n" +
            "  void meth() {\n" +
            "    { ->\n" +
            "      { ->\n" +
            "        print message.length()\n" +
            "      }.call()\n" +
            "    }.call()\n" +
            "  }\n" +
            "  \n" +
            "  static main(args) {\n" +
            "    new Main().meth()\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runConformTest(sources, "5");
    }

    @Test @Ignore // https://issues.apache.org/jira/browse/GROOVY-9074
    public void testCompileStatic9074() {
        //@formatter:off
        String[] sources = {
            "Main.groovy",
            "@groovy.transform.CompileStatic\n" +
            "class Main {\n" +
            "  private static Collection<?> c = new ArrayList<String>()\n" +
            "  static main(args) {\n" +
            "    c.add(new Object())\n" +
            "  }\n" +
            "}\n",
        };
        //@formatter:on

        runNegativeTest(sources, "The method add(capture#1-of ?) in the type Collection<capture#1-of ?> is not applicable for the arguments (Object)");
    }

    @Test // https://issues.apache.org/jira/browse/GROOVY-9086
    public void testCompileStatic9086() {
        //@formatter:off
        String[] sources = {
            "Script.groovy",
            "class C1 {\n" +
            "  void m1() {\n" +
            "    print 'outer delegate'\n" +
            "  }\n" +
            "}\n" +
            "class C2 {\n" +
            "  void m2() {\n" +
            "    print 'inner delegate'\n" +
            "  }\n" +
            "}\n" +
            "void outer(@DelegatesTo(value = C1, strategy = Closure.DELEGATE_FIRST) Closure block) {\n" +
            "  block.delegate = new C1()\n" +
            "  block.call()\n" +
            "}\n" +
            "void inner(@DelegatesTo(value = C2, strategy = Closure.DELEGATE_FIRST) Closure block) {\n" +
            "  block.delegate = new C2()\n" +
            "  block.call()\n" +
            "}\n" +
            "@groovy.transform.CompileStatic\n" +
            "void test() {\n" +
            "  outer {\n" +
            "    inner {\n" +
            "      m1()\n" +
            "      print ' '\n" +
            "      m2()\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "test()\n",
        };
        //@formatter:on

        runConformTest(sources, "outer delegate inner delegate");
    }

    @Test // https://issues.apache.org/jira/browse/GROOVY-9086
    public void testCompileStatic9086a() {
        //@formatter:off
        String[] sources = {
            "Script.groovy",
            "class C1 {\n" +
            "  void m1() {\n" +
            "    print 'outer delegate'\n" +
            "  }\n" +
            "}\n" +
            "class C2 {\n" +
            "  void m2() {\n" +
            "    print 'inner delegate'\n" +
            "  }\n" +
            "}\n" +
            "void outer(@DelegatesTo(value = C1, strategy = Closure.DELEGATE_FIRST) Closure block) {\n" +
            "  block.delegate = new C1()\n" +
            "  block.call()\n" +
            "}\n" +
            "void inner(@DelegatesTo(value = C2, strategy = Closure.OWNER_FIRST) Closure block) {\n" +
            "  block.delegate = new C2()\n" +
            "  block.call()\n" +
            "}\n" +
            "@groovy.transform.CompileStatic\n" +
            "void test() {\n" +
            "  outer {\n" +
            "    inner {\n" +
            "      m1()\n" +
            "      print ' '\n" +
            "      m2()\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "test()\n",
        };
        //@formatter:on

        runConformTest(sources, "outer delegate inner delegate");
    }

    @Test // https://issues.apache.org/jira/browse/GROOVY-9086
    public void testCompileStatic9086b() {
        //@formatter:off
        String[] sources = {
            "Script.groovy",
            "class C1 {\n" +
            "  void m1() {\n" +
            "    print 'outer delegate'\n" +
            "  }\n" +
            "}\n" +
            "class C2 {\n" +
            "  void m2() {\n" +
            "    print 'inner delegate'\n" +
            "  }\n" +
            "}\n" +
            "void outer(@DelegatesTo(value = C1, strategy = Closure.OWNER_FIRST) Closure block) {\n" +
            "  block.delegate = new C1()\n" +
            "  block.call()\n" +
            "}\n" +
            "void inner(@DelegatesTo(value = C2, strategy = Closure.DELEGATE_FIRST) Closure block) {\n" +
            "  block.delegate = new C2()\n" +
            "  block.call()\n" +
            "}\n" +
            "@groovy.transform.CompileStatic\n" +
            "void test() {\n" +
            "  outer {\n" +
            "    inner {\n" +
            "      m1()\n" +
            "      print ' '\n" +
            "      m2()\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "test()\n",
        };
        //@formatter:on

        runConformTest(sources, "outer delegate inner delegate");
    }

    @Test // https://issues.apache.org/jira/browse/GROOVY-9086
    public void testCompileStatic9086c() {
        //@formatter:off
        String[] sources = {
            "Script.groovy",
            "class C1 {\n" +
            "  void m1() {\n" +
            "    print 'outer delegate'\n" +
            "  }\n" +
            "}\n" +
            "class C2 {\n" +
            "  void m2() {\n" +
            "    print 'inner delegate'\n" +
            "  }\n" +
            "}\n" +
            "void outer(@DelegatesTo(value = C1, strategy = Closure.OWNER_FIRST) Closure block) {\n" +
            "  block.delegate = new C1()\n" +
            "  block.call()\n" +
            "}\n" +
            "void inner(@DelegatesTo(value = C2, strategy = Closure.OWNER_FIRST) Closure block) {\n" +
            "  block.delegate = new C2()\n" +
            "  block.call()\n" +
            "}\n" +
            "@groovy.transform.CompileStatic\n" +
            "void test() {\n" +
            "  outer {\n" +
            "    inner {\n" +
            "      m1()\n" +
            "      print ' '\n" +
            "      m2()\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "test()\n",
        };
        //@formatter:on

        runConformTest(sources, "outer delegate inner delegate");
    }

    @Test // https://issues.apache.org/jira/browse/GROOVY-9086
    public void testCompileStatic9086d() {
        //@formatter:off
        String[] sources = {
            "Script.groovy",
            "class C1 {\n" +
            "  void m() {\n" +
            "    print 'outer delegate'\n" +
            "  }\n" +
            "}\n" +
            "class C2 {\n" +
            "  void m() {\n" +
            "    print 'inner delegate'\n" +
            "  }\n" +
            "}\n" +
            "void outer(@DelegatesTo(value = C1, strategy = Closure.DELEGATE_FIRST) Closure block) {\n" +
            "  block.delegate = new C1()\n" +
            "  block.call()\n" +
            "}\n" +
            "void inner(@DelegatesTo(value = C2, strategy = Closure.DELEGATE_FIRST) Closure block) {\n" +
            "  block.delegate = new C2()\n" +
            "  block.call()\n" +
            "}\n" +
            "@groovy.transform.CompileStatic\n" +
            "void test() {\n" +
            "  outer {\n" +
            "    m()\n" +
            "    print ' '\n" +
            "    inner {\n" +
            "      m()\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "test()\n",
        };
        //@formatter:on

        runConformTest(sources, "outer delegate inner delegate");
    }

    @Test // https://issues.apache.org/jira/browse/GROOVY-9086
    public void testCompileStatic9086e() {
        //@formatter:off
        String[] sources = {
            "Script.groovy",
            "class C1 {\n" +
            "  void m() {\n" +
            "    print 'outer delegate'\n" +
            "  }\n" +
            "}\n" +
            "class C2 {\n" +
            "  void m() {\n" +
            "    print 'inner delegate'\n" +
            "  }\n" +
            "}\n" +
            "void outer(@DelegatesTo(value = C1, strategy = Closure.DELEGATE_FIRST) Closure block) {\n" +
            "  block.delegate = new C1()\n" +
            "  block.call()\n" +
            "}\n" +
            "void inner(@DelegatesTo(value = C2, strategy = Closure.OWNER_FIRST) Closure block) {\n" +
            "  block.delegate = new C2()\n" +
            "  block.call()\n" +
            "}\n" +
            "@groovy.transform.CompileStatic\n" +
            "void test() {\n" +
            "  outer {\n" +
            "    m()\n" +
            "    print ' '\n" +
            "    inner {\n" +
            "      m()\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "test()\n",
        };
        //@formatter:on

        runConformTest(sources, "outer delegate outer delegate");
    }

    @Test // https://issues.apache.org/jira/browse/GROOVY-9086
    public void testCompileStatic9086f() {
        //@formatter:off
        String[] sources = {
            "Script.groovy",
            "class C1 {\n" +
            "  void m() {\n" +
            "    print 'outer delegate'\n" +
            "  }\n" +
            "}\n" +
            "class C2 {\n" +
            "  void m() {\n" +
            "    print 'inner delegate'\n" +
            "  }\n" +
            "}\n" +
            "void outer(@DelegatesTo(value = C1, strategy = Closure.OWNER_FIRST) Closure block) {\n" +
            "  block.delegate = new C1()\n" +
            "  block.call()\n" +
            "}\n" +
            "void inner(@DelegatesTo(value = C2, strategy = Closure.OWNER_FIRST) Closure block) {\n" +
            "  block.delegate = new C2()\n" +
            "  block.call()\n" +
            "}\n" +
            "@groovy.transform.CompileStatic\n" +
            "void test() {\n" +
            "  outer {\n" +
            "    m()\n" +
            "    print ' '\n" +
            "    inner {\n" +
            "      m()\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "test()\n",
        };
        //@formatter:on

        runConformTest(sources, "outer delegate outer delegate");
    }

    @Test @Ignore // https://issues.apache.org/jira/browse/GROOVY-9089
    public void testCompileStatic9089() {
        //@formatter:off
        String[] sources = {
            "Script.groovy",
            "class C1 {\n" +
            "  void m() {\n" +
            "    print 'outer delegate'\n" +
            "  }\n" +
            "}\n" +
            "class C2 {\n" +
            "  void m() {\n" +
            "    print 'inner delegate'\n" +
            "  }\n" +
            "}\n" +
            "@SuppressWarnings('rawtypes')\n" +
            "void outer(@DelegatesTo(value = C1) Closure block) {\n" +
            "  block.delegate = new C1()\n" +
            "  block()\n" +
            "}\n" +
            "@SuppressWarnings('rawtypes')\n" +
            "void inner(@DelegatesTo(value = C2) Closure block, strategy = Closure.DELEGATE_FIRST) {\n" +
            "  block.delegate = new C2()\n" +
            "  block()\n" +
            "}\n" +
            "@groovy.transform.CompileStatic\n" +
            "void test() {\n" +
            "  outer {\n" +
            "    inner {\n" +
            "      owner.m()\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "test()\n",
        };
        //@formatter:on

        runConformTest(sources, "outer delegate");
    }

    @Test
    public void testCompileStatic9089a() {
        //@formatter:off
        String[] sources = {
            "Script.groovy",
            "class C1 {\n" +
            "  void m() {\n" +
            "    print 'outer delegate'\n" +
            "  }\n" +
            "}\n" +
            "class C2 {\n" +
            "  void m() {\n" +
            "    print 'inner delegate'\n" +
            "  }\n" +
            "}\n" +
            "@SuppressWarnings('rawtypes')\n" +
            "void outer(@DelegatesTo(value = C1) Closure block) {\n" +
            "  block.delegate = new C1()\n" +
            "  block()\n" +
            "}\n" +
            "@SuppressWarnings('rawtypes')\n" +
            "void inner(@DelegatesTo(value = C2) Closure block, strategy = Closure.OWNER_FIRST) {\n" +
            "  block.delegate = new C2()\n" +
            "  block()\n" +
            "}\n" +
            "@groovy.transform.CompileStatic\n" +
            "void test() {\n" +
            "  outer {\n" +
            "    inner {\n" +
            "      delegate.m()\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "test()\n",
        };
        //@formatter:on

        runConformTest(sources, "inner delegate");
    }
}
