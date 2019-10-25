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
                            postHttp(requestedElement);
                        }else if(requestType.equals("HEAD")) {
                            headHttp(requestedElement);
                        }else if(requestType.equals("PUT")) {
                            putHttp(requestedElement);
                        }else if(requestType.equals("DELETE")) {
                            deleteHttp(requestedElement);
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

    /**Cette Méthode permet de traiter une requete GET envoyé par un client.
     * Si le fichier n'existe pas on envoie a l'utiliasteur une erreur 404 Not Found et affiche la page html page not found.
     * Si le fichier existe on le lit et on ecrit dans le body de la reponse le fichier. ET on envoie dans le header 200 OK
     * Si il y a une erreur de lecture du fichier ou autre on renvera une erreur 500 qui indique une erreur interne au serveur.
     * @param filename
     */
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

    /** La méthode Post permet de mettre a jour une resource.
     *
     * Si le fichier exisiste on se place a la fin de ce dernier et on ajoute le reste. On renvoie au client 200 OK
     * Si le ficher existe pas on le crée et on y ajoute ce qu'il faut y ajouter. On renvoie ensuite 201 Created.
     * En cas d'erreur interne au serveur. On essaye d'envoyer au clienr qu'une erreur interne au projet c'est produite et on envoie 500 Internal serveur error
     *
     * @param filename
     * @throws IOException
     */
    protected void postHttp( String filename) throws IOException {
        System.out.println("Call to POST " + filename);
         try {
             File file = new File(filename);
             boolean existed = file.exists();

             // Ouverture du fichier en mode écriture a la fin.
             BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(file,existed));

             byte[] buffer = new byte[256];

             //Ecrire le body dans le fichier.
             while(in.available() > 0) {
                 int nbRead = in.read(buffer);
                 fileOut.write(buffer, 0, nbRead);
             }
             fileOut.flush();
             // Fermetude du fichier
             fileOut.close();

             // Envoyer le header en fonction de ce qui c'est passé
             if(existed) {
                 // Réusite
                 out.write(createHeader("200 OK").getBytes());
             } else {
                 // Création de la resource
                 out.write(createHeader("201 Created").getBytes());
             }

             //Envoyer le header
             out.flush();

         } catch (FileNotFoundException e) {
             e.printStackTrace();
            // Si il yb a une erreur on envoie au client l'erreur.
             out.write(createHeader("500 Internal Server Error").getBytes());
             out.flush();
         } catch (IOException e) {}
    }

    /** La méthode Head permet de récupérer seulement le header d'une requete. Elle est utilise pour avoir les informations importantes du fichier mais sans le contenu du body.
     * Par exemple pour recevoir la taille du fichier ou type de fichier.
     * Si tout ce passe bien on rénvoie un header avec les infos et un code 200 OK.
     * Si le fichier n'existe pas on renvoie 404 page not found.
     * Si une erreur interne au seveur se produit on tente d'envoyer au client le code 500 Internal serveur error.
     *
     * @param filename
     */
    protected void headHttp(String filename) {
        System.out.println("HEAD " + filename);
        try {
            // On regarde si le fichier existe
            File resource = new File(filename);
            if(resource.exists() && resource.isFile()) {
                // Si il existe on envoie le header correspondant. sans le corps car c'est un head
                out.write(createHeader("200 OK", filename, resource.length()).getBytes());
            } else {
                // Envoi du Header signalant une erreur sans le corps car c'est un head
                out.write(createHeader("404 Not Found").getBytes());
            }
            // envoie des données
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                // En cas d'erreur on essaie d'avertir le client
                out.write(createHeader("500 Internal Server Error").getBytes());
                out.flush();
            } catch (Exception e2) {};
        }
    }

    protected void putHttp(String filename) {
        System.out.println("PUT " + filename);
        try {
            File resource = new File(filename);
            boolean existed = resource.exists();

            // Efface le contenu fichier
            PrintWriter pw = new PrintWriter(resource);
            pw.close();

            // Flux d'criture sur le fichier
            BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(resource));

            // Ecrire dans le fichier
            byte[] buffer = new byte[256];
            while(in.available() > 0) {
                int nbRead = in.read(buffer);
                fileOut.write(buffer, 0, nbRead);
            }
            // Envoyer sur le fichier
            fileOut.flush();

            //Fermeture du flux d'ecriture
            fileOut.close();

            // Envoi du Header (pas besoin de corps)
            if(existed) {
                // Si il existe on a bien réecrit dessus donc modifier le contenu
                out.write(createHeader("204 Modified").getBytes());
            } else {
                // On indique que l'on a créer le fichier
                out.write(createHeader("201 Created").getBytes());
            }
            // Envoi au client
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            // En cas d'erreur on essaie d'avertir le client
            try {
                out.write(createHeader("500 Internal Server Error").getBytes());
                out.flush();
            } catch (Exception e2) {};
        }
    }

    /**
     * Suppression d'un fichier sur le Serveur Http, en cas de non existence du fichier une erreur 404 est renvoyé, si
     * le fichier n'es pas accessible une erreur 403 est renvoyé. Dans le cas d'une suppression réussie un message 204
     * sera envoyé.
     * @param filename
     */
    protected void deleteHttp(String filename) {
        System.out.println("DELETE " + filename);
        try {
            File resource = new File(filename);
            // Suppression du fichier
            boolean deleted  = false;
            boolean existed = resource.exists();
            if(existed && resource.isFile()) {
                deleted = resource.delete();
            }

            // Envoi du Header
            if(deleted) {
                // Le ficher a été suprrimé corectement mais on a rien a renvoyer
                out.write(createHeader("204 File Deleted").getBytes());
            } else if (!existed) {
                // Le fichier n'a pas été trouvé sur le seveur
                out.write(createHeader("404 Not Found").getBytes());
            } else {
                // Erreur dans la suppression ou dans l'acces de la resource
                out.write(createHeader("403 Forbidden").getBytes());
            }
            // on envoie tout
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            // En cas d'erreur on essaie d'avertir le client
            try {
                out.write(createHeader("500 Internal Server Error").getBytes());
                out.flush();
            } catch (Exception ignored) {};
        }
    }





}
