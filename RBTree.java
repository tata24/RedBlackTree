public class RBTree<K extends Comparable<K>, V> {

    private static final boolean RED = true;
    private static final boolean BLACK = false;
    private RBNode<K ,V> root;

    public RBNode<K ,V> getRoot() {
        return root;
    }

    /**
     * 获取当前结点的父节点
     * @param node
     */
    public RBNode<K ,V> parentOf(RBNode<K ,V> node){
        if(node != null){
            return node.getParent();
        }
        return null;
    }

    /**
     * 当前结点是否为红色结点
     * @param node
     */
    public boolean isRed(RBNode<K ,V> node){
        if(node != null){
            return node.getColor() == RED;
        }
        return false;
    }

    /**
     * 当前结点是否为黑色结点
     * @param node
     */
    public boolean isBlack(RBNode<K ,V> node){
        if(node != null){
            return node.getColor() == BLACK;
        }
        return true;
    }

    /**
     * 设置结点为红色
     * @param node
     */
    public void setRed(RBNode<K ,V> node){
        if(node != null){
            node.setColor(RED);
        }
    }

    /**
     * 设置结点为黑色
     * @param node
     */
    public void setBlack(RBNode<K ,V> node){
        if(node != null){
            node.setColor(BLACK);
        }
    }

    /**
     * 中序遍历
     */
    public void inOrderPrint(){
        inOrder(this.root);
    }
    private void inOrder(RBNode<K ,V> node){
        if(node != null){
            inOrder(node.getLeft());
            System.out.println("key:"+node.getKey()+",key:"+node.getValue());
            inOrder(node.getRight());
        }
    }

    /**
     * 左旋操作
     *
     *     f                      f
     *     ↑↓                     ↑↓
     *     x                      r
     *   ↗↙ ↘↖     ---->        ↗↙ ↘↖
     *   l   r                  x   rr
     *     ↗↙ ↘↖              ↗↙ ↘↖
     *    rl   rr            l    rl
     *
     * @param x
     */
    private void leftRotate(RBNode<K ,V> x){
        // 1.将x的右节点指向rl, rl的父节点更新为x
        RBNode<K ,V> r = x.getRight();
        x.setRight(r.getLeft());
        if(x.getRight() != null){
            x.getRight().setParent(x);
        }
        // 2.当f不为空时，将r的父节点设置为f, f的子节点设置为r
        RBNode<K ,V> f = x.getParent();
        r.setParent(f);
        if(f != null){
            if(x == f.getLeft()){
                f.setLeft(r);
            } else {
                f.setRight(r);
            }
        } else { // 此时r是根结点
            this.root = r;
        }

        // 3.将x的父节点更新为r, r的左结点更新为x
        x.setParent(r);
        r.setLeft(x);
    }

    /**
     * 右旋操作
     *
     *        f                      f
     *        ↑↓                     ↑↓
     *        x                      l
     *      ↗↙ ↘↖     ---->        ↗↙ ↘↖
     *      l   r                  ll   x
     *    ↗↙ ↘↖                       ↗↙ ↘↖
     *   ll   lr                     lr    r
     *
     * @param x
     */
    private void rightRotate(RBNode<K ,V> x){
        // 1.将x的左边节点指向lr, lr的父节点更新为x
        RBNode<K ,V> l = x.getLeft();
        x.setLeft(l.getRight());
        if(l.getRight() != null){
            l.getRight().setParent(x);
        }
        // 2.当f不为空时，将l的父节点设置为f, f的子节点设置为l
        RBNode<K ,V> f = x.getParent();
        l.setParent(f);
        if(f != null){
            if(f.getLeft() == x){
                f.setLeft(l);
            } else {
                f.setRight(l);
            }
        } else {
            this.root = l;
        }
        // 3.将x的父节点更新为l, l的右节点更新为x
        x.setParent(l);
        l.setRight(x);
    }

    /**
     * 公开的插入方法
     * @param key
     * @param value
     */
    public void insert(K key, V value){
        RBNode<K, V> node = new RBNode<>();
        node.setKey(key);
        node.setValue(value);
        setRed(node);
        insert(node);
    }

    private void insert(RBNode<K, V> node){
        //用来保存待插入结点的父结点
        RBNode<K, V> parent = null;
        // 当前遍历的结点
        RBNode<K, V> x = this.root;

        //找到待插入结点的父节点
        while(x != null){
            parent = x;
            int cmp = node.getKey().compareTo(x.getKey());
            if(cmp > 0){
                x = x.getRight();
            } else if (cmp == 0){
                x.setValue(node.getValue());
                return;
            }else{
                x = x.getLeft();
            }
        }

        // 插入
        node.setParent(parent);
        if(parent != null){
            if(node.getKey().compareTo(parent.getKey()) > 0){
                parent.setRight(node);
            }else{
                parent.setLeft(node);
            }
        }else{
            this.root = node;
        }

        // 恢复红黑树平衡
        insertFixup(node);
    }

    /**
     * q1 插入节点已经存在，重写value
     * 2 插入结点为根结点，插入结点改为黑色
     * 3 插入节点的父节点是黑色，直接插入
     * 4 插入节点的父节点是红色：
     *      4.1 叔叔节点存在，并且颜色为红色，把父节点和叔叔节点改为黑色，爷爷节点改为红色，然后将爷爷节点当作当前节点，
     *          继续调用insertFixup。(叔父双红情况)
     *      4.2 叔叔节点为黑色或者不存在,父节点为爷爷结点的左节点
     *              4.2.1 插入节点为父节点的左子结点，父节点改为黑色，爷爷节点改为红色，以爷爷节点为当前节点进行右旋 （LL情况）
     *
     *                         ↑↓                      ↑↓                     ↑↓
     *                        ff_B                    ff_R                    f_B
     *                       ↗↙ ↘↖    --改色->        ↗↙ ↘↖     --右旋->      ↗↙ ↘↖
     *                     f_R   u_B                f_B  u_B               l_B  ff_R
     *                    ↗↙                       ↗↙                              ↘↖
     *                  l_R                      l_B                               u_B
     *
     *             4.2.2 插入节点为父节点的右子结点，以父节点作为作为当前结点进行左旋，变成4.2.1的情况，指定父结点作为当前结点，
     *                   继续调用insertFixup。(LR情况)
     *
     *                         ↑↓                      ↑↓
     *                        ff_B                    ff_B
     *                       ↗↙ ↘↖      --左旋->      ↗↙ ↘↖
     *                     f_R   u_B                r_R  u_B
     *                       ↘↖                    ↗↙
     *                        r_R                f_R
     *
     *      4.3 叔叔节点为黑色或者不存在,父节点为爷爷结点的右节点
     *             4.3.1 插入节点为父节点的右子结点，父节点改为黑色，爷爷节点改为红色，以爷爷节点为当前节点进行左旋 （RR情况）
     *
     *                         ↑↓                      ↑↓                      ↑↓
     *                        ff_B                    ff_R                    f_B
     *                       ↗↙ ↘↖      --改色->      ↗↙ ↘↖     --左旋->       ↗↙ ↘↖
     *                     u_B   f_R                u_B  f_B               ff_R  r_R
     *                             ↘↖                      ↘↖             ↗↙
     *                              r_R                     r_R          u_B
     *
     *             4.3.2 插入节点为父节点的左子结点，以父节点作为作为当前结点进行右旋，变成4.3.1的情况，指定父结点作为当前结点，
     *                   继续调用insertFixup。(RL情况)
     *                         ↑↓                      ↑↓
     *                        ff_B                    ff_B
     *                       ↗↙ ↘↖      --右旋->      ↗↙ ↘↖
     *                     u_B   f_R                u_B  l_R
     *                          ↗↙                         ↘↖
     *                        l_R                           f_R
     *
     * @param node
     */
    private void insertFixup(RBNode<K, V> node){

        // 第1种情况
        setBlack(this.root);

        RBNode<K ,V> parent = parentOf(node);
        RBNode<K ,V> grandParent = parentOf(parent);

        // 4 插入节点的父节点是红色,那么爷爷节点也一定存在
        if(parent!=null && isRed(parent)){

            // 4.1 叔叔节点存在，并且颜色为红色
            if(isRed(grandParent.getLeft()) && isRed(grandParent.getRight())){
                setBlack(grandParent.getLeft());
                setBlack(grandParent.getRight());
                setRed(grandParent);
                insertFixup(grandParent);
                return;
            }

            RBNode<K ,V> uncle = null;

            // 4.2 父节点为爷爷结点的左节点
            if(grandParent.getLeft()== parent){

                uncle = grandParent.getRight();

                // 4.2 叔叔节点为黑色且不存在
                if(uncle==null || isBlack(uncle)){
                    // 4.2.1 插入节点为父节点的左子结点
                    if(parent.getLeft() == node){
                        setBlack(parent);
                        setRed(grandParent);
                        rightRotate(grandParent);
                        return;
                    }
                    // 4.2.2 插入节点为父节点的右子结点
                    if(parent.getRight() == node){
                        leftRotate(parent);
                        insertFixup(parent);
                        return;
                    }
                }

            } else { //4.3 父节点为爷爷结点的右节点

                uncle = grandParent.getLeft();

                // 4.3 叔叔节点为黑色且不存在
                if(uncle==null || isBlack(uncle)){
                    // 4.3.1 插入节点为父节点的右子结点
                    if(parent.getRight() == node){
                        setBlack(parent);
                        setRed(grandParent);
                        leftRotate(grandParent);
                        return;
                    }
                    // 4.3.2 插入节点为父节点的左子结点
                    if(parent.getLeft() == node){
                        rightRotate(parent);
                        insertFixup(parent);
                        return;
                    }
                }
            }
        }
    }

    /**
     * 根据key值获得RBnode
     * @param key
     * @return
     */
    public RBNode<K ,V> getRBNodeBykey(K key){

        RBNode<K ,V> x = this.root;

        while(x != null){
            int cmp = x.getKey().compareTo(key);
            if(cmp < 0){
                x = x.getRight();
            } else if(cmp == 0){
                return x;
            } else {
                x = x.getLeft();
            }
        }
        return x;
    }


    /**
     * 根据key值删除节点
     * @param key
     * @return
     */
    public V delete(K key){

        RBNode<K, V> p = getRBNodeBykey(key);
        if(p == null){
            return null;
        }
        V v = p.getValue();
        delete(p);
        return v;

    }

    /**
     * 找到后继节点
     * @param p
     * @return
     */
    private RBNode<K, V> successor(RBNode<K, V> p){
        if(p == null){
            return null;
        }
        else {
            RBNode<K, V>  s = p.getRight();
            while(s.getLeft() != null){
                s = s.getLeft();
            }
            return s;
        }
    }

    /**
     * 根据节点引用删除节点
     *
     * 1.当前节点没有子节点，如果是红色节点，直接删除，如果是黑色节点，进行平衡恢复处理
     * 2.当前节点有一个子节点，那么当前节点肯定为黑色，子节点肯定为红色，直接用子节点覆盖当前节点，并改为黑色
     * 3.当前节点有两个子节点，找到后继节点，交换key和value，并将当前节点指向后继节点，此时转到情况1或2处理
     *
     * 所以需要平衡处理的操作的是删除黑色叶子节点
     *
     * @param p
     */
    private void delete(RBNode<K ,V> p){

        //3.当前节点有两个子节点，找到后继节点，交换key和value，
        //  并将当前节点指向后继节点，此时转到情况1或2处理
        if(p.getLeft() != null && p.getRight() != null){
            RBNode<K, V> s = successor(p);
            p.setValue(s.getValue());
            p.setKey(s.getKey());
            p = s;
        }

        RBNode<K,V> replacement = (p.getLeft() != null ? p.getLeft() : p.getRight());

        // 2.当前节点有一个子节点，那么当前节点肯定为黑色，子节点肯定为红色，
        //   直接用子节点覆盖当前节点，并改为黑色
        if(replacement != null){
            replacement.setParent(p.getParent());
            setBlack(replacement);
            if(p.getParent() == null){
                root = replacement;
            }
            else if(p == p.getParent().getLeft()){
                p.getParent().setLeft(replacement);
            }
            else{
                p.getParent().setRight(replacement);
            }
            p.setLeft(null);
            p.setRight(null);
            p.setParent(null);
        }
        else if(p.getParent() == null){ //1 当前节点没有子节点，且当前节点是根节点
            root = null;
        }
        else {
            //1 当前节点没有子节点，当前节点是黑色节点
            if(isBlack(p)){
                deleteFixup(p);
            }
            // 删除当前节点
            if (p.getParent() != null) {
                if (p == p.getParent().getLeft())
                    p.getParent().setLeft(null);
                else if (p == p.getParent().getRight())
                    p.getParent().setRight(null);
                p.setParent(null);
            }
        }
    }

    /**
     * 删除黑色节点，需要进行平衡恢复处理
     *
     * 1.如果当前节点(x_B)是父节点的左节点
     *    1.1 如果兄弟节点为红色，将其转化为兄弟节点为黑色的情况(情况1.2)
     *
     *                    ↑↓                      ↑↓                     ↑↓
     *                   ff_B                    ff_R                    sib_B
     *                  ↗↙ ↘↖    --改色->        ↗↙ ↘↖      --右旋->      ↗↙ ↘↖
     *                x_B   sib_R             x_B   sib_B             ff_R  sibr_B
     *                     ↗↙ ↘↖                    ↗↙ ↘↖             ↗↙ ↘↖
     *                 sibl_B  sibr_B           sibl_B  sibr_B      x_B  sibl_B
     *
     *    1.2 如果当前节点的兄弟节点为黑色
     *          1.2.1 如果兄弟节点的子节点都为黑色(空节点也是黑色)，将父节点(ff_c)作为当前节点，进行下一轮递归
     *
     *                           ↑↓                      ↑↓
     *                          ff_c                    ff_c
     *                         ↗↙ ↘↖      --改色->      ↗↙ ↘↖
     *                       x_B   sib_B             x_B   sib_R
     *
     *          1.2.2 如果兄弟节点的右节点为黑色，左节点为红色，转为情况2.3
     *
     *                          ↑↓                      ↑↓                       ↑↓
     *                         ff_c                    ff_c                     ff_c
     *                        ↗↙ ↘↖      --改色->      ↗↙ ↘↖       --右旋->      ↗↙ ↘↖
     *                     x_B   sib_B             x_B   sib_R               x_B   sibl_B
     *                          ↗↙                      ↗↙                           ↘↖
     *                      sibl_R                  sibl_B                           sib_R
     *
     *          1.2.3 如果兄弟节点的右节点为红色
     *
     *                          ↑↓                      ↑↓                       ↑↓
     *                         ff_c                    ff_B                     sib_c
     *                        ↗↙ ↘↖      --改色->      ↗↙ ↘↖       --左旋->      ↗↙ ↘↖
     *                     x_B   sib_B             x_B   sib_c               ff_B   sibr_B
     *                              ↘↖                      ↘↖              ↗↙
     *                              sibr_R                  sibr_B         x_B
     *
     * 2.如果当前节点是父节点的右边节点，与情况1对称
     *
     * @param x
     */
    private void deleteFixup(RBNode<K ,V> x){
        while(x != root && isBlack(x)){
            // 如果当前节点是父节点左节点
            if(x == x.getParent().getLeft()){

                RBNode<K, V> sib = x.getParent().getRight(); //兄弟节点

                // 1.如果兄弟节点是红色，将其转为兄弟节点为黑色的情况
                if(isRed(sib)){
                    setBlack(sib);
                    setRed(x.getParent());
                    leftRotate(x.getParent());
                    sib = x.getParent().getRight();
                }

                // 兄弟节点为黑色
                if(isBlack(sib.getRight()) && isBlack(sib.getLeft())){// 情况1.2.1：如果兄弟节点的子节点都为黑色
                    setRed(sib);
                    x = parentOf(x);
                }
                else {
                    // 情况1.2.2: 如果兄弟节点的右节点为黑色，左节点为红色，转为情况2.3
                    if(isBlack(sib.getRight())){
                        setBlack(sib.getLeft());
                        setRed(sib);
                        rightRotate(sib);
                        sib = x.getParent().getRight();
                    }
                    // 情况1.2.3:如果兄弟节点的左节点是黑色的,右节点是红色
                    sib.setColor(x.getParent().getColor());
                    setBlack(x.getParent());
                    setBlack(sib.getRight());
                    leftRotate(x.getParent());
                    x = root;
                }
            }
            else{ // 如果当前节点是父节点右节点

                RBNode<K, V> sib = x.getParent().getLeft(); //兄弟节点

                if (isRed(sib)) {
                    setBlack(sib);
                    setRed(x.getParent());
                    rightRotate(x.getParent());
                    sib = x.getParent().getLeft();
                }

                if(isBlack(sib.getRight()) && isBlack(sib.getLeft())){
                    setRed(sib);
                    x = parentOf(x);
                }
                else {

                    if(isBlack(sib.getLeft())){
                        setBlack(sib.getRight());
                        setRed(sib);
                        leftRotate(sib);
                        sib = x.getParent().getLeft();
                    }
                    sib.setColor(x.getParent().getColor());
                    setBlack(x.getParent());
                    setBlack(sib.getLeft());
                    rightRotate(x.getParent());
                    x = root;
                }
            }
        }
        setBlack(x);
    }
}
























