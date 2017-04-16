package io.github.xyzxqs.libs.xrv;

import android.support.v7.util.SortedList;

/**
 * @author xyzxqs (xyzxqs@gmail.com)
 */

public class XrvSortedListAdapter extends XrvAdapter {
    private SortedList<?> sortedList;

    public XrvSortedListAdapter() {
        this(null);
    }

    public XrvSortedListAdapter(SortedList<?> sortedList) {
        super();
        this.sortedList = sortedList;
    }

    @Override
    public int getItemCount() {
        return sortedList == null ? 0 : sortedList.size();
    }

    @Override
    public Object getItem(int position) {
        return sortedList.get(position);
    }

    public void setSortedList(SortedList<?> sortedList) {
        this.sortedList = sortedList;
    }
}
