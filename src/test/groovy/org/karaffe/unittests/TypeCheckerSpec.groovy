package org.karaffe.unittests


import org.karaffe.compiler.frontend.karaffe.typechecker.TypeChecker
import org.karaffe.compiler.tree.NodeType
import org.karaffe.compiler.tree.Tree
import org.karaffe.compiler.util.CompilerContext
import org.karaffe.compiler.util.Position
import org.karaffe.compiler.util.TypeInfo
import spock.lang.Specification

class TypeCheckerSpec extends Specification {
  def "literal"() {
    def context = new CompilerContext()
    def checker = new TypeChecker(context)
    expect:
    checker.unify(tree) == result

    where:
    tree                                                       || result
    new Tree(NodeType.IntLiteral, "1", Position.noPos())       || new TypeInfo(int)
    new Tree(NodeType.StringLiteral, "Hoge", Position.noPos()) || new TypeInfo(String)
  }

  def "method"() {
    def context = new CompilerContext()
    def checker = new TypeChecker(context)
    expect:
    checker.unify(tree) == result

    where:
    tree                                                   || result
    new Tree(NodeType.DefMethod, "main", Position.noPos()) || new TypeInfo(Void)
  }

}
