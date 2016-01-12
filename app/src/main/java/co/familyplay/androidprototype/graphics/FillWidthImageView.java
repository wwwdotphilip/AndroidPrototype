package co.familyplay.androidprototype.graphics;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/*This class stretches the photo to fit the devices with and scales the image as weel
You must declare this inside the layout

<co.familyplay.androidprototype.graphics.FillWidthImageView
        android:id="@+id/ivPhoto"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:scaleType="centerCrop" />
*/

public class FillWidthImageView extends ImageView {

    public FillWidthImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final Drawable d = this.getDrawable();

        if (d != null) {
            final int width = MeasureSpec.getSize(widthMeasureSpec);
            final int height = (int) Math.ceil(width * (float) d.getIntrinsicHeight() / d.getIntrinsicWidth());
            this.setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
