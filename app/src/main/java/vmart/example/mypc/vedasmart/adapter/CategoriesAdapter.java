package vmart.example.mypc.vedasmart.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.activities.CategoriesActivity;
import vmart.example.mypc.vedasmart.model.Categories;
import vmart.example.mypc.vedasmart.model.Products;
import java.util.ArrayList;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder>
{
    ArrayList<Products> categoriesArraylist = new ArrayList<>();
    ArrayList<Categories> categories = new ArrayList<>();
    Context context;

    public CategoriesAdapter(ArrayList<Products> categoires,Context context) {
        this.categoriesArraylist = categoires;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_navigation, viewGroup, false);
        return new CategoriesAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapter.ViewHolder viewHolder, int position) {
        categories = categoriesArraylist.get(position).getCategories();
        viewHolder.categoriesText.setText(categories.get(position).getCategoryName());
    }

    @Override
    public int getItemCount() {
        return categoriesArraylist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoriesText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoriesText = itemView.findViewById(R.id.tvNavigationName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent catactivity = new Intent(context, CategoriesActivity.class);
                    catactivity.putExtra("selectedPos",getAdapterPosition());
                    catactivity.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(catactivity);
                }
            });
        }
    }
}