import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.molgenis.data.Entity;
import org.molgenis.data.Repository;
import org.molgenis.data.RepositoryCollection;
import org.molgenis.data.Writable;
import org.molgenis.data.excel.ExcelRepositoryCollection;
import org.molgenis.data.excel.ExcelWriter;
import org.molgenis.data.processor.TrimProcessor;
import org.molgenis.data.support.MapEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by charbonb on 06/02/15.
 */
public class RemoveDuplicateLines
{
	public static void main(String[] args) throws IOException, InvalidFormatException
	{
		FileOutputStream bos1 = new FileOutputStream("/Users/charbonb/data/col7a1/categories_mut.xls");
		String inputfile = "/Users/charbonb/Downloads/Workbook3.xlsx";
		RepositoryCollection inputRepoCollection = new ExcelRepositoryCollection(new File(inputfile),
				new TrimProcessor());
		Repository inputRepo = inputRepoCollection.getRepositoryByEntityName("Sheet2");

		Map<String, List<String>> excelMap = new HashMap<String, List<String>>();
		for (Entity entity : inputRepo)
		{
			for (String attribute : entity.getAttributeNames())
			{
				List categories = excelMap.get(attribute);
				if (categories == null)
				{
					categories = new ArrayList();
				}
 try {
     if (entity.getString(attribute)!= null && !categories.contains(entity.getString(attribute))
             && !(entity.getString(attribute).equals(attribute))
             && StringUtils.isNotEmpty(entity.getString(attribute))) {
         categories.add(entity.getString(attribute));
     }
     excelMap.put(attribute, categories);

 }catch(Exception e){
                     System.out.println("");
            }		}
		}
		ExcelWriter excelWriter1 = new ExcelWriter(bos1);
		for (String inputColumn : excelMap.keySet())
		{
			Writable writable = excelWriter1.createWritable(
					inputColumn.replace('/', '_').replace('?', '_').replace(':', '_'),
					Collections.singletonList(inputColumn));
			MapEntity result = new MapEntity();
			List<String> lines = excelMap.get(inputColumn);
			for (String line : lines)
			{
				result.set(inputColumn, line);
				writable.add(result);
			}
		}
		excelWriter1.close();
	}
}
