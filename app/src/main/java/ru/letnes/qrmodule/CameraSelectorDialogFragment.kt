package ru.letnes.qrmodule

/**
 * Created by Shara on 24.09.2017.
 */
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.support.v4.app.DialogFragment


class CameraSelectorDialogFragment : DialogFragment() {

    private var mCameraId: Int = 0
    private var mListener: CameraSelectorDialogListener? = null

    interface CameraSelectorDialogListener {
        fun onCameraSelected(cameraId: Int)
    }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        retainInstance = true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (mListener == null) {
            dismiss()
        }
        val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val numberOfCameras = manager.cameraIdList.size
        val cameraNames = arrayOfNulls<String>(numberOfCameras)
        var checkedIndex = 0

        for (i in 0 until numberOfCameras) {
            val cameraIds = manager.cameraIdList
            val characteristics = manager.getCameraCharacteristics(cameraIds[i])
            val info = characteristics.get<Int>(CameraCharacteristics.LENS_FACING)

            if (info == CameraCharacteristics.LENS_FACING_FRONT) {
                cameraNames[i] = getString(R.string.front_camera)
            } else if (info == CameraCharacteristics.LENS_FACING_BACK) {
                cameraNames[i] = getString(R.string.back_camera)
            } else {
                cameraNames[i] = "Camera ID: " + i
            }
            if (i == mCameraId) {
                checkedIndex = i
            }
        }

        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.select_camera)
                .setSingleChoiceItems(cameraNames, checkedIndex,
                        { _, which -> mCameraId = which })
                .setPositiveButton(R.string.ok_button, { _, _ ->
                    if (mListener != null) {
                        mListener!!.onCameraSelected(mCameraId)
                    }
                })
                .setNegativeButton(R.string.cancel_button, { _, _ -> })
        return builder.create()
    }

    companion object {
        fun newInstance(listener: CameraSelectorDialogListener, cameraId: Int): CameraSelectorDialogFragment {
            val fragment = CameraSelectorDialogFragment()
            fragment.mCameraId = cameraId
            fragment.mListener = listener
            return fragment
        }
    }
}