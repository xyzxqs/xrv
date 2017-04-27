package io.github.xyzxqs.libs.xrv;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;

/**
 * @author xyzxqs (xyzxqs@gmail.com)
 */

class FuncMap<X, Y> {
    private static final int DEFAULT_CAPACITY = 10;
    private static final Object[] EMPTY_XDATA = {};
    private static final int[] EMPTY_MAPER = {};
    private static final YHolder[] EMPTY_YDATA = {};

    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private static class YHolder {
        private Object value;
        private int refCount;
    }

    private int[] maper;
    private int xesLength;
    private Object[] xes;

    private YHolder[] yes;
    private int yesLength;

    public FuncMap() {
        xes = EMPTY_XDATA;
        yes = EMPTY_YDATA;
        maper = EMPTY_MAPER;
    }

    public FuncMap(int capacity) {
        xes = new Object[capacity];
        yes = new YHolder[capacity];
        maper = new int[capacity];
    }

    public void put(X x, Y y) {
        int xi = indexOfX(x);
        int yi = indexOfY(y);
        if (xi >= 0) {
            if (yi >= 0) {
                maper[xi] = yi;
            } else {
                ensureYCapacity(yesLength + 1);
                maper[xi] = yesLength;
                yes[yesLength++] = createY(y);
            }
        } else {
            ensureXCapacity(xesLength + 1);
            if (yi >= 0) {
                maper[xesLength] = yi;
            } else {
                ensureYCapacity(yesLength + 1);
                maper[xesLength] = yesLength;
                yes[yesLength++] = createY(y);
            }
            xes[xesLength++] = x;
        }
    }

    private YHolder createY(Object y) {
        YHolder yHolder = new YHolder();
        yHolder.value = y;
        yHolder.refCount = 1;
        return yHolder;
    }

    private void ensureXCapacity(int minCapacity) {
        if (xes == EMPTY_XDATA) {
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }

        // overflow-conscious code
        if (minCapacity - xes.length > 0)
            growX(minCapacity);
    }

    private void ensureYCapacity(int minCapacity) {
        if (yes == EMPTY_YDATA) {
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }

        // overflow-conscious code
        if (minCapacity - yes.length > 0)
            growY(minCapacity);
    }

    /**
     * Increases the capacity to ensure that it can hold at least the
     * number of elements specified by the minimum capacity argument.
     *
     * @param minCapacity the desired minimum capacity
     */
    private void growX(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = xes.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        xes = Arrays.copyOf(xes, newCapacity);
        maper = Arrays.copyOf(maper, newCapacity);
    }

    /**
     * Increases the capacity to ensure that it can hold at least the
     * number of elements specified by the minimum capacity argument.
     *
     * @param minCapacity the desired minimum capacity
     */
    private void growY(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = yes.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        yes = Arrays.copyOf(yes, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }

    @SuppressWarnings("unchecked")
    public X getXAt(int index) {
        if (index >= xesLength) {
            throw new IndexOutOfBoundsException();
        } else {
            return (X) xes[index];
        }
    }

    @SuppressWarnings("unchecked")
    public Y getYAt(int index) {
        if (index >= yesLength) {
            throw new IndexOutOfBoundsException();
        } else {
            return (Y) yes[index];
        }
    }

    @SuppressWarnings("unchecked")
    @NonNull
    public X[] getX(Y y) {
        int yi = indexOfY(y);
        int rc = yes[yi].refCount;
        X[] rs = (X[]) new Object[yi >= 0 ? (rc - 1) : 0];
        for (int i = 0, ri = 0; i < xesLength && ri < rc; i++) {
            if (maper[i] == yi) {
                rs[ri++] = (X) xes[i];
            }
        }
        return rs;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public Y getY(X x) {
        int xi = indexOfX(x);
        if (xi >= 0) {
            return (Y) yes[maper[xi]].value;
        }
        return null;
    }

    public int indexOfY(Y y) {
        for (int i = 0; i < yesLength; i++) {
            if (yes[i].value == y) {
                return i;
            }
        }
        return -1;
    }

    public int indexOfX(X x) {
        for (int i = 0; i < xesLength; i++) {
            if (xes[i] == x) {
                return i;
            }
        }
        return -1;
    }

    public boolean hasX(X x) {
        if (x == null) {
            return false;
        } else {
            for (int i = 0; i < xesLength; i++) {
                if (x == xes[i]) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean hasY(Y y) {
        if (y == null) {
            return false;
        } else {
            for (int i = 0; i < xesLength; i++) {
                if (y == yes[i].value) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean rmX(X x) {
        if (x == null) {
            return false;
        } else {
            int i = 0;
            for (; i < xesLength; i++) {
                if (x == xes[i]) {
                    break;
                }
            }

            if (i < xesLength) {
                int yi = maper[i];
                System.arraycopy(xes, i + 1, xes, i, xesLength - 1 - i);
                System.arraycopy(maper, i + 1, maper, i, xesLength - 1 - i);
                xesLength--;
                YHolder yHolder = yes[yi];
                yHolder.refCount--;

                if (yHolder.refCount == 0) {
                    yHolder.value = null;

                    System.arraycopy(yes, yi + 1, yes, yi, yesLength - 1 - yi);
                    yesLength--;

                    for (int mi = 0; mi < xesLength; mi++) {
                        if (maper[mi] > yi) {
                            maper[mi]--;
                        }
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean rmY(Y y) {
        int yi = indexOfY(y);
        if (yi == -1) {
            return false;
        } else {
            YHolder yHolder = yes[yi];
            yHolder.refCount = 0;
            yHolder.value = null;
            System.arraycopy(yes, yi + 1, yes, yi, yesLength - 1 - yi);
            yesLength--;

            for (int i = 0; i < xesLength; i++) {
                if (maper[i] == yi) {
                    System.arraycopy(xes, i + 1, xes, i, xesLength - 1 - i);
                    System.arraycopy(maper, i + 1, maper, i, xesLength - 1 - i);
                    xesLength--;
                }
            }
            for (int mi = 0; mi < xesLength; mi++) {
                if (maper[mi] > yi) {
                    maper[mi]--;
                }
            }
            return true;
        }
    }
}
