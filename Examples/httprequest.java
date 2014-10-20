
String username = "username";
String password = "monkey";

String host = "http://web.engr.illinois.edu/~dyel-net/readquery.php";
List<BasicNameValuePair> nvps = null;
HttpParams httpParameters = new BasicHttpParams();

int timeoutConnection = 20000;
HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
int timeoutSocket = 20000;
HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

HttpClient httpclient = new DefaultHttpClient(httpParameters);
HttpPost httpPost = new HttpPost(host);

nvps = new ArrayList<BasicNameValuePair>();  
nvps.add(new BasicNameValuePair("user", "dyel-net_admin" ));
nvps.add(new BasicNameValuePair("pw", "teamturtle" ));
nvps.add(new BasicNameValuePair("sql", SQL));

httpPost.setEntity(new UrlEncodedFormEntity(nvps));
HttpResponse response = httpclient.execute(httpPost);
String htmlresponse = EntityUtils.toString(response.getEntity());

