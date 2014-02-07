package chd7.tools.patientIdentifier.excl_notation;

import java.io.*;
import java.util.*;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.molgenis.data.Entity;
import org.molgenis.data.Repository;
import org.molgenis.data.RepositorySource;
import org.molgenis.data.excel.ExcelEntity;
import org.molgenis.data.excel.ExcelRepositorySource;
import org.molgenis.data.excel.ExcelSheetWriter;
import org.molgenis.data.excel.ExcelWriter;
import org.molgenis.data.processor.TrimProcessor;
import org.molgenis.data.support.MapEntity;

/**
 * Created with IntelliJ IDEA. User: charbonb Date: 06/02/14 Time: 11:04 To change this template use File | Settings |
 * File Templates.
 */
public class PatientMapToCHD7ID_Patient__excl_notation
{

    public static final String NOTATION_POSITION_PUBMED = "notation_position_pubmed";
    public static final String P_NUMBERS = "P_numbers";
    public static final String OUTPUTFILE = "/Users/charbonb/data/chd7_2014/chd7ID_key_patient_map_excl_notation.xls";
    public static final String INPUTFILE = "/Users/charbonb/data/chd7_2014/molgenis33_key_patient_map_excl_notation.xls";
    public static final String RAWINPUTFILE = "/Users/charbonb/data/chd7_2014/chd7_raw.xls";
    private static ExcelWriter excelWriter;
    private static ExcelSheetWriter esw;

    public static void main(String[] args) throws IOException, InvalidFormatException {
        try
        {
            // inputfile
            InputStream inputStream = new FileInputStream(INPUTFILE);
            RepositorySource entitySource = new ExcelRepositorySource("repositorySource", inputStream, new TrimProcessor(false, true));
            Repository sheetReader = entitySource.getRepository("sheet2");
            // inputfile
            InputStream rawInputStream = new FileInputStream(RAWINPUTFILE);
            RepositorySource rawEntitySource = new ExcelRepositorySource("rawEntitySource", inputStream, new TrimProcessor(false, true));
            Repository rawReader = rawEntitySource.getRepository("CHD7_mutaties_22_06_2011");
            // outputfile
            File outputFile = new File(OUTPUTFILE);
            List<String> columns = new ArrayList<String>();
            columns.add("CHD7ID");
            columns.add(P_NUMBERS);
            OutputStream os = new FileOutputStream(outputFile);
            excelWriter = new ExcelWriter(os, ExcelWriter.FileFormat.XLSX);
            esw = excelWriter.createWritable("sheet2", columns);

            List<String> patients = new ArrayList<String>();
            HashMap<String, String> inputMap = new HashMap<String, String>();
            HashMap<String, String> outputMap = new HashMap<String, String>();

            Iterator inputIterator = sheetReader.iterator();
            while (inputIterator.hasNext())
            {
                ExcelEntity element = (ExcelEntity) inputIterator.next();
                String notation_position_pubmed = element.get(NOTATION_POSITION_PUBMED).toString();
                String p_number = element.get(P_NUMBERS).toString();
                inputMap.put(notation_position_pubmed,p_number);
            }
            Iterator rawIterator = rawReader.iterator();
            while (rawIterator.hasNext())
            {
                ExcelEntity element = (ExcelEntity) rawIterator.next();
                try{
                    String chd7ID = element.get("ID CHD7 mutations").toString();
                    String position = toString(element.get("Start nucleotide"));
                    String pubmed = element.get("Pubmed ID").toString().replaceAll("null", "");;
                    String key = position + "," + pubmed;

                    if (!outputMap.containsKey(key))
                    {
                        outputMap.put(chd7ID, inputMap.get(key));
                    }
                    else
                    {
                        System.out.print("ERROR, PANIC, PANIC");
                    }
                }catch(Exception e){
                    System.out.print(e.getMessage());

                }
            }
            for (Map.Entry entry : outputMap.entrySet())
            {
                Entity kvt = new MapEntity();
                kvt.set("CHD7ID", entry.getKey());
                kvt.set(P_NUMBERS, entry.getValue());
                esw.add(kvt);
                esw.flush();
            }
        }
        finally
        {
            esw.close();
            excelWriter.close();
        }
    }

    public static String toString(Object o){
        String result = "";
        if(o!=null){
            result = o.toString();
        }
        return result;
    }

}
