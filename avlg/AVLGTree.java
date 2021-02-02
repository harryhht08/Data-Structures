package projects.avlg;

import projects.UnimplementedMethodException;
import projects.avlg.exceptions.EmptyTreeException;
import projects.avlg.exceptions.InvalidBalanceException;

import java.util.*;

/** <p>{@link AVLGTree}  is a class representing an <a href="https://en.wikipedia.org/wiki/AVL_tree">AVL Tree</a> with
 * a relaxed balance condition. Its constructor receives a strictly  positive parameter which controls the <b>maximum</b>
 * imbalance allowed on any subtree of the tree which it creates. So, for example:</p>
 *  <ul>
 *      <li>An AVL-1 tree is a classic AVL tree, which only allows for perfectly balanced binary
 *      subtrees (imbalance of 0 everywhere), or subtrees with a maximum imbalance of 1 (somewhere). </li>
 *      <li>An AVL-2 tree relaxes the criteria of AVL-1 trees, by also allowing for subtrees
 *      that have an imbalance of 2.</li>
 *      <li>AVL-3 trees allow an imbalance of 3.</li>
 *      <li>...</li>
 *  </ul>
 *
 *  <p>The idea behind AVL-G trees is that rotations cost time, so maybe we would be willing to
 *  accept bad search performance now and then if it would mean less rotations. On the other hand, increasing
 *  the balance parameter also means that we will be making <b>insertions</b> faster.</p>
 *
 * @author Haitian Hao
 *
 * @see EmptyTreeException
 * @see InvalidBalanceException
 * @see StudentTests
 */
public class AVLGTree<T extends Comparable<T>> {

    /* ********************************************************* *
     * Write any private data elements or private methods here...*
     * ********************************************************* */

    private Node root;
    private int maxImbalance;

    private class Node {
        private T data;
        private Node leftChild, rightChild, parent;
        private int height;
        private int depth;

        private Node() {
            height = 0;
        }

        private Node(T data){
            height = 0;
            this.data = data;
        }

    }


    /* ******************************************************** *
     * ************************ PUBLIC METHODS **************** *
     * ******************************************************** */

    /**
     * The class constructor provides the tree with the maximum imbalance allowed.
     * @param maxImbalance The maximum imbalance allowed by the AVL-G Tree.
     * @throws InvalidBalanceException if maxImbalance is a value smaller than 1.
     */
    public AVLGTree(int maxImbalance) throws InvalidBalanceException {
        if (maxImbalance < 1)
            throw new InvalidBalanceException("The maxImbalance should be greater than or equal to 1!");
//        root = new Node();
        this.maxImbalance = maxImbalance;
    }

    /**
     * Insert key in the tree. You will <b>not</b> be tested on
     * duplicates! This means that in a deletion test, any key that has been
     * inserted and subsequently deleted should <b>not</b> be found in the tree!
     * s
     * @param key The key to insert in the tree.
     */
    public void insert(T key) {
        Node newNode = new Node(key);

        if (isEmpty()) {
            this.root = newNode;
            return;
        }

        addNode(root, newNode);
        resetParentsHeight(newNode);
        Node issueDetected = findImbalance(newNode);
        if (issueDetected == null) return;

        int diff = getHeightDifference(issueDetected);
        if (diff < 0) {
            if (getHeightDifference(issueDetected.rightChild) < 0)
                L_Rotate(issueDetected);
            else
                RL_Rotate(issueDetected);
        } else {
            if (getHeightDifference(issueDetected.leftChild) < 0)
                LR_Rotate(issueDetected);
            else
                R_Rotate(issueDetected);
        }
        resetAllHeights();
    }

    /**
     * Four rotation methods.
     */

    private void L_Rotate(Node n) {
        Node tmp = n.rightChild.leftChild;
        n.rightChild.leftChild = n;
        n.rightChild.parent = n.parent;
        if (n.parent == null) {
            this.root = n.rightChild;
        } else {
            if (n.parent.leftChild == n)
                n.parent.leftChild = n.rightChild;
            else
                n.parent.rightChild = n.rightChild;
        }
        n.parent = n.rightChild;
        n.rightChild = tmp;
        if (tmp != null)
            tmp.parent = n;
    }

    private void R_Rotate(Node n) {
        Node tmp = n.leftChild.rightChild;
        n.leftChild.rightChild = n;
        n.leftChild.parent = n.parent;
        if (n.parent == null) {
            this.root = n.leftChild;
        } else {
            if (n.parent.rightChild == n)
                n.parent.rightChild = n.leftChild;
            else
                n.parent.leftChild = n.leftChild;
        }
        n.parent = n.leftChild;
        n.leftChild = tmp;
        if (tmp != null)
            tmp.parent = n;
    }

    private void RL_Rotate(Node n) {
        Node n1 = n, n2 = n1.rightChild, n3 = n2.leftChild;
        n1.rightChild = n3.leftChild;
        if (n3.leftChild != null)
            n3.leftChild.parent = n1;
        n2.leftChild = n3.rightChild;
        if (n3.rightChild != null)
            n3.rightChild.parent = n2;

        n3.leftChild = n1;
        n3.rightChild = n2;
        n3.parent = n1.parent;
        if (n1.parent == null)
            this.root = n3;
        else {
            if (n1.parent.leftChild == n1)
                n1.parent.leftChild = n3;
            else
                n1.parent.rightChild = n3;
        }
        n1.parent = n3;
        n2.parent = n3;
    }

    private void LR_Rotate(Node n) {
        Node n1 = n, n2 = n1.leftChild, n3 = n2.rightChild;
        n1.leftChild = n3.rightChild;
        if (n3.rightChild != null)
            n3.rightChild.parent = n1;
        n2.rightChild = n3.leftChild;
        if (n3.leftChild != null)
            n3.leftChild.parent = n2;

        n3.rightChild = n1;
        n3.leftChild = n2;
        n3.parent = n1.parent;
        if (n1.parent == null)
            this.root = n3;
        else {
            if (n1.parent.rightChild == n1)
                n1.parent.rightChild = n3;
            else
                n1.parent.leftChild = n3;
        }

        n1.parent = n3;
        n2.parent = n3;

    }

    /**
     * @param n
     * @return Height difference.
     */
    private int getHeightDifference(Node n) {
        int diff;
        if(n.leftChild == null && n.rightChild == null)
            diff = 0;
        else if (n.leftChild == null)
            diff = -1 - n.rightChild.height;
        else if (n.rightChild == null)
            diff = n.leftChild.height + 1;
        else
            diff = n.leftChild.height = n.rightChild.height;
        return diff;
    }


    private void addNode(Node n, Node newNode) {
        if (n.rightChild == null && newNode.data.compareTo(n.data) > 0) {
            n.rightChild = newNode;
            newNode.parent = n;
        } else if (n.leftChild == null && newNode.data.compareTo(n.data) < 0) {
            n.leftChild = newNode;
            newNode.parent = n;
        } else {
            if (newNode.data.compareTo(n.data) > 0)
                addNode(n.rightChild, newNode);
            else
                addNode(n.leftChild, newNode);
        }
    }

    /**
     * Reset the height of all the predecessors of node n.
     * @param n
     */
    private void resetParentsHeight(Node n) {
        int h = n.height;
        int addon = 0;
        Node curr = n;
        while (curr.parent != null) {
            curr = curr.parent;
            addon++;
            int currHeight = curr.height;
            int newHeight = h + addon;
            if (newHeight > currHeight)
                curr.height = newHeight;
            else
                curr.height = currHeight;
        }
    }

    /**
     * Reset the height of all the nodes in the tree.
     */
    private void resetAllHeights() {
        set0(this.root);
        ArrayList<Node> arr = new ArrayList<>();
        addNodeToList(this.root, arr);
        for (Node n : arr) {
            n.height = 0;
            resetParentsHeight(n);
        }
    }

    private void set0(Node n) {
        if (n == null) return;
        n.height = 0;
        set0(n.leftChild);
        set0(n.rightChild);
    }


    private void addNodeToList(Node n, ArrayList<Node> arr) {
        if (n == null) return;
        if (n.leftChild == null && n.rightChild == null) arr.add(n);
        addNodeToList(n.leftChild, arr);
        addNodeToList(n.rightChild, arr);
    }

    /**
     * Use bottom-up method to look for imbalance, starting from the input node.
     * @param bottom The starting node of the check.
     * @return The position where imbalance is detected, or null if the imbalance is tolerable.
     */
    private Node findImbalance(Node bottom) {
        Node curr = bottom;

        while(curr != null) {

            if (curr.leftChild == null && curr.rightChild == null){

            }
            else if (curr.leftChild == null) {
                if (curr.rightChild.height >= maxImbalance)
                    return curr;

            }
            else if (curr.rightChild == null) {
                if (curr.leftChild.height >= maxImbalance)
                    return curr;
            }
            else {
                if (curr.leftChild.height - curr.rightChild.height > maxImbalance
                        || curr.leftChild.height - curr.rightChild.height < -maxImbalance) {
                    return curr;
                }
            }
            curr = curr.parent;
        }
        return null;
    }


    /**
     * Delete the key from the data structure and return it to the caller.
     * @param key The key to delete from the structure.
     * @return The key that was removed, or {@code null} if the key was not found.
     * @throws EmptyTreeException if the tree is empty.
     */
        public T delete(T key) throws EmptyTreeException {
            if (this.root == null)
                throw new EmptyTreeException("The tree is empty!");
            if (searchKey(key, this.root) == null)
                return null;

        Node marker;
        Node delete = searchNode(key, this.root);

        if (delete.leftChild == null && delete.rightChild == null) {
            marker = delete.parent;
            if (delete.parent == null)
                this.root = null;
            else if (delete.parent.leftChild == delete)
                delete.parent.leftChild = null;
            else
                delete.parent.rightChild = null;
        } else if (delete.leftChild == null) {
            if (delete.parent == null) {
                this.root = delete.rightChild;
                delete.rightChild.parent = null;
                marker = this.root;
            } else {
                if (delete.parent.leftChild == delete)
                    delete.parent.leftChild = delete.rightChild;
                else
                    delete.parent.rightChild = delete.rightChild;

                delete.rightChild.parent = delete.parent;
                marker = delete.rightChild;
            }
        } else if (delete.rightChild == null) {
            if (delete.parent == null) {
                this.root = delete.leftChild;
                delete.leftChild.parent = null;
                marker = this.root;
            } else {
                if (delete.parent.leftChild == delete)
                    delete.parent.leftChild = delete.leftChild;
                else
                    delete.parent.rightChild = delete.leftChild;

                delete.leftChild.parent = delete.parent;
                marker = delete.leftChild;
            }
        } else {
            Node curr = delete.rightChild;
            while(curr.leftChild != null) {
                curr = curr.leftChild;
            }
            marker = curr.parent;
            delete.data = curr.data;
            if (curr.parent.leftChild == curr)
                curr.parent.leftChild = null;
            else
                curr.parent.rightChild = null;
        }

        resetAllHeights();


        Node issueDetected = findImbalance(marker);
        if (issueDetected == null) return key;

        int diff = getHeightDifference(issueDetected);
        if (diff < 0) {
            if (getHeightDifference(issueDetected.rightChild) <= 0)
                L_Rotate(issueDetected);
            else
                RL_Rotate(issueDetected);
        } else {
            if (getHeightDifference(issueDetected.leftChild) >= 0)
                R_Rotate(issueDetected);
            else
                LR_Rotate(issueDetected);
        }
        resetAllHeights();
        return key;
    }

    /**
     * <p>Search for key in the tree. Return a reference to it if it's in there,
     * or {@code null} otherwise.</p>
     * @param key The key to search for.
     * @return key if key is in the tree, or {@code null} otherwise.
     * @throws EmptyTreeException if the tree is empty.
     */
    public T search(T key) throws EmptyTreeException {
        if (this.root == null)
            throw new EmptyTreeException("The tree is empty!");
        return searchKey(key, this.root);
    }

    private T searchKey(T key, Node n) {
        if (n == null)
            return null;
        if (n.data.compareTo(key) == 0)
            return n.data;
        if (key.compareTo(n.data) < 0)
            return searchKey(key, n.leftChild);
        else
            return searchKey(key, n.rightChild);
    }

    private Node searchNode(T key, Node n) {
        if (n == null) return null;
        if (n.data.compareTo(key) == 0) return n;
        if (n.data.compareTo(key) < 0)
            return searchNode(key, n.rightChild);
        else
            return searchNode(key, n.leftChild);
    }


    /**
     * Retrieves the maximum imbalance parameter.
     * @return The maximum imbalance parameter provided as a constructor parameter.
     */
    public int getMaxImbalance(){
        return this.maxImbalance;
    }


    /**
     * <p>Return the height of the tree. The height of the tree is defined as the length of the
     * longest path between the root and the leaf level. By definition of path length, a
     * stub tree has a height of 0, and we define an empty tree to have a height of -1.</p>
     * @return The height of the tree. If the tree is empty, returns -1.
     */
    public int getHeight() {
        if (isEmpty()) return -1;
        return this.root.height;
    }

    /**
     * Query the tree for emptiness. A tree is empty iff it has zero keys stored.
     * @return {@code true} if the tree is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return this.root == null;
    }

    /**
     * Return the key at the tree's root node.
     * @return The key at the tree's root node.
     * @throws  EmptyTreeException if the tree is empty.
     */
    public T getRoot() throws EmptyTreeException{
        if (isEmpty()) throw new EmptyTreeException("The tree is empty!");
        return this.root.data;
    }


    /**
     * <p>Establishes whether the AVL-G tree <em>globally</em> satisfies the BST condition. This method is
     * <b>terrifically useful for testing!</b></p>
     * @return {@code true} if the tree satisfies the Binary Search Tree property,
     * {@code false} otherwise.
     */
    public boolean isBST() {
        if (this.root == null) return false;
        return checkBST(this.root);
    }

    private boolean checkBST(Node n) {
        if (n == null) return true;
        if (n.leftChild == null && n.rightChild == null) return true;
        if (n.leftChild == null && n.rightChild.data.compareTo(n.data) > 0) return checkBST(n.rightChild);
        if (n.rightChild == null && n.leftChild.data.compareTo(n.data) < 0) return checkBST(n.leftChild);
        if (n.leftChild.data.compareTo(n.data) < 0 && n.rightChild.data.compareTo(n.data) > 0)
            return checkBST(n.leftChild) && checkBST(n.rightChild);
        return false;
    }


    /**
     * <p>Establishes whether the AVL-G tree <em>globally</em> satisfies the AVL-G condition. This method is
     * <b>terrifically useful for testing!</b></p>
     * @return {@code true} if the tree satisfies the balance requirements of an AVLG tree, {@code false}
     * otherwise.
     */
    public boolean isAVLGBalanced() {
        if (!isBST()) return false;
        return checkDiff(this.root);
    }

    private boolean checkDiff(Node n) {
        if (n == null) return true;
        int diff = getHeightDifference(n);
        return diff >= -maxImbalance && diff <= maxImbalance && checkBST(n.leftChild) && checkDiff(n.rightChild);
    }

    /**
     * <p>Empties the AVL-G Tree of all its elements. After a call to this method, the
     * tree should have <b>0</b> elements.</p>
     */
    public void clear(){
        this.root = null;
    }


    /**
     * <p>Return the number of elements in the tree.</p>
     * @return  The number of elements in the tree.
     */
    public int getCount(){
        if (this.isEmpty()) return 0;
        return count(this.root);
    }

    private int count(Node n) {
        if (n == null) return 0;
        return 1 + count(n.leftChild) + count(n.rightChild);
    }



    public void printTree(Node n) {
        if (n == null) return;
        if(n.leftChild != null && n.rightChild != null){
                System.out.println(n.data + " left: " + n.leftChild.data + " right: " + n.rightChild.data);
        }
        else if(n.leftChild == null && n.rightChild != null){
                System.out.println(n.data + " left: NULL" + " right: " + n.rightChild.data);
        }
        else if(n.leftChild != null && n.rightChild == null){
                System.out.println(n.data + " left: " + n.leftChild.data + " right: NULL");
        }
        else{
                System.out.println(n.data + " left: NULL" + " right: NULL");
        }
        printTree(n.leftChild);
        printTree(n.rightChild);
    }

    public Node getRootNode(){
        return this.root;
    }

}
