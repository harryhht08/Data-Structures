package projects.bpt;

import projects.UnimplementedMethodException;

import java.util.*;

/**
 * <p>{@code BinaryPatriciaTrie} is a Patricia Trie over the binary alphabet &#123;	 0, 1 &#125;. By restricting themselves
 * to this small but terrifically useful alphabet, Binary Patricia Tries combine all the positive
 * aspects of Patricia Tries while shedding the storage cost typically associated with tries that
 * deal with huge alphabets.</p>
 *
 * @author Haitian Hao
 */
public class BinaryPatriciaTrie {

    /* We are giving you this class as an example of what your inner node might look like.
     * If you would prefer to use a size-2 array or hold other things in your nodes, please feel free
     * to do so. We can *guarantee* that a *correct* implementation exists with *exactly* this data
     * stored in the nodes.
     */
    private static class TrieNode {
        private TrieNode left, right;
        private String str;
        private boolean isKey;

        // Add two private fields by myself, to keep track of the depth and the parental relationships
        private int depth;
        private TrieNode parent;

        // Default constructor for your inner nodes.
        TrieNode() {
            this("", false);
        }

        // Non-default constructor.
        TrieNode(String str, boolean isKey) {
            left = right = null;
            this.str = str;
            this.isKey = isKey;
        }
    }

    private TrieNode root;

    private void addDepth(TrieNode n, int d) {
        if (n == null) return;
        n.depth = d;
        addDepth(n.left, d + 1);
        addDepth(n.right, d + 1);
    }

    private void addParent(TrieNode n) {
        if (n == null) return;
        if (n.left != null) n.left.parent = n;
        if (n.right != null) n.right.parent = n;
        addParent(n.left);
        addParent(n.right);
    }

    /**
     * Simple constructor that will initialize the internals of {@code this}.
     */
    public BinaryPatriciaTrie() {
        root = new TrieNode();
    }



    /**
     * Searches the trie for a given key.
     *
     * @param key The input {@link String} key.
     * @return {@code true} if and only if key is in the trie, {@code false} otherwise.
     */
    public boolean search(String key) {
        return searchKey(key, 0, root);
    }

    private boolean searchKey(String key, int index, TrieNode n) {
        if (n == null) return false;
        int len = key.length() - index;
        if (n.str.length() > len) return false;
        if (n.str.equals(key.substring(index)) && n.isKey) return true;
        if (n.str.length() == len) return false;
        if (!key.substring(index).startsWith(n.str)) return false;

        // Find out which next node should go to.
        int newIndex = index + n.str.length();
        TrieNode next;
        if (key.charAt(newIndex) == '0') next = n.left;
        else next = n.right;

        return searchKey(key, newIndex, next);
    }


    /**
     * Inserts key into the trie.
     *
     * @param key The input {@link String}  key.
     * @return {@code true} if and only if the key was not already in the trie, {@code false} otherwise.
     */
    public boolean insert(String key) {
        return insertKey(key, 0, root);
    }

    private TrieNode findParentOfNull(String key, TrieNode n) {
        // Find the parent, starting from the root
        int index = n.str.length();
        TrieNode next;
        if (key.charAt(index) == '1')
            next = n.right;
        else
            next = n.left;
        if (next == null) return n;
        return findParentOfNull(key.substring(index), next);
    }


    private boolean insertKey(String key, int index, TrieNode n) {
        if (n == null) {
            TrieNode parent = findParentOfNull(key, root);
            n = new TrieNode(key.substring(index), true);
            if (key.charAt(index) == '1')
                parent.right = n;
            else
                parent.left = n;
            return true;
        }
        int len = key.length() - index;
        String thisString = key.substring(index);
        if (len > n.str.length()) {
            if (thisString.startsWith(n.str)) {
                int newIndex = index + n.str.length();
                if (key.charAt(newIndex) == '0')
                    return insertKey(key, newIndex, n.left);
                else
                    return insertKey(key, newIndex, n.right);
            }
            else {
                int i = index;
                while (i - index < n.str.length() && key.charAt(i) == n.str.charAt(i - index)) {
                    i++;
                }
                String common = key.substring(index, i);
                TrieNode newCurr = new TrieNode(n.str.substring(i - index), n.isKey);
                newCurr.left = n.left;
                newCurr.right = n.right;
                n.str = common;
                n.isKey = false;
                TrieNode newCurr2 = new TrieNode(key.substring(i), true);
                if (key.charAt(i) == '1') {
                    n.right = newCurr2;
                    n.left = newCurr;
                } else {
                    n.left = newCurr2;
                    n.right = newCurr;
                }
            }
        }

        else if (len == n.str.length()) {
            if (key.substring(index).equals(n.str)) {
                if (n.isKey == true)
                    return false;
                else {
                    n.isKey = true;
                    return true;
                }
            }
            else {
                // Find common prefix
                int i = index;
                while (i - index < n.str.length() && key.charAt(i) == n.str.charAt(i - index)) {
                    i++;
                }
                String common = key.substring(index, i);
                TrieNode newCurr = new TrieNode(n.str.substring(i - index), n.isKey);
                newCurr.left = n.left;
                newCurr.right = n.right;
                n.str = common;
                n.isKey = false;
                TrieNode newCurr2 = new TrieNode(key.substring(i), true);
                if (key.charAt(i) == '1') {
                    n.right = newCurr2;
                    n.left = newCurr;
                } else {
                    n.left = newCurr2;
                    n.right = newCurr;
                }
            }
        }

        else {
            int i = 0;
            while (i < thisString.length() && thisString.charAt(i) == n.str.charAt(i)) {
                i++;
            }
            String common = thisString.substring(0, i);
            if (i == thisString.length()) {
                TrieNode newNode = new TrieNode(n.str.substring(i), n.isKey);
                newNode.left = n.left;
                newNode.right = n.right;
                n.left = null;
                n.right = null;
                n.str = common;
                n.isKey = true;
                if (newNode.str.charAt(0) == '0')
                    n.left = newNode;
                else
                    n.right = newNode;
            } else {
                TrieNode newNode = new TrieNode(n.str.substring(i), n.isKey);
                newNode.left = n.left;
                newNode.right = n.right;
                n.left = null;
                n.right = null;
                n.str = common;
                n.isKey = false;
                TrieNode newNode2 = new TrieNode(thisString.substring(i), true);
                if (newNode.str.charAt(0) == '0') {
                    n.left = newNode;
                    n.right = newNode2;
                }
                else {
                    n.right = newNode;
                    n.left = newNode2;
                }
            }
        }
        return true;
    }

    /**
     * Deletes key from the trie.
     *
     * @param key The {@link String}  key to be deleted.
     * @return {@code true} if and only if key was contained by the trie before we attempted deletion, {@code false} otherwise.
     */
    public boolean delete(String key) {
        addParent(root);
        return deleteKey(key, root);
    }

    private boolean deleteKey(String key, TrieNode n) {
        if (n == null) return false;
        if (n.str.length() > key.length()) return false;
        if (n.str.equals(key) && n.isKey) {
            n.isKey = false;
            if (n.left == null && n.right == null && n != root)
                deleteNode(n);
            else if ((n.left == null || n.right == null) && n != root) {
                mergeWithChild(n);
            }
            return true;
        }
        if (n.str.length() == key.length()) return false;
        else {
            if (!key.startsWith(n.str)) return false;
            else {
                if (key.charAt(n.str.length()) == '1')
                    return deleteKey(key.substring(n.str.length()), n.right);
                return deleteKey(key.substring(n.str.length()), n.left);
            }
        }
    }

    private void deleteNode(TrieNode n) {
        if (n != root) {
            TrieNode parent = n.parent;
            if (n.parent.left == n)
                n.parent.left = null;
            else
                n.parent.right = null;

            if (parent != root && !parent.isKey &&
                    ((parent.left == null && parent.right != null) || (parent.right == null && parent.left != null))) {
                mergeWithChild(parent);
            }
        }
    }

    private void mergeWithChild(TrieNode n) {
        TrieNode child = n.left;
        if (child == null) child = n.right;
        String s = n.str += child.str;
        child.str = s;
        child.parent = n.parent;
        if (n.parent.left == n) n.parent.left = child;
        else n.parent.right = child;
    }

    /**
     * Queries the trie for emptiness.
     *
     * @return {@code true} if and only if {@link #getSize()} == 0, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return getSize() == 0;
    }

    /**
     * Returns the number of keys in the tree.
     *
     * @return The number of keys in the tree.
     */
    public int getSize() {
        return countKeys(root);
    }

    private int countKeys(TrieNode n) {
        if (n == null) return 0;
        int thisOne = 0;
        if (n.isKey) thisOne++;
        return thisOne + countKeys(n.left) + countKeys(n.right);
    }

    /**
     * <p>Performs an <i>inorder (symmetric) traversal</i> of the Binary Patricia Trie. Remember from lecture that inorder
     * traversal in tries is NOT sorted traversal, unless all the stored keys have the same length. This
     * is of course not required by your implementation, so you should make sure that in your tests you
     * are not expecting this method to return keys in lexicographic order. We put this method in the
     * interface because it helps us test your submission thoroughly and it helps you debug your code! </p>
     *
     * <p>We <b>neither require nor test </b> whether the {@link Iterator} returned by this method is fail-safe or fail-fast.
     * This means that you  do <b>not</b> need to test for thrown {@link java.util.ConcurrentModificationException}s and we do
     * <b>not</b> test your code for the possible occurrence of concurrent modifications.</p>
     *
     * <p>We also assume that the {@link Iterator} is <em>immutable</em>, i,e we do <b>not</b> test for the behavior
     * of {@link Iterator#remove()}. You can handle it any way you want for your own application, yet <b>we</b> will
     * <b>not</b> test for it.</p>
     *
     * @return An {@link Iterator} over the {@link String} keys stored in the trie, exposing the elements in <i>symmetric
     * order</i>.
     */
    public Iterator<String> inorderTraversal() {
       ArrayList<String> arr = new ArrayList<>();
       inOrderQueue(root, arr);
       return myIter(arr);
    }

    private Iterator<String> myIter(ArrayList<String> arr) {
        return new Iterator<String>() {
            int index = 0;
            @Override
            public boolean hasNext() {
                return index < arr.size();
            }

            @Override
            public String next() {
                index++;
                return arr.get(index - 1);
            }
        };
    }

    public void inOrderQueue(TrieNode n, ArrayList<String> q) {
        if (n == null) return;
        inOrderQueue(n.left, q);
        if (n.isKey) {
            String s = makeStringToThisPoint(n);
            q.add(s);
        }
        inOrderQueue(n.right, q);
    }

    private String makeStringToThisPoint(TrieNode n) {
        addParent(root);
        ArrayList<TrieNode> arr = new ArrayList<>();
        TrieNode curr = n;
        while (curr != null) {
            arr.add(curr);
            curr = curr.parent;
        }
        Collections.reverse(arr);
        StringBuilder sb = new StringBuilder();
        for (TrieNode node : arr)
            sb.append(node.str);

        return sb.toString();
    }

    /**
     * Finds the longest {@link String} stored in the Binary Patricia Trie.
     * @return <p>The longest {@link String} stored in this. If the trie is empty, the empty string &quot;&quot; should be
     * returned. Careful: the empty string &quot;&quot;is <b>not</b> the same string as &quot; &quot;; the latter is a string
     * consisting of a single <b>space character</b>! It is also <b>not the same as the</b> null <b>reference</b>!</p>
     *
     * <p>Ties should be broken in terms of <b>value</b> of the bit string. For example, if our trie contained
     * only the binary strings 01 and 11, <b>11</b> would be the longest string. If our trie contained
     * only 001 and 010, <b>010</b> would be the longest string.</p>
     */
    public String getLongest() {
        if (isEmpty()) return "";
        ArrayList<TrieNode> arr = new ArrayList<>();
        addDepth(root, 0);
        collectLastNodes(root, arr);

        ArrayList<String> arrString = new ArrayList<>();
        for (TrieNode n : arr) {
            arrString.add(makeStringToThisPoint(n));
        }
        int maxLength = 0;
        for (String s : arrString) {
            if (s.length() > maxLength)
                maxLength = s.length();
        }
        ArrayList<String> arrString2 = new ArrayList<>();
        for (String s : arrString) {
            if (s.length() == maxLength)
                arrString2.add(s);
        }
        Collections.sort(arrString2);
        return arrString2.get(arrString2.size() - 1);
    }


    private void collectLastNodes(TrieNode n, ArrayList<TrieNode> arr) {
        if (n == null) return;
        if (n.left == null && n.right == null) arr.add(n);
        collectLastNodes(n.right, arr);
        collectLastNodes(n.left, arr);
    }

    /**
     * Makes sure that your trie doesn't have splitter nodes with a single child. In a Patricia trie, those nodes should
     * be pruned.
     * @return {@code true} iff all nodes in the trie either denote stored strings or split into two subtrees, {@code false} otherwise.
     */
    public boolean isJunkFree(){
        return isEmpty() || (isJunkFree(root.left) && isJunkFree(root.right));
    }

    private boolean isJunkFree(TrieNode n){
        if(n == null){   // Null subtrees trivially junk-free
            return true;
        }
        if(!n.isKey){   // Non-key nodes need to be strict splitter nodes
            return ( (n.left != null) && (n.right != null) && isJunkFree(n.left) && isJunkFree(n.right) );
        } else {
            return ( isJunkFree(n.left) && isJunkFree(n.right) ); // But key-containing nodes need not.
        }
    }
}
