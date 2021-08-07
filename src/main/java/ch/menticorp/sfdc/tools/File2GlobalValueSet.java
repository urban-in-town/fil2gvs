package ch.menticorp.sfdc.tools;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * @author Greg Urbanek
 */
public class File2GlobalValueSet {

    // main entry
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    public static void main(String[] argv) {
        File2GlobalValueSet instance = new File2GlobalValueSet(argv);
        instance.gvl = instance.toGlobalValueSet();
        instance.outputFile = instance.writeGvl2File("globalValueSet");
    }
    
    // constructor
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    public File2GlobalValueSet (String[] argv) {
        // init checks
        if ( (argv == null) || (argv.length == 0) ) throw new RuntimeException( "Invalid call, "+ this.getClass().getName() +" expects one <file> param!" );
        // copy param(s)
        this.inputFile = argv[0];
    }

    // API method(s)
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    public String writeGvl2File(String fileExt) {
        File outFile = null;
        FileOutputStream fos = null;
        try {
            // create out file name
            File inFile = new File(this.inputFile);
            String outFn = inFile.getName();
            int pos = outFn.lastIndexOf(".");
            outFn = (pos < 0)? outFn +"."+ fileExt : outFn.substring(0,pos) +"."+ fileExt;
            outFile = new File( inFile.getParent(), outFn );
            // create out file stream
            fos = new FileOutputStream(outFile);
            // write doc to file
            byte[] docBytes = toBytes(this.gvl);
            fos.write(docBytes);
        } catch (Throwable e) {
            throw new RuntimeException( "Error while writing gvl to file: "+ e );
        } finally {
            if ( fos != null ) try { fos.flush(); fos.close(); } catch (Throwable e2) {}
        }
        return outFile.getAbsolutePath();
    }
    
    public Document toGlobalValueSet() {
        // slurp file
        BufferedReader reader = null;
        List<String> lines = new ArrayList();
        try {
            reader = new BufferedReader( new FileReader(new File(this.inputFile)) );
            String line = null;
            while ( (line = reader.readLine()) != null ) {
                if ( line.length() != 0 ) lines.add(line);
            }
        } catch (Throwable e) {
            throw new RuntimeException( "Error while oppening file["+this.inputFile+"]"+ e );
        } finally {
            if ( reader != null ) try { reader.close();} catch (Throwable e2) {}
        }
        if ( lines.size() == 0 ) return null;
        
        // convert lines to GlobalValueSet
        String[] lineParts = lines.get(0).split("\\t");
        if ( lineParts.length < 2 ) throw new RuntimeException( "Invalid title line, expected at least 2 fields separated by \\t, got: ["+ lines.get(0) +"]" );
        String gvlName = lineParts[0];
        String gvlDescr = lineParts[1];
        List<GlobalValueMember> members = new ArrayList();
        for (int i = 1; i < lines.size(); i++) {
            lineParts = lines.get(i).split("\\t");
            if ( lineParts == null || lineParts.length < 2 ) throw new RuntimeException( "Invalid member line["+ i +"], expected at least 2 fields separated by \\t, got: ["+ lines.get(i) +"]" );;
            GlobalValueMember member = new GlobalValueMember(lineParts[1], lineParts[0]);
            members.add(member);
        }
        if ( members.size() == 0 ) return null;
        GlobalValueSet gvl = new GlobalValueSet(gvlName, gvlDescr, members);
        
        // convert GlobalValueSet to xml elem
        Element topElem = gvl.toXml();
        Document doc = DocumentHelper.createDocument();
        doc.add(topElem);
        return doc;
    }
    
    
    // heloers
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    protected static byte[] toBytes(Document xmlObject){
        return toBytes( xmlObject, defaultPPOptions );
    }
    protected static byte[] toBytes( Document xmlObject, OutputFormat ppOptions ){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        XMLWriter writer = null;
        try {
            writer = new XMLWriter( stream, ppOptions );
        } catch( Exception e ) {
            throw new RuntimeException( e );
        }
        doc2bytes(xmlObject, writer);
        return stream.toByteArray();
    }
    protected static final OutputFormat defaultPPOptions = OutputFormat.createPrettyPrint();
    protected static final OutputFormat compactPPOptions = OutputFormat.createCompactFormat();
    public static void doc2bytes(Document xmlObject, XMLWriter writer)  {
        try {
            writer.write( xmlObject );
        } catch( IOException e ) {
            throw new RuntimeException( e );
        }
    }
    
    public static String ccTolabel(String input) {
        // group by case
        List<String> chunks = new ArrayList(); chunks.add("");
        for (String s : input.split("")) {
            int idx = chunks.size()-1;
            String chunk = chunks.get(idx);
            if ( 0 == chunk.length() ) {
                chunks.set(idx, s);
            } else  if ( sameCase(s.charAt(0), chunk.charAt(0)) ) {
                chunks.set(idx, chunk+s);
            } else {
                chunks.add(s);
            }
        }
        // stick camelecase strings together
        List<String> result = new ArrayList();
        for (String chunk : chunks) {
            int resIdx = (result.isEmpty())? 0 : result.size()-1;
            String resPart = (result.isEmpty())? null : result.get(resIdx);
            if ( (result.isEmpty()) || (resPart.length() > 1) ) {
                result.add(chunk);
            } else if ( Character.isUpperCase(chunk.charAt(0)) ) {
                result.add(chunk);
            } else {
                result.set(resIdx, resPart+chunk);
            }
        }
        // return
        return String.join(" ", result);
    }
    public static boolean sameCase(int c1, int c2) {
        return (Character.isUpperCase(c1) && Character.isUpperCase(c2)) || (Character.isLowerCase(c1) && Character.isLowerCase(c2));
    }
    
    
    // local classes
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    public static class GlobalValueSet {
       // properties
       public  String name;
       public  String label;
       public  String description;
       public List<GlobalValueMember> members;
       // constructor
       public GlobalValueSet(String name, String description, List<GlobalValueMember> members) {
           this.name = name;
           this.label = name2label(name);
           this.description = description;
           this.members = members;
       }
       // methods
       public String name2label(String name) {
           return ccTolabel(name);
       }
       // <GlobalValueSet xmlns="http://soap.sforce.com/2006/04/metadata"> <customValue/> <description/> <masterLabel/> <sorted/>< /GlobalValueSet>
       public Element toXml() {
           Element elem = DocumentHelper.createElement(QName.get("GlobalValueSet", ns));
           for (GlobalValueMember member : this.members) elem.add( member.toXml() );
           elem.addElement(QName.get("description", ns)).setText(this.description);
           elem.addElement(QName.get("masterLabel", ns)).setText(this.label);
           elem.addElement(QName.get("sorted", ns)).setText("false");
           return elem;
       }
    }
            
    public static class GlobalValueMember {
       // properties
       public  String value;
       public  String label;
       // constructor
       public GlobalValueMember(String value, String label) {
           this.value = value;
           this.label = label;
           this.label = convertLabel(this.label);
       }
       // methods
       public String convertLabel(String label) {
           List<String> parts = Arrays.asList(label.split("_"))
            .stream()
            .map( s -> ( s.substring(0,1) + s.substring(1).toLowerCase() ) )
            .collect(Collectors.toList());
           return String.join(" ", parts);
       }
       // <customValue xmlns="http://soap.sforce.com/2006/04/metadata"> <fullName>61</fullName> <default>false</default> <label>Deutschland</label> </customValue>
       public Element toXml() {
           Element elem = DocumentHelper.createElement(QName.get("customValue", ns));
           elem.addElement(QName.get("fullName", ns)).setText(this.value);
           elem.addElement(QName.get("default", ns)).setText("false");
           elem.addElement(QName.get("label", ns)).setText(this.label);
           return elem;
       }
    }
    
    
    // properties
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    public String inputFile;
    public Document gvl;
    public String outputFile;
    
    
    // properties
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    static Namespace ns = Namespace.get("http://soap.sforce.com/2006/04/metadata");
}
