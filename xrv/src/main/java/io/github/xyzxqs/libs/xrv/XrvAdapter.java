/*
 * Copyright 2017 xyzxqs (xyzxqs@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.xyzxqs.libs.xrv;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * XrvAdapter, subclass of {@link android.support.v7.widget.RecyclerView.Adapter}.
 *
 * @author xyzxqs (xyzxqs@gmail.com)
 * @see android.support.v7.widget.RecyclerView.Adapter
 * @see XrvProvider
 */

public final class XrvAdapter extends RecyclerView.Adapter {

    private static final String TAG = XrvAdapter.class.getSimpleName();

    private List<?> dataList;

    //viewType = clazzList.indexOf(clazz);
    private final List<Class<?>> clazzList;

    private final SparseArray<XrvProvider> typeProviderMap;

    private LayoutInflater layoutInflater;

    public XrvAdapter() {
        this(null);
    }

    public XrvAdapter(List<?> items) {
        dataList = items;
        clazzList = new ArrayList<>();
        typeProviderMap = new SparseArray<>();
    }

    /**
     * Register a {@link XrvProvider} to this adapter. if this adapter already
     * have a provider handle the same type model data, will replace previous one.
     *
     * @param dataType the model data type
     * @param provider the provider
     */
    public <T> void register(Class<T> dataType, @NonNull XrvProvider<? extends T, ? extends RecyclerView.ViewHolder> provider) {
        int type = addTypeByClazz(dataType);

        if (containTypeProvider(type)) {
            Log.w(TAG, "register: ",
                    new Throwable("provider {a}.class already handle the {b}.class type, replace it"
                            .replace("{a}", getProviderByType(type).getClass().getSimpleName())
                            .replace("{b}", dataType.getSimpleName())));
        }

        addTypeProviderPair(type, provider);
    }

    /**
     * Update the items, but not update items view. If you want to refresh the items views, you should
     * call {@link RecyclerView.Adapter#notifyDataSetChanged()}, or RecyclerView.Adapter#notifyItem*()
     * by yourself.
     * <p>
     * You can use {@link Items} if you like, so you can add any object type to it.
     * <p>
     * Note: If the items you set contain a object type that there is no provider registered for,
     * it will throw {@link ProviderNotFoundException} when update items view, because we know nothing
     * about what to do for this case.
     *
     * @param items the new items
     * @see Items
     */
    public void setItems(@Nullable List<?> items) {
        dataList = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        XrvProvider provider = getProviderByType(viewType);
        provider.adapter = XrvAdapter.this;
        return provider.onCreateViewHolder(layoutInflater, parent);
    }

    @Override
    @SuppressWarnings("unchecked")//guarded by register
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object item = dataList.get(position);
        int type = getItemViewType(position);
        getProviderByType(type).onBindViewHolder(holder, item);
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //assert dataList != null;
        Object item = dataList.get(position);
        return getTypeByClazz(item.getClass());
    }

    private int getTypeByClazz(Class<?> clazz) {
        int type = clazzList.indexOf(clazz);
        if (type == -1) {
            throw new ProviderNotFoundException("no provider registered for {a}.class"
                    .replace("{a}", clazz.getSimpleName()));
        } else {
            return type;
        }
    }

    private int addTypeByClazz(Class<?> clazz) {
        int type = clazzList.indexOf(clazz);
        if (type == -1) {
            clazzList.add(clazz);
            type = clazzList.indexOf(clazz);
        }
        return type;
    }

    private XrvProvider getProviderByType(int viewType) {
        return typeProviderMap.get(viewType, null);
    }

    private void addTypeProviderPair(int type, XrvProvider provider) {
        //only support one to one mapping
        typeProviderMap.put(type, provider);
    }

    private boolean containTypeProvider(int type) {
        return typeProviderMap.indexOfKey(type) > 0;
    }
}
