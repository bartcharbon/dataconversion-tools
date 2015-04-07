import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.molgenis.data.*;
import org.molgenis.data.excel.ExcelRepositoryCollection;
import org.molgenis.data.excel.ExcelSheetWriter;
import org.molgenis.data.excel.ExcelWriter;
import org.molgenis.data.processor.TrimProcessor;
import org.molgenis.data.support.MapEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Categorizer
{
    private static ExcelSheetWriter writable = null;
    private static String tabName = "Mutations";
    //private static List<String> categoricals = Arrays.asList("Blistering,Location,Hands,Feet,Arms,Legs,Proximal_body_flexures,Trunk,Mucosa,Skin_atrophy,Milia,Nail_dystrophy,Albopapuloid_papules,Pruritic_papules,Alopecia,Squamous_cell_carcinoma(s),Revertant_skin_patch(es),Mechanism,Flexion_contractures,Pseudosyndactyly_(hands),Microstomia,Ankyloglossia,Swallowing_difficulties/_dysphagia/_oesophagus_strictures,Growth_retardation,Anaemia,Renal_failure,Dilated_cardiomyopathy,LH7:2_Amount_of_type_VII_collagen,IF_Retention_of_type_VII_Collagen_in_basal_cells,Anchoring_fibrils_Number,Anchoring_fibrils_Ultrastructure,EM_Retention_of_type_VII_Collagen_in_basal_cells,Phenotype_short,Phenotype,Age,Gender,Ethnicity,Deceased,Material_stored?".split(","));
    //private static List<String> categoricals = Arrays.asList("event,ntchange,codonchange,exon,consequence,pathogenicity,population,Journal".split(","));
    //private static List<String> categoricals = Arrays.asList("Positive family history,Coloboma,Congenital heart defect,C(L)P,Choanal anomaly,Intellectual disability,Growth retardation,Genital hypoplasia,External ear anomaly,Semicircular canal anomaly,Hearing loss,Feeding difficulties,TE anomaly,Facial palsy,Sense of smell,Deceased".split(","));
    private static List<String> categoricals = Arrays.asList("Pathogenicity,Mutation type".split(","));

    public static void main(String[] args) throws IOException, InvalidFormatException
	{
        //Variables to set:
        FileOutputStream bos1 = new FileOutputStream("/Users/charbonb/Desktop/CHD7_24_03_2015/results/temp.xls");
        String input = "/Users/charbonb/Desktop/CHD7_24_03_2015/CHD7_EMX.xlsx";

        //variables
        RepositoryCollection repositorySourceInfo = new ExcelRepositoryCollection(new File(input), new TrimProcessor());
        Repository repo = repositorySourceInfo.getRepositoryByEntityName(tabName);
        ExcelWriter excelWriter1 = new ExcelWriter(bos1);
        HashMap<String, List> categories = new HashMap<>();

        //Do it!
        normalizeCategoricalValues(repo, excelWriter1, categoricals, categories);
        writeCategoriesToFile(excelWriter1, categories);

        //finish up
        excelWriter1.close();

}

    private static void normalizeCategoricalValues(Repository repo, ExcelWriter excelWriter1, List<String> categoricals, HashMap<String, List> categories) throws IOException {
        boolean inited = false;
        try
		{
            for (Entity entity : repo)
			{
                if(inited == false) {
                    writable = excelWriter1.createWritable(tabName,
                            Lists.newArrayList(entity.getAttributeNames()));
                    inited = true;
                }

				MapEntity result = new MapEntity(entity);
				for (String attribute : entity.getAttributeNames())
				{
                    if(categoricals.contains(attribute)){
                        List<String> values = categories.get(attribute);
                        if(values == null){
                            values = new ArrayList();
                        }

                        String value = entity.getString(attribute);
                        if(value != null) {
                            //value = value.toLowerCase();
                            //value = removeTrailingCommaAndSpace(value);
                        }
                            result.set(attribute, value);

                        if(!values.contains(value)){
                            values.add(value);
                        }
                        categories.put(attribute, values);
                    }else {
                        result.set(attribute, entity.get(attribute));
                    }
				}
				writable.add(result);

			}
		}
		finally
		{
            writable.close();
			System.out.println("Removed trailing comma's and spaces and lowercased all categoricals!");
		}
    }

    private static void writeCategoriesToFile(ExcelWriter excelWriter1, HashMap<String, List> categories) throws IOException {
        for(String category : categories.keySet()){
            try{
                List<String> categoryValues = categories.get(category);
                writable = excelWriter1.createWritable(category.replace('/', '_').replace('?', '_').replace(':','_'),
                        Collections.singletonList(category));
                for(String categoryValue : categoryValues) {
                    MapEntity result = new MapEntity(category);
                    result.set(category, categoryValue);
                    writable.add(result);
                }

            }
            catch (Exception e)
            {
                System.out.println("ERROR!" + e.getMessage());
            }
            finally
            {
                writable.close();
                System.out.println("Written categorytab for category: "+category);
            }
        }
    }

    private static String removeTrailingCommaAndSpace(String value) {
        if(value.endsWith(",")||value.endsWith(" ")){
            value = StringUtils.chop(value);
            value = removeTrailingCommaAndSpace(value);
        }
        return value;
    }
}