package ru.letnes.qrmodule

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = "BarcodeMain"
    var barcode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 0)
        }

        button.setOnClickListener {
            val intent = Intent(this@MainActivity, ScannerViewActivity::class.java)
            startActivityForResult(intent, 2)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(data!!.getBooleanExtra("EXIT", false)) {
            Log.e("EXIT", "EXIIIIIIIIIIIIIIIIIIIIIIIIIIIIIT")
            finishActivity(2)
            finish()
        }
        if(data.getBooleanExtra("Backpress", false)) {
            barcode = data.extras.getString("BarCode")

            if (barcode.equals("")) {
                Toast.makeText(this@MainActivity, "Bar code not found", Toast.LENGTH_LONG).show()
            } else {
                bar_code_txt?.text = barcode
                Log.e("handler", barcode)
            }
        }
    }
}
