package co.familyplay.androidprototype.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/*
  Handles the drawing functionality of the app
  Draw draw = new Draw(context, null, Color.BLACK);
  draw.changeBrushColor(Color.TRANSPARENT);

  after you have instantiate the class add it to a parent view like
  a LinearLayout or a RelativeLayout.

  parentView.addView(draw);

  to clear the canvas simple call
  draw.ClearCanvas();
*/

public class Draw extends View {

	private Bitmap _Bitmap;
	private Canvas _Canvas;
	private Path _Path;
	private Paint _BitmapPaint;
	private Paint _paint;
	private float _mX;
	private float _mY;

	public Draw(Context context, AttributeSet attr, int color) {
		super(context, attr);
		_Path = new Path();
		_BitmapPaint = new Paint(Paint.DITHER_FLAG);
		_paint = new Paint();
		_paint.setAntiAlias(true);
		_paint.setDither(true);
		_paint.setColor(color);
		_paint.setStyle(Paint.Style.STROKE);
		_paint.setStrokeJoin(Paint.Join.ROUND);
		_paint.setStrokeCap(Paint.Cap.ROUND);
        float lineThickness = 4;
        _paint.setStrokeWidth(lineThickness);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		_Bitmap = Bitmap.createBitmap(w,
				(h > 0 ? h : ((View) this.getParent()).getHeight()),
				Bitmap.Config.ARGB_8888);
		_Canvas = new Canvas(_Bitmap);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.TRANSPARENT);
		canvas.drawBitmap(_Bitmap, 0, 0, _BitmapPaint);
		canvas.drawPath(_Path, _paint);
	}

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent e) {
        super.onTouchEvent(e);
        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                TouchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                TouchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                TouchUp();
                invalidate();
                break;
        }

        return true;
    }

	private void TouchStart(float x, float y) {
		_Path.reset();
		_Path.moveTo(x, y);
		_mX = x;
		_mY = y;
	}

	private void TouchMove(float x, float y) {
		float dx = Math.abs(x - _mX);
		float dy = Math.abs(y - _mY);

        float touchTolerance = 4;
        if (dx >= touchTolerance || dy >= touchTolerance) {
			_Path.quadTo(_mX, _mY, (x + _mX) / 2, (y + _mY) / 2);
			_mX = x;
			_mY = y;
		}
	}

	private void TouchUp() {
		if (!_Path.isEmpty()) {
			_Path.lineTo(_mX, _mY);
			_Canvas.drawPath(_Path, _paint);
		} else {
			_Canvas.drawPoint(_mX, _mY, _paint);
		}

		_Path.reset();
	}

	public void ClearCanvas() {
		_Canvas.drawColor(Color.TRANSPARENT);
		invalidate();
	}

	public void changeBrushColor(int color){
		_paint.setColor(color);
	}
}
