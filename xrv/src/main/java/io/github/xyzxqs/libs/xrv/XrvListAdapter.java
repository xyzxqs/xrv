package io.github.xyzxqs.libs.xrv;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * @author xyzxqs (xyzxqs@gmail.com)
 */

public class XrvListAdapter extends XrvAdapter {

    private List<?> dataList;

    public XrvListAdapter() {
        this(null);
    }

    public XrvListAdapter(List<?> list) {
        dataList = list;
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    /**
     * Update the items, but not update items view. If you want to refresh the items views, you should
     * call {@link RecyclerView.Adapter#notifyDataSetChanged()}, or RecyclerView.Adapter#notifyItem*()
     * by yourself.
     * <p>
     * Note: If the items you set contain a object type that there is no provider/providerAssigner registered for,
     * it will throw {@link NotFoundException} when update items view, because we know nothing
     * about what to do for this case.
     *
     * @param items the new items
     */
    public void setList(@Nullable List<?> items) {
        dataList = items;
    }
}
