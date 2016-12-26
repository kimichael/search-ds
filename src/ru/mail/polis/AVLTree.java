package ru.mail.polis;

import java.util.*;

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
        size = 0;
    }

    public AVLTree(Comparator<E> comparator) {
        this.comparator = comparator;
        size = 0;
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
        inorderTraverse(curr.right, list);
    }

    @Override
    public int size() {
        if (size >= 0)
            return size;
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

    /**
     * Balance the tree starting from the given node
     * @param node node, which we want to rebalance
     */
    private void rebalance(Node node){
        if (node == null)
            return;
        setBalance(node);

        //Перевешено влево...
        if (node.balance == -2){
            //...а левый сын влево
            if (height(node.left.left) >= height(node.left.right)) {
                node = rotateRight(node);
            } else {
                //.. а левый сын вправо
                node = bigRotateLeft(node);
            }
            //Перевешено вправо...
        } else if (node.balance == 2) {
            //...а правый сын вправо
            if (height(node.right.right) >= height(node.right.left))
                node = rotateLeft(node);
            else
                //...а правый сын влево
                node = bigRotateRight(node);
        }

        if (node.parent != null) {
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
        //Подкорректируем указатель на родительской ветке
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

        setBalance(x);
        setBalance(y);

        return y;
    }

    //Малый поворот вправо
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

        setBalance(x);
        setBalance(y);

        return y;
    }

    //Большой поворот влево
    private Node bigRotateLeft(Node node) {
        node.left = rotateLeft(node.left);
        return rotateRight(node);
    }

    //Большой поворот вправо
    private Node bigRotateRight(Node node) {
        node.right = rotateRight(node.right);
        return rotateLeft(node);
    }

    private void setBalance(Node node){
        node.balance = height(node.right) - height(node.left);
    }

    private int height(Node node){
        if (node == null){
            return 0;
        }
        return Math.max(height(node.left), height(node.right)) + 1;
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }

    public String toString() {
        return "AVL{" + root + "}";
    }


    @Override
    public boolean remove(E value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (root == null) {
            return false;
        }

        Node nodeToDel = null;
        Node child = root;
        Node curr = root;
        int cmp;

        //Ищем ноду
        while (child != null) {
            curr = child;
            cmp = compare(value, curr.value);
            if (cmp >= 0) child = child.right;
            else child = curr.left;
            if (value == curr.value)
                nodeToDel = curr;
        }

        //Не нашли
        if (nodeToDel == null)
            return false;

        Node parent = curr.parent;
        nodeToDel.value = curr.value;
        child = curr.left != null ? curr.left : curr.right;

        reLink(parent, curr, child, value);

        size--;
        return true;
    }

    private void reLink(Node parent, Node curr, Node child, E value) {
        if (root.value == value) {
            root = child;
        } else {
            if (parent.left == curr) {
                parent.left = child;
            } else {
                parent.right = child;
            }
            rebalance(parent);
        }
    }


    @Override
    public boolean add(E value) {
        if (value == null)
            throw new NullPointerException("value is null");
        if (root == null){
            root = new Node(value);
        } else {
            Node curr = root;
            while (true) {
                int cmp = compare(curr.value, value);
                if (cmp == 0) {
                    return false;
                } else if (cmp < 0) {
                    if (curr.right == null) {
                        curr.right = new Node(value, curr);
                        curr = curr.right;
                        break;
                    }
                    curr = curr.right;
                } else {
                    if (curr.left == null) {
                        curr.left = new Node(value, curr);
                        curr = curr.left;
                        break;
                    }
                    curr = curr.left;
                }
            }
            rebalance(curr.parent);
        }
        size++;
        return true;
    }

    public static void main(String[] args) {
        ISortedSet<Integer> set = new AVLTree<>();
        Random random = new Random();

        for (int i = 0; i < 100; i++) set.add(i);
        for (int i = 100; i >= 60; i--) {
            set.remove(i);
            System.out.print(set.first() + " "); //0 0 0 0 4
        }
        System.out.println();
        set = new AVLTree<>();
        for (int i = 0; i < 100; i++) set.add(0);
        System.out.println(set.size());
        SortedSet<Integer> OK = new TreeSet<>();
        set = new AVLTree<>();
        for (int i = 0; i < 100000; i++) {
            int next = random.nextInt(1000);
            assert OK.add(next) == set.add(next);
        }

        set = new AVLTree<>();
        for (int i = 0; i < 100000; i++) {
            set.add(random.nextInt(100));
            System.out.println(set.size());
        }
        for (int i = 0; i < 100000; i++) {
            set.remove(random.nextInt(100));
        }

        set = new AVLTree<>();
        set.add(10);
        System.out.println(set);
        System.out.println(set.size());
        set.add(15);
        System.out.println(set);
        System.out.println(set.size());
        set.add(5);
        System.out.println(set);
        System.out.println(set.size());
        set.remove(5);
        System.out.println(set);
        System.out.println(set.size());
        set.remove(10);
        System.out.println(set);
        System.out.println(set.size());
        set.remove(15);
        System.out.println(set);
        System.out.println(set.size());

        set = new AVLTree<>();
        System.out.println(set.size());

        int failedNPERemove = 0;
        for (int k = 0; k < 1000; k++) {
            set = new AVLTree<>();
            for (int i = 0; i < 1000; i++) {
                int value = random.nextInt(1000);
                set.add(value);
            }
            for (int i = 0; i < 1000; i++) {
                try {
                    int value = random.nextInt(1000);
                    set.remove(value);
                    set.contains(value); //< NPE
                } catch (NullPointerException e) {
                    failedNPERemove++;
                }
            }
        }
        System.out.println(failedNPERemove);

        set = new AVLTree<>();
        System.out.println(set.add(20));
        System.out.println(set.add(20));
        System.out.println(set.add(20));
        System.out.println(set.add(20));
        System.out.println(set.add(20));
        System.out.println(set.add(20));
        System.out.println(set.add(20));
        System.out.println(set.add(20));
        System.out.println(set.add(20));
        System.out.println(set.add(20));
        System.out.println(set.add(20));
        System.out.println(set.remove(20));
        System.out.println(set.remove(20));
        System.out.println(set.remove(20));
        System.out.println(set.remove(20));
        System.out.println(set.remove(20));
        System.out.println(set.remove(20));
        System.out.println(set.remove(20));
        System.out.println(set.remove(20));
        System.out.println(set.remove(20));
        System.out.println(set.remove(20));
    }

}
