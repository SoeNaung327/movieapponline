package com.itonemm.movieapponline1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.CategoryHolder> {

    ArrayList<CategoryModel> categoryModels=new ArrayList<CategoryModel>();
    Context context;
    LayoutInflater inflater;

    public CategoryRecyclerAdapter(ArrayList<CategoryModel> categoryModels, Context context, LayoutInflater inflater) {
        this.categoryModels = categoryModels;
        this.context = context;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.categoryitem,parent,false);

        return new CategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryHolder holder, final int position) {

        holder.txt_sr.setText(position+1+"");
        holder.txt_name.setText(categoryModels.get(position).categoryjName);
        holder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(context,holder.options);
                MenuInflater inflater=popupMenu.getMenuInflater();
                inflater.inflate(R.menu.popup,popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if(item.getItemId()==R.id.delete_menu)

                        {

                            FirebaseFirestore db=FirebaseFirestore.getInstance();
                            CollectionReference categoryRef=db.collection("categories");
                            categoryRef.document(CategoryFragment.categoryIds.get(position)).delete();
                            CategoryFragment.loadData();
                        }

                        if(item.getItemId()==R.id.edit_menu)
                        {
                            CategoryPopUp categoryPopUp=new CategoryPopUp();
                            categoryPopUp.model=categoryModels.get(position);

                            categoryPopUp.id=CategoryFragment.categoryIds.get(position);
                            categoryPopUp.show(CategoryFragment.fragmentManager,"Show Category!");
                        }
                        return true;
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    public class CategoryHolder extends RecyclerView.ViewHolder{

        TextView txt_sr;
        TextView txt_name;
        ImageView options;

        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
            txt_sr=itemView.findViewById(R.id.txt_category_sr);
            txt_name=itemView.findViewById(R.id.txt_category_name);
            options=itemView.findViewById(R.id.category_options);
        }
    }
}
