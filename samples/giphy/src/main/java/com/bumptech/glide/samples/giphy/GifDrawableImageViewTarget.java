package com.bumptech.glide.samples.giphy;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;

/**
 * Created by sabari on 30/3/18.
 */

public class GifDrawableImageViewTarget extends ImageViewTarget<Drawable> {

  private int mLoopCount = GifDrawable.LOOP_FOREVER;

  GifDrawableImageViewTarget(ImageView view, int loopCount) {
    super(view);
    mLoopCount = loopCount;
  }

  @Override
  protected void setResource(@Nullable Drawable resource) {
    if (resource instanceof GifDrawable) {
      ((GifDrawable) resource).setLoopCount(mLoopCount);
    }
    view.setImageDrawable(resource);
  }

}