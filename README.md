We try to reintroduce the patient ID(P##) from the CHD7 website (molgenis33)
Three sources are used:
1) a dump from the molgenis 33 database
2) a chd7file from 2011
3) the current OMX file

Part1
-----
using TOOL1 we create a map of P## based on the combination of cdna_position, cdna_notation and pubmed id from file1
using TOOL2 we merge this map with the CHD7ID in File2, the combination of cdna_position, cdna_notation and pubmed id is the overlap used to combine the two
This results in a list of CHD7ID's (unique) with comma separated lists of P## mapped to them
We repeat the previous without the cdna_notation, this is less specific, but will also match a lot more rows.

Part2
-----
We then manually (using excel) compare the version with(1) and without(2) the notation, and create a merged list based on the following rules per row:
- Both versions are identical, no problem, just pick one
- version 1 is empty, but version 2has data -> pick version 2
- version 2 is empty, version one has data does not occur
- both are not empty, and not equal -> pick the most specific one, this is always version 1

Part3
-----
The merged list now maps CHD7ID to P## for more than 900 rows, the CHD7ID is available in the OMX file, and can be used to add the original P##.

Part4
-----
Order both OM and the merged list on CHD7ID - mergedlist columns to OMX file
check if both CHD7 columns are indeed the same (formula)
remove one CHD7 column (we don't need 2 ;-) )
update metadata for extra column - add features, update protocol etc.

NB: off course every row should only have one P##, we added the rows with multiple P## to have a preselection for manual correction
The tools can be found @ https://github.com/bartcharbon/dataconversion-tools


Separate from these tools there is another tool:
TOOL, this tool is used to add patient_id references to the mutation dataset. This references are used to link to the entity explorer from the dataexplorer