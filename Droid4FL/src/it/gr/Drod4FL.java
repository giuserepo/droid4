package it.gr;

import it.gr.CustomMenu.OnMenuItemSelectedListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;

public class Drod4FL extends Activity implements OnClickListener,OnMenuItemSelectedListener {
    private static final String TAG = "DBRoulette";
    final static private String APP_KEY = "fty3o40dthxbgmk";
    final static private String APP_SECRET = "004yht81q51xvni";
    final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
    final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";
    DropboxAPI<AndroidAuthSession> mApi;
    private boolean mLoggedIn; 
	private dbMgr myDbMgr;
	private OdsParser op;
	Spinner spinner;
	ListView list;
	LazyAdapter lzAdapter;
	LazyAdapterODS lzAdapterOds;
	private CustomMenu mMenu;
	public static final int MENU_1 = 1;
	public static final int MENU_2 = 2;
	public static final int MENU_3 = 3;
	
	public static final int MENU_ITEM_1 = 1;
	public static final int MENU_ITEM_2 = 2;
	public static final int MENU_ITEM_3 = 3;
	public static final int MENU_ITEM_4 = 4;
	private CustomMenu mMenuFattura;
	public static final int MENU_ITEM_FATTURA_1 = 5;
	public static final int MENU_ITEM_FATTURA_2 = 6;
	public static final int MENU_ITEM_FATTURA_3 = 7;
	private CustomMenu mMenuEdit;
	public static final int MENU_ITEM_EDIT_1 = 8;
	public static final int MENU_ITEM_EDIT_2 = 9;
	public static final int MENU_ITEM_EDIT_3 = 10;	
	private int intLastInvoice; 
	private Globals g;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);


        setContentView(R.layout.main);
        //initializzo il menu principale 
        mMenu = new CustomMenu(this, this, getLayoutInflater());
        mMenu.setHideOnSelect(true);
        mMenu.setItemsPerLineInPortraitOrientation(4);
        mMenu.setItemsPerLineInLandscapeOrientation(8);
        //initializzo il menu fattura 
        mMenuFattura = new CustomMenu(this, this, getLayoutInflater());
        mMenuFattura.setHideOnSelect(true);
        mMenuFattura.setItemsPerLineInPortraitOrientation(3);
        mMenuFattura.setItemsPerLineInLandscapeOrientation(6);    
        //initializzo il menu edit 
        //mMenuEdit = new CustomMenu(this, this, getLayoutInflater());
        //mMenuEdit.setHideOnSelect(true);
        //mMenuEdit.setItemsPerLineInPortraitOrientation(3);
        //mMenuEdit.setItemsPerLineInLandscapeOrientation(6);           
        
        //load the menu items principale
        loadMenuItems();
        //load the menu items fattura
        loadMenuItemsFattura();
      //load the menu items principale
        //loadMenuItemsEdit();
        
        Log.v("onCreate", "1");       
    	
    	AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);
        
        checkAppKeySetup(); 
        
       
        new Handler().postDelayed(new Runnable() {
            public void run() {
            	 mMenu.show(findViewById(R.id.any_old_widget));
            	 //Imposto una variabile globale che mi ritorna la view attiva
	             g = Globals.getInstance();
	             g.setData(MENU_1);
            }
        }, 100);
    	
    }

    private void dropboxLink(){
	    mApi.getSession().startAuthentication(Drod4FL.this);
	}
    
    private void dropboxSync() {
		final ProgressDialog progressDialog;
		progressDialog = ProgressDialog.show(this, "", "Sincronizzazione in corso...");
		final Boolean fileExists = false;
		final FileOutputStream outputStream = null;
		
		new Thread() {

			public void run() {

				try {
					FileOutputStream outputStream = null;
					Boolean fileExists = false;
					FileOutputStream outputStreamODS = null;
					FileOutputStream outputStreamXSL = null;
					FileOutputStream outputStreamTLP = null;
					Decompress unzipLib= null;
					File fileODS = new File("/mnt/sdcard/gr/gestione economica_2013.ods");
					outputStreamODS = new FileOutputStream(fileODS);
					File fXSL = new File("/mnt/sdcard/gr/ods/content.xslt");
					outputStreamXSL = new FileOutputStream(fXSL);
					File fTPL = new File("/mnt/sdcard/gr/ods/tplfattura.html");
					outputStreamTLP = new FileOutputStream(fTPL);
					//DropboxFileInfo info = mApi.getFile("/some-file.txt", null, outputStream, null);
					//Log.i("DbExampleLog", "The file's rev is: " + info.getMetadata().rev);
					mApi.getFile("/gestione economica_2013.ods", null, outputStreamODS, null);
					mApi.getFile("/xsl/content.xslt", null, outputStreamXSL, null);
					mApi.getFile("/xsl/tplfattura.html", null, outputStreamTLP, null);
					
					unzipLib=new Decompress("/mnt/sdcard/gr/gestione economica_2013.ods","/mnt/sdcard/gr/ods/");
					unzipLib.unzip();
					fileExists = true;
				} catch (DropboxException e) {
					Log.e("dropboxSync","Errore di dropbox:"+ e.toString());
				} catch (FileNotFoundException e) {
					Log.e("dropboxSync", e.toString());
				} finally {
					if (outputStream != null) {
						try {
						outputStream.close();
						} catch (IOException e) {}
					}
				}
				/*
				if (fileExists == true){
					progressDialog.setMessage("Sincronizzazione terminata correttamente...");
				} else {
					progressDialog.setMessage("Sincronizzazione NON terminata correttamente...");
				}
				*/
				progressDialog.dismiss();
			}

			}.start();

    
    }
    
    private void unZipODS() {
    	setContentView(R.layout.list_fatture);

    	Boolean fileExists = false; // create a boolean var to record if the file exists on dropbox
		// Get file.
		FileOutputStream outputStreamODS = null;
		FileOutputStream outputStreamXSL = null;
		Decompress unzipLib= null;
		try {
			File fXSL = new File("/mnt/sdcard/gr/ods/content.xslt");
			//Log.v("unZipODS", "1");
			File fXML = new File("/mnt/sdcard/gr/ods/content.xml");
			
			//Log.v("unZipODS", "2");
			op=new OdsParser();
			//Log.v("unZipODS", "3");
			//String strXmlRes=op.parseODS(new FileInputStream(fXML),new FileInputStream(fXSL));
			ArrayList<HashMap<String, String>> fattList = op.parseODS(new FileInputStream(fXML),new FileInputStream(fXSL));
			//Log.v("unZipODS", "4:" + fattList.isEmpty());

	        list=(ListView)findViewById(R.id.list_fatture);
	        //spinner = (Spinner) findViewById(R.id.spinner_fatture);
	        
	        lzAdapterOds=new LazyAdapterODS(this, fattList);
	        
	        list.setAdapter(lzAdapterOds);
	        
	        list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					//Toast toast=Toast.makeText(getApplicationContext(),"Selezionato:"+Integer.toString(position),Toast.LENGTH_SHORT);
					//toast.show();
					
					setContentView(R.layout.fattura);							
					loadDataFatturaODS(position);
	            	 //Imposto una variabile globale che mi ritorna la view attiva
		             g = Globals.getInstance();
		             g.setData(MENU_3);
				}
			});	
			
			fileExists = true;
		 } catch (FileNotFoundException e) {
			Log.e("unZipODS", "File not found.");
		 } finally {
		}

    }
    private void createListFatture() {
        setContentView(R.layout.list_fatture);
  /*      
        TextView txtHello=(TextView)findViewById(R.id.any_old_widget);
        txtHello.setVisibility(TextView.INVISIBLE);
    */    
        ListView listFatture = (ListView)findViewById(R.id.contentlist);
        
       
        myDbMgr =new dbMgr(getApplicationContext(),this);
        myDbMgr.open();  //apriamo il db
        
        ArrayList<HashMap<String, String>> fattList = myDbMgr.getArrLabels();
        list=(ListView)findViewById(R.id.list_fatture);
        //spinner = (Spinner) findViewById(R.id.spinner_fatture);
        
        lzAdapter=new LazyAdapter(this, fattList);
        list.setAdapter(lzAdapter);
        
        list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//Toast toast=Toast.makeText(getApplicationContext(),"Selezionato:"+Integer.toString(position),Toast.LENGTH_SHORT);
				//toast.show();
				
				setContentView(R.layout.fattura);							
				loadDataFattura(position);
			}
		});	
        
        myDbMgr.close();
 
            
    }
    private void loadDataFatturaODS(int intPos) {
     try{
    	intLastInvoice=intPos;
    	List<String> lstLabels = op.getArrLabels(intPos);
		//Toast toast=Toast.makeText(getApplicationContext(),lstLabels.get(3),Toast.LENGTH_SHORT);
		//toast.show();       
        final TableLayout table = (TableLayout) findViewById(R.id.tabfattura);
        table.setColumnStretchable(1, true);
        
        TextView intestazione = (TextView) findViewById(R.id.intestazione); 
        intestazione.setText("Fattura n."+lstLabels.get(0)+"/"+lstLabels.get(1)+" del "+lstLabels.get(2));
        TextView azienda = (TextView) findViewById(R.id.azienda); 
        azienda.setText(lstLabels.get(3)+", "+lstLabels.get(4)+", P.I. "+lstLabels.get(5));

        TextView descrizione = (TextView) findViewById(R.id.descrizione); 
        descrizione.setText("Descrizione e natura prestazioni: "+lstLabels.get(6));
        //descrizione.setText("Descrizione e natura prestazioni: ");

        TextView onorari = (TextView) findViewById(R.id.onorari); 
        onorari.setText("Onorari: "+lstLabels.get(7));
        TextView inarcassa = (TextView) findViewById(R.id.inarcassa); 
        inarcassa.setText("Inarcassa (4%): "+lstLabels.get(8));
        TextView imponibile = (TextView) findViewById(R.id.imponibile); 
        imponibile.setText("Imponibile: "+lstLabels.get(9));
        TextView iva = (TextView) findViewById(R.id.iva); 
        iva.setText("IVA (21%): "+lstLabels.get(10));

        TextView totale_fattura = (TextView) findViewById(R.id.totale_fattura); 
        totale_fattura.setText("Totale fattura: "+lstLabels.get(11));
        TextView ra = (TextView) findViewById(R.id.ra); 
        Float fRA=Float.parseFloat(lstLabels.get(7).replace(",", "."))*Float.parseFloat("0.2");
        String strRA = Float.toString(fRA);
        ra.setText("R/A (20%): "+strRA.replace(".", ","));
        TextView totale_pagare = (TextView) findViewById(R.id.totale_pagare); 
        String strTot = Float.toString(Float.parseFloat(lstLabels.get(11).replace(",", "."))-fRA);
        totale_pagare.setText("Totale da pagare: "+strTot.replace(".", ","));
     } catch (Exception e){
    	 Log.v("loadDataFatturaODS", e.getMessage());
     }
        
        
    }

    private void loadDataFattura(int intPos) {
        ArrayList<String> lstLabels = myDbMgr.getListLabelsFromPos(intPos);
		//Toast toast=Toast.makeText(getApplicationContext(),lstLabels.get(3),Toast.LENGTH_SHORT);
		//toast.show();       
        final TableLayout table = (TableLayout) findViewById(R.id.tabfattura);
        table.setColumnStretchable(1, true);
        
        TextView intestazione = (TextView) findViewById(R.id.intestazione); 
        intestazione.setText("Fattura n."+lstLabels.get(1)+"/"+lstLabels.get(2)+" del "+lstLabels.get(3));
        TextView azienda = (TextView) findViewById(R.id.azienda); 
        azienda.setText(lstLabels.get(4)+", "+lstLabels.get(5)+", P.I. "+lstLabels.get(6));

        TextView descrizione = (TextView) findViewById(R.id.descrizione); 
        descrizione.setText("Descrizione e natura prestazioni: "+lstLabels.get(7));
        //descrizione.setText("Descrizione e natura prestazioni: ");

        TextView onorari = (TextView) findViewById(R.id.onorari); 
        onorari.setText("Onorari: "+lstLabels.get(8));
        TextView inarcassa = (TextView) findViewById(R.id.inarcassa); 
        inarcassa.setText("Inarcassa (4%): "+lstLabels.get(9));
        TextView imponibile = (TextView) findViewById(R.id.imponibile); 
        imponibile.setText("Imponibile: "+lstLabels.get(10));
        TextView iva = (TextView) findViewById(R.id.iva); 
        iva.setText("IVA (21%): "+lstLabels.get(11));

        TextView totale_fattura = (TextView) findViewById(R.id.totale_fattura); 
        totale_fattura.setText("Totale fattura: "+lstLabels.get(12));
        TextView ra = (TextView) findViewById(R.id.ra); 
        ra.setText("R/A (20%): ");
        TextView totale_pagare = (TextView) findViewById(R.id.totale_pagare); 
        totale_pagare.setText("Totale da pagare: ");        
        
        
    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.layout.menu, menu);
        return true;
    }*/
    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
 
        switch (item.getItemId())
        {
        case R.id.sync:
        	dropboxSync();
            return true;
 
        case R.id.open_invoice:
        	createListFatture();
            //Toast.makeText(AndroidMenusActivity.this, "Save is Selected", Toast.LENGTH_SHORT).show();
            return true;
 
        case R.id.menu_preferences:   	
        	dropboxLink();
        	//Toast.makeText(AndroidMenusActivity.this, "Search is Selected", Toast.LENGTH_SHORT).show();
            return true;
 
        default:
            return super.onOptionsItemSelected(item);
        }
    }        
   */
/*    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
        	createListFatture();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
   */    
	@Override
	public void MenuItemSelectedEvent(CustomMenuItem selection) {
        switch (selection.getId())
        {
        case MENU_ITEM_1:
        	dropboxLink();
        	return;
        case MENU_ITEM_2:
        	dropboxSync();
        	return;
        case MENU_ITEM_3:
        	unZipODS();
       	 	//Imposto una variabile globale che mi ritorna la view attiva
            g = Globals.getInstance();
            g.setData(MENU_2);
        	return;
        case MENU_ITEM_4:
        	
        	return;
        case MENU_ITEM_FATTURA_1:
        	printInvoice();
         	return;
        case MENU_ITEM_FATTURA_2:
        	viewPDF();
         	return;
        case MENU_ITEM_FATTURA_3:
        	sharePdf();
        	return;
        case MENU_ITEM_EDIT_1:
        	newInvoice();
         	return;
        case MENU_ITEM_EDIT_2:
        	editInvoice();
         	return;
        case MENU_ITEM_EDIT_3:
        	deleteInvoice();
        	return;
        default:
        	return;

        }  

		/*
		Toast t = Toast.makeText(this, "You selected item #"+Integer.toString(selection.getId()), Toast.LENGTH_SHORT);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
		*/
	}
    @Override
    protected void onResume() {
        super.onResume();
        AndroidAuthSession session = mApi.getSession();

        if (session.authenticationSuccessful()) {
            try {
                // Mandatory call to complete the auth
                session.finishAuthentication();

                // Store it locally in our app for later use
                TokenPair tokens = session.getAccessTokenPair();
                storeKeys(tokens.key, tokens.secret);
                //setLoggedIn(true);
            } catch (IllegalStateException e) {
                showToast("Couldn't authenticate with Dropbox:" + e.getLocalizedMessage());
                Log.i(TAG, "Error authenticating", e);
            }
        }
        
       
    }
	@Override
	public void onClick(View v) {
        switch ( v.getId() ) {
	        case R.id.ButtonOkInvoice:
		        final EditText edit_name = (EditText)findViewById(R.id.numero_fattura_edit);
		        final EditText edit_lastname = (EditText)findViewById(R.id.anno_fattura_edit);
		        //edit_name.
		        //insertNewInvoice
	
	        break;
	    }		

		
	}    
    public void launchForm(View button) {

        Intent launchFormActivity = new Intent(this, FormActivity.class); 
        startActivity(launchFormActivity);

    }   
    private void logOut() {
        // Remove credentials from the session
        mApi.getSession().unlink();

        // Clear our stored keys
        clearKeys();
        // Change UI state to display logged out version
        //setLoggedIn(false);
    }

    private void checkAppKeySetup() {
        // Check to make sure that we have a valid app key
        if (APP_KEY.startsWith("CHANGE") ||
                APP_SECRET.startsWith("CHANGE")) {
            showToast("You must apply for an app key and secret from developers.dropbox.com, and add them to the DBRoulette ap before trying it.");
            finish();
            return;
        }

        // Check if the app has set up its manifest properly.
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        String scheme = "db-" + APP_KEY;
        String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
        testIntent.setData(Uri.parse(uri));
        PackageManager pm = getPackageManager();
        if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
            showToast("URL scheme in your app's " +
                    "manifest is not set up correctly. You should have a " +
                    "com.dropbox.client2.android.AuthActivity with the " +
                    "scheme: " + scheme);
            finish();
        }
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     *
     * @return Array of [access_key, access_secret], or null if none stored
     */
    private String[] getKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key != null && secret != null) {
        	String[] ret = new String[2];
        	ret[0] = key;
        	ret[1] = secret;
        	return ret;
        } else {
        	return null;
        }
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void storeKeys(String key, String secret) {
        // Save the access key for later
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.putString(ACCESS_KEY_NAME, key);
        edit.putString(ACCESS_SECRET_NAME, secret);
        edit.commit();
    }

    private void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session;

        String[] stored = getKeys();
        if (stored != null) {
            AccessTokenPair accessToken = new AccessTokenPair(stored[0], stored[1]);
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessToken);
        } else {
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
        }

        return session;
    }
    /**
     * Snarf the menu key.
     */
	public boolean onKeyDown(int keyCode, KeyEvent event) { 
	    if (keyCode == KeyEvent.KEYCODE_MENU) {
	    	g = Globals.getInstance();
	    	int idMenu=g.getData();
	    	Log.v("onKeyDown", Integer.toString(idMenu)); 
	    	switch (idMenu) {
            	case MENU_1:
            		doMenu();
            		return true;
            //	case MENU_2:
            //		doMenuEdit();
            //		return true;    
            	case MENU_3:
            		doMenuFattura();
            		return true;       
                default:
                	return true;            		
	    	}
	    	/*
	    	try{
	    		if (findViewById(R.id.inarcassa) != null){
		    		if (findViewById(R.id.inarcassa).isShown()){
		    			doMenuFattura();
		    		} else {
		    			doMenu();
		    		}
	    		} else {
	    			doMenu();
	    		}
	    	} catch(Exception e){

	    		
	    	}
	    */
	    }
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
        	 setContentView(R.layout.main);
        	 doMenu();
            return true;
        }	    
		return super.onKeyDown(keyCode, event); 
	} 
	
	/**
     * Load up our menu.
     */
	private void loadMenuItems() {
		//This is kind of a tedious way to load up the menu items.
		//Am sure there is room for improvement.
		ArrayList<CustomMenuItem> menuItems = new ArrayList<CustomMenuItem>();
		CustomMenuItem cmi = new CustomMenuItem();
		cmi.setCaption("Link");
		cmi.setImageResourceId(R.drawable.dropboxlink_trasp);
		cmi.setId(MENU_ITEM_1);
		menuItems.add(cmi);
		cmi = new CustomMenuItem();
		cmi.setCaption("Sync");
		cmi.setImageResourceId(R.drawable.dropboxsync_trasp);
		cmi.setId(MENU_ITEM_2);
		menuItems.add(cmi);
		cmi = new CustomMenuItem();
		cmi.setCaption("Entrate");
		cmi.setImageResourceId(R.drawable.fatture_a_trasp);
		cmi.setId(MENU_ITEM_3);
		menuItems.add(cmi);
		cmi = new CustomMenuItem();
		cmi.setCaption("Spese");
		cmi.setImageResourceId(R.drawable.spese);
		cmi.setId(MENU_ITEM_4);
		menuItems.add(cmi);
		if (!mMenu.isShowing())
		try {
			mMenu.setMenuItems(menuItems);
		} catch (Exception e) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Attenzione!");
			alert.setMessage(e.toString());
			alert.show();
		}
	}
	
	private void loadMenuItemsFattura() {
		//This is kind of a tedious way to load up the menu items.
		//Am sure there is room for improvement.
		ArrayList<CustomMenuItem> menuItems = new ArrayList<CustomMenuItem>();
		CustomMenuItem cmi = new CustomMenuItem();
		cmi.setCaption("Stampa");
		cmi.setImageResourceId(R.drawable.stampa);
		cmi.setId(MENU_ITEM_FATTURA_1);
		menuItems.add(cmi);
		cmi = new CustomMenuItem();
		cmi.setCaption("Visualizza");
		cmi.setImageResourceId(R.drawable.pdf);
		cmi.setId(MENU_ITEM_FATTURA_2);
		menuItems.add(cmi);
		cmi = new CustomMenuItem();
		cmi.setCaption("Invia");
		cmi.setImageResourceId(R.drawable.busta);
		cmi.setId(MENU_ITEM_FATTURA_3);
		menuItems.add(cmi);
		if (!mMenuFattura.isShowing())
		try {
			mMenuFattura.setMenuItems(menuItems);
		} catch (Exception e) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Attenzione!");
			alert.setMessage(e.getMessage());
			alert.show();
		}
	}	
	private void loadMenuItemsEdit() {
		//This is kind of a tedious way to load up the menu items.
		//Am sure there is room for improvement.
		ArrayList<CustomMenuItem> menuItems = new ArrayList<CustomMenuItem>();
		CustomMenuItem cmi = new CustomMenuItem();
		cmi.setCaption("Nuovo");
		cmi.setImageResourceId(R.drawable.busta);
		cmi.setId(MENU_ITEM_EDIT_1);
		menuItems.add(cmi);
		cmi = new CustomMenuItem();
		cmi.setCaption("Modifica");
		cmi.setImageResourceId(R.drawable.busta);
		cmi.setId(MENU_ITEM_EDIT_2);
		menuItems.add(cmi);
		cmi = new CustomMenuItem();
		cmi.setCaption("Elimina");
		cmi.setImageResourceId(R.drawable.busta);
		cmi.setId(MENU_ITEM_EDIT_3);
		menuItems.add(cmi);
		if (!mMenuEdit.isShowing())
		try {
			mMenuEdit.setMenuItems(menuItems);
		} catch (Exception e) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Attenzione!");
			alert.setMessage(e.getMessage());
			alert.show();
		}
	}
	
	/**
     * Toggle our menu on user pressing the menu key.
     */
	private void doMenu() {
		if (mMenu.isShowing()) {
			mMenu.hide();
			mMenuEdit.hide();
			mMenuFattura.hide();
		} else {
			//Note it doesn't matter what widget you send the menu as long as it gets view.
			mMenu.show(findViewById(R.id.any_old_widget));
			mMenuEdit.hide();
			mMenuFattura.hide();
		}
	}
	private void doMenuEdit() {
		Log.v("doMenuEdit", "1");
	/*	
		if (mMenuEdit.isShowing()) {
			mMenu.hide();
			mMenuEdit.hide();
			mMenuFattura.hide();
		} else {
			mMenuEdit.show(findViewById(R.id.list_fatture));
			mMenu.hide();
			mMenuFattura.hide();
		}
	 */
	}	
	private void doMenuFattura() {
		if (mMenuFattura.isShowing()) {
			mMenuFattura.hide();
			mMenuEdit.hide();
			mMenu.hide();
			
		} else {
			//Note it doesn't matter what widget you send the menu as long as it gets view.
			mMenu.hide();
			mMenuEdit.hide();
			mMenuFattura.show(findViewById(R.id.intestazione));
		}
	}
	
	private void newInvoice(){
		try {
			setContentView(R.layout.fattura_edit);			
		 } catch (Exception e){
			 Log.v("newInvoice", e.toString());
			 return;
		 }
	}
	private void editInvoice(){
		try {
			
		 } catch (Exception e){
			 Log.v("newInvoice", e.toString());
			 return;
		 }
	}
	private void deleteInvoice(){
		try {
			
		 } catch (Exception e){
			 Log.v("newInvoice", e.toString());
			 return;
		 }
	}	
	private void printInvoice(){
		try {
			Log.v("printInvoice", "0:"+new Integer(intLastInvoice).toString());
			List<String> lstLabels = op.getArrLabels(intLastInvoice);
			Log.v("printInvoice", "1");
			createPdf pdf = new createPdf();
			Log.v("printInvoice", "2");
	     	File fTPL = new File("/mnt/sdcard/gr/ods/tplfattura.html");
	     	Log.v("printInvoice", "3");
	     	String strTPL=StringToInputStream(new FileInputStream(fTPL));
	     	Log.v("printInvoice", "4");
	     	//strTPL=strTPL.replace("%titolo%", lstLabels.get(0)+"/"+lstLabels.get(1));
	     	strTPL=strTPL.replace("%numero_fattura%", lstLabels.get(0));
	     	strTPL=strTPL.replace("%anno_fattura%", lstLabels.get(1));
	     	strTPL=strTPL.replace("%data_fattura%", lstLabels.get(2));
	     	strTPL=strTPL.replace("%azienda%", lstLabels.get(3));
	     	strTPL=strTPL.replace("%indirizzo_azienda%", lstLabels.get(4));
	     	strTPL=strTPL.replace("%partita_iva%", lstLabels.get(5));
	     	strTPL=strTPL.replace("%descrizione_prestazione%", lstLabels.get(6));
	     	strTPL=strTPL.replace("%onorari%", NumberFormat.getCurrencyInstance().format(new BigDecimal(lstLabels.get(7).replace(",", "."))));;
	     	strTPL=strTPL.replace("%inarcassa%", NumberFormat.getCurrencyInstance().format(new BigDecimal(lstLabels.get(8).replace(",", "."))));;
	     	strTPL=strTPL.replace("%imponibile%", NumberFormat.getCurrencyInstance().format(new BigDecimal(lstLabels.get(9).replace(",", "."))));;
	     	strTPL=strTPL.replace("%iva%", NumberFormat.getCurrencyInstance().format(new BigDecimal(lstLabels.get(10).replace(",", "."))));;
	     	strTPL=strTPL.replace("%totale_fattura%", NumberFormat.getCurrencyInstance().format(new BigDecimal(lstLabels.get(11).replace(",", "."))));;
	     	strTPL=strTPL.replace("%ra%", NumberFormat.getCurrencyInstance().format(new BigDecimal(lstLabels.get(12).replace(",", "."))));;
	     	strTPL=strTPL.replace("%totale%", NumberFormat.getCurrencyInstance().format(new BigDecimal(lstLabels.get(13).replace(",", "."))));;
	     	Log.v("printInvoice", strTPL);
	     	pdf.printPdf(strTPL,"/mnt/sdcard/gr/stampe/"+lstLabels.get(1)+lstLabels.get(0)+".pdf");
            Toast.makeText(this, 
                    "File creato: "+"/mnt/sdcard/gr/stampe/"+lstLabels.get(1)+lstLabels.get(0)+".pdf", 
                    Toast.LENGTH_SHORT).show();
		 } catch (Exception e){
			 Log.v("printInvoice", e.toString());
			 return;
		 }
	}
	private void viewPDF(){
		List<String> lstLabels = op.getArrLabels(intLastInvoice);
		File file = new File("/mnt/sdcard/gr/stampe/"+lstLabels.get(1)+lstLabels.get(0)+".pdf");
		/*
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		
		intent.setDataAndType(Uri.fromFile(file), "pdf/*");
		startActivity(intent);
        */
		if (file.exists()) {
            Uri path = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(path, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {
                startActivity(intent);
            } 
            catch (ActivityNotFoundException e) {
                Toast.makeText(this, 
                    "Non ci sono app per visualizzare pdf", 
                    Toast.LENGTH_SHORT).show();
            }
        }		
	}
	private String StringToInputStream(FileInputStream ifHTML ) {
		try {
			// read it with BufferedReader
			BufferedReader br = new BufferedReader(new InputStreamReader(ifHTML));
			String strFile="";
			String strLine="";
			while ((strLine = br.readLine()) != null) {
				strFile +=strLine;
			}
		 
			br.close();
			return strFile;
		 } catch (Exception e){
			 Log.v("StringToInputStream", e.toString());
			 return "";
		 }
	}	
	private void sharePdf(){
		List<String> lstLabels = op.getArrLabels(intLastInvoice);
		File file = new File("/mnt/sdcard/gr/stampe/"+lstLabels.get(1)+lstLabels.get(0)+".pdf");

		if (file.exists()) {
            Uri path = Uri.fromFile(file);

            
            Intent emailIntent = new Intent(Intent.ACTION_SEND); 
            emailIntent.setType("application/pdf");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] 
            {"giuseppe.repole@gmail.com"}); 
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, 
            "Invio della fattura n. "+lstLabels.get(0)+"/"+lstLabels.get(1)+" tramite Droid4FL"); 
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, 
            "Questa fattura è stata inviata per mezzo di Droid4FL"); 
           
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+ path));
        
            try {
            	startActivity(Intent.createChooser(emailIntent, "Invia fattura ..."));
            } 
            catch (ActivityNotFoundException e) {
                Toast.makeText(this, 
                    "Non ci sono app per inviare pdf", 
                    Toast.LENGTH_SHORT).show();
            }
        }
		
	}
	private Intent createShareIntent() {
		  Intent shareIntent = new Intent(Intent.ACTION_SEND);
		        shareIntent.setType("text/plain");
		        shareIntent.putExtra(Intent.EXTRA_TEXT, 
		          "http://android-er.blogspot.com/");
		        return shareIntent;
	}
	private void insertNewInvoice(String[] arrNewRow){
		ArrayList<String[]> arrlAllInvoices= op.getAllValues();
		arrlAllInvoices.add(arrNewRow);
	}

}
