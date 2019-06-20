import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.util.ArrayList;


class ServiceChat extends Thread {

	Socket socket;
	final static int NBCLIENTSMAX = 1000;
	BufferedReader input;
	static PrintStream output[] = new PrintStream[NBCLIENTSMAX];
	static int nbClients = 0;
	static int clientID = 0;
	public int myclientID;
	static ArrayList<Integer> listClientConnected = new ArrayList<Integer>();


	boolean serviceActif, authentification=false;
	String message;

	private String login, pass;

	public ServiceChat( Socket socket ) {
		this.socket = socket;
		serviceActif = true;
		this.start();

	}

	public synchronized void connexion(){
		SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
		Date now = new Date();
		System.out.println(time.format(now) + " : un client s'est connecté");
		nbClients++;
		clientID++;
		myclientID = clientID;
		listClientConnected.add(clientID);
	}

	public synchronized void deconnexion(int indice) {
		SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
		Date now = new Date();
		System.out.println(time.format(now) + " : un client s'est déconnecté");
		nbClients--;

		listClientConnected.remove(indice-1);
		try{
			socket.close();
		} catch(Exception e){}
	}

	private static boolean isValid(String login, String pass) {

		boolean autorisation = false;
		try {
			Scanner sc = new Scanner(new File("utilisateurs.txt"));

			while(sc.hasNext()){
				if(sc.nextLine().equals(login+" "+pass)){
					autorisation=true;
					break;
				}
			}

		} catch (FileNotFoundException e) {
			System.err.println("Le fichier des utilisateurs n'a pas été trouvé");
		}
		return autorisation;

	}

	private static boolean loginExist(String login) {

		boolean autorisation = false;
		try {
			Scanner sc = new Scanner(new File("utilisateurs.txt"));

			while(sc.hasNext()){
				if(sc.nextLine().equals(login)){
					autorisation=true;
					break;
				}
			}

		} catch (FileNotFoundException e) {
			System.err.println("Le fichier des utilisateurs n'a pas été trouvé");
		}
		return autorisation;

	}

	public void run() {

		if(nbClients < NBCLIENTSMAX) {

			try {

				connexion();

				input = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
				output[clientID] = new PrintStream( socket.getOutputStream() );

				while(!authentification) {

					output[clientID].println("Entrez votre login :");
					System.out.println("En attente d'authentification");
					output[clientID].flush();
					login = input.readLine();

					output[clientID].println("Entrez votre mot de passe :");
					output[clientID].flush();
					pass = input.readLine();

					System.out.println("Authentification...");

					/*					if(loginExist(login)== false){
										System.out.println("first connexion");
										try
										{

										FileWriter fw = new FileWriter("utilisateurs.txt", true);
										fw.write(login + pass);
										fw.close();
										break;
										}
										catch(IOException e)
										{
										System.out.println("" + e.getMessage());
										}


										}
					 */

					if(isValid(login, pass)) {

						System.out.println(login +" vient de se connecter ");
						output[clientID].print("Hello " + login);
						output[clientID].print("\n -> Il y a " + nbClients + " clients connectés sur le chat \n");
						output[clientID].flush();
						authentification = true;
					}
					else {output[clientID].println("erreur d'authentification"); output[clientID].flush();}

				}
				StringTokenizer st;
				String[] op;
				int j;


				while(serviceActif) {
					message = input.readLine();
					st = new StringTokenizer(message);
					op = new String[st.countTokens()];
					j=0;

					while (st.hasMoreTokens()) {

						op[j] = st.nextToken();
						j+=1;
					}

					System.out.print(message);
					

					switch (op[0]) {
						case "quit":
							output[myclientID].println("console : tu quittes");
							deconnexion();
							break;
						case "list":
							output[myclientID].println("tu listes");
							break;
						case "msg":
							output[myclientID].println("tu envoies un msg");
							break;
						case "file":
							output[myclientID].println("tu envoies un fichier");
							break;
						default:
							//broadcast getMessage

							if(listClientConnected.size()>0) {
								for(int i = 0; i < listClientConnected.size(); i++) {

									if(message == null){

										deconnexion(myclientID);
										break;
									}


									if(listClientConnected.get(i) != myclientID){

										output[listClientConnected.get(i)].println(login + " :" + message);
										output[listClientConnected.get(i)].flush();

									}

								}
							}
							break;
					}








				}

			} catch (IOException e){System.exit(1);}

			finally{

				try{
					socket.close();
				}
				catch(IOException e){System.err.println("Erreur serveur");}
			}

		}
		else {}

	}
}


public class ServerChat {

	public static void main( String[] args ) {

		int port;
		if(args.length<=0) port= 12345; // port par défaut
		else port = Integer.parseInt(args[0]); // sinon port en argument

		boolean serveurActif = true;
		PrintWriter out;

		ServerSocket receive;
		Socket socket;

		System.out.println("Serveur");
		System.out.println("----------------------------------------");

		try {
			receive = new ServerSocket(port);

			while(serveurActif) {

				socket = receive.accept();
				System.out.println( "nouvelle connexion port : " + receive.getLocalPort() );
				out = new PrintWriter(socket.getOutputStream());
				out.println("Bienvenu sur le serveur !");
				out.flush();

				new ServiceChat( socket );

			}
			receive.close();

		} catch( IOException e ) {
			System.out.println("" + e.getMessage());
			System.exit(1);

		}

	}
}
