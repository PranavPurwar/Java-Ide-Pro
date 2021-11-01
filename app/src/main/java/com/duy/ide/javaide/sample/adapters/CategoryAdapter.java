package com.duy.ide.javaide.sample.adapters;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duy.ide.R;
import com.duy.ide.javaide.sample.fragments.SelectCategoryFragment;
import com.duy.ide.javaide.sample.model.CodeCategory;

import java.util.ArrayList;

/**
 * Adapter for list sample code
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private SelectCategoryFragment.CategoryClickListener listener;
    private LayoutInflater layoutInflater;
    private ArrayList<CodeCategory> categories;

    public CategoryAdapter(FragmentActivity activity, ArrayList<CodeCategory> categories) {
        this.layoutInflater = LayoutInflater.from(activity);
        this.categories = categories;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.list_item_sample_category, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.txtName.setText(categories.get(position).getTitle());
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCategoryClick(categories.get(holder.getAdapterPosition()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setListener(SelectCategoryFragment.CategoryClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName;
        public View root;

        public ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_name);
            root = itemView.findViewById(R.id.root);
        }
    }
}
