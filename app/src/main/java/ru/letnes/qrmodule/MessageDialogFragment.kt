package ru.letnes.qrmodule

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment


/**
 * Created by Shara on 24.09.2017.
 */
class MessageDialogFragment : DialogFragment() {

    private var mTitle: String? = null
    private var mMessage: String? = null
    private var mListener: MessageDialogListener? = null

    interface MessageDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, message: String?)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(mMessage)
                .setTitle(mTitle)

        builder.setPositiveButton("OK", { _, _ ->
            if (mListener != null) {
                mListener!!.onDialogPositiveClick(this@MessageDialogFragment, mMessage)
            }
        })
        builder.setNegativeButton("ЗАНОВО", { _, _ ->
            if (mListener != null) {
                mListener!!.onDialogNegativeClick(this@MessageDialogFragment)
            }
        })

        return builder.create()
    }

    companion object {
        fun newInstance(title: String, message: String, listener: MessageDialogListener): MessageDialogFragment {
            val fragment = MessageDialogFragment()
            fragment.mTitle = title
            fragment.mMessage = message
            fragment.mListener = listener
            return fragment
        }
    }
}