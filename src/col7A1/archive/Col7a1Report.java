package col7A1;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.molgenis.data.*;
import org.molgenis.data.excel.ExcelRepositoryCollection;
import org.molgenis.data.excel.ExcelWriter;
import org.molgenis.data.processor.TrimProcessor;
import org.molgenis.data.support.MapEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Col7a1Report
{

	public static void main(String[] args) throws IOException, InvalidFormatException
	{
		FileOutputStream bos1 = new FileOutputStream("/Users/charbonb/data/col7a1/col7a1_report.xls");
		String fileInfo = "/Users/charbonb/data/col7a1/col7a1info_pubmed.xls";
		String fileDup1 = "/Users/charbonb/data/col7a1/result_duplicates1.xls";
        String fileDup2 = "/Users/charbonb/data/col7a1/result_duplicates2.xls";
        String fileUnique = "/Users/charbonb/data/col7a1/result_unique_identifiers_secMut_patients.xls";
		RepositoryCollection repositorySourceInfo = new ExcelRepositoryCollection(new File(fileInfo),
				new TrimProcessor());
		Repository repoInfo = repositorySourceInfo.getRepositoryByEntityName("col7_info_pubmed");
		RepositoryCollection repositorySourceDup1 = new ExcelRepositoryCollection(new File(fileDup1), new TrimProcessor());
        RepositoryCollection repositorySourceDup2 = new ExcelRepositoryCollection(new File(fileDup2), new TrimProcessor());
        RepositoryCollection repositoryUnique = new ExcelRepositoryCollection(new File(fileUnique), new TrimProcessor());
		Repository duplicates1 = repositorySourceDup1.getRepositoryByEntityName("col7_duplicates1");
        Repository duplicates2 = repositorySourceDup2.getRepositoryByEntityName("col7_duplicates2");
        Repository uniques = repositoryUnique.getRepositoryByEntityName("col7_unique");

		List<String> duplicates1list = new ArrayList<String>();
        List<String> duplicates2list = new ArrayList<String>();
        Map<String, String> uniquesPatientMap = new HashMap<String, String>();
        Map<String, String> uniquesMutationsMap = new HashMap<String, String>();

        for (Entity entity : duplicates1)
        {
            duplicates1list.add(entity.getString("cDNA change") + "_" + entity.getString("PubMed ID"));
        }
        for (Entity entity : duplicates2)
        {
            duplicates2list.add(entity.getString("cDNA change") + "_" + entity.getString("PubMed ID"));
        }
        for (Entity entity : uniques)
        {
            uniquesPatientMap.put(entity.getString("cDNA change 1") + "_" + entity.getString("PubMed ID"),entity.getString("Patient ID"));
            uniquesMutationsMap.put(entity.getString("cDNA change 1") + "_" + entity.getString("PubMed ID"),entity.getString("Mutation ID"));
        }

		try
		{
			List attributes = new ArrayList<String>();
			for (AttributeMetaData attributeMetaData : repoInfo.getEntityMetaData().getAttributes())
			{
				attributes.add(attributeMetaData.getName());
			}
            attributes.add("duplicate cdna1");
            attributes.add("duplicate cdna2");
            attributes.add("no duplicate found");
            attributes.add("Molgenis Patient ID");
            attributes.add("Molgenis Mutation ID");

			ExcelWriter excelWriter1 = new ExcelWriter(bos1);
            Writable writable1 = excelWriter1.createWritable("col7_report", attributes);
            try
			{

				for (Entity entity : repoInfo)
				{
                    String key = entity.getString("cDNA change") + "_" + entity.getString("PubMed ID");

                    Map<String, Object> values = new HashMap<String, Object>();
                    for (String attributeName : entity.getAttributeNames())
                    {
                        values.put(attributeName, entity.get(attributeName));
                    }

                    if(duplicates1list.contains(key)){
                        values.put("duplicate cdna1","true");
                    }
                    if(duplicates2list.contains(key)){
                        values.put("duplicate cdna2","true");
                    }
                    if(uniquesPatientMap.containsKey(key)){
                        values.put("no duplicate found","true");
                        values.put("Molgenis Patient ID",uniquesPatientMap.get(key));
                        values.put("Molgenis Mutation ID",uniquesMutationsMap.get(key));
                    }
                    MapEntity newEntity = new MapEntity(values);
                    writable1.add(newEntity);
				}
			}
			finally
			{
                writable1.close();
                excelWriter1.close();
			}
		}
		finally
		{

		}
	}
}