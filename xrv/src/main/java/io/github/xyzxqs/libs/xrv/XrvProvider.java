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

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * One XrvProvider is one view type in {@link RecyclerView.Adapter}
 *
 * @param <I> the type of item model data used in this type view
 * @param <V> the subclass type of {@link RecyclerView.ViewHolder}
 * @author xyzxqs (xyzxqs@gmail.com)
 * @see XrvAdapter
 */

public abstract class XrvProvider<I, V extends RecyclerView.ViewHolder> {

    /* internal */ XrvAdapter adapter;

    public abstract V onCreateViewHolder(LayoutInflater inflater, ViewGroup parent);

    public abstract void onBindViewHolder(V holder, I itemData);

    public void onBindViewHolder(V holder, I itemData, List<Object> payloads) {
        onBindViewHolder(holder, itemData);
    }

    /**
     * Get the {@link XrvAdapter}
     *
     * @return the {@link XrvAdapter} this assignProvider associated with.
     */
    public final XrvAdapter getAdapter() {
        return adapter;
    }
}