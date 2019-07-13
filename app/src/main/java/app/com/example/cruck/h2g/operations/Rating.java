package app.com.example.cruck.h2g.operations;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import app.com.example.cruck.h2g.R;
import app.com.example.cruck.h2g.firebaseconfig.Config;
import app.com.example.cruck.h2g.model.Comment;
import app.com.example.cruck.h2g.model.ServiceProvider;

public class Rating extends AppCompatActivity {

    private Firebase mRef;
    private String mUserId;

    private TextView serviceName;
    private TextView serviceDescription;
    private EditText commentWrite;
    private ImageButton submitComment;
    private RatingBar commentRating;
    private ListView lv;

    private String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        serviceName = (TextView) findViewById(R.id.textView2);
        serviceDescription = (TextView) findViewById(R.id.textView);
        commentWrite = (EditText) findViewById(R.id.editText);
        submitComment = (ImageButton) findViewById(R.id.imageButton2);
        commentRating = (RatingBar) findViewById(R.id.ratingBar);
        lv = (ListView) findViewById(R.id.listView);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        lv.setAdapter(adapter);

        Intent i = getIntent();
        key = i.getStringExtra("key");

        mRef = new Firebase(Config.FIREBASE_URL);
        try {
            mUserId = mRef.getAuth().getUid();
        } catch (Exception e) {
            loadLoginView();
        }

        //For showing the service Name and Description.
        String highwaysUrl = Config.FIREBASE_URL + "/services";
        Firebase highwaySearch = new Firebase(highwaysUrl);

        highwaySearch.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    ServiceProvider post = postSnapshot.getValue(ServiceProvider.class);
                    if (key.equals(postSnapshot.getKey().toString())) {
                        serviceName.setText(post.getServiceName());
                        serviceDescription.setText(post.getServiceDescription());
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        //For showing the comments.
        String commentUrl = Config.FIREBASE_URL +"/comments/"+key;
        Firebase commentDisplay = new Firebase(commentUrl);
        commentDisplay.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                adapter.clear();
                float count = 0;
                float div = 0;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Comment post = postSnapshot.getValue(Comment.class);
                    adapter.add(post.getComment());
                    count = count + Float.parseFloat(post.getRating());
                    div++;
                }

                if(div !=0) {
                    float answer = count/div;
                    commentRating.setRating(answer);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });


        commentWrite.setSelected(false);

        submitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String commentPosted = commentWrite.getText().toString();
                String ratingGiven = String.valueOf(commentRating.getRating());
                if(commentPosted.isEmpty() || ratingGiven.isEmpty() || ratingGiven.equals("0.0")) {
                    Toast.makeText(Rating.this, "The rating or the comment is missing. Try again.", Toast.LENGTH_LONG).show();
                }
                else {
                    String saveURL = Config.FIREBASE_URL;
                    Firebase saveFirebase = new Firebase(saveURL);
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("comment", commentPosted);
                    map.put("rating", ratingGiven);
                    saveFirebase.child("comments").child(key).child(mUserId).setValue(map);
                    commentWrite.setText("");
                    commentRating.setRating(0F);
                    Toast.makeText(Rating.this, "Comment Added.", Toast.LENGTH_LONG).show();
                }
            }
        });

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
            mRef.unauth();
            loadLoginView();
//            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadLoginView() {
        Toast.makeText(this, "Please login to continue",
                Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
