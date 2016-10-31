/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package pingclient;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;


public class PingClientRTT {

    /**
     * @param args the command line arguments
     */
	static long min = 1000,max = 0,soma = 0;
	static int cont = 0;
    public static void main(String[] args) throws Exception {
        
        InetAddress enderecoIP = InetAddress.getByName(args[0].toString());
        int port = Integer.parseInt(args[1]);

// Criar um socket de datagrama para receber e enviar pacotes UDP através da porta especificada na linha de comando.
        DatagramSocket socket = new DatagramSocket();

// Loop de processamento.
        for (int i = 0; i < 10; i++) {
// Enviar mensagem.
            Date date = new Date();
            String str = "PING " + i + " " + date.toString();
            byte[] buf = str.getBytes();

            DatagramPacket message = new DatagramPacket(buf, buf.length, enderecoIP, port);

            socket.send(message);
	    long init = System.currentTimeMillis();
// Criar um pacote de datagrama para comportar o pacote UDP // de chegada.
            DatagramPacket serverReply = new DatagramPacket(new byte[1024], 1024);

// Bloquear até que o hospedeiro receba o pacote UDP.
            socket.setSoTimeout(1000);
            try{
            socket.receive(serverReply);
            }
            catch(Exception e)
            {
		Thread.sleep(1000);
                continue;
            }

// Imprimir os dados recebidos.
	    long delay = System.currentTimeMillis()- init;
            printData(serverReply, delay);
	    calcRTT(delay);
	    Thread.sleep(1000);
        }

	System.out.println("RTT minimo : "+ min +"\n"+ "RTT maximo: " + max+"\n"+"RTT medio: "+ (soma/cont));

    }
    private static void calcRTT(long delay)
{
	if(delay< min)
	{
		min = delay;
	}
	if(delay > max)
	{
		max = delay;
	}
	soma += delay;
	cont++;
}
    private static void printData(DatagramPacket request, long delay) throws Exception {

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
        System.out.println("Received reply from " + request.getAddress().getHostAddress() + ":" + new String(line)+ "\n with a delay of  "+ delay);

    }
}
