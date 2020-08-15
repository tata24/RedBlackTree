class RBNode<K extends Comparable<K>, V>{
    private RBNode parent;
    private RBNode left;
    private RBNode right;
    private boolean color;
    private K key;
    private V value;

    public RBNode() {

    }

    public RBNode(RBNode parent, RBNode left, RBNode right, boolean color, K key, V value) {
        this.parent = parent;
        this.left = left;
        this.right = right;
        this.color = color;
        this.key = key;
        this.value = value;
    }

    public RBNode getParent() {
        return parent;
    }

    public void setParent(RBNode parent) {
        this.parent = parent;
    }

    public RBNode getLeft() {
        return left;
    }

    public void setLeft(RBNode left) {
        this.left = left;
    }

    public RBNode getRight() {
        return right;
    }

    public void setRight(RBNode right) {
        this.right = right;
    }

    public boolean getColor() {
        return color;
    }

    public void setColor(boolean color) {
        this.color = color;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
