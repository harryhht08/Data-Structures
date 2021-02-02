package projects.bpt;
import com.sun.security.jgss.GSSUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * A jUnit test suite for {@link BinaryPatriciaTrie}.
 *
 * @author Haitian Hao
 */
public class StudentTests {


    @Test public void testEmptyTrie() {
        BinaryPatriciaTrie trie = new BinaryPatriciaTrie();

        assertTrue("Trie should be empty",trie.isEmpty());
        assertEquals("Trie size should be 0", 0, trie.getSize());

        assertFalse("No string inserted so search should fail", trie.search("0101"));

    }

    @Test public void testFewInsertionsWithSearch() {
        BinaryPatriciaTrie trie = new BinaryPatriciaTrie();

        assertTrue("String should be inserted successfully",trie.insert("00000"));
        assertTrue("String should be inserted successfully",trie.insert("00011"));

        assertTrue("String should be inserted successfully",trie.insert("000100"));
        assertTrue("String should be inserted successfully",trie.insert("0001011"));

        assertFalse("Search should fail as string does not exist",trie.search("000"));

    }


    //testing isEmpty function
    @Test public void testFewInsertionsWithDeletion() {
        BinaryPatriciaTrie trie = new BinaryPatriciaTrie();

        trie.insert("000");
        trie.insert("001");
        trie.insert("011");
        trie.insert("1001");
        trie.insert("1");

        assertFalse("After inserting five strings, the trie should not be considered empty!", trie.isEmpty());
        assertEquals("After inserting five strings, the trie should report five strings stored.", 5, trie.getSize());

        trie.delete("0"); // Failed deletion; should affect exactly nothing.
        assertEquals("After inserting five strings and requesting the deletion of one not in the trie, the trie " +
                "should report five strings stored.", 5, trie.getSize());
        assertTrue("After inserting five strings and requesting the deletion of one not in the trie, the trie had some junk in it!",
                trie.isJunkFree());

        trie.delete("011"); // Successful deletion
        assertEquals("After inserting five strings and deleting one of them, the trie should report 4 strings.", 4, trie.getSize());
        assertTrue("After inserting five strings and deleting one of them, the trie had some junk in it!",
                trie.isJunkFree());
    }


    // My own tests
    @Test
    public void test01() {
        BinaryPatriciaTrie trie = new BinaryPatriciaTrie();

        trie.insert("00110");
        trie.insert("0010");
        trie.insert("000");
        trie.insert("001");
        trie.insert("011");
        trie.insert("1001");
        trie.insert("1");
        trie.insert("010101101011");
        trie.insert("10000110101");
        trie.insert("11111111");

        trie.delete("010101101010");
        trie.delete("010101101011");
        trie.delete("10000110101");
        trie.delete("1");

        System.out.println(trie.getLongest());
        System.out.println(trie.getSize());
        System.out.println(trie.isJunkFree());
    }


    @Test
    public void tryouts() {
        ArrayList<String> arr = new ArrayList<>();
        arr.add("00110");
        arr.add("01110");
        arr.add("10110");
        arr.add("11110");
        Collections.sort(arr);
        System.out.println(arr);
    }


    @Test
    public void test02() {
        BinaryPatriciaTrie trie = new BinaryPatriciaTrie();

        trie.insert("00110");
        trie.insert("0010");
        trie.insert("000");
        trie.insert("001");
        trie.insert("011");
        trie.insert("1001");
        trie.insert("1");
        trie.insert("010101101011");
        trie.insert("10000110101");
        trie.insert("11111111");

       Iterator<String> it = trie.inorderTraversal();
       while(it.hasNext())
           System.out.println(it.next());
    }


}