package io.github.xyzxqs.app.xrvdemo.providers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.github.xyzxqs.app.xrvdemo.Item;
import io.github.xyzxqs.app.xrvdemo.R;
import io.github.xyzxqs.libs.xrv.XrvProvider;

/**
 * @author xyzxqs (xyzxqs@gmail.com)
 */

public class ItemType2Provider extends XrvProvider<Item, ItemType2Provider.Type2ViewHolder> {

    @Override
    public Type2ViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new Type2ViewHolder(inflater.inflate(R.layout.item_type2, parent, false));
    }

    @Override
    public void onBindViewHolder(Type2ViewHolder holder, Item itemData) {
        holder.textView.setText(itemData.data);
    }

    static class Type2ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public Type2ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.type2_txt);
        }
    }
}
