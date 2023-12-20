package com.example;


import java.io.*;
import java.net.*;
import java.util.*;

public class Server {


  private ServerSocket serverSocket;
    private Socket client;
    private BufferedReader input;
    private DataOutputStream output;

    public void start() throws IOException {
        this.serverSocket = new ServerSocket(8000);
    

        while (true) {
            this.client = serverSocket.accept();
            
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new DataOutputStream(client.getOutputStream());

            String request = input.readLine();
            String uri = request.split(" ")[1];
        

            String content = "";

            if (uri.equals("/")) {
                content = this.leggiFile("presente.html");
                int estensioneNum = 0;
                this.invioRisposta(output, content , estensioneNum);
            } else {
                String dir = uri.replaceFirst("/", "");
                content = this.leggiFile(dir);
                System.out.println(dir);
                int index = uri.indexOf(".");
                String estensione = uri.substring(index);
                int estensioneNum = 1;
                

                if(estensione.equals(".html")){
                    estensioneNum = 0;
                    this.invioRisposta(output, content , estensioneNum);
                }else if (estensione.equals(".jpeg") ||estensione.equals(".jpg") ){
                    estensioneNum = 1;
                    content = this.leggiFile("./images/lollo.jpeg");
                    this.invioRisposta(output, content , estensioneNum);
                    

                }
                if (content.equals("404")) {
                    content = this.leggiFile("errore.html");
                    this.sendResponse(output , content);

                } else {
                    this.invioRisposta(output, content, estensioneNum);
                }
            }

            this.close();
            
        }
    }

    public String leggiFile(String path) {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(path);

            Scanner scanner = new Scanner(is);
            String html = "";
        
            while (scanner.hasNextLine()) {
                html += scanner.nextLine();
            }
            scanner.close();
            return html;
        } catch (Exception e) {
        
            return "404";
        }
    }

    public void invioRisposta(DataOutputStream out, String content , int  estensione) throws IOException {
        try {
            byte[] body = content.getBytes();
            int contentLength = body.length;
            out.write("HTTP/1.1 200 OK\r\n".getBytes());
            switch (estensione) {
                //html
                case 0:
                out.write("Content-Type: text/html\r\n".getBytes());
                break;
                //img
                case 1 :
                out.write("content-Type : image/jpeg\r\n".getBytes());
                break;


               
            }
            out.write(("Content-Length: " + contentLength + "\r\n").getBytes());
            out.write("\r\n".getBytes());
            out.write(body);
        } catch (IOException e) {
            System.err.println("ERRORE!!!");
        }
    }

    public void sendResponse(DataOutputStream out , String content) throws IOException {
        try {
            out.write("HTTP/1.1 404 Not Found\r\n".getBytes());
            out.write("Content-Type: text/html\r\n".getBytes());
            byte[] body = content.getBytes();
            int contentLength = body.length;
            out.write(("Content-Length: " + contentLength + "\r\n").getBytes());
            out.write("\r\n".getBytes());
            out.write("<h1>404 Not Found</h1>".getBytes());
            

        } catch (IOException e) {
            System.err.println("Errore!!!");
        }
    }

    public void close() {
        try {
            this.input.close();
            this.output.close();
            this.client.close();
        } catch (IOException e) {
            System.err.println("ERRORE!!!");
        }
    }

}