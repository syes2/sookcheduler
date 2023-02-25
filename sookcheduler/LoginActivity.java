package com.example.sookcheduler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Button btn_login = (Button)findViewById(R.id.btnlogin);
        final EditText loginid = (EditText)findViewById(R.id.loginId);
        final EditText loginpw = (EditText)findViewById(R.id.loginPassword);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id,pw;
                id = loginid.getEditableText().toString();
                pw = loginpw.getEditableText().toString();
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id",id);
                bundle.putString("pw",pw);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

}
