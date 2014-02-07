package chd7.tools.patientIdentifier.inclNotation;

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
public class dbExportToPatientMap
{

	public static final String NOTATION_POSITION_PUBMED = "notation_position_pubmed";
	public static final String P_NUMBERS = "P_numbers";
	public static final String OUTPUTFILE = "/Users/charbonb/data/chd7_2014/molgenis33_key_patient_map.xls";
	public static final String INPUTFILE = "/Users/charbonb/data/chd7_2014/molgenis33export.xlsx";
	private static ExcelWriter excelWriter;
	private static ExcelSheetWriter esw;

	public static void main(String[] args) throws IOException, InvalidFormatException {
		try
		{

            // inputfile
            InputStream inputStream = new FileInputStream(INPUTFILE);
            RepositorySource entitySource = new ExcelRepositorySource("repositorySource", inputStream, new TrimProcessor(false, true));
            Repository sheetReader = entitySource.getRepository("Sheet1");
			// outputfile
			File outputFile = new File(OUTPUTFILE);
			List<String> columns = new ArrayList<String>();
			columns.add(NOTATION_POSITION_PUBMED);
			columns.add(P_NUMBERS);
			OutputStream os = new FileOutputStream(outputFile);
			excelWriter = new ExcelWriter(os, ExcelWriter.FileFormat.XLSX);
			esw = excelWriter.createWritable("sheet2", columns);

			List<String> patients = new ArrayList<String>();
			HashMap<String, String> resultMap = new HashMap<String, String>();
			Iterator inputIterator = sheetReader.iterator();
			while (inputIterator.hasNext())
			{
				ExcelEntity element = (ExcelEntity) inputIterator.next();
				String notation = element.get("cdna_notation").toString();
				String position = element.get("cdna_position").toString();
				String pubmed = element.get("name").toString().replaceAll("\\\\N", "");
				String patient = element.get("Pidentifier").toString();
				String key = notation + "," + position + "," + pubmed;
				if (!resultMap.containsKey(key))
				{
					resultMap.put(key, patient);
				}
				else
				{
					// key already exists? than we create a comnma separated value with all patient identifiers
					String value = resultMap.get(key);
					value = value + "," + patient;
					resultMap.put(key, value);
				}
			}
			for (Map.Entry entry : resultMap.entrySet())
			{
				Entity kvt = new MapEntity();
				kvt.set(NOTATION_POSITION_PUBMED, entry.getKey());
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
}
