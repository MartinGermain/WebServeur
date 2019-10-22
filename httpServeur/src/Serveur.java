import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur
{
    //Chemins d'acces vers les fichier du serveur.
    // Seul fichier ou le client a acces.
    protected static final String RES_FOLDER = "resources";
    protected static final String FILE_NOT_FOUND = "resources/notfilenotfound.html";
    protected static final String HOME = "resources/index.html";

    //PARAMETRE EN RAPORT AVEC LA CONNEXION EN COURS
    protected Socket client = null;
    protected BufferedInputStream in = null;
    protected BufferedOutputStream out = null;

    protected void start(int port) {
        ServerSocket s;

        System.out.println("Serveur started on port " + port);

        try {
            // Création du socket pour ecouter le port / Requetes clients
            s = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("Waiting for connection");
        for(;;) {
            try {
                // Attente de connexion
                client = s.accept();
                System.out.println("Connection accepted from " + client.getInetAddress() + " IO Streams Opened");
                // Une fois la connexion faite on ouvre les flux.
                in = new BufferedInputStream(client.getInputStream());
                out = new BufferedOutputStream(client.getOutputStream());


                // Une fois que la connexion est établie on va lire le header puis la requette.

                // Lecture du header (Se terminant toujours par une ligne vide)
                // read the data sent. We basically ignore it,
                // stop reading once a blank line is hit. This
                // blank line signals the end of the client HTTP
                // headers.

                // C'est genre ca :
                /*Keep-Alive : [timeout=5, max=100]
                Accept-Ranges : [bytes]
                null : [HTTP/1.1 200 OK]*/

                // Bon un header est suivi par une ligne vide donc il faut repérer deux retour a la ligne
                // Soit \r \n soit lire ces deux bits deux fois d'affilé.
                // On stocke que reste dans une string.
                String header = new String();

                int bitCurent = '\0', bitPrecedent = '\0';
                boolean newline = false;
                while((bitCurent = in.read()) != -1 && !(newline && bitPrecedent == '\r' && bitCurent == '\n')) {
                    if(bitPrecedent == '\r' && bitCurent == '\n') {
                        newline = true;
                    } else if(!(bitPrecedent == '\n' && bitCurent == '\r')) {
                        newline = false;
                    }
                    bitPrecedent = bitCurent;
                    header += (char) bitCurent;
                }

                System.out.println("REQUEST : " + header);
                //On va lire la requete seulement si on a encore un bit a lire et si le header n'est pas vide.

                if( bitCurent != -1 && !header.isEmpty()) {
                    String[] request = header.split(" ");
                    String requestType = request[0];
                    String requestedElement = request[1].substring(1, request[1].length());

                    // Si il ne demande aucuns elements on l'envoie vers la page d'acceuil comme un serveur quoi
                    if (requestedElement.isEmpty()) {
                        //Envoie de la page d'accueil.
                        getHttp(HOME);
                        //Ensuite on verifie que l'on possède bien l'element demandé.
                    }else if (requestedElement.startsWith(RES_FOLDER)) {
                        // ON fait ensuite appele a la bonne méthode pour gérer chauque types de request sur l'element demandé.
                        if (requestType.equals("GET")){
                            getHttp(requestedElement);
                        }else if(requestType.equals("POST")) {

                        }else if(requestType.equals("HEAD")) {

                        }else if(requestType.equals("PUT")) {

                        }else if(requestType.equals("DELETE")) {

                        }else {
                            // Si la requète ne possède pas un type connu on renvoie une erreur.
                            // getBytes transforme la string du header en bytes.
                            out.write(createHeader("501 Not Implemented").getBytes());
                            out.flush(); // Envoie le message et clean.
                        }

                    }else {
                        // Si le ficher n'est pas dans files alors le client n'y a pas acces..
                        // getBytes transforme la string du header en bytes.
                        out.write(createHeader("403 Forbidden").getBytes());
                        out.flush(); // Envoie le message et clean.
                    }
                }else {
                    // Sinon la requete n'est pas bien écrite
                    // getBytes transforme la string du header en bytes.
                    out.write(createHeader("400 Bad Request").getBytes());
                    out.flush(); // Envoie le message et clean.
                }

                // Puis on referme le socket et on recommence.
                client.close();

            } catch (IOException e) {
                // Print les erreurs coté serveur
                e.printStackTrace();

                // Mais aussi avertir le client que ca marche pas coté serveur.
                try {
                    // Ecrire que ca ne marche pas
                    //out.write();
                    // Envoyer l'info
                    out.flush();
                } catch (IOException ex) {};

                try {
                    // Meme si il y a des erreurs ils faut fermer la connexion.
                    client.close();
                    // On mets tout a null car j'ai envie mais ca sert pas a grand chose
                    client = null;
                    in = null;
                    out = null;
                } catch (IOException ex) {};
            }
        }
    }

    public static void main(String args[]) {
        Serveur s = new Serveur();
        s.start(5000); // Accéder au port 5000 de localhost pour réaliser les requetes.
    }

    /** Cette méthode permet de renvoyer un header sans corps par exemple juste pour les
     * messages d'erreur.
     * @param headerRsps
     * @return Un string d'un header http.
     */
    protected String createHeader(String headerRsps) {
        // ON écrit un header pour notre reponse.
        String header = "HTTP/1.0 " + headerRsps + "\r\n";
        header += "Server: TPSOCKET\r\n";
        header += "\r\n";
        System.out.println("ANSWER HEADER :" + header);
        return header;
    }


    /** Cette méthode permet de créer une header et de lui ajouter un ficher en corps.
     * On oura aussi péciser le type de fichier ainsi que d'autres parametres dans ce header.
     * On précisera aussi la longeur de ce dernier.
     * Cette méthode est utiliser pour les reqest type get put ect...
     * @param headerRsps
     * @param filename
     * @param length
     * @return
     */
    protected String createHeader(String headerRsps, String filename, long length) {
        // ON écrit un header pour notre reponse.
        String header = "HTTP/1.0 " + headerRsps + "\r\n";
        if(filename.endsWith(".html") || filename.endsWith(".htm"))
            header += "Content-Type: text/html\r\n";
        else if(filename.endsWith(".mp4"))
            header += "Content-Type: video/mp4\r\n";
        else if(filename.endsWith(".png"))
            header += "Content-Type: image/png\r\n";
        else if(filename.endsWith(".jpeg") || filename.endsWith(".jpeg"))
            header += "Content-Type: image/jpg\r\n";
        else if(filename.endsWith(".mp3"))
            header += "Content-Type: audio/mp3\r\n";
        else if(filename.endsWith(".avi"))
            header += "Content-Type: video/x-msvideo\r\n";
        else if(filename.endsWith(".css"))
            header += "Content-Type: text/css\r\n";
        else if(filename.endsWith(".pdf"))
            header += "Content-Type: application/pdf\r\n";
        else if(filename.endsWith(".odt"))
            header += "Content-Type: application/vnd.oasis.opendocument.text\r\n";
        header += "Content-Length: " + length + "\r\n";
        header += "Server: TPSOCKET\r\n";
        header += "\r\n";
        System.out.println("ANSWER HEADER :");
        System.out.println(header);
        return header;
    }

    protected void getHttp(String filename)
    {
        System.out.println("Call to GET " + filename);
        try {
            // On vérifie que le fichier existe.
            File file = new File(filename);
            if (file.exists() && file.isFile()) {
                // ON crée le header qui indique que l'on a tourvé le fichier avant de l'envoyer.
                out.write(createHeader("200 OK", filename, file.length()).getBytes());
            } else {
                // Si le fichier n'existe pas en lui envoie la page file not found
                file = new File(FILE_NOT_FOUND);
                // Et on indique l'erreur dans le header
                out.write(createHeader("404 Not Found", FILE_NOT_FOUND, file.length()).getBytes());
            }

            // Ensuite on ouvre le fichier et on l'envoie au client.
            BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(file));
            // Envoi du corps http : le fichier ex html img ...
            // On va lire chaques bits du fichier et les ecrire dans notre sortie.
            byte[] buffer = new byte[256];
            int nbRead;
            while((nbRead = fileIn.read(buffer)) != -1) {
                out.write(buffer, 0, nbRead);
            }
            // Fermeture du flux de lecture du file
            fileIn.close();
            // ENVOIE PLUS CLEAR
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            // En cas d'erreur on averti le client
            try {
                out.write(createHeader("500 Internal Server Error").getBytes());
                out.flush();
            } catch (Exception e2) {};
        }
    }






}
