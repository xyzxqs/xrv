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

import android.support.v7.widget.RecyclerView;

/**
 * @author xyzxqs (xyzxqs@gmail.com)
 */

public interface XrvProviderAssigner<T> {
    /**
     * get XrvProvider to handle special item
     * <p>
     * NOTE: for performance, do not create new assignProvider instance in this method
     *
     * @param item data item
     * @return XrvProvider to handle this item
     */
    XrvProvider<? super T, ? extends RecyclerView.ViewHolder> assignProvider(T item);
}
