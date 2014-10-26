import java.io.*;
import java.net.*;

class Client
{
	public static void main(String argv[]) throws Exception
	{
		String sentence;
		String modifiedSentence;
		BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
		sentence = inFromUser.readLine();
		modifiedSentence = Client.messageServer(sentence);
		System.out.println("FROM SERVER: " + modifiedSentence);

	}
	public static String messageServer(String input) throws Exception
	{
		Socket clientSocket = new Socket("localhost", 6789);
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		outToServer.writeBytes(input + '\n');
		String result = inFromServer.readLine();
		clientSocket.close();
		return result;
	}
}