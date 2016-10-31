/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//package pingclienttime;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PingClientTime
{
     public static class Ping {
    
    Timer timer;

    public Ping(DatagramSocket socket,InetAddress ip,int port) {
        
        timer = new Timer();
        timer.schedule(new PingTask(socket,ip,port),
                       0,        //initial delay
                       1000);  //subsequent rate
    }

    class PingTask extends TimerTask  {
        int pings = 9;
        DatagramSocket socket;
        InetAddress ip;
        int port;
        public  PingTask (DatagramSocket socket,InetAddress ip,int port){
            this.socket = socket;
            this.ip = ip;
            this.port = port;
        }
        @Override
        public void run()  {
            if (pings >= 0) {
            Date date = new Date();
            String str = "PING " + (9-pings) + " " + date.toString();
            byte[] buf = str.getBytes();

            DatagramPacket message = new DatagramPacket(buf, buf.length, ip, port);
                try {
                    socket.send(message);
                } catch (IOException ex) {
                    Logger.getLogger(PingClientTime.class.getName()).log(Level.SEVERE, null, ex);
                }
	    long init = System.currentTimeMillis();
                
                pings--;
            } else {
                
                System.exit(0);   
            }
        }
    }
   }

    /**
     * @param args the command line arguments
     */
     static Ping ping;
    public static void main(String[] args) throws Exception {
        
        InetAddress enderecoIP = InetAddress.getByName(args[0].toString());
        int port = Integer.parseInt(args[1]);

// Criar um socket de datagrama para receber e enviar pacotes UDP através da porta especificada na linha de comando.
        DatagramSocket socket = new DatagramSocket();
// Enviar mensagem.
         ping = new Ping(socket,enderecoIP,port);
// Loop de processamento.
        for (int i = 0; i < 10; ) {

            
// Criar um pacote de datagrama para comportar o pacote UDP // de chegada.
            DatagramPacket serverReply = new DatagramPacket(new byte[1024], 1024);

// Bloquear até que o hospedeiro receba o pacote UDP.
            socket.setSoTimeout(1000);
            try{
            socket.receive(serverReply);
            }
            catch(Exception e)
            {
                continue;
            }

// Imprimir os dados recebidos.
	    i++;
            printData(serverReply);

        }

    }

    private static void printData(DatagramPacket request) throws Exception {

// Obter referências para a ordem de pacotes de bytes.
        byte[] buf = request.getData();

// Envolver os bytes numa cadeia de entrada vetor de bytes, de modo que você possa ler os dados como uma cadeia de bytes.
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);

// Envolver a cadeia de saída do vetor bytes num leitor de cadeia de entrada,de modo que você possa ler os dados como uma cadeia de caracteres.
        InputStreamReader isr = new InputStreamReader(bais);

// Envolver o leitor de cadeia de entrada num leitor com armazenagem, de modo que você possa ler os dados de caracteres linha a linha. (A linha é uma seqüência de caracteres terminados por alguma combinação de \r e \n.)
        BufferedReader br = new BufferedReader(isr);

// O dado da mensagem está contido numa única linha, então leia esta linha.
        String line = br.readLine();

// Imprimir o endereço do hospedeiro e o dado recebido dele.
        System.out.println("Received reply from " + request.getAddress().getHostAddress() + ":" + new String(line));

    }
}
