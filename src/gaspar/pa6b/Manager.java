package gaspar.pa6b;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class Manager extends Activity {
    private static SoundPool sounds;
	private static int match;
	private static MediaPlayer music;
	private static boolean sound = false;
	
	
	public Manager(){
		createMaps();
		createList();
	}
    HashMap<String,String> map,map2;
	ArrayList<String> list;
	public void createMaps(){
    	map = new HashMap<String,String>();
		map.put("Algeria", "Algiers");
		map.put("Angola","Luanda");
		map.put("Botswana","Gaborone");
		map.put("Burundi","Bujumbura");
		map.put("Chad","N'Djamena");
		map.put("Comoros","Moroni");
		map.put("Egypt","Cairo");
		map.put("Ghana","Accra");
    	
		map2 = new HashMap<String,String>();
		map2.put("Algiers", "Algeria");
		map2.put("Luanda","Angola");
		map2.put("Gaborone","Botswana");
		map2.put("Bujumbura", "Burundi");
		map2.put("N'Djamena","Chad");
		map2.put("Moroni","Comoros");
		map2.put("Cairo","Egypt");
		map2.put("Accra","Ghana");
//		map.put("Guinea","Conakry");
//		map.put("Kenya","Nairobi");
//		map.put("Lesotho","Maseru");
//		map.put("Liberia","Monrovia");
//		map.put("Libya","Tripoli");
//		map.put("Malawi","Lilongwe");
//		map.put("Mali","Bamako");
//		map.put("Mozambique","Maputo");
//		map.put("Namibia","Windhoek");
//		map.put("Niger","Niamey");
//		map.put("Nigeria","Abuja");
//		map.put("Rwanda","Kigali");
//		map.put("Senegal","Dakar");
//		map.put("Sudan","Khartoum");
//		map.put("Togo","Lome");
//		map.put("Tunisia","Tunis");
//		map.put("Uganda","Kampala");
//		map.put("Zambia","Lusaka");
//		map.put("Zimbabwe","Harare");
//		map.put("Bahamas","Nassau");
//		map.put("Belize","Belmopan");
//		map.put("Canada","Ottawa");
//		map.put("Cuba","Havana");
//		map.put("Dominica","Roseau");
//		map.put("Greenland","Nuuk");
//		map.put("Nicaragua","Managua");
//		map.put("Belarus","Minsk");
//		map.put("Belgium","Brussels");
//		map.put("Bulgaria","Sofia");
  } 
    
    public void createList(){
    	ArrayList<String> countries = new ArrayList<String>(map.keySet());
    	ArrayList<String> cities = new ArrayList<String>(map.values());
    	list = new ArrayList<String>();
    	list.addAll(countries);
    	list.addAll(cities);
	   Collections.shuffle(list);
    }
    public static void loadSound(Context context) {
	   // sound = SilhouPreferences.sound(context); // should there be sound?
	    sounds = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
	    match = sounds.load(context, R.raw.explosion, 1);
	}
    public static void playSelect() {
	    if (!sound) return; // if sound is turned off no need to continue
	    sounds.play(match, 1, 1, 0, 0, 1);
	  //  _soundPool.play(_playbackFile, 1, 1, 0, 0, 1);
	    
    }
    public static final void pauseMusic() {
        if (!sound) return;
        if (music.isPlaying()) music.pause();
    }
}