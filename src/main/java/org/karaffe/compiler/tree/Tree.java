package org.karaffe.compiler.tree;

import org.karaffe.compiler.tree.attr.Attribute;
import org.karaffe.compiler.tree.attr.Attributes;
import org.karaffe.compiler.util.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class Tree {
    private Position position;
    private NodeType nodeType;
    private String name;
    private Attributes attributes;
    private List<Tree> children;

    public Tree(NodeType nodeType, String name, Position position) {
        this.position = position;
        this.nodeType = Objects.requireNonNull(nodeType);
        this.name = Objects.requireNonNull(name);
        this.attributes = new Attributes();
        this.children = new ArrayList<>();
    }

    public void addAttribute(Attribute attribute) {
        this.attributes.add(Objects.requireNonNull(attribute));
    }

    public void addChild(Tree child) {
        this.children.add(Objects.requireNonNull(child));
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public String getName() {
        return name;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public List<Tree> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    public Optional<Tree> findFirstFromChildren(Predicate<Tree> p) {
        return this.getChildren().stream().filter(p).findFirst();
    }

    public Optional<Attribute> findFirstFromAttributes(Predicate<Attribute> p) {
        return this.getAttributes().stream().filter(p).findFirst();
    }

    @Override
    public String toString() {
        return String.format("%s (\"%s\", %s, %s)", this.nodeType.name(), this.name, this.attributes, this.children);
    }
}
