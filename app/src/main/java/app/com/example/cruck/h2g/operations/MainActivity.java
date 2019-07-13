package app.com.example.cruck.h2g.operations;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.firebase.client.Firebase;

import app.com.example.cruck.h2g.R;
import app.com.example.cruck.h2g.firebaseconfig.Config;
import app.com.example.cruck.h2g.signin.ServiceProviderSignIn;
import app.com.example.cruck.h2g.signin.ServiceUserSignIn;

public class MainActivity extends AppCompatActivity {

    private Firebase mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Buttons initialisation
        Button customerBut = (Button) findViewById(R.id.signinBut);
        Button serviceproviderBut = (Button) findViewById(R.id.signupBut);
        //Buttons on click actions
        customerBut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ServiceUserSignIn.class);
                startActivity(i);
            }
        });

        serviceproviderBut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ServiceProviderSignIn.class);
                startActivity(i);
            }
        });

        // Check Authentication
        mRef = new Firebase(Config.FIREBASE_URL);
//        if (mRef.getAuth() == null) {
//            loadLoginView();
//        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            if(mRef.getAuth() != null) {
                mRef.unauth();
            }
//                loadLoginView();
        }

        return super.onOptionsItemSelected(item);
    }

//    private void loadLoginView() {
//        Intent intent = new Intent(this, ServiceUserSignIn.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//    }
}
