package projects.pqueue.heaps; // ******* <---  DO NOT ERASE THIS LINE!!!! *******

/* *****************************************************************************************
 * THE FOLLOWING IMPORT IS NECESSARY FOR THE ITERATOR() METHOD'S SIGNATURE. FOR THIS
 * REASON, YOU SHOULD NOT ERASE IT! YOUR CODE WILL BE UNCOMPILABLE IF YOU DO!
 * ********************************************************************************** */

import projects.UnimplementedMethodException;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;


/**
 * <p>{@link ArrayMinHeap} is a {@link MinHeap} implemented using an internal array. Since heaps are <b>complete</b>
 * binary trees, using contiguous storage to store them is an excellent idea, since with such storage we avoid
 * wasting bytes per {@code null} pointer in a linked implementation.</p>
 *
 * <p>You <b>must</b> edit this class! To receive <b>any</b> credit for the unit tests related to this class,
 * your implementation <b>must</b> be a <b>contiguous storage</b> implementation based on a linear {@link java.util.Collection}
 * like an {@link java.util.ArrayList} or a {@link java.util.Vector} (but *not* a {@link java.util.LinkedList} because it's *not*
 * contiguous storage!). or a raw Java array. We provide an array for you to start with, but if you prefer, you can switch it to a
 * {@link java.util.Collection} as mentioned above. </p>
 *
 * @author -- Haitian Hao ---
 *
 * @see MinHeap
 * @see LinkedMinHeap
 * @see demos.GenericArrays
 */

public class ArrayMinHeap<T extends Comparable<T>> implements MinHeap<T> {

	/* *****************************************************************************************************************
	 * This array will store your data. You may replace it with a linear Collection if you wish, but
	 * consult this class' 	 * JavaDocs before you do so. We allow you this option because if you aren't
	 * careful, you can end up having ClassCastExceptions thrown at you if you work with a raw array of Objects.
	 * See, the type T that this class contains needs to be Comparable with other types T, but Objects are at the top
	 * of the class hierarchy; they can't be Comparable, Iterable, Clonable, Serializable, etc. See GenericArrays.java
	 * under the package demos* for more information.
	 * *****************************************************************************************************************/
	private Object[] data;
	private int checkForConcurrModification = 0;
	/* *********************************************************************************** *
	 * Write any further private data elements or private methods for ArrayMinHeap here...*
	 * *************************************************************************************/

	// Find the parent node of a node at index i in the array
	private int getParent(int i){
		return (i-1)/2;
	}

	// Find the valid smaller child node of a node at index i in the array
	private int getChild(int i, Object[] objList){
		if((2 * i + 2 > objList.length - 1)){
			return 2 * i + 1;
		}
		if(((T)(objList[2 * i + 1])).compareTo((T)(objList[2 * i + 2])) <= 0){
			return 2 * i + 1;
		}else{
			return 2 * i + 2;
		}
	}


	/* *********************************************************************************************************
	 * Implement the following public methods. You should erase the throwings of UnimplementedMethodExceptions.*
	 ***********************************************************************************************************/

	/**
	 * Default constructor initializes the data structure with some default
	 * capacity you can choose.
	 */
	public ArrayMinHeap(){
		this.data = new Object[0];
	}

	/**
	 *  Second, non-default constructor which provides the element with which to initialize the heap's root.
	 *  @param rootElement the element to create the root with.
	 */
	public ArrayMinHeap(T rootElement){
		this.data = new Object[1];
		this.data[0] = rootElement;
	}

	/**
	 * Copy constructor initializes {@code this} as a carbon copy of the {@link MinHeap} parameter.
	 *
	 * @param other The MinHeap object to base construction of the current object on.
	 */
	public ArrayMinHeap(MinHeap<T> other){
		Object[] array = ((ArrayMinHeap)other).data;
		this.data = new Object[array.length];
		for(int i=0; i<array.length; i++){
			this.data[i] = array[i];
		}
	}

	/**
	 * Standard {@code equals()} method. We provide it for you: DO NOT ERASE! Consider its implementation when implementing
	 * {@link #ArrayMinHeap(MinHeap)}.
	 * @return {@code true} if the current object and the parameter object
	 * are equal, with the code providing the equality contract.
	 * @see #ArrayMinHeap(MinHeap)
	 */
	@Override
	public boolean equals(Object other){
		if(other == null || !(other instanceof MinHeap))
			return false;
		Iterator itThis = iterator();
		Iterator itOther = ((MinHeap) other).iterator();
		while(itThis.hasNext())
			if(!itThis.next().equals(itOther.next()))
				return false;
		return !itOther.hasNext();
	}


	@Override
	public void insert(T element) {
		checkForConcurrModification++;
		Object[] newArray = new Object[this.data.length + 1];
		int index = 0;
		for(Object obj : this.data){
			newArray[index] = obj;
			index++;
		}
		newArray[index] = element;
		this.data = newArray;

		int indexCursor = index;
		while(indexCursor > 0){
			if(((T)this.data[getParent(indexCursor)]).compareTo(((T)this.data[indexCursor])) > 0){
				swap(this.data, indexCursor, getParent(indexCursor));
				indexCursor = getParent(indexCursor);
			}
			else break;
		}
	}

	private void swap(Object[] arr, int index1, int index2){
		T tmp = (T)arr[index1];
		arr[index1] = arr[index2];
		arr[index2] = tmp;
	}

	@Override
	public T deleteMin() throws EmptyHeapException { // DO *NOT* ERASE THE "THROWS" DECLARATION!
		if(this.data.length == 0){
			throw new EmptyHeapException("The heap is empty!");
		}
		checkForConcurrModification++;
		T min = (T)this.data[0];
		if(this.data.length == 1){
			this.data = new Object[0];
		}
		else {
			Object[] newArray = new Object[this.data.length - 1];
			newArray[0] = this.data[this.data.length - 1];
			for (int i = 0; i < newArray.length; i++) {
				if (i >= 1) {
					newArray[i] = this.data[i];
				}
			}

			int indexCursor = 0;
			while (2 * indexCursor + 1 <= newArray.length - 1) {
				int childIndex = getChild(indexCursor, newArray);
				if (((T) newArray[indexCursor]).compareTo((T) newArray[childIndex]) > 0) {
					swap(newArray, indexCursor, childIndex);
					indexCursor = childIndex;
				} else break;
			}
			this.data = newArray;
		}
		return min;
	}

	@Override
	public T getMin() throws EmptyHeapException {	// DO *NOT* ERASE THE "THROWS" DECLARATION!
		if(this.data.length == 0){
			throw new EmptyHeapException("The heap is empty!");
		}
		return (T)this.data[0];
	}

	@Override
	public int size() {
		return this.data.length;
	}

	@Override
	public boolean isEmpty() {
		return this.data.length == 0;
	}

	/**
	 * Standard equals() method.
	 * @return {@code true} if the current object and the parameter object
	 * are equal, with the code providing the equality contract.
	 */


	@Override
	public Iterator<T> iterator() {
		return new MyIterator<T>(this.data);
	}

	// This is my private Iterator generator
	private class MyIterator<T extends Comparable<T>> implements Iterator<T>{
		Object[] innerArray;

		// Constructor
		public MyIterator(Object[] array) {
			innerArray = new Object[array.length];
			for(int i = 0; i < array.length; i++)
				innerArray[i] = array[i];
			checkForConcurrModification = 0;
		}

		@Override
		public boolean hasNext() {
			return innerArray.length > 0;
		}

		@Override
		public T next() {
			if(checkForConcurrModification != 0){
				throw new ConcurrentModificationException();
			}

			T min = (T)innerArray[0];

			if(innerArray.length == 1){
				innerArray = new Object[0];
			}
			else {
				Object[] newArray = new Object[innerArray.length - 1];
				newArray[0] = innerArray[innerArray.length - 1];
				for (int i = 0; i < newArray.length; i++) {
					if (i >= 1) {
						newArray[i] = innerArray[i];
					}
				}

				int indexCursor = 0;
				while (2 * indexCursor + 1 <= newArray.length - 1) {
					int childIndex = getChild(indexCursor, newArray);
					if (((T) newArray[indexCursor]).compareTo((T) newArray[childIndex]) > 0) {
						swap(newArray, indexCursor, childIndex);
						indexCursor = childIndex;
					} else break;
				}
				innerArray = newArray;
			}
			return min;
		}
	}

}
