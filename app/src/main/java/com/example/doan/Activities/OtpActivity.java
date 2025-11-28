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
import com.example.doan.Models.LoginResponse;
import com.example.doan.Models.VerifyOtpRequest;
import com.example.doan.Network.RetrofitClient;
import com.example.doan.R;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity {

    private static final String TAG = "OtpActivity";

    private EditText otpInput;
    private Button verifyButton;
    private TextView resendOtpText;
    private String userIdentifier; // This holds the email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        otpInput = findViewById(R.id.input_otp);
        verifyButton = findViewById(R.id.btn_verify_otp);
        resendOtpText = findViewById(R.id.text_resend_otp);

        if (getIntent().hasExtra("USER_IDENTIFIER")) {
            userIdentifier = getIntent().getStringExtra("USER_IDENTIFIER");
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        verifyButton.setOnClickListener(v -> {
            String otp = otpInput.getText().toString().trim();
            if (otp.length() != 6) {
                Toast.makeText(this, "Mã OTP phải có 6 chữ số.", Toast.LENGTH_SHORT).show();
                return;
            }
            verifyOtp(otp);
        });

        resendOtpText.setOnClickListener(v -> resendOtp());
    }

    private void verifyOtp(String otp) {
        // Đảm bảo VerifyOtpRequest của bạn mapping đúng key "email"
        // (Xem lưu ý bên dưới về VerifyOtpRequest)
        VerifyOtpRequest request = new VerifyOtpRequest(userIdentifier, otp);

        // SỬA LẠI: Dùng ApiResponse<String>
        RetrofitClient.getInstance(this).getApiService().verifyOtp(request)
                .enqueue(new Callback<ApiResponse<String>>() { // <--- Đổi thành String
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<String>> call, @NonNull Response<ApiResponse<String>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<String> apiResponse = response.body();

                            // Vì bên backend dùng hàm ApiResponse.success() nên success luôn là true
                            if (apiResponse.isSuccess()) {
                                Toast.makeText(OtpActivity.this, "Kích hoạt thành công! Vui lòng đăng nhập.", Toast.LENGTH_LONG).show();

                                // --- CHUYỂN VỀ TRANG ĐĂNG NHẬP (LoginActivity) ---
                                // Theo yêu cầu của thầy bạn là xác thực xong phải đăng nhập lại
                                Intent intent = new Intent(OtpActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(OtpActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Xử lý lỗi từ server (400, 500)
                            String errorMessage = "Lỗi xác thực.";
                            try {
                                if (response.errorBody() != null) {
                                    String errorBodyStr = response.errorBody().string();
                                    JSONObject errorObj = new JSONObject(errorBodyStr);
                                    if (errorObj.has("message")) {
                                        errorMessage = errorObj.getString("message");
                                    }
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error body", e);
                            }
                            Toast.makeText(OtpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<String>> call, @NonNull Throwable t) {
                        // Nếu vẫn vào đây, hãy xem Logcat để biết chính xác lỗi gì
                        Log.e(TAG, "Lỗi Retrofit: " + t.getMessage());
                        t.printStackTrace();
                        Toast.makeText(OtpActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void resendOtp() {
        RetrofitClient.getInstance(this).getApiService().resendOtp(userIdentifier)
                .enqueue(new Callback<ApiResponse<String>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<String>> call, @NonNull Response<ApiResponse<String>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(OtpActivity.this, "OTP đã được gửi lại!", Toast.LENGTH_SHORT).show();
                        } else {
                             String errorMessage = "Lỗi gửi lại OTP.";
                             if(response.body() != null) {
                                 errorMessage = response.body().getMessage();
                             } else if (response.errorBody() != null) {
                                 try {
                                     errorMessage = new JSONObject(response.errorBody().string()).getString("message");
                                 } catch(Exception e) { /* Do nothing */ }
                             }
                            Toast.makeText(OtpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<String>> call, @NonNull Throwable t) {
                        Log.e(TAG, "Network Error: " + t.getMessage());
                        Toast.makeText(OtpActivity.this, "Không thể kết nối đến server.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
