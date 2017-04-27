package io.github.xyzxqs.libs.xrv;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;

/**
 * Like the definition of a function, each x has a corresponding y,
 * but a y can have multiple x correspondences
 *
 * @author xyzxqs (xyzxqs@gmail.com)
 */

class FuncMap<X, Y> {
    private static final int DEFAULT_CAPACITY = 10;
    private static final Object[] EMPTY_X_DATA = {};
    private static final int[] EMPTY_MAPPER = {};
    private static final YHolder[] EMPTY_Y_DATA = {};

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

    private int[] mapper;

    private int xLength;

    private Object[] xArray;

    private int yLength;

    private YHolder[] yArray;

    public FuncMap() {
        xArray = EMPTY_X_DATA;
        yArray = EMPTY_Y_DATA;
        mapper = EMPTY_MAPPER;
    }

    public FuncMap(int capacity) {
        xArray = new Object[capacity];
        yArray = new YHolder[capacity];
        mapper = new int[capacity];
    }

    public void connect(X x, Y y) {
        int xi = indexOfX(x);
        int yi = indexOfY(y);
        if (xi >= 0) {
            int oldYi = mapper[xi];
            yArray[oldYi].refCount--;
            rmYIfRefCountIsZero(oldYi);

            if (yi >= 0) {
                mapper[xi] = yi;
                yArray[yi].refCount++;
            } else {
                ensureYCapacity(yLength + 1);
                mapper[xi] = yLength;
                yArray[yLength++] = createY(y);
            }
        } else {
            ensureXCapacity(xLength + 1);
            if (yi >= 0) {
                mapper[xLength] = yi;
                yArray[yi].refCount++;
            } else {
                ensureYCapacity(yLength + 1);
                mapper[xLength] = yLength;
                yArray[yLength++] = createY(y);
            }
            xArray[xLength++] = x;
        }
    }

    private void rmYIfRefCountIsZero(int index) {
        YHolder oldY = yArray[index];
        if (oldY.refCount == 0) {
            oldY.value = null;
            System.arraycopy(yArray, index + 1, yArray, index, yLength - 1 - index);
            yLength--;

            for (int mi = 0; mi < xLength; mi++) {
                if (mapper[mi] > index) {
                    mapper[mi]--;
                }
            }
        }
    }

    private YHolder createY(Object y) {
        YHolder yHolder = new YHolder();
        yHolder.value = y;
        yHolder.refCount = 1;
        return yHolder;
    }

    private void ensureXCapacity(int minCapacity) {
        if (xArray == EMPTY_X_DATA) {
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }

        // overflow-conscious code
        if (minCapacity - xArray.length > 0)
            growX(minCapacity);
    }

    private void ensureYCapacity(int minCapacity) {
        if (yArray == EMPTY_Y_DATA) {
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }

        // overflow-conscious code
        if (minCapacity - yArray.length > 0)
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
        int oldCapacity = xArray.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        xArray = Arrays.copyOf(xArray, newCapacity);
        mapper = Arrays.copyOf(mapper, newCapacity);
    }

    /**
     * Increases the capacity to ensure that it can hold at least the
     * number of elements specified by the minimum capacity argument.
     *
     * @param minCapacity the desired minimum capacity
     */
    private void growY(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = yArray.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        yArray = Arrays.copyOf(yArray, newCapacity);
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
        if (index >= xLength) {
            throw new IndexOutOfBoundsException();
        } else {
            return (X) xArray[index];
        }
    }

    @SuppressWarnings("unchecked")
    public Y getYAt(int index) {
        if (index >= yLength) {
            throw new IndexOutOfBoundsException();
        } else {
            return (Y) yArray[index];
        }
    }

    @SuppressWarnings("unchecked")
    @NonNull
    public X[] getX(Y y) {
        int yi = indexOfY(y);
        int rc = yArray[yi].refCount;
        Object[] rs = new Object[yi >= 0 ? rc : 0];
        for (int i = 0, ri = 0; i < xLength && ri < rc; i++) {
            if (mapper[i] == yi) {
                rs[ri++] = xArray[i];
            }
        }
        return (X[]) rs;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public Y getY(X x) {
        int xi = indexOfX(x);
        if (xi >= 0) {
            return (Y) yArray[mapper[xi]].value;
        }
        return null;
    }

    public int indexOfY(Y y) {
        for (int i = 0; i < yLength; i++) {
            if (yArray[i].value == y) {
                return i;
            }
        }
        return -1;
    }

    public int indexOfX(X x) {
        for (int i = 0; i < xLength; i++) {
            if (xArray[i] == x) {
                return i;
            }
        }
        return -1;
    }

    public boolean hasX(X x) {
        if (x == null) {
            return false;
        } else {
            for (int i = 0; i < xLength; i++) {
                if (x == xArray[i]) {
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
            for (int i = 0; i < xLength; i++) {
                if (y == yArray[i].value) {
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
            for (; i < xLength; i++) {
                if (x == xArray[i]) {
                    break;
                }
            }

            if (i < xLength) {
                int yi = mapper[i];
                System.arraycopy(xArray, i + 1, xArray, i, xLength - 1 - i);
                System.arraycopy(mapper, i + 1, mapper, i, xLength - 1 - i);
                xLength--;
                yArray[yi].refCount--;
                rmYIfRefCountIsZero(yi);
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
            for (int i = 0; i < xLength; i++) {
                if (mapper[i] == yi) {
                    System.arraycopy(xArray, i + 1, xArray, i, xLength - 1 - i);
                    System.arraycopy(mapper, i + 1, mapper, i, xLength - 1 - i);
                    xLength--;
                }
            }
            yArray[yi].refCount = 0;
            rmYIfRefCountIsZero(yi);
            return true;
        }
    }
}
