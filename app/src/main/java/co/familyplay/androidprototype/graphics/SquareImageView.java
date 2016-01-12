package co.familyplay.androidprototype.graphics;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/*Force the image to be displayed as square shape no matter what dimension it has.

declare it inside the layout

<co.familyplay.androidprototype.graphics.SquareImageView
        android:id="@+id/picture"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scaleType="centerCrop" />
 */

public class SquareImageView extends ImageView {
    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
    }
}
