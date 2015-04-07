package chd7;

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
import java.util.Arrays;
import java.util.List;

public class CHD7PatientIds
{
	public static void main(String[] args) throws IOException, InvalidFormatException
	{
		FileOutputStream bos1 = new FileOutputStream(
				"/Users/charbonb/data/chd7/29012015/2015-01-29_CHD7SplitOMXWithMutationID.xlsx");

		String fileOmx = "/Users/charbonb/data/chd7/29012015/2014-04-15_CHD7SplitOMXWithMutationID.xlsx";
		String fileNicole = "/Users/charbonb/data/chd7/29012015/2012-03-07_uitdraai_mutations_uit_online_database_28012015.xls";
		RepositoryCollection repositorySourceNicole = new ExcelRepositoryCollection(new File(fileNicole),
				new TrimProcessor());
		Repository repoNicole = repositorySourceNicole.getRepositoryByEntityName("chd7");
		RepositoryCollection repositorySourceOmx = new ExcelRepositoryCollection(new File(fileOmx), new TrimProcessor());
		Repository repoOmx = repositorySourceOmx.getRepositoryByEntityName("dataset_patients");

		try
		{
			List attributes = new ArrayList<String>();
			for (AttributeMetaData attributeMetaData : repoOmx.getEntityMetaData().getAttributes())
			{
				attributes.add(attributeMetaData.getName());
			}
			attributes.add("mut");
			attributes.add("pat");

			ExcelWriter excelWriter1 = new ExcelWriter(bos1);
			try
			{
				Writable writable = excelWriter1.createWritable("chd7", attributes);

				for (Entity omxEntity : repoOmx)
				{
					MapEntity result = new MapEntity(omxEntity);
					for (Entity nicoleEntity : repoNicole)
					{
						String omxMutation = omxEntity.getString("Mutation ID");
						List<String> omxMutations = Arrays.asList(omxMutation.split(","));
                        String nicoleMutation = nicoleEntity.getString("Mutation ID");
                        if(omxMutations.contains(nicoleMutation)) {
                            result.set("pat", nicoleEntity.getString("Patient ID"));
                            result.set("mut", nicoleMutation);
                        }
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