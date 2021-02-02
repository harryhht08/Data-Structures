package projects.spatial.knnutils;

import projects.UnimplementedMethodException;

import javax.print.attribute.standard.NumberOfDocuments;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;


/**
 * <p>{@link BoundedPriorityQueue} is a priority queue whose number of elements
 * is bounded. Insertions are such that if the queue's provided capacity is surpassed,
 * its length is not expanded, but rather the maximum priority element is ejected
 * (which could be the element just attempted to be enqueued).</p>
 *
 * <p><b>YOU ***** MUST ***** IMPLEMENT THIS CLASS!</b></p>
 *
 * @author  <a href = "https://github.com/JasonFil/">Jason Filippou</a>
 *
 * @see PriorityQueue
 * @see PriorityQueueNode
 */
public class BoundedPriorityQueue<T> implements PriorityQueue<T>{

	/* *********************************************************************** */
	/* *************  PLACE YOUR PRIVATE FIELDS AND METHODS HERE: ************ */
	/* *********************************************************************** */

	private int capacity = 0;
 	private ArrayList<Node> arr;
 	private int checkChange;

 	private class Node {
 		private T data;
 		private BigDecimal priority;

 		public Node (T data, BigDecimal priority) {
 			this.data = data;
 			this.priority = priority;
		}
	}

	/* *********************************************************************** */
	/* ***************  IMPLEMENT THE FOLLOWING PUBLIC METHODS:  ************ */
	/* *********************************************************************** */

	/**
	 * Constructor that specifies the size of our queue.
	 * @param size The static size of the {@link BoundedPriorityQueue}. Has to be a positive integer.
	 * @throws IllegalArgumentException if size is not a strictly positive integer.
	 */
	public BoundedPriorityQueue(int size) throws IllegalArgumentException{
		if (size <= 0) throw new IllegalArgumentException();
		capacity = size;
		arr = new ArrayList<>();
	}

	/**
	 * <p>Enqueueing elements for BoundedPriorityQueues works a little bit differently from general case
	 * PriorityQueues. If the queue is not at capacity, the element is inserted at its
	 * appropriate location in the sequence. On the other hand, if the object is at capacity, the element is
	 * inserted in its appropriate spot in the sequence (if such a spot exists, based on its priority) and
	 * the maximum priority element is ejected from the structure.</p>
	 * 
	 * @param element The element to insert in the queue.
	 * @param priority The priority of the element to insert in the queue.
	 */
	@Override
	public void enqueue(T element, BigDecimal priority) {
		checkChange++;
		insertNode(element, priority);
		if (arr.size() > capacity) arr.remove(arr.size() - 1);
	}

	private void insertNode(T element, BigDecimal priority) {
		int index = -1;
		for (int i = 0; i < arr.size(); i++) {
			if (arr.get(i).priority.compareTo(priority) > 0) {
				index = i;
				break;
			}
		}
		if (index == -1) index = arr.size();
		arr.add(index, new Node(element, priority));
	}

	@Override
	public T dequeue() {
		checkChange++;
		Node n = arr.remove(0);
		return n.data;
	}

	@Override
	public T first() {
		if (arr.size() == 0) return null;
		return arr.get(0).data;
	}
	
	/**
	 * Returns the last element in the queue. Useful for cases where we want to 
	 * compare the priorities of a given quantity with the maximum priority of 
	 * our stored quantities. In a minheap-based implementation of any {@link PriorityQueue},
	 * this operation would scan O(n) nodes and O(nlogn) links. In an array-based implementation,
	 * it takes constant time.
	 * @return The maximum priority element in our queue, or null if the queue is empty.
	 */
	public T last() {
		if (arr.size() == 0) return null;
		return arr.get(arr.size() - 1).data;
	}

	/**
	 * Inspects whether a given element is in the queue. O(N) complexity.
	 * @param element The element to search for.
	 * @return {@code true} iff {@code element} is in {@code this}, {@code false} otherwise.
	 */
	public boolean contains(T element)
	{
		for (Node n : arr) {
			if (n.data.equals(element))
				return true;
		}
		return false;
	}

	@Override
	public int size() {
		return arr.size();
	}

	@Override
	public boolean isEmpty() {
		return arr.size() == 0;
	}

	@Override
	public Iterator<T> iterator() {
		return new myIter<T>(this.arr);
	}

	private class myIter<T> implements Iterator<T> {

		private ArrayList<T> array;
		private int curr = 0;

		private myIter(ArrayList<Node> arr) {
			array = new ArrayList<>();
			for (Node n : arr) {
				array.add((T) n.data);
			}
			checkChange = 0;
		}

		@Override
		public boolean hasNext() {
			return curr < array.size();
		}

		@Override
		public T next() {
			if (checkChange != 0) throw new ConcurrentModificationException();
			curr++;
			return array.get(curr - 1);
		}
	}

}
