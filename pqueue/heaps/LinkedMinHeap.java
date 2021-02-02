package projects.pqueue.heaps; // ******* <---  DO NOT ERASE THIS LINE!!!! *******

/* *****************************************************************************************
 * THE FOLLOWING IMPORT IS NECESSARY FOR THE ITERATOR() METHOD'S SIGNATURE. FOR THIS
 * REASON, YOU SHOULD NOT ERASE IT! YOUR CODE WILL BE UNCOMPILABLE IF YOU DO!
 * ********************************************************************************** */

import projects.UnimplementedMethodException;

import java.lang.management.MemoryNotificationInfo;
import java.util.*;
import java.util.Iterator;

/**
 * <p>A {@link LinkedMinHeap} is a tree (specifically, a <b>complete</b> binary tree) where every node is
 * smaller than or equal to its descendants (as defined by the {@link Comparable#compareTo(Object)} overridings of the type T).
 * Percolation is employed when the root is deleted, and insertions guarantee maintenance of the heap property in logarithmic time. </p>
 *
 * <p>You <b>must</b> edit this class! To receive <b>any</b> credit for the unit tests related to this class,
 * your implementation <b>must</b> be a &quot;linked&quot;, <b>non-contiguous storage</b> implementation based on a
 * binary tree of nodes and references. Use the skeleton code we have provided to your advantage, but always remember
 * that the only functionality our tests can test is {@code public} functionality.</p>
 * 
 * @author --- Haitian Hao ---
 *
 * @param <T> The {@link Comparable} type of object held by {@code this}.
 *
 * @see MinHeap
 * @see ArrayMinHeap
 */
public class LinkedMinHeap<T extends Comparable<T>> implements MinHeap<T> {

	/* ***********************************************************************
	 * An inner class representing a minheap's node. YOU *SHOULD* BUILD YOUR *
	 * IMPLEMENTATION ON TOP OF THIS CLASS!                                  *
 	 * ********************************************************************* */
	private class MinHeapNode {
		private T data;
		private MinHeapNode lChild, rChild;

        /* *******************************************************************
         * Write any further data elements or methods for MinHeapNode here...*
         ********************************************************************* */

        private MinHeapNode parent;
		private int depth;
	}

	/* *********************************
	  * Root of your tree: DO NOT ERASE!
	  * *********************************
	 */
	private MinHeapNode root;

	private int checkForConcurrModification = 0;


    /* *********************************************************************************** *
     * Write any further private data elements or private methods for LinkedMinHeap here...*
     * *************************************************************************************/

	private void addParentalRelation(MinHeapNode node) {
		if(node == null) return;
		if(node.lChild != null){
			node.lChild.parent = node;
		}
		if(node.rChild != null){
			node.rChild.parent = node;
		}
		addParentalRelation(node.lChild);
		addParentalRelation(node.rChild);
	}

	private void addDepthParameter(MinHeapNode node, int depth) {
		if (node == null) return;
		node.depth = depth;
		addDepthParameter(node.lChild, 1 + depth);
		addDepthParameter(node.rChild, 1 + depth);
	}

	private void swap(MinHeapNode n1, MinHeapNode n2){
		T tmp = n1.data;
		n1.data = n2.data;
		n2.data = tmp;
	}

    /* *********************************************************************************************************
     * Implement the following public methods. You should erase the throwings of UnimplementedMethodExceptions.*
     ***********************************************************************************************************/

	/**
	 * Default constructor.
	 */
	public LinkedMinHeap() {
//		this.root = new MinHeapNode();
//		root.lChild = null;
//		root.rChild = null;
//		root.data = null;
//		addParentalRelation(this.root);
//		addDepthParameter(this.root, 0);
		this.root = null;
	}

	/**
	 * Second constructor initializes {@code this} with the provided element.
	 *
	 * @param rootElement the data to create the root with.
	 */
	public LinkedMinHeap(T rootElement) {
		this.root = new MinHeapNode();
		root.data = rootElement;
		addParentalRelation(this.root);
		addDepthParameter(this.root, 0);
	}

	/**
	 * Copy constructor initializes {@code this} as a carbon
	 * copy of the parameter, which is of the general type {@link MinHeap}!
	 * Since {@link MinHeap} is an {@link Iterable} type, we can access all
	 * of its elements in proper order and insert them into {@code this}.
	 *
	 * @param other The {@link MinHeap} to copy the elements from.
	 */
	public LinkedMinHeap(MinHeap<T> other) {
		this.root = addChild(this.root, ((LinkedMinHeap<T>) other).root);
		addParentalRelation(this.root);
		addDepthParameter(this.root, 0);
	}

	// Recursive method
	private MinHeapNode addChild(MinHeapNode root, MinHeapNode addNode) {
		if(addNode == null) return root;
		root = new MinHeapNode();
		root.data = addNode.data;
		root.lChild = addChild(root.lChild, addNode.lChild);
		root.rChild = addChild(root.rChild, addNode.rChild);
		return root;
	}


    /**
     * Standard {@code equals} method. We provide this for you. DO NOT EDIT!
     * You should notice how the existence of an {@link Iterator} for {@link MinHeap}
     * allows us to access the elements of the argument reference. This should give you ideas
     * for {@link #LinkedMinHeap(MinHeap)}.
     * @return {@code true} If the parameter {@code Object} and the current MinHeap
     * are identical Objects.
     *
     * @see Object#equals(Object)
     * @see #LinkedMinHeap(MinHeap)
     */
	/**
	 * Standard equals() method.
	 *
	 * @return {@code true} If the parameter Object and the current MinHeap
	 * are identical Objects.
	 */
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MinHeap))
			return false;
		Iterator itThis = iterator();
		Iterator itOther = ((MinHeap) other).iterator();
		while (itThis.hasNext())
			if (!itThis.next().equals(itOther.next()))
				return false;
		return !itOther.hasNext();
	}

	@Override
	public boolean isEmpty() {
		return this.root == null;
	}

	@Override
	public int size() {
    	return count(this.root);
	}

	// Recursively count the number of nodes in the tree.
	private int count(MinHeapNode node) {
    	if(node == null) return 0;
    	return 1 + count(node.lChild) + count(node.rChild);
	}


	@Override
	public void insert(T element) {
		checkForConcurrModification++;

		MinHeapNode insertion = new MinHeapNode();
		insertion.data = element;
		if(this.root == null){
			this.root = insertion;
			return;
		}

		int deepest = maxDepth(this.root);
		ArrayList<MinHeapNode> listOfNodes = new ArrayList<>();
		addList(listOfNodes, this.root);
		boolean exist = false;
		for(int i = 0; i < listOfNodes.size(); i++){
			if(listOfNodes.get(i).depth == deepest - 1) {
				exist = true;
				MinHeapNode parent = listOfNodes.get(i);
				insertion.parent = parent;

				if (parent.lChild == null) parent.lChild = insertion;
				else parent.rChild = insertion;

				break;
			}
		}
		if(!exist){
			MinHeapNode curr = this.root;
			while(curr.lChild != null){
				curr = curr.lChild;
			}
			curr.lChild = insertion;
			insertion.parent = curr;
		}

		// Do the swap if needed
		if (this.size() <= 1) return;

		MinHeapNode curr = insertion;
		while(curr.parent != null) {
			if(curr.data.compareTo(curr.parent.data) < 0) {
				// Simply swap the data inside! Don't need to swap the whole nodes!!
				// Man...it is now so much easier......
				swap(curr, curr.parent);
				curr = curr.parent;
			}
			else{
				break;
			}
		}
	}

	private int maxDepth(MinHeapNode root) {
    	MinHeapNode curr = root;
    	while(curr.lChild != null){
    		curr = curr.lChild;
		}
    	return curr.depth;
	}

	// Collect all the nodes at the bottom of the tree.
	private void addList(ArrayList<MinHeapNode> arr, MinHeapNode node) {
    	if(node == null) return;
    	if(node.lChild == null && node.rChild == null){
    		arr.add(node);
		}
		addList(arr, node.lChild);
		addList(arr, node.rChild);
	}


	@Override
	public T getMin() throws EmptyHeapException {		// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if(this.isEmpty()){
			throw new EmptyHeapException("The heap is empty!");
		}
		return this.root.data;
	}

	@Override
	public T deleteMin() throws EmptyHeapException {    // DO *NOT* ERASE THE "THROWS" DECLARATION!
		if(this.isEmpty()){
			throw new EmptyHeapException("The heap is empty!");
		}
		checkForConcurrModification++;

		T output = this.root.data;

		if(this.size() == 1){
			this.root = null;
		}else if(this.size() == 2){
			this.root = this.root.lChild;
			this.root.parent = null;
		}else{
			// Find the last node at the bottom row
			MinHeapNode lastNode = findLastNode(this.root);

			// Get rid of this last node at the bottom
			if(lastNode.parent.lChild == lastNode){
				lastNode.parent.lChild = null;
			}
			if(lastNode.parent.rChild == lastNode){
				lastNode.parent.rChild = null;
			}
			lastNode.parent = null;

			// Insert the lastNode to the top of the heap
			// Again, simply replace the data would work!!!
			this.root.data = lastNode.data;

			// Do the swap if needed
			MinHeapNode curr = this.root;

			while(curr.lChild != null || curr.rChild != null){
				// Only left child exists
				if(curr.rChild == null){
					if(curr.lChild.data.compareTo(curr.data) < 0){
						swap(curr, curr.lChild);
						curr = curr.lChild;
					}else{
						break;
					}
				}
				// Both children exist
				else{
					if(curr.lChild.data.compareTo(curr.rChild.data) <= 0){
						if(curr.lChild.data.compareTo(curr.data) < 0){
							swap(curr, curr.lChild);
							curr = curr.lChild;
						}else{
							break;
						}
					}
					else{
						if(curr.rChild.data.compareTo(curr.data) < 0){
							swap(curr, curr.rChild);
							curr = curr.rChild;
						}else{
							break;
						}
					}
				}

			}
		}
		return output;
    }


    private MinHeapNode findLastNode(MinHeapNode root) {
    	MinHeapNode lastNode = new MinHeapNode();
		ArrayList<MinHeapNode> arrOfBottoms = new ArrayList<>();
		addList(arrOfBottoms, root);
		int maxDepth = maxDepth(root);
		boolean exist = false;
		for(int i = 0; i < arrOfBottoms.size(); i++){
			if(arrOfBottoms.get(i).depth == maxDepth - 1){
				exist = true;
				lastNode = arrOfBottoms.get(i-1);
				break;
			}
		}
		if(!exist){
			lastNode = arrOfBottoms.get(arrOfBottoms.size() - 1);
		}
		return lastNode;
	}



	@Override
	public Iterator<T> iterator() {
		return new MyIterator<>(this.root);
	}

	private class MyIterator<T extends Comparable<T>> implements Iterator<T>{
		int size = 0;
		MinHeapNode innerRoot = null;
		MinHeapNode iteratorHeap = null;

		// Constructor
		public MyIterator(MinHeapNode root) {
			checkForConcurrModification = 0;
			innerRoot = addChild(innerRoot, root);
			addParentalRelation(innerRoot);
			addDepthParameter(innerRoot, 0);
			size = count(innerRoot);

			iteratorHeap = addChild(iteratorHeap, root);
			addParentalRelation(iteratorHeap);
			addDepthParameter(iteratorHeap, 0);
		}

		@Override
		public boolean hasNext(){
			return size > 0;
		}


		@Override
		public T next() {
			if(checkForConcurrModification != 0)
				throw new ConcurrentModificationException();

			T output = (T)iteratorHeap.data;
			if(count(iteratorHeap) == 1){
				iteratorHeap = null;
			}else if(count(iteratorHeap) == 2){
				iteratorHeap = iteratorHeap.lChild;
				iteratorHeap.parent = null;
			}else{
				// Find the last node at the bottom row
				MinHeapNode lastNode = findLastNode(iteratorHeap);

				// Get rid of this last node at the bottom
				if(lastNode.parent.lChild == lastNode){
					lastNode.parent.lChild = null;
				}
				if(lastNode.parent.rChild == lastNode){
					lastNode.parent.rChild = null;
				}
				lastNode.parent = null;

				// Insert the lastNode to the top of the heap
				// Again, simply replace the data would work!!!
				iteratorHeap.data = lastNode.data;

				// Do the swap if needed
				MinHeapNode curr = iteratorHeap;

				while(curr.lChild != null || curr.rChild != null){
					// Only left child exists
					if(curr.rChild == null){
						if(curr.lChild.data.compareTo(curr.data) < 0){
							swap(curr, curr.lChild);
							curr = curr.lChild;
						}else{
							break;
						}
					}
					// Both children exist
					else{
						if(curr.lChild.data.compareTo(curr.rChild.data) <= 0){
							if(curr.lChild.data.compareTo(curr.data) < 0){
								swap(curr, curr.lChild);
								curr = curr.lChild;
							}else{
								break;
							}
						}
						else{
							if(curr.rChild.data.compareTo(curr.data) < 0){
								swap(curr, curr.rChild);
								curr = curr.rChild;
							}else{
								break;
							}
						}
					}

				}
			}
			size--;
			return output;
		}
	}
}
