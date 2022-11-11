package vmart.example.mypc.vedasmart.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import vmart.example.mypc.vedasmart.R;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ViewHolder>{
    Context context;

    public AdminProductAdapter(Context context) {
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_products_list, parent, false);
        return new AdminProductAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.productIamge.setBackgroundResource(R.drawable.icon_product);
        holder.productname.setText("Indian Gate Basmati Rice");
        holder.productmrp.setText("Mrp"+" "+context.getResources().getString(R.string.rupee)+" "+"490"+".00");
        holder.productvmart.setText("Vmart Mrp"+" "+context.getResources().getString(R.string.rupee)+" "+"350"+".00");
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
       TextView productname,productmrp,productvmart;
       ImageView productIamge,rightIcon;
        public ViewHolder(View itemView)
        {
            super(itemView);
            productname = itemView.findViewById(R.id.seller_product_name);
            productmrp = itemView.findViewById(R.id.seller_product_marp);
            productvmart = itemView.findViewById(R.id.seller_product_vmart);
            productIamge = itemView.findViewById(R.id.seller_product_img);
            rightIcon = itemView.findViewById(R.id.right_img);
        }
    }
}