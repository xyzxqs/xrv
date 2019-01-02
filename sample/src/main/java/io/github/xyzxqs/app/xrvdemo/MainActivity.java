package io.github.xyzxqs.app.xrvdemo;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.github.xyzxqs.app.xrvdemo.providers.ItemType1Provider;
import io.github.xyzxqs.app.xrvdemo.providers.ItemType2Provider;
import io.github.xyzxqs.app.xrvdemo.providers.StringProvider;
import io.github.xyzxqs.libs.xrv.XrvListAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private XrvListAdapter listAdapter;
    private List<Object> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        itemList = new ArrayList<>();
        listAdapter = new XrvListAdapter();

        final ItemType1Provider type1Provider = new ItemType1Provider();
        final ItemType2Provider type2Provider = new ItemType2Provider();

        listAdapter.register(String.class, new StringProvider());
        listAdapter.register(Item.class, item -> {
            //do not create new provider instance in this method
            if (item.type == 1) {
                return type1Provider;
            }
            else {
                return type2Provider;
            }
        });

        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList.add("xyzxqs: hello world");

        int i = 10;
        while (i-- > 0) {
            boolean is1 = i % 2 == 0;
            itemList.add(new Item(is1 ? 1 : 2, (is1 ? "hello" : "world ") + i));
        }

        listAdapter.setList(itemList);
        listAdapter.notifyDataSetChanged();
    }
}
