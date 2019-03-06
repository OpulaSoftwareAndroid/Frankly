package com.opula.chatapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.opula.chatapp.R;
import com.opula.chatapp.constant.WsConstant;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class MainShowStatusActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    private  int PROGRESS_COUNT ;
    private StoriesProgressView storiesProgressView;
    private ImageView image;
    private int counter = 0;

    //    private final int[] resources = new int[]{
//            R.drawable.avatar,
//            R.drawable.avatar,
//            R.drawable.avatar,
//            R.drawable.avatar,
//            R.drawable.avatar,
//            R.drawable.avatar,
//    };
    private  String[] resources = new String[]{};

    private final long[] durations = new long[]{
            500L, 1000L, 1500L, 4000L, 5000L, 1000,
    };
    String TAG="MainShowStatusActivity";
//    ArrayList<String> arrayListStatusImageUrl;
    List<String> arrayListStatusImageUrl;
    long pressTime = 0L;
    long limit = 500L;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_show_status);
        //arrayListStatusImageUrl=new ArrayList<String>();

        Intent intent = getIntent();
//        arrayListStatusImageUrl= intent.getStringExtra(WsConstant.STATUS_IMAGE_URL);
        String strStatusImage= intent.getStringExtra(WsConstant.STATUS_IMAGE_URL);
        arrayListStatusImageUrl = new ArrayList<String>(Arrays.asList(strStatusImage.split(",")));
        Log.d(TAG,"jigar the image status list have is "+strStatusImage);
        PROGRESS_COUNT=arrayListStatusImageUrl.size();
        //        PROGRESS_COUNT=arrayListStatusImageUrl.size();

//        for(int i=0;i<arrayListStatusImageUrl.size();i++)
//        {
//
//
//        }
                resources = new String[]{
                        strStatusImage
                };
//                R.drawable.avatar,
//                R.drawable.avatar,
//                R.drawable.avatar,
//                R.drawable.avatar,
//                R.drawable.avatar,
//                R.drawable.avatar,
//        };


        storiesProgressView = (StoriesProgressView) findViewById(R.id.stories);
        storiesProgressView.setStoriesCount(PROGRESS_COUNT);
        storiesProgressView.setStoryDuration(7000L);
        // or
        // storiesProgressView.setStoriesCountWithDurations(durations);
        storiesProgressView.setStoriesListener(this);
//        storiesProgressView.startStories();
        counter = 0;
        storiesProgressView.startStories(counter);

        image = (ImageView) findViewById(R.id.image);

 //       Picasso.get().load(resources[counter]).into(image);
        Picasso.get().load(arrayListStatusImageUrl.get(counter))
         //       .placeholder(R.drawable.image_blur_place_holder)
                .into(image);

     //   image.setImageResource(resources[counter]);

        // bind reverse view
        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);

        // bind skip view
        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);
    }

    @Override
    public void onNext() {
//         image.setImageResource(resources[++counter]);
//        Picasso.get().load(resources[counter]).into(image);

        Picasso.get().load(arrayListStatusImageUrl.get(++counter))
    //            .placeholder(R.drawable.image_blur_place_holder)
                .into(image);


    }

    @Override
    public void onPrev() {
        if ((counter - 1) < 0) return;
        Picasso.get().load(arrayListStatusImageUrl.get(--counter))
          //      .placeholder(R.drawable.image_blur_place_holder)
                .into(image);
//        storiesProgressView.pause();
//        counter=counter--;
//        Picasso.get()
//                .load(arrayListStatusImageUrl.get(--counter))
//                .into(new Target() {
//                    @Override
//                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
////                        storiesProgressView.startStories(counter);
//                        image.setImageBitmap(bitmap);
//                    //    image.setTag(bitmap);
//                    }
//
//                    @Override
//                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
//
//                    }
//
//                    @Override
//                    public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                    }
//                });

//        Picasso.get().load(resources[counter]).into(image);
    }

    @Override
    public void onComplete() {

        finish();
        MainShowStatusActivity.this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        // Very important !
        storiesProgressView.destroy();
        super.onDestroy();
    }
}