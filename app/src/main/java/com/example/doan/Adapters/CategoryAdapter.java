/* MSSV: 23162114
        NAME: Pham Quang Vinh*/

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
import com.example.doan.Models.Category;
import com.example.doan.Network.RetrofitClient;
import com.example.doan.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    // Callback khi click vào 1 category
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    private Context context;
    private List<Category> categoryList;
    private OnCategoryClickListener listener;

    public CategoryAdapter(Context context, List<Category> categoryList, OnCategoryClickListener listener) {
        this.context = context;
        this.categoryList = categoryList != null ? categoryList : new ArrayList<>();
        this.listener = listener;
    }

    // Cho phép cập nhật lại data sau khi gọi API
    public void setData(List<Category> list) {
        this.categoryList = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        if (category == null) return;

        // Set tên
        holder.tvCategoryName.setText(category.getName());

        // Tạo URL ảnh đầy đủ từ baseUrl + "images/" + tên file
        // BASE_URL hiện trong RetrofitClient là "http://app.iotstar.vn:8081/appfoods/"
        String baseUrl = RetrofitClient.getBaseUrl(); // đã loại "/api/" nếu có
        String imageUrl = baseUrl + "images/" + category.getImage();

        // Load ảnh bằng Glide
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground) // ảnh tạm khi loading
                .error(R.drawable.ic_launcher_foreground)       // ảnh khi lỗi
                .into(holder.imgCategory);

        // Click item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        ImageView imgCategory;
        TextView tvCategoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategory = itemView.findViewById(R.id.imgCategory);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}
