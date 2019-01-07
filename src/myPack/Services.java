package myPack;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import javax.ws.rs.core.*;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gargoylesoftware.htmlunit.javascript.host.Console;




@Path("services")
public class Services{
	
	private String setQueryFilters(String place, String range, String interval) {
		String query = " WHERE ";
		System.out.println(place + " " + range + " " + interval);
		//Set data odierna
		Calendar date = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		query = query + "data_a >= '" + formatter.format(date.getTime()) + "' ";
		
		//Filtra eventi per intervallo di tempo
		Calendar endDate = Calendar.getInstance();
		if ("1d".equals(interval)) endDate = date;
		else if ("7d".equals(interval)) endDate.add(Calendar.DATE, 7);
			else if ("1m".equals(interval)) endDate.add(Calendar.MONTH, 1);
				else if ("6m".equals(interval)) endDate.add(Calendar.MONTH, 6);
		query= query + "AND data_da <= '" + formatter.format(endDate.getTime()) + "' ";
		
		//Filtra eventi per comune e distanza
		if(!"all-cities".equals(place)) {
			if("0".equals(range)) {
				//Aggiungo filtro per singolo comune
				query = query +  "AND comune = '" + place + "' ";
			}
			else {
				// Sub-query per reperire anche le città vicine
				query = query +  "AND ( comune IN (SELECT b FROM distanze WHERE a = '" + place + "' AND distanza <= " + range + ") OR comune = '" + place +"') ";
				
				
			}
		}
		return query;
	}
	
	private String getQueryDistanze(String q, String place) {
		// Aggiungo il campo FROM con LEFT OUTER JOIN su tabella distanze per reperire la distanza dall'origine
		return	"FROM (SELECT link,titolo,comune,posto_nome,posto_link,data_da,data_a,descrizione,popolarita FROM eventi " +
				q + ")AS eventiFiltrati LEFT OUTER JOIN distanze ON a = '" + place +  "' AND comune = b ";
	}

	@GET
	@Path("getEvents/{place}/{range}/{interval}/{offset}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEvents(@DefaultValue("") @PathParam("place") String place, @DefaultValue("") @PathParam("range") String range, @DefaultValue("") @PathParam("interval") String interval, @PathParam("offset") String offset) throws Exception {
        Connection conn = null;
		/*SELECT elements from */
		try{
			conn = DBConnection.getConnection();
		}catch(Exception e) {}
		
		//Creo la query iniziale e aggiungo i filtri
		String q = "SELECT link,titolo,comune,posto_nome,posto_link,data_da,data_a,descrizione,popolarita,distanza " + getQueryDistanze(setQueryFilters(place, range, interval),place);

			//Aggiungo l'offset
			if (offset == null) offset = "0";
			int off = Integer.parseInt(offset);	
			q = q+ "ORDER BY data_a ASC ";
			if(off != 0) {
				q = q+ " OFFSET "+off+" ";
			}
			Statement st = conn.createStatement();
			//System.out.println(q);
			ResultSet rs = st.executeQuery(q);
			
			JSONArray json = new JSONArray();
			ResultSetMetaData rsmd = rs.getMetaData();
			while(rs.next()) {
			  int numColumns = rsmd.getColumnCount();
			  JSONObject obj = new JSONObject();
			  for (int i=1; i<=numColumns; i++) {
			    String column_name = rsmd.getColumnName(i);
			    //System.out.println(rsmd.getColumnName(i));
			    obj.put(column_name, rs.getObject(column_name));
			  }
			  json.put(obj);
			}
			return Response.status(200).entity(json.toString()).build();
		
		
	}
	
	@GET
	@Path("countEvents/{place}/{range}/{interval}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response countEvents(@DefaultValue("") @PathParam("place") String place, @DefaultValue("") @PathParam("range") String range, @DefaultValue("") @PathParam("interval") String interval, @DefaultValue("0") @PathParam("offset") String offset) throws Exception {
        Connection conn = null;
		/*SELECT elements from */
		try{
			conn = DBConnection.getConnection();
		}catch(Exception e) {}
		//Creo la query iniziale e aggiungo i filtri
		String q = "SELECT COUNT(link) as num FROM eventi " + setQueryFilters(place, range, interval);
			
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(q);
			
			JSONArray json = new JSONArray();
			ResultSetMetaData rsmd = rs.getMetaData();
			while(rs.next()) {
			  int numColumns = rsmd.getColumnCount();
			  JSONObject obj = new JSONObject();
			  for (int i=1; i<=numColumns; i++) {
			    String column_name = rsmd.getColumnName(i);
			    obj.put(column_name, rs.getObject(column_name));
			  }
			  json.put(obj);
			}
			return Response.status(200).entity(json.toString()).build();
		
		
	}
	
	@GET
	@Path("getComuni/{term}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getComuni(@PathParam("term") String term) throws Exception {
		
        Connection conn = null;
		/*SELECT elements from */
		try{
			conn = DBConnection.getConnection();
		}catch(Exception e) {}
		
		String q = "";
	
			q = "SELECT DISTINCT a FROM distanze where a ILIKE '%"+term+"%'";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(q);
			
			JSONArray json = new JSONArray();
			ResultSetMetaData rsmd = rs.getMetaData();
			while(rs.next()) {
			  json.put(rs.getString("a"));
			}
			return Response.status(200).entity(json.toString()).build();
		
		
	}
	
	@GET
	@Path("getEvento/{term}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEvento(@PathParam("term") String term) throws Exception {
        Connection conn = null;
		/*SELECT elements from */
		try{
			conn = DBConnection.getConnection();
		}catch(Exception e) {}
		
		String q = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		Calendar date = Calendar.getInstance();
		
			q = "SELECT DISTINCT titolo FROM eventi where titolo ILIKE '%"+term+"%' AND data_a >= '"+formatter.format(date.getTime())+"' ";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(q);
			
			JSONArray json = new JSONArray();
			ResultSetMetaData rsmd = rs.getMetaData();
			while(rs.next()) {
			  json.put(rs.getString("titolo"));
			}
			return Response.status(200).entity(json.toString()).build();
		
		
	}
}
