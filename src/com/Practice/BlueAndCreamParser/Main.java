package com.Practice.BlueAndCreamParser;

import Model.ClothingProduct;
import Model.HTMLParser;
import Model.ProductManager;
import Model.DatabaseParser;

import java.io.*;
import java.net.MalformedURLException;
import java.util.Scanner;


public class Main {
    static OutputStream os;
    public static OutputStreamWriter osw;
    static FileInputStream is;
    static ProductManager pm;
    static Scanner scanner;
    static boolean exit = false;



    public static void main(String[] args) throws MalformedURLException,IOException {
        pm = ProductManager.getInstance();
        scanner = new Scanner(System.in);

        while(!exit){
            mainMenu();
        }
        System.exit(0);
    }

    private static void mainMenu() {
        System.out.println("\nMain Menu");
        System.out.println(String.format("%1$10s", "[1] Load Data From HTML (fetch manually by product page HTML)"));
        System.out.println(String.format("%1$10s", "[2] Load Data From Database"));
        System.out.println(String.format("%1$10s", "[3] Display Products"));
        System.out.println(String.format("%1$10s", "[4] Exit..."));
        try {
            int selection = Integer.parseInt(scanner.next());
            switch (selection) {
                case 1:
                    parseFromHTML();
                    break;
                case 2:
                    parseFromData();
                    break;
                case 3:
                    displayAllProducts();
                    break;
                case 4:
                    exit = true;
                    break;
            }
        } catch (FileNotFoundException fnfe) {
            System.out.println("File Not Found Exception Thrown");
        } catch (IOException ioe) {
            System.out.println("IO Exception Thrown" + ioe.getMessage());
        }

    }

    private static void parseFromHTML() throws IOException {
        System.out.println("Type file name to save all data to:");
        String file = scanner.next();
        os = new FileOutputStream("data/" + file, false);
        osw = new OutputStreamWriter(os);
        HTMLParser testParser = new HTMLParser("data/allProducts.html", osw);
    }

    private static void parseFromData() throws IOException{
        System.out.println("Type file name to load data from:");
        String inputFile = scanner.next();
        System.out.println("data/" + inputFile);
        is = new FileInputStream("data/" + inputFile);
        System.out.println("Type file name to save all data to:");
        String file = scanner.next();
        os = new FileOutputStream("data/" + file, false);
        osw = new OutputStreamWriter(os);
        DatabaseParser.parseProducts(is);
        int counter = 0;
        for(ClothingProduct clothingProduct : ProductManager.getInstance()){
            counter++;
            osw.write(clothingProduct.toString() + '\n');
            osw.flush();
        }
        System.out.println(counter);
    }

    private static void displayAllProducts(){
        int counter = 0;
        for(ClothingProduct p: ProductManager.getInstance()){
            counter++;
            System.out.println(p.toMultiLineString());
        }
        System.out.println(counter);
    }

}
