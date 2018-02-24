package ch.starthack.volvo.nitro;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {
    private ListView mainListView ;
    private PlayerAdapter listAdapter ;
    private List<Pair<String, Integer>> playerList;
    private Integer initialScore = 35;
    private Integer initialEco = 3;
    private Integer initialSafety = 1;
    private Integer initialSharing = 2;

    private Integer playerScore;
    private Integer playerEcoBoost;
    private Integer playerSharingBoost;
    private Integer playerSafetyBoost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        playerScore = sharedPref.getInt("playerScore", initialScore);
        playerEcoBoost = sharedPref.getInt("playerEcoBoost", initialEco);
        playerSharingBoost = sharedPref.getInt("playerSharingBoost", initialSharing);
        playerSafetyBoost = sharedPref.getInt("playerSafetyBoost", initialSafety);

        Intent intent = getIntent();
        Boolean won = intent.getBooleanExtra("playerWon", false);
        if (won) {
            playerScore = playerScore + 2;
            editor.putInt("playerScore", playerScore);
            editor.apply();
        }
        Integer extraEco = intent.getIntExtra("extraEcoBoost", 0);
        Integer extraSafety = intent.getIntExtra("extraSafetyBoost", 0);
        Integer extraSharing = intent.getIntExtra("extraSharingBoost", 0);
        playerEcoBoost = playerEcoBoost + extraEco;
        if (playerEcoBoost >= 0) {
            editor.putInt("playerEcoBoost", playerEcoBoost);
            editor.apply();
        }
        playerSharingBoost = playerSharingBoost + extraSharing;
        if (playerSharingBoost >= 0) {
            editor.putInt("playerSharingBoost", playerSharingBoost);
            editor.apply();
        }
        playerSafetyBoost = playerSafetyBoost + extraSafety;
        if (playerSafetyBoost != 0) {
            editor.putInt("playerSafetyBoost", playerSafetyBoost);
            editor.apply();
        }
        editor.commit();
        TextView totalPoints = (TextView) findViewById(R.id.totalPoints);
        totalPoints.setText("" + playerScore);
        TextView ecoBoostView = (TextView) findViewById(R.id.eco_boost_number);
        ecoBoostView.setText(playerEcoBoost.toString());
        TextView safetyBoostView = (TextView) findViewById(R.id.safety_boost_number);
        safetyBoostView.setText(playerSafetyBoost.toString());
        TextView sharingBoostView = (TextView) findViewById(R.id.sharing_boost_number);
        sharingBoostView.setText(playerSharingBoost.toString());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer playerNumber = ThreadLocalRandom.current().nextInt(0, playerList.size());
                Pair<String, Integer> randomPlayer = playerList.get(playerNumber);
                Intent raceIntent = new Intent(MainActivity.this, Race.class);
                raceIntent.putExtra("playerName", randomPlayer.first);
                raceIntent.putExtra("ecoBoost", playerEcoBoost);
                raceIntent.putExtra("safetyBoost", playerSafetyBoost);
                raceIntent.putExtra("sharingBoost", playerSharingBoost);
                MainActivity.this.startActivity(raceIntent);
            }
        });

        // Find the ListView resource.
        mainListView = (ListView) findViewById( R.id.mainListView );

        // Create and populate a List of planet names.
        String[] players = new String[] { "Alice", "Tom", "Bob", "Eve"};
        playerList = new ArrayList<Pair<String, Integer>>();
        for (String player : players){
            playerList.add(new Pair<String, Integer>(player, ThreadLocalRandom.current().nextInt(30, 50 + 1)));
        }

        // Create ArrayAdapter using the player list.
        listAdapter = new PlayerAdapter(this.getApplicationContext(), playerList);

        // Set the ArrayAdapter as the ListView's adapter.
        mainListView.setAdapter( listAdapter );
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                Pair<String, Integer> o = (Pair<String, Integer>) mainListView.getItemAtPosition(position);
                Intent raceIntent = new Intent(MainActivity.this, Race.class);
                raceIntent.putExtra("playerName", o.first);
                raceIntent.putExtra("ecoBoost", playerEcoBoost);
                raceIntent.putExtra("safetyBoost", playerSafetyBoost);
                raceIntent.putExtra("sharingBoost", playerSharingBoost);
                MainActivity.this.startActivity(raceIntent);
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showChallengeNotification("Donald");
            }
        }, 10000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showEcoNotification();
            }
        }, 15000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showPenaltyNotification();
            }
        }, 20000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showChallengeNotification(String name){
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, Race.class);
        intent.putExtra("playerName", name); //Optional parameters
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.ic_finish_flag_50)
                .setContentTitle("NITRO Racing")
                .setContentText(name + " is nearby. Wanna beat him?")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(33222111, mBuilder.build());
    }

    private void showEcoNotification(){
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("extraEcoBoost", 2);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.ic_leave)
                .setContentTitle("NITRO Racing")
                .setContentText("Well done! You earned 2 Eco boosts by driving ecologically!")
                .setStyle(new NotificationCompat.BigTextStyle())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(33222111, mBuilder.build());
    }

    private void showPenaltyNotification(){
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("extraSafetyBoost", -1);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.ic_local_hospital_white_24dp)
                .setContentTitle("NITRO Racing")
                .setContentText("John! You didn't respect the speed limits! You lost 1 safety boost.")
                .setStyle(new NotificationCompat.BigTextStyle())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(33222111, mBuilder.build());
    }
}
