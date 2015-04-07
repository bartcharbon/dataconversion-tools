package col7A1.archive;

import com.google.gdata.util.common.base.StringUtil;
import org.apache.commons.lang3.StringUtils;
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

public class Col7a1Duplicates_volledig
{
	static Map<String, List<String>> cdna1Map = new HashMap<String, List<String>>();
	static Map<String, List<String>> cdna2Map = new HashMap<String, List<String>>();
	static Map<String, List<String>> pdna1Map = new HashMap<String, List<String>>();
	static Map<String, List<String>> pdna2Map = new HashMap<String, List<String>>();

	public static void main(String[] args) throws IOException, InvalidFormatException
	{
		FileOutputStream bos1 = new FileOutputStream(
				"/Users/charbonb/Desktop/Dec2014_col7a1/temp/col7a1info_report.xls");

		String fileInfo = "/Users/charbonb/Desktop/Dec2014_col7a1/step3/col7a1info_pubmed.xls";
		String fileOmx = "/Users/charbonb/Desktop/Dec2014_col7a1/step4/col7a1omx_combined.xlsx";

		RepositoryCollection repositorySourceInfo = new ExcelRepositoryCollection(new File(fileInfo),
				new TrimProcessor());
		Repository repoInfo = repositorySourceInfo.getRepositoryByEntityName("col7_info_pubmed");
		RepositoryCollection repositorySourceOmx = new ExcelRepositoryCollection(new File(fileOmx), new TrimProcessor());
		Repository repoOmx = repositorySourceOmx.getRepositoryByEntityName("dataset_patients");

		for (Entity entity : repoOmx)
		{
			if (cdna1Map.containsKey(entity.getString("cDNA change 1")))
			{
				List<String> refs = cdna1Map.get(entity.getString("cDNA change 1"));
				refs.add(entity.getString("PubMed ID"));
				cdna1Map.put(entity.getString("cDNA change 1"), refs);
			}
			else
			{
				List<String> refs = new ArrayList<String>();
				refs.add(entity.getString("PubMed ID"));
				cdna1Map.put(entity.getString("cDNA change 1"), refs);
			}
			if (cdna2Map.containsKey(entity.getString("cDNA change 2")))
			{
				List<String> refs = cdna2Map.get(entity.getString("cDNA change 2"));
				refs.add(entity.getString("PubMed ID"));
				cdna2Map.put(entity.getString("cDNA change 2"), refs);
			}
			else
			{
				List<String> refs = new ArrayList<String>();
				refs.add(entity.getString("PubMed ID"));
				cdna2Map.put(entity.getString("cDNA change 2"), refs);
			}

			if (pdna1Map.containsKey(entity.getString("Protein change 1")))
			{
				List<String> refs = pdna1Map.get(entity.getString("Protein change 1"));
				refs.add(entity.getString("PubMed ID"));
				pdna1Map.put(entity.getString("Protein change 1"), refs);
			}
			else
			{
				List<String> refs = new ArrayList<String>();
				refs.add(entity.getString("PubMed ID"));
				pdna1Map.put(entity.getString("Protein change 2"), refs);
			}
			if (pdna2Map.containsKey(entity.getString("Protein change 2")))
			{
				List<String> refs = pdna2Map.get(entity.getString("Protein change 2"));
				refs.add(entity.getString("PubMed ID"));
				pdna2Map.put(entity.getString("Protein change 2"), refs);
			}
			else
			{
				List<String> refs = new ArrayList<String>();
				refs.add(entity.getString("PubMed ID"));
				pdna2Map.put(entity.getString("Protein change 2"), refs);
			}
		}

		try
		{
			List attributes = new ArrayList<String>();
			for (AttributeMetaData attributeMetaData : repoInfo.getEntityMetaData().getAttributes())
			{
				attributes.add(attributeMetaData.getName());
			}
			attributes.add("duplicate cDNA and pubmed");
			attributes.add("duplicate p and pubmed");
			attributes.add("duplicate cDNA");
			attributes.add("duplicate p");
            attributes.add("unique");

			ExcelWriter excelWriter1 = new ExcelWriter(bos1);
			try
			{
				Writable writable = excelWriter1.createWritable("col7_duplicates1", attributes);

				for (Entity entity : repoInfo)
				{
					MapEntity result = new MapEntity(entity);
					String cDNA = entity.getString("cDNA change");
					String pDNA = entity.getString("protein change 3 letter code");
					String cDNA2 = entity.getString("cDNA 2");
					String pDNA2 = entity.getString("p 2");

                    if (StringUtils.isNotEmpty(entity.getString("Pubmed ID")) && cdna1Map.get(cDNA) != null && cdna1Map.get(cDNA).contains(entity.get("Pubmed ID")))
                    {
                        result.set("duplicate cDNA and pubmed", "deb cDNA1:cDNA1");
                    }
                    else if (StringUtils.isNotEmpty(entity.getString("Pubmed ID")) && cdna2Map.get(cDNA) != null && cdna2Map.get(cDNA).contains(entity.get("Pubmed ID")))
                    {
                        result.set("duplicate cDNA and pubmed", "deb cDNA2:cDNA1");
                    }
                    else if (StringUtils.isNotEmpty(entity.getString("Pubmed ID")) && StringUtils.isNotEmpty(pDNA) && pdna1Map.get(pDNA) != null && pdna1Map.get(pDNA).contains(entity.get("Pubmed ID")))
                    {
                        result.set("duplicate p and pubmed", "deb p1:p1");
                    }
                    else if (StringUtils.isNotEmpty(entity.getString("Pubmed ID")) && StringUtils.isNotEmpty(pDNA) && pdna2Map.get(pDNA) != null && pdna2Map.get(pDNA).contains(entity.get("Pubmed ID")))
                    {
                        result.set("duplicate p and pubmed", "deb p2:p1");
                    }
                    else if (StringUtils.isNotEmpty(entity.getString("Pubmed ID")) && StringUtils.isNotEmpty(cDNA2) && cdna1Map.get(cDNA2) != null && cdna1Map.get(cDNA2).contains(entity.get("Pubmed ID")))
                    {
                        result.set("duplicate cDNA and pubmed", "deb cDNA1:cDNA2");
                    }
                    else if (StringUtils.isNotEmpty(entity.getString("Pubmed ID")) && StringUtils.isNotEmpty(cDNA2) && cdna2Map.get(cDNA2) != null && cdna2Map.get(cDNA2).contains(entity.get("Pubmed ID")))
                    {
                        result.set("duplicate cDNA and pubmed", "deb cDNA2:cDNA2");
                    }
                    else if (StringUtils.isNotEmpty(entity.getString("Pubmed ID")) && StringUtils.isNotEmpty(pDNA2) && pdna1Map.get(pDNA2) != null && pdna1Map.get(pDNA2).contains(entity.get("Pubmed ID")))
                    {
                        result.set("duplicate p and pubmed", "deb p1:p2");
                    }
                    else if (StringUtils.isNotEmpty(entity.getString("Pubmed ID")) && StringUtils.isNotEmpty(pDNA2) && pdna2Map.get(pDNA2) != null && pdna2Map.get(pDNA2).contains(entity.get("Pubmed ID")))
                    {
                        result.set("duplicate p and pubmed", "deb p2:p2");
                    }

                    else if (cdna1Map.keySet().contains(cDNA))
                    {
                        result.set("duplicate cDNA", "deb cDNA1:cDNA1");
                    }
                    else if (cdna2Map.keySet().contains(cDNA))
                    {
                        result.set("duplicate cDNA", "deb cDNA2:cDNA1");
                    }
                    else if (StringUtils.isNotEmpty(pDNA) && pdna1Map.keySet().contains(pDNA))
                    {
                        result.set("duplicate p", "deb p1:p1");
                    }
                    else if (StringUtils.isNotEmpty(pDNA) && pdna2Map.keySet().contains(pDNA))
                    {
                        result.set("duplicate p", "deb p2:p1");
                    }

                    else if (StringUtils.isNotEmpty(cDNA2) && cdna1Map.keySet().contains(cDNA2))
                    {
                        result.set("duplicate cDNA", "deb cDNA1:cDNA2");
                    }
                    else if (StringUtils.isNotEmpty(cDNA2) && cdna2Map.keySet().contains(cDNA2))
                    {
                        result.set("duplicate cDNA", "deb cDNA2:cDNA2");
                    }
                    else if (StringUtils.isNotEmpty(pDNA2) && pdna1Map.keySet().contains(pDNA2))
                    {
                        result.set("duplicate p", "deb p1:p2");
                    }
                    else if (StringUtils.isNotEmpty(pDNA2) && pdna2Map.keySet().contains(pDNA2))
                    {
                        result.set("duplicate p", "deb p2:p2");
                    }
                    else{
                        result.set("unique", true);
                    }

                    writable.add(result);
				}
			}
			finally
			{
				System.out.println("DONE!");
				excelWriter1.close();
			}
		}
		finally
		{

		}
	}
}