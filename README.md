# BlueAndCreamParser
Product Parser for Blue And Cream Product Catalog

## This console-based application has several modes:
1. Database input output  
This mode takes a csv with product information and loads it as Java Objects inside the ProductManager  
2. Fetching from existing products in ProductManager  
It is possible to load a csv with only product name and product page (:URI) in [1]. This mode iteratates through those products and
retrieves the associated product information. Since html is generated dynamically loaded, a 3 second delay is hardcoded before fetching
size/colour availability information. Product codes are parsed by a set of Regex patterns that scrub the product description for known patterns.
3. Write to a database file  
Writes the current ProductCatalog to a csv file to be loaded next time (for data processing, without needing to reacquire HTML content)
