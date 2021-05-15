
package com.example.eventapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.eventapp.Class.Product;
import com.example.eventapp.ui.gallery.GalleryViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.internal.ViewOverlayImpl;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailProductActivity extends AppCompatActivity {

    private static String TAG = "DetailFragment";
    private StorageReference storageReference;
    private ImageSlider imageSlider;
    private ProgressDialog progressDialog;
    private Bitmap image;
    private List<SlideModel> imageList;

    TextView name ;
    TextView price ;
    EditText cantidad ;
    TextView costoXmayor ;
    TextView cantidadCostoXmayor ;
    TextView costoUnitario ;
    TextView departamento  ;
    TextView ciudad ;
    TextView descripcion;
    ImageButton incrementar;
    ImageButton disminuir;
    ImageButton favorito;

    Button ComprarProducto;
    Boolean isFavorite = false;

    Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);

        //region declaracion de componentes

        imageSlider = findViewById(R.id.image_slider_datail_product);
         name = findViewById(R.id.tv_Name);
         price = findViewById(R.id.tv_Price);
         cantidad = findViewById(R.id.et_amount);
         costoXmayor = findViewById(R.id.tv_WholeSaleCost);
         cantidadCostoXmayor = findViewById(R.id.tv_WholeSaleQuantity);
         costoUnitario = findViewById(R.id.tv_UnitCost);
         departamento  = findViewById(R.id.tv_Department);
         ciudad = findViewById(R.id.tv_City);
         descripcion = findViewById(R.id.tv_Description);

        incrementar = findViewById(R.id.btn_increase);
        disminuir = findViewById(R.id.btn_diminish);
        favorito = findViewById(R.id.btn_favorites);
        ComprarProducto = findViewById(R.id.btn_Comprar);



        //endregion

        //region Invocacion de metodos
        storageReference = FirebaseStorage.getInstance().getReference();

        // descargar producto(info e imagenes)
        DownLoadProdcuts(getIntent().getExtras().getString("id"));

        incrementar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int auxCantidad = Integer.parseInt(cantidad.getText().toString());
                cantidad.setText(String.valueOf(auxCantidad+1) );
            }
        });

        disminuir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int auxCantidad = Integer.parseInt(cantidad.getText().toString());
                cantidad.setText(String.valueOf(auxCantidad-1));
            }
        });

        ComprarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CarActivity.class);
//                intent.putExtra("id", products.get(position).getId());
                startActivity(intent);
            }
        });

        favorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFavorite){
                    NewFavorite();
                }else{
                    favorito.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    isFavorite = false;
                }

            }
        });


        //endregion

    }

    public void downLoadWithURL(StorageReference imageRef1){

        imageRef1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL
                Log.i(TAG, uri.toString());
                imageList.add(new SlideModel(uri.toString(), ScaleTypes.CENTER_INSIDE));
                imageSlider.setImageList(imageList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });


    }
    public void downLoadWithBytesAllDocuments(String pchild){

        StorageReference listRef = storageReference.child("PhotosProduct/").child(pchild+"/");
        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            // All the prefixes under listRef.
                            // You may call listAll() recursively on them.
                        }

                        for (StorageReference item : listResult.getItems()) {
                            // All the items under listRef.
                            downLoadWithURL(item);
//                                Toast.makeText(getApplicationContext(), item.getName(), Toast.LENGTH_SHORT).show();

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
    private void DownLoadProdcuts (String idProduct){

        imageList = new ArrayList<>();
        // descarga de informacion del producto
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Productos").document(idProduct).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.i(TAG, documentSnapshot.get("Name").toString());
                name.setText(documentSnapshot.get("Name").toString());
                price.setText(documentSnapshot.get("UnitCost").toString());
                cantidad.setText("1");
                costoXmayor.setText(documentSnapshot.get("WholesaleCost").toString());
                cantidadCostoXmayor.setText(documentSnapshot.get("WholesaleQuantity").toString());
                costoUnitario.setText(documentSnapshot.get("UnitCost").toString());
                departamento.setText(documentSnapshot.get("Department").toString());
                ciudad.setText(documentSnapshot.get("City").toString());

                product = new Product();
                product.setName(documentSnapshot.get("Name").toString());
                product.setUnitCost(Integer.parseInt(documentSnapshot.get("UnitCost").toString()));
                product.setWholesaleCost(Integer.parseInt(documentSnapshot.get("WholesaleCost").toString()));
                product.setWholesaleQuantity(Integer.parseInt(documentSnapshot.get("WholesaleQuantity").toString()));
                product.setDepartament(documentSnapshot.get("Department").toString());
                product.setCity(documentSnapshot.get("City").toString());
                product.setId(documentSnapshot.getId());

            }

        });

        // descarga de imagenes
        downLoadWithBytesAllDocuments(idProduct);
    }

    private void NewFavorite(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //llamamos las preferencias para poder obtener el IDuser
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        String IdUser =  preferences.getString("idAccount",null);
        Map<String,Object> favorite =  new HashMap<>();
        favorite.put("City",product.getCity());
        favorite.put("Department",product.getDepartament());
        favorite.put("IdProduct",product.getId());
        favorite.put("IdUser",IdUser);
        favorite.put("Name",product.getName());
        favorite.put("UnitCost",product.getUnitCost());


        db.collection("Favoritos").add(favorite).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                favorito.setImageResource(R.drawable.ic_baseline_favorite_24);
                isFavorite = true;
            }
        });
    }
}