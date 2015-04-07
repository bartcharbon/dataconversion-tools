package col7A1.archive;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.molgenis.data.*;
import org.molgenis.data.excel.ExcelRepositoryCollection;
import org.molgenis.data.excel.ExcelWriter;
import org.molgenis.data.processor.TrimProcessor;
import org.molgenis.data.support.MapEntity;

public class Col7a1Pubmed
{

	public static void main(String[] args) throws IOException, InvalidFormatException
	{
		FileOutputStream bos = new FileOutputStream("/Users/charbonb/Desktop/Dec2014_col7a1/col7a1info_pubmed.xls");
		String file = "/Users/charbonb/Desktop/Dec2014_col7a1/starting_point/col7a1info.xlsx";
		RepositoryCollection repositorySource = new ExcelRepositoryCollection(new File(file), new TrimProcessor());
		Repository repo = repositorySource.getRepositoryByEntityName("col7a1_17may2014");

		try
		{
			List attributes = new ArrayList<String>();
			for (AttributeMetaData attributeMetaData : repo.getEntityMetaData().getAttributes())
			{
				attributes.add(attributeMetaData.getName());
			}
			attributes.add("Pubmed ID");

			ExcelWriter excelWriter = new ExcelWriter(bos);
			try
			{
				Writable writable = excelWriter.createWritable("col7_info_pubmed", attributes);

				for (Entity entity : repo)
				{

                    Map<String, Object> values = new HashMap<String, Object>();
                    for (String attributeName : entity.getAttributeNames())
                    {
                        values.put(attributeName, entity.get(attributeName));
                    }
                    MapEntity newEntity = new MapEntity(values);

                    URL url = new URL("http://www.ncbi.nlm.nih.gov/pubmed?term="
							+ entity.getString("Reference").replaceAll(" ", "%20") + "&report=uilist");
					System.out.println(url.toString());
					Scanner s = new Scanner(url.openStream());
					while (s.hasNext())
					{
						String line = s.nextLine();
						if (line.indexOf("<pre>") != -1)
						{
							if (line.indexOf("</pre>") == -1)
							{
								String pubmed = line.substring(line.indexOf("<pre>") + 5);

								values.put("Pubmed ID", pubmed);
							}
						}
					}
                    writable.add(newEntity);
				}
			}
			finally
			{
                 excelWriter.close();
			}
		}
		finally
		{

		}
	}
}