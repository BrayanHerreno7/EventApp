package com.example.eventapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.eventapp.ui.sell.SellViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.List;

public class CreateProductActivity extends AppCompatActivity {

    private String TAG = "CreateProductActivity";
    private SellViewModel mViewModel;
    private StorageReference storageReference;
    private Button buttonUpload;
    private ImageView imagen;
    private ProgressDialog progressDialog;
    private static final int GALLERY_INTENT = 1;
    private List<String> listaDepartamentos;
    private List<String> listaCiudades;
    private List<String> listaTipoProducto;
    private ArrayAdapter<String> AdapterDepartamento;
    private ArrayAdapter<String> AdapterCiudad;
    private ArrayAdapter<String> AdapterTipoProdcuto;
    private Spinner sDepartamento;
    private Spinner sCiudad;
    private Spinner sTipoProducto;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);

        storageReference = FirebaseStorage.getInstance().getReference();
        buttonUpload = (Button) findViewById(R.id.buttonUploadImage);
        imagen = (ImageView) findViewById(R.id.imageView5);
        sDepartamento = (Spinner) findViewById(R.id.s_departamento);
        sCiudad = (Spinner) findViewById(R.id.s_ciudad);
        sTipoProducto = (Spinner) findViewById(R.id.s_tipo_producto);

        listaDepartamentos = Arrays.asList(new String[]{"Cundinamarca", "Valle del Cauca", "Boyaca", "Santander", "Bolivar"});
        listaCiudades = Arrays.asList(new String[]{"Bogota D.C", "Madrid", "Mosquera", "Cali", "Tunja", "Bucaramanga", "Cartagena"});
        listaTipoProducto = Arrays.asList(new String[]{"Vestidos", "Decoracion", "Bebidas", "Comida", "Salones", "Otro"});

        AdapterDepartamento =  new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, listaDepartamentos);
        AdapterCiudad =  new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, listaCiudades);
        AdapterTipoProdcuto =  new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, listaTipoProducto);

        sDepartamento.setAdapter(AdapterDepartamento);
        sCiudad.setAdapter(AdapterCiudad);
        sTipoProducto.setAdapter(AdapterTipoProdcuto);


        //consulta y descarga de productos
        downLoadWithBytesAll();

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_INTENT);
            }
        });
    }
    public void downLoadWithBytes(){
        StorageReference imageRef1 = storageReference.child("Product Photos/").child("ASFRTDF445648");
        long MAXBYTES = 7000000;
        imageRef1.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                imagen.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error al descargar imagenes", Toast.LENGTH_LONG).show();
                Log.e("Descarga de imagen", e.toString() );
            }
        });

    }
    public void downLoadWithBytes2( StorageReference imageRef1){
        long MAXBYTES = 7000000;
        imageRef1.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                imagen.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error al descargar imagenes", Toast.LENGTH_LONG).show();
                Log.e("Descarga de imagen", e.toString() );
            }
        });

    }
    public void downLoadWithBytesAll(){
        StorageReference listRef = storageReference.child("PhotosProduct/").child("HDd8QEPoN57p5C3ayDf1/");
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
                            downLoadWithBytes2(item);
                            Toast.makeText(getApplicationContext(), item.getName(), Toast.LENGTH_SHORT).show();

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        progressDialog.setTitle("Subiendo Foto...");
        progressDialog.setMessage("Subiendo Foto a Firebase");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if(requestCode == GALLERY_INTENT && resultCode == Activity.RESULT_OK){
            Uri uri = data.getData();
            StorageReference filePath = storageReference.child("PhotosProduct").child("ASFRTDF445649");
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
//                    Uri descargarFoto = taskSnapshot.getUploadSessionUri();
                    Toast.makeText(getApplicationContext(), "Se subio exitosamente la foto", Toast.LENGTH_SHORT).show();

//                    Toast.makeText(getContext(), descargarFoto.toString(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

}