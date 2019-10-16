package tree;



public class RBTree<T extends Comparable<T>> {

    private static final boolean RED = false;

    private static final boolean BLACK = true;

    //根节点
    private RBNode<T> mRoot;

    public class RBNode<T extends Comparable<T>> {
        //        颜色
        boolean color;
        //        键值
        T key;
        //        左孩子
        RBNode<T> left;
        //        右孩子
        RBNode<T> right;

        //        父节点
        RBNode<T> parent;

        public RBNode(T key, boolean color, RBNode<T> parent, RBNode<T> left, RBNode<T> right) {
            this.key = key;
            this.color = color;
            this.parent = parent;
            this.left = left;
            this.right = right;
        }

    }

    /*
     * 对红黑树的节点(x)进行左旋转
     *
     * 左旋示意图(对节点x进行左旋)：
     *      px                              px
     *     /                               /
     *    x                               y
     *   /  \      --(左旋)-.           / \                #
     *  lx   y                          x  ry
     *     /   \                       /  \
     *    ly   ry                     lx  ly
     *
     *
     */
    private void leftRotate(RBNode<T> x) {

        //将x右子树的左子树设置成x的右子树
        RBNode<T> y = x.right;

        x.right = y.left;

        //设置y.left的父节点是x
        if (y.left != null) {
            y.left.parent = x;
        }


        //将x的父节点设置成y的父亲
        y.parent = x.parent;

        //

        if (x.parent == null) {
            this.mRoot = y;
        } else {
            if (x.parent.left == x) {
//                x是左子树
                x.parent.left = y;

            } else {
//                x是右子树
                x.parent.right = y;
            }
        }

        //x设为y的左子树
        y.left = x;

        //y设置成x的父节点
        x.parent = y;
    }


    /*
     * 对红黑树的节点(y)进行右旋转
     *
     * 右旋示意图(对节点y进行左旋)：
     *            py                               py
     *           /                                /
     *          y                                x
     *         /  \      --(右旋)-.            /  \                     #
     *        x   ry                           lx   y
     *       / \                                   / \                   #
     *      lx  rx                                rx  ry
     *
     */
    private void rightRotate(RBNode<T> y) {

        RBNode<T> x = y.left;

        y.left = x.right;
        if (x.right != null) {
            x.right.parent = y;
        }

        //设置父节点
        x.parent = y.parent;
        if (y.parent == null) {
            mRoot = x;
        } else {
            if (y.parent.left == y) {
                y.parent.left = x;
            } else {
                y.parent.right = x;
            }
        }

        x.right = y;

        y.parent = x;
    }

    public void insert(T key) {
        RBNode<T> node = new RBNode<>(key, RED, null, null, null);
        if (node != null) {
            insert(node);
        }
    }

    private void insert(RBNode<T> node) {

        int cmp;
        RBNode<T> y = null;
        RBNode<T> x = this.mRoot;

        while (x != null) {
            y = x;
            cmp = node.key.compareTo(x.key);
            if (cmp < 0) {
                x = x.left;

            } else {
                x = x.right;
            }
        }

        node.parent = y;
        if (y != null) {
            int cpr = node.key.compareTo(y.key);
            if (cpr < 0) {
                y.left = node;
            } else {
                y.right = node;
            }
        } else {
            this.mRoot = node;
        }


        node.color = RED;

        insertFixUp(node);
    }

    private void insertFixUp(RBNode<T> node) {
        RBNode<T> parent, gparent;

        //父节点存在且父节点是红色的
        while (((parent = parentOf(node)) != null) && parent.color == RED) {
            gparent = parentOf(parent);
            //父节点是左子树
            if (parent == gparent.left) {
                RBNode<T> uncle = gparent.right;

                //1 叔叔节点是红色的
                if (uncle != null && uncle.color == RED) {
                    setBlack(parent);
                    setBlack(uncle);
                    setRed(gparent);
                    node = gparent;
                    continue;
                }
                if (parent.right == node) {
                    //经过下面变换后  当前节点是父节点的左孩子
                    RBNode<T> tmp;
                    leftRotate(parent);
                    tmp = parent;
                    parent = node;
                    node = tmp;
                }
                //叔叔节点是黑色的 且当前节点是做孩子
                setBlack(parent);
                setRed(gparent);
                rightRotate(gparent);
            } else {
                //父节点是右子树
                RBNode<T> uncle = gparent.left;

                //叔叔节点是红色的
                if (uncle != null && uncle.color == RED) {
                    setBlack(parent);
                    setBlack(uncle);
                    setRed(gparent);
                    node = gparent;
                    continue;
                }

                if (parent.left == node) {

                    RBNode<T> tmp;
                    rightRotate(parent);
                    tmp = parent;
                    parent = node;
                    node = tmp;
                }

                //叔叔是黑色节点 且 当前节点是右孩子
                setRed(gparent);
                setBlack(parent);
                leftRotate(gparent);
            }
        }

        setBlack(mRoot);
    }

    private void remove(RBNode<T> node) {

        RBNode<T> parent, child;
        boolean color;
        //被删除的节点的左右孩子都不为空
        if (node.left != null && node.right != null) {
            RBNode<T> replace = node;
            //获取后继节点
            replace = replace.right;
            while (replace.left != null) {
                replace = replace.left;
            }

            //node节点不是根节点 - 只有跟节点不存在父节点
            if (parentOf(node) != null) {

                if (parentOf(node).left == node) {
                    //node是左节点
                    parentOf(node).left = replace;
                } else {
                    //node 是右节点
                    parentOf(node).right = replace;
                }
            } else {
                this.mRoot = replace;
            }


            //child是取代节点的右孩子 也是需要调整的节点 取代节点是不存在左孩子的 因为它是一个后继节点
            child = replace.right;

            parent = parentOf(replace.parent);

            color = replace.color;

            //删除节点是取代节点的父节点
            if (parent == node) {
                parent = replace;
            } else {
                //替代节点右孩子不为空 将替代节点的右孩子设置成替代节点父节点的左孩子
                if (child != null) {
                    setParent(child,parent);
                }

                parent.left = child;

                //被删除节点的右节点设置成替换节点的右节点
                replace.right = node.right;
                setParent(node.right,replace);

            }

            replace.parent = node.parent;
            replace.color = node.color;
            replace.left = node.left;
            node.left.parent = replace;

            if (color == BLACK) {
                //TODO 调整红黑树
                removeFixUp(child,parent);
            }

            node = null;
            return;
        }

        //左子树和右子树不能同时存在
        if(node.left!=null){
            child = node.left;
        }else{
            child = node.right;
        }

        parent = node.parent;
        color = node.color;

        if(null!=child){
            child.parent = parent;
        }

        //将节点的子节点给节点的父节点
        if(null!=parent){
            if(parent.left ==node){
                parent.left = child;
            }else{
                parent.right = child;
            }
        }

        if(color == BLACK){
            removeFixUp(child,parent);
        }

        node = null;

    }

    private  void removeFixUp(RBNode<T> node,RBNode<T> parent){
        RBNode<T> other ;

        while ((node!=null || node.color==BLACK) && (node!=this.mRoot)){
            //node 是左节点
            if(parent.left == node){
                //父节点的右子树
                other  = parent.right;

                //
                if(other.color == RED){
                    //node 的兄弟是红色的
                    setBlack(other);
                    setRed(parent);
                    leftRotate(parent);
                    other = parent.right;
                }

                //node节点的兄弟是黑色节点 且兄弟的两个孩子节点都是黑色的
                if((other.left!=null || other.left.color == BLACK ) && (other.right!=null || other.right.color == BLACK )){
                    //叶子节点也是黑色的
                    other.color = RED;
                    node = parent;
                    parent = parentOf(node);
                }else{
                    if(other.right ==null || other.right.color == BLACK){
                        //兄弟是黑色的 且左侄子为红 右侄为黑
                        setBlack(other.left);
                        setRed(other);
                        rightRotate(other);
                        other = parent.right;
                    }

                    //兄弟是黑色的 且左侄随意 右侄为红
                    other.color = parent.color;
                    setBlack(parent);
                    setBlack(other.right);
                    leftRotate(parent);
                    node = this.mRoot;
                    break;
                }
            }else{

            }


        }

        if(node!=null){
            setBlack(node);
        }
    }


    private RBNode<T> parentOf(RBNode<T> node) {
        return node != null ? node.parent : null;
    }

    private void setBlack(RBNode<T> node) {
        if (node != null)
            node.color = BLACK;
    }

    private void setRed(RBNode<T> node) {
        if (node != null)
            node.color = RED;
    }

    private void setParent(RBNode<T> node, RBNode<T> parent) {
        if (node != null)
            node.parent = parent;
    }


    private static final int a[] = {10, 40, 30, 60, 90, 70, 20, 50, 80};

    public static void main(String[] args) {
        RBTree<Integer> tree = new RBTree<>();
        for (int i = 0; i < a.length; i++) {
            tree.insert(a[i]);
        }
        System.out.println(true);
    }
}
