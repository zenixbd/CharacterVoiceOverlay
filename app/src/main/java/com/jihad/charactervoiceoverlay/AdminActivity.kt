package com.jihad.charactervoiceoverlay
import android.app.*
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.*
import android.widget.*
import java.io.File

class AdminActivity:Activity(){
 private lateinit var list:LinearLayout
 private val prefs by lazy{getSharedPreferences("admin",0)}
 private val imagePick=501; private val audioPick=502
 override fun onCreate(b:Bundle?){super.onCreate(b)
  if(!prefs.getBoolean("logged",false)){login();return};showAdmin()
 }
 private fun login(){
  val box=LinearLayout(this);box.orientation=LinearLayout.VERTICAL;box.setPadding(30,30,30,30)
  val u=EditText(this);u.hint="Username";val p=EditText(this);p.hint="Password";p.inputType=129
  val go=Button(this);go.text="LOGIN";box.addView(u);box.addView(p);box.addView(go);setContentView(box)
  go.setOnClickListener{if(u.text.toString()=="admin"&&p.text.toString()=="Jihad@12345"){prefs.edit().putBoolean("logged",true).apply();showAdmin()}else Toast.makeText(this,"Wrong login",Toast.LENGTH_SHORT).show()}
 }
 private fun showAdmin(){
  val root=LinearLayout(this);root.orientation=LinearLayout.VERTICAL;root.setPadding(18,18,18,18)
  val title=TextView(this);title.text="ADMIN PANEL";title.textSize=24f
  root.addView(title)
  val img=Button(this);img.text="🖼 CHARACTER IMAGE SELECT";root.addView(img)
  val audio=Button(this);audio.text="🔊 VOICE AUDIO ADD";root.addView(audio)
  val clear=Button(this);clear.text="🗑 DELETE ALL SAVED VOICES/IMAGE";root.addView(clear)
  val note=TextView(this);note.text="Selected files are copied into app storage. The floating panel reads these settings."
  note.setPadding(0,15,0,15);root.addView(note)
  list=LinearLayout(this);list.orientation=LinearLayout.VERTICAL;root.addView(list)
  img.setOnClickListener{startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply{type="image/*";addCategory(Intent.CATEGORY_OPENABLE)},imagePick)}
  audio.setOnClickListener{startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply{type="audio/*";addCategory(Intent.CATEGORY_OPENABLE)},audioPick)}
  clear.setOnClickListener{File(filesDir,"voices").deleteRecursively();File(filesDir,"character.img").delete();refresh()}
  setContentView(root);refresh()
 }
 override fun onActivityResult(r:Int,c:Int,d:Intent?){super.onActivityResult(r,c,d);if(c!=RESULT_OK||d?.data==null)return
  val uri=d.data!!
  if(r==imagePick)copy(uri,File(filesDir,"character.img"))
  if(r==audioPick){val dir=File(filesDir,"voices");dir.mkdirs();copy(uri,File(dir,(fileName(uri)?: "voice_${System.currentTimeMillis()}.mp3")))}
  refresh()
 }
 private fun copy(uri:Uri,out:File){contentResolver.openInputStream(uri)?.use{input->out.outputStream().use{input.copyTo(it)}}}
 private fun fileName(uri:Uri):String?{var n:String?=null;contentResolver.query(uri,arrayOf(OpenableColumns.DISPLAY_NAME),null,null,null)?.use{if(it.moveToFirst())n=it.getString(0)};return n}
 private fun refresh(){if(!::list.isInitialized)return;list.removeAllViews();val dir=File(filesDir,"voices");dir.mkdirs()
  dir.listFiles()?.forEach{f->val row=LinearLayout(this);row.orientation=LinearLayout.HORIZONTAL;val t=TextView(this);t.text="🎵 "+f.name;t.layoutParams=LinearLayout.LayoutParams(0,60,1f);val del=Button(this);del.text="DELETE";row.addView(t);row.addView(del);del.setOnClickListener{f.delete();refresh()};list.addView(row)}
 }
}