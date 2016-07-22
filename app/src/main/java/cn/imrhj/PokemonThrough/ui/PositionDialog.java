package cn.imrhj.PokemonThrough.ui;

import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yarolegovich.lovelydialog.LovelyCustomDialog;

import cn.imrhj.PokemonThrough.R;

/**
 *  修改位置的 dialog
 * Created by rhj on 16/7/21.
 */
public class PositionDialog extends LovelyCustomDialog implements View.OnTouchListener {

    private Button mConfirmButton;
    private ImageView mMoveImageView;
    private ImageView mBackground;

    private Point mPosition = new Point(0,0);
    private Context mContext;

    public PositionDialog(Context context) {
        super(context);
        init(context);
    }

    public PositionDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_position, null);
        mConfirmButton = (Button) contentView.findViewById(R.id.dialog_btn_confirm);
        mMoveImageView = (ImageView) contentView.findViewById(R.id.dialog_btn_move);
        mBackground = (ImageView) contentView.findViewById(R.id.dialog_bakcground);
        mBackground.setOnTouchListener(this);


        this.setView(contentView);
        this.setTopColorRes(R.color.lovelyDialogTop);
        this.setIcon(R.drawable.ic_open_with);


    }

    public PositionDialog setConfirmButton(View.OnClickListener listener) {
        mConfirmButton.setOnClickListener(listener);
        return this;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mMoveImageView.getLayoutParams();
                params.setMargins((int) motionEvent.getX(), (int) motionEvent.getY(), 0, 0);
                mMoveImageView.setLayoutParams(params);
                mPosition.set((int)motionEvent.getX(), (int)motionEvent.getY());
                break;

        }
        return false;
    }

    public Point getPosition() {
        int x = mPosition.x - mBackground.getWidth() / 2;
        int y = mPosition.y - mBackground.getHeight() / 2;
        mPosition.set(x, y);

        return mPosition;
    }

}
