package com.pnf.penequillaunch.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class OrderActivity extends AppCompatActivity {
  /*  ItemOrderAdapter adapter;
    ArrayList<ItemOrderAdapter.Orders> arrayList;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* setContentView(R.layout.activity_order);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        arrayList = new ArrayList<>();
        arrayList.add(new ItemOrderAdapter.Orders(500, 0,500));
        arrayList.add(new ItemOrderAdapter.Orders(1000, 1,900));
        arrayList.add(new ItemOrderAdapter.Orders(2000, 3,1750));
        arrayList.add(new ItemOrderAdapter.Orders(5000, 10,4000));
        arrayList.add(new ItemOrderAdapter.Orders(10000, 30,7000));
        adapter = new ItemOrderAdapter(arrayList, this);
        adapter.notifyDataSetChanged();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setRecycleChildrenOnDetach(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);*/

    }
}
