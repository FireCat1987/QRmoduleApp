package ru.letnes.qrmodule

import android.content.ClipData
import android.content.ClipboardManager
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.scanner_view_activity.*
import me.dm7.barcodescanner.zbar.BarcodeFormat
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView


/**
 * Created by Shara on 24.09.2017.
 */
class ScannerViewActivity : AppCompatActivity(), ZBarScannerView.ResultHandler, MessageDialogFragment.MessageDialogListener, FormatSelectorDialogFragment.FormatSelectorDialogListener, CameraSelectorDialogFragment.CameraSelectorDialogListener {
    private lateinit var mScannerView: ZBarScannerView
    private lateinit var clipboardManager: ClipboardManager
    private val FLASH_STATE = "FLASH_STATE"
    private val AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE"
    private val SELECTED_FORMATS = "SELECTED_FORMATS"
    private val CAMERA_ID = "CAMERA_ID"
    private var mFlash: Boolean = false
    private var mAutoFocus: Boolean = false
    private var mSelectedIndices: ArrayList<Int>? = null
    private var mCameraId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 0)
        }
        clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        if (savedInstanceState != null) {
            mFlash = savedInstanceState.getBoolean(FLASH_STATE, false)
            mAutoFocus = savedInstanceState.getBoolean(AUTO_FOCUS_STATE, true)
            mSelectedIndices = savedInstanceState.getIntegerArrayList(SELECTED_FORMATS)
            mCameraId = savedInstanceState.getInt(CAMERA_ID, -1)
        } else {
            mFlash = false
            mAutoFocus = true
            mSelectedIndices = null
            mCameraId = -1
        }


        setContentView(R.layout.scanner_view_activity)
        setupToolbar()

        mScannerView = ZBarScannerView(this)
        setupFormats()
        content_frame.addView(mScannerView)

    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        try {
            outState.putBoolean(FLASH_STATE, mFlash)
            outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus)
            outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices)
            outState.putInt(CAMERA_ID, mCameraId)
        } catch (e: Exception) {
            Log.e("(onSIState) Error ", e.message)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("onResume", "======================================")
        try {
            mScannerView.setResultHandler(this)
            mScannerView.startCamera(mCameraId)
            mScannerView.flash = mFlash
            mScannerView.setAutoFocus(mAutoFocus)
        } catch (e: Exception) {
            Log.e("(onResume) Error ", e.message)
        }
    }

    fun setupFormats() {
        val formats = ArrayList<BarcodeFormat>()
        if (!(mSelectedIndices != null && !mSelectedIndices!!.isEmpty())) {
            mSelectedIndices = ArrayList()
            for (i in BarcodeFormat.ALL_FORMATS.indices) {
                mSelectedIndices!!.add(i)
            }
        }
        mSelectedIndices!!.mapTo(formats) { BarcodeFormat.ALL_FORMATS[it] }
        mScannerView.setFormats(formats)
    }

    override fun onPause() {
        super.onPause()
        Log.e("onPause", "======================================")
        mScannerView.stopCamera()
        closeMessageDialog()
        closeFormatsDialog()
    }

    fun setupToolbar() {
        setSupportActionBar(toolbar)
        val ab = supportActionBar
        //ab?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (mFlash) {
            menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_on)
        } else {
            menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_off)
        }
        if (mAutoFocus) {
            menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_on)
        } else {
            menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_off)
        }
        menu.add(Menu.NONE, R.id.menu_formats, 0, R.string.formats)
        menu.add(Menu.NONE, R.id.menu_camera_selector, 0, R.string.select_camera)
        menu.add(Menu.NONE, R.id.menu_exit, 0, R.string.exit)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_flash -> {
                mFlash = !mFlash
                if (mFlash) {
                    item.setTitle(R.string.flash_on)
                } else {
                    item.setTitle(R.string.flash_off)
                }
                mScannerView.flash = mFlash
                return true
            }
            R.id.menu_auto_focus -> {
                mAutoFocus = !mAutoFocus
                if (mAutoFocus) {
                    item.setTitle(R.string.auto_focus_on)
                } else {
                    item.setTitle(R.string.auto_focus_off)
                }
                mScannerView.setAutoFocus(mAutoFocus)
                return true
            }
            R.id.menu_formats -> {
                val fragment = FormatSelectorDialogFragment.newInstance(this, mSelectedIndices)
                fragment.show(supportFragmentManager, "format_selector")
                return true
            }
            R.id.menu_camera_selector -> {
                mScannerView.stopCamera()
                val cFragment = CameraSelectorDialogFragment.newInstance(this, mCameraId)
                cFragment.show(supportFragmentManager, "camera_selector")
                return true
            }
            R.id.menu_exit -> {
                moveTaskToBack(true)
                android.os.Process.killProcess(android.os.Process.myPid())
                System.exit(1)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(1)
    }

    override fun handleResult(rawResult: Result) {
        Log.e("handler", rawResult.contents)
        Log.e("handler", rawResult.barcodeFormat.name)

        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()
        } catch (e: Exception) {
            Log.e("(handleResult) Error", e.message)
        }

        showMessageDialog(rawResult.contents)
    }

    private fun showMessageDialog(message: String) {
        val fragment = MessageDialogFragment.newInstance("Подтвердите результат", message, this)
        fragment.show(supportFragmentManager, "scan_results")
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, message: String?) {
        try {
            mScannerView.stopCamera()
            val clipData: ClipData = ClipData.newPlainText("text", message)
            clipboardManager.primaryClip = clipData
            finish()
        } catch (e: Exception) {
            Log.e("Error", e.message)
        }
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        mScannerView.resumeCameraPreview(this)
    }

    override fun onFormatsSaved(selectedIndices: java.util.ArrayList<Int>?) {
        mSelectedIndices = selectedIndices
        setupFormats()
    }

    override fun onCameraSelected(cameraId: Int) {
        mCameraId = cameraId
        mScannerView.startCamera(mCameraId)
        mScannerView.flash = mFlash
        mScannerView.setAutoFocus(mAutoFocus)
    }

    fun closeMessageDialog() {
        closeDialog("scan_results")
    }

    fun closeFormatsDialog() {
        closeDialog("format_selector")
    }

    fun closeDialog(dialogName: String) {
        val fragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag(dialogName) as? DialogFragment
        fragment?.dismiss()
    }

}