package com.janady;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.funsdkdemo.R;

public class Dialogs {

    private static AlertDialog alert;
    private static AlertDialog.Builder builder;
    private static DialogInterface.OnCancelListener btnCancleClick;
    private static DialogInterface.OnClickListener btnOkClick;
    private static String btnCustomText = "";
    private static DialogInterface.OnClickListener btnCustomClick;

    private static DialogInterface.OnClickListener BtnOkClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Log.d("Dialogs", "你点击了确定按钮~");
            return;
        }
    };

    private static DialogInterface.OnClickListener BtnCancleClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Log.d("Dialogs", "你点击了取消按钮~");
            return;
        }
    };

    private static DialogInterface.OnClickListener BtnCustomClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Log.d("Dialogs", "你点击了"+btnCustomText+"按钮~");
            return;
        }
    };


    //错误警示
    public static void alertMessage(Context context, String title, String message){
        alertDialogBtn(context,title,message,BtnOkClick,null);
    }

    //错误警示,带取消监听
    public static void alertMessage(Context context, String title, String message, DialogInterface.OnCancelListener onCancle){
        alertDialogBtn(context,title,message,BtnOkClick, onCancle);
    }

    //单按钮Dialog（确定）
    public static void alertDialogBtn(Context context, String title, String content, DialogInterface.OnClickListener btnOk, DialogInterface.OnCancelListener onCancle){
        alert = null;
        builder = new AlertDialog.Builder(context);
        alert = builder.setIcon(R.drawable.xmjp_camera)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton("确定", btnOk)
                .setOnCancelListener(onCancle)
                .create();
        alert.show();
    }

    //两按钮Dialog（取消确定）
    public static void alertDialog2Btn(Context context, String title, String content, DialogInterface.OnClickListener btnOk, DialogInterface.OnClickListener btnCancle){
        alert = null;
        builder = new AlertDialog.Builder(context);
        alert = builder.setIcon(R.drawable.xmjp_camera)
                .setTitle(title)
                .setMessage(content)
                .setNegativeButton("取消", btnCancle)
                .setPositiveButton("确定", btnOk)
                .create();
        alert.show();
    }

    //三按钮Dialog（取消，确定，自定）
    public static void alertDialog3Btn(Context context, String title, String content, DialogInterface.OnClickListener btnOk, DialogInterface.OnClickListener btnCancle, String btnCustomerText, DialogInterface.OnClickListener btnCustomerClick ){
        alert = null;
        builder = new AlertDialog.Builder(context);
        alert = builder.setIcon(R.drawable.xmjp_camera)
                .setTitle(title)
                .setMessage(content)
                .setNegativeButton("取消", btnCancle)
                .setPositiveButton("确定", btnOk)
                .setNeutralButton(btnCustomerText, btnCustomerClick).create();             //创建AlertDialog对象
        alert.show();
    }

    //多选Dialog
    public static void alertDialogMulitSelect(Context context, String title, String[] list, String btnCustomerText, DialogInterface.OnClickListener selectedClick){
        alert = null;
        builder = new AlertDialog.Builder(context);
        alert = builder.setIcon(R.drawable.xmjp_camera)
                .setTitle(title)
                .setItems(list, selectedClick).create();
        alert.show();
    }

    //单选Dialog
    public static void alertDialogSingleSelect(Context context, String title, String[] list, String btnCustomerText, DialogInterface.OnClickListener selectedClick){
        alert = null;
        builder = new AlertDialog.Builder(context);
        alert = builder.setIcon(R.drawable.xmjp_camera)
                .setTitle(title)
                .setSingleChoiceItems(list, 0, selectedClick)
                .create();
        alert.show();
    }


    //自定义Dialog
    public static void alertDialogCustomerView(Context context, View view){
        alert = null;
        builder = new AlertDialog.Builder(context);
//        final LayoutInflater inflater = MainActivity.this.getLayoutInflater();
//        view_custom = inflater.inflate(R.layout.view_dialog_custom, null,false);
        builder.setView(view);
        builder.setCancelable(false);
        alert = builder.create();

        alert.show();
    }



}
