package com.example.eventapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventapp.Class.Product;

import java.util.ArrayList;

public class AdaptadorListProduct extends BaseAdapter {
    private static LayoutInflater inflater = null;
    Context context;
    private ArrayList<Product> products;

    public AdaptadorListProduct (Context pcontext, ArrayList<Product> products){
        this.context = pcontext;
        this.products = products;
        inflater =   (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /* private view holder class */
    private class ViewHolder {
        ImageView imageProduct;
        TextView ProductLocation;
        TextView PriceProduct;
        TextView ProductName;
    }


    @Override
    public View getView(int i, View convertView, ViewGroup parent) {


        ViewHolder holder;
        if(convertView == null){
           convertView = inflater.inflate(R.layout.fragment_product_adapter, null);
           holder = new ViewHolder();
           holder.ProductName = (TextView) convertView.findViewById(R.id.tv_productName);
           holder.ProductLocation = (TextView) convertView.findViewById(R.id.tv_productLocation);
           holder.PriceProduct = (TextView) convertView.findViewById(R.id.tv_priceProduct);
           holder.imageProduct= (ImageView) convertView.findViewById(R.id.imageProduct);
           convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

//        ImageButton btmFavorite = (ImageButton) view.findViewById(R.id.btn_favorites);

        holder.ProductName.setText(products.get(i).getName());
        holder.ProductLocation.setText(products.get(i).getDepartament() + " - " + products.get(i).getCity());
        holder.PriceProduct.setText(String.valueOf(products.get(i).getUnitCost()));
        holder.imageProduct.setImageBitmap(products.get(i).getImage());

        Log.i("Product", products.get(i).getName());
        Log.i("Product", String.valueOf(products.get(i).getUnitCost()));
        return convertView;
    }
}
