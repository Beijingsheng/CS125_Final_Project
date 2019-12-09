package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Visibility;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.util.Log;
import android.net.Uri;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity {
    Button add;
    Button notify;
    EditText name;
    EditText email;
    FirebaseDatabase fbdb;
    DatabaseReference db;
    Spinner position;
    TextView allHitters;
    TextView allSetters;
    TextView allPassers;
    String toShowHitters;
    String toShowSetters;
    String toShowPasser;
    //JSONArray memData;
    Button showHide;
    List<String> CCList = new ArrayList<String>();;

    //int countShowHide;
    TextView mem;
    ImageView vball;
    Spinner team;
    Button showHideTeam;
    Button undo;
    String undoId = "";
    int undoed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fbdb = FirebaseDatabase.getInstance();
        db = fbdb.getReference("members");
        add = findViewById(R.id.add);
        notify = findViewById(R.id.notify);
        undo = findViewById(R.id.undo);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        position = findViewById(R.id.position);
        team = findViewById(R.id.team);
        showHide = findViewById(R.id.showHide);
        showHideTeam = findViewById(R.id.showHideTeam);
        showHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Member start = new Member("A","B","C","D", "E");
                String idStart = db.push().getKey();
                db.child(idStart).setValue(start);
                db.child(idStart).setValue(null);
                if (showHideTeam.getVisibility() == View.VISIBLE) {
                    showHide.setText("Hide(Position)");
                    allPassers.setVisibility(View.VISIBLE);
                    allSetters.setVisibility(View.VISIBLE);
                    allHitters.setVisibility(View.VISIBLE);
                    notify.setVisibility(View.VISIBLE);
                    mem.setVisibility(View.VISIBLE);
                    showHideTeam.setVisibility(GONE);
                } else {
                    showHide.setText("Show(Position)");
                    allPassers.setVisibility(GONE);
                    allSetters.setVisibility(GONE);
                    allHitters.setVisibility(GONE);
                    mem.setVisibility(GONE);
                    notify.setVisibility(GONE);
                    showHideTeam.setVisibility(View.VISIBLE);
                }
            }
        });

        showHideTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Member start = new Member("A","B","C","D", "E");
                String idStart = db.push().getKey();
                db.child(idStart).setValue(start);
                db.child(idStart).setValue(null);
                if (showHide.getVisibility() == View.VISIBLE) {
                    showHideTeam.setText("Hide(Team)");
                    allPassers.setVisibility(View.VISIBLE);
                    allSetters.setVisibility(View.VISIBLE);
                    allHitters.setVisibility(View.VISIBLE);
                    notify.setVisibility(View.VISIBLE);
                    mem.setVisibility(View.VISIBLE);
                    showHide.setVisibility(GONE);
                } else {
                    showHideTeam.setText("Show(Team)");
                    allPassers.setVisibility(GONE);
                    allSetters.setVisibility(GONE);
                    allHitters.setVisibility(GONE);
                    notify.setVisibility(GONE);
                    mem.setVisibility(GONE);
                    showHide.setVisibility(View.VISIBLE);
                }
            }
        });

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (undoed == 0) {
                    if (!undoId.equals("")) {
                        db.child(undoId).setValue(null);
                        if( CCList.size() > 0 )
                            CCList.remove( CCList.size() - 1 );
                        undoed = 1;
                    }
                } else {
                    RotateAnimation rotateAnimation = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF,
                            .5f, RotateAnimation.RELATIVE_TO_SELF, .5f);
                    rotateAnimation.setDuration(1000);
                    vball.startAnimation(rotateAnimation);
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String n = name.getText().toString().trim();
                if (!n.equals("")) {
                    String id = db.push().getKey();
                    undoId = id;
                    undoed = 0;
                    String p = position.getSelectedItem().toString();
                    String t = team.getSelectedItem().toString();
                    String e = email.getText().toString();
                    //String defaultTeam = "none";
                    Member m = new Member(n, id, p, t, e);
                    db.child(id).setValue(m);
                    name.setText("");
                    email.setText("");
                } else {
                    RotateAnimation rotateAnimation = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF,
                            .5f, RotateAnimation.RELATIVE_TO_SELF, .5f);
                    rotateAnimation.setDuration(1000);
                    vball.startAnimation(rotateAnimation);
                }
            }
        });

        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        fbdb = FirebaseDatabase.getInstance();
        db = fbdb.getReference("members");
        allHitters = findViewById(R.id.hitters_one);
        allPassers = findViewById(R.id.passer_three);
        allSetters = findViewById(R.id.setter_two);
        mem = findViewById(R.id.textView4);
        vball = findViewById(R.id.vball);
        allPassers.setVisibility(GONE);
        allSetters.setVisibility(GONE);
        allHitters.setVisibility(GONE);
        notify.setVisibility(GONE);
        mem.setVisibility(GONE);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /**JSONObject value = (JSONObject) dataSnapshot.getValue();
                try {
                    memData = (JSONArray) value.get("names");
                } catch (JSONException e) { }
                 **/
                //toShow = memData.toString();
                if (showHideTeam.getVisibility() == GONE) {
                    toShowHitters = "Hitters \n\n";
                    toShowPasser = "Passers \n\n";
                    toShowSetters = "Setters \n\n";
                    int countHitter = 1;
                    int countPasser = 1;
                    int countSetter = 1;
                    for (DataSnapshot m : dataSnapshot.getChildren()) {
                        if (m.child("position").getValue().toString().equals("Hitter")) {
                            toShowHitters = toShowHitters + countHitter + ". " + m.child("name").getValue() + "   " + "   " + m.child("team").getValue() + "\n";
                            CCList.add(m.child("email").getValue().toString());
                            countHitter++;
                        } else if (m.child("position").getValue().toString().equals("Setter")) {
                            toShowSetters = toShowSetters + countSetter + ". " + m.child("name").getValue() + "   " + "   " + m.child("team").getValue() + "\n";
                            CCList.add(m.child("email").getValue().toString());
                            countSetter++;
                        } else if (m.child("position").getValue().toString().equals("Passer")) {
                            toShowPasser = toShowPasser + countPasser + ". " + m.child("name").getValue() + "   " + "   " + m.child("team").getValue() + "\n";
                            CCList.add(m.child("email").getValue().toString());
                            countPasser++;
                        }
                    }
                }
                else if (showHide.getVisibility() == GONE) {
                    toShowHitters = "Team One \n\n";
                    toShowPasser = "Team Three \n\n";
                    toShowSetters = "Team Two \n\n";
                    int countHitter = 1;
                    int countPasser = 1;
                    int countSetter = 1;
                    for (DataSnapshot m : dataSnapshot.getChildren()) {

                        if (m.child("team").getValue().toString().equals("One")) {
                            toShowHitters = toShowHitters + countHitter + ". " + m.child("name").getValue() + "   " + "   " + m.child("position").getValue() + "\n";
                            CCList.add(m.child("email").getValue().toString());
                            countHitter++;
                        } else if (m.child("team").getValue().toString().equals("Two")) {
                            toShowSetters = toShowSetters + countSetter + ". " + m.child("name").getValue() + "   " + "   " + m.child("position").getValue() + "\n";
                            CCList.add(m.child("email").getValue().toString());
                            countSetter++;
                        } else if (m.child("team").getValue().toString().equals("Three")) {
                            toShowPasser = toShowPasser + countPasser + ". " + m.child("name").getValue() + "   " + "   " + m.child("position").getValue() + "\n";
                            CCList.add(m.child("email").getValue().toString());
                            countPasser++;
                        }
                    }
                }
                allHitters.setText(toShowHitters);
                allSetters.setText(toShowSetters);
                allPassers.setText(toShowPasser);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    protected void sendEmail() {
        Log.i("Send email", "");
        String[] TO = {"risheng2@illinois.edu"};
        //String[] CC = (String[]) CCList.toArray();
        String[] CC = new String[CCList.size()];

        // Convert ArrayList to object array
        Object[] objArr = CCList.toArray();

        // Iterating and converting to String
        int i = 0;
        for (Object obj : objArr) {
            CC[i++] = (String)obj;
        }
        String toSend = "\n" + toShowHitters + "\n" + "\n" + toShowSetters + "\n" + "\n" + toShowPasser + "\n";
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Team_List");
        emailIntent.putExtra(Intent.EXTRA_TEXT, toSend);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
