package projects.pqueue.lists;

/**
 * <p>A {@link Exception} thrown by tree-based data structures when the tree is empty.</p>
 *
 * <p>Simply returning {@code null}  from search methods is ambiguous on the part of the caller, because the caller can then not tell whether
 * the tree is empty or not by simply checking for {@code null}-ity of the object reference. While, strictly
 * speaking, in both cases of an empty tree and a non-empty tree where the element in question is simply not there
 * we have the same semantics, it is good practice to notify the caller that a search has been made for a tree
 * that is empty, so that the application can make sure that at the time of the call the tree is guaranteed
 * to have some data stored.</p>
 *
 * <p>You should <b>not</b> edit this class! It is given to you as a resource for your project.</p>
 *
 * @see projects.pqueue.lists.EmptyListException
 * @see projects.pqueue.trees.EmptyTreeException
 * @see projects.pqueue.heaps.EmptyHeapException
 *
 * @author  <a href="https://github.com/JasonFil">Jason Filippou</a>
 */
public class EmptyListException extends Exception{
	public EmptyListException(String msg){
		super(msg); // Another way to ensure that the message is saved. Pretty straightforward.
	}
}
