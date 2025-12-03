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
import com.example.doan.Adapters.ProductGridAdapter;
import com.example.doan.Models.ApiResponse;
import com.example.doan.Models.Category;
import com.example.doan.Models.Drink;
import com.example.doan.Models.Product;
import com.example.doan.R;
import com.example.doan.Network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener {

    private RecyclerView productRecyclerView;
    private RecyclerView categoryRecyclerView;
    private ProductGridAdapter productAdapter;
    private CategoryAdapter categoryAdapter;

    private final List<Product> currentProductList = new ArrayList<>();
    private final List<Product> allProducts = new ArrayList<>(); // To store all products
    private final List<Category> categoryList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Product RecyclerView
        productRecyclerView = view.findViewById(R.id.product_recycler_view);
        productRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productAdapter = new ProductGridAdapter(currentProductList);
        productRecyclerView.setAdapter(productAdapter);

        // Category RecyclerView
        categoryRecyclerView = view.findViewById(R.id.category_recycler_view);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(categoryList, this);
        categoryRecyclerView.setAdapter(categoryAdapter);

        loadCategories();
        loadAllProducts();

        return view;
    }

    private void loadCategories() {
        RetrofitClient.getInstance(requireContext()).getApiService().getCategories().enqueue(new Callback<ApiResponse<List<Category>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Category>>> call, @NonNull Response<ApiResponse<List<Category>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Category>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        categoryList.clear();
                        // Add an "All" category
                        Category allCategory = new Category();
                        allCategory.setName("Tất cả");
                        allCategory.setImage("all_icon"); // Use a placeholder or a specific icon
                        categoryList.add(allCategory);
                        categoryList.addAll(apiResponse.getData());
                        categoryAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Lỗi tải danh mục: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Lỗi Server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Category>>> call, @NonNull Throwable t) {
                Log.e("HomeFragment", "Lỗi kết nối API: " + t.getMessage());
                Toast.makeText(getContext(), "Không thể kết nối Server để tải danh mục.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadAllProducts() {
        RetrofitClient.getInstance(requireContext()).getApiService().getDrinks().enqueue(new Callback<ApiResponse<List<Drink>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Drink>>> call, @NonNull Response<ApiResponse<List<Drink>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Drink>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        allProducts.clear();
                        for (Drink drink : apiResponse.getData()) {
                            Product product = new Product(
                                    drink.getId(),
                                    drink.getName(),
                                    drink.getDescription() != null ? drink.getDescription() : "",
                                    drink.getBasePrice(),
                                    drink.getCategoryName() != null ? drink.getCategoryName() : "",
                                    drink.getImageUrl(),
                                    drink.isActive()
                            );
                            allProducts.add(product);
                        }
                        // Initially, show all products
                        filterProductsByCategory(null);
                    } else {
                        Toast.makeText(getContext(), "Lỗi: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Lỗi tải thực đơn: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("HomeFragment", "Lỗi tải sản phẩm, Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Drink>>> call, @NonNull Throwable t) {
                Log.e("HomeFragment", "Lỗi kết nối API: " + t.getMessage());
                Toast.makeText(getContext(), "Không thể kết nối Server để tải thực đơn.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCategoryClick(Category category) {
        if (category.getName().equals("Tất cả")) {
            filterProductsByCategory(null); // Pass null to show all
        } else {
            filterProductsByCategory(category.getName());
        }
    }

    private void filterProductsByCategory(String categoryName) {
        currentProductList.clear();
        if (categoryName == null) {
            currentProductList.addAll(allProducts);
        } else {
            for (Product product : allProducts) {
                if (product.getCategory().equalsIgnoreCase(categoryName)) {
                    currentProductList.add(product);
                }
            }
        }
        // Sort products by price in ascending order
        currentProductList.sort((p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
        productAdapter.notifyDataSetChanged();
    }
}
