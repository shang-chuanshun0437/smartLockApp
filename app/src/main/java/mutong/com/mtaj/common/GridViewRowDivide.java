package mutong.com.mtaj.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

public class GridViewRowDivide extends GridView
{
    private int rowCount;

    public GridViewRowDivide(Context context) {
        super(context);
    }

    public GridViewRowDivide(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridViewRowDivide(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GridViewRowDivide(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        //获取列数
        int column = getNumColumns();

        //获取item总数
        int items = getChildCount();

        //计算行数
        rowCount = items % column == 0 ? (items / column) : (items / column + 1);

        //设置画笔
        Paint paint = new Paint();
        //画笔实心
        paint.setStyle(Paint.Style.STROKE);
        paint.setARGB(255,0x87,0x87,0x87);
        paint.setStrokeWidth(2.5f);

        View view0 = getChildAt(0); //第一个view
        View viewColLast = getChildAt(column - 1);//第一行最后一个view
        //View viewRowLast = getChildAt((rownum - 1) * colnum); //第一列最后一个view

        int i = 1;
        for (; i < rowCount; i++)
        {
            //画横线
            canvas.drawLine(view0.getLeft() +  Constant.MAIN_GRID_VIEW_ROW_DIVID * 2*i,view0.getBottom() * i + Constant.MAIN_GRID_VIEW_ROW_DIVID * i,
                    viewColLast.getRight() - Constant.MAIN_GRID_VIEW_ROW_DIVID * 2*i, viewColLast.getBottom() * i + Constant.MAIN_GRID_VIEW_ROW_DIVID * i,
                    paint);
        }
    }
}
