public class SAP {
    private final Digraph g;

    private boolean[] markedA;
    private int[] distToA;
    private boolean[] markedB;
    private int[] distToB;
    private int dist;
    private int ancestor;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new NullPointerException();

        g = new Digraph(G);

        markedA = new boolean[g.V()];
        distToA = new int[g.V()];

        markedB = new boolean[g.V()];
        distToB = new int[g.V()];

        for (int v = 0; v < g.V(); v++) {
            distToA[v] = Integer.MAX_VALUE;
            distToB[v] = Integer.MAX_VALUE;
        }

    }

    private boolean inRange(int i) {
        return 0 <= i && i < g.V();
    }


    private void bfs(Digraph G, int s, int t) {
        Queue<Integer> p = new Queue<Integer>();
        Queue<Integer> q = new Queue<Integer>();

        markedA[s] = true;
        distToA[s] = 0;

        markedB[t] = true;
        distToB[t] = 0;

        dist = Integer.MAX_VALUE;
        ancestor = -1;

        p.enqueue(s);
        q.enqueue(t);

        while (!p.isEmpty()) {
            int w = p.dequeue();
            for (int x : G.adj(w)) {
                if (!markedA[x]) {
                    distToA[x] = distToA[w] + 1;
                    markedA[x] = true;
                    p.enqueue(x);
                }
            }
        }

        while (!q.isEmpty()) {
            int v = q.dequeue();
            for (int y : G.adj(v)) {
                if (!markedB[y]) {
                    distToB[y] = distToB[v] + 1;
                    markedB[y] = true;
                    q.enqueue(y);
                }
            }
        }

        for (int i = 0; i < g.V(); i++) {
            if (markedA[i] && markedB[i]) {
                if (distToA[i] + distToB[i] < dist) {
                    dist = distToA[i] + distToB[i];
                    ancestor = i;
                }
            }
            markedA[i] = false;
            distToA[i] = Integer.MAX_VALUE;
            markedB[i] = false;
            distToB[i] = Integer.MAX_VALUE;
        }
    }


    private void bfs(Digraph G, Iterable<Integer> s, Iterable<Integer> t) {
        Queue<Integer> p = new Queue<Integer>();
        Queue<Integer> q = new Queue<Integer>();

        dist = Integer.MAX_VALUE;
        ancestor = -1;

        for (int a : s) {
            markedA[a] = true;
            distToA[a] = 0;
            p.enqueue(a);
        }

        for (int b : t) {
            markedB[b] = true;
            distToB[b] = 0;
            q.enqueue(b);
        }

        while (!p.isEmpty()) {
            int w = p.dequeue();
            for (int x : G.adj(w)) {
                if (!markedA[x]) {
                    distToA[x] = distToA[w] + 1;
                    markedA[x] = true;
                    p.enqueue(x);
                }
            }
        }

        while (!q.isEmpty()) {
            int v = q.dequeue();
            for (int y : G.adj(v)) {
                if (!markedB[y]) {
                    distToB[y] = distToB[v] + 1;
                    markedB[y] = true;
                    q.enqueue(y);
                }
            }
        }

        for (int i = 0; i < g.V(); i++) {
            if (markedA[i] && markedB[i]) {
                if (distToA[i] + distToB[i] < dist) {
                    dist = distToA[i] + distToB[i];
                    ancestor = i;
                }
            } else if (markedA[i]) {
                markedA[i] = false;
                distToA[i] = Integer.MAX_VALUE;
            } else if (markedB[i]) {
                markedB[i] = false;
                distToB[i] = Integer.MAX_VALUE;
            }
        }
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (!inRange(v) || !inRange(w)) throw new IndexOutOfBoundsException();

        bfs(g, v, w);
        if (dist == Integer.MAX_VALUE) return -1;
        return dist;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (!inRange(v) || !inRange(w)) throw new IndexOutOfBoundsException();

        bfs(g, v, w);
        return ancestor;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new NullPointerException();

        bfs(g, v, w);
        if (dist == Integer.MAX_VALUE) return -1;
        return dist;
    }

    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new NullPointerException();

        bfs(g, v, w);
        return ancestor;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In("Digraph2.txt");
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);

        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
