/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servertcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alessandro
 */
public class ServerClient extends Thread{
    private Socket s = null;
    private boolean termina = false;
    private String l = "";
    private PrintWriter out;
    private InputStreamReader isr;
    public BufferedReader in;
    public LinkedList<ServerClient> otherClients = new LinkedList<ServerClient>();

    public ServerClient(Socket client) throws IOException{
        this.s = client;
        this.isr = new InputStreamReader(client.getInputStream());
        this.in = new BufferedReader(isr);
        this.out = new PrintWriter(client.getOutputStream(), true);
        this.start();
    }
    //manda messaggi al client
    public void sendMsg(Object msg){
        this.out.println(msg);
    }
    //aggiorna la lista dei client
    public void updateOther(ServerClient client){
        this.otherClients.add(client);
    }
    
    public void sendAll(Object msg){
        for(ServerClient other : this.otherClients){
            if(other.getName() != this.getName()){
                other.sendMsg(msg);
            }
        }
    }

    public boolean terminated(){
        return termina;
    }
    //termina la sessione e chiude il client
    private void terminate() throws IOException{
        this.termina = true;
        this.s.close();
    }
    //restituisce il socket a cui vengono mandati i messaggi
    /*public ServerClient getTo(){
        return this.to;
    }*/
    //ritoran la porta del client
    private int getPort(){
        return this.s.getPort();
    }

    @Override
    public void run() {
        while(!termina){
            try {
                //aspetta che ci sia qualcosa nel buffer
                if(this.in != null){
                    //lo inserisce nella variabile "l" e lo printa a schermo di odo da sapere chi llo ha  mandato
                    this.l = this.in.readLine();
                    System.out.println("client message from "+s.getPort()+ ":" + this.l);
                    //viene capito cosa fare del messaggio
                    //da cambiare con un ifelse molto probabilmente
                    switch (this.l) {
                        case "termina":
                            this.terminate();
                            break;
                    
                        default:
                        //prova a ricavare la porta del client a cui mandare i messaggi se non ci riesce manda il mesaggio al client
                        //da modificare
                            this.l = "message from "+this.s.getLocalAddress()+": "+this.l;
                            this.sendAll(this.l);
                            break;
                    }
                }
            } catch (Exception e) {
                try {
                    this.terminate();
                } catch (IOException ex) {
                    Logger.getLogger(ServerClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("error: "+e.getMessage());
            }
        }
        this.interrupt();
    }
}
