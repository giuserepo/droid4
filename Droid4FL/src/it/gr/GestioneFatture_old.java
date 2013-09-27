package it.gr;

import it.gr.R;

import java.util.List;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class GestioneFatture_old extends Activity {
 private dbMgr myDbMgr;
 Spinner spinner;
    @Override
    public void onCreate(Bundle savedInstanceState) {
 
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ListView listFatture = (ListView)findViewById(R.id.contentlist);

        //myDbMgr.deleteAll();
        //TextView productsTv=(TextView)findViewById(R.id.numfattura);
        //productsTv.setTypeface(Typeface.createFromAsset(getAssets(),"kberry.ttf"));  
        //caricamento di un font esterno, piazzato nella cartella assets
       
        
       
        myDbMgr =new dbMgr(getApplicationContext(),this);
        myDbMgr.open();  //apriamo il db
       
        spinner = (Spinner) findViewById(R.id.spinner_fatture);
        loadSpinnerData();
       /*
        if(myDbMgr.fetchProducts().getCount()==0){//inserimento dati, solo se il db è vuoto
       
                myDbMgr.insertProduct("Telefono", 400);
                myDbMgr.insertProduct("Scarpe", 100);
                myDbMgr.insertProduct("PC", 500);
                myDbMgr.insertProduct("Pane", 2);
                myDbMgr.insertProduct("Patente guida", 100); //lol
       
        }
       */
       

        Cursor c=myDbMgr.fetchProducts(); // query
        startManagingCursor(c);


       
       
        SimpleCursorAdapter adapter=new SimpleCursorAdapter( //semplice adapter per i cursor
                        this,
                        R.layout.row, //il layout di ogni riga/prodotto
                        c,
                        new String[]{dbMgr.CicloAttivoMetaData.NUMERO_FATTURA},//questi colonne
                        new int[]{R.id.numfattura});//in queste views
       
       

       
       
        //listFatture.setAdapter(adapter); //la listview ha questo adapter
       
       /*
        //qui vediamo invece come reperire i dati e usarli, in questo caso li stampiamo in una textview
       
        int nameCol=c.getColumnIndex(myDbMgr.ProductsMetaData.PRODUCT_NAME_KEY);  //indici delle colonne
        int priceCol=c.getColumnIndex(myDbMgr.ProductsMetaData.PRODUCT_PRICE_KEY);      
       
        if(c.moveToFirst()){  //se va alla prima entry, il cursore non è vuoto
                do {
                               
                        productsTv.append("Product Name:"+c.getString(nameCol)+", Price:"+c.getInt(priceCol)+"\n"); //estrazione dei dati dalla entry del cursor
                                       
                        } while (c.moveToNext());//iteriamo al prossimo elemento
        }
       */
        myDbMgr.close();
       
/*       
       

        getWindow().setFormat(PixelFormat.RGBA_8888);   //visto che usiamo i gradient, usiamo questo trick (vedi snippet forum)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);  
       
        productsLv.setBackgroundDrawable(new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{Color.RED,Color.parseColor("#f2bf26")}));
        productsTv.setBackgroundDrawable(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{Color.RED,Color.parseColor("#f2bf26")}));
        //definizione ed uso di gradient in modo programmatico
       
       
        //animazioni in modo programmatico (vedi snippet forum)
        Animation a1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        a1.setDuration(1000);
        a1.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.anim.decelerate_interpolator));
        productsLv.startAnimation(a1);
        //entra da sotto
       
       
        Animation a2 = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        a2.setDuration(1000);
        a2.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.anim.decelerate_interpolator));
        productsTv.startAnimation(a2);
        //entra da sopra
  */     
       
            
    }

    private void loadSpinnerData() {
        // database handler
                
 
        // Spinner Drop down elements
        List<String> lables = myDbMgr.getAllLabels();
 
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, lables);
 
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
 
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_gestione_fatture, menu);
        return true;
    }
    public void launchForm(View button) {

        Intent launchFormActivity = new Intent(this, FormActivity.class); 
        startActivity(launchFormActivity);

    }    
       
}
