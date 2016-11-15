package Model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.crypto.spec.DESedeKeySpec;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
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
        this.name = name;
        this.brand = brand;
        this.mainPage = mainPage;
        this.price = price;
        sizes = new ArrayList<String>();
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

    public String getSizes() {
        StringBuilder sb = new StringBuilder();
        for(String s: sizes){
            sb.append(s + ":");
        }
        return (sb.toString().length() >= 1?  sb.toString().substring(0, sb.toString().length()-1):"");
    }

    public void setSizes(List<String> sizes) {
        this.sizes = sizes;
    }

    @Override
    public String toString(){
        return (sku + "," + brand + "," + name + "," + getSizes() + "," + description + "," + colour + "," + alternateSKU + '\n' ).replace("\n", "");
    }

    public void parsePage(OutputStreamWriter osw) throws IOException{
        try{
            Document doc = Jsoup.connect(mainPage.toString()).userAgent("Mozilla").cookie("auth", "token").timeout(3000).post();
            //parse SKU
            parseSKU(doc);
            //parse sizes
            parseSizes(doc);
            //parse description
            parseDescription(doc);

            osw.write(toString() + '\n');
            osw.flush();
            System.out.println(toString());

        } catch(NullPointerException npe){
            System.out.println(npe.getMessage() + "Problem in Parse Page");
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
                if(s.toLowerCase().contains("made in")){
                    break;
                } else if(m2.find()){
                    alternateSKU = s.replace(m.group(), "");
                } else if (s.contains("- Style #: ")) {
                    alternateSKU = s.replace("- Style #: ", "");
                } else if (m.find() && !s.contains("%")) {
                    alternateSKU = m.group();
                } else if (s.contains(" - Color: ")) {
                    colour = s.replace(" - Color: ", "");
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
