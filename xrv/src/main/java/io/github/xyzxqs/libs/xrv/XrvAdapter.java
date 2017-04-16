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
import android.view.LayoutInflater;
import android.view.ViewGroup;

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

    private final ArrayMap<Class<?>, XrvProviderAssigner> dataTypeAssignerMap;
    private final ArrayMap<Class<? extends XrvProvider>, XrvProvider> providerTypeProviderMap;

    private LayoutInflater layoutInflater;

    public XrvAdapter() {
        this(null);
    }

    public XrvAdapter(List<?> items) {
        dataList = items;
        dataTypeAssignerMap = new ArrayMap<>();
        providerTypeProviderMap = new ArrayMap<>();
    }

    /**
     * Register a {@link XrvProvider} to this adapter, for one data type map to one assignProvider
     * if this adapter already have a provider handle the same type model data, will replace previous one.
     *
     * @param dataType the model data type
     * @param provider the provider
     * @see #register(Class, XrvProviderAssigner) for one data type map to many provider
     */
    public <T> void register(@NonNull Class<T> dataType,
                             @NonNull final XrvProvider<? super T, ? extends RecyclerView.ViewHolder> provider) {
        register(dataType, new XrvProviderAssigner<T>() {
            @Override
            public XrvProvider<? super T, ? extends RecyclerView.ViewHolder> assignProvider(T item) {
                return provider;
            }
        });
    }

    /**
     * Register a {@link XrvProviderAssigner} to this adapter, for one data type map to many assignProvider
     * if this adapter already have a assignProvider handle the same type model data, will replace previous one.
     *
     * @param dataType         the model data type
     * @param providerAssigner provider assigner
     * @see #register(Class, XrvProvider) for one data type map to one provider
     */
    public <T> void register(@NonNull Class<T> dataType, @NonNull XrvProviderAssigner<T> providerAssigner) {
        if (dataTypeAssignerMap.containKey(dataType)) {
            Log.w(TAG, "register: ", new Throwable("providerAssigner {a}.class already handle the {b}.class type, replace it"
                    .replace("{a}", dataTypeAssignerMap.getValue(dataType).getClass().getSimpleName())
                    .replace("{b}", dataType.getSimpleName())));
        }
        dataTypeAssignerMap.put(dataType, providerAssigner);
    }

    /**
     * Update the items, but not update items view. If you want to refresh the items views, you should
     * call {@link RecyclerView.Adapter#notifyDataSetChanged()}, or RecyclerView.Adapter#notifyItem*()
     * by yourself.
     * <p>
     * You can use {@link Items} if you like, so you can add any object type to it.
     * <p>
     * Note: If the items you set contain a object type that there is no provider registered for,
     * it will throw {@link NotFoundException} when update items view, because we know nothing
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
        Object obj = dataList.get(position);
        XrvProviderAssigner assigner = dataTypeAssignerMap.getValue(obj.getClass());
        if (assigner != null) {
            //guarded by dataTypeAssignerMap
            @SuppressWarnings("unchecked")
            XrvProvider provider = assigner.assignProvider(obj);
            Class<? extends XrvProvider> providerClazz = provider.getClass();
            if (!providerTypeProviderMap.containKey(providerClazz)) {
                providerTypeProviderMap.put(providerClazz, provider);
            }
            return providerTypeProviderMap.indexOfKey(providerClazz);
        } else {
            throw new NotFoundException("no XrvProvider or XrvProviderAssigner found for {a}.class"
                    .replace("{a}", obj.getClass().getSimpleName()));
        }
    }

    private XrvProvider getProviderByType(int type) {
        return providerTypeProviderMap.getValueAt(type);
    }
}
