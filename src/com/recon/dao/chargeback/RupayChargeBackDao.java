package com.recon.dao.chargeback;

import java.sql.SQLException;

import org.json.*;
import org.json.simple.JSONArray;

public interface RupayChargeBackDao {
	
	public  String makeSeq() ;
	
	public JSONArray getChargeBack(JSONArray table) throws ClassNotFoundException, SQLException ;
	
	public JSONArray getChargeBackFromBkp(JSONArray table, JSONArray appendTo) throws ClassNotFoundException, SQLException ;

	public JSONArray getChargeBackFromBkp2(JSONArray table, JSONArray toXmlModel)throws ClassNotFoundException, SQLException ;

	public JSONArray getChargeBackFromBkp3(JSONArray table, JSONArray toXmlModel)throws ClassNotFoundException, SQLException ;
	
}
