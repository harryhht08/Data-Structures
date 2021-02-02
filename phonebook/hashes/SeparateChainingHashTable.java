package projects.phonebook.hashes;

import projects.UnimplementedMethodException;
import projects.phonebook.utils.KVPair;
import projects.phonebook.utils.KVPairList;
import projects.phonebook.utils.PrimeGenerator;
import projects.phonebook.utils.Probes;

import java.util.ArrayList;
import java.util.Iterator;

/**<p>{@link SeparateChainingHashTable} is a {@link HashTable} that implements <b>Separate Chaining</b>
 * as its collision resolution strategy, i.e the collision chains are implemented as actual
 * Linked Lists. These Linked Lists are <b>not assumed ordered</b>. It is the easiest and most &quot; natural &quot; way to
 * implement a hash table and is useful for estimating hash function quality. In practice, it would
 * <b>not</b> be the best way to implement a hash table, because of the wasted space for the heads of the lists.
 * Open Addressing methods, like those implemented in {@link LinearProbingHashTable} and {@link QuadraticProbingHashTable}
 * are more desirable in practice, since they use the original space of the table for the collision chains themselves.</p>
 *
 * @author YOUR NAME HERE!
 * @see HashTable
 * @see SeparateChainingHashTable
 * @see LinearProbingHashTable
 * @see OrderedLinearProbingHashTable
 * @see CollisionResolver
 */
public class SeparateChainingHashTable implements HashTable{

    /* ****************************************************************** */
    /* ***** PRIVATE FIELDS / METHODS PROVIDED TO YOU: DO NOT EDIT! ***** */
    /* ****************************************************************** */

    private KVPairList[] table;
    private int count;
    private PrimeGenerator primeGenerator;

    // We mask the top bit of the default hashCode() to filter away negative values.
    // Have to copy over the implementation from OpenAddressingHashTable; no biggie.
    private int hash(String key){
        return (key.hashCode() & 0x7fffffff) % table.length;
    }

    /* **************************************** */
    /*  IMPLEMENT THE FOLLOWING PUBLIC METHODS:  */
    /* **************************************** */
    /**
     *  Default constructor. Initializes the internal storage with a size equal to the default of {@link PrimeGenerator}.
     */
    public SeparateChainingHashTable(){
        primeGenerator = new PrimeGenerator();
        table = new KVPairList[primeGenerator.getCurrPrime()];
        for (int i = 0; i < table.length; i++)
            table[i] = new KVPairList();
        count = 0;
    }

    @Override
    public Probes put(String key, String value) {
        Probes p = new Probes(value, 1);
        count++;
        int addr = hash(key);
        table[addr].addBack(key, value);
        return p;
    }

    @Override
    public Probes get(String key) {
        int addr = hash(key);
        return table[addr].getValue(key);
    }

    @Override
    public Probes remove(String key) {
        int addr = hash(key);
        Probes p = table[addr].removeByKey(key);
        if (p.getValue() == null)
            return p;
        count--;
        return p;
    }

    @Override
    public boolean containsKey(String key) {
        int addr = hash(key);
        return table[addr].containsKey(key);
    }

    @Override
    public boolean containsValue(String value) {
        int i = 0;
        while (i < table.length) {
            if (table[i].containsValue(value))
                return true;
            i++;
        }
        return false;
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public int capacity() {
        return table.length; // Or the value of the current prime.
    }

    /**
     * Enlarges this hash table. At the very minimum, this method should increase the <b>capacity</b> of the hash table and ensure
     * that the new size is prime. The class {@link PrimeGenerator} implements the enlargement heuristic that
     * we have talked about in class and can be used as a black box if you wish.
     * @see PrimeGenerator#getNextPrime()
     */
    public void enlarge() {
        int newSize = primeGenerator.getNextPrime();
        ArrayList<KVPair> arr = new ArrayList<>();
        for (int i = 0; i < table.length; i++) {
            Iterator<KVPair> it = table[i].iterator();
            while (it.hasNext()) {
                KVPair pair = it.next();
                arr.add(pair);
            }
        }
        table = new KVPairList[newSize];
        for (int i = 0; i < table.length; i++)
            table[i] = new KVPairList();
        count = 0;
        if (arr.isEmpty()) return;
        for (KVPair pair : arr)
            put(pair.getKey(), pair.getValue());
    }

    /**
     * Shrinks this hash table. At the very minimum, this method should decrease the size of the hash table and ensure
     * that the new size is prime. The class {@link PrimeGenerator} implements the shrinking heuristic that
     * we have talked about in class and can be used as a black box if you wish.
     *
     * @see PrimeGenerator#getPreviousPrime()
     */
    public void shrink(){
        int newSize = primeGenerator.getPreviousPrime();
        ArrayList<KVPair> arr = new ArrayList<>();
        for (int i = 0; i < table.length; i++) {
            Iterator<KVPair> it = table[i].iterator();
            while (it.hasNext()) {
                KVPair pair = it.next();
                arr.add(pair);
            }
        }
        table = new KVPairList[newSize];
        for (int i = 0; i < table.length; i++)
            table[i] = new KVPairList();
        count = 0;
        if (arr.isEmpty()) return;
        for (KVPair pair : arr)
            put(pair.getKey(), pair.getValue());
    }
}
