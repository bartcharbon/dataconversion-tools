package chd7.tools.patientIdToMutationDataset;

import java.io.*;
import java.util.*;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.molgenis.data.Repository;
import org.molgenis.data.RepositorySource;
import org.molgenis.data.excel.ExcelEntity;
import org.molgenis.data.excel.ExcelRepositorySource;
import org.molgenis.data.excel.ExcelSheetWriter;
import org.molgenis.data.excel.ExcelWriter;
import org.molgenis.data.processor.TrimProcessor;
import org.molgenis.data.support.MapEntity;

public class addPatientidToMutationTab
{
	private static ExcelWriter excelWriter;
    private static ExcelSheetWriter esw;

    public static void main(String[] args) throws IOException, InvalidFormatException {
		try
		{
			InputStream inputStream = new FileInputStream("/Users/charbonb/data/chd7/2014-01-07 CHD7SplitOMXWithMutationID.xlsx");
			File outputFile = new File("/Users/charbonb/data/chd7/chd7_patientId2mutationset_output.xls");
			OutputStream os = new FileOutputStream(outputFile);

            RepositorySource entitySource = new ExcelRepositorySource("repositorySource", inputStream, new TrimProcessor(false, true));
            Repository patient_sheet = entitySource.getRepository("dataset_patients");
            Repository mutation_sheet = entitySource.getRepository("dataset_mutations");

            //create a map of all mutations(values) by patient(key)
            Map<String, String> dataset_patients_map = createPatientsMutationMap(patient_sheet);
            //invert the map, mutations as key and patients as value (comma seperated)
            Map<String, String> mutationMap = createMutationsPatientsMap(dataset_patients_map);
			
			Iterator mutationIterator = mutation_sheet.iterator();
			excelWriter = new ExcelWriter(os, ExcelWriter.FileFormat.XLSX);
			boolean writeColNames = true;

			while (mutationIterator.hasNext())
			{
				ExcelEntity element = (ExcelEntity) mutationIterator.next();
				if (writeColNames)
				{
					List<String> columns = new ArrayList<String>();
				    for (String item : element.getAttributeNames()) {
				    	columns.add(item);
				    }
                    columns.add("patient_identifier");
                    esw = excelWriter.createWritable("sheet3", columns);
                    writeColNames = false;
				}
				String patientID = mutationMap.get(element.get("Mutation IDs"));
				MapEntity kvt = new MapEntity();
				for (String colname : element.getAttributeNames())
				{
					kvt.set(colname, element.getString(colname));
				}
				kvt.set("patient_identifier", patientID);
				esw.add(kvt);
                esw.flush();
			}
		}

		finally
		{
            esw.flush();
			esw.close();
			excelWriter.close();
        }
	}

    private static Map<String, String> createMutationsPatientsMap(Map<String, String> dataset_patients_map) {
        Map<String, String> mutationMap;
        mutationMap = new HashMap<String, String>();
        for (String key : dataset_patients_map.keySet())
        {
            String mutations = dataset_patients_map.get(key);
            String patient = key;
            String[] mutationsList = mutations.split(",");

            for (String mutation : mutationsList)
            {
                if (mutationMap.get(mutation) == null)
                {
                    mutationMap.put(mutation, patient);
                }
                else
                {
                    String currentValue = mutationMap.get(mutation);
                    mutationMap.put(mutation, currentValue.concat(",").concat(patient));
                }
            }
        }
        return mutationMap;
    }

    private static Map<String, String> createPatientsMutationMap(Repository patient_sheet) {
        HashMap<String, String> dataset_patients_map = new HashMap<String, String>();
        Iterator patientIterator = patient_sheet.iterator();
        while (patientIterator.hasNext())
        {
            ExcelEntity element = null;
            element = (ExcelEntity) patientIterator.next();
            Object patientID = element.get("Family/Patient ID");
            Object mutationID = element.get("Mutation IDs");
            if(patientID != null && mutationID != null){
                dataset_patients_map.put(patientID.toString(), mutationID.toString());
            }
        }
        return dataset_patients_map;
    }
}
