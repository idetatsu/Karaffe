package org.karaffe.unittests

import org.karaffe.compiler.tree.NodeType
import org.karaffe.compiler.tree.Tree
import org.karaffe.compiler.tree.walker.TreeWalkerAdapter
import spock.lang.Specification

class TreeWalkerSpec extends Specification {

  private Tree tree

  def setup() {
    def apply = new Tree(NodeType.Apply)
    def a = new Tree(NodeType.Identifier, "A")
    def b = new Tree(NodeType.Identifier, "B")
    apply.addChild(a)
    apply.addChild(b)
    this.tree = apply
  }

  def "walk order"() {
    setup:
    def walker = new TreeWalkerAdapter() {
      @Override
      void onApply(Tree tree) {

      }

      @Override
      void onIdentifier(Tree tree) {

      }
    }

    when:
    walker.walk(tree)

    then:
    notThrown(AssertionError)
  }

  def "bailout"() {
    setup:
    def count = 0
    def walker = new TreeWalkerAdapter() {
      @Override
      void onApply(Tree tree) {
        assert false
      }

      @Override
      void onIdentifier(Tree tree) {
        count++
        bailout()
      }
    }
    def apply = new Tree(NodeType.Apply)
    def a = new Tree(NodeType.Identifier, "A")
    def b = new Tree(NodeType.Identifier, "B")
    apply.addChild(a)
    apply.addChild(b)

    when:
    walker.walk(apply)

    then:
    notThrown(AssertionError)

    expect:
    count == 1
  }
}
