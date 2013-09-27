package it.gr;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Element;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.*;

public class dbMgr {
    SQLiteDatabase mDb;
    DbHelper mDbHelper;
    Drod4FL mGestFatt;
    Context mContext;
    private static final String DB_NAME="/mnt/sdcard/gr/gestione_economica_2012.sqlite";//"gestione_economica_2012";//nome del db
    private static final int DB_VERSION=1; //numero di versione del nostro db
   
    public dbMgr(Context ctx, Object oGestFatt){
            mContext=ctx;
            
            mDbHelper=new DbHelper(ctx, DB_NAME, null, DB_VERSION,(Drod4FL) oGestFatt);   //quando istanziamo questa classe, istanziamo anche l'helper (vedi sotto)    
    }
   
    public void open(){  //il database su cui agiamo è leggibile/scrivibile
            mDb=mDbHelper.getWritableDatabase();
           
    }
   
    public void close(){ //chiudiamo il database su cui agiamo
            mDb.close();
    }
 
    public List<String> getAllLabels(){ 
        return mDbHelper.getAllLabels();
    }  
    public ArrayList<String> getListLabelsFromPos(int intPos){
    	return mDbHelper.getListLabelsFromPos(intPos);
    }
	public ArrayList<HashMap<String, String>> getArrLabels() {
		return mDbHelper.getArrLabels();
	}    
	
    //i seguenti 2 metodi servono per la lettura/scrittura del db. aggiungete e modificate a discrezione
   // consiglio:si potrebbe creare una classe Prodotto, i quali oggetti verrebbero passati come parametri dei seguenti metodi, rispettivamente ritornati. Lacio a voi il divertimento

   
    public void insertProduct(int numero_fattura, int anno, Date data, String nome_societa, String indirizzo_societa, String p_iva_societa, String descrizione_prestazione, double onorari, double inarcassa, double imponibile, double iva, double totale_fattura){ //metodo per inserire i dati
            ContentValues cv=new ContentValues();
            cv.put(CicloAttivoMetaData.NUMERO_FATTURA,numero_fattura);
            cv.put(CicloAttivoMetaData.ANNO,anno);
            cv.put(CicloAttivoMetaData.DATA,data.toString());
            cv.put(CicloAttivoMetaData.NOME_SOCIETA,nome_societa);
            cv.put(CicloAttivoMetaData.INDIRIZZO_SOCIETA,indirizzo_societa);
            cv.put(CicloAttivoMetaData.P_IVA_SOCIETA,p_iva_societa);
            cv.put(CicloAttivoMetaData.DESCRIZIONE_PRESTAZIONE,descrizione_prestazione);
            cv.put(CicloAttivoMetaData.ONORARI,onorari);
            cv.put(CicloAttivoMetaData.INARCASSA,inarcassa);
            cv.put(CicloAttivoMetaData.IMPONIBILE,imponibile);
            cv.put(CicloAttivoMetaData.IVA,iva);
            cv.put(CicloAttivoMetaData.TOTALE_FATTURA,totale_fattura);
            mDb.insert(CicloAttivoMetaData.CICLO_ATTIVO_TABLE, null, cv);
    }
    public Cursor fetchProducts(){ //metodo per fare la query di tutti i dati
            return mDb.query(CicloAttivoMetaData.CICLO_ATTIVO_TABLE, null,null,null,null,null,null);              
    }

    public class CicloAttivoMetaData {  // i metadati della tabella, accessibili ovunque
            static final String CICLO_ATTIVO_TABLE = "ciclo_attivo";
            static final String NUMERO_FATTURA = "Numero Fattura";
            static final String ANNO = "Anno";
            static final String DATA = "Data";
            static final String NOME_SOCIETA = "Nome Società";
            static final String INDIRIZZO_SOCIETA = "Indirizzo Società";
            static final String P_IVA_SOCIETA = "Partita IVA Società";
            static final String DESCRIZIONE_PRESTAZIONE = "Descrizione Prestazione";
            static final String ONORARI = "Onorari";
            static final String INARCASSA = "Inarcassa";
            static final String IMPONIBILE = "Totale Imponibile";
            static final String IVA = "IVA 21%";
            static final String TOTALE_FATTURA = "Totale fattura";
        	static final String KEY_ID = "id";
        	static final String KEY_TITLE = "title";
        	static final String KEY_ANNO = "anno";
        	static final String KEY_IMPORTO = "importo";
        	static final String KEY_THUMB_URL = "thumb_url";
    }

    private static final String CICLO_ATTIVO_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "  //codice sql di creazione della tabella
                    +  " \"ciclo_attivo\" (\"Numero Fattura\" INTEGER PRIMARY KEY  NOT NULL , \"Anno\" INTEGER NOT NULL , \"Data\" DATETIME NOT NULL , \"Nome Società\" TEXT NOT NULL , \"Indirizzo Società\" TEXT NOT NULL , \"Partita IVA Società\" TEXT NOT NULL , \"Descrizione Prestazione\" TEXT NOT NULL , \"Onorari\" DOUBLE, \"Inarcassa\" DOUBLE, \"Totale Imponibile\" DOUBLE, \"IVA 21%\" DOUBLE, \"Totale fattura\" DOUBLE)";

    private class DbHelper extends SQLiteOpenHelper { //classe che ci aiuta nella creazione del db
    	Drod4FL mGestFatt;
            public DbHelper(Context context, String name, CursorFactory factory,int version, Drod4FL oGestFatt) {
                    super(context, name, factory, version);
                    mGestFatt = oGestFatt;
            }

            @Override
            public void onCreate(SQLiteDatabase _db) { //solo quando il db viene creato, creiamo la tabella
                    _db.execSQL(CICLO_ATTIVO_TABLE_CREATE);
            }

            @Override
            public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
                    //qui mettiamo eventuali modifiche al db, se nella nostra nuova versione della app, il db cambia numero di versione

            }
            public List<String> getAllLabels(){
                List<String> labels = new ArrayList<String>();
         
                // Select All Query
                String selectQuery = "SELECT  * FROM " + dbMgr.CicloAttivoMetaData.CICLO_ATTIVO_TABLE;
         
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery(selectQuery, null);
         
                // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    do {
                        labels.add(cursor.getString(1));
                    } while (cursor.moveToNext());
                }
         
         
                // returning lables
                return labels;
            }  
            public ArrayList<HashMap<String, String>> getArrLabels(){
            	ArrayList<HashMap<String, String>> arrlabels = new ArrayList<HashMap<String, String>>();
                String selectQuery = "SELECT  * FROM " + dbMgr.CicloAttivoMetaData.CICLO_ATTIVO_TABLE;
         
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery(selectQuery, null);
                
                HashMap<String, String> map = new HashMap<String, String>();
    			cursor.moveToFirst();
    			
    			do {
                    	map.put(dbMgr.CicloAttivoMetaData.KEY_ID, cursor.getString(1));
                    	map.put(dbMgr.CicloAttivoMetaData.KEY_ANNO, cursor.getString(2));
                    	map.put(dbMgr.CicloAttivoMetaData.KEY_TITLE, cursor.getString(1) + " - " +cursor.getString(4));
                    	map.put(dbMgr.CicloAttivoMetaData.KEY_IMPORTO,cursor.getString(8));
                    	map.put("thumb_url", "#");

                    	arrlabels.add(map);
                    	map = new HashMap<String, String>();
                    } while (cursor.moveToNext());

    			return arrlabels;
            }             
            public ArrayList<String> getListLabelsFromPos(int intPos){
            	ArrayList<String> listLabels = new ArrayList<String>();
                String selectQuery = "SELECT  * FROM " + dbMgr.CicloAttivoMetaData.CICLO_ATTIVO_TABLE + " where \"Numero Fattura\" ="+ Integer.toString(intPos);
         
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery(selectQuery, null);
                
                HashMap<String, String> map = new HashMap<String, String>();
    			cursor.moveToFirst();
    			int i;
    			//for (i=1; i < cursor.getColumnCount(); i++);
    			//{
    			listLabels.add(cursor.getString(0));
    			listLabels.add(cursor.getString(1));
    			listLabels.add(cursor.getString(2));
    			listLabels.add(cursor.getString(3));
    			listLabels.add(cursor.getString(4));
    			listLabels.add(cursor.getString(5));
    			listLabels.add(cursor.getString(6));
    			listLabels.add(cursor.getString(7));
    			
    			listLabels.add(cursor.getString(8));
    			listLabels.add(cursor.getString(9));
    			listLabels.add(cursor.getString(10));
    			listLabels.add(cursor.getString(11));
    			listLabels.add(cursor.getString(12));
    			/*
    			listLabels.add(cursor.getString(13));
                */
                //}

    			return listLabels;
            }                       

    }


           

}
