package com.example.doan.Fragments;

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

public class HomeFragment extends Fragment {

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
        loadProducts();

        return view;
    }

    private void setupCategoryRecyclerView() {
        categoryAdapter = new CategoryAdapter(getContext(), categoryList);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryRecyclerView.setAdapter(categoryAdapter);
    }

    private void setupProductRecyclerView() {
        productAdapter = new ProductAdapter(getContext(), productList);
        productRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productRecyclerView.setAdapter(productAdapter);
    }

    private void loadCategories() {
        ApiService apiService = RetrofitClient.getInstance(getContext()).getApiService();
        Call<ApiResponse<List<Category>>> call = apiService.getCategories();

        call.enqueue(new Callback<ApiResponse<List<Category>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Category>>> call, Response<ApiResponse<List<Category>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> newCategories = response.body().getData();
                    if (newCategories != null) {
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

    private void loadProducts() {
        ApiService apiService = RetrofitClient.getInstance(getContext()).getApiService();
        Call<ApiResponse<List<Drink>>> call = apiService.getDrinks();

        call.enqueue(new Callback<ApiResponse<List<Drink>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Drink>>> call, Response<ApiResponse<List<Drink>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Drink>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        // Convert Drink to Product for adapter
                        productList.clear();
                        for (com.example.doan.Models.Drink drink : apiResponse.getData()) {
                            Product product = new Product(); // Simplified for example
                            productList.add(product);
                        }
                        productAdapter.notifyDataSetChanged();

                        if (productList.isEmpty()) {
                            Toast.makeText(getContext(), "Thực đơn hiện tại đang trống.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Lỗi: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Lỗi tải thực đơn: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("HomeFragment", "Lỗi tải sản phẩm, Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Drink>>> call, Throwable t) {
                Log.e("HomeFragment", "Lỗi kết nối API: " + t.getMessage());
                Toast.makeText(getContext(), "Không thể kết nối Server để tải thực đơn.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
