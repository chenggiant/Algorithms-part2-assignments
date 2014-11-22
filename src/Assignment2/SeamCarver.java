import java.awt.Color;

public class SeamCarver {
    private final Picture pic;
    private double[][] weight;
    private Color[][] picColor;
    private int width;
    private int height;
    private int num;
    private double[] distTo;
    private int[] edgeTo;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        pic = picture;
        width = pic.width();
        height = pic.height();

        num = width * height;

        weight = new double[height][width];
        picColor = new Color[height][width];

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                weight[h][w] = energy(w, h);
                picColor[h][w] = pic.get(w, h);
            }
        }
    }

    // current picture
    public Picture picture() {
        Picture picture = new Picture(width, height);

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                picture.set(w, h, picColor[h][w]);
            }
        }
        return picture;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    private double square(double a) {
        return a*a;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= this.width || y < 0 || y >= this.height) throw new IndexOutOfBoundsException();

        if (x == 0 || x == this.width - 1 || y == 0 || y == this.height - 1) return 195075.0;

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
            for (int i = 0; i < width; i++) {
                s.push(i);
            }
            return s;
        }

        if (x == num) return s;

        // last row
        if (x >= num - width) {
            s.push(num);
            return s;
        }

        // left edge
        if (x % width == 0) {
            s.push(x + width);
            s.push(x + width + 1);
            return s;
        }

        // right edge
        if (x % width == width - 1) {
            s.push(x + width - 1);
            s.push(x + width);
            return s;
        }

        s.push(x + width - 1);
        s.push(x + width);
        s.push(x + width + 1);

        return s;
    }

    private void relax(int v, int e) {
        int x = e % width;
        int y = e / width;

        double w;

        if (y >= height) w = 0;
        else w = weight[y][x];

        if (distTo[e] > distTo[v] + w) {
            distTo[e] = distTo[v] + w;
            edgeTo[e] = v;
        }
    }

    // transpose everything
    private void transpose() {
        double[][] temp = new double[width][height];
        Color[][] tempColor = new Color[width][height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                temp[j][i] = weight[i][j];
                tempColor[j][i] = picColor[i][j];
            }
        }
        weight = temp;
        picColor = tempColor;

        int tmp = height;
        height = width;
        width = tmp;

    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {

        num = width * height;

        Digraph G = new Digraph(num + 2);

        for (int i = 0; i < num + 2; i++) {
            for (int j : adj(i)) G.addEdge(i, j);
        }

        Topological topological = new Topological(G);

        distTo = new double[num + 2];
        edgeTo = new int[num + 2];

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                distTo[h * width + w] = Double.POSITIVE_INFINITY;
            }
        }

        distTo[num + 1] = 0.0;
        distTo[num] = Double.POSITIVE_INFINITY;

        for (int v : topological.order()) {
            for (int e : G.adj(v)) {
                relax(v, e);
            }
        }

        int[] path = new int[height];

        int j = num;
        Stack<Integer> s = new Stack<Integer>();
        while (edgeTo[j] != num + 1) {
            j = edgeTo[j];
            s.push(j % width);
        }

        for (int i = 0; i < height; i++) {
            path[i] = s.pop();
        }

        return path;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        transpose();
        int[] path = findVerticalSeam();
        transpose();
        return path;
    }

    public void removeVerticalSeam(int[] seam) {
        if (seam == null) throw new NullPointerException();
        if (seam.length != height) throw new IllegalArgumentException();

        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= width) throw new IllegalArgumentException();
            if (i < seam.length - 1) {
                if (Math.abs(seam[i + 1] - seam[i]) > 1) throw new IllegalArgumentException();
            }

            System.arraycopy(picColor[i], seam[i] + 1, picColor[i], seam[i], width - seam[i] - 1);
            System.arraycopy(weight[i], seam[i] + 1, weight[i], seam[i], width - seam[i] - 1);
        }
        width--;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) throw new NullPointerException();
        if (seam.length != width) throw new IllegalArgumentException();

        transpose();
        removeVerticalSeam(seam);
        transpose();

    }
}