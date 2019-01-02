package io.github.xyzxqs.app.xrvdemo.providers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import io.github.xyzxqs.app.xrvdemo.R;
import io.github.xyzxqs.libs.xrv.XrvProvider;

/**
 * @author xyzxqs (xyzxqs@gmail.com)
 */

public class StringProvider extends XrvProvider<String, StringProvider.TextViewViewHolder> {

    @Override
    public TextViewViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new TextViewViewHolder(inflater.inflate(R.layout.item_text_view, parent, false));
    }

    @Override
    public void onBindViewHolder(TextViewViewHolder holder, String itemData) {
        holder.textView.setText(itemData);
    }

    static class TextViewViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public TextViewViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
        }
    }
}
