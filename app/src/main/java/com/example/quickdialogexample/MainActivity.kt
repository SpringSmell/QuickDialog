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
                .animStyle(R.style.dialogAnimBottom2Up)
                .blockBackKey(true)/*阻塞返回键*/
                .canceledOnTouchOutside(false)
                .gravity(Gravity.BOTTOM)
                .size(WindowManager.LayoutParams.MATCH_PARENT, (1920 * 0.4).toInt())
                .onInit { dialog, holder ->
                    Log.e("QuickDialog", "初始化完成")
                    holder.setOnClick({ view, VHService ->
                        QuickDialog.dismiss()
                    }, R.id.closeActionTv)
                }
                .onDismiss {
                    Log.e("QuickDialog", "弹窗消失")
                }
                .show{dialog, holder ->
                    holder.itemView
                }
        }
    }


}
