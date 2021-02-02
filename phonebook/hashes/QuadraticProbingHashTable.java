package projects.phonebook.hashes;

import projects.UnimplementedMethodException;
import projects.phonebook.utils.KVPair;
import projects.phonebook.utils.PrimeGenerator;
import projects.phonebook.utils.Probes;

import java.util.ArrayList;

/**
 * <p>{@link QuadraticProbingHashTable} is an Openly Addressed {@link HashTable} which uses <b>Quadratic
 * Probing</b> as its collision resolution strategy. Quadratic Probing differs from <b>Linear</b> Probing
 * in that collisions are resolved by taking &quot; jumps &quot; on the hash table, the length of which
 * determined by an increasing polynomial factor. For example, during a key insertion which generates
 * several collisions, the first collision will be resolved by moving 1^2 + 1 = 2 positions over from
 * the originally hashed address (like Linear Probing), the second one will be resolved by moving
 * 2^2 + 2= 6 positions over from our hashed address, the third one by moving 3^2 + 3 = 12 positions over, etc.
 * </p>
 *
 * <p>By using this collision resolution technique, {@link QuadraticProbingHashTable} aims to get rid of the
 * &quot;key clustering &quot; problem that {@link LinearProbingHashTable} suffers from. Leaving more
 * space in between memory probes allows other keys to be inserted without many collisions. The tradeoff
 * is that, in doing so, {@link QuadraticProbingHashTable} sacrifices <em>cache locality</em>.</p>
 *
 * @author Haitian Hao
 *
 * @see HashTable
 * @see SeparateChainingHashTable
 * @see OrderedLinearProbingHashTable
 * @see LinearProbingHashTable
 * @see CollisionResolver
 */
public class QuadraticProbingHashTable extends OpenAddressingHashTable {

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
        int newLength = primeGenerator.getNextPrime();
        table = new KVPair[newLength];
        count = 0;
        numOfTombstones = 0;
        if (arr.isEmpty()) return;
        for (KVPair pair : arr)
            put(pair.getKey(), pair.getValue());
    }

    private Probes softDelete(String key) {
        int addr = hash(key);
        int curr = addr;
        int probeCount = 1;
        int step = 1;
        while (table[curr] != null) {
            if (table[curr] != TOMBSTONE && table[curr].getKey().equals(key)) {
                String val = table[curr].getValue();
                table[curr] = TOMBSTONE;
                numOfTombstones++;
                return new Probes(val, probeCount);
            }
            probeCount++;
            curr += step * 2;
            step++;
            if (curr >= table.length) curr = curr % table.length;
        }
        return new Probes(null, probeCount);
    }

    private Probes hardDelete(String key) {
        int addr = hash(key);
        int curr = addr;
        int probeCount = 1;
        int start;
        int step = 1;
        while (table[curr] != null) {
            if (table[curr].getKey().equals(key)) {
                String val = table[curr].getValue();
                Probes p = new Probes(val, probeCount);
                table[curr] = null;
                start = curr;

                ArrayList<KVPair> arr = new ArrayList<>();
                for (int i = 0; i < table.length; i++) {
                    if (i != start && table[i] != null)
                        arr.add(table[i]);
                    table[i] = null;
                }

                count = 0;
                numOfTombstones = 0;
                if (arr.isEmpty()) return p;
                for (KVPair pair : arr)
                    put(pair.getKey(), pair.getValue());
                return p;
            }
            probeCount++;
            curr += step * 2;
            step++;
            if (curr >= table.length) curr = curr % table.length;
        }
        return new Probes(null, probeCount);
    }

    /* ******************************************/
    /*  IMPLEMENT THE FOLLOWING PUBLIC METHODS: */
    /* **************************************** */

    /**
     * Constructor with soft deletion option. Initializes the internal storage with a size equal to the starting value of  {@link PrimeGenerator}.
     * @param soft A boolean indicator of whether we want to use soft deletion or not. {@code true} if and only if
     *               we want soft deletion, {@code false} otherwise.
     */
    public QuadraticProbingHashTable(boolean soft) {
        primeGenerator = new PrimeGenerator();
        table = new KVPair[primeGenerator.getCurrPrime()];
        softFlag = soft;
        count = 0;
        numOfTombstones = 0;
    }

    @Override
    public Probes put(String key, String value) {
        if (key == null || value == null) throw new IllegalArgumentException();
        if (count >= (table.length + 1) / 2) { resize();}
        KVPair pair = new KVPair(key, value);
        int probeCount = 1;
        int addr = hash(key);
        int curr = addr;
        int step = 1;
        while (table[curr] != null && table[curr] != TOMBSTONE) {
            probeCount++;
            curr += step * 2;
            step++;
            if (curr >= table.length) curr = curr % table.length;
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
        int step = 1;
        while (table[curr] != null) {
            if (table[curr] != TOMBSTONE && table[curr].getKey().equals(key)) {
                return new Probes(table[curr].getValue(), probeCount);
            }
            probeCount++;
            curr += step * 2;
            step++;
            if (curr >= table.length) curr = curr % table.length;
        }
        return new Probes(null, probeCount);
    }


    @Override
    public Probes remove(String key) {
        if (softFlag == true) return softDelete(key);
        return hardDelete(key);
    }


    @Override
    public boolean containsKey(String key) {
        int addr = hash(key);
        int curr = addr;
        int step = 1;
        while (table[curr] != null) {
            if (table[curr] != TOMBSTONE && table[curr].getKey().equals(key)) return true;
            curr += step * 2;
            step++;
            if (curr >= table.length) curr = curr % table.length;
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