package col7A1;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.molgenis.data.*;
import org.molgenis.data.excel.ExcelRepositoryCollection;
import org.molgenis.data.excel.ExcelWriter;
import org.molgenis.data.processor.TrimProcessor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Col7a1Duplicates_other_mutation_and_reference
{

	public static void main(String[] args) throws IOException, InvalidFormatException
	{
		FileOutputStream bos1 = new FileOutputStream("/Users/charbonb/data/col7a1/result_other_duplicates1.xls");
		FileOutputStream bos2 = new FileOutputStream("/Users/charbonb/data/col7a1/result_other_duplicates2.xls");
		FileOutputStream bos3 = new FileOutputStream("/Users/charbonb/data/col7a1/result_other_unique.xls");
		String fileInfo = "/Users/charbonb/data/col7a1/col7a1info_pubmed_Pnrs.xls";
		String fileOmx = "/Users/charbonb/data/col7a1/col7a1omx_combined_unmerged.xlsx";
		RepositoryCollection repositorySourceInfo = new ExcelRepositoryCollection(new File(fileInfo),
				new TrimProcessor());
		Repository repoInfo = repositorySourceInfo.getRepositoryByEntityName("col7_info_pubmed");
		RepositoryCollection repositorySourceOmx = new ExcelRepositoryCollection(new File(fileOmx), new TrimProcessor());
		Repository repoOmx = repositorySourceOmx.getRepositoryByEntityName("dataset_patients");

		Map<String, List<String>> cdna1Map = new HashMap<String, List<String>>();
		Map<String, List<String>> cdna2Map = new HashMap<String, List<String>>();

		for (Entity entity : repoOmx)
		{
            if(cdna1Map.containsKey(entity.getString("cDNA change 1"))){
                List<String> refs = cdna1Map.get(entity.getString("cDNA change 1"));
                refs.add(entity.getString("PubMed ID"));
                cdna1Map.put(entity.getString("cDNA change 1"), refs);
            }else{
                List<String> refs = new ArrayList<String>();
                refs.add(entity.getString("PubMed ID"));
                cdna1Map.put(entity.getString("cDNA change 1"), refs);
            }
            if(cdna2Map.containsKey(entity.getString("cDNA change 2"))){
                List<String> refs = cdna2Map.get(entity.getString("cDNA change 2"));
                refs.add(entity.getString("PubMed ID"));
                cdna2Map.put(entity.getString("cDNA change 2"), refs);
            }else{
                List<String> refs = new ArrayList<String>();
                refs.add(entity.getString("PubMed ID"));
                cdna2Map.put(entity.getString("cDNA change 2"), refs);
            }
		}

		try
		{
			List attributes = new ArrayList<String>();
			for (AttributeMetaData attributeMetaData : repoInfo.getEntityMetaData().getAttributes())
			{
				attributes.add(attributeMetaData.getName());
			}

			ExcelWriter excelWriter1 = new ExcelWriter(bos1);
			ExcelWriter excelWriter2 = new ExcelWriter(bos2);
			ExcelWriter excelWriter3 = new ExcelWriter(bos3);
			try
			{
				Writable writable1 = excelWriter1.createWritable("col7_duplicates1", attributes);
				Writable writable2 = excelWriter2.createWritable("col7_duplicates2", attributes);
				Writable writable3 = excelWriter3.createWritable("col7_unique", attributes);

				for (Entity entity : repoInfo)
				{
					boolean matched = false;
					String pubmed = entity.getString("Pubmed ID");
					String cDNA = entity.getString("Other mutation detected");
                    if(cDNA != null) {
                        cDNA = parse(cDNA);
                        if (cdna1Map.get(cDNA) != null) {
                            System.out.println("1: " + entity.getString("Other mutation detected"));
                            writable1.add(entity);
                            matched = true;
                        }
                        if (cdna2Map.get(cDNA) != null) {
                            System.out.println("2: " + entity.getString("Other mutation detected"));
                            writable2.add(entity);
                            matched = true;
                        }
                        if (!matched) {
                            System.out.println("3: " + entity.getString("Other mutation detected"));
                            writable3.add(entity);
                        }
                    }

				}
			}
			finally
			{
				excelWriter1.close();
				excelWriter2.close();
				excelWriter3.close();
			}
		}
		finally
		{

		}
	}

    private static String parse(String cDNA) {
        int index = cDNA.indexOf(" ");
        if(index != -1 && !"no data".equals(cDNA)) {
            cDNA = cDNA.substring(0,index);
        }
        return cDNA;
    }
}