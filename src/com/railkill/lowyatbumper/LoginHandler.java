package com.railkill.lowyatbumper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;
import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.apache.http.client.ClientProtocolException;




import android.os.AsyncTask;

public class LoginHandler extends AsyncTask<String, Void, String[][]> {
	
	private List<String> cookies;
	private HttpsURLConnection conn;
	private final String USER_AGENT = "Mozilla/5.0";
	private ArrayList<String> bumpList;
	public AsyncResponse delegate = null;
	  
	public LoginHandler() {
		// empty constructor
	}
	
	public LoginHandler(ArrayList<String> bumping) {
		this.bumpList = bumping;
	}
	
	 protected String[][] doInBackground(String... urls) {
		 String LOWYAT_LOGIN = urls[0];
         String username = urls[1];
         String password = urls[2];
         
         try {        	         	 
        		// make sure cookies is turn on
        		CookieHandler.setDefault(new CookieManager());
        	 
        		// 1. Send a "GET" request, so that you can extract the form's data.
        		String page = GetPageContent("https://forum.lowyat.net/index.php?act=Login&CODE=00");
        		String postParams = getFormParams(page, username, password);
        	 
        		// 2. Construct above post's content and then send a POST request for
        		// authentication
        		sendPost(LOWYAT_LOGIN, postParams);
        	 
        		// IF BUMPTOPICS IS NULL, THEN RETRIEVE USER INFORMATION AND TOPICS TO DISPLAY
        		if (bumpList == null) {
        			// 3. Load a simple page to see if user is actually logged in.
            		// Tested and works. Logged in as railkill and can see my shit.
            		String faqPage = GetPageContent("https://forum.lowyat.net/index.php?act=faq");
            		
            		String member_id = "";
            		String member_auth_key = "";
            		// Search the page source for member_id, it only exists when the user is logged in.
            		int loggedint = faqPage.indexOf("var member_id = ");
            		if (loggedint != -1) { // If not found, means the user is not logged in.
            			member_id = faqPage.substring(loggedint + 16);
                		member_id = member_id.substring(0, member_id.indexOf(";"));
                		member_auth_key = faqPage.substring(faqPage.indexOf("var member_auth_key = \"") + 23);
                		member_auth_key = member_auth_key.substring(0, member_auth_key.indexOf("\";"));
                		
                		// 4. Once the member_id is found, show all topics of the user.
                		String topicsPage = GetPageContent("https://forum.lowyat.net/index.php?act=Search&CODE=gettopicsuser&mid=" + member_id);
                		
                		// 5. Parse the page to find topics
                		Document doc = Jsoup.parse(topicsPage);
                		Elements topics = doc.select("a[href*=https://forum.lowyat.net/index.php?showtopic=]");
                		Elements forums = doc.select("a[href*=https://forum.lowyat.net/index.php?showforum=]");
                		Elements lastaction = doc.select("span[class=lastaction]:matches((Bumped)|(Created))");
                		// For every topic, extract topic title, status (bumped 5m ago) and bump link.
                		String[][] topicArray = new String[topics.size() - 1][6];
                		for (int i = 0; i < topics.size() - 1; i++) {
                			topicArray[i][0] = topics.get(i).html(); // Assign title
                			
                			String t = topics.get(i).attr("href");
                		    String topic_id = t.substring(t.indexOf("showtopic=") + 10);
                		    topic_id = topic_id.substring(0, topic_id.indexOf("&hl="));
                		    topicArray[i][1] = topic_id; // Assign topic ID
                		    
                		    String f = forums.get(i).attr("href");
                		    String forum_id = f.substring(f.indexOf("showforum=") + 10);
                		    topicArray[i][2] = forum_id; // Assign forum ID
                		    
                		    topicArray[i][3] = lastaction.get(i).html(); // Assign status
                		    topicArray[i][4] = "N"; // N - representing no bump
                		    topicArray[i][5] = member_auth_key;
                		}
                		
                		// YOU CAN ONLY BUMP THREADS IN TRADE ZONE OR CLASSIFIED.
                		return topicArray;
            		}
            		else {
            			// User not logged in. Handle failure.
            			return null;
            		}
        		}
        		else {
        			// BUMPTOPICS IS NOT NULL, THERE'S SOMETHING TO BUMP
        			for (int i = 0; i < bumpList.size(); i++) {
        				GetPageContent(bumpList.get(i));
        			}
        			// return something maybe?
        			return null;
        		}
        	
        		
	        } catch (ClientProtocolException e) {
	            e.printStackTrace();
	            return null;
	        } catch (Exception e) {
	        	e.printStackTrace();
	        	return null;
	        }
     }

     protected void onPostExecute(String[][] result) {
    	 delegate.processFinish(result);
     }
     
     private void sendPost(String url, String postParams) throws Exception {
    	 
    		URL obj = new URL(url);
    		conn = (HttpsURLConnection) obj.openConnection();
    	 
    		// Acts like a browser
    		conn.setUseCaches(false);
    		conn.setRequestMethod("POST");
    		conn.setRequestProperty("Host", "forum.lowyat.net");
    		conn.setRequestProperty("User-Agent", USER_AGENT);
    		conn.setRequestProperty("Accept",
    			"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
    		conn.setRequestProperty("Accept-Language", "en-GB,en-US;q=0.8,en;q=0.6");
    		for (String cookie : this.cookies) {
    			conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
    		}
    		conn.setRequestProperty("Connection", "keep-alive");
    		conn.setRequestProperty("Referer", "https://forum.lowyat.net/index.php?act=Login&CODE=00");
    		conn.setRequestProperty("Origin", "https://forum.lowyat.net");
    		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    		//conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()));
    	 
    		conn.setDoOutput(true);
    		conn.setDoInput(true);
    	 
    		// Send post request
    		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
    		wr.writeBytes(postParams);
    		wr.flush();
    		wr.close();
    	 
    		//int responseCode = conn.getResponseCode();
    	 
    		BufferedReader in = 
    	             new BufferedReader(new InputStreamReader(conn.getInputStream()));
    		String inputLine;
    		StringBuffer response = new StringBuffer();
    	 
    		while ((inputLine = in.readLine()) != null) {
    			response.append(inputLine);
    		}
    		in.close();
    	 
     }
    	 
	  private String GetPageContent(String url) throws Exception {
    		URL obj = new URL(url);
    		conn = (HttpsURLConnection) obj.openConnection();
    	 
    		// default is GET
    		conn.setRequestMethod("GET");
    	 
    		conn.setUseCaches(false);
    	 
    		// act like a browser
    		conn.setRequestProperty("User-Agent", USER_AGENT);
    		conn.setRequestProperty("Accept",
    			"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
    		conn.setRequestProperty("Accept-Language", "en-GB,en-US;q=0.8,en;q=0.6");
    		if (cookies != null) {
    			for (String cookie : this.cookies) {
    				conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
    			}
    		}
    		//int responseCode = conn.getResponseCode();
    	 
    		BufferedReader in = 
    	            new BufferedReader(new InputStreamReader(conn.getInputStream()));
    		String inputLine;
    		StringBuffer response = new StringBuffer();
    	 
    		while ((inputLine = in.readLine()) != null) {
    			response.append(inputLine);
    		}
    		in.close();
    	 
    		// Get the response cookies
    		setCookies(conn.getHeaderFields().get("Set-Cookie"));
    	 
    		return response.toString();
    	 
	  }
    	 
	  public String getFormParams(String html, String username, String password)
			throws UnsupportedEncodingException {
    	 
    	 
    		Document doc = Jsoup.parse(html);
    	 
    		// Google form id
    		Element loginform = doc.select("form[name=LOGIN]").first();
    		Elements inputElements = loginform.getElementsByTag("input");
    		List<String> paramList = new ArrayList<String>();
    		for (Element inputElement : inputElements) {
    			String key = inputElement.attr("name");
    			String value = inputElement.attr("value");
    	 
    			if (key.equals("UserName"))
    				value = username;
    			else if (key.equals("PassWord"))
    				value = password;
    			else if (key.equals("referer"))
    				value = "https://forum.lowyat.net/index.php?";
    			
    			// Exclude checkbox parameters
    			if (key.equals("CookieDate") || key.equals("Privacy") || key.equals("use_ssl")) {
    				// Do nothing.
    			}
    			else {
    				paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
    			}
    		}
    	 
    		// build parameters list
    		StringBuilder result = new StringBuilder();
    		for (String param : paramList) {
    			if (result.length() == 0) {
    				result.append(param);
    			} else {
    				result.append("&" + param);
    			}
    		}
    		return result.toString();
      }
    	 
	  public List<String> getCookies() {
		return cookies;
	  }
    	 
	  public void setCookies(List<String> cookies) {
		this.cookies = cookies;
	  }
     
        
}

     
