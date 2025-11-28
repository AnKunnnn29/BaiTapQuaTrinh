package com.example.doan.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.Models.ApiResponse;
import com.example.doan.Models.RegisterRequest;
import com.example.doan.Network.RetrofitClient;
import com.example.doan.R;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private EditText usernameInput, passwordInput, confirmPasswordInput, phoneInput;
    private Button registerButton;
    private TextView loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameInput = findViewById(R.id.input_reg_username);
        passwordInput = findViewById(R.id.input_reg_password);
        confirmPasswordInput = findViewById(R.id.input_reg_confirm_password);
        phoneInput = findViewById(R.id.input_reg_email);
        registerButton = findViewById(R.id.btn_register_submit);
        loginLink = findViewById(R.id.text_login_link);

        registerButton.setOnClickListener(v -> attemptRegister());
        loginLink.setOnClickListener(v -> finish());
    }

    private void attemptRegister() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();
        String email = phoneInput.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự.", Toast.LENGTH_SHORT).show();
            return;
        }

        RegisterRequest registerRequest = new RegisterRequest(username, email, password, username, "");

        RetrofitClient.getInstance(this).getApiService().registerWithOtp(registerRequest)
                .enqueue(new Callback<ApiResponse<String>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<String>> call, @NonNull Response<ApiResponse<String>> response) {
                        // Case 1: Phản hồi HTTP thành công (mã 2xx)
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<String> apiResponse = response.body();
                            // Kiểm tra cờ 'success' trong nội dung phản hồi
                            if (apiResponse.isSuccess()) {
                                Toast.makeText(RegisterActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, OtpActivity.class);
                                intent.putExtra("USER_IDENTIFIER", email);
                                startActivity(intent);
                                finish();
                            } else {
                                // Server trả về 200 OK nhưng báo lỗi (ví dụ: success: false)
                                Toast.makeText(RegisterActivity.this, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // Case 2: Phản hồi HTTP thất bại (mã 4xx, 5xx)
                            String errorMessage = "Lỗi đăng ký. Code: " + response.code();
                            if (response.errorBody() != null) {
                                try {
                                    String errorBodyStr = response.errorBody().string();
                                    Log.e(TAG, "Server error response: " + errorBodyStr);
                                    // Cố gắng phân tích JSON để lấy thông báo lỗi chi tiết
                                    JSONObject errorObj = new JSONObject(errorBodyStr);
                                    if (errorObj.has("message")) {
                                        errorMessage = errorObj.getString("message");
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error parsing error body", e);
                                }
                            }
                            Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<String>> call, @NonNull Throwable t) {
                        Log.e(TAG, "Lỗi mạng: " + t.getMessage());
                        Toast.makeText(RegisterActivity.this, "Lỗi mạng, không thể kết nối server.", Toast.LENGTH_SHORT).show();
                    }
        });
    }
}
