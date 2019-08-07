package com.example.quickdialogexample

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.quick.dialog.QuickDialog

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        openActionTv.setOnClickListener {
            QuickDialog.Builder(this@MainActivity, R.layout.dialog_main, R.style.dialogBottomUp)
                .setAnimStyle(R.style.dialogAnimBottom2Up)
                .setBlockBackKey(true)/*阻塞返回键*/
                .setCanceledOnTouchOutside(false)
                .setGravity(Gravity.BOTTOM)
                .setSize(WindowManager.LayoutParams.MATCH_PARENT, (1920 * 0.4).toInt())
                .setOnInitListener { dialog, holder ->
                    Log.e("QuickDialog", "初始化完成")
                    holder.setOnClickListener({ view, VHService ->
                        QuickDialog.dismiss()
                    }, R.id.closeActionTv)
                }
                .setOnDismissListener { dialog, iDialog, holder ->
                    Log.e("QuickDialog", "弹窗消失")
                }
                .show()
        }
//        closeActionTv.setOnClickListener {
//            QuickDialog.dismiss()
//        }
    }


}
