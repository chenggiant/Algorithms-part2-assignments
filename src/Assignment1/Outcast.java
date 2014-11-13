public class Outcast {
    private final WordNet wordNet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordNet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int i = 0;
        int[] dist = new int[nouns.length];
        int max = -1;
        int maxIndex = 0;

        for (String nounA : nouns) {
            for (String nounB : nouns) {
                dist[i] += wordNet.distance(nounA, nounB);
            }
            if (dist[i] >= max) {
                maxIndex = i;
                max = dist[i];
            }
            i++;
        }
        return nouns[maxIndex];
    }

    // see test client below
    public static void main(String[] args) {
        WordNet wordnet = new WordNet("synsets.txt", "hypernyms.txt");
        Outcast outcast = new Outcast(wordnet);

        String[] filenames = {"outcast5.txt", "outcast8.txt", "outcast11.txt"};
        for (int t = 0; t < filenames.length; t++) {
            In in = new In(filenames[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(filenames[t] + ": " + outcast.outcast(nouns));
        }
    }
}