/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package servertcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Alessandro
 */
public class ServerTcp {

    /**
     * @param args the command line arguments
     */
    private static long lastIndex = 0;
    private static RemoveClient tt = null;
    private static Timer t = new Timer();
    
    public static void main(String[] args) throws IOException, InterruptedException {
        try (// socket del server, in ascolto sulla porta 9999
                // la differenza con la socket del client è che questa socket può restare in
                // attesa di connessione (accept del TCP)
        ServerSocket ss = new ServerSocket(9999)) {
            tt = new RemoveClient();
            t.schedule(tt, 0, 100);

            while (true) {
                // Creo la socket di connessione al client specifico
                // per farlo metto la ServerSocket in attesa di richiesta di connessione
                System.out.println("Server in attesa...");
                Socket s = ss.accept();
                System.out.println("Richiesta di connessione ricevuta da: " + s.getRemoteSocketAddress());
                tt.addClients(new ServerClient(s));
                for (int i = 0; i < tt.getC().size(); i++) {
                    tt.getC().get(i).sendMsg("started");
                    String cl = "client:";
                    for(ServerClient client : tt.getC()){
                        client.otherClients = tt.getC();
                        cl += client.getName();
                    }
                    tt.getC().get(i).sendMsg(cl);
                }
            }
        }
    }
    
    private static class RemoveClient extends TimerTask{
        private LinkedList<ServerClient> clientList = new LinkedList<ServerClient>();
        private LinkedList<ServerClient> removable = new LinkedList<ServerClient>();

        public RemoveClient(){}

        public void addClients(ServerClient s){
            this.clientList.add(s);
        }

        public void updateClients(LinkedList<ServerClient> s){
            this.clientList = s;
        }

        public LinkedList<ServerClient> getC(){
            return this.clientList;
        }

        @Override
        public void run(){
            for (int i = 0; i < clientList.size(); i++) {
                if(clientList.get(i).terminated()){
                    removable.add(clientList.get(i));
                }
            }

            clientList.removeAll(removable);
        }
    }
}
