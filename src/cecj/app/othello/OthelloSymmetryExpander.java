package cecj.app.othello;

import games.SymmetryExpander;

public class OthelloSymmetryExpander implements SymmetryExpander {

	static int N = 8;
    static int M = N - 1;
    
	public int[] getSymmetries(int location) {
        int x = location % N;
        int y = location / N;
        
        int[] a = new int[8];
        a[0] = (flat(x, y));
        a[1] = (flat(M - x, y));
        a[2] = (flat(x, M - y));
        a[3] = (flat(M - x, M - y));
        a[4] = (flat(y, x));
        a[5] = (flat(M - y, x));
        a[6] = (flat(y, M - x));
        a[7] = (flat(M - y, M - x));
        
        return a;
	}
	
	public static int flat(int x, int y) {
        return x + N * y;
    }
}
