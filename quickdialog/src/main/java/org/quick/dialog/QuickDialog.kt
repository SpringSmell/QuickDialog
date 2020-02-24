package org.quick.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import androidx.annotation.LayoutRes
import org.quick.viewHolder.ViewHolder

/**
 * @describe 快速使用自定义Dialog
 * @author ChrisZou
 * @date 2018/7/9-9:36
 * @from https://github.com/SpringSmell/quick.library
 * @email chrisSpringSmell@gmail.com
 */
open class QuickDialog private constructor() {

    private val defaultPadding = 100
    private var dialog: Dialog? = null
    private var holder: ViewHolder? = null
    lateinit var builder: Builder

    private fun setupQuickDialog(builder: Builder) = also { this.builder = builder }

    @SuppressLint("ResourceType")
    private fun createViewHolder(): ViewHolder {
        assert(builder.resId != -1 || builder.layoutView != null)
        when {
            builder.layoutView != null ->
                if (holder?.itemView != builder.layoutView)
                    holder = ViewHolder(builder.layoutView!!)

            builder.resId != -1 -> {
                if (holder == null || holder?.itemView?.tag != builder.resId || holder!!.itemView.context != builder.context) {
                    holder = ViewHolder(
                        LayoutInflater.from(builder.context).inflate(
                            builder.resId,
                            null
                        ).apply {
                            tag = builder.resId
                        })
                }
            }
        }
        return holder!!
    }

    private fun createDialog(): Dialog {
        if (isCreate()) {
            builder.run {
                dialog = Dialog(context, style).apply {
                    val isBlockBackKey = isBlockBackKey
                    setContentView(createViewHolder().itemView)
                    setOnKeyListener { dialog, keyCode, _ ->
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            if (!isBlockBackKey)
                                dialog.dismiss()
                            else
                                return@setOnKeyListener true
                        }
                        return@setOnKeyListener false
                    }

                    setOnDismissListener(onDismissListener)
                    onInitListener?.invoke(this, createViewHolder())
                }
            }
        }
        dialog?.run {
            builder.run {
                window?.run {
                    setGravity(gravity)
                    setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    setLayout(width, height)
                    setWindowAnimations(animStyle)
                    decorView.setPadding(
                        if (paddingLeft == -1) defaultPadding else paddingLeft,
                        paddingTop,
                        if (paddingRight == -1) defaultPadding else paddingRight,
                        paddingBottom
                    )
                }
                setCanceledOnTouchOutside(canceledOnTouchOutside)
            }
        }
        return dialog!!
    }

    /**
     * 是否创建Dialog
     */
    private fun isCreate() =
        when {
            builder.isRewrite ->
                true
            dialog == null ->
                true
            builder.layoutView != null && holder?.itemView != builder.layoutView ->
                true
            builder.resId != -1 && holder?.itemView?.tag != builder.resId ->
                true
            contextChange() ->
                true
            else ->
                false
        }

    /**
     * context变化
     */
    private fun contextChange() =
        dialog != null && (if (dialog!!.context is ContextThemeWrapper) (dialog!!.context as ContextThemeWrapper).baseContext else dialog!!.context) != builder.context

    /**
     * 解散当前的弹框
     */
    fun dismiss() {
        dialog?.dismiss()
    }

    fun show(onAfterListener: ((dialog: Dialog, holder: ViewHolder) -> Unit)? = null): ViewHolder {
        if (Utils.checkActivityIsRunning(builder.context as Activity)) {
            createDialog().show()
        }
        onAfterListener?.invoke(createDialog(), createViewHolder())
        return createViewHolder()
    }

    fun dip2px(value: Float): Int =
        (value * builder.context.resources.displayMetrics.density + 0.5f).toInt()

    fun resetInternal() {
        dialog = null
        holder = null
    }

    companion object {

        fun dismiss() {
            ClassHolder.INSTANCE.dismiss()
        }

        fun resetInternal() {
            ClassHolder.INSTANCE.resetInternal()
        }

        fun show() {
            ClassHolder.INSTANCE.show()
        }
    }

    private object ClassHolder {
        val INSTANCE = QuickDialog()
    }

    /**
     * @param resId 资源ID
     * @param style 主题风格
     */
    class Builder constructor(val context: Context, @LayoutRes internal var resId: Int = -1, internal var style: Int = 0) {
        internal var animStyle = -1
        internal var layoutView: View? = null
        internal var width = WindowManager.LayoutParams.MATCH_PARENT
        internal var height = WindowManager.LayoutParams.WRAP_CONTENT
        internal var gravity = Gravity.CENTER
        internal var canceledOnTouchOutside = true
        /*是否每次都重新创建dialog*/
        internal var isRewrite = false
        internal var paddingLeft = -1
        internal var paddingRight = -1
        internal var paddingTop = 0
        internal var paddingBottom = 0
        internal var isBlockBackKey = false/*屏蔽返回键*/
        /*初始化完成调用*/
        internal var onInitListener: ((dialog: Dialog, holder: ViewHolder) -> Unit)? = null
        /*弹窗消失调用*/
        internal var onDismissListener: ((dialog: DialogInterface) -> Unit)? = null

        /**
         * 动画效果
         */
        fun animStyle(animStyle: Int) = also { this.animStyle = animStyle }

        /**
         * 屏蔽返回键
         */
        fun blockBackKey(isBlockBackKey: Boolean) = also { this.isBlockBackKey = isBlockBackKey }

        fun canceledOnTouchOutside(canceledOnTouchOutside: Boolean) =
            also { this.canceledOnTouchOutside = canceledOnTouchOutside }

        fun gravity(gravity: Int) = also { this.gravity = gravity }

        fun setRewrite(isRewrite: Boolean) = also { this.isRewrite = isRewrite }

        fun setPadding(left: Int, top: Int, right: Int, bottom: Int) = also {
            this.paddingLeft = left
            this.paddingTop = top
            this.paddingRight = right
            this.paddingBottom = bottom
        }

        fun size(width: Int, height: Int) = also {
            this.width = width
            this.height = height
            if (paddingLeft == -1) paddingLeft = 0
            if (paddingRight == -1) paddingRight = 0
        }

        fun setLayoutView(view: View, style: Int = 0) = also {
            this.layoutView = view
            this.style = style
        }

        fun build() = ClassHolder.INSTANCE.setupQuickDialog(this)

        fun createDialog(): Dialog = build().createDialog()

        fun createViewHolder() = build().createViewHolder()

        fun onInit(onInitListener: (dialog: Dialog, holder: ViewHolder) -> Unit) =
            also { this.onInitListener = onInitListener }

        fun onDismiss(onDismissListener: (dialog: DialogInterface) -> Unit) =
            also { this.onDismissListener = onDismissListener }

        fun show(onAfterListener: ((dialog: Dialog, holder: ViewHolder) -> Unit)? = null): ViewHolder =
            build().show(onAfterListener)

        fun dismiss() {
            QuickDialog.dismiss()
        }

    }
}