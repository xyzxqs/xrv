package io.github.xyzxqs.app.xrvdemo.providers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import io.github.xyzxqs.app.xrvdemo.Item;
import io.github.xyzxqs.app.xrvdemo.R;
import io.github.xyzxqs.libs.xrv.XrvProvider;

/**
 * @author xyzxqs (xyzxqs@gmail.com)
 */

public class ItemType1Provider extends XrvProvider<Item, ItemType1Provider.Type1ViewHolder> {

    @Override
    public Type1ViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new Type1ViewHolder(inflater.inflate(R.layout.item_type1, parent, false));
    }

    @Override
    public void onBindViewHolder(Type1ViewHolder holder, Item itemData) {
        holder.textView.setText(itemData.data);
    }

    static class Type1ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public Type1ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.type1_txt);
        }
    }
}
