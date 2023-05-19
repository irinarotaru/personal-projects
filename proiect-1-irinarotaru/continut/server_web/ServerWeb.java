import org.json.JSONArray;
import org.json.JSONObject;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public class ServerWeb {
    public static void main(String[] args) throws IOException {
        System.out.println("#########################################################################");
        System.out.println("Serverul asculta potentiali clienti.");
        // pornește un server pe portul 5678
        ServerSocket serverSocket = new ServerSocket(5678);
        FileInputStream fis = null;
        Socket clientSocket = null;
        while(true) {
            try {
                fis = null;
                // așteaptă conectarea unui client la server
                // metoda accept este blocantă
                // clientSocket - socket-ul clientului conectat
                clientSocket = serverSocket.accept();
                System.out.println("S-a conectat un client." + clientSocket.toString());
                // socketWriter - wrapper peste fluxul de ieșire folosit pentru a transmite date clientului
                PrintWriter socketWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                // socketReader - wrapper peste fluxul de intrare folosit pentru a primi date de la client
                BufferedReader socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "utf-8"));
                System.out.println("aici1");
                // este citită prima linie de text din cerere
                String linieDeStart = socketReader.readLine();
                System.out.println("aici2");
                if (linieDeStart == null) {
                    clientSocket.close();
                    System.out.println("S-a terminat comunicarea cu clientul - nu s-a primit niciun mesaj.");
                    continue;
                }
                System.out.println("aici3");
                System.out.println("S-a citit linia de start din cerere: ##### " + linieDeStart + " #####");
                // mesajul citit este transmis la client
                // interpretarea sirului de caractere `linieDeStart` pentru a extrage numele resursei cerute
                String elem[]=linieDeStart.split(" ");
                String numeResursaCeruta = linieDeStart.split(" ")[1];
                if (numeResursaCeruta.equals("/")) {
                    numeResursaCeruta = "/index.html";
                }
                // calea este relativa la directorul de unde a fost executat scriptul
                String numeFisier =  "D:/PW/proiect-1-irinarotaru/continut" + numeResursaCeruta;
                // trimiterea răspunsului HTTP
                File f = new File(numeFisier);
                byte[] buffer = new byte[1024];
                int n;
                if (f.exists()) {
                    String numeExtensie = numeFisier.substring(numeFisier.lastIndexOf(".") + 1);
                    String tipMedia;
                    switch(numeExtensie) {
                        case "html": tipMedia = "text/html; charset=utf-8"; break;
                        case "css": tipMedia = "text/css; charset=utf-8"; break;
                        case "js": tipMedia = "text/javascript; charset=utf-8"; break;
                        case "png": tipMedia = "image/png"; break;
                        case "jpg": tipMedia = "image/jpeg"; break;
                        case "jpeg": tipMedia = "image/jpeg"; break;
                        case "gif": tipMedia = "image/gif"; break;
                        case "ico": tipMedia = "image/x-icon"; break;
                        case "xml": tipMedia = "application/xml; charset=utf-8"; break;
                        case "json": tipMedia = "application/json; charset=utf-8"; break;
                        case "gzip":
                            tipMedia="application/gzip";
                            break;
                        default: tipMedia = "text/plain; charset=utf-8";
                    }
                    if(numeExtensie=="gzip")
                    {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        GZIPOutputStream gzip = new GZIPOutputStream(baos);
                        while ((n = fis.read(buffer)) != -1) {
                            gzip.write(buffer, 0, n);
                        }
                        gzip.close();
                        byte[] compressedContent = baos.toByteArray();
                        socketWriter.print("HTTP/1.1 200 OK\r\n");
                        socketWriter.print("Content-Encoding: gzip\r\n");
                        socketWriter.print("Content-Length: " + compressedContent.length + "\r\n");
                        socketWriter.print("Content-Type: " + tipMedia +"\r\n");
                        socketWriter.print("Server: Irina Rotaru\r\n");
                        socketWriter.print("\r\n");
                        socketWriter.flush();
                        clientSocket.getOutputStream().write(compressedContent);
                        clientSocket.getOutputStream().flush();
                    }
                    else
                    {
                        socketWriter.print("HTTP/1.1 200 OK\r\n");
                        socketWriter.print("Content-Length: " + f.length() + "\r\n");
                        socketWriter.print("Content-Type: " + tipMedia +"\r\n");
                        socketWriter.print("Server: Irina Rotaru\r\n");
                        socketWriter.print("\r\n");
                        socketWriter.flush();
                        fis = new FileInputStream(f);
                        while ((n = fis.read(buffer)) != -1) {
                            clientSocket.getOutputStream().write(buffer, 0, n);
                        }
                        clientSocket.getOutputStream().flush();
                        fis.close();
                    }
                } else {
                    if(elem[0].equals("POST") && elem[1].equals("/api/utilizatori"))
                    {
                        String line;
                        Map<String, String> headers = new HashMap<>();
                        while (!(line = socketReader.readLine()).isEmpty()) {
                            if (line.contains(": ")) {
                                String[] parts = line.split(": ", 2);
                                headers.put(parts[0].trim(), parts[1].trim());
                            }
                        }
                        int contentLength = Integer.parseInt(headers.get("Content-Length"));
                        char[] payload = new char[contentLength];
                        socketReader.read(payload, 0, contentLength);
                        String payloadJson = new String(payload);

                        if (payloadJson.trim().startsWith("{")) {
                            Path filePath = Paths.get("D:/PW/proiect-1-irinarotaru/continut/resurse/utilizatori.json");
                            byte[] fileBytes = Files.readAllBytes(filePath);
                            String existingJsonData = new String(fileBytes, StandardCharsets.UTF_8);
                            JSONArray existingUsersArray;
                            if (!existingJsonData.trim().isEmpty()) {
                                existingUsersArray = new JSONArray(existingJsonData);
                            } else {
                                existingUsersArray = new JSONArray();
                            }

                            JSONObject newUser = new JSONObject(payloadJson);
                            existingUsersArray.put(newUser);
                            byte[] stringBytes = existingUsersArray.toString().getBytes(StandardCharsets.UTF_8);
                            Files.write(filePath, stringBytes, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
                            socketWriter.print("HTTP/1.1 404 Not Found\r\n");
                            socketWriter.print("Content-Length: " + f.length() + "\r\n");
                            socketWriter.print("Content-Type: text/plain; charset=utf-8\r\n");
                            socketWriter.print("Server: Irina Rotaru\r\n");
                            socketWriter.print("\r\n");
                        }
                    }
                    else {
                        // daca fisierul nu exista trebuie trimis un mesaj de 404 Not Found
                        String msg = "Eroare! Resursa ceruta " + numeResursaCeruta + " nu a putut fi gasita!";
                        System.out.println(msg);
                        socketWriter.print("HTTP/1.1 404 Not Found\r\n");
                        socketWriter.print("Content-Length: " + msg.getBytes("UTF-8").length + "\r\n");
                        socketWriter.print("Content-Type: text/plain; charset=utf-8\r\n");
                        socketWriter.print("Server: Irina Rotaru\r\n");
                        socketWriter.print("\r\n");
                        socketWriter.print(msg);
                        socketWriter.flush();
                    }
                }
                // închide conexiunea cu clientul
                // la apelul metodei close() se închid automat fluxurile de intrare și ieșire (socketReader și socketWriter)
                clientSocket.close();
                System.out.println("S-a terminat comunicarea cu clientul.");
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch(Exception e) {}
                }
                if (clientSocket != null) {
                    try {
                        clientSocket.close();
                    } catch(Exception e) {}
                }
            }
        }
        // închide serverul
        //serverSocket.close();
    }
}