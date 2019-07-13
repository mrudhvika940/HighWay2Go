package app.com.example.cruck.h2g.signup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

import app.com.example.cruck.h2g.R;
import app.com.example.cruck.h2g.firebaseconfig.Config;
import app.com.example.cruck.h2g.serviceuser.ServiceUserLanding;

public class ServiceUserPostSignUp extends AppCompatActivity {

    protected EditText usernameEditText;
    protected EditText fullnameEditText;
    protected EditText phoneEditText;
    protected Button createButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_data);

        usernameEditText = (EditText) findViewById(R.id.usernamefield);
        fullnameEditText = (EditText) findViewById(R.id.fullnamefield);
        phoneEditText = (EditText) findViewById(R.id.phonefield);
        createButton = (Button) findViewById(R.id.createbutton);

        final Firebase ref = new Firebase(Config.FIREBASE_URL);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernameEditText.getText().toString().trim();
                final String fullname = fullnameEditText.getText().toString().trim();
                final String phone = phoneEditText.getText().toString().trim();

                if(username.isEmpty() || fullname.isEmpty() || phone.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ServiceUserPostSignUp.this);
                    builder.setMessage(R.string.empty_message)
                            .setTitle(R.string.signup_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    String email = getIntent().getExtras().getString("email");
                    String password = getIntent().getExtras().getString("password");

                    final String emailAddress = email;
                    ref.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {
                            // Authenticated successfully with payload authData
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("email", emailAddress);
                            map.put("username", username);
                            map.put("name", fullname);
                            map.put("phone", phone);
                            ref.child("users").child(authData.getUid()).setValue(map);

                            Intent intent = new Intent(ServiceUserPostSignUp.this, ServiceUserLanding.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            // Authenticated failed with error firebaseError
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ServiceUserPostSignUp.this);
                            builder.setMessage(firebaseError.getMessage())
                                    .setTitle(R.string.login_error_title)
                                    .setPositiveButton(android.R.string.ok, null);
                            android.app.AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });

                }
            }
        });
    }
}

