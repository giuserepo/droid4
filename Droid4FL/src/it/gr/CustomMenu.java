package it.gr;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.util.Log;									

/**
 * CustomMenu class
 *
 * This is the class that manages our menu items and the popup window.
 *
 * @category   Helper
 * @author     William J Francis (w.j.francis@tx.rr.com)
 * @copyright  Enjoy!
 * @version    1.0
 */
public class CustomMenu {

	/**
	 * Some global variables.
	 */
	private ArrayList<CustomMenuItem> mMenuItems;
	private OnMenuItemSelectedListener mListener = null;
	private Context mContext = null;
	private LayoutInflater mLayoutInflater = null;
	private PopupWindow mPopupWindow = null;
	private boolean mIsShowing = false;
	private boolean mHideOnSelect = true;
	private int mRows = 0;
	private int mItemsPerLineInPortraitOrientation = 3;
	private int mItemsPerLineInLandscapeOrientation = 6;
	
	/**
	 * The interface for returning the item clicked.
	 */
	public interface OnMenuItemSelectedListener {
		public void MenuItemSelectedEvent(CustomMenuItem selection);
	}
	
	/**
	 * Use this method to determine if the menu is currently displayed to the user.
	 * @return boolean isShowing
	 */	
	public boolean isShowing() { return mIsShowing; }
	
	/**
	 * This setting controls whether or not the menu closes after an item is selected.
	 * @param boolean doHideOnSelect
	 * @return void
	 */	
	public void setHideOnSelect(boolean doHideOnSelect) { mHideOnSelect = doHideOnSelect; } 
	
	/**
	 * Use this method to decide how many of your menu items you'd like one a single line.
	 * This setting in particular applied to portrait orientation.
	 * @param int count
	 * @return void
	 */	
	public void setItemsPerLineInPortraitOrientation(int count) { mItemsPerLineInPortraitOrientation = count; }
	
	/**
	 * Use this method to decide how many of your menu items you'd like one a single line.
	 * This setting in particular applied to landscape orientation.
	 * @param int count
	 * @return void
	 */	
	public void setItemsPerLineInLandscapeOrientation(int count) { mItemsPerLineInLandscapeOrientation = count; }
	
	/**
	 * Use this method to assign your menu items. You can only call this when the menu is hidden.
	 * @param ArrayList<CustomMenuItem> items
	 * @return void
	 * @throws Exception "Menu list may not be modified while menu is displayed."
	 */	
	public synchronized void setMenuItems(ArrayList<CustomMenuItem> items) throws Exception {
		if (mIsShowing) {
			throw new Exception("Menu list may not be modified while menu is displayed.");
		}
		mMenuItems = items;
	}
	
	/**
	 * This is our constructor.  Note we require a layout inflater.  There is probably a way to
	 * grab one of these from the local context but I failed to find it.
	 * @param Context context
	 * @param OnMenuItemSelectedListener listener
	 * @param LayoutInflater lo
	 * @return void
	 */	
	public CustomMenu(Context context, OnMenuItemSelectedListener listener, LayoutInflater lo) {
		mListener = listener;
		mMenuItems = new ArrayList<CustomMenuItem>();
		mContext = context;
		mLayoutInflater = lo;
	}
	
	/**
	 * Display your menu. Not we require a view from the parent.  This is so we can get
	 * a window token.  It doesn't matter which view on the parent is passed in.
	 * @param View v
	 * @return void
	 */	
	public synchronized void show(View viewLoc) {
    	try{
			mIsShowing = true;
			boolean isLandscape = false;
			Log.v("show","1");
			int itemCount = mMenuItems.size();
			Log.v("show","12");
			if (itemCount<1) return; //no menu items to show
			Log.v("show","13");
			if (mPopupWindow != null) return; //already showing
			Log.v("show","14");
			Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			Log.v("show","15");
			if (display.getWidth() > display.getHeight()) isLandscape = true;
			Log.v("show","16");
			View mView= mLayoutInflater.inflate(R.layout.custom_menu, null);
			Log.v("show","17");
			mPopupWindow = new PopupWindow(mView,LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT, false);
			Log.v("show","18");
	        mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
	        Log.v("show","19");
	        mPopupWindow.setWidth(display.getWidth());
	        Log.v("show","20");
	        mPopupWindow.showAtLocation(viewLoc, Gravity.BOTTOM, 0, 0);
	        Log.v("show","2");
	        int divisor = mItemsPerLineInPortraitOrientation;
	        if (isLandscape) divisor = mItemsPerLineInLandscapeOrientation;
	        int remainder = 0;
	        if (itemCount < divisor) {
	        	mRows = 1;
	        	remainder = itemCount;
	        } else {
	        	mRows = (itemCount / divisor);
	        	remainder = itemCount % divisor;
	        	if (remainder != 0) mRows++;
	        }
	        TableLayout table = (TableLayout)mView.findViewById(R.id.custom_menu_table);
	        table.removeAllViews();
	        Log.v("show","2");
	        for (int i=0; i < mRows; i++) {
	        	Log.v("show","3");
	        	TableRow row = null;
	    		TextView tv = null;
	    		ImageView iv = null;
	    		//create headers
	    		row = new TableRow(mContext);
	    		row.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	    		for (int j=0; j< divisor; j++) {
	    			if (i*divisor+j >= itemCount) break;
	    			final CustomMenuItem cmi = mMenuItems.get(i*divisor+j);
	    			View itemLayout = mLayoutInflater.inflate(R.layout.custom_menu_item, null);
	    			tv = (TextView)itemLayout.findViewById(R.id.custom_menu_item_caption);
	    			tv.setText(cmi.getCaption());
	    			iv = (ImageView)itemLayout.findViewById(R.id.custom_menu_item_icon);
	    			iv.setImageResource(cmi.getImageResourceId());
	    			itemLayout.setOnClickListener( new OnClickListener() {
					   @Override
					   public void onClick(View v) {
							mListener.MenuItemSelectedEvent(cmi);
							if (mHideOnSelect) hide();
					   }
					});
	    			row.addView(itemLayout);
	    		}
	    		Log.v("show","4");
	    		table.addView(row);
	        }
    	} catch(Exception e){
    		/*
    		AlertDialog.Builder alert = new AlertDialog.Builder(this.mContext);
			alert.setTitle("CustomMenu");
			alert.setMessage("Errore:"+e.toString());
			alert.show();
			*/
			Log.v("CustomMenu",Log.getStackTraceString(new Exception()));
    		
    	}
		      
	}
	
	/**
	 * Hide your menu.
	 * @return void
	 */	
	public synchronized void hide() {
		mIsShowing = false;
		if (mPopupWindow != null) {
			mPopupWindow.dismiss();
			mPopupWindow = null;
		}
		return;
	}
}
