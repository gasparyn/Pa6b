package gaspar.pa6b;

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
/**
 * This is my main game activity,
 * loads the board from the game board class and asks the user to play the game
 * Game consists of picking two cards
 * 1st card is a question -can be a city or a country
 * 2nd card is the answer canbe city or country
 * a match results into a win and no match you loose points
 * keep playing until all the cards have been cleared on the screen
 * 
 * CREDITS TO MARTIN GRIEBERG for the module
 * and the Hello Android text book for the board/maze concepts
 * @author gasparobimba
 *
 */
public class MainGameActivity extends Activity {
    
	private static int ROW_COUNT = -1;
	private static int COL_COUNT = -1;
	private Context context;
	private Drawable backImage;
	private int [] [] cards;
	private Card firstPick;
	private Card secondPick;
	private static Manager manager;
	private ButtonListener buttonListener;
	private static SoundPool sounds;
	private static int match;
	private static int not_match;
	private static int won;
	private static MediaPlayer music;
	private static boolean sound = false;
	
	
	//CreateData data;
	
	private static Object lock = new Object();
	
	int turns;
	private TableLayout mainTable;
	private UpdateCardsHandler handler;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
  
        
        handler = new UpdateCardsHandler();
        backImage =  getResources().getDrawable(R.drawable.icon);
      
        buttonListener = new ButtonListener();
        
        mainTable = (TableLayout)findViewById(R.id.TableLayout03);
         
        context  = mainTable.getContext();
        
       	Spinner s = (Spinner) findViewById(R.id.Spinner01);
	    ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.type, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    s.setAdapter(adapter); 
	    
	    s.setOnItemSelectedListener(new OnItemSelectedListener(){
	        	
	    	  @Override
	    	  public void onItemSelected(android.widget.AdapterView<?> arg0, View arg1, int pos, long arg3){

	    		  ((Spinner) findViewById(R.id.Spinner01)).setSelection(0);

	    		  int x,y;

	    		  switch (pos) {
	    		  case 1:
	    			  x=4;y=4;
	    			  break;
	    		  default:
	    			  return;
	    		  }
	    		  newGame(x,y);
	    	  }
	    	  @Override
	    	  public void onNothingSelected(AdapterView<?> arg0) {
	    		  // TODO Auto-generated method stub

	    	  }

	    });
    }
   /**
    * creates a new game to be played
    * @param c -columns
    * @param r- rows
    */
    private void newGame(int c, int r) {
         manager=new Manager();
    	ROW_COUNT = r;
    	COL_COUNT = c;
    	
    	cards = new int [COL_COUNT] [ROW_COUNT];
    	
    	mainTable.removeView(findViewById(R.id.TableRow01));
    	
    	TableRow tr = ((TableRow)findViewById(R.id.TableRow03));
    	tr.removeAllViews();
    	
    	mainTable = new TableLayout(context);
    	tr.addView(mainTable);
    	
    	 for (int y = 0; y < ROW_COUNT; y++) {
    		 mainTable.addView(createRow(y));
          }
    	 
    	 firstPick=null;
    	 loadCards();
    	 
    	 turns=0;
    	 /*text to display on top*/
    	 ((TextView)findViewById(R.id.tv1)).setText("Tries: "+turns);
    	 
			
	}
/**
 * loads cards to the game
 * */
	private void loadCards(){
		try{
			
			for(int i=0; i<ROW_COUNT;i++){
				for(int j=0; j<COL_COUNT;j++){
					cards[i][j] = 0;
			}
			}
	    }
		catch (Exception e) {
			Log.e("loadCards()", e+"");
		}
		
    }
    /**
     * creates a table for the game, where cards will be placed
     * @param y no of rows the table will have
     * @return
     */
    private TableRow createRow(int y){
    	 TableRow row = new TableRow(context);
    	 row.setHorizontalGravity(Gravity.CENTER);
         
         for (int x = 0; x < COL_COUNT; x++) {
		         row.addView(createImageButton(x,y));
         }
         return row;
    }
    /**
     * image buttons that will hide our questions/answers
     * 
     * @param x
     * @param y
     * @return
     */
    private View createImageButton(int x, int y){
    	Button button = new Button(context);
    	//button.setBackgroundDrawable(backImage);
    	button.setText("             ");
    	button.setId(100*x+y);
    	button.setOnClickListener(buttonListener);
    	return button;
    }
    
    class ButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			
			synchronized (lock) {
				if(firstPick!=null && secondPick != null){
					return;
				}
				int id = v.getId();
				int x = id/100;
				int y = id%100;
				turnCard((Button)v,x,y);
			}
			
		}
/**
 * flip card when match is wrong
 * @param button
 * @param x
 * @param y
 */
		private void turnCard(Button button,int x, int y) {
			button.setText(manager.list.get(x*4+y));

			if(firstPick==null){
				firstPick = new Card(button,x,y);
			}
			else{ 
				
				if(firstPick.x == x && firstPick.y == y){
					return; //the user pressed the same card
				}
					
				secondPick = new Card(button,x,y);
				
				turns++;
				//if (turns<16)
				((TextView)findViewById(R.id.tv1)).setText("Tries: "+turns);
				sounds.play(match, 1, 1, 0, 0, 1);			
				TimerTask tt = new TimerTask() {
					
					@Override
					public void run() {
						try{
							synchronized (lock) {
							  handler.sendEmptyMessage(0);
							}
						}
						catch (Exception e) {
							Log.e("E1", e.getMessage());
						}
					}
				};
				
				  Timer t = new Timer(false);
			        t.schedule(tt, 1300);
			}
			
				
		   }
			
		}
    
    class UpdateCardsHandler extends Handler{
    	
    	@Override
    	public void handleMessage(Message msg) {
    		synchronized (lock) {
    			checkCards();
    		}
    	}
    	
    	 public void checkCards(){
    		 sounds = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
    		    match = sounds.load(context, R.raw.explosion, 1);
    	    	//if(cards[seconedCard.x][seconedCard.y] == cards[firstCard.x][firstCard.y]){
    		 	if(manager.map.get(manager.list.get(secondPick.x*4+secondPick.y)) == manager.list.get(firstPick.x*4+firstPick.y) || 
    		 			manager.map2.get(manager.list.get(secondPick.x*4+secondPick.y)) == manager.list.get(firstPick.x*4+firstPick.y)){
    		 		 sounds.play(match, 1, 1, 1, 0, 1);
    				firstPick.button.setVisibility(View.INVISIBLE);
    				secondPick.button.setVisibility(View.INVISIBLE);
    				//playSelect();
    				
    			}
    			else {
    				firstPick.button.setText("             ");
    				secondPick.button.setText("             ");
   		 		 sounds.play(match, 1, 1, 0, 0, 1);

    			}
    	    	
    	    	firstPick=null;
    			secondPick=null;
    	    }
    }
}