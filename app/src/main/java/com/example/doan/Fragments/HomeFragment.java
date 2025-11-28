package com.example.doan.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.Activities.CategoryActivity;
import com.example.doan.Activities.LoginActivity;
import com.example.doan.Adapters.CategoryAdapter;
import com.example.doan.Models.ApiResponse;
import com.example.doan.Models.Category;
import com.example.doan.Models.Drink;
import com.example.doan.Models.Product;
import com.example.doan.Adapters.ProductAdapter;
import com.example.doan.R;
import com.example.doan.Network.ApiService;
import com.example.doan.Network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener {

    private RecyclerView categoryRecyclerView;
    private RecyclerView productRecyclerView;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private final List<Category> categoryList = new ArrayList<>();
    private final List<Product> productList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        categoryRecyclerView = view.findViewById(R.id.category_recycler_view);
        productRecyclerView = view.findViewById(R.id.product_recycler_view);

        setupCategoryRecyclerView();
        setupProductRecyclerView();

        loadCategories();
        loadAllProducts(); // Initially load all products

        return view;
    }

    private void setupCategoryRecyclerView() {
        categoryAdapter = new CategoryAdapter(getContext(), categoryList, this);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryRecyclerView.setAdapter(categoryAdapter);
    }

    private void setupProductRecyclerView() {
        productAdapter = new ProductAdapter(getContext(), productList);
        productRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productRecyclerView.setAdapter(productAdapter);
    }

    @Override
    public void onCategoryClick(int categoryId) {
        if (categoryId == 0) {
            loadAllProducts();
            return;
        }

        // Kiểm tra xem người dùng đã đăng nhập chưa
        SharedPreferences prefs = getContext().getSharedPreferences("UTETeaPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token == null || token.isEmpty()) {
            // Người dùng chưa đăng nhập, chuyển đến màn hình đăng nhập
            Toast.makeText(getContext(), "Please log in to see products by category", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        } else {
            // Người dùng đã đăng nhập, tải sản phẩm
            loadProductsByCategory(categoryId);
        }
    }

    private void loadCategories() {
        ApiService apiService = RetrofitClient.getInstance(getContext()).getApiService();
        Call<ApiResponse<List<Category>>> call = apiService.getCategories();

        call.enqueue(new Callback<ApiResponse<List<Category>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Category>>> call, Response<ApiResponse<List<Category>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Category> newCategories = response.body().getData();
                    if (newCategories != null) {
                        categoryList.clear();
                        // Add an "All" category to the beginning of the list
                        Category allCategory = new Category();
                        allCategory.setId(0); // Use 0 or another special ID for "All"
                        allCategory.setName("All");
                        categoryList.add(allCategory);
                        categoryList.addAll(newCategories);
                        categoryAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Category>>> call, Throwable t) {
                Toast.makeText(getContext(), "An error occurred while loading categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAllProducts() {
        ApiService apiService = RetrofitClient.getInstance(getContext()).getApiService();
        Call<ApiResponse<List<Drink>>> call = apiService.getDrinks();

        call.enqueue(new Callback<ApiResponse<List<Drink>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Drink>>> call, Response<ApiResponse<List<Drink>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    updateProductListFromDrinks(response.body().getData());
                } else {
                    Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Drink>>> call, Throwable t) {
                Toast.makeText(getContext(), "An error occurred while loading products", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProductsByCategory(int categoryId) {
        ApiService apiService = RetrofitClient.getInstance(getContext()).getApiService();
        Call<ApiResponse<List<Drink>>> call = apiService.getProductsByCategory(categoryId);

        call.enqueue(new Callback<ApiResponse<List<Drink>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Drink>>> call, Response<ApiResponse<List<Drink>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    updateProductListFromDrinks(response.body().getData());
                } else {
                    Toast.makeText(getContext(), "Failed to load products for this category", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Drink>>> call, Throwable t) {
                Toast.makeText(getContext(), "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProductListFromDrinks(List<Drink> drinks) {
        productList.clear();
        if (drinks != null) {
            for (Drink drink : drinks) {
                Product product = new Product();
                product.setId(String.valueOf(drink.getId()));
                product.setName(drink.getName());
                product.setDescription(drink.getDescription());
                product.setPrice((int) drink.getBasePrice());
                // Construct the full image URL correctly
                String fullImageUrl = RetrofitClient.getBaseUrl() + drink.getImageUrl();
                product.setThumbnail(fullImageUrl);
                productList.add(product);
            }
        }
        productAdapter.notifyDataSetChanged();
        checkIfProductListIsEmpty();
    }

    private void checkIfProductListIsEmpty() {
        if (productList.isEmpty()) {
            Toast.makeText(getContext(), "No products found.", Toast.LENGTH_SHORT).show();
        }
    }
}
