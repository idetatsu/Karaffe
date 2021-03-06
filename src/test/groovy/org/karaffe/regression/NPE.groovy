package org.karaffe.regression

import org.karaffe.compiler.KaraffeCompiler
import org.karaffe.compiler.util.CompilerContext
import org.karaffe.compiler.util.KaraffeSource
import spock.lang.Specification

class NPE extends Specification {

  def "1"() {
    setup:
    def context = new CompilerContext()
    context.add(KaraffeSource.fromString(
      """class A {
                  |  enrypoint {
                  |    print()
                  |  }
                  |}""".stripMargin()))
    def compiler = new KaraffeCompiler(context)
    compiler.run()

    expect:
    context.hasError()
  }

  def "2"() {
    when:
    def context = new CompilerContext()
    context.parseRawArgs("-", "--emit", "ast")
    context.add(KaraffeSource.fromString("class\n"))
    def compiler = new KaraffeCompiler(context)
    compiler.run()

    then:
    notThrown(NullPointerException)
  }
}
