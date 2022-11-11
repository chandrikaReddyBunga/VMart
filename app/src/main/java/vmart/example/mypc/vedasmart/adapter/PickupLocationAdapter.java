package vmart.example.mypc.vedasmart.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.activities.PickupLocationMapActivity;
import vmart.example.mypc.vedasmart.activities.SelfPickUpActivity;
import vmart.example.mypc.vedasmart.model.AllPickupLists;
import vmart.example.mypc.vedasmart.model.PickupLists;
import java.util.ArrayList;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class PickupLocationAdapter extends RecyclerView.Adapter<PickupLocationAdapter.ViewHolder> {
    Context context;
    ArrayList<AllPickupLists> allPickupListsArray;
    ArrayList<PickupLists> pickupLists = new ArrayList<>();

    public PickupLocationAdapter(Context context, ArrayList<AllPickupLists> allPickupListsArray) {
        this.context = context;
        this.allPickupListsArray = allPickupListsArray;
    }
    @NonNull
    @Override
    public PickupLocationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pickup_locations_list, parent, false);
        return new PickupLocationAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final PickupLocationAdapter.ViewHolder holder, int position) {
        pickupLists = allPickupListsArray.get(position).getPickuplistsArrayList();
        holder.address.setText(pickupLists.get(position).getAddress());
        holder.landmark.setText(pickupLists.get(position).getLandmark());
        holder.area.setText(pickupLists.get(position).getArea());
        holder.city.setText(pickupLists.get(position).getCity());
        holder.pincode.setText(pickupLists.get(position).getPincode());
        holder.viewmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent locactivity = new Intent(context, PickupLocationMapActivity.class);
                locactivity.putExtra("latitude",pickupLists.get(holder.getAdapterPosition()).getLatitude());
                locactivity.putExtra("longitude",pickupLists.get(holder.getAdapterPosition()).getLangitude());
                locactivity.addFlags(FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(locactivity);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent catactivity = new Intent(context, SelfPickUpActivity.class);
                catactivity.putExtra("locationselectedPos",holder.getAdapterPosition());
                catactivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(catactivity);
            }
        });
        holder.favouriteLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public int getItemCount() {
        if (allPickupListsArray.size() > 0) {
            return allPickupListsArray.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView address, landmark, area, city, pincode, viewmap;
        ImageButton favouriteLocation;

        public ViewHolder(View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.pick_up_address);
            landmark = itemView.findViewById(R.id.pick_up_landmark);
            area = itemView.findViewById(R.id.pickup_area);
            city = itemView.findViewById(R.id.pickup_city);
            pincode = itemView.findViewById(R.id.pickup_pincode);
            viewmap = itemView.findViewById(R.id.view_map);
        }
    }
}