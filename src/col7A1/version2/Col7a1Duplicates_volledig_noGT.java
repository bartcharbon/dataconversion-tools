package col7A1.version2;

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

public class Col7a1Duplicates_volledig_noGT
{
    public static void main(String[] args) throws IOException, InvalidFormatException
    {
        FileOutputStream bos1 = new FileOutputStream(
                "/Users/charbonb/Desktop/Dec2014_col7a1/versie2/step4/col7a1info_report-step9.xls");

        String fileInfo = "/Users/charbonb/Desktop/Dec2014_col7a1/step5/col7a1info_report_standardize_columns_stap8d.xls";
        String fileOmx = "/Users/charbonb/Desktop/Dec2014_col7a1/step4/debcentral.xlsx";

        RepositoryCollection repositorySourceInfo = new ExcelRepositoryCollection(new File(fileInfo),
                new TrimProcessor());
        Repository repoInfo = repositorySourceInfo.getRepositoryByEntityName("patients");
        RepositoryCollection repositorySourceOmx = new ExcelRepositoryCollection(new File(fileOmx), new TrimProcessor());
        Repository repoOmx = repositorySourceOmx.getRepositoryByEntityName("dataset_patients");


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
            attributes.add("debID 1");
            attributes.add("debID 2");
            attributes.add("debID 3");
            attributes.add("debID 4");

            ExcelWriter excelWriter1 = new ExcelWriter(bos1);
            try
            {
                Writable writable = excelWriter1.createWritable("col7_duplicate_step9", attributes);

                for (Entity infoEntity : repoInfo)
                {
                    MapEntity result = new MapEntity(infoEntity);
                    for (Entity omxEntity : repoOmx)
                    {
                        String c1omx = omxEntity.getString("cDNA change 1")==null?"":omxEntity.getString("cDNA change 1");
                        String c2omx = omxEntity.getString("cDNA change 2")==null?"":omxEntity.getString("cDNA change 2");
                        String c1omxNoGT = omxEntity.getString("cDNA change 1")==null?"":omxEntity.getString("cDNA change 1").replaceAll(">","");
                        String c2omxNoGT = omxEntity.getString("cDNA change 2")==null?"":omxEntity.getString("cDNA change 2").replaceAll(">","");

                        String p1omx = omxEntity.getString("Protein change 1")==null?"":omxEntity.getString("Protein change 1");
                        String p2omx = omxEntity.getString("Protein change 2")==null?"":omxEntity.getString("Protein change 2");
                        String pubmedomx = omxEntity.getString("PubMed ID")==null?"":omxEntity.getString("PubMed ID");
                        List<String> pubmedomxList = Arrays.asList(omxEntity.getString("PubMed ID")==null?"".split(";"):omxEntity.getString("PubMed ID").split(";"));


                        String c1info = infoEntity.getString("cDNA change 1")==null?"":infoEntity.getString("cDNA change 1");
                        String p1info = infoEntity.getString("Protein change 1")==null?"":infoEntity.getString("Protein change 1");
                        String c2info = infoEntity.getString("cDNA change 2")==null?"":infoEntity.getString("cDNA change 2");
                        String p2info = infoEntity.getString("Protein change 2")==null?"":infoEntity.getString("Protein change 2");
                        String pubmedinfo = infoEntity.getString("Pubmed ID")==null?"":infoEntity.getString("Pubmed ID");

                        boolean equalCDNA = (c1info.equals(c1omx)||c1info.equals(c2omx))&&(c2info.equals(c1omx)||c2info.equals(c2omx));
                        boolean equalCDNAnoGT = (c1info.equals(c1omxNoGT)||c1info.equals(c2omx))&&(c2info.equals(c1omxNoGT)||c2info.equals(c2omx))||(c1info.equals(c1omx)||c1info.equals(c2omxNoGT))&&(c2info.equals(c1omx)||c2info.equals(c2omxNoGT))||(c1info.equals(c1omxNoGT)||c1info.equals(c2omxNoGT))&&(c2info.equals(c1omxNoGT)||c2info.equals(c2omxNoGT));
                        boolean equalP = (p1info.equals(p1omx)||p1info.equals(p2omx))&&(p2info.equals(p1omx)||p2info.equals(p2omx));
                        boolean equalPubmed = pubmedinfo!= null && pubmedinfo.equals(pubmedomx);

                        if(equalCDNA && equalPubmed){
                            System.out.println("test1");
                            result.set("duplicate cDNA and pubmed", "v1");
                            result.set("debID 1", result.getString("debID 1")+"-"+omxEntity.getString("Patient ID"));
                        }else if(equalCDNA && pubmedomxList.contains(pubmedinfo)){
                            System.out.println("test1b");
                            result.set("duplicate cDNA and pubmed", "v2");
                            result.set("debID 1", "!!!2:" + result.getString("debID 1")+"-"+omxEntity.getString("Patient ID"));
                        }
                        else if(equalCDNAnoGT && pubmedomxList.contains(pubmedinfo)){
                            System.out.println("test1 NEW");
                            result.set("duplicate cDNA and pubmed", "v3");
                            result.set("debID 1", "NEW! !!!2:"+result.getString("debID 2")+"-"+omxEntity.getString("Patient ID"));
                        }
                        else if(equalCDNA && pubmedomxList.contains(pubmedinfo)){
                            System.out.println("test1b NEW");
                            result.set("duplicate cDNA and pubmed", "NEWb!");
                            result.set("debID 1", "NEWb! "+result.getString("debID 2")+"-"+omxEntity.getString("Patient ID"));
                        }
                        if(equalP && equalPubmed){
                            System.out.println("test2");
                            result.set("duplicate p and pubmed", true);
                            result.set("debID 2", result.getString("debID 2")+"-"+omxEntity.getString("Patient ID"));
                        }else if(equalP && pubmedomxList.contains(pubmedinfo)){
                            System.out.println("test2 NEW");
                            result.set("duplicate p and pubmed", "NEW!");
                            result.set("debID 2", "NEW! "+result.getString("debID 2")+"-"+omxEntity.getString("Patient ID"));
                        }
                        if(equalCDNA && !equalPubmed){
                            System.out.println("test3");result.set("duplicate cDNA","deb pubmed: "+pubmedomx);
                            result.set("debID 3", result.getString("debID 3")+"-"+omxEntity.getString("Patient ID"));
                        }
                        if(equalP && !equalPubmed){
                            System.out.println("test4");
                            result.set("duplicate p","deb pubmed: "+pubmedomx);
                            result.set("debID 4", result.getString("debID 4")+"-"+omxEntity.getString("Patient ID"));
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