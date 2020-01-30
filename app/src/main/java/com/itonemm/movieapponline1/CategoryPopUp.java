package com.itonemm.movieapponline1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import es.dmoral.toasty.Toasty;

public class CategoryPopUp extends DialogFragment {
    public CategoryModel model;
    public  String id="";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       final View view=inflater.inflate(R.layout.categorypopup,container,false);
        ImageView img_close=view.findViewById(R.id.category_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        final EditText edt_category_name=view.findViewById(R.id.category_name);

        if(model!=null)
        {
            edt_category_name.setText(model.categoryjName);
        }
        Button btn_save_category=view.findViewById(R.id.save_category);
        btn_save_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                if(!edt_category_name.getText().toString().equals("")){

                    if(model==null)
                    {
                        CategoryModel categoryModel=new CategoryModel(edt_category_name.getText().toString().trim());
                        FirebaseFirestore db=FirebaseFirestore.getInstance();
                        CollectionReference Ref=db.collection("categories");
                        Ref.add(categoryModel);
                        edt_category_name.setText("");


                        Toasty.success(getContext(),"Category Saved Successfully!",Toasty.LENGTH_LONG).show();;

                        CategoryFragment.loadData();
                    }
                    else
                    {
                        CategoryModel categoryModel=new CategoryModel(edt_category_name.getText().toString().trim());
                        FirebaseFirestore db=FirebaseFirestore.getInstance();
                        CollectionReference Ref=db.collection("categories");
                        Ref.document(id).set(categoryModel);
                        edt_category_name.setText("");


                        Toasty.success(getContext(),"Category Update Successfully!",Toasty.LENGTH_LONG).show();;

                        CategoryFragment.loadData();
                        model=null;
                        id="";
                    }
                }
                else
                {
                    Toasty.error(getContext(),"Please Fill Category Data!",Toasty.LENGTH_LONG).show();

                }
            }
        });


        Button btn_cancel_category=view.findViewById(R.id.cancel_category);
        btn_cancel_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_category_name.setText("");
            }
        });

       return view;
    }
}
