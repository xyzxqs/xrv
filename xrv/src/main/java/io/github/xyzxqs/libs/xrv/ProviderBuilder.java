package io.github.xyzxqs.libs.xrv;

import android.support.v7.widget.RecyclerView;

/**
 * @author xyzxqs (xyzxqs@gmail.com)
 */

public interface ProviderFacry<T> {
    XrvProvider<? super T, ? extends RecyclerView.ViewHolder> getProvier(T item);
}
