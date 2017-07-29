/*
 * MIT License
 * Copyright (c) 2016 alshell7
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.projects.alshell.flippi;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.alshell.flippi.bubbles.BubbleLayout;
import com.projects.alshell.flippi.bubbles.BubblesManager;
import com.projects.alshell.flippi.bubbles.OnInitializedCallback;
import com.projects.alshell.flippi.explosionfield.ExplosionField;

import java.util.Arrays;

public class MainActivity extends Activity
{
    int selectedStyleInSpinner = 0;
    ExplosionField explosionField;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Log("SimpleFlip Characters..");
        for (int i = 0; i < SimpleFlip.length(); i++) {
            Log("Character is : " + ((char) SimpleFlip.charAt(i)));
        }
        Log("Flip test begin..");
        Log("For a : " + getFlippedOrEncircled("owaiz", true));*/

        startActivity(new Intent(MainActivity.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setFlippiFloatShowing(false);
        initializeBubblesManager();

        if(getActionBar() != null)
        {
            //getActionBar().setHomeButtonEnabled(false);
            //getActionBar().hide();
            //getActionBar().setDisplayUseLogoEnabled(false);
            getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
            getActionBar().setLogo(null);
            getActionBar().setTitle("Flippi");
        }
        explosionField = ExplosionField.attach2Window(this);
        Spinner spinnerStyle = (Spinner) findViewById(R.id.spinner);
        spinnerStyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                selectedStyleInSpinner = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                selectedStyleInSpinner = 0;
            }
        });

        findViewById(R.id.flipButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText text = (EditText) findViewById(R.id.editTextToEncode);
                String encodedString = "";
                ((ScrollView) findViewById(R.id.main_scroll_view)).smoothScrollTo(findViewById(R.id.main_scroll_view).getTop(), findViewById(R.id.main_scroll_view).getLeft());
                switch (selectedStyleInSpinner)
                {
                    case 0:
                        //Simple flip
                        encodedString = getFlippedOrEncircled(text.getText().toString(), false, getApplicationContext());
                        break;
                    case 1:
                        //Encircled
                        encodedString = getFlippedOrEncircled(text.getText().toString(), true,getApplicationContext());
                        break;
                    case 2:
                        //Parenthesized
                        encodedString = getParenthesized(text.getText().toString());
                        break;
                    case 3:
                        //Reversed
                        StringBuilder builder = new StringBuilder();
                        encodedString = builder.append(text.getText().toString()).reverse().toString();
                        break;
                }

                if (!TextUtils.isEmpty(text.getText().toString())) {
                    explosionField.explode(findViewById(R.id.imageViewBanner));
                    final View aboutAppView = getLayoutInflater().inflate(R.layout.flipped_layout_dialog, null, false);
                    final ImageButton buttonCopy = (ImageButton) aboutAppView.findViewById(R.id.buttonCopy);
                    final ImageButton buttonSend = (ImageButton) aboutAppView.findViewById(R.id.buttonSend);
                    final TextView textView = (TextView) aboutAppView.findViewById(R.id.textViewEncoded);
                    final SeekBar zoomSeekBar = (SeekBar) aboutAppView.findViewById(R.id.seekBarZoom);
                    final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setView(aboutAppView)
                            .create();

                    textView.setText(encodedString);
                    buttonCopy.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {

                            ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            manager.setText(textView.getText().toString());
                            Toast.makeText(MainActivity.this, "You can now paste this Flippi text any where you can type :)", Toast.LENGTH_LONG).show();
                            alertDialog.dismiss();
                        }
                    });
                    buttonSend.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            shareVia(textView.getText().toString(), "Send using ?", "Flippi text", getApplicationContext());
                            alertDialog.dismiss();
                        }
                    });

                    zoomSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
                        {
                            if (progress >= 10) {
                                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, progress);
                            }
                        }
                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar)
                        {

                        }
                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar)
                        {
                          /*  textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                            zoomSeekBar.setProgress(20);*/
                        }
                    });

                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            alertDialog.show();
                        }
                    }, 1000);

                    //clear back the explosion
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            resetExplosion(findViewById(R.id.imageViewBanner));
                        }
                    }, 2000);
                } else {
                    Toast.makeText(MainActivity.this, "You must enter some text to jazz up !", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public static void shareVia(String data, String title, String subject, Context context) {
        Intent shareSaveIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareSaveIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareSaveIntent.setType("text/plain");
        shareSaveIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        shareSaveIntent.putExtra(android.content.Intent.EXTRA_TEXT, data);
        context.startActivity(Intent.createChooser(shareSaveIntent, title).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static Intent getIntentForEmail(String to, String subject, String message) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        return intent;
    }

    private void resetExplosion(View root) {
        //explosionField.clear();
        if (root instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) root;
            for (int i = 0; i < parent.getChildCount(); i++) {
                resetExplosion(parent.getChildAt(i));
            }
        } else {
            root.setScaleX(1);
            root.setScaleY(1);
            root.setAlpha(1);
        }
    }

    public void Log(String message)
    {
        Log.i("Flippi", message);
    }

    public static String getFlippedOrEncircled(String toEncode, boolean encircled, Context context) {
        int index;
        String unEncoded = toEncode.toLowerCase();
        final int length = toEncode.length();
        StringBuilder styledString = new StringBuilder(length);
        String style[];
        if (encircled)
        {
            style = context.getResources().getStringArray(R.array.encircled);
            //Log("encircled : " + Arrays.toString(style));
        } else
        {
            style = context.getResources().getStringArray(R.array.simple_flip);
            //Log("simple_flip : " + Arrays.toString(style));
        }

        for (index=0 ; index< length; index++) {
            switch (unEncoded.charAt(index))
            {
                case 'a':
                    styledString.append(style[0]);
                    break;
                case 'b':
                    styledString.append(style[1]);
                    break;
                case 'c':
                    styledString.append(style[2]);
                    break;
                case 'd':
                    styledString.append(style[3]);
                    break;
                case 'e':
                    styledString.append(style[4]);
                    break;
                case 'f':
                    styledString.append(style[5]);
                    break;
                case 'g':
                    styledString.append(style[6]);
                    break;
                case 'h':
                    styledString.append(style[7]);
                    break;
                case 'i':
                    styledString.append(style[8]);
                    break;
                case 'j':
                    styledString.append(style[9]);
                    break;
                case 'k':
                    styledString.append(style[10]);
                    break;
                case 'l':
                    styledString.append(style[11]);
                    break;
                case 'm':
                    styledString.append(style[12]);
                    break;
                case 'n':
                    styledString.append(style[13]);
                    break;
                case 'o':
                    styledString.append(style[14]);
                    break;
                case 'p':
                    styledString.append(style[15]);
                    break;
                case 'q':
                    styledString.append(style[16]);
                    break;
                case 'r':
                    styledString.append(style[17]);
                    break;
                case 's':
                    styledString.append(style[18]);
                    break;
                case 't':
                    styledString.append(style[19]);
                    break;
                case 'u':
                    styledString.append(style[20]);
                    break;
                case 'v':
                    styledString.append(style[21]);
                    break;
                case 'w':
                    styledString.append(style[22]);
                    break;
                case 'x':
                    styledString.append(style[23]);
                    break;
                case 'y':
                    styledString.append(style[24]);
                    break;
                case 'z':
                    styledString.append(style[25]);
                    break;
                case ',':
                    if (!encircled){
                        styledString.append("ʻ");
                    }
                    break;
                case '"':
                    if (!encircled) {
                        styledString.append("❝");
                    }
                    break;
                case '_':
                    if (!encircled) {
                        styledString.append("‾");
                    }
                    break;
                case '?':
                    if (!encircled) {
                        styledString.append("¿");
                    }
                    break;
                case '!':
                    if (!encircled) {
                        styledString.append("i");
                    }
                    break;
                case '1':
                    if (encircled) {
                        styledString.append(style[26]);
                    }
                    break;
                case '2':
                    if (encircled) {
                        styledString.append(style[27]);
                    }
                    break;
                case '3':
                    if (encircled) {
                        styledString.append(style[28]);
                    }
                    break;
                case '4':
                    if (encircled) {
                        styledString.append(style[29]);
                    }
                    break;
                case '5':
                    if (encircled) {
                        styledString.append(style[30]);
                    }
                    break;
                case '6':
                    if (encircled) {
                        styledString.append(style[31]);
                    }
                    break;
                case '7':
                    if (encircled) {
                        styledString.append(style[32]);
                    }
                    break;
                case '8':
                    if (encircled) {
                        styledString.append(style[33]);
                    }
                    break;
                case '9':
                    if (encircled) {
                        styledString.append(style[34]);
                    }
                    break;
                case '0':
                    if (encircled) {
                        styledString.append(style[14]);
                    }
                    break;
                default:
                    styledString.append(unEncoded.charAt(index));
                    break;
            }
        }
        if (encircled){
            return styledString.toString();
        }
        return styledString.reverse().toString();
    }

    public String getParenthesized(String toEncode) {
        int index;
        String unEncoded = toEncode.toLowerCase();
        final int length = unEncoded.length();
        StringBuilder styledString = new StringBuilder(length);
        String style[] = getResources().getStringArray(R.array.paranthesized);
        Log("paranthesized : " + Arrays.toString(style));

        for (index=0 ; index< length; index++)
        {
            switch (unEncoded.charAt(index))
            {
                case 'a':
                    styledString.append(style[0]);
                    break;
                case 'b':
                    styledString.append(style[1]);
                    break;
                case 'c':
                    styledString.append(style[2]);
                    break;
                case 'd':
                    styledString.append(style[3]);
                    break;
                case 'e':
                    styledString.append(style[4]);
                    break;
                case 'f':
                    styledString.append(style[5]);
                    break;
                case 'g':
                    styledString.append(style[6]);
                    break;
                case 'h':
                    styledString.append(style[7]);
                    break;
                case 'i':
                    styledString.append(style[8]);
                    break;
                case 'j':
                    styledString.append(style[9]);
                    break;
                case 'k':
                    styledString.append(style[10]);
                    break;
                case 'l':
                    styledString.append(style[11]);
                    break;
                case 'm':
                    styledString.append(style[12]);
                    break;
                case 'n':
                    styledString.append(style[13]);
                    break;
                case 'o':
                    styledString.append(style[14]);
                    break;
                case 'p':
                    styledString.append(style[15]);
                    break;
                case 'q':
                    styledString.append(style[16]);
                    break;
                case 'r':
                    styledString.append(style[17]);
                    break;
                case 's':
                    styledString.append(style[18]);
                    break;
                case 't':
                    styledString.append(style[19]);
                    break;
                case 'u':
                    styledString.append(style[20]);
                    break;
                case 'v':
                    styledString.append(style[21]);
                    break;
                case 'w':
                    styledString.append(style[22]);
                    break;
                case 'x':
                    styledString.append(style[23]);
                    break;
                case 'y':
                    styledString.append(style[24]);
                    break;
                case 'z':
                    styledString.append(style[25]);
                    break;
                case '1':
                    styledString.append(style[26]);
                    break;
                case '2':
                    styledString.append(style[27]);
                    break;
                case '3':
                    styledString.append(style[28]);
                    break;
                case '4':
                    styledString.append(style[29]);
                    break;
                case '5':
                    styledString.append(style[30]);
                    break;
                case '6':
                    styledString.append(style[31]);
                    break;
                case '7':
                    styledString.append(style[32]);
                    break;
                case '8':
                    styledString.append(style[33]);
                    break;
                case '9':
                    styledString.append(style[34]);
                    break;
                case '0':
                    styledString.append(style[35]);
                    break;
                default:
                    styledString.append(unEncoded.charAt(index));
                    break;
            }
        }

        return styledString.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add("About").setIcon(R.mipmap.ic_about).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("Like").setIcon(R.drawable.ic_like).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add("Flippi Float").setIcon(R.drawable.ic_flippi_plain).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getTitle() == "About") {
            Intent intent = new Intent(MainActivity.this, About.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
        if (item.getTitle() == "Like") {
            View rootLikeView = getLayoutInflater().inflate(R.layout.like_layout_dialog, null, false);
            final RatingBar ratingBar = (RatingBar) rootLikeView.findViewById(R.id.ratingBar);
            final EditText feedbackText = (EditText) rootLikeView.findViewById(R.id.editTextFeedback);
            final Button submitBtn = (Button) rootLikeView.findViewById(R.id.buttonSubmit);

            final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setView(rootLikeView)
                    .show();
            submitBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    String data = "Submitted like with rating : " + ratingBar.getRating() + "\n";
                    if (TextUtils.isEmpty(feedbackText.getText().toString())){
                        data =  data.concat("Nothing in regards for feedback or suggestion.");
                    } else {
                        data = data.concat("Feedback (or suggestion) : \n" + feedbackText.getText().toString());
                    }

                    Intent intent = getIntentForEmail("owaizkhan96@gmail.com", "Flippi Feedback", data);

                    // start email activity only if this activity was not started with startActivityForResult(). Otherwise, return the results to the
                    // previous activity.
                    if( getCallingActivity() == null) {
                        startActivity(intent);
                    }
                    else {
                        setResult(RESULT_OK, intent);
                    }
                    Toast.makeText(MainActivity.this, "Thank You :)", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        }
        if (item.getTitle() == "Flippi Float") {

            addNewBubble();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        //// TODO: 27/Oct/2016 Use a background service to keep the bubble remain on the screen even if the app closes.
        //// TODO: instead of making it look that app is closed on select "No"
        new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle("Are you sure you wish to close Flippi ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                }).show();
        //super.onBackPressed();
    }
    private BubblesManager bubblesManager;
    private void initializeBubblesManager() {
        if ((!Settings.canDrawOverlays(this)) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Toast.makeText(this, "Enable the app to draw over, to add flippi float on screen", Toast.LENGTH_LONG).show();
            Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(myIntent);
        } else {
            bubblesManager = new BubblesManager.Builder(this)
                    .setTrashLayout(R.layout.bubble_trash_layout)

                    .setInitializationCallback(new OnInitializedCallback() {
                        @Override
                        public void onInitialized() {
                            addNewBubble();
                        }
                    })
                    .build();
            bubblesManager.initialize();
        }
    }


    private void addNewBubble() {

            BubbleLayout bubbleView = (BubbleLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.bubble_layout, null);
            bubbleView.setOnBubbleRemoveListener(new BubbleLayout.OnBubbleRemoveListener()
            {
                @Override
                public void onBubbleRemoved(BubbleLayout bubble)
                {
                    setFlippiFloatShowing(false);
                }
            });
            bubbleView.setOnBubbleClickListener(new BubbleLayout.OnBubbleClickListener()
            {

                @Override
                public void onBubbleClick(BubbleLayout bubble)
                {
                    final View stay_top_layout = getLayoutInflater().inflate(R.layout.stay_top_layout, null, false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext(), R.style.SplashTheme).setView(stay_top_layout);

                    final AlertDialog alertDialog = builder.create();
                    alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    alertDialog.show();

                    final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
                    animation.setInterpolator(new BounceInterpolator());
                    animation.setDuration(1000);
                    final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    final Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

                    stay_top_layout.findViewById(R.id.st_copy_button).setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {

                            alertDialog.dismiss();

                            final String text = ((EditText) stay_top_layout.findViewById(R.id.st_editText)).getText().toString();
                            final String flipped = MainActivity.getFlippedOrEncircled(text, false, getApplicationContext());
                            ((EditText) stay_top_layout.findViewById(R.id.st_editText)).setAnimation(shake);
                            if (!TextUtils.isEmpty(((EditText) stay_top_layout.findViewById(R.id.st_editText)).getText().toString())) {
                                ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                manager.setText(flipped);
                                //Animation animation= AnimationUtils.makeOutAnimation(getApplicationContext(), true);
                                stay_top_layout.setAnimation(animation);
                                stay_top_layout.animate();
                                stay_top_layout.getAnimation().setAnimationListener(new Animation.AnimationListener()
                                {
                                    @Override
                                    public void onAnimationStart(Animation animation)
                                    {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation)
                                    {
                                        alertDialog.dismiss();
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation)
                                    {

                                    }
                                });
                                //explosionField.explode(stay_top_layout.findViewById(R.id.to_burst_layout));
                                Toast toast = Toast.makeText(MainActivity.this, "You can now paste this Flippi text\n any where you can type :)", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 20);
                                toast.show();

                            } else {
                                Toast.makeText(getApplicationContext(), "You must enter some text to jazz up !", Toast.LENGTH_SHORT).show();
                                ((EditText) stay_top_layout.findViewById(R.id.st_editText)).animate();
                                vibrator.vibrate(300);
                            }
                        }
                    });
                    stay_top_layout.findViewById(R.id.st_send_button).setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            alertDialog.dismiss();

                            final String text = ((EditText) stay_top_layout.findViewById(R.id.st_editText)).getText().toString();
                            final String flipped = MainActivity.getFlippedOrEncircled(text, false, getApplicationContext());
                            if (!TextUtils.isEmpty(((EditText) stay_top_layout.findViewById(R.id.st_editText)).getText().toString())) {
                                //explosionField.explode(stay_top_layout.findViewById(R.id.to_burst_layout));
                                stay_top_layout.setAnimation(animation);
                                stay_top_layout.animate();
                                stay_top_layout.getAnimation().setAnimationListener(new Animation.AnimationListener()
                                {
                                    @Override
                                    public void onAnimationStart(Animation animation)
                                    {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation)
                                    {
                                        alertDialog.dismiss();
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation)
                                    {

                                    }
                                });
                                shareVia(flipped, "Send using ?", "Flippi text", getApplicationContext());

                            } else {
                                Toast.makeText(getApplicationContext(), "You must enter some text to jazz up !", Toast.LENGTH_SHORT).show();
                                vibrator.vibrate(300);
                            }

                        }
                    });

                    //Toast.makeText(getApplicationContext(), "Clicked !", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY));
                }
            });
            bubbleView.setShouldStickToWall(true);

            if(bubblesManager == null){
                setFlippiFloatShowing(false);
                initializeBubblesManager();
            } else {
                if (!isFlippiFloatShowing()) {
                    bubblesManager.addBubble(bubbleView, 60, 20);
                    setFlippiFloatShowing(true);
                } else {
                    Toast.makeText(this, "Flippi Float already been added to your screen", Toast.LENGTH_SHORT).show();
                }
            }
    }

    @Override
    protected void onDestroy() {
        bubblesManager.recycle();
        setFlippiFloatShowing(false);
        super.onDestroy();
    }

    public static final String KEY_IS_FLOAT_SHOWING  = "KEY_IS_FLOAT_SHOWING";

    @SuppressLint("ApplySharedPref")
    private void setFlippiFloatShowing(boolean showing)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        preferences.edit().putBoolean(KEY_IS_FLOAT_SHOWING, showing).commit();
    }

    private boolean isFlippiFloatShowing()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        return  preferences.getBoolean(KEY_IS_FLOAT_SHOWING, false);
    }
}
