package com.lab7;
import java.util.LinkedList;
import java.net.MalformedURLException;
import java.net.URL;
//Класс для представления пар типа [URL, depth]
public class URLDepthPair {
    public static final String URL_PREFIX = "http://";  //Константа URL-префикса

    public String URL; //Строка хранящая URL-адрес
    public int depth;  //Переменная хранящая глубину поиска
    /** Конструктор **/
    public URLDepthPair (String URL, int depth){
        this.URL=URL;
        this.depth=depth;
    }
    /** Геттеры **/
//MalformedURLException - вылетает когда URL-адрес не начинается с "http://"
    public String getHost() throws MalformedURLException {
        URL host = new URL(URL);
        return host.getHost();
    }
    public String getPath() throws MalformedURLException {
        URL path = new URL(URL);
        return path.getPath();
    }
    public int getDepth() {
        return depth;
    }
    public String getURL() {
        return URL;
    }
    /** Метод проверки на то, есть ли второй входной параметр в
     списке, который является первым параметром **/
    public static boolean check(LinkedList<URLDepthPair> resultLink, URLDepthPair pair) {
        boolean isAlready = true;
        for (URLDepthPair c : resultLink)
            if (c.getURL().equals(pair.getURL()))
                isAlready=false;
        return isAlready;
    }
}