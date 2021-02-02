package projects.phonebook.hashes;

import projects.UnimplementedMethodException;
import projects.phonebook.utils.KVPair;
import projects.phonebook.utils.PrimeGenerator;
import projects.phonebook.utils.Probes;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * <p>{@link LinearProbingHashTable} is an Openly Addressed {@link HashTable} implemented with <b>Linear Probing</b> as its
 * collision resolution strategy: every key collision is resolved by moving one address over. It is
 * the most famous collision resolution strategy, praised for its simplicity, theoretical properties
 * and cache locality. It <b>does</b>, however, suffer from the &quot; clustering &quot; problem:
 * collision resolutions tend to cluster collision chains locally, making it hard for new keys to be
 * inserted without collisions. {@link QuadraticProbingHashTable} is a {@link HashTable} that
 * tries to avoid this problem, albeit sacrificing cache locality.</p>
 *
 * @author Haitian Hao
 *
 * @see HashTable
 * @see SeparateChainingHashTable
 * @see OrderedLinearProbingHashTable
 * @see QuadraticProbingHashTable
 * @see CollisionResolver
 */
public class LinearProbingHashTable extends OpenAddressingHashTable {

    /* ********************************************************************/
    /* ** INSERT ANY PRIVATE METHODS OR FIELDS YOU WANT TO USE HERE: ******/
    /* ********************************************************************/

    private int numOfTombstones;

    private void resize() {
        ArrayList<KVPair> arr = new ArrayList<>();
        for (KVPair pair : table) {
            if (pair != null && pair != TOMBSTONE)
                arr.add(pair);
        }
        table = new KVPair[primeGenerator.getNextPrime()];
        count = 0;
        numOfTombstones = 0;
        if (arr.isEmpty()) return;
        for (KVPair pair : arr)
            put(pair.getKey(), pair.getValue());
        count = arr.size();
    }

    private Probes softDelete(String key) {
        int addr = hash(key);
        int curr = addr;
        int probeCount = 1;
        while (table[curr] != null) {
            if (table[curr] != TOMBSTONE && table[curr].getKey().equals(key)) {
                String val = table[curr].getValue();
                table[curr] = TOMBSTONE;
                numOfTombstones++;
                return new Probes(val, probeCount);
            }
            probeCount++;
            curr++;
            if (curr >= table.length) curr = 0;
        }
        return new Probes(null, probeCount);
    }

    private Probes hardDelete(String key) {
        int addr = hash(key);
        int curr = addr;
        int probeCount = 1;
        int start, end;
        while (table[curr] != null) {
            if (table[curr].getKey().equals(key)) {
                String val = table[curr].getValue();
                Probes p = new Probes(val, probeCount);
                table[curr] = null;
                start = curr;
                end = start + 1;
                if (end >= table.length) end = 0;
                ArrayList<KVPair> arr = new ArrayList<>();
                while (table[end] != null) {
                    arr.add(table[end]);
                    table[end] = null;
                    end++;
                    if (end >= table.length) end = 0;
                }

                count = 0;
                numOfTombstones = 0;
                if (arr.isEmpty()) return p;
                for (KVPair pair : arr)
                    put(pair.getKey(), pair.getValue());
                return p;
            }
            probeCount++;
            curr++;
            if (curr >= table.length) curr = 0;
        }
        return new Probes(null, probeCount);
    }

    /* ******************************************/
    /*  IMPLEMENT THE FOLLOWING PUBLIC METHODS: */
    /* **************************************** */

    /**
     * Constructor with soft deletion option. Initializes the internal storage with a size equal to the starting value of  {@link PrimeGenerator}.
     *
     * @param soft A boolean indicator of whether we want to use soft deletion or not. {@code true} if and only if
     *             we want soft deletion, {@code false} otherwise.
     */
    public LinearProbingHashTable(boolean soft) {
        primeGenerator = new PrimeGenerator();
        table = new KVPair[primeGenerator.getCurrPrime()];
        softFlag = soft;
        count = 0;
        numOfTombstones = 0;
    }

    /**
     * Inserts the pair &lt;key, value&gt; into this. The container should <b>not</b> allow for {@code null}
     * keys and values, and we <b>will</b> test if you are throwing a {@link IllegalArgumentException} from your code
     * if this method is given {@code null} arguments! It is important that we establish that no {@code null} entries
     * can exist in our database because the semantics of {@link #get(String)} and {@link #remove(String)} are that they
     * return {@code null} if, and only if, their key parameter is {@code null}. This method is expected to run in <em>amortized
     * constant time</em>.
     * <p>
     * Instances of {@link LinearProbingHashTable} will follow the writeup's guidelines about how to internally resize
     * the hash table when the capacity exceeds 50&#37;
     *
     * @param key   The record's key.
     * @param value The record's value.
     * @return The {@link projects.phonebook.utils.Probes} with the value added and the number of probes it makes.
     * @throws IllegalArgumentException if either argument is {@code null}.
     */
    @Override
    public Probes put(String key, String value) {
        if (key == null || value == null) throw new IllegalArgumentException();
        if (count >= (table.length + 1) / 2) { resize();}
        KVPair pair = new KVPair(key, value);
        int probeCount = 1;
        int addr = hash(key);
        int curr = addr;
        while (table[curr] != null && table[curr] != TOMBSTONE) {
            probeCount++;
            curr++;
            if (curr >= table.length) curr = 0;
        }
        if (table[curr] == TOMBSTONE) {
            numOfTombstones--;
            count--;
        }
        table[curr] = pair;
        count++;
        return new Probes(value, probeCount);
    }

    @Override
    public Probes get(String key) {
        int addr = hash(key);
        int curr = addr;
        int probeCount = 1;
        while (table[curr] != null) {
            if (table[curr] != TOMBSTONE && table[curr].getKey().equals(key)) {
                return new Probes(table[curr].getValue(), probeCount);
            }
            probeCount++;
            curr++;
            if (curr >= table.length) curr = 0;
        }
        return new Probes(null, probeCount);
    }

    /**
     * <b>Return</b> and <b>remove</b> the value associated with key in the {@link HashTable}. If key does not exist in the database
     * or if key = {@code null}, this method returns {@code null}. This method is expected to run in <em>amortized constant time</em>.
     *
     * @param key The key to search for.
     * @return The {@link projects.phonebook.utils.Probes} with associated value and the number of probe used. If the key is {@code null}, return value {@code null}
     * and 0 as number of probes; if the key doesn't exists in the database, return {@code null} and the number of probes used.
     */
    @Override
    public Probes remove(String key) {
        if (softFlag == true) return softDelete(key);
        return hardDelete(key);
    }

    @Override
    public boolean containsKey(String key) {
        int addr = hash(key);
        int curr = addr;
        while (table[curr] != null) {
            if (table[curr] != TOMBSTONE && table[curr].getKey().equals(key)) return true;
            curr++;
            if (curr >= table.length) curr = 0;
        }
        return false;
    }

    @Override
    public boolean containsValue(String value) {
        for (int i = 0; i < table.length; i++) {
            if (table[i].getValue().equals(value)) return true;
        }
        return false;
    }

    @Override
    public int size() {
        return count - numOfTombstones;
    }

    @Override
    public int capacity() {
        return table.length;
    }
}
