public class SeamCarver {
    private Picture pic;
    private double[][] weight;
    private int num;
    private double[] distTo;
    private int[] edgeTo;
    private Digraph G;
    private Topological topological;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        pic = picture;

        num = width() * height();

        distTo = new double[num + 2];
        edgeTo = new int[num + 2];

        G = new Digraph(num + 2);

        for (int i = 0; i < num + 2; i++) {
            for (int j : adj(i)) G.addEdge(i, j);
        }

        topological = new Topological(G);

        weight = new double[width()][height()];

        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                weight[x][y] = energy(x, y);
                distTo[y * width() + x] = Double.POSITIVE_INFINITY;
            }
        }
        distTo[num + 1] = 0.0;
        distTo[num] = Double.POSITIVE_INFINITY;
    }

    // current picture
    public Picture picture() {
        return pic;
    }

    // width() of current picture
    public int width() {
        return pic.width();
    }

    // height() of current picture
    public int height() {
        return pic.height();
    }

    private double square(double a) {
        return a*a;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= this.width() || y < 0 || y >= this.height()) throw new IndexOutOfBoundsException();

        if (x == 0 || x == this.width() - 1 || y == 0 || y == this.height() - 1) return 195075.0;

        return square(pic.get(x + 1, y).getRed() - pic.get(x - 1, y).getRed())
                + square(pic.get(x + 1, y).getGreen() - pic.get(x - 1, y).getGreen())
                + square(pic.get(x + 1, y).getBlue() - pic.get(x - 1, y).getBlue())
                + square(pic.get(x, y + 1).getRed() - pic.get(x, y - 1).getRed())
                + square(pic.get(x, y + 1).getGreen() - pic.get(x, y - 1).getGreen())
                + square(pic.get(x, y + 1).getBlue() - pic.get(x, y - 1).getBlue());
    }

    private Stack<Integer> adj(int x) {
        Stack<Integer> s = new Stack<Integer>();

        if (x == num + 1) {
            for (int i = 0; i < width(); i++) s.push(i);
            return s;
        }

        if (x == num) return s;

        // last row
        if (x >= num - width()) {
            s.push(num);
            return s;
        }

        // left edge
        if (x % width() == 0) {
            s.push(x + width());
            s.push(x + width() + 1);
            return s;
        }

        // right edge
        if (x % width() == width() - 1) {
            s.push(x + width() - 1);
            s.push(x + width());
            return s;
        }

        s.push(x + width() - 1);
        s.push(x + width());
        s.push(x + width() + 1);

        return s;
    }

    private void relax(int v, int e) {
        int x = e % width();
        int y = e / width();

        double w;

        if (y >= height()) w = 0;
        else w = weight[x][y];

        if (distTo[e] > distTo[v] + w) {
            distTo[e] = distTo[v] + w;
            edgeTo[e] = v;
        }
    }

//    private double[][] transpose(double[][] a) {
//        double[][] temp = new double[a[0].length][a.length];
//        for (int i = 0; i < a.length; i++)
//            for (int j = 0; j < a[0].length; j++)
//                temp[j][i] = a[i][j];
//        return temp;
//    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        for (int v : topological.order()) {
            StdOut.println(v);
            for (int e : G.adj(v)) {
                StdOut.print(e + " ");
                relax(v, e);
            }
            StdOut.println();
        }

        int[] path = new int[height()];

        for (int i = 0; i < num + 2; i++) {
            StdOut.println(i + " " + edgeTo[i]);
        }


        int j = num;
        Stack<Integer> s = new Stack<Integer>();
        while (edgeTo[j] != num + 1) {
            j = edgeTo[j];
            s.push(j % width());
        }

        for (int i = 0; i < height(); i++) {
            path[i] = s.pop();
        }

        return path;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() { throw new UnsupportedOperationException();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) throw new NullPointerException();
        throw new UnsupportedOperationException();
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) throw new NullPointerException();
        throw new UnsupportedOperationException();
    }
}