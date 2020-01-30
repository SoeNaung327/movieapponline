package com.itonemm.movieapponline1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class MoviePopUp extends DialogFragment {

    MovieModel movieModel;
    String id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.moviepopup,container,false);
        ImageView close=view.findViewById(R.id.movie_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        final EditText movieName=view.findViewById(R.id.edt_movie_name);
        final EditText movieImage=view.findViewById(R.id.edt_movie_image_link);
        final EditText movieVideo=view.findViewById(R.id.edt_movie_video_link);
        final Spinner sp_category=view.findViewById(R.id.movie_category);
        final Spinner sp_series=view.findViewById(R.id.movie_series);
        final ArrayList<String> categorynames=new ArrayList<String>();
        final ArrayList<String> seriesnames=new ArrayList<String>();
        final FirebaseFirestore db=FirebaseFirestore.getInstance();
        final CollectionReference movieRef=db.collection("movies");
        CollectionReference categoryRef=db.collection("categories");
        categoryRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                categorynames.clear();
                for(DocumentSnapshot s:queryDocumentSnapshots)
                {
                    CategoryModel categoryModel=s.toObject(CategoryModel.class);
                    categorynames.add(categoryModel.categoryjName);
                }
                ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line,categorynames);
                sp_category.setAdapter(adapter);
                if(movieModel!=null)
                {
                    for(int i=0;i<categorynames.size();i++)
                    {
                        if(categorynames.get(i).equals(movieModel.movieCategory))
                        {
                            sp_category.setSelection(i);
                            break;
                        }
                    }

                }
            }
        });

        CollectionReference seresRef=db.collection("series");
        seresRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                seriesnames.clear();;
                for(DocumentSnapshot snapshot:queryDocumentSnapshots){
                    SeriesModel seriesModel=snapshot.toObject(SeriesModel.class);
                    seriesnames.add(seriesModel.seriesName);

                    if(movieModel!=null)
                    {
                        for(int i=0;i<seriesnames.size();i++)
                        {
                            if(seriesnames.get(i).equals(movieModel.movieSeries))
                            {
                                sp_series.setSelection(i);
                                break;
                            }
                        }
                    }
                }

                ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line,seriesnames);
                sp_series.setAdapter(adapter);
            }
        });

        if(movieModel!=null)
        {
            movieName.setText(movieModel.movieName);
            movieImage.setText(movieModel.movieImage);
            movieVideo.setText(movieModel.movieVideo);


        }

        Button btnsave=view.findViewById(R.id.save_movie);
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!movieImage.getText().toString().equals("")
                && !movieVideo.getText().toString().equals("")
                && !movieName.getText().toString().equals(""))
                {

                    MovieModel movieModel1=new MovieModel(
                     movieName.getText().toString(),
                     movieImage.getText().toString(),
                     movieVideo.getText().toString(),
                     categorynames.get(sp_category.getSelectedItemPosition()),
                     seriesnames.get(sp_series.getSelectedItemPosition())

                    );
                    if(movieModel==null) {
                        movieRef.add(movieModel1);
                    }
                    else
                    {
                        movieRef.document(id).set(movieModel1);
                        movieModel=null;
                        id="";

                    }
                    CollectionReference movies=db.collection("movies");

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
                            MovieRecyclerAdapter adapter=new MovieRecyclerAdapter(movieModels,getContext(),getFragmentManager());
                            MovieFragment.recyclerView.setAdapter(adapter);
                            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false);
                            MovieFragment.recyclerView.setLayoutManager(linearLayoutManager);
                            MovieFragment.progressBar.setVisibility(View.GONE);
                        }
                    });
                    movieImage.setText("");
                    movieName.setText("");
                    movieVideo.setText("");
                    sp_category.setSelection(0);
                    sp_series.setSelection(0);
                    Toasty.success(getContext(),"Movie Saved Successfully",Toasty.LENGTH_LONG).show();


                }
                else
                {
                    Toasty.error(getContext(),"Please Fill Movie Data",Toasty.LENGTH_LONG).show();
                }
            }
        });

        Button btncancel=view.findViewById(R.id.cancel_movie);
        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movieImage.setText("");
                movieName.setText("");
                movieVideo.setText("");
                sp_category.setSelection(0);
                sp_series.setSelection(0);
            }
        });
        return view;
    }
}
