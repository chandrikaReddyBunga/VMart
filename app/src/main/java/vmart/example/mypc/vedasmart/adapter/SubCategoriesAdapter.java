package vmart.example.mypc.vedasmart.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import vmart.example.mypc.vedasmart.R;
import vmart.example.mypc.vedasmart.activities.CategoriesActivity;
import vmart.example.mypc.vedasmart.model.Subcategories;
import java.util.ArrayList;

public class SubCategoriesAdapter extends RecyclerView.Adapter<SubCategoriesAdapter.ViewHolder> {
    Context context;
    ArrayList<Subcategories> subcategories;
    String selectedCategory;

    public SubCategoriesAdapter(Context context, ArrayList<Subcategories> subcategories) {
        this.context = context;
        this.subcategories = subcategories;
    }
    @NonNull
    @Override
    public SubCategoriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.items_categories, viewGroup, false);
        return new SubCategoriesAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SubCategoriesAdapter.ViewHolder holder, int position) {
        holder.subcategoryName.setText(subcategories.get(position).getSubCategoryName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof CategoriesActivity) {
                    selectedCategory = subcategories.get(holder.getAdapterPosition()).getSubCategoryName();
                    ((CategoriesActivity) context).fetchSubItems( holder.getAdapterPosition(),selectedCategory);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return subcategories.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView subcategoryName;
        public ViewHolder(View itemView) {
            super(itemView);
            subcategoryName = itemView.findViewById(R.id.subcategoryName);
        }
    }
}