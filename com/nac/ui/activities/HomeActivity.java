package com.nac.ui.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nac.R;
import com.nac.adapters.CursorPagerAdapter;
import com.nac.database.ProgramDataSource;
import com.nac.ui.dialogs.WarningDialog;
import com.nac.ui.fragments.ProgramFragment;
import com.nac.ui.views.CircularViewPager;
import com.nac.util.IabBroadcastReceiver;
import com.nac.util.IabHelper;
import com.nac.util.IabResult;
import com.nac.util.Inventory;
import com.nac.util.Purchase;

import java.util.Locale;

/**
 * Created by andreikaralkou on 1/14/14.
 */
public class HomeActivity extends FragmentActivity implements View.OnClickListener, IabBroadcastReceiver.IabBroadcastListener {
    public static final String TAG = "HomeActivity";
    private final int NEW_PROGRAM_REQUEST_CODE = 324;
    private final int REVIEW_REQUEST_CODE = 325;
    private CircularViewPager viewPager;
    private CursorPagerAdapter<ProgramFragment> pageAdapter;
    private ProgramDataSource dataSource;
    private TextView buildVersionLabel;
    private View emptyView;
    private int programIndex;
    private boolean needScrollToLastProgram;
    boolean mSubscribedToApp = false;
    boolean mAutoRenewEnabled = false;
    static final String SKU_APP_YEARLY_REAL = "yearly_subscription";
    //static final String SKU_APP_YEARLY_TEST = "annual_subscription_test";
    static final int RC_REQUEST = 16986;
    private boolean mSubscribed = false;
    private boolean appEnabled = false;
    IabHelper mHelper;
    IabBroadcastReceiver mBroadcastReceiver;

    // Track the subscription owned SKU
    String mSubscriptionSku = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuzQrzgPtExgzrwMiPvI5GSkaHOy5JCYIhXsdFt6qApEA0uY9c586RNU8XqtwSMyzDnJxLsU70bAOMo1IKwB+usoFubO2bp0h1mceTeo64CSjPmlnp6Xqqmb9SA5yRRI2O1kbvmLaIC+DdW/BixQQ0yAnqTMh10Qo3PMvmWQlHqzY/csfm8xi8dLpa47CwkJ2W4zq60x54D9CZJwqWN4cJUxtlYy4ZqScvbRnGqHuzYIasJssRPXMHfRaezXame8FdFp0Q6B30icxj850B6YVRBOz+umb5MUXY2Q+y89XqONZfflKBHs+W6ITjYScFFWO2I/o6rBfBNaVpufXdAEtSQIDAQAB";
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_home);
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }
                if (mHelper == null) return;

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                mBroadcastReceiver = new IabBroadcastReceiver(HomeActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error querying subscription. Another async operation in progress.");
                }
            }
        });
        viewPager = (CircularViewPager) findViewById(R.id.program_view_pager);
        dataSource = new ProgramDataSource(this);
        emptyView = findViewById(R.id.empty_view);
        buildVersionLabel = (TextView) findViewById(R.id.txt_build_version);
        setBuildVersion();
        findViewById(R.id.btn_configure).setOnClickListener(this);
        findViewById(R.id.btn_add_program).setOnClickListener(this);
        findViewById(R.id.btn_next_program).setOnClickListener(this);
        findViewById(R.id.btn_previous_program).setOnClickListener(this);
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_review).setOnClickListener(this);
        findViewById(R.id.btn_del_program).setOnClickListener(this);
        findViewById(R.id.subscriptionStatus).setOnClickListener(this);
    }

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // First find out which subscription is auto renewing
            Purchase subsYearly = inventory.getPurchase(SKU_APP_YEARLY_REAL);
            if (subsYearly != null && subsYearly.isAutoRenewing()) {
                mSubscriptionSku = SKU_APP_YEARLY_REAL;
                mAutoRenewEnabled = true;
            } else {
                mSubscriptionSku = "";
                mAutoRenewEnabled = false;
            }

            // The user is subscribed if either subscription exists, even if neither is auto
            // renewing
            mSubscribed = (subsYearly != null && verifyDeveloperPayload(subsYearly));
            Log.d(TAG, "User " + (mSubscribed ? "HAS" : "DOES NOT HAVE")
                    + " an annual subscription.");
            if (mSubscribed) appEnabled = true;

            updateUi();
            setWaitScreen(false);
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
            if (mHelper == null) return;
            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                setWaitScreen(false);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                setWaitScreen(false);
                return;
            }

            Log.d(TAG, "Purchase successful.");

            if (purchase.getSku().equals(SKU_APP_YEARLY_REAL)) {
                // bought the subscription
                Log.d(TAG, "Subscription purchased.");
                alert("Thank you for subscribing to NAC!");
                mSubscribed = true;
                mAutoRenewEnabled = purchase.isAutoRenewing();
                mSubscriptionSku = purchase.getSku();
                appEnabled = true;
                updateUi();
                setWaitScreen(false);
            }
        }
    };


    private void updateUi() {
        if (mSubscribed) {
            ((TextView)findViewById(R.id.subscriptionStatus)).setText("Valid subscription");
            ((TextView)findViewById(R.id.subscriptionStatus)).setTextColor(Color.GREEN);
            Button startButton = ((Button)findViewById(R.id.btn_start));
            startButton.setText("Start");
        }
        else {
            ((TextView)findViewById(R.id.subscriptionStatus)).setText("Invalid subscription. Click here to subscribe!");
            ((TextView)findViewById(R.id.subscriptionStatus)).setTextColor(Color.RED);
            Button startButton = ((Button)findViewById(R.id.btn_start));
            startButton.setText("Subscribe");
        }
    }


    private void setBuildVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String buildVersionString = String.format(Locale.US, getString(R.string.build_version_pattern), info.versionName);
            buildVersionLabel.setText(buildVersionString);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setEmptyView(boolean isTrue) {
        if (isTrue) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
        //((Button)findViewById(R.id.btn_start)).setEnabled(!isTrue & mSubscribed);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataSource.open();
        updateProgramList();
    }

    private void updateProgramList() {
        Cursor cursor = dataSource.getProgramCursor();
            pageAdapter = new CursorPagerAdapter<ProgramFragment>(
                    getSupportFragmentManager(),
                    ProgramFragment.class,
                    ProgramDataSource.PROGRAM_TABLE_SELECTION,
                    cursor
            );
            viewPager.setAdapter(pageAdapter);
//        if (programIndex >= cursor.getCount()) {
//            programIndex = cursor.getCount() - 1;
//        }
        viewPager.setCurrentItem(programIndex, false);
        setEmptyView(cursor.getCount() == 0);
        if (needScrollToLastProgram) {
            needScrollToLastProgram = false;
            if (cursor.getCount() != 0) {
                viewPager.setCurrentItem(cursor.getCount() - 1, false);
            }
        }
    }

    @Override
    protected void onPause() {
        programIndex = viewPager.getCurrentRealItem();
        dataSource.close();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }

        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_configure:
                ConfigureActivity.start(this);
                break;
            case R.id.btn_add_program:
                CreateProgramActivity.start(this, NEW_PROGRAM_REQUEST_CODE);
                break;
            case R.id.btn_next_program:
                nextProgram();
                break;
            case R.id.btn_previous_program:
                previousProgram();
                break;
            case R.id.btn_start:
                if (mSubscribed) {
                    startTestForCurrentProgram();
                    break;
                }
            case R.id.subscriptionStatus:
                Log.d(TAG, "Buy Subscription clicked.");
                if (mSubscribed) {
                    complain("No need! You're already subscribed!");
                    updateUi();
                    return;
                }

                // launch the gas purchase UI flow.
                // We will be notified of completion via mPurchaseFinishedListener
                setWaitScreen(true);
                Log.d(TAG, "Launching purchase flow for subscription.");

                /* TODO: for security, generate your payload here for verification. See the comments on
                 *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
                 *        an empty string, but on a production app you should carefully generate this. */
                String payload = "";

                try {
                    mHelper.launchPurchaseFlow(this, SKU_APP_YEARLY_REAL, RC_REQUEST,
                            mPurchaseFinishedListener, payload);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error launching purchase flow. Another async operation in progress.");
                    setWaitScreen(false);
                }
                updateUi();
                break;
            case R.id.btn_review:
                ReviewActivity.start(this, viewPager.getCurrentRealItem(), REVIEW_REQUEST_CODE);
                break;
            case R.id.btn_del_program:
                deleteSelectedProgram();
                break;
        }
    }

    private void deleteSelectedProgram() {
        int position = viewPager.getCurrentRealItem();
        final int programId = pageAdapter.getProgramIdForCursorPosition(position);
        if (programId != -1) {
            if (dataSource.isProgramWithIdHasTest(programId)) {
                new WarningDialog(this, R.string.home_warning_message, null).show();
            } else {
                dataSource.deleteProgramById(programId);
                programIndex = position;
                updateProgramList();
            }
        } else {
            Toast.makeText(this, R.string.nothing_delete, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mHelper.handleActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case NEW_PROGRAM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    needScrollToLastProgram = true;
                }
                break;
            case REVIEW_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    programIndex = data.getIntExtra("programCount", 0);
                }
                break;
        }
    }

    private void startTestForCurrentProgram() {
        Bundle programBundle = pageAdapter.getBundleForPosition(viewPager.getCurrentItem());
        if (programBundle != null) {
            TestActivity.start(this, programBundle);
        } else {
            Toast.makeText(this, "Please create a Program first by clicking on the New Button.", Toast.LENGTH_LONG).show();
        }
    }

    private void previousProgram() {
        viewPager.setCurrentItem(getItem(-1), false);
    }

    private void nextProgram() {
        viewPager.forceSetCurrentItem(getItem(+1), false);
    }

    private int getItem(int i) {
        int a = viewPager.getCurrentItem();
        i += a;
        return i;
    }

    @Override
    public void receivedBroadcast() {

    }

    void complain(String message) {
        Log.e(TAG, "**** NAC Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    // Enables or disables the "please wait" screen.
    void setWaitScreen(boolean set) {
        findViewById(R.id.screen_main).setVisibility(set ? View.GONE : View.VISIBLE);
        findViewById(R.id.main_buttons).setVisibility(set ? View.GONE : View.VISIBLE);
        findViewById(R.id.screen_wait).setVisibility(set ? View.VISIBLE : View.GONE);
    }

}
