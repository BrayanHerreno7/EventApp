package com.example.eventapp.ui.favorites;

import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.eventapp.AdaptadorListProduct;
import com.example.eventapp.Class.Product;
import com.example.eventapp.DetailProductActivity;
import com.example.eventapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {

    private FavoritesViewModel mViewModel;
    private static String TAG = "FavoritesFragment";
    private StorageReference storageReference;
    private ArrayList<Product> products = new ArrayList<Product>();
    private ListView ListProduct;
    private ProgressDialog progressDialog;
    private Bitmap image;


    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.favorites_fragment, container, false);

        storageReference = FirebaseStorage.getInstance().getReference();
        ListProduct = (ListView) root.findViewById(R.id.list_Product_Favorites);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Cargando...");
        progressDialog.setMessage("Cargando lista de productos favoritos");
        progressDialog.setCancelable(false);
        progressDialog.show();

        DownLoadProdcuts();

        ListProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Product-favorites", "position: "+position );
                Intent intent = new Intent(view.getContext(), DetailProductActivity.class);
                intent.putExtra("id", products.get(position).getId());
                startActivity(intent);

            }
        });


        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);
        // TODO: Use the ViewModel
    }

    public void downLoadWithBytes2( StorageReference imageRef1, QueryDocumentSnapshot document){
        long MAXBYTES = 7000000;
        imageRef1.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                Product product = new Product();
                // Set value of product
                product.setId(document.getId());
                product.setName(document.get("Name").toString());
                product.setUnitCost( Integer.parseInt(document.get("UnitCost").toString()));
                product.setCity(document.get("City").toString());
                product.setDepartament(document.get("Department").toString());
                product.setImage(BitmapFactory.decodeByteArray(bytes,0,bytes.length));
                products.add(product);
                Log.i(TAG, imageRef1.getName());
                // Add Product to ArrayList Product
                AdaptadorListProduct adaptadorListProduct =new AdaptadorListProduct(getContext(), products);
                ListProduct.setAdapter(adaptadorListProduct);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Error al descargar imagenes", Toast.LENGTH_LONG).show();
                Log.e("Descarga de imagen", e.toString() );
            }
        });

    }
    public void downLoadWithBytesAll(QueryDocumentSnapshot docuement){
        StorageReference listRef = storageReference.child("PhotosProduct/").child(docuement.get("IdProduct").toString()+"/");
        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        int count = 0;
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            // All the prefixes under listRef.
                            // You may call listAll() recursively on them.
                        }

                        for (StorageReference item : listResult.getItems()) {
                            // All the items under listRef.
                            if(count==0){
                                downLoadWithBytes2(item, docuement);
//                                Toast.makeText(getContext(), item.getName(), Toast.LENGTH_SHORT).show();
                            }
                            count++;

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.toString());
                    }
                });
    }
    private void DownLoadProdcuts (){

        SharedPreferences preferences = getContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        String IdUser =  preferences.getString("idAccount",null);
        Log.i(TAG, "IdUser: "+IdUser);
        // descarga de informacion del producto
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Favoritos").whereEqualTo("IdUser",IdUser).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.i(TAG, document.getId() + " => " + document.getData());

                        downLoadWithBytesAll(document);
                    }
                    // to asing product the adapter
                    progressDialog.dismiss();

                } else {
                    Log.i(TAG, "Error getting documents: ", task.getException());
                }
            }

        });

    }

}