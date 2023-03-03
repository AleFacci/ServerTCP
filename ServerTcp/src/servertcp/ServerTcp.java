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
    private static Timered tt = null;
    private static Timer t = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        try (// socket del server, in ascolto sulla porta 9999
                // la differenza con la socket del client è che questa socket può restare in
                // attesa di connessione (accept del TCP)
        ServerSocket ss = new ServerSocket(9999)) {
            while (true) {
                // Creo la socket di connessione al client specifico
                // per farlo metto la ServerSocket in attesa di richiesta di connessione
                System.out.println("Server in attesa...");
                Socket s = ss.accept();
                System.out.println("Richiesta di connessione ricevuta da: " + s.getRemoteSocketAddress());
                clientList.add(new ServerClient(s,clientList.getLast()));
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
                    for (ServerClient c : clientList) {
                        c.updateOther(clientList.getLast());
                    }
                    lastIndex = tmp;
                }
                tt = new Timered();
                t = new Timer();
                t.schedule(tt, 0, 1000);
            }
        }
    }
    
    private static class Timered extends TimerTask{
        
        public Timered(){
            this.run();
        }

        @Override
        public void run() {
            
        }
        
    }    
}
