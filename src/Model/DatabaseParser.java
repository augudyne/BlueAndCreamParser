package Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import static com.Practice.BlueAndCreamParser.Main.osw;

/**
 * Created by Austin on 2016-11-15.
 */
public class DatabaseParser {
    static ProductManager pm;


    public static void parseProducts(InputStream is) throws IOException{
        pm = ProductManager.getInstance();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;

        while((line = br.readLine()) != null) {
            if(!line.equals("")) {
                parseOneProduct(line);
            }
        }
        br.close();
    }

    private static void parseOneProduct(String line){
        String[] splitString = line.split(",");
        String altSKU = splitString[0].trim();
        String brand = splitString[1].trim();
        String name = splitString[2].trim();
        String size = splitString[3].trim();
        String desc = splitString[4].trim();
        String colour = splitString[5].trim();
        String SKU = splitString[6].trim();
        String price = splitString[7].trim();
        String rawUrl = splitString[8].trim();
        pm.addProductFromData(altSKU, brand, name, size, desc, colour, SKU, price, rawUrl);
    }
}
