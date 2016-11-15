package com.Practice.BlueAndCreamParser;

import Model.ClothingProduct;
import Model.HTMLParser;
import Model.ProductManager;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class Main {
    static OutputStream os;
    static OutputStreamWriter osw;



    public static void main(String[] args) throws MalformedURLException,IOException {
        os = new FileOutputStream("data/log2.csv", true);
        osw = new OutputStreamWriter(os);

        try {
            HTMLParser testParser = new HTMLParser("data/allProducts.html", osw);
        } catch (IOException e){
            System.out.println("ignore me");
        }

        System.out.println("Printing to file");



    }

}
