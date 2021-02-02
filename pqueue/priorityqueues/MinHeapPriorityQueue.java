package projects.pqueue.priorityqueues; // ******* <---  DO NOT ERASE THIS LINE!!!! *******


/* *****************************************************************************************
 * THE FOLLOWING IMPORTS WILL BE NEEDED BY YOUR CODE, BECAUSE WE REQUIRE THAT YOU USE
 * ANY ONE OF YOUR EXISTING MINHEAP IMPLEMENTATIONS TO IMPLEMENT THIS CLASS. TO ACCESS
 * YOUR MINHEAP'S METHODS YOU NEED THEIR SIGNATURES, WHICH ARE DECLARED IN THE MINHEAP
 * INTERFACE. ALSO, SINCE THE PRIORITYQUEUE INTERFACE THAT YOU EXTEND IS ITERABLE, THE IMPORT OF ITERATOR
 * IS NEEDED IN ORDER TO MAKE YOUR CODE COMPILABLE. THE IMPLEMENTATIONS OF CHECKED EXCEPTIONS
 * ARE ALSO MADE VISIBLE BY VIRTUE OF THESE IMPORTS.
 ** ********************************************************************************* */


import projects.UnimplementedMethodException;
import projects.pqueue.exceptions.InvalidPriorityException;
import projects.pqueue.heaps.ArrayMinHeap;
import projects.pqueue.heaps.EmptyHeapException;
import projects.pqueue.heaps.MinHeap;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
/**
 * <p>{@link MinHeapPriorityQueue} is a {@link PriorityQueue} implemented using a {@link MinHeap}.</p>
 *
 * <p>You  <b>must</b> implement the methods of this class! To receive <b>any credit</b> for the unit tests
 * related to this class, your implementation <b>must</b> use <b>whichever</b> {@link MinHeap} implementation
 * among the two that you should have implemented you choose!</p>
 *
 * @author  ---- Haitian Hao ----
 *
 * @param <T> The Type held by the container.
 *
 * @see LinearPriorityQueue
 * @see MinHeap
 * @see PriorityQueue
 */
public class MinHeapPriorityQueue<T> implements PriorityQueue<T>{

	/* ***********************************************************************************
	 * Write any private data elements or private methods for MinHeapPriorityQueue here...*
	 * ***********************************************************************************/

	private ArrayMinHeap<Node> arr;
	private int checkForConcurrModification = 0;

	private class Node implements Comparable<Node> {
		T data;
		int priority;

		private Node(T data, int priority){
			this.data = data;
			this.priority = priority;
		}

		@Override
		public int compareTo(Node other) {
			if(this.priority < other.priority)
				return -1;
			else if(this.priority > other.priority)
				return 1;
			else
				return 0;
		}

	}


	/* *********************************************************************************************************
	 * Implement the following public methods. You should erase the throwings of UnimplementedMethodExceptions.*
	 ***********************************************************************************************************/
		/**
	 * Simple default constructor.
	 */
	public MinHeapPriorityQueue(){
		arr = new ArrayMinHeap();
	}

	@Override
	public void enqueue(T element, int priority) throws InvalidPriorityException {	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if(priority < 1)
			throw new InvalidPriorityException("Invalid priority!");
		checkForConcurrModification++;
		Node n = new Node(element, priority);
		arr.insert(n);
	}

	@Override
	public T dequeue() throws EmptyPriorityQueueException {		// DO *NOT* ERASE THE "THROWS" DECLARATION!
		Node n;
		try {
			n = arr.getMin();
			arr.deleteMin();
		}catch (EmptyHeapException e){
			String s = e.getMessage();
			throw new EmptyPriorityQueueException(s);
		}
		checkForConcurrModification++;
		return n.data;
	}

	@Override
	public T getFirst() throws EmptyPriorityQueueException {	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		T output;
		try {
			output = arr.getMin().data;
		}catch (EmptyHeapException e){
			throw new EmptyPriorityQueueException(e.getMessage());
		}
		return output;
	}

	@Override
	public int size() {
		return arr.size();
	}

	@Override
	public boolean isEmpty() {
		return arr.isEmpty();
	}


	@Override
	public Iterator<T> iterator() {
		return new MyIterator<T>(arr);
	}

	private class MyIterator<T> implements Iterator<T>{

		ArrayMinHeap<Node> innerArray;
		Iterator<Node> it;

		private MyIterator(ArrayMinHeap<Node> arr) {
			innerArray = arr;
			it = arr.iterator();
			checkForConcurrModification = 0;
		}

		@Override
		public boolean hasNext() {
			return it.hasNext();
		}

		@Override
		public T next() {
			if(checkForConcurrModification != 0)
				throw new ConcurrentModificationException();

			return (T)(it.next().data);
		}
	}


}
