package Model;

import javax.naming.OperationNotSupportedException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Created by Austin on 2016-11-14.
 */
public class ProductManager implements Iterable<ClothingProduct> {
    private static ProductManager productManager;
    private Map<URL, ClothingProduct> products;
    private int callCounter;


    private ProductManager() {
        products = new HashMap<URL, ClothingProduct>();
    }

    public static ProductManager getInstance(){
        if(productManager == null) {
            productManager = new ProductManager();
        }
            return productManager;
    }

    /**
     * Adds one product from raw data -> given from formatted log file delimited by commas and then split into strings
     * @param altSKU product_code (identifier in URL)
     * @param brand the brand of the product
     * @param name the name of the product
     * @param size the size of the specific instance of product
     * @param desc the description of the product
     * @param colour the colour of the instance of product
     * @param SKU the SKU from description of the product (could == null)
     * @param priceString the price of the product in string format (undiscounted)
     * @param productPage the raw url of the product page
     */

    public void addProductFromData(String altSKU, String brand, String name, String size, String desc, String colour, String SKU, String priceString, String productPage, String listOfPhotos){
        callCounter++;
        try{
            Double price = Double.parseDouble(priceString);
            ClothingProduct clothingProductBuffer = new ClothingProduct(altSKU, brand, name, size, desc, colour, SKU, price, productPage, listOfPhotos);
            insert(clothingProductBuffer);
        } catch (NumberFormatException nfe){
            System.out.println("Invalid Cost String: Tried to parse as double");
        }
    }

    /**
     * adds product from main page scrape -> only the brand, brandURL, altSKU, name, price and url is available
     * @param brand the brand of the product : String
     * @param brandUrl the url to the brand page : String
     * @param name the name of the product : String
     * @param price the price of the product : String
     * @param productPage the url of the product : String
     */
    public void addProductFromRaw(String brand, String brandUrl, String altSKU, String name, String price, String productPage) throws IOException{
        try{
            URL url = new URL(productPage);
            Double priceDouble = Double.parseDouble(price.replace(",", ""));
            ClothingProduct cpBuffer = new ClothingProduct(brand, name, priceDouble, url);
            insert(cpBuffer);
        } catch (MalformedURLException e){
            System.out.println("Malformed URL Exception in getting from raw");
        } catch (NumberFormatException e2){
            System.out.println("Invalid price in getting from raw");
        }

    }

    private void insert(ClothingProduct cp){
        if(!products.keySet().contains(cp.getMainPage())){
            products.put(cp.getMainPage(), cp);
        } else {
            ClothingProduct existingCP = products.get(cp.getMainPage());
            existingCP.insertSizes(cp);
            System.out.println("Product already exists, adding sizes only");
        }
    }

    public Map<URL, ClothingProduct> getProducts() {
        return Collections.unmodifiableMap(products);
    }
    @Override
    public Iterator<ClothingProduct> iterator() {
        return
                products.values().iterator();
    }

}
