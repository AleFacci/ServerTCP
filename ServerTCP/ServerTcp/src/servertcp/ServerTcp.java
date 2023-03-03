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
    private static LinkedList<ServerClient> clientList = new LinkedList<ServerClient>();
    private static long lastIndex = 0;
    private static RemoveClient tt = null;
    private static Timer t = new Timer();
    
    public static void main(String[] args) throws IOException, InterruptedException {
        try (// socket del server, in ascolto sulla porta 9999
                // la differenza con la socket del client è che questa socket può restare in
                // attesa di connessione (accept del TCP)
        ServerSocket ss = new ServerSocket(9999)) {
            tt = new RemoveClient(clientList);
            t.schedule(tt, 0, 100);

            while (true) {
                // Creo la socket di connessione al client specifico
                // per farlo metto la ServerSocket in attesa di richiesta di connessione
                System.out.println("Server in attesa...");
                Socket s = ss.accept();
                System.out.println("Richiesta di connessione ricevuta da: " + s.getRemoteSocketAddress());
                clientList.add(new ServerClient(s,clientList));
                long tmp = clientList.indexOf(clientList.getLast());
                if (clientList.indexOf(clientList.getLast()) > 0) {
                    for (ServerClient c : clientList) {
                        c.sendMsg("started");
                        String cl = "client:";
                        for(ServerClient client : clientList){
                            cl += client.getName();
                        }
                        c.sendMsg(cl);
                    }
                }
                if (lastIndex != tmp) {
                    for (int i = 0; i < clientList.size(); i++) {
                        clientList.get(i).updateOther(clientList.getLast());
                    }
                    tt.updateClients(clientList.getLast());
                    lastIndex = tmp;
                }
                System.out.println(clientList);
            }
        }
    }
    
    private static class RemoveClient extends TimerTask{
        private LinkedList<ServerClient> clientList = new LinkedList<ServerClient>();
        private LinkedList<ServerClient> removable = new LinkedList<ServerClient>();

        public RemoveClient(LinkedList<ServerClient> aClientList){
            this.clientList = aClientList;
        }

        public void updateClients(ServerClient s){
            this.clientList.add(s);
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
