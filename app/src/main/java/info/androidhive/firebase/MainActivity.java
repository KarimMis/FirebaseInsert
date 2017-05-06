package info.androidhive.firebase;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import info.androidhive.firebase.CookerInsertsData.InsertCoockerInfo;
import info.androidhive.firebase.CookerInsertsData.InsertCookerDishInfo;
import info.androidhive.firebase.loginSignUpAttachments.LoginActivity;


public class MainActivity extends AppCompatActivity {


    //Drawer Variables
    private AccountHeader Header = null;
    private Drawer myDrawer = null;
    private IProfile profile;


    private EditText EditTextNewINFO;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    Toolbar toolbar;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        // Create a few sample profile
        profile = new ProfileDrawerItem().withName("Mike Penz").withEmail("mikepenz@gmail.com").withIcon(getResources().getDrawable(R.mipmap.chef));







        // Create the AccountHeader
        buildHeader(false, savedInstanceState);

        //Create the drawer

        createDrawer();



    //get current user


        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user !=null) {
                    // User is signed in.
                    String x = "";
                    x = user.getDisplayName();

                    if (x!=null)
                    {
                        Toast.makeText(MainActivity.this,  user.getDisplayName().toString(),Toast.LENGTH_LONG).show();
                    }

                }
                 else if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };


        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }


    }


    public void createDrawer(){

    myDrawer = new DrawerBuilder()
            .withActivity(this)
            .withToolbar(toolbar)
            .withAccountHeader(Header) //set the AccountHeader we created earlier for the header
            .addDrawerItems(
                    new PrimaryDrawerItem().withName("Inset yours info").withIcon(FontAwesome.Icon.faw_user_circle).withIdentifier(1),
                    new PrimaryDrawerItem().withName("Insert new dish").withIcon(FontAwesome.Icon.faw_cutlery).withIdentifier(2),
                    new PrimaryDrawerItem().withName("Show yours dishes").withIcon(FontAwesome.Icon.faw_birthday_cake).withIdentifier(3)

            )

            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {

                @Override
                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                    if (drawerItem != null) {
                        Intent intent = null;
                        if (drawerItem.getIdentifier() == 1) {
                              intent = new Intent(MainActivity.this, InsertCoockerInfo.class);
                            startActivity(intent);

                        } else if (drawerItem.getIdentifier() == 2) {
                            intent = new Intent(MainActivity.this, InsertCookerDishInfo.class);
                            startActivity(intent);
                        } else if (drawerItem.getIdentifier() == 3) {
                            Toast.makeText(MainActivity.this,"hi3",Toast.LENGTH_LONG).show();
                        }
                    }

                    return false;
                }
            })

            .build();


}


    /**
     * small helper method to reuse the logic to build the AccountHeader
     * this will be used to replace the header of the drawer with a compact/normal header
     *
     * @param compact
     * @param savedInstanceState
     */

    private void buildHeader(boolean compact, Bundle savedInstanceState) {
        // Create the AccountHeader
        Header = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withCompactStyle(compact)
                .addProfiles(
                        profile
                )

                .withSavedInstance(savedInstanceState)
                .build();
    }


    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);

        // return true so that the menu pop up is opened
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.change_email:
                createInputDialog( "Change the old E-mail");

                break;

            case R.id.change_password:
                createInputDialog( "Change the old Password");
                break;

            case R.id.sign_out:
                signOut();
                break;
        }
        return true;
    }


    public void  createInputDialog(final String title ){

        EditTextNewINFO = new EditText(this);



        new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(EditTextNewINFO)

                .setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                          if(title.equals("Change the old E-mail"))
                          {
                              progressBar.setVisibility(View.VISIBLE);
                              if (user != null && !EditTextNewINFO.getText().toString().trim().equals("")) {
                                  user.updateEmail(EditTextNewINFO.getText().toString().trim())
                                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                                              @Override
                                              public void onComplete(@NonNull Task<Void> task) {
                                                  if (task.isSuccessful()) {
                                                      Toast.makeText(MainActivity.this, "Email address is updated. Please sign in with new email id!", Toast.LENGTH_LONG).show();
                                                      signOut();
                                                      progressBar.setVisibility(View.GONE);
                                                  } else {
                                                      Toast.makeText(MainActivity.this, "Failed to update email!", Toast.LENGTH_LONG).show();

                                                      progressBar.setVisibility(View.GONE);
                                                  }
                                              }
                                          });
                              } else if (EditTextNewINFO.getText().toString().trim().equals("")) {
                                 Toast.makeText(MainActivity.this,"Enter your E-mail",Toast.LENGTH_LONG).show();
                                  progressBar.setVisibility(View.GONE);
                              }
                          }





                          else if (title.equals("Change the old Password")){




                            progressBar.setVisibility(View.VISIBLE);
                            if (user != null && !EditTextNewINFO.getText().toString().trim().equals("")) {
                                if (EditTextNewINFO.getText().toString().trim().length() < 6) {
                                    EditTextNewINFO.setError("Password too short, enter minimum 6 characters");
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    user.updatePassword(EditTextNewINFO.getText().toString().trim())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(MainActivity.this, "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                                        signOut();
                                                        progressBar.setVisibility(View.GONE);
                                                    } else {
                                                        Toast.makeText(MainActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                                        progressBar.setVisibility(View.GONE);
                                                    }
                                                }
                                            });
                                }
                            } else if (EditTextNewINFO.getText().toString().trim().equals("")) {
                                Toast.makeText(MainActivity.this,"Enter Password",Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }


                        }








                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    //sign out method
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
    }



    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (myDrawer != null && myDrawer.isDrawerOpen()) {
            myDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = myDrawer.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = Header.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }





}
