package com.example.eventapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.eventapp.Class.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CarActivity extends AppCompatActivity {

    private static String TAG = "CarActivity";
    private StorageReference storageReference;
    private ArrayList<Product> products = new ArrayList<Product>();;
    private ListView ListProduct;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        storageReference = FirebaseStorage.getInstance().getReference();
        ListProduct = (ListView) findViewById(R.id.list_Car_Product);

        ListProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Product-search", "position: "+position );
                Intent intent = new Intent(view.getContext(), DetailProductActivity.class);
                intent.putExtra("id", products.get(position).getId());
                startActivity(intent);
            }
        });

        DownLoadProdcuts();

    }

    public void downLoadWithBytes2(StorageReference imageRef1, QueryDocumentSnapshot document){
        long MAXBYTES = 7000000;
        imageRef1.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                Product product = new Product();
                // Set value of product
                product.setId(document.get("IdProduct").toString());
                product.setName(document.get("Name").toString());
                product.setUnitCost(Integer.parseInt(document.get("UnitCost").toString()));
//                product.setCity(document.get("City").toString());
//                product.setDepartament(document.get("Department").toString());
                product.setImage(BitmapFactory.decodeByteArray(bytes,0,bytes.length));
                products.add(product);
                Log.i(TAG, imageRef1.getName());
                // Add Product to ArrayList Product
                AdaptadorListProduct adaptadorListProduct =new AdaptadorListProduct(getApplicationContext(), products);
                ListProduct.setAdapter(adaptadorListProduct);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error al descargar imagenes", Toast.LENGTH_LONG).show();
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

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        String IdUser =  preferences.getString("idAccount",null);
        // descarga de informacion del producto
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Carrito").whereEqualTo("IdUser",IdUser).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.i(TAG, document.getId() + " => " + document.getData());

                        downLoadWithBytesAll(document);
                    }
                    // to asing product the adapter
//                    progressDialog.dismiss();

                } else {
                    Log.i(TAG, "Error getting documents: ", task.getException());
                }
            }

        });

    }
}