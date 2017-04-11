<%@page import="java.util.Enumeration"%>
<%@page import="java.util.Hashtable"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="java.security.MessageDigest"%>
<%@page import="javax.crypto.spec.IvParameterSpec"%>
<%@page import="java.security.spec.AlgorithmParameterSpec"%>
<%@page import="javax.crypto.spec.SecretKeySpec"%>
<%@page import="java.io.OutputStream"%>
<%@page import="java.io.InputStream"%>
<%@page import="javax.crypto.Cipher"%>
<%@page import="javax.crypto.CipherInputStream"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Response Handler</title>
</head>
<body>
	<%!
		String key = "7A2540D168D34E23A5A1E57957470716";//Put your working key(AES key)
		public String decrypt(String hexCipherText){
	        try {
		        SecretKeySpec skey = new SecretKeySpec(getMD5(key), "AES");
		        byte[] buf = new byte[1024];
		        byte[] iv = new byte[]{
		            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f
		        };
		        AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
	            Cipher dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	
	            // CBC requires an initialization vector
	            dcipher.init(Cipher.DECRYPT_MODE, skey, paramSpec);
	            
	            return new String(dcipher.doFinal(hexToByte(hexCipherText)), "UTF-8");
	        } catch (Exception e) {
	             e.printStackTrace();
	        }
	        return null;
	    }
	    
	    private static byte[] getMD5(String input){
	        try{
	            byte[] bytesOfMessage = input.getBytes("UTF-8");
	            MessageDigest md = MessageDigest.getInstance("MD5");
	            return md.digest(bytesOfMessage);
	        }  catch (Exception e){
	             return null;
	        }
	    }
	    
	    public static byte[] hexToByte( String hexString){
	        int len = hexString.length();
	        byte[] ba = new byte[len / 2];
	        for (int i = 0; i < len; i += 2) {
	            ba[i/2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i+1), 16));
	        }
	        return ba;
	    }
	%>
	<%
		String encResp= request.getParameter("encResp");
		String decResp = decrypt(encResp);	
		String format = "{";	
		StringTokenizer tokenizer = new StringTokenizer(decResp, "&");
		Hashtable hs=new Hashtable();
		String pair=null, pname=null, pvalue=null;
		while (tokenizer.hasMoreTokens()) {
			pair = (String)tokenizer.nextToken();
			if(pair!=null) {
				StringTokenizer strTok=new StringTokenizer(pair, "=");
				pname=""; pvalue="";
				if(strTok.hasMoreTokens()) {
					pname=(String)strTok.nextToken();
					if(strTok.hasMoreTokens())
						pvalue=(String)strTok.nextToken();
					
					hs.put(pname, pvalue);
				}
			}
		}
		        Enumeration enumeration = hs.keys();
	          while(enumeration.hasMoreElements()) {
	    	String key = (String)enumeration.nextElement();
	            	format = format + key + ":'"+hs.get(key)+"',";
	         }
	          format = format.substring(0,format.length()-1);
	          format = format+"}";	
	%>
	 <script src="https://code.jquery.com/jquery-1.9.1.min.js"></script>
	<script type="text/javascript">
  $(document).ready(function() {
	  var jsonstr=<%=format%>;
	var jsonString = JSON.stringify(jsonstr);
 $.ajax({
	type:"POST",
	 url: "http://reachout.to/reachout/api/transactions",
	 data: jsonString,
	 contentType: "application/json;",
     dataType :'json',
	success: successmsg,
	error: function(XMLHttpRequest, textStatus, errorThrown){
		 alert("status code for error response "+XMLHttpRequest.status);
        alert("text error for response "+XMLHttpRequest.responseText);
	}
   });
      function successmsg(data){
		  document.write("Your request is successful: " +data.status_message);
    }
});
</script>
</body>
</html>
