package com.yss1.lib7;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.content.Intent;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;


import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.jme3.app.AndroidHarness;


import com.yss1.lib_jm.AppIF;
import com.yss1.lib_jm.AndroidIF;

import java.util.logging.Level;
import java.util.logging.Logger;

import static android.app.Activity.RESULT_OK;

/*
public interface AndroidIF  {

    //сохранение параметров
    public void saveSettingInt(String k,int v);
    public int  loadSettingInt(String k,int dv);
    public void saveSettingLong(String k,long v);
    public long  loadSettingLong(String k,long dv);
    public void saveSettingBool(String k,boolean v);
    public boolean  loadSettingBool(String k,boolean dv);
    public void saveSettingStr(String k,String v);
    public String  loadSettingStr(String k,String dv);
    public void saveSettingStrSet(String k,Set<String> v);
    public Set<String>  loadSettingStrSet(String k);
    public void removeStrings(ArrayList<String> al);
    public void saveSettingsMap(Map<String,Object> M);
    public Map<String,?> readAll();
    //конец сохранения параметров

    public void displayInterstitial();
    public boolean isInterstitialLoaded();
    public void loadAd();
    public void showBanner(boolean sb);
    public boolean isBannerVisible();

    //public void setIds();
    public String  getLanguage();
    public void setScreenOFF(boolean off);
    public void writeLog(String theme);
    public void getInput(int dt,String tit, String sinit);
    public void showMessage(String t, String m);
    public void showInfo(String t);
    public void rate(String what);
    public void sendEventGA(String app,String s);
    //public String getName();
    public void mp_signIn();
    public void mp_signOut(boolean signout_google);
    public boolean mp_isSignedIn();
    public void mp_writeDB(String field, Map<String,Object> value);
    public void mp_readDB(String field);
    public void mp_connectDataReceiverFor(String field);

    //public boolean gp_isSignedIn();
    //public boolean mp_connect();
    //public void mp_disconnect();
    //public void gp_ShowAchivements();
    //public void gp_ShowLeaderboard(String id);
    //public void gp_SubmitScore(String id, int sc);
    //public void gp_UnlockAchivement(String Aid);
    //public void gp_IncrementAchivement(String Aid,int sc);

    //public void mp_SelectOpponents(boolean allowAutomatch);
    //public void mp_QuickStartGame();
    //public void mp_StartWaitingRoom();
    //public void mp_InvitationInbox();
    //public int  mp_MaxMessageLength();
//  public void mp_Send2All(byte [] data);
//  public void mp_Send2One(String id, byte [] data);
//  public void mp_Sendll(byte [] data);
    //public void mp_ReSendTo(String id, byte [] data);
    //public void mp_SetSendCounter(int val);
    //public int mp_SendTo(String id, byte [] data);
    //public void mp_endNetworkGame();

}
*/
public class JmeFragmentBase extends AndroidHarness implements
        AndroidIF
//        GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener,
//        RealTimeMessageReceivedListener,
//        RoomStatusUpdateListener,
//        RoomUpdateListener,
//        OnInvitationReceivedListener,
//        ReliableMessageSentCallback
        //AdListener
{
    //protected MobileAds mMobileAds;
    //protected AdView mAdView;
    protected String mAdViewID;
    //protected InterstitialAd mInterstitialAd;
    protected String mInterstitialAdID;


    //protected AdRequest bannerRequest;
    protected AppIF jmeapp;
    protected SharedPreferences mSettings;
    protected FirebaseAuth mAuth;
    protected FirebaseUser user;
    //protected GoogleSignInClient mGoogleSignInClient;
    protected String mClientID;
    protected String mURLdb;
    protected DatabaseReference mDatabase;
    protected long startTime;
    protected FirebaseAnalytics mFirebaseAnalytics;


    //    protected Room  mRoom;
    protected String mIncomingInvitationId;

    // are we already playing?
    boolean mPlaying = false;
    boolean isServer = false;

    //tune in concret implementation
    protected int MIN_PLAYERS = -1;


    protected boolean trakerSent;
    protected boolean AD_BANNER;

    private boolean off_screen;
    private boolean tmp_ad_loaded;

    final int ID_EDITNAME = 1;
    final int ID_VIEWTEXT = 2;
    final int ID_ANSWER = 3;


    final static int RC_SIGN_IN = 9001;
    final static int REQUEST_ACHIEVEMENTS = 9009;
    final static int REQUEST_LEADERBOARD = 9010;
    final static int RC_SELECT_PLAYERS = 10000;
    //final static int RC_LOOK_AT_MATCHES =10001;
    final static int RC_INVITATION_INBOX = 10001;
    final static int RC_WAITING_ROOM = 10002;

    private boolean mResolvingConnectionFailure = false;
    protected boolean mAutoStartSignIn = true;
    private boolean mSignInClicked = false;

    public boolean firstStart = true;

    private boolean firstScreen;

    protected boolean paused;

    public JmeFragmentBase() {
        handleExitHook = false;
        mouseEventsEnabled = false;
        joystickEventsEnabled = false;
        keyEventsEnabled = false;
        startTime = System.nanoTime();
        // mInterstitialAd = null;
        trakerSent = false;
//        GP_ACTIVATE = false;
//        GP_MULTIPLAYER = false;
        AD_BANNER = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getJmeApplication() != null) {
            jmeapp = (AppIF) getJmeApplication();
            jmeapp.setAndroidIF(this);
        } else {
            throw new IllegalArgumentException("app is null!");
        }
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        checkVersion();

    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View v = super.onCreateView(inflater, container, savedInstanceState);
//        return v;
//    }


//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity().getBaseContext());
//        mAuth = FirebaseAuth.getInstance();
//        MobileAds.initialize(getActivity().getBaseContext());
//        setIds();
//        initBanner();
//        loadAd();
//    }


    //override for set id's

    public void setIds() {

    }


    private void checkVersion() {
        try {
            PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int versionNumber = pinfo.versionCode;
            int savedVersion = loadSettingInt("VERSION", 0);
            if (versionNumber != savedVersion && savedVersion != 0) {
                if (mSettings != null) {
                    jmeapp.prepareClearOnWrongVersion();
                    Editor Ed = mSettings.edit();
                    Ed.clear();
                    Ed.putInt("VERSION", versionNumber);
                    Ed.apply();
                    jmeapp.afterClearOnWrongVersion();
                }
            }
            if (savedVersion == 0) {
                saveSettingInt("VERSION", versionNumber);
            }


        } catch (PackageManager.NameNotFoundException ex) {
            Logger.getLogger(JmeFragmentBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //region multiplayer
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = cm.getActiveNetwork();
        NetworkCapabilities networkCapabilities=cm.getNetworkCapabilities(network);
        if (networkCapabilities!=null){
            haveConnectedMobile =networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
            haveConnectedWifi =networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
        }

//        NetworkInfo[] networkInfo = cm.getAllNetworkInfo();
//        for (NetworkInfo nw : networkInfo) {
//            if (nw.getTypeName().equalsIgnoreCase("WIFI")) {
//                if (nw.isConnected()) {
//                    haveConnectedWifi = true;
//                }
//            }
//            if (nw.getTypeName().equalsIgnoreCase("MOBILE")) {
//                if (nw.isConnected()) {
//                    haveConnectedMobile = true;
//                }
//            }
//        }
        return haveConnectedWifi || haveConnectedMobile;
    }


    @Override
    public void mp_signIn() {
//        if (!haveNetworkConnection()) return;
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(mClientID)
//                .requestEmail()
//                .build();
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//
//        mp_signOut(true);
//                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void mp_signOut(boolean google_signout) {
//        if (mDatabase!=null) mDatabase=null;
//        if (google_signout) {
//            Task<Void> task = mGoogleSignInClient.signOut();
//            task.addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(Task<Void> t) {
//                    Toast.makeText(getBaseContext(), "Signed out", Toast.LENGTH_SHORT).show();
//                    // showInfo("Success logout");
//                }
//            });
//        }
//        if (mAuth!=null) mAuth.signOut();
//        user = mAuth.getCurrentUser();
    }


    @Override
    public boolean mp_isSignedIn() {
        return user != null;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RC_SIGN_IN) {
//            if (resultCode == RESULT_OK) {
//                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//                try {
//                    // Google Sign In was successful, authenticate with Firebase
//                    GoogleSignInAccount account =  task.getResult(ApiException.class);
//                    firebaseAuthWithGoogle(account.getIdToken());
//                    Toast.makeText(getBaseContext(),"Sign in as "+account.getDisplayName(),Toast.LENGTH_SHORT ).show();
//                    mDatabase = FirebaseDatabase.getInstance(mURLdb).getReference("kabs");
//                } catch (ApiException e) {
//                    // Google Sign In failed, update UI appropriately
//                    Log.w("Yss1", "Google sign in failed:" , e);
//                    showMessage("Connection failed","not connected "+e.getLocalizedMessage());
//                }
//            } else {
//                Log.d("Yss1", "Login canceled by user");
//                showMessage("NOT Connected","user cancelled");
//            }
//        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Yss1", "signInWithCredential:failure", task.getException());
                            //Snackbar.make(mBinding.mainLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                    }
                });
    }

    //region db_operations

    public void mp_writeDB(String field, Map<String, Object> value) {
        if (mDatabase == null) return;
        mDatabase.child(field).setValue(value);
    }

    public void mp_readDB(String field) {
        if (mDatabase == null) return;
        mDatabase.child(field).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    //showInfo("ERROR");
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    if (task.getResult() == null || task.getResult().getValue() == null) {
                        jmeapp.recvData(field, null);
                    } else {
                        jmeapp.recvData(field, (Map<String, Object>) task.getResult().getValue());
                    }
                    //Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            }

        });

    }

    public void mp_connectDataReceiverFor(String field) {
        if (mDatabase == null) return;
        mDatabase.child(field).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                jmeapp.recvData(field, value);
                //Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
//endregion db_operations

    //endregion multiplayer

    @Override
    public void onResume() {
        super.onResume();
        mAutoStartSignIn = loadSettingBool("AUTOLOGON", false);
        if (mAutoStartSignIn && haveNetworkConnection()) {
            if (!mp_isSignedIn()) mp_signIn();
        }
//        if (mAdView != null) {
//            mAdView.resume();
//        }
        firstScreen = true;
    }

    @Override
    public void update() {
        super.update(); //To change body of generated methods, choose Tools | Templates.
        if (firstScreen) {
            removeSplashScreen();
            firstScreen = false;
        }
    }

    @Override
    public void onPause() {
        saveSettingBool("AUTOLOGON", mAutoStartSignIn);

//        if (!mPlaying && !GP_MULTIPLAYER) {
//            gp_Disconnect();
//        }
        super.onPause();//calling loseFocus here (see in Main)
    }

    @Override
    public void onStop() {
        //если в сетевой игре, отключаемся по приходу уведомления о рассылке о выходе
        //иначе отсоединяемся прямо сдесь
//        if (mAdView != null) {
//            mAdView.pause();
//        }

//        if (gp_isSignedIn() && !mPlaying) {
//            // gp_Disconnect();
//        }
        jmeapp.appStop();
        super.onStop(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onDestroy() {
//        if (mAdView != null) {
//            mAdView.destroy();
//        }
        super.onDestroy();
        System.runFinalization();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//    }

    @Override
    public void rate(String what) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.yss1." + what));
        startActivity(intent);
    }

    @Override
    public void writeLog(String theme) {
        long tm = (System.nanoTime() - startTime);
        startTime = System.nanoTime();
        //Log.i("Yss", "("+android.os.Build.MODEL+")"+theme+" ["+String.format("%.3f", tm/1000000000.0f)+"s]");
        Log.i("Yss1", theme + " [" + String.format("%.3f", tm / 1000000000.0f) + "s]");
    }

    @Override
    public void setScreenOFF(boolean off) {
        off_screen = off;
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    if (off_screen) {
//                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//                    } else {
//                        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//                    }
//                } catch (Exception E) {
//                    Log.d("Yss", "Error in [setScreenOFF] " + E.getMessage());
//                }
//            }
//        });
    }

    @Override
    public String getLanguage() {
        return Resources.getSystem().getConfiguration().locale.getLanguage();
    }


//region AD

    @Override
    public boolean isBannerVisible() {
        return false;
        //mAdView.getVisibility()==View.VISIBLE;
    }

    @Override
    public void showBanner(boolean sb) {
//        if (mAdView == null) {
//            return;
//        }
        return;
//        if (sb) {
//            shBanner();
//        } else {
//            hideBanner();
//        }
    }

    private void shBanner() {

//        if (mAdView == null) {
//            return;
//        }
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    //showMessage(mFirebaseAnalytics.getFirebaseInstanceId(), "visible");
//                    mAdView.setVisibility(View.VISIBLE);
//                } catch (Exception e) {
//                    showMessage("AD_MANNER"+AD_BANNER,"exception");
//                }
//            }
//        });
    }

    private void hideBanner() {

//        if (mAdView == null) {
//            return;
//        }
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    mAdView.setVisibility(View.GONE);
//                } catch (Exception e) {
//                }
//            }
//        });
    }


    @Override
    public void loadAd() {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//             try {
//                    if (mInterstitialAd==null) {
//                        requestNewInterstitial();
//                    }
//                    if (mAdView != null) {
//                        AdRequest adRequest = new AdRequest.Builder().build();
//                        mAdView.loadAd(adRequest);
//                    }
//                } catch (Exception e) {
//                }
//            }
//        });
    }

    private void initBanner() {
//        if (AD_BANNER) {
//            mAdView = new AdView(getActivity().getBaseContext());
//            mAdView.setAdSize(AdSize.BANNER);
//            FrameLayout LL=new FrameLayout(this.getActivity());
//            LL.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.TOP+Gravity.RIGHT));
//
//            frameLayout.addView(LL);
//            LL.addView(mAdView);
//            mAdView.setVisibility(View.GONE);
//            mAdView.setAdUnitId(mAdViewID);
//        }
    }


    private void requestNewInterstitial() {
//        AdRequest adRequest = new AdRequest.Builder().build();
//        InterstitialAd.load(getActivity().getBaseContext(),mInterstitialAdID, adRequest, new InterstitialAdLoadCallback() {
//            @Override
//            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//                 mInterstitialAd = interstitialAd;
// //               Log.i("Yss1", "onAdLoaded");
//            }
//
//            @Override
//            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                // Handle the error
//                Log.i("Yss1", loadAdError.getMessage());
//                mInterstitialAd = null;
//            }
//        });


        //AdRequest adRequest = new AdRequest.Builder().build();
        //mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public boolean isInterstitialLoaded() {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    tmp_ad_loaded = mInterstitialAd!=null;
//                } catch (Exception e) {
//                    tmp_ad_loaded = false;
//                    Log.d("Yss", "tmp_loaded Error" + e.getMessage());
//                }
//            }
//        });
        return tmp_ad_loaded;
    }

    @Override
    public void displayInterstitial() {
//        if (mInterstitialAd != null) {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    mInterstitialAd.show(getActivity());
//                }
//            });
//
//        } else {
//            Log.d("Yss1", "The interstitial ad wasn't ready yet.");
//            loadAd();
//        }

    }
//endregion AD

//region SETTINGS SAVE_LOAD

    @Override
    public Map<String, ?> readAll() {
        return mSettings.getAll();
    }

    @Override
    public void removeStrings(ArrayList<String> al) {
        try {
            Editor Ed = mSettings.edit();
            for (String SR : al) {
                Ed.remove(SR);
            }
            Ed.apply();
        } catch (Exception E) {
            Log.d("Yss", "Error in [removeStrings] " + E.getMessage());
        }
    }

    @Override
    public void saveSettingsMap(Map<String, Object> M) {
        try {
            Editor Ed = mSettings.edit();
            for (Map.Entry entry : M.entrySet()) {
                if (entry.getValue() instanceof Long) {
                    Ed.putLong(entry.getKey().toString(), (Long) entry.getValue());
                } else if (entry.getValue() instanceof Integer) {
                    Ed.putInt(entry.getKey().toString(), (Integer) entry.getValue());
                } else if (entry.getValue() instanceof Float) {
                    Ed.putFloat(entry.getKey().toString(), (Float) entry.getValue());
                } else if (entry.getValue() instanceof Boolean) {
                    Ed.putBoolean(entry.getKey().toString(), (Boolean) entry.getValue());
                } else {
                    Ed.putString(entry.getKey().toString(), entry.getValue().toString());
                }
            }
            Ed.apply();
            // writeLog("Saved objects "+M.size());
        } catch (Exception E) {
            Log.d("Yss", "Error in [saveSettingInt] " + E.getMessage());
        }
    }

    @Override
    public void saveSettingStrSet(String key, Set<String> value) {
        try {
            Editor Ed = mSettings.edit();
            Ed.putStringSet(key, value);
            Ed.apply();
        } catch (Exception E) {
            Log.d("Yss", "Error in [saveSettingInt] " + E.getMessage());
        }
    }

    @Override
    public Set<String> loadSettingStrSet(String key) {
        return mSettings.getStringSet(key, new HashSet<String>());
    }

    @Override
    public void saveSettingStr(String key, String value) {
        try {
            Editor Ed = mSettings.edit();
            Ed.putString(key, value);
            Ed.apply();
        } catch (Exception E) {
            Log.d("Yss", "Error in [saveSettingInt] " + E.getMessage());
        }
    }

    @Override
    public String loadSettingStr(String key, String def) {

        return mSettings.getString(key, def);
    }

    @Override
    public void saveSettingInt(String key, int value) {
        try {
            Editor Ed = mSettings.edit();
            Ed.putInt(key, value);
            Ed.apply();
            //Log.d("Yss", "Saved "+mSettings.getInt(key, 1));
        } catch (Exception E) {
            Log.d("Yss", "Error in [saveSettingInt] " + E.getMessage());
        }
    }

    @Override
    public int loadSettingInt(String key, int def) {
        return mSettings.getInt(key, def);
    }

    @Override
    public void saveSettingLong(String key, long value) {
        try {
            Editor Ed = mSettings.edit();
            Ed.putLong(key, value);
            Ed.apply();
        } catch (Exception E) {
            Log.d("Yss", "Error in [saveSettingInt] " + E.getMessage());
        }
    }

    @Override
    public long loadSettingLong(String key, long def) {
        return mSettings.getLong(key, def);
    }

    @Override
    public void saveSettingBool(String key, boolean value) {
        try {
            Editor Ed = mSettings.edit();
            Ed.putBoolean(key, value);
            Ed.apply();
        } catch (Exception E) {
            Log.d("Yss", "Error in [saveSettingInt] " + E.getMessage());
        }
    }

    @Override
    public boolean loadSettingBool(String key, boolean def) {
        return mSettings.getBoolean(key, def);
    }
    //endregion of SETTINGS SAVE_LOAD

    //region Google Analytics
    @Override
    public void sendEventGA(String app, String s) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, s);
        
        bundle.putString(FirebaseAnalytics.Param.CONTENT, app + "_" + s);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LEVEL_START, bundle);
    }

    //endregion Google Analytics


    @Override
    public void getInput(int dType, String title, String sinit) {
        new EditNameDialog().init(dType, title, sinit, this).
                show(getFragmentManager(), "Inp");
    }


    @Override
    public void showMessage(String tit, String msg) {
        new MessageDialog().init(tit, msg, this).show(getFragmentManager(), "msg");
    }


    @Override
    public void showInfo(String tit) {
        user = mAuth.getCurrentUser();
        String info = "";
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                info += "ProviderId=";
                info += profile.getProviderId();
                info += " Uid=";
                info += profile.getUid();
                info += " name=";
                info += profile.getDisplayName();
                info += " email=";
                info += profile.getEmail();
            }
        } else {
            info = "User is null";
        }
        new MessageDialog().init(tit, info, this).show(getFragmentManager(), "msg");
    }

    public int getID(int idFor) {
        return 0;
    }

    // @SuppressLint("ValidFragment")


//    public class DialogNew extends DialogFragment {
//        private View rootView;
//        private String title;
//        private String message; // Do nothing by default
//        private Consumer mSuccess = (boolean b) -> {};
//        private Runnable mCancel = () -> {};
//        public void setArgs(String title, String message)
//        { Bundle args = new Bundle();
//        args.putString("title", title);
//        args.putString("message", message);
//        setArguments(args);
//        }
//        @Override public void onCreate(Bundle savedInstanceState)
//        { super.onCreate(savedInstanceState); setStyle(STYLE_NO_TITLE, 0); }
//        @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  {
//            rootView = inflater.inflate(R.layout.fragment_new_dialog, container, false); // use mSuccess.accept(boolean) when needed init();
//            setListeners(); return rootView;
//        }
//        public void setSuccess(Consumer success) { mSuccess = success; }
//        public void setCancel(Runnable cancel) { mCancel = cancel; }
//    }

    // @SuppressLint("ValidFragment")


//    @SuppressLint("ValidFragment")
//    public class AnswerDialog extends DialogFragment implements OnClickListener {
//
//        private String eText;
//        private String eTitle;
//        private View form = null;
//        private int type;
//        private TextView mViewText;
//
//        public AnswerDialog() {
//            // Empty constructor required for DialogFragment
//        }
//
//        public AnswerDialog init(int dt, String eTitle, String eText) {
//            this.eText = eText;
//            this.eTitle = eTitle;
//            type = dt;
//            return this;
//        }
//
//        @Override
//        public void onStart() {
//            super.onStart();
//            mViewText = (TextView) form.findViewById(getID(101));
//            mViewText.setText(eText);
//            getDialog().setTitle(eTitle);
//        }
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
//            form = getActivity().getLayoutInflater().inflate(getID(1), null);
//            String okBtn = "";
//            String negBtn = "";
//            switch (type) {
//                case 0:
//                    okBtn = ToolsBase.getTextBase(603);
//                    negBtn = ToolsBase.getTextBase(604);
//                    break;
//            }
//            adb.setView(form).setTitle("Ti").
//                    setPositiveButton(okBtn, this).
//                    setNegativeButton(negBtn, this);
//            return adb.create();
//        }
//
//        @Override
//        public void onClick(DialogInterface dialog, int which) {
//            switch (type) {
//                case 0:
//                    switch (which) {
//                        case Dialog.BUTTON_POSITIVE:
////                            mp_endNetworkGame();
////                            RoomConfig.Builder roomConfigBuilder = mp_makeBasicRoomConfigBuilder();
////                            roomConfigBuilder.setInvitationIdToAccept(mIncomingInvitationId);
////                            Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfigBuilder.build());
//                            break;
//                        case Dialog.BUTTON_NEGATIVE:
////                            Games.RealTimeMultiplayer.declineInvitation(mGoogleApiClient, mIncomingInvitationId);
//                            break;
//                    }
//                    break;
//                case 1:
//                    switch (which) {
//                        case Dialog.BUTTON_POSITIVE:
//                            break;
//                        case Dialog.BUTTON_NEGATIVE:
//                            break;
//                    }
//                    break;
//            }
//
//        }
//    }




/*
    //Google play

    @Override
    public void onConnected(Bundle bundle) {

        mSignInClicked=false;
        mAutoStartSignIn=true;
        if (jmeapp!=null) jmeapp.onConnected();
        if (GP_MULTIPLAYER)
        {
            Invitation inv = (bundle!=null)?(Invitation)bundle.getParcelable(Multiplayer.EXTRA_INVITATION):null;
            if (inv != null) {
                // accept invitation
                RoomConfig.Builder roomConfigBuilder = mp_makeBasicRoomConfigBuilder();
                roomConfigBuilder.setInvitationIdToAccept(inv.getInvitationId());
                Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfigBuilder.build());
                // prevent screen from sleeping during handshake
                setScreenOFF(false);
                // go to game screen
            }
            Games.Invitations.registerInvitationListener(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // displayName = "Suspended";
        if (mAutoStartSignIn) mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult cr) {
        if (mResolvingConnectionFailure) {
            // Already resolving
            mAutoStartSignIn=false;
            return;
        }

        if (mSignInClicked) {
            mSignInClicked = false;
            if (cr.hasResolution()) {
                try {
                    cr.startResolutionForResult(getActivity(), RC_SIGN_IN);
                    mResolvingConnectionFailure = true;
                } catch (IntentSender.SendIntentException e) {
                    // The intent was canceled before it was sent.  Return to the default
                    // state and attempt to connect to get an updated ConnectionResult.
                    mGoogleApiClient.connect();
                    mResolvingConnectionFailure = false;
                }
            } else {
                // not resolvable... so show an error message
                //           int errorCode = cr.getErrorCode();
                mAutoStartSignIn=false;
                mResolvingConnectionFailure = false;
            }
        }

    }


    @Override
    public boolean gp_isSignedIn() {

        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    @Override
    public void gp_Connect()
    {
        if (!GP_ACTIVATE || mGoogleApiClient==null || mGoogleApiClient.isConnected()) return;
        try
        {
            mResolvingConnectionFailure = false;
            mSignInClicked=true;
            mGoogleApiClient.connect();
        }
        catch (Exception E)
        {
            showMessage("Connection Error",E.getMessage());
        }
    }

    @Override
    public void gp_Disconnect()
    {
        mAutoStartSignIn=false;
        mp_endNetworkGame();
        if (gp_isSignedIn())  mGoogleApiClient.disconnect();
    }

    @Override
    public void gp_ShowAchivements()
    {
        if (!gp_isSignedIn())
        {
            showMessage("Error","Please connect to Google Play!");
            return;
        }
        startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient),REQUEST_ACHIEVEMENTS);
    }

    @Override
    public void gp_UnlockAchivement(String Aid)
    {
        if (!gp_isSignedIn())
        {
            showMessage("Error","Please connect to Google Play!");
            return;
        }
        Games.Achievements.unlock(mGoogleApiClient, Aid);
    }

    @Override
    public void gp_IncrementAchivement(String Aid,int sc)
    {
        if (!gp_isSignedIn())
        {
            showMessage("Error","Please connect to Google Play!");
            return;
        }
        Games.Achievements.increment(mGoogleApiClient, Aid, sc);
    }



    @Override
    public void gp_ShowLeaderboard(String lid)
    {
        if (!gp_isSignedIn())
        {
            showMessage(ToolsBase.getTextBase(52),ToolsBase.getTextBase(53));
            return;
        }
        startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,lid),REQUEST_LEADERBOARD);
    }

    @Override
    public void gp_SubmitScore(String Lid, int sc)
    {
        if (!gp_isSignedIn())
        {
            showMessage(ToolsBase.getTextBase(52),ToolsBase.getTextBase(53));
            return;
        }
        Games.Leaderboards.submitScore(mGoogleApiClient, Lid, sc);
    }
    //End of google play


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {

            mResolvingConnectionFailure = false;
            mSignInClicked=false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
            else
            {
                mAutoStartSignIn=false;
            }
        }
        if (requestCode==RC_INVITATION_INBOX)
        {
            if (resultCode != Activity.RESULT_OK) {
                // canceled
                return;
            }

            // get the selected invitation
            mp_endNetworkGame();
            Bundle extras = data.getExtras();
            Invitation invitation = extras.getParcelable(Multiplayer.EXTRA_INVITATION);
            // accept it!
            RoomConfig roomConfig = mp_makeBasicRoomConfigBuilder()
                    .setInvitationIdToAccept(invitation.getInvitationId())
                    .build();
            Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfig);

            // prevent screen from sleeping during handshake
            setScreenOFF(false);
            // go to game screen

        }
        else if (requestCode == RC_WAITING_ROOM) {
            if (resultCode == Activity.RESULT_OK) {
                // (start game)
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                // Waiting room was dismissed with the back button. The meaning of this
                // action is up to the game. You may choose to leave the room and cancel the
                // match, or do something else like minimize the waiting room and
                // continue to connect in the background.

                // in this example, we take the simple approach and just leave the room:
                mp_endNetworkGame();
            }
            else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                // player wants to leave the room.
                if (mRoom!=null) Games.RealTimeMultiplayer.leave(mGoogleApiClient, null, mRoom.getRoomId());
                mp_endNetworkGame();
            }
        } else if (requestCode == RC_SELECT_PLAYERS) {
            // Returned from 'Select players to Invite' dialog

            if (resultCode != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            // get the invitee list
            //Bundle extras = data.getExtras();
            mp_endNetworkGame();
            final ArrayList<String> invitees =
                    data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
            // get auto-match criteria
            Bundle autoMatchCriteria;

            int minAutoMatchPlayers =
                    data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
            int maxAutoMatchPlayers =
                    data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

            if (minAutoMatchPlayers > 0) {
                autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                        minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            } else {
                autoMatchCriteria = null;
            }
            // create the room and specify a variant if appropriate
            RoomConfig.Builder roomConfigBuilder = mp_makeBasicRoomConfigBuilder();
            roomConfigBuilder.addPlayersToInvite(invitees);
            if (autoMatchCriteria != null) {
                roomConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
            }
            RoomConfig roomConfig = roomConfigBuilder.build();
            try
            {
                Games.RealTimeMultiplayer.create(mGoogleApiClient, roomConfig);
            }
            catch (Exception E)
            {
                writeLog(E.getMessage()+"\nConnected="+mGoogleApiClient.isConnected());
            }
            isServer=true;
            setScreenOFF(false);

        }

    }

*/

/*
// MULTIPLAYER SUPPORT

    // create a RoomConfigBuilder that's appropriate for your implementation
    private RoomConfig.Builder mp_makeBasicRoomConfigBuilder() {
        return RoomConfig.builder(this)
                .setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(this);
    }

    @Override
    public void mp_InvitationInbox()
    {
        if (!GP_MULTIPLAYER) return;
        if (!gp_isSignedIn())
        {
            showMessage(ToolsBase.getTextBase(52),ToolsBase.getTextBase(53));
            return;
        }
        Intent intent = Games.Invitations.getInvitationInboxIntent(mGoogleApiClient);
        startActivityForResult(intent, RC_INVITATION_INBOX);
    }

    @Override
    public void mp_SelectOpponents(boolean allowAutomatch)
    {
        if (!GP_MULTIPLAYER) return;
        if (!gp_isSignedIn())
        {
            showMessage(ToolsBase.getTextBase(52),ToolsBase.getTextBase(53));
            return;
        }
        Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, MIN_PLAYERS,MIN_PLAYERS);
        //switchToScreen(R.id.screen_wait);
        startActivityForResult(intent, RC_SELECT_PLAYERS);
        //Intent intent = Games.TurnBasedMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 1, 3, allowAutomatch);
        //startActivityForResult(intent,RC_SELECT_PLAYERS);
    }

    @Override
    public void mp_QuickStartGame()
    {
        if (!GP_MULTIPLAYER) return;
        //showMessage("message","Quick start");
        if (!gp_isSignedIn())
        {
            showMessage(ToolsBase.getTextBase(52),ToolsBase.getTextBase(53));
            return;
        }
        mp_endNetworkGame();
        //min players, max players, bitmask
        Bundle autoMatchCriteria=RoomConfig.createAutoMatchCriteria(MIN_PLAYERS,MIN_PLAYERS,0);
        RoomConfig.Builder rtmConfigBuilder = this.mp_makeBasicRoomConfigBuilder();
        rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
        setScreenOFF(false);
        //switchToScreen(R.id.screen_wait);
        //keepScreenOn();
        //resetGameVars();
        //setScreenOFF(false);
        Games.RealTimeMultiplayer.create(mGoogleApiClient, rtmConfigBuilder.build());
    }

    @Override
    public void mp_StartWaitingRoom()
    {
        if (mRoom==null) return;
        //writeLog("mp_StartWaitingRoom");
        Intent intent = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, mRoom, Integer.MAX_VALUE);
        startActivityForResult(intent, RC_WAITING_ROOM);
    }


    public void mp_AcceptInvitate(Invitation ivt){
//     string2title=ToolsBase.getTextBase(50);
//     string2view=String.format(ToolsBase.getTextBase(51),ivt.getInviter().getDisplayName(),ivt.getGame().getDisplayName());
        new AnswerDialog().init(0, ToolsBase.getTextBase(50),
                String.format(ToolsBase.getTextBase(51),ivt.getInviter().getDisplayName(),
                        ivt.getGame().getDisplayName())).show(getFragmentManager(), "Invite");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    //showDialog(ID_ANSWER);
                }
                catch(Exception Ex)
                {

                }
            }
        });
    }


    //RealTimeMessageReceivedListener
    @Override
    public void onRealTimeMessageReceived(RealTimeMessage rtm) {
        jmeapp.dataReceived(rtm.getSenderParticipantId(),rtm.getMessageData());
    }
// end of RealTimeMessageReceivedListener


    //RoomStatusUpdateListener
    @Override
    public void onRoomConnecting(Room room) {
        //writeLog("onRoomConnecting");
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onRoomAutoMatching(Room room) {
        //приняли обычных начинаем автоматч
        writeLog("onRoomAutoMatching"+isServer);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> list) {
        //writeLog("onPeerInvitedToRoom");
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onPeerDeclined(Room room, List<String> list) {
        //writeLog("onPeerDeclined");
        // peer declined invitation -- see if game should be canceled
        if (!mPlaying && mp_shouldCancelGame(room)) {

            mp_endNetworkGame();
        }

    }

    @Override
    public void onPeerJoined(Room room, List<String> list) {
        //String S="onPeerJoined";
        //for(String s:list) S+=" "+s;
        //writeLog(S);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onPeerLeft(Room room, List<String> list) {
        //writeLog("onPeerLeft");
        // peer left -- see if game should be canceled
        if (!mPlaying && mp_shouldCancelGame(room)) {
            mp_endNetworkGame();
        }

        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onConnectedToRoom(Room room) {
        //writeLog("onConnectedToRoom ID="+room.getRoomId()+" mRoom="+mRoom.getRoomId());
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onDisconnectedFromRoom(Room room) {
        //когда на стадии согласования акцептнувший юзер соскакивает
        //writeLog("onDisconnectedFromRoom");
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        mp_endNetworkGame();
    }

    @Override
    public void onPeersConnected(Room room, List<String> list) {
        //String S="onPeersConnected";
        //for(String s:list) S+=" "+s;
        //writeLog(S);
        if (mPlaying) {//игра уже идет
            // add new player to an ongoing game
            //} else if (mp_shouldStartGame(room)) {
            // start game!
        }

        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onPeersDisconnected(Room room, List<String> list) {
        //writeLog("onPeersDisconnected");
        if (mPlaying) {
            // do game-specific handling of this -- remove player's avatar
            // from the screen, etc. If not enough players are left for
            // the game to go on, end the game and leave the room.
        } else if (mp_shouldCancelGame(room)) {
            // cancel the game
            mp_endNetworkGame();
        }

    }

    @Override
    public void onP2PConnected(String string) {
        //writeLog("onP2PConnected "+string);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onP2PDisconnected(String string) {
        //writeLog("onP2PDisconnected");
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
//end of RoomStatusUpdateListener

    //RoomUpdateListener
    @Override
    public void onRoomCreated(int i, Room room) {
        //writeLog("onRoomCreated id="+room.getRoomId()+" mRoom="+(mRoom!=null?mRoom.getRoomId():0));
        if (i != GamesStatusCodes.STATUS_OK) {
            // let screen go to sleep
            writeLog("onRoomCreated error="+i);
            mp_endNetworkGame();
        }
        else
        {
            mRoom=room;
            //isServer=true;
            // writeLog("onRoomCreated");
            mp_StartWaitingRoom();
        }

    }

    @Override

    public void onJoinedRoom(int i, Room room) {
        //клиент нажавший аксепт первым делом попадает сюда
        //writeLog("onJoinedRoom");
        if (i != GamesStatusCodes.STATUS_OK) {
            // let screen go to sleep
            writeLog("onJoinedRoom Error="+i);
            mp_endNetworkGame();
        }
        else
        {
            // get waiting room intent
            mRoom=room;
            //isServer=false;
            //writeLog("onJoinedRoom");
            mp_StartWaitingRoom();
        }

    }

    @Override
    public void onLeftRoom(int i, String string) {
        //writeLog("onLeftRoom");
    }

    @Override
    public void onRoomConnected(int i, Room room) {
        //когда все зацепились и можно начинать играть
        //writeLog("onRoomConnected");
        if (i != GamesStatusCodes.STATUS_OK) {
            // let screen go to sleep
            //writeLog("onRoomConnected Error="+i);
            //setScreenOFF(SettBase.screen_on);
            mp_endNetworkGame();
            return;
        }
        mRoom=room;
        mp_BeginGame();
        //начинаим игру
    }
    //end of RoomUpdateListener

    //on OnInvitationReceivedListener
    @Override
    public void onInvitationReceived(Invitation invtn) {
        mIncomingInvitationId=invtn.getInvitationId();
        mp_AcceptInvitate(invtn);//go to answer -> dismiss/accept
    }

    @Override
    public void onInvitationRemoved(String string) {
        networkFailed();
    }
    //end of OnInvitationReceivedListener

    //send message callback уведомление о вручении
    @Override
    public void onRealTimeMessageSent(int statusCode, int tokenId, String recipientParticipantId) {
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            writeLog("Error send data. Status code="+statusCode);
            jmeapp.errorSend(recipientParticipantId);
            return;
        }
        jmeapp.successSend(recipientParticipantId);
    }

    // returns whether there are enough players to start the game
    boolean mp_shouldStartGame(Room room) {
        int connectedPlayers = 0;
        for (Participant p : room.getParticipants()) {
            if (p.isConnectedToRoom()) ++connectedPlayers;
        }
        return connectedPlayers == MIN_PLAYERS;
    }

    // Returns whether the room is in a state where the game should be canceled.
    boolean mp_shouldCancelGame(Room room) {
        // TODO: Your game-specific cancellation logic here. For example, you might decide to
        // cancel the game if enough people have declined the invitation or left the room.
        // You can check a participant's status with Participant.getStatus().
        // (Also, your UI should have a Cancel button that cancels the game too)
        return true;
    }

    @Override
    public int mp_MaxMessageLength()
    {
        return com.google.android.gms.games.multiplayer.Multiplayer.MAX_RELIABLE_MESSAGE_LEN;
    }

    @Override
    public int mp_SendTo(String id, byte [] data)
    {
        if (!mPlaying) return -1;//REAL_TIME_MESSAGE_FAILED = -1
        return Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, this, data, mRoom.getRoomId(), id);
    }

    protected void mp_BeginGame() {
        if (mRoom == null) {
            return;
        }
        ArrayList<String> Pids = mRoom.getParticipantIds();
        ArrayList<UserBase> uBL = new ArrayList<>();
        UserBase ub;
        Player pl, iIS = Games.Players.getCurrentPlayer(mGoogleApiClient);
        boolean isI;

        if (Pids.size() < MIN_PLAYERS + 1 || jmeapp == null) {
            mp_endNetworkGame();
            showMessage("Net error", "Not enough players");
            return;
        } else if (Pids.size() > MIN_PLAYERS + 1) {
            //чет много народу
            while (Pids.size() > MIN_PLAYERS + 1) {
                Pids.remove(Pids.size() - 1);
            }
            if (!Pids.contains(iIS.getPlayerId()))//себя удалил
            {
                mp_endNetworkGame();
                showMessage("Net error", "Player removed.");
                return;
            }
        }

        for (String st : Pids) {//Init users
            pl = mRoom.getParticipant(st).getPlayer();
            isI = (pl != null && pl.getPlayerId().equals(iIS.getPlayerId()));
            ub = new UserBase(mRoom.getParticipant(st).getDisplayName(), st, isI);
            ub.setUtype(isI ? UserBase.UserType.MAN : UserBase.UserType.NET);
            ub.setServer(false);
            ub.setNewServer(isI && isServer);//real server
            uBL.add(ub);
        }
        Collections.sort(uBL);

        uBL.get(0).setServer(true);
        jmeapp.initUsers(uBL, uBL.get(0).getUid());

        mPlaying = true;
        jmeapp.startNetworkGame();
    }

    private void networkFailed()
    {
        mPlaying=false;
        isServer=false;
        setScreenOFF(SettBase.screen_on);
    }
    @Override
    public void mp_endNetworkGame()
    {
        if (mRoom!=null) Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoom.getRoomId());
        mRoom=null;
        networkFailed();
    }

//END OF MULTIPLAYER SUPPORT

 */
}
