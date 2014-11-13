import java.util.HashSet;

public class WordNet {
    private int V;
    private SET<String>[] adjNouns;
    private String[] synsetArray;
    private HashSet<String> words;
    private final Digraph g;
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) throw new NullPointerException();

        words = new HashSet<String>();
        In inSynsets = new In(synsets);

        V = 0;
        String line;
        while ((line = inSynsets.readLine()) != null && !line.trim().isEmpty()) V++;

        synsetArray =  new String[V];
        adjNouns = (SET<String>[]) new SET[V];

        inSynsets = new In(synsets);
        for (int v = 0; v < V; v++) {
            String synset = inSynsets.readLine().split(",")[1];
            synsetArray[v] = synset;
            String[] syns = synset.split(" ");
            adjNouns[v] = new SET<String>();
            for (String syn : syns) adjNouns[v].add(syn);
        }

        for (int v = 0; v < V; v++) {
            for (String noun : adjNouns[v]) words.add(noun);
        }

        g = new Digraph(V);

        In inHypernyms = new In(hypernyms);
        while ((line = inHypernyms.readLine()) != null && !line.trim().isEmpty()) {
            String[] numbers = line.split(",");
            int v = Integer.parseInt(numbers[0]);
            for (String number : numbers) {
                int w = Integer.parseInt(number);
                if (v != w) g.addEdge(v, w);
            }
        }

        // The constructor should throw a java.lang.IllegalArgumentException if the input does not
        // correspond to a rooted DAG.
        int numRoot = 0;
        for (int v = 0; v < V; v++) {
            if (!g.adj(v).iterator().hasNext()) numRoot++;
        }
        if (numRoot != 1) throw new IllegalArgumentException();

        sap = new SAP(g);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return words;
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new NullPointerException();
        return words.contains(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException();

        Stack<Integer> a = new Stack<Integer>();
        Stack<Integer> b = new Stack<Integer>();

        for (int v = 0; v < V; v++) {
            if (adjNouns[v].contains(nounA)) a.push(v);
            if (adjNouns[v].contains(nounB)) b.push(v);
        }
        return sap.length(a, b);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException();

        Stack<Integer> a = new Stack<Integer>();
        Stack<Integer> b = new Stack<Integer>();

        for (int v = 0; v < V; v++) {
            if (adjNouns[v].contains(nounA)) a.push(v);
            if (adjNouns[v].contains(nounB)) b.push(v);
        }
        return synsetArray[sap.ancestor(a, b)];
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wordnet = new WordNet("synsets.txt", "hypernyms.txt");
        StdOut.println(wordnet.sap("table", "bed"));
    }
}
