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

public class SeriesPopUp  extends DialogFragment {

    SeriesModel model;
    String id;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.seriespopup,container,false);
        ImageView close=view.findViewById(R.id.series_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        final Spinner spinner=view.findViewById(R.id.series_category);
        final ArrayList<String> categorynames=new ArrayList<String>();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference categoryRef=db.collection("categories");
        categoryRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                categorynames.clear();
                for(DocumentSnapshot snapshot:queryDocumentSnapshots)
                {
                    CategoryModel categoryModel=snapshot.toObject(CategoryModel.class);
                    categorynames.add(categoryModel.categoryjName);
                }
                ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line,categorynames);
                spinner.setAdapter(adapter);
                if(model!=null)
                {
                    for(int i=0;i<categorynames.size();i++)
                    {
                        if(categorynames.get(i).equals(model.seriesCategory))
                        {
                            spinner.setSelection(i);
                            break;
                        }
                    }
                }

            }
        });

        final EditText edt_seriesName=view.findViewById(R.id.edt_series_name);
        final EditText edt_seriesImage=view.findViewById(R.id.edt_series_image_link);
        final EditText edt_seriesVideo=view.findViewById(R.id.edt_series_video_link);
        if(model!=null)
        {
            edt_seriesImage.setText(model.seriesImage);
            edt_seriesVideo.setText(model.seriesVideo);
            edt_seriesName.setText(model.seriesName);

        }
        String series_category="";
        Button btnsaveseries=view.findViewById(R.id.save_series);
        btnsaveseries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(!edt_seriesImage.getText().toString().equals("") &&
                    !edt_seriesName.getText().toString().equals("") &&
                       !edt_seriesVideo.getText().toString().equals("")
               )
               {
                   SeriesModel seriesModel=new SeriesModel(
                           edt_seriesName.getText().toString(),
                           edt_seriesImage.getText().toString(),
                           edt_seriesVideo.getText().toString(),
                           categorynames.get(spinner.getSelectedItemPosition())
                   );
                   FirebaseFirestore db=FirebaseFirestore.getInstance();
                   CollectionReference seriesRef=db.collection("series");
                   if(model==null)
                   {
                       seriesRef.add(seriesModel);
                       Toasty.success(getContext(),"Series Save Successfully",Toasty.LENGTH_LONG).show();
                       edt_seriesImage.setText("");
                       edt_seriesName.setText("");
                       edt_seriesVideo.setText("");
                       spinner.setSelection(0);
                   }
                   else
                   {
                       seriesRef.document(id).set(seriesModel);
                       Toasty.success(getContext(),"Series Update Successfully",Toasty.LENGTH_LONG).show();
                       edt_seriesImage.setText("");
                       edt_seriesName.setText("");
                       edt_seriesVideo.setText("");
                       spinner.setSelection(0);
                       model=null;
                       id="";


                   }
                   CollectionReference collectionReference=db.collection("series");
                   collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                       @Override
                       public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                           ArrayList<SeriesModel> seriesModels=new ArrayList<SeriesModel>();
                           SeriesFragment.documentIds.clear();
                           for(DocumentSnapshot s : queryDocumentSnapshots)
                           {
                               seriesModels.add(s.toObject(SeriesModel.class));
                               SeriesFragment.documentIds.add(s.getId());
                           }
                           SeriesRecyclerAdapter adapter=new SeriesRecyclerAdapter(seriesModels,getContext(),getFragmentManager());
                           SeriesFragment.recyclerView.setAdapter(adapter);
                           LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false);
                           SeriesFragment.recyclerView.setLayoutManager(linearLayoutManager);
                           SeriesFragment.progressBar.setVisibility(View.GONE);
                       }
                   });
               }
               else
               {
                   Toasty.error(getContext(),"Please Fill Series Data!",Toasty.LENGTH_LONG).show();
               }
            }
        });

        Button btncancel=view.findViewById(R.id.cancel_series);
        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_seriesImage.setText("");
                edt_seriesName.setText("");
                edt_seriesVideo.setText("");
                spinner.setSelection(0);
            }
        });
        return view;
    }
}
