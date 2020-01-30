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

public class MovieRecyclerAdapter  extends RecyclerView.Adapter<MovieRecyclerAdapter.MovieHolder> {

    ArrayList<MovieModel> movieModels=new ArrayList<MovieModel>();
    Context context;
    FragmentManager fragmentManager;


    public MovieRecyclerAdapter(ArrayList<MovieModel> movieModels, Context context, FragmentManager fragmentManager) {
        this.movieModels = movieModels;
        this.context=context;
        this.fragmentManager=fragmentManager;
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.movieitem,parent,false);

        return new MovieHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieHolder holder, final int position) {

        holder.moviesr.setText((position+1)+"");
        holder.moviename.setText(movieModels.get(position).movieName);

        Glide.with(context)
                .load(movieModels.get(position).movieImage)
                .into(holder.movieimage);
        holder.movieimage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                PopupMenu popupMenu=new PopupMenu(context,holder.movieimage);
                popupMenu.getMenuInflater().inflate(R.menu.popup,popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if(item.getItemId()==R.id.delete_menu)
                        {
                            FirebaseFirestore db=FirebaseFirestore.getInstance();
                            CollectionReference movies=db.collection("movies");
                            movies.document(MovieFragment.documentIds.get(position)).delete();
                            movies.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    ArrayList<MovieModel> movieModels=new ArrayList<MovieModel>();
                                    MovieFragment.documentIds.clear();
                                    for(DocumentSnapshot s : queryDocumentSnapshots)
                                    {
                                        movieModels.add(s.toObject(MovieModel.class));
                                        MovieFragment.documentIds.add(s.getId());
                                    }
                                    MovieRecyclerAdapter adapter=new MovieRecyclerAdapter(movieModels,context,fragmentManager);
                                    MovieFragment.recyclerView.setAdapter(adapter);
                                    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                                    MovieFragment.recyclerView.setLayoutManager(linearLayoutManager);
                                    MovieFragment.progressBar.setVisibility(View.GONE);
                                }
                            });
                        }

                        if(item.getItemId()==R.id.edit_menu)
                        {
                            MoviePopUp popUp=new MoviePopUp();
                            popUp.movieModel=movieModels.get(position);
                            popUp.id=MovieFragment.documentIds.get(position);
                            popUp.show(fragmentManager,"Edit Movie");

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
        return movieModels.size();
    }

    public class MovieHolder extends RecyclerView.ViewHolder{

        ImageView movieimage;
        TextView moviesr,moviename;
        public MovieHolder(@NonNull View itemView) {
            super(itemView);
            moviesr=itemView.findViewById(R.id.txt_item_sr);
            moviename=itemView.findViewById(R.id.txt_item_name);
            movieimage=itemView.findViewById(R.id.item_image);

        }
    }
}
