package com.PMSystems;

import java.io.*;
import java.util.*;
import com.PMSystems.PMSDefinitions;
import com.PMSystems.util.NameValue;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class LoadTemplates {

    private static LoadTemplates loadTemplates = null;
    private static HashMap templatesHtmlCode = new HashMap();

    private static File tempDir = new File(PMSDefinitions.RESOURCE_DIR + "/templates/");

//    private static NameValue TemplateNameValue = new NameValue();

/*    private LoadTemplates() {
        templatesHtmlCode = new HashMap();
        loadVector();
    }

    public static LoadTemplates getInstance() {
        if(loadTemplates == null) {
            return (loadTemplates = new LoadTemplates());
        }
        return loadTemplates;
    }*/

    public static HashMap getAllTemplates() {
        loadHashmap();
        return templatesHtmlCode;
    }

    private static void loadHashmap() {
        File templates[] = tempDir.listFiles();
        FileReader fr = null;
        BufferedReader br = null;
        try {
            for(int i=0; i < templates.length; i++) {
                if(!templates[i].isFile()) {
                    continue;
                }
                fr = new FileReader(templates[i]);
                br = new BufferedReader(fr);
                String line = null;
                StringBuffer sb = new StringBuffer();
                while ( (line = br.readLine()) != null) {
                    sb.append(line);
                }
                templatesHtmlCode.put(templates[i].getName(), sb.toString()); // key = file name or template name ; value = html code of template
            }
        }
        catch (java.io.FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        finally {
            try {
                br.close();
                fr.close();
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public static Vector getTemplateNames() {
        Properties properties = new Properties();
        Vector templateNames = new Vector();
        try {
            FileInputStream fileIn = new FileInputStream(PMSDefinitions.TEMPLATES_PROPS);
            properties.load(fileIn);
            Enumeration e = properties.propertyNames();
            while(e.hasMoreElements()) {
                String name = properties.getProperty((String)e.nextElement());
                if(name != null) {
                    templateNames.add(name);
                }
            }
            Collections.sort(templateNames);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return templateNames;
        /*File templates[] = tempDir.listFiles();
        Vector templateNames = new Vector();
        for(int i = 0; i < templates.length; i++) {
            templateNames.add(templates[i].getName());
        }
        return templateNames;*/
    }

    public static void main(String[] args) {
        Vector vec = LoadTemplates.getTemplateNames();
        for(int i = 0; i < vec.size(); i++) {
            System.out.println("template " + i + ". " + vec.get(i));
        }
    }

}