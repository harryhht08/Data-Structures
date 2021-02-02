package projects.pqueue;

import org.junit.Test;
import projects.pqueue.exceptions.InvalidCapacityException;
import projects.pqueue.exceptions.InvalidPriorityException;
import projects.pqueue.heaps.ArrayMinHeap;
import projects.pqueue.heaps.EmptyHeapException;
import projects.pqueue.heaps.LinkedMinHeap;
import projects.pqueue.heaps.MinHeap;
import projects.pqueue.priorityqueues.EmptyPriorityQueueException;
import projects.pqueue.priorityqueues.LinearPriorityQueue;
import projects.pqueue.priorityqueues.MinHeapPriorityQueue;
import projects.pqueue.priorityqueues.PriorityQueue;

import java.util.Iterator;
import static org.junit.Assert.*;
import java.util.ArrayList;


/**
 * {@link StudentTests} is a {@code jUnit} testing library which you should extend with your own tests.
 *
 * @author  <a href="https://github.com/JasonFil">Jason Filippou</a> and --- YOUR NAME HERE! ----
 */
public class StudentTests {

    private static String throwableInfo(Throwable thrown){
        return "Caught a " + thrown.getClass().getSimpleName() +
                " with message: " + thrown.getMessage();
    }

    private MinHeap<String> myHeap;
    private PriorityQueue<String> myQueue;

    @Test
    public void initAndAddOneElement() throws InvalidPriorityException {
        try {
            myHeap = new ArrayMinHeap<>();
            myQueue = new MinHeapPriorityQueue<>();
        } catch(Throwable t){
            fail(throwableInfo(t));
        }
        assertTrue("After initialization, all MinHeap and PriorityQueue implementations should report that they are empty.",
                myHeap.isEmpty() && myQueue.isEmpty());
        assertTrue("After initialization, all MinHeap and PriorityQueue implementations should report a size of 0.",
                (myHeap.size() == 0) && (myQueue.size() == 0));
        myHeap.insert("Mary");
        assertEquals("After inserting an element, ArrayMinHeap instances should report a size of 1.", 1, myHeap.size());

        // MinHeap::enqueue() declares that it checks InvalidPriorityException if priority <= 0 (from the docs of MinHeap).
        // In this case, we know for sure that InvalidPriorityException should *not* be thrown, since priority = 2 >= 0.
        // To avoid cluttering a code with "dummy" try-catch blocks, we declare InvalidPriorityException as checked from
        // this test as well. This is why we have the throws declaration after the name of the test.
        myQueue.enqueue("Jason", 2);
        assertEquals("After inserting an element, MinHeapPriorityQueue instances should report a size of 1.", 1, myQueue.size());
    }

    // Here is one simple way to write tests that expect an Exception to be thrown. Another, more powerful method is to
    // use the class org.junit.rules.ExpectedException: https://junit.org/junit4/javadoc/4.12/org/junit/rules/ExpectedException.html
    @Test(expected = InvalidCapacityException.class)
    public void ensureInvalidCapacityExceptionThrown() throws InvalidCapacityException{
         myQueue = new LinearPriorityQueue<>(-2);
    }

    @Test(expected = InvalidPriorityException.class)
    public void ensureInvalidPriorityExceptionThrown() throws InvalidPriorityException, InvalidCapacityException{
        myQueue = new LinearPriorityQueue<>(4);
        myQueue.enqueue("Billy", -1);
    }

    @Test
    public void testEnqueingOrder() throws InvalidPriorityException, EmptyPriorityQueueException {
        myQueue = new MinHeapPriorityQueue<>();
        myQueue.enqueue("Ashish", 8);
        myQueue.enqueue("Diana", 2);        // Lower priority, so should be up front.
        myQueue.enqueue("Adam", 2);        // Same priority, but should be second because of FIFO.
        assertEquals("We were expecting Diana up front.", "Diana", myQueue.getFirst());
    }

    @Test
    public void testDequeuingOrder() throws InvalidPriorityException, EmptyPriorityQueueException {
        testEnqueingOrder();    // To populate myQueue with the same elements.
        myQueue.dequeue();      // Now Adam should be up front.
        assertEquals("We were expecting Adam up front.", "Adam", myQueue.getFirst());
    }

    /* ******************************************************************************************************** */
    /* ********************** YOU SHOULD ADD TO THESE UNIT TESTS BELOW. *************************************** */
    /* ******************************************************************************************************** */

    @Test
    public void testLinkedMinHeap() throws EmptyHeapException {
        LinkedMinHeap arr = new LinkedMinHeap();
        assertEquals(0, arr.size());
        arr.insert(1);
        arr.insert(2);
        arr.insert(3);
        arr.insert(4);
        arr.insert(2);
        assertEquals(5, arr.size());

//        Iterator it = arr.iterator();
//        while(it.hasNext()){
//            System.out.println(it.next());
//        }

        assertEquals(1, arr.deleteMin());
        assertEquals(4, arr.size());

        assertEquals(2, arr.deleteMin());
        assertEquals(3, arr.size());

        assertEquals(2, arr.deleteMin());
        assertEquals(2, arr.size());

        assertEquals(3, arr.deleteMin());
        assertEquals(1, arr.size());

        assertEquals(4, arr.deleteMin());
        assertEquals(0, arr.size());
    }

    @Test
    public void testArrayMinHeap() throws EmptyHeapException {
        ArrayMinHeap arr = new ArrayMinHeap();
        assertEquals(0, arr.size());
        arr.insert(1);
        arr.insert(2);
        arr.insert(3);
        arr.insert(4);
        arr.insert(2);
        assertEquals(5, arr.size());


        Iterator it = arr.iterator();
        while(it.hasNext()){
            System.out.println(it.next());
        }

        assertEquals(1, arr.deleteMin());
        assertEquals(4, arr.size());

        assertEquals(2, arr.deleteMin());
        assertEquals(3, arr.size());

        assertEquals(2, arr.deleteMin());
        assertEquals(2, arr.size());

        assertEquals(3, arr.deleteMin());
        assertEquals(1, arr.size());

        assertEquals(4, arr.deleteMin());
        assertEquals(0, arr.size());
    }


    @Test
    public void testLinearPQ() throws InvalidPriorityException, EmptyPriorityQueueException {
        LinearPriorityQueue pq = new LinearPriorityQueue();
        assertEquals(0, pq.size());

        pq.enqueue(1, 1);
        assertEquals(1, pq.size());

        pq.enqueue(5, 5);
        assertEquals(2, pq.size());

        pq.enqueue(2, 2);
        assertEquals(3, pq.size());

        pq.enqueue(3, 3);
        pq.enqueue(6, 6);
        pq.enqueue(4, 4);
        pq.enqueue(10, 4);
        assertEquals(7,pq.size());

//        Iterator it = pq.iterator();
//        while(it.hasNext()){
//            System.out.println(it.next());
//        }

        assertEquals(1, pq.dequeue());
        assertEquals(2, pq.dequeue());
        assertEquals(3, pq.dequeue());
        assertEquals(4, pq.dequeue());
        assertEquals(10, pq.dequeue());
        assertEquals(5, pq.dequeue());
        assertEquals(6, pq.dequeue());
        assertEquals(0, pq.size());
    }

    @Test
    public void testMinHeapPQ() throws InvalidPriorityException {
        MinHeapPriorityQueue pq = new MinHeapPriorityQueue();

        pq.enqueue(1, 1);
        pq.enqueue(52, 52);
        pq.enqueue(21, 21);
        pq.enqueue(34, 34);
        pq.enqueue(16, 16);
        pq.enqueue(4, 4);
        pq.enqueue(26, 26);
        pq.enqueue(13, 13);
        pq.enqueue(46, 46);
        pq.enqueue(44, 44);
        pq.enqueue(18,18);
        pq.enqueue(41,41);
        pq.enqueue(65,65);
        pq.enqueue(78,78);
        pq.enqueue(71,71);
        pq.enqueue(100,100);
        pq.enqueue(62,62);
        pq.enqueue(12,12);
        pq.enqueue(11,11);


        Iterator it = pq.iterator();
        while(it.hasNext()){
            System.out.println(it.next());
        }

    }







}
