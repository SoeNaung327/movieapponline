package com.itonemm.movieapponline1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SeriesRecyclerAdapter  extends RecyclerView.Adapter<SeriesRecyclerAdapter.SeriesHolder> {

    ArrayList<SeriesModel> seriesModels=new ArrayList<SeriesModel>();
    Context context;
    FragmentManager fragmentManager;


    public SeriesRecyclerAdapter(ArrayList<SeriesModel> seriesModels, Context context, FragmentManager fragmentManager) {
        this.seriesModels = seriesModels;
        this.context=context;
        this.fragmentManager=fragmentManager;
    }

    @NonNull
    @Override
    public SeriesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.movieitem,parent,false);

        return new SeriesHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final SeriesHolder holder, final int position) {

        holder.moviesr.setText((position+1)+"");
        holder.moviename.setText(seriesModels.get(position).seriesName);

        Glide.with(context)
                .load(seriesModels.get(position).seriesImage)
                .into(holder.movieimage);
        holder.movieimage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final PopupMenu popupMenu=new PopupMenu(context,holder.movieimage);
                popupMenu.getMenuInflater().inflate(R.menu.popup,popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if(item.getItemId()==R.id.delete_menu)
                        {
                            FirebaseFirestore db=FirebaseFirestore.getInstance();
                            CollectionReference Ref=db.collection("series");
                            Ref.document(SeriesFragment.documentIds.get(position)).delete();
                            Ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    SeriesFragment.documentIds.clear();
                                    for(DocumentSnapshot s : queryDocumentSnapshots)
                                    {
                                        seriesModels.add(s.toObject(SeriesModel.class));
                                        SeriesFragment.documentIds.add(s.getId());
                                    }
                                    SeriesRecyclerAdapter adapter=new SeriesRecyclerAdapter(seriesModels,context,fragmentManager);
                                    SeriesFragment.recyclerView.setAdapter(adapter);
                                    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                    SeriesFragment.recyclerView.setLayoutManager(linearLayoutManager);
                                    SeriesFragment.progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                        if(item.getItemId()==R.id.edit_menu)
                        {
                            SeriesPopUp popUp=new SeriesPopUp() ;
                            popUp.id=SeriesFragment.documentIds.get(position);
                            popUp.model=seriesModels.get(position);
                            popUp.show(fragmentManager,"Series Edit");

                        }
                        return true;
                    }
                });
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return seriesModels.size();
    }

    public class SeriesHolder extends RecyclerView.ViewHolder{

        ImageView movieimage;
        TextView moviesr,moviename;
        public SeriesHolder(@NonNull View itemView) {
            super(itemView);
            moviesr=itemView.findViewById(R.id.txt_item_sr);
            moviename=itemView.findViewById(R.id.txt_item_name);
            movieimage=itemView.findViewById(R.id.item_image);

        }
    }
}
