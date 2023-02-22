package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import com.google.firebase.database.*;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private EditText mobile, password;
    private Button mlogin;
    private String pass;
    static String mobileno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mobile = findViewById(R.id.login_mobile);
        password = findViewById(R.id.login_password);
        mlogin = findViewById(R.id.login_button);

        mlogin.setOnClickListener(v -> {
            mobileno = mobile.getText().toString().trim();
            pass = password.getText().toString().trim();

            //if mobile number is less than 10 digit
            if (mobileno.length() != 10) {
                Toast.makeText(this, "Enter 10 digit Mobile Number", Toast.LENGTH_LONG).show();
            }
            //if password field is empty.
            if (pass.isEmpty()) {
                Toast.makeText(this, "Enter Password", Toast.LENGTH_LONG).show();
            } else {
                //If everything is correct this function will check user exit in database or not
                checkuser(mobileno, pass);
            }
        });
    }

    private void checkuser(String mobileno, String pass) {

        DatabaseReference rootNode = FirebaseDatabase.getInstance().getReference();
        DatabaseReference reference = rootNode.child("users");

        reference.child("u->" + mobileno).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //Getting password from realtime firebase if mobile no. exit in firebase.
                    String pass1 = snapshot.child("Login_info").child("Password").getValue(String.class);
                    login(pass1);
                } else {
                    //If user not exit it will store the new user info.
                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("Mobile_no", mobileno);
                    hashMap.put("Password", pass);
                    storedata(hashMap);
                }
            }

            private void login(String pass1) {
                if (pass1.equals(pass)) {
                    startActivity(new Intent(LoginActivity.this, MainActivity2.class));
                    Toast.makeText(LoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Enter Correct Password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });
    }

    private void storedata(HashMap<Object, String> hashMap) {

        //Firebase Connection
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();
        reference.child("users").child("u->" + mobileno).child("Login_info").setValue(hashMap);

        //Open different page add intent line hear
        startActivity(new Intent(LoginActivity.this, MainActivity2.class));

        //To display toast Message after login
        Toast.makeText(this, "New candidate registration Login successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
