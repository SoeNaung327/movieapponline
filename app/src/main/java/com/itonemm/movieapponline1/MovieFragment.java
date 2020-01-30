package com.itonemm.movieapponline1;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment {

    public static ArrayList<String> documentIds=new ArrayList<>();
     static ProgressBar progressBar;
    static RecyclerView recyclerView;
    public MovieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_movie, container, false);

        final EditText editText=view.findViewById(R.id.edt_search_movie);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(editText.getText().toString().equals(""))
                {
                    FirebaseFirestore db=FirebaseFirestore.getInstance();
                    CollectionReference collectionReference=db.collection("movies");
                    collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            ArrayList<MovieModel> movieModels=new ArrayList<MovieModel>();
                            documentIds.clear();
                            for(DocumentSnapshot s : queryDocumentSnapshots)
                            {
                                movieModels.add(s.toObject(MovieModel.class));
                                documentIds.add(s.getId());
                            }
                            MovieRecyclerAdapter adapter=new MovieRecyclerAdapter(movieModels,getContext(),getFragmentManager());
                            recyclerView.setAdapter(adapter);
                            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            progressBar.setVisibility(View.GONE);

                        }
                    });
                }
                else
                {
                    FirebaseFirestore db=FirebaseFirestore.getInstance();
                    CollectionReference collectionReference=db.collection("movies");
                   collectionReference.whereEqualTo("movieName",s.toString()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                       @Override
                       public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                           ArrayList<MovieModel> movieModels=new ArrayList<MovieModel>();
                           documentIds.clear();
                           for(DocumentSnapshot s : queryDocumentSnapshots)
                           {
                               movieModels.add(s.toObject(MovieModel.class));
                               documentIds.add(s.getId());
                           }
                           MovieRecyclerAdapter adapter=new MovieRecyclerAdapter(movieModels,getContext(),getFragmentManager());
                           recyclerView.setAdapter(adapter);
                           LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
                           recyclerView.setLayoutManager(linearLayoutManager);
                           progressBar.setVisibility(View.GONE);
                       }
                   });
                }
            }
        });

        FloatingActionButton add_movie=view.findViewById(R.id.add_movie);
        add_movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoviePopUp popUp=new MoviePopUp();
                popUp.show(getFragmentManager(),"Show Movie");
            }
        });

        progressBar=view.findViewById(R.id.movieloading);
        recyclerView=view.findViewById(R.id.movie_list);
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference collectionReference=db.collection("movies");
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<MovieModel> movieModels=new ArrayList<MovieModel>();
                documentIds.clear();
                for(DocumentSnapshot s : queryDocumentSnapshots)
                {
                    movieModels.add(s.toObject(MovieModel.class));
                    documentIds.add(s.getId());
                }
                MovieRecyclerAdapter adapter=new MovieRecyclerAdapter(movieModels,getContext(),getFragmentManager());
                recyclerView.setAdapter(adapter);
                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
                recyclerView.setLayoutManager(linearLayoutManager);
                progressBar.setVisibility(View.GONE);

            }
        });
        return view;
    }

}
