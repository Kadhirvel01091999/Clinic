package in.dentocare.clinic_management;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.viewpager.widget.ViewPager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class UserInfo extends AppCompatActivity {

    private ViewPager mViewPager;
    static String emailStr, dateStr, timeStr;
    DatabaseReference appointBase;
    SectionsPageAdapter adapter;
    String lDate,lTime;
    static String ap = new String();
    String[] usrData = new String[5];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        emailStr = getIntent().getStringExtra("emailStr");

        mViewPager =  findViewById(R.id.container);
        setupViewPager(mViewPager);
//        final ProgressDialog progressDialog = new ProgressDialog(UserInfo.this);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Fetching...");
//        progressDialog.show();
//        progressDialog.getWindow().setLayout(900,400);
        TabLayout tabLayout =  findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        appointBase = FirebaseDatabase.getInstance().getReference("users/"+emailStr.replace(".",","));
        appointBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                userData = dataSnapshot.getValue(UserData.class);
//                StringBuilder b = new StringBuilder();
                int i = 0;
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
//                    b.append(data.getValue().toString()+" -- ");
                    if(!data.getKey().matches("appointments")) {
                        usrData[i] = data.getValue().toString();
                        i++;
                    }
                }
//                progressDialog.dismiss();
                Fragment2 f =(Fragment2) adapter.getItem(1);
                f.updateValues(usrData);
//                setupViewPager(mViewPager);
//                Toast.makeText(UserInfo.this,b+" // "+dataSnapshot.getChildrenCount(),Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserInfo.this, "Failed to read value."+error.toException(), Toast.LENGTH_LONG).show();
            }
        });

        getAppointments(FirebaseDatabase.getInstance().getReference("Appointments"));
    }
    public void getAppointments(DatabaseReference reference){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren())
                {
                    DatabaseReference year = data.getRef();
                    year.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot years) {
                            for(DataSnapshot monthN : years.getChildren()) {
                                DatabaseReference month = monthN.getRef();
                                month.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot months) {
                                        for(DataSnapshot dayN : months.getChildren()) {
                                            DatabaseReference day = dayN.getRef();
                                            day.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot days) {
                                                    for(DataSnapshot timeN : days.getChildren())
                                                    {
                                                        final DatabaseReference time = timeN.getRef();
//                                                        Toast.makeText(UserInfo.this, time.toString(), Toast.LENGTH_LONG).show();
                                                        time.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot times) {
                                                                lTime = times.getKey();
                                                                char [] d = times.getRef().toString().toCharArray();
                                                                lDate = ""+d[60]+d[61]+d[62]+d[63]+d[64]+d[65]+d[66]+d[67]+d[68]+d[69];
                                                                ap+=lDate+lTime;
//                                                                  Toast.makeText(UserInfo.this, lDate, Toast.LENGTH_LONG).show();

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                Toast.makeText(UserInfo.this, "Failed to read value."+databaseError.toException(), Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    Toast.makeText(UserInfo.this, "Failed to read value."+databaseError.toException(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(UserInfo.this, "Failed to read value."+databaseError.toException(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(UserInfo.this, "Failed to read value."+databaseError.toException(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserInfo.this, "Failed to read value."+databaseError.toException(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void setupViewPager(ViewPager viewPager){
        adapter = new SectionsPageAdapter(this.getSupportFragmentManager());
        adapter.addfragment(new Fragment1(), "Home");
        adapter.addfragment(new Fragment2(), "Info");
        adapter.addfragment(new Fragment3(), "Book");
        viewPager.setAdapter(adapter);

    }

    public AlertDialog.Builder alertBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit ?  You will be logged off from your account");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return builder;

    }

    @Override
    public void onBackPressed() {
        if(mViewPager.getCurrentItem()>0){
            mViewPager.setCurrentItem(0,true);
        }else if(mViewPager.getCurrentItem()==0){
            alertBox().show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.test_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_5:
                Toast.makeText(this,"hello", Toast.LENGTH_LONG).show();

                addNotification();

//                scheduleNotification(getNotification("5 second delay"), 5000);
                return true;
            case R.id.action_10:
                scheduleNotification(getNotification("10 second delay"), 10000);
                return true;
            case R.id.action_30:
                scheduleNotification(getNotification("30 second delay"), 30000);
                return true;
            case R.id.connect:
               // startActivity(new Intent(this,Connectivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void scheduleNotification(Notification notification, int delay) {
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }


    private Notification getNotification(String content) {

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder appointment = new NotificationCompat.Builder(this,"appoint")
                .setSmallIcon(R.drawable.appointment_notification)
                .setContentTitle("Appointment Reminder")
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        return appointment.build();
    }

    private void addNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.appointment_notification) //set icon for notification
                        .setContentTitle("Notifications Example") //set title of notification
                        .setContentText("This is a notification message")//this is notification message
                        .setAutoCancel(true) // makes auto cancel of notification
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT); //set priority of notification


        Intent notificationIntent = new Intent(this, LoginActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //notification message will get at NotificationView
        notificationIntent.putExtra("message", "This is a notification message");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}
