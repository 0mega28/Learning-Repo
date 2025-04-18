import java.util.Arrays;

public class Skiplist {
    private final int LEVELS = 16;
    private final double P = 1d / 2d;
    int size = 0;

    class Node {
        int value;
        Node[] next;

        Node(int value, int level) {
            this.value = value;
            this.next = new Node[level];
        }

        Node(int value, int level, Node next) {
            this(value, level);
            for (int i = 0; i < level; i++)
                this.next[i] = next;
        }
    }

    Node head;
    Node tail;

    int randomLevel() {
        int level = 1;

        while (Math.random() < P && level < LEVELS)
            level++;

        return level;
    }

    public Skiplist() {
        tail = new Node(Integer.MAX_VALUE, 0, null);
        head = new Node(Integer.MIN_VALUE, LEVELS, tail);
    }

    public boolean search(int target) {
        Node node = walkSkipList(target, null);
        return node.value == target;
    }

    private Node walkSkipList(int num, Node[] walk) {
        int level = LEVELS - 1;
        Node curr = head;
        while (level >= 0) {
            if (walk != null)
                walk[level] = curr;

            Node next = curr.next[level];

            if (num < next.value) {
                level--;
            } else {
                curr = next;
            }
        }

        return curr;
    }

    public void add(int num) {
        Node[] walk = new Node[LEVELS];

        walkSkipList(num, walk);

        int newElemLevel = randomLevel();
        Node newNode = new Node(num, newElemLevel);

        for (int i = 0; i < newElemLevel; i++) {
            newNode.next[i] = walk[i].next[i];
            walk[i].next[i] = newNode;
        }
        size++;
    }

    public boolean erase(int num) {
        Node[] walk = new Node[LEVELS];
        Node nodeToDelete = walkSkipList(num, walk);

        if (nodeToDelete.value != num) {
            return false;
        }

        Node prevNode = head;
        // [H 80 90 90]
        int level = LEVELS - 1;
        while (walk[level] != nodeToDelete) {
            prevNode = walk[level];
            level--;
        }

        while (level >= 0) {
            while (prevNode.next[level] != nodeToDelete) {
                prevNode = prevNode.next[level];
            }

            prevNode.next[level] = nodeToDelete.next[level];
            level--;
        }

        size--;
        return true;
    }

    @Override
    public String toString() {
        int[][] values = new int[LEVELS][size];
        for (int[] value : values) {
            Arrays.fill(value, 0);
        }
        int idx = 0;
        for (Node node = head.next[0]; node != tail; node = node.next[0]) {
            for (int i = 0; i < node.next.length; i++) {
                values[LEVELS - i - 1][idx] = node.value;
            }
            idx++;
        }
        StringBuilder sb = new StringBuilder();
        for (int[] value : values) {
            sb.append(Arrays.toString(value)).append('\n');
        }
        return sb.toString();
    }
}
