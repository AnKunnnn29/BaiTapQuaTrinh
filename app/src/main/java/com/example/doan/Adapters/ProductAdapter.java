package com.example.doan.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doan.Models.Drink;
import com.example.doan.Models.Product;
import com.example.doan.R;
import com.example.doan.Network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;

    // Constructor for Product list
    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    // Overloaded constructor for Drink list
    public ProductAdapter(Context context, List<Drink> drinkList, boolean isDrink) {
        this.context = context;
        this.productList = new ArrayList<>();
        for (Drink drink : drinkList) {
            Product product = new Product();
            product.setId(String.valueOf(drink.getId()));
            product.setName(drink.getName());
            product.setPrice((int) drink.getBasePrice());
            // The imageUrl from the API is a relative path, so we prepend the base URL.
            String fullImageUrl = RetrofitClient.getBaseUrl() + "/" + drink.getImageUrl();
            product.setThumbnail(fullImageUrl);
            this.productList.add(product);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_grid, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        if (product == null) {
            return; // Avoid processing null data
        }

        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.format("%,dđ", product.getPrice()));

        String thumbnailUrl = product.getThumbnail();

        // Use placeholder and error drawables for robustness
        Glide.with(context)
                .load(thumbnailUrl)
                .placeholder(R.drawable.ic_tea_cup) // Display while loading
                .error(R.drawable.ic_tea_cup)       // Display on error or if URL is null
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView productName;
        TextView productPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
        }
    }
}
