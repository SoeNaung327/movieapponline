package com.itonemm.movieapponline1;


import android.content.Context;
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
import androidx.fragment.app.FragmentManager;
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
public class CategoryFragment extends Fragment {

    public static FragmentManager fragmentManager;
    static ProgressBar progressBar;
    static RecyclerView recyclerView;
    static Context context;
    static LayoutInflater myinflater;
    public static ArrayList<String> categoryIds=new ArrayList<String>();
    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData();

    }


    public static void searchData(String searchquery)
    {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference categoryRef=db.collection("categories");

        categoryRef.whereEqualTo("categoryjName",searchquery).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                ArrayList<CategoryModel> categoryModels=new ArrayList<>();
                categoryIds.clear();;
                for(DocumentSnapshot s:queryDocumentSnapshots)
                {
                    categoryModels.add(s.toObject(CategoryModel.class));
                    categoryIds.add(s.getId());
                }
                CategoryRecyclerAdapter adapter=new CategoryRecyclerAdapter(categoryModels,context,myinflater);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context,RecyclerView.VERTICAL,false));

                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }
    public static void loadData()
    {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference categoryRef=db.collection("categories");
        categoryRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<CategoryModel> categoryModels=new ArrayList<>();
                categoryIds.clear();;
                for(DocumentSnapshot s:queryDocumentSnapshots)
                {
                    categoryModels.add(s.toObject(CategoryModel.class));
                    categoryIds.add(s.getId());
                }
                CategoryRecyclerAdapter adapter=new CategoryRecyclerAdapter(categoryModels,context,myinflater);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context,RecyclerView.VERTICAL,false));

                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_category, container, false);
        FloatingActionButton add_category=view.findViewById(R.id.add_category);
        add_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CategoryPopUp popUp=new CategoryPopUp();
                popUp.show(getFragmentManager(),"Show PopUp");
            }
        });



       progressBar=view.findViewById(R.id.progressBar);
       recyclerView=view.findViewById(R.id.category_list);
       context=getContext();
       myinflater=getLayoutInflater();
       fragmentManager=getFragmentManager();
        final EditText edt_search=view.findViewById(R.id.edt_search_category);
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(edt_search.getText().toString().equals(""))
                {
                    loadData();
                }
                else
                {
                    searchData(s.toString());
                }
            }
        });
       loadData();
        return  view;
    }

}
