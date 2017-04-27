package io.github.xyzxqs.libs.xrv;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class FuncMapTest {
    static class X {
        private int i;

        X(int i) {
            this.i = i;
        }

        @Override
        public String toString() {
            return "x: " + i;
        }
    }

    static class Y {
        private int i;

        Y(int i) {
            this.i = i;
        }

        @Override
        public String toString() {
            return "y: " + i;
        }
    }

    private Y y1 = new Y(1);
    private Y y2 = new Y(2);

    private X x1 = new X(1);
    private X x2 = new X(2);
    private X x3 = new X(3);

    @Test
    public void test_getX_getY() throws Exception {
        FuncMap<X, Y> xyFuncMap = new FuncMap<>();

        xyFuncMap.connect(x1, y1);
        xyFuncMap.connect(x2, y1);
        xyFuncMap.connect(x3, y2);

        assertArrayEquals(new X[]{x1, x2}, xyFuncMap.getX(y1));
        assertArrayEquals(new X[]{x3}, xyFuncMap.getX(y2));

        assertEquals(y1, xyFuncMap.getY(x1));
        assertEquals(y1, xyFuncMap.getY(x2));
        assertEquals(y2, xyFuncMap.getY(x3));
    }

    @Test
    public void test_rmX_rmY() throws Exception {
        FuncMap<X, Y> xyFuncMap = new FuncMap<>(10);
        xyFuncMap.connect(x1, y1);
        xyFuncMap.connect(x2, y1);
        xyFuncMap.connect(x3, y2);

        xyFuncMap.rmX(x1);
        assertArrayEquals(new X[]{x2}, xyFuncMap.getX(y1));

        xyFuncMap.connect(x1, y1);
        assertArrayEquals(new X[]{x2, x1}, xyFuncMap.getX(y1));
        xyFuncMap.rmY(y1);

        assertFalse(xyFuncMap.hasX(x1));
        assertFalse(xyFuncMap.hasX(x2));
        assertTrue(xyFuncMap.hasX(x3));

        assertFalse(xyFuncMap.hasY(y1));
        assertTrue(xyFuncMap.hasY(y2));

    }
}