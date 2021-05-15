package com.example.eventapp.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.eventapp.AdaptadorListProduct;
import com.example.eventapp.Class.Product;
import com.example.eventapp.R;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
        ListView ListProduct  = root.findViewById(R.id.list_Product);
        ImageView imageView3 = root.findViewById(R.id.imageView3);
        ImageView imageView4 = root.findViewById(R.id.imageView4);
        ImageSlider imageSlider = root.findViewById(R.id.image_slider);

        List<SlideModel> imageList = new ArrayList<>();
        imageList.add(new SlideModel(R.drawable.event1, ScaleTypes.CENTER_INSIDE));
        imageList.add(new SlideModel(R.drawable.product1, ScaleTypes.CENTER_INSIDE));
        imageSlider.setImageList(imageList);

        imageView3.setImageResource(R.drawable.event2);
        imageView4.setImageResource(R.drawable.event3);




        ArrayList<Product> products = new ArrayList<>();
        for(int i=0; i<=2; i++){
            Product product = new Product();
            // Set value of product
            product.setName("Product "+(i+1));
            product.setUnitCost((i+1)*1000);
            product.setCity("Bogota D.C.");
            product.setDepartament("Cundinamarca");
            // Add Product to ArrayList Product
            products.add(product);

        }
        Log.i("Product-prueba", products.toString() );


        AdaptadorListProduct adaptadorListProduct =new AdaptadorListProduct(getContext(), products);
        ListProduct.setAdapter(adaptadorListProduct);

        ListProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast toast=Toast. makeText(getContext(),"Hello Javatpoint",Toast. LENGTH_SHORT);
                toast.show();
            }
        });

//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
////                textView.setText(s);
//            }
//        });

        return root;
    }
}