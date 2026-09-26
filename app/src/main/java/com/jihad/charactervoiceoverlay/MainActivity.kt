package com.jihad.charactervoiceoverlay
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity: AppCompatActivity(){
 private lateinit var status:TextView
 override fun onCreate(b:Bundle?){super.onCreate(b);setContentView(R.layout.activity_main)
  status=findViewById(R.id.status)
  findViewById<Button>(R.id.permissionBtn).setOnClickListener{
   startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
  }
  findViewById<Button>(R.id.startBtn).setOnClickListener{
   if(Settings.canDrawOverlays(this)) startForegroundService(Intent(this,OverlayService::class.java))
   else Toast.makeText(this,"আগে Display over other apps Allow করুন",Toast.LENGTH_SHORT).show()
  }
  findViewById<Button>(R.id.adminBtn).setOnClickListener{startActivity(Intent(this,AdminActivity::class.java))}
 }
 override fun onResume(){super.onResume();status.text=if(Settings.canDrawOverlays(this))"Overlay Permission: ON ✓" else "Overlay Permission: OFF"}
}