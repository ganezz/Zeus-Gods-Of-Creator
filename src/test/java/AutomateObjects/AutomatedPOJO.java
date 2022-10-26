package AutomateObjects;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class AutomatedPOJO {

    public static Properties prop = new Properties();
    public static String sheetLocation, sheetName;
    public static List<List<String>> resultList = new ArrayList<>();
    public static FileWriter javaFile;
    public static String className;

    public AutomatedPOJO(){
        try {
            FileInputStream ip = new FileInputStream(new File("src/main/resources/automatedPOJO.properties"));
            prop.load(ip);
            sheetLocation = prop.getProperty("sheetLocation");
            sheetName = prop.getProperty("sheetName");
            className = prop.getProperty("className");
        } catch (Exception var3) {
            System.out.println("Property File not able to find : "+var3);
        }
    }

    public static void main(String[] args) throws IOException {
        AutomatedPOJO automatedPOJO = new AutomatedPOJO();
        System.out.println("Initiated Writing File : "+className);
        automatedPOJO.createPageObjects();
        System.out.println("Created Java File : "+className);
    }

    public void createPageObjects() throws IOException {
        getAllElementDetails(sheetLocation,sheetName);
        createJavaClass();
    }

    public void getAllElementDetails(String file, String testSheet) throws IOException {
        try {
            FileInputStream fs = new FileInputStream(file);
            XSSFWorkbook ex = new XSSFWorkbook(fs);
            XSSFSheet xs;
            int noSheet = ex.getNumberOfSheets(), i;
            for (i = 0; i < noSheet; i++) {
                if (ex.getSheetName(i).equalsIgnoreCase(testSheet)) {
                    break;
                }
            }
            xs = ex.getSheetAt(i);
            Iterator<Row> rows = xs.iterator();
            rows.next();
            while (rows.hasNext()) {
                List<String> result = new ArrayList<>();
                Iterator<Cell> reqCells = rows.next().iterator();
                result.add(0, reqCells.next().getStringCellValue());
                result.add(1, reqCells.next().getStringCellValue());
                resultList.add(result);
            }
        }catch (Exception e){
            System.out.println("Error while reading excel. Please check excel details..!");
        }
    }



    private void createJavaClass() throws IOException {
        javaFile = new FileWriter(System.getProperty("user.dir")+prop.getProperty("output_location")+className+".java");

        javaFile.write("import java.util.List;");

        javaFile.write("\n\n\npublic class "+className+"{\n\n");

        writePageObjects();

        writeConstructor();

        writeImplementations();

        javaFile.write("}");

        javaFile.close();

    }

    private static void writePageObjects() throws IOException {
        try {
            for (List<String> result : resultList) {
                String elementName = result.get(0);
                switch (result.get(1)){
                    case "string" :javaFile.write("\tprivate String " + elementName + ";\n");   break;
                    case "int" :javaFile.write("\tprivate int " + elementName + ";\n");  break;
                    case "float" :javaFile.write("\tprivate double " + elementName + ";\n");   break;
                    case "object" :javaFile.write("\tprivate Object " + elementName + ";\n");  break;
                    case "list_object" :javaFile.write("\tprivate List<Object> " + elementName + ";\n");     break;
                    default: javaFile.write("");
                }
            }
        }catch (Exception e){
            System.out.println("Not able to Create Java file \n Please make sure Excel Data provided Correct \n ex List - 2 additional xpath should provide");
        }
    }

    private void writeConstructor() throws IOException {
        javaFile.write("\n\tpublic "+className+"(");
        for (List<String> result : resultList) {
            String elementName = result.get(0);
            switch (result.get(1)){
                case "string" : javaFile.write("String " + elementName);   break;
                case "int" :javaFile.write("int " + elementName); break;
                case "float" : javaFile.write("double " + elementName); break;
                case "object" : javaFile.write("Object " + elementName);  break;
                case "list_object" :javaFile.write("List<Object> " + elementName);     break;
                default: javaFile.write("");
            }
            if (!resultList.get(resultList.size()-1).equals(result)){
                javaFile.write(",");
            }
        }
        javaFile.write(") { \n");
        for (List<String> result : resultList) {
            String methodName = result.get(0).substring(0,1).toUpperCase()+result.get(0).substring(1);
            javaFile.write("\t\tset"+methodName+"("+result.get(0)+");\n");
        }
        javaFile.write("\t} \n\n");
    }


    private static void writeImplementations() throws IOException {
        for (List<String> result: resultList) {
            String methodName = result.get(0).substring(0,1).toUpperCase()+result.get(0).substring(1);
            String elementName = result.get(0);
            switch (result.get(1)){
                case "string" : javaFile.write("\tpublic String get"+methodName+"(){\n\t\t return "+elementName+";\n\t}\n\n");
                                javaFile.write("\tpublic void set"+methodName+"(String "+elementName+") {\n\t\tthis."+elementName+" = "+elementName+";\n\t}\n\n");
                                break;
                case "int" :    javaFile.write("\tpublic int get"+methodName+"(){\n\t\t return "+elementName+";\n\t}\n\n");
                                javaFile.write("\tpublic void set"+methodName+"(int "+elementName+") {\n\t\tthis."+elementName+" = "+elementName+";\n\t}\n\n");
                                break;
                case "float" :  javaFile.write("\tpublic double get"+methodName+"(){\n\t\t return "+elementName+";\n\t}\n\n");
                                javaFile.write("\tpublic void set"+methodName+"(double "+elementName+") {\n\t\tthis."+elementName+" = "+elementName+";\n\t}\n\n");
                                break;
                case "object" :javaFile.write("\tpublic Object get"+methodName+"(){\n\t\t return "+elementName+";\n\t}\n\n");
                               javaFile.write("\tpublic void set"+methodName+"(Object "+elementName+") {\n\t\tthis."+elementName+" = "+elementName+";\n\t}\n\n");
                               break;
                case "list_object" :javaFile.write("\tpublic List<Object> get"+methodName+"(){\n\t\t return "+elementName+";\n\t}\n\n");
                                    javaFile.write("\tpublic void set"+methodName+"(List<Object>  "+elementName+") {\n\t\tthis."+elementName+" = "+elementName+";\n\t}\n\n");
                                    break;
                default: javaFile.write("");
            }
        }
    }
}
