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

@Path("services")
public class Services{

	@GET
	@Path("getEvents/{offset}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEvents(@DefaultValue("") @PathParam("place") String place, @DefaultValue("") @PathParam("range") String range, @DefaultValue("") @PathParam("interval") String interval, @PathParam("offset") String offset) throws Exception {
        Connection conn = null;
		/*SELECT elements from */
		try{
			conn = DBConnection.getConnection();
		}catch(Exception e) {}
		
		String q = "";
		
			Calendar date = Calendar.getInstance();
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
			q = "SELECT link,titolo,comune,posto_nome,posto_link,data_da,data_a,descrizione,popolarita FROM eventi WHERE data_a >= '"+formatter.format(date.getTime())+"' ";
			if (offset == null) offset = "0";
			int off = Integer.parseInt(offset);
			//System.out.println(off);
			
			q = q+ "ORDER BY data_a ASC ";
			if(off != 0) {
				q = q+ " OFFSET "+off+" ";
			}
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
	@Path("countEvents")
	@Produces(MediaType.APPLICATION_JSON)
	public Response countEvents(@DefaultValue("") @PathParam("place") String place, @DefaultValue("") @PathParam("range") String range, @DefaultValue("") @PathParam("interval") String interval, @DefaultValue("0") @PathParam("offset") String offset) throws Exception {
        Connection conn = null;
		/*SELECT elements from */
		try{
			conn = DBConnection.getConnection();
		}catch(Exception e) {}
		
		String q = "";
		
			Calendar date = Calendar.getInstance();
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
			q = "SELECT COUNT(link) as num FROM eventi WHERE data_a >= '"+formatter.format(date.getTime())+"' ";
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
