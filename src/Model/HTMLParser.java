package Model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Austin on 2016-11-14.
 */
public class HTMLParser {
    private Document doc;
    private Set<String> listOfLinks;

    /**
     * Creates a new HTML Parser - currently set to use load data from static HTML Page
     *  - obtains superficial product information from catalogue page and creates object
     * @param str the file location string of static HTML
     * @param osw the output file writer to log the information
     * @throws IOException
     */
    public HTMLParser(String str, OutputStreamWriter osw) throws IOException {
        listOfLinks = new HashSet<String>();
        File input = new File(str);
        doc = Jsoup.parse(input, "UTF-8");
            /* Alternate code used for fetching from HTML
                doc = Jsoup.connect(str).userAgent("Mozilla").cookie("auth", "token")
                    .timeout(3000)
                    .post();
            setupStreamWriter();*/

        Elements elements = doc.getElementsByClass("category-loop-product col-xs-6 col-sm-4 col-md-3");

        for(Element product: elements){
            parseOneElement(product);
        }
        int counter = 0;
        for(ClothingProduct cp: ProductManager.getInstance()){
            try {
                cp.parsePage(osw);
                counter++;
                System.out.println("Parsing product number:" + counter);
                osw.write(cp.toString() + '\n');
                osw.flush();
            } catch (IOException io){
                System.out.println("Could not write to file for product: " + cp.getMainPage());
            }
        }
    }

    /**
     * Parses one element in Product Row
     * @param product - each product has the following information:
     *                     - Name
     *                     - Price
     *                     - URL for product page
     *                     - Brand
     *                     - Brand URL
     */
    private void parseOneElement(Element product) throws IOException{
        Elements productMetadata = product.getElementsByClass("product-meta");
        try {
            String brand = productMetadata.first().select("h1").text();
            String brandURL = product.select("h1").select("a").attr("href");
            String name = productMetadata.first().select("h2").text();
            String[] prices = productMetadata.first().select("h3").text().split("\\$");
            String price = prices[1];
            String productPage = product.select("h2").select("a").attr("href");
            String SKU = productPage.replace("http://www.blueandcream.com/mens_clothing/", "").replace(".html", "");
            //should only have one
            //System.out.println("Brand: "+ brand + " Brand URL: " + brandURL+ "Name: " + name + " Price: " + price + " URL: " + url);

            listOfLinks.add(productPage);
            addToDatabase(brand, brandURL, SKU, name, price, productPage);
        } catch (ArrayIndexOutOfBoundsException aob){
            System.out.println("Array Index when parsing price is Out Of Bounds!");
        } catch (MalformedURLException e){
            System.out.println("Malformed URL Exception");
        }
    }

    /**
     * Given a single product information, adds object to manager with the following parameters
     * @param brand obtained from text in brand image link
     * @param brandURL obtained from href in brand image link
     * @param name obtained from text in product link
     * @param price obtained from price under <h3></h3>
     * @param productPage obtained from href in product image link
     * @throws IOException when:
     *  - writing to file fails
     *  - connection to product page cannot be opened //TODO: catch case in product parse
     */
    private void addToDatabase(String brand, String brandURL, String SKU, String name, String price, String productPage) throws IOException{
        ProductManager instance = ProductManager.getInstance();
        instance.addProductFromRaw(brand, brandURL, SKU, name, price, productPage);
    }

}
