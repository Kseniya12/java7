package com.lab7;
import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class Crawler {
/** Два списка: один для всех сайтов, рассмотренных до текущего момента, и один,
     который включает только необработанные сайты **/
    static LinkedList <URLDepthPair> findLink = new LinkedList <URLDepthPair>();
    static LinkedList <URLDepthPair> viewedLink = new LinkedList <URLDepthPair>();
    /** Метод, выводящий на экран результаты из списка **/
    public static void showResult(LinkedList<URLDepthPair> viewedLink) {
        for (URLDepthPair c : viewedLink)
            System.out.println("Depth : "+c.getDepth() + "\tLink : "+c.getURL());
    }

    /** Метод запросов **/
    public static void request(PrintWriter out,URLDepthPair pair) throws MalformedURLException {
        //Отправляем введенные строки на другой конец соединения
        out.println("GET " + pair.getPath() + " HTTP/1.1");
        out.println("Host: " + pair.getHost());
        out.println("Connection: close");
        out.println();
        out.flush(); //Очистка потока
    }
    /** Основной метод **/
    public static void Process(String pair, int maxDepth) throws IOException {
        findLink.add(new URLDepthPair(pair, 0));
        while (!findLink.isEmpty()) {
            //Текущая рассматриваемая пара
            URLDepthPair currentPair = findLink.removeFirst();
            if (currentPair.depth < maxDepth) {
                //Создаем сокет, с номером порта 80
                Socket my_socket = new Socket(currentPair.getHost(), 80);
                //Устанавливаем время ожидания сокета (1 сек)
                my_socket.setSoTimeout(1000);
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(my_socket.getInputStream()));
                    PrintWriter out = new PrintWriter(my_socket.getOutputStream(), true);
                    request(out, currentPair);
                    String line;
                    while ((line = in.readLine()) != null) {
                        if (line.indexOf(currentPair.URL_PREFIX) != -1 && line.indexOf('"') != -1) {
                            StringBuilder currentLink = new StringBuilder();
                            int i = line.indexOf(currentPair.URL_PREFIX);
                            while (line.charAt(i) != '"' && line.charAt(i) != ' ') {
                                if (line.charAt(i) == '<') {
                                    currentLink.deleteCharAt(currentLink.length() - 1);
                                    break;
                                }
                                else {
                                    currentLink.append(line.charAt(i));
                                    i++;
                                }
                            }
                            URLDepthPair newPair = new URLDepthPair(currentLink.toString(), currentPair.depth + 1);
                            if (currentPair.check(findLink, newPair) && currentPair.check(viewedLink, newPair) && !currentPair.URL.equals(newPair.URL))
                                findLink.add(newPair);
                        }
                    }
                    my_socket.close(); //Закрываем сокет
                } catch (SocketTimeoutException e) {
                    my_socket.close();  //Закрываем сокет
                }
            }

            viewedLink.add(currentPair);
            //Добавляем текущую пару в список рассмотренных сайтов

        }
        //Вызываем метод показа результатов из списка
        showResult(viewedLink);
    }
    //В аргументах командной строки прописаны 2 параметра:
    //1)URL-адрес; 2)Глубина поиска
    //https://www.htpp.ru/

    public static void main(String[] args) {
        String[] arg = new String[]{"http://hmpg.net/","4"};
        try {
            Process(arg[0], Integer.parseInt(arg[1]));
        } catch (NumberFormatException | IOException e) {
            //Если аргументы некорректны выдаем сообщение:
            System.out.println("usage: java crawler " + arg[0] + " " + arg[1]);
        }
    }
}