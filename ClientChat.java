import java.net.*;
import java.io.*;
import java.util.StringTokenizer;

/* How to use
   java ClientChat <host> <port>
   java ClientChat localhost 2222
 */

public class ClientChat extends Thread {

	BufferedReader inputConsole, inputNetwork;
	PrintStream outputConsole, outputNetwork;


	public ClientChat (String[] args) throws Exception { //gerer les exceptions

		Socket s = new Socket(args[0], Integer.parseInt(args[1]));

		initInputOutput(s);
		start();
		listenConsole();

	}

	public void run(){

		listenNetwork();
	}

	public static void main(String[] args) throws Exception {

		if (args.length < 2){
			System.err.println("Usage : <host> <port>");
		}

		else {
			new ClientChat(args);
		}
	}


	public void initInputOutput(Socket s) throws Exception {

		try{
			inputConsole = new BufferedReader (new InputStreamReader (System.in));
			outputConsole = new PrintStream(System.out);
			inputNetwork = new BufferedReader (new InputStreamReader (s.getInputStream()));
			outputNetwork = new PrintStream( s.getOutputStream() );

		} catch (IOException e) {
			System.out.print("erreur dans initInputOuput");
		}

	}

	public void listenConsole(){
		String msgConsole="";
		StringTokenizer st;
		String[] op;
		int i;

		try{
			while(msgConsole!=null){

				msgConsole = inputConsole.readLine();
				outputNetwork.println(msgConsole);
				st = new StringTokenizer(msgConsole);
				op = new String[st.countTokens()];
				i=0;

				while (st.hasMoreTokens()) {

					op[i] = st.nextToken();
					i+=1;
				}



				switch (op[0]) {
					case "quit":
						outputConsole.println("console : tu quittes");
						//intputConsole.close();
						outputNetwork.close();
						outputConsole.close();
						break;
					case "list":
						outputConsole.println("tu listes");
						break;
					case "msg":
						outputConsole.println("tu envoies un msg");
						break;
					case "file":
						outputConsole.println("tu envoies un fichier");
						break;
					default:
						break;
				}

			}
			outputNetwork.close();
			outputConsole.close();


		} catch (IOException e) {
			System.out.println("erreur dans listenConsole");
		}
	}

	public void listenNetwork(){
		String msgNetwork="";
		StringTokenizer st;
		String[] op;


		try {
			while(msgNetwork!=null){
				msgNetwork = inputNetwork.readLine();
				outputConsole.println(msgNetwork);
				st = new StringTokenizer(msgNetwork);
				op = new String[st.countTokens()];
				int i=0;


				while(st.hasMoreTokens()) {
					op[i] = st.nextToken();
					i+=1;
				}


				switch (op[0]) {
					case "quit":
						outputConsole.println("Network : au revoir");
						break;
					case "list":
						outputConsole.println("Network : tu listes");
						break;
					case "msg":
						outputConsole.println("Network : tu envoies un msg");
						break;
					case "file":
						outputConsole.println("Network : tu envoies un fichier");
						break;
					default:
						break;
				}



			}
			outputConsole.close();
			outputNetwork.close();

		} catch (IOException e) {
			System.out.println("erreur dans listenNetwork");
			System.err.println("Le serveur distant s'est déconnecté !");
		}


	}
}
