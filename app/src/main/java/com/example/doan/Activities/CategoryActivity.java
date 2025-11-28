package com.example.doan.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.Models.ApiResponse;
import com.example.doan.Models.Product;
import com.example.doan.Adapters.ProductAdapter;
import com.example.doan.Network.ApiService;
import com.example.doan.Network.RetrofitClient;
import com.example.doan.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private NestedScrollView nestedScrollView;
    private ProgressBar progressBar;

    private int categoryId;
    private int page = 1;
    private int limit = 10;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoryId = getIntent().getIntExtra("category_id", -1);
        if (categoryId == -1) {
            Toast.makeText(this, "Invalid Category ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recycler_view_products);
        nestedScrollView = findViewById(R.id.nested_scroll_view);
        progressBar = findViewById(R.id.progress_bar);

        setupRecyclerView();
        loadProducts();

        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                if (!isLoading && !isLastPage) {
                    page++;
                    progressBar.setVisibility(View.VISIBLE);
                    loadProducts();
                }
            }
        });
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(this, productList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(productAdapter);
    }

    private void loadProducts() {
        isLoading = true;
        ApiService apiService = RetrofitClient.getInstance(this).getApiService();
        Call<ApiResponse<List<Product>>> call = apiService.getProductsByCategory(categoryId, "price", "asc", page, limit);

        call.enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                progressBar.setVisibility(View.GONE);
                isLoading = false;

                if (response.isSuccessful() && response.body() != null) {
                    List<Product> newProducts = response.body().getData();
                    if (newProducts != null && !newProducts.isEmpty()) {
                        productList.addAll(newProducts);
                        productAdapter.notifyDataSetChanged();
                    } else {
                        isLastPage = true;
                        Toast.makeText(CategoryActivity.this, "No more products", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CategoryActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                isLoading = false;
                Toast.makeText(CategoryActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
