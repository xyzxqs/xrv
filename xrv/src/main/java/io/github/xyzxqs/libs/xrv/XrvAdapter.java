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

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.ArraySet;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * XrvAdapter, subclass of {@link android.support.v7.widget.RecyclerView.Adapter}.
 *
 * @author xyzxqs (xyzxqs@gmail.com)
 * @see android.support.v7.widget.RecyclerView.Adapter
 * @see XrvProvider
 */

public abstract class XrvAdapter extends RecyclerView.Adapter {
    private static final String TAG = XrvAdapter.class.getSimpleName();

    private final Map<Class<?>, XrvProviderAssigner> dataTypeAssignerMap;
    private final Set<Class<? extends XrvProvider>> providerTypeSet;
    private final FuncMap<XrvProvider, XrvProviderAssigner> assignerProviderMap;

    private LayoutInflater layoutInflater;

    public XrvAdapter() {
        dataTypeAssignerMap = new ArrayMap<>();
        providerTypeSet = new ArraySet<>();
        assignerProviderMap = new FuncMap<>(10);
    }

    /**
     * Register a {@link XrvProvider} to this adapter, for one data type to one provider mapping
     * if this adapter already have a provider handle the same type model data, will replace previous one.
     *
     * @param dataType the model data type
     * @param provider the provider
     * @see #register(Class, XrvProviderAssigner) for one data type to many provider mapping
     */
    @CallSuper
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
     * Register a {@link XrvProviderAssigner} to this adapter, for one data type to many provider mapping
     * if this adapter already have a assignProvider handle the same type model data, will replace previous one.
     *
     * @param dataType         the model data type
     * @param providerAssigner provider assigner
     * @see #register(Class, XrvProvider) for one data type to one provider mapping
     */
    @CallSuper
    public <T> void register(@NonNull Class<T> dataType, @NonNull XrvProviderAssigner<T> providerAssigner) {
        if (dataTypeAssignerMap.containsKey(dataType)) {
            XrvProviderAssigner oldAssigner = dataTypeAssignerMap.get(dataType);
            for (XrvProvider p : assignerProviderMap.getX(oldAssigner)) {
                providerTypeSet.remove(p.getClass());
            }
            assignerProviderMap.rmY(oldAssigner);
            Log.w(TAG, "register: ", new Throwable("providerAssigner {a}.class already handle the {b}.class type, replace it"
                    .replace("{a}", dataTypeAssignerMap.get(dataType).getClass().getSimpleName())
                    .replace("{b}", dataType.getSimpleName())));
        }
        dataTypeAssignerMap.put(dataType, providerAssigner);
    }

    @Override
    @CallSuper
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        XrvProvider provider = getProvider(viewType);
        provider.adapter = XrvAdapter.this;
        return provider.onCreateViewHolder(layoutInflater, parent);
    }

    @Override
    @CallSuper
    @SuppressWarnings("unchecked")//guarded by register
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object item = getItem(position);
        int type = getItemViewType(position);
        getProvider(type).onBindViewHolder(holder, item);
    }

    @Override
    @CallSuper
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    public abstract Object getItem(int position);

    @Override
    @CallSuper
    public int getItemViewType(int position) {
        Object obj = getItem(position);
        XrvProviderAssigner assigner = dataTypeAssignerMap.get(obj.getClass());
        if (assigner != null) {
            //guarded by dataTypeAssignerMap
            @SuppressWarnings("unchecked")
            XrvProvider provider = assigner.assignProvider(obj);
            Class<? extends XrvProvider> providerClazz = provider.getClass();
            if (providerTypeSet.contains(providerClazz)) {
                if (!assignerProviderMap.hasX(provider)) {
                    //If a new instance is allowed to add, a potential bug will causes undefined behavior
                    throw new IllegalStateException("do not create new instance in " +
                            "XrvProviderAssigner#assignProvider method.");
                }
            } else {
                providerTypeSet.add(providerClazz);
                assignerProviderMap.connect(provider, assigner);
            }
            return assignerProviderMap.indexOfX(provider);
        } else {
            throw new NotFoundException("no XrvProvider or XrvProviderAssigner found for {a}.class"
                    .replace("{a}", obj.getClass().getSimpleName()));
        }
    }

    protected XrvProvider getProvider(int viewType) {
        return assignerProviderMap.getXAt(viewType);
    }
}
