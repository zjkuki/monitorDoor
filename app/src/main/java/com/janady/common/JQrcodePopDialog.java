package com.janady.common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lkd.smartlocker.R;

public class JQrcodePopDialog extends Dialog {

    public JQrcodePopDialog(Context context) {
        super(context);
    }

    public JQrcodePopDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private Bitmap image;
        private String dialog_msg = context.getString(R.string.DLG_QR_SHARE) ;

        public Builder(Context context) {
            this.context = context;
        }
        public Builder(Context context, String Dialog_msg) {
            this.dialog_msg = Dialog_msg;
            this.context = context;
        }

        public Bitmap getImage() {
            return image;
        }

        public void setImage(Bitmap image) {
            this.image = image;
        }

        public String getDialog_msg(){return dialog_msg;}
        public void setDialog_msg(String msg){this.dialog_msg = msg; }

        public JQrcodePopDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final JQrcodePopDialog dialog = new JQrcodePopDialog(context, R.style.QRCodePopDialog);
            View layout = inflater.inflate(R.layout.jqrcode_pop_dialog, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                    , android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            dialog.setContentView(layout);
            ImageView img = (ImageView)layout.findViewById(R.id.img_qrcode);
            img.setImageBitmap(getImage());
            TextView tv_dialog_msg = (TextView)layout.findViewById(R.id.tv_qrdialog_msg);
            tv_dialog_msg.setText(getDialog_msg());
            return dialog;
        }
    }
}

