package Model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Austin on 2016-11-14.
 */
public class ClothingProduct{
    private String category;
    private String name;
    private String sku;
    private String brand;
    private URL mainPage;
    private double price;
    private List<String> sizes;
    private Document doc;
    private String description;
    private String colour;
    private String alternateSKU;


    public ClothingProduct(String brand, String name,  double price, URL mainPage) {
        this.sku = "";
        this.alternateSKU = "";
        this.description = "";
        this.colour = "";
        this.name = name;
        this.brand = brand;
        this.mainPage = mainPage;
        this.price = price;
        sizes = new ArrayList<String>();

    }

    public ClothingProduct(String alternateSKU, String brand, String name, String size, String description, String colour, String sku, double price, String url) {
        try{
            this.alternateSKU = alternateSKU;
            this.brand = brand;
            this.name = name;
            this.description = description;
            sizes = new ArrayList<String>();
            sizes.add(size);
            this.colour = colour;
            this.sku = sku;
            this.price = price;
            this.mainPage = new URL(url);
        } catch (MalformedURLException e){
            System.out.println("Malformed URL");
        }

    }
    public void insertSizes(ClothingProduct product){
        List<String> otherSizes = product.getSizes();
        for(String s: otherSizes){
            if(!sizes.contains(s)){
                sizes.add(s);
            }
        }
    }

    public List<String> getSizes(){
        return Collections.unmodifiableList(sizes);
    }

    public boolean containsSize(String size){
        return sizes.contains(size);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public URL getMainPage() {
        return mainPage;
    }

    public void setMainPage(URL mainPage) {
        this.mainPage = mainPage;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setSizes(List<String> sizes) {
        this.sizes = sizes;
    }

    @Override
    public String toString(){
        if(sku.equals("")) sku = "XXXX-XXXX";
        StringBuilder sb = new StringBuilder();
        for(String s: sizes){
            sb.append(s);
            if(sizes.iterator().hasNext()){
                sb.append(":");
            }
        }
        return (sku + "," + brand + "," + name + "," + sb.toString() + "," + description + "," + colour + "," + alternateSKU + "," + price + "," + mainPage.toString());
    }

    public String toMultiLineString(){
        if(sku.equals("")) sku = "XXXX-XXXX";
        StringBuilder sb = new StringBuilder();

        for(String s: sizes){
            sb.append((sku + "," + brand + "," + name + "," + s + "," + description + "," + colour + "," + alternateSKU + "," + price + "," + mainPage.toString()).replace("\n", ""));
            if(sizes.iterator().hasNext()){
                sb.append("\n");
            }
        }
        return sb.toString();
    }



    public void parsePage(OutputStreamWriter osw) throws IOException{
        try{
            Document doc = Jsoup.connect(mainPage.toString()).userAgent("Chrome").cookie("auth", "token").timeout(3000).post();
            //parse SKU
            parseSKU(doc);
            //parse sizes
            parseSizes(doc);
            //parse description
            parseDescription(doc);
        } catch(NullPointerException npe){
            System.out.println(npe.getMessage() + "Problem in Parse Page");
        } catch(IOException io){
            System.out.println("Product is out of stock at:" + mainPage.toString());
        }
    }

    private void parseSKU(Document doc) throws IOException {
        Elements formOptions = doc.getElementsByClass("prod-info").select("form").select("input");
        for (Element option : formOptions) {
            if (option.attr("name").equals("Product_Code")) {
                this.sku = option.attr("value");
            }
        }
    }

    private void parseSizes(Document doc) throws IOException{
        Elements hasAttributeSelect = doc.getElementsByClass("attr-wrapper").select("select");
        for(Element option: hasAttributeSelect) {
            Elements sizeOptions = option.select("option");
            for (Element sizeOption : sizeOptions) {
                sizes.add(sizeOption.attr("value"));
            }
        }
    }

    private void parseDescription(Document doc) throws IOException {
        Elements infoWrapper = doc.getElementsByClass("info wrapper open").select("div");

        StringBuilder sb = new StringBuilder();

        try {
            String[] rawDescriptionLines = infoWrapper.get(1).toString().replace("<div>", "").replace("</div>", "").split("<br>");
            Pattern typicalSKU = Pattern.compile("- [\\p{Upper}\\d]+[-[\\p{Upper}\\d]]+");
            Pattern variationSKU = Pattern.compile("-[\\s]+Style[\\s]*#[\\s:]*");
            Matcher m2;
            Matcher m;
            for (String t : rawDescriptionLines) {
                String s = t.replace(",", "(comma)");
                m = typicalSKU.matcher(s);
                m2 = variationSKU.matcher(s);
                if (s.contains("<b>")){
                    break;
                } else if(s.toLowerCase().contains("made in")){
                    break;
                } else if(m2.find()){
                    alternateSKU = s.replace(m2.group(), "").trim();
                } else if (s.contains("- Style #: ")) {
                    alternateSKU = s.replace("- Style #: ", "").trim();
                } else if (m.find() && !s.contains("%")) {
                    alternateSKU = m.group().trim();
                } else if (s.contains(" - Color: ")) {
                    colour = s.replace(" - Color: ", "").trim();
                } else if (s.contains("Model")) {
                    break;
                } else {
                    sb.append(s);
                    if (!s.trim().equals("")) sb.append(":");
                }
                description = sb.toString().replace("\n", "");
            }
        } catch (IndexOutOfBoundsException n1){
            System.out.println(n1.getMessage() + " with description: " + infoWrapper.toString());
        }

    }


}
