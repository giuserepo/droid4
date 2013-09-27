package it.gr;


import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class OdsParser {

    //private static String TAG = XsltTester.class.getSimpleName();
	private ArrayList<HashMap<String, String>> _arrlabels;
	private ArrayList<String[]> _arrAllValues;
	
	private String strOut="";
	
    
	public OdsParser() { 

      } 
	public ArrayList<String[]> getAllValues(){
  
		return _arrAllValues;
	}	
	public List<String> getArrLabels(int intPos){
        String[] arrRows=strOut.split(";:");
        Log.v("getArrLabels", Integer.toString(arrRows.length));
        List<String> arrlabels = Arrays.asList(arrRows[intPos].split(";"));

 
		//List<String> arrVal= Arrays.asList(_arrlabels.get(intPos).values().toArray(new String[_arrlabels.get(intPos).size()]));
		return arrlabels;
	}
    public ArrayList<HashMap<String, String>> parseODS(InputStream isXML, InputStream isXSL) {

    	ArrayList<HashMap<String, String>> arrlabels = new ArrayList<HashMap<String, String>>();
    	_arrAllValues=new ArrayList<String[]>();
    	HashMap<String, String> map = new HashMap<String, String>();
    	String[] arrLAllValues;

        try {
        	
            Source xmlSource = new StreamSource(isXML);
            Source xsltSource = new StreamSource(isXSL);

            TransformerFactory transFact = TransformerFactory.newInstance();
            Transformer trans = transFact.newTransformer(xsltSource);

            Writer outWriter = new StringWriter();  
            StreamResult result = new StreamResult( outWriter );  
            
            trans.transform(xmlSource, result);
            
            strOut=outWriter.toString();
            strOut=strOut.replaceAll("&#8211;", "-");
            strOut=strOut.replaceAll("&#8217;", "'");
            //Log.v("parseODS", strOut);
            String[] arrRows=strOut.split(";:");
           // Log.v("parseODS", "2: "+Integer.toString(arrRows.length));
            //Log.v("parseODS", "3: "+arrRows[0]);
            //Il for seguente crea la lista delle fatture!
            
            for (int i=0; i<arrRows.length; i++){
            	Log.v("parseODS", "1: "+arrRows[i]);
            	arrLAllValues=arrRows[i].split(";");
            	_arrAllValues.add(arrLAllValues);
            	map.put(dbMgr.CicloAttivoMetaData.KEY_ID, arrRows[i].split(";")[0]);
	        	map.put(dbMgr.CicloAttivoMetaData.KEY_ANNO, arrRows[i].split(";")[1]);
	        	map.put(dbMgr.CicloAttivoMetaData.KEY_TITLE, arrRows[i].split(";")[0] + " - " +arrRows[i].split(";")[3]);
	        	map.put(dbMgr.CicloAttivoMetaData.KEY_IMPORTO,arrRows[i].split(";")[7]);
	        	map.put("thumb_url", "#");
	        	arrlabels.add(map);
	        	map = new HashMap<String, String>();
	        	
            }
            _arrlabels=arrlabels;
            
            return arrlabels;

        } catch (Exception e) {
        	Log.v("parseODS", "errore: "+e.toString());
        	_arrlabels=arrlabels;
        	arrlabels.add(map);
        	return arrlabels;
        }
    }
    public ArrayList<String> getListLabelsFromPos(int intPos){
    	ArrayList<String> listLabels = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " + dbMgr.CicloAttivoMetaData.CICLO_ATTIVO_TABLE + " where \"Numero Fattura\" ="+ Integer.toString(intPos);
 
        SQLiteDatabase db = null;//this.getReadableDatabase();
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