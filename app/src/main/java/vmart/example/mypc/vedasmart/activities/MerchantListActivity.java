package vmart.example.mypc.vedasmart.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.controllers.MerchantListController;
import vmart.example.mypc.vedasmart.fragments.SideMenuFragment;
import vmart.example.mypc.vedasmart.model.MerchantList;

public class MerchantListActivity extends AppCompatActivity {
    public RecyclerView merchantRecyclerview;
    public MerchantAdapter merchantAdapter;
    public ArrayList<MerchantList> merchant;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchantlist);
        MerchantListController.getInstance().FetchAllMerchant(MerchantListActivity.this);
        MerchantListController.getInstance().fillContext(MerchantListActivity.this);
        pref = getSharedPreferences("vmart", MODE_PRIVATE);
        editor = pref.edit();
        init();
    }

    private void init() {
        merchantRecyclerview = findViewById(R.id.merchant_recyclerview);
        merchant = new ArrayList<>();
        merchantRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        merchant = MerchantListController.getInstance().merchantLists;
        merchantAdapter = new MerchantAdapter(MerchantListActivity.this,merchant);
        merchantRecyclerview.setAdapter(merchantAdapter);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SideMenuFragment.MessageEvent event) {
        String resultData = event.message.trim();
        if (resultData.equals("fetchMerchantlist")) {
            merchantAdapter.notifyDataSetChanged();
        } else if (resultData.equals("error")) {

        }
    }
    public class MerchantAdapter extends RecyclerView.Adapter<MerchantAdapter.ViewHolder> {

        Context context;
        ArrayList<MerchantList> merchantarray;

        public MerchantAdapter(Context context,ArrayList<MerchantList> merchantlist) {
            this.context = context;
            this.merchantarray = merchantlist;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.items_merchant, viewGroup, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
            viewHolder.name.setText(merchantarray.get(position).getName());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("merchantId"," "+merchantarray.get(position).getMerchantId()+merchantarray.get(position).getName());
                    editor.putString("merchantId", merchantarray.get(position).getMerchantId());
                    editor.commit();
                    finish();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            });
        }

        @Override
        public int getItemCount() {
            if (MerchantListController.getInstance().merchantLists.size() > 0) {
                return MerchantListController.getInstance().merchantLists.size();
            } else {
                return 0;
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            public ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.merchantName);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
