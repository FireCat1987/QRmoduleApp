package ru.letnes.qrmodule

/**
 * Created by Shara on 24.09.2017.
 */

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import me.dm7.barcodescanner.zbar.BarcodeFormat
import java.util.*

class FormatSelectorDialogFragment : DialogFragment() {

    private var mSelectedIndices: ArrayList<Int>? = null
    private var mListener: FormatSelectorDialogListener? = null

    interface FormatSelectorDialogListener {
        fun onFormatsSaved(selectedIndices: ArrayList<Int>?)
    }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        retainInstance = true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (mSelectedIndices == null || mListener == null) {
            dismiss()
        }
        val formats = arrayOfNulls<String>(BarcodeFormat.ALL_FORMATS.size)
        val checkedIndices = BooleanArray(BarcodeFormat.ALL_FORMATS.size)
        var i = 0
        for (format in BarcodeFormat.ALL_FORMATS) {
            formats[i] = format.name
            checkedIndices[i] = mSelectedIndices!!.contains(i)
            i++
        }

        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.choose_formats)
                .setMultiChoiceItems(formats, checkedIndices,
                        { _, which, isChecked ->
                            if (isChecked) {
                                mSelectedIndices!!.add(which)
                            } else if (mSelectedIndices!!.contains(which)) {
                                mSelectedIndices!!.removeAt(mSelectedIndices!!.indexOf(which))
                            }
                        })
                .setPositiveButton(R.string.ok_button, { _, _ ->
                    if (mListener != null) {
                        mListener!!.onFormatsSaved(mSelectedIndices)
                    }
                })
                .setNegativeButton(R.string.cancel_button, { _, _ -> })

        return builder.create()
    }

    companion object {
        fun newInstance(listener: FormatSelectorDialogListener, selectedIndices: ArrayList<Int>?): FormatSelectorDialogFragment {
            var selectedIndices = selectedIndices
            val fragment = FormatSelectorDialogFragment()
            if (selectedIndices == null) {
                selectedIndices = ArrayList()
            }
            fragment.mSelectedIndices = ArrayList(selectedIndices)
            fragment.mListener = listener
            return fragment
        }
    }
}