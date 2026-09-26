package com.jihad.charactervoiceoverlay
import android.app.*;import android.content.*;import android.graphics.PixelFormat;import android.media.MediaPlayer;import android.os.*;import android.provider.Settings;import android.view.*;import android.widget.*;import androidx.core.app.NotificationCompat;import java.io.File

class OverlayService:Service(){
 private lateinit var wm:WindowManager;private var panel:View?=null;private var lp:WindowManager.LayoutParams?=null;private var player:MediaPlayer?=null
 private val ch="voice_panel"
 override fun onCreate(){super.onCreate();channel();startForeground(900,NotificationCompat.Builder(this,ch).setSmallIcon(android.R.drawable.ic_media_play).setContentTitle("CHARACTER VOICE").setContentText("Floating voice panel active").setOngoing(true).build());show()}
 private fun show(){if(!Settings.canDrawOverlays(this))return
  wm=getSystemService(WINDOW_SERVICE) as WindowManager;panel=LayoutInflater.from(this).inflate(R.layout.overlay,null)
  val type=if(Build.VERSION.SDK_INT>=26)WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE
  lp=WindowManager.LayoutParams(-2,-2,type,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT).apply{gravity=Gravity.TOP or Gravity.START;x=25;y=180}
  val root=panel!!.findViewById<LinearLayout>(R.id.root)
  root.setOnTouchListener(object:View.OnTouchListener{var sx=0f;var sy=0f;var ox=0;var oy=0
   override fun onTouch(v:View,e:MotionEvent):Boolean{when(e.action){MotionEvent.ACTION_DOWN->{sx=e.rawX;sy=e.rawY;ox=lp!!.x;oy=lp!!.y;return true}
   MotionEvent.ACTION_MOVE->{lp!!.x=ox+(e.rawX-sx).toInt();lp!!.y=oy+(e.rawY-sy).toInt();wm.updateViewLayout(panel,lp);return true}};return true}})
  val img=panel!!.findViewById<ImageView>(R.id.characterImage);val image=File(filesDir,"character.img");if(image.exists())img.setImageURI(Uri.fromFile(image))
  val list=panel!!.findViewById<LinearLayout>(R.id.voiceList);val dir=File(filesDir,"voices");dir.mkdirs()
  dir.listFiles()?.forEach{f->val b=Button(this);b.text=f.nameWithoutExtension;b.setOnClickListener{play(f)};list.addView(b)}
  wm.addView(panel,lp)
 }
 private fun play(f:File){player?.release();player=MediaPlayer().apply{setDataSource(f.absolutePath);prepare();start()}}
 private fun channel(){if(Build.VERSION.SDK_INT>=26)(getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(NotificationChannel(ch,"Voice Panel",NotificationManager.IMPORTANCE_LOW))}
 override fun onDestroy(){player?.release();panel?.let{try{wm.removeView(it)}catch(_:Exception){}};super.onDestroy()}
 override fun onBind(i:Intent?):IBinder?=null
}