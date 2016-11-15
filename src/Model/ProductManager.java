package Model;

import java.io.IOException;
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
    private List<ClothingProduct> products;
    private Map<String, URL> brands;

    private ProductManager() {
        products = new ArrayList<ClothingProduct>();
        brands = new HashMap<String, URL>();
    }

    public static ProductManager getInstance(){
        if(productManager == null) {
            productManager = new ProductManager();
        }
            return productManager;
    }

    /**
     * addsProductToManager
     * @param brand
     * @param brandUrl
     * @param name
     * @param price
     * @param url
     */
    public void addProduct(String brand, String brandUrl, String name, String price, String url) throws IOException{
        URL brandURL = null;
        URL productURL = null;

        double priceDouble = 0f;
        try{
            brandURL = new URL(brandUrl);
            productURL = new URL(url);
            String priceCleaned = price.replace(",", "");
           priceDouble = Double.parseDouble(priceCleaned);

        } catch (MalformedURLException e){
            System.out.println(brandUrl);
            System.out.println("Malformed URL Exception in getProductByName - Likely Brand has no specific page");
        } catch (NumberFormatException e2){
            System.out.println("Invalid price in getProductByName: " + priceDouble);
        }
            ClothingProduct bufferProduct = new ClothingProduct(brand, name, priceDouble, productURL);
            products.add(bufferProduct);
            addBrand(brand, brandURL);
         /*else {
            System.out.println("Product already in database: ");

            ClothingProduct productToUpdate = products.get(name);
            System.out.println("Old     :      New");
            System.out.println(productToUpdate.getName() + " : " + name);
            System.out.println(productToUpdate.getBrand() + " : " + brand);
            System.out.println(productToUpdate.getMainPage() + " : " + productURL);
            System.out.println(productToUpdate.getPrice() + " : " + price);
            productToUpdate.setName(name);
            productToUpdate.setBrand(brand);
            productToUpdate.setMainPage(productURL);
            productToUpdate.setPrice(priceDouble);
        }
*/
    }

    private void addBrand(String brand, URL brandURL){
        if (!brands.containsKey(brand)){
            brands.put(brand, brandURL);
        }
    }

    public List<ClothingProduct> getProducts() {
        return Collections.unmodifiableList(products);
    }

    public Map<String, URL> getBrands() {
        return Collections.unmodifiableMap(brands);
    }

    @Override
    public Iterator<ClothingProduct> iterator() {
        return products.iterator();
    }
}
