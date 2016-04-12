package edu.ap.jaxrs;

import java.util.Set;

import javax.json.JsonObject;
import javax.ws.rs.*;


import redis.clients.jedis.Jedis;

/* Redis statements 
 * SET author:1 "Jonas"
 * SET author:2 "Tim"
 * SADD quote:1 "Hallo"
 * SADD quote:1 "Mijn naam is Jonas"
 * SADD quote:2 "Mijn naam is Tim"*/


@Path("/quotes")
public class Quote {
	
	@GET
	@Produces({"text/html"})
	public String getQuotesHTML() {
		String htmlString = "<html><body>";
		try {
			Jedis jedis = new Jedis("localhost");
			Set<String> quoteSet = jedis.keys("quote:*");
			
			Object[] quoteArray = quoteSet.toArray();
			
			for(int i = 0; i<quoteArray.length; i++){
				
				Set<String> tempQuoteSet = jedis.smembers((String) quoteArray[i]);
				Object[] tempQuoteArray = tempQuoteSet.toArray();
				htmlString += "Quote"+(i+1)+" : \"" + tempQuoteArray[i] + "\"<br>";
			}
			
		}
		catch(Exception ex) {
			htmlString = "<html><body>" + ex.getMessage();
		}
		
		return htmlString + "</body></html>";
	}
	
	@POST
	@Consumes({"text/plain"})
	@Produces({"text/html"})
	public String getQuotesByAuthor(String author){
		String htmlString = "<html><body>";
		try {
			Jedis jedis = new Jedis("localhost");
			Set<String> authorSet = jedis.keys("author:*");
			
			Object[] authorArray = authorSet.toArray();
			
			String nummerAuthor = "0";
			
			for(int i = 0; i<authorArray.length; i++){
				String tempAuthor = jedis.get((String) authorArray[i]);
				String authorKey = (String) authorArray[i];
				if(tempAuthor.equals(author)){
					nummerAuthor = authorKey.split(":")[1];
				}
			}
			
			Set<String> quoteSet = jedis.smembers("quote:"+nummerAuthor);
			
			Object[] quoteArray = quoteSet.toArray();
			
			for(int i = 0; i<quoteArray.length; i++){
				htmlString += author + " Quote"+(i+1)+" : \"" + quoteArray[i] + "\"<br>";
			}
			
		}
		catch(Exception ex) {
			htmlString = "<html><body>" + ex.getMessage();
		}
		
		return htmlString + "</body></html>";
	}
	
	
	
}
