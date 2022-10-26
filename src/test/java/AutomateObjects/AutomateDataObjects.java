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

public class AutomateDataObjects {

    public static Properties prop = new Properties();
    public static String sheetLocation, sheetName;
    public static List<List<String>> resultList = new ArrayList<>();
    public static FileWriter javaFile;
    public static String className;

    public AutomateDataObjects(){
        try {
            FileInputStream ip = new FileInputStream(new File("src/main/resources/automatedDataObject.properties"));
            prop.load(ip);
            sheetLocation = prop.getProperty("sheetLocation");
            sheetName = prop.getProperty("sheetName");
            className = prop.getProperty("className");
        } catch (Exception var3) {
            System.out.println("Property File not able to find : "+var3);
        }
    }

    public static void main(String[] args) throws IOException {
        AutomateDataObjects automateDataObjects = new AutomateDataObjects();
        System.out.println("Initiated Writing File : "+className);
        automateDataObjects.createPageObjects();
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
                result.add(2, reqCells.next().getStringCellValue());
                if (reqCells.hasNext()) {
                    result.add(3, reqCells.next().getStringCellValue());
                }
                resultList.add(result);
            }
        }catch (Exception e){
            System.out.println("Error while reading excel. Please check excel details..!");
        }
       // System.out.println(resultList.get(0));
    }



    private void createJavaClass() throws IOException {
        javaFile = new FileWriter(System.getProperty("user.dir")+prop.getProperty("output_location")+className+".java");

        javaFile.write("\n\nimport BrowserInitiation.TestBase;\n" +
                "import org.openqa.selenium.WebDriver;\n" +
                "import org.openqa.selenium.WebElement;\n" +
                "import org.openqa.selenium.support.FindBy;\n" +
                "import org.openqa.selenium.support.PageFactory;\n" +
                "import java.util.List;\n"+
                "import CommonUtilities.QuickUtility;");

        javaFile.write("\n\n\npublic class "+className+"{\n\n");
        javaFile.write("\tWebDriver driver = TestBase.getDriver();\n\n");

        writePageObjects();

        javaFile.write("\n\tprivate final QuickUtility utilities;");
        javaFile.write("\n\n\tpublic "+className+"(){\n\t\tPageFactory.initElements(driver,this);\n\t\tutilities = new QuickUtility();\n\t}\n\n");

        writeImplementations();

        javaFile.write("}");

        javaFile.close();

    }



    private static void writePageObjects() throws IOException {
        try {
            for (List<String> result : resultList) {
                if (result.get(1).equalsIgnoreCase("list")) {
                    javaFile.write("\t@FindBy(xpath = \"" + result.get(2) + "\")\n\tWebElement " + result.get(0) + ";\n");
                    javaFile.write("\t@FindBy(xpath = \"" + result.get(3) + "\")\n\tList<WebElement> " + result.get(0) + "List;\n");
                } else {
                    javaFile.write("\t@FindBy(xpath = \"" + result.get(2) + "\")\n\tWebElement " + result.get(0) + ";\n");
                }
            }
        }catch (Exception e){
            System.out.println("Not able to Create Java file \n Please make sure Excel Data provided Correct \n ex List - 2 additional xpath should provide");
        }
    }

    private static void writeImplementations() throws IOException {
        for (List<String> result: resultList) {
            String methodName = result.get(0).substring(0,1).toUpperCase()+result.get(0).substring(1);
            String elementName = result.get(0);
            switch (result.get(1)){
                case "text" : javaFile.write("\tpublic void set"+methodName+"(String value) throws Exception {\n\t\tutilities.sendKeys("+elementName+",value);\n\t}\n\n"); break;
                case "button" : javaFile.write("\tpublic void click"+methodName+"() throws Exception {\n\t\tutilities.click("+elementName+");\n\t}\n\n"); break;
                case "list" : javaFile.write("\tpublic void select"+methodName+"(String value) throws Exception {\n\t\tutilities.listSelection("+elementName+","+elementName+"List,value);\n\t}\n\n"); break;
                case "select" : javaFile.write("\tpublic void select"+methodName+"(String value) throws Exception {\n\t\tutilities.selectTagSelection("+elementName+",value);\n\t}\n\n"); break;
                case "assert" : javaFile.write("\tpublic void assert"+methodName+"(String value) throws Exception {\n\t\tutilities.assertValidation("+elementName+",value);\n\t}\n\n"); break;
                default: javaFile.write("");
            }
        }
    }
}
