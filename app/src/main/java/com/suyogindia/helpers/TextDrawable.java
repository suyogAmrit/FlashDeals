package com.suyogindia.helpers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import java.util.Random;

/**
 * Created by Suyog on 9/28/2016.
 */

public class TextDrawable extends Drawable {
    private TextPaint textPaint;
    private StaticLayout textLayout;
    private Paint background;
    private Paint border;
    private float textLeft,textWidth,textHeight;
    String arrBgColors[]=new String[]{"#0059b3","#00a386","#c5cefd","#f41c60","#7c8be8","#438585","#00c6d2","#f6546a"};
    public TextDrawable(String text, int textSize){
        Random random=new Random();
        int pos=random.nextInt(arrBgColors.length-1)+1;
        background=new Paint();
        if(pos>=arrBgColors.length){
            pos=pos-1;
        }
        background.setColor(Color.parseColor(arrBgColors[pos]));
        background.setAntiAlias(true);
        background.setStyle(Paint.Style.FILL);
        border=new Paint();
        border.setColor(Color.YELLOW);
        border.setAntiAlias(true);
        border.setStyle(Paint.Style.STROKE);
        border.setStrokeWidth(2);

        textPaint=new TextPaint();
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        textLayout=new StaticLayout(text.substring(0,1),textPaint,100, Layout.Alignment.ALIGN_CENTER,1.0f,1.0f,false);
        textLeft = textLayout.getLineLeft(0);
        textWidth = textLayout.getLineWidth(0);
        textHeight = textLayout.getLineBottom(0);

    }
    @Override
    public void draw(Canvas canvas) {
        Rect bound=getBounds();
        int size=bound.width();
        canvas.save();
        canvas.translate(bound.left,bound.top);
        canvas.drawCircle(size/2,size/2,size/2,background);
        RectF rect = new RectF(getBounds());
        rect.inset(2, 2);
        canvas.drawOval(rect, border);
        canvas.translate((size-textWidth)/2-textLeft,(size-textHeight)/2);
        textLayout.draw(canvas);
        canvas.restore();

    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }

}
