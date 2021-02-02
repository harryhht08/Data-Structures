package projects.spatial.nodes;

import projects.UnimplementedMethodException;
import projects.spatial.kdpoint.KDPoint;
import projects.spatial.knnutils.BoundedPriorityQueue;
import projects.spatial.knnutils.NNData;
import projects.spatial.trees.KDTree;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>{@link KDTreeNode} is an abstraction over nodes of a KD-Tree. It is used extensively by
 * {@link projects.spatial.trees.KDTree} to implement its functionality.</p>
 *
 * <p><b>YOU ***** MUST ***** IMPLEMENT THIS CLASS!</b></p>
 *
 * @author  Haitian Hao
 *
 * @see projects.spatial.trees.KDTree
 */
public class KDTreeNode {


    /* *************************************************************************** */
    /* ************* WE PROVIDE THESE FIELDS TO GET YOU STARTED.  **************** */
    /* ************************************************************************** */
    private KDPoint p;
    private int height;
    private KDTreeNode left, right;

    /* *************************************************************************************** */
    /* *************  PLACE ANY OTHER PRIVATE FIELDS AND YOUR PRIVATE METHODS HERE: ************ */
    /* ************************************************************************************* */

    private KDTreeNode parent;
    private int dimension;

    private void setDimension(KDTreeNode n, int start, int dims) {
        if (n == null) return;
        int next = start + 1;
        if (next == dims) next = 0;
        n.dimension = start;
        setDimension(n.left, next, dims);
        setDimension(n.right, next, dims);
    }

    private void addParentalRelation (KDTreeNode n) {
        if (n == null) return;
        if (n.left != null)
            n.left.parent = n;
        if (n.right != null)
            n.right.parent = n;
        addParentalRelation(n.left);
        addParentalRelation(n.right);
    }

    /* *********************************************************************** */
    /* ***************  IMPLEMENT THE FOLLOWING PUBLIC METHODS:  ************ */
    /* *********************************************************************** */

    /**
     * 1-arg constructor. Stores the provided {@link KDPoint} inside the freshly created node.
     * @param p The {@link KDPoint} to store inside this. Just a reminder: {@link KDPoint}s are
     *          <b>mutable!!!</b>.
     */
    public KDTreeNode(KDPoint p){
        this.p = new KDPoint(p);
        height = 0;
    }

    /**
     * <p>Inserts the provided {@link KDPoint} in the tree rooted at this. To select which subtree to recurse to,
     * the KD-Tree acts as a Binary Search Tree on currDim; it will examine the value of the provided {@link KDPoint}
     * at currDim and determine whether it is larger than or equal to the contained {@link KDPoint}'s relevant dimension
     * value. If so, we recurse right, like a regular BST, otherwise left.</p>
     * @param currDim The current dimension to consider
     * @param dims The total number of dimensions that the space considers.
     * @param pIn The {@link KDPoint} to insert into the node.
     * @see #delete(KDPoint, int, int)
     */
    public void insert(KDPoint pIn, int currDim, int dims){
        int nextDim = currDim + 1;
        if (currDim == dims - 1) nextDim = 0;
        if (p.coords[currDim].compareTo(pIn.coords[currDim]) >= 0) {
            if (right == null) {
                right = new KDTreeNode(pIn);
                right.parent = this;
            }
            else
                right.insert(pIn, nextDim, dims);
        }
        else {
            if (left == null) {
                left = new KDTreeNode(pIn);
                left.parent = this;
            }
            else
                left.insert(pIn, nextDim, dims);
        }
    }

    /**
     * <p>Deletes the provided {@link KDPoint} from the tree rooted at this. To select which subtree to recurse to,
     * the KD-Tree acts as a Binary Search Tree on currDim; it will examine the value of the provided {@link KDPoint}
     * at currDim and determine whether it is larger than or equal to the contained {@link KDPoint}'s relevant dimension
     * value. If so, we recurse right, like a regular BST, otherwise left. There exist two special cases of deletion,
     * depending on whether we are deleting a {@link KDPoint} from a node who either:</p>
     *
     * <ul>
     *      <li>Has a NON-null subtree as a right child.</li>
     *      <li>Has a NULL subtree as a right child.</li>
     * </ul>
     *
     * <p>You should consult the class slides, your notes, and the textbook about what you need to do in those two
     * special cases.</p>
     * @param currDim The current dimension to consider.
     * @param dims The total number of dimensions that the space considers.
     * @param pIn The {@link KDPoint} to insert into the node.
     * @see #insert(KDPoint, int, int)
     * @return A reference to this after the deletion takes place.
     */
    public KDTreeNode delete(KDPoint pIn, int currDim, int dims){
        KDTreeNode del = searchNode(pIn, currDim, dims);
        if (del != null) {
            if (del == this && left == null && right == null)
                this.p = null;
            else
                deleteHelper(findDimension(this, pIn, 0, dims), dims, this);
        }
        return this;
    }

    private int findDimension(KDTreeNode n, KDPoint pIn, int dim, int dims) {
        int nextDim = dim + 1;
        if (dim == dims - 1) nextDim = 0;
        if (checkSame(n.p, pIn)) return dim;
        if (n.p.coords[dim].compareTo(pIn.coords[dim]) >= 0) {
            return right.findDimension(n.right, pIn, nextDim, dims);
        }
        else {
            return left.findDimension(n.left, pIn, nextDim, dims);
        }
    }

    private void deleteHelper(int currDim, int dims, KDTreeNode root){
        if (right == null && left == null) {
            if (this.parent.left == this)
                this.parent.left = null;
            else
                this.parent.right = null;
        }

        if (right == null) {
            KDTreeNode n = getMinNode(left, currDim);
            KDPoint newP = new KDPoint(n.p);
            p = newP;
            right = left;
            left = null;
            int dim = findDimension(root, n.p, 0, dims);
            deleteHelper(dim, dims, root);
        }
        else {
            KDTreeNode n = getMinNode(right, currDim);
            KDPoint newP = new KDPoint(n.p);
            p = newP;
            int dim = findDimension(root, n.p, 0, dims);
            deleteHelper(dim, dims, root);
        }
    }

    private KDTreeNode getMinNode(KDTreeNode n, int currDim) {
        ArrayList<KDTreeNode> arr = new ArrayList<>();
        makeArraylist(n, arr);
        BigDecimal min = arr.get(0).p.coords[currDim];
        for (int i = 0; i < arr.size(); i++) {
            BigDecimal curr = arr.get(i).p.coords[currDim];
            if (curr.compareTo(min) < 0)
                min = curr;
        }
        for (int i = 0; i < arr.size(); i++) {
            BigDecimal curr = arr.get(i).p.coords[currDim];
            if (curr.compareTo(min) == 0) {
                return arr.get(i);
            }
        }
        return null;
    }

    private void makeArraylist(KDTreeNode n, ArrayList<KDTreeNode> arr) {
        if (n == null) return;
        if (n.left == null && n.right == null) arr.add(n);
        makeArraylist(n.left, arr);
        arr.add(n);
        makeArraylist(n.right, arr);
    }


    private KDTreeNode searchNode(KDPoint pIn, int currDim, int dims){
        int nextDim = currDim + 1;
        if (currDim == dims - 1) nextDim = 0;
        if (checkSame(p, pIn)) return this;
        if (p.coords[currDim].compareTo(pIn.coords[currDim]) >= 0) {
            if (right == null) {
                return null;
            }
            else
                return right.searchNode(pIn, nextDim, dims);
        }
        else {
            if (left == null) {
                return null;
            }
            else
                return left.searchNode(pIn, nextDim, dims);
        }
    }

    /**
     * Searches the subtree rooted at the current node for the provided {@link KDPoint}.
     * @param pIn The {@link KDPoint} to search for.
     * @param currDim The current dimension considered.
     * @param dims The total number of dimensions considered.
     * @return true iff pIn was found in the subtree rooted at this, false otherwise.
     */
    public boolean search(KDPoint pIn, int currDim, int dims){
        int nextDim = currDim + 1;
        if (currDim == dims - 1) nextDim = 0;
        if (checkSame(p, pIn)) return true;
        if (p.coords[currDim].compareTo(pIn.coords[currDim]) >= 0) {
            if (right == null) {
                return false;
            }
            else
                return right.search(pIn, nextDim, dims);
        }
        else {
            if (left == null) {
                return false;
            }
            else
                return left.search(pIn, nextDim, dims);
        }
    }

    private boolean checkSame(KDPoint p1, KDPoint p2) {
        if (p1.coords.length != p2.coords.length) return false;
        for (int i = 0; i < p1.coords.length; i++) {
            if (p1.coords[i].compareTo(p2.coords[i]) != 0) return false;
        }
        return true;
    }


    /**
     * <p>Executes a range query in the given {@link KDTreeNode}. Given an &quot;anchor&quot; {@link KDPoint},
     * all {@link KDPoint}s that have a {@link KDPoint#distanceSquared(KDPoint) distanceSquared} of <b>at most</b> range
     * <b>INCLUSIVE</b> from the anchor point <b>except</b> for the anchor itself should be inserted into the {@link Collection}
     * that is passed.</p>
     *
     * <p>Remember: range queries behave <em>greedily</em> as we go down (approaching the anchor as &quot;fast&quot;
     * as our currDim allows and <em>prune subtrees</em> that we <b>don't</b> have to visit as we backtrack. Consult
     * all of our resources if you need a reminder of how these should work.</p>
     *
     * <p>Finally, note that the range parameter is a Euclidean Distance, not the square of a Euclidean
     * Distance! </p>
     * @param anchor The centroid of the hypersphere that the range query implicitly creates.
     * @param results A {@link Collection} that accumulates all the {@link }
     * @param currDim The current dimension examined by the {@link KDTreeNode}.
     * @param dims The total number of dimensions of our {@link KDPoint}s.
     * @param range The <b>INCLUSIVE</b> range from the &quot;anchor&quot; {@link KDPoint}, within which all the
     *              {@link KDPoint}s that satisfy our query will fall. The distanceSquared metric used} is defined by
     *              {@link KDPoint#distanceSquared(KDPoint)}.
     */
    public void range(KDPoint anchor, Collection<KDPoint> results,
                      BigDecimal range, int currDim , int dims){
        addParentalRelation(this);
        setDimension(this, 0, dims);
//        rangeHelper(this, anchor, results, range, dims);

        getRangePoint(this, anchor, results, range);



    }

    private void getRangePoint(KDTreeNode n, KDPoint anchor, Collection<KDPoint> results,
                               BigDecimal range) {
        if (n == null) return;
        if (compareDist(n.p, anchor, range) <= 0) {
            results.add(n.p);
        }
        getRangePoint(n.left, anchor, results, range);
        getRangePoint(n.right, anchor, results, range);
    }


    private void rangeHelper(KDTreeNode n, KDPoint anchor, Collection<KDPoint> results, BigDecimal range, int dims) {
        KDTreeNode end = greedyApproach(n, anchor, n.dimension, dims);
        if (compareDist(end.p, anchor, range) > 0) return;
        else {
            results.add(n.p);
            if (end.right != null && !(outOfRange(end.right, anchor, range)))
                rangeHelper(end.right, anchor, results, range, dims);
            if (end.left != null && !(outOfRange(end.left, anchor, range)))
                rangeHelper(end.left, anchor, results, range, dims);

            KDTreeNode curr = end;
            KDTreeNode curr2 = end.parent;

            while (curr2 != n.parent) {
                if (!outOfRange(curr2, anchor, range)) {
                    if (curr == curr2.left)
                        rangeHelper(curr2.right, anchor, results, range, dims);
                    else
                        rangeHelper(curr2.left, anchor, results, range, dims);
                }
                curr = curr2;
                curr2 = curr2.parent;
            }

        }
    }

    private boolean outOfRange(KDTreeNode n, KDPoint anchor, BigDecimal range) {
        BigDecimal index = n.p.coords[n.dimension];
        BigDecimal index2 = anchor.coords[n.dimension];
        if (index.subtract(index2).abs().compareTo(range) >= 0)
            return true;
        return false;
    }

    private KDTreeNode greedyApproach(KDTreeNode n, KDPoint anchor, int currDim, int dims) {
        int nextDim = currDim + 1;
        if (nextDim >= dims) nextDim = 0;
        if (n.p.coords[currDim].compareTo(anchor.coords[currDim]) >= 0) {
            if (n.right == null)
                return n;
            else {
                return greedyApproach(n.right, anchor, nextDim, dims);
            }
        }
        else {
            if (n.left == null)
                return n;
            else {
                return greedyApproach(n.left, anchor, nextDim, dims);
            }
        }
    }

    private BigDecimal rootOfBigDecimal(BigDecimal bd){
        return new BigDecimal(Math.sqrt(bd.doubleValue()));
    }

    private int compareDist(KDPoint p1, KDPoint p2, BigDecimal range) {
        return rootOfBigDecimal(p1.distanceSquared(p2)).compareTo(range);
    }

    /**
     * <p>Executes a nearest neighbor query, which returns the nearest neighbor, in terms of
     * {@link KDPoint#distanceSquared(KDPoint)}, from the &quot;anchor&quot; point.</p>
     *
     * <p>Recall that, in the descending phase, a NN query behaves <em>greedily</em>, approaching our
     * &quot;anchor&quot; point as fast as currDim allows. While doing so, it implicitly
     * <b>bounds</b> the acceptable solutions under the current <b>best solution</b>, which is passed as
     * an argument. This approach is known in Computer Science as &quot;branch-and-bound&quot; and it helps us solve an
     * otherwise exponential complexity problem (nearest neighbors) efficiently. Remember that when we want to determine
     * if we need to recurse to a different subtree, it is <b>necessary</b> to compare the distanceSquared reported by
     * {@link KDPoint#distanceSquared(KDPoint)} and coordinate differences! Those are comparable with each other because they
     * are the same data type ({@link Double}).</p>
     *
     * @return An object of type {@link NNData}, which exposes the pair (distance_of_NN_from_anchor, NN),
     * where NN is the nearest {@link KDPoint} to the anchor {@link KDPoint} that we found.
     *
     * @param anchor The &quot;anchor&quot; {@link KDPoint}of the nearest neighbor query.
     * @param currDim The current dimension considered.
     * @param dims The total number of dimensions considered.
     * @param n An object of type {@link NNData}, which will define a nearest neighbor as a pair (distance_of_NN_from_anchor, NN),
     *      * where NN is the nearest neighbor found.
     *
     * @see NNData
     * @see #kNearestNeighbors(int, KDPoint, BoundedPriorityQueue, int, int)
     */
    public  NNData<KDPoint> nearestNeighbor(KDPoint anchor, int currDim,
                                            NNData<KDPoint> n, int dims){

        KDTreeNode curr = this;
        ArrayList<NNData<KDPoint>> arr = new ArrayList<>();
        makeNN(this, arr, anchor);
        BigDecimal b = arr.get(0).bestDist;
        NNData<KDPoint> out = arr.get(0);
        for (NNData nn : arr) {
            if (nn.bestDist.compareTo(b) < 0) {
                b = nn.bestDist;
                out = nn;
            }
        }
        return out;
    }

    private void makeNN(KDTreeNode n, ArrayList<NNData<KDPoint>> arr, KDPoint anchor) {
        if (n == null) return;
        arr.add(new NNData(n.p, n.p.distanceSquared(anchor)));
        makeNN(n.left, arr, anchor);
        makeNN(n.right, arr, anchor);
    }

    /**
     * <p>Executes a nearest neighbor query, which returns the nearest neighbor, in terms of
     * {@link KDPoint#distanceSquared(KDPoint)}, from the &quot;anchor&quot; point.</p>
     *
     * <p>Recall that, in the descending phase, a NN query behaves <em>greedily</em>, approaching our
     * &quot;anchor&quot; point as fast as currDim allows. While doing so, it implicitly
     * <b>bounds</b> the acceptable solutions under the current <b>worst solution</b>, which is maintained as the
     * last element of the provided {@link BoundedPriorityQueue}. This is another instance of &quot;branch-and-bound&quot;
     * Remember that when we want to determine if we need to recurse to a different subtree, it is <b>necessary</b>
     * to compare the distanceSquared reported by* {@link KDPoint#distanceSquared(KDPoint)} and coordinate differences!
     * Those are comparable with each other because they are the same data type ({@link Double}).</p>
     *
     * <p>The main difference of the implementation of this method and the implementation of
     * {@link #nearestNeighbor(KDPoint, int, NNData, int)} is the necessity of using the class
     * {@link BoundedPriorityQueue} effectively. Consult your various resources
     * to understand how you should be using this class.</p>
     *
     * @param k The total number of neighbors to retrieve. It is better if this quantity is an odd number, to
     *          avoid ties in Binary Classification tasks.
     * @param anchor The &quot;anchor&quot; {@link KDPoint} of the nearest neighbor query.
     * @param currDim The current dimension considered.
     * @param dims The total number of dimensions considered.
     * @param queue A {@link BoundedPriorityQueue} that will maintain at most k nearest neighbors of
     *              the anchor point at all times, sorted by distanceSquared to the point.
     *
     * @see BoundedPriorityQueue
     */
    public void kNearestNeighbors(int k, KDPoint anchor, BoundedPriorityQueue<KDPoint> queue, int currDim, int dims){
        queue = new BoundedPriorityQueue<>(k);
        KDTreeNode curr = this;
        ArrayList<NNData<KDPoint>> arr = new ArrayList<>();
        makeNN(this, arr, anchor);

        while(k>0) {
            BigDecimal b = arr.get(0).bestDist;
            NNData<KDPoint> out = arr.get(0);
            int index = 0;
            for (int i = 0; i < arr.size(); i++) {
                if (arr.get(i).bestDist.compareTo(b) < 0) {
                    b = arr.get(i).bestDist;
                    out = arr.get(i);
                    index = i;
                }
            }
            queue.enqueue(arr.get(index).bestGuess, arr.get(index).bestDist);
            arr.remove(index);
            k--;
        }
    }

    /**
     * Returns the height of the subtree rooted at the current node. Recall our definition of height for binary trees:
     * <ol>
     *     <li>A null tree has a height of 0.</li>
     *     <li>A non-null tree has a height equal to max(height(left_subtree), height(right_subtree))</li>
     * </ol>
     * @return the height of the subtree rooted at the current node.
     */
    public int height(){
        return getHeight(this);
    }

    private int getHeight(KDTreeNode n) {
        if (n == null) return -1;
        if (n.left == null && n.right == null)
            return 0;
        return getHeight(n.left) > getHeight(n.right) ? getHeight(n.left) + 1 : getHeight(n.right) + 1;
    }


    /**
     * A simple getter for the {@link KDPoint} held by the current node. Remember: {@link KDPoint}s ARE
     * MUTABLE, SO WE NEED TO DO DEEP COPIES!!!
     * @return The {@link KDPoint} held inside this.
     */
    public KDPoint getPoint(){
        KDPoint kdp = new KDPoint(p);
        return kdp;
    }

    public KDTreeNode getLeft(){
        return left;
    }

    public KDTreeNode getRight(){
        return right;
    }
}