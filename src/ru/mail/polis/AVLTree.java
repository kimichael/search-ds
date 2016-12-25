package ru.mail.polis;

import java.util.*;

//TODO: write code here
public class AVLTree<E extends Comparable<E>> implements ISortedSet<E> {

    private Node root;
    private int size;
    private final Comparator<E> comparator;

    class Node {

        E value;
        Node left, right, parent;
        int balance;

        public Node(E value) {
            this.value = value;
        }

        public Node(E value, Node parent) {
            this.value = value;
            this.parent = parent;
            this.balance = 0;
        }

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

    public AVLTree() {
        this.comparator = null;
    }

    public AVLTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException("set is empty, no first element");
        }
        Node curr = root;
        while (curr.left != null) {
            curr = curr.left;
        }
        return curr.value;
    }

    @Override
    public E last() {
        if (isEmpty()) {
            throw new NoSuchElementException("set is empty, no last element");
        }
        Node curr = root;
        while (curr.right != null) {
            curr = curr.right;
        }
        return curr.value;
    }

    @Override
    public List<E> inorderTraverse() {
        List<E> list = new ArrayList<E>(size);
        inorderTraverse(root, list);
        return list;
    }

    private void inorderTraverse(Node curr, List<E> list) {
        if (curr == null) {
            return;
        }
        inorderTraverse(curr.left, list);
        list.add(curr.value);
        System.out.println(height(curr));
        inorderTraverse(curr.right, list);
    }

    @Override
    public int size() {
        if (size > 0) {
            return size;
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public boolean contains(E value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (root != null) {
            Node curr = root;
            while (curr != null) {
                int cmp = compare(curr.value, value);
                if (cmp == 0) {
                    return true;
                } else if (cmp < 0) {
                    curr = curr.right;
                } else {
                    curr = curr.left;
                }
            }
        }
        return false;
    }

    @Override
    public boolean add(E value) {
        //Сначала вставляем как в обычном бинарном дереве
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (root == null) {
            root = new Node(value);

        } else {
            Node curr = root;
            Node parent;
            while (true){
                if (curr.value == value)
                    return false;

                parent = curr;

                boolean toLeft = compare(curr.value, value) > 0;
                curr = toLeft ? curr.left : curr.right;

                if (curr == null){
                    if (toLeft){
                        parent.left = new Node(value, parent);
                    } else {
                        parent.right = new Node(value, parent);
                    }
                    rebalance(parent);
                    break;
                }
            }
        }
        size++;
        return true;
    }

    /**
     * Balance the tree starting from the given node
     * @param node node, which we want to rebalance
     */
    private void rebalance(Node node){
        setBalance(node);
        //Перевешено влево...
        if (node.balance == -2){
            //...а левый сын влево
            if (height(node.left.left) >= height(node.left.right))
                node = rotateRight(node);
            else
            //.. а левый сын вправо
                node = rotateLeftThenRight(node);
            //Перевешено вправо...
        } else if (node.balance == 2) {
            //...а правый сын вправо
            if (height(node.right.right) >= height(node.right.left))
                node = rotateLeft(node);
            else
                //...а правый сын влево
                node = rotateRightThenLeft(node);
        }

        if (node.parent != null){
            rebalance(node.parent);
        } else {
            root = node;
        }
    }
    //Малый поворот влево
    private Node rotateLeft(Node x){
        Node y = x.right;
        y.parent = x.parent;
        //Перекинем ветку
        x.right = y.left;
        //Подкорректируем указатель на родителя ветке
        if (x.right != null)
            x.right.parent = x;
        //Поворот
        y.left = x;
        x.parent = y;
        //Подкорректируем указатель у родителя на сына
        if (y.parent != null) {
            if (y.parent.right == x) {
                y.parent.right = y;
            } else {
                y.parent.left = y;
            }
        }

        setBalance(x, y);

        return y;
    }

    private Node rotateRight(Node x) {
        Node y = x.left;
        y.parent = x.parent;
        //Перекинем ветку
        x.left = y.right;

        if (x.left != null)
            x.left.parent = x;
        //Поворот
        y.right = x;
        x.parent = y;

        if (y.parent != null) {
            if (y.parent.right == x) {
                y.parent.right = y;
            } else {
                y.parent.left = y;
            }
        }

        setBalance(x, y);

        return y;
    }

    private Node rotateLeftThenRight(Node node) {
        node.left = rotateLeft(node.left);
        return rotateRight(node);
    }

    private Node rotateRightThenLeft(Node node) {
        node.right = rotateRight(node.right);
        return rotateLeft(node);
    }

    private void setBalance(Node... nodes){
        for (Node node : nodes){
            node.balance = height(node.right) - height(node.left);
        }
    }

    private int height(Node node){
        if (node == null)
            return -1;
        return 1 + Math.max(height(node.left), height(node.right));
    }

    @Override
    public boolean remove(E value) {
        if (value == null)
            throw new NullPointerException("value is null");
        if (root == null)
            return false;

        Node node = root;
        Node parent = root;
        Node deletedNode = null;
        Node child = root;
        //Найдем значение как обычно
        while (child != null){
            parent = node;
            node = child;
            child = compare(value, node.value) >= 0 ? node.right : node.left;
            if (value == node.value){
                deletedNode = node;
            }
        }
        //Если нашли
        if (deletedNode != null) {
            deletedNode.value = node.value;

            child = node.left != null ? node.left : node.right;

            //Если корень удаляем
            if (root.value == value) {
                root = child;
            } else {
                if (parent.left == node) {
                    parent.left = child;
                } else {
                    parent.right = child;
                }
                rebalance(parent);
            }
            size--;
            return true;
        }
        return false;
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }

    public String toString() {
        return "BST{" + root + "}";
    }

    public static void main(String[] args) {
        AVLTree<Integer> tree = new AVLTree<>();
        tree.add(10);
        tree.add(5);
        tree.add(15);
        System.out.println(tree.inorderTraverse());
        System.out.println(tree.size);
        System.out.println(tree);
        tree.remove(10);
        tree.remove(15);
        System.out.println(tree.size);
        System.out.println(tree);
        tree.remove(5);
        System.out.println(tree.size);
        System.out.println(tree);
        tree.add(15);
        System.out.println(tree.size);
        System.out.println(tree);

        System.out.println("------------");
        Random rnd = new Random();
        tree = new AVLTree<>();
        for (int i = 0; i < 15; i++) {
            tree.add(15);
        }
        System.out.println(tree);
        System.out.println(tree.inorderTraverse());
    }
}
