package myPack;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

class Recommenders {
	
	static private class Topic{
		String name;
		int value;
		
		public Topic(String name, int value) {
			this.name = name;
			this.value = value;
		}
		
		 @Override
		 public String toString() {
		      return "<" + this.name + ", " + this.value + ">";
		 }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Topic other = (Topic) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
	}
	
	static class Personality{
		Double empathy;
		Double agreeableness;
		Double conscientiousness;
		Double extroversion;
		Double neuroticism;
		Double openness;
		
		@Override
		public String toString() {
		      return "[emp:" + this.empathy + ", agr:" + this.agreeableness + ", con:" + this.conscientiousness +
		    		  ", ext:" + this.extroversion + ", neu:" + this.neuroticism + ", ope:" + this.openness + "]";
		 }
		
		public String maxbig5Name() {
			String[] traits = {"agreeableness", "conscientiousness", "extroversion", "neuroticism" ,"openness"};
			Double[] values = {this.agreeableness, this.conscientiousness, this.extroversion, this.neuroticism, this.openness};
			int maxAt = 0;
			for (int i = 1; i < values.length; i++) {
			    maxAt = values[i] > values[maxAt] ? i : maxAt;
			}
			return traits[maxAt];
		}
		
		public Double maxbig5Value() {
			Double[] values = {this.agreeableness, this.conscientiousness, this.extroversion, this.neuroticism, this.openness};
			int maxAt = 0;
			for (int i = 1; i < values.length; i++) {
			    maxAt = values[i] > values[maxAt] ? i : maxAt;
			}
			return values[maxAt];
		}
	}
	

	
	public static JSONArray emotionRecommender (JSONObject inputJSON) throws SQLException {
		
		final int N_OF_RECOMMENDATIONS = 10;
		
		Connection conn = null;
		
		try{
			conn = DBConnection.getConnection();
		}catch(Exception e) {}
		
		
		
		
		Statement st = conn.createStatement();
		
		JSONArray topicsArr = inputJSON.getJSONObject("data").getJSONArray("topics");
		JSONObject jsonBehavior = inputJSON.getJSONObject("data").getJSONObject("behavior");
	 	
	 	// GET TOPICS FROM INPUT JSON
		List<Topic> userTopics = new ArrayList<Topic>();
		int processedWords = 0;
	 	for (int i = 0; i < topicsArr.length(); i++) {
            String 	key = topicsArr.getJSONObject(i).getString("key");
            int 	value = Integer.parseInt(topicsArr.getJSONObject(i).getString("value"));
            processedWords += value;
            
            // cut topic string if _ is found
            int iend = key.indexOf("_");
            String topicName = "";
            if (iend != -1) topicName = key.substring(0 , iend);
            else topicName = key;
            
            // check if topic already inserted, then add value
            Topic t = new Topic(topicName,value);
            if(userTopics.contains(t)) 
            	userTopics.get(userTopics.indexOf(t)).value += value;
            else 
            	userTopics.add(t);
        }
	 		 	
	 	// GET USER BEHAVIOR FROM INPUTJSON
	 	Personality p = new Personality();
	 	p.empathy = 	jsonBehavior.getDouble("empathy");
	 	p.agreeableness = jsonBehavior.getDouble("agreeableness");
	 	p.conscientiousness = jsonBehavior.getDouble("conscientiousness");
	 	p.extroversion = jsonBehavior.getDouble("extroversion");
	 	p.neuroticism = jsonBehavior.getDouble("neuroticism");
	 	p.openness = jsonBehavior.getDouble("openness");
	 	
	 	
	 	JSONArray json = new JSONArray();
	 	String selectFields = "eventi.link,"
	 			+ "eventi.titolo,"
	 			+ "eventi.comune,"
	 			+ "eventi.posto_nome,"
	 			+ "eventi.posto_link,"
	 			+ "eventi.data_da,"
	 			+ "eventi.data_a,"
	 			+ "eventi.descrizione,popolarita";
	 	
	 	
	 	for (Topic x : userTopics) {
	 		String query = "";
	 		int normalizedValue = Math.round((float)(x.value * N_OF_RECOMMENDATIONS) / processedWords);
	 		System.out.println(x.name + " " + ((float)x.value * N_OF_RECOMMENDATIONS) / processedWords + " -> " + normalizedValue );
	 		switch (x.name) {
	 		case "tech":
	 			System.out.println("case: tech");
	 			if(p.neuroticism + (p.neuroticism * p.empathy / 78) > 0.5547)
	 				query = "SELECT  " + selectFields + "  FROM eventi WHERE geek = 1 AND popolarita < 180 ORDER BY data_da DESC LIMIT " + normalizedValue;
	 			else if (p.neuroticism + (p.neuroticism * p.empathy / 78) < 0.5547 || p.openness + (p.openness * p.empathy / 78) < 0.7573)
	 				query = "SELECT  " + selectFields + "  FROM eventi WHERE geek = 1 AND popolarita > 180 ORDER BY data_da DESC LIMIT " + normalizedValue;
	 			else
	 				query = "SELECT  " + selectFields + "  FROM eventi WHERE geek = 1 ORDER BY data_da DESC LIMIT " + normalizedValue;
	 			break;
	 		case "sport":
	 		case "travel":
	 			System.out.println("case: sport or travel (" + x.name +")");
	 			if(p.neuroticism + (p.neuroticism * p.empathy / 78) > 0.5547)
	 				query = "SELECT  " + selectFields + "  FROM eventi WHERE avventura = 1 AND popolarita < 180 ORDER BY data_da DESC LIMIT " + normalizedValue;
	 			else if (p.neuroticism + (p.neuroticism * p.empathy / 78) < 0.5547 || p.openness + (p.openness * p.empathy / 78) < 0.7573)
	 				query = "SELECT  " + selectFields + "  FROM eventi WHERE avventura = 1 AND popolarita > 180 ORDER BY data_da DESC LIMIT " + normalizedValue;
	 			else
	 				query = "SELECT " + selectFields + " FROM eventi WHERE avventura = 1 ORDER BY data_da DESC LIMIT " + normalizedValue;
	 			break;
	 		case "politics":
	 			System.out.println("case: politics");
	 			if(p.neuroticism + (p.neuroticism * p.empathy / 78) > 0.5547)
	 				query = "SELECT " + selectFields + " FROM eventi WHERE cittadinanza = 1 AND popolarita < 180 ORDER BY data_da DESC LIMIT " + normalizedValue;
	 			else if (p.neuroticism + (p.neuroticism * p.empathy / 78) < 0.5547 || p.openness + (p.openness * p.empathy / 78) < 0.7573)
	 				query = "SELECT " + selectFields + " FROM eventi WHERE cittadinanza = 1 AND popolarita > 180 ORDER BY data_da DESC LIMIT " + normalizedValue;
	 			else
	 				query = "SELECT " + selectFields + " FROM eventi WHERE cittadinanza = 1 ORDER BY data_da DESC LIMIT " + normalizedValue;
	 			break;
	 		case "music":
	 			System.out.println("case: music");
	 			switch (p.maxbig5Name()){
	 			case "openness":
	 				if(p.neuroticism + (p.neuroticism * p.empathy / 78) > 0.5547)
		 				query = "SELECT " + selectFields + " FROM eventi WHERE (jazz = 1 OR musica_classica = 1) AND popolarita < 180 ORDER BY data_da DESC LIMIT " + normalizedValue;
		 			else if (p.neuroticism + (p.neuroticism * p.empathy / 78) < 0.5547 || p.openness + (p.openness * p.empathy / 78) < 0.7573)
		 				query = "SELECT " + selectFields + " FROM eventi WHERE (jazz = 1 OR musica_classica = 1) AND popolarita > 180 ORDER BY data_da DESC LIMIT " + normalizedValue;
		 			else
		 				query = "SELECT " + selectFields + " FROM eventi WHERE jazz = 1 OR musica_classica = 1 ORDER BY data_da DESC LIMIT " + normalizedValue;
	 				break;
	 			case "conscientiousness":
	 				if(p.neuroticism + (p.neuroticism * p.empathy / 78) > 0.5547)
		 				query = "SELECT " + selectFields + " FROM eventi WHERE (concerti = 1 OR folklore = 1) AND popolarita < 180 ORDER BY data_da DESC LIMIT " + normalizedValue;
		 			else if (p.neuroticism + (p.neuroticism * p.empathy / 78) < 0.5547 || p.openness + (p.openness * p.empathy / 78) < 0.7573)
		 				query = "SELECT " + selectFields + " FROM eventi WHERE (concerti = 1 OR folklore = 1) AND popolarita > 180 ORDER BY data_da DESC LIMIT " + normalizedValue;
		 			else
		 				query = "SELECT " + selectFields + " FROM eventi WHERE concerti = 1 OR folklore = 1 ORDER BY data_da DESC LIMIT " + normalizedValue;
	 				break;
	 			case "extroversion":
	 				if(p.neuroticism + (p.neuroticism * p.empathy / 78) > 0.5547)
		 				query = "SELECT " + selectFields + " FROM eventi WHERE (concerti = 1 OR folklore = 1) AND popolarita < 180 ORDER BY data_da DESC LIMIT " + normalizedValue;
		 			else if (p.neuroticism + (p.neuroticism * p.empathy / 78) < 0.5547 || p.openness + (p.openness * p.empathy / 78) < 0.7573)
		 				query = "SELECT " + selectFields + " FROM eventi WHERE (concerti = 1 OR folklore = 1) AND popolarita > 180 ORDER BY data_da DESC LIMIT " + normalizedValue;
		 			else
		 				query = "SELECT " + selectFields + " FROM eventi WHERE concerti = 1 OR folklore = 1 ORDER BY data_da DESC LIMIT " + normalizedValue;
	 				break;
	 			case "agreeableness":
	 				if(p.neuroticism + (p.neuroticism * p.empathy / 78) > 0.5547)
		 				query = "SELECT " + selectFields + " FROM eventi WHERE concerti = 1 AND popolarita < 180 ORDER BY data_da DESC LIMIT " + normalizedValue;
		 			else if (p.neuroticism + (p.neuroticism * p.empathy / 78) < 0.5547 || p.openness + (p.openness * p.empathy / 78) < 0.7573)
		 				query = "SELECT " + selectFields + " FROM eventi WHERE concerti = 1 AND popolarita > 180 ORDER BY data_da DESC LIMIT " + normalizedValue;
		 			else
		 				query = "SELECT " + selectFields + " FROM eventi WHERE concerti = 1 ORDER BY data_da DESC LIMIT " + normalizedValue;
	 				break;
	 			case "neuroticism":
	 				query = "SELECT " + selectFields + " FROM eventi WHERE concerti = 1 AND popolarita < 180 ORDER BY data_da DESC LIMIT " + normalizedValue;
	 				break;
	 			default:
	 				query = "SELECT " + selectFields + " FROM eventi WHERE concerti = 1 ORDER BY data_da DESC LIMIT " + normalizedValue;
	 				break;	
	 			}
	 			break;
	 		case "style":
	 			System.out.println("case: style");
	 			if(p.extroversion + (p.extroversion * p.empathy / 78) > 0.715 || p.openness + (p.openness * p.empathy / 78) > 0.7574){
	 				if(p.neuroticism + (p.neuroticism * p.empathy / 78) > 0.5547)
		 				query = "SELECT " + selectFields + " FROM eventi WHERE vita_notturna = 1 AND popolarita < 180 ORDER BY data_da DESC LIMIT " + normalizedValue;
		 			else if (p.neuroticism + (p.neuroticism * p.empathy / 78) < 0.5547 || p.openness + (p.openness * p.empathy / 78) < 0.7573)
		 				query = "SELECT " + selectFields + " FROM eventi WHERE vita_notturna = 1 AND popolarita > 180 ORDER BY data_da DESC LIMIT " + normalizedValue;
		 			else
		 				query = "SELECT " + selectFields + " FROM eventi WHERE vita_notturna = 1 ORDER BY data_da DESC LIMIT " + normalizedValue;
	 			}
	 			else {
	 				if(p.neuroticism + (p.neuroticism * p.empathy / 78) > 0.5547)
		 				query = "SELECT " + selectFields + " FROM eventi INNER JOIN luoghi ON eventi.posto_nome = luoghi.nomeposto WHERE raffinato = 1 AND popolarita < 180 ORDER BY data_da DESC LIMIT " + normalizedValue;
		 			else if (p.neuroticism + (p.neuroticism * p.empathy / 78) < 0.5547 || p.openness + (p.openness * p.empathy / 78) < 0.7573)
		 				query = "SELECT " + selectFields + " FROM eventi INNER JOIN luoghi ON eventi.posto_nome = luoghi.nomeposto WHERE raffinato = 1 AND popolarita > 180 ORDER BY data_da DESC LIMIT " + normalizedValue;
		 			else
		 				query = "SELECT " + selectFields + " FROM eventi INNER JOIN luoghi ON eventi.posto_nome = luoghi.nomeposto WHERE raffinato = 1 ORDER BY data_da DESC LIMIT " + normalizedValue;
	 			}
	 			break;
	 		case "art":
	 			System.out.println("case: art");
	 			if(p.conscientiousness + (p.conscientiousness * p.empathy / 78) > 0.6973 && p.extroversion + (p.extroversion * p.empathy / 78) > 0.715) {
	 				if(p.neuroticism + (p.neuroticism * p.empathy / 78) > 0.5547)
		 				query = "SELECT " + selectFields + " FROM eventi INNER JOIN luoghi ON eventi.posto_nome = luoghi.nomeposto WHERE eventi.cultura = 1 AND libri = 1 AND popolarita < 180 ORDER BY data_da DESC LIMIT " + normalizedValue;
		 			else if (p.neuroticism + (p.neuroticism * p.empathy / 78) < 0.5547 || p.openness + (p.openness * p.empathy / 78) < 0.7573)
		 				query = "SELECT " + selectFields + " FROM eventi INNER JOIN luoghi ON eventi.posto_nome = luoghi.nomeposto WHERE eventi.cultura = 1 AND libri = 1 AND popolarita > 180 ORDER BY data_da DESC LIMIT " + normalizedValue;
		 			else
		 				query = "SELECT " + selectFields + " FROM eventi INNER JOIN luoghi ON eventi.posto_nome = luoghi.nomeposto WHERE eventi.cultura = 1 AND libri = 1 ORDER BY data_da DESC LIMIT " + normalizedValue;
	 			}
	 			else {
	 				if(p.neuroticism + (p.neuroticism * p.empathy / 78) > 0.5547)
		 				query = "SELECT " + selectFields + " FROM eventi INNER JOIN luoghi ON eventi.posto_nome = luoghi.nomeposto WHERE eventi.arte = 1 AND popolarita < 180 ORDER BY data_da DESC LIMIT " + normalizedValue;
		 			else if (p.neuroticism + (p.neuroticism * p.empathy / 78) < 0.5547 || p.openness + (p.openness * p.empathy / 78) < 0.7573)
		 				query = "SELECT " + selectFields + " FROM eventi INNER JOIN luoghi ON eventi.posto_nome = luoghi.nomeposto WHERE eventi.arte = 1 AND popolarita > 180 ORDER BY data_da DESC LIMIT " + normalizedValue;
		 			else
		 				query = "SELECT " + selectFields + " FROM eventi INNER JOIN luoghi ON eventi.posto_nome = luoghi.nomeposto WHERE eventi.arte = 1 ORDER BY data_da DESC LIMIT " + normalizedValue;
	 			}
	 			break;
	 		case "movie":
	 			System.out.println("case: movie");
	 			if(p.conscientiousness + (p.conscientiousness * p.empathy / 78) > 0.6973) {
	 				if(p.neuroticism + (p.neuroticism * p.empathy / 78) > 0.5547)
		 				query = "SELECT " + selectFields + " FROM eventi INNER JOIN luoghi ON eventi.posto_nome = luoghi.nomeposto WHERE eventi.teatro = 1 AND popolarita < 180 ORDER BY data_da DESC LIMIT " + normalizedValue;
		 			else if (p.neuroticism + (p.neuroticism * p.empathy / 78) < 0.5547 || p.openness + (p.openness * p.empathy / 78) < 0.7573)
		 				query = "SELECT " + selectFields + " FROM eventi INNER JOIN luoghi ON eventi.posto_nome = luoghi.nomeposto WHERE eventi.teatro = 1 AND popolarita > 180 ORDER BY data_da DESC LIMIT " + normalizedValue;
		 			else
		 				query = "SELECT " + selectFields + " FROM eventi INNER JOIN luoghi ON eventi.posto_nome = luoghi.nomeposto WHERE eventi.teatro = 1 ORDER BY data_da DESC LIMIT " + normalizedValue;
	 			}
	 			else {
	 				if(p.neuroticism + (p.neuroticism * p.empathy / 78) > 0.5547)
		 				query = "SELECT " + selectFields + " FROM eventi INNER JOIN luoghi ON eventi.posto_nome = luoghi.nomeposto WHERE cinema = 1 AND popolarita < 180 ORDER BY data_da DESC LIMIT " + normalizedValue;
		 			else if (p.neuroticism + (p.neuroticism * p.empathy / 78) < 0.5547 || p.openness + (p.openness * p.empathy / 78) < 0.7573)
		 				query = "SELECT " + selectFields + " FROM eventi INNER JOIN luoghi ON eventi.posto_nome = luoghi.nomeposto WHERE cinema = 1 AND popolarita > 180 ORDER BY data_da DESC LIMIT " + normalizedValue;
		 			else
		 				query = "SELECT " + selectFields + " FROM eventi INNER JOIN luoghi ON eventi.posto_nome = luoghi.nomeposto WHERE cinema = 1 ORDER BY data_da DESC LIMIT " + normalizedValue;
	 			}
	 			break;
	 		}
 			
 			System.out.println(query);
 			ResultSet rs = st.executeQuery(query);
 			ResultSetMetaData rsmd = rs.getMetaData();
 			int j = 0;
 			while(rs.next()) {
 				j++;
 			  int numColumns = rsmd.getColumnCount();
 			  JSONObject obj = new JSONObject();
 			  for (int i=1; i<=numColumns; i++) {
 			    String column_name = rsmd.getColumnName(i);
 			    //System.out.println(rsmd.getColumnName(i));
 			    obj.put(column_name, rs.getObject(column_name));
 			  }
 			  json.put(obj);
 			  System.out.println(x.name + " total: " + j);
 			}
 		 	
	 	}
	 	
	 	
	 	
	 	
	 
		
		
		
	 	

	 	System.out.println(p.toString());
	 	System.out.println(userTopics.toString());
	 	System.out.println("Total: " + processedWords);
	 	System.out.println("max: " + p.maxbig5Name() + "  " + p.maxbig5Value());
	 	
	 	return json;
	}
	
	

	
}
