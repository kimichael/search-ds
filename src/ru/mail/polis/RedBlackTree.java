package ru.mail.polis;

import java.util.Comparator;
import java.util.List;

//TODO: write code here
public class RedBlackTree<E extends Comparable<E>> implements ISortedSet<E> {



    class Node {

        Node(E value, boolean isRed) {
            this.value = value;
            this.isRed = isRed;
        }

        Node(E value, Node parent, boolean isRed) {
            this.value = value;
            this.isRed = isRed;
            this.parent = parent;
        }

        E value;
        Node left;
        Node right;
        boolean isRed;
        Node parent;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("N{");
            sb.append("d=").append(value);
            if (left != null) {
                sb.append(", l=").append(left);
            }
            if (right != null) {
                sb.append(", r=").append(right);
            }
            sb.append('}');
            return sb.toString();
        }
    }

    private int size;
    private final Comparator<E> comparator;
    private final Node nilNode = new Node(null, false);
    private Node root;

    public RedBlackTree() {
        this.comparator = null;
        root = nilNode;
    }

    public RedBlackTree(Comparator<E> comparator) {
        this.comparator = comparator;
        root = nilNode;
    }

    @Override
    public E first() {
        return null;
    }

    @Override
    public E last() {
        return null;
    }

    @Override
    public List<E> inorderTraverse() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(E value) {
        return false;
    }

    @Override
    public boolean add(E value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (root == nilNode) {
            root = new Node(value, false);
        } else {
            Node curr = root;
            while (true) {
                int cmp = compare(curr.value, value);
                if (cmp == 0) {
                    return false;
                } else if (cmp < 0) {
                    if (curr.right != null) {
                        curr = curr.right;
                    } else {
                        curr.right = new Node(value, curr, true);
                        rebalance(curr.right);
                        break;
                    }
                } else if (cmp > 0) {
                    if (curr.left != null) {
                        curr = curr.left;
                    } else {
                        curr.left = new Node(value, curr, true);
                        rebalance(curr.left);
                        break;
                    }
                }
            }
        }
        size++;
        return true;
    }

    private void rebalance(Node node){
        //Если дядя красный
        Node uncle;
        if (node.parent == node.parent.parent.left){
            uncle = node.parent.parent.right;
        } else {
            uncle = node.parent.parent.left;
        }

    }

    @Override
    public boolean remove(E value) {
        return false;
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }
}
