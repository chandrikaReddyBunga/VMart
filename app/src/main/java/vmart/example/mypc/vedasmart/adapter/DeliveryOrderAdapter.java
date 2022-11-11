package vmart.example.mypc.vedasmart.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.model.CartList;

public class DeliveryOrderAdapter extends RecyclerView.Adapter<DeliveryOrderAdapter.ViewHolder> {
    Context context;
    ArrayList<CartList> cartListArrayList;
    String  totlaMrp,totalvmart,totalSavings;

    public DeliveryOrderAdapter(Context context, ArrayList<CartList> cartListArrayList) {
        this.context = context;
        this.cartListArrayList = cartListArrayList;
    }

    @NonNull
    @Override
    public DeliveryOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.ordersummaryitems, viewGroup, false);
        return new DeliveryOrderAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryOrderAdapter.ViewHolder holder, int position) {
        if(cartListArrayList !=null){
            Glide.with(context).load(cartListArrayList.get(position).getUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.loadingpic1))
                    .into(holder.productImage);
            holder.productName.setText(cartListArrayList.get(position).getItemName());
            holder.productQuantity.setText(cartListArrayList.get(position).getQuantity());
            holder.productNetWeight.setText(cartListArrayList.get(position).getNetWeight());
            totlaMrp = getTotalMrp(cartListArrayList.get(position).getQuantity(),cartListArrayList.get(position).getMrpPrice());
            totalvmart = getTotalVmart(cartListArrayList.get(position).getQuantity(),cartListArrayList.get(position).getVmartPrice());
            totalSavings = getTotalSavings(totlaMrp,totalvmart);
            holder.productYouPay.setText("Vedas Mart" + " " + "Rs."+""+totalvmart+".00");
            holder.youSavings.setText("You Save "+"Rs."+totalSavings+".00");
            holder.textcat_quantity.setText("QUANTITY");
        }
    }

    @Override
    public int getItemCount() {
        if(cartListArrayList!=null) { return cartListArrayList.size(); }
        else{ return 0;}
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productQuantity;
        TextView productNetWeight;
        TextView productYouPay;
        TextView youSavings;TextView textcat_quantity;

        public ViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.item_Img);
            productName = itemView.findViewById(R.id.item_Name);
            productQuantity = itemView.findViewById(R.id.item_quantity);
            productNetWeight = itemView.findViewById(R.id.cat_netweight);
            productYouPay = itemView.findViewById(R.id.mrp);
            youSavings = itemView.findViewById(R.id.youSave);
            textcat_quantity=itemView.findViewById(R.id.textcat_quantity);
        }
    }
    ///getting the total Mrp for adding
    public String getTotalMrp(String count, String mrp) {
        int totalmrp = Integer.valueOf(count) * Integer.valueOf(mrp);
        return String.valueOf(totalmrp);
    }
    ///getting the totalVmart for adding
    public String getTotalVmart(String count, String vmart) {
        int totalVmart = Integer.valueOf(count) * Integer.valueOf(vmart);
        return String.valueOf(totalVmart);
    }
    public String getTotalSavings(String totalMrp, String totalVmart) {
        String totalSavings = String.valueOf(Integer.valueOf(totalMrp) - Integer.valueOf(totalVmart));
        return totalSavings;
    }
}