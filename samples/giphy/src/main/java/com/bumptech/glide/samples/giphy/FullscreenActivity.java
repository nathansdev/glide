package com.bumptech.glide.samples.giphy;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable.AnimationListener;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;

/**
 * An {@link android.app.Activity} for displaying full size original GIFs.
 */
public class FullscreenActivity extends Activity {
  private static final String EXTRA_RESULT_JSON = "result_json";
  private GifDrawable gifDrawable;

  public static Intent getIntent(Context context, Api.GifResult result) {
    Intent intent = new Intent(context, FullscreenActivity.class);
    intent.putExtra(EXTRA_RESULT_JSON, new Gson().toJson(result));
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.fullscreen_activity);

    String resultJson = getIntent().getStringExtra(EXTRA_RESULT_JSON);
    final Api.GifResult result = new Gson().fromJson(resultJson, Api.GifResult.class);

    ImageView gifView = findViewById(R.id.gif_preview);
    final ImageView gifPlay = findViewById(R.id.gif_play);
    gifPlay.setImageDrawable(getTintedVectorAsset(this, R.drawable.ic_gif_watermark));
    gifView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("giphy_url", result.images.original.url);
        clipboard.setPrimaryClip(clip);

        if (gifDrawable != null) {
          if (gifDrawable.isRunning()) {
            gifDrawable.stop();
          } else {
            gifDrawable.start();
          }
        }
      }
    });

    RequestBuilder<Drawable> thumbnailRequest = GlideApp.with(this)
        .load(result)
        .decode(Bitmap.class);

    GlideApp.with(this)
        .load(result.images.original.url)
        .thumbnail(thumbnailRequest)
        .listener(new RequestListener<Drawable>() {
          @Override
          public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target,
              boolean isFirstResource) {
            return false;
          }

          @Override
          public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
              DataSource dataSource, boolean isFirstResource) {
            if (resource instanceof GifDrawable) {
              gifDrawable = (GifDrawable) resource;
              gifDrawable.setOnAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStarted() {
                  Log.d("onAnimation", "Started");
                  gifPlay.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationEnded() {
                  Log.d("onAnimation", "Stopped");
                  gifPlay.setVisibility(View.VISIBLE);
                }
              });
            } else {
              gifDrawable = null;
            }
            return false;
          }
        })
        .into(new GifDrawableImageViewTarget(gifView, 3));
  }

  /**
   * @param context           UI context.
   * @param drawableVectorRes Drawable vector resource.
   * @return get tintd vector drawable
   */
  public static Drawable getTintedVectorAsset(Context context,
      @DrawableRes int drawableVectorRes) {
    VectorDrawableCompat nonWhite = VectorDrawableCompat.create(context.getResources(),
        drawableVectorRes, context.getTheme());
    Drawable white = DrawableCompat.wrap(nonWhite);
    return white;
  }
}
