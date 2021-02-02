package projects.pqueue.priorityqueues; // ******* <---  DO NOT ERASE THIS LINE!!!! *******

/* *****************************************************************************************
 * THE FOLLOWING IMPORTS ARE HERE ONLY TO MAKE THE JAVADOC AND iterator() METHOD SIGNATURE
 * "SEE" THE RELEVANT CLASSES. SOME OF THOSE IMPORTS MIGHT *NOT* BE NEEDED BY YOUR OWN
 * IMPLEMENTATION, AND IT IS COMPLETELY FINE TO ERASE THEM. THE CHOICE IS YOURS.
 * ********************************************************************************** */

import demos.GenericArrays;
import projects.UnimplementedMethodException;
import projects.pqueue.exceptions.InvalidCapacityException;
import projects.pqueue.exceptions.InvalidPriorityException;
import projects.pqueue.fifoqueues.FIFOQueue;
import projects.pqueue.heaps.ArrayMinHeap;

import java.util.*;
/**
 * <p>{@link LinearPriorityQueue} is a {@link PriorityQueue} implemented as a linear {@link java.util.Collection}
 * of common {@link FIFOQueue}s, where the {@link FIFOQueue}s themselves hold objects
 * with the same priority (in the order they were inserted).</p>
 *
 * <p>You  <b>must</b> implement the methods in this file! To receive <b>any credit</b> for the unit tests related to
 * this class, your implementation <b>must</b>  use <b>whichever</b> linear {@link Collection} you want (e.g
 * {@link ArrayList}, {@link LinkedList}, {@link java.util.Queue}), or even the various {@link List} and {@link FIFOQueue}
 * implementations that we provide for you. You can also use <b>raw</b> arrays, but take a look at {@link GenericArrays}
 * if you intend to do so. Note that, unlike {@link ArrayMinHeap}, we do not insist that you use a contiguous storage
 * {@link Collection}, but any one available (including {@link LinkedList}) </p>
 *
 * @param <T> The type held by the container.
 *
 * @author  ---- Haitian Hao ----
 *
 * @see MinHeapPriorityQueue
 * @see PriorityQueue
 * @see GenericArrays
 */
public class LinearPriorityQueue<T> implements PriorityQueue<T> {

	/* ***********************************************************************************
	 * Write any private data elements or private methods for LinearPriorityQueue here...*
	 * ***********************************************************************************/
	private int checkForConcurrModification = 0;
	private ArrayList<Node> pq;
	private int capacity;

	private class Node {
		T data;
		int priority;

		private Node(T data, int priority){
			this.data = data;
			this.priority = priority;
		}
	}

	/* *********************************************************************************************************
	 * Implement the following public methods. You should erase the throwings of UnimplementedMethodExceptions.*
	 ***********************************************************************************************************/

	/**
	 * Default constructor initializes the element structure with
	 * a default capacity. This default capacity will be the default capacity of the
	 * underlying element structure that you will choose to use to implement this class.
	 */
	public LinearPriorityQueue(){
		pq = new ArrayList<>();
		capacity = 100;
	}

	/**
	 * Non-default constructor initializes the element structure with
	 * the provided capacity. This provided capacity will need to be passed to the default capacity
	 * of the underlying element structure that you will choose to use to implement this class.
	 * @see #LinearPriorityQueue()
	 * @param capacity The initial capacity to endow your inner implementation with.
	 * @throws InvalidCapacityException if the capacity provided is negative.
	 */
	public LinearPriorityQueue(int capacity) throws InvalidCapacityException{	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if(capacity < 0)
			throw new InvalidCapacityException("Negative capacity!");
		this.capacity = capacity;
		pq = new ArrayList<>();
	}

	@Override
	public void enqueue(T element, int priority) throws InvalidPriorityException{	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if(priority < 1)
			throw new InvalidPriorityException("Invalid priority!");
		Node n = new Node(element, priority);
		checkForConcurrModification++;

		if(pq.size() == 0)
			pq.add(n);
		else if(pq.size() == 1){
			if(n.priority < pq.get(0).priority){
				pq.add(0, n);
			}
			else{
				pq.add(n);
			}
		}
		else{
			for(int i = 0; i < pq.size(); i++) {
				if(pq.get(i).priority > n.priority){
					pq.add(i, n);
					return;
				}
			}
			pq.add(n);
		}
	}

	@Override
	public T dequeue() throws EmptyPriorityQueueException { 	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if(pq.size() == 0)
			throw new EmptyPriorityQueueException("The queue is empty!");
		checkForConcurrModification++;
		T output = pq.get(0).data;
		pq.remove(0);
		return output;
	}

	@Override
	public T getFirst() throws EmptyPriorityQueueException {	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if(pq.size() == 0)
			throw new EmptyPriorityQueueException("The queue is empty!");
		return pq.get(0).data;
	}

	@Override
	public int size() {
		return pq.size();
	}

	@Override
	public boolean isEmpty() {
		return pq.size() == 0;
	}

	@Override
	public Iterator<T> iterator() {
		return new MyIterator<T>(pq);
	}

	private class MyIterator<T> implements Iterator<T>{
		private ArrayList<Node> queue;
		int index = 0;

		private MyIterator(ArrayList<Node> q) {
			checkForConcurrModification = 0;
			queue = q;
		}

		@Override
		public boolean hasNext() {
			return index <= queue.size() - 1;
		}

		@Override
		public T next() {
			if(checkForConcurrModification != 0)
				throw new ConcurrentModificationException();

			index++;
			return (T)(queue.get(index-1).data);
		}
	}

}