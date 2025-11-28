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
import com.example.doan.Models.Product;
import com.example.doan.Adapters.ProductAdapter;
import com.example.doan.R;
import com.example.doan.Network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;
    private final List<Product> productList = new ArrayList<>();
    private RecyclerView rvCategory;
    private CategoryAdapter categoryAdapter;
    private final List<Category> categoryList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        rvCategory = view.findViewById(R.id.rvCategory);
        rvCategory.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        categoryAdapter = new CategoryAdapter(getContext(), categoryList, category ->
                Toast.makeText(getContext(), category.getName(), Toast.LENGTH_SHORT).show()
        );
        rvCategory.setAdapter(categoryAdapter);

        productRecyclerView = view.findViewById(R.id.product_recycler_view);


        productRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));


        productAdapter = new ProductAdapter(productList);
        productRecyclerView.setAdapter(productAdapter);

        loadProducts();

        return view;
    }

    /* MSSV: 23162114
        NAME: Pham Quang Vinh*/
    private void loadCategories() {
        RetrofitClient.getInstance(requireContext()).getApiService().getCategories()
                .enqueue(new Callback<ApiResponse<List<Category>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<List<Category>>> call,
                                           @NonNull Response<ApiResponse<List<Category>>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            List<Category> list = response.body().getData();

                            if (list != null) {

                                // 👉 CHỈ LẤY 3 CATEGORY ĐẦU TIÊN
                                if (list.size() > 3) {
                                    list = list.subList(0, 3);
                                }

                                categoryList.clear();
                                categoryList.addAll(list);
                                categoryAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<List<Category>>> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(), "Lỗi tải danh mục", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadProducts() {
        // Gọi API drinks từ backend mới
        RetrofitClient.getInstance(requireContext()).getApiService().getDrinks().enqueue(new Callback<com.example.doan.Models.ApiResponse<List<com.example.doan.Models.Drink>>>() {
            @Override
            public void onResponse(@NonNull Call<com.example.doan.Models.ApiResponse<List<com.example.doan.Models.Drink>>> call, 
                                 @NonNull Response<com.example.doan.Models.ApiResponse<List<com.example.doan.Models.Drink>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.doan.Models.ApiResponse<List<com.example.doan.Models.Drink>> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        // Convert Drink to Product for adapter
                        productList.clear();
                        for (com.example.doan.Models.Drink drink : apiResponse.getData()) {
                            Product product = new Product(
                                drink.getId(),
                                drink.getName(),
                                drink.getDescription() != null ? drink.getDescription() : "",
                                drink.getBasePrice(),
                                drink.getCategoryName() != null ? drink.getCategoryName() : "",
                                drink.getImageUrl(),
                                drink.isActive()
                            );
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
            public void onFailure(@NonNull Call<com.example.doan.Models.ApiResponse<List<com.example.doan.Models.Drink>>> call, @NonNull Throwable t) {
                Log.e("HomeFragment", "Lỗi kết nối API: " + t.getMessage());
                Toast.makeText(getContext(), "Không thể kết nối Server để tải thực đơn.", Toast.LENGTH_LONG).show();
            }
        });
    }
}